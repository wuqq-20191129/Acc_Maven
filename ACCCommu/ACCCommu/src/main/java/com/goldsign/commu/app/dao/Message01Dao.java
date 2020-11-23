package com.goldsign.commu.app.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author zhangjh
 */
public class Message01Dao {

	// private static Logger logger = Logger.getLogger(Message01Dao.class);

	public static Map<String, String> queryAccVer(DbHelper dataDbHelper, String lineId)
			throws SQLException {
                //磁悬浮接入，条件添加line_id字段 20151217 by lindaquan
		String sqlStr = "select parm_type_id,version_no,version_type from "+FrameDBConstant.COM_ST_P+"op_prm_para_ver_cm where  parm_type_id in("
				+ FrameCodeConstant.parmSynchType
				+ ") and line_id= '"+lineId+"' order by parm_type_id,version_type";
		boolean result = dataDbHelper.getFirstDocument(sqlStr);
		Map<String, String> accVer = new HashMap<String, String>();
		while (result) {
			String parmTypeId = dataDbHelper.getItemValue("parm_type_id");
			String verNum = dataDbHelper.getItemValue("version_no");
			String verType = dataDbHelper.getItemValue("version_type");
			// 黑名单只有当前版本？
			if (parmTypeId.substring(0, 2).equals("06")) {
				accVer.put(parmTypeId + "0", verNum);
			} else {
				accVer.put(parmTypeId + verType, verNum);
			}
			result = dataDbHelper.getNextDocument();
		}
		return accVer;
	}
}
