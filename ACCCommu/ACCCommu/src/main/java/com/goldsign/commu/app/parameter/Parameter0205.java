package com.goldsign.commu.app.parameter;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.Parameter0205Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;

/**
 * 线路表
 * 
 * @author zhangjh
 */
public class Parameter0205 extends ParameterBase {

	private static final String PARMTYPE = "0205";
	private static final int[] FORMAT = { 2, 50 };
	private static Logger logger = Logger.getLogger(Parameter0205.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		// logger.info("参数0101 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0205Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0101 error! " + e);
			result = false;
		}
		logger.info("参数0101 ended! ");
		return result;
	}
}
