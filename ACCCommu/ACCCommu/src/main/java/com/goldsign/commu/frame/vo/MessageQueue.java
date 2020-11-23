/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author hejj
 */
public class MessageQueue implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -548492306913328574L;
    private long messageId;
    private Date messageTime;
    private String lineId;
    private String stationId;
    private String ipAddress;
    private byte[] message;
    private String processFlag;
    private String isParaInformMsg; // '1' this message is a parameter
    // distribute inform message
    private int paraInformWaterNo; // corresponding water_no in op_prm_para_inform_dtl
    private String messageType;
    private String messageTypeSub;
    private String messageRemark;

    public String getMessageTypeSub() {
        return messageTypeSub;
    }

    public void setMessageTypeSub(String messageTypeSub) {
        this.messageTypeSub = messageTypeSub;
    }

    public String getMessageRemark() {
        return messageRemark;
    }

    public void setMessageRemark(String messageRemark) {
        this.messageRemark = messageRemark;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
     * Returns the ipAddress.
     *
     * @return String
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Returns the isParaInformMsg.
     *
     * @return String
     */
    public String getIsParaInformMsg() {
        return isParaInformMsg;
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
     * Returns the message.
     *
     * @return byte[]
     */
    public byte[] getMessage() {
        return message;
    }

    /**
     * Returns the messageId.
     *
     * @return long
     */
    public long getMessageId() {
        return messageId;
    }

    /**
     * Returns the messageTime.
     *
     * @return Date
     */
    public Date getMessageTime() {
        return messageTime;
    }

    /**
     * Returns the paraInformWaterNo.
     *
     * @return int
     */
    public int getParaInformWaterNo() {
        return paraInformWaterNo;
    }

    /**
     * Returns the processFlag.
     *
     * @return String
     */
    public String getProcessFlag() {
        return processFlag;
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
     * Sets the ipAddress.
     *
     * @param ipAddress The ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Sets the isParaInformMsg.
     *
     * @param isParaInformMsg The isParaInformMsg to set
     */
    public void setIsParaInformMsg(String isParaInformMsg) {
        this.isParaInformMsg = isParaInformMsg;
    }

    /**
     * Sets the lineId.
     *
     * @param lineId The lineId to set
     */
    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    /**
     * Sets the message.
     *
     * @param message The message to set
     */
    public void setMessage(byte[] message) {
        this.message = message;
    }

    /**
     * Sets the messageId.
     *
     * @param messageId The messageId to set
     */
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    /**
     * Sets the messageTime.
     *
     * @param messageTime The messageTime to set
     */
    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    /**
     * Sets the paraInformWaterNo.
     *
     * @param paraInformWaterNo The paraInformWaterNo to set
     */
    public void setParaInformWaterNo(int paraInformWaterNo) {
        this.paraInformWaterNo = paraInformWaterNo;
    }

    /**
     * Sets the processFlag.
     *
     * @param processFlag The processFlag to set
     */
    public void setProcessFlag(String processFlag) {
        this.processFlag = processFlag;
    }

    /**
     * Sets the stationId.
     *
     * @param stationId The stationId to set
     */
    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

}
