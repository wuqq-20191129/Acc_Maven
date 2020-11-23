/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

import java.util.HashMap;

/**
 *
 * @author zhangjh
 */
public class FileRecordBase {

    private int waterNo;//流水号
    /**
     * 设备相关
     */
    private String lineId;//线路ID
    private String stationId;//车站ID
    private String devTypeId;//设备类型ID
    private String deviceId;//设备ID
    /**
     * 卡相关
     *
     */
    private String cardMainId;//票卡主类型（5）
    private String cardSubId;//票卡子类型（5）
    private String cardLogicalId;//票卡逻辑卡号（6）
    private String cardPhysicalId;//票卡物理卡号（7）
    private String cardAppFlag;//应用标识（19）
    private int cardStatusId;//卡状态
    /**
     * SAM
     */
    private String samLogicalId;//SAM卡逻辑卡号（10）
    /**
     * 操作
     */
    private String operatorId;//操作员ID（16）
    private String shiftId;//BOM班次序号（17）
    private String trdType;//交易类型
    private int balanceWaterNo;//清算流水号
    private String fileName;//文件名
    private String checkFlag;//校验标志
    private int samTradeSeq; //本次交易SAM卡脱机交易流水号
    private HashMap subRecords;

    /**
     * @return the lineId
     */
    public String getLineId() {
        return lineId;
    }

    /**
     * @param lineId the lineId to set
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
     * @param stationId the stationId to set
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
     * @param devTypeId the devTypeId to set
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
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return the balanceWaterNo
     */
    public int getBalanceWaterNo() {
        return balanceWaterNo;
    }

    /**
     * @param balanceWaterNo the balanceWaterNo to set
     */
    public void setBalanceWaterNo(int balanceWaterNo) {
        this.balanceWaterNo = balanceWaterNo;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the checkFlag
     */
    public String getCheckFlag() {
        return checkFlag;
    }

    /**
     * @param checkFlag the checkFlag to set
     */
    public void setCheckFlag(String checkFlag) {
        this.checkFlag = checkFlag;
    }

    /**
     * @return the waterNo
     */
    public int getWaterNo() {
        return waterNo;
    }

    /**
     * @param waterNo the waterNo to set
     */
    public void setWaterNo(int waterNo) {
        this.waterNo = waterNo;
    }

    /**
     * @return the cardMainId
     */
    public String getCardMainId() {
        return cardMainId;
    }

    /**
     * @param cardMainId the cardMainId to set
     */
    public void setCardMainId(String cardMainId) {
        this.cardMainId = cardMainId;
    }

    /**
     * @return the cardSubId
     */
    public String getCardSubId() {
        return cardSubId;
    }

    /**
     * @param cardSubId the cardSubId to set
     */
    public void setCardSubId(String cardSubId) {
        this.cardSubId = cardSubId;
    }

    /**
     * @return the cardLogicalId
     */
    public String getCardLogicalId() {
        return cardLogicalId;
    }

    /**
     * @param cardLogicalId the cardLogicalId to set
     */
    public void setCardLogicalId(String cardLogicalId) {
        this.cardLogicalId = cardLogicalId;
    }

    /**
     * @return the cardPhysicalId
     */
    public String getCardPhysicalId() {
        return cardPhysicalId;
    }

    /**
     * @param cardPhysicalId the cardPhysicalId to set
     */
    public void setCardPhysicalId(String cardPhysicalId) {
        this.cardPhysicalId = cardPhysicalId;
    }

    /**
     * @return the cardAppFlag
     */
    public String getCardAppFlag() {
        return cardAppFlag;
    }

    /**
     * @param cardAppFlag the cardAppFlag to set
     */
    public void setCardAppFlag(String cardAppFlag) {
        this.cardAppFlag = cardAppFlag;
    }

    /**
     * @return the samLogicalId
     */
    public String getSamLogicalId() {
        return samLogicalId;
    }

    /**
     * @param samLogicalId the samLogicalId to set
     */
    public void setSamLogicalId(String samLogicalId) {
        this.samLogicalId = samLogicalId;
    }

    /**
     * @return the operatorId
     */
    public String getOperatorId() {
        return operatorId;
    }

    /**
     * @param operatorId the operatorId to set
     */
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * @return the shiftId
     */
    public String getShiftId() {
        return shiftId;
    }

    /**
     * @param shiftId the shiftId to set
     */
    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    /**
     * @return the trdType
     */
    public String getTrdType() {
        return trdType;
    }

    /**
     * @param trdType the trdType to set
     */
    public void setTrdType(String trdType) {
        this.trdType = trdType;
    }

    /**
     * @return the samTradeSeq
     */
    public int getSamTradeSeq() {
        return samTradeSeq;
    }

    /**
     * @param samTradeSeq the samTradeSeq to set
     */
    public void setSamTradeSeq(int samTradeSeq) {
        this.samTradeSeq = samTradeSeq;
    }

    /**
     * @return the cardStatusId
     */
    public int getCardStatusId() {
        return cardStatusId;
    }

    /**
     * @param cardStatusId the cardStatusId to set
     */
    public void setCardStatusId(int cardStatusId) {
        this.cardStatusId = cardStatusId;
    }

    /**
     * @return the subRecords
     */
    public HashMap getSubRecords() {
        return subRecords;
    }

    /**
     * @param subRecords the subRecords to set
     */
    public void setSubRecords(HashMap subRecords) {
        this.subRecords = subRecords;
    }
}
