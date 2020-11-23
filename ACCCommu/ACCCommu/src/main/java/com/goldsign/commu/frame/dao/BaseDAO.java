package com.goldsign.commu.frame.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.lib.db.util.DbHelper;

public abstract class BaseDAO {

	// protected DbHelper dbHelper;

	// protected BaseDAO() {
	// String thisClassName = this.getClass().getName();
	// thisClassName = thisClassName.substring(
	// thisClassName.lastIndexOf(".") + 1, thisClassName.length());
	// // this.dbHelper = new
	// //
	// DbHelper(thisClassName,ApplicationConstant.OTHER_DBCPHELPER.getConnection());
	// // this.dbHelper = new DbHelper(thisClassName,
	// // FrameDBConstant.OP_DBCPHELPER.getConnection());
	// }

	public abstract void batchInsert(HandleMessageBase handlingMsg)
			throws SQLException;

	public String getInsertSql(String tableName, String fields) {
		Vector fieldV = this.getVectorForStr(fields, ",");
		String paraForInsert = this.getParaSqlForInsert(fieldV.size());
		String sql = "insert into " + tableName + "(" + fields + ")"
				+ " values(" + paraForInsert + ")";
		return sql;

	}

	public Vector getVectorForStr(String strIDs, String delim) {
		StringTokenizer st = new StringTokenizer(strIDs, delim);
		String ID = null;
		Vector<String> v = new Vector<String>();
		while (st.hasMoreTokens()) {
			ID = st.nextToken();
			v.add(ID);
		}
		return v;

	}

	public String getParaSqlForInsert(int size) {
		String para = "";
		for (int i = 0; i < size; i++)
			para = para + "?" + ",";

		return para.substring(0, para.length() - 1);
	}

	public void addValuesByBatch(DbHelper dbHelper, String[] fieldValues,
			String[] fieldTypes) throws IllegalArgumentException,
			IllegalStateException, SQLException {

		Object[] values = this.getFieldValues(fieldValues, fieldTypes);
		dbHelper.addBatch(values);

		values = null;

	}

	public Object[] getFieldValues(String[] fieldValuesStr, String[] fieldTypes) {

		// if (fieldValuesStr.length != fieldTypes.length)
		// throw new Exception("字段数量与字段类型数量不一致");

		Object[] fieldValues = new Object[fieldValuesStr.length];
		Object fieldValue;
		String fieldType;

		for (int i = 0; i < fieldValuesStr.length; i++) {
			fieldType = (String) fieldTypes[i];

			if (fieldType.equals("string") || fieldType.equals("date")) {
				fieldValue = fieldValuesStr[i];
				fieldValues[i] = fieldValue;
				continue;
			}
			if (fieldType.equals("decimal")) {
				fieldValue = new BigDecimal(fieldValuesStr[i]);
				fieldValues[i] = fieldValue;
				continue;
			}
			if (fieldType.equals("int")) {
				fieldValue = new Integer(fieldValuesStr[i]);
				fieldValues[i] = fieldValue;
				continue;
			}
		}
		return fieldValues;
	}
}
