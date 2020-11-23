/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;

import org.apache.log4j.Logger;

import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author hejj
 */
public class ParaVerDao {

	private static Logger logger = Logger.getLogger(ParaVerDao.class.getName());

	public ParaVerDao() throws Exception {
		super();
	}

	public boolean updateDistributeTimes(String verNum, String parmTypeId,
			String verType) {
		boolean result = false;
		String sqlStr = "update "+FrameDBConstant.COM_ST_P+"op_prm_para_ver set distribute_times=distribute_times+1"
				+ " where version_no=? and parm_type_id=? and record_flag=?";
		Object[] values = { verNum, parmTypeId, verType };
		DbHelper dbHelper = null;
		try {
			dbHelper = new DbHelper("ParaVerDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			dbHelper.executeUpdate(sqlStr, values);
			result = true;
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return result;
	}
}
