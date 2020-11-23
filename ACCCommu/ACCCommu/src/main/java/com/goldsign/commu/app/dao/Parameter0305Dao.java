package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 手机票逻辑印刻号对照
 * Date 20150413
 * version:1.08
 * @author lindaquan
 */
public class Parameter0305Dao {

	private static Logger logger = Logger.getLogger(Parameter0305Dao.class
			.getName());
	private static String sqlStr = "select logic_no, icc_id from "+FrameDBConstant.COM_ST_P+"op_prm_logic_icc_list where record_flag=? ";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;
		List<String> pStmtValues = new ArrayList<String>();
		pStmtValues.clear();
		pStmtValues.add(paraGenDtl.getVerType());
		try {
			result = dbHelper.getFirstDocument(sqlStr, pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("logic_no");
					fields[1] = dbHelper.getItemValue("icc_id");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table op_prm_logic_icc_list error! " + e);
			return null;
		}
		return recV;
	}
}
