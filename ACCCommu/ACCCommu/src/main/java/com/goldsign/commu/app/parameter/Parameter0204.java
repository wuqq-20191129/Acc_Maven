package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0204Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 换乘车站代码表
 * 
 * @author zhangjh
 */
public class Parameter0204 extends ParameterBase {

	private static final String PARMTYPE = "0204";
	private static final int[] FORMAT = { 4, 4 };
	private static Logger logger = Logger.getLogger(Parameter0204.class
			.getName());

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean formParaFile() {
		boolean result;
		// logger.info("参数0204 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0204Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0204 error! " + e);
			result = false;
		}
		logger.info("参数0204 ended! ");
		return result;
	}
}
