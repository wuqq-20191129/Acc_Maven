package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.buffer.PubBuffer;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.MessageUtil;
import com.goldsign.commu.frame.vo.CurrentDate;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 清理缓存
 * 
 * @author hejj
 */
public class CommuBufferClearThread extends Thread {
	private static Logger logger = Logger
			.getLogger(CommuBufferClearThread.class.getName());
	private CurrentDate curDate = new CurrentDate();

	public CommuBufferClearThread() {
		super();
	}

	@Override
	public void run() {
		Vector<String> runtime = null;

		while (true) {
			try {
				// 每天获取一次运行时间
				if (isNewDate(curDate)) {// 是否新的一天，每天在固定的时间点上仅运行一次
					runtime = getRuntime();// 获取每天运行的时间点
				}
				// 支持多个运行时间点

				// 当前时间大于运行时间点，运行完后删除当前运行时间点，如果有第二个时间点，在第二个时间点后再运行

				if (isTimeToRun(runtime))
					clearBuffer();// 清理缓存

				threadSleep();
			} catch (Exception e) {
				logger.error("catch one exception", e);
			}

		}

	}

	private void threadSleep() {

		try {
			sleep(FrameCodeConstant.BUFFERCLEAR_SLEEPTIME);
		} catch (NumberFormatException e) {
			logger.error("catch one exception", e);
		} catch (InterruptedException e) {
			logger.error("catch one exception", e);
		}

	}

	private Vector<String> getRuntime() {
		Vector<String> v = new Vector<String>();
		v.add(FrameCodeConstant.BUFFERCLEAR_RUN_TIME);
		return v;
	}

	public boolean isTimeToRun(Vector<String> runtime) {

		String currentHour = DateHelper.curentDateToHHMM();
		String tmp;
		for (int i = 0; i < runtime.size(); i++) {
			tmp = (String) runtime.get(i);
			if (currentHour.compareTo(tmp) >= 0) {
				runtime.remove(i);

				return true;
			}
		}

		return false;
	}

	private void clearBuffer() {

		boolean isClear = false;
		logger.info("进站缓存清理开始...");
		if (MessageUtil
				.decreaseBufferKeepCurrent(PubBuffer.bufferFlowMinFiveEntry)) {
			isClear = true;
			logger.error("进站缓存清理完后，缓存个数："
					+ PubBuffer.bufferFlowMinFiveEntry.size());
		}
		logger.info("出站缓存清理开始...");
		if (MessageUtil
				.decreaseBufferKeepCurrent(PubBuffer.bufferFlowMinFiveExit)) {
			isClear = true;
			logger.error("出站缓存清理完后，缓存个数："
					+ PubBuffer.bufferFlowMinFiveExit.size());
		}

		if (isClear) {
			logger.error("缓存清理完后调用系统内存回收功能");
			System.gc();
			System.runFinalization();
		}

	}

	private boolean isNewDate(CurrentDate curDate) {
		if (curDate.getCurDate() == null || curDate.getCurDate().length() == 0) {
			curDate.setCurDate(DateHelper.datetimeToStringOnlyDateF(new Date()));
			return true;
		}
		String tmp = DateHelper.datetimeToStringOnlyDateF(new Date());

		if (tmp.equals(curDate.getCurDate())) {
			return false;
		}

		curDate.setCurDate(tmp);
		return true;

	}

}
