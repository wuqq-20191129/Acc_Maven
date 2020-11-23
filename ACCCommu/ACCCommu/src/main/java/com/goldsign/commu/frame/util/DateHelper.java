package com.goldsign.commu.frame.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author hejj
 */
public class DateHelper {
	//
	public static final String yyyyMMdd = "yyyyMMdd";
	public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
	public static final String yyyy_MM_dd = "yyyy-MM-dd";
	public static final String HHmm = "HHmm";
	public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

	// private static int N = 0;
	// /**
	// * 时间格式:yyyyMMdd
	// */
	// private static final SimpleDateFormat YYYYMMDD = new SimpleDateFormat(
	// yyyyMMdd);
	// /**
	// * 时间格式:HHmm
	// */
	// private static final SimpleDateFormat HHMM = new SimpleDateFormat(HHmm);
	// /**
	// * 时间格式:yyyyMMddHHmmss
	// */
	// private static final SimpleDateFormat YYYYMMDDHHMMSS = new
	// SimpleDateFormat(
	// yyyyMMddHHmmss);
	// /**
	// * 时间格式:yyyy-MM-dd
	// */
	// private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat(
	// yyyy_MM_dd);
	// /**
	// * 时间格式:yyyy-MM-dd HH:mm:ss
	// */
	// private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new
	// SimpleDateFormat(
	// yyyy_MM_dd_HH_mm_ss);

	public static String datetimeToStringOnlyDateF(java.util.Date d) {
		SimpleDateFormat YYYYMMDD = new SimpleDateFormat(yyyyMMdd);
		return YYYYMMDD.format(d);
	}

	public static String curentDateToHHMM() {
		SimpleDateFormat HHMM = new SimpleDateFormat(HHmm);
		return HHMM.format(new Date());
	}

	public static String curentDateToYYYYMMDDHHMMSS() {
		SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat(yyyyMMddHHmmss);
		return YYYYMMDDHHMMSS.format(new Date());
	}

	/**
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToStr(Date date, String format) {
		SimpleDateFormat YYYYMMDD = new SimpleDateFormat(yyyyMMdd);
		SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat(yyyy_MM_dd);
		SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat(yyyyMMddHHmmss);
		SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
				yyyy_MM_dd_HH_mm_ss);
		String dateStr = "";
		if (yyyyMMdd.equals(format)) {
			dateStr = YYYYMMDD.format(date);
		} else if (yyyy_MM_dd.equals(format)) {
			dateStr = YYYY_MM_DD.format(date);
		} else if (yyyyMMddHHmmss.equals(format)) {
			dateStr = YYYYMMDDHHMMSS.format(date);
		} else if (yyyy_MM_dd_HH_mm_ss.equals(format)) {
			dateStr = YYYY_MM_DD_HH_MM_SS.format(date);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			dateStr = sdf.format(date);

		}
		return dateStr;
	}

	public static String getDateBefore(String curDate, long beforeTime) {
		SimpleDateFormat YYYYMMDD = new SimpleDateFormat(yyyyMMdd);
		Date d;
		String dateBefore = "";
		try {
			d = YYYYMMDD.parse(curDate);
			Date datePre = new Date(d.getTime() - beforeTime);
			dateBefore = YYYYMMDD.format(datePre);
		} catch (ParseException e) {
			Logger.getLogger(DateHelper.class.getName()).log(Level.SEVERE,
					null, e);
		}
		return dateBefore;

	}

	public static String getDateAfter(String curDate, long afterTime) {
		SimpleDateFormat YYYYMMDD = new SimpleDateFormat(yyyyMMdd);
		Date d;
		String dateAfter = "";
		try {
			d = YYYYMMDD.parse(curDate);
			Date datePre = new Date(d.getTime() + afterTime);
			dateAfter = YYYYMMDD.format(datePre);
		} catch (ParseException e) {
			Logger.getLogger(DateHelper.class.getName()).log(Level.SEVERE,
					null, e);
		}
		return dateAfter;

		// df.

	}

	public static String getDateTimeAfter(String curDate, long afterTime) {
		SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat(yyyyMMddHHmmss);
		Date d;
		String dateAfter = "";
		try {
			d = YYYYMMDDHHMMSS.parse(curDate);
			Date datePre = new Date(d.getTime() + afterTime);
			dateAfter = YYYYMMDDHHMMSS.format(datePre);
		} catch (ParseException e) {
			Logger.getLogger(DateHelper.class.getName()).log(Level.SEVERE,
					null, e);
		}
		return dateAfter;

	}

	public static String dateToString(java.util.Date d) {
		SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat(yyyyMMddHHmmss);
		return YYYYMMDDHHMMSS.format(d);
	}

	public static String dateOnlyToString(java.util.Date d) {
		SimpleDateFormat YYYYMMDD = new SimpleDateFormat(yyyyMMdd);
		return YYYYMMDD.format(d);
	}

	public static String dateToStringName(java.util.Date d) {
		SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
				yyyy_MM_dd_HH_mm_ss);
		return YYYY_MM_DD_HH_MM_SS.format(d);
	}

	public static String datetimeToString(java.util.Date d) {
		SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
				yyyy_MM_dd_HH_mm_ss);
		return YYYY_MM_DD_HH_MM_SS.format(d);
	}

	public static String datetimeToStringByDate(java.util.Date d) {
		SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat(yyyy_MM_dd);
		return YYYY_MM_DD.format(d);
	}

	// public static void screenPrintForEx(Map m) {
	// String date = datetimeToString(new Date());
	// String key;
	// Long value;
	// Set set = m.keySet();
	// Iterator it = set.iterator();
	// while (it.hasNext()) {
	// key = (String) it.next();
	// value = (Long) m.get(key);
	// logger.info(date + "  " + key + "=" + value);
	// }
	// }

	public static String currentTodToString() {
		Date d = new Date(System.currentTimeMillis());
		return dateToString(d);
	}

	public static java.sql.Timestamp utilDateToSqlTimestamp(
			java.util.Date utilDate) {
		return new java.sql.Timestamp(utilDate.getTime());
	}

	public static java.sql.Timestamp getCurrentSqlTimestamp() {
		Date utilDate = new Date(System.currentTimeMillis());
		return new java.sql.Timestamp(utilDate.getTime());
	}

	public static long getDiffer(String date, Date date1) throws ParseException {
		SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
				yyyy_MM_dd_HH_mm_ss);
		Date d = YYYY_MM_DD_HH_MM_SS.parse(date);
		long n1 = date1.getTime();
		long n = d.getTime();
		return Math.abs(n1 - n);

	}

	public static long getDiffer(String date, String date1)
			throws ParseException {
		SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
				yyyy_MM_dd_HH_mm_ss);
		Date d = YYYY_MM_DD_HH_MM_SS.parse(date);
		Date d1 = YYYY_MM_DD_HH_MM_SS.parse(date1);
		long n1 = d1.getTime();
		long n = d.getTime();
		return Math.abs(n1 - n);

	}

	public static long getDifferInDays(String date, String date1)
			throws ParseException {
		SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat(yyyy_MM_dd);
		Date d = YYYY_MM_DD.parse(date);
		Date d1 = YYYY_MM_DD.parse(date1);
		long n1 = d1.getTime();
		long n = d.getTime();
		long days = (n1 - n) / (24 * 3600 * 1000);
		return days;

	}

	public static String convertStandFormat(String ymd) {
		return ymd.substring(0, 4) + "-" + ymd.substring(4, 6) + "-"
				+ ymd.substring(6, 8);
	}

	public static boolean isGEIndicateDays(String date, Date date1, int difDays)
			throws ParseException {
		SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat(yyyy_MM_dd);
		Date d = YYYY_MM_DD.parse(date);
		long n1 = date1.getTime();
		long n = d.getTime();
		long dif = 24L * 3600 * 1000 * difDays;
		if (n1 - n >= dif) {
			return true;
		}
		return false;

	}

	public static boolean isLEIndicateDays(String date, Date date1, int difDays)
			throws ParseException {
		SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat(yyyy_MM_dd);
		Date d = YYYY_MM_DD.parse(date);
		long n1 = date1.getTime();
		long n = d.getTime();
		long dif = 24L * 3600 * 1000 * difDays;
		if (n1 - n <= dif) {
			return true;
		}
		return false;

	}

	private static Date dateStrToUtilDate(String dateStr) throws ParseException {
		SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat(yyyyMMddHHmmss);
		ParsePosition pos = new ParsePosition(0);
		return YYYYMMDDHHMMSS.parse(dateStr, pos);
	}

	public static Timestamp dateStrToSqlTimestamp(String dateStr)
			throws ParseException {
		java.util.Date utilDate = dateStrToUtilDate(dateStr);
		return new Timestamp(utilDate.getTime());
	}

	private static Date dateStrToUtilDate1(String dateStr)
			throws ParseException {
		SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
				yyyy_MM_dd_HH_mm_ss);
		return YYYY_MM_DD_HH_MM_SS.parse(dateStr);
	}

	public static Timestamp dateStrToSqlTimestamp1(String dateStr)
			throws ParseException {
		java.util.Date utilDate = dateStrToUtilDate1(dateStr);
		return new Timestamp(utilDate.getTime());
	}
        
        public static String dateStrToStr(String d){
            String result = curentDateToYYYYMMDDHHMMSS();
            try {
                result = dateToString(dateStrToUtilDate1(d));
            } catch (ParseException ex) {
                ex.printStackTrace();
            } finally {
                return result;
            }
        }
}
