/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.constant;

/**
 * 
 * @author zhangjh
 */
public class FrameFileHandledConstant {

	/**
	 * 文件处理错误代码
	 */
	// 文件级0-10
	public static String[] FILE_ERR_FILE_NAME_LEN = { "01", "文件名长度不合法" };// 文件名长度不合法
	public static String[] FILE_ERR_FILE_NAME_FMT = { "02", "文件名格式不合法" };// 文件名格式不合法
	public static String[] FILE_ERR_FILE_NAME_STATION = { "03", "文件名车站不合法" };// 文件名车站不合法
	public static String[] FILE_ERR_FILE_NAME_TIME = { "04", "文件名时间不合法" };// 文件名时间不合法
	public static String[] FILE_ERR_FILE_NAME_REPEAT = { "05", "文件名重复" };// 文件名重复
	// 记录级11-20
	public static String[] FILE_ERR_FILE_HEAD_LINE = { "11",
			"文件头的线路与名称的线路不一致" };// 文件头的线路与名称的线路不一致
	public static String[] FILE_ERR_FILE_HEAD_SEQ = { "12", "文件头的序列号与名称的序列号不一致" };// 文件头的序列号与名称的序列号不一致
	public static String[] FILE_ERR_FILE_HEAD_ROW_COUNT = { "13",
			"文件头的记录数与实际记录数不一致" };// 文件头的记录数与实际记录数不一致
	// 处理过程的其他异常30-35
	public static String[] FILE_ERR_FILE_UNKOWN = { "30", "文件处理过程的其他异常" };// 处理过程的其他异常
	// 完整性CRC21-25
	public static String[] FILE_ERR_FILE_CRC = { "21", "文件的CRC码不正确" };// 文件的CRC码不正确
	/**
	 * *************************************************************************
	 * **************************
	 */
	/**
	 * 记录处理错误代码41-55
	 */
	public static String[] RECORD_ERR_TAC = { "41", "tac码不合法" };// tac码不合法
	public static String[] RECORD_ERR_REPEAT = { "42", "记录重复" };// 记录重复
	public static String[] RECORD_ERR_DEVICE = { "43", "设备非法" };// 设备非法
	public static String[] RECORD_ERR_DATETIME = { "44", "交易时间非法或超过设定期限" };// 交易时间非法或超过设定期限
	public static String[] RECORD_ERR_CARD = { "45", "票卡类型非法" };// 票卡类型非法
	public static String[] RECORD_ERR_FEE = { "46", "交易金额非法或超过设定上限" };// 交易金额非法或超过设定上限
	public static String[] RECORD_ERR_OTHER = { "55", "其他错误" };// 其他错误
	/**
	 * *************************************************************************
	 * ***********************
	 */
	/**
	 * 文件处理标志
	 */
	public static String FILE_HDL_NO = "0";// 未处理
	public static String FILE_HDL_YES = "1";// 已处理
	/**
	 * FTP相关
	 */
	/**
	 * FTP文件数据类型
	 */
	public static String[] FTP_FILE_DATA_TYPE = { "1", "2", "3", "4" }; // 数据类型
	public static String[] FTP_FILE_DATA_TYPE_NAME = { "TRX", "PRO", "AUD",
			"REG" }; // 数据类型名称{交易数据，收益数据，审计数据，寄存器数据}
	/**
	 * 记录类型
	 */
	public static String RECORD_TYPE_AUD_AGM = "1";// 闸机审计数据
	public static String RECORD_TYPE_AUD_TVM = "2";// TVM审计数据
	// public static String RECORD_TYPE_AUD_TVM="2";//班次收益数据（）
	/**
	 * 记录校验标志
	 */
	public static String RECORD_CHECKED = "1";// 数据完成校验
	public static String RECORD_UNCHECKED = "0";// 数据没有完成校验
	/**
	 * 审计文件前缀
	 */
	public static String AUD_FILE_PREFIX_FTP = "FTP";// 上传文件的审计文件
	public static String AUD_FILE_PREFIX_ER = "ER";// 错误文件的审计文件
}
