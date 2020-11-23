/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameFileHandledConstant;
import com.goldsign.commu.frame.filter.FileFilterOther;
import com.goldsign.commu.frame.filter.FileFilterTrx;
import com.goldsign.commu.frame.vo.FileNameSection;
import java.io.File;
import java.util.Date;

/**
 * 
 * @author Administrator
 */
public class FileUtil {

	public File[] getFilesForTrx(String path) {
		File f = new File(path);
		if (!f.isDirectory()) {
			return null;
		}
		FileFilterTrx filter = new FileFilterTrx();
		File[] files = f.listFiles(filter);
		return files;

	}

	public File[] getFilesForOther(String path) {
		File f = new File(path);
		if (!f.isDirectory()) {
			return null;
		}
		FileFilterOther filter = new FileFilterOther();
		File[] files = f.listFiles(filter);
		return files;

	}

	/**
	 * 文件移动
	 * 
	 * @param fileNameSrc
	 * @param pathSrc
	 * @param fileNameDes
	 * @param pathDes
	 * @param batchWaterNo
	 */
	public static void moveFile(String fileNameSrc, String pathSrc,
			String fileNameDes, String pathDes, int batchWaterNo) {
		String fileNameFullSrc = pathSrc + "/" + fileNameSrc;
		String fileNameFullDes = pathDes + "/" + fileNameDes;
		String pathSubDes = pathDes;
		String fileNameFullDesRep;
		File path = new File(pathSubDes);
		if (!path.exists()) {
			path.mkdirs();
		}
		File src = new File(fileNameFullSrc);
		File des = new File(fileNameFullDes);
		if (des.exists()) {
			fileNameFullDesRep = fileNameFullDes + "."
					+ DateHelper.dateToString(new Date());
			File rep = new File(fileNameFullDesRep);
			des.renameTo(rep);
		}
		src.renameTo(des);

	}

	public static String getFieDataType(String fileName) {
		if (fileName == null || fileName.length() < 3) {
			return "";
		}
		String fileNameId = fileName.substring(0, 3);
		String s;
		for (int i = 0; i < FrameFileHandledConstant.FTP_FILE_DATA_TYPE_NAME.length; i++) {
			s = FrameFileHandledConstant.FTP_FILE_DATA_TYPE_NAME[i];
			if (fileNameId.equals(s)) {
				return FrameFileHandledConstant.FTP_FILE_DATA_TYPE[i];
			}

		}
		return "";
	}

	public static String getFileType(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return "";
		}
		return fileName.substring(0, 3);
	}

	public static FileNameSection getFileSectForTwo(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return null;
		}
		String tradType = fileName.substring(0, 3);
		String line = fileName.substring(3, 5);
		String tradDate = fileName.substring(6, 14);
		String strSeq = fileName.substring(15);
		FileNameSection sec = new FileNameSection();
		sec.setTradType(tradType);
		sec.setLine(line);
		sec.setTradDate(tradDate);
		sec.setSeq(Integer.parseInt(strSeq));
		return sec;

	}
	
}
