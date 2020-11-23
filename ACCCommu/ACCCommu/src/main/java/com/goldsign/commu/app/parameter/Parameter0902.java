package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0901Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * 周末时间表
 * 
 * @author zhangjh
 */
public class Parameter0902 extends ParameterBase {

	private static final String PARMTYPE = "0902";
	private static final int[] FORMAT = { 4, 4, 4, 4, 4 };
	private static Logger logger = Logger.getLogger(Parameter0902.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
                logger.info("Paramerer0902 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0901Dao.getRecordFromTable(paraGenDtl,
					dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("Paramerer0902 error! " + e);
			result = false;
		}
		logger.info("Paramerer0902 ended! ");
		return result;
	}
}
