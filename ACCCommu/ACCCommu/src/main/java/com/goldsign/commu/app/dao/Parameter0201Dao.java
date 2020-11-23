package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameParameterConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 车站表
 * 
 * @author zhangjh
 */
public class Parameter0201Dao {

	private static Logger logger = Logger.getLogger(Parameter0201Dao.class
			.getName());
	private static String sqlStr = "select line_id,station_id,chinese_name,english_name,uygur_name,sc_ip,lc_ip from "
                +FrameDBConstant.COM_ST_P+"op_prm_station where version_no=? and record_flag=? order by line_id,station_id asc";

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
                                String chineseName = "";
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("line_id")
							+ dbHelper.getItemValue("station_id");
                                        chineseName = dbHelper.getItemValue("chinese_name");
					fields[1] = chineseName;
					fields[2] = dbHelper.getItemValue("english_name");
                                        fields[3] = dbHelper.getItemValue("uygur_name");
					fields[4] = dbHelper.getItemValue("sc_ip");
					fields[5] = dbHelper.getItemValue("lc_ip");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_station error! " + e);
			return null;
		}
		return recV;
	}
}
