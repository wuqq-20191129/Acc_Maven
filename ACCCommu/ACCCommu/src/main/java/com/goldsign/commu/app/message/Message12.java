package com.goldsign.commu.app.message;

import com.goldsign.commu.app.util.CommuFtp;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.LogDbUtil;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * 文件通知信息
 * 
 * @author zhangjh
 */
public class Message12 extends MessageBase {

	private static Logger logger = Logger.getLogger(Message12.class.getName());

	// private static final byte[] SYNCONTROL = new byte[0];

	@Override
	public void run() throws Exception {
		String result = FrameLogConstant.RESULT_HDL_SUCESS;
		this.level = FrameLogConstant.LOG_LEVEL_INFO;
		try {
			this.hdlStartTime = System.currentTimeMillis();
			logger.info("--处理12消息开始--");
			this.process();
			logger.info("--处理12消息结束--");
			this.hdlEndTime = System.currentTimeMillis();
		} catch (Exception e) {
			result = FrameLogConstant.RESULT_HDL_FAIL;
			this.hdlEndTime = System.currentTimeMillis();
			this.level = FrameLogConstant.LOG_LEVEL_ERROR;
			this.remark = e.getMessage();
			throw e;
		} finally {// 记录处理日志
			LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_FILE_NOTICE,
					this.messageFrom, this.hdlStartTime, this.hdlEndTime,
					result, this.threadNum, this.level, this.remark,
					this.getCmDbHelper());
		}
	}

	public void process() throws Exception {

		// logger.info(thisClassName+" started!");
		// synchronized (SYNCONTROL) {
		try {
			getBcdString(2, 7);
			String stationId = getCharString(9, 4);
			int repeatCount = getInt(13);
			Vector<String> fileNames = getFileNames(14, 24, repeatCount);
			logger.info("传入的文件名有：");
			for (String filename : fileNames) {
				logger.info(filename);
			}
                        
			String server = (String) ((FrameCodeConstant.ALL_LCC_IP)
					.get(stationId.substring(0, 2)));
			logger.info("=====server===="+server);
			if (server == null) {
                            //判断手机平台IP 手机支付业务
//                            if(FrameCodeConstant.MOBILE_CONTROL.equals("1") 
//                                    && FrameCodeConstant.MOBILE_IP.containsKey(stationId.substring(0, 2))){
//                                server = (String) ((FrameCodeConstant.MOBILE_IP).get(stationId.substring(0, 2)));
//                            }
                            if(server == null) {
				throw new CommuException("No such LCC or Mobile Platform : " + stationId.substring(0, 2));
                            }
			}
			CommuFtp ftp = new CommuFtp();
			ftp.ftpGetFiles(server, FrameCodeConstant.ftpUserName,
					FrameCodeConstant.ftpUserPassword, fileNames,
					this.getCmDbHelper());
		} catch (CommuException e) {
			logger.error(thisClassName + " error! messageSequ:" + messageSequ
					+ ". ", e);
			throw e;
		}

		// logger.info(DateHelper.datetimeToString(new
		// Date())+"  "+thisClassName+" ended!");
		// }
	}

	private Vector<String> getFileNames(int offset, int length, int aRepeatCount)
			throws CommuException {
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < aRepeatCount; i++) {
			String fileName = getCharString(offset + length * i, length).trim();
			// logger.info("message12:fileName="+fileName);
			v.add(fileName);

		}
		return v;
	}
}
