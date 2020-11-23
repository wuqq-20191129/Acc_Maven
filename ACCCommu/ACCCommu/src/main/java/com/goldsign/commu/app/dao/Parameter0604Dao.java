package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 一卡通黑名单段
 * 增加字符过滤，修改数量限制 20150803 lindaquan
 * @author zhangjh
 */
public class Parameter0604Dao {

	private static Logger logger = Logger.getLogger(Parameter0604Dao.class
			.getName());
	private static String sqlStr = "select begin_logical_id,end_logical_id,action_type "
			+ " from "+FrameDBConstant.COM_ST_P+"op_prm_black_list_oct_sec "
			//+ "where gen_datetime <= sysdate "
			+ " order by begin_logical_id,end_logical_id";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = new Vector<String[]>(); // black list can be
                Vector<String[]> errV = new Vector<String[]>(); // 含非法字符黑名单
														// empty
		boolean result;

		try {
			result = dbHelper.getFirstDocument(sqlStr);
			while (result) {
				String[] fields = new String[formtLen];
				fields[0] = dbHelper.getItemValue("begin_logical_id");
				fields[1] = dbHelper.getItemValue("end_logical_id");
				fields[2] = dbHelper.getItemValue("action_type");
                                if(Parameter06Util.matchMtr(fields[0]) || Parameter06Util.matchMtr(fields[1])){
                                    String[] fieldx = new String[4];
                                    fieldx[0] = fields[0];
                                    fieldx[1] = fields[1];
                                    fieldx[2] = fields[2];
                                    fieldx[3] = "地铁卡";
                                    errV.add(fieldx);
                                } else if(Parameter06Util.patternMacher(fields[0]) && Parameter06Util.patternMacher(fields[1])){
                                    recV.add(fields);
                                } else {
                                    String[] fieldx = new String[4];
                                    fieldx[0] = fields[0];
                                    fieldx[1] = fields[1];
                                    fieldx[2] = fields[2];
                                    fieldx[3] = "含非法字符";
                                    errV.add(fieldx);
                                }
				result = dbHelper.getNextDocument();
			}
		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_black_list_oct_sec error! " + e);
			return null;
		}
                // 黑名单段数量限定
		recV = Parameter06Util.getRecordNotOverLimit(recV, Parameter06Util.blacklistSecMax);
                Parameter06Util.inErrBlackDownSEC(errV,"2");
                
		return recV;
	}
}
