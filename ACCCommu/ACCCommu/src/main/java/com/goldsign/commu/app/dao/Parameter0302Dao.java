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
 * 闸机专用通道参数
 * 
 * @author zhangjh
 */
public class Parameter0302Dao {

	private static Logger logger = Logger.getLogger(Parameter0302Dao.class
			.getName());
	private static String sqlStr = "select card_main_id,card_sub_id,discount,sound "
			+ "from "+FrameDBConstant.COM_ST_P+"op_prm_gate_voice_promp "
			+ "where  version_no=? and record_flag=? ";

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
					fields[0] = dbHelper.getItemValue("card_main_id")
							+ dbHelper.getItemValue("card_sub_id");
					fields[1] = dbHelper.getItemValue("discount");
					fields[2] = dbHelper.getItemValue("sound");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("取闸机语音提示参数数据错误:" + e);
			return null;
		}
		return recV;
	}
}
