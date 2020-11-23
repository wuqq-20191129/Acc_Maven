package com.goldsign.commu.frame.util;

import com.goldsign.lib.db.util.DbHelper;
import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class PubDbUtil {
	private static Logger logger = Logger.getLogger(PubDbUtil.class.getName());

	public static int getTableSequence(String seqName, DbHelper dbHelper)
			throws Exception {
		String strSql = "select " + seqName + ".nextval from dual";
		dbHelper.getFirstDocument(strSql);
		return dbHelper.getItemIntValue("nextval");

	}

}
