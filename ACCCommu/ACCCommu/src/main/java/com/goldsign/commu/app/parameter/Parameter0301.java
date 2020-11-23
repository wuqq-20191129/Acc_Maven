package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0301Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 票卡参数
 * 
 * @author zhangjh
 */
public class Parameter0301 extends ParameterBase {

	private static final String PARMTYPE = "0301";
	private static final int[] FORMAT = { 2, 2, 1, 6, 1, 6, 1, 6, 1, 1, 1, 1,
			3, 2, 4, 16, 1, 4, 6, 6, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 16, 16, 1,
			6, 6, 16, 1, 1, 16 };
	private static Logger logger = Logger.getLogger(Parameter0301.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
                logger.info("参数0301 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0301Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0301 error! ", e);
			result = false;
		}
		logger.info("参数0301 ended! ");
		return result;
	}
}
