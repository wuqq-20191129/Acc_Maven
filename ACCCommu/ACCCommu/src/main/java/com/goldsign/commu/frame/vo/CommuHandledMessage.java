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
public class CommuHandledMessage {
	private String ip = null;
	private Vector<Object> readResult = null;
	private BridgeBetweenConnectionAndMessage bridge = null;

	private String hdlQueueType;
	private String hdlThreadId;

	public CommuHandledMessage(String ip, Vector<Object> readResult) {
		this.ip = ip;
		this.readResult = readResult;

	}

	public CommuHandledMessage(String ip, Vector<Object> readResult,
			BridgeBetweenConnectionAndMessage bridge) {
		this.ip = ip;
		this.readResult = readResult;
		this.bridge = bridge;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Vector<Object> getReadResult() {
		return this.readResult;
	}

	public void setReadResult(Vector<Object> readResult) {
		this.readResult = readResult;
	}

	/**
	 * @return the bridge
	 */
	public BridgeBetweenConnectionAndMessage getBridge() {
		return bridge;
	}

	/**
	 * @param bridge
	 *            the bridge to set
	 */
	public void setBridge(BridgeBetweenConnectionAndMessage bridge) {
		this.bridge = bridge;
	}

	/**
	 * @return the hdlQueueType
	 */
	public String getHdlQueueType() {
		return hdlQueueType;
	}

	/**
	 * @param hdlQueueType
	 *            the hdlQueueType to set
	 */
	public void setHdlQueueType(String hdlQueueType) {
		this.hdlQueueType = hdlQueueType;
	}

	/**
	 * @return the hdlThreadId
	 */
	public String getHdlThreadId() {
		return hdlThreadId;
	}

	/**
	 * @param hdlThreadId
	 *            the hdlThreadId to set
	 */
	public void setHdlThreadId(String hdlThreadId) {
		this.hdlThreadId = hdlThreadId;
	}

}
