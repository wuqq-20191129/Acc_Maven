/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;
import org.apache.log4j.Logger;

/**
 * 查询基本配置
 * @author lindaquan
 */
public class ConfigBaseDao {

	private static Logger logger = Logger.getLogger(ConfigBaseDao.class
			.getName());

	public ConfigBaseDao() {
		super();
	}

	public String getConfigValue(String configKey) throws Exception {
		boolean result = false;
		DbHelper dbHelper = null;
		String configValue = "0";
		Object[] values = {configKey};

		try {
			dbHelper = new DbHelper("ConfigBaseDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			String sqlStr = "select cfg_value from "+FrameDBConstant.COM_ST_P+"st_cfg_sys_base where record_flag='0' and cfg_key=?";

			result = dbHelper.getFirstDocument(sqlStr, values);
			if (!result) {
                            throw new Exception("获取w_st_cfg_sys_base配置失败，记录不存在");
			}else{
                            configValue = dbHelper.getItemValue("cfg_value");
                        }

		} catch (Exception e) {
			PubUtil.handleException(e, logger);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		return configValue;
	}
        
        /*
         * 查询CM_CFG_SYS表配置
         * add by lindaquan in 20160427
         */
        public String getCmCfgSysValue(String configKey) throws Exception {
		boolean result = false;
		DbHelper dbHelper = null;
		String configValue = "0";
		Object[] values = {configKey};

		try {
			dbHelper = new DbHelper("ConfigBaseDao",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			String sqlStr = "select config_value from "+FrameDBConstant.COM_COMMU_P+"CM_CFG_SYS where config_name=?";

			result = dbHelper.getFirstDocument(sqlStr, values);
			if (!result) {
                            throw new Exception("获取"+configKey+"控制失败，记录不存在");
			}else{
                            configValue = dbHelper.getItemValue("config_value");
                        }

		} catch (Exception e) {
			PubUtil.handleException(e, logger);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		return configValue;
	}
}
