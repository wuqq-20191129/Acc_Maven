package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0501Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 降级模式使用记录
 * 
 * @author zhangjh
 */
public class Parameter0501 extends ParameterBase {

	private static final int[] FORMAT = { 4, 3, 14, 14, 6, 6 };
	private static final String PARMTYPE = "0501";
	private static Logger logger = Logger.getLogger(Parameter0501.class
			.getName());

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean formParaFile() {
		boolean result;
                logger.info("参数0501 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0501Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
                        if(result){
                            Parameter0501Dao.updateDegradeModeRecd(dbHelper);
                        }
		} catch (Exception e) {
			logger.error("参数0501 error! " + e);
			result = false;
		}
		logger.info("参数0501 ended! ");
		return result;
	}
}
