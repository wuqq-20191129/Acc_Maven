/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

import java.util.List;

/**
 * 逻辑卡号查询
 * @author lindaquan
 */
public class Message23Vo {

    private String messageId;
    private String currentTod;// 消息生成时间
    private String currentBom;// 当前BOM
    private String IDCardType;//证件类型
    private String IDNumber;//证件号码
    private List<CardInfo> cardInfos;//卡信息
    private List<BusinessInfo> businessInfos;//业务信息

    public List<BusinessInfo> getBusinessInfos() {
        return businessInfos;
    }

    public void setBusinessInfos(List<BusinessInfo> businessInfos) {
        this.businessInfos = businessInfos;
    }

    public List<CardInfo> getCardInfos() {
        return cardInfos;
    }

    public void setCardInfos(List<CardInfo> cardInfos) {
        this.cardInfos = cardInfos;
    }
    
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    
}
