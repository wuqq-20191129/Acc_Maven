/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.vo;

/**
 * @datetime 2018-4-12 11:41:22
 * @author lind
 * 业务信息
 */
public class BusinessInfo {
    
    private String BusyType;//业务类型
    private String ApplyBill;//凭证号

    public String getBusyType() {
        return BusyType;
    }

    public void setBusyType(String BusyType) {
        this.BusyType = BusyType;
    }

    public String getApplyBill() {
        return ApplyBill;
    }

    public void setApplyBill(String ApplyBill) {
        this.ApplyBill = ApplyBill;
    }
    

}
