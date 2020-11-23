package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0605Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 交通部一卡单个黑名单
 * @author lind
 */
public class Parameter0605 extends ParameterBase {

	private static final String PARMTYPE = "0605";
	private static final int[] FORMAT = { 20, 3 };
	private static Logger logger = Logger.getLogger(Parameter0605.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("参数0605 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0605Dao.getRecordFromTable(paraGenDtl,
					dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0605 error! " + e);
			result = false;
		}
		logger.info("参数0605 ended! ");
		return result;
	}
}
