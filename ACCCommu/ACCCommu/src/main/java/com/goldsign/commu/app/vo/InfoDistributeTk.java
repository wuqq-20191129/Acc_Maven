package com.goldsign.commu.app.vo;

/**
 * 配票数据
 * 
 * @author zhangjh
 * 
 */
public class InfoDistributeTk extends InfoTkBase {

	/**
	 * 有效期
	 */
	private String validDate;
	/**
	 * 限制使用模式
	 */
	private String model;
	/**
	 * 限制进站代码
	 */
	private String entryStation;
	/**
	 * 限制出站代码
	 */
	private String exitStation;
	/**
	 * 数量
	 */
	private int quantity;
	/**
	 * 配票时间
	 */
	private String distDate;

	public String getValidDate() {
		return validDate;
	}

	public void setValidDate(String effective) {
		this.validDate = effective;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getEntryStation() {
		return entryStation;
	}

	public void setEntryStation(String inStation) {
		this.entryStation = inStation;
	}

	public String getExitStation() {
		return exitStation;
	}

	public void setExitStation(String exitStation) {
		this.exitStation = exitStation;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getDistDate() {
		return distDate;
	}

	public void setDistDate(String distDate) {
		this.distDate = distDate;
	}

	public String[] toStrArr() {
		return new String[] { deptId, ticketTypeId, value + "", validDate,
				model, entryStation, exitStation, "" + quantity, distDate, "0" };
	}

}
