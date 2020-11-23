package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.vo.CommuHandledMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class CommuThreadLogUtil {

	public static final String unHandleName = "unHandleMsg.log";
	public static final String errName = "ErrHandleMsg.log";
	private static Logger logger = Logger.getLogger(CommuThreadLogUtil.class
			.getName());

	public synchronized static void writeUnHandleMsgToFile(String threadName,
			Vector<CommuHandledMessage> msgs) {
		// if(msgs == null || msgs.isEmpty() )
		// return;

		String fileName = FrameCodeConstant.unHanledMsgLogDir + "/"
				+ unHandleName;
		File f = null;
		FileOutputStream fos = null;
		byte[] data = null;
		String msgID = "";
		String head = "";
		String biValue = "";
		try {
			f = new File(fileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			fos = new FileOutputStream(f, true);
			if (msgs == null || msgs.isEmpty()) {
				head = DateHelper.datetimeToString(new Date(System
						.currentTimeMillis()))
						+ "   线程"
						+ threadName
						+ "未处理消息 " + msgID + "\r\n";
				fos.write(head.getBytes("GB18030"));
				fos.write(head.getBytes("GB18030"));

			} else {
				for (int i = 0; i < msgs.size(); i++) {
					data = (byte[]) (((CommuHandledMessage) msgs.get(i))
							.getReadResult()).get(1);
					msgID = "" + (char) data[0] + (char) data[1];
					head = "\r\n"
							+ DateHelper.datetimeToString(new Date(System
									.currentTimeMillis())) + "   线程"
							+ threadName + "未处理消息 " + msgID + "\r\n";
					fos.write(head.getBytes("GB18030"));
					for (int j = 0; j < data.length; j++) {
						// value =(byte)(0x30+data[j]);
						biValue = Integer.toHexString((int) data[j]);
						fos.write(biValue.getBytes());
					}

					// fos.write(data);
				}

			}

			fos.flush();

		} catch (IOException e) {
			logger.error("写未处理消息到文件失败" + e.getMessage());

		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			}

		}

	}

	public synchronized static void writeErrHandleMsgToFile(String threadName,
			Vector<Object> msg) {
		// if(msgs == null || msgs.isEmpty() )
		// return;

		String fileName = FrameCodeConstant.unHanledMsgLogDir + "/" + errName;
		File f = null;
		FileOutputStream fos = null;
		String msgID = "";
		String head = "";
		byte[] data = (byte[]) msg.get(1);
		String biValue = "";
		try {
			f = new File(fileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			fos = new FileOutputStream(f, true);

			msgID = "" + (char) data[0] + (char) data[1];
			head = "\r\n"
					+ DateHelper.datetimeToString(new Date(System
							.currentTimeMillis())) + "   线程" + threadName
					+ "未处理消息 " + msgID + "\r\n";
			fos.write(head.getBytes("GB18030"));
			for (int j = 0; j < data.length; j++) {
				// value =(byte)(0x30+data[j]);
				biValue = Integer.toHexString((int) data[j]);
				fos.write(biValue.getBytes());
			}

			// fos.write(data);

			fos.flush();

		} catch (IOException e) {
			logger.error("写未处理消息到文件失败", e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			}

		}

	}

	public CommuThreadLogUtil() {
	}
}
