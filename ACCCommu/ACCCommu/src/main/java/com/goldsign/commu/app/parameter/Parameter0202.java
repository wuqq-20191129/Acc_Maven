package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0202Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 车站配置表
 * 
 * @author zhangjh
 */
public class Parameter0202 extends ParameterBase {

	private static final String PARMTYPE = "0202";
	private static final int[] FORMAT = { 4, 3, 2, 1, 3, 3, 15,30 };
	private static Logger logger = Logger.getLogger(Parameter0202.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
                logger.info("参数0202 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0202Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0202 error! " + e);
			result = false;
		}
		logger.info("参数0202 ended! ");
		return result;
	}
}
