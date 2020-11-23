/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import org.apache.log4j.Logger;

import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author hejj
 */
public class SquadTimeDao {

	private static Logger logger = Logger.getLogger(SquadTimeDao.class
			.getName());

	public SquadTimeDao() {
		super();
	}

	private String getSquadTimeForApp(String squadTime) {
		String hour = squadTime.substring(0, 2);
		String min = squadTime.substring(2);
		int iMin = Integer.parseInt(min) + 5;
		if (iMin < 10) {
			min = "0" + iMin;
		} else {
			min = "" + iMin;
		}
		return hour + min;
	}

	public String getSquadTime() throws Exception {
		boolean result = false;
		DbHelper dbHelper = null;
		String squadTime = "";
//		Object[] values = { "sys.squadday" };

		try {
			dbHelper = new DbHelper("SquadTimeDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			String sqlStr = "select cfg_value from "+FrameDBConstant.COM_ST_P+"st_cfg_sys_base where record_flag='0' and cfg_key='sys.squadday'";

			result = dbHelper.getFirstDocument(sqlStr);
			if (!result) {
				throw new Exception("获取运营时间失败，记录不存在");
			}

			squadTime = dbHelper.getItemValue("cfg_value");

			squadTime = this.getSquadTimeForApp(squadTime);

		} catch (Exception e) {
			PubUtil.handleException(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		return squadTime;
	}
}
