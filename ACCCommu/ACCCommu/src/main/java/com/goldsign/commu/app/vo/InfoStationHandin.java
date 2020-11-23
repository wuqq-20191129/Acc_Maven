package com.goldsign.commu.app.vo;

/**
 * 车站上交数据
 * 
 * @author zhangjh
 * 
 */
public class InfoStationHandin extends InfoIncomedepHandin {

	/**
	 * 是否弃票
	 */
	protected String isAbandon;

	/**
	 * 起始逻辑卡号
	 */
	protected String idStart;
	/**
	 * 终止逻辑卡号
	 */
	protected String idEnd;
	/**
	 * 备注
	 */
	protected String remark;

	public String getIsAbandon() {
		return isAbandon;
	}

	public void setIsAbandon(String isAbandon) {
		this.isAbandon = isAbandon;
	}

	public String getIdEnd() {
		return idEnd;
	}

	public void setIdEnd(String idEnd) {
		this.idEnd = idEnd;
	}

	public String getIdStart() {
		return idStart;
	}

	public void setIdStart(String idStart) {
		this.idStart = idStart;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String[] toStrArr(String fileName) {
		return new String[] { deptId, handinTypeId, idEnd,idStart,isAbandon,"" + quantity, remark,reportDate,
				ticketTypeId, "" + value, fileName};
	}
}
