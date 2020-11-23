package com.goldsign.commu.app.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.message.ConstructMessage06;
import com.goldsign.commu.app.vo.DeviceStatus;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author zhangjh
 */
public class DegradeModeRecordDAO{

	private static Logger logger = Logger.getLogger(DegradeModeRecordDAO.class
			.getName());

	// public DegradeModeRecordDAO(DbcpHelper dbcp) throws Exception {
	// this.dbHelper = new DbHelper("DegradeModeRecordDAO",
	// dbcp.getConnection());
	// }
	public static boolean isNewSquadDate(String hourMin) {
		String squadHour = FrameCodeConstant.SQUAD_TIME.substring(0, 2);
		String squadMin = FrameCodeConstant.SQUAD_TIME.substring(2);
		return hourMin.compareTo(squadHour + squadMin) > 0;
	}

	public static String getSquadDate() {
		// YYYYMMDDHHMMSS
		String current = DateHelper.dateToString(new Date());
		// HHMM
		String curHourMin = current.substring(8, 12);
		// YYYYMMDD
		String curDate = DateHelper.dateOnlyToString(new Date());
		if (isNewSquadDate(curHourMin)) {
			return curDate;
		}
		// YYYYMMDD-1
		String nextDate = DateHelper.getDateBefore(curDate, (24 * 3600 * 1000));

		return nextDate;

	}

	public static synchronized void recordStart(DeviceStatus ds,
			Vector<String> ipToSend, String command, String flag)
			throws Exception {
		String newVerNum;
		int waterNo = 0;
		//版本号改成按运营日生成，不按自然日
		String dateverNum = getSquadDate();// formatMode.format(new Date());
		DbHelper dbHelper = null;
		try {
			dbHelper = new DbHelper("",
					FrameDBConstant.OP_DBCPHELPER.getConnection());

			dbHelper.setAutoCommit(false);

			String sql = "select  water_no from "+FrameDBConstant.COM_ST_P+"st_water_no where water_no_key='dmode' ";
			boolean result = dbHelper.getFirstDocument(sql);
			if (result) {
				String verNum = dbHelper.getItemValue("water_no");
				if (verNum == null || verNum.length() < 10) {
					verNum = "0000000000";
				}
				if ((verNum.substring(0, 8)).equals(dateverNum)) {
					int no = Integer.parseInt(verNum.substring(8, 10)) + 1;
					if (no > 9) {
						newVerNum = verNum.substring(0, 8) + no;
					} else {
						newVerNum = verNum.substring(0, 8) + "0" + no;
					}
				} else {
					newVerNum = dateverNum + "01";
				}

				logger.info("verNum is :" + verNum);

			} else {
				newVerNum = dateverNum + "01";
			}

			// ADD BY CS
			if (!result) {
				sql = "insert into "+FrameDBConstant.COM_ST_P+"st_water_no values('dmode'," + "to_number("
						+ newVerNum + "))";
				dbHelper.executeUpdate(sql);
			}

			// update water_no
			sql = "update "+FrameDBConstant.COM_ST_P+"st_water_no set water_no= to_number(" + newVerNum
					+ ") where water_no_key='dmode'";
			dbHelper.executeUpdate(sql);

			String insertSql = "insert into "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd(water_no,line_id,station_id,degrade_mode_id,start_time,end_time,set_oper_id,cancel_oper_id,version_no,hdl_flag)"
                                + "values("+FrameDBConstant.COM_ST_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"op_prm_degrade_recd.nextval,?,?,?,?,'',?,'',?,?)";
			Object[] values = new Object[7];
			values[0] = ds.getLineId();
			values[1] = ds.getStationId();
			values[2] = ds.getStatusId();
			values[3] = ds.getStatusDatetimeStr();
			values[4] = ds.getOperatorId();
			values[5] = newVerNum;
			values[6] = "0";
			dbHelper.executeUpdate(insertSql, values);

			StringBuilder stbf = new StringBuilder();

			stbf.append("update "+FrameDBConstant.COM_ST_P+"st_water_no set water_no=water_no+1 where water_no_key='param'");
			dbHelper.executeUpdate(stbf.toString());

			stbf.setLength(0);
			stbf.append("select water_no from "+FrameDBConstant.COM_ST_P+"st_water_no where water_no_key='param'");

			if (dbHelper.getFirstDocument(stbf.toString())) {
				waterNo = dbHelper.getItemIntValue("water_no");
			} else {
				throw new Exception("get new water_no from st_water_no errr! ");
			}
			logger.info("生成的降级模式的版本号是：" + waterNo);
			stbf.setLength(0);
			stbf.append(
					"\n insert into "+FrameDBConstant.COM_ST_P+"op_prm_para_gen_dtl(water_no,parm_type_id,version_no,version_type,gen_result)values(")
					.append(waterNo).append(",'0501','").append(newVerNum)
					.append("','0','0')");
			dbHelper.executeUpdate(stbf.toString());
			stbf = new StringBuilder();
			for (Enumeration e = FrameCodeConstant.ALL_LCC_IP.keys(); e
					.hasMoreElements();) {
				String lineId = (String) e.nextElement();

				stbf.append(
						"insert into "+FrameDBConstant.COM_ST_P+"op_prm_para_inform_dtl(water_no,line_id,station_id,inform_result)values(")
						.append(waterNo).append(",'").append(lineId)
						.append("','00','0')");
				dbHelper.executeUpdate(stbf.toString());
				stbf = new StringBuilder();
			}
			stbf = new StringBuilder();
			stbf.append("insert into "+FrameDBConstant.COM_ST_P+"op_prm_para_distribute_dtl(water_no,distribute_datetime,operator_id,distribute_result,distribute_memo)values(?,?,'commu','0','')");
			Object[] vals = new Object[2];
			vals[0] = waterNo;
			vals[1] = DateHelper.getCurrentSqlTimestamp();
			String sqle = stbf.toString();
			logger.info(" the sql is :" + sqle);
			// logger.info(stbf.toString());
			dbHelper.executeUpdate(sqle, vals);

			// SEND MESSAGE TO LCC
			logger.info("构建06消息开始");
			ConstructMessage06 cm = new ConstructMessage06();
			cm.constructAndSend(ipToSend, ds.getLineId() + ds.getStationId(),
					command, flag);

			dbHelper.commitTran();
			dbHelper.setAutoCommit(true);

		} catch (SQLException e) {
			if (null != dbHelper) {
				dbHelper.rollbackTran();
			}
			logger.error(
					"insert into "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd -- Update table error -- Step 3",
					e);
			throw e;
		} catch (Exception e) {
			if (null != dbHelper) {
				dbHelper.rollbackTran();
			}
			logger.error(
					"insert into "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd -- Update table error -- Step 3",
					e);
			throw e;
		} finally {
			try {
				if (null != dbHelper) {
					dbHelper.closeConnection();
				}

			} catch (Exception e) {
				logger.error("insert into "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd -- Update table error -- Step 4"
						+ e);
			}
		}
	}

	public static synchronized void recordEnd(DeviceStatus ds,
			Vector<String> ipToSend, String command, String flag)
			throws Exception {
		String newVerNum;
		int waterNo = 0;

		String dateverNum = getSquadDate();
		DbHelper degradeHelper = null;

		try {

			degradeHelper = new DbHelper("",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			degradeHelper.setAutoCommit(false);

			String sql = "select water_no from "+FrameDBConstant.COM_ST_P+"st_water_no where water_no_key='dmode' ";
			boolean result = degradeHelper.getFirstDocument(sql);
			if (result) {
				String verNum = degradeHelper.getItemValue("water_no");
				if (verNum == null || verNum.length() < 10) {
					verNum = "0000000000";
				}

				if ((verNum.substring(0, 8)).equals(dateverNum)) {
					int no = Integer.parseInt(verNum.substring(8, 10)) + 1;
					if (no > 9) {
						newVerNum = verNum.substring(0, 8) + no;
					} else {
						newVerNum = verNum.substring(0, 8) + "0" + no;
					}
				} else {
					newVerNum = dateverNum + "01";
				}
			} else { // 无dmode，增加记录
				newVerNum = dateverNum + "01";
				sql = "insert into "+FrameDBConstant.COM_ST_P+"st_water_no values('dmode'," + "to_number("
						+ newVerNum + "))";
				degradeHelper.executeUpdate(sql);
			}

			// update water_no
			sql = "update "+FrameDBConstant.COM_ST_P+"st_water_no set water_no= to_number(" + newVerNum
					+ ") where water_no_key='dmode'";
			degradeHelper.executeUpdate(sql);

			// logger.info("newVerNum:"+newVerNum);
			String sql1 = "select max(water_no) waterno from "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd where line_id = ? and station_id = ? and degrade_mode_id = ? and hdl_flag='0'";
			Object[] values = new Object[3];
			values[0] = ds.getLineId();
			values[1] = ds.getStationId();
			values[2] = ds.getStatusId();
			if (degradeHelper.getFirstDocument(sql1, values)) {
				waterNo = degradeHelper.getItemIntValue("waterno");
			} else {
				throw new Exception("get new water_no from water_no errr! ");
			}
			// update degrade mode end record
			String sql2 = "update "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd set end_time=?,cancel_oper_id=?,version_no=?,hdl_flag='1' where water_no=?";
			values = null;
			values = new Object[4];
			values[0] = ds.getStatusDatetimeStr();
			values[1] = ds.getOperatorId();
			values[2] = newVerNum;
			values[3] = waterNo;
			int updated = degradeHelper.executeUpdate(sql2, values);

			// ADD BY CS
			// dbHelper.commitTran();
			// dbHelper.setAutoCommit(true);

			if (updated <= 0) {
				throw new Exception("No related degrade mode start record! ");
			}

			StringBuilder stbf = new StringBuilder();

			stbf.append("update "+FrameDBConstant.COM_ST_P+"st_water_no set water_no=water_no+1 where water_no_key='param'");
			degradeHelper.executeUpdate(stbf.toString());
			stbf.setLength(0);
			stbf.append("select water_no from "+FrameDBConstant.COM_ST_P+"st_water_no where water_no_key='param'");
			if (degradeHelper.getFirstDocument(stbf.toString())) {
				waterNo = degradeHelper.getItemIntValue("water_no");
			} else {
				throw new Exception("get new water_no from ater_no error! ");
			}
			stbf = null;
			stbf = new StringBuilder();
			stbf.append(
					"\n insert into "+FrameDBConstant.COM_ST_P+"op_prm_para_gen_dtl(water_no,parm_type_id,VERSION_NO,VERSION_TYPE,GEN_RESULT)values(")
					.append(waterNo).append(",'0501','").append(newVerNum)
					.append("','0','0')");
			degradeHelper.executeUpdate(stbf.toString());
			stbf = null;
			for (Enumeration e = FrameCodeConstant.ALL_LCC_IP.keys(); e
					.hasMoreElements();) {
				stbf = new StringBuilder();
				String lineId = (String) e.nextElement();
				stbf.append(
						"\n insert into "+FrameDBConstant.COM_ST_P+"op_prm_para_inform_dtl(water_no,line_id,station_id,inform_result)values(")
						.append(waterNo).append(",'").append(lineId)
						.append("','00','0')");
				degradeHelper.executeUpdate(stbf.toString());
			}
			stbf = null;
			stbf = new StringBuilder();
			stbf.append(
					"\n insert into "+FrameDBConstant.COM_ST_P+"op_prm_para_distribute_dtl(water_no,distribute_datetime,operator_id,distribute_result,distribute_memo)values(")
					.append(waterNo).append(",?,'commu','0','')");
			Object[] value = new Object[1];
			value[0] = DateHelper.getCurrentSqlTimestamp();
			degradeHelper.executeUpdate(stbf.toString(), value);

			// Send Message to LCC
			ConstructMessage06 cm = new ConstructMessage06();
			cm.constructAndSend(ipToSend, ds.getLineId() + ds.getStationId(),
					command, flag);

			degradeHelper.commitTran();
			degradeHelper.setAutoCommit(true);

		} catch (Exception e) {
			if (null != degradeHelper) {
				degradeHelper.rollbackTran();
			}

			logger.error("recordEnd error! ", e);
			throw e;
		} finally {
			if (null != degradeHelper) {
				try {
					degradeHelper.closeConnection();
				} catch (Exception e) {
					logger.error("closeConnection error:" + e);
				}
			}

		}
	}

	public static boolean judgeDup(DeviceStatus ds, String flag)
			throws Exception {

		String lineId = ds.getLineId();
		String stationId = ds.getStationId();
		String statusId = ds.getStatusId();
		String statusTime = ds.getStatusDatetimeStr();
		String sqlStr = null;
		DbHelper DupHelper = null;
		int dup;
		boolean result;

		DupHelper = new DbHelper("",
				FrameDBConstant.OP_DBCPHELPER.getConnection());

		if (flag.equals("01")) {
			sqlStr = "select count(*) dup from "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd where line_id=? and station_id=? and degrade_mode_id=? and start_time=?";
		} else if (flag.equals("02")) {
			sqlStr = "select count(*) dup from "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd where line_id=? and station_id=? and degrade_mode_id=? and end_time=?";
		}
		Object[] values = new Object[4];
		values[0] = lineId;
		values[1] = stationId;
		values[2] = statusId;
		values[3] = statusTime;

		// logger.info("lineId:"+lineId);
		// logger.info("stationId:"+stationId);
		// logger.info("statusId:"+statusId);
		// logger.info("statusTime:"+statusTime);

		try {
			// logger.info("Judging start degrademode duplication");
			result = DupHelper.getFirstDocument(sqlStr, values);

			if (result) {
				logger.info("query OK");
			} else {
				logger.info("query error");
			}

			dup = DupHelper.getItemIntValue("dup");
			// logger.info("dup:"+dup);
			if (dup == 0) {
				return true;
			} else {
				logger.error("degrade message duplicate");
				return false;
			}

		} catch (SQLException e) {
			try {
				DupHelper.rollbackTran();
			} catch (SQLException e1) {
				logger.error("catch one exception.", e);
			}
			logger.error("judgeDup error", e);
			throw e;
		} catch (Exception e) {
			try {
				DupHelper.rollbackTran();
			} catch (SQLException e1) {
				logger.error("catch one exception.", e1);
			}
			logger.error("judgeDup error.", e);
			throw e;
		} finally {
			DupHelper.closeConnection();
		}
	}

	public static boolean judgeLastTime(DeviceStatus ds, String flag)
			throws Exception {

		String lineId = ds.getLineId();
		String stationId = ds.getStationId();
		String statusId = ds.getStatusId();
		String statusTime = ds.getStatusDatetimeStr();
		// String operator = ds.getOperatorId();
		String sqlStr = null;
		DbHelper LastHelper = null;
		boolean result;
		String startTime, endTime;

		LastHelper = new DbHelper("",
				FrameDBConstant.OP_DBCPHELPER.getConnection());

		if (flag.equals("01")) {
			sqlStr = "select start_time,end_time from "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd where "
					+ "water_no=(select max(water_no) from "+FrameDBConstant.COM_ST_P+"op_prm_degrade_mode_recd where line_id=? and station_id=? and degrade_mode_id=?)";
		}
		Object[] values = new Object[3];
		values[0] = lineId;
		values[1] = stationId;
		values[2] = statusId;

		// logger.info("lineId:"+lineId);
		// logger.info("stationId:"+stationId);
		// logger.info("statusId:"+statusId);

		try {
			// logger.info("judgeLastTime start ");
			// LastHelper.executeUpdate(sqlStr, values);
			result = LastHelper.getFirstDocument(sqlStr, values);
			if (!result) {
				// logger.info("judgeLastTime OK");
				return true;
			}
			startTime = LastHelper.getItemValue("start_time");
			endTime = LastHelper.getItemValue("end_time");

		} catch (SQLException e) {
			try {
				LastHelper.rollbackTran();
			} catch (SQLException e1) {
				logger.error(e);
			}
			logger.error("judgeDup error" + e);
			throw e;
		} catch (Exception e) {
			try {
				LastHelper.rollbackTran();
			} catch (SQLException e1) {
				logger.error(e1);
			}
			logger.error("judgeDup error" + e);
			throw e;
		} finally {
			LastHelper.closeConnection();
		}
		if ((startTime == null || startTime.equals(""))
				&& (endTime == null || endTime.equals(""))) {
			return true;
		}

		if ((startTime != null || !"".equals(startTime))
				&& (endTime == null || "".equals(endTime))) {
			return false;
		}

		if (endTime.compareTo(statusTime) >= 0) {
			return false;
		} else {
			return true;
		}

	}

}
