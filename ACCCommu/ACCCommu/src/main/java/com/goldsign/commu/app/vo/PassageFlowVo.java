package com.goldsign.commu.app.vo;

import java.util.ArrayList;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-23
 * @Time: 16:44
 */
public class PassageFlowVo {
    private String lineId;
    private String stationId;
    private String cardMainType;
    private String cardSubType;
    private String totalBeginTime;
    private String totalEndTime;
    private String trafficDatetime;
    private String flag;
    private int trafficNum;

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

    public String getCardMainType() {
        return cardMainType;
    }

    public void setCardMainType(String cardMainType) {
        this.cardMainType = cardMainType;
    }

    public String getCardSubType() {
        return cardSubType;
    }

    public void setCardSubType(String cardSubType) {
        this.cardSubType = cardSubType;
    }

    public String getTotalBeginTime() {
        return totalBeginTime;
    }

    public void setTotalBeginTime(String totalBeginTime) {
        this.totalBeginTime = totalBeginTime;
    }

    public String getTotalEndTime() {
        return totalEndTime;
    }

    public void setTotalEndTime(String totalEndTime) {
        this.totalEndTime = totalEndTime;
    }

    public String getTrafficDatetime() {
        return trafficDatetime;
    }

    public void setTrafficDatetime(String trafficDatetime) {
        this.trafficDatetime = trafficDatetime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getTrafficNum() {
        return trafficNum;
    }

    public void setTrafficNum(int trafficNum) {
        this.trafficNum = trafficNum;
    }
}
