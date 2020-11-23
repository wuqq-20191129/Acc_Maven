package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.autodownload.DownloadHandlerBase;
import com.goldsign.commu.frame.constant.FrameParameterConstant;
import com.goldsign.commu.frame.dao.ParaAutoDWConfigDao;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.vo.ParaAutoDWConfigTimeVo;
import com.goldsign.commu.frame.vo.ParaAutoDWConfigVo;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Vector;

/**
 * 参数自动下发处理线程
 * 
 * @author hejj
 */
public class ParaAutoDWThread extends Thread {
	private static Logger logger = Logger.getLogger(ParaAutoDWThread.class
			.getName());

	public void run() {
		Vector<ParaAutoDWConfigVo> v;

		while (true) {
			try {
				v = getNeedDownloadPara();
				if (!v.isEmpty()) {
					downloadPara(v);
					logger.info("自动下发参数数量：" + v.size());
				}

				threadSleep();
			} catch (Exception e) {
				logger.info("捕获异常信息:", e);
			}

		}

	}

	private void threadSleep() {

		try {
			sleep(FrameParameterConstant.paraDownloadThreadSleepTime);
		} catch (NumberFormatException e) {
			logger.info("捕获异常信息:", e);
		} catch (InterruptedException e) {
			logger.info("捕获异常信息:", e);
		}

	}

	/**
	 * 获取需要下发的参数
	 * 
	 * @return
	 * @throws Exception
	 */
	private Vector<ParaAutoDWConfigVo> getNeedDownloadPara() throws Exception {
		ParaAutoDWConfigDao dao = new ParaAutoDWConfigDao();
		Vector<ParaAutoDWConfigVo> v = dao.getConfig();
		if (v.isEmpty()) {
			return new Vector<ParaAutoDWConfigVo>();
		}
		Vector<ParaAutoDWConfigVo> vRun = getDownloadPara(v);
		return vRun;
	}

	private boolean isTimeToRun(String cur, ParaAutoDWConfigVo vo) {
		String year = cur.substring(0, 4);
		String month = cur.substring(4, 6);
		String day = cur.substring(6, 8);
		String hour = cur.substring(8, 10);
		String min = cur.substring(10, 12);
		boolean isTimeForYear = isTimeToRunForCommon(year, vo.getYears());
		boolean isTimeForMon = isTimeToRunForCommon(month, vo.getMonths());
		boolean isTimeForDay = isTimeToRunForCommon(day, vo.getDays());
		boolean isTimeForHour = isTimeToRunForCommon(hour, vo.getHours());
		boolean isTimeForMin = isTimeToRunForCommon(min, vo.getMins());
		
		return isTimeForYear && isTimeForMon && isTimeForDay && isTimeForHour
				&& isTimeForMin;

	}

	private boolean isTimeToRunForCommon(String time,
			Vector<ParaAutoDWConfigTimeVo> vTime) {
		ParaAutoDWConfigTimeVo vo;
		int itime = Integer.parseInt(time);
		for (int i = 0; i < vTime.size(); i++) {
			vo = vTime.get(i);
			if (vo.getValueType().equals(
					FrameParameterConstant.paraDownloadTimeValueTypeAll)) {
				return true;
			}
			if (vo.getValueType().equals(
					FrameParameterConstant.paraDownloadTimeValueTypeRange)) {
				if (itime >= vo.getValueMin() && itime <= vo.getValueMax()) {
					return true;
				}
			}
			if (vo.getValueType().equals(
					FrameParameterConstant.paraDownloadTimeValueTypeSingle)) {
				if (itime == vo.getValueSinge()) {
					return true;
				}
			}
		}
		return false;

	}

	private Vector<ParaAutoDWConfigVo> getDownloadPara(
			Vector<ParaAutoDWConfigVo> paraAutoDWConfigVos) {
		ParaAutoDWConfigVo vo;
		// 当前时间
		String curTime = DateHelper.dateToString(new Date());
		Vector<ParaAutoDWConfigVo> vRun = new Vector<ParaAutoDWConfigVo>();
		for (int i = 0; i < paraAutoDWConfigVos.size(); i++) {
			vo = (ParaAutoDWConfigVo) paraAutoDWConfigVos.get(i);
			if (isTimeToRun(curTime, vo)) {
				vRun.add(vo);
			}

		}
		return vRun;
	}

	private void downloadPara(String paramTypeId) {
		DownloadHandlerBase dw;
		try {
			dw = (DownloadHandlerBase) Class.forName(
					FrameParameterConstant.paraDownloadClassPrefix
							+ paramTypeId).newInstance();
			dw.setParmTypeId(paramTypeId);
			dw.download();
		} catch (Exception ex) {
			logger.error("catch one exception",ex);
		}
	}

	private void downloadPara(Vector<ParaAutoDWConfigVo> v) {
		for (ParaAutoDWConfigVo vo : v) {
			downloadPara(vo.getParamTypeId());
		}
	}

}
