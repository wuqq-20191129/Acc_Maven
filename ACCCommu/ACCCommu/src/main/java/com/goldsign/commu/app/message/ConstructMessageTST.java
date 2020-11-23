package com.goldsign.commu.app.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.goldsign.commu.app.dao.TkTSTDao;
import com.goldsign.commu.app.vo.InfoStationSale;
import com.goldsign.commu.app.vo.InfoTkBase;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.MessageException;

/**
 * 售存数据文件（测试）
 * 
 * @author zhangjh
 * 
 */
public class ConstructMessageTST extends ConstructMessageTK {
	// 每行数据的长度（不包含结尾的换行符）
	private static final int DATA_LENGTH = 26;

	// private static String file_flag = "THD";
	private static Logger logger = Logger.getLogger(ConstructMessageTST.class
			.getName());

	private static int[] data_type = { T_STR, T_BCD, T_INT, T_INT, T_INT,
			T_INT, T_BCD };

	private static int[] data_len = { FrameTicketConstant.LEN_LINE_STATION,
			FrameTicketConstant.LEN_CARD_TYPE, FrameTicketConstant.LEN_VALUE,
			FrameTicketConstant.LEN_QUANTITY_SALE,
			FrameTicketConstant.LEN_QUANTITY_REC,
			FrameTicketConstant.LEN_BALANCE,
			FrameTicketConstant.LEN_REPORT_DATE };
	private static int num = 0;

	/**
	 * 
	 */
	public void handle() {
		if (num > 0) {
			return;
		}
		num++;
		logger.info(Thread.currentThread().getName() + "启动生成售存数据文件");
		file_flag = "TST";
		// 查询数据
		List<InfoStationSale> list = query();
		// 重新组装消息，按照线路排序
		Map<String, List<InfoTkBase>> maps = dealList(list);
		// 处理消息:生成文件
		dealMap(maps);

	}

	@Override
	protected void dealMsg(char[] msg, String line, List<InfoTkBase> list,
			int offset) throws MessageException, IOException {

		for (InfoTkBase base : list) {
			InfoStationSale tk = (InfoStationSale) base;
			String sationid = tk.getDeptId();
			String tkTypeId = tk.getTicketTypeId();
			int value = tk.getValue();
			int quantitySale = tk.getQuantitySale();
			int quantityRec = tk.getQuantityRec();
			int balance = tk.getBalance();
			String reportDate = tk.getReportDate();
			// 数据
			Object[] datas = { sationid, tkTypeId, value, quantitySale,
					quantityRec, balance, reportDate };
			// 文件记录
			char[] cs = getLine(datas, data_len, data_type);
			addCharArrayToBuffer(msg, cs, offset);
			offset += (DATA_LENGTH + CRLF_LENGTH);
		}

	}

	/**
	 * 查询数据
	 * 
	 * @return
	 */
	private List<InfoStationSale> query() {
		List<InfoStationSale> list = new TkTSTDao().query();
		logger.info("查询生成车站上交数据文件,记录数：" + list.size());
		return list;
	}

	/**
	 * 按照线路区分 map对象的键为线路,值为该线路对应的值
	 * 
	 * @return
	 */
	private Map<String, List<InfoTkBase>> dealList(List<InfoStationSale> list) {
		logger.info("按照线路区分");
		Map<String, List<InfoTkBase>> map = new HashMap<String, List<InfoTkBase>>();
		for (InfoStationSale info : list) {
			String deptId = info.getDeptId();
			String line = deptId.substring(0, 2);
			List<InfoTkBase> subList = null;
			if (map.containsKey(line)) {
				subList = map.get(line);
			} else {
				subList = new ArrayList<InfoTkBase>();
				map.put(line, subList);
			}
			InfoTkBase base = info;
			subList.add(base);
		}
		return map;
	}

	@Override
	protected int caculateDateLen(int size) {
		return (DATA_LENGTH + CRLF_LENGTH) * size;
	}
}
