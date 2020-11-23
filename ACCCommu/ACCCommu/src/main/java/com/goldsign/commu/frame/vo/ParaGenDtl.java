/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 * 
 * @author hejj
 */
public class ParaGenDtl {

	private int waterNo;
	private String parmTypeId;
	private String verNum;
	private String genResult;
	private String verType;

	public int getWaterNo() {
		return this.waterNo;
	}

	public void setWaterNo(int aWaterNo) {
		this.waterNo = aWaterNo;
	}

	public String getVerNum() {
		return this.verNum;
	}

	public void setVerNum(String aVerNum) {
		this.verNum = aVerNum;
	}

	public String getParmTypeId() {
		return this.parmTypeId;
	}

	public void setParmTypeId(String aParmTypeId) {
		this.parmTypeId = aParmTypeId;
	}

	public String getGenResult() {
		return this.genResult;
	}

	public String getVerType() {
		return verType;
	}

	public void setGenResult(String aGenResult) {
		this.genResult = aGenResult;
	}

	public void setVerType(String verType) {
		this.verType = verType;
	}
}
