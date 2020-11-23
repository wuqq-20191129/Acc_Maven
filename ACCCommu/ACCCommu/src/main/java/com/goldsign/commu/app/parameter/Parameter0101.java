package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0101Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 设备控制参数
 * 
 * @author zhangjh
 */
public class Parameter0101 extends ParameterBase {

	private static final String PARMTYPE = "0101";
	private static final int[] FORMAT = { 4, 4, 4, 4, 2, 8, 8, 8, 8, 8, 8 };
	private static Logger logger = Logger.getLogger(Parameter0101.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("参数0101 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0101Dao.getRecordFromTable(
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
