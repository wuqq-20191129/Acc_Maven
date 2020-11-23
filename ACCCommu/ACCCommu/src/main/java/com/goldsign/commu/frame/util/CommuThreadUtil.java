package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.vo.CommuHandledMessage;

/**
 * 
 * @author hejj
 */
public class CommuThreadUtil {

	public CommuThreadUtil() {
	}

	public String getBcdString(byte[] data, int offset, int length)
			throws CommuException {
		StringBuffer sb = new StringBuffer();
		try {
			for (int i = 0; i < length; i++) {
				sb.append(byte1ToBcd2(data[offset + i]));
			}
		} catch (Exception e) {
			throw new CommuException(" " + e);
		}
		return sb.toString();
	}

	private String byte1ToBcd2(int i) {
		return (new Integer(i / 16)).toString()
				+ (new Integer(i % 16)).toString();
	}

	// 进出站客流信息
	public boolean isTraffic(CommuHandledMessage msg) {
		byte[] data = (byte[]) msg.getReadResult().get(1);
		String messageId = null;

		if (data == null || data.length == 0) {
			return false;
		}
		messageId = "" + (char) data[0] + (char) data[1];
		if (messageId.equals("13") || messageId.equals("14")
				|| messageId.equals("15")) {
			return true;
		}
		return false;
	}

	public boolean isNonReturnMsg(CommuHandledMessage msg) {
		byte[] data = (byte[]) msg.getReadResult().get(1);
		String messageId = null;

		if (data == null || data.length == 0) {
			return false;
		}
		messageId = "" + (char) data[0] + (char) data[1];
		if (messageId.equals("16")) {
			return true;
		}
		return false;
	}
        
        // 20150520添加挂失内容 by lindaquan v0.74
        public boolean isLossReportMsg(CommuHandledMessage msg) {
		byte[] data = (byte[]) msg.getReadResult().get(1);
		String messageId = null;

		if (data == null || data.length == 0) {
			return false;
		}
		messageId = "" + (char) data[0] + (char) data[1];
		if (messageId.equals("21")) {
			return true;
		}
		return false;
	}

	public boolean isDegradeMode(CommuHandledMessage msg) {
		byte[] data = (byte[]) msg.getReadResult().get(1);
		String messageId = null;
		String device = null;
		// String lineID = null;
		// String stationID = null;
		String deviceTypeID = null;
		String deviceID = null;
		CommuThreadUtil util = null;
		int repeatCount = -1;
		String statusID = null;
		int iStatusID = -1;
		String status = null;

		int offset = -1;

		if (data == null || data.length == 0) {
			return false;
		}
		messageId = "" + (char) data[0] + (char) data[1];
		if (!messageId.equals("10")) {
			return false;
		}
		util = new CommuThreadUtil();
		try {
			device = util.getBcdString(data, 9, 5);
			repeatCount = util.getInt(data, 17);

			// lineID = device.substring(0, 2);
			// stationID = device.substring(2, 4);
			deviceTypeID = device.substring(4, 6);
			deviceID = device.substring(6, 10);

			offset = 18;
			for (int i = 0; i < repeatCount; i++) {
				status = util.getBcdString(data, offset, 2);
				statusID = status.substring(0, 3);
				iStatusID = Integer.parseInt(statusID);
				if (iStatusID >= 1 && iStatusID <= 6
						&& deviceTypeID.equals("01") && deviceID.equals("0000")) {
					// logger.info("优先处理降级模式");
					return true;
				}
				offset += 9;
			}
		} catch (CommuException e) {
			return false;

		} catch (Exception e) {
			return false;

		}
		return false;
	}

	public int getInt(byte[] data, int offset) {
		return byteToInt(data[offset]);
	}

	private int byteToInt(byte b) {
		int i = 0;
		if (b < 0) {
			i = 256 + b;
		} else {
			i = b;
		}
		return i;
	}
}
