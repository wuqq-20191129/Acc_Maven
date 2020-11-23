package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.SysLogLevelVo;
import org.apache.log4j.Logger;

import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author hejj
 */
public class LogLevelDao {

	private static Logger logger = Logger
			.getLogger(LogLevelDao.class.getName());

	public LogLevelDao() {
		super();
	}

	public SysLogLevelVo getLogLevel() {
		boolean result = false;
		DbHelper dbHelper = null;
		SysLogLevelVo vo = null;

		try {
			dbHelper = new DbHelper("LogDbUtil",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			String sqlStr = "select sys_code,sys_level,set_time,operator,"
					+ "version_no,record_flag from "+FrameDBConstant.COM_ST_P+"op_log_level where "
					+ "record_flag=? and sys_code=? ";
			Object[] values = { FrameCodeConstant.RECORD_FLAG_CURRENT,
					FrameCodeConstant.SYSTEM_COMMU };
			result = dbHelper.getFirstDocument(sqlStr, values);
			if (result) {
				vo = this.getResultRecord(dbHelper);
			}

		} catch (Exception e) {
			logger.error("访问op_log_level表错误! " + e);
		} finally {
			try {
				if (dbHelper != null) {
					dbHelper.closeConnection();
				}
			} catch (Exception e) {
				logger.error("catch one exception",e);
			}
		}

		return vo;
	}

	private SysLogLevelVo getResultRecord(DbHelper dbHelper) throws Exception {
		SysLogLevelVo vo = new SysLogLevelVo();
		vo.setSysCode(dbHelper.getItemValue("sys_code"));
		vo.setSysLevel(dbHelper.getItemValue("sys_level"));
		vo.setSetTime(dbHelper.getItemValue("set_time"));
		vo.setOperator(dbHelper.getItemValue("operator"));
		vo.setVersionNo(dbHelper.getItemValue("version_no"));
		vo.setRecordFlag(dbHelper.getItemValue("record_flag"));
		return vo;

	}
}
