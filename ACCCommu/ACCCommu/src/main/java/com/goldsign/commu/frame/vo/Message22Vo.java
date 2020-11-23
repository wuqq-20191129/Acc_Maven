/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 * 挂失/解挂申请审核（22） v0.74
 * date in 20150527
 * @author lindaquan
 */
public class Message22Vo {
    private String ID;
    private String busnissType;//业务类型
    private String currentTod;// 消息生成时间
    private String resultCode;//结果


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBusnissType() {
        return busnissType;
    }

    public void setBusnissType(String busnissType) {
        this.busnissType = busnissType;
    }

    public String getCurrentTod() {
        return currentTod;
    }

    public void setCurrentTod(String currentTod) {
        this.currentTod = currentTod;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}
