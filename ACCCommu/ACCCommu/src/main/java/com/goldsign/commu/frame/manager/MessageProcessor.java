package com.goldsign.commu.frame.manager;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.goldsign.commu.frame.buffer.CommuExceptionBuffer;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.BridgeBetweenConnectionAndMessage;
import com.goldsign.lib.db.util.DbcpHelper;

/**
 * 
 * @author hejj
 */
public class MessageProcessor {

	private Vector<Object> v;
	private String ip;
	private String threadNum;// 线程号
	private BridgeBetweenConnectionAndMessage bridge;// 连接与消息处理器间的桥
	private static Logger logger = Logger.getLogger(MessageProcessor.class
			.getName());

	public MessageProcessor(String aId, Vector<Object> aV) {
		this.ip = aId;
		this.v = aV;
	}

	public MessageProcessor(String aId, Vector<Object> aV, String threadNum,
			BridgeBetweenConnectionAndMessage bridge) {
		this.ip = aId;
		this.v = aV;
		this.threadNum = threadNum;
		this.bridge = bridge;

	}

	public void  runMsg(Vector<MessageBase> msgInProcess) {
		logger.info("runMsg begin ...");
		String messageId = null;
		String messageSequ = null;
		MessageBase msg = null;
		byte[] data = null;
		// DbcpHelper opHelper = FrameDBConstant.COMMU_DBCPHELPER;
		// DbcpHelper dbcpHelper_other = FrameDBConstant.OTHER_DBCPHELPER;
		DbcpHelper opHelper = FrameDBConstant.OP_DBCPHELPER;
		DbcpHelper dbcpHelper_other = null;
		DbcpHelper cmHelper = FrameDBConstant.CM_DBCPHELPER;
		try {
			data = (byte[]) (v.get(1)); 
			messageId = "" + (char) data[0] + (char) data[1];
			logger.info("请求的消息类型：" + messageId);
			messageSequ = PubUtil.getMessageSequ();
			// 当客流消息及车站设备状态消息时，使用客流状态数据库的连接池
			if (this.isFlowMessage(messageId)) {
				// opHelper = FrameDBConstant.FLOWSTATUS_DBCPHELPER;
				// opHelper = null;////modify by hejj 客流/状态消息不使用data数据库
			} else if (this.isFileUploadMessage(messageId)) {// 文件上传消息,仅使用log,不使用数据/客流状态数据库
				opHelper = null;
				dbcpHelper_other = null;
			} else {
				dbcpHelper_other = null;// 其他,仅使用data/log数据库,不使用客流状态数据库
			}
//			String messageClass = (String) FrameCodeConstant.MESSAGE_CLASSES
//					.get(messageId);
//			msg = (MessageBase) Class.forName(
//					FrameCodeConstant.messageClassPrefix + messageClass)
//					.newInstance();
                        msg = (MessageBase) Class.forName(
					FrameCodeConstant.messageClassPrefix + messageId)
					.newInstance();
			msg.init(ip, messageSequ, data, opHelper, dbcpHelper_other,
					cmHelper, this.threadNum, this.bridge, messageId);
			// 返回正在处理的消息，以便是否手工释放连接资源
			msgInProcess.clear();
			msgInProcess.add(msg);
			msg.run();

			LogUtil.writeRecvSendLog(new Date(System.currentTimeMillis()), ip,
					"0", messageId, messageSequ, data, "0", msg.getCmDbHelper());
			// 统计正常处理的消息包
			// this.addHandledCount();

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			// LogUtil.writeRecvSendLog(new
			// Date(System.currentTimeMillis()),ip,"0",messageId,messageSequ,data,"1");
			// 连接数据库异常

                        if (e.getErrorCode() == 0) {
				logger.error("捕获连接数据库异常:" + "错误代码 " + e.getErrorCode() + " 消息"
						+ e.getMessage() + "消息将放入缓存恢复连接时处理");
				CommuExceptionBuffer.setSqlUnHandleMsgs(this.ip, data);
				// 统计异常处理的消息包
				// this.addExHandledCount();
			} else {
				try {
					if (null != msg) {
						LogUtil.writeRecvSendLog(
								new Date(System.currentTimeMillis()), ip, "0",
								messageId, messageSequ, data, "1",
								msg.getCmDbHelper());
					}

				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				if (null != msg) {
					LogUtil.writeRecvSendLog(
							new Date(System.currentTimeMillis()), ip, "0",
							messageId, messageSequ, data, "1",
							msg.getCmDbHelper());
				}

				logger.error("Invoke message(id=" + messageId
						+ ") error! Message sequ:" + messageSequ + "."
						+ e.getMessage());
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		} finally {
			if (null != msg) {
				msg.release();
			}
		}

	}

	private boolean isFlowMessage(String messageID) {
		if (messageID == null) {
			return false;
		}
		if (messageID.equals("08") || messageID.equals("09")
				|| messageID.equals("10") || messageID.equals("11")
				|| messageID.equals("13") || messageID.equals("14")
				|| messageID.equals("15")) {
			return true;
		}
		return false;
	}

	private boolean isFileUploadMessage(String messageID) {
		if (messageID == null) {
			return false;
		}
		if (messageID.equals("12")) {
			return true;
		}
		return false;
	}
}
