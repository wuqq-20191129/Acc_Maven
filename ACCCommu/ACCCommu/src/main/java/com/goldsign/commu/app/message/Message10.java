package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.DegradeModeRecordDAO;
import com.goldsign.commu.app.dao.Message08Dao;
import com.goldsign.commu.app.vo.DeviceStatus;
import com.goldsign.commu.frame.buffer.TccBuffer;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 设备状态变化信息
 * 
 * @author zhangjh
 */
public class Message10 extends Message08 {

	private static Logger logger = Logger.getLogger(Message10.class.getName());

	// private static final byte[] SYNCONTROL = new byte[0];

	@Override
	public void run() throws Exception {
		String result = FrameLogConstant.RESULT_HDL_SUCESS;
		level = FrameLogConstant.LOG_LEVEL_INFO;
		try {
			hdlStartTime = System.currentTimeMillis();
			logger.info("--处理10消息开始--");
			process();
			logger.info("--处理10消息结束--");
			hdlEndTime = System.currentTimeMillis();
		} catch (Exception e) {
			result = FrameLogConstant.RESULT_HDL_FAIL;
			hdlEndTime = System.currentTimeMillis();
			level = FrameLogConstant.LOG_LEVEL_ERROR;
			remark = e.getMessage();
			throw e;
		} finally {
			// 记录处理日志
			LogDbUtil.logForDbDetail(
					FrameLogConstant.MESSAGE_ID_DEV_STATUS_CHANGE, messageFrom,
					hdlStartTime, hdlEndTime, result, threadNum, level, remark,
					getCmDbHelper());
		}
	}

	@Override
	public void process() throws Exception {
		// logger.error(thisClassName + " started!");

		// synchronized (SYNCONTROL) {
		try {
			String currentTod = getBcdString(2, 7);
			logger.debug("currentTod:" + currentTod);
			statusDateTime = DateHelper.dateStrToSqlTimestamp(currentTod);
			logger.debug("statusDateTime:" + statusDateTime);
			statusDateTimeStr = currentTod;
			int deviceCount = 1;
			int offset = 9;
			boolean differTime = true;
			getDeviceStatus(deviceCount, offset, differTime);

			Message08Dao.updateCurrentStatus(getCmDbHelper(), currentStatusV);
			Message08Dao.insertHisStatus(getCmDbHelper(), hisStatusV);
            //add by zhongzq 用于tcc转发 20190605 生产者
            if (FrameCodeConstant.TCC_INTERFACE_USE_KEY.equals(FrameCodeConstant.TCC_INTERFACE_CONTROL)) {
                TccBuffer.TCC_MESSAGE_QUEUE.offer(this.data);
                Object[] paras = {"10", messageSequ};
                getCmDbHelper().executeUpdate(TccBuffer.logSql, paras);
            }
		} catch (SQLException e) {
			logger.error("数据库异常,错误代码" + e.getErrorCode() + " 消息", e);
			throw e;
		} catch (Exception e) {
			logger.error(thisClassName + " 处理异常! messageSequ:" + messageSequ
					+ ". ", e);
			throw e;
		}
		// }
		// logger.error(thisClassName + " ended!");

	}

	@Override
	protected void getDeviceStatus(int deviceCount, int offset,
			boolean differTime) throws Exception {
		// devices loop
		for (int h = 0; h < deviceCount; h++) {
			String lineid = getCharString(offset, 2);
			offset = offset + 2;
			String stationid = getCharString(offset, 2);
			offset = offset + 2;
			String devTypId = getCharString(offset, 2);
			offset = offset + 2;
			String devId = getCharString(offset, 3);
			offset = offset + 3;
			String operator = getBcdString(offset, 3);
			offset = offset + 3;
			int repeatCount = getInt(offset);
			offset = offset + 1;

			DeviceStatus currentds = new DeviceStatus();
			currentds.setStatusDatetime(statusDateTime);
			currentds.setStatusDatetimeStr(statusDateTimeStr);
			currentds.setLineId(lineid);
			currentds.setStationId(stationid);
			currentds.setDevTypeId(devTypId);
			currentds.setDeviceId(devId);
			currentds.setOperatorId(operator);
			currentds.setStatusId("");
			currentds.setStatusValue("");
			currentds.setAccStatusValue("");

			int highStatus = -1;
			String highStatusStr = "";
			Timestamp newStatusDateTime = new Timestamp(0);
			logger.info("repeatCount :" + repeatCount);
			// status loop
			for (int i = 0; i < repeatCount; i++) {
				String status = getBcdString(offset, 2);
				//状态标识+状态值
				logger.info("status:" + status);
				offset = offset + 2;

				Timestamp differStatusDateTime = null;
				// for message which status time differ from message time
				if (differTime) {
					String differTod = getBcdString(offset, 7);
					offset = offset + 7;
					differStatusDateTime = DateHelper
							.dateStrToSqlTimestamp(differTod);
					// 新增加
					currentds.setStatusDatetime(differStatusDateTime);
					currentds.setStatusDatetimeStr(differTod);
				}

				// 处理降级模式
				processDegradeMode(currentds, status);

				String iccsStatus = (String) FrameCodeConstant.DEV_STATUS_MAPPING
						.get(status);

				if (iccsStatus == null) {
//					 logger.error("No define for device status : " + status);
					iccsStatus = "0";
				}
				int iccsStatusInt = Integer.parseInt(iccsStatus);
				logger.debug("iccsStatusInt:" + iccsStatusInt);
				logger.debug("differTime:" + differTime);
				logger.debug("newStatusDateTime:" + newStatusDateTime);
				logger.debug("differStatusDateTime:" + differStatusDateTime);
				logger.debug("highStatus:" + highStatus);
				// compare and get the highest status(without differ time) or
				// newest status(with differ time)
				if (differTime) {
					if (null != differStatusDateTime
							&& differStatusDateTime.after(newStatusDateTime)) {
						newStatusDateTime = differStatusDateTime;
						highStatus = iccsStatusInt;
						highStatusStr = status;
					}
				} else {
					// logger.error("iccsstatus:"+iccsStatusInt);
					if (iccsStatusInt > highStatus) {
						highStatus = iccsStatusInt;
						highStatusStr = status;
					}
				}
				// if (iccsStatusInt > 0) {//正常设备状态也插入到设备状态表作为IQ数据源
				DeviceStatus statuss = new DeviceStatus();
				statuss.setLineId(currentds.getLineId());
				statuss.setStationId(currentds.getStationId());
				statuss.setDevTypeId(currentds.getDevTypeId());
				statuss.setDeviceId(currentds.getDeviceId());
				statuss.setOperatorId(currentds.getOperatorId());
				if (differTime) {
					statuss.setStatusDatetime(differStatusDateTime);
				} else {
					statuss.setStatusDatetime(currentds.getStatusDatetime());
				}
				statuss.setStatusId(status.substring(0, 3));
				statuss.setStatusValue(status.substring(3));
				statuss.setAccStatusValue("" + iccsStatusInt);
				hisStatusV.add(statuss);
				// }
			}

			currentds.setAccStatusValue("" + highStatus);
			// logger.error("highStatus set:"+highStatus);
			currentds.setStatusId(highStatusStr.substring(0, 3));
			currentds.setStatusValue(highStatusStr.substring(3, 4));
			if (differTime) {
				currentds.setStatusDatetime(newStatusDateTime);
			}
			currentStatusV.add(currentds);
		}
	}

	private Vector<String> getIpSend(String lineIdDegrade) {

		Vector<String> ipToSend = new Vector<String>();
		// 消息转发线路存放ipToSend
		for (Enumeration<String> e = FrameCodeConstant.ALL_LCC_IP.keys(); e
				.hasMoreElements();) {
			String lineId = e.nextElement();
			ipToSend.add(FrameCodeConstant.ALL_LCC_IP.get(lineId));
		}
		return ipToSend;

	}

	/**
	 * 
	 * @param ds
	 * @param status
	 *            Status ID(3) + status value(1)
	 * @throws Exception
	 */
	private void processDegradeMode(DeviceStatus ds, String status)
			throws Exception {
		try {
			String statusId = status.substring(0, 3).trim();
			String statusVal = status.substring(3).trim();
			int mode = Integer.parseInt(statusId);
			// 打印日志 debug
			print(ds, status);

			// 降级模式判断: 状态ID整型值:1-6 ;设备类型:SC(01) ;设备代码:000
			if (mode > 0 && mode < 7 && (ds.getDevTypeId().equals("01"))
					&& (ds.getDeviceId().equals("000"))) {

				Vector<String> ipToSend = getIpSend(ds.getLineId());

				String command = "0" + statusId;
				String flag = "";

				if ((statusVal).equals("1")) {
					flag = "01";
				} else if (statusVal.equals("0")) {
					flag = "02";
				}

				ds.setStatusId(statusId);
				if (statusVal.equals("1")) {
					// 降级模式开始
					if (DegradeModeRecordDAO.judgeDup(ds, flag)) {
						if (DegradeModeRecordDAO.judgeLastTime(ds, flag)) {
							logger.info("降级模式开始...");
							DegradeModeRecordDAO.recordStart(ds, ipToSend,
									command, flag);
						}
					}
				} else if (statusVal.equals("0")) {
					// degrade降级模式结束
					if (DegradeModeRecordDAO.judgeDup(ds, flag)) {
						logger.info("降级模式结束....");
						DegradeModeRecordDAO.recordEnd(ds, ipToSend, command,
								flag);

					}
				}

			}
		} catch (SQLException e) {
			logger.error("数据库异常,错误代码" + e.getErrorCode() + " 消息", e);
			throw e;
		} catch (Exception e) {
			logger.error(thisClassName + " processDegradeMode error! ", e);
			throw e;
		}
	}

	private void print(DeviceStatus ds, String status) {
		logger.debug("status is :" + status);
		logger.debug("deviceID:" + ds.getDeviceId());
		logger.debug("deviceTypeID:" + ds.getDevTypeId());
		logger.debug("LCCID:" + ds.getLineId());
		logger.debug("OperID:" + ds.getOperatorId());
		logger.debug("stationID:" + ds.getStationId());
		logger.debug("timeStr:" + ds.getStatusDatetimeStr());
		logger.debug("StatusID:" + ds.getStatusId());
		logger.debug("statusTime:" + ds.getStatusDatetime());
	}
}
