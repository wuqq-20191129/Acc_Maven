package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0901Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * 工作日时间表
 * 
 * @author zhangjh
 */
public class Parameter0901 extends ParameterBase {

	private static final String PARMTYPE = "0901";
	private static final int[] FORMAT = { 4, 4, 4, 4, 4 };
	private static Logger logger = Logger.getLogger(Parameter0901.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
                logger.info("Paramerer0901 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0901Dao.getRecordFromTable(paraGenDtl,
					dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("Paramerer0901 error! " + e);
			result = false;
		}
		logger.info("Paramerer0901 ended! ");
		return result;
	}
}
