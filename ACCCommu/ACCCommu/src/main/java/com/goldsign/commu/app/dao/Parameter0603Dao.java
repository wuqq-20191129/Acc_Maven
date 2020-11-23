package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 一卡通黑名单
 * 增加字符过滤，修改数量限制 20150803 lindaquan
 * @author zhangjh
 */
public class Parameter0603Dao {

	private static Logger logger = Logger.getLogger(Parameter0603Dao.class
			.getName());
	
	private static String sqlStr1 = "select card_logical_id,action_type "
			+ "from "+FrameDBConstant.COM_ST_P+"op_prm_black_list_oct "
			//+ "where gen_datetime <= sysdate "
                        + " order by card_logical_id asc";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = new Vector<String[]>(); // black list can be
                Vector<String[]> errV = new Vector<String[]>(); // 非法黑名单
		boolean result;
                
                // 一卡通黑名单限定从配置中读取
		Parameter06Util.getLimit();

		try {
			result = dbHelper.getFirstDocument(sqlStr1);
			while (result) {
				String[] fields = new String[formtLen];
				fields[0] = dbHelper.getItemValue("card_logical_id");
                                fields[1] = dbHelper.getItemValue("action_type");
                                if(Parameter06Util.matchMtr(fields[0])){
                                    String[] fieldx = new String[3];
                                    fieldx[0] = fields[0];
                                    fieldx[1] = fields[1];
                                    fieldx[2] = "地铁卡";
                                    errV.add(fieldx);
                                }else if(Parameter06Util.patternMacher(fields[0])){
                                    recV.add(fields);
                                } else{
                                    String[] fieldx = new String[3];
                                    fieldx[0] = fields[0];
                                    fieldx[1] = fields[1];
                                    fieldx[2] = "含非法字符";
                                    errV.add(fieldx);
                                }
				result = dbHelper.getNextDocument();
			}
		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_black_list_oct error! " + e);
			return null;
		}
		// 黑名单数量限定
		recV = Parameter06Util.getRecordNotOverLimit(recV, Parameter06Util.blacklistOctMax);
                Parameter06Util.inErrBlackDown(errV,"2");
		return recV;
	}

}
