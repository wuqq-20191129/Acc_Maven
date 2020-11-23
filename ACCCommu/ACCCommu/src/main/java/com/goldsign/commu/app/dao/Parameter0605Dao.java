package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 交通部一卡通黑名单
 * @author lind
 */
public class Parameter0605Dao {

	private static Logger logger = Logger.getLogger(Parameter0605Dao.class
			.getName());
	
	private static String sqlStr1 = "select card_logical_id,action_type "
			+ " from "+FrameDBConstant.COM_ST_P+"OP_PRM_BLACK_LIST_MOC "
			//+ " where create_datetime <= sysdate"
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
                                if(!Parameter06Util.matchs(fields[0], Parameter06Util.patternReg)){
                                    String[] fieldx = new String[3];
                                    fieldx[0] = fields[0];
                                    fieldx[1] = fields[1];
                                    fieldx[2] = "含非法字符";
                                    errV.add(fieldx);
                                } else if(fields[0].length()<16 || fields[0].length()>19){
                                    String[] fieldx = new String[3];
                                    fieldx[0] = fields[0];
                                    fieldx[1] = fields[1];
                                    fieldx[2] = "长度不是16~19";
                                    errV.add(fieldx);
                                }else{
                                    recV.add(fields);
                                }
				result = dbHelper.getNextDocument();
			}
		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"OP_PRM_BLACK_LIST_MOC error! " + e);
			return null;
		}
		// 黑名单数量限定
		recV = Parameter06Util.getRecordNotOverLimit(recV, Parameter06Util.blacklistMocMax);
                Parameter06Util.inErrBlackDown(errV,"3");
		return recV;
	}

}
