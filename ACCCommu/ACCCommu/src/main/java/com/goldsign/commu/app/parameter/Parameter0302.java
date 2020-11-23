package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0302Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 闸机专用通道参 数
 * 
 * @author zhangjh
 */
public class Parameter0302 extends ParameterBase {

	private static final String PARMTYPE = "0302";
	private static final int[] FORMAT = { 4, 1, 1 };
	private static Logger logger = Logger.getLogger(Parameter0302.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		// logger.info("Paramerer0302 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0302Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("Paramerer0302 error! " + e);
			result = false;
		}
		logger.info("Paramerer0302 ended! ");
		return result;

	}
}
