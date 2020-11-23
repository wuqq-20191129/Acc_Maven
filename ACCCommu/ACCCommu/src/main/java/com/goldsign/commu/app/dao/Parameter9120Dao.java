package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;

public class Parameter9120Dao {
	private static Logger logger = Logger.getLogger(Parameter9120Dao.class
			.getName());
	private static String sqlstr = "select file_path from "+FrameDBConstant.COM_ST_P+"op_prm_dev_program where version_no=? and record_flag=? and parm_type_id=?";

	public static String querRemoteFilePath(ParaGenDtl paraGenDtl,
			DbHelper dbHelper) {
		String remotePath = "";
		List<String> pStmtValues = new ArrayList<String>();
		pStmtValues.clear();
		pStmtValues.add(paraGenDtl.getVerNum());
		pStmtValues.add(paraGenDtl.getVerType());
		pStmtValues.add(paraGenDtl.getParmTypeId());
		try {
			boolean result = dbHelper.getFirstDocument(sqlstr,
					pStmtValues.toArray());
			if (result) {
				remotePath = dbHelper.getItemValue("file_path");
			}
		} catch (Exception e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_chargeserv_conf error! ", e);
		}
		return remotePath;
	}

}
