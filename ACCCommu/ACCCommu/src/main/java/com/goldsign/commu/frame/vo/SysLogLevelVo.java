/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 * 
 * @author hejj
 */
public class SysLogLevelVo {

	private String sysCode;
	private String sysLevel;
	private String sysCodeText;
	private String sysLevelText;
	private String setTime;
	private String operator;
	private String versionNo;
	private String recordFlag;

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRecordFlag() {
		return recordFlag;
	}

	public void setRecordFlag(String recordFlag) {
		this.recordFlag = recordFlag;
	}

	public String getSetTime() {
		return setTime;
	}

	public void setSetTime(String setTime) {
		this.setTime = setTime;
	}

	public String getSysCode() {
		return sysCode;
	}

	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}

	public String getSysCodeText() {
		return sysCodeText;
	}

	public void setSysCodeText(String sysCodeText) {
		this.sysCodeText = sysCodeText;
	}

	public String getSysLevel() {
		return sysLevel;
	}

	public void setSysLevel(String sysLevel) {
		this.sysLevel = sysLevel;
	}

	public String getSysLevelText() {
		return sysLevelText;
	}

	public void setSysLevelText(String sysLevelText) {
		this.sysLevelText = sysLevelText;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public SysLogLevelVo() {
		super();
		// TODO Auto-generated constructor stub
	}
}
