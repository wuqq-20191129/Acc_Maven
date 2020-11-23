package com.goldsign.commu.frame.message;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.vo.BridgeBetweenConnectionAndMessage;
import com.goldsign.lib.db.util.DbHelper;
import com.goldsign.lib.db.util.DbcpHelper;

/**
 * 
 * @author hejj
 */
public abstract class MessageBase {

	private static Logger logger = Logger
			.getLogger(MessageBase.class.getName());

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	protected String messageFrom;
	protected String messageSequ;
	protected String thisClassName = this.getClass().getName();
	protected byte[] data;
	private DbHelper opDbHelper;
	private DbHelper otherDbHelper;
	private DbHelper cmDbHelper;
	// protected DbcpHelper commuDbcpHelper;
	// protected DbcpHelper otherDbcpHelper;
	// protected DbcpHelper logDbcpHelper;
	protected long hdlStartTime; // 处理的起始时间
	protected long hdlEndTime;// 处理的结束时间
	protected String threadNum;// 处理线程号
	protected String level = FrameLogConstant.LOG_LEVEL_INFO;// 日志级别
	protected String remark = "";// 备注
	protected BridgeBetweenConnectionAndMessage bridge;

	/*
	 * public void init(String ip,String sequ,byte[] b,DbcpHelper
	 * dbcp,DbcpHelper dbcp2,String threadNum,BridgeBetweenConnectionAndMessage
	 * bridge) throws Exception{ messageFrom=ip; messageSequ=sequ; data=b;
	 * thisClassName =
	 * thisClassName.substring(thisClassName.lastIndexOf(".")+1,thisClassName
	 * .length()); this.threadNum = threadNum; this.bridge=bridge; try{
	 * commuDbHelper = new DbHelper(thisClassName, dbcp.getConnection());
	 * otherDbHelper = new DbHelper(thisClassName, dbcp2.getConnection());
	 * }catch(SQLException e){ throw e; }
	 * 
	 * //if(thisClassName.equals("Message08")|thisClassName.equals("Message09")|
	 * thisClassName
	 * .equals("Message10")|thisClassName.equals("Message02")|thisClassName
	 * .equals("Message16") ){
	 * 
	 * //} // logger.info(thisClassName+":  "+messageSequ+"  init ended!"); }
	 */

	/*
	 * public DbcpHelper getDbcpHelperForCommu() { return this.commuDbcpHelper;
	 * }
	 * 
	 * public DbcpHelper getDbcpHelperForOther() { return this.otherDbcpHelper;
	 * }
	 * 
	 * public DbcpHelper getDbcpHelperForLog() { return this.logDbcpHelper; }
	 */

	public void init(String ip, String sequ, byte[] b, DbcpHelper dbcp,
			DbcpHelper dbcp2, DbcpHelper dbcp3, String threadNum,
			BridgeBetweenConnectionAndMessage bridge, String messageId)
			throws Exception {
		messageFrom = ip;
		messageSequ = sequ;
		data = b;
		thisClassName = thisClassName.substring(
				thisClassName.lastIndexOf(".") + 1, thisClassName.length());
		this.threadNum = threadNum;
		this.bridge = bridge;
		this.bridge.setMessageProcessor(this);
		this.bridge.setMsgType(messageId);
		try {
			if (dbcp != null) {
				opDbHelper = new DbHelper(thisClassName, dbcp.getConnection());
			}
			if (dbcp2 != null) {
				otherDbHelper = new DbHelper(thisClassName,
						dbcp2.getConnection());
			}
			if (dbcp3 != null) {
				cmDbHelper = new DbHelper(thisClassName, dbcp3.getConnection());
			}

			// this.commuDbcpHelper = dbcp;
			// this.otherDbcpHelper = dbcp2;
			// this.logDbcpHelper = dbcp3;

		} catch (SQLException e) {
			throw e;
		}

		// if(thisClassName.equals("Message08")|thisClassName.equals("Message09")|thisClassName.equals("Message10")|thisClassName.equals("Message02")|thisClassName.equals("Message16")
		// ){

		// }
		// logger.info(thisClassName+":  "+messageSequ+"  init ended!");
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public abstract void run() throws Exception;

	public void release() {
		try {
			if (getOpDbHelper() != null) {
				getOpDbHelper().closeConnection();
			}
			if (getOtherDbHelper() != null) {
				getOtherDbHelper().closeConnection();
			}
			if (getCmDbHelper() != null) {
				getCmDbHelper().closeConnection();
			}

		} catch (Exception e) {
			getLogger().error(" close Connection error.", e);
		}
	}

	/*
	 * public void releaseForMonitorForSyn(MessageBase mb) { try { synchronized
	 * (mb.getDbcpHelperForCommu()) { this.releaseConnection(commuDbHelper,
	 * "commuDbHelper"); } synchronized (mb.getDbcpHelperForOther()) {
	 * this.releaseConnection(otherDbHelper, "otherDbHelper"); } synchronized
	 * (mb.getDbcpHelperForLog()) { this.releaseConnection(cmDbHelper,
	 * "cmDbHelper"); }
	 * 
	 * 
	 * 
	 * } catch (Exception e) { logger.error("catch one exception",e)(); } }
	 */

	public void releaseForMonitor() {
		try {

			this.releaseConnection(getOpDbHelper(), "opDbHelper");

			this.releaseConnection(getOtherDbHelper(), "otherDbHelper");

			this.releaseConnection(getCmDbHelper(), "cmDbHelper");

		} catch (Exception e) {
			getLogger().error(" close Connection error.", e);
		}
	}

	private boolean isStatusOk(DbHelper dbHelper) {
		if (dbHelper == null) {
			return true;
		}
		boolean isClosed = true;
		try {
			isClosed = dbHelper.isConClosed();
		} catch (Exception e) {
			return true;
		}
		return isClosed;

	}

	public boolean checkClose() {
		boolean isCloseAll = true;
		boolean isCloseForCommu = true;
		boolean isCloseForOther = true;
		boolean isCloseForLog = true;
		isCloseForCommu = this.isStatusOk(this.getOpDbHelper());
		isCloseForOther = this.isStatusOk(this.getOtherDbHelper());
		isCloseForLog = this.isStatusOk(this.getCmDbHelper());
		isCloseAll = isCloseForCommu & isCloseForOther & isCloseForLog;
		return isCloseAll;

	}

	private void releaseConnection(DbHelper dbHelper, String key)
			throws Exception {
		if (dbHelper != null) {
			/*
			 * if (!dbHelper.getAutoCommit()) { logger.error(key +
			 * "连接的事务提交方式为手工,将回滚事务及恢复自动方式."); dbHelper.rollbackTran();
			 * dbHelper.setAutoCommit(true); }
			 */
			dbHelper.closeConnectionForException();
			getLogger().error(key + "关闭连接，释放资源");
		}
	}

	// when use in.read() get an int(byte) for example 152(0x98),run this method
	// to get "98";
	public String byte1ToBcd2(int i) {
		return (new Integer(i / 16)).toString()
				+ (new Integer(i % 16)).toString();
	}

	// when transform one byte for example (byte)0x98,run this method to get
	// "98";
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

	public String getBcdString(int offset, int length) throws CommuException {
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

	public char byteToChar(byte b) {
		return (char) b;
	}

	public String getCharString(int offset, int length) throws CommuException {
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

	public String getCharString(char[] data, int offset, int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(data[offset + i]);
		}
		return sb.toString();
	}

	// when transform one byte for example (byte)0x98,run this method to get
	// 104;
	public int byteToInt(byte b) {
		int i = 0;
		if (b < 0) {
			i = 256 + b;
		} else {
			i = b;
		}
		return i;
	}

	public int getInt(int offset) {
		return byteToInt(data[offset]);
	}

	// when transform one short(two bytes) for example 0x12(low),0x34(high),run
	// this method to get 13330
	public int getShort(int offset) {
		int low = byteToInt(data[offset]);
		int high = byteToInt(data[offset + 1]);
		return high * 16 * 16 + low;
	}

	// when transform one long(two shorts) for example 0x12,0x34,0x56,0x78
	public int getLong(int offset) {
		int low = getShort(offset);
		int high = getShort(offset + 2);
		return high * 16 * 16 * 16 * 16 + low;
	}

	/**
	 * @return the opDbHelper
	 */
	public DbHelper getOpDbHelper() {
		return opDbHelper;
	}

	/**
	 * @return the otherDbHelper
	 */
	public DbHelper getOtherDbHelper() {
		return otherDbHelper;
	}

	/**
	 * @return the cmDbHelper
	 */
	public DbHelper getCmDbHelper() {
		return cmDbHelper;
	}

	public char[] getLineCharFromFile(String line) throws Exception {
		if (line == null || line.length() == 0) {
			return null;
		}
		return line.toCharArray();// 中文时，非原始数据20130811
	}

	public String getBcdString(char[] data, int offset, int length)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < length; i++) {
				sb.append(byte1ToBcd2(data[offset + i]));
			}
		} catch (Exception e) {
			throw new Exception(" " + e);
		}
		return sb.toString();
	}

	public int getInt(char[] data, int offset) {
		return byteToInt(data[offset]);
	}

	private int byteToInt(char b) {
		return (int) b;

	}

}
