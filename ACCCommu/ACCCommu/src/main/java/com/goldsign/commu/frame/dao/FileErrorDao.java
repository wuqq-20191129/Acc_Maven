/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameFileHandledConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.FileErrorVo;
import com.goldsign.lib.db.util.DbHelper;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 
 * @author zhangjh
 */
public class FileErrorDao {

	private static Logger logger = Logger.getLogger(FileErrorDao.class
			.getName());

	public int insert(FileErrorVo vo) throws Exception {
		String sql = "insert into "+FrameDBConstant.COM_ST_P+"st_err_data_file(balance_water_no,file_name,err_code,hdl_flag,gen_time,remark) "
				+ "values(?,?,?,?,sysdate,?) ";
		DbHelper dbHelper = null;
		int result = 0;
		Object[] values = { vo.getBalanceWaterNo(), vo.getFileName(),
				vo.getErrorCode(), FrameFileHandledConstant.FILE_HDL_NO,
				vo.getRemark() };
		try {
			dbHelper = new DbHelper("",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			result = dbHelper.executeUpdate(sql, values);

		} catch (Exception e) {
			PubUtil.handleException(e, logger);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return result;

	}

	private void addRecord(Hashtable<String, Vector> ht, String fileName,
			String errorCode) {
		String line = fileName.substring(3, 5);
		if (!ht.containsKey(line)) {
			ht.put(line, new Vector());
		}
		Vector v = ht.get(line);
		String[] record = new String[2];
		record[0] = fileName;
		record[1] = errorCode;
		v.add(record);
	}

	public Hashtable<String, Vector> getRecordsForAudit(String balanceWaterNo)
			throws Exception {

		String sql = "select a.file_name,b.err_code_lcc from "+FrameDBConstant.COM_ST_P+"st_err_data_file a,"+FrameDBConstant.COM_ST_P+"st_cfg_err_code_mapping b  "
				+ "where a.balance_water_no=? and a.err_code (+)= b.err_code_acc "
				+ " order by gen_time";
		DbHelper dbHelper = null;
		boolean result = false;
		String fileName, errorCode;
		Hashtable<String, Vector> files = new Hashtable();

		Object[] values = { balanceWaterNo };
		try {
			dbHelper = new DbHelper("",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			result = dbHelper.getFirstDocument(sql, values);
			while (result) {
				fileName = dbHelper.getItemValue("FILE_NAME");
				errorCode = dbHelper.getItemValue("err_code_lcc");
				this.addRecord(files, fileName, errorCode);

				result = dbHelper.getNextDocument();
			}

		} catch (Exception e) {
			PubUtil.handleException(e, logger);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return files;

	}
}
