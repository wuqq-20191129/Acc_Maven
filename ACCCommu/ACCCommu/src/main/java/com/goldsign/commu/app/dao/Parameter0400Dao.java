package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 票价参数
 * @author zhangjh
 */
public class Parameter0400Dao {

	private static Logger logger = Logger.getLogger(Parameter0400Dao.class
			.getName());
    //modify by zhongzq 增加排序 根据规范字段顺序排序 20190618
    private static String sqlStr01 = "select b_station_id,b_line_id,e_line_id,e_station_id,fare_zone,max_travel_time,over_time_charge "
            + "from " + FrameDBConstant.COM_ST_P + "op_prm_fare_zone where version_no=? and record_flag=?"
            + "  order by b_station_id,b_line_id,e_line_id,e_station_id,fare_zone,max_travel_time,over_time_charge ";

	private static String sqlStr02 = "select time_code,card_sub_id,card_main_id,fare_table_id,fare_deal_id,fare_time_id "
            + "from " + FrameDBConstant.COM_ST_P + "op_prm_fare_conf where version_no=? and record_flag=?"
            + " order by card_main_id,card_sub_id,time_code,fare_time_id,fare_deal_id,fare_table_id ";

	private static String sqlStr03 = "select fare_table_id,fare_zone,fare "
            + "from " + FrameDBConstant.COM_ST_P + "op_prm_fare_table where version_no=? and record_flag=?"
            + " order by fare_table_id,fare_zone,fare ";

    private static String sqlStr04 = "select holiday_type,start_date,end_date from " + FrameDBConstant.COM_ST_P + "op_prm_holiday_table where version_no=? and record_flag=?"
            + " order by holiday_type,start_date,end_date ";

    private static String sqlStr05 = "select day_of_week,start_time,end_time from " + FrameDBConstant.COM_ST_P + "op_prm_off_peak_hours where version_no=? and record_flag=?"
            + " order by day_of_week,start_time,end_time ";

    private static String sqlStr06 = "select time_id, time_min, time_max from " + FrameDBConstant.COM_ST_P + "op_prm_fare_time_interval where version_no=? and record_flag=?"
            + " order by time_id, time_min, time_max ";

    private static String sqlStr07 = "select deal_id, deal_min, deal_max from " + FrameDBConstant.COM_ST_P + "op_prm_fare_deal_total where version_no=? and record_flag=?"
            + " order by deal_id, deal_min, deal_max ";

	public static Vector<String[]> getRecordFromTable01(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;
//		logger.info("-----------------------");
		List<String> pStmtValues = getValues(paraGenDtl);
//		logger.info("paraGenDtl.getVerNum()---->" + pStmtValues.get(0));
//		logger.info("paraGenDtl.getVerType()---->" + pStmtValues.get(1));
//		logger.info("-----------------------");
		try {
			result = dbHelper.getFirstDocument(sqlStr01, pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("b_line_id")
							+ dbHelper.getItemValue("b_station_id");
					fields[1] = dbHelper.getItemValue("e_line_id")
							+ dbHelper.getItemValue("e_station_id");
					fields[2] = dbHelper.getItemValue("fare_zone");
					fields[3] = dbHelper.getItemValue("max_travel_time");
					fields[4] = dbHelper.getItemValue("over_time_charge");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_fare_zone error! ", e);
			return null;
		}
		return recV;
	}

	public static Vector<String[]> getRecordFromTable02(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;
		List<String> pStmtValues = getValues(paraGenDtl);
		try {
			result = dbHelper.getFirstDocument(sqlStr02, pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("card_main_id")
							+ dbHelper.getItemValue("card_sub_id");
					fields[1] = dbHelper.getItemValue("time_code");
					fields[2] = dbHelper.getItemValue("fare_time_id");
					fields[3] = dbHelper.getItemValue("fare_deal_id");
                                        fields[4] = dbHelper.getItemValue("fare_table_id");

					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_fare_conf error! " + e);
			return null;
		}
		return recV;
	}

	public static Vector<String[]> getRecordFromTable03(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;
		List<String> pStmtValues = getValues(paraGenDtl);
		try {
			result = dbHelper.getFirstDocument(sqlStr03, pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("fare_table_id");
					fields[1] = dbHelper.getItemValue("fare_zone");
					fields[2] = dbHelper.getItemValue("fare");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_fare_table error! " + e);
			return null;
		}
		return recV;
	}

	private static List<String> getValues(ParaGenDtl paraGenDtl) {
		List<String> pStmtValues = new ArrayList<String>();
		pStmtValues.clear();
		pStmtValues.add(paraGenDtl.getVerNum());
		pStmtValues.add(paraGenDtl.getVerType());
		return pStmtValues;
	}

	public static Vector<String[]> getRecordFromTable04(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;
		List<String> pStmtValues = getValues(paraGenDtl);
		try {
			result = dbHelper.getFirstDocument(sqlStr04, pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];

					fields[0] = dbHelper.getItemValue("holiday_type");
					fields[1] = dbHelper.getItemValue("start_date");
					fields[2] = dbHelper.getItemValue("end_date");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_holiday_table error! " + e);
			return null;
		}
		return recV;
	}

	public static Vector<String[]> getRecordFromTable05(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;
        //modify by zhongzq  20190618
//		Object[] values = { paraGenDtl.getVerNum() };
        Object[] values = {paraGenDtl.getVerNum(), paraGenDtl.getVerType()};
		try {
			result = dbHelper.getFirstDocument(sqlStr05, values);
			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("day_of_week");
					fields[1] = dbHelper.getItemValue("start_time");
					fields[2] = dbHelper.getItemValue("end_time");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_off_peak_hours error! " + e);
			return null;
		}
		return recV;
	}

    public static Vector<String[]> getRecordFromTable06(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;

		List<String> pStmtValues = getValues(paraGenDtl);
		try {
			result = dbHelper.getFirstDocument(sqlStr06, pStmtValues.toArray());
			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("time_id");
					fields[1] = dbHelper.getItemValue("time_min");
					fields[2] = dbHelper.getItemValue("time_max");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_fare_time_interval error! " + e);
			return null;
		}
		return recV;
	}

    public static Vector<String[]> getRecordFromTable07(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;

		List<String> pStmtValues = getValues(paraGenDtl);
		try {
			result = dbHelper.getFirstDocument(sqlStr07, pStmtValues.toArray());
			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("deal_id");
					fields[1] = dbHelper.getItemValue("deal_min");
					fields[2] = dbHelper.getItemValue("deal_max");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_fare_deal_total error! " + e);
			return null;
		}
		return recV;
	}
}
