package com.goldsign.commu.app.parameter;

import com.goldsign.commu.app.dao.Parameter0400Dao;
import com.goldsign.commu.frame.exception.ParameterException;
import com.goldsign.commu.frame.parameter.ParameterBase;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 票价参数
 * 
 * @author hejj
 */
public class Parameter0400 extends ParameterBase {

	private static final String PARMTYPE01 = "0401";
	private static final String PARMTYPE02 = "0402";
	private static final String PARMTYPE03 = "0403";
	private static final String PARMTYPE04 = "0404";
	private static final String PARMTYPE05 = "0405";
        private static final String PARMTYPE06 = "0406";
	private static final String PARMTYPE07 = "0407";
	// 收费区段
	private static final int[] FORMAT01 = { 4, 4, 2, 5, 4 };
	// 收费配置
	private static final int[] FORMAT02 = { 4, 1, 2, 2, 3 };
	// 票价表
	private static final int[] FORMAT03 = { 3, 2, 10 };
	// 节假日定义表
	private static final int[] FORMAT04 = { 1, 8, 8 };
	// 非繁忙时间定义表
	private static final int[] FORMAT05 = { 1, 4, 4 };
        // 联乘时间间隔定义表
	private static final int[] FORMAT06 = { 2, 6, 6 };
	// 累计消费额定义表
	private static final int[] FORMAT07 = { 2, 6, 6 };
	private static Logger logger = Logger.getLogger(Parameter0400.class
			.getName());

	@Override
	public boolean formParaFile() {
		boolean result;
		// logger.info("参数0400 started! ");
		try {
			// get parameter data
			Vector<String[]> rec01 = Parameter0400Dao.getRecordFromTable01(
					paraGenDtl, dbHelper, FORMAT01.length);
			Vector<String[]> rec02 = Parameter0400Dao.getRecordFromTable02(
					paraGenDtl, dbHelper, FORMAT02.length);
			Vector<String[]> rec03 = Parameter0400Dao.getRecordFromTable03(
					paraGenDtl, dbHelper, FORMAT03.length);

			Vector<String[]> rec04 = Parameter0400Dao.getRecordFromTable04(
					paraGenDtl, dbHelper, FORMAT04.length);
			Vector<String[]> rec05 = Parameter0400Dao.getRecordFromTable05(
					paraGenDtl, dbHelper, FORMAT05.length);
                        Vector<String[]> rec06 = Parameter0400Dao.getRecordFromTable06(
					paraGenDtl, dbHelper, FORMAT06.length);
			Vector<String[]> rec07 = Parameter0400Dao.getRecordFromTable07(
					paraGenDtl, dbHelper, FORMAT07.length);
			/*
			 * if(rec01==null||rec02==null||rec03==null||rec04==null||rec05==null
			 * ){ throw new
			 * ParameterException("Get record from parameter table error! "); }
			 */

			// format data
			// 记录从数据库取出将要写文件的记录数 add by hejj 2012-08-27
			this.setDbRecordNum(rec01, rec02, rec03, rec04, rec05, rec06, rec07);

			Vector<String> formatedRecV = new Vector<String>();
			if (rec01 != null) {
				Vector<String> formatedRecV01 = formatRecord(PARMTYPE01, rec01, FORMAT01);
				formatedRecV.addAll(formatedRecV01);
			}
			if (rec02 != null) {
				Vector<String> formatedRecV02 = formatRecord(PARMTYPE02, rec02, FORMAT02);
				formatedRecV.addAll(formatedRecV02);
			}
			if (rec03 != null) {
				Vector<String> formatedRecV03 = formatRecord(PARMTYPE03, rec03, FORMAT03);
				formatedRecV.addAll(formatedRecV03);
			}
			if (rec04 != null) {
				Vector<String> formatedRecV04 = formatRecord(PARMTYPE04, rec04, FORMAT04);
				formatedRecV.addAll(formatedRecV04);
			}
			if (rec05 != null) {
				Vector<String> formatedRecV05 = formatRecord(PARMTYPE05, rec05, FORMAT05);
				formatedRecV.addAll(formatedRecV05);
			}
                        if (rec06 != null) {
				Vector<String> formatedRecV06 = formatRecord(PARMTYPE06, rec06, FORMAT06);
				formatedRecV.addAll(formatedRecV06);
			}
			if (rec07 != null) {
				Vector<String> formatedRecV07 = formatRecord(PARMTYPE07, rec07, FORMAT07);
				formatedRecV.addAll(formatedRecV07);
			}

			// write data to file
			result = writeDataToFile(formatedRecV);
			if (!result) {
				throw new ParameterException("Write parameter data to file "
						+ paraFileName + " error! ");
			}
			logger.info("Parameter file " + paraFileName
					+ " generated successfully. ");
		} catch (Exception e) {
			logger.error("参数0400 error! " + e);
			result = false;
		}
		logger.info("参数0400 ended! ");
		return result;
	}
}
