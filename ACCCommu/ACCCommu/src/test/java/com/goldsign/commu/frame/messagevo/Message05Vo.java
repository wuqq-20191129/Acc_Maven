package com.goldsign.commu.frame.messagevo;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-05-31
 * @Time: 9:53
 */
public class Message05Vo extends BaseMessageVo {
    private byte[] stationRepeatNum;
    private String lineStation;
    private String deviceType;
    private String deviceCode;
    private byte[] paraRepeatNum;
    private String paraType;

    public String getParaType() {
        return paraType;
    }

    public void setParaType(String paraType) {
        this.paraType = paraType;
    }

    private String paraCurrentVer;
    private String paraFutureVer;

    public byte[] getStationRepeatNum() {
        return stationRepeatNum;
    }

    public void setStationRepeatNum(byte[] stationRepeatNum) {
        this.stationRepeatNum = stationRepeatNum;
    }

    public String getLineStation() {
        return lineStation;
    }

    public void setLineStation(String lineStation) {
        this.lineStation = lineStation;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public byte[] getParaRepeatNum() {
        return paraRepeatNum;
    }

    public void setParaRepeatNum(byte[] paraRepeatNum) {
        this.paraRepeatNum = paraRepeatNum;
    }

    public String getParaCurrentVer() {
        return paraCurrentVer;
    }

    public void setParaCurrentVer(String paraCurrentVer) {
        this.paraCurrentVer = paraCurrentVer;
    }

    public String getParaFutureVer() {
        return paraFutureVer;
    }

    public void setParaFutureVer(String paraFutureVer) {
        this.paraFutureVer = paraFutureVer;
    }
}
