package com.goldsign.commu.frame.thread;

import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.message.ConstructMessagePSD;
import java.util.List;

import com.goldsign.commu.app.message.ConstructMessageTDS;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.dao.FileLogDao;
import com.goldsign.commu.frame.handler.HandleBase;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.util.FileUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.RecvFileVo;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 票务接口文件下发、解析
 * 
 * @author zhangjh
 * 
 */
public class TKInterfaceThread implements Runnable {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(TKInterfaceThread.class.getName());
	public static boolean finish_flag = true;//
        public static boolean finish_flag_psd = true;//
        private int n = 0;

	@Override
	public void run() {
		try {
			// 票务文件下发
			down();
			// 票务文件解析(文件上传接收12消息下载)
			up();
			// sleepForNext();
		} catch (Exception e) {
			logger.error("票务文件下发或者解析入库出错", e);
		}

	}

	private void up() {
		logger.debug("票务文件解析任务开始...");
		FileLogDao dao = new FileLogDao();
		// 查询需要处理的文件
		List<RecvFileVo> list = dao.query();
		// 更新文件状态为正在处理
		List<RecvFileVo> list2 = dao.update(list);
		if (list2.isEmpty()) {
			// logger.info("没有需要处理的文件...");
			return;
		}
		// 将文件移动到handling目录
		move(list2);
		// 逐个处理文件
		handle(list2);
	}

	private void handle(List<RecvFileVo> list) {
		for (RecvFileVo vo : list) {
			String fileType = vo.getFileType();
			HandleBase hander;
			try {
				hander = (HandleBase) Class
						.forName(
								FrameTicketConstant.CLASS_HANDLE_FILE_PREFIX
										+ fileType).newInstance();
				HandleMessageBase msg = new HandleMessageBase();
				msg.setWaterNo(vo.getWaterNo());
				msg.setFileName(vo.getFileName());
				logger.info("FileName=="+vo.getFileName());
				msg.setHandlePath(vo.getHandlePath());
				msg.setPathHis(vo.getHisPath());
				msg.setPath(vo.getFilePath());
				msg.setPathError(vo.getErrorPath());
				msg.setTradType(fileType);
				hander.handleMessage(msg);
                                
                                //修改为数据库表记录序号
                                updateTkFileSeq(vo.getFileName(),fileType);
			} catch (Exception e) {
				logger.error("初始化处理类出现异常：", e);
			}

		}

	}

	/**
	 * 文件移动到‘handling’目录
	 * 
	 * @param list
	 */
	private void move(List<RecvFileVo> list) {
		for (RecvFileVo msg : list) {
			FileUtil.moveFile(msg.getFileName(), msg.getFilePath(),
					msg.getFileName(),
					msg.getFilePath() + "/" + msg.getHandlePath(),
					msg.getWaterNo());
		}

	}

	private void down() {
		try {
			ConstructMessageBase base = null;
                        //下发配票数据
			base = new ConstructMessageTDS();
			base.handle();
                        n++;
                        //每15分钟生成一次预制票交易文件
                        if(n==5){
                            base = new ConstructMessagePSD();
                            base.handle();
                            n=0;
                        }
		} catch (Exception e) {
			logger.info("生成文件出现异常", e);
		}

	}

	/**
	 * 休眠3分钟
	 */
	public void sleepForNext() {
		try {
			Thread.sleep(180000);
		} catch (InterruptedException ex) {
			logger.error("睡眠被打断", ex);
		}
	}

	/**
	 * 以下设置状态和获取状态的方法增加了同步
	 * 
	 * @return
	 */
	public static synchronized boolean isFinish_flag() {
		return finish_flag;
	}

	public static synchronized void setFinish_flag(boolean finishFlag) {
		finish_flag = finishFlag;
	}
        
	public static synchronized boolean isFinish_flag_psd() {
		return finish_flag_psd;
	}

	public static synchronized void setFinish_flag_psd(boolean finishFlag) {
		finish_flag_psd = finishFlag;
	}

        /*
         * 处理文件后，更新下载文件名序号到数据表
         */
        private void updateTkFileSeq(String fileName, String fileType) {
            DbHelper cmDbHelper = null;
            try {
                int s = 0;
                int len = fileName.length();
                s = Integer.parseInt(fileName.substring(len-3, len));
                cmDbHelper = new DbHelper("SeqDao",
                                FrameDBConstant.CM_DBCPHELPER.getConnection());
                SeqDao.getInstance().updateTkFileSeq(s, fileType, cmDbHelper);
				logger.info("updateTkFileSeq修改seq 为 "+ s);
            } catch (Exception e) {
                    logger.error("更改票库通讯接口文件名序号:", e);
            } finally {
                    PubUtil.finalProcess(cmDbHelper);
            }
        }

}
