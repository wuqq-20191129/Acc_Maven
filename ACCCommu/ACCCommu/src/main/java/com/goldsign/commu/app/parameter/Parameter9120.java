package com.goldsign.commu.app.parameter;

import java.io.File;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.Parameter9120Dao;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.parameter.ParameterBase;
import com.goldsign.commu.frame.util.FtpUtil;

/**
 * AGM 读卡器程序
 * 
 * @author zhangjh
 * 
 */
public class Parameter9120 extends ParameterBase {

	private static Logger logger = Logger.getLogger(Parameter9120.class
			.getName());

	@Override
	public boolean formParaFile() {
		logger.info("开始构建读写器或者TVM地图参数! ");
		// 查询远程文件名
		String remoteFilePath = null;
		try {
			// get parameter data
			remoteFilePath = Parameter9120Dao.querRemoteFilePath(paraGenDtl,
					dbHelper);
		} catch (Exception e) {
			logger.error("查询读写参数文件路径错误! ", e);
			return false;
		}
		if ("".equals(remoteFilePath)) {
			logger.warn("没有查询到读写参数文件路径! ");
			return false;
		}
		int indexOfLast = remoteFilePath.lastIndexOf("/");
		String remotePath = remoteFilePath.substring(0, indexOfLast);
		String remoteFile = remoteFilePath.substring(indexOfLast + 1);
		logger.info("remotePath:" + remotePath);
		logger.info("remoteFile:" + remoteFile);
		if ("".equals(remotePath) && "".equals(remoteFile)) {
			logger.error("远程目录或文件不正确");
			return false;
		}
		// 下载远程文件到本地
		// ftp取文件
		boolean flag = FtpUtil.downFile(FrameCodeConstant.OP_URL,
				FrameCodeConstant.OP_PORT, FrameCodeConstant.OP_USERNAME,
				FrameCodeConstant.OP_PASSWORD, remotePath, remoteFile,
				FrameCodeConstant.parmDstrbPath);
		if (!flag) {
			logger.error("下载文件失败");
			return false;
		}
		// 更改文件名为标准文件名

		String fileName = paraFileName;

		File srcFile = new File(FrameCodeConstant.parmDstrbPath + "/"
				+ remoteFile);
		File destFile = new File(FrameCodeConstant.parmDstrbPath + "/"
				+ fileName);
		boolean isSuccess = srcFile.renameTo(destFile);
		logger.info("构建读写器或者TVM地图参数结束! ");
		//
		return true;
	}
}
