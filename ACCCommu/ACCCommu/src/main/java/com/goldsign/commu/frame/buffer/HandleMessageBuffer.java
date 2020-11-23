package com.goldsign.commu.frame.buffer;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.message.HandleMessageBase;

public class HandleMessageBuffer {
	/**
	 * 队列
	 */
	protected int threadBufferCapacity = 10;
	protected int threadBufferIncrement = 10;
	private Vector<HandleMessageBase> msgs = new Vector<HandleMessageBase>(
			threadBufferCapacity, threadBufferCapacity);
	private Vector<HandleMessageBase> priorityMsgs = new Vector<HandleMessageBase>(
			threadBufferCapacity, threadBufferCapacity);
	/**
	 * 同步控制
	 */
	private final byte[] CONTROL_MSGS = new byte[0];
	private final byte[] CONTROL_MSGS_PRIORITY = new byte[0];
	/**
	 * 状态
	 */
	protected boolean isHandling = false;

	/**
	 * 处理的消息
	 */
	protected HandleMessageBase handlingMsg = null;
	protected HandleMessageBase handlingPriorityMsg = null;

	private static Logger logger = Logger.getLogger(HandleMessageBuffer.class
			.getName());

	public Vector<HandleMessageBase> getMsgs() {
		synchronized (this.CONTROL_MSGS) {
			return this.msgs;
		}

	}

	public Vector<HandleMessageBase> getAllMsgs() {
		Vector<HandleMessageBase> allMsgs = new Vector<HandleMessageBase>();
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

	public HandleMessageBase getHandlingMsg() {
		return this.handlingMsg;
	}

	public boolean isHandlingMsg() {
		return this.isHandling;

	}

	public HandleMessageBase getMsg() {
		HandleMessageBase msg = null;

		synchronized (this.CONTROL_MSGS) {
			if (msgs.isEmpty()) {
				return null;
			}
			msg = (HandleMessageBase) msgs.get(0);
			msgs.remove(0);

		}
		logger.info(Thread.currentThread().getName() + ":普通队列剩余未处理消息 ="
				+ priorityMsgs.size());

		return msg;

	}

	public void setMsg(HandleMessageBase msg) {
		synchronized (this.CONTROL_MSGS) {
			msgs.add(msg);
		}
	}

	public HandleMessageBase getPriorityMsg() {
		HandleMessageBase msg = null;

		synchronized (this.CONTROL_MSGS_PRIORITY) {
			if (priorityMsgs.isEmpty()) {
				return null;
			}
			msg = (HandleMessageBase) priorityMsgs.get(0);
			priorityMsgs.remove(0);

		}

		// DateHelper.screenPrint(this.getName()+":left msgs ="+msgs.size());

		return msg;

	}

	public HandleMessageBase getHandingMsg() {
		HandleMessageBase msg = null;
		synchronized (this.CONTROL_MSGS_PRIORITY) {
			if (!priorityMsgs.isEmpty()) {// 优先级队列有未处理消息

				msg = (HandleMessageBase) priorityMsgs.get(0);
				priorityMsgs.remove(0);
				logger.info(Thread.currentThread().getName() + ":优先队列剩余未处理消息 ="
						+ priorityMsgs.size());
				return msg;

			} else// 优先级队列娥没有未处理消息

			{
				synchronized (this.CONTROL_MSGS) {
					if (msgs.isEmpty()) {
						return null;
					} else {
						msg = (HandleMessageBase) msgs.get(0);
						msgs.remove(0);
						logger.info(Thread.currentThread().getName()
								+ ":普通队列剩余未处理消息 =" + msgs.size());

					}

				}

			}
			return msg;

		}
	}

	public void setHandlingMsg(HandleMessageBase msg) {
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

	public boolean isPriorityMsg(HandleMessageBase msg) {
		return false;

	}

	public void setPriorityMsg(HandleMessageBase priorityMsg) {
		synchronized (this.CONTROL_MSGS_PRIORITY) {
			priorityMsgs.add(priorityMsg);
		}
	}
}
