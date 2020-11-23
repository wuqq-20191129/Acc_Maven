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
 * 设备控制参数
 * 
 * @author zhangjh
 */
public class Parameter0101Dao {

	private static Logger logger = Logger.getLogger(Parameter0101Dao.class
			.getName());
	private static String sql = "select logoff_idle_sc,interval_connectsc,logoff_idle_bom,time_out_pass,degrade_time,upper_amount,upper_count,upper_sjt,under_amount,under_count,under_sjt from "
                +FrameDBConstant.COM_ST_P+ "op_prm_dev_contrl where version_no=? and record_flag=?";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;
		List<String> pStmtValues = new ArrayList<String>();
		pStmtValues.clear();
		pStmtValues.add(paraGenDtl.getVerNum());
		pStmtValues.add(paraGenDtl.getVerType());
		try {
			result = dbHelper.getFirstDocument(sql, pStmtValues.toArray());
			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("logoff_idle_sc");
					fields[1] = dbHelper.getItemValue("interval_connectsc");
					fields[2] = dbHelper.getItemValue("logoff_idle_bom");
					fields[3] = dbHelper.getItemValue("time_out_pass");
					fields[4] = dbHelper.getItemValue("degrade_time");
					fields[5] = dbHelper.getItemValue("upper_amount");
					fields[6] = dbHelper.getItemValue("upper_count");
					fields[7] = dbHelper.getItemValue("upper_sjt");
					fields[8] = dbHelper.getItemValue("under_amount");
					fields[9] = dbHelper.getItemValue("under_count");
					fields[10] = dbHelper.getItemValue("under_sjt");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}
		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"dev_contrl_prm error! " + e);
			return null;
		}
		return recV;
	}
}
