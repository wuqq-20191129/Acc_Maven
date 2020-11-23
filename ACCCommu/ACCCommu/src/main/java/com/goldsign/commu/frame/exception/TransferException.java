package com.goldsign.commu.frame.exception;

public class TransferException extends BaseException {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 6201855813676710119L;

	public TransferException(String s) {
		super(s);
	}

	public TransferException(String s, String errorCode) {
		super(s, errorCode);

	}
}
