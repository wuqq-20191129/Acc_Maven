package com.goldsign.commu.app.vo;

/**
 * 预制票数据上传
 *
 * @author lindq
 *
 */
public class InfoPreTicketSale extends InfoTkBase {

    //操作员
    protected String operatorId;
    //开始逻辑卡号
    protected String logicalBegin;
    //结束逻辑卡号
    protected String logicalEnd;
    //发售时间
    protected String saleTime;
    //是否生成文件
    protected String fileFlag;
    //生成文件名
    protected String fileName;
    //数量
    protected int quantity;
    //报表日期
    protected String reportDate;
    //原文件名
    protected String tkFileName;

    public String getTkFileName() {
        return tkFileName;
    }

    public void setTkFileName(String tkFileName) {
        this.tkFileName = tkFileName;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getLogicalBegin() {
        return logicalBegin;
    }

    public void setLogicalBegin(String logicalBegin) {
        this.logicalBegin = logicalBegin.trim();
    }

    public String getLogicalEnd() {
        return logicalEnd;
    }

    public void setLogicalEnd(String logicalEnd) {
        this.logicalEnd = logicalEnd.trim();
    }

    public String getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(String saleTime) {
        this.saleTime = saleTime;
    }

    public String getFileFlag() {
        return fileFlag;
    }

    public void setFileFlag(String fileFlag) {
        this.fileFlag = fileFlag;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
    public String[] toStrArr() {
        String fileFlag = "0";
        if(!"0000000000000000".equals(logicalBegin)){
            int num = Integer.parseInt(logicalEnd.substring(7, 16)) - Integer.parseInt(logicalBegin.substring(7, 16))+1;
            //逻辑卡号前半段不一致时，记录状态标志为2:逻辑卡号段不一致
            if(!logicalBegin.substring(0, 7).equals(logicalEnd.substring(0, 7))){
                fileFlag = "2";
            } else if(quantity != num){
                fileFlag = "3";
            } else if(num<1 || num>100000){//逻辑卡号段间数量1~100000
                fileFlag = "4";
            }
        }
        return new String[] { deptId, ticketTypeId, value + "", "" + quantity, logicalBegin, logicalEnd, saleTime, operatorId, reportDate, fileFlag, tkFileName };
    }

}
