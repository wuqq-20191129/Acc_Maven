/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 * 挂失/解挂申请/挂失补卡申请（21）
 * @author lindaquan
 */
public class Message21Vo {

    private String currentTod;// 消息生成时间
    private String currentBom;// 当前BOM
    private String clineId;// 当前BOM的线路ID
    private String cstationId;// 当前BOM的车站ID
    private String cdevTypeId;// 当前BOM的设备类型
    private String cdeviceId;// 当前BOM的设备ID

    private String busnissType;//业务类型
    private String IDCardType;//证件类型
    private String IDNumber;//证件号码
    
    private String cardType;//票卡类型
    private String cardLogicalID;//逻辑卡号

    /*
        即凭证组成格式为YYYYMMDD-SS-STID-NNN-RRRR。运营日-BOM班次序号-STID为BOM所属车站编号-RRRR班次交易序号
        如20131228-03-0101-001-0009 表示车站0101的001号BOM在2013年12月28日第3个班次产生的第9号交易。
     */
    private String applyBill;// 凭证
    private String tkTime;// 运营日
    private String shiftId;// BOM班次序号
    private String lineId;// STID为BOM所属线路编号
    private String stationId;// STID为BOM所属车站编号
    private String devTypeId;// BOM的设备类型
    private String deviceId;// BOM的设备ID

    private String dealId;// RRRR班次交易序号
    private String action;// 操作类型

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardLogicalID() {
        return cardLogicalID;
    }

    public void setCardLogicalID(String cardLogicalID) {
        this.cardLogicalID = cardLogicalID;
    }

    public String getCurrentTod() {
        return currentTod;
    }

    public void setCurrentTod(String currentTod) {
        this.currentTod = currentTod;
    }

    public String getCurrentBom() {
        return currentBom;
    }

    public void setCurrentBom(String currentBom) {
        this.currentBom = currentBom;
    }

    public String getClineId() {
        return clineId;
    }

    public void setClineId(String clineId) {
        this.clineId = clineId;
    }

    public String getCstationId() {
        return cstationId;
    }

    public void setCstationId(String cstationId) {
        this.cstationId = cstationId;
    }

    public String getCdevTypeId() {
        return cdevTypeId;
    }

    public void setCdevTypeId(String cdevTypeId) {
        this.cdevTypeId = cdevTypeId;
    }

    public String getCdeviceId() {
        return cdeviceId;
    }

    public void setCdeviceId(String cdeviceId) {
        this.cdeviceId = cdeviceId;
    }

    public String getBusnissType() {
        return busnissType;
    }

    public void setBusnissType(String busnissType) {
        this.busnissType = busnissType;
    }

    public String getIDCardType() {
        return IDCardType;
    }

    public void setIDCardType(String IDCardType) {
        this.IDCardType = IDCardType;
    }

    public String getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(String IDNumber) {
        this.IDNumber = IDNumber;
    }

    public String getApplyBill() {
        return applyBill;
    }

    public void setApplyBill(String applyBill) {
        this.applyBill = applyBill;
    }

    public String getTkTime() {
        return tkTime;
    }

    public void setTkTime(String tkTime) {
        this.tkTime = tkTime;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getDevTypeId() {
        return devTypeId;
    }

    public void setDevTypeId(String devTypeId) {
        this.devTypeId = devTypeId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "Message21Vo{" + "currentTod=" + currentTod + ", currentBom=" + currentBom + ", clineId=" + clineId + ", cstationId=" + cstationId + ", cdevTypeId=" + cdevTypeId + ", cdeviceId=" + cdeviceId + ", busnissType=" + busnissType + ", IDCardType=" + IDCardType + ", IDNumber=" + IDNumber + ", applyBill=" + applyBill + ", tkTime=" + tkTime + ", shiftId=" + shiftId + ", lineId=" + lineId + ", stationId=" + stationId + ", devTypeId=" + devTypeId + ", deviceId=" + deviceId + ", dealId=" + dealId + ", action=" + action + '}';
    }
    
}
