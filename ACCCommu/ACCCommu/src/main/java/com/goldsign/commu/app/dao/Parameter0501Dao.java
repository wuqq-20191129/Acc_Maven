package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 降级模式使用记录
 * 
 * @author zhangjh
 */
public class Parameter0501Dao {

	private static Logger logger = Logger.getLogger(Parameter0501Dao.class
			.getName());
	private static String sqlStr = "select line_id,station_id,degrade_mode_id,start_time,end_time,set_oper_id,cancel_oper_id from "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd "
			+ "where hdl_flag<>'0' and version_no<>'0000000000' order by water_no";

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = new Vector<String[]>(); // degrade mode history
														// can be empty
		boolean result;
		// String sqlStr =
		// "select line_id,station_id,degrade_mode_id,start_time,end_time,SET_OPER_ID,CANCEL_OPER_ID from op_prm_degrade_mode_recd where ver_num=?";

		// Object[] values = {paraGenDtl.getVerNum()};
		try {
			// result = dbHelper.getFirstDocument(sqlStr,values);
			result = dbHelper.getFirstDocument(sqlStr);
			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("line_id")
							+ dbHelper.getItemValue("station_id");
					fields[1] = dbHelper.getItemValue("degrade_mode_id");
					fields[2] = dbHelper.getItemValue("start_time");
					fields[3] = dbHelper.getItemValue("end_time");
					fields[4] = dbHelper.getItemValue("set_oper_id");
					fields[5] = dbHelper.getItemValue("cancel_oper_id");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_degrade_mode_recd error! " + e);
			return null;
		}
		return recV;
	}
        
        public static int updateDegradeModeRecd(DbHelper dbHelper){
            int n = 0;
            try {
                String sql = "update "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd set hdl_flag='1' where version_no<>'0000000000' and hdl_flag='2'";
                n = dbHelper.executeUpdate(sql);
            } catch (Exception e) {
                logger.error("更新"+FrameDBConstant.TABLE_PREFIX+"op_prm_degrade_mode_recd error! " + e);
            }
            return n;
        }
}
