/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

import java.util.Vector;

/**
 * 
 * @author hejj
 */
public class ParaAutoDWConfigVo {

	private String paramTypeId;
	private Vector<ParaAutoDWConfigTimeVo> years;
	private Vector<ParaAutoDWConfigTimeVo> months;
	private Vector<ParaAutoDWConfigTimeVo> days;
	private Vector<ParaAutoDWConfigTimeVo> hours;
	private Vector<ParaAutoDWConfigTimeVo> mins;
	private String remark;
	private String downloadFlag;

	/**
	 * @return the paramTypeId
	 */
	public String getParamTypeId() {
		return paramTypeId;
	}

	/**
	 * @param paramTypeId
	 *            the paramTypeId to set
	 */
	public void setParamTypeId(String paramTypeId) {
		this.paramTypeId = paramTypeId;
	}

	/**
	 * @return the years
	 */
	public Vector<ParaAutoDWConfigTimeVo> getYears() {
		return years;
	}

	/**
	 * @param years
	 *            the years to set
	 */
	public void setYears(Vector<ParaAutoDWConfigTimeVo> years) {
		this.years = years;
	}

	/**
	 * @return the months
	 */
	public Vector<ParaAutoDWConfigTimeVo> getMonths() {
		return months;
	}

	/**
	 * @param months
	 *            the months to set
	 */
	public void setMonths(Vector<ParaAutoDWConfigTimeVo> months) {
		this.months = months;
	}

	/**
	 * @return the days
	 */
	public Vector<ParaAutoDWConfigTimeVo> getDays() {
		return days;
	}

	/**
	 * @param days
	 *            the days to set
	 */
	public void setDays(Vector<ParaAutoDWConfigTimeVo> days) {
		this.days = days;
	}

	/**
	 * @return the hours
	 */
	public Vector<ParaAutoDWConfigTimeVo> getHours() {
		return hours;
	}

	/**
	 * @param hours
	 *            the hours to set
	 */
	public void setHours(Vector<ParaAutoDWConfigTimeVo> hours) {
		this.hours = hours;
	}

	/**
	 * @return the mins
	 */
	public Vector<ParaAutoDWConfigTimeVo> getMins() {
		return mins;
	}

	/**
	 * @param mins
	 *            the mins to set
	 */
	public void setMins(Vector<ParaAutoDWConfigTimeVo> mins) {
		this.mins = mins;
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

	/**
	 * @return the downloadFlag
	 */
	public String getDownloadFlag() {
		return downloadFlag;
	}

	/**
	 * @param downloadFlag
	 *            the downloadFlag to set
	 */
	public void setDownloadFlag(String downloadFlag) {
		this.downloadFlag = downloadFlag;
	}
}
