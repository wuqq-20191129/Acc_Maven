package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 地铁黑名单
 * 增加字符过滤，修改数量限制 20150803 lindaquan
 * @author zhangjh
 */
public class Parameter0601Dao {

	private static Logger logger = Logger.getLogger(Parameter0601Dao.class
			.getName());
	private static String sqlStr = "select card_logical_id,action_type from "+FrameDBConstant.COM_ST_P+"op_prm_black_list_mtr "
			//+ " where gen_datetime<=sysdate "
			+ " order by card_logical_id asc";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = new Vector<String[]>(); // black list can be
                Vector<String[]> errV = new Vector<String[]>(); // 含非法字符黑名单
														// empty
		boolean result;
                // 一卡通黑名单限定从配置中读取
		Parameter06Util.getLimit();

		try {
			result = dbHelper.getFirstDocument(sqlStr);
			while (result) {
				String[] fields = new String[formtLen];
				fields[0] = dbHelper.getItemValue("card_logical_id");
				fields[1] = dbHelper.getItemValue("action_type");
				if(Parameter06Util.patternMacher(fields[0])){
                                    recV.add(fields);
                                } else {
                                    String[] fieldx = new String[3];
                                    fieldx[0] = fields[0];
                                    fieldx[1] = fields[1];
                                    fieldx[2] = "含非法字符";
                                    errV.add(fieldx);
                                }
				result = dbHelper.getNextDocument();
			}
		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_black_list_mtr error! " + e);
			return null;
		}
                
                // 黑名单数量限定
		recV = Parameter06Util.getRecordNotOverLimit(recV, Parameter06Util.blacklistMetroMax);
                Parameter06Util.inErrBlackDown(errV,"1");
                
		return recV;
	}
}
