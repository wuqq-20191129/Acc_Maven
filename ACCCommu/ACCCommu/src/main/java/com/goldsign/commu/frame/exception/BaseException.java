/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.exception;

/**
 * 
 * @author zhangjh
 */
public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3462860641019885857L;
	private String errorCode;

	public BaseException(String msg) {
		super(msg);
	}

	public BaseException(String msg, String errorCode) {
		super(msg);
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
