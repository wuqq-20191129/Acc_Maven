package com.goldsign.commu.frame.manager;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.dao.ComMessageQueueDao;
import com.goldsign.commu.frame.dao.ParaDistributeDtlDao;
import com.goldsign.commu.frame.util.DateHelper;
import org.apache.log4j.Logger;

import com.goldsign.commu.frame.vo.MessageQueue;
import com.goldsign.commu.frame.vo.SynchronizedControl;

/**
 * 
 * @author hejj
 */
public class MessageQueueManager {

	public static Map<String, Vector<MessageQueue>> MESSAG_QUEUES = new ConcurrentHashMap<String, Vector<MessageQueue>>();
	public static Vector<MessageQueue> MESSAGE_QUEUES_DOING = new Vector<MessageQueue>();
	public final static SynchronizedControl SYN_CONTROL_DOING = new SynchronizedControl();
	public final static SynchronizedControl SYN_CONTROL = new SynchronizedControl();
	private static Logger logger = Logger.getLogger(MessageQueueManager.class
			.getName());

	public MessageQueueManager() {
	}

	// public static void main(String[] args) {
	// MessageQueueManager messageQueueManager1 = new MessageQueueManager();
	// }

	public static MessageQueue getMessageQueue(String id) {
		MessageQueue mq = null;
		Vector<MessageQueue> mqs = null;
		synchronized (SYN_CONTROL) {
			if (MESSAG_QUEUES.isEmpty()) {
				return null;
			}
			if (!MESSAG_QUEUES.containsKey(id)) {
				return null;
			}
			mqs = (Vector<MessageQueue>) MESSAG_QUEUES.get(id);
			if (mqs.isEmpty()) {
				return null;
			}
			if(!isBlackListDistri()){
				mqs=checkForBlack(mqs);
				if (mqs.isEmpty()) {
					return null;
				}
			}
			mq = (MessageQueue) mqs.get(0);
			mqs.remove(0);
			// 将要处理的消息放入正在处理的消息队列中 add by hejj 2011-06-22
			MessageQueueManager.setMessageQueueDoing(mq);
			logger.info(id + " - 消息缓存队列剩余" + mqs.size() + "个消息未处理");
			return mq;
		}

	}


	// public static void removeMessageQueueFromBuffer(String ip,
	// String messageType) {
	// synchronized (MESSAG_QUEUES) {
	// if (MESSAG_QUEUES == null || MESSAG_QUEUES.isEmpty()) {
	// return;
	// }
	// if (!MESSAG_QUEUES.containsKey(ip)) {
	// return;
	// }
	// Vector<MessageQueue> msgs = (Vector<MessageQueue>) MESSAG_QUEUES
	// .get(ip);
	// MessageQueue msg = null;
	// Vector<MessageQueue> buffer = new Vector<MessageQueue>();
	//
	// for (int i = 0; i < msgs.size(); i++) {
	// msg = (MessageQueue) msgs.get(i);
	// if (!isMessageForType(msg, messageType)) {// 保留非清除消息
	// buffer.add(msg);
	// } else {
	// logger.info("消息缓存清除消息" + msg.getMessageId());
	// }
	//
	// }
	// msgs.clear();
	// if (!buffer.isEmpty()) {// 将保留消息放回消息队列
	//
	// msgs.addAll(buffer);
	// }
	//
	// }
	//
	// }

	// private static boolean isMessageForType(MessageQueue msg, String
	// messageType) {
	// if (msg == null) {
	// return false;
	// }
	// byte[] bMsg = msg.getMessage();
	// String sMsg = new String(bMsg);
	// if (sMsg.startsWith(messageType)) {
	// return true;
	// }
	// return false;
	// }

	public static void setMessageQueueDoing(MessageQueue mq) {
		synchronized (SYN_CONTROL_DOING) {
			MESSAGE_QUEUES_DOING.add(mq);
		}

	}

	public static void removeMessageFromQueueDoing(MessageQueue mq) {
		synchronized (SYN_CONTROL_DOING) {
			if (MESSAGE_QUEUES_DOING.contains(mq)) {
				MESSAGE_QUEUES_DOING.remove(mq);
			}
		}

	}

	public static boolean isInMessageQueueDoing(MessageQueue mq) {

		if (MESSAGE_QUEUES_DOING == null || MESSAGE_QUEUES_DOING.isEmpty()) {
			return false;
		}
		MessageQueue vo;
		for (int i = 0; i < MESSAGE_QUEUES_DOING.size(); i++) {
			vo = (MessageQueue) MESSAGE_QUEUES_DOING.get(i);
			if (vo.getMessageId() == mq.getMessageId()) {
				return true;
			}

		}
		return false;
	}

	public static Vector<MessageQueue> removeMessageDoing(
			Vector<MessageQueue> mqs) {
		synchronized (SYN_CONTROL_DOING) {
			Vector<MessageQueue> v = new Vector<MessageQueue>();
			MessageQueue mq;
			if (mqs == null || mqs.isEmpty()) {
				return v;
			}

			for (int i = 0; i < mqs.size(); i++) {
				mq = (MessageQueue) mqs.get(i);
				if (!isInMessageQueueDoing(mq)) {
					v.add(mq);
				}

			}
			return v;
		}
	}

	public static void setMessageQueue(Vector<MessageQueue> mqs) {
		Vector<MessageQueue> msgsLeft;
		synchronized (SYN_CONTROL) {
			if (mqs.isEmpty()) {
				return;
			}

			MessageQueue mq = null;
			String ip = null;
			Vector<MessageQueue> v = null;
			// 清除正在处理的消息，这些消息不能重新放入消息队列 modify by hejj 2011-06-22
			msgsLeft = MessageQueueManager.removeMessageDoing(mqs);
			// 清除旧的未处理队列

			MESSAG_QUEUES.clear();
			for (int i = 0; i < msgsLeft.size(); i++) {
				mq = (MessageQueue) msgsLeft.get(i);
				ip = mq.getIpAddress();
				if (ip == null || ip.length() == 0) {
					continue;
				}
				if (!MESSAG_QUEUES.containsKey(ip)) {
					v = new Vector<MessageQueue>();
					v.add(mq);
					MESSAG_QUEUES.put(ip, v);
				} else {
					v = MESSAG_QUEUES.get(ip);
					v.add(mq);
				}
				logger.debug(ip + " - 消息缓存队列现有" + v.size() + "个消息未处理");

			}

		}

	}
	private static Vector<MessageQueue> checkForBlack(Vector<MessageQueue> mqs) {
		Iterator it =mqs.iterator();
		MessageQueue mq = null;
		while (it.hasNext()){
			mq = (MessageQueue) it.next();
			int parmWaterNo= mq.getParaInformWaterNo();
			if(mq.getIsParaInformMsg().equals("1") && checkForParmGen(parmWaterNo)){
				it.remove();
			}
		}
		return mqs;
	}

	private static boolean checkForParmGen(int parmWaterNo) {
		boolean result = false;
		try {
			ComMessageQueueDao dao = new ComMessageQueueDao();
			result = dao.checkForParaGen(parmWaterNo);
		} catch (Exception e) {
			logger.error("isBlackListDistri error  " + e);
		}
		return result;
	}
	public static boolean isBlackListDistri(){
		boolean result =false;
		String current = DateHelper.dateToString(new Date());
		logger.debug("测试时间============"+current);
		// HHMM
		String curTime = current.substring(8, 12);
		if(Integer.parseInt(FrameCodeConstant.BLACKDISTRI_BEGIN)>Integer.parseInt(FrameCodeConstant.BLACKDISTRI_END)){
			if(Integer.parseInt(curTime)>=Integer.parseInt(FrameCodeConstant.BLACKDISTRI_BEGIN) || Integer.parseInt(curTime)<Integer.parseInt(FrameCodeConstant.BLACKDISTRI_END) ){
				result = true;
			}
		}else{
			if(Integer.parseInt(curTime)>=Integer.parseInt(FrameCodeConstant.BLACKDISTRI_BEGIN) && Integer.parseInt(curTime)<Integer.parseInt(FrameCodeConstant.BLACKDISTRI_END)){
				result = true;
			}
		}
		return result;
	}
}
