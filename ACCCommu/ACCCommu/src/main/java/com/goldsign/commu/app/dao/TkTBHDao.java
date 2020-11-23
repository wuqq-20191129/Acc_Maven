package com.goldsign.commu.app.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.vo.InfoIncomedepHandin;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.dao.BaseDAO;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 收益组上交数据数据库访问类
 * 
 * @author zhangjh
 * 
 */
public class TkTBHDao extends BaseDAO {
	private static Logger logger = Logger.getLogger(TkTBHDao.class.getName());
	private static final String QR_SQL = "select dept_id,tickettype_id,value,handintype_id,quantity,to_char(reportdate,'yyyymmdd') reportdate from "+FrameDBConstant.COM_TK_P+"ic_inf_incomedep_handin ";
	private static final String INSERT_SQL = "insert into "+FrameDBConstant.COM_TK_P+"ic_inf_incomedep_handin (dept_id, handintype_id, quantity, reportdate, tickettype_id, value, file_name) "
			+ "values (?, ?, ?, to_date(?, 'yyyymmdd'), ?, ?, ?)";
	private static String[] fieldTypes = { "string", "string", "int", "string",
			"string", "int", "string" };

	/**
	 * 数据查询
	 * 
	 * @return
	 */
	public List<InfoIncomedepHandin> query() {
		DbHelper dbHelper = null;
		List<InfoIncomedepHandin> list = new ArrayList<InfoIncomedepHandin>();
		boolean result = false;
		try {
			dbHelper = new DbHelper("TkTBHDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());

			result = dbHelper.getFirstDocument(QR_SQL);
			while (result) {
				InfoIncomedepHandin tk = new InfoIncomedepHandin();
				tk.setDeptId(dbHelper.getItemValue("dept_id"));
				tk.setTicketTypeId(dbHelper.getItemValue("tickettype_id"));
				tk.setValue(dbHelper.getItemIntValue("value"));
				tk.setHandinTypeId(dbHelper.getItemValue("handintype_id"));
				tk.setQuantity(dbHelper.getItemIntValue("quantity"));
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
		logger.info("收益组上交数据批量入库开始");
		List<Object> content = handlingMsg.getContent();
		int n = 0;
		DbHelper dbHelper = null;
		try {
			dbHelper = new DbHelper("TkTBHDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());
			dbHelper.prepareStatement(INSERT_SQL);
			String[] fieldValues;
			for (Object obj : content) {
				InfoIncomedepHandin in = (InfoIncomedepHandin) obj;
				fieldValues = in.toStrArr(handlingMsg.getFileName());
				addValuesByBatch(dbHelper, fieldValues, fieldTypes);
			}
			n = dbHelper.executeBatch();
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.info("收益组上交数据批量入库结束，插入" + n + "条数据");
	}

}
