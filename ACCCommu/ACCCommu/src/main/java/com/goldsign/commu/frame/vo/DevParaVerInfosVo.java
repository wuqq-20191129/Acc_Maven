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
public class DevParaVerInfosVo {

	private String lineId;
	private String stationId;
	private String devTypeId;
	private String deviceId;
	private String reportDate;
	private Vector<DevParaVerInfoVo> params;

	/**
	 * @return the lineId
	 */
	public String getLineId() {
		return lineId;
	}

	/**
	 * @param lineId
	 *            the lineId to set
	 */
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	/**
	 * @return the stationId
	 */
	public String getStationId() {
		return stationId;
	}

	/**
	 * @param stationId
	 *            the stationId to set
	 */
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	/**
	 * @return the devTypeId
	 */
	public String getDevTypeId() {
		return devTypeId;
	}

	/**
	 * @param devTypeId
	 *            the devTypeId to set
	 */
	public void setDevTypeId(String devTypeId) {
		this.devTypeId = devTypeId;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the params
	 */
	public Vector<DevParaVerInfoVo> getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(Vector<DevParaVerInfoVo> params) {
		this.params = params;
	}

	/**
	 * @return the reportDate
	 */
	public String getReportDate() {
		return reportDate;
	}

	/**
	 * @param reportDate
	 *            the reportDate to set
	 */
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
}
