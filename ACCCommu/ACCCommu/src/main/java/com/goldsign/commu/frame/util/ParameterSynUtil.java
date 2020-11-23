package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.dao.ParaFileCheckDao;
import com.goldsign.commu.frame.exception.ParaCheckException;
import com.goldsign.commu.frame.vo.ParaFileAttribute;
import com.goldsign.commu.frame.vo.ParaFileCheckRule;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.commu.frame.vo.ParaInformDtl;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.util.Iterator;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class ParameterSynUtil {

    /**
     * 运营管理系统的FTP配置结束
     */
    // 如下参数不需要校验文件行数，因为如下几个读写器程序参数非本系统生成
    private static final String[] NOT_DEED_VALID_FILELINES_PARM = {"8100",
        "9120", "9220", "9320", "9420", "9520"};
    private static String OPERATOR_GREATER = ">";
    private static String OPERATOR_LESS = "<";
    private static String OPERATOR_EQUAL = "=";
    private static Logger logger = Logger.getLogger(ParameterSynUtil.class
            .getName());
    public static String FILE_PARAMETER_PREFIX = "PRM";
    // private static String ERROR_CODE_DB_RECORD_ZERO = "1";//参数数据库记录数为0
    private static String ERROR_CODE_DB_RECORD_NOT_EQUAL = "2";// 参数数据库记录数与文件记录数不等
    // private static String ERROR_CODE_FILE_SIZE_ZERO = "3";//参数文件大小为0
    private static String ERROR_CODE_FILE_SIZE_RULE = "4";// 参数文件大小不符合校验规则
    // private static String ERROR_CODE_FILE_RECORD_ZERO = "5";//参数文件记录数为0
    private static String ERROR_CODE_OTHER = "9";// 其他原因

    public static String getParameterFileName(String parmTypeId, String verNum) {
        return FILE_PARAMETER_PREFIX + "." + parmTypeId + "."
                + verNum.substring(0, 8) + "." + verNum.substring(8, 10);
    }

    /**
     * 获取参数文件的记录数、文件大小（byte)
     *
     * @param path
     * @param fileName
     * @return
     * @throws Exception
     */
    public ParaFileAttribute getParaFileAttribute(String path, String fileName)
            throws Exception {
        ParaFileAttribute pfa = new ParaFileAttribute();
        File f = null;
        FileReader fr = null;
        BufferedReader br = null;
        int recordNum = 0;
        try {
            f = new File(path, fileName);
            fr = new FileReader(f);
            br = new BufferedReader(fr);

            while ((br.readLine()) != null) {
                recordNum++;
            }

            pfa.setRecordNum(recordNum - 1);
            pfa.setFileSize(f.length());

        } catch (Exception ex) {
            logger.error(ex);
            throw ex;

        } finally {
            close(fr, br);
        }
        return pfa;

    }

    public int deleteFilesForError(String path, Vector vHandled) {
        ParaGenDtl paraGenDtl;
        String fileName;
        int n = 0;
        for (int i = 0; i < vHandled.size(); i++) {
            paraGenDtl = (ParaGenDtl) vHandled.get(i);
            fileName = ParameterSynUtil.getParameterFileName(
                    paraGenDtl.getParmTypeId(), paraGenDtl.getVerNum());
            n += this.deleteFileForError(path, fileName);
        }
        return n;

    }

    public int deleteFileForError(String path, String fileName) {
        File f = new File(path, fileName);
        int n = 0;
        if (f.exists()) {
            f.delete();
            n = 1;
        }
        return n;

    }

    private void close(FileReader fr, BufferedReader br) {
        try {
            if (fr != null) {
                fr.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (Exception e) {
            logger.error("catch one exception", e);
        }

    }

    private boolean checkParaFileForFileSize(Vector<ParaFileCheckRule> rules,
            int fileSize) {
        if (rules.isEmpty()) {
            return true;
        }
        ParaFileCheckRule rule;
        for (int i = 0; i < rules.size(); i++) {
            rule = rules.get(i);
            if (!checkParaFileForFileSizeByRule(rule, fileSize)) {
                return false;
            }

        }
        return true;

    }

    private boolean checkParaFileForFileSizeByRule(ParaFileCheckRule rule,
            int fileSize) {
        // int ruleSize = rule.getSize() * 1024;
        int ruleSize = rule.getSize();
        String operator = rule.getOperator();

        if (operator.equals(ParameterSynUtil.OPERATOR_GREATER)) {
            if (fileSize > ruleSize) {
                return true;
            }
        }
        if (operator.equals(ParameterSynUtil.OPERATOR_LESS)) {
            if (fileSize < ruleSize) {
                return true;
            }
        }
        if (operator.equals(ParameterSynUtil.OPERATOR_EQUAL)) {
            if (fileSize == ruleSize) {
                return true;
            }
        }
        return false;

    }

    public boolean checkParaFile(String paramTypeId, String verType,
            String verNum, int dbRecordNum) {
        ParaFileCheckDao dao = new ParaFileCheckDao();

        String paramFileName = ParameterSynUtil.getParameterFileName(
                paramTypeId, verNum);
        try {
            ParaFileAttribute pfa = this.getParaFileAttribute(
                    FrameCodeConstant.parmDstrbPath, paramFileName);

            Vector<ParaFileCheckRule> rules = dao
                    .getFileSizeConfig(paramTypeId);
            // by zhangjh @ 2013-05-09
            // if (pfa.getRecordNum() == 0) {
            // throw new ParaCheckException("参数文件" + paramFileName + "文件记录数为0",
            // ParameterSynUtil.ERROR_CODE_FILE_RECORD_ZERO);
            // }
            // if (pfa.getFileSize() == 0) {
            // throw new ParaCheckException("参数文件" + paramFileName + "文件大小为0",
            // ParameterSynUtil.ERROR_CODE_FILE_SIZE_ZERO);
            // }
            // if (dbRecordNum == 0) {
            // throw new ParaCheckException("参数文件" + paramFileName + "数据库记录数为0",
            // ParameterSynUtil.ERROR_CODE_DB_RECORD_ZERO);
            // }
            if (dbRecordNum != pfa.getRecordNum()) {
                throw new ParaCheckException("参数文件" + paramFileName + "数据库记录数:"
                        + dbRecordNum + "文件记录数:" + pfa.getRecordNum() + "不相等",
                        ParameterSynUtil.ERROR_CODE_DB_RECORD_NOT_EQUAL);
            }
            if (!this.checkParaFileForFileSize(rules, (int) pfa.getFileSize())) {
                throw new ParaCheckException("参数文件" + paramFileName + "文件大小:"
                        + pfa.getFileSize() + "不符合校验规则",
                        ParameterSynUtil.ERROR_CODE_FILE_SIZE_RULE);
            }
        } catch (ParaCheckException ex) {
            this.writeFileCheckLog(paramTypeId, verType, verNum, ex);
            return false;
        } catch (Exception e) {
            this.writeFileCheckLog(paramTypeId, verType, verNum, e);
            return false;
        }
        return true;

    }

    private void writeFileCheckLog(String paramTypeId, String verType,
            String verNum, ParaCheckException e) {
        ParaFileCheckDao dao = new ParaFileCheckDao();
        try {
            dao.writeFileCheckLog(paramTypeId, verNum, verType,
                    e.getErrorCode(), e.getMessage());
        } catch (Exception ex) {
            logger.error(e.getMessage());
        }
    }

    private void writeFileCheckLog(String paramTypeId, String verType,
            String verNum, Exception e) {
        ParaFileCheckDao dao = new ParaFileCheckDao();
        try {
            dao.writeFileCheckLog(paramTypeId, verNum, verType,
                    ParameterSynUtil.ERROR_CODE_OTHER, e.getMessage());
        } catch (Exception ex) {
            logger.error(e.getMessage());
        }
    }

    //磁悬浮接入，添加line_id字段 20151217 by lindaquan
    public int updateParaVerSyn(String parmTypeId, String verNum, String verType, String lineIds)
            throws Exception {
        ParaFileCheckDao dao = new ParaFileCheckDao();
        return dao.updateParaVerSyn(parmTypeId, verNum, verType, lineIds);
    }

    public int updateParaVerSynForAll(Vector<ParaGenDtl> v, Vector<ParaInformDtl> lineStationsV) throws Exception {
        // String parmTypeId, verNum, verType;
        ParaGenDtl paraGenDtl;
        int n = 0;
        for (int i = 0; i < v.size(); i++) {
            paraGenDtl = v.get(i);
            //磁悬浮接入，添加line_id字段 20151217 by lindaquan
            String lineIds = getLineIds(lineStationsV);
            n += updateParaVerSyn(paraGenDtl.getParmTypeId(),
                    paraGenDtl.getVerNum(), paraGenDtl.getVerType() ,lineIds);
        }
        return n;

    }

    /**
     *
     * @param parmTypeId
     * @return 读写器程序参数返回false，非读写器程序参数返回true
     */
    public boolean isNeedToCheckFile(String parmTypeId) {
        boolean result = true;
        // 改参数文件是否是读写器程序参数
        for (String paramType : NOT_DEED_VALID_FILELINES_PARM) {
            if (paramType.equals(parmTypeId)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /*
     * 拼接线路ID
     */
    private String getLineIds(Vector<ParaInformDtl> lineStationsV) {
        String linesString="";
        int i=0;
        for (ParaInformDtl pifd : lineStationsV) {
            if(linesString.indexOf(pifd.getLineId())==-1){
                if(i>0){
                    linesString = linesString + "#";
                }
                linesString = linesString + pifd.getLineId();
            }
            i++;
        }
        return linesString;
    }
}
