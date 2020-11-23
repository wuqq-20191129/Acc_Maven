package com.goldsign.commu.app.message;

import java.util.Vector;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.goldsign.commu.app.vo.DeviceEvent;
import com.goldsign.commu.app.dao.Message11Dao;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;

/**
 * 设备事件信息
 * 
 * @author zhangjh
 */
public class Message11 extends MessageBase {

	private static Logger logger = Logger.getLogger(Message11.class.getName());

	// private static final byte[] SYNCONTROL = new byte[0];

	@Override
	public void run() throws Exception {
		String result = FrameLogConstant.RESULT_HDL_SUCESS;
		this.level = FrameLogConstant.LOG_LEVEL_INFO;

		try {
			this.hdlStartTime = System.currentTimeMillis();
			logger.info("--处理11消息开始--");
			this.process();
			logger.info("--处理11消息结束--");
			this.hdlEndTime = System.currentTimeMillis();
		} catch (Exception e) {
			result = FrameLogConstant.RESULT_HDL_FAIL;
			this.hdlEndTime = System.currentTimeMillis();
			this.level = FrameLogConstant.LOG_LEVEL_ERROR;
			this.remark = e.getMessage();
			throw e;
		} finally {// 记录处理日志
			LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_DEV_EVENT,
					this.messageFrom, this.hdlStartTime, this.hdlEndTime,
					result, this.threadNum, this.level, this.remark,
					this.getCmDbHelper());
		}
	}

	public void process() throws Exception {

		// logger.info(thisClassName + " started!");
		// synchronized (SYNCONTROL) {
		byte[] msg = data;
		String strRead = new String(msg, "ISO-8859-1");
		strRead = String.copyValueOf(strRead.toCharArray(), 0, msg.length);
		
		char[] b = getLineCharFromFile(strRead);
		int offset = 0;
		try {
			int len = 2;// 消息类型
			getCharString(b, offset, len);// 站点代码
			offset += len;

			len = 7;// 消息类型
			getBcdString(b, offset, len);
			offset += len;

			len = 9;// 车站设备
			String device = getCharString(b, offset, len);// 站点代码
			offset += len;

			len = 1;//
			int repeatCount = getInt(b, offset);
			offset += len;

			// String currentTod = getBcdString(2, 7);
			// String device = getCharString(9, 9);
			// int repeatCount = getInt(18);b

			logger.info("offset:" + offset);

			Vector<DeviceEvent> eventV = getDeviceEvent(b, repeatCount, offset,
					device);

			Message11Dao.recordDeviceEvent(getCmDbHelper(), eventV);
		} catch (SQLException e) {
			logger.error("数据库异常,错误代码" + e.getErrorCode() + " 消息.", e);
			throw e;
		} catch (Exception e) {
			logger.error(thisClassName + " error! messageSequ:" + messageSequ
					+ ". ", e);
			throw e;
		}
		// }

		// logger.error(thisClassName + " ended!");
	}

	private Vector<DeviceEvent> getDeviceEvent(char[] b, int repeatCount,
			int offset, String device) throws Exception {
		Vector<DeviceEvent> eventV = new Vector<DeviceEvent>();
		// event loop
		for (int i = 0; i < repeatCount; i++) {
			DeviceEvent event = new DeviceEvent();

			int len = 1;//
			String eventId = getBcdString(b, offset, len);
			offset += len;

			len = 32;
			String eventArgument = getCharString(b, offset, len);// 站点代码
			offset += len;

			len = 7;
			String currentTod = getBcdString(b, offset, len);
			offset = offset + len;

			event.setLineId(device.substring(0, 2));
			event.setStationId(device.substring(2, 4));
			event.setDevTypeId(device.substring(4, 6));
			event.setDeviceId(device.substring(6));
			event.setEventId(eventId);
			event.setEventArgument(eventArgument);
			event.setEventDatetime(DateHelper.dateStrToSqlTimestamp(currentTod));
			eventV.add(event);
		}
		return eventV;
	}

}
