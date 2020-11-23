package com.goldsign.commu.frame.constant;

import java.util.TreeMap;

/**
 * 
 * @author hejj
 */
public class FrameThreadPoolConstant {
	/**
	 * **线程池监控****
	 */
	public static int TPMonitorThreadSleepTime = 10000;// 线程池监控时间间隔

	// public static int TPMonitorThreadResetAferSleepTime
	// =10000;//线程池重设后再次判断时间间隔
	/**
	 * 线程状态*
	 */
	public final static String ThreadStatusInit = "0";// 初始化启动

	public final static String ThreadStatusHandling = "1";// 正在处理消息
	public final static String ThreadStatusFinish = "2";// 成功处理消息
	public final static String ThreadStatusHandup = "3";// 线程挂起
	public final static String ThreadStatusFinishException = "4";// 异常完成消息
	/*************************************************************************/

	/**************** * 线程转存原因 ************************************/

	public final static String ThreadDumpResonReplace = "1";// 线程替换
	public final static String ThreadDumpResonRestart = "2";// 系统重启
	/******************************************************************/

	/** 线程处理消息最大时间 ************************************************************/
	public static TreeMap<String,Long> ThreadMsgHandleMaxTime = new TreeMap<String,Long>();
	public static long ThreadMsgHandleMaxTimeDefault = 60;// 秒单位，缺省
	public static String ThreadMsgHandleMaxTimeDefaultKey = "-1";// 缺省值的消息ID
	public static String ThreadMsgHandleTimeEmpty = "-1";// 线程处理时间空值

	public static String ThreadMsgAnalyzeClassPrefix = "com.goldsign.commu.app.monitor.AnalyzeMessage";// 消息解释类前缀
	public static long ThreadMsgHandUpMaxNumberAllow = 16;// 消息处理允许的最大挂起数
	public static int ThreadReleaseResourceWaitCount = 5;// 线程释放资源最多等候次数

	/**************************************************************/

}
