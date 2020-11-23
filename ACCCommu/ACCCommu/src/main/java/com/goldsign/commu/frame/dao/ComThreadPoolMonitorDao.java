package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameThreadPoolConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.util.SqlUtil;
import com.goldsign.commu.frame.vo.CommuHandledMessage;
import com.goldsign.commu.frame.vo.SqlVo;
import com.goldsign.commu.frame.vo.TMThreadMsg;
import com.goldsign.commu.frame.vo.TMThreadResetVo;
import com.goldsign.commu.frame.vo.TMThreadStatusVo;
import com.goldsign.lib.db.util.DbHelper;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * 
 * @author hejj
 */
public class ComThreadPoolMonitorDao {

	private static Logger logger = Logger
			.getLogger(ComThreadPoolMonitorDao.class.getName());

	public int dumpThreadStatus() throws Exception {
		DbHelper dbHelper = null;
		int n = 0;
		try {
			dbHelper = new DbHelper("flowDbUtil",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			dbHelper.setAutoCommit(false);
			String sqlStr = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_trd_monitor_his"
					+ "(water_no,reason_id,dump_date,thread_id,thread_name,thread_status,msg_id,msg_name,hdl_time_start,hdl_end_time,remark) "
					+ " select "+FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_trd_monitor_his.nextval,?,sysdate,thread_id,thread_name,thread_status,msg_id,msg_name,hdl_time_start,hdl_end_time,remark "
					+ " from "+FrameDBConstant.COM_COMMU_P+"cm_trd_monitor ";
			logger.debug(sqlStr);
			String[] values = { FrameThreadPoolConstant.ThreadDumpResonRestart };

			n = dbHelper.executeUpdate(sqlStr, values);
			sqlStr = "delete from "+FrameDBConstant.COM_COMMU_P+"cm_trd_monitor ";
			dbHelper.executeUpdate(sqlStr);
			dbHelper.commitTran();
		} catch (Exception e) {
			PubUtil.handleExceptionForTran(e, logger, dbHelper);
		} finally {
			PubUtil.finalProcessForTran(dbHelper);
		}
		return n;

	}

	public int writeInfoForMsgHandupNumber(String msgId, int handupNum)
			throws Exception {
		DbHelper dbHelper = null;
		int n = 0;
		try {
			dbHelper = new DbHelper("flowDbUtil",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			String sqlStr = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_trd_msg_handup_num"
					+ "(water_no,msg_id,hand_up_num,hand_up_date,remark) values("+FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_trd_msg_handup_num.nextval,?,?,?,?)";
			Object[] values = { msgId, new Integer(handupNum),
					new java.sql.Timestamp(new Date().getTime()), "线程替换时，消息阻塞次数" };
			// CharUtil.GbkToIso("线程替换时，消息阻塞次数")};
			n = dbHelper.executeUpdate(sqlStr, values);
		} catch (Exception e) {
			PubUtil.handleException(e, logger);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return n;

	}

	public static int updateStatus(TMThreadStatusVo vo) throws Exception {
		DbHelper dbHelper = null;
		int n = 0;

		String[] values = { vo.getThreadStatus(), vo.getHdlTimeStart(),
				vo.getHdlEndTime(), vo.getMsgId(), vo.getMsgName() };
		String[] values_1 = { vo.getThreadId(), vo.getThreadName(),
				vo.getThreadStatus(), vo.getMsgId(), vo.getMsgName(),
				vo.getHdlTimeStart(), vo.getHdlEndTime(), vo.getRemark() };

		String[] valuesWhere = { vo.getThreadId() };
		SqlUtil util = new SqlUtil();
		String[] fieldNames = { "thread_status", "hdl_time_start",
				"hdl_end_time", "msg_id", "msg_name" };
		String[] fieldNames1 = { "thread_id", "thread_name", "thread_status",
				"msg_id", "msg_name", "hdl_time_start", "hdl_end_time",
				"remark" };
		String[] fieldNamesWhere = { "thread_id" };
		SqlVo sqlVoUpdate = util.getSqlForUpdate(values, fieldNames,
				FrameDBConstant.COM_COMMU_P+"cm_trd_monitor", valuesWhere, fieldNamesWhere);
		SqlVo sqlVoInsert = util.getSqlForInsert(values_1, fieldNames1,
				FrameDBConstant.COM_COMMU_P+"cm_trd_monitor");
		String sql;

		try {
			dbHelper = new DbHelper("flowDbUtil",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			sql = sqlVoUpdate.getSql();
			n = dbHelper.executeUpdate(sql, sqlVoUpdate.getValues().toArray());

			if (n != 1) {
				sql = sqlVoInsert.getSql();
				n = dbHelper.executeUpdate(sql, sqlVoInsert.getValues()
						.toArray());
			}

		} catch (Exception e) {
			PubUtil.handleException(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return n;

	}

	public int writeInfoForReplaceThread(TMThreadResetVo vo) throws Exception {
		DbHelper dbHelper = null;
		int n = 0;

                byte[] message = vo.getMessage();
                byte[] newMessage = new byte[200];
                if(message.length>200){
                    System.arraycopy(message, 0, newMessage, 0, 200);
                }
		Object[] values_1 = { vo.getReasonId(), vo.getDumpDate(),
				vo.getThreadId(), vo.getThreadName(), vo.getThreadStatus(),
				vo.getThreadQueuePriNum(), vo.getThreadQueueOrdNum(),
				vo.getThreadIdNew(), vo.getThreadNameNew(),
				vo.getThreadStatusNew(), vo.getThreadQueuePriNumNew(),
				vo.getThreadQueueOrdNumNew(), vo.getMsgId(), vo.getMsgName(),
				vo.getHdltimeStart(), vo.getHdltimeEnd(), vo.getRemark(),
				message.length>200?newMessage:message };

		SqlUtil util = new SqlUtil();

		String[] fieldNames1 = { "reason_id", "dump_date", "thread_id",
				"thread_name", "thread_status", "thread_queue_pri_num",
				"thread_queue_ord_num", "thread_id_new", "thread_name_new",
				"thread_status_new", "thread_queue_pri_num_new",
				"thread_queue_ord_num_new", "msg_id", "msg_name",
				"hdl_time_start", "hdl_time_end", "remark", "message" };

		SqlVo sqlVoInsert = util.getSqlForInsert(values_1, fieldNames1,
				FrameDBConstant.COM_COMMU_P+"cm_trd_reset");
		String sql;

		try {
			dbHelper = new DbHelper("flowDbUtil",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			sql = sqlVoInsert.getSql();
			n = dbHelper.executeUpdate(sql, sqlVoInsert.getValues().toArray());

		} catch (Exception e) {
			PubUtil.handleException(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return n;

	}

	public int writeInfoForDelMsgs(
			HashMap<TMThreadMsg, Vector<CommuHandledMessage>> msgsDel,
			String curDate) throws Exception {
		DbHelper dbHelper = null;
		int n = 0;
		Set set = msgsDel.keySet();
		Iterator it = set.iterator();
		TMThreadMsg msgKey;
		Vector<CommuHandledMessage> cmsgs;
		try {
			dbHelper = new DbHelper("flowDbUtil",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			dbHelper.setAutoCommit(false);
			while (it.hasNext()) {
				msgKey = (TMThreadMsg) it.next();
				cmsgs = msgsDel.get(msgKey);
				n += this.writeInfoForDelMsg(dbHelper, msgKey, cmsgs, curDate);

			}
			dbHelper.commitTran();

		} catch (Exception e) {
			PubUtil.handleExceptionForTran(e, logger, dbHelper);

		} finally {
			PubUtil.finalProcessForTran(dbHelper);
		}
		return n;
	}

	public int writeInfoForDelMsg(DbHelper dbHelper, TMThreadMsg msgKey,
			Vector<CommuHandledMessage> msgs, String curDate) throws Exception {
		CommuHandledMessage cmsg;
		byte[] msg;
		int n = 0;
		for (int i = 0; i < msgs.size(); i++) {
			cmsg = (CommuHandledMessage) msgs.get(i);
			msg = (byte[]) cmsg.getReadResult().get(1);
			n += this.writeInfoForDelMsg(dbHelper, msgKey, msg, curDate,
					cmsg.getHdlThreadId(), cmsg.getHdlQueueType());
		}

		return n;

	}

	public int writeInfoForDelMsg(DbHelper dbHelper, TMThreadMsg msgKey,
			byte[] msg, String curDate, String threadId, String queueType)
			throws Exception {
		int n;
		Object[] values = { curDate, msgKey.getMsgId(), msg, "线程队列删除",
				threadId, queueType };
		String sql = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_trd_msg_del(water_no,del_date,msg_id,message,remark,thread_id,queue_type) values("+FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_trd_msg_del.nextval,?,?,?,?,?,?)";
		n = dbHelper.executeUpdate(sql, values);
		return n;

	}

	private boolean isDefaultValue(String key) {
		return FrameThreadPoolConstant.ThreadMsgHandleMaxTimeDefaultKey
				.equals(key);
	}

	public void setMsgMaxHdlTime() throws Exception {
		boolean result;
		DbHelper dbHelper = null;
//		Vector v = new Vector();
		String msgId;
		long maxHdlTime;
		try {
			dbHelper = new DbHelper("flowDbUtil",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			String sqlStr = "select msg_id,max_hdl_time from "+FrameDBConstant.COM_COMMU_P+"cm_trd_max_hdltime ";
			result = dbHelper.getFirstDocument(sqlStr);
			while (result) {
				msgId = dbHelper.getItemValue("msg_id");
				maxHdlTime = dbHelper.getItemLongValue("max_hdl_time");
				if (this.isDefaultValue(msgId)) {
					FrameThreadPoolConstant.ThreadMsgHandleMaxTimeDefault = maxHdlTime;
				} else {
					FrameThreadPoolConstant.ThreadMsgHandleMaxTime.put(msgId,
							new Long(maxHdlTime));
				}
				result = dbHelper.getNextDocument();
			}

		} catch (Exception e) {
			PubUtil.handleException(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}

	}
}
