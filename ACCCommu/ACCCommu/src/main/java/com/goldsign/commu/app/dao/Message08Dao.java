package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.DeviceStatus;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 
 * @author zhangjh
 */
public class Message08Dao {

	private static Logger logger = Logger.getLogger(Message08Dao.class
			.getName());
	private static String sqlStr1 = " update "+FrameDBConstant.COM_COMMU_P+"cm_dev_current_status set acc_status_value=?,status_datetime=?,status_id=?,status_value=? ,oper_id=? "
			+ " where line_id=? and station_id=? and dev_type_id=? and device_id=? ";
	private static String sqlStr2 = " insert into "+FrameDBConstant.COM_COMMU_P+"cm_dev_current_status(line_id,station_id,dev_type_id,device_id,acc_status_value,status_datetime,status_id,status_value,oper_id) "
			+ " values(?,?,?,?,?,?,?,?,?)  ";
	private static String sqlStr3 = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_dev_his_status(status_datetime,line_id,station_id,dev_type_id,device_id,status_id,status_value,oper_id,acc_status_value) values(?,?,?,?,?,?,?,?,?)";

	public static void updateCurrentStatus(DbHelper otherDbHelper,
			Vector<DeviceStatus> currentStatusV) throws Exception {
		logger.info(DateHelper.datetimeToString(new Date())
				+ " 更新cm_dev_current_status记录数" + currentStatusV.size());

		int result;
		try {
			int num = currentStatusV.size();
			List<Object> values = new ArrayList<Object>();
			otherDbHelper.setAutoCommit(false);
			// 增加操作员信息 2011-09-15 by hejj
			for (int i = 0; i < num; i++) {

				DeviceStatus ds = currentStatusV.get(i);
				values.clear();
				values.add(ds.getAccStatusValue());
				values.add(ds.getStatusDatetime());
				values.add(ds.getStatusId());
				values.add(ds.getStatusValue());
				values.add(ds.getOperatorId());

				values.add(ds.getLineId());
				values.add(ds.getStationId());
				values.add(ds.getDevTypeId());
				values.add(ds.getDeviceId());
				// values.add(ds.getStatusDatetime());
				result = otherDbHelper.executeUpdate(sqlStr1, values.toArray());
                //zhongziqi 为了解决游标耗尽问题，DbHelper封装的executeUpdate方法并没有手工释放PreparedStatement
                otherDbHelper.close();
				// 如果没有对应设备，增加设备及其状态
				if (result != 1) {
					values.clear();
					values.add(ds.getLineId());
					values.add(ds.getStationId());
					values.add(ds.getDevTypeId());
					values.add(ds.getDeviceId());
					values.add(ds.getAccStatusValue());
					values.add(ds.getStatusDatetime());
					values.add(ds.getStatusId());
					values.add(ds.getStatusValue());
					values.add(ds.getOperatorId());
					otherDbHelper.executeUpdate(sqlStr2, values.toArray());
                    //zhongziqi 为了解决游标耗尽问题，DbHelper封装的executeUpdate方法并没有手工释放PreparedStatement
                    otherDbHelper.close();
				}

			}
			otherDbHelper.commitTran();
			otherDbHelper.setAutoCommit(true);
		} catch (SQLException e) {

			otherDbHelper.rollbackTran();
			otherDbHelper.setAutoCommit(true);
			throw e;
		} catch (Exception e) {
			otherDbHelper.rollbackTran();
			otherDbHelper.setAutoCommit(true);
			throw e;
		}

	}

	public static void insertHisStatus(DbHelper otherDbHelper,
			Vector<DeviceStatus> hisStatusV) throws Exception {
		logger.info(DateHelper.datetimeToString(new Date())
				+ "  cm_dev_his_status增加记录数 " + hisStatusV.size());
		// 增加操作员信息 2011-09-15 by hejj
		try {
			int num = hisStatusV.size();
			List<Object> values = new ArrayList<Object>();
			otherDbHelper.setAutoCommit(false);
			for (int i = 0; i < num; i++) {
				DeviceStatus ds = (DeviceStatus) hisStatusV.get(i);
				values.clear();
				values.add(ds.getStatusDatetime());
				values.add(ds.getLineId());
				values.add(ds.getStationId());
				values.add(ds.getDevTypeId());
				values.add(ds.getDeviceId());
				values.add(ds.getStatusId());
				values.add(ds.getStatusValue());
				values.add(ds.getOperatorId());
				values.add(ds.getAccStatusValue());
				otherDbHelper.executeUpdate(sqlStr3, values.toArray());
                //zhongziqi 为了解决游标耗尽问题，DbHelper封装的executeUpdate方法并没有手工释放PreparedStatement
                otherDbHelper.close();
			}
			otherDbHelper.commitTran();
			otherDbHelper.setAutoCommit(true);
		} catch (SQLException e) {
			otherDbHelper.rollbackTran();
			otherDbHelper.setAutoCommit(true);
			throw e;
		} catch (Exception e) {
			otherDbHelper.rollbackTran();
			otherDbHelper.setAutoCommit(true);
			throw e;
		}

	}
}
