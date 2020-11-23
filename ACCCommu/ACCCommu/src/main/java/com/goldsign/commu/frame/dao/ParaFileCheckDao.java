package com.goldsign.commu.frame.dao;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubDbUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.ParaFileCheckRule;
import com.goldsign.lib.db.util.DbHelper;

/**
 *
 * @author hejj
 */
public class ParaFileCheckDao {

    private static Logger logger = Logger.getLogger(ParaFileCheckDao.class
            .getName());
    private static String CHECK_FLAG_YES = "1";

    // private static String CHECK_FLAG_NO = "0";
    public Vector<ParaFileCheckRule> getFileSizeConfig(String parmTypeId)
            throws Exception {
        boolean result = false;
        DbHelper dbHelper = null;
        Vector<ParaFileCheckRule> v = new Vector<ParaFileCheckRule>();
        ParaFileCheckRule vo;

        try {
            dbHelper = new DbHelper("dataDbUtil",
                    FrameDBConstant.OP_DBCPHELPER.getConnection());

            String sqlStr = "select parm_type_id,chk_operator,chk_size from "+FrameDBConstant.COM_ST_P+"op_cm_cfg_para_fle_sz where parm_type_id=? and check_flag=?  ";
            Object[] values = {parmTypeId, ParaFileCheckDao.CHECK_FLAG_YES};

            result = dbHelper.getFirstDocument(sqlStr, values);
            while (result) {
                vo = this.getParaFileCheckRule(dbHelper);
                v.add(vo);
                result = dbHelper.getNextDocument();
            }

        } catch (Exception e) {
            PubUtil.handleException(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }

        return v;
    }

    private ParaFileCheckRule getParaFileCheckRule(DbHelper dbHelper)
            throws Exception {
        ParaFileCheckRule vo = new ParaFileCheckRule();
        vo.setParmTypeId(dbHelper.getItemValue("parm_type_id"));
        vo.setOperator(dbHelper.getItemValue("chk_operator"));
        vo.setSize(dbHelper.getItemIntValue("chk_size"));
        return vo;
    }

    public int writeFileCheckLog(String parmTypeId, String verNum,
            String verType, String errorCode, String remark) throws Exception {

        DbHelper dbHelper = null;
        int n = 0;
        try {
            dbHelper = new DbHelper("dataDbUtil",
                    FrameDBConstant.OP_DBCPHELPER.getConnection());

            String sqlStr = "insert into "+FrameDBConstant.COM_ST_P+"op_cm_log_para_fle_chk(water_no,parm_type_id,version_no,version_type,error_code,check_time,remark) values(?,?,?,?,?,sysdate,?)";
            int waterNo = PubDbUtil.getTableSequence(
                    FrameDBConstant.COM_ST_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"op_cm_log_para_fle_chk", dbHelper);
            Object[] values = {new Integer(waterNo), parmTypeId, verNum,
                verType, errorCode, remark};
            n = dbHelper.executeUpdate(sqlStr, values);

        } catch (Exception e) {
            PubUtil.handleException(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }

        return n;

    }

    //磁悬浮接入，添加line_id字段 20151217 by lindaquan
    public int updateParaVerSyn(String parmTypeId, String verNum, String verType, String lineIds)
            throws Exception {

        DbHelper dbHelper = null;
        int n = 0;
        
        try {
            dbHelper = new DbHelper("dataDbUtil",
                    FrameDBConstant.OP_DBCPHELPER.getConnection());
            
            String[] lines = lineIds.split("#");
            for(String lineId : lines){
                n = 0;
                Object[] values = {verNum, parmTypeId, verType, lineId};
                String sqlStr = "update "+FrameDBConstant.COM_ST_P+"op_prm_para_ver_cm set version_no=? where parm_type_id=? and version_type=?  and line_id =?";
                n = dbHelper.executeUpdate(sqlStr, values);
                if (n == 0) {
                    sqlStr = "insert into "+FrameDBConstant.COM_ST_P+"op_prm_para_ver_cm(version_no,parm_type_id,version_type,line_id) values(?,?,?,?)";
                    n = dbHelper.executeUpdate(sqlStr, values);
                }
            }
            
        } catch (Exception e) {
            PubUtil.handleException(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }

        return n;

    }
}
