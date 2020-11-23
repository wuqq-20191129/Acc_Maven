package com.goldsign.commu.app.vo;

/**
 * 收益租上交
 * 
 * @author zhangjh
 * 
 */
public class InfoIncomedepHandin extends InfoTkBase {

	/**
	 * 上交类型
	 */
	protected String handinTypeId;
	/**
	 * 数量
	 */
	protected int quantity;
	/**
	 * 报表日期
	 */
	protected String reportDate;

	public String getHandinTypeId() {
		return handinTypeId;
	}

	public void setHandinTypeId(String handinTypeId) {
		this.handinTypeId = handinTypeId;
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

	public String[] toStrArr(String fileName) {
		return new String[] { deptId, handinTypeId, "" + quantity, reportDate,
				ticketTypeId, "" + value, fileName };
	}

}
