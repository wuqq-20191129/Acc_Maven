package com.goldsign.commu.app.message;

import java.util.Vector;
import org.apache.log4j.Logger;
import com.goldsign.commu.frame.dao.DevParaVerDao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.DevParaVerInfoVo;
import com.goldsign.commu.frame.vo.DevParaVerInfosVo;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;

/**
 * LCC->ACC 车站设备软件版本查询结果报文
 * 
 * @author zhangjh
 */
public class Message05 extends MessageBase {

	private static Logger logger = Logger.getLogger(Message05.class.getName());
//	private static final byte[] SYNCONTROL = new byte[0];

	@Override
	public void run() throws Exception {
		String result = FrameLogConstant.RESULT_HDL_SUCESS;
		level = FrameLogConstant.LOG_LEVEL_INFO;
		try {
			hdlStartTime = System.currentTimeMillis();
			logger.info("--处理05消息开始--");
			process();
			logger.info("--处理05消息结束--");
			hdlEndTime = System.currentTimeMillis();
		} catch (Exception e) {
			result = FrameLogConstant.RESULT_HDL_FAIL;
			level = FrameLogConstant.LOG_LEVEL_ERROR;
			hdlEndTime = System.currentTimeMillis();
			remark = e.getMessage();
			throw e;
		} finally {// 记录处理日志
			LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_FLOW_ENTRY,
					messageFrom, hdlStartTime, hdlEndTime, result, threadNum,
					level, remark, getCmDbHelper());
		}
	}

	public void process() throws Exception {

		DevParaVerInfosVo vo;
		DevParaVerDao dao = new DevParaVerDao();

		try {
			String currentTod = getBcdString(2, 7);
			int count = getInt(9);
			int offset = 10;
			getCmDbHelper().setAutoCommit(false);
			String reportDate = getStandardDate(currentTod);
			logger.info("站点设备重复次数N:[" + count + "]");
			for (int i = 0; i < count; i++) {
				vo = new DevParaVerInfosVo();
				vo.setReportDate(reportDate);
				offset = devParaVerInfosVo(offset, vo);
				//modify by zhongzq 20190531
//				dao.writeInfo(getCmDbHelper(), vo);
				dao.writeInfoForBatch(getCmDbHelper(), vo);
			}
			getCmDbHelper().commitTran();
			getCmDbHelper().setAutoCommit(true);
		} catch (Exception e) {
			PubUtil.handleExceptionForTran(e, logger, getCmDbHelper());
		} finally{
                        getCmDbHelper().setAutoCommit(true);
                }

	}

	private int devParaVerInfosVo(int offset, DevParaVerInfosVo vo)
			throws CommuException {

		DevParaVerInfoVo ivo;
		Vector<DevParaVerInfoVo> v = new Vector<DevParaVerInfoVo>();

		String parmTypeId, versionCur, versionFur;
		String lineStation = getCharString(offset, 4);
		offset += 4;
		vo.setLineId(lineStation.substring(0, 2));
		vo.setStationId(lineStation.substring(2));

		vo.setDevTypeId(getCharString(offset, 2));
		offset += 2;
		vo.setDeviceId(getCharString(offset, 3));
		offset += 3;

		int count = getInt(offset);
		offset += 1;
		for (int i = 0; i < count; i++) {
			parmTypeId = getBcdString(offset, 2);
			offset += 2;
			versionCur = getBcdString(offset, 5);
//                        logger.warn(offset+":versionCur:"+versionCur);
			offset += 5;
			versionFur = getBcdString(offset, 5);
//                        logger.warn(offset+":versionFur:"+versionFur);
			offset += 5;
			ivo = getDevParaVerInfoVo(parmTypeId, versionCur,
					FrameCodeConstant.RECORD_FLAG_CURRENT);
			v.add(ivo);
			ivo = getDevParaVerInfoVo(parmTypeId, versionFur,
					FrameCodeConstant.RECORD_FLAG_FUTURE);
			v.add(ivo);
		}
		vo.setParams(v);

		return offset;
	}

	private DevParaVerInfoVo getDevParaVerInfoVo(String parmTypeId,
			String versionNo, String recordFlag) {
		DevParaVerInfoVo ivo = new DevParaVerInfoVo();
		ivo.setParmTypeId(parmTypeId);
		ivo.setVersionNo(versionNo);
		ivo.setRecordFlag(recordFlag);
		return ivo;
	}

	private String getStandardDate(String currentTod) {
		return currentTod.substring(0, 4) + "-" + currentTod.substring(4, 6)
				+ "-" + currentTod.substring(6, 8) + " "
				+ currentTod.substring(8, 10) + ":"
				+ currentTod.substring(10, 12) + ":"
				+ currentTod.substring(12, 14);
	}
}
