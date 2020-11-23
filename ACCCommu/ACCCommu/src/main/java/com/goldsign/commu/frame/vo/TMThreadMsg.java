package com.goldsign.commu.frame.vo;

/**
 * 
 * @author hejj
 */
public class TMThreadMsg implements Comparable {

	private String msgId = "";
	private String lineId = "";
	private String stationId = "";

	/**
	 * @return the msgId
	 */
	public String getMsgId() {
		return msgId;
	}

	/**
	 * @param msgId
	 *            the msgId to set
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	/**
	 * @return the lineId
	 */
	public String getLineId() {
		return lineId;
	}

	/**
	 * @param lineId
	 *            the lineId to set
	 */
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	/**
	 * @return the stationId
	 */
	public String getStationId() {
		return stationId;
	}

	/**
	 * @param stationId
	 *            the stationId to set
	 */
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public int compareTo(Object o) {
		if ((o instanceof com.goldsign.commu.frame.vo.TMThreadMsg) == false) {
			throw new UnsupportedOperationException("非同类对象不能比较");
		}
		TMThreadMsg to = (TMThreadMsg) o;
		return (this.getMsgId() + this.getLineId() + this.getStationId())
				.compareTo((to.getMsgId() + to.getLineId() + to.getStationId()));
	}
}
