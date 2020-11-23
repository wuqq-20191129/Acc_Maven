package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.SynchronizedControl;

import org.apache.log4j.Logger;

import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 
 * @author hejj
 */
public class LogUtil {

	private static Logger logger = Logger.getLogger(LogUtil.class.getName());
	public static SynchronizedControl SYNCONTROL_RECEIVE_SEND = new SynchronizedControl();
	private static String sqlStr1 = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_log_connect(water_no,connect_datetime,connect_ip,connect_result,remark) values("+FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_log_connect.nextval,?,?,?,?)";
	//private static String sqlStr2 = "insert into cm_ec_recv_send_log(water_no,datetime_rec,ip,type,message_code,message_sequ,message,result) values(seq_cm_ec_recv_send_log.nextval,sysdate,?,?,?,?,?,?)";
	private static String sqlStr3 = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_log_recv_send(water_no,datetime_rec,ip,type,message_code,message_sequ,message,result) values("+FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_log_recv_send.nextval,sysdate,?,?,?,?,?,?)";
	private static String sqlStr4 = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_log_ftp(water_no,datetime_ftp,ip,filename,start_time,spend_time,result)  values("+FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_log_ftp.nextval,sysdate,?,?,?,?,?)";

	public static synchronized void writeConnectLog(java.util.Date time,
			String ip, String result, String remark) {
		// Framelogger.info(FrameDateHelper.datetimeToString(new
		// Date())+"  增加记录com_connect_log");
		DbHelper dbHelper = null;
		Object[] values = { new java.sql.Timestamp(time.getTime()), ip, result,
				remark };
		try {
			// dbHelper = new DbHelper("LogUtil", dbcpHelper.getConnection());
			dbHelper = new DbHelper("LogUtil",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			dbHelper.executeUpdate(sqlStr1, values);
		} catch (Exception e) {
			logger.error("向cm_log_connect表插入数据出现异常! ", e);
		} finally {
			if (dbHelper != null) {
				try {
					dbHelper.closeConnection();
				} catch (Exception e) {
					logger.error("Close connection error! ", e);
				}
			}
		}
	}

	public static synchronized void writeRecvSendLog(java.util.Date time,
			String ip, String type, String messageCode, String messageSequ,
			byte[] message, String result) throws Exception {
		// Framelogger.info(FrameDateHelper.datetimeToString(new
		// Date())+"  增加记录com_recv_send_log");
		DbHelper dbHelper = null;
                messageCode = "" + (char)message[0] + (char)message[1];

		Object[] values = { ip, type, messageCode, messageSequ, message, result };
		try {
			// dbHelper = new DbHelper("LogUtil", dbcpHelper.getConnection());
			// synchronized (SYNCONTROL_RECEIVE_SEND) {
			dbHelper = new DbHelper("LogUtil",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			dbHelper.executeUpdate(sqlStr3, values);
			// }
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			logger.error("向cm_log_recv_send表插入数据出现异常! ", e);
		} finally {
			if (dbHelper != null) {
				try {
					dbHelper.closeConnection();
				} catch (Exception e) {
					logger.error("Close connection error! " + e);
				}
			}
		}
	}

	public static synchronized void writeRecvSendLog(java.util.Date time,
			String ip, String type, String messageCode, String messageSequ,
			byte[] message, String result, DbHelper dbHelper) throws Exception {

		Object[] values = { ip, type, messageCode, messageSequ, message, result };
		// synchronized (SYNCONTROL_RECEIVE_SEND) {
		dbHelper.executeUpdate(sqlStr3, values);
		// }

	}

	public static synchronized void writeFtpLog(java.util.Date time, String ip,
			String fileName, Date startTime, int spendTime, String result,
			DbHelper dbHelper) {

		Object[] values = { ip, fileName, new Timestamp(startTime.getTime()),
				spendTime, result };
		try {
			dbHelper.executeUpdate(sqlStr4, values);
		} catch (Exception ex) {
			logger.error("向cm_log_ftp表插入数据出现异常", ex);
		}

	}
}
