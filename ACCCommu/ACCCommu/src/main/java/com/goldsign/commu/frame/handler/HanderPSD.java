package com.goldsign.commu.frame.handler;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.message.HandleMessageBase;

/**
 * 预制票数据文件解析处理<br/>
 * 1，查询数据库，找到需要处理的文件名，更改数据库状态为正在处理状态 <br/>
 * 2，根据文件名+文件目录，找到文件<br/>
 * 3，解析文件<br/>
 * 4，对文件进行基本的校验 <br/>
 * 5，将解析的内容存放到内存中<br/>
 * 
 * @author zhangjh
 */
public class HanderPSD extends HanderTk {

	private static final Logger logger = Logger.getLogger(HanderPSD.class
			.getName());

	@Override
	public void handleMessage(HandleMessageBase msg) {
		tradType = msg.getTradType();
		String fileName = msg.getFileName();
		logger.info("售存数据文件解析处理开始，文件名：" + fileName);
		doParse(msg, fileName);
		logger.info("售存数据文件解析处理结束，文件名：" + fileName);
	}

}
