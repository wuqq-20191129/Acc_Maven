package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0901Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * 节假日时间表1
 * 
 * @author zhangjh
 */
public class Parameter0903 extends ParameterBase {

	private static final String PARMTYPE = "0903";
	private static final int[] FORMAT = { 4, 4, 4, 4, 4 };
	private static Logger logger = Logger.getLogger(Parameter0903.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("Paramerer0903 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0901Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("Paramerer0903 error! " + e);
			result = false;
		}
		logger.info("Paramerer0903 ended! ");
		return result;
	}
}
