package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.vo.LogCommuVo;
import com.goldsign.commu.frame.vo.SynchronizedControl;
import com.goldsign.lib.db.util.DbHelper;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class LogDbUtil {

	private static Logger logger = Logger.getLogger(LogDbUtil.class.getName());
	private static SynchronizedControl SYN_CONTROL = new SynchronizedControl();
	private static String[] messageIds = {
			FrameLogConstant.MESSAGE_ID_PARAM_SYN,
			FrameLogConstant.MESSAGE_ID_PARAM_REPLY,
			FrameLogConstant.MESSAGE_ID_DEV_STATUS_ALL,
			FrameLogConstant.MESSAGE_ID_DEV_STATUS_EACH,
			FrameLogConstant.MESSAGE_ID_DEV_STATUS_CHANGE,
			FrameLogConstant.MESSAGE_ID_DEV_EVENT,
			FrameLogConstant.MESSAGE_ID_FILE_NOTICE,
			FrameLogConstant.MESSAGE_ID_FLOW_ENTRY,
			FrameLogConstant.MESSAGE_ID_FLOW_EXIT,
			FrameLogConstant.MESSAGE_ID_TVM_SJT,
			FrameLogConstant.MESSAGE_ID_NON_RETURN,
                        FrameLogConstant.MESSAGE_ID_LOSS_REPORT,
			FrameLogConstant.MESSAGE_ID_START,
			FrameLogConstant.MESSAGE_ID_CONFIG,
			FrameLogConstant.MESSAGE_ID_PARAM_DISTRIBUTE,
			FrameLogConstant.MESSAGE_ID_PUSH_QUEUE,
			FrameLogConstant.MESSAGE_ID_PARAM_COMMU_QUEUE,
			FrameLogConstant.MESSAGE_ID_CONNECTION,
			FrameLogConstant.MESSAGE_ID_SOCKET_EXCHAGE,
			FrameLogConstant.MESSAGE_ID_FTP };
	private static String[] messageNames = {
			FrameLogConstant.MESSAGE_ID_PARAM_SYN_NAME,
			FrameLogConstant.MESSAGE_ID_PARAM_REPLY_NAME,
			FrameLogConstant.MESSAGE_ID_DEV_STATUS_ALL_NAME,
			FrameLogConstant.MESSAGE_ID_DEV_STATUS_EACH_NAME,
			FrameLogConstant.MESSAGE_ID_DEV_STATUS_CHANGE_NAME,
			FrameLogConstant.MESSAGE_ID_DEV_EVENT_NAME,
			FrameLogConstant.MESSAGE_ID_FILE_NOTICE_NAME,
			FrameLogConstant.MESSAGE_ID_FLOW_ENTRY_NAME,
			FrameLogConstant.MESSAGE_ID_FLOW_EXIT_NAME,
			FrameLogConstant.MESSAGE_ID_TVM_SJT_NAME,
			FrameLogConstant.MESSAGE_ID_NON_RETURN_NAME,
                        FrameLogConstant.MESSAGE_ID_LOSS_REPORT_NAME,
			FrameLogConstant.MESSAGE_ID_START_NAME,
			FrameLogConstant.MESSAGE_ID_CONFIG_NAME,
			FrameLogConstant.MESSAGE_ID_PARAM_DISTRIBUTE_NAME,
			FrameLogConstant.MESSAGE_ID_PUSH_QUEUE_NAME,
			FrameLogConstant.MESSAGE_ID_PARAM_COMMU_QUEUE_NAME,
			FrameLogConstant.MESSAGE_ID_CONNECTION_NAME,
			FrameLogConstant.MESSAGE_ID_SOCKET_EXCHAGE_NAME,
			FrameLogConstant.MESSAGE_ID_FTP_NAME };
	private static String[] levelTexts = {
			FrameLogConstant.LOG_LEVEL_INFO_TEXT,
			FrameLogConstant.LOG_LEVEL_WARN_TEXT,
			FrameLogConstant.LOG_LEVEL_ERROR_TEXT,
			FrameLogConstant.LOG_LEVEL_ERROR_SYS_TEXT, };
	private static String[] levels = { FrameLogConstant.LOG_LEVEL_INFO,
			FrameLogConstant.LOG_LEVEL_WARN, FrameLogConstant.LOG_LEVEL_ERROR,
			FrameLogConstant.LOG_LEVEL_ERROR_SYS };

	public LogDbUtil() {
		super();
	}

	// 根据级别控制明细输出，如级别>=当前设定的级别，当前级别是警告，而级别是信息的，其不输出
	public static int logForDbDetail(LogCommuVo vo, String level)
			throws Exception {
		if (level.compareTo(FrameLogConstant.LOG_LEVEL_CURRENT) >= 0) {
			return logForDbDetail(vo);
		}
		return 0;

	}

	public static int logForDbDetail(LogCommuVo vo, String level,
			DbHelper dbHelper) throws Exception {
		if (level.compareTo(FrameLogConstant.LOG_LEVEL_CURRENT) >= 0) {
			return logForDbDetail(vo, dbHelper);
		}
		return 0;

	}

	public static int logForDbDetail(String messageId, String messageFrom,
			long startTime, long endTime, String result, String hdlThread,
			String level) throws Exception {
		LogCommuVo vo = getLogCommuDetailVo(messageId, messageFrom, startTime,
				endTime, result, hdlThread, level);
		int n = 0;
		synchronized (SYN_CONTROL) {
			n = logForDbDetail(vo, level);
		}
		return n;

	}

	

	public static int logForDbDetail(String messageId, String messageFrom,
			long startTime, long endTime, String result, String hdlThread,
			String level, String remark) {
		int n = 0;
		try {
			LogCommuVo vo = getLogCommuDetailVo(messageId, messageFrom,
					startTime, endTime, result, hdlThread, level, remark);

			synchronized (SYN_CONTROL) {
				n = logForDbDetail(vo, level);
			}

		} catch (Exception e) {
			logger.error(" catch one one exception ", e);
		}
		return n;

	}

	public static int logForDbDetail(String messageId, String messageFrom,
			long startTime, long endTime, String result, String hdlThread,
			String level, String remark, DbHelper dbHelper) {
		int n = 0;
		try {
			LogCommuVo vo = getLogCommuDetailVo(messageId, messageFrom,
					startTime, endTime, result, hdlThread, level, remark);

			synchronized (SYN_CONTROL) {
				n = logForDbDetail(vo, level, dbHelper);
			}

		} catch (Exception e) {
			logger.error(" catch one one exception ", e);
		}
		return n;

	}





	public static int logForDbDetail(LogCommuVo vo) throws Exception {
		return insertDetail(vo);

	}

	public static int logForDbDetail(LogCommuVo vo, DbHelper dbHelper)
			throws Exception {
		return insertDetail(vo, dbHelper);

	}

	public static String getRemark(LogCommuVo vo) {
		if (vo.getRemark() == null || vo.getRemark().length() == 0) {
			return "";
		}
                return enLength(vo.getRemark(),200);
	}
        
        private static String enLength(String remark, int k) {
            String result = "";
            int n = 0;
            try {
                n = remark.getBytes("GBK").length;
                if(n>k){
                    byte[] ns = remark.getBytes("GBK");
                    byte[] tmp = new byte[k];
                    for(int i=0;i<k-1;i++){
                        tmp[i] = ns[i];
                    }
                    result = new String(tmp,"gbk");
                }else{
                    result = remark;
                }
            } catch (UnsupportedEncodingException ex) {
                logger.error(ex.getMessage());
            }finally{
                return result;
            }
        }

	public synchronized static int insertDetail(LogCommuVo vo) throws Exception {

		DbHelper dbHelper = null;
		String sql = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_log_commu(water_no,message_id,message_name,message_from,start_time,"
				+ "end_time,use_time,result,hdl_thread,sys_level,remark) "
				+ " values("+FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_log_commu.nextval,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?,?,?) ";

		int result = 0;

		try {

			dbHelper = new DbHelper("LogDbUtil",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			if (!dbHelper.isAvailableForConn()) {
				return -1;
			}
			Object[] values = { vo.getMessageId(), vo.getMessageName(),
					vo.getMessageFrom(), vo.getStartTime(), vo.getEndTime(),
					NumberUtil.getIntegerValue(vo.getUseTime(), 0),
					vo.getResult(), vo.getHdlThread(), vo.getSysLevel(),
					getRemark(vo) };

			result = dbHelper.executeUpdate(sql, values);

		} catch (Exception e) {
			PubUtil.handleException(e, logger);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		return result;
	}

	public synchronized static int insertDetail(LogCommuVo vo, DbHelper dbHelper)
			throws Exception {
		String sql = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_log_commu( water_no,message_id,message_name,message_from,start_time,"
				+ "end_time,use_time,result,hdl_thread,sys_level,remark) "
				+ " values(?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?,?,?) ";
		int result = 0;
		int waterNo = PubDbUtil.getTableSequence(FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_log_commu", dbHelper);
		Object[] values = { new Integer(waterNo), vo.getMessageId(),
				vo.getMessageName(), vo.getMessageFrom(), vo.getStartTime(),
				vo.getEndTime(),
				NumberUtil.getIntegerValue(vo.getUseTime(), 0), vo.getResult(),
				vo.getHdlThread(), vo.getSysLevel(), getRemark(vo) };

		if (!dbHelper.isAvailableForConn()) {
			return -1;
		}
		result = dbHelper.executeUpdate(sql, values);

		return result;
	}

	public static LogCommuVo getLogCommuDetailVo(String messageId,
			String messageFrom, long startTime, long endTime, String result,
			String hdlThread, String level) {
		LogCommuVo vo = new LogCommuVo();

		String messageName = getMessageName(messageId);
		vo.setMessageId(messageId);
		vo.setMessageName(messageName);
		vo.setMessageFrom(messageFrom);
		vo.setHdlThread(hdlThread);

		vo.setStartTime(DateHelper.datetimeToString(new Date(startTime)));
		vo.setEndTime(DateHelper.datetimeToString(new Date(endTime)));
		vo.setUseTime(getUseTime(startTime, endTime));

		vo.setResult(result);

		vo.setSysLevel(level);
		return vo;

	}

	public static LogCommuVo getLogCommuDetailVo(String messageId,
			String messageFrom, long startTime, long endTime, String result,
			String hdlThread, String level, String remark) {
		LogCommuVo vo = new LogCommuVo();

		String messageName = getMessageName(messageId);
		vo.setMessageId(messageId);
		vo.setMessageName(messageName);
		vo.setMessageFrom(messageFrom);
		vo.setHdlThread(hdlThread);

		vo.setStartTime(DateHelper.datetimeToString(new Date(startTime)));
		vo.setEndTime(DateHelper.datetimeToString(new Date(endTime)));
		vo.setUseTime(getUseTime(startTime, endTime));

		vo.setResult(result);
		vo.setRemark(remark);

		vo.setSysLevel(level);
		return vo;

	}

	public static int getReportSize(int reportSize) {
		int n = reportSize / 1024;
		if (n == 0) {
			return 1;
		}
		return n;
	}

	public static String getUseTime(long genStartTime, long genEndTime) {
		long useTime = genEndTime - genStartTime;
		useTime = useTime / 1000;
		return new Long(useTime).toString();
	}

	private static String getMessageName(String messageId) {

		for (int i = 0; i < messageIds.length; i++) {
			if (messageId.equals(messageIds[i])) {
				return messageNames[i];
			}
		}
		return "";
	}

	public static String getLevelText(String level) {
		for (int i = 0; i < levels.length; i++) {
			if (level.equals(levels[i])) {
				return levelTexts[i];
			}
		}
		return "";
	}
}
