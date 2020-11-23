package com.goldsign.commu.frame.dao;

import com.goldsign.commu.app.message.Message16;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.Message16Vo;
import com.goldsign.commu.frame.vo.Message17Vo;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * 
 * @author zhangjh
 */
public class Message16Dao {

	private static Logger logger = Logger.getLogger(Message16Dao.class
			.getName());

	/**
	 * 查询非即时退款信息
	 * 
	 * @param vo
	 *            查询信息
	 * @param id
	 *            连接ID
	 * @param dbHelper
	 * @return
	 * @throws Exception
	 */
	public Message17Vo getNonReturnInfogFromSp(Message16Vo vo, int id,
			DbHelper dbHelper) throws Exception {

		Message17Vo vo17 = new Message17Vo();

		int[] pInIndexes = { 1, 2, 3, 4 };

		Object[] pInStmtValues = { new Integer(id), vo.getApplyBill(),
				vo.getAction(), vo.getCurrentBom() };
		// 存储过程输出参数索引列表
		int[] pOutIndexes = { 5 };

		// 存储过程输出参数值类型
		int[] pOutTypes = { DbHelper.PARAM_OUT_TYPE_CURSOR };

		String sql = "call "+FrameDBConstant.COM_ST_P+"up_non_return_action(?,?,?,?,?)";

		// 执行存储过程
		dbHelper.runStoreProcForOracle(sql, pInIndexes, pInStmtValues,
				pOutIndexes, pOutTypes);

		// 判断结果集是否为空
		boolean result = dbHelper.getFirstDocumentForOracle();

		while (result) {
			vo17 = this.getMessage17VoForSp(dbHelper);
			// 判断结果集是否存在下一记录
			result = dbHelper.getNextDocumentForOracle();
		}
		return vo17;
	}

	/**
	 * 获取会话标识
	 * 
	 * @param dbHelper
	 *            数据库访问辅助对象
	 * @return 会话标识
	 * @throws Exception
	 */
	public int getId(DbHelper dbHelper) throws Exception {
		int[] pInIndexes = {};
		Object[] pInStmtValues = {};
		int[] pOutIndexes = { 1 };
		int[] pOutTypes = { DbHelper.PARAM_OUT_TYPE_INTEGER };
		String sql = "call "+FrameDBConstant.COM_ST_P+"up_st_id_non_return(?) ";
		// 执行存储过程
		dbHelper.runStoreProcForOracle(sql, pInIndexes, pInStmtValues,
				pOutIndexes, pOutTypes);
		// 获取返回代码输出参数值
		int id = dbHelper.getOutParamIntValue(1);
		return id;
	}

	private Message17Vo getMessage17VoForSp(DbHelper dbHelper)
			throws SQLException {

		String cardNo = dbHelper.getItemValue("card_logical_id");
		String flag = dbHelper.getItemValue("hdl_flag");
		String deposit = this.getIntValue(dbHelper.getItemIntValue("deposit_fee"));
		String balance = this.getIntValue(dbHelper.getItemIntValue("card_balance_fee"));
		String penalty = this.getIntValue(dbHelper.getItemIntValue("penalty"));
		String penalty_reason = dbHelper.getItemValue("penalty_reason");
		penalty_reason = this.getValue(penalty_reason, "0");

		Message17Vo vo = new Message17Vo();
		vo.setCardNo(cardNo);
		vo.setFlag(flag);
		vo.setDeposit(deposit);
		vo.setBalance(balance);
		vo.setPenalty(penalty);
		vo.setPenaltyReason(penalty_reason);
		return vo;

	}

	private String getValue(String value, String defaultValue) {
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return value;
	}

	private String getIntValue(int value) {
		return new Integer(value).toString();

	}

	public int unLock(int id, String applyBill) {
		int result = 0;
		DbHelper dbHelper = null;

		try {
			dbHelper = new DbHelper("Message16Dao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			String sqlStr = "update "+FrameDBConstant.COM_ST_P+"st_flow_non_rtn set processing=? "
					+ "where id=? and apply_bill=? and apply_action=? ";
			Object[] values = { Message16.UNLOCK, new Integer(id), applyBill,
					Message16.ACTION_RETURN };
			result = dbHelper.executeUpdate(sqlStr, values);

		} catch (Exception e) {
			logger.error("非即时退款解锁错误! ", e);
		} finally {
			try {
				if (dbHelper != null) {
					dbHelper.closeConnection();
				}
			} catch (Exception e) {
				logger.error("closeConnection error. ", e);
			}
		}

		return result;
	}
}
