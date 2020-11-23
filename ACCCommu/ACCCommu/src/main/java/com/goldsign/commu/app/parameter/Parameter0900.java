package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0900Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * TVM 停售时间 配置表
 * 
 * @author zhangjh
 */
public class Parameter0900 extends ParameterBase {

	private static final String PARMTYPE = "0900";
	private static final int[] FORMAT = { 12, 12, 2, 4 };
	private static Logger logger = Logger.getLogger(Parameter0900.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
                logger.info("Paramerer0900 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0900Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("Paramerer0900 error! " + e);
			result = false;
		}
		logger.info("Paramerer0900 ended! ");
		return result;
	}
}
