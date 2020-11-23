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
public class DevParaVerVo {

	private Vector<DevParaVerAddressVo> addressSend;
	private String waterNo;
	private String lineId;
	private String lccLineId;
	private String stationId;
	private String devTypeId;
	private String deviceId;
	private String status;
	private String queryDate;
	private String send_date;
	private String operatorId;
	private String remark;

	/**
	 * @return the waterNo
	 */
	public String getWaterNo() {
		return waterNo;
	}

	/**
	 * @param waterNo
	 *            the waterNo to set
	 */
	public void setWaterNo(String waterNo) {
		this.waterNo = waterNo;
	}

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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the queryDate
	 */
	public String getQueryDate() {
		return queryDate;
	}

	/**
	 * @param queryDate
	 *            the queryDate to set
	 */
	public void setQueryDate(String queryDate) {
		this.queryDate = queryDate;
	}

	/**
	 * @return the send_date
	 */
	public String getSend_date() {
		return send_date;
	}

	/**
	 * @param send_date
	 *            the send_date to set
	 */
	public void setSend_date(String send_date) {
		this.send_date = send_date;
	}

	/**
	 * @return the operatorId
	 */
	public String getOperatorId() {
		return operatorId;
	}

	/**
	 * @param operatorId
	 *            the operatorId to set
	 */
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
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
	 * @return the lccLineId
	 */
	public String getLccLineId() {
		return lccLineId;
	}

	/**
	 * @param lccLineId
	 *            the lccLineId to set
	 */
	public void setLccLineId(String lccLineId) {
		this.lccLineId = lccLineId;
	}

	/**
	 * @return the addressSend
	 */
	public Vector<DevParaVerAddressVo> getAddressSend() {
		return addressSend;
	}

	/**
	 * @param addressSend
	 *            the addressSend to set
	 */
	public void setAddressSend(Vector<DevParaVerAddressVo> addressSend) {
		this.addressSend = addressSend;
	}
}
