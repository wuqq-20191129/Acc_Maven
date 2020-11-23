package com.goldsign.commu.app.vo;

/**
 * 售存数据
 * 
 * @author zhangjh
 * 
 */
public class InfoStationSale extends InfoTkBase {

	/**
	 * 发售数量
	 */
	protected int quantitySale;
	/**
	 * 闸机单程票回收数量
	 */
	protected int quantityRec;
	/**
	 * 车票本日结存数
	 */
	protected int balance;
	/**
	 * 报表日期
	 */
	protected String reportDate;

	public int getQuantitySale() {
		return quantitySale;
	}

	public void setQuantitySale(int quantitySale) {
		this.quantitySale = quantitySale;
	}

	public int getQuantityRec() {
		return quantityRec;
	}

	public void setQuantityRec(int quantityRec) {
		this.quantityRec = quantityRec;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String[] toStrArr(String fileName) {

		return new String[] { deptId, ticketTypeId, value + "",
				"" + quantitySale, "" + quantityRec, "" + balance, reportDate, fileName };
	}

}
