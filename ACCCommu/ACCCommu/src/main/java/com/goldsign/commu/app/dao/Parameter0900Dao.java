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
 * TVM停售时间配置表
 * 
 * @author zhangjh
 */
public class Parameter0900Dao {

	private static Logger logger = Logger.getLogger(Parameter0900Dao.class
			.getName());
	private static String sqlStr = "select begin_date,end_date,timetable_id,param_type_id from "+FrameDBConstant.COM_ST_P+"op_prm_tvm_stopsell_config where version_no=? and record_flag=?";

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
					fields[0] = dbHelper.getItemValue("begin_date");
					fields[1] = dbHelper.getItemValue("end_date");
					fields[2] = dbHelper.getItemValue("timetable_id");
                                        fields[3] = dbHelper.getItemValue("param_type_id");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_tvm_stopsell_config error! " + e);
			return null;
		}
		return recV;
	}
}
