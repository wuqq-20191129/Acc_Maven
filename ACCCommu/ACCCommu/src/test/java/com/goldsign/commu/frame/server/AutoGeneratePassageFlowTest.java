package com.goldsign.commu.frame.server;

import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.MessageUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-13
 * @Time: 12:21
 */
public class AutoGeneratePassageFlowTest extends BaseMessage implements Runnable, DataImpl {

//    public static final String yyyyMMdd = "yyyyMMdd";
//    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
//    public static final String yyyyMMddHHmm = "yyyyMMddHHmm";
//    public static final String yyyy_MM_dd = "yyyy-MM-dd";
//    public static final String HHmm = "HHmm";
//    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";


    public static void main(String[] args) {
        Thread thread = new Thread(new AutoGeneratePassageFlowTest(), "AutoGeneratePassageFlowTest");
        thread.start();

    }

    /**
     * 默认生成车站0110 票种0100 0101 运营日 0230
     *
     * @param messageType 消息类型
     * @param beginTime   开始时间
     * @param endTIme     结束时间
     * @param flowNum     开始客流
     * @param increaseNum 增长客流
     * @return
     */
    public BlockingQueue genPassageFlow(String messageType, String beginTime, String endTIme, int flowNum, int increaseNum) {
        BlockingQueue blockingQueue = new LinkedBlockingQueue();
        beginTime.compareTo(endTIme);
        String cardMainType = "01";
        ArrayList<String> cardSub = new ArrayList<>(2);
        cardSub.add("00");
        cardSub.add("01");
        String trafficTime = beginTime;
        String hourMin = "";
        while (trafficTime.compareTo(endTIme) < 0) {
            hourMin = MessageUtil.getFiveHourMin(trafficTime);
            if (hourMin.compareTo("0200") == 0) {
                //运营日更换
                flowNum = increaseNum;
            }

            try {
                //每小时发一次
//                System.out.println("hourMin.substring(4) = " + hourMin.substring(3));
//                if (hourMin.substring(2).equals("20")) {
//                blockingQueue.put(data(messageType, cardMainType, cardSub, trafficTime, flowNum));
//                }
//                else {
//                    blockingQueue.put(dataNon(messageType, trafficTime));
//                }
                //standard
                blockingQueue.put(data(messageType, cardMainType, cardSub, trafficTime, flowNum));
                //compatible
//                blockingQueue.put(data(messageType, cardMainType, cardSub, trafficTime, increaseNum));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            flowNum = flowNum + increaseNum;


            trafficTime = DateHelper.getDateTimeAfter(trafficTime, 5 * 60 * 1000);
        }
        return blockingQueue;
    }


    @Override
    public byte[] data() {
        byte[] data = new byte[100];
        return data;
    }

    public byte[] data(String messageType, String cardMain, ArrayList<String> cardSub, String trafficTime, int trafficNum) {
        byte[] data = new byte[21 + 6 * cardSub.size()];
        byte[] repeatCount = new byte[1];
        repeatCount[0] = (byte) cardSub.size();
        System.arraycopy(messageType.getBytes(), 0, data, 0, 2);//消息类型
        System.arraycopy(bcdStringToByteArray(DateHelper.currentTodToString()), 0, data, 2, 7);//消息生成时间
        System.arraycopy("0110".getBytes(), 0, data, 9, 4);
        System.arraycopy(bcdStringToByteArray(trafficTime), 0, data, 13, 7);//消息生成时间
        System.arraycopy(repeatCount, 0, data, 20, 1);
        int j = 21;
        for (String vo : cardSub) {
            System.arraycopy(bcdStringToByteArray(cardMain), 0, data, j, 1);
            j++;
            System.arraycopy(bcdStringToByteArray(vo), 0, data, j, 1);
            j++;
            System.arraycopy(longStringToByteArray(trafficNum, 4), 0, data, j, 4);
            j = j + 4;
        }
        return data;
    }

    public byte[] dataNon(String messageType, String trafficTime) {
        byte[] data = new byte[21];
        byte[] repeatCount = {0};
        System.arraycopy(messageType.getBytes(), 0, data, 0, 2);//消息类型
        System.arraycopy(bcdStringToByteArray(DateHelper.currentTodToString()), 0, data, 2, 7);//消息生成时间
        System.arraycopy("0110".getBytes(), 0, data, 9, 4);
        System.arraycopy(bcdStringToByteArray(trafficTime), 0, data, 13, 7);//消息生成时间
        System.arraycopy(repeatCount, 0, data, 20, 1);
        return data;
    }

    @Override
    public void run() {
        String type = "13";
        String cur = DateHelper.dateOnlyToString(new Date());
        System.out.println("cur = " + cur);
        String last = DateHelper.getDateBefore(cur, 24 * 60 * 60 * 1000);
        System.out.println("last = " + last);
        String beginTime = last + "235400";
        String endTime = cur + "000000";
        int beginFlowNum = 1200;
        int increaseNum = 10;
        System.out.println("消息类型 = " + type + ",开始时间 = " + beginTime + ",结束时间 = " + endTime + "开始客流 = " + beginFlowNum + ",增长客流 = " + increaseNum);
        BlockingQueue blockingQueue = new AutoGeneratePassageFlowTest().genPassageFlow(type, beginTime, endTime, beginFlowNum, increaseNum);
        System.out.println("消息数 = " + blockingQueue.size());
        sendMultiDataNoResponse(Integer.MAX_VALUE, blockingQueue, "批量生产客流数据");
    }
//
//    public static String getDateMinAfter(String curDate, long afterTime) {
//        SimpleDateFormat YYYYMMDDHHMM = new SimpleDateFormat(yyyyMMddHHmm);
//        Date d;
//        String dateAfter = "";
//        try {
//            d = YYYYMMDDHHMM.parse(curDate);
//            Date datePre = new Date(d.getTime() + afterTime);
//            dateAfter = YYYYMMDDHHMM.format(datePre);
//        } catch (ParseException e) {
//            Logger.getLogger(DateHelper.class.getName()).log(Level.SEVERE,
//                    null, e);
//        }
//        return dateAfter;
//
//    }
}
