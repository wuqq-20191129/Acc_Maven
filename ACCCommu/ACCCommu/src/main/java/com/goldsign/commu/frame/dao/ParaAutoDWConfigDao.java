package com.goldsign.commu.frame.dao;

import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameParameterConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.ParaAutoDWConfigNumVo;
import com.goldsign.commu.frame.vo.ParaAutoDWConfigTimeVo;
import com.goldsign.commu.frame.vo.ParaAutoDWConfigVo;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 
 * @author hejj
 */
public class ParaAutoDWConfigDao {
	private static Logger logger = Logger.getLogger(ParaAutoDWConfigDao.class
			.getName());

	private ParaAutoDWConfigNumVo getResultRecordForNum(DbHelper dbHelper)
			throws SQLException {
		ParaAutoDWConfigNumVo vo = new ParaAutoDWConfigNumVo();
		vo.setParmTypeId(dbHelper.getItemValue("parm_type_id"));
		vo.setMinDownloadNum(dbHelper.getItemIntValue("min_download_num"));
		vo.setRemark(dbHelper.getItemValue("remark"));
		return vo;
	}

	public Vector<ParaAutoDWConfigNumVo> getConfigNum() throws Exception {
		boolean result = false;
		DbHelper dbHelper = null;
		Vector<ParaAutoDWConfigNumVo> v = new Vector<ParaAutoDWConfigNumVo>();
		ParaAutoDWConfigNumVo vo;

		try {
			dbHelper = new DbHelper("dataDbUtil",
					FrameDBConstant.OP_DBCPHELPER.getConnection());

			String sqlStr = "select parm_type_id,min_download_num,remark from "+FrameDBConstant.COM_ST_P+"OP_PRM_DW_AUTO_CONFIG_NUM";

			result = dbHelper.getFirstDocument(sqlStr);
			while (result) {

				vo = this.getResultRecordForNum(dbHelper);
				v.add(vo);
				result = dbHelper.getNextDocument();
			}

		} catch (Exception e) {
			PubUtil.handleException(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		return v;
	}

	public int getConfigNum(String paramTypeId) throws Exception {
		boolean result = false;
		DbHelper dbHelper = null;
		// Vector<ParaAutoDWConfigNumVo> v = new
		// Vector<ParaAutoDWConfigNumVo>();
		// ParaAutoDWConfigNumVo vo;
		Object[] values = { paramTypeId };
		int num = 1;

		try {
			dbHelper = new DbHelper("dataDbUtil",
					FrameDBConstant.OP_DBCPHELPER.getConnection());

			String sqlStr = "select parm_type_id,min_download_num,remark from "
                                +FrameDBConstant.COM_ST_P+"OP_PRM_DW_AUTO_CONFIG_NUM where parm_type_id=? ";

			result = dbHelper.getFirstDocument(sqlStr, values);
			if (!result) {
				return FrameParameterConstant.paraDownloadMinNum;
			}
			num = dbHelper.getItemIntValue("min_download_num");

		} catch (Exception e) {
			PubUtil.handleException(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		if (num < 1) {
			return FrameParameterConstant.paraDownloadMinNum;
		} else {
			return num;
		}

	}

	public Vector<ParaAutoDWConfigVo> getConfig() throws Exception {
		boolean result = false;
		DbHelper dbHelper = null;
		Vector<ParaAutoDWConfigVo> paras = new Vector<ParaAutoDWConfigVo>();
		ParaAutoDWConfigVo vo;
		Object[] values = { FrameParameterConstant.paraDownload };

		try {
			dbHelper = new DbHelper("dataDbUtil",
					FrameDBConstant.OP_DBCPHELPER.getConnection());

			String sqlStr = "select parm_type_id,cfg_year,cfg_month,cfg_date,cfg_hour,cfg_minute,download_flag,remark from "
                                +FrameDBConstant.COM_ST_P+"op_prm_dw_auto_config where download_flag=? ";

			result = dbHelper.getFirstDocument(sqlStr, values);
			while (result) {
				vo = this.getResultRecord(dbHelper);
				paras.add(vo);
				result = dbHelper.getNextDocument();
			}

		} catch (Exception e) {
			PubUtil.handleException(e, logger);

		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		return paras;
	}

	private String getTimeValueType(String timeValue) {
		timeValue = timeValue.trim();
		if (timeValue.equals(FrameParameterConstant.paraDownloadTimeValueAll)) {
			return FrameParameterConstant.paraDownloadTimeValueTypeAll;
		}
		if (timeValue
				.indexOf(FrameParameterConstant.paraDownloadTimeValueRange) != -1) {
			return FrameParameterConstant.paraDownloadTimeValueTypeRange;
		}
		return FrameParameterConstant.paraDownloadTimeValueTypeSingle;
	}

	private ParaAutoDWConfigTimeVo getTimeValue(String timeType,
			String valueType, String timeValue) {
		ParaAutoDWConfigTimeVo vo = new ParaAutoDWConfigTimeVo();
		vo.setValueType(valueType);
		vo.setTimeType(timeType);
		if (valueType
				.equals(FrameParameterConstant.paraDownloadTimeValueTypeAll)) {
			return vo;
		}

		if (valueType
				.equals(FrameParameterConstant.paraDownloadTimeValueTypeRange)) {
			int index = timeValue
					.indexOf(FrameParameterConstant.paraDownloadTimeValueRange);
			int min = Integer.parseInt(timeValue.substring(0, index));
			int max = Integer.parseInt(timeValue.substring(index + 1));
			vo.setValueMin(min);
			vo.setValueMax(max);
			return vo;
		}

		if (valueType
				.equals(FrameParameterConstant.paraDownloadTimeValueTypeSingle)) {
			int value = Integer.parseInt(timeValue);
			vo.setValueSinge(value);
			return vo;
		}
		return vo;

	}

	private Vector<ParaAutoDWConfigTimeVo> getTime(String time, String timeType) {
		Vector<ParaAutoDWConfigTimeVo> v = new Vector<ParaAutoDWConfigTimeVo>();
		ParaAutoDWConfigTimeVo vo;
		StringTokenizer st = new StringTokenizer(time, "#");
		String timeValue, timeValueType;
		while (st.hasMoreTokens()) {
			timeValue = st.nextToken();
			timeValueType = this.getTimeValueType(timeValue);
			vo = this.getTimeValue(timeType, timeValueType, timeValue);
			v.add(vo);

		}

		return v;
	}

	private ParaAutoDWConfigVo getResultRecord(DbHelper dbHelper)
			throws Exception {
		ParaAutoDWConfigVo vo = new ParaAutoDWConfigVo();
		vo.setYears(this.getTime(dbHelper.getItemValue("cfg_year"),
				FrameParameterConstant.paraDownloadTimeTypeYear));
		vo.setMonths(this.getTime(dbHelper.getItemValue("cfg_month"),
				FrameParameterConstant.paraDownloadTimeTypeMonth));
		vo.setDays(this.getTime(dbHelper.getItemValue("cfg_date"),
				FrameParameterConstant.paraDownloadTimeTypeDay));
		vo.setHours(this.getTime(dbHelper.getItemValue("cfg_hour"),
				FrameParameterConstant.paraDownloadTimeTypeHour));
		vo.setMins(this.getTime(dbHelper.getItemValue("cfg_minute"),
				FrameParameterConstant.paraDownloadTimeTypeMin));
		vo.setParamTypeId(dbHelper.getItemValue("parm_type_id"));
		vo.setDownloadFlag(dbHelper.getItemValue("download_flag"));
		vo.setRemark(dbHelper.getItemValue("remark"));

		return vo;

	}

}
