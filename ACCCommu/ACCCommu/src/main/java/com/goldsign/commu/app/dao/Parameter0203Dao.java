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
 * SAM卡对照表
 * 
 * @author zhangjh
 */
public class Parameter0203Dao {

	private static Logger logger = Logger.getLogger(Parameter0203Dao.class
			.getName());
	private static String sqlStr = "select sam_logical_id,sam_type_id,line_id,station_id,device_id,dev_type_id from "
                +FrameDBConstant.COM_ST_P+ "op_prm_sam_list where version_no=? and record_flag=? order by sam_logical_id";

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
					fields[0] = dbHelper.getItemValue("sam_logical_id");
					fields[1] = dbHelper.getItemValue("sam_type_id");
					fields[2] = dbHelper.getItemValue("line_id")
							+ dbHelper.getItemValue("station_id");
					fields[3] = dbHelper.getItemValue("device_id");
					fields[4] = dbHelper.getItemValue("dev_type_id");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_sam_list error! " + e);
			return null;
		}
		return recV;
	}
}
