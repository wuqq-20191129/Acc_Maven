/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.constant;

/**
 * 
 * @author hejj
 */
public class FrameParameterConstant {

	/**** 参数自动下发 ***************************************************/
	public static String paraDownloadNot = "0";// 不自动下发

	public static String paraDownload = "1";// 自动下发

	public static String paraDownloadTimeTypeYear = "1";// 年

	public static String paraDownloadTimeTypeMonth = "2";// 月

	public static String paraDownloadTimeTypeDay = "3";// 日

	public static String paraDownloadTimeTypeHour = "4";// 时

	public static String paraDownloadTimeTypeMin = "5";// 分

	public static String paraDownloadTimeValueTypeAll = "1";// 所有时间

	public static String paraDownloadTimeValueTypeRange = "2";// 时间范围
	public static String paraDownloadTimeValueTypeSingle = "3";// 单时间

	public static String paraDownloadTimeValueAll = "*";// 时间通配符所有

	public static String paraDownloadTimeValueRange = "-";// 时间通配符范围

	public static long paraDownloadThreadSleepTime = 80000;// 自动下发时间间隔
	public static int paraDownloadMinNum = 1;// 参数下发最少新增记录数
	public static String paraDownloadClassPrefix = "com.goldsign.iccs.commu.download.DownloadHandler";// 下发处理解释类前缀
	/**** 参数自动下发 **********************************************************/
	public static String parmTypeSamlist = "0203";

	/**** 设备参数版本 *********************************************************/
	// 状态

	public static String devParaVerStatusUnhandled = "0";// 请求待处理

	public static String devParaVerStatusInQueue = "1";// 请求在消息队列

	// 时间间隔
	public static long devParaVerThreadSleepTime = 10000;// 查询时间间隔

	public static String devParaVerAllLine = "00";// 全线路代码

	public static String devParaVerAllStation = "00";// 全车站代码

	public static String devTypeLcc = "98";// LCC设备类型
	/***********************************************************************/
}
