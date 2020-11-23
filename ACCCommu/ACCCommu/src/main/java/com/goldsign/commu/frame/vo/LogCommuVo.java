/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 * 
 * @author hejj
 */
public class LogCommuVo {
	private String waterNo;
	private String messageId;// 消息ID
	private String messageName;// 消息名称
	private String messageFrom;// 消息来源
	private String startTime;// 处理开始时间

	private String endTime;// 处理结束时间
	private String useTime;// 处理用时
	private String result;// 处理结果
	private String hdlThread;// 处理线程
	private String sysLevel;// 日志级别
	private String sysLevelText;// 日志级别

	private String timeStart;
	private String timeEnd;

	private String remark;

	public String getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}

	public String getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getHdlThread() {
		return hdlThread;
	}

	public void setHdlThread(String hdlThread) {
		this.hdlThread = hdlThread;
	}

	public String getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getSysLevel() {
		return sysLevel;
	}

	public void setSysLevel(String sysLevel) {
		this.sysLevel = sysLevel;
	}

	public String getUseTime() {
		return useTime;
	}

	public void setUseTime(String useTime) {
		this.useTime = useTime;
	}

	public String getWaterNo() {
		return waterNo;
	}

	public void setWaterNo(String waterNo) {
		this.waterNo = waterNo;
	}

	public LogCommuVo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getSysLevelText() {
		return sysLevelText;
	}

	public void setSysLevelText(String sysLevelText) {
		this.sysLevelText = sysLevelText;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

}
