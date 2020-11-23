package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0801Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 行政收费罚金参数
 * 
 * @author zhangjh
 */
public class Parameter0801 extends ParameterBase {

	private static final String PARMTYPE = "0801";
	private static final int[] FORMAT = { 2, 4, 4, 4 };
	private static Logger logger = Logger.getLogger(Parameter0801.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("参数0801 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0801Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0801 error! " + e);
			result = false;
		}
		logger.info("参数0801 ended! ");
		return result;
	}
}
