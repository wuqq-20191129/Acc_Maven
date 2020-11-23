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
public class AnalyzeMessage11 extends com.goldsign.commu.frame.message.AnalyzeMessageBase {
	private static Logger logger = Logger.getLogger(AnalyzeMessage11.class
			.getName());

	@Override
	public TMThreadMsg getMsgKeyInfo(byte[] msg) {
		AnalyzeMessage am = new AnalyzeMessage();
		am.setData(msg);
		TMThreadMsg keyInfo = new TMThreadMsg();
		try {
			String msgId = am.getCharString(0, 2);
			String device = am.getCharString(9, 4);
			String lineId = device.substring(0, 2);
			String stationId = device.substring(2, 4);

			keyInfo.setMsgId(msgId);
			keyInfo.setLineId(lineId);
			keyInfo.setStationId(stationId);
		} catch (CommuException ex) {
			logger.error(ex);

		}
		return keyInfo;
	}

}
