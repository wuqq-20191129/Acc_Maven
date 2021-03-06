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
 * 出站信息
 * 
 * @author zhangjh
 */
public class Message14 extends MessageBase {

	private static Logger logger = Logger.getLogger(Message14.class.getName());

	// private static final byte[] SYNCONTROL = new byte[0];

	@Override
	public void run() throws Exception {
		String result = FrameLogConstant.RESULT_HDL_SUCESS;
		level = FrameLogConstant.LOG_LEVEL_INFO;
		try {
			hdlStartTime = System.currentTimeMillis();
			logger.info("--处理14消息开始--");
			process();
			logger.info("--处理14消息结束--");
			hdlEndTime = System.currentTimeMillis();
		} catch (Exception e) {
			result = FrameLogConstant.RESULT_HDL_FAIL;
			hdlEndTime = System.currentTimeMillis();
			level = FrameLogConstant.LOG_LEVEL_ERROR;
			remark = e.getMessage();
			throw e;
		} finally {// 记录处理日志
			LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_FLOW_EXIT,
					messageFrom, hdlStartTime, hdlEndTime, result, threadNum,
					level, remark, getCmDbHelper());
		}
	}

	public void process() throws Exception {

		// logger.info(thisClassName + " started! " + messageSequ);
		// synchronized (SYNCONTROL) {
		try {
			String currentTod = getBcdString(2, 7);
			boolean result = MessageCommonUtil.isNomalDateMessageForTraffic(
					DateHelper.convertStandFormat(currentTod),
					DateHelper.datetimeToStringByDate(new Date()));
			// 不处理滞留数据超过设定天数的客流数据2012-04-16 hejj
			if (!result) {
				String errorMsg = "出站客流数据时间为：" + currentTod
						+ ",距当前日期间隔超过设定的最大值"
						+ FrameCodeConstant.trafficDelayMaxDay + "天,消息将不处理.";
				logger.info(errorMsg);
				logger.error(errorMsg);
				return;

			}

			String stationId = getCharString(9, 4);
			String lineId = stationId.substring(0, 2);
			String cstationId = stationId.substring(2, 4);
			String generateTime = getBcdString(13, 7);
			int totalRepeatCount = getInt(20);
			// logger.info("车站ID是 : " + stationId);
			// logger.info("客流重复次数 : " + totalRepeatCount);

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
				int accumulatedTrafficCount = getLong(j);
				j = j + 4;
				sqlStr = " insert into "+FrameDBConstant.COM_COMMU_P+"cm_traffic_detail(message_sequ,line_id,station_id,flag,traffic_datetime,card_main_code,card_sub_code,traffic) values(?,?,?,?,?,?,?,?)";
				values.clear();
				values.add(messageSequ);
				values.add(lineId);
				values.add(cstationId);
				values.add(FrameCodeConstant.FLAG_Exit);
				values.add(trafficDatetime);
				values.add(ticketMainType);
				values.add(tickerSubType);
				values.add(Integer.valueOf(accumulatedTrafficCount));
				// logger.info("票卡 " + ticketMainType + tickerSubType +
				// " 的出站客流 : " + accumulatedTrafficCount);
				getCmDbHelper().executeUpdate(sqlStr, values.toArray());

				totalTraffic = totalTraffic + accumulatedTrafficCount;

				// handleFlowHour(lineId,cstationId,ticketMainType,tickerSubType,MessageUtil.FLAG_Exit,generateTime,accumulatedTrafficCount);
				// 处理5分钟客流
				handleFlowHourMinFive(lineId, cstationId, ticketMainType,
						tickerSubType, FrameCodeConstant.FLAG_Exit,
						generateTime, accumulatedTrafficCount);
			}

			sqlStr = " insert into "+FrameDBConstant.COM_COMMU_P+"cm_traffic(message_sequ,line_id,station_id,traffic_datetime,traffic_type,invalid_ticket,refuse_ticket,blacklist_ticket,total_traffic) values(?,?,?,?,?,?,?,?,?)";
			values.clear();
			values.add(messageSequ);
			values.add(lineId);
			values.add(cstationId);
			values.add(trafficDatetime);
			values.add(FrameCodeConstant.FLAG_Exit);
			values.add(0);// new Integer(invalidExitDetected));
			values.add(0);// new Integer(ticketRefuseCount));
			values.add(0);// new Integer(blackListCount));
			values.add(new Integer(totalTraffic));

			getCmDbHelper().executeUpdate(sqlStr, values.toArray());
			getCmDbHelper().commitTran();
			getCmDbHelper().setAutoCommit(true);
            //add by zhongzq 用于tcc转发 20190605 生产者
            if (FrameCodeConstant.TCC_INTERFACE_USE_KEY.equals(FrameCodeConstant.TCC_INTERFACE_CONTROL)) {
                TccBuffer.TCC_MESSAGE_QUEUE.offer(this.data);
				Object[] paras = {"14", messageSequ, lineId, cstationId};
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
			logger.error("出站客流消息处理异常：", e);
			throw e;
		}
		// }
		// logger.info(thisClassName + " ended!");
	}

	private void handleFlowHourMinFive(String lineId, String stationId,
			String cardMainType, String cardSubType, String flag,
			String trafficDate, int traffic) {
		MessageUtil util = new MessageUtil();
		// if(!MessageUtil.isNeeedEmergentTraffic())
		// return;
		try {
			util.handleFlowHourMinFive(PubBuffer.bufferFlowMinFiveExit, lineId,
					stationId, cardMainType, cardSubType, flag, trafficDate,
					traffic);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
