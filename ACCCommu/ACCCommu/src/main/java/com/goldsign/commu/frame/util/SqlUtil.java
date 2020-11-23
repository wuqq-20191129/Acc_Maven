package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.vo.SqlVo;
import java.util.Vector;

/**
 * 
 * @author hejj
 */
public class SqlUtil {
	public boolean isValidValue(String value) {
		return value != null && value.length() != 0;
	}
	/**
	 * 判断是否为有效值：对象不为空、如果为String对象，长度大于0
	 * @param value
	 * @return
	 */
	public boolean isValidValue(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof java.lang.String) {
			if (((String) value).length() == 0) {
				return false;
			}

		}

		return true;
	}

	public SqlVo getSqlForUpdate(String[] values, String[] fieldNames,
			String tableName, String[] valuesWhere, String[] fieldNamesWhere) {
		Vector<String> v = new Vector<String>();
		Vector<String> v1 = new Vector<String>();
		SqlVo vo = new SqlVo();
		for (int i = 0; i < values.length; i++) {
			if (this.isValidValue(values[i])) {
				v.add(fieldNames[i]);
				v1.add(values[i]);
			}
		}
		String sqlField = this.formSqlForUpdate(v);
		String where = this.fromSqlForWhere(fieldNamesWhere);
		String sql = "update " + tableName + " set " + sqlField + " " + where;
		vo.setValues(v1);
		vo.addValues(valuesWhere);
		vo.setSql(sql);

		return vo;

	}

	public SqlVo getSqlForInsert(String[] values, String[] fieldNames,
			String tableName) {
		Vector<String> v = new Vector<String>();
		Vector<String> v1 = new Vector<String>();
		SqlVo vo = new SqlVo();
		for (int i = 0; i < values.length; i++) {

			if (this.isValidValue(values[i])) {
				v.add(fieldNames[i]);
				v1.add(values[i]);

			}
		}
		String sqlField = this.formSqlForInsert(v);
		String sqlValue = this.formSqlForInsertValue(v);
		String sql = "insert into " + tableName + "(" + sqlField + ") "
				+ " values(" + sqlValue + ")";
		vo.setSql(sql);
		vo.setValues(v1);

		return vo;
	}

	public SqlVo getSqlForInsert(Object[] values, String[] fieldNames,
			String tableName) {
		Vector<String> v = new Vector<String>();
		Vector<Object> v1 = new Vector<Object>();
		SqlVo vo = new SqlVo();
		for (int i = 0; i < values.length; i++) {

			if (this.isValidValue(values[i])) {
				v.add(fieldNames[i]);
				v1.add(values[i]);

			}
		}
		String sqlField = this.formSqlForInsert(v);
		String sqlValue = this.formSqlForInsertValue(v);
		String sql = "insert into " + tableName + "(" + sqlField + ") "
				+ " values(" + sqlValue + ")";
		vo.setSql(sql);
		vo.setValues(v1);

		return vo;
	}

	private String fromSqlForWhere(String[] fieldNamesWhere) {
		String where = " where ";
		for (int i = 0; i < fieldNamesWhere.length; i++) {
			where += fieldNamesWhere[i] + "=?,";
		}
		if (where.length() > 7) {
			where = where.substring(0, where.length() - 1);
		}
		return where;
	}

	private String formSqlForUpdate(Vector<String> fieldNames) {
		String stmt = "";
		for (int i = 0; i < fieldNames.size(); i++) {
			stmt += (String) fieldNames.get(i) + "=?,";
		}
		if (stmt.length() != 0) {
			stmt = stmt.substring(0, stmt.length() - 1);
		}
		return stmt;
	}

	private String formSqlForInsert(Vector<String> fieldNames) {
		String stmt = "";
		for (int i = 0; i < fieldNames.size(); i++) {
			stmt += (String) fieldNames.get(i) + ",";
		}
		if (stmt.length() != 0) {
			stmt = stmt.substring(0, stmt.length() - 1);
		}
		return stmt;
	}

	private String formSqlForInsertValue(Vector<String> fieldNames) {
		String stmt = "";
		for (int i = 0; i < fieldNames.size(); i++) {
			stmt += "?,";
		}
		if (stmt.length() != 0) {
			stmt = stmt.substring(0, stmt.length() - 1);
		}

		return stmt;
	}

}
