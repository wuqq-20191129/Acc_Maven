package com.goldsign.commu.frame.dao;

import java.util.Date;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author hejj
 */
public class DeviceDao {

	private static Logger logger = Logger.getLogger(DeviceDao.class.getName());

	public DeviceDao() throws Exception {
		super();
	}

	public Hashtable<String, String> getAllBomIp() {
		// logger.info(DateHelper.datetimeToString(new
		// Date())+" 取得bom信息from w_op_prm_dev_code" );
		boolean result = false;
		Hashtable<String, String> allBomIp = new Hashtable<String, String>();
		DbHelper dbHelper = null;
		String sqlStr = "select line_id||station_id||dev_type_id||device_id bom,ip_address from "+FrameDBConstant.COM_ST_P+"op_prm_dev_code where dev_type_id='03' and record_flag='0'";
		try {
			dbHelper = new DbHelper("DeviceDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			result = dbHelper.getFirstDocument(sqlStr);
			while (result) {
				allBomIp.put(dbHelper.getItemValue("bom"),
						dbHelper.getItemValue("ip_address"));
				result = dbHelper.getNextDocument();
			}
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return allBomIp;
	}

	public Hashtable<String, String> getStatusMapping() {
		logger.debug(DateHelper.datetimeToString(new Date())
				+ " 取得信息from w_op_cod_com_status_mapping");
		boolean result = false;
		Hashtable<String, String> mapping = new Hashtable<String, String>();
		String sqlStr = "select status_id,status_value,acc_status_value from "+FrameDBConstant.COM_ST_P+"op_cod_com_status_mapping ";
		DbHelper dbHelper = null;
		try {
			dbHelper = new DbHelper("DeviceDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			result = dbHelper.getFirstDocument(sqlStr);
			while (result) {
				mapping.put(
						dbHelper.getItemValue("status_id")
								+ dbHelper.getItemValue("status_value"),
						dbHelper.getItemValue("acc_status_value"));
				result = dbHelper.getNextDocument();
			}
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return mapping;
	}
}
