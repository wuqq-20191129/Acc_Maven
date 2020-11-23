package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 交通部一卡通白名单
 * @author lind
 */
public class Parameter0606Dao {

	private static Logger logger = Logger.getLogger(Parameter0606Dao.class
			.getName());
	
	private static String sqlStr1 = "select iss_identify_id,iss_identify_num from "
                +FrameDBConstant.COM_ST_P+"op_prm_white_list_moc order by iss_identify_id,iss_identify_num asc";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = new Vector<String[]>(); 
		boolean result;
                
		try {
			result = dbHelper.getFirstDocument(sqlStr1);
			while (result) {
				String[] fields = new String[formtLen];
				fields[0] = dbHelper.getItemValue("iss_identify_id");
                                fields[1] = dbHelper.getItemValue("iss_identify_num");
                                recV.add(fields);
				result = dbHelper.getNextDocument();
			}
		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_white_list_moc error! " + e);
			return null;
		}
		return recV;
	}

}
