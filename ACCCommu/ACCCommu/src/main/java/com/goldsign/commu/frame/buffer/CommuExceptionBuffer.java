package com.goldsign.commu.frame.buffer;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.vo.CommuHandledMessage;

/**
 * 
 * @author hejj
 */
public class CommuExceptionBuffer {

	private static Logger logger = Logger.getLogger(ThreadMonitorBuffer.class
			.getName());
	private static Vector<CommuHandledMessage> jmsUnHandleMsgs = new Vector<CommuHandledMessage>(
			3000, 100);
	private static Vector<CommuHandledMessage> sqlUnHandleMsgs = new Vector<CommuHandledMessage>(
			3000, 100);
	private static final String retCode = "0100";

	public CommuExceptionBuffer() {
	}

	public static void setJmsUnHandleMsgs(String id, byte[] data) {

		if (data == null || data.length == 0) {
			return;
		}
		logger.info("JMS异常消息.消息发送方:" + id + " 消息数据长度:" + data.length);
		Vector<Object> readResult = new Vector<Object>();
		readResult.add(retCode);
		readResult.add(data);
		CommuHandledMessage chm = new CommuHandledMessage(id, readResult);
		synchronized (jmsUnHandleMsgs) {
			jmsUnHandleMsgs.add(chm);
			logger.info("JMS异常消息缓存目前有记录:" + jmsUnHandleMsgs.size() + "条");
		}
	}

	public static Vector<CommuHandledMessage> getJmsUnHandleMsgs() {
		Vector<CommuHandledMessage> ret = new Vector<CommuHandledMessage>(3000,
				100);
		synchronized (jmsUnHandleMsgs) {
			ret.addAll(jmsUnHandleMsgs);
			jmsUnHandleMsgs.clear();
			return ret;
		}
	}

	public static void setSqlUnHandleMsgs(String id, byte[] data) {
		if (data == null || data.length == 0) {
			return;
		}
		logger.info("sql异常消息.消息发送方:" + id + " 消息数据长度:" + data.length);

		Vector<Object> readResult = new Vector<Object>();
		readResult.add(retCode);
		readResult.add(data);
		CommuHandledMessage chm = new CommuHandledMessage(id, readResult);
		synchronized (sqlUnHandleMsgs) {
			sqlUnHandleMsgs.add(chm);
			logger.info("sql异常消息缓存目前有记录:" + sqlUnHandleMsgs.size() + "条");
		}
	}

	public static Vector<CommuHandledMessage> getSqlUnHandleMsgs() {
		Vector<CommuHandledMessage> ret = new Vector<CommuHandledMessage>(3000,
				100);
		synchronized (sqlUnHandleMsgs) {
			ret.addAll(sqlUnHandleMsgs);
			sqlUnHandleMsgs.clear();
			return ret;
		}
	}
}
