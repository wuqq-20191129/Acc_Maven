package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 充值终端通讯参数
 * 
 * @author zhangjh
 * 
 */
public class Parameter8101Dao {
	private static Logger logger = Logger.getLogger(Parameter0901Dao.class
			.getName());
	private static String sqlstr = "select server_ip,server_port,server_level from "+FrameDBConstant.COM_ST_P+"op_prm_chargeserv_conf where version_no=? and record_flag=?";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int length) {
		Vector<String[]> recV = null;
		boolean result;

		List<String> pStmtValues = new ArrayList<String>();
		pStmtValues.clear();
		pStmtValues.add(paraGenDtl.getVerNum());
		pStmtValues.add(paraGenDtl.getVerType());
		try {
			result = dbHelper.getFirstDocument(sqlstr, pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[length];
					fields[0] = dbHelper.getItemValue("server_ip");
					fields[1] = dbHelper.getItemValue("server_port");
					fields[2] = dbHelper.getItemValue("server_level");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_chargeserv_conf error! " + e);
			return null;
		}
		return recV;
	}

}
