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
 * 线路表
 * @author zhangjh
 */
public class Parameter0205Dao {

	private static Logger logger = Logger.getLogger(Parameter0205Dao.class
			.getName());
	private static String sqlStr = "select line_id,line_name from "+FrameDBConstant.COM_ST_P+"op_prm_line where version_no=? and record_flag=? and line_flag=?";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;
		List<String> pStmtValues = new ArrayList<String>();
		pStmtValues.clear();
		pStmtValues.add(paraGenDtl.getVerNum());
		pStmtValues.add(paraGenDtl.getVerType());
                pStmtValues.add("0");//实体线路
		try {
			result = dbHelper.getFirstDocument(sqlStr, pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("line_id");
					fields[1] = dbHelper.getItemValue("line_name");

					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_line error! " + e);
			return null;
		}
		return recV;
	}
}
