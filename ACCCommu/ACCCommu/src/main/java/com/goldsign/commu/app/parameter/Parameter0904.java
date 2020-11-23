/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0901Dao;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 节假日时间表2
 * 
 * @author zhangjh
 */
public class Parameter0904 extends ParameterBase {

	private static final String PARMTYPE = "0904";
	private static final int[] FORMAT = { 4, 4, 4, 4, 4 };
	private static Logger logger = Logger.getLogger(Parameter0904.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		logger.info("Paramerer0904 started! ");
		try {
			// get parameter data
			Vector<String[]> recV = Parameter0901Dao.getRecordFromTable(
					paraGenDtl, dbHelper, FORMAT.length);
			result = dealData(recV, PARMTYPE, FORMAT);
		} catch (Exception e) {
			logger.error("Paramerer0904 error! " + e);
			result = false;
		}
		logger.info("Paramerer0904 ended! ");
		return result;
	}
}
