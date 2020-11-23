package com.goldsign.commu.frame.dao;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.ParaInformDtl;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author hejj
 */
public class ParaInformDtlDao {

	private static Logger logger = Logger.getLogger(ParaInformDtlDao.class
			.getName());

	public ParaInformDtlDao() throws Exception {
		super();
	}

	public void updateResult(ParaInformDtl dtl) {
		String sqlStr = "update "+FrameDBConstant.COM_ST_P+"op_prm_para_inform_dtl set inform_result=? where water_no=? and line_id=? and station_id=?";
		Object values[] = new Object[4];
		values[0] = dtl.getInformResult();
		values[1] = new Integer(dtl.getWaterNo());
		values[2] = dtl.getLineId();
		values[3] = dtl.getStationId();
		DbHelper dbHelper = null;

		try {
			dbHelper = new DbHelper("ParaInformDtlDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			dbHelper.executeUpdate(sqlStr, values);
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}
	}
}
