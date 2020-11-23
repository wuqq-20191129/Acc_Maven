package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0602Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 地铁黑名单段
 * @author zhangjh
 */
public class Parameter0602 extends ParameterBase {

	private static final String PARMTYPE = "0602";
	private static final int[] FORMAT = { 20, 20, 3 };
	private static Logger logger = Logger.getLogger(Parameter0602.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		// logger.info("参数0602 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0602Dao.getRecordFromTable(paraGenDtl,
					dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0602 error! " + e);
			result = false;
		}
		logger.info("参数0602 ended! ");
		return result;
	}
}
