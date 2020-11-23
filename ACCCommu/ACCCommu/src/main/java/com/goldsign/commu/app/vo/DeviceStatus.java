/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

import java.sql.Timestamp;

/**
 * 
 * @author zhangjh
 */
public class DeviceStatus {

	private Timestamp statusDatetime;
	private String statusDatetimeStr;
	private String lineId;
	private String stationId;
	private String devTypeId;
	private String deviceId;
	private String operatorId;
	private String statusId;
	private String statusValue;
	private String accStatusValue;

	/**
	 * Returns the deviceId.
	 * 
	 * @return String
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Returns the lineId.
	 * 
	 * @return String
	 */
	public String getLineId() {
		return lineId;
	}

	/**
	 * Returns the stationId.
	 * 
	 * @return String
	 */
	public String getStationId() {
		return stationId;
	}

	/**
	 * Returns the statusDatetime.
	 * 
	 * @return Timestamp
	 */
	public Timestamp getStatusDatetime() {
		return statusDatetime;
	}

	/**
	 * Returns the statusId.
	 * 
	 * @return String
	 */
	public String getStatusId() {
		return statusId;
	}

	/**
	 * Returns the statusValue.
	 * 
	 * @return String
	 */
	public String getStatusValue() {
		return statusValue;
	}

	/**
	 * Sets the deviceId.
	 * 
	 * @param deviceId
	 *            The deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * Sets the lineId.
	 * 
	 * @param lineId
	 *            The lineId to set
	 */
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	/**
	 * Sets the stationId.
	 * 
	 * @param stationId
	 *            The stationId to set
	 */
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	/**
	 * Sets the statusDatetime.
	 * 
	 * @param statusDatetime
	 *            The statusDatetime to set
	 */
	public void setStatusDatetime(Timestamp statusDatetime) {
		this.statusDatetime = statusDatetime;
	}

	/**
	 * Sets the statusId.
	 * 
	 * @param statusId
	 *            The statusId to set
	 */
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	/**
	 * Sets the statusValue.
	 * 
	 * @param statusValue
	 *            The statusValue to set
	 */
	public void setStatusValue(String statusValue) {
		this.statusValue = statusValue;
	}

	/**
	 * Returns the devTypeId.
	 * 
	 * @return String
	 */
	public String getDevTypeId() {
		return devTypeId;
	}

	/**
	 * Sets the devTypeId.
	 * 
	 * @param devTypeId
	 *            The devTypeId to set
	 */
	public void setDevTypeId(String devTypeId) {
		this.devTypeId = devTypeId;
	}

	public String getAccStatusValue() {
		return accStatusValue;
	}

	public void setAccStatusValue(String accStatusValue) {
		this.accStatusValue = accStatusValue;
	}

	/**
	 * Returns the operatorId.
	 * 
	 * @return String
	 */
	public String getOperatorId() {
		return operatorId;
	}

	/**
	 * Sets the operatorId.
	 * 
	 * @param operatorId
	 *            The operatorId to set
	 */
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	/**
	 * Returns the statusDatetimeStr.
	 * 
	 * @return String
	 */
	public String getStatusDatetimeStr() {
		return statusDatetimeStr;
	}

	/**
	 * Sets the statusDatetimeStr.
	 * 
	 * @param statusDatetimeStr
	 *            The statusDatetimeStr to set
	 */
	public void setStatusDatetimeStr(String statusDatetimeStr) {
		this.statusDatetimeStr = statusDatetimeStr;
	}
}
