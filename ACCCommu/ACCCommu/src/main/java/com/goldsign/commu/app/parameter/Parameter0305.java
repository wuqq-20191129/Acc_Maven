package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0305Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 手机票逻辑印刻号对照
 * Date 20150413
 * version:1.08
 * @author lindaquan
 */
public class Parameter0305 extends ParameterBase {

	private static final String PARMTYPE = "0305";
	private static final int[] FORMAT = { 20, 20 };
	private static Logger logger = Logger.getLogger(Parameter0305.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("参数0305 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0305Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0305 error! " + e);
			result = false;
		}
		logger.info("参数0305 ended! ");
		return result;
	}
}
