package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0203Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * SAM卡对照表
 * 
 * @author zhangjh
 */
public class Parameter0203 extends ParameterBase {

	private static final String PARMTYPE = "0203";
	private static final int[] FORMAT = { 16, 1, 4, 3, 2 };
	private static Logger logger = Logger.getLogger(Parameter0203.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
                logger.info("参数0203 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0203Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0203 error! " + e);
			result = false;
		}
		logger.info("参数0203 ended! ");
		return result;
	}
}
