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
 * 换乘车站代码表
 * 
 * @author zhangjh
 */
public class Parameter0204Dao {

	private static Logger logger = Logger.getLogger(Parameter0204Dao.class
			.getName());
	private static String sqlStr = "select line_id,station_id,transfer_line_id,transfer_station_id from "
                +FrameDBConstant.COM_ST_P+ "op_prm_transfer_station where version_no=? and record_flag=?";

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
					fields[0] = dbHelper.getItemValue("line_id")
							+ dbHelper.getItemValue("station_id");
					fields[1] = dbHelper.getItemValue("transfer_line_id")
							+ dbHelper.getItemValue("transfer_station_id");

					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_transfer_station error! " + e);
			return null;
		}
		return recV;
	}
}
