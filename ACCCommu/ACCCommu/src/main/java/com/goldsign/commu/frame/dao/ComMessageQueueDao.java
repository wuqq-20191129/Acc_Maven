package com.goldsign.commu.frame.dao;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author hejj
 */
public class ComMessageQueueDao {

	private DbHelper dbHelper;
	private static Logger logger = Logger.getLogger(ComMessageQueueDao.class
			.getName());

	// default,the queue is on the communication database
	public ComMessageQueueDao() {
	}

	public ComMessageQueueDao(boolean isUseDefaultDb) {
	}

	public ComMessageQueueDao(DbHelper db) {
		this.dbHelper = db;
	}

	public void pushQueue(MessageQueue message) {
		// logger.info("Push message to queue start!");
		logger.info("准备将消息存入到队列表...");
		String sqlStr = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_que_message"
                        + " (message_time,ip_address,message,process_flag,is_para_inform_msg,para_inform_water_no,line_id,station_id,message_id,message_type,message_type_sub,remark) "
                        + " values(?,?,?,?,?,?,?,?,"+FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_que_message.nextval,?,?,?)";
		Object[] values = new Object[11];
		values[0] = DateHelper.utilDateToSqlTimestamp(message.getMessageTime());
		values[1] = message.getIpAddress();
		// logger.info("Message will be send to " + message.getIpAddress());
		values[2] = message.getMessage();
		values[3] = "0";
		values[4] = message.getIsParaInformMsg();
		values[5] =  Integer.valueOf(message.getParaInformWaterNo());
		values[6] = message.getLineId();
		values[7] = message.getStationId();
                values[8] = message.getMessageType();
                values[9] = message.getMessageTypeSub();
                values[10] = message.getMessageRemark();

		try {
			dbHelper = new DbHelper("",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			dbHelper.executeUpdate(sqlStr, values);
			// logger.info("Push message to queue end!");
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.info("消息存入到队列表结束...");
	}

	public void updateFlagQueue(long id) {
		// logger.info("Update message start!");
		String sqlStr = "update "+FrameDBConstant.COM_COMMU_P+"cm_que_message set process_flag='1' where message_id=?";
		Object values[] = new Object[1];
		values[0] = Long.valueOf(id);
		try {
			dbHelper = new DbHelper("",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			dbHelper.executeUpdate(sqlStr, values);
			// logger.info("Update message end!");
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}

	}


	public Vector<MessageQueue> pullQueuesForThread() throws Exception {

		MessageQueue mq = null;
		boolean result = false;
		// byte[] data = null;
		StringBuffer sb = new StringBuffer();
		Vector<MessageQueue> mqs = new Vector<MessageQueue>();

		sb.append("select message_id,message_time,line_id,station_id,ip_address,message,process_flag,is_para_inform_msg,para_inform_water_no from "+FrameDBConstant.COM_COMMU_P+"cm_que_message");
		sb.append(" where process_flag='0' order by ip_address,message_id");

		try {
			dbHelper = new DbHelper("",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			if (!dbHelper.isAvailableForConn()) {
				return mqs;
			}
			result = dbHelper.getFirstDocument(sb.toString());
			while (result) {
				// logger.info("Message : "
				// +dbHelper.getItemLongValue("message_id")
				// +" got from message queue!");
				mq = new MessageQueue();
				mq.setMessageId(dbHelper.getItemLongValue("message_id"));
				mq.setMessageTime(dbHelper.getItemDateTimeValue("message_time"));
				mq.setLineId(dbHelper.getItemValue("line_id"));
				mq.setStationId(dbHelper.getItemValue("station_id"));
				mq.setIpAddress(dbHelper.getItemValue("ip_address"));
				mq.setMessage(dbHelper.getItemBytesValue("message"));
				mq.setProcessFlag(dbHelper.getItemValue("process_flag"));
				mq.setIsParaInformMsg(dbHelper
						.getItemValue("is_para_inform_msg"));
				mq.setParaInformWaterNo(dbHelper
						.getItemIntValue("para_inform_water_no"));
				mqs.add(mq);
				result = dbHelper.getNextDocument();
			}
			// logger.info("test Pull messages from queue ! msqs size="+mqs.size());
			// logger.info("Pull message from queue end!");
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		return mqs;
	}
	public boolean checkForParaGen(int waterNo){
		boolean sqlResult =false;
		boolean result =false;
		Vector<String> parmList =new Vector<String>();
		StringBuffer sb = new StringBuffer();

		sb.append("select water_no, parm_type_id, version_type, version_no, gen_result from  "+FrameDBConstant.COM_ST_P+"op_prm_para_gen_dtl");
		sb.append(" where water_no=?  and gen_result='1' order by water_no");

		Object[] values = { new Integer(waterNo) };
		try {
			dbHelper = new DbHelper("",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			if (!dbHelper.isAvailableForConn()) {
				return result;
			}
			logger.debug("================="+sb);
			sqlResult = dbHelper.getFirstDocument(sb.toString(), values);
			while (sqlResult) {
				String pramTyepId =dbHelper.getItemValue("parm_type_id");
				parmList.add(pramTyepId.substring(0,2));
				sqlResult = dbHelper.getNextDocument();
			}
			if(parmList.contains("06")){
				result=true;
			}
		} catch (Exception e) {
			PubUtil.handleExceptionNoThrow(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		return result;
	}


}
