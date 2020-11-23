/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.download;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.ParaAutoDWSamListDao;
import com.goldsign.commu.frame.autodownload.DownloadHandlerBase;

/**
 * 
 * @author hejj
 */
public class DownloadHandler0203 extends DownloadHandlerBase {
	private static Logger logger = Logger.getLogger(DownloadHandler0203.class
			.getName());

	@Override
	public void download() {
		try {
			// if (this.isNeedDownload()) {
			this.downloadSamList();
			logger.info("成功自动下发SAM卡参数");
			// }

		} catch (Exception ex) {
			logger.error("自动下发SAM卡参数失败", ex);
		}

	}

	private void downloadSamList() throws Exception {
		ParaAutoDWSamListDao sdao = new ParaAutoDWSamListDao();
		sdao.download();
	}

	// private boolean isNeedDownload() throws Exception {
	// ParaAutoDWConfigDao dao = new ParaAutoDWConfigDao();
	// ParaAutoDWSamListDao sdao = new ParaAutoDWSamListDao();
	// int minNum = dao.getConfigNum(this.paramTypeId);
	// int curNum = sdao.getNumNewAdd();
	// if (curNum >= minNum)
	// return true;
	// logger.info("无需下发SAM参数，新增参数记录数量：" + curNum);
	// return false;
	//
	// }

}
