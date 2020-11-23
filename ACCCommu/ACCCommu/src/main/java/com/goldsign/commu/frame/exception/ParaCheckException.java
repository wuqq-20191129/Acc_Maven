/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.exception;

/**
 * 
 * @author hejj
 */
public class ParaCheckException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5045921702293371520L;
	private String errorCode;

	public ParaCheckException(String s) {
		super(s);
	}

	public ParaCheckException(String s, String errorCode) {
		super(s);
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
