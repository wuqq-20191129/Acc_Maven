package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0606Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 交通部一卡白名单
 * @author lind
 */
public class Parameter0606 extends ParameterBase {

	private static final String PARMTYPE = "0606";
	private static final int[] FORMAT = { 11, 10};
	private static Logger logger = Logger.getLogger(Parameter0606.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("参数0606 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0606Dao.getRecordFromTable(paraGenDtl,
					dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0606 error! " + e);
			result = false;
		}
		logger.info("参数0606 ended! ");
		return result;
	}
}
