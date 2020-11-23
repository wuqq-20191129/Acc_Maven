/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

import com.goldsign.commu.frame.exception.FileWriteException;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Vector;

/**
 *
 * @author Administrator
 */
public abstract class FileWriterBase {

    protected static String FIELD_DELIM = ",";
    protected static String CHARSET = "GBK";
    protected static String FILE_NAME_BCP_PRIX = "bcp";
    protected final static char[] CRLF_1 = {0x0d, 0x0a};//换行符
    protected final static char[] CRLF_UNIX = {0x0a};//换行符
    protected final static char[] CRLF_WIN = {0x0d, 0x0a};//换行符

    public abstract void writeFile(String pathBcp, String fileName, String tradType, Vector records) throws FileWriteException;

    public static String getFileNameBcp(String tradType, String fileName) {
        return FILE_NAME_BCP_PRIX + "." + tradType + "." + fileName;
    }

    public static char[] getLineDelimter() {
        String os = System.getProperty("os.name");
        if (os.indexOf("Windows") != -1) {
            return CRLF_WIN;
        } else {
            return CRLF_UNIX;
        }
    }

    protected void closeFile(FileOutputStream fos, OutputStreamWriter osr, BufferedWriter bw) {
        try {

            if (fos != null) {
                fos.close();
            }
            if (osr != null) {
                osr.close();
            }
            if (bw != null) {
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void closeFile(FileOutputStream fos, DataOutputStream dos) {
        try {

            if (fos != null) {
                fos.close();
            }
            if (dos != null) {
                dos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    protected String convertFenToYuan(int fen) {
//        BigDecimal yan = new BigDecimal(fen);
//        yan.setScale(2);
//        BigDecimal result = yan.divide(new BigDecimal(100));
//        return result.toString();
//    }

    protected void addCommonToBuff(StringBuffer sb, FileRecordBase vo) {
        sb.append(vo.getBalanceWaterNo() + FileWriterBase.FIELD_DELIM); //清算流水号

        sb.append(vo.getFileName() + FileWriterBase.FIELD_DELIM); //文件
        sb.append(vo.getCheckFlag()); //校验标志
    }

    protected void writeFileForCommon(String pathBcp, String fileName, String tradType, Vector records) throws FileWriteException {
        String fileNameBcp = pathBcp + "/" + this.getFileNameBcp(tradType, fileName);
        FileOutputStream fos = null;
        DataOutputStream dos = null; // new DataOutputStream(os);
        FileRecordBase vo;
        String line;
        char[] cs;
        try {
            fos = new FileOutputStream(fileNameBcp);
            dos = new DataOutputStream(fos);
            for (int i = 0; i < records.size(); i++) {
                vo = (FileRecordBase) records.get(i);
                line = this.getLine(vo);
                cs = line.toCharArray();
                for (char c : cs) {
                    dos.write((int) c);
                }
                dos.flush();
            }
        } catch (Exception ex) {
            throw new FileWriteException(ex.getMessage());
        } finally {
            this.closeFile(fos, dos);
        }
    }

    public abstract String getLine(FileRecordBase vob);
    // @Override
}
