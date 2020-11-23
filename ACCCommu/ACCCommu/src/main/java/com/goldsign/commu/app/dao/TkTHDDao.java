package com.goldsign.commu.app.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.vo.InfoStationHandin;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.dao.BaseDAO;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 车站上交数据数据库访问类
 * 
 * @author zhangjh
 * 
 */
public class TkTHDDao extends BaseDAO {
	private static Logger logger = Logger.getLogger(TkTHDDao.class.getName());
	private static final String QR_SQL = "select dept_id,tickettype_id,value,isabandon,handintype_id,quantity,idstart,idend,remark,to_char(reportdate,'yyyymmdd') reportdate from "+FrameDBConstant.COM_TK_P+"ic_inf_station_handin ";
	private static final String INSERT_SQL = " insert into "+FrameDBConstant.COM_TK_P+"ic_inf_station_handin (dept_id, handintype_id, idend, idstart, isabandon, quantity, remark, reportdate, tickettype_id, value, createtime, file_name) "
			+ " values (?, ?, ?, ?, ?, ?, ?, to_date(?, 'yyyymmdd'), ?, ?, sysdate, ?)";
	private static String[] fieldTypes = { "string", "string", "string", "string","string", "int", "string", "string", "string", "int", "string", "string" };

	/**
	 * 数据查询
	 * 
	 * @return
	 */
	public List<InfoStationHandin> query() {
		logger.info("查询需要处理的配票数据..");
		DbHelper dbHelper = null;
		List<InfoStationHandin> list = new ArrayList<InfoStationHandin>();
		boolean result = false;
		try {
			dbHelper = new DbHelper("TkTHDDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());

			result = dbHelper.getFirstDocument(QR_SQL);
			while (result) {
				InfoStationHandin tk = new InfoStationHandin();
				tk.setDeptId(dbHelper.getItemValue("dept_id"));
				tk.setTicketTypeId(dbHelper.getItemValue("tickettype_id"));
				tk.setValue(dbHelper.getItemIntValue("value"));
				tk.setIsAbandon(dbHelper.getItemValue("isabandon"));
				tk.setHandinTypeId(dbHelper.getItemValue("handintype_id"));
				tk.setQuantity(dbHelper.getItemIntValue("quantity"));
				tk.setIdStart(dbHelper.getItemValue("idstart"));
				tk.setIdEnd(dbHelper.getItemValue("idend"));
				tk.setRemark(dbHelper.getItemValue("remark"));
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
		logger.info("车站上交数据批量入库开始");
		List<Object> content = handlingMsg.getContent();
		int n = 0;
		DbHelper dbHelper = null;
		try {
			dbHelper = new DbHelper("TkTHDDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());
			dbHelper.prepareStatement(INSERT_SQL);
			String[] fieldValues;
			for (Object obj : content) {
				InfoStationHandin in = (InfoStationHandin) obj;
				fieldValues = in.toStrArr(handlingMsg.getFileName());
				addValuesByBatch(dbHelper, fieldValues, fieldTypes);
			}
			n = dbHelper.executeBatch();
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.info("车站上交数据批量入库结束，插入" + n + "条数据");
	}

}
