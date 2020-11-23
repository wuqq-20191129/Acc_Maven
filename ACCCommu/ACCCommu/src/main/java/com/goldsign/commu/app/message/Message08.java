package com.goldsign.commu.app.message;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import com.goldsign.commu.frame.buffer.TccBuffer;
import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.Message08Dao;
import com.goldsign.commu.app.vo.DeviceStatus;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;

/**
 * 全部设备状态信息
 * 
 * @author zhangjh
 */
public class Message08 extends MessageBase {

	protected Vector<DeviceStatus> currentStatusV = new Vector<DeviceStatus>();
	protected Vector<DeviceStatus> hisStatusV = new Vector<DeviceStatus>();
	protected Timestamp statusDateTime;
	protected String statusDateTimeStr;
	// private static final byte[] SYNCONTROL = new byte[0];
	private static Logger logger = Logger.getLogger(Message08.class.getName());

	@Override
	public void run() throws Exception {
		String result = FrameLogConstant.RESULT_HDL_SUCESS;
		this.level = FrameLogConstant.LOG_LEVEL_INFO;
		try {
			this.hdlStartTime = System.currentTimeMillis();
			logger.info("--处理08消息开始--");
			this.process();
			logger.info("--处理08消息结束--");
			this.hdlEndTime = System.currentTimeMillis();
		} catch (Exception e) {
			result = FrameLogConstant.RESULT_HDL_FAIL;
			this.hdlEndTime = System.currentTimeMillis();
			this.level = FrameLogConstant.LOG_LEVEL_ERROR;
			this.remark = e.getMessage();
			throw e;
		} finally {// 记录处理日志
			LogDbUtil.logForDbDetail(
					FrameLogConstant.MESSAGE_ID_DEV_STATUS_ALL,
					this.messageFrom, this.hdlStartTime, this.hdlEndTime,
					result, this.threadNum, this.level, this.remark, this.getCmDbHelper());
		}
	}

	public void process() throws Exception {

		// synchronized (SYNCONTROL) {
		try {
			String currentTod = getBcdString(2, 7);
			statusDateTime = DateHelper.dateStrToSqlTimestamp(currentTod);
			statusDateTimeStr = currentTod;
			int deviceCount = getInt(9);
			getDeviceStatus(deviceCount, 10, false);

			Message08Dao.updateCurrentStatus(getCmDbHelper(), currentStatusV);
			Message08Dao.insertHisStatus(getCmDbHelper(), hisStatusV);
			//add by zhongzq 用于tcc转发 20190605 生产者
			if (FrameCodeConstant.TCC_INTERFACE_USE_KEY.equals(FrameCodeConstant.TCC_INTERFACE_CONTROL)) {
				TccBuffer.TCC_MESSAGE_QUEUE.offer(this.data);
                getCharString(9, 9);
                Object[] paras = {"08", messageSequ};
                getCmDbHelper().executeUpdate(TccBuffer.logSql, paras);
			} 
		} catch (SQLException e) {
			logger.info("数据库异常,错误代码" + e.getErrorCode() + " 消息", e);
			throw e;
		} catch (Exception e) {
			logger.error(thisClassName + " error! messageSequ:" + messageSequ
					+ ". ", e);
			throw e;
		}
		// }
	}

	protected void getDeviceStatus(int deviceCount, int offset,
			boolean differTime) throws Exception {
		// devices loop
		logger.info("设备重复次数N:" + deviceCount);
                if(deviceCount==0){
                    return;
                }
		for (int h = 0; h < deviceCount; h++) {
			String device = getCharString(offset, 9);
			offset = offset + 9;
			String operator = getBcdString(offset, 3);
			offset = offset + 3;
			// 状态重复次数N1
			int repeatCount = getInt(offset);
//			logger.warn("状态重复次数N1:" + repeatCount);
                        if(repeatCount==0){
                            return;
                        }
			offset = offset + 1;

			DeviceStatus currentds = new DeviceStatus();
			currentds.setStatusDatetime(statusDateTime);
			currentds.setStatusDatetimeStr(statusDateTimeStr);
			currentds.setLineId(device.substring(0, 2));
			currentds.setStationId(device.substring(2, 4));
			currentds.setDevTypeId(device.substring(4, 6));
			currentds.setDeviceId(device.substring(6));
			currentds.setOperatorId(operator);
			currentds.setStatusId("");
			currentds.setStatusValue("");
			currentds.setAccStatusValue("");

			int highStatus = -1;
			String highStatusStr = "";
			Timestamp newStatusDateTime = new Timestamp(0);

			// status loop
			for (int i = 0; i < repeatCount; i++) {
				String status = getBcdString(offset, 2);
				// logger.info("status:"+status);
				offset = offset + 2;
				// processDegradeMode(currentds,status);
				Timestamp differStatusDateTime = null;
				if (differTime) { // for message which status time differ from
									// message time
					String differTod = getBcdString(offset, 7);
					offset = offset + 7;
					differStatusDateTime = DateHelper
							.dateStrToSqlTimestamp(differTod);
					// 新增加
					currentds.setStatusDatetime(differStatusDateTime);
					currentds.setStatusDatetimeStr(differTod);
				}

				// 处理降级模式
				// processDegradeMode(currentds, status);

				String accStatus = FrameCodeConstant.DEV_STATUS_MAPPING
						.get(status);
				if (accStatus == null) {
					logger.warn("No define for device status : " + status);
					accStatus = "0";
				}
				int accStatusInt = Integer.parseInt(accStatus);

				// compare and get the highest status(without differ time) or
				// newest status(with differ time)
				if (differTime) {
					if (null != differStatusDateTime
							&& differStatusDateTime.after(newStatusDateTime)) {
						newStatusDateTime = differStatusDateTime;
						highStatus = accStatusInt;
						highStatusStr = status;
					}
				} else {
					// logger.info("Accstatus:"+AccStatusInt);
					if (accStatusInt > highStatus) {
						highStatus = accStatusInt;
						highStatusStr = status;
					}
				}
				// if (AccStatusInt > 0) { ////正常设备状态也插入到设备状态表作为IQ数据源
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
				statuss.setAccStatusValue("" + accStatusInt);
				hisStatusV.add(statuss);
				// }
			}

			currentds.setAccStatusValue("" + highStatus);
			logger.info("状态重复次数N1:" + repeatCount + ",highStatus set:" + highStatus);
			if (!"".equals(highStatusStr)) {
				currentds.setStatusId(highStatusStr.substring(0, 3));
				currentds.setStatusValue(highStatusStr.substring(3));
			} else {
				currentds.setStatusId("");
				currentds.setStatusValue("");
			}
			if (differTime) {
				currentds.setStatusDatetime(newStatusDateTime);
			}
			currentStatusV.add(currentds);
		}

	}

}
