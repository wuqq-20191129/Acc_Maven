package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import java.sql.SQLException;

import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author zhangjh
 */
public class Message03Dao {

	private static String insertSqlStr = "insert into "+FrameDBConstant.COM_ST_P+"op_prm_para_sts_dl(insert_time,status_datetime,line_id,station_id,parm_type_id,ver_num,download_status)"
			+ " values(sysdate,?,?,?,?,?,?)";

	public static void insert(DbHelper dbHelper, Object[] values)
			throws SQLException {
		dbHelper.executeUpdate(insertSqlStr, values);
	}

}
