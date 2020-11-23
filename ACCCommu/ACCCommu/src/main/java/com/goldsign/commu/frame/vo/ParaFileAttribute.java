/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 * 
 * @author hejj
 */
public class ParaFileAttribute {

	private int recordNum = 0;
	private long fileSize = 0;

	/**
	 * @return the recordNum
	 */
	public int getRecordNum() {
		return recordNum;
	}

	/**
	 * @param recordNum
	 *            the recordNum to set
	 */
	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}
