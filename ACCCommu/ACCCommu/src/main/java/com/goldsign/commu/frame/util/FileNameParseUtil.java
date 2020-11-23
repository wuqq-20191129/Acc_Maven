/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameCollectionConstant;
import com.goldsign.commu.frame.constant.FrameFileHandledConstant;
import com.goldsign.commu.frame.exception.FileNameException;

/**
 * 
 * @author Administrator
 */
public class FileNameParseUtil {

	/**
	 * 文件名称校验相关
	 */
	// 长度
	public static int FILE_LEN_TST = 18;// 售存文件名称长度
	public static int FILE_LEN_THD = 18;// 车站上交数据文件名称长度
	public static int FILE_LEN_TBH = 18;// 收益组上交数据文件名称长度
	public static int FILE_LEN_TDS = 18;// 配票数据文件名称长度

	// 开头
	public static String FILE_START_TST = "TST";// 售存文件名称的开头
	public static String FILE_START_THD = "THD";// 车站上交数据文件名称的开头
	public static String FILE_START_TBH = "TBH";// 收益组上交数据文件名称的开头
	public static String FILE_START_TDS = "TDS";// 配票数据文件名称的开头

	// 分隔符
	public static String FILE_DELIMIT = ".";// 文件名称的分隔

	public void parseLen(String fileName, int len) throws FileNameException {
		if (fileName.length() != len) {
			throw new FileNameException(
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_LEN[1],
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_LEN[0]);
		}

	}

	public void parseFmtForTwo(String fileName, String start)
			throws FileNameException {

		if (!fileName.startsWith(start)) {
			throw new FileNameException(
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[1],
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[0]);
		}
		String delim = (String) fileName.substring(7, 8);
		if (!delim.equals(FileNameParseUtil.FILE_DELIMIT)) {
			throw new FileNameException(
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[1],
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[0]);
		}
		delim = (String) fileName.substring(16, 17);
		if (!delim.equals(FileNameParseUtil.FILE_DELIMIT)) {
			throw new FileNameException(
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[1],
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[0]);
		}
	}

	public void parseFmtForOne(String fileName, String start)
			throws FileNameException {

		if (!fileName.startsWith(start)) {
			throw new FileNameException(
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[1],
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[0]);
		}
		String delim = (String) fileName.substring(14, 15);
		if (!delim.equals(FileNameParseUtil.FILE_DELIMIT)) {
			throw new FileNameException(
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[1],
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_FMT[0]);
		}

	}

	public void parseStation(String fileName) throws FileNameException {
		String station = fileName.substring(3, 7);
		String lineAll = fileName.substring(5, 7);
		if (FrameCollectionConstant.BUF_STATION_CODE == null) {
			return;
		}
		if (lineAll.equals("00")) {
			return;
		}

		if (!FrameCollectionConstant.BUF_STATION_CODE.contains(station)) {
			throw new FileNameException(
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_STATION[1],
					FrameFileHandledConstant.FILE_ERR_FILE_NAME_STATION[0]);
		}

	}

	public void parseDate(String fileName) throws FileNameException {
		String date = fileName.substring(8, 8);
	}
}
