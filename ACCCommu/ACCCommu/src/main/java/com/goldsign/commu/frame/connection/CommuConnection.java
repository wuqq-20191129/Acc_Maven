package com.goldsign.commu.frame.connection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.message.Message16;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.ComMessageQueueDao;
import com.goldsign.commu.frame.dao.Message16Dao;
import com.goldsign.commu.frame.dao.ParaInformDtlDao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.manager.CommuThreadManager;
import com.goldsign.commu.frame.manager.MessageQueueManager;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.server.CommuServer;
import com.goldsign.commu.frame.thread.CommuMessageHandleThread;
import com.goldsign.commu.frame.util.CommuThreadUtil;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.vo.BridgeBetweenConnectionAndMessage;
import com.goldsign.commu.frame.vo.CommuConnectionControl;
import com.goldsign.commu.frame.vo.CommuHandledMessage;
import com.goldsign.commu.frame.vo.MessageQueue;
import com.goldsign.commu.frame.vo.ParaInformDtl;
import com.goldsign.commu.frame.vo.SynchronizedControl;

/**
 * 
 * @author hejj
 */
public class CommuConnection implements Runnable {

	private Socket socket;
	private String id;
	private BufferedInputStream in;
	private OutputStream out;
	private boolean readWithThread;
	private int frequency = 1;
	private boolean connecting = true;
	private boolean isWriteDB = true;
	private CommuConnectionControl control = new CommuConnectionControl();
	private boolean isData = true;
	private String resultCode = "";
	private final String SEND = "1";
	private final String IsParaInformMsg = "1";
	private final String ParaInformed = "1";
	private final byte STX_B = (byte) 0xEB;
	private final byte ETX = 0x03;
	private final byte QRY = 0x01;
	private final byte DTA = 0x03;
	private final byte[] HEADER = { STX_B, DTA, 0 };
	private final byte[] QUERY = { STX_B, QRY, 0, 0, 0, ETX };
	private static Logger logger = Logger.getLogger(CommuConnection.class
			.getName());
	private int connID = -1;
	/**
	 * 日志记录使用
	 */
	private long hdlStartTime; // 处理的起始时间
	private long hdlEndTime;// 处理的结束时间
	/*
	 * 连接中的消息队列
	 */
	private Vector<MessageQueue> MSG_QUEUE = new Vector<MessageQueue>();
	private final static SynchronizedControl SYNCONTROL = new SynchronizedControl();
	/*
	 * 非即时退款使用
	 */
	private BridgeBetweenConnectionAndMessage bridge = new BridgeBetweenConnectionAndMessage();
	private int dbId = -1;
	private boolean isNeedUnlock = false;

	public void setIsConnnection(boolean isConnected) {
		this.connecting = isConnected;
	}

	public boolean getIsConnnection() {
		return this.connecting;
	}

	public void setDbId(int id) {
		this.dbId = id;
	}

	public int getDbId() {
		return this.dbId;
	}

	public CommuConnection() {
	}

	public MessageQueue getMessageFromConnectionQueue() {
		MessageQueue mq;

		synchronized (SYNCONTROL) {
			if (MSG_QUEUE.isEmpty()) {
				return null;
			}

			mq = (MessageQueue) MSG_QUEUE.get(0);
			MSG_QUEUE.remove(0);

			return mq;
		}

	}

	public void setMessageInConnectionQueue(MessageQueue mq) {
		synchronized (SYNCONTROL) {
			MSG_QUEUE.add(mq);

		}

	}

	public CommuConnection(Socket aSocket, String aId, int connID)
			throws CommuException {
		socket = aSocket;
		id = aId;

		this.connID = connID;

		frequency = FrameCodeConstant.getMessageFrequency;
		readWithThread = FrameCodeConstant.readWithThread;

		try {
			socket.setSoTimeout(FrameCodeConstant.readOneMessageTimeOut);
			in = new BufferedInputStream(socket.getInputStream());
			out = socket.getOutputStream();
		} catch (SocketException e) {
			logger.error("Socket error - ", e);
			throw new CommuException(e.getMessage());
		} catch (IOException e) {
			logger.error("I/O error - ", e);
			throw new CommuException(e.getMessage());
		}
	}

	@Override
	public void run() {
		int serialNo = -1;
		int serialNo2 = 0;
		byte[] qData;
		MessageQueue message = null;
		// long startTime = 0;
		long endTime;
		long tStartTime;
		Vector<Object> fromReader;
		boolean isMessageFromSysQueue = false;

		try {
			while (connecting) {
				this.hdlStartTime = System.currentTimeMillis();
				tStartTime = System.currentTimeMillis();
				message = null;
				serialNo = serialNo + 1;
				if (serialNo > 255) {
					serialNo = 0; // reset serialNo
				}

				// 从本地连接队列中取消息
				message = this.getMessageFromConnectionQueue();
				isMessageFromSysQueue = false;
				// 如果本地消息队列没有消息再定时从系统的消息队列中取消息

				if (serialNo2 == 0 && message == null) {
					message = getMessageFromQueue();
					if (message != null) {
						isMessageFromSysQueue = true;
					}
				}
				serialNo2 = serialNo2 + 1;
				if (serialNo2 > frequency - 1) {
					serialNo2 = 0; // reset serialNo2
				}
				if (message != null) {
					message = checkMessage(message);
					serialNo2 = 0; // reset 0 if last message not null
				}
				if (message != null) {
					qData = message.getMessage();
					 sendData(qData, serialNo);
					logger.info(id + "- 发送的序列号：[" + serialNo + "] - 发送结果数据到终端"
							+ Arrays.toString(qData));
				} else {
					// 暂时屏蔽测试连接问题
					if (!this.isData) {
						Thread.sleep(FrameCodeConstant.sendQueryWait);
					}
					 sendQuery(serialNo);
					logger.debug(id + "- 发送的序列号[" + serialNo + "] - 发送查询消息到终端");
				}

				fromReader = readMsgFromClient(serialNo);

				// 设置连接及处理消息间的对象值
				this.setBridgeAttribute();
				processReaderResult(this.id, fromReader, this.bridge);
				// 发送消息成功并且连接正常
				if (connecting) {
					if (message != null) {
						// startTime = System.currentTimeMillis();
						// 消息已发送完毕，从正在处理消息队列中删除该消息
						if (isMessageFromSysQueue) {
							// 更新消息队列中消息的处理标志，如是参数下发消息，更新参数下发队列中的标志
							this.postHandle(message, id);
							MessageQueueManager
									.removeMessageFromQueueDoing(message);
						} else {
                                                        String messageCode = "" + (char) message.getMessage()[0] + (char) message.getMessage()[1];
							LogUtil.writeRecvSendLog(
									new Date(System.currentTimeMillis()), id,
									SEND, messageCode, "", message.getMessage(), "0");
						}
					}
				} else {
					if (message != null) {

						// 写发送接收消息日志
                                                String messageCode = "" + (char) message.getMessage()[0] + (char) message.getMessage()[1];
						LogUtil.writeRecvSendLog(
								new Date(System.currentTimeMillis()), id, SEND,
								messageCode, "", message.getMessage(), "1");
					}
					logger.warn(id + " - 对方消息错误，连接将断开!");
				}
				synchronized (this.control) {
					if (this.control.getClosed()) {
						break;
					}
				}
				endTime = System.currentTimeMillis();
				if ((endTime - tStartTime) >= 3000) {
					logger.warn(id + "-接收消息时间超过3秒为：" + (endTime - tStartTime));
				}
				// logger.info("总共运行时间：" + (endTime - tStartTime) +
				// " ms");

			}
		} catch (ClassNotFoundException e) {
			logger.error(id + " - Error,connection will be closed!", e);
			this.hdlEndTime = System.currentTimeMillis();
			// 记录日志
			LogDbUtil.logForDbDetail(
					FrameLogConstant.MESSAGE_ID_SOCKET_EXCHAGE, "",
					this.hdlStartTime, this.hdlEndTime,
					FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread()
							.getName(), FrameLogConstant.LOG_LEVEL_ERROR, e
							.getMessage());

		} catch (InterruptedIOException e) {
			logger.error(id + " - Time out,connection will be closed!", e);
			this.hdlEndTime = System.currentTimeMillis();
			// 记录日志
			LogDbUtil.logForDbDetail(
					FrameLogConstant.MESSAGE_ID_SOCKET_EXCHAGE, "",
					this.hdlStartTime, this.hdlEndTime,
					FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread()
							.getName(), FrameLogConstant.LOG_LEVEL_ERROR, e
							.getMessage());

		} catch (IOException e) {
			logger.error(id, e);
			this.hdlEndTime = System.currentTimeMillis();
			// 记录日志
			LogDbUtil.logForDbDetail(
					FrameLogConstant.MESSAGE_ID_SOCKET_EXCHAGE, "",
					this.hdlStartTime, this.hdlEndTime,
					FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread()
							.getName(), FrameLogConstant.LOG_LEVEL_ERROR, e
							.getMessage());

		} catch (Exception e) {
			logger.error(id + " - Error,connection will be closed!", e);
			this.hdlEndTime = System.currentTimeMillis();
			// 记录日志
			LogDbUtil.logForDbDetail(
					FrameLogConstant.MESSAGE_ID_SOCKET_EXCHAGE, "",
					this.hdlStartTime, this.hdlEndTime,
					FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread()
							.getName(), FrameLogConstant.LOG_LEVEL_ERROR, e
							.getMessage());

		} finally {
			logger.info(id + " - 连接关闭!");
			// 设置连接关闭标志
			this.setIsConnnection(false);
			// 最后处理的消息发送不成功而连接中断，从正在处理消息队列中删除该消息

			if (message != null && isMessageFromSysQueue) {
				MessageQueueManager.removeMessageFromQueueDoing(message);
			}

			try {
				FrameCodeConstant.all_connecting_ip.put(id, "0");
				if (this.isWriteDB) {
					// 写连接关闭日志
					LogUtil.writeConnectLog(
							new Date(System.currentTimeMillis()), id, "2", "");
					// 如果是非即时退款，需对当前连接判断是否有申请退款处理锁，如有需解锁
					// 主要是防止连接没有完整处理非即时退款时中断，如完整处理时，非即时退款完成时会自动解锁
					if (isIsNeedUnlock()) {
						logger.info("非即时退款解锁");
						this.unlock();
					}
					// 处理最后一条消息异常，如果该消息是从系统队列中取出，需从当前消息队列中删除及更新消息的处理标志
					// 如果系统在连接时已更新，由于无法判断，中断点，再更新一遍
					if (message != null && isMessageFromSysQueue) {
						MessageQueueManager
								.removeMessageFromQueueDoing(message);
						// 更新消息队列中消息的处理标志，如是参数下发消息，更新参数下发队列中的标志
						// this.postHandle(message, id);
					}

				}
                                socket.setSoLinger(true, 1000);//Socket关闭后，底层Socket延迟3600秒再关闭
//                                socket.setSoLinger(true, 0);//Socket关闭后，底层Socket立即关闭
				// 关闭客户端连接的socket
				socket.close();

				// 从缓存中删除连接对象
				CommuServer.removeConnection(this.connID);
			} catch (IOException e) {
				logger.error(id + " - close rror! - ", e);
			} catch (Exception e) {
				logger.error(id + " - close rror! - ", e);
			}
		}
	}

	private Vector<Object> readMsgFromClient(int serialNo) throws IOException,
			InterruptedException, ClassNotFoundException {
		Vector<Object> fromReader;
		if (readWithThread) {
			PipedOutputStream pipeout = new PipedOutputStream();
			PipedInputStream pipein = new PipedInputStream(pipeout);
			ConnectionReader cr = new ConnectionReader(in, pipeout, serialNo);
			cr.setPriority(Thread.NORM_PRIORITY
					+ FrameCodeConstant.readThreadPriorityAdd);
			cr.start();
			cr.join(FrameCodeConstant.readOneMessageTimeOut);
			cr.stopReader();
			ObjectInputStream oin = new ObjectInputStream(pipein);
			fromReader = (Vector<Object>) (oin.readObject());
		} else {
			ConnectionReaderNormal cr2 = new ConnectionReaderNormal(in,
					serialNo);
			fromReader = cr2.read();
		}
		return fromReader;
	}

	/**
	 * 即时退款解锁
	 */
	private void unlock() {
		int nid = this.getDbId();
		logger.info("非即时退款解锁 nid=" + nid);
		MessageBase mp = this.bridge.getMessageProcessor();
		if (mp == null) {
			return;
		}
		Message16 mp16 = (Message16) mp;
		String applyBill = mp16.getApplyBill();
		logger.info("非即时退款解锁 applyBill=" + applyBill);
		if (applyBill == null || applyBill.length() == 0) {
			return;
		}
		Message16Dao dao = new Message16Dao();
		dao.unLock(nid, applyBill);

	}

	private void setBridgeAttribute() {

		this.bridge.setConnection(this);

	}

	private void postHandle(MessageQueue message, String id) {
		try {
			if (message != null) {
				updateMessageFlag(message);
				if ((message.getIsParaInformMsg()).equals(IsParaInformMsg)) {
					updateParaInformFlag(message);
				}
                                String messageCode = "" + (char) message.getMessage()[0] + (char) message.getMessage()[1];    
				LogUtil.writeRecvSendLog(new Date(System.currentTimeMillis()),
						id, SEND, messageCode, "", message.getMessage(), "0");
			}
		} catch (Exception e) {
			logger.error(id + " - ", e);
		}

	}

	public void processReaderResult(String id, Vector<Object> aReaderResult,
			BridgeBetweenConnectionAndMessage brigde) {
		CommuHandledMessage msg;
		CommuThreadManager manager;
		CommuMessageHandleThread handleThread;
		CommuThreadUtil cmht = new CommuThreadUtil();

		resultCode = (String) (aReaderResult.get(0));
		if (resultCode.substring(0, 1).equals("0")) {
			connecting = true;
			if (resultCode.equals("0100")) {
				isData = true;

				msg = new CommuHandledMessage(id, aReaderResult, bridge);
				manager = new CommuThreadManager();
				// 降级模式，客流信作单线程处理（主要考虑数据库性能低下时，影响整体数据库查询速度，如数据库性能较好，客流采用多线称处理较好）
				// if(cmht.isDegradeMode(msg)||cmht.isTraffic(msg))
				if (cmht.isDegradeMode(msg)) {
					handleThread = manager.getDegradeMsgHandleThread();
				} else {
					// 同IP的非即时退款的消息,挂失及客流消息放到同一队列处理
                                        // 20150520添加挂失内容 by lindaquan v0.74
					if (cmht.isTraffic(msg) || cmht.isNonReturnMsg(msg) || cmht.isLossReportMsg(msg)) {
						handleThread = manager.getTrafficHandleThread(msg);
					} else {
						handleThread = manager.getNextMsgHandleThread();
					}
				}

				logger.info("当前消息处理线程名称:" + handleThread.getName());
				handleThread.setHandlingMsg(msg);
				// this.addReadedCount();

			} else {
				isData = false;
			}
		} else {
			connecting = false;
			logger.info(id + " - Reader return message error( " + resultCode
					+ ")!Connection will be closed!");
		}
	}

	public void processReaderResult(String id, Vector<Object> aReaderResult) {
		CommuHandledMessage msg;
		CommuThreadManager manager;
		CommuMessageHandleThread handleThread;
		CommuThreadUtil cmht = new CommuThreadUtil();

		// resultCode = (String) (aReaderResult.get(0));
		resultCode = (String) (aReaderResult.get(0));
		if (resultCode.substring(0, 1).equals("0")) {
			connecting = true;
			if (resultCode.equals("0100")) {
				isData = true;

				msg = new CommuHandledMessage(id, aReaderResult, bridge);
				manager = new CommuThreadManager();
				// 降级模式，客流信作单线程处理（主要考虑数据库性能低下时，影响整体数据库查询速度，如数据库性能较好，客流采用多线称处理较好）
				// if(cmht.isDegradeMode(msg)||cmht.isTraffic(msg))
				if (cmht.isDegradeMode(msg)) {
					handleThread = manager.getDegradeMsgHandleThread();
				} else {
					if (cmht.isTraffic(msg)) {
						handleThread = manager.getTrafficHandleThread(msg);
					} else {
						handleThread = manager.getNextMsgHandleThread();
					}
				}
				handleThread.setHandlingMsg(msg);
			} else {
				isData = false;
			}
		} else {
			connecting = false;
			logger.info(id + " - Reader return message error( " + resultCode
					+ ")!Connection will be closed!");
		}
	}

	public MessageQueue getMessageFromQueue() {
		logger.debug(id + " - getDataFromQueue.");
		return MessageQueueManager.getMessageQueue(id);
	}

	public void closeConnection() {
		synchronized (this.control) {
			this.control.setClosed(true);

		}
		this.isWriteDB = false;

	}

	private MessageQueue checkMessage(MessageQueue mq) {
		byte[] data = mq.getMessage();
		try {
			if (data != null) {
				if (data.length > 65535) {
					logger.info("Message length longer than 65536 bytes.");
					updateMessageFlag(mq);
                                        String messageCode = "" + (char) data[0] + (char) data[1];
					LogUtil.writeRecvSendLog(
							new Date(System.currentTimeMillis()), id, "1", messageCode,
							"", mq.getMessage(), "2");
					mq = null;
				}
				if (data.length == 0) {
					logger.info("Message length is 0.");

					// updateMessageFlag(mq);
					this.updateMessageFlag(mq);
					LogUtil.writeRecvSendLog(
							new Date(System.currentTimeMillis()), id, "1", "",
							"", mq.getMessage(), "2");
					mq = null;
				}
			} else {
				logger.info("Message is null.");
				updateMessageFlag(mq);
				LogUtil.writeRecvSendLog(new Date(System.currentTimeMillis()),
						id, "1", "", "", null, "2");
				mq = null;
			}
		} catch (Exception e) {
			return null;
		}
		return mq;
	}

	public void updateMessageFlag(MessageQueue mq) {
		// logger.info(id +
		// " - updateFlagQueue which message id is : " +
		// mq.getMessageId());
		ComMessageQueueDao queueDAO = new ComMessageQueueDao();
		queueDAO.updateFlagQueue(mq.getMessageId());
	}

	private void updateParaInformFlag(MessageQueue mq) {
		ParaInformDtl dtl = new ParaInformDtl();
		dtl.setWaterNo(mq.getParaInformWaterNo());
		dtl.setLineId(mq.getLineId());
		dtl.setStationId(mq.getStationId());
		dtl.setInformResult(ParaInformed);
		try {
			// ParaInformDtlDao dao = new ParaInformDtlDao();
			ParaInformDtlDao dao = new ParaInformDtlDao();
			dao.updateResult(dtl);
		} catch (Exception e) {
			logger.error(id + " - updateParaInformFlag error!", e);
		}
	}

	private void sendData(byte[] b, int aSerialNo) throws IOException {
		HEADER[2] = (byte) aSerialNo;
		out.write(HEADER);
		out.write((byte) ((b.length) % 256));
		out.write((byte) ((b.length) / 256));
		out.write(b);
		out.write(ETX);
	}

	private void sendQuery(int aSerialNo) throws IOException {
		QUERY[2] = (byte) aSerialNo;
		out.write(QUERY);
	}

	/**
	 * @return the isNeedUnlock
	 */
	public boolean isIsNeedUnlock() {
		return isNeedUnlock;
	}

	/**
	 * @param isNeedUnlock
	 *            the isNeedUnlock to set
	 */
	public void setIsNeedUnlock(boolean isNeedUnlock) {
		this.isNeedUnlock = isNeedUnlock;
	}
}
