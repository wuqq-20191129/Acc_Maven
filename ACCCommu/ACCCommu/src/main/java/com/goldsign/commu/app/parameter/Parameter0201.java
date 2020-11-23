package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0201Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 车站表
 * 
 * @author zhangjh
 */
public class Parameter0201 extends ParameterBase {

	private static final String PARMTYPE = "0201";
	private static final int[] FORMAT = { 4, 40, 50, 200, 15, 15 };
	private static Logger logger = Logger.getLogger(Parameter0201.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
                logger.info("参数0201 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0201Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0201 error! " + e);
			result = false;
		}
		logger.info("参数0201 ended! ");
		return result;
	}
}
