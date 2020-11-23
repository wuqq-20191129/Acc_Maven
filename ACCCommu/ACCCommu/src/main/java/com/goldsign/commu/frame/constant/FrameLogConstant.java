/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.constant;

/**
 * 
 * @author hejj
 */
public class FrameLogConstant {
	/******* 系统日志级别 **************************************************/

	public  static String LOG_LEVEL_CURRENT = "1";// 日志级别
	public static String LOG_LEVEL_INFO = "1";// 普通信息级
	public static String LOG_LEVEL_WARN = "2";// 警告信息级

	public static String LOG_LEVEL_ERROR = "3";// 一般错误级
	public static String LOG_LEVEL_ERROR_SYS = "4";// 系统错误级

	public static String LOG_LEVEL_TEXT_CURRENT = "普通信息";
	public static String LOG_LEVEL_INFO_TEXT = "普通信息";// 普通信息级
	public static String LOG_LEVEL_WARN_TEXT = "警告信息";// 警告信息级

	public static String LOG_LEVEL_ERROR_TEXT = "一般错误";// 一般错误级
	public static String LOG_LEVEL_ERROR_SYS_TEXT = "系统错误";// 系统错误级

	/*********************************************************************/

	/************** 消息ID ********************************************/
	public static String MESSAGE_ID_PARAM_SYN = "01";// 参数版本同步请求
	public static String MESSAGE_ID_PARAM_REPLY = "02";// //参数版本获取后应答
	public static String MESSAGE_ID_DEV_STATUS_ALL = "08";// //全部设备状态信息
	public static String MESSAGE_ID_DEV_STATUS_EACH = "09";// 单个设备状态信息
	public static String MESSAGE_ID_DEV_STATUS_CHANGE = "10";// 设备状态变化信息
	public static String MESSAGE_ID_DEV_EVENT = "11";// 设备事件信息
	public static String MESSAGE_ID_FILE_NOTICE = "12";// 文件通知信息
	public static String MESSAGE_ID_FLOW_ENTRY = "13";// 进站信息
	public static String MESSAGE_ID_FLOW_EXIT = "14";// 出站信息
	public static String MESSAGE_ID_TVM_SJT = "15";// TVM SJT赋值信息
	public static String MESSAGE_ID_NON_RETURN = "16";// 非即时退款申请信息
        public static String MESSAGE_ID_LOSS_REPORT = "21";// 挂失申请信息
        public static String MESSAGE_ID_CARD_INFO = "23";// 逻辑卡号查询信息
	public static String MESSAGE_ID_START = "101";// 应用启动
	public static String MESSAGE_ID_CONFIG = "102";// 配置设置
	public static String MESSAGE_ID_PARAM_DISTRIBUTE = "103";// 参数下发
	public static String MESSAGE_ID_PUSH_QUEUE = "104";// 消息入库待发送
	public static String MESSAGE_ID_PARAM_COMMU_QUEUE = "105";// 通讯队列处理
	public static String MESSAGE_ID_CONNECTION = "106";// SOCKET连接处理
	public static String MESSAGE_ID_SOCKET_EXCHAGE = "107";// SOCKET消息交换
	public static String MESSAGE_ID_FTP = "108";// FTP文件
	/*******************************************************************/
        
	/**
	 * /* * 消息名称
	 ******************************************************************************/
	public static String MESSAGE_ID_PARAM_SYN_NAME = "参数版本同步请求";// 参数版本同步请求
	public static String MESSAGE_ID_PARAM_REPLY_NAME = "参数版本同步应答";// //参数版本同步应答
	public static String MESSAGE_ID_DEV_STATUS_ALL_NAME = "全部设备状态信息";// //全部设备状态信息
	public static String MESSAGE_ID_DEV_STATUS_EACH_NAME = "单个设备状态信息";// 单个设备状态信息
	public static String MESSAGE_ID_DEV_STATUS_CHANGE_NAME = "设备状态变化信息";// 设备状态变化信息
	public static String MESSAGE_ID_DEV_EVENT_NAME = "设备事件信息";// 设备事件信息
	public static String MESSAGE_ID_FILE_NOTICE_NAME = "文件通知信息";// 文件通知信息
	public static String MESSAGE_ID_FLOW_ENTRY_NAME = "进站信息";// 进站信息
	public static String MESSAGE_ID_FLOW_EXIT_NAME = "出站信息";// 出站信息
	public static String MESSAGE_ID_TVM_SJT_NAME = "TVM SJT赋值信息";// TVM SJT赋值信息
	public static String MESSAGE_ID_NON_RETURN_NAME = "非即时退款申请信息";// 非即时退款申请信息
        public static String MESSAGE_ID_LOSS_REPORT_NAME = "挂失申请信息";// 挂失申请信息

	public static String MESSAGE_ID_START_NAME = "应用启动";// 应用启动
	public static String MESSAGE_ID_CONFIG_NAME = "配置设置";// 配置设置
	public static String MESSAGE_ID_PARAM_DISTRIBUTE_NAME = "参数下发";// 参数下发
	public static String MESSAGE_ID_PUSH_QUEUE_NAME = "消息入库待发送";// 消息入库待发送
	public static String MESSAGE_ID_PARAM_COMMU_QUEUE_NAME = "通讯队列处理";// 通讯队列处理
	public static String MESSAGE_ID_CONNECTION_NAME = "SOCKET连接";// SOCKET连接处理
	public static String MESSAGE_ID_SOCKET_EXCHAGE_NAME = "SOCKET消息交换";// SOCKET消息交换
	public static String MESSAGE_ID_FTP_NAME = "FTP文件";// FTP文件
	/*************************************************************************************/
        
        /************** 消息子ID ********************************************/
	public static String MESSAGE_ID_SUB_AUDIT = "01";// 审计文件
	public static String MESSAGE_ID_SUB_ERROR = "02";// 错误文件
	public static String MESSAGE_ID_SUB_TICKET = "03";// 配票数据文件
        /*************************************************************************************/

	/* 消息的处理结果，记录日志使用************************ */
	public static String RESULT_HDL_SUCESS = "0";// 成功
	public static String RESULT_HDL_FAIL = "1";// 失败
	public static String RESULT_HDL_WARN = "2";// 警告
	/**************************************************/
	// 日志级别线程时间间隔
	public static long threadLogLevel = 300000;
}
