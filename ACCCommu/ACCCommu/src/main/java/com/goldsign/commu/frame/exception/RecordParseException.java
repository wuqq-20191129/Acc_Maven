/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.exception;

/**
 *
 * @author Administrator
 */
public class RecordParseException extends BaseException {

    public RecordParseException(String s) {
        super(s);
    }

    public RecordParseException(String s, String errorCode) {
        super(s, errorCode);

    }
}
