package com.goldsign.commu.frame.thread;

import com.goldsign.commu.app.dao.TrafficLccDao;
import com.goldsign.commu.app.message.ConstructMessageForTcc;
import com.goldsign.commu.app.vo.FlowHourOrg;
import com.goldsign.commu.app.vo.PassageFlowVo;
import com.goldsign.commu.frame.buffer.TccBuffer;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.MessageUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-06
 * @Time: 12:43
 */
public class TccInterface implements Runnable {

    private static Logger logger = Logger.getLogger(TccInterface.class
            .getName());

    private final int REMOVE_MSG_LENGTH = 9;
    private final int OFFSET_FOUR = 4;
    private final String ENTER_MESSAGE = "13";
    private final String EXIST_MESSAGE = "14";
    public static final String FIVE_ENTER_MESSAGE = "23";
    public static final String FIVE_EXIST_MESSAGE = "24";


    @Override
    public void run() {
        byte[] data;
        while (true) {
            if (!TccBuffer.TCC_MESSAGE_QUEUE.isEmpty()) {
                data = TccBuffer.TCC_MESSAGE_QUEUE.poll();
                if (checkData(data, "Tcc转发数据异常")) {
                    byte[] messageBytes = new byte[2];
                    System.arraycopy(data, 0, messageBytes, 0, messageBytes.length);
                    String msgType = new String(messageBytes);
//                logger.info("Tcc msgType = " + msgType);
//                ConstructMessageForTcc messageForTcc = null;
                    //遗留问题穿插其他不定可能 现在只能每次从从min表获取信息 待混沌情况确定了可考虑直接读内存结果
                    if (ENTER_MESSAGE.equals(msgType) || EXIST_MESSAGE.equals(msgType)) {
                        //按规范模式处理 前提lc上传正确的 13 14报文
                        if (FrameCodeConstant.MODE_STANDARD.equals(FrameCodeConstant.USER_MODE)) {
                             handleByStandard(data, msgType);
                        } else {
                            //兼容模式 兼容错误的13 14报文
                            handleByCompatible(data, msgType);
                        }
                    } else {
                        //08 09 10
                        ConstructMessageForTcc messageForTcc = new ConstructMessageForTcc(msgType, TccBuffer.MESSAGE_NAME_MAPPING.get(msgType));
                        handleData(data, messageForTcc);
                    }
                }
            } else {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 上传上来的累计客流是分钟客流 手工转换一下
     *
     * @param data
     * @param msgType
     */
    private void handleByCompatible(byte[] data, String msgType) {
        ConstructMessageForTcc messageForTcc = null;
        if (ENTER_MESSAGE.equals(msgType)) {
            messageForTcc = new ConstructMessageForTcc(FIVE_ENTER_MESSAGE, TccBuffer.MESSAGE_NAME_MAPPING.get(FIVE_ENTER_MESSAGE));
        } else {
            messageForTcc = new ConstructMessageForTcc(FIVE_EXIST_MESSAGE, TccBuffer.MESSAGE_NAME_MAPPING.get(FIVE_EXIST_MESSAGE));
        }
        try {
            String lineStation = getCharString(9, 4, data);
            String trafficTime = getBcdString(13, 7, data);
            String dayKey = MessageUtil.getDateKey(trafficTime);

            String hourFive = MessageUtil.getFiveHourMin(trafficTime);
            String trafficTimeFive = dayKey + hourFive;
            String totalBeginTime = "";
            String totalEndTime = "";
            if (MessageUtil.isTimeBetween00And0230(hourFive)) {
                totalBeginTime = DateHelper.getDateBefore(dayKey, 3600000 * 24) + FrameCodeConstant.SQUAD_TIME;
                totalEndTime = trafficTimeFive;
            } else {
                totalBeginTime = dayKey + FrameCodeConstant.SQUAD_TIME;
                totalEndTime = trafficTimeFive;
            }
            //13客流实则23分钟客流数据 14同理 只需要处理一下客流时间即可
            handleByCompatibleMinData(data, lineStation, trafficTimeFive, messageForTcc);
            //生成真正累计客流数据
            handleByCompatibleTotalData(data, msgType, lineStation, trafficTime, totalBeginTime, totalEndTime);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * 直接统计min表计算结果
     *
     * @param data
     * @param msgType
     * @param lineStation
     * @param trafficTime
     * @param totalBeginTime
     * @param totalEndTime
     * @throws CommuException
     */
    private void handleByCompatibleTotalData(byte[] data, String msgType, String lineStation, String trafficTime, String totalBeginTime, String totalEndTime) throws Exception {
        ConstructMessageForTcc messageForTcc;
        int totalRepeatCount = getInt(20, data);
        String flag = "0";
        if (msgType.equals("14")) {
            flag = "1";
        }
        if (totalRepeatCount <= 0) {
            //客流0的时候 不能具体发哪个票种 不确定是否存在多次发
            messageForTcc = new ConstructMessageForTcc(msgType, TccBuffer.MESSAGE_NAME_MAPPING.get(msgType));
            messageForTcc.constructAndSend(FrameCodeConstant.TCC_IP, messageForTcc.constructMessage(data));
        } else {
            //有客流变化的时候
            ArrayList<PassageFlowVo> condition = new ArrayList<>(totalRepeatCount);
            int j = 21;
            for (int i = 0; i < totalRepeatCount; i++) {
                PassageFlowVo vo = new PassageFlowVo();
                vo.setLineId(lineStation.substring(0, 2));
                vo.setStationId(lineStation.substring(2, 4));
                vo.setCardMainType(getBcdString(j, 1, data));
                j++;
                vo.setCardSubType(getBcdString(j, 1, data));
                vo.setTrafficDatetime(trafficTime);
                vo.setTotalBeginTime(totalBeginTime);
                vo.setTotalEndTime(totalEndTime);
                vo.setFlag(flag);
                j = j + 5;
                condition.add(vo);
            }
            TrafficLccDao trafficLccDao = new TrafficLccDao();
            trafficLccDao.countTrafficTotal(condition);
            messageForTcc = new ConstructMessageForTcc(msgType, TccBuffer.MESSAGE_NAME_MAPPING.get(msgType));
            messageForTcc.constructAndSend(FrameCodeConstant.TCC_IP, messageForTcc.constructMessage(condition));
//                messageForTcc = new ConstructMessageForTcc(msgType, TccBuffer.MESSAGE_NAME_MAPPING.get(msgType));
//                messageForTcc.constructAndSend(FrameCodeConstant.TCC_IP, messageForTcc.constructMessage(flowHourOrg, cardTypes));
        }
    }


    private boolean checkData(byte[] data, String s) {
        if (data == null || data.length <= REMOVE_MSG_LENGTH) {
            logger.error(s);
            return false;
        }
        return true;
    }

    private void handleByStandard(byte[] data, String msgType) {
        //13 14
        ConstructMessageForTcc messageForTcc = new ConstructMessageForTcc(msgType, TccBuffer.MESSAGE_NAME_MAPPING.get(msgType));
        handleData(data, messageForTcc);
        if (ENTER_MESSAGE.equals(msgType)) {
            msgType = FIVE_ENTER_MESSAGE;
        } else {
            msgType = FIVE_EXIST_MESSAGE;
        }
        try {
            String lineStation = getCharString(9, 4, data);
            String trafficTime = getBcdString(13, 7, data);
            String trafficTimeFive = MessageUtil.getDateKey(trafficTime) + MessageUtil.getFiveHourMin(trafficTime);
            FlowHourOrg flowHourOrg = new FlowHourOrg();
            flowHourOrg.setLineId(lineStation.substring(0, 2));
            flowHourOrg.setStationId(lineStation.substring(2, 4));
            flowHourOrg.setTrafficDatetime(trafficTimeFive);
            int totalRepeatCount = getInt(20, data);
            if (totalRepeatCount <= 0) {
                //客流不变的时候
                messageForTcc = new ConstructMessageForTcc(msgType, TccBuffer.MESSAGE_NAME_MAPPING.get(msgType));
                messageForTcc.constructAndSend(FrameCodeConstant.TCC_IP, messageForTcc.constructMessage(flowHourOrg));
            } else {
                //有客流变化的时候
                ArrayList<String> cardTypes = new ArrayList<String>(totalRepeatCount);
                int j = 21;
                for (int i = 0; i < totalRepeatCount; i++) {
                    String ticketMainType = getBcdString(j, 1, data);
                    j++;
                    String tickerSubType = getBcdString(j, 1, data);
                    j = j + 5;
                    cardTypes.add(ticketMainType + tickerSubType);
                }
                messageForTcc = new ConstructMessageForTcc(msgType, TccBuffer.MESSAGE_NAME_MAPPING.get(msgType));
                messageForTcc.constructAndSend(FrameCodeConstant.TCC_IP, messageForTcc.  constructMessage(flowHourOrg, cardTypes));
            }

        } catch (CommuException e) {
            e.printStackTrace();
        }
    }

    /**
     * 去掉头部数据
     *
     * @param data
     * @param vo
     */
    private void handleData(byte[] data, ConstructMessageForTcc vo) {
//        checkData(data, vo.messageType + "==Tcc转发数据异常");
        //1累计客流和分钟客流报文偏差4
        byte[] handledData = new byte[data.length - REMOVE_MSG_LENGTH];

        System.arraycopy(data, REMOVE_MSG_LENGTH, handledData, 0, handledData.length);
//        vo.constructAndSend(FrameCodeConstant.TCC_IP, handledData);
        vo.constructAndSend(FrameCodeConstant.TCC_IP, vo.constructMessage(handledData));
    }

    /**
     * 处理累计客流转换分钟客流
     *
     * @param data
     * @param lineStation
     * @param trafficTimeFive
     * @param vo
     */
    private void handleByCompatibleMinData(byte[] data, String lineStation, String trafficTimeFive, ConstructMessageForTcc vo) {
        if (vo == null) {
            logger.error("Constructor is null ");
            return;
        }

        byte[] handledData = new byte[data.length - REMOVE_MSG_LENGTH + 5];
        System.arraycopy(lineStation.getBytes(), 0, handledData, 0, 4);
        System.arraycopy(trafficTimeFive.getBytes(), 0, handledData, 4, 12);
//        logger.info(data.length + "," + handledData.length);
//        StringBuilder builder = new StringBuilder();
//        builder.append("data:");
//        for (byte b : data) {
//            builder.append(b);
//            builder.append(",");
//        }
//        builder.append("handledData:");
//        for (byte b : handledData) {
//            builder.append(b);
//            builder.append(",");
//        }
//        logger.info(builder.toString());
        System.arraycopy(data, 20, handledData, 16, data.length - 20);
        vo.constructAndSend(FrameCodeConstant.TCC_IP, vo.constructMessage(handledData));

    }

    public String getBcdString(int offset, int length, byte[] data) throws CommuException {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < length; i++) {
                // logger.info("bcd之前："+data[offset + i]);
                sb.append(byte1ToBcd2(data[offset + i]));
                // logger.info("bcd之后："+byte1ToBcd2(data[offset + i]));
            }
        } catch (Exception e) {
            throw new CommuException(" " + e);
        }
        return sb.toString();
    }

    public String byte1ToBcd2(byte b) {
        int i = 0;
        if (b < 0) {
            i = 256 + b;
        } else {
            i = b;
        }
        return (new Integer(i / 16)).toString()
                + (new Integer(i % 16)).toString();
    }

    public String getCharString(int offset, int length, byte[] data) throws CommuException {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < length; i++) {
                sb.append(byteToChar(data[offset + i]));
            }
        } catch (Exception e) {
            throw new CommuException(" " + e);
        }
        return sb.toString();
    }

    public char byteToChar(byte b) {
        return (char) b;
    }

    public int getInt(int offset, byte[] data) {
        return byteToInt(data[offset]);
    }

    public int byteToInt(byte b) {
        int i = 0;
        if (b < 0) {
            i = 256 + b;
        } else {
            i = b;
        }
        return i;
    }
}
