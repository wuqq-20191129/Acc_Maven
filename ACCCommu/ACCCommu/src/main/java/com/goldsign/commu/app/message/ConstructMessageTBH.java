package com.goldsign.commu.app.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.TkTBHDao;
import com.goldsign.commu.app.vo.InfoIncomedepHandin;
import com.goldsign.commu.app.vo.InfoTkBase;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.MessageException;

/**
 * 收益组上交数据文件
 * 
 * @author zhangjh
 * 
 */
public class ConstructMessageTBH extends ConstructMessageTK {
	private static final int DATA_LENGTH = 18;
	private static Logger logger = Logger.getLogger(ConstructMessageTBH.class
			.getName());
	private static int[] data_type = { T_STR, T_BCD, T_INT, T_STR, T_INT, T_BCD };
	private static int[] data_len = { FrameTicketConstant.LEN_LINE,
			FrameTicketConstant.LEN_CARD_TYPE, FrameTicketConstant.LEN_VALUE,
			FrameTicketConstant.LEN_HANDIN_TYPE_ID,
			FrameTicketConstant.LEN_QUANTITY,
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
		logger.info(Thread.currentThread().getName() + "启动生成车站上交数据文件");
		file_flag = "TBH";
		// 查询数据
		List<InfoIncomedepHandin> list = query();
		// 重新组装消息，按照线路排序
		Map<String, List<InfoTkBase>> maps = dealList(list);
		// 处理消息:生成文件
		dealMap(maps);

	}

	@Override
	protected void dealMsg(char[] msg, String line, List<InfoTkBase> list,
			int offset) throws MessageException, IOException {

		for (InfoTkBase base : list) {
			InfoIncomedepHandin tk = (InfoIncomedepHandin) base;
			String tkTypeId = tk.getTicketTypeId();
			int value = tk.getValue();
			String handinTypeId = tk.getHandinTypeId();
			int quantity = tk.getQuantity();
			String reportdate = tk.getReportDate();

			// 数据
			Object[] datas = { line, tkTypeId, value, handinTypeId, quantity,
					reportdate };
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
	private List<InfoIncomedepHandin> query() {
		List<InfoIncomedepHandin> list = new TkTBHDao().query();
		logger.info("查询生成车站上交数据文件,记录数：" + list.size());
		return list;
	}

	/**
	 * 按照线路区分 map对象的键为线路,值为该线路对应的值
	 * 
	 * @return
	 */
	private Map<String, List<InfoTkBase>> dealList(
			List<InfoIncomedepHandin> list) {
		logger.info("按照线路区分");
		Map<String, List<InfoTkBase>> map = new HashMap<String, List<InfoTkBase>>();
		for (InfoIncomedepHandin info : list) {
			String line = info.getDeptId();
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
