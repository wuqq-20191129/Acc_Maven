/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.ComMessageQueueDao;
import com.goldsign.commu.frame.manager.MessageQueueManager;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.vo.MessageQueue;

import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class MessageQueueThread extends Thread {
	private static Logger logger = Logger.getLogger(MessageQueueThread.class
			.getName());
	/**
	 * 日志记录使用
	 */
	private long hdlStartTime; // 处理的起始时间

	private long hdlEndTime;// 处理的结束时间

	public MessageQueueThread() {
	}

	// public static void main(String[] args) {
	// MessageQueueThread messageQueueThread1 = new MessageQueueThread();
	// }

	public void run() {
		ComMessageQueueDao dao = null;
		Vector<MessageQueue> mqs = null;
		while (true) {
			try {
				this.hdlStartTime = System.currentTimeMillis();

				dao = new ComMessageQueueDao();
				mqs = dao.pullQueuesForThread();
				if (mqs != null && !mqs.isEmpty()) {
					MessageQueueManager.setMessageQueue(mqs);
				}
			} catch (Exception e) {
				logger.error(e);

				this.hdlEndTime = System.currentTimeMillis();
				// 记录日志
				LogDbUtil.logForDbDetail(
						FrameLogConstant.MESSAGE_ID_PARAM_COMMU_QUEUE, "",
						this.hdlStartTime, this.hdlEndTime,
						FrameLogConstant.RESULT_HDL_FAIL, Thread
								.currentThread().getName(),
						FrameLogConstant.LOG_LEVEL_ERROR, "通讯队列处理");
			}
			this.threadSleep();

		}

	}

	private void threadSleep() {
		try {
			sleep(FrameCodeConstant.messageQueueThreadSleepTime);
		} catch (Exception e) {
		}
	}

}
