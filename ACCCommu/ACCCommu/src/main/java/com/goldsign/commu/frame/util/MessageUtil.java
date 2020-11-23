/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.EmergentTrafficLccDao;
import com.goldsign.commu.app.dao.TrafficLccDao;
import com.goldsign.commu.app.vo.FlowHourMinFive;
import com.goldsign.commu.app.vo.FlowHourMinFiveTotal;
import com.goldsign.commu.app.vo.FlowHourMinFiveUnit;
import com.goldsign.commu.app.vo.FlowHourOrg;
import com.goldsign.commu.app.vo.FlowResult;
import com.goldsign.commu.frame.constant.FrameCodeConstant;

/**
 * 
 * @author hejj
 */
public class MessageUtil {
	//

	private static Logger logger = Logger
			.getLogger(MessageUtil.class.getName());

	/**
	 * 清理进出站缓存
	 * 
	 * @param buffer
	 *            进出站缓存
	 * @return
	 */
	public static boolean decreaseBufferKeepCurrent(
			TreeMap<String, HashMap<String, FlowHourMinFive>> buffer) {
		synchronized (buffer) {
			if (buffer.isEmpty()) {
				return false;
			}
			boolean isClear = false;
			// 取今天、昨天、明天的日期
			String current = DateHelper.dateOnlyToString(new Date());
			String before = DateHelper.getDateBefore(current, 3600000 * 24);
			String after = DateHelper.getDateAfter(current, 3600000 * 24);
			String[] keeps = { before, current, after };

			Set keys = buffer.keySet();
			Iterator it = keys.iterator();
			String key;
			Vector<String> vectors = new Vector<String>();
			while (it.hasNext()) {
				key = (String) it.next();
				if (!isKeep(keeps, key)) {
					vectors.add(key);

				}
			}
			for (int i = 0; i < vectors.size(); i++) {
				key = vectors.get(i);
				buffer.remove(key);
				isClear = true;
				logger.error(" 缓存删除 key=" + key + "的内容");
			}

			return isClear;

		}
	}

	private static boolean isKeep(String[] keeps, String key) {
		String tmp;
		for (int i = 0; i < keeps.length; i++) {
			tmp = keeps[i];
			if (tmp.equals(key)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 取得规定日期的所有车站票卡的5分钟客流
	 * 
	 * @param date
	 * @param flag
	 * @return Vector<FlowHourOrg>
	 * @throws Exception
	 */
	public static Vector<FlowHourOrg> getFlowHourMinFiveFromAfc(String dateKey,
			String flag) throws Exception {
		TrafficLccDao dao = new TrafficLccDao();
		Vector<FlowHourOrg> bufferFlowHourMinFive = dao.getFlowHourMinFive(
				dateKey, flag);
		return bufferFlowHourMinFive;

	}

	/**
	 * 取得规定日期的所有车站票卡的5分钟总客流
	 * 
	 * @param date
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public static Vector<FlowHourOrg> getFlowHourMinFiveTotalFromAfc(
			String date, String flag) throws Exception {
		TrafficLccDao dao = new TrafficLccDao();
		Vector<FlowHourOrg> bufferFlowHourMinFive = dao
				.getFlowHourMinFiveTotal(date, flag);
		return bufferFlowHourMinFive;

	}

	/**
	 * 获取当前日期上一天客流缓存
	 * 
	 * @param bufferFlow
	 *            5分钟进出站客流缓存
	 * @param dateKey
	 *            客流日期
	 * @param flag
	 *            进出站标志
	 * @return 当前日期上一天客流缓存
	 * @throws Exception
	 */
	public static HashMap<String, FlowHourMinFive> getStationTrafficFromBufForDate(
			TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlow,
			String dateKey, String flag) throws Exception {

		HashMap<String, FlowHourMinFive> bufferFlowHourMinFive = null;

		// 缓存中不包含客流日期对应缓存，从数据库取值.
		if (!bufferFlow.containsKey(dateKey)) {
			// 改为定时清理
			// if (isOverFlow(bufferFlow)) {
			// decreaseBuffer(bufferFlow);
			// }
			// logger.error("initBuffer");
			// 从数据库表取得5分钟客流缓存bufferFlowHourMinFive 对象键是车站票卡
			// 值是所有分钟客流值，使用FlowHourMinFive封装
			bufferFlowHourMinFive = initBufferForHourMinFiveALL(dateKey, flag);
			addBuffer(bufferFlow, dateKey, bufferFlowHourMinFive);
		}
		bufferFlowHourMinFive = bufferFlow.get(dateKey);
		return bufferFlowHourMinFive;
	}

	/**
	 * 计算5分钟客流
	 * 
	 * @param bufferFlow
	 *            客流缓存
	 * @param dateKey
	 *            日期 YYYYMMDD
	 * @param stationCardKey
	 *            车站表卡格式
	 * @param flag
	 *            进出站标志
	 * @param hourMinFive
	 *            客流时间所属的5分钟时间范畴，如20071226041125 值为0410,20071226041525 值为0415
	 * @param totalHourTraffic
	 *            客流累计数
	 * @return
	 * @throws Exception
	 */
	public static FlowResult calculateHourMinFiveTraffic(
			TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlow,
			String dateKey, String stationCardKey, String flag,
			String hourMinFive, int totalHourTraffic) throws Exception {

		// 判断是否存在当前日期的5分钟客流缓存,如果不存在,判断缓存是否已抵达规定的最大值,超过最大值,先删除日期最小的缓存
		// bufferFlow缓存存放的数据主键是日期,值是5分钟客流缓存
		// 上一天，按运营日计算时，需上一个自然日数据 格式：20180716
		String datekeyPre = DateHelper.getDateBefore(dateKey, 3600000 * 24);

		// 下一天，按运营日计算滞留数据时，需下一个自然日数据
		// String datekeyAft = DateHelper.getDateAfter(dateKey, 3600000 * 24);

		// 当前日期上一天客流缓存 : bufferFlowHourMinFivePre中的键：车站票卡组合；值为各小时客流数据
		HashMap<String, FlowHourMinFive> bufferFlowHourMinFivePre = getStationTrafficFromBufForDate(
				bufferFlow, datekeyPre, flag);

		// 当前日期客流缓存:
		HashMap<String, FlowHourMinFive> bufferFlowHourMinFive = getStationTrafficFromBufForDate(
				bufferFlow, dateKey, flag);

		// 当前日期下一天客流缓存
		// HashMap<String, FlowHourMinFive> bufferFlowHourMinFiveAft =
		// getStationTrafficFromBufForDate(
		// bufferFlow, datekeyAft, flag);

		// bufferFlowHour缓存存放的数据主键是车站票卡组合,值是各小时客流
		// 当前日期的当前车站客流缓存
		if (!bufferFlowHourMinFive.containsKey(stationCardKey)) {
			bufferFlowHourMinFive.put(stationCardKey, new FlowHourMinFive());
		}

		FlowHourMinFive flowHourMinFive = (FlowHourMinFive) bufferFlowHourMinFive
				.get(stationCardKey);

		// 当前日期上一天的当前车站客流缓存
		if (!bufferFlowHourMinFivePre.containsKey(stationCardKey)) {
			bufferFlowHourMinFivePre.put(stationCardKey, new FlowHourMinFive());
		}

		FlowHourMinFive flowHourMinFivePre = (FlowHourMinFive) bufferFlowHourMinFivePre
				.get(stationCardKey);

		FlowResult result = null;

		// 计算5分钟客流
		synchronized (flowHourMinFive) {
			result = flowHourMinFive.calculateFlowHourMinFive(hourMinFive,
					totalHourTraffic, flowHourMinFivePre);
			return result;

		}

	}

	/**
	 * 从数据库表取得5分钟客流缓存bufferFlowHourMinFive; 键是车站票卡,
	 * 值是所有分钟客流值，使用FlowHourMinFive封装
	 * 
	 * @param dateKey
	 *            客流日期 YYYYMMDD
	 * @param flag
	 *            进出站标志
	 * @return
	 * @throws Exception
	 */
	private static HashMap<String, FlowHourMinFive> initBufferForHourMinFiveALL(
			String dateKey, String flag) throws Exception {
		// 取得规定日期的所有车站票卡的5分钟客流，数据封装对象为FlowHourOrg
		// ,初始化当前日期的5分钟客流缓存,该缓存主键为车站票卡,值为5分钟客流缓存
		HashMap<String, FlowHourMinFive> bufferFlowHour = initBufferForHourMinFive(
				dateKey, flag);

		// 取得规定日期的所有车站票卡的5分钟客流，数据封装对象为FlowHourOrg,并将总客流放置再FlowFiveMinTotal对象中
		initBufferForHourMinFiveTotal(dateKey, flag, bufferFlowHour);

		return bufferFlowHour;

	}

	private static HashMap<String, FlowHourMinFive> initBufferForHourMinFive(
			String dateKey, String flag) throws Exception {
		// 取得规定日期的所有车站票卡的5分钟客流，数据封装对象为FlowHourOrg
		Vector<FlowHourOrg> bufferFlowHourMinFiveDB = getFlowHourMinFiveFromAfc(
				dateKey, flag);

		// 初始化当前日期的5分钟客流缓存,该缓存主键为车站票卡,值为5分钟客流缓存
		HashMap<String, FlowHourMinFive> bufferFlowHour = getBufferFlowHourMinFiveFromDB(bufferFlowHourMinFiveDB);
		return bufferFlowHour;
	}

	private static void initBufferForHourMinFiveTotal(String dateKey,
			String flag, HashMap<String, FlowHourMinFive> bufferFlowHourMin)
			throws Exception {
		// 取得规定日期的所有车站票卡的5分钟客流，数据封装对象为FlowHourOrg
		Vector<FlowHourOrg> bufferFlowHourMinFiveDBTotal = getFlowHourMinFiveTotalFromAfc(
				dateKey, flag);

		getBufferFlowHourMinFiveFromDBTotal(dateKey,
				bufferFlowHourMinFiveDBTotal, bufferFlowHourMin);

	}

	/**
	 * 缓存客流信息
	 * 
	 * @param buffer
	 * @param dateKey
	 * @param bufferFlowHourMinFive
	 */
	private static void addBuffer(
			TreeMap<String, HashMap<String, FlowHourMinFive>> buffer,
			String dateKey,
			HashMap<String, FlowHourMinFive> bufferFlowHourMinFive) {
		synchronized (buffer) {
			buffer.put(dateKey, bufferFlowHourMinFive);
		}

	}

	/**
	 * 初始化当前日期的5分钟客流缓存,该缓存主键为车站票卡,值为5分钟客流缓存
	 * 总客流计算时，00-0230时间段，总客流值为当天时间止总客流＋上一天0230－2355总客流
	 * 
	 * @param date
	 *            客流时间
	 * @param bufferFlowHourMinFive
	 *            该客流日期对应的客流数据
	 * @return
	 */
	private static HashMap<String, FlowHourMinFive> getBufferFlowHourMinFiveFromDB(
			Vector<FlowHourOrg> bufferFlowHourMinFive) {
		// 主键为车站票卡,值为当前时间所有5分钟得客流，使用对象FlowHourMinFive封装
		// FlowHourMinFive对象中变量集合flowHourMinFive，键为时分，值为客流，使用FlowHourMinFiveUnit封装
		HashMap<String, FlowHourMinFive> buffer = new HashMap<String, FlowHourMinFive>();

		if (bufferFlowHourMinFive.isEmpty()) {
			logger.info("数据库中没有分钟客流信息");
			return buffer;
		}

		// 票卡格式
		String stationCardKey = null;
		FlowHourMinFive flowHourMinFive = null;

		for (FlowHourOrg vo : bufferFlowHourMinFive) {
			stationCardKey = getKey(vo);// 车站票卡格式
			flowHourMinFive = getFlowHourMinFive(stationCardKey, buffer);// 车站票卡对应的5分钟客流
			setValueForMinFive(vo, flowHourMinFive);
		}
		// 计算当前时间的车站票卡对应的5分钟总客流改为从数据表中获取
		// calculateFlowHourMinFiveTotal(buffer);

		return buffer;

	}

	private static void getBufferFlowHourMinFiveFromDBTotal(String date,
			Vector<FlowHourOrg> bufferFlowHourMinFiveDBTotal,
			HashMap<String, FlowHourMinFive> bufferFlowHourMin) {
		// 数据库没有值
		if (bufferFlowHourMinFiveDBTotal.isEmpty()) {
			logger.error("数据库没有五分钟总客流信息");
			return;
		}
		FlowHourOrg vo;
		String key;
		FlowHourMinFive value;
		// 主键为车站票卡,值为当前时间所有5分钟得客流，使用对象FlowHourMinFive封装
		// FlowHourMinFive对象中变量集合flowHourMinFive，键为时分，值为客流，使用FlowHourMinFiveUnit封装
		// HashMap buffer = new HashMap();

		for (int i = 0; i < bufferFlowHourMinFiveDBTotal.size(); i++) {
			vo = (FlowHourOrg) bufferFlowHourMinFiveDBTotal.get(i);
			key = getKey(vo);// 车站票卡格式
			value = getFlowHourMinFive(key, bufferFlowHourMin);// 车站票卡对应的5分钟总客流
			setValueForMinFiveTotal(vo, value);
		}

	}

	private static FlowHourMinFive getFlowHourMinFive(String stationCardKey,
			HashMap<String, FlowHourMinFive> buffer) {
		FlowHourMinFive fh = null;
		if (!buffer.containsKey(stationCardKey)) {
			// 初始化
			fh = new FlowHourMinFive();
			buffer.put(stationCardKey, fh);
		}
		return (FlowHourMinFive) buffer.get(stationCardKey);

	}

	/**
	 * 组装车站票卡格式
	 * 
	 * @param vo
	 * @return
	 */
	private static String getKey(FlowHourOrg vo) {

		return getStationCardKey(vo.getLineId(), vo.getStationId(),
				vo.getCardMainType(), vo.getCardSubType());

	}

	/**
	 * 获取车站票卡，格式：线路+车站+主卡类型+子卡类型
	 * 
	 * 
	 * @param lineId
	 *            线路
	 * @param stationId
	 *            车站
	 * @param cardMainType
	 *            主卡
	 * @param cardSubType
	 *            子卡
	 * @return 车站票卡格式
	 */
	private static String getStationCardKey(String lineId, String stationId,
			String cardMainType, String cardSubType) {
		StringBuilder sbBuilder = new StringBuilder();
		sbBuilder.append(lineId).append(stationId).append(cardMainType)
				.append(cardSubType);
		return sbBuilder.toString();
	}

	/**
	 * 获取年月日，格式YYYYMMDD
	 * 
	 * @param trafficDatetime
	 *            客流时间
	 * @return YYYYMMDD格式的日期
	 */
	public static String getDateKey(String trafficDatetime) {
		return trafficDatetime.substring(0, 8);
	}

	// public static String getHour(String trafficDatetime) {
	// return trafficDatetime.substring(8, 10);
	// }
	public static String getFiveHourMin(String trafficDatetime) {
		String Min = trafficDatetime.substring(10, 12);
		String hour = trafficDatetime.substring(8, 10);
		String hourMinFive = "99";
		String hourMinFivePre;
		for (int i = 0; i < FrameCodeConstant.FIVE_HOUR_MINS.length; i++) {
			hourMinFive = FrameCodeConstant.FIVE_HOUR_MINS[i];
			if (i == 0) {
				hourMinFivePre = FrameCodeConstant.FIVE_HOUR_MINS[0];
			} else {
				hourMinFivePre = FrameCodeConstant.FIVE_HOUR_MINS[i - 1];
			}
			// 找出第一个大于等于客流小时分钟的5分钟数据
			if (hourMinFive.compareTo(Min) == 0) {
				return hour + hourMinFive;
			}
			if (hourMinFive.compareTo(Min) > 0) {
				return hour + hourMinFivePre;
			}
		}
		return hour + hourMinFive;
	}

	public static String getDateTimeForMinFive(String trafficDatetime) {
		return trafficDatetime.substring(0, 8)
				+ getFiveHourMin(trafficDatetime);

	}

	private static void setValueForMinFive(FlowHourOrg vo, FlowHourMinFive value) {
		value.setFlowHourMinFive(vo);
	}

	private static void setValueForMinFiveTotal(FlowHourOrg vo,
			FlowHourMinFive value) {
		value.setFlowHourMinFiveTotal(vo);
	}

	/**
	 * 处理5分钟客流
	 * 
	 * @param bufferFlow
	 *            5分钟进站客流缓存
	 * @param lineId
	 *            线路
	 * @param stationId
	 *            车站
	 * @param cardMainType
	 *            主卡类型
	 * @param cardSubType
	 *            子卡类型
	 * @param flag
	 *            进出站标志
	 * @param trafficDate
	 *            客流时间
	 * @param traffic
	 *            进出站流量累计计数
	 * @throws Exception
	 *             异常
	 */
	public void handleFlowHourMinFive(
			TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlow,
			String lineId, String stationId, String cardMainType,
			String cardSubType, String flag, String trafficDate, int traffic)
			throws Exception {
		// bufferFlow,静态对象，
		// 缓存存放格式，主键为日期， 值为所有车站的所有5分钟客流，使用hashMap
		// 对象封装，主键为车站票卡，值为车站所有5分钟客流，使用HourMinFive对象封装

		// 获取客流日期  如20071226041125-  20071226
		String dateKey = getDateKey(trafficDate);

		// 车站票卡格式如01010001
		String stationCardKey = getStationCardKey(lineId, stationId,
				cardMainType, cardSubType);

		// 计算出客流时间所属的5分钟时间范畴，如20071226041125 值为0410,20071226041525 值为0415
		String hourMinFive = getFiveHourMin(trafficDate);

		handleFlowHourMinFiveByDate(bufferFlow, lineId, stationId,
				cardMainType, cardSubType, flag, trafficDate, traffic, dateKey,
				hourMinFive, stationCardKey);

		// 处理客流数据滞留
		handleDataDelay(bufferFlow, lineId, stationId, cardMainType,
				cardSubType, flag, dateKey, stationCardKey, hourMinFive);

	}

	private void handleDataDelay(
			TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlow,
			String lineId, String stationId, String cardMainType,
			String cardSubType, String flag, String dateKey,
			String stationCardKey, String hourMinFive) throws Exception {

		// 处理客流数据滞留，如存在滞留客流，重新计算滞留客流时间点的最近下一个5分钟客流
		// 当前时间的下5分钟时间：如当前时间是20100722030000
		// 下一个5分钟可是20100722030500、20100722031000、20100722031500
		// 获取日期缓存，如时间是00-0230则取当天缓存，否则取当天缓存及下一天缓存，如时间是2010070500 则取
		// 201007、及201008共2各缓存，因为运营日跨2个自然日
		Vector<FlowHourMinFiveTotal> fhmfTotals = getNextTrafficTotalV(dateKey,
				hourMinFive, stationCardKey, bufferFlow);

		// 获取下一个最近的不为0的5分钟客流
		FlowHourMinFiveUnit hourMinFiveUnitNext = getFlowHourMinFiveTotalAfterByVector(
				fhmfTotals, hourMinFive);

		// 不存在最近的5分钟客流
		if (hourMinFiveUnitNext == null
				|| hourMinFiveUnitNext.getTraffic() <= 0) {
			return;
		}

		String hourMinFiveNext = hourMinFiveUnitNext.getHourMinFive();// 时分
		String trafficDateNext = hourMinFiveUnitNext.getDateKey()
				+ hourMinFiveNext + "00";
		String dateKeyNext = getDateKey(trafficDateNext);// 日期
		int trafficNext = hourMinFiveUnitNext.getTraffic();

		// 获取总客流数

		// 如总客流数为0,表示非滞留数据，不为0，表示滞留数据
		if (isDataDelay(trafficNext)) {
			handleFlowHourMinFiveByDate(bufferFlow, lineId, stationId,
					cardMainType, cardSubType, flag, trafficDateNext,
					trafficNext, dateKeyNext, hourMinFiveNext, stationCardKey);

		}
	}

	/**
	 * 当前时间是否在0点至0230之间
	 * 
	 * @param hourMinFive
	 *            当前时间
	 * @return 如果当前在0点和0230之间返回true,否则返回false
	 */
	public static boolean isTimeBetween00And0230(String hourMinFive) {
		return hourMinFive.compareTo(FrameCodeConstant.SQUAD_TIME) < 0;
	}

	/**
	 * 获取下一个最近的不为0的5分钟客流
	 * 
	 * @param fhmfTotals
	 * @param minFivePreStart
	 * @return
	 */
	private FlowHourMinFiveUnit getFlowHourMinFiveTotalAfterByVector(
			Vector<FlowHourMinFiveTotal> fhmfTotals, String minFivePreStart) {

		FlowHourMinFiveUnit unit = null;
		FlowHourMinFiveTotal totalCur = null;
		FlowHourMinFiveTotal totalAft = null;

		int size = fhmfTotals.size();
		if (size == 2) {
			totalCur = (FlowHourMinFiveTotal) fhmfTotals.get(0);
			totalAft = (FlowHourMinFiveTotal) fhmfTotals.get(1);
		} else if (size == 1) {
			totalCur = (FlowHourMinFiveTotal) fhmfTotals.get(0);
		}
		// 如果时间小于运营日如0100，仅查找至当天0230前的缓存
		if (isTimeBetween00And0230(minFivePreStart)) {
			unit = getFlowHourMinFiveTotalAfterBetween00To0230(totalCur,
					minFivePreStart);
		} else {
			// 如果时间大于运营日如0300，仅查找至当天0300后的缓存及下一天至0230
			unit = getFlowHourMinFiveTotalAfterExcept0230(totalCur, totalAft,
					minFivePreStart);
		}
		return unit;

	}

	/**
	 * 查找00至当天0230前的缓存
	 * 
	 * @param total
	 * @param minFivePreStart
	 * @return
	 */
	private FlowHourMinFiveUnit getFlowHourMinFiveTotalAfterBetween00To0230(
			FlowHourMinFiveTotal total, String minFivePreStart) {

		SortedMap<String, FlowHourMinFiveUnit> fhmfTotalTail = null;
		SortedMap<String, FlowHourMinFiveUnit> fhmfTotalTailTemp = null;

		// printTreeMap(fhmfTotal);
		// 如果时间小于运营日如0100，仅查找至当天0230前的缓存
		if (total == null) {
			return null;
		}

		TreeMap<String, FlowHourMinFiveUnit> fhmfTotal = total
				.getFlowHourMinTotal();
		// 返回此映射的部分视图，其键值严格小于 toKey.
		fhmfTotalTailTemp = fhmfTotal.headMap(FrameCodeConstant.SQUAD_TIME);

		if (fhmfTotalTailTemp != null) {
			// 返回此映射的部分视图，其键大于（或等于，如果 inclusive 为 true）fromKey.
			fhmfTotalTail = fhmfTotalTailTemp.tailMap(minFivePreStart);
		}

		if (fhmfTotalTail == null) {
			return null;
		}

		FlowHourMinFiveUnit unit = getFlowHourMinFiveUnit(minFivePreStart,
				fhmfTotalTail);
		if (unit != null) {
			unit.setDateKey(total.getDayKey());
		}
		return unit;

	}

	private FlowHourMinFiveUnit getFlowHourMinFiveUnit(String minFivePreStart,
			SortedMap<String, FlowHourMinFiveUnit> fhmfTotalTail) {

		FlowHourMinFiveUnit unit = null;
		Set keys = fhmfTotalTail.keySet();
		Iterator it = keys.iterator();
		String hourMinFive;

		while (it.hasNext()) {
			hourMinFive = (String) it.next();
			if (hourMinFive.equals(minFivePreStart)) {
				continue;
			}
			unit = (FlowHourMinFiveUnit) fhmfTotalTail.get(hourMinFive);
			// unit中只有traffic有值
			unit.setHourMinFive(hourMinFive);
			if (unit.getTraffic() > 0) {
				return unit;
			}
		}
		return null;

	}

	private FlowHourMinFiveUnit getFlowHourMinFiveTotalAfterExcept0230(
			FlowHourMinFiveTotal total, FlowHourMinFiveTotal totalAft,
			String minFivePreStart) {

		SortedMap<String, FlowHourMinFiveUnit> fhmfTotalTail = null;
		// SortedMap<String, FlowHourMinFiveUnit> fhmfTotalTailTemp = null;
		TreeMap<String, FlowHourMinFiveUnit> fhmfTotal = null;
		TreeMap<String, FlowHourMinFiveUnit> fhmfTotalAfter = null;
		FlowHourMinFiveUnit u = null;
		if (total == null) {
			return null;
		}

		fhmfTotal = total.getFlowHourMinTotal();

		// 如果时间大于运营日如0300，仅查找至当天0300后的缓存及下一天至0230
		// 查找当天0300后的缓存
		fhmfTotalTail = fhmfTotal.tailMap(minFivePreStart);

		if (fhmfTotalTail != null) {
			u = getFlowHourMinFiveUnit(minFivePreStart, fhmfTotalTail);
			if (u != null) {
				u.setDateKey(total.getDayKey());
				return u;
			}
		}

		if (totalAft == null) {
			return null;
		}

		fhmfTotalAfter = totalAft.getFlowHourMinTotal();

		// 当天没找到，下一天至0230缓存
		fhmfTotalTail = fhmfTotalAfter.headMap(FrameCodeConstant.SQUAD_TIME);
		if (fhmfTotalTail != null) {
			u = getFlowHourMinFiveUnit(minFivePreStart, fhmfTotalTail);
			if (u != null) {
				u.setDateKey(totalAft.getDayKey());
			}

		}

		return u;

	}

	/**
	 * 
	 * @param bufferFlow
	 *            缓存信息
	 * @param lineId
	 *            线路
	 * @param stationId
	 *            车站
	 * @param cardMainType
	 *            主卡类型
	 * @param cardSubType
	 *            子卡类型
	 * @param flag
	 *            进出站标志
	 * @param trafficDate
	 *            客流时间
	 * @param traffic
	 *            进出站流量累计数
	 * @param dateKey
	 *            客流日期
	 * @param hourMinFive
	 *            客流时间所属的5分钟时间范畴，如20071226041125 值为0410,20071226041525 值为0415
	 * @param stationCardKey
	 *            车站票卡格式如01010001
	 * @return
	 * @throws Exception
	 */
	public FlowResult handleFlowHourMinFiveByDate(
			TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlow,
			String lineId, String stationId, String cardMainType,
			String cardSubType, String flag, String trafficDate, int traffic,
			String dateKey, String hourMinFive, String stationCardKey)
			throws Exception {

		// 计算出5分钟客流
		FlowResult result = calculateHourMinFiveTraffic(bufferFlow, dateKey,
				stationCardKey, flag, hourMinFive, traffic);

		if (!result.getIsValid()) {
			logger.error("结果无效. 分钟客流量=" + result.getHTraffic());
			return null;
		}

		// 当前5分钟客流
		int hTraffic = result.getHTraffic();

		// 客流信息入库：5分钟客流、分钟总客流
		writeDB(lineId, stationId, cardMainType, cardSubType, flag,
				trafficDate, traffic, dateKey, hourMinFive, hTraffic);

		return result;

	}

	private void writeDB(String lineId, String stationId, String cardMainType,
			String cardSubType, String flag, String trafficDate, int traffic,
			String dateKey, String hourMinFive, int hTraffic) {

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("处理5分钟客流：日期：").append(dateKey).append(" 时间：")
				.append(hourMinFive).append(" 车站：").append(lineId)
				.append(stationId).append(" 票卡：").append(cardMainType)
				.append(cardSubType).append(" 进出站标志：").append(flag)
				.append(" 5分钟客流：").append(hTraffic).append("总客流：").append(traffic);
		logger.info(sBuilder.toString());

		sBuilder = null;

		// 写清分通讯客流分钟客流
		writeFlowHourMinFive(lineId, stationId, cardMainType, cardSubType,
				flag, trafficDate, hTraffic);

		// 写清分通讯客流分钟总客流
		writeFlowHourMinFiveTotal(lineId, stationId, cardMainType, cardSubType,
				flag, trafficDate, traffic);

		// 写应急指挥中心客流分钟客流
		if (MessageUtil.isNeeedEmergentTraffic()) {
			sBuilder = new StringBuilder();
			sBuilder.append("写应急指挥中心5分钟客流：日期：").append(dateKey).append(" 时间：")
					.append(hourMinFive).append(" 车站：").append(lineId)
					.append(stationId).append(" 票卡：").append(cardMainType)
					.append(cardSubType).append(" 进出站标志：").append(flag)
					.append(" 5分钟客流：").append(hTraffic);
			logger.info(sBuilder.toString());
			// 写应急指挥中心客流分钟客流
			writeFlowHourMinFiveForEmergentTraffic(lineId, stationId,
					cardMainType, cardSubType, flag, trafficDate, hTraffic);
			// 写应急指挥中心客流分钟总客流
			writeFlowHourMinFiveForEmergentTrafficTotal(lineId, stationId,
					cardMainType, cardSubType, flag, trafficDate, traffic);
		}
	}

	private boolean isDataDelay(int nextTotal) {
		return nextTotal > 0;

	}

	/**
	 * 处理客流数据滞留，如存在滞留客流，重新计算滞留客流时间点的最近下一个5分钟客流
	 * 
	 * @param dateKey
	 *            客流日期
	 * @param hourMinFive
	 *            客流时间的最近5分钟时间格式
	 * @param stationCardKey
	 *            车站票卡格式
	 * @param bufferFlow
	 *            进出站客流
	 * @return
	 */
	private Vector<FlowHourMinFiveTotal> getNextTrafficTotalV(String dateKey,
			String hourMinFive, String stationCardKey,
			TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlow) {

		Vector<FlowHourMinFiveTotal> totals = new Vector<FlowHourMinFiveTotal>();

		TreeMap<String, FlowHourMinFiveUnit> flowHourMinFiveTotal = null;

		FlowHourMinFiveTotal total = null;

		// 获取当前日期的下一天
		String dateKeyNext = DateHelper.getDateAfter(dateKey, 24 * 3600 * 1000);

		// 获取当前日期的对应车站票卡的汇总客流集合
		flowHourMinFiveTotal = getNextTrafficTotal(dateKey, stationCardKey,
				bufferFlow);

		if (flowHourMinFiveTotal != null) {
			total = new FlowHourMinFiveTotal();
			total.setDayKey(dateKey);
			total.setFlowHourMinTotal(flowHourMinFiveTotal);
			totals.add(total);
		}

		// 如果分钟不在00－0230之间，获取当前日期下一天对应车站票卡的汇总客流集合
		if (!isTimeBetween00And0230(hourMinFive)) {
			flowHourMinFiveTotal = getNextTrafficTotal(dateKeyNext,
					stationCardKey, bufferFlow);
			if (flowHourMinFiveTotal != null) {
				total = new FlowHourMinFiveTotal();
				total.setDayKey(dateKeyNext);
				total.setFlowHourMinTotal(flowHourMinFiveTotal);
				totals.add(total);
			}
		}

		return totals;
	}

	/**
	 * 获取当前日期的对应车站票卡的汇总客流集合
	 * 
	 * @param dateKey
	 * @param stationCardKey
	 * @param bufferFlow
	 * @return
	 */
	private TreeMap<String, FlowHourMinFiveUnit> getNextTrafficTotal(
			String dateKey, String stationCardKey,
			TreeMap<String, HashMap<String, FlowHourMinFive>> bufferFlow) {

		// 进出站客流不包含日期
		if (!bufferFlow.containsKey(dateKey)) {
			return null;
		}

		// 根据日期获取客流缓存
		HashMap<String, FlowHourMinFive> bufferFlowHourMinFive = (HashMap<String, FlowHourMinFive>) bufferFlow
				.get(dateKey);

		if (!bufferFlowHourMinFive.containsKey(stationCardKey)) {
			return null;
		}

		FlowHourMinFive flowHourMinFive = (FlowHourMinFive) bufferFlowHourMinFive
				.get(stationCardKey);

		TreeMap<String, FlowHourMinFiveUnit> flowHourMinFiveTotal = flowHourMinFive
				.getFlowHourMinFiveTotal();

		return flowHourMinFiveTotal;

	}

	/**
	 * 写应急指挥中心客流分钟客流
	 * 
	 * @param lineId
	 * @param stationId
	 * @param cardMainType
	 * @param cardSubType
	 * @param flag
	 * @param trafficDate
	 * @param hTraffic
	 */
	private void writeFlowHourMinFiveForEmergentTraffic(String lineId,
			String stationId, String cardMainType, String cardSubType,
			String flag, String trafficDate, int hTraffic) {

		EmergentTrafficLccDao dao = new EmergentTrafficLccDao();
		try {
			dao.writeTrafficForMin(lineId, stationId, cardMainType,
					cardSubType, flag, trafficDate, hTraffic);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 写清分通讯客流分钟客流
	 * 
	 * @param lineId
	 * @param stationId
	 * @param cardMainType
	 * @param cardSubType
	 * @param flag
	 * @param trafficDate
	 * @param hTraffic
	 */
	private void writeFlowHourMinFive(String lineId, String stationId,
			String cardMainType, String cardSubType, String flag,
			String trafficDate, int hTraffic) {

		TrafficLccDao dao = new TrafficLccDao();
		try {
			dao.writeTrafficForMin(lineId, stationId, cardMainType,
					cardSubType, flag, trafficDate, hTraffic);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 写应急指挥中心客流分钟总客流
	 * 
	 * @param lineId
	 * @param stationId
	 * @param cardMainType
	 * @param cardSubType
	 * @param flag
	 * @param trafficDate
	 * @param hTraffic
	 */
	private void writeFlowHourMinFiveForEmergentTrafficTotal(String lineId,
			String stationId, String cardMainType, String cardSubType,
			String flag, String trafficDate, int hTraffic) {

		EmergentTrafficLccDao dao = new EmergentTrafficLccDao();
		try {
			dao.writeTrafficForMinTotal(lineId, stationId, cardMainType,
					cardSubType, flag, trafficDate, hTraffic);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 写清分通讯客流分钟总客流
	 * 
	 * @param lineId
	 * @param stationId
	 * @param cardMainType
	 * @param cardSubType
	 * @param flag
	 * @param trafficDate
	 * @param hTraffic
	 */
	private void writeFlowHourMinFiveTotal(String lineId, String stationId,
			String cardMainType, String cardSubType, String flag,
			String trafficDate, int hTraffic) {

		TrafficLccDao dao = new TrafficLccDao();
		try {
			dao.writeTrafficForMinTotal(lineId, stationId, cardMainType,
					cardSubType, flag, trafficDate, hTraffic);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public static boolean isNeeedEmergentTraffic() {
		return FrameCodeConstant.isWriteEmergentTraffic
				& FrameCodeConstant.isWriteEmergentTrafficForDb;
	}
}
