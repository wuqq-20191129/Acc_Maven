package com.goldsign.commu.app.message;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.util.PubUtil;

/**
 * TVM SJT赋值信息
 *
 * @author zhangjh
 */
public class Message15 extends MessageBase {

    private static Logger logger = Logger.getLogger(Message15.class.getName());
//	private static final byte[] SYNCONTROL = new byte[0];
    private final static String sqlStr = " insert into "+FrameDBConstant.COM_COMMU_P+"cm_traffic_tvm(message_sequ,traffic_datetime,line_id,station_id,card_main_code,card_sub_code,traffic) values(?,?,?,?,?,?,?)";

    @Override
    public void run() throws Exception {
        String result = FrameLogConstant.RESULT_HDL_SUCESS;
        this.level = FrameLogConstant.LOG_LEVEL_INFO;
        try {
            this.hdlStartTime = System.currentTimeMillis();
            logger.info("--处理15消息开始--");
            this.process();
            logger.info("--处理15消息结束--");
            this.hdlEndTime = System.currentTimeMillis();
        } catch (Exception e) {
            result = FrameLogConstant.RESULT_HDL_FAIL;
            this.hdlEndTime = System.currentTimeMillis();
            this.level = FrameLogConstant.LOG_LEVEL_ERROR;
            this.remark = e.getMessage();
            throw e;
        } finally {// 记录处理日志
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_TVM_SJT,
                    this.messageFrom, this.hdlStartTime, this.hdlEndTime,
                    result, this.threadNum, this.level, this.remark, this.getCmDbHelper());
        }
    }

    public void process() throws Exception {

        // logger.info(thisClassName + " started!");
        // synchronized (SYNCONTROL) {
        try {
            getBcdString(2, 7);
            String station = getCharString(9, 4);
            String lineId = station.substring(0, 2);
            String stationId = station.substring(2, 4);
            String generateTime = getBcdString(13, 7);
            int totalRepeatCount = getInt(20);
            // logger.info("车站ID是 : " + station);
            getCmDbHelper().setAutoCommit(false);
            ArrayList<Object> values = new ArrayList<Object>();
            java.sql.Timestamp trafficDatetime = DateHelper
                    .dateStrToSqlTimestamp(generateTime);
            int j = 21;
            for (int i = 0; i < totalRepeatCount; i++) {
                this.messageSequ = PubUtil.getMessageSequ();

                values.clear();
                values.add(messageSequ);
                values.add(trafficDatetime);
                values.add(lineId);
                values.add(stationId);

                String ticketMainType = getBcdString(j, 1);
                values.add(ticketMainType);
                j++;
                String ticketSubType = getBcdString(j, 1);
                values.add(ticketSubType);
                j++;
                int accumulatedTrafficCount = getLong(j);
                values.add(new Integer(accumulatedTrafficCount));
                j = j + 4;
                // logger.info("票卡 " + ticketMainType + ticketSubType +
                // " 的数量 : " + accumulatedTrafficCount);
                getCmDbHelper().executeUpdate(sqlStr, values.toArray());
            }
            getCmDbHelper().commitTran();
            getCmDbHelper().setAutoCommit(true);
        } catch (SQLException e) {
            getOpDbHelper().rollbackTran();
            getOpDbHelper().setAutoCommit(true);
            logger.error("数据库异常,错误代码" + e.getErrorCode() + " 消息", e);
            throw e;
        } catch (Exception e) {
            getOpDbHelper().rollbackTran();
            getOpDbHelper().setAutoCommit(true);
            logger.error(thisClassName + " error! messageSequ:" + messageSequ
                    + ". ", e);
            throw e;
        }
    }
    // }
}
