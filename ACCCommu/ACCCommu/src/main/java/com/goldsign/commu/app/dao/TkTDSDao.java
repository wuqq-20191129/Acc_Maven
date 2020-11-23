package com.goldsign.commu.app.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.vo.InfoDistributeTk;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.dao.BaseDAO;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 配票数据数据库访问类
 * 
 * @author zhangjh
 * 
 */
public class TkTDSDao extends BaseDAO {
	private static String BIGGEST_DATE = "20990101";
	private static final String QR_SQL = "select t.water_no, t.line_id,t.ticket_id,card_money,t.valid_date,t.model,t.entry_station,t.exit_station,t.distribute_num,t.distribute_time from "+FrameDBConstant.COM_TK_P+"ic_out_distribute_data t where t.flag=0 order by t.water_no desc";

	private static final String UPDATE_SQL = " update "+FrameDBConstant.COM_TK_P+"ic_out_distribute_data t set  t.flag=1 where t.water_no between ? and ? ";
	private static final String INSERT_SQL = " insert into "+FrameDBConstant.COM_TK_P+"ic_out_distribute_data (water_no, line_id, ticket_id, card_money, valid_date, model, entry_station, exit_station, distribute_num, distribute_time, flag)"
			+ " values ("+FrameDBConstant.COM_TK_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"ic_out_distribute_data.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static String[] fieldTypes = { "string", "string", "int", "string",
			"string", "string", "string", "int", "string", "string" };

	private static Logger logger = Logger.getLogger(TkTDSDao.class.getName());

	/**
	 * 数据查询
	 * 
	 * @return
	 */
	public List<InfoDistributeTk> query() {
		logger.debug("查询需要处理的配票数据..");
		DbHelper dbHelper = null;
		List<InfoDistributeTk> list = new ArrayList<InfoDistributeTk>();
		boolean result = false;
		try {
			dbHelper = new DbHelper("TkTDSDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());
			result = dbHelper.getFirstDocument(QR_SQL);
			while (result) {
				InfoDistributeTk tk = new InfoDistributeTk();
				tk.setWaterNo(dbHelper.getItemIntegerValue("water_no"));
				tk.setDeptId(dbHelper.getItemValue("line_id"));
				tk.setTicketTypeId(dbHelper.getItemValue("ticket_id"));
				tk.setValue(dbHelper.getItemIntValue("card_money"));
				String validDate = dbHelper.getItemValue("valid_date");
				validDate = (validDate == null || "".equals(validDate.trim())) ? BIGGEST_DATE
						: validDate.trim();
				tk.setValidDate(validDate);
				tk.setModel(dbHelper.getItemValue("model"));
				tk.setExitStation(dbHelper.getItemValue("exit_station"));
				tk.setEntryStation(dbHelper.getItemValue("entry_station"));
				tk.setQuantity(dbHelper.getItemIntValue("distribute_num"));
				tk.setDistDate(dbHelper.getItemValue("distribute_time"));
				list.add(tk);
				result = dbHelper.getNextDocument();
			}

		} catch (SQLException e) {
			logger.info("查询消息出现异常：", e);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.debug("查询到" + list.size() + "条配票数据");
		return list;
	}

	/**
	 * 更改数据状态
	 * 
	 * @param tk
	 * @param tk2
	 */
	public void updateStatus(InfoDistributeTk tk1, InfoDistributeTk tk2) {
		logger.info("配票数据的状态为已处理，待处理数据的最大流水号:" + tk1.getWaterNo() + ",最小流水号:"
				+ tk2.getWaterNo());
		DbHelper dbHelper = null;
		int result = 0;
		try {
			dbHelper = new DbHelper("TkTDSDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());
			Object[] params = { tk2.getWaterNo(), tk1.getWaterNo() };
			result = dbHelper.executeUpdate(UPDATE_SQL, params);

		} catch (SQLException e) {
			logger.info("更改数据状态出现异常：", e);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.info("更改" + result + "条配票数据的状态");

	}

	@Override
	public void batchInsert(HandleMessageBase handlingMsg) throws SQLException {
		logger.info("配票数据批量入库开始");
		List<Object> content = handlingMsg.getContent();
		int n = 0;
		DbHelper dbHelper = null;
		try {
			dbHelper = new DbHelper("TkTDSDao",
					FrameDBConstant.TK_DBCPHELPER.getConnection());
			dbHelper.prepareStatement(INSERT_SQL);
			String[] fieldValues;
			for (Object obj : content) {
				InfoDistributeTk in = (InfoDistributeTk) obj;
				fieldValues = in.toStrArr();
				addValuesByBatch(dbHelper, fieldValues, fieldTypes);
			}
			n = dbHelper.executeBatch();
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.info("配票数据批量入库结束，插入" + n + "条数据");
	}
}
