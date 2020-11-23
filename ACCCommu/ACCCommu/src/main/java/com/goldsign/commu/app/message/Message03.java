package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.Message03Dao;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * 参数版本应用报告 LCC->ACC
 * 
 * @author zhangjh
 */
public class Message03 extends MessageBase {

	private static Logger logger = Logger.getLogger(Message03.class.getName());
//	private static final byte[] SYNCONTROL = new byte[0];

	@Override
	public void run() throws Exception {
		String result = FrameLogConstant.RESULT_HDL_SUCESS;
		this.level = FrameLogConstant.LOG_LEVEL_INFO;
		try {
			this.hdlStartTime = System.currentTimeMillis();
			logger.info("--处理03消息开始--");
			this.process();
			logger.info("--处理03消息结束--");
			this.hdlEndTime = System.currentTimeMillis();
		} catch (Exception e) {
			result = FrameLogConstant.RESULT_HDL_FAIL;
			this.hdlEndTime = System.currentTimeMillis();
			this.level = FrameLogConstant.LOG_LEVEL_ERROR;
			this.remark = e.getMessage();
			throw e;
		} finally {// 记录处理日志
			LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_PARAM_REPLY,
					this.messageFrom, this.hdlStartTime, this.hdlEndTime,
					result, this.threadNum, this.level, this.remark, this.getCmDbHelper());
		}
	}

	public void process() throws Exception {

		// synchronized (SYNCONTROL) {
		try {
			String currentTod = getBcdString(2, 7);
			logger.info("生成时间currentTod:"+currentTod);
			String station = getCharString(9, 4);
			String lineId = station.substring(0, 2);
			String stationId = station.substring(2, 4);
			String parmTypeId = getBcdString(13, 2);
			String verNum = getBcdString(15, 5);
			String status = getCharString(20, 1);

			Object[] values = new Object[6];
			values[0] = DateHelper.dateStrToSqlTimestamp(currentTod);
			logger.info("生成时间date:"+values[0]);
			values[1] = lineId;
			values[2] = stationId;
			values[3] = parmTypeId;
			values[4] = verNum;
			values[5] = status;

			Message03Dao.insert(getOpDbHelper(), values);

		} catch (SQLException e) {
			logger.error("数据库异常,错误代码" + e.getErrorCode() + " 消息", e);
			throw e;
		} catch (Exception e) {
			logger.error(thisClassName + " error! messageSequ:" + messageSequ
					+ ". ", e);
			throw e;
		}
	}

	// }
}
