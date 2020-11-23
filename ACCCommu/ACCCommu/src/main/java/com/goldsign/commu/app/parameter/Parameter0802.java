package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0802Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 行政处理原因
 * 
 * @author zhangjh
 */
public class Parameter0802 extends ParameterBase {

	private static final String PARMTYPE = "0802";
	private static final int[] FORMAT = { 2, 90 };
	private static Logger logger = Logger.getLogger(Parameter0802.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
                logger.info("参数0802 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0802Dao.getRecordFromTable(paraGenDtl,
					dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("参数0802 error! " + e);
			result = false;
		}
		logger.info("参数0802 ended! ");
		return result;
	}
}
