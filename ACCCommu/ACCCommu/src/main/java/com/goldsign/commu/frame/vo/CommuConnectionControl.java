/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 * 
 * @author hejj
 */
public class CommuConnectionControl {

	private boolean closed = false;

	public CommuConnectionControl() {
	}

	public void setClosed(boolean isClosed) {
		this.closed = isClosed;
	}

	public boolean getClosed() {
		return this.closed;
	}
}
