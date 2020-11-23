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
 * 票卡类型
 * 
 * @author zhangjh
 */
public class Parameter0304Dao {

	private static Logger logger = Logger.getLogger(Parameter0304Dao.class
			.getName());
	private static String sqlStr = "select card_main_id,card_sub_id,card_sub_name,card_sub_name_en,card_sub_name_uey, card_original_id from "
                +FrameDBConstant.COM_ST_P+"op_prm_card_sub where version_no=? and record_flag=?";

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
					fields[2] = dbHelper.getItemValue("card_sub_name");
					fields[3] = dbHelper.getItemValue("card_sub_name_en");
                                        fields[4] = dbHelper.getItemValue("card_sub_name_uey");
                                        fields[5] = dbHelper.getItemValue("card_original_id");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_card_sub error! " + e);
			return null;
		}
		return recV;
	}
}
