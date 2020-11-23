package com.goldsign.commu.app.message;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.vo.InfoTkBase;
import com.goldsign.commu.frame.constant.FrameCharConstant;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.BaseException;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.CrcUtil;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.lib.db.util.DbHelper;

public abstract class ConstructMessageTK extends ConstructMessageBase {

	private static Logger logger = Logger.getLogger(ConstructMessageTK.class
			.getName());
	protected String file_flag = "";
	private static String TK_PATH = FrameCodeConstant.ftpLocalDir.substring(0,
			FrameCodeConstant.ftpLocalDir.lastIndexOf('/')) + "/ticket";

	// 文件头

	private static int[] data_type_head = { T_STR, T_BCD, T_BCD, T_INT, T_INT };
	private static int[] data_len_head = { FrameTicketConstant.LEN_LINE,
			FrameTicketConstant.LEN_BEGIN_TIME,
			FrameTicketConstant.LEN_END_TIME, FrameTicketConstant.LEN_SEQ,
			FrameTicketConstant.LEN_RECORDS };

	// 文件尾

	private static int[] crc_type_tail = { T_STR, T_STR };
	private static int[] crc_len_tail = { 4, 8 };

	/**
	 * 写文件头
	 * 
	 * @param msg
	 * @param line
	 * @param begintime
	 * @param endtime
	 * @param seqNo
	 * @param size
	 * @param offset
	 * @return
	 * @throws MessageException
	 * @throws IOException
	 */
	protected void dealHead(char[] msg, String line, String beginTime,
			String endTime, String seqNo, int size, int offset)
			throws MessageException, IOException {

		Object[] data_head = { line, beginTime, endTime, seqNo, size };
		// 文件头

		char[] cs = this.getLine(data_head, data_len_head, data_type_head);
		addCharArrayToBuffer(msg, cs, offset);
		//int length = this.getBufferLen(data_len_head); // 长度+回车换行符


	}

	protected void write(DataOutputStream dos, char[] msg) throws IOException {
		for (char c : msg) {
			dos.write((int) c);
		}
	}

	protected abstract void dealMsg(char[] msg, String line,
			List<InfoTkBase> list, int offset) throws MessageException,
			IOException;

	protected void dealMap(Map<String, List<InfoTkBase>> maps) {
		logger.info("按照线路生成文件");
		if (null == maps) {
			logger.warn("数据为空");
			return;
		}
		Iterator iter = maps.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String line = (String) entry.getKey();
			List<InfoTkBase> list = (List<InfoTkBase>) entry.getValue();
			// 每条线路生成一个文件

			try {
				createFileAndSendMsd(line, list);
			} catch (Exception e) {
				logger.error("生成线路" + line + "的" + file_flag + "文件报错", e);
			}

		}
	}

	/**
	 * 创建文件并发送消息

	 * 
	 * @param line
	 * @param list
	 * @throws BaseException
	 * @throws SQLException
	 * @throws MessageException
	 */
	protected void createFileAndSendMsd(String line, List<InfoTkBase> list)
			throws SQLException, BaseException, MessageException {
		String fileName = createFile(line, list);
		logger.info("创建文件：" + fileName + ",线路：" + line);
		sendMsg(line, fileName);
	}

	/**
	 * 发送消息

	 * 
	 * @param line
	 * @param fileName
	 */
	protected void sendMsg(String line, String fileName) {
		logger.info("获取线路对应的ip,线路[" + line + "]");
		Vector<String> ips = getIpSend(line.trim());
		logger.info("查找到新路:" + line + "对应的ip个数：" + ips.size());
		for (String ip : ips) {
                    logger.info("开始发送12消息到线路[" + line + "],ip[" + ip + "]");
                    ConstructMessage12 msg12 = new ConstructMessage12();
                    msg12.messageTypeSub = FrameLogConstant.MESSAGE_ID_SUB_TICKET;
                    msg12.constructAndSend(ip, fileName, line, "00");
                    logger.info("成功发送12消息到线路[" + line + "],ip[" + ip + "]");
		}

	}

	public Vector<String> getIpSend(String lineIdToSend) {

		Vector<String> ipToSend = new Vector<String>();
		// 消息转发线路存放ipToSend
		for (Enumeration<String> e = FrameCodeConstant.ALL_LCC_IP.keys(); e
				.hasMoreElements();) {
			String lineId = e.nextElement();
			if (lineId.equals(lineIdToSend)) {
				ipToSend.add(FrameCodeConstant.ALL_LCC_IP.get(lineId));
			}
		}
		return ipToSend;

	}

	protected String getSeq() throws SQLException, BaseException {
		DbHelper cmDbHelper = new DbHelper("ConstructMessageTK",
				FrameDBConstant.CM_DBCPHELPER.getConnection());
                //修改为数据库表记录序号  modify by lindaquan 20160303
//		return SeqDao.getInstance().qrSeqNo(3, cmDbHelper);
                return SeqDao.getInstance().tkFileSeq(file_flag, cmDbHelper);
	}

	private String createFile(String line, List<InfoTkBase> list)
			throws SQLException, BaseException, MessageException {
		// 文件序列号

		String seqNo = getSeq();
		Date now = new Date();
		String nowDateStr = DateHelper.dateToStr(now, DateHelper.yyyyMMdd);

		// 文件名

		String fileName = file_flag + line + FrameCharConstant.POINT
				+ nowDateStr + FrameCharConstant.POINT + seqNo;

		File newFile = new File(TK_PATH + "/" + fileName);

		// 同名文件存在
		if (newFile.exists()) {
			Date nowDate = new Date();
			// 移动到his目录，并在文件名后加上日期

			File dest = new File(TK_PATH + "/his/" + fileName + "."
					+ DateHelper.dateToString(nowDate));
			newFile.renameTo(dest);
		}

		try {
			// 打开时间
			String beginTime = DateHelper.dateToStr(now, DateHelper.yyyyMMdd)
					+ "000000";
			String endTime = DateHelper.dateToStr(now, DateHelper.yyyyMMdd)
					+ "235959";
			int offset = 0;
			// 文件中数据的记录数

			int size = list.size();// 记录数


			int headLen = FrameTicketConstant.HEAD_LENGTH + CRLF_LENGTH; // 长度+回车换行符

			// 计算文件内容字符大小
			int dataLen = caculateDateLen(size);
                        //modify by hejj 20141120 CRC不加回车换行
                        int crcLen = FrameTicketConstant.CRC_LENGTH ; // CRC不加回车换行
			//int crcLen = FrameTicketConstant.CRC_LENGTH + 2; // CRC不加回车换行
			// 总字符数
			char[] msg = new char[headLen + dataLen + crcLen];

			dealHead(msg, line, beginTime, endTime, seqNo, size, offset);

			offset += headLen;
			dealMsg(msg, line, list, offset);
			offset += dataLen;
			dealCrc(msg, offset);
			writeFile(msg, TK_PATH, fileName);
			// // 关闭时间
			// String endtime = DateHelper.dateToStr(now,
			// DateHelper.yyyyMMddHHmmss);

		} catch (FileNotFoundException e1) {
			logger.error("文件未找到：" + fileName);
		} catch (IOException e) {
			logger.error("生成：" + fileName + "发生错误.");
		} finally {
		}

		return fileName;
	}

	protected void dealCrc(char[] msg, int offset)
			throws UnsupportedEncodingException {

		Object[] crc_tail = { "CRC:", "" };
		// 获取CRC
		String crc = CrcUtil.getCRC32ValueByChar(msg, offset, 8);
		crc_tail[1] = crc;
		// 文件尾
                //modify by hejj 20141120 不加回车换行符
		//char[] cs = getLine(crc_tail, crc_len_tail, crc_type_tail);
                char[] cs = this.getLineWithoutLineDelimiter(crc_tail, crc_len_tail, crc_type_tail);
		addCharArrayToBuffer(msg, cs, offset);
	}

	protected abstract int caculateDateLen(int size);

}
