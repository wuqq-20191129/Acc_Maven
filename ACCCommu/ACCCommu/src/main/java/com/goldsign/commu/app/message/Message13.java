package com.goldsign.commu.app.message;

import com.goldsign.commu.frame.buffer.PubBuffer;
import com.goldsign.commu.frame.buffer.TccBuffer;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.*;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 进站信息
 *
 * @author zhangjh
 */
public class Message13 extends MessageBase {


    private static Logger logger = Logger.getLogger(Message13.class.getName());

    // private static final byte[] SYNCONTROL = new byte[0];
    @Override
    public void run() throws Exception {
        String result = FrameLogConstant.RESULT_HDL_SUCESS;
        level = FrameLogConstant.LOG_LEVEL_INFO;
        try {
            hdlStartTime = System.currentTimeMillis();
            logger.info("--处理13消息开始--");
            process();
            logger.info("--处理13消息结束--");
            hdlEndTime = System.currentTimeMillis();
            // throw new Exception("测试异常");
        } catch (Exception e) {
            result = FrameLogConstant.RESULT_HDL_FAIL;
            level = FrameLogConstant.LOG_LEVEL_ERROR;
            hdlEndTime = System.currentTimeMillis();
            remark = e.getMessage();
            throw e;
        } finally {// 记录处理日志
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_FLOW_ENTRY,
                    messageFrom, hdlStartTime, hdlEndTime, result, threadNum,
                    level, remark, getCmDbHelper());
        }
    }

    public void process() throws Exception {

        logger.info(thisClassName + " started! " + messageSequ);
        // synchronized (SYNCONTROL) {
        try {
            // 消息生成时间
            String currentTod = getBcdString(2, 7);

            boolean result = MessageCommonUtil.isNomalDateMessageForTraffic(
                    DateHelper.convertStandFormat(currentTod),
                    DateHelper.datetimeToStringByDate(new Date()));

            // 不处理滞留数据超过设定天数的客流数据2012-04-16 hejj
            if (!result) {
                String errorMsg = "进站客流数据时间为：" + currentTod
                        + ",距当前日期间隔超过设定的最大值"
                        + FrameCodeConstant.trafficDelayMaxDay + "天,消息将不处理.";
                logger.error(errorMsg);
                return;

            }
            // 站点
            String stationId = getCharString(9, 4);
            String lineId = stationId.substring(0, 2);
            String cstationId = stationId.substring(2, 4);

            // 客流时间
            String generateTime = getBcdString(13, 7);

            // 票卡客流重复次数N
            int totalRepeatCount = getInt(20);

            String sqlStr;
            List<Object> values = new ArrayList<Object>();
            getCmDbHelper().setAutoCommit(false);

            int totalTraffic = 0;
            java.sql.Timestamp trafficDatetime = DateHelper
                    .dateStrToSqlTimestamp(generateTime);
            int j = 21;
            for (int i = 0; i < totalRepeatCount; i++) {
                messageSequ = PubUtil.getMessageSequ();
                String ticketMainType = getBcdString(j, 1);
                j++;
                String tickerSubType = getBcdString(j, 1);
                j++;
                // 进站客流累计数
                int accumulatedTrafficCount = getLong(j);
                j = j + 4;
                sqlStr = " insert into "+FrameDBConstant.COM_COMMU_P+"cm_traffic_detail(message_sequ,line_id,station_id,flag,traffic_datetime,card_main_code,card_sub_code,traffic) values(?,?,?,?,?,?,?,?)";
                values.clear();
                values.add(messageSequ);
                values.add(lineId);
                values.add(cstationId);
                values.add(FrameCodeConstant.FLAG_ENTRY);
                values.add(trafficDatetime);
                values.add(ticketMainType);
                values.add(tickerSubType);
                values.add(Integer.valueOf(accumulatedTrafficCount));
                // logger.info("票卡类型 " + ticketMainType + tickerSubType +
                // " 的进站客流 : " + accumulatedTrafficCount);
                getCmDbHelper().executeUpdate(sqlStr, values.toArray());

                totalTraffic = totalTraffic + accumulatedTrafficCount;
                // 处理小时客流改为清分同时写分钟客流
                // handleFlowHour(lineId,cstationId,ticketMainType,tickerSubType,MessageUtil.FLAG_ENTRY,generateTime,accumulatedTrafficCount);
                // 处理5分钟客流
                handleFlowHourMinFive(lineId, cstationId, ticketMainType,
                        tickerSubType, FrameCodeConstant.FLAG_ENTRY,
                        generateTime, accumulatedTrafficCount);

            }

            sqlStr = " insert into "+FrameDBConstant.COM_COMMU_P+"cm_traffic(message_sequ,line_id,station_id,traffic_datetime,traffic_type,invalid_ticket,refuse_ticket,blacklist_ticket,total_traffic) values(?,?,?,?,?,?,?,?,?)";
            values.clear();
            values.add(messageSequ);
            values.add(lineId);
            values.add(cstationId);
            values.add(trafficDatetime);
            values.add(FrameCodeConstant.FLAG_ENTRY);
            values.add(0);// new Integer(invalidExitDetected));
            values.add(0);// new Integer(ticketRefuseCount));
            values.add(0);// new Integer(blackListCount));
            values.add(new Integer(totalTraffic));

            getCmDbHelper().executeUpdate(sqlStr, values.toArray());
            getCmDbHelper().commitTran();
            getCmDbHelper().setAutoCommit(true);
            //add by zhongzq 用于tcc转发 20190605 生产者
            if (FrameCodeConstant.TCC_INTERFACE_USE_KEY.equals(FrameCodeConstant.TCC_INTERFACE_CONTROL)) {
                TccBuffer.TCC_MESSAGE_QUEUE.offer(data);
                Object[] paras = {"13", messageSequ, lineId, cstationId};
                getCmDbHelper().executeUpdate(TccBuffer.logSqlPassageFlow, paras);
            }
        } catch (SQLException e) {
            getCmDbHelper().rollbackTran();
            getCmDbHelper().setAutoCommit(true);
            logger.error("数据库异常,错误代码" + e.getErrorCode() + " 消息", e);
            throw e;
        } catch (Exception e) {
            getCmDbHelper().rollbackTran();
            getCmDbHelper().setAutoCommit(true);
            logger.error("进站客流消息处理异常：", e);
            throw e;
        }
        // }
    }

    /**
     * 处理5分钟客流
     *
     * @param lineId 线路
     * @param stationId 车站
     * @param cardMainType 主卡类型
     * @param cardSubType 子卡类型
     * @param flag 进出站标志
     * @param trafficDate 客流时间
     * @param traffic 进站客流累计数
     */
    private void handleFlowHourMinFive(String lineId, String stationId,
            String cardMainType, String cardSubType, String flag,
            String trafficDate, int traffic) {
        MessageUtil util = new MessageUtil();
        // if(!MessageUtil.isNeeedEmergentTraffic())
        // return;
        try {
            util.handleFlowHourMinFive(PubBuffer.bufferFlowMinFiveEntry,
                    lineId, stationId, cardMainType, cardSubType, flag,
                    trafficDate, traffic);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
