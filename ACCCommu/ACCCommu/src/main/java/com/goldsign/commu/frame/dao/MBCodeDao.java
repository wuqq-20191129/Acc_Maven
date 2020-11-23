/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
 * 手机平台IP查询
 * @author lindaquan
 */
public class MBCodeDao {
        private static Logger logger = Logger.getLogger(MBCodeDao.class.getName());

	public MBCodeDao() throws Exception {
		super();
	}
        
        public Hashtable<String, String> getMobileIp() {
		boolean result = false;
		Hashtable<String, String> mobileIp = new Hashtable<String, String>();
		DbHelper dbHelper = null;
		String sqlStr = "select lcc_line_id, lcc_line_name, lcc_ip from "+FrameDBConstant.COM_ST_P+"st_mb_cod_lcc_line";
		try {
			dbHelper = new DbHelper("MBCodeDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			result = dbHelper.getFirstDocument(sqlStr);
			while (result) {
				mobileIp.put((String) dbHelper.getItemValue("lcc_line_id"),
						(String) dbHelper.getItemValue("lcc_ip"));
				result = dbHelper.getNextDocument();
			}
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return mobileIp;
	}
        
}
