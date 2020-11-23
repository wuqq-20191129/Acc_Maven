/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 *
 * @author Administrator
 */
public class FileLogVo {

    private String fileName;
    private String balanceWaterNo;
    private String genTime;
    private String fileType;

    public FileLogVo(String fileName, String balanceWaterNo, String fileType) {
        this.fileName = fileName;
        this.balanceWaterNo = balanceWaterNo;
        this.fileType = fileType;
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
     * @return the balanceWaterNo
     */
    public String getBalanceWaterNo() {
        return balanceWaterNo;
    }

    /**
     * @param balanceWaterNo the balanceWaterNo to set
     */
    public void setBalanceWaterNo(String balanceWaterNo) {
        this.balanceWaterNo = balanceWaterNo;
    }

    /**
     * @return the genTime
     */
    public String getGenTime() {
        return genTime;
    }

    /**
     * @param genTime the genTime to set
     */
    public void setGenTime(String genTime) {
        this.genTime = genTime;
    }

    /**
     * @return the fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
