package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0603Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 一卡通黑名单
 * 
 * @author zhangjh
 */
public class Parameter0603 extends ParameterBase {

	private static final String PARMTYPE = "0603";
	private static final int[] FORMAT = { 20, 3 };
	private static Logger logger = Logger.getLogger(Parameter0603.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("参数0603 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0603Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0603 error! " + e);
			result = false;
		}
		logger.info("参数0603 ended! ");
		return result;
	}
}
