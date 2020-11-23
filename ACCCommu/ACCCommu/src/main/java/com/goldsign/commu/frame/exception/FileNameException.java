/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.exception;

/**
 *
 * @author zjh
 */
public class FileNameException extends BaseException {

    public FileNameException(String msg) {
        super(msg);
    }

    public FileNameException(String msg, String errorCode) {
        super(msg, errorCode);
    }
}
