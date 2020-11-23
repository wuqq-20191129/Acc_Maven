package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.ParaDistributeDtlDao;
import com.goldsign.commu.frame.dao.ParaVerDao;
import com.goldsign.commu.frame.exception.ParameterException;
import com.goldsign.commu.app.message.ConstructMessage02;
import com.goldsign.commu.frame.parameter.ParameterBase;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.util.ParameterSynUtil;
import com.goldsign.commu.frame.vo.ParaDistributeDtl;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.commu.frame.vo.ParaInformDtl;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 参数下发线程 1,读取参数信息 2,生成参数文件 2,以02消息下发各车站
 * 
 * @author hejj
 */
public class ParameterDistributeThread implements Runnable {

	private final static String CLASSPREFIX = "com.goldsign.commu.app.parameter.Parameter";
	private final static String PRM = "PRM";
	private static Logger logger = Logger
			.getLogger(ParameterDistributeThread.class.getName());
	/**
	 * 日志记录使用
	 */
	private long hdlStartTime; // 处理的起始时间
	private long hdlEndTime;// 处理的结束时间

	@Override
	public void run() {
		while (true) {
			try {
				this.hdlStartTime = System.currentTimeMillis();

				ParaDistributeDtlDao dao = new ParaDistributeDtlDao();
				// 从下发队列中查找是否有需要下发的参数
				ParaDistributeDtl dtl = dao.getOneParaDistribute();
				if (dtl != null) {
					// logger.info("ParameterDistribute.run started! ");
					// 生成参数文件
					boolean result = genParameterFiles(dtl);

					if (result) {

						informLcc(dtl);// 通知LCC有参数需下载
					}

					this.hdlEndTime = System.currentTimeMillis();
					// 记录日志
					LogDbUtil.logForDbDetail(
							FrameLogConstant.MESSAGE_ID_PARAM_DISTRIBUTE, "",
							this.hdlStartTime, this.hdlEndTime,
							FrameLogConstant.RESULT_HDL_SUCESS, Thread
									.currentThread().getName(),
							FrameLogConstant.LOG_LEVEL_INFO, "参数下发");
				}
			} catch (Exception e) {
				logger.error("ParameterDistribute.run error - " + e);
				this.hdlEndTime = System.currentTimeMillis();
				// 记录日志
				LogDbUtil.logForDbDetail(
						FrameLogConstant.MESSAGE_ID_PARAM_DISTRIBUTE, "",
						this.hdlStartTime, this.hdlEndTime,
						FrameLogConstant.RESULT_HDL_FAIL, Thread
								.currentThread().getName(),
						FrameLogConstant.LOG_LEVEL_ERROR, e.getMessage());
			} finally {
				try {
					Thread.sleep(FrameCodeConstant.parmDstrbInterval);
				} catch (Exception e) {
					logger.error("Parameter distribute.run sleep error - " + e);
				}
			}
		}
	}

	private boolean  genParameterFiles(ParaDistributeDtl dtl)
			throws ParameterException {
		Vector<ParaGenDtl> v = dtl.getParaGenDtl();
		Vector<ParaGenDtl> vHandled = new Vector<ParaGenDtl>();
		Iterator it = v.iterator();
		String failFileName = "";

		ParameterSynUtil util = new ParameterSynUtil();
		boolean result = true;
		ParaGenDtl paraGenDtl = null;
		int bNum = v.size();
		try {
			clearTempDir();
			// 逐个生成所有参数文件

			while (it.hasNext()) {
				paraGenDtl = (ParaGenDtl) (it.next());
				if(paraGenDtl.getParmTypeId().substring(0,2).equals("06")) {
					if (!isBlackListDistri()) {
						//v.removeElement(paraGenDtl);
						it.remove();
						//updateParaDistribute(dtl.getWaterNo(), "3", "");
						continue;
					}
				}
				vHandled.add(paraGenDtl);// 正在处理的参数

				String parmClass = CLASSPREFIX + paraGenDtl.getParmTypeId();
				// logger.info("Parameter class" +
				// parmClass.substring(44) + " being invoked! ");
				ParameterBase parm = (ParameterBase) Class.forName(parmClass)
						.newInstance();
				// parm.init(ApplicationConstant.OTHER_DBCPHELPER, paraGenDtl);
				parm.init(FrameDBConstant.OP_DBCPHELPER, paraGenDtl);
				result = parm.formParaFile();
				parm.release();

				// 参数文件校验包括记录数、文件大小 add by hejj 2012-08-28
				// 参数文件校验通过更新参数同步表

				/*
				 * if (util.checkParaFile(paraGenDtl.getParmTypeId(),
				 * paraGenDtl.getVerType(), paraGenDtl.getVerNum(),
				 * parm.getDbRecordNum())) {
				 * util.updateParaVerSyn(paraGenDtl.getParmTypeId(),
				 * paraGenDtl.getVerNum(), paraGenDtl.getVerType()); } else
				 * {//参数文件校验不通过 result = false; }
				 */

				if (util.isNeedToCheckFile(paraGenDtl.getParmTypeId())) {
					// 参数文件校验包括记录数、文件大小 modify by hejj 2012-09-12
					if (!util.checkParaFile(paraGenDtl.getParmTypeId(),
							paraGenDtl.getVerType(), paraGenDtl.getVerNum(),
							parm.getDbRecordNum())) {
						result = false;
						break;// 有错误文件，不再继续
					}
				}

			}
			if(v.isEmpty()){
				logger.info("此次下发除黑名单外，不包含其他参数，且不在黑名单下发时间段！！");
				updateParaDistribute(dtl.getWaterNo(), "3", "");
				return false;
			}
			//dtl.setParaGenDtl(v);

			if (!result) {
				// 删除已生成的参数文件 modify by hejj 2012-09-12
				util.deleteFilesForError(FrameCodeConstant.parmDstrbPath,
						vHandled);
				failFileName = PRM + "." + paraGenDtl.getParmTypeId() + "."
						+ paraGenDtl.getVerNum().substring(0, 8) + "."
						+ paraGenDtl.getVerNum().substring(8, 10);
				throw new ParameterException("生成参数文件失败!参数文件名 : " + failFileName);
			} else {
                                //磁悬浮接入，添加line_id字段 20151217 by lindaquan
                                Vector<ParaInformDtl> lineStationsV = dtl.getParaInformDtl();
                            
				// 所有参数文件校验通过更新参数同步表 modify by hejj 2012-09-12
				util.updateParaVerSynForAll(v,lineStationsV);
			}

			// copy generated parm files from temp dir to parm distribution dir

			copyParmFiles();
			// 更新本次所有下发参数的完成状态
			if(v.size()<bNum){
				//此次下发 未下发黑名单参数
				updateParaDistribute(dtl.getWaterNo(), "3", "");
				//updateParaInform(dtl.getWaterNo(),"5");
			}else{
				updateParaDistribute(dtl.getWaterNo(), "2", "");
				//updateParaInform(dtl.getWaterNo(),"1");
			}
			// 更新本次所有下发参数的版本信息
			updateParaVer(v);
			//更新本次所有下发参数文件生成明细标志
			updateParaGenDtl(v);
		} catch (ParameterException e) {
			logger.error("生成参数文件失败!", e);
			updateParaDistribute(dtl.getWaterNo(), "1", failFileName);
			return false;
		} catch (Exception e) {
			logger.error("生成参数文件失败!" + e);
			updateParaDistribute(dtl.getWaterNo(), "1", "");
			return false;
		}
		return true;
	}

	//add by wuqq 黑名单不在下发时间更新通知标志
	//private void updateParaInform(int waterNo, String result) {
	//	try {
	//		ParaDistributeDtlDao dao = new ParaDistributeDtlDao();
	//		dao.updateParaInform(waterNo, result);
	//	} catch (Exception e) {
	//		logger.error("updateParaInform error  " + e);
	//	}
	//
	//}
	private void updateParaDistribute(int waterNo, String result, String memo) {
		try {
			ParaDistributeDtlDao dao = new ParaDistributeDtlDao();
			dao.updateOneParaDistribute(waterNo, result, memo);
		} catch (Exception e) {
			logger.error("updateParaDistribute error  " + e);
		}

	}

	private void clearTempDir() throws Exception {
	}

	private void copyParmFiles() throws Exception {
	}

	private void updateParaVer(Vector v) throws Exception {

		Iterator it = v.iterator();

		while (it.hasNext()) {
			ParaVerDao dao = new ParaVerDao();
			ParaGenDtl paraGenDtl = (ParaGenDtl) (it.next());
			dao.updateDistributeTimes(paraGenDtl.getVerNum(),
					paraGenDtl.getParmTypeId(), paraGenDtl.getVerType());
		}
	}

	private void informLcc(ParaDistributeDtl dtl) {
		ConstructMessage02 msg = new ConstructMessage02();
		try {
			msg.constructAndSend(dtl.getParaInformDtl(), dtl.getParaGenDtl());
		} catch (Exception e) {
			logger.error(e);
		}
	}

	////add by wuqq 20201009 黑名单下发时间控制 增加下发标志定义 3(运营期间不下发黑名单)
	//public boolean isBlackListDistri(){
	//	boolean result =false;
	//	String current = DateHelper.dateToString(new Date());
	//	logger.info("测试时间============"+current);
	//	// HHMM
	//	String curTime = current.substring(8, 12);
	//	if(Integer.parseInt(FrameCodeConstant.BLACKDISTRI_BEGIN)>Integer.parseInt(FrameCodeConstant.BLACKDISTRI_END)){
	//		if(Integer.parseInt(curTime)>=Integer.parseInt(FrameCodeConstant.BLACKDISTRI_BEGIN) || Integer.parseInt(curTime)<Integer.parseInt(FrameCodeConstant.BLACKDISTRI_END) ){
	//			result = true;
	//		}
	//	}else{
	//		if(Integer.parseInt(curTime)>=Integer.parseInt(FrameCodeConstant.BLACKDISTRI_BEGIN) && Integer.parseInt(curTime)<Integer.parseInt(FrameCodeConstant.BLACKDISTRI_END)){
	//			result = true;
	//		}
	//	}
	//	return result;
	//}

	private boolean isBlackListDistri() {
		boolean result = false;
		try {
			ParaDistributeDtlDao dao = new ParaDistributeDtlDao();
			result = dao.isBlackListDistri();
		} catch (Exception e) {
			logger.error("isBlackListDistri error  " + e);
		}
		return result;
	}

	private void updateParaGenDtl(Vector v) throws Exception {

		Iterator it = v.iterator();

		while (it.hasNext()) {
			ParaDistributeDtlDao dao = new ParaDistributeDtlDao();
			ParaGenDtl paraGenDtl = (ParaGenDtl) (it.next());
			dao.updateParaGenDtl(paraGenDtl.getWaterNo(),paraGenDtl.getParmTypeId(), "1");
		}
	}
}
