package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.InfoPreTicketSale;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.dao.BaseDAO;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 预制票数据数据库访问类
 * 
 * @author lindq
 * 
 */
public class TkPSDDao extends BaseDAO {
	private static Logger logger = Logger.getLogger(TkPSDDao.class.getName());
        private static final String UPDATE_SQL = " update "+FrameDBConstant.COM_TK_P+"ic_inf_preticket_sale t set t.file_flag='1',t.file_name=? where t.tk_file_name = ? ";
	private static final String QR_SQL = "select dept_id, tickettype_id, value, quantity, logical_begin, logical_end, sale_time, operator_id, report_date, file_name, tk_file_name from "
                +FrameDBConstant.COM_TK_P+"ic_inf_preticket_sale where file_flag='0'";
	private static final String INSERT_SQL = " insert into "+FrameDBConstant.COM_TK_P+"ic_inf_preticket_sale"
                + " (dept_id, tickettype_id, value, quantity, logical_begin, logical_end, sale_time, operator_id, report_date, file_flag, file_name, tk_file_name)"
                + "values (?, ?, ?, ?, ?, ?, to_date(?,'yyyymmddhh24miss'), ?, to_date(?,'yyyymmdd'), ?, null, ?) ";
	private static String[] fieldTypes = { "string", "string", "int", "int", "string", "string", "string", "string", "string" , "string" , "string"};

	/**
	 * 数据查询
	 * 
	 * @return
	 */
	public List<InfoPreTicketSale> query() {
		DbHelper dbHelper = null;
		List<InfoPreTicketSale> list = new ArrayList<InfoPreTicketSale>();
		boolean result = false;
		try {
			dbHelper = new DbHelper("TkPSDDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());

			result = dbHelper.getFirstDocument(QR_SQL);
			while (result) {
				InfoPreTicketSale tk = new InfoPreTicketSale();
				tk.setDeptId(dbHelper.getItemValue("dept_id"));
				tk.setTicketTypeId(dbHelper.getItemValue("tickettype_id"));
				tk.setValue(dbHelper.getItemIntValue("value"));
				tk.setQuantity(dbHelper.getItemIntValue("quantity"));
				tk.setLogicalBegin(dbHelper.getItemValue("logical_begin"));
				tk.setLogicalEnd(dbHelper.getItemValue("logical_end"));
                                tk.setSaleTime(dbHelper.getItemValue("sale_time"));
				tk.setOperatorId(dbHelper.getItemValue("operator_id"));
				tk.setReportDate(dbHelper.getItemValue("report_date"));
                                tk.setFileName(dbHelper.getItemValue("tk_file_name"));
				list.add(tk);
				result = dbHelper.getNextDocument();
			}

		} catch (SQLException e) {
			logger.info("查询消息出现异常：", e);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		return list;
	}

	@Override
	public void batchInsert(HandleMessageBase handlingMsg) throws SQLException {
		logger.info("预制票数据批量入库开始");
		List<Object> content = handlingMsg.getContent();
		int n = 0;
		DbHelper dbHelper = null;
		try {
			dbHelper = new DbHelper("TkTSTDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());
			dbHelper.prepareStatement(INSERT_SQL);
			String[] fieldValues;
			for (Object obj : content) {
				InfoPreTicketSale in = (InfoPreTicketSale) obj;
                                in.setTkFileName(handlingMsg.getFileName());
				fieldValues = in.toStrArr();
				addValuesByBatch(dbHelper, fieldValues, fieldTypes);
			}
			n = dbHelper.executeBatch();
                        if(n==-2){
                            n=content.size();
                        }
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.info("预制票数据批量入库结束，插入" + n + "条数据");
	}
        
        /**
	 * 更改数据状态
         * @param fileName
	 * @param list
	 */
	public void updateStatus(String fileName, List<InfoPreTicketSale> list) {
		logger.info("预制票数据的状态为已处理，处理数据的数量:" + list.size());
		DbHelper dbHelper = null;
		int result = 0;
		try {
			dbHelper = new DbHelper("TkPSDDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());
			Object[] params = {fileName, ""};
                        for(InfoPreTicketSale infoPreTicketSale : list){
                            params[1] = infoPreTicketSale.getFileName();
                            result = result + dbHelper.executeUpdate(UPDATE_SQL, params);
                        }
		} catch (SQLException e) {
			logger.info("更改数据状态出现异常：", e);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.info("更改" + result + "条预制票数据的状态");

	}
	/**
	 * 处理押金类型，押金金额，发售金额
	 * @param ticketTypeId
	 * @Date  2020-04-13
	 */

	public List queryForDeposit(String ticketTypeId){
		DbHelper dbHelper = null;
		boolean result =false;
		boolean result1=false;
		String deposit_type="00";
		int deposit_amnt =0;
		int is_deposit_refund=0;
		int card_cost=0;
	//	Map<String,Object> map =new HashMap<String, Object>();
		List<Object> list =new ArrayList<Object>();
		//1 先判断是否乘次票
		String TCT_SQL = "select card_main_id ,card_sub_id ,card_sub_name from " + FrameDBConstant.COM_ST_P+"st_cod_card_tct where card_main_id ||card_sub_id =?";
		String SQL= "select deposit_amnt , is_deposit_refund , card_cost  from "+FrameDBConstant.COM_ST_P+"op_prm_card_para " +
				"where record_flag ='0' and card_main_id||card_sub_id=? ";
		Object[] values={ticketTypeId};
		try{
			dbHelper = new DbHelper("TkPSDDao",
					FrameDBConstant.OP_DBCPHELPER.getConnection());
			result=dbHelper.getFirstDocument(TCT_SQL,values);
			result1=dbHelper.getFirstDocument(SQL,values);
			if(result1){
				deposit_amnt=dbHelper.getItemIntValue("deposit_amnt");
				is_deposit_refund = dbHelper.getItemIntValue("is_deposit_refund");
				card_cost=dbHelper.getItemIntValue("card_cost");
			}
			if(result){
				deposit_type="02";
			}else{
				card_cost=0;
				if(is_deposit_refund==0){
					deposit_type="01";
				}
			}
//			if(result){
//				 deposit_type="02";
//				result1=dbHelper.getFirstDocument(SQL,values);
//				if(result1){
//					 deposit_amnt=dbHelper.getItemIntValue("deposit_amnt");
//					 card_cost=dbHelper.getItemIntValue("card_cost");
//				}
//			}else{
//				card_cost=0;
//				result1=dbHelper.getFirstDocument(SQL,values);
//				if(result1){
//					deposit_amnt=dbHelper.getItemIntValue("deposit_amnt");
//					int is_deposit_refund=dbHelper.getItemIntValue("is_deposit_refund");
//					if(is_deposit_refund==0){
//						deposit_type="01";
//					}
//				}
//			}
//			map.put("deposit_type",deposit_type);
//			map.put("deposit_amnt",deposit_amnt);
//			map.put("card_cost",card_cost);
			list.add(deposit_type);
			list.add(deposit_amnt);
			list.add(card_cost);

		} catch (SQLException e) {
			logger.error("查询押金类型，押金金额，次票发售金额出错：", e);
		}finally {
			PubUtil.finalProcess(dbHelper);
		}
		return list;
	}
}
