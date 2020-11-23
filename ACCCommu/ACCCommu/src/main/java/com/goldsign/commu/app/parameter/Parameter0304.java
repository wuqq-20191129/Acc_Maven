package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0304Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 票卡类型
 * 
 * @author zhangjh
 */
public class Parameter0304 extends ParameterBase {

	private static final String PARMTYPE = "0304";
	private static final int[] FORMAT = { 2, 2, 30, 60, 100, 4};
	private static Logger logger = Logger.getLogger(Parameter0304.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("参数0304 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0304Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0304 error! " + e);
			result = false;
		}
		logger.info("参数0304 ended! ");
		return result;
	}
}
