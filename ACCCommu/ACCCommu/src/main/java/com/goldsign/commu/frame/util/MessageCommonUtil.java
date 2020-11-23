package com.goldsign.commu.frame.util;

import java.text.ParseException;

import com.goldsign.commu.frame.constant.FrameCodeConstant;

/**
 * 
 * @author hejj
 */
public class MessageCommonUtil {

	public static boolean isInPriorityMsg(String messageId) {
		return (messageId.equals("12") || messageId.equals("02")
				|| messageId.equals("04") || messageId.equals("16"));
	}

	public static boolean isNomalDateMessageForTraffic(String msgDate,
			String cur) throws ParseException {
		if (FrameCodeConstant.trafficDelayMaxDay == FrameCodeConstant.trafficDelayUnLimit) {
			return true;
		}
		long n = DateHelper.getDifferInDays(msgDate, cur);
		if (n <= FrameCodeConstant.trafficDelayMaxDay) {
			return true;
		}
		return false;

	}
}
