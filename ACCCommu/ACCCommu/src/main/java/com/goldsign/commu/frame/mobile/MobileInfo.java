package com.goldsign.commu.frame.mobile;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.exception.ParameterException;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.vo.MBInfoDetailVo;
import com.goldsign.commu.frame.vo.MBInfoVo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;
import org.apache.log4j.Logger;


/**
 * 
 * @author lindaquan
 */
public class MobileInfo {

        protected final static String ftpDir = FrameCodeConstant.ftpLocalDir.substring(0,
			FrameCodeConstant.ftpLocalDir.lastIndexOf('/')) + "/inf";
	protected final static char[] CRLF = { 0x0d, 0x0a };
	protected final static String SVER = "CRC:";
        protected final static String SPACER = ",";
        private static final int[] FORMAT = { 1, 10, 14, 10, 14, 1, 1024 };
	protected Vector<MBInfoVo> mbInfos;
	protected String infoFileName;
	private static Logger logger = Logger.getLogger(MobileInfo.class.getName());

	public void init(Vector<MBInfoVo> mBInfos, String fileName) throws Exception {
		this.mbInfos = mBInfos;
		this.infoFileName = fileName;;
	}

        public boolean formInfoFile() {
                boolean result;
                logger.info("手机支付信息下发文件 started! ");
                try {
                    Vector<String[]> recV = getRecord();
                    result = dealData(recV, FORMAT);
                } catch (Exception e) {
                    logger.error("手机支付信息下发文件 error! " + e);
                    result = false;
                }
                logger.info("手机支付信息下发文件 ended! ");
                return result;
	}

	protected Vector<String> formatRecord(Vector<String[]> recV, int[] format) 
                throws UnsupportedEncodingException {
		Vector<String> result = new Vector<String>();
		Iterator it = recV.iterator();
		int len = format.length;
		while (it.hasNext()) {
			String[] fields = (String[]) (it.next());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < len; i++) {
				sb.append(formatField(fields[i], format[i]));
                                if((i+1)<len){
                                    sb.append(SPACER);
                                }
			}
			result.add(sb.toString());
		}
		return result;
	}

	protected String formatField(String field, int length)
			throws UnsupportedEncodingException {
		int inputStrLen = field.getBytes("GB18030").length;
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
			paraFile = new File(ftpDir, this.infoFileName);
			out = new FileWriter(paraFile);
			fos = new FileOutputStream(paraFile);
			out = new OutputStreamWriter(fos, "GB18030");

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
                                out.write(CRLF);
				out.flush();
			} else if (data.isEmpty()) {
				out.write(SVER + "00000000");
				out.flush();
			}
			result = true;
		} catch (Exception e) {
                        e.printStackTrace();
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
	 * @param recV 消息记录
	 * @param format 格式
	 * @return 文件是否成功生成
	 * @throws UnsupportedEncodingException
	 * @throws ParameterException
	 */
	protected boolean dealData(Vector<String[]> recV, int[] format) 
                throws UnsupportedEncodingException, ParameterException {
		// format data
		Vector<String> formatedRecV;
		if (recV == null) {
			logger.warn("Get record from mobile info table error! ");
			return false;
		} else {
			// format data
			formatedRecV = formatRecord(recV, format);
		}
		// write data to file
		boolean result = writeDataToFile(formatedRecV);
		if (!result) {
			throw new ParameterException("Write mobile info to file "
					+ this.infoFileName + " error! ");
		}
		logger.info("Mobile info file " + this.infoFileName
				+ " generated successfully. ");
		return result;
	}


    private Vector<String[]> getRecord() {
        Vector<String[]> recV  = new Vector<String[]>();
        for(MBInfoVo infoVo: this.mbInfos){
                String[] fields = new String[FORMAT.length];
                fields[0] = infoVo.getInfoLevel();
                fields[1] = infoVo.getAddOperator();
                fields[2] = infoVo.getAddDate();
                fields[3] = infoVo.getAuditOperator();
                fields[4] = infoVo.getAuditDate();
                fields[5] = infoVo.getInfoFlag();
                Vector<MBInfoDetailVo> detailVos = infoVo.getmBInfoDetails();
                String content = "";
                for(MBInfoDetailVo detailVo : detailVos){
                    content += detailVo.getInfoContent();
                }
                fields[6] = content;
                recV.add(fields);
        }
        return recV;
    }
}
