package com.goldsign.commu.frame.thread;

import java.util.Vector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;
import com.goldsign.commu.frame.constant.FrameSynchronizeConstant;
import com.goldsign.commu.frame.constant.FrameThreadPoolConstant;
import com.goldsign.commu.frame.manager.CommuThreadManager;
import com.goldsign.commu.frame.util.CommuThreadPoolUtil;
import com.goldsign.commu.frame.util.SocketUtil;
import com.goldsign.commu.frame.util.ThreadUtil;
import com.goldsign.commu.frame.vo.CommuHandledMessage;
import com.goldsign.commu.frame.vo.TMMonitorResult;
import com.goldsign.commu.frame.vo.TMThreadMsg;
import com.goldsign.commu.frame.vo.TMThreadStatusVo;

/**
 * 
 * @author hejj
 */
public class CommuThreadPoolMonitor extends Thread {

	private static Logger logger = Logger
			.getLogger(CommuThreadPoolMonitor.class.getName());

	private String getThreadIds(Vector<TMThreadStatusVo> vos) {
		String threadIds = "";
		TMThreadStatusVo vo;
		for (int i = 0; i < vos.size(); i++) {
			vo = (TMThreadStatusVo) vos.get(i);
			threadIds += vo.getThreadId() + "#";
		}
		return threadIds;
	}

	@Override
	public void run() {
		logger.info("线程池监听线程已启动...");
		TMMonitorResult mr;
		String threadIds;
		Vector<TMThreadMsg> msgKeysDel = new Vector<TMThreadMsg>();
		Vector<CommuMessageHandleThread> newThreads = new Vector<CommuMessageHandleThread>();
		Vector<CommuMessageHandleThread> oldThreads = new Vector<CommuMessageHandleThread>();
		HashMap<TMThreadMsg, Vector<CommuHandledMessage>> msgsDel;
		while (true) {
			try {
				// 获取线程池各线程状态
				// 线程池中存在需处理的线程，该线程状态不正常
				mr = CommuThreadPoolUtil.getMonitorResult();
				if (mr.isExistedFailureThread()) {
					// 关闭服务端口
					// 挂起的所有线程
					threadIds = this.getThreadIds(mr.getVos());
					// 关闭监听端口及所有连接
					SocketUtil.closeServerSocketForThreadMonitor(threadIds);

					// 没有挂起的线程处理完当前消息后等候挂起线程更新完后一起处理，主要考虑线程池重置问题

					// CommuThreadManager.synAllThreads();
					// logger.error("线程阻塞,设置线程同步锁");
					// 等候所有线程同步后，再处理

					// 记录挂起线程的消息，作为分析是否需要将同类消息在所有消息队列中清理掉，已防止

					// 在新线程中同样造成堵塞挂起,统计消息的挂起次数

					CommuThreadPoolUtil.repForSetBufferThreadHandups(mr);

					// CommuThreadPoolMonitor.sleep(ApplicationConstant.TPMonitorThreadResetAferSleepTime);
					// 完成线程替换后,再次判断线程状态是否正常，避免在短时间内重复启动服务器端口
					// mr = CommuThreadPoolUtil.getMonitorResult();

					// 为避免有些消息处理一直会挂起，如挂起次数超过一定数量，需从队列中删除该类消息
					// 如某线路的文件获取，该线路获取文件时，就会消息挂起，处理时，只有将该线路的所有消息删除

					// 再如某车站的全设备状态消息处理超时，该车站累计发送了大量的消息，处理时，也只有将该车站的同类消息删除
					msgKeysDel.clear();
					msgKeysDel.addAll(ThreadUtil.getNeedDelMsgs());
					if (!msgKeysDel.isEmpty()) {
						msgsDel = ThreadUtil
								.delMsgsFromThreadMsgQueue(msgKeysDel);
						logger.error("共从线程消息队列中删除消息类型：" + msgsDel.size()
								+ " 消息数量：" + this.getMsgDelSize(msgsDel));
						// 记录删除消息
						if (!msgsDel.isEmpty()) {
							ThreadUtil.writeInfoForDelMsgs(msgsDel);
						}
					}

					// 替换异常线程(包括新线程取代旧线程在线程池中的位置、更新线程状态缓存、记录线程替换日志)
					newThreads.clear();
					oldThreads.clear();
					CommuThreadPoolUtil.replaceHandleThreads(mr, newThreads,
							oldThreads);
					// 检查数据库资源释放释放完毕
					this.waitResourceRelease(
							oldThreads,
							FrameThreadPoolConstant.ThreadReleaseResourceWaitCount);
					logger.error("检查资源释放已完毕");
					CommuThreadManager.killOldThread(oldThreads);
					logger.error("统一杀死所有旧线程");
					// 重置连接池
					// ConnectionPoolUtil.resetConnectionPool(ApplicationConstant.commuConfig);
					// logger.error("完成连接池重置");
					// 释放线程同步锁 
					CommuThreadManager.synReleaseAllThreads();
					logger.error("释放线程同步锁");
					// 启动新线程 
					CommuThreadManager.startNewThread(newThreads);

					// if(!mr.isExistedFailureThread()){
					// 设置可以重新启动服务器SOCK端口
					SocketUtil.setRestartedStatus(true);
					// 重新启动服务端口
					this.restartServerSokcet();

					// }

				}

				CommuThreadPoolMonitor
						.sleep(FrameThreadPoolConstant.TPMonitorThreadSleepTime);
			} catch (InterruptedException e) {
				logger.error("线程池监听线程将终止...");
			} catch (Exception e) {
				logger.error("线程池监听线程处理异常：" + e.getMessage());
			}

		}

	}

	private void waitResourceRelease(Vector oldThreads, int maxWaitCount) {
		CommuMessageHandleThread t;
		boolean isCloseAll = true;
		for (int j = 0; j < maxWaitCount; j++) {
			for (int i = 0; i < oldThreads.size(); i++) {
				t = (CommuMessageHandleThread) oldThreads.get(i);
				isCloseAll = isCloseAll & t.checkReleaseResource();
				;
			}
			if (isCloseAll) {
				break;
			}
			this.threadSleep(1000);

		}
		if (!isCloseAll) {
			logger.error("等候" + maxWaitCount + "次,资源释放还是没有完全释放,可能造成资源泄漏");
		}
	}

	private void threadSleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
		}
	}

	private int getMsgDelSize(HashMap<TMThreadMsg, Vector<CommuHandledMessage>> hm) {
		Collection c = hm.values();
		Iterator it = c.iterator();
		int n = 0;
		Vector<CommuHandledMessage> v;
		while (it.hasNext()) {
			v = (Vector<CommuHandledMessage>) it.next();
			n += v.size();
		}
		return n;

	}

	private void restartServerSokcet() {
		if (SocketUtil.isNeedRestartServerSocket()) {
			// 准备重新启动服务器端口，禁止多次重新启动服务器端口

			SocketUtil.setRestartedStatus(false);
			logger.info("线程池异常处理结束,重新启动监听服务器");
			CommuServerThread server = new CommuServerThread();
			server.start();
			// 恢复控制参数
			this.restoreControl();

		}
	}

	private void restoreControl() {
		logger.info("恢复控制参数");
		synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_START) {
			SocketUtil.isClosedServerSocket = new Boolean(false);
		}
		synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_RESTART) {
			SocketUtil.isRestartedServerSocket = new Boolean(false);
		}

	}

	private void testThreadPools() {
		/*
		 * 
		 * //测试连接 if (!this.testConnection()) {
		 * if(this.isEmergent()){//应急指挥中心数据库
		 * this.disableEmergent();//数据库状态标志改为false } //设置连接不可用
		 * 
		 * this.setConnectStatus(flag, false); //关闭监听服务器,除应急指挥中心数据库连接异常外
		 * 
		 * if (!this.isEmergent()) {
		 * 
		 * synchronized (this.isClosedServerSocket) { if
		 * (!this.isClosedServerSocket.booleanValue()) {
		 * this.closeServerSocket(); this.isClosedServerSocket = new
		 * Boolean(true); } } } logger.info("尝试重建" + msgFlag +
		 * "连接池" + (++count) + "次....."); return; } //连接可用,正常运行测试连接无问题
		 * 
		 * if (count == 0) { this.setConnectStatus(flag, true);
		 * 
		 * return; } if(this.isEmergent())//应急指挥中心数据库
		 * this.enableEmergent();//数据库状态标志改为true
		 * 
		 * 
		 * 
		 * logger.info(msgFlag + "连接中断后已可用....."); //重建连接池
		 * 
		 * if (this.resetConnectonPool()) { //设置连接池可用标志
		 * 
		 * 
		 * this.setConnectStatus(this.flag, true); // 设置可以重新启动服务器SOCK端口 if
		 * (!this.isEmergent()) { this.setRestartedStatus(true); }
		 * 
		 * }
		 */
	}
}
