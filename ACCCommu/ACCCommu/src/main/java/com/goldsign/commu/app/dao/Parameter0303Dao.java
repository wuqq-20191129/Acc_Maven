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
 * 乘次票专用参数
 * 
 * @author zhangjh
 */
public class Parameter0303Dao {

	private static Logger logger = Logger.getLogger(Parameter0303Dao.class
			.getName());
	private static String sqlStr = "select card_main_id,card_sub_id,once_charge_money,"
			+ "once_charge_count,add_begin_day,add_valid_day,exit_timeout_count,"
			+ "exit_timeout_money,entry_timeout_count,entry_timeout_money,"
			+ "package_id,field2,field3 from "+FrameDBConstant.COM_ST_P+"op_prm_card_tct_attr where version_no=? and record_flag=?";

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
					fields[0] = dbHelper.getItemValue("card_main_id");
					fields[1] = dbHelper.getItemValue("card_sub_id");
					fields[2] = dbHelper.getItemValue("once_charge_money");
					fields[3] = dbHelper.getItemValue("once_charge_count");
					fields[4] = dbHelper.getItemValue("add_begin_day");
					fields[5] = dbHelper.getItemValue("add_valid_day");
					fields[6] = dbHelper.getItemValue("exit_timeout_count");
					fields[7] = dbHelper.getItemValue("exit_timeout_money");
					fields[8] = dbHelper.getItemValue("entry_timeout_count");
					fields[9] = dbHelper.getItemValue("entry_timeout_money");
					fields[10] = dbHelper.getItemValue("package_id");
					fields[11] = dbHelper.getItemValue("field2");
					fields[12] = dbHelper.getItemValue("field3");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_card_tct_attr error! " + e);
			return null;
		}
		return recV;
	}
}
