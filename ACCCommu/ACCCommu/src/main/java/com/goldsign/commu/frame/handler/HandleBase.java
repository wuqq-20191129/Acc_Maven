package com.goldsign.commu.frame.handler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.parser.FileRecordParserCrc;
import com.goldsign.commu.app.parser.FileRecordParserHead;
import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.frame.buffer.HandleMessageBuffer;
import com.goldsign.commu.frame.constant.FrameFileHandledConstant;
import com.goldsign.commu.frame.dao.FileErrorDao;
import com.goldsign.commu.frame.dao.FileLogDao;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.parser.FileRecordParserBase;
import com.goldsign.commu.frame.util.CrcUtil;
import com.goldsign.commu.frame.util.FileUtil;
import com.goldsign.commu.frame.vo.FileErrorVo;
import com.goldsign.commu.frame.vo.FileNameSection;
import com.goldsign.commu.frame.vo.FileRecordBase;
import com.goldsign.commu.frame.vo.FileRecordCrc;
import com.goldsign.commu.frame.vo.FileRecordHead;
import com.goldsign.commu.frame.vo.RecvFileVo;

/**
 * 
 * @author zhangjh
 */
public abstract class HandleBase {
	private static String delimit = "\\x0d\\x0a\\x0d\\x0a\\x0d\\x0a";
	private static Logger logger = Logger.getLogger(HandleBase.class.getName());
	public static String FLAG_CRC = "CRC:";
	public static String CLASS_PARSER_PRIX = "com.goldsign.commu.app.parser.FileRecordParser";
	public static String CLASS_PARSER_SHORT_NAME_HEAD = "Head";
	public static String CLASS_PARSER_SHORT_NAME_CRC = "Crc";
	// 消息队列,只有一个对象，初始化一次
	public final static HandleMessageBuffer MSG_BUFFER = new HandleMessageBuffer();

	protected static String[] TRAD_SUFIX = { "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J" };
	protected static String[] TRAD_TYPES_WITH_SUB = { "00", "81", "1", "2" };// 有子记录的数据类型，包括BOM收益数据、寄存器数据、AGM审计/TVM审计
	protected static String CLASS_PREFIX = "com.goldsign.settle.app.dao.BufferToQueue";

	public abstract void handleMessage(HandleMessageBase msg);

	private char byteToChar(byte b) {
		return (char) b;
	}

	private char byteToChar(char b) {
		return b;
	}

	private int byteToInt(char b) {
		return (int) b;

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

	private String byte1ToBcd2(byte b) {
		int i = 0;
		if (b < 0) {
			i = 256 + b;
		} else {
			i = b;
		}
		return (new Integer(i / 16)).toString()
				+ (new Integer(i % 16)).toString();
	}

	private String byte1ToBcd2(char b) {
		String s = Integer.toHexString((int) b);
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;

	}

	public int delete_getInt(byte[] data, int offset) {
		return byteToInt(data[offset]);
	}

	public int getInt(char[] data, int offset) {
		return byteToInt(data[offset]);
	}

	// when transform one short(two bytes) for example 0x12(low),0x34(high),run
	// this method to get 13330
	public int delete_getShort(byte[] data, int offset) {
		int low = byteToInt(data[offset]);
		int high = byteToInt(data[offset + 1]);
		return high * 16 * 16 + low;
	}

	public int getShort(char[] data, int offset) {
		int low = byteToChar(data[offset]);
		int high = byteToChar(data[offset + 1]);
		return high * 16 * 16 + low;
	}

	// when transform one long(two shorts) for example 0x12,0x34,0x56,0x78
	public int delete_getLong(byte[] data, int offset) {
		int low = delete_getShort(data, offset);
		int high = delete_getShort(data, offset + 2);
		return high * 16 * 16 * 16 * 16 + low;
	}

	public int getLong(char[] data, int offset) {
		int low = getShort(data, offset);
		int high = getShort(data, offset + 2);
		return high * 16 * 16 * 16 * 16 + low;
	}

	public String delete_getBcdString(byte[] data, int offset, int length)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < length; i++) {
				sb.append(byte1ToBcd2(data[offset + i]));
			}
		} catch (Exception e) {
			throw new Exception(" " + e);
		}
		return sb.toString();
	}

	public String getBcdString(char[] data, int offset, int length)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < length; i++) {
				sb.append(byte1ToBcd2(data[offset + i]));
			}
		} catch (Exception e) {
			throw new Exception(" " + e);
		}
		return sb.toString();
	}

	public String delete_getCharString(byte[] data, int offset, int length)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < length; i++) {
				sb.append(byteToChar(data[offset + i]));
			}
		} catch (Exception e) {
			throw new Exception(" " + e);
		}
		return sb.toString();
	}

	public String getCharString(char[] data, int offset, int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(data[offset + i]);
		}
		return sb.toString();
	}

	public byte[] getLineByteFromFileTest(String line) throws Exception {
		if (line == null || line.length() == 0) {
			return null;
		}
		int len = line.length();
		if (len % 2 != 0) {
			throw new Exception("一个字节使用2个16进制数值表示");
		}
		int j = 0;
		byte[] b = new byte[len / 2];
		for (int i = 0; i < len; i = i + 2) {
			b[j] = (byte) Integer.parseInt(line.substring(i, i + 2), 16);
			j++;
		}
		return b;
	}

	public byte[] delete_getLineByteFromFile(String line) throws Exception {
		if (line == null || line.length() == 0) {
			return null;
		}
		return line.getBytes();
	}

	public char[] getLineCharFromFile(String line) throws Exception {
		if (line == null || line.length() == 0) {
			return null;
		}
		return line.toCharArray();// 中文时，非原始数据20130811
	}

	public char[] getCharArray(String line) throws Exception {
		byte[] bs = line.getBytes("GBK");
		char[] cs = new char[bs.length];
		int b;
		for (int i = 0; i < bs.length; i++) {
			b = bs[i];
			cs[i] = (char) b;
		}
		return cs;
	}

	protected void delete_closeFile(FileInputStream fis, InputStreamReader isr,
			BufferedReader br) {
		try {
			if (fis != null) {
				fis.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (br != null) {
				br.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void closeFile(FileInputStream fis) {
		try {
			if (fis != null) {
				fis.close();
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	protected boolean delte_isFileCRC(String line) throws Exception {
		// byte[] b = this.getLineByteFromFileTest(line);
		byte[] b = this.delete_getLineByteFromFile(line);
		String crcFlag = this.delete_getCharString(b, 0, 4);

		return crcFlag.startsWith(HandleBase.FLAG_CRC);

	}

	protected boolean isFileCRC(String line) throws Exception {
		// byte[] b = this.getLineByteFromFileTest(line);
		char[] b = this.getLineCharFromFile(line);
		String crcFlag = this.getCharString(b, 0, 4);
		return crcFlag.startsWith(HandleBase.FLAG_CRC);
	}

	/**
	 * 是否是文件第一行
	 * 
	 * @param i
	 * @return
	 */
	protected boolean isFileHead(int index) {
		return index == 1;
	}

	protected void putMap(String fileName, Object ob,
			Map<String, List<Object>> hm) {
		if (!hm.containsKey(fileName)) {
			hm.put(fileName, new ArrayList<Object>());
		}
		List<Object> list = hm.get(fileName);
		list.add(ob);
	}

	protected void putMap(String trdType, Vector obs, HashMap<String, Vector> hm) {
		if (!hm.containsKey(trdType)) {
			hm.put(trdType, new Vector());
		}
		Vector v = (Vector) hm.get(trdType);
		v.addAll(obs);
	}

	protected void writeFileError(String balanceWaterNo, String fileName,
			String errorCode, String remark) {
		FileErrorDao dao = new FileErrorDao();
		FileErrorVo vo = new FileErrorVo(balanceWaterNo, fileName, errorCode,
				remark);
		try {
			dao.insert(vo);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

//	protected void writeFileErrorForOctReturn(String balanceWaterNo,
//			String fileName, String errorCode, String remark) {
//		FileErrorDao dao = new FileErrorDao();
//		FileErrorVo vo = new FileErrorVo(balanceWaterNo, fileName, errorCode,
//				remark);
//		try {
//			// dao.insertForOctReturn(vo);
//		} catch (Exception ex) {
//			logger.error(ex.getMessage(), ex);
//		}
//	}

	protected void movFile(HandleMessageBase msg, boolean isExistFileError) {
		// msg.getPath() + "/" + msg.getHanlePath() 为‘正在处理’的文件子目录
		if (isExistFileError) {
			FileUtil.moveFile(msg.getFileName(),
					msg.getPath() + "/" + msg.getHandlePath(),
					msg.getNewFileName(),
					msg.getPath() + "/" + msg.getPathError(), msg.getWaterNo());
		} else {
			FileUtil.moveFile(msg.getFileName(),
					msg.getPath() + "/" + msg.getHandlePath(),
					msg.getNewFileName(),
					msg.getPath() + "/" + msg.getPathHis(), msg.getWaterNo());
		}
	}

	protected void writeFileNormal(int waterNo, String fileName,
			String newFileName, int finishFlag) {
		FileLogDao dao = new FileLogDao();
		RecvFileVo vo = new RecvFileVo(waterNo, fileName, newFileName,
				finishFlag, "");
		try {
			dao.update(vo);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	protected String[] getLines(StringBuilder sb) {
		String allLines = new String(sb);
		String[] lines = allLines.split(delimit);
		return lines;
	}

	protected FileRecordHead parseFileHead(String line, FileRecordAddVo lineAdd)
			throws Exception {
		FileRecordParserHead parser = new FileRecordParserHead();
		FileRecordHead frh = (FileRecordHead) parser.parse(line, lineAdd);
		return frh;
	}

	//
	// protected FileRecordHead parseFileHeadOtherLineStation(String line,
	// FileRecordAddVo lineAdd) throws Exception {
	// // FileRecordParserHeadOtherLine parser = new
	// FileRecordParserHeadOtherLine();
	// FileRecordParserHeadOther parser = new FileRecordParserHeadOther();
	// FileRecordHead frh = (FileRecordHead) parser.parse(line, lineAdd);
	// return frh;
	// }

	protected FileRecordHead parseFileHeadOtherOnlyLine(String line,
			FileRecordAddVo lineAdd) throws Exception {
		// FileRecordParserHeadOtherLine parser = new
		// FileRecordParserHeadOtherLine();
		// //FileRecordParserHeadOther parser =new FileRecordParserHeadOther();
		// FileRecordHead frh = (FileRecordHead) parser.parse(line, lineAdd);
		FileRecordHead frh = null;
		return frh;
	}

	protected void addLineToBuff(StringBuffer sb, String line) {
		sb.append(line);
		sb.append((char) 13);
		sb.append((char) 10);
                sb.append((char) 13);//modify by hejj 20141112 一个0d0a改3个0d0a
		sb.append((char) 10);
                sb.append((char) 13);
		sb.append((char) 10);
	}

	protected FileRecordCrc parseFileCrc(String line, FileRecordAddVo lineAdd)
			throws Exception {
		FileRecordParserCrc parser = new FileRecordParserCrc();
		FileRecordCrc frc = (FileRecordCrc) parser.parse(line, lineAdd);
		return frc;
	}

	/**
	 * 解析文件内容
	 * 
	 * @param line
	 *            当前行内容
	 * @param hm
	 * @param lineAdd
	 * @param trdType
	 * @throws RecordParseException
	 */
	protected void parseFileData(String line, Map<String, List<Object>> hm,
			FileRecordAddVo lineAdd, String trdType, String fileName)
			throws RecordParseException {
		try {

			Object ob;
			String className = HandleBase.CLASS_PARSER_PRIX + trdType;
			ob = ((FileRecordParserBase) Class.forName(className).newInstance())
					.parse(line, lineAdd);
			this.putMap(fileName, ob, hm);
		} catch (Exception e) {
			throw new RecordParseException("解释记录类型" + trdType + "出错："
					+ e.getMessage());
		}
	}

	protected void checkFileHeadTwo(FileRecordHead fh, FileNameSection sect)
			throws RecordParseException {
		String line = fh.getLineId();
		if (!line.equals(sect.getLine())) {
			throw new RecordParseException(
					FrameFileHandledConstant.FILE_ERR_FILE_HEAD_LINE[1],
					FrameFileHandledConstant.FILE_ERR_FILE_HEAD_LINE[0]);
		}
		if (fh.getSeq() != sect.getSeq()) {
			throw new RecordParseException(
					FrameFileHandledConstant.FILE_ERR_FILE_HEAD_SEQ[1],
					FrameFileHandledConstant.FILE_ERR_FILE_HEAD_SEQ[0]);
		}
	}

	
	

	protected void checkFileRecordNum(FileRecordHead fh, int rowCount)
			throws RecordParseException {
		if (rowCount != fh.getRowCount()) {
			throw new RecordParseException(
					FrameFileHandledConstant.FILE_ERR_FILE_HEAD_ROW_COUNT[1],
					FrameFileHandledConstant.FILE_ERR_FILE_HEAD_ROW_COUNT[0]);
		}
	}

	protected void checkFileCrc(FileRecordCrc frc, StringBuffer sb)
			throws RecordParseException {
		try {
			// String crcCal = CrcUtil.getCRC32Value(sb, CrcUtil.CRC_LEN);
			String crcCal = CrcUtil.getCRC32ValueByChar(sb, CrcUtil.CRC_LEN);

			logger.info("系统计算CRC码：" + crcCal.toUpperCase() + " 文件计算的CRC:"
					+ frc.getCrc().toUpperCase());
			if (!crcCal.equalsIgnoreCase(frc.getCrc())) {
				throw new RecordParseException(
						FrameFileHandledConstant.FILE_ERR_FILE_CRC[1],
						FrameFileHandledConstant.FILE_ERR_FILE_CRC[0]);
			}
		} catch (Exception e) {
			throw new RecordParseException(
					FrameFileHandledConstant.FILE_ERR_FILE_CRC[1],
					FrameFileHandledConstant.FILE_ERR_FILE_CRC[0]);
		}
	}

	protected boolean existSubRecord(FileRecordBase r) {
		if (r == null) {
			return false;
		}
		if (r.getSubRecords() == null) {
			return false;
		}
		return true;
	}

	protected void threadSleep(long interval) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
		}
	}

	// private boolean isSubTrade(String tradeType) {
	// for (String sufix : HandleBase.TRAD_SUFIX) {
	// if (tradeType.indexOf(sufix) != -1) {
	// return true;
	// }
	// }
	// return false;
	//
	// }
}
