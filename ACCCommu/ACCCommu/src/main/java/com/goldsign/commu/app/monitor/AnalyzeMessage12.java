/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.monitor;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.vo.TMThreadMsg;

/**
 * 
 * @author zhangjh
 */
public class AnalyzeMessage12 extends com.goldsign.commu.frame.message.AnalyzeMessageBase {
	private static Logger logger = Logger.getLogger(AnalyzeMessage12.class
			.getName());

	@Override
	public TMThreadMsg getMsgKeyInfo(byte[] msg) {
		AnalyzeMessage am = new AnalyzeMessage();
		am.setData(msg);

		TMThreadMsg keyInfo = new TMThreadMsg();
		try {
			String msgId = am.getCharString(0, 2);
			String stationId = am.getCharString(9, 4);
			String lineId = stationId.substring(0, 2);
			keyInfo.setMsgId(msgId);
			keyInfo.setLineId(lineId);
		} catch (CommuException ex) {
			logger.error(ex);

		}
		return keyInfo;

	}

}
