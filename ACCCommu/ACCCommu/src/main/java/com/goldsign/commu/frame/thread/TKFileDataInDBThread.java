package com.goldsign.commu.frame.thread;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.dao.BaseDAO;
import com.goldsign.commu.frame.handler.HandleBase;
import com.goldsign.commu.frame.message.HandleMessageBase;

/**
 * 票务数据批量入库定时任务
 * 
 * @author zhangjh
 * 
 */
public class TKFileDataInDBThread implements Runnable {

	private static Logger logger = Logger.getLogger(TKFileDataInDBThread.class);

	@Override
	public void run() {
		HandleMessageBase handlingMsg = null;
		try {
			// 获取需要处理的消息
			handlingMsg = HandleBase.MSG_BUFFER.getHandingMsg();

			if (handlingMsg == null) {
				logger.debug(Thread.currentThread().getName() + "没有需要处理的消息");
				return;
			}
			handlingMsg.setFinished(FrameTicketConstant.FILE_IS_HANDLEING);// 正在处理
			BaseDAO baseDao = (BaseDAO) Class.forName(
					FrameTicketConstant.CLASS_HANDLE_DAO_PREFIX
							+ handlingMsg.getTradType() + "Dao").newInstance();
			baseDao.batchInsert(handlingMsg);

			handlingMsg.setFinished(FrameTicketConstant.FILE_IS_HANDLED);// 设置消息处理完成标志

			// this.isHandling = false;
//			Thread.sleep(300000);

		}  catch (IllegalArgumentException e) {
			logger.error("批量插入数据出现异常:", e);
			if (handlingMsg != null) {
				handlingMsg.setFinished(FrameTicketConstant.FILE_IN_DB_ERROR);// 入库出现异常
			}
		} catch (IllegalStateException e) {
			logger.error("批量插入数据出现异常:", e);
			if (handlingMsg != null) {
				handlingMsg.setFinished(FrameTicketConstant.FILE_IN_DB_ERROR);// 入库出现异常
			}
		} catch (SQLException e) {
			logger.error("批量插入数据出现异常:", e);
			if (handlingMsg != null) {
				handlingMsg.setFinished(FrameTicketConstant.FILE_IN_DB_ERROR);// 入库出现异常
			}
		} catch (Exception e) {
			// CommuThreadLogUtil.writeErrHandleMsgToFile(this.getName(),
			// (Vector) handlingMsg.getReadResult());
			logger.error(e.getMessage() + "  "
					+ Thread.currentThread().getName() + ":消息缓存未处理消息为"
					+ HandleBase.MSG_BUFFER.getMsgs().size(), e);

		}

	}

}
