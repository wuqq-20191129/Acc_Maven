package com.goldsign.commu.app.vo;

/**
 * 
 * @author zhangjh
 */
public class FileRecordAddVo {

	private int waterNo;
	private String fileName;

	public FileRecordAddVo(int waterNo, String fileName) {
		this.waterNo = waterNo;
		this.fileName = fileName;
	}

	public int getWaterNo() {
		return waterNo;
	}

	public void setWaterNo(int waterNo) {
		this.waterNo = waterNo;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
