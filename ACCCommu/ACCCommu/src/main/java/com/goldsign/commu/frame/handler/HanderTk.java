package com.goldsign.commu.frame.handler;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.frame.constant.FrameCharConstant;
import com.goldsign.commu.frame.constant.FrameFileHandledConstant;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.FileNameException;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.FileNameParseUtil;
import com.goldsign.commu.frame.util.FileUtil;
import com.goldsign.commu.frame.vo.FileData;
import com.goldsign.commu.frame.vo.FileNameSection;
import com.goldsign.commu.frame.vo.FileRecordCrc;
import com.goldsign.commu.frame.vo.FileRecordHead;

/**
 * 
 * @author zhangjh
 * 
 */
public abstract class HanderTk extends HandleBase {
	private static Logger logger = Logger.getLogger(HanderTk.class.getName());

	protected String tradType = "";
	public static Map<Integer, String> FILE_STATUS = new HashMap<Integer, String>(
			16);
	static {
		FILE_STATUS.put(FrameTicketConstant.FILE_NOT_HANDLE, "文件未处理");
		FILE_STATUS.put(FrameTicketConstant.FILE_IS_HANDLEING, "文件正在处理");
		FILE_STATUS.put(FrameTicketConstant.FILE_IS_ERROR, "文件有误");
		FILE_STATUS.put(FrameTicketConstant.FILE_IN_DB_ERROR, "文件数据入库异常");
		FILE_STATUS.put(FrameTicketConstant.FILE_IS_HANDLED, "文件已处理");
		FILE_STATUS.put(FrameTicketConstant.FILE_NOT_EXIT, "文件不存在");
	}

	/**
	 * 
	 * @param fileName
	 * @param nameLen
	 * @param namePrefix
	 * @throws FileNameException
	 */
	protected void parseFileName(String fileName, int nameLen, String namePrefix)
			throws FileNameException {
		FileNameParseUtil util = new FileNameParseUtil();
		util.parseLen(fileName, nameLen);
		util.parseFmtForOne(fileName, namePrefix);
		// util.parseDate(fileName);

	}

	/**
	 * 批量入库,如果处理时间
	 * 
	 * @param fileName
	 * @param fd
	 * @return
	 */
	protected HandleMessageBase batchDB(String fileName, FileData fd) {
		HandleMessageBase msg = new HandleMessageBase();
		msg.setFileName(fileName);
		msg.setContent(fd.getContent().get(fileName));
		msg.setTradType(tradType);
		MSG_BUFFER.setHandlingMsg(msg);
		// 等待入库线程池处理完成，有没有可能会一直数据库插入一直执行？
		while (msg.getFinished() == FrameTicketConstant.FILE_IS_HANDLEING) {
			forSleep();
		}
		return msg;
	}

	public void forSleep() {
		try {
			Thread.currentThread().sleep(30000);
		} catch (InterruptedException e) {
			logger.info("休眠被打断..");
		}
	}

	/**
	 * 文件处理
	 * 
	 * @param path
	 * @param fileName
	 * @param batchWaterNo
	 * @return
	 * @throws RecordParseException
	 */
	protected FileData parseFile(String path, String fileName, int batchWaterNo)
			throws RecordParseException {
		FileInputStream fis = null;
		// 文件全路径
		String fileNameFull = path + "/" + fileName;
		StringBuilder sBuilder = new StringBuilder();
		String[] lines = null;
		int index = 0;
		// 文件校验使用
		StringBuffer sb = new StringBuffer();
		// 文件的交易记录行数
		int rowCount = 0;
		// 文件处理结果
		FileData fd = new FileData();
		FileRecordHead frh = null;
		FileRecordCrc frc = null;
		Map<String, List<Object>> hm = new HashMap<String, List<Object>>();
		int iChar = 0;
		FileRecordAddVo lineAdd = new FileRecordAddVo(batchWaterNo, fileName);
		try {
			fis = new FileInputStream(fileNameFull);
			while ((iChar = fis.read()) != -1) {
				sBuilder.append((char) iChar);
			}
			lines = getLines(sBuilder);

			for (String line : lines) {
				// while ((line = br.readLine()) != null) {
				index++;
				if (isFileHead(index)) {
					frh = parseFileHead(line, lineAdd);// 解析文件头
					addLineToBuff(sb, line);
					continue;
				}
				if (isFileCRC(line)) {
					frc = parseFileCrc(line, lineAdd);// 解析CRC
					continue;
				}

				// 解析文件内容
				parseFileData(line, hm, lineAdd, tradType, fileName);

				addLineToBuff(sb, line);
				// sb.append(line);
				rowCount++;
			}
			checkFileContent(frh, frc, fileName, rowCount, sb);// 校验文件内容
			fd.setHead(frh);
			fd.setCrc(frc);
			fd.setContent(hm);

		} catch (RecordParseException e) {
			logger.info("解析文件出错", e);
			throw new RecordParseException(e.getMessage(),
					FrameFileHandledConstant.FILE_ERR_FILE_UNKOWN[0]);
		} catch (Exception e) {
			logger.info("解析文件出错", e);
			throw new RecordParseException(e.getMessage(),
					FrameFileHandledConstant.FILE_ERR_FILE_UNKOWN[0]);
		} finally {
			closeFile(fis);
		}
		return fd;

	}

	private void checkFileContent(FileRecordHead fh, FileRecordCrc frc,
			String fileName, int rowCount, StringBuffer sb)
			throws RecordParseException {
		FileNameSection sect = FileUtil.getFileSectForTwo(fileName);
		checkFileHeadTwo(fh, sect);
		checkFileRecordNum(fh, rowCount);
		checkFileCrc(frc, sb);
	}

	protected void doParse(HandleMessageBase msg, String fileName) {
		String path = msg.getPath() + "/" + msg.getHandlePath();
		int waterNo = msg.getWaterNo();
		boolean isExistFileError = false;// 文件没有问题
		HandleMessageBase messageBase = null;
		int finishFlag = FrameTicketConstant.FILE_IS_HANDLED;

		String newFileName = fileName + FrameCharConstant.POINT
				+ DateHelper.dateToString(new Date());
		msg.setNewFileName(newFileName);
		try {
			int nameLen = FileNameParseUtil.FILE_LEN_TST;
			String namePrefix = tradType;
			// 基本校验
			parseFileName(fileName, nameLen, namePrefix);
			// 解析文件和深度校验
			FileData fd = parseFile(path, fileName, waterNo);
			messageBase = batchDB(fileName, fd);// 将信息入库
			finishFlag = messageBase.getFinished();

		} catch (FileNameException ex) {
			logger.info("处理文件出现异常：", ex);
			// // 作为文件错误，需LC处理
			// writeFileError(waterNo, fileName, ex.getErrorCode(),
			// ex.getMessage());
			isExistFileError = true;// 文件有问题
			finishFlag = FrameTicketConstant.FILE_IS_ERROR;

		} catch (RecordParseException ex) {
			logger.info("处理文件出现异常：", ex);
			// // 作为文件错误，需LC处理
			// writeFileError(waterNo, fileName, ex.getErrorCode(),
			// ex.getMessage());
			isExistFileError = true;// 文件有问题
			finishFlag = FrameTicketConstant.FILE_IS_ERROR;
		} catch (Exception ex) {
			logger.info("处理文件出现异常：", ex);
			// // 作为文件错误，需LC处理
			// writeFileError(waterNo, fileName, ex.getErrorCode(),
			// ex.getMessage());
			// isExistFileError = true;
			// finishFlag = FrameTicketConstant.FILE_IS_ERROR;

		} finally {

			// 记录正常处理的文件日志
			writeFileNormal(waterNo, fileName, newFileName, finishFlag);

			// 文件移到历史目录
			movFile(msg, isExistFileError);
		}
	}
	
	public static void main(String[] args){
		
	}
}
