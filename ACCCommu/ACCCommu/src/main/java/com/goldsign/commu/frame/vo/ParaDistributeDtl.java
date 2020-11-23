/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

import java.util.Date;
import java.util.Vector;

/**
 * 
 * @author hejj
 */
public class ParaDistributeDtl {

	private int waterNo;
	private Date distributeDatetime;
	private String sysOperatorId;
	private String distributeResult;
	private String distributeMemo;
	private Vector paraInformDtl;
	private Vector<ParaGenDtl> paraGenDtl;

	public int getWaterNo() {
		return this.waterNo;
	}

	public void setWaterNo(int aWaterNo) {
		this.waterNo = aWaterNo;
	}

	public Date getDistributeDatetime() {
		return this.distributeDatetime;
	}

	public void setDistributeDatetime(Date aDistributeDatetime) {
		this.distributeDatetime = aDistributeDatetime;
	}

	public String getSysOperatorId() {
		return this.sysOperatorId;
	}

	public void setSysOperatorId(String aSysOperatorId) {
		this.sysOperatorId = aSysOperatorId;
	}

	public String getDistributeResult() {
		return this.distributeResult;
	}

	public void setDistributeResult(String aDistributeResult) {
		this.distributeResult = aDistributeResult;
	}

	public String getDistributeMemo() {
		return this.distributeMemo;
	}

	public void setDistributeMemo(String aDistributeMemo) {
		this.distributeMemo = aDistributeMemo;
	}

	public Vector getParaInformDtl() {
		return this.paraInformDtl;
	}

	public void setParaInformDtl(Vector aParaInformDtl) {
		this.paraInformDtl = aParaInformDtl;
	}

	public Vector<ParaGenDtl> getParaGenDtl() {
		return this.paraGenDtl;
	}

	public void setParaGenDtl(Vector<ParaGenDtl> aParaGenDtl) {
		this.paraGenDtl = aParaGenDtl;
	}
}
