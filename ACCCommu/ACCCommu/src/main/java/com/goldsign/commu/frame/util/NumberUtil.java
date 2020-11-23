package com.goldsign.commu.frame.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 
 * @author hejj
 */
public class NumberUtil {
	public NumberUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static String formatNumber(int number, int len) {
		if (number < 0)
			return "";
		String sn = Integer.toString(number);
		String tmp = "";
		for (int i = 0; i < len - sn.length(); i++)
			tmp += "0";
		return tmp + sn;
	}

	public static String formatNumber(BigInteger number, int len) {

		String sn = number.toString();
		String tmp = "";
		for (int i = 0; i < len - sn.length(); i++)
			tmp += "0";
		return tmp + sn;
	}

	public static String formatNumber(String number, int len) {

		String sn = number;
		String tmp = "";
		for (int i = 0; i < len - sn.length(); i++)
			tmp += "0";
		return tmp + sn;
	}

	public static Integer getIntegerValue(String value, int defaultValue) {
		if (value == null || value.length() == 0)
			return new Integer(defaultValue);
		return new Integer(value);
	}

	public static String getIntegerStrValue(String value, int defaultValue) {
		if (value == null || value.length() == 0)
			return new Integer(defaultValue).toString();
		return value;
	}

	public static String getDateTimeValue(String value, String defaultValue) {
		if (value == null || value.length() == 0)
			return defaultValue;
		return value;
	}

	public static Object getDateTimeValue(String value, Object defaultValue) {
		if (value == null || value.length() == 0)
			return defaultValue;
		return value;
	}

	public static String getDateTimeBeginValue(String value) {
		if (value == null || value.length() == 0)
			return "";
		return value + " 00:00:00";
	}

	public static String getDateTimeEndValue(String value) {
		if (value == null || value.length() == 0)
			return "";
		return value + " 23:59:59";
	}

	public static String getDateValue(String value) {
		if (value == null || value.length() == 0)
			return "";
		return value.substring(0, 10);
	}

	public static String getStringValue(String value) {
		if (value == null || value.length() == 0)
			return "";
		return value;
	}

	public static String getStringValue(String value, String defaultValue) {
		if (value == null || value.length() == 0)
			return defaultValue;
		return value;
	}

	public static BigDecimal getBigDecimalValue(String value,
			String defaultValue) {
		if (value == null || value.length() == 0)
			return new BigDecimal(defaultValue);
		return new BigDecimal(value);
	}

	public static BigDecimal getBigDecimalValue(String value,
			String defaultValue, int scale) {
		if (value == null || value.length() == 0)
			value = defaultValue;
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale);
		return bd;
	}

	public static String add(String n, int addNum) {
		return new Integer(new Integer(n).intValue() + addNum).toString();
	}

	//
	// public static void main(String[] args){
	// String tmp ="60";
	// BigDecimal bg = new BigDecimal(tmp);
	//
	// // bg.setScale(3);
	// logger.info(bg.toString());
	// // logger.info("n ="+NumberUtil.formatNumber(new
	// BigInteger("20080101"),10));
	//
	// }

}
