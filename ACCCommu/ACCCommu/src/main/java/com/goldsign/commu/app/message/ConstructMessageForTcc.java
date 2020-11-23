package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.FlowHourMinFive;
import com.goldsign.commu.app.vo.FlowHourMinFiveUnit;
import com.goldsign.commu.app.vo.FlowHourOrg;
import com.goldsign.commu.app.vo.PassageFlowVo;
import com.goldsign.commu.frame.buffer.PubBuffer;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.thread.TccInterface;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.MessageUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-06
 * @Time: 16:17
 */
public class ConstructMessageForTcc extends ConstructMessageBase {
    public static final String ENTRY_FLAG = "0";
    public static final String EXISR_FLAG = "1";
    private static Logger logger = Logger.getLogger(ConstructMessageForTcc.class
            .getName());

    public ConstructMessageForTcc(String messageType, String messageRemark) {
        super();
        this.messageType = messageType;
        this.messageRemark = messageRemark;
    }

    /**
     * 直接转发数据
     *
     * @param data
     * @return
     */
    public byte[] constructMessage(byte[] data) {
        logger.info("--构建" + messageType + "消息开始-- ");
        try {
            initMessage();
            AddStringToMessage(messageType, 2);
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            AddByteArrayToMessage(data, data.length);
            byte[] msg = trimMessage();
            logger.info("--成功构建" + messageType + "消息--");
            return msg;
        } catch (Exception e) {
            logger.error("构建" + messageType + "消息失败! " + e);
            return new byte[0];
        }
    }

    /**
     * 转发分钟客流数据为0的时候
     *
     * @param flowHourOrg
     * @return
     */
    public byte[] constructMessage(FlowHourOrg flowHourOrg) {
        logger.info("--构建" + messageType + "消息开始-- ");
        try {
            initMessage();
            AddStringToMessage(messageType, 2);
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            AddStringToMessage(flowHourOrg.getLineId() + flowHourOrg.getStationId(), 4);
            AddStringToMessage(flowHourOrg.getTrafficDatetime(), 12);
            AddIntToMessage(0, 1);
            byte[] msg = trimMessage();
            logger.info("--成功构建" + messageType + "消息--");
            return msg;
        } catch (Exception e) {
            logger.error("构建" + messageType + "消息失败! " + e);
            return new byte[0];
        }
    }

    /**
     * 转发有数据的5分钟客流 从缓存中获取
     *
     * @param flowHourOrg
     * @param cardTypes
     * @return
     */
    public byte[] constructMessage(FlowHourOrg flowHourOrg, ArrayList<String> cardTypes) {
        logger.info("--构建" + messageType + "消息开始-- ");
        if (cardTypes.size() == 0) {
            return constructMessage(flowHourOrg);
        }
        try {
            String lineStation = flowHourOrg.getLineId() + flowHourOrg.getStationId();
            String trafficDate = flowHourOrg.getTrafficDatetime();
            initMessage();
            AddStringToMessage(messageType, 2);
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            AddStringToMessage(lineStation, 4);
            AddStringToMessage(trafficDate, 12);
            AddIntToMessage(cardTypes.size(), 1);
            String dateKey = MessageUtil.getDateKey(trafficDate);
            String hourMinFive = trafficDate.substring(8);
            TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlow = PubBuffer.bufferFlowMinFiveEntry;
            String flag = ENTRY_FLAG;
            if (messageType.equals(TccInterface.FIVE_EXIST_MESSAGE)) {
                bufferFlow = PubBuffer.bufferFlowMinFiveExit;
                flag = EXISR_FLAG;
            }
            // 当前日期客流缓存:
            HashMap<String, FlowHourMinFive> bufferFlowHourMinFive = MessageUtil.getStationTrafficFromBufForDate(
                    bufferFlow, dateKey, flag);

            for (String cardType : cardTypes) {
                AddBcdToMessage(cardType, 2);
                String stationCardKey = lineStation + cardType;
                //当前站点客流统计
                FlowHourMinFive flowHourMinFive = (FlowHourMinFive) bufferFlowHourMinFive.get(stationCardKey);
                int traffic = ((FlowHourMinFiveUnit) flowHourMinFive.getFlowHourMinFive().get(hourMinFive)).getTraffic();
                AddLongToMessage(traffic, 4);
            }
            byte[] msg = trimMessage();
            logger.info("--成功构建" + messageType + "消息--");
            return msg;
        } catch (Exception e) {
            logger.error("构建" + messageType + "消息失败! " + e);
            return new byte[0];
        }
    }

    public void constructAndSend(String ip, byte[] data) {
        sendToJms(ip, data, "99", "00");
    }

    /**
     * 累计客流
     *
     * @param condition
     * @return
     */
    public byte[] constructMessage(ArrayList<PassageFlowVo> condition) {
        logger.info("--构建" + messageType + "消息开始-- ");
        initMessage();
        AddStringToMessage(messageType, 2);
        try {
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            for (int i = 0; i < condition.size(); i++) {
                PassageFlowVo vo = condition.get(0);
                if (i == 0) {
                    AddStringToMessage(vo.getLineId() + vo.getStationId(), 4);
                    AddBcdToMessage(vo.getTrafficDatetime(), 7);
                    AddIntToMessage(condition.size(), 1);
                }
                AddBcdToMessage(vo.getCardMainType(), 1);
                AddBcdToMessage(vo.getCardSubType(), 1);
                AddLongToMessage(vo.getTrafficNum(), 4);
            }

        } catch (MessageException e) {
            e.printStackTrace();
        }
        byte[] msg = trimMessage();
        return msg;
    }
}
