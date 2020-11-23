package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;
import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class LccLineCodeDao {

	private static Logger logger = Logger.getLogger(LccLineCodeDao.class
			.getName());

	public LccLineCodeDao() throws Exception {
		super();
	}

	public Hashtable<String, String> getAllLccDetail() {
		boolean result = false;
		Hashtable<String, String> allLccIp = new Hashtable<String, String>();
		DbHelper dbHelper = null;
		String sqlStr = "select line_id,lc_ip,line_name from "+FrameDBConstant.COM_ST_P+"op_prm_line where record_flag='0'";
		try {
			dbHelper = new DbHelper("LccLineCodeDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			result = dbHelper.getFirstDocument(sqlStr);
			while (result) {
				allLccIp.put(dbHelper.getItemValue("line_id"),
						dbHelper.getItemValue("lc_ip"));
                                FrameCodeConstant.ALL_LCC_IP_NAME.put(
						(String) dbHelper.getItemValue("lc_ip"),
						(String) dbHelper.getItemValue("line_name"));
				result = dbHelper.getNextDocument();
			}
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return allLccIp;
	}
        
        
        public Hashtable<String, String> getAllLccIpCod() {
		boolean result = false;
		Hashtable<String, String> allLccIp = new Hashtable<String, String>();
		DbHelper dbHelper = null;
		String sqlStr = "select lcc_line_id,lcc_line_name,lcc_ip from "+FrameDBConstant.COM_ST_P+"st_cod_lcc_line";
		try {
			dbHelper = new DbHelper("LccLineCodeDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			result = dbHelper.getFirstDocument(sqlStr);
			while (result) {
				allLccIp.put((String) dbHelper.getItemValue("lcc_line_id"),
						(String) dbHelper.getItemValue("lcc_ip"));
				result = dbHelper.getNextDocument();
			}
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return allLccIp;
	}
}
