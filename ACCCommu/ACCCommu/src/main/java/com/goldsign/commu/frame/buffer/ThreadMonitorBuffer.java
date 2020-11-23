package com.goldsign.commu.frame.buffer;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.goldsign.commu.frame.constant.FrameThreadPoolConstant;
import com.goldsign.commu.frame.dao.ComThreadPoolMonitorDao;
import com.goldsign.commu.frame.manager.CommuThreadManager;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.util.SqlUtil;
import com.goldsign.commu.frame.util.ThreadUtil;
import com.goldsign.commu.frame.vo.SynchronizedControl;
import com.goldsign.commu.frame.vo.TMMonitorResult;
import com.goldsign.commu.frame.vo.TMThreadHandUpNumber;
import com.goldsign.commu.frame.vo.TMThreadMsg;
import com.goldsign.commu.frame.vo.TMThreadStatusVo;

/**
 * 
 * @author hejj
 */
public class ThreadMonitorBuffer {
	private static Logger logger = Logger.getLogger(ThreadMonitorBuffer.class
			.getName());
	public final static SynchronizedControl CONTROL = new SynchronizedControl();
	public final static SynchronizedControl CONTROL_HANDUP = new SynchronizedControl();
	// 线程状态缓存,键值为线程标识，值为状态等相关信息
	public static TreeMap<String, TMThreadStatusVo> BUFER_THREAD_STATUS = new TreeMap<String, TMThreadStatusVo>();
	// 线程挂起次数,键值为消息代码+线路，值为挂起次数相关信息
	public static TreeMap<TMThreadMsg, TMThreadHandUpNumber> BUFER_THREAD_HANDUP = new TreeMap<TMThreadMsg, TMThreadHandUpNumber>();

	private static boolean isSameMsgKey(TMThreadMsg tvo, TMThreadMsg vo) {
		if (tvo.getMsgId().equals(vo.getMsgId())
				&& tvo.getLineId().equals(vo.getLineId())
				&& tvo.getStationId().equals(vo.getStationId()))
			return true;
		return false;
	}

	private static boolean isExistedInBufferThreadHandup(TMThreadMsg vo) {
		if (ThreadMonitorBuffer.BUFER_THREAD_HANDUP.isEmpty())
			return false;
		Set set = ThreadMonitorBuffer.BUFER_THREAD_HANDUP.keySet();
		Iterator it = set.iterator();
		TMThreadMsg tvo;
		while (it.hasNext()) {
			tvo = (TMThreadMsg) it.next();

			if (ThreadMonitorBuffer.isSameMsgKey(tvo, vo)) {

				return true;
			}

		}
		// 不存在消息代码及线路代码相同键值

		return false;
	}

	private static void addHandUpCount(TMThreadMsg vo) {
		Set set = ThreadMonitorBuffer.BUFER_THREAD_HANDUP.keySet();
		Iterator it = set.iterator();
		TMThreadMsg tvo;
		TMThreadHandUpNumber nvo;
		while (it.hasNext()) {
			tvo = (TMThreadMsg) it.next();
			if (ThreadMonitorBuffer.isSameMsgKey(tvo, vo)) {

				nvo = (TMThreadHandUpNumber) ThreadMonitorBuffer.BUFER_THREAD_HANDUP
						.get(tvo);
				nvo.setHandupNumber(nvo.getHandupNumber() + 1);
				break;
			}
		}
	}

	public static void setBufferThreadHandup(TMThreadMsg vo) {
		synchronized (ThreadMonitorBuffer.CONTROL_HANDUP) {
			if (ThreadMonitorBuffer.isExistedInBufferThreadHandup(vo))
				ThreadMonitorBuffer.addHandUpCount(vo);
			else {
				ThreadMonitorBuffer.BUFER_THREAD_HANDUP.put(vo,
						new TMThreadHandUpNumber(1));
			}

		}

	}

	public static Vector<TMThreadMsg> getNeedDelMsgs() {
		Vector<TMThreadMsg> v = new Vector<TMThreadMsg>();
		TMThreadMsg key;
		TMThreadHandUpNumber value;
		synchronized (ThreadMonitorBuffer.CONTROL_HANDUP) {
			Set keys = ThreadMonitorBuffer.BUFER_THREAD_HANDUP.keySet();
			Iterator it = keys.iterator();
			while (it.hasNext()) {
				key = (TMThreadMsg) it.next();
				value = ThreadMonitorBuffer.BUFER_THREAD_HANDUP.get(key);
				if (value.getHandupNumber() >= FrameThreadPoolConstant.ThreadMsgHandUpMaxNumberAllow) {
					v.add(key);
					ThreadUtil.writeInfoForMsgHandupNumber(key.getMsgId(),
							value.getHandupNumber());// 记录阻塞消息发生的次数

					value.setHandupNumber(0);// 重新初始化

					logger.error("需从线程消息队列删除消息,消息代码：" + key.getMsgId()
							+ "消息线路：" + key.getLineId() + "消息车站："
							+ key.getStationId());
				}
			}
			return v;

		}

	}

	private static boolean isExists(String threadId) {
		if (threadId == null || threadId.length() == 0) {
			return false;
		}
		if (ThreadMonitorBuffer.BUFER_THREAD_STATUS.containsKey(threadId)) {
			return true;
		}
		return false;
	}

	private static void copyVoStatus(TMThreadStatusVo vo, TMThreadStatusVo svo) {
		// svo.setThreadId(vo.getThreadId());
		// svo.setThreadName(vo.getThreadName());
		SqlUtil util = new SqlUtil();
		if (util.isValidValue(vo.getThreadName())) {
			svo.setThreadName(vo.getThreadName());
		}

		if (util.isValidValue(vo.getThreadStatus())) {
			svo.setThreadStatus(vo.getThreadStatus());
		}
		if (util.isValidValue(vo.getMsgId())) {
			svo.setMsgId(vo.getMsgId());
		}
		if (util.isValidValue(vo.getMsgName())) {
			svo.setMsgName(vo.getMsgName());
		}
		if (util.isValidValue(vo.getHdlTimeStart())) {
			svo.setHdlTimeStart(vo.getHdlTimeStart());
		}
		if (util.isValidValue(vo.getHdlEndTime())) {
			svo.setHdlEndTime(vo.getHdlEndTime());
		}
		// svo.setRemark(vo.getRemark());

	}

	/**
	 * 记录线程状态
	 * 
	 * @param vo
	 * @return
	 */
	public static int updateThreadStatus(TMThreadStatusVo vo) {
		String threadId;
		TMThreadStatusVo svo;
		synchronized (ThreadMonitorBuffer.CONTROL) {
			threadId = vo.getThreadId();
			// 更新缓存的线程状态
			if (ThreadMonitorBuffer.isExists(threadId)) {
				svo = (TMThreadStatusVo) ThreadMonitorBuffer.BUFER_THREAD_STATUS
						.get(threadId);
				ThreadMonitorBuffer.copyVoStatus(vo, svo);
			} else {// 添加缓存的线程状态
				ThreadMonitorBuffer.BUFER_THREAD_STATUS.put(threadId, vo);
			}
			// 更新或添加数据库的线程状态
			ThreadMonitorBuffer.updateThreadStatusInDb(vo);
			return 1;
		}

	}

	private static int updateThreadStatusInDb(TMThreadStatusVo vo) {
		int n = 0;
		try {
			n = ComThreadPoolMonitorDao.updateStatus(vo);
		} catch (Exception ex) {
			PubUtil.handleExceptionNoThrow(ex, logger);
		}
		return n;

	}

	private static boolean isNotFinish(String hdlEndTime) {
		return hdlEndTime == null
				|| hdlEndTime.length() == 0
				|| hdlEndTime
						.equals(FrameThreadPoolConstant.ThreadMsgHandleTimeEmpty);
	}

	/**
	 * 获取各种消息最大处理时间
	 * 
	 * @param msgId
	 * @return
	 */
	private static long getMaxHandleTime(String msgId) {
		if (FrameThreadPoolConstant.ThreadMsgHandleMaxTime.isEmpty()) {
			return FrameThreadPoolConstant.ThreadMsgHandleMaxTimeDefault;
		}
		Set set = FrameThreadPoolConstant.ThreadMsgHandleMaxTime.keySet();
		Iterator it = set.iterator();
		String key;
		Long value;
		while (it.hasNext()) {
			key = (String) it.next();
			value = FrameThreadPoolConstant.ThreadMsgHandleMaxTime.get(key);
			if (key.equals(msgId)) {
				return value.longValue();
			}
		}
		return FrameThreadPoolConstant.ThreadMsgHandleMaxTimeDefault;

	}

	/**
	 * 判断当前线程的运行时间是否超过配置值
	 * 
	 * @param vo
	 * @return
	 * @throws ParseException
	 */
	private static boolean isOverMaxHandleTime(TMThreadStatusVo vo)
			throws ParseException {
		long maxTime = ThreadMonitorBuffer.getMaxHandleTime(vo.getMsgId());
		maxTime = maxTime * 1000;
		long hdlTime = DateHelper.getDiffer(vo.getHdlTimeStart(), new Date());
		// logger.info("当前线程"+vo.getThreadId()+"已处理消息时间:"+hdlTime+"，最大允许的处理时间为:"+maxTime);
		return hdlTime > maxTime;
	}

	/**
	 * 获取当前线程运行状态
	 * 
	 * @param vo
	 * @return
	 * @throws ParseException
	 */
	private static String getStatusInFact(TMThreadStatusVo vo)
			throws ParseException {
		String status = vo.getThreadStatus();
		// logger.info("当前处理线程"+vo.getThreadId()+"的缓存状态为："+status);
		// 线程为正在处理才进行判断，其余返回原状态
		if (!status.equals(FrameThreadPoolConstant.ThreadStatusHandling)) {
			return status;
		}
		// 通过线程处理消息是否有结束时间，如没有，处理时间是否超过设定值，判断线程是否挂起，如挂起，改变线程的状态为挂起
		// String hdlStartTime = vo.getHdlTimeStart();
		String hdlEndTime = vo.getHdlEndTime();
		if (ThreadMonitorBuffer.isNotFinish(hdlEndTime)) {
			if (ThreadMonitorBuffer.isOverMaxHandleTime(vo)) {
				logger.error("线程" + vo.getThreadId() + "处理消息" + vo.getMsgId()
						+ "时间超过设定的最大值，状态将改为挂起.");
				return FrameThreadPoolConstant.ThreadStatusHandup;
			}
		}
		return status;
	}

	/**
	 * 判断当前线程的运行状态是否正常
	 * 
	 * @param vo
	 * @return
	 */
	private static boolean isLegalStatusAndChangeStatus(TMThreadStatusVo vo) {
		String status = vo.getThreadStatus();
		if (status == null || status.length() == 0) {
			return false;
		}
		try {
			status = ThreadMonitorBuffer.getStatusInFact(vo);
			if (status.equals(FrameThreadPoolConstant.ThreadStatusHandup)) {
				vo.setThreadStatus(status);
				return false;
			}
			return true;
		} catch (ParseException ex) {
			return true;
		}
	}
	public static TMMonitorResult getMonitorResult() {
		TMThreadStatusVo vo;
		TMMonitorResult mr = new TMMonitorResult();
		mr.setExistedFailureThread(false);
		synchronized (ThreadMonitorBuffer.CONTROL) {
			if (ThreadMonitorBuffer.BUFER_THREAD_STATUS.isEmpty()) {
				// mr.setExistedFailureThread(false);
				// logger.info("线程池状态为空");
				return mr;
			}
			CommuThreadManager.synAllThreads();
			// logger.error("线程状态判断,设置线程同步锁");
			Collection c = ThreadMonitorBuffer.BUFER_THREAD_STATUS.values();
			Iterator it = c.iterator();
			// 获取所有挂起状态
			while (it.hasNext()) {
				vo = (TMThreadStatusVo) it.next();
				// 判断状态是否为非法，主要是否挂起，如果是设置状态为挂起
				if (!ThreadMonitorBuffer.isLegalStatusAndChangeStatus(vo)) {
					mr.setExistedFailureThread(true);
					mr.setVos(vo);
				}
			}
			if (!mr.isExistedFailureThread()) {
				CommuThreadManager.synReleaseAllThreads();
				// logger.error("线程状态正常,释放线程同步锁");
			}

		}

		return mr;

	}

}
