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
 * 行政收费罚金参数
 * 
 * @author zhangjh
 */
public class Parameter0801Dao {

	private static Logger logger = Logger.getLogger(Parameter0801Dao.class
			.getName());
	private static String sqlStr = "select admin_manager_id,admin_charge,line_id,station_id,card_main_id,card_sub_id "
                + "from "+FrameDBConstant.COM_ST_P+"op_prm_admin_manager_type t where  version_no=? and record_flag=?";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;

		List<String> pStmtValues = new ArrayList<String>();
		pStmtValues.clear();
		pStmtValues.add(paraGenDtl.getVerNum());
		pStmtValues.add(paraGenDtl.getVerType());
		try {
			result = dbHelper.getFirstDocument(sqlStr, pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("admin_manager_id");
					fields[1] = dbHelper.getItemValue("admin_charge");
					fields[2] = dbHelper.getItemValue("line_id")
							+ dbHelper.getItemValue("station_id");
					fields[3] = dbHelper.getItemValue("card_main_id")
							+ dbHelper.getItemValue("card_sub_id");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_admin_manager_type error! " + e);
			return null;
		}
		return recV;
	}
}
