package com.goldsign.commu.app.vo;
/**
 * 
 * @author zhangjh
 *
 */
public class FlowHourOrg {

	private String lineId;
	private String stationId;
	private String trafficDatetime;
	private String cardMainType;
	private String cardSubType;
	private String flag;
	private int traffic;

	public FlowHourOrg() {
		super();
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;

	}

	public String getLineId() {
		return this.lineId;

	}

	public void setStationId(String stationId) {
		this.stationId = stationId;

	}

	public String getStationId() {
		return this.stationId;

	}

	public void setTrafficDatetime(String trafficDatetime) {
		this.trafficDatetime = trafficDatetime;

	}

	public String getTrafficDatetime() {
		return this.trafficDatetime;

	}

	public void setCardMainType(String cardMainType) {
		this.cardMainType = cardMainType;

	}

	public String getCardMainType() {
		return this.cardMainType;

	}

	public void setCardSubType(String cardSubType) {
		this.cardSubType = cardSubType;

	}

	public String getCardSubType() {
		return this.cardSubType;

	}

	public void setFlag(String flag) {
		this.flag = flag;

	}

	public String getFlag() {
		return this.flag;

	}

	public void setTraffic(int traffic) {
		this.traffic = traffic;

	}

	public int getTraffic() {
		return this.traffic;

	}
}
