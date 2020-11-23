/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.frame.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.log4j.Logger;

/**
 * 增加检测未捕获异常处理 v1.13 in 20170928 by lindaquan
 * @author goldsign
 */
public class ExceptionHandler implements UncaughtExceptionHandler {
    
    private static Logger logger = Logger.getLogger(ExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.error("UncaughtException:" + e.getMessage());
    }

}
