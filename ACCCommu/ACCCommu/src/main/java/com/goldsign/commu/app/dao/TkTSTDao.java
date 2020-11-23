package com.goldsign.commu.app.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.vo.InfoStationSale;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.dao.BaseDAO;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 售存数据数据库访问类
 * 
 * @author zhangjh
 * 
 */
public class TkTSTDao extends BaseDAO {
	private static Logger logger = Logger.getLogger(TkTSTDao.class.getName());
	private static final String QR_SQL = "select t.dept_id,t.tickettype_id,t.value,t.salenum,t.returnnum,t.currenttotal,to_char(t.reportdate,'yyyymmdd') reportdate from "+FrameDBConstant.COM_TK_P+"ic_inf_station_sale t";
	private static final String INSERT_SQL = " insert into "+FrameDBConstant.COM_TK_P+"ic_inf_station_sale (dept_id, tickettype_id, value, salenum, returnnum, currenttotal, reportdate, file_name) "
			+ "values (?, ?, ?, ?, ?, ?, to_date(?, 'yyyymmdd'), ?) ";
	private static String[] fieldTypes = { "string", "string", "int", "int",
			"int", "int", "string", "string" };

	/**
	 * 数据查询
	 * 
	 * @return
	 */
	public List<InfoStationSale> query() {
		DbHelper dbHelper = null;
		List<InfoStationSale> list = new ArrayList<InfoStationSale>();
		boolean result = false;
		try {
			dbHelper = new DbHelper("TkTSTDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());

			result = dbHelper.getFirstDocument(QR_SQL);
			while (result) {
				InfoStationSale tk = new InfoStationSale();
				tk.setDeptId(dbHelper.getItemValue("dept_id"));
				tk.setTicketTypeId(dbHelper.getItemValue("tickettype_id"));
				tk.setValue(dbHelper.getItemIntValue("value"));
				tk.setQuantitySale(dbHelper.getItemIntValue("salenum"));
				tk.setQuantityRec(dbHelper.getItemIntValue("returnnum"));
				tk.setBalance(dbHelper.getItemIntValue("currenttotal"));
				tk.setReportDate(dbHelper.getItemValue("reportdate"));
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
		logger.info("售存数据批量入库开始");
		List<Object> content = handlingMsg.getContent();
		int n = 0;
		DbHelper dbHelper = null;
		try {
			dbHelper = new DbHelper("TkTSTDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());
			dbHelper.prepareStatement(INSERT_SQL);
			String[] fieldValues;
			for (Object obj : content) {
				InfoStationSale in = (InfoStationSale) obj;
				fieldValues = in.toStrArr(handlingMsg.getFileName());
				addValuesByBatch(dbHelper, fieldValues, fieldTypes);
			}
			n = dbHelper.executeBatch();
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.info("售存数据批量入库结束，插入" + n + "条数据");
	}
}
