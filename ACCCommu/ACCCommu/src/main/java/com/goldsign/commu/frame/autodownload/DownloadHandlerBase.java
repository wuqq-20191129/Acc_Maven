/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.autodownload;

/**
 * 
 * @author hejj
 */
public abstract class DownloadHandlerBase {

	protected String paramTypeId;

	public abstract void download();

	public String getParmTypeId() {
		return this.paramTypeId;
	}

	public void setParmTypeId(String paramTypeId) {
		this.paramTypeId = paramTypeId;
	}
}
