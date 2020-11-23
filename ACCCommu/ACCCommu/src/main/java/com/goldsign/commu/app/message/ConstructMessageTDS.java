package com.goldsign.commu.app.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.TkTDSDao;
import com.goldsign.commu.app.vo.InfoDistributeTk;
import com.goldsign.commu.app.vo.InfoTkBase;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.thread.TKInterfaceThread;

/**
 * 配票数据文件
 * 
 * @author zhangjh
 * 
 */
public class ConstructMessageTDS extends ConstructMessageTK {
	private static final int DATA_LENGTH = 30;
	private static Logger logger = Logger.getLogger(ConstructMessageTDS.class
			.getName());
	private static int[] data_type = { T_STR, T_BCD, T_INT, T_BCD, T_STR,
			T_BCD, T_BCD, T_INT, T_BCD };
	private static int[] data_len = { FrameTicketConstant.LEN_LINE,
			FrameTicketConstant.LEN_CARD_TYPE, FrameTicketConstant.LEN_VALUE,
			FrameTicketConstant.LEN_EFFECTIVE, FrameTicketConstant.LEN_MODEL,
			FrameTicketConstant.LEN_IN_STATION,
			FrameTicketConstant.LEN_EXIT_STATION,
			FrameTicketConstant.LEN_QUANTITY, FrameTicketConstant.LEN_DIST_DATE };

	/**
	 * 任务完成状态,true:已完成，false：未完成 ,对finish_flag的取值、赋值必须同步
	 */
	public void handle() {
		// logger.info(Thread.currentThread().getName() + "启动下发配票数据文件...");

		// 配置一个下发时间，每天一次或者几次

		try {
			if (!TKInterfaceThread.isFinish_flag()) {
				logger.info("上一次的配票数据文件的下发还没有完成.");
				return;
			}
			TKInterfaceThread.setFinish_flag(false);// 设置为正在运行

			file_flag = "TDS";
			// 查询数据
			List<InfoDistributeTk> list = query();
			if (list.isEmpty()) {
				logger.debug("未查询到数据...");
				return;
			}
			// 重新组装消息，按照线路排序
			Map<String, List<InfoTkBase>> maps = dealList(list);
			// 处理消息:生成文件
			dealMap(maps);
			InfoDistributeTk tk1 = list.get(0);
			InfoDistributeTk tk2 = list.get(list.size() - 1);
			new TkTDSDao().updateStatus(tk1, tk2);
		} catch (Exception e) {
			logger.error("下发配票数据文件出现异常", e);
		} finally {
			// logger.info("----------设置状态为结束");
			TKInterfaceThread.setFinish_flag(true);// 设置为结束运行
		}
		logger.info("完成下发配票数据文件.");

	}

	@Override
	protected void dealMsg(char[] msg, String line, List<InfoTkBase> list,
			int offset) throws MessageException, IOException {

		for (InfoTkBase base : list) {
			InfoDistributeTk tk = (InfoDistributeTk) base;
			String tkTypeId = tk.getTicketTypeId();
			int value = tk.getValue();
			String effective = tk.getValidDate();
			String model = tk.getModel();
			String inStation = tk.getEntryStation();
			String exitStation = tk.getExitStation();
			int quantity = tk.getQuantity();
			String distDate = tk.getDistDate();
			// 数据
			Object[] datas = { line, tkTypeId, value, effective, model,
					inStation, exitStation, quantity, distDate };
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
	private List<InfoDistributeTk> query() {
		List<InfoDistributeTk> list = new TkTDSDao().query();
		return list;
	}

	/**
	 * 按照线路区分 map对象的键为线路,值为该线路对应的值
	 * 
	 * @return
	 */
	private Map<String, List<InfoTkBase>> dealList(List<InfoDistributeTk> list) {
		logger.info("按照线路区分");
		Map<String, List<InfoTkBase>> map = new HashMap<String, List<InfoTkBase>>();
		for (InfoDistributeTk info : list) {
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
		// 换行符用三个0X0D0X0A;
		return (DATA_LENGTH + CRLF_LENGTH) * size;
	}

}
