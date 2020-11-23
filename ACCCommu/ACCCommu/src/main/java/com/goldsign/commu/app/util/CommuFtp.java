package com.goldsign.commu.app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.goldsign.commu.frame.exception.FileNameException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.FileLogDao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.vo.RecvFileVo;
import com.goldsign.lib.db.util.DbHelper;

public class CommuFtp {

	private long startTime;
	private long endTime;
	/**
	 * 日志记录使用
	 */
	private long hdlStartTime; // 处理的起始时间
	private long hdlEndTime;// 处理的结束时间
	private static Logger logger = Logger.getLogger(CommuFtp.class.getName());
	private static final Map<String, String> map = new HashMap<String, String>(
			16);
	static {
		map.put("TRX", "st");
		map.put("PRO", "st");
		map.put("AUD", "st");
		map.put("REG", "st");
                map.put("ORD", "st");
                map.put("ORI", "st");
                map.put("STL", "st");
		map.put("TST", "tk");
		map.put("THD", "tk");
		map.put("TBH", "tk");
		map.put("TDS", "tk");
                map.put("PSD", "tk");
	}

	private void setFtpClientSocketOption(FTPClient ftpClient, String server)
			throws SocketException {
		// ftpClient.setDataTimeout(FrameCodeConstant.ftpTimeout);
		ftpClient.setDefaultTimeout(FrameCodeConstant.ftpTimeout);
		// ftpClient.setSoTimeout(FrameCodeConstant.ftpTimeout);

		ftpClient.setConnectTimeout(FrameCodeConstant.ftpTimeout);
		logger.info("设置连接超时时间：" + FrameCodeConstant.ftpTimeout + " 准备连接服务器："
				+ server);

	}

	private void setFtpClientSocketOptionAfter(FTPClient ftpClient,
			String server) throws SocketException {
		ftpClient.setDataTimeout(FrameCodeConstant.ftpTimeout);

		ftpClient.setSoTimeout(FrameCodeConstant.ftpTimeout);

		ftpClient.setConnectTimeout(FrameCodeConstant.ftpTimeout);
		logger.info("控制socket超时时间：" + FrameCodeConstant.ftpTimeout
				+ "数据socket超时时间：" + FrameCodeConstant.ftpTimeout);

	}

	private void setFtpClientFileOption(FTPClient ftpClient) throws Exception {
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		int retCode;
		retCode = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(retCode)) {
			throw new CommuException("设置FTP被动模式或文件类型错误! ");
		}

	}

	private void disconnection(FTPClient ftpClient) {
		if (ftpClient != null && ftpClient.isConnected()) {
			try {
				ftpClient.disconnect();
			} catch (IOException ioe) {
				// do nothing
			}
		}

	}

	private void threadSleep() {
		try {
			Thread.sleep(FrameCodeConstant.ftpRetryWaiting);
		} catch (InterruptedException ex) {
			logger.warn(ex);
		}
	}

	private FTPClient getFtpClient(String server, String userName,
			String password) throws Exception {
		int ftpTime = FrameCodeConstant.ftpRetryTime + 1; // first time +
		// retry time
		FTPClient ftpClient = null;
		while (ftpTime > 0) {
			try {

				ftpClient = new FTPClient();
				logger.info("设置FTP的控制socket读超时时间、数据读socket超时时间、连接超时时间、缺省超时时间");
				this.setFtpClientSocketOption(ftpClient, server);
				ftpClient.connect(server, FrameCodeConstant.ftpPort);
				this.setFtpClientSocketOptionAfter(ftpClient, server);

				if (!ftpClient.login(userName, password)) {
					throw new CommuException("Ftp 登陆" + server + "错误! ");
				}

				logger.info("登陆服务器 : " + server + "!");
				// 设置文件类型为bin、被动方式
				this.setFtpClientFileOption(ftpClient);

				ftpTime = -1; // if success anytime
			} catch (Exception e) {
				ftpTime = ftpTime - 1;
				logger.error("Ftp 建立连接或登陆" + server + "错误! 将重试" + (ftpTime + 1)
						+ " 次! " + e);
				this.disconnection(ftpClient);// 释放登陆失败连接
				this.threadSleep();// 重试等待时间间隔

			}
		}
		if (ftpTime == 0) {
			throw new CommuException("重试"
					+ (FrameCodeConstant.ftpRetryTime + 1) + "次后，Ftp 仍然不能连接或登陆"
					+ server + "! ");
		}

		return ftpClient;

	}

	/**
	 * 批量下载文件
	 * 
	 * @param server
	 *            FTP服务器ip地址
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param fileNames
	 *            文件组
	 * @param dbHelper
	 */
	public void ftpGetFiles(String server, String userName, String password,
			Vector<String> fileNames, DbHelper dbHelper) {
		FTPClient ftpClient = null;
		String fileName;
		try {
			this.hdlStartTime = System.currentTimeMillis();
			// 如不存在，创建本地工作目录
			logger.debug(" 如不存在，创建本地工作目录:" + FrameCodeConstant.ftpLocalDir);
			setLocalDirectory(FrameCodeConstant.ftpLocalDir);
			logger.debug(" 成功创建目录：" + FrameCodeConstant.ftpLocalDir);
			// 每个文件使用独立的连接
			for (int i = 0; i < fileNames.size(); i++) {
				// 建立连接、及设置连接属性
				ftpClient = this.getFtpClient(server, userName, password);
				fileName = fileNames.get(i);
				// 取文件
				logger.info("取文件开始：" + fileName);
				this.ftpGetFile(fileName, server,
						FrameCodeConstant.ftpLocalDir, ftpClient, dbHelper);

				logger.info("取文件结束：" + fileName);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			this.hdlEndTime = System.currentTimeMillis();
			// 记录日志
			LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_FTP, "",
					this.hdlStartTime, this.hdlEndTime,
					FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread()
							.getName(), FrameLogConstant.LOG_LEVEL_ERROR, e
							.getMessage(), dbHelper);

		} finally {
			this.disconnection(ftpClient);

		}

	}

	public void ftpGetFile(String fileName, String server, String localDir,
			FTPClient ftpClient, DbHelper dbHelper) throws Exception {
		boolean dirChanged = false;
		OutputStream output = null;
		File f = null;
		File destination = null;
		int spendTime;

		// 文件名前缀
		String prefix = fileName.substring(0, 3);
		// 文件所属系统：tk（票务），st（运营）
		String flag = getFileType(prefix);
		try {

			startTime = System.currentTimeMillis();

			// 设置服务器工作目录
			String remoteDir = (String) (FrameCodeConstant.FTP_PATHS)
					.get(prefix);
			logger.info("remoteDir===="+remoteDir);
			if (remoteDir != null && remoteDir.length() != 0) {
				if (!ftpClient.changeWorkingDirectory(remoteDir)) {
					throw new Exception("远程工作目录：" + remoteDir + "不存在");
				}
				dirChanged = true;
			}

			if ("tk".equals(flag)) {
				localDir = localDir.substring(0, localDir.lastIndexOf('/'))
						+ "/ticket";
			}
			// 取文件,如失败，删除临时文件，成功，临时文件命名为正式文件
			destination = new File(localDir, FrameCodeConstant.ftpTmpFilePrefix
					+ fileName);
			output = new FileOutputStream(destination);
			try {
				if (!ftpClient.retrieveFile(fileName, output)) {
					throw new IOException("获取文件" + fileName + "失败.返回消息："
							+ ftpClient.getReplyString());
				}
				f = new File(localDir, fileName);

			} catch (IOException e) {
				destination.delete();// 删除临时文件
				throw new IOException(e.getMessage());

			}

			logger.info("FTP 收取文件 : " + fileName + " 成功!");
			endTime = System.currentTimeMillis();
			spendTime = (int) (endTime - startTime);
			LogUtil.writeFtpLog(new Date(System.currentTimeMillis()), server,
					fileName, (new java.util.Date(startTime)), spendTime, "0",
					dbHelper);
		} catch (Exception e) {
			logger.error("取文件出现问题", e);

			endTime = System.currentTimeMillis();
			spendTime = (int) (endTime - startTime);
			LogUtil.writeFtpLog(new Date(System.currentTimeMillis()), server,
					fileName, (new java.util.Date(startTime)), spendTime, "1",
					dbHelper);

			throw new Exception(e.getMessage());

		} finally {
			// 关闭文件输出流、服务器返回工作目录的上一级
			this.finalHandle(output, dirChanged, ftpClient);
			this.disconnection(ftpClient);
			if (null != destination && null != f) {
				this.renameFile(destination, f);// 临时文件命名为正式文件
			}
                        
                        try {
                            if(destination.exists()){
                                destination.delete();// 删除临时文件
                            }
                        } catch (Exception e) {
                        }

			// if ("tk".equals(flag)) {
			if (null != f && f.exists()) {
				RecvFileVo vo = new RecvFileVo();
				vo.setFileName(fileName);
				vo.setFilePath(localDir);
				vo.setFileType(prefix);
				vo.setFlag(flag);
				vo.setHandlePath("handling");
				vo.setErrorPath("error");
				vo.setHisPath("his");
				//只对票务文件查重
				if("tk".equals(flag) && isRepeat(fileName)){
					vo.setStatus(6);//文件存在正常入库记录)
					f.delete();
				}else {
					vo.setStatus(0);// 0表示未处理
				}
				new FileLogDao().insert(vo);
			}
			// }
		}
	}

	private String getFileType(String prefix) {
		return map.get(prefix);
	}

	private void  finalHandle(OutputStream output, boolean dirChanged,
			FTPClient ftpClient) {
		try {
			if (output != null) {
				output.close();
			}
			if (dirChanged) {
				try {
					ftpClient.changeToParentDirectory();
				} catch (Exception e) {
					logger.error(e);
				}
			}
		} catch (Exception e) {
		}
	}

	private void renameFile(File destination, File f) {
		// 交易文件大小为0，不作处理
		if (destination.length() == 0) {
			destination.delete();
			return;
		}
		// 同名交易文件存在且交易文件大小为0，删除同名交易文件，将新的交易文件重命名
		if (f.exists() && f.length() == 0) {
			f.delete();
			destination.renameTo(f);
			return;
		}
		// 同名交易文件存在且交易文件大小不为0，不作处理
		if (f.exists() && f.length() != 0) {
			destination.delete();
			return;
		}
		// 同名交易文件不存且交易文件大小不为0，将新的交易文件重命名
		destination.renameTo(f);

	}

	private void setLocalDirectory(String path) {

		File filePath = new File(path);
		if (!filePath.exists()) {
			logger.info("FTP路径" + path + "不存在, 创建该目录!");
			filePath.mkdirs();
		}
	}
	private boolean isRepeat(String fileName){
		return new FileLogDao().isRepeat(fileName);
	}
}
