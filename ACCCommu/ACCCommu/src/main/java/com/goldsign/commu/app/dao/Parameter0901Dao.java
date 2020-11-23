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
 * 
 * @author zhangjh
 */
public class Parameter0901Dao {

	private static Logger logger = Logger.getLogger(Parameter0901Dao.class
			.getName());
	private static String sqlStr = "select start_station,dest_station,stopsell_time1,stopsell_time2,stopsell_endtime "
                + "from "+FrameDBConstant.COM_ST_P+"op_prm_tvm_stopsell_detail_d where version_no=? and record_flag=? and parm_type_id=?";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;

		List<String> pStmtValues = new ArrayList<String>();
		pStmtValues.clear();
		pStmtValues.add(paraGenDtl.getVerNum());
		pStmtValues.add(paraGenDtl.getVerType());
		pStmtValues.add(paraGenDtl.getParmTypeId());
		try {
			result = dbHelper.getFirstDocument(sqlStr, pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("start_station");
					fields[1] = dbHelper.getItemValue("dest_station");
					fields[2] = dbHelper.getItemValue("stopsell_time1");
					fields[3] = dbHelper.getItemValue("stopsell_time2");
					fields[4] = dbHelper.getItemValue("stopsell_endtime");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_tvm_stopsell_detail_d error! " + e);
			return null;
		}
		return recV;
	}
}
