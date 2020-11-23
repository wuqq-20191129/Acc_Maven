/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.message.MessageBase;

/**
 * 
 * @author hejj
 */
public class BridgeBetweenConnectionAndMessage {
	private String msgType;
	private CommuConnection connection;
	private MessageBase messageProcessor;

	/**
	 * @return the msgType
	 */
	public String getMsgType() {
		return msgType;
	}

	/**
	 * @param msgType
	 *            the msgType to set
	 */
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	/**
	 * @return the connection
	 */
	public CommuConnection getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public void setConnection(CommuConnection connection) {
		this.connection = connection;
	}

	/**
	 * @return the messageProcessor
	 */
	public MessageBase getMessageProcessor() {
		return messageProcessor;
	}

	/**
	 * @param messageProcessor
	 *            the messageProcessor to set
	 */
	public void setMessageProcessor(MessageBase messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

}
