package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.LogLevelDao;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.vo.SysLogLevelVo;
import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class CommuLogLevelThread extends Thread {

	private static Logger logger = Logger.getLogger(CommuLogLevelThread.class
			.getName());

	public CommuLogLevelThread() {
		super();
	}

	public void run() {

		while (true) {
			try {
				this.setCurrentLogLevel();
				this.threadSleep();
			} catch (Exception e) {
				logger.warn("异常：", e);
			}

		}

	}

	public void runOne() {
		this.setCurrentLogLevel();

	}

	public void setCurrentLogLevel() {
		LogLevelDao dao = new LogLevelDao();
		SysLogLevelVo level = dao.getLogLevel();
		if (level == null) {
			FrameLogConstant.LOG_LEVEL_CURRENT = FrameLogConstant.LOG_LEVEL_INFO;
			FrameLogConstant.LOG_LEVEL_TEXT_CURRENT = FrameLogConstant.LOG_LEVEL_INFO_TEXT;

		} else {
			FrameLogConstant.LOG_LEVEL_CURRENT = level.getSysLevel();
			FrameLogConstant.LOG_LEVEL_TEXT_CURRENT = LogDbUtil
					.getLevelText(level.getSysLevel());
		}
		logger.debug("设置日志级别为：" + FrameLogConstant.LOG_LEVEL_CURRENT);

	}

	private void threadSleep() {

		try {
			sleep(FrameLogConstant.threadLogLevel);
		} catch (NumberFormatException e) {
			logger.error("catch one exception", e);
		} catch (InterruptedException e) {
			logger.error("catch one exception", e);
		}

	}
}
