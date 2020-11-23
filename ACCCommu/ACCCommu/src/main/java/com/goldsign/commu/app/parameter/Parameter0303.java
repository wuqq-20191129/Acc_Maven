package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0303Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 乘次票专用参数
 * 
 * @author zhangjh
 */
public class Parameter0303 extends ParameterBase {

	private static final String PARMTYPE = "0303";
	private static final int[] FORMAT = { 2, 2, 6, 4, 2, 2, 2, 4, 2, 4, 4, 4, 8 };
	private static Logger logger = Logger.getLogger(Parameter0303.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("Paramerer0303 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0303Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("Paramerer0303 error! " + e);
			result = false;
		}
		logger.info("Paramerer0303 ended! ");
		return result;

	}
}
