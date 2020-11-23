/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

import java.util.Vector;

/**
 * 手机支付平台信息类
 * @author lind
 */
public class MBInfoVo {
    
    private String infoNo;
    private String infoLevel;
    private String addOperator;
    //distribute_date
    private String addDate;
    private String auditDate;
    private String auditOperator;
    private String infoFlag;
    private String distributeResult;
    private String distributeMemo;
    private String remark;
    private String fileName;
    private Vector<MBInfoDetailVo> mBInfoDetails;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }

    public Vector<MBInfoDetailVo> getmBInfoDetails() {
        return mBInfoDetails;
    }

    public void setmBInfoDetails(Vector<MBInfoDetailVo> mBInfoDetails) {
        this.mBInfoDetails = mBInfoDetails;
    }
    
    public String getInfoNo() {
        return infoNo;
    }

    public void setInfoNo(String infoNo) {
        this.infoNo = infoNo;
    }

    public String getInfoLevel() {
        return infoLevel;
    }

    public void setInfoLevel(String infoLevel) {
        this.infoLevel = infoLevel;
    }

    public String getAddOperator() {
        return addOperator;
    }

    public void setAddOperator(String addOperator) {
        this.addOperator = addOperator;
    }

    public String getAuditOperator() {
        return auditOperator;
    }

    public void setAuditOperator(String auditOperator) {
        this.auditOperator = auditOperator;
    }

    public String getInfoFlag() {
        return infoFlag;
    }

    public void setInfoFlag(String infoFlag) {
        this.infoFlag = infoFlag;
    }

    public String getDistributeResult() {
        return distributeResult;
    }

    public void setDistributeResult(String distributeResult) {
        this.distributeResult = distributeResult;
    }

    public String getDistributeMemo() {
        return distributeMemo;
    }

    public void setDistributeMemo(String distributeMemo) {
        this.distributeMemo = distributeMemo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    
}
