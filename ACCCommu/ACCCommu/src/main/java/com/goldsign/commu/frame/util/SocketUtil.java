package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameSynchronizeConstant;
import com.goldsign.commu.frame.listener.CommuConnectionPoolListener;
import com.goldsign.commu.frame.server.CommuServer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class SocketUtil {

	public static Boolean isClosedServerSocket = false;
	public static Boolean isRestartedServerSocket = false;
	private static Logger logger = Logger.getLogger(SocketUtil.class.getName());
	private static Object lockObj = new Object();

	public static void closeServerSocketForCPListener(String msgFlag) {

		try {
			if (CommuServer.serverSocket != null) {
				synchronized (lockObj) {
					if (CommuServer.serverSocket != null) {
						synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CLOSE_EXCEPTION) {
							CommuConnectionPoolListener.isSqlConExceptionClose = true;
						}

						CommuServer.serverSocket.close();
						CommuServer.serverSocket = null;
						closeConnectedSockets();

						CommuConnectionPoolListener.dumpThreadUnHandledMsgs();
						logger.info(msgFlag + "数据库连接异常,关闭监听服务器");
					}
				}

			}
		} catch (IOException e) {
			logger.error(e.getMessage());

		}

	}

	private static void closeConnectedSockets() {
		synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CONNECTION) {
			if (CommuServer.connectedSockets.isEmpty()) {
				return;
			}
			Set keys = CommuServer.connectedSockets.keySet();
			Iterator it = keys.iterator();
			Integer key;
			CommuConnection con;

			while (it.hasNext()) {
				key = (Integer) it.next();
				con = (CommuConnection) CommuServer.connectedSockets.get(key);
				con.closeConnection();
			}
			CommuServer.connectedSockets.clear();
		}
	}

	public static void closeServerSocketForThreadMonitor(String threadId) {

		try {
			if (CommuServer.serverSocket != null) {
				synchronized (lockObj) {
					if (CommuServer.serverSocket != null) {
						synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CLOSE_EXCEPTION) {
							CommuConnectionPoolListener.isSqlConExceptionClose = true;
						}

						CommuServer.serverSocket.close();
						CommuServer.serverSocket = null;
						closeConnectedSockets();

						// CommuConnectionPoolListener.dumpThreadUnHandledMsgs();
						logger.info(threadId + "线程异常,关闭监听服务器");
					}
				}

			}
		} catch (IOException e) {
			logger.error(e.getMessage());

		}

	}

	public static void setRestartedStatus(boolean status) {
		synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_RESTART) {
			SocketUtil.isRestartedServerSocket = status;
		}
	}

	public static boolean isNeedRestartServerSocket() {
		synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_RESTART) {
			return SocketUtil.isRestartedServerSocket.booleanValue();
		}
	}
}
