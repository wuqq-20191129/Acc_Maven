package com.goldsign.commu.app.parameter;

import java.util.Vector;
import org.apache.log4j.Logger;
import com.goldsign.commu.app.dao.Parameter8101Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;

/**
 * 充值终端通讯参数
 * 
 * @author zhangjh
 * 
 */
public class Parameter8101 extends ParameterBase {
	private static final String PARMTYPE = "8101";
	private static final int[] FORMAT = { 15, 4, 1 };
	private static Logger logger = Logger.getLogger(Parameter8101.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("参数8101 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter8101Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数8101 error! " + e);
			result = false;
		}
		logger.info("参数8101 ended! ");
		return result;
	}

}
