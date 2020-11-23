package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * 票卡参数
 * 
 * @author zhangjh
 */
public class Parameter0301Dao {

	private final static StringBuilder sql;

	static {
		sql = new StringBuilder();
		sql.append("select card_main_id,");
		sql.append("card_sub_id,");
		sql.append("purse_value_type,");
		sql.append("max_store_val,");
		sql.append("is_overdraw,");
		sql.append("overdraw_limit,");
		sql.append("is_recharge,");
		sql.append("max_recharge_val, ");
		sql.append("update_fee_type,");
		sql.append("is_chk_cur_date,");
		sql.append("is_chk_cur_station,");
		sql.append("is_refund,");
		sql.append("refund_limit,");
		sql.append("daily_trip_cnt_lmt,");
		sql.append("month_trip_cnt_lmt,");
		sql.append("exp_date,");
		sql.append("is_allow_postpone,");
		sql.append("extend_expire_day,");
		sql.append("deposit_amnt,");
		sql.append("card_cost,");
		sql.append("auxiliary_expenses,");
		sql.append("is_activation,");
		sql.append("is_chk_blk,");
		sql.append("is_chk_remain,");
		sql.append("is_chk_valid_phy_logic,");
		sql.append("is_chk_sequ,");
		sql.append("is_chk_exce_time,");
		sql.append("is_chk_exce_st,");
		sql.append("is_limit_entry_exit,");
		sql.append("is_limit_station,");
		sql.append("add_val_equi_type,");
		sql.append("use_on_equi,");
		sql.append("is_limit_sale_entry,");
		sql.append("refund_limit_balance,");
		sql.append("refund_auxiliary_expense, sale_equi_type, is_deposit_refund, IS_CHK_VALID_DAY, VALID_DAY "
				+ "from "+FrameDBConstant.COM_ST_P+"op_prm_card_para where version_no=? and record_flag=?");

	}
	private static Logger logger = Logger.getLogger(Parameter0301Dao.class
			.getName());

	public static Vector<String[]> getRecordFromTable(ParaGenDtl paraGenDtl,
			DbHelper dbHelper, int formtLen) {
		Vector<String[]> recV = null;
		boolean result;
		List<String> pStmtValues = new ArrayList<String>();
		pStmtValues.clear();
		pStmtValues.add(paraGenDtl.getVerNum());
		pStmtValues.add(paraGenDtl.getVerType());
		try {
			result = dbHelper.getFirstDocument(sql.toString(),
					pStmtValues.toArray());

			if (result) {
				recV = new Vector<String[]>();
				while (result) {
					String[] fields = new String[formtLen];
					fields[0] = dbHelper.getItemValue("card_main_id");
					fields[1] = dbHelper.getItemValue("card_sub_id");
					// 钱包值类型
					fields[2] = dbHelper.getItemValue("purse_value_type");
					fields[3] = dbHelper.getItemValue("max_store_val");
					fields[4] = dbHelper.getItemValue("is_overdraw");
					fields[5] = dbHelper.getItemValue("overdraw_limit");
					fields[6] = dbHelper.getItemValue("is_recharge");
					fields[7] = dbHelper.getItemValue("max_recharge_val");
					fields[8] = dbHelper.getItemValue("update_fee_type");
					fields[9] = dbHelper.getItemValue("is_chk_cur_date");
					fields[10] = dbHelper.getItemValue("is_chk_cur_station");
					fields[11] = dbHelper.getItemValue("is_refund");
					fields[12] = dbHelper.getItemValue("refund_limit");
					fields[13] = dbHelper.getItemValue("daily_trip_cnt_lmt");
					fields[14] = dbHelper.getItemValue("month_trip_cnt_lmt");
					fields[15] = dbHelper.getItemValue("exp_date");
					fields[16] = dbHelper.getItemValue("is_allow_postpone");
					fields[17] = dbHelper.getItemValue("extend_expire_day");
					fields[18] = dbHelper.getItemValue("deposit_amnt");
					fields[19] = dbHelper.getItemValue("card_cost");
					fields[20] = dbHelper.getItemValue("auxiliary_expenses");// 发售手续费
					fields[21] = dbHelper.getItemValue("is_activation");
					fields[22] = dbHelper.getItemValue("is_chk_blk");
					fields[23] = dbHelper.getItemValue("is_chk_remain");
					fields[24] = dbHelper
							.getItemValue("is_chk_valid_phy_logic");
					fields[25] = dbHelper.getItemValue("is_chk_sequ");
					fields[26] = dbHelper.getItemValue("is_chk_exce_time");
					fields[27] = dbHelper.getItemValue("is_chk_exce_st");
					fields[28] = dbHelper.getItemValue("is_limit_entry_exit");
					fields[29] = dbHelper.getItemValue("is_limit_station");
					fields[30] = dbHelper.getItemValue("add_val_equi_type");
					fields[31] = dbHelper.getItemValue("use_on_equi");
					fields[32] = dbHelper.getItemValue("is_limit_sale_entry");
					fields[33] = dbHelper.getItemValue("refund_limit_balance");
					fields[34] = dbHelper
							.getItemValue("refund_auxiliary_expense");
					fields[35] = dbHelper.getItemValue("sale_equi_type");
                                        fields[36] = dbHelper.getItemValue("is_deposit_refund");
                                        
                                        fields[37] = dbHelper.getItemValue("IS_CHK_VALID_DAY");
                                        fields[38] = dbHelper.getItemValue("VALID_DAY");
					recV.add(fields);
					result = dbHelper.getNextDocument();
				}
			}

		} catch (SQLException e) {
			logger.error("Access table "+FrameDBConstant.TABLE_PREFIX+"op_prm_card_para error! ", e);
			return null;
		}
		return recV;
	}
}
