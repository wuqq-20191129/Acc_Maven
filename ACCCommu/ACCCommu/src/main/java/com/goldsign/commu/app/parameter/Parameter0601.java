package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0601Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 地铁黑名单
 * @author zhangjh
 */
public class Parameter0601 extends ParameterBase {

	private static final String PARMTYPE = "0601";
	private static final int[] FORMAT = { 20, 3 };
	private static Logger logger = Logger.getLogger(Parameter0601.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("参数0601 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0601Dao.getRecordFromTable(paraGenDtl,
					dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0601 error! " + e);
			result = false;
		}
		logger.info("参数0601 ended! ");
		return result;
	}
}
