/*
 * Amendment History:
 * 
 * Date          By             Description
 * ----------    -----------    -------------------------------------------
 * 2005-04-22    Rong Weitao    Create the class
 */

package com.goldsign.commu.app.vo;

import java.sql.Timestamp;

public class DeviceEvent {

	private Timestamp eventDatetime;
	private String lineId;
	private String stationId;
	private String devTypeId;
	private String deviceId;
	private String eventId;
	private String eventArgument;

	/**
	 * Returns the deviceId.
	 * 
	 * @return String
	 */
	public String getDeviceId() {
		return deviceId;
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
	 * Returns the eventDatetime.
	 * 
	 * @return Timestamp
	 */
	public Timestamp getEventDatetime() {
		return eventDatetime;
	}

	/**
	 * Returns the eventId.
	 * 
	 * @return String
	 */
	public String getEventId() {
		return eventId;
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
	 * Sets the deviceId.
	 * 
	 * @param deviceId
	 *            The deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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

	/**
	 * Sets the eventDatetime.
	 * 
	 * @param eventDatetime
	 *            The eventDatetime to set
	 */
	public void setEventDatetime(Timestamp eventDatetime) {
		this.eventDatetime = eventDatetime;
	}

	/**
	 * Sets the eventId.
	 * 
	 * @param eventId
	 *            The eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
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
	 * Returns the eventArgument.
	 * 
	 * @return String
	 */
	public String getEventArgument() {
		return eventArgument;
	}

	/**
	 * Sets the eventArgument.
	 * 
	 * @param eventArgument
	 *            The eventArgument to set
	 */
	public void setEventArgument(String eventArgument) {
		this.eventArgument = eventArgument;
	}

}
