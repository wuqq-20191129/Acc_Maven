package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameThreadPoolConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.manager.MessageProcessor;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.CommuThreadLogUtil;
import com.goldsign.commu.frame.util.CommuThreadUtil;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.MessageCommonUtil;
import com.goldsign.commu.frame.util.ThreadUtil;
import com.goldsign.commu.frame.vo.CommuHandledMessage;
import com.goldsign.commu.frame.vo.SynchronizedControl;
import com.goldsign.commu.frame.vo.TMThreadMsg;
import com.goldsign.commu.frame.vo.TMThreadStatusVo;
import java.util.Vector;

import java.util.Date;
import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class CommuMessageHandleThread extends Thread {

	private Logger logger = Logger
			.getLogger("com.goldsign.iccs.commu.thread.CommuMessageHandleThread");
	private final SynchronizedControl CONTROL_MSGS = new SynchronizedControl();
	private final SynchronizedControl CONTROL_MSGS_PRIORITY = new SynchronizedControl();
	private Vector<CommuHandledMessage> msgs = new Vector<CommuHandledMessage>(
			FrameCodeConstant.threadBufferCapacity,
			FrameCodeConstant.threadBufferIncrement);
	private Vector<CommuHandledMessage> priorityMsgs = new Vector<CommuHandledMessage>(
			FrameCodeConstant.priorityThreadBufferCapacity,
			FrameCodeConstant.priorityThreadBufferIncrement);
	private boolean isHandling = false;
	private CommuHandledMessage handlingMsg = null;
	// private CommuHandledMessage handlingPriorityMsg = null;
	private MessageProcessor mp = null;
	private TMThreadStatusVo threadStatusStart = new TMThreadStatusVo();// 线程处理开始状态
	private TMThreadStatusVo threadStatusEnd = new TMThreadStatusVo();// 线程处理完成状态
	private boolean isSynForAll = false;// 控制本线程与线程池的其他线程同步，如当前消息处理完了，就停止处理其他消息
	private Vector<MessageBase> msgInProcess = new Vector<MessageBase>();
	private boolean isSyningForAll = false;// 进入与其他线程同步状态

	public CommuMessageHandleThread() {
	}

	@Override
	public void run() {

		while (true) {
			try {

				handlingMsg = this.getHandingMsg();

				if (handlingMsg == null) {
					// logger.info(this.getName() + "没有需要处理的消息");
					CommuMessageHandleThread
							.sleep(FrameCodeConstant.threadSleepTime);
					continue;
				}
				logger.info("线程名[" + this.getName() + "]有需要处理的消息");
				this.isHandling = true;
				// 获取线程处理开始状态及记录状态
				this.updateThreadStatusForStart();

				mp = new MessageProcessor(handlingMsg.getIp(),
						handlingMsg.getReadResult(), this.getName(),
						handlingMsg.getBridge());
				// mp.run();

				mp.runMsg(msgInProcess);
				// 处理连接池用尽问题
				if (this.isSynForAll) {
					this.isSyningForAll = true;
					logger.info("线程" + this.getName() + "将暂停处理其他消息，保持与其他线程同步");
					this.waitToSyn();
				}

				this.isHandling = false;
				// 获取线程处理完成状态及记录状态
				this.updateThreadStatusForEnd();
				CommuMessageHandleThread.sleep(FrameCodeConstant.threadSleepTime);

			} catch (InterruptedException e) {
				logger.info("线程" + this.getName() + "被中断.........");
				// 线程异常中断时,将线程未处理的消息写入未处理文件中
				// CommuThreadLog.writeUnHandleMsgToFile(this.getName(),msgs);
				break;

			} catch (Exception e) {
				this.updateThreadStatusForEndException();// 获取线程处理完成状态及记录状态
				// 线程处理消息出现异常时,将处理的消息写入处理异常文件中
				CommuThreadLogUtil.writeErrHandleMsgToFile(this.getName(),
						handlingMsg.getReadResult());
				logger.error(this.getName() + ":消息缓存未处理消息为" + this.msgs.size(),
						e);
			}
		}
	}

	public boolean getIsSynForAll() {
		return this.isSynForAll;
	}

	public void setIsSynForAll(boolean isSynForAll) {
		this.isSynForAll = isSynForAll;
	}

	public boolean getIsSyningForAll() {
		return this.isSyningForAll;
	}

	public void setIsSyningForAll(boolean isSyningForAll) {
		this.isSyningForAll = isSyningForAll;
	}

	public Vector<CommuHandledMessage> getMsgs() {
		synchronized (this.CONTROL_MSGS) {
			return this.msgs;
		}

	}

	public Vector<CommuHandledMessage> getAllMsgs() {
		Vector<CommuHandledMessage> allMsgs = new Vector<CommuHandledMessage>(
				FrameCodeConstant.threadBufferCapacity
						+ FrameCodeConstant.priorityThreadBufferCapacity,
				FrameCodeConstant.threadBufferIncrement);
		synchronized (this.CONTROL_MSGS) {
			synchronized (this.CONTROL_MSGS_PRIORITY) {
				allMsgs.addAll(this.priorityMsgs);
				this.priorityMsgs.clear();
				allMsgs.addAll(this.msgs);
				this.msgs.clear();
				return allMsgs;

			}

		}

	}

	public CommuHandledMessage getHandlingMsg() {
		return this.handlingMsg;
	}

	public boolean isHandlingMsg() {
		return this.isHandling;

	}

	public CommuHandledMessage getMsg() {
		CommuHandledMessage msg = null;

		synchronized (this.CONTROL_MSGS) {
			if (msgs.isEmpty()) {
				return null;
			}
			msg = (CommuHandledMessage) msgs.get(0);
			msgs.remove(0);

		}
		logger.info(this.getName() + ":left msgs =" + msgs.size());

		return msg;

	}

	public void setMsg(CommuHandledMessage msg) {
		synchronized (this.CONTROL_MSGS) {
			msgs.add(msg);
		}
	}

	public CommuHandledMessage getPriorityMsg() {
		CommuHandledMessage msg = null;

		synchronized (this.CONTROL_MSGS_PRIORITY) {
			if (priorityMsgs.isEmpty()) {
				return null;
			}
			msg = (CommuHandledMessage) priorityMsgs.get(0);
			priorityMsgs.remove(0);

		}

		// logger.info(this.getName()+":left msgs ="+msgs.size());

		return msg;

	}

	private CommuHandledMessage getHandingMsg() {
		CommuHandledMessage msg = null;
		synchronized (this.CONTROL_MSGS_PRIORITY) {
			if (!priorityMsgs.isEmpty()) {// 优先级队列有未处理消息

				msg = (CommuHandledMessage) priorityMsgs.get(0);
				priorityMsgs.remove(0);
				logger.info(this.getName() + ":left priorityMsgs ="
						+ priorityMsgs.size());
				return msg;

			} else// 优先级队列没有未处理消息
			{
				synchronized (this.CONTROL_MSGS) {
					if (msgs.isEmpty()) {
						return null;
					} else {
						msg = (CommuHandledMessage) msgs.get(0);
						msgs.remove(0);
						logger.info(this.getName() + ":left msgs ="
								+ msgs.size());
					}

				}

			}
			return msg;

		}
	}

	public void setHandlingMsg(CommuHandledMessage msg) {
		// 消息是否是优先级消息
		if (this.isPriorityMsg(msg)) {
			synchronized (this.CONTROL_MSGS_PRIORITY) {// 消息加入优先级队列
				priorityMsgs.add(msg);
			}

		} else {
			synchronized (this.CONTROL_MSGS) {// 消息加入普通级队列
				msgs.add(msg);
			}

		}

	}

	public boolean isPriorityMsg(CommuHandledMessage msg) {
		byte[] data = (byte[]) msg.getReadResult().get(1);
		if (data == null || data.length == 0) {
			return false;
		}
		String messageId = "" + (char) data[0] + (char) data[1];
		// 文件接收,参数同步，参数同步结果，非及时退款及降级模式优先处理
                // 20150520添加挂失内容 by lindaquan v0.74
		if (messageId.equals("12") || messageId.equals("01")
				|| messageId.equals("02") || messageId.equals("16") || messageId.equals("21")
				|| this.isDegradeMode(msg)) {
			return true;
		} else {
			return false;
		}

	}

	private String getMsgId(CommuHandledMessage msg) {
		byte[] data = (byte[]) msg.getReadResult().get(1);
		String messageId = "" + (char) data[0] + (char) data[1];
		return messageId;
	}

	public boolean isDegradeMode(CommuHandledMessage msg) {
		byte[] data = (byte[]) msg.getReadResult().get(1);
		String messageId = null;
		String device = null;
		// String lineID = null;
		// String stationID = null;
		String deviceTypeID = null;
		String deviceID = null;
		CommuThreadUtil util = null;
		int repeatCount = -1;
		String statusID = null;
		int iStatusID = -1;
		String status = null;

		int offset = -1;

		if (data == null || data.length == 0) {
			return false;
		}
		messageId = "" + (char) data[0] + (char) data[1];
		if (!messageId.equals("10")) {
			return false;
		}
		util = new CommuThreadUtil();
		try {
			device = util.getBcdString(data, 9, 5);
			repeatCount = util.getInt(data, 17);

			// lineID = device.substring(0, 2);
			// stationID = device.substring(2, 4);
			deviceTypeID = device.substring(4, 6);
			deviceID = device.substring(6, 10);

			offset = 18;
			for (int i = 0; i < repeatCount; i++) {
				status = util.getBcdString(data, offset, 2);
				statusID = status.substring(0, 3);
				iStatusID = Integer.parseInt(statusID);
				if (iStatusID >= 1 && iStatusID <= 6
						&& deviceTypeID.equals("01") && deviceID.equals("0000")) {
					logger.info("优先处理降级模式");
					return true;
				}
				offset += 9;
			}
		} catch (CommuException e) {
			return false;

		} catch (Exception e) {
			return false;

		}

		return false;

	}

	public void setPriorityMsg(CommuHandledMessage priorityMsg) {
		synchronized (this.CONTROL_MSGS_PRIORITY) {
			priorityMsgs.add(priorityMsg);
		}
	}

	private void waitToSyn() {
		try {
			while (true) {
				CommuMessageHandleThread
						.sleep(FrameCodeConstant.threadSleepTime);
				if (!this.isSynForAll) {
					logger.error("线程" + this.getName() + "结束与其他线程同步，开始处理队列消息");
					break;
				}
			}

		} catch (InterruptedException ex) {
			logger.error("catch one exception", ex);
		}
	}

	public void releaseResource() {
		if (this.msgInProcess.isEmpty()) {
			return;
		}
		MessageBase mb = (MessageBase) this.msgInProcess.get(0);
		mb.releaseForMonitor();
	}

	public boolean checkReleaseResource() {
		if (this.msgInProcess.isEmpty()) {
			return true;
		}
		MessageBase mb = (MessageBase) this.msgInProcess.get(0);
		return mb.checkClose();
	}

	private void updateThreadStatusForStart() {
		this.setThreadStatusForStart(this.threadStatusStart);
		ThreadUtil.updateThreadStatus(this.threadStatusStart);
	}

	private void updateThreadStatusForEnd() {
		this.setThreadStatusForEnd(this.threadStatusEnd);
		ThreadUtil.updateThreadStatus(this.threadStatusEnd);
	}

	private void updateThreadStatusForEndException() {
		this.setThreadStatusForEndException(this.threadStatusEnd);
		ThreadUtil.updateThreadStatus(this.threadStatusEnd);
	}

	private void setThreadStatusForStart(TMThreadStatusVo vo) {
		this.setThreadStatusCommon(vo);
		vo.setThreadStatus(FrameThreadPoolConstant.ThreadStatusHandling);
		vo.setHdlTimeStart(DateHelper.datetimeToString(new Date()));
		vo.setHdlEndTime(FrameThreadPoolConstant.ThreadMsgHandleTimeEmpty);

	}

	private void setThreadStatusForEnd(TMThreadStatusVo vo) {
		this.setThreadStatusCommon(vo);
		vo.setThreadStatus(FrameThreadPoolConstant.ThreadStatusFinish);
		vo.setHdlTimeStart("");
		vo.setHdlEndTime(DateHelper.datetimeToString(new Date()));
	}

	private void setThreadStatusCommon(TMThreadStatusVo vo) {
		vo.setThreadId(this.getName());
		vo.setThreadName(this.getName());
		String msgId = this.getMsgId(this.handlingMsg);
		String msgName = ThreadUtil.getMessageName(msgId);
		vo.setMsgId(msgId);
		vo.setMsgName(msgName);
	}

	private void setThreadStatusForEndException(TMThreadStatusVo vo) {
		this.setThreadStatusCommon(vo);
		vo.setThreadStatus(FrameThreadPoolConstant.ThreadStatusFinishException);
		vo.setHdlTimeStart("");
		vo.setHdlEndTime(DateHelper.datetimeToString(new Date()));
	}

	public void setPriorityMsgs(Vector<CommuHandledMessage> priorityMsgs) {
		synchronized (this.CONTROL_MSGS_PRIORITY) {
			this.priorityMsgs.addAll(priorityMsgs);
		}

	}

	public void setMsgs(Vector<CommuHandledMessage> msgsOrd) {
		synchronized (this.CONTROL_MSGS) {
			this.msgs.addAll(msgsOrd);
		}

	}

	public Vector<CommuHandledMessage> getPriorityMsgs() {
		synchronized (this.CONTROL_MSGS_PRIORITY) {
			return this.priorityMsgs;
		}

	}

	public Vector<CommuHandledMessage> delHandupMsgs(TMThreadMsg vo) {

		if (MessageCommonUtil.isInPriorityMsg(vo.getMsgId())) {
			return this.delHandupMsgsForPriority(vo);
		} else {
			return this.delHandupMsgsForOrdinary(vo);
		}

	}

	private boolean isNeedDel(CommuHandledMessage commuMsg, TMThreadMsg vo) {
		String msgIdDel = vo.getMsgId();
		String msgId = this.getMsgId(commuMsg);
		if (!msgId.equals(msgIdDel)) {
			return false;
		}
		TMThreadMsg msgKey = ThreadUtil.getMessageKeyInfo(commuMsg);
		if (msgKey == null) {
			return false;
		}
		if (!msgKey.getLineId().equals(vo.getLineId())) {
			return false;
		}
		String stationIdDel = vo.getStationId();
		String stationId = msgKey.getStationId();
		if (stationIdDel != null && stationId != null) {
			if (!stationIdDel.equals(stationId)) {
				return false;
			}
		}
		return true;

	}

	public Vector<CommuHandledMessage> delHandupMsgsForPriority(TMThreadMsg vo) {
		CommuHandledMessage commuMsg;
		Vector<CommuHandledMessage> v = new Vector<CommuHandledMessage>();
		synchronized (this.CONTROL_MSGS_PRIORITY) {
			for (int i = this.priorityMsgs.size() - 1; i >= 0; i--) {
				commuMsg = (CommuHandledMessage) this.priorityMsgs.get(i);
				if (this.isNeedDel(commuMsg, vo)) {
					commuMsg.setHdlQueueType(FrameCodeConstant.threadMsgQueueTypePri);
					commuMsg.setHdlThreadId(this.getName());
					v.add(commuMsg);
					this.priorityMsgs.remove(i);
				}
			}
		}
		return v;

	}

	public Vector<CommuHandledMessage> delHandupMsgsForOrdinary(TMThreadMsg vo) {
		CommuHandledMessage commuMsg;
		Vector<CommuHandledMessage> v = new Vector<CommuHandledMessage>();
		synchronized (this.CONTROL_MSGS) {
			for (int i = this.msgs.size() - 1; i >= 0; i--) {
				commuMsg = (CommuHandledMessage) this.msgs.get(i);
				if (this.isNeedDel(commuMsg, vo)) {
					commuMsg.setHdlQueueType(FrameCodeConstant.threadMsgQueueTypeOrd);
					commuMsg.setHdlThreadId(this.getName());
					v.add(commuMsg);
					this.msgs.remove(i);
				}
			}
		}
		return v;

	}
}
