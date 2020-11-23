package com.goldsign.commu.frame.parameter;
import com.goldsign.commu.frame.constant.FrameCharConstant;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.exception.ParameterException;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import com.goldsign.lib.db.util.DbcpHelper;

/**
 * 
 * @author hejj
 */
public abstract class ParameterBase {

	protected final static char[] CRLF = { 0x0d, 0x0a };
	protected final static String PRM = "PRM";
	protected final static String SVER = "CRC:";
	protected DbHelper dbHelper;
	protected ParaGenDtl paraGenDtl;
	protected String paraFileName;
	protected int dbRecordNum;
	private static Logger logger = Logger.getLogger(ParameterBase.class
			.getName());

	public void init(DbcpHelper dbcpHelper, ParaGenDtl dtl) throws Exception {
		String thisClassName = this.getClass().getName();
		thisClassName = thisClassName.substring(
				thisClassName.lastIndexOf(".") + 1, thisClassName.length());
		this.dbHelper = new DbHelper(thisClassName, dbcpHelper.getConnection());
		this.paraGenDtl = dtl;
		this.paraFileName = PRM + "." + paraGenDtl.getParmTypeId() + "."
				+ paraGenDtl.getVerNum().substring(0, 8) + "."
				+ paraGenDtl.getVerNum().substring(8, 10);
		// logger.info("Parm"+paraGenDtl.getParmTypeId()+"  init ended!");
	}

	public void setDbRecordNum(Vector v) {
		if (v == null) {
			this.dbRecordNum = 0;
		} else {
			this.dbRecordNum = v.size();
		}

	}

	private int getVSize(Vector v) {
		if (v == null) {
			return 0;
		}
		return v.size();
	}

	public void setDbRecordNum(Vector v1, Vector v2, Vector v3, Vector v4,
			Vector v5, Vector v6, Vector v7) {

		this.dbRecordNum = this.getVSize(v1) + this.getVSize(v2)
				+ this.getVSize(v3) + this.getVSize(v4) + this.getVSize(v5) + this.getVSize(v6) + this.getVSize(v7);

	}

	public int getDbRecordNum() {
		return this.dbRecordNum;
	}

	public abstract boolean formParaFile();

	public void release() {
		try {
			dbHelper.closeConnection();
		} catch (Exception e) {
		}
	}

	protected Vector<String> formatRecord(String type, Vector<String[]> recV,
			int[] format) throws UnsupportedEncodingException {
		Vector<String> result = new Vector<String>();
		Iterator it = recV.iterator();
		String colon = ":";
		int len = format.length;
		while (it.hasNext()) {
			String[] fields = (String[]) (it.next());
			StringBuilder sb = new StringBuilder();
			sb.append(type).append(colon);
			for (int i = 0; i < len; i++) {
				sb.append(formatField(fields[i], format[i]));
			}
			result.add(sb.toString());
		}
		return result;
	}

	protected String formatField(String field, int length)
			throws UnsupportedEncodingException {
                int inputStrLen = field.getBytes(FrameCharConstant.SYSTEM_CODING).length;//field.getBytes("GB18030").length;
		String space = " ";
		int i = length - inputStrLen;
		if (i <= 0) {
			field = field.substring(0, length);
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(field);
			for (int j = 0; j < i; j++) {
				sb.append(space);
			}
			field = sb.toString();
		}
		// logger.info(CharUtil.IsoToGbk(field));
		// return CharUtil.GbkToIso(field);
		return field;
	}

	protected boolean writeDataToFile(Vector data) {
		boolean result = false;
		OutputStreamWriter out = null;
		FileOutputStream fos = null;
		File paraFile;
		int len;
		int recordLen = -1;
		try {
			paraFile = new File(FrameCodeConstant.parmDstrbPath, paraFileName);
			out = new FileWriter(paraFile);
			
			fos = new FileOutputStream(paraFile);
			out = new OutputStreamWriter(fos, FrameCharConstant.SYSTEM_CODING);// OutputStreamWriter(new FileOutputStream(paraFile), "GB18030"));

			if (data.size() > 0) {
				StringBuilder sb = new StringBuilder();
				Iterator it = data.iterator();
				while (it.hasNext()) {
					String line = (String) (it.next());

					if (line != null) {
						len = line.length();
						if (recordLen == -1) {
							recordLen = len;
						}
						if (len != recordLen) {
//							logger.warn("记录长度不一样:" + line);
						}
						out.write(line, 0, line.length());
					}
					sb.append(line);
					out.write(CRLF);
					sb.append(CRLF);
					out.flush();
				}

				String s = sb.toString();
				byte[] b = null;// new byte[2800000];
				b = s.getBytes();
				long crc32 = CharUtil.getCRC32Value(b);
				String crc = Long.toHexString(crc32);
				for (int i = crc.length(); i < 8; i++) {
					crc = "0" + crc;
				}
				out.write(SVER + crc);
				out.flush();
			} else if (data.isEmpty()) {
				out.write(SVER + "00000000");
				out.flush();
			}
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
					// encode(paraFile);
				} catch (Exception e) {
				}
			}
			if (out != null) {
				try {
					out.close();
					// encode(paraFile);
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

	/**
	 * 生成文件
	 * 
	 * @param recV
	 *            消息记录
	 * @param paramType
	 *            参数类型
	 * @param format
	 *            参数格式
	 * @return 文件是否成功生成
	 * @throws UnsupportedEncodingException
	 * @throws ParameterException
	 */
	protected boolean dealData(Vector<String[]> recV, String paramType,
			int[] format) throws UnsupportedEncodingException,
			ParameterException {
		// 记录从数据库取出将要写文件的记录数 add by hejj 2012-08-27
		this.setDbRecordNum(recV);
		// format data
		Vector<String> formatedRecV;
		if (recV == null) {
			logger.warn("Get record from parameter table error! ");
			formatedRecV = new Vector<String>();
		} else {
			// format data
			formatedRecV = formatRecord(paramType, recV, format);
		}
		// write data to file
		boolean result = writeDataToFile(formatedRecV);
		if (!result) {
			throw new ParameterException("Write parameter data to file "
					+ paraFileName + " error! ");
		}
		logger.info("Parameter file " + paraFileName
				+ " generated successfully. ");
		return result;
	}

	// private void encode(File of) {
	// File ef = new File(FrameCodeConstant.ParmEncodePath, paraFileName);
	// EncodeDecodeUtil ed = new EncodeDecodeUtil();
	// ed.encode(of, ef);
	// }
}
