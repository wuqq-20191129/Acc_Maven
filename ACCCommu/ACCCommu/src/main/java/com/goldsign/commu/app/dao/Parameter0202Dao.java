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
 * 车站配置表
 * 
 * @author zhangjh
 */
public class Parameter0202Dao {

	private static Logger logger = Logger.getLogger(Parameter0202Dao.class
			.getName());
        //添加设置类型表（op_cod_dev_type）关联查询
	private static String sqlStr = "select line_id,station_id,device_id,dev_type_id,csc_num,array_id,concourse_id,ip_address,dev_name from "
                +FrameDBConstant.COM_ST_P+ "op_prm_dev_code "
                + "where version_no=? and record_flag=? and dev_type_id in (select dev_type_id from "+FrameDBConstant.COM_ST_P+"op_prm_dev_type where record_flag='0') "
                + "order by  line_id asc,station_id asc";

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
					fields[1] = dbHelper.getItemValue("device_id");
					fields[2] = dbHelper.getItemValue("dev_type_id");
					fields[3] = dbHelper.getItemValue("csc_num");
					fields[4] = dbHelper.getItemValue("array_id");
					fields[5] = dbHelper.getItemValue("concourse_id");
					fields[6] = dbHelper.getItemValue("ip_address");
					fields[7] = dbHelper.getItemValue("dev_name");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_dev_code error! " + e);
			return null;
		}
		return recV;
	}
}
