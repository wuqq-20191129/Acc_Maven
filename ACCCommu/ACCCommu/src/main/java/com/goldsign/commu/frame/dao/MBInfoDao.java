/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.MBInfoDetailVo;
import com.goldsign.commu.frame.vo.MBInfoVo;
import com.goldsign.lib.db.util.DbHelper;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 手机平台信息发布查询、更新
 * @author lindaquan
 */
public class MBInfoDao {
        private static Logger logger = Logger.getLogger(MBInfoDao.class.getName());

	public MBInfoDao() throws Exception {
		super();
	}
        
        public Vector<MBInfoVo> getMBInfo(){
            boolean result;
            Vector<MBInfoVo> mBInfos = new Vector<MBInfoVo>();
            String sql = "select info_no, info_level, add_operator, to_char(add_date,'yyyymmddhhmiss') add_date, audit_operator, "
                    + " to_char(audit_date,'yyyymmddhhmiss') audit_date, info_flag, remark"
                    + " from "+FrameDBConstant.COM_ST_P+"OP_MB_INFO where info_flag='1' and distribute_result='0'";
            DbHelper dbHelper = null;
            
            try {
                    dbHelper = new DbHelper("MBInfoDao",FrameDBConstant.OP_DBCPHELPER.getConnection());
                    result = dbHelper.getFirstDocument(sql);
                    while (result) {
                            MBInfoVo mBInfoVo = new MBInfoVo();
                            String infoNo = dbHelper.getItemValue("info_no");
                            mBInfoVo.setInfoNo(infoNo);
                            mBInfoVo.setInfoLevel(dbHelper.getItemValue("info_level"));
                            mBInfoVo.setAddOperator(dbHelper.getItemValue("add_operator"));
                            mBInfoVo.setAddDate(dbHelper.getItemValue("add_date"));
                            mBInfoVo.setAuditOperator(dbHelper.getItemValue("audit_operator"));
                            mBInfoVo.setAuditDate(dbHelper.getItemValue("audit_date"));
                            mBInfoVo.setRemark(dbHelper.getItemValue("remark"));
                            mBInfoVo.setInfoFlag(dbHelper.getItemValue("info_flag"));
                            Vector<MBInfoDetailVo> mBInfoDetails = getInfoDetails(infoNo);
                            if(mBInfoDetails.size()>0){
                                mBInfoVo.setmBInfoDetails(mBInfoDetails);
                                mBInfos.add(mBInfoVo);
                            }
                            result = dbHelper.getNextDocument();
                    }
            } catch (Exception e) {
                    PubUtil.handleExceptionNoThrow(e, logger);
            } finally {
                    PubUtil.finalProcess(dbHelper);
            }
            
            return mBInfos;
        }
        
        public boolean updateMBInfo(String infoNo, String flag, String fileName){
            boolean result = false;
            DbHelper dbHelper = null;
            String sqlStr = "update "+FrameDBConstant.COM_ST_P+"OP_MB_INFO set distribute_result=?,distribute_date=sysdate, file_name=? where info_no=?";
            Object[] values = {flag, fileName, infoNo};
            try {
                    dbHelper = new DbHelper("MBInfoDao", FrameDBConstant.OP_DBCPHELPER.getConnection());
                    result = dbHelper.executeUpdate(sqlStr, values)>0;
            } catch (Exception e) {
                    PubUtil.handleExceptionNoThrow(e, logger);
            } finally {
                    PubUtil.finalProcess(dbHelper);
            }
            return result;
        }

        private Vector<MBInfoDetailVo> getInfoDetails(String infoNo) {
            Vector<MBInfoDetailVo> mBInfoDetails = new Vector<MBInfoDetailVo>();
            boolean result;
            String sql = "select info_no, info_content from "+FrameDBConstant.COM_ST_P+"op_mb_info_detail where info_no=?";
            DbHelper dbHelper = null;
            Object[] values = {infoNo};

            try {
                    dbHelper = new DbHelper("MBInfoDao",FrameDBConstant.OP_DBCPHELPER.getConnection());
                    result = dbHelper.getFirstDocument(sql, values);
                    while (result) {
                            MBInfoDetailVo detailVo = new MBInfoDetailVo();
                            detailVo.setInfoNo(dbHelper.getItemValue("info_no"));
                            detailVo.setInfoContent(dbHelper.getItemValue("info_content"));
                            mBInfoDetails.add(detailVo);
                            result = dbHelper.getNextDocument();
                    }
            } catch (Exception e) {
                    PubUtil.handleExceptionNoThrow(e, logger);
            } finally {
                    PubUtil.finalProcess(dbHelper);
            }
            return mBInfoDetails;
        }
        
        
        /*
         * 取当天最大文件序号
         */
        public int getMaxSeq(String curDay) {
            boolean result;
            int seq = 0;
            String sql = "select to_number(nvl(substr(max(file_name),-3),0)) seq_no from "+FrameDBConstant.COM_ST_P+"OP_MB_INFO where instr(file_name, ?) > 0";
            DbHelper dbHelper = null;
            Object[] values = {curDay};

            try {
                    dbHelper = new DbHelper("MBInfoDao",FrameDBConstant.OP_DBCPHELPER.getConnection());
                    result = dbHelper.getFirstDocument(sql, values);
                    if (result) {
                            seq = dbHelper.getItemIntValue("seq_no");
                    }
            } catch (Exception e) {
                    PubUtil.handleExceptionNoThrow(e, logger);
            } finally {
                    PubUtil.finalProcess(dbHelper);
            }
            return seq;
        }
}
