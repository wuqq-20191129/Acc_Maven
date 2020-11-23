package com.goldsign.commu.app.vo;

import java.util.Vector;

import com.goldsign.commu.frame.constant.FrameCodeConstant;

public class FlowHour {
	// 序号对应的是小时整数值，值为小时总客流

	Vector<FlowHourUnit> flowHourTotal = new Vector<FlowHourUnit>();
	// 序号对应的是小时整数值，值为小时客流
	Vector<FlowHourUnit> flowHour = new Vector<FlowHourUnit>();
	// 序号对应的是小时整数值，值为允许的数据库操作
	Vector<FlowFlagInserted> flagInserted = new Vector<FlowFlagInserted>();

	// private static Logger logger =
	// Logger.getLogger(FlowHour.class.getName());

	public FlowHour() {
		this.init();

	}

	private void init() {
		for (int i = 0; i < 24; i++) {
			this.flowHour.add(new FlowHourUnit());
			this.flowHourTotal.add(new FlowHourUnit());
			this.flagInserted.add(new FlowFlagInserted());

		}

	}

	public void calulateFlowHourTotal() {
		FlowHourUnit fhu;
		FlowHourUnit fhu1;
		int total;
		for (int i = 0; i < this.flowHour.size(); i++) {
			fhu = (FlowHourUnit) this.flowHour.get(i);
			if (!this.isLegalTraffic(fhu.getTraffic())) {
				continue;
			}
			total = this.getFlowHourTotal(i);
			fhu1 = (FlowHourUnit) this.flowHourTotal.get(i);
			fhu1.setTraffic(total);

			// logger.error("calulateFlowHourTotal i="+i+" total="+total);

		}
	}

	private int getFlowHourTotal(int i) {
		int total = 0;
		FlowHourUnit fhu;
		for (int j = 0; j <= i; j++) {
			fhu = (FlowHourUnit) this.flowHour.get(j);
			total += fhu.getTraffic();
		}
		return total;

	}

	public void setFlowHour(FlowHourOrg vo) {
		String datetime = vo.getTrafficDatetime();
		String hh = datetime.substring(8);
		int ihh = Integer.parseInt(hh);
		int hTraffic = vo.getTraffic();
		// 设置小时客流
		FlowHourUnit fhu = (FlowHourUnit) this.flowHour.get(ihh);
		fhu.setTraffic(hTraffic);
		// 设置数据库操作应是update,而不是insert
		FlowFlagInserted inserted = (FlowFlagInserted) this.flagInserted
				.get(ihh);
		inserted.setInserted(false);

		// logger.error("ihh="+ihh+" hTraffic="+hTraffic);

	}

	public FlowResult calculateFlowHour(int hh, int hTotalTraffic) {
		FlowResult result = new FlowResult();
		// 如果当前小时客流非法不做处理
		if (!this.setFlowHourUnit(hh, hTotalTraffic)) {
			return result;
		}
		int hTraffic = ((FlowHourUnit) this.flowHour.get(hh)).getTraffic();
		FlowFlagInserted ffi = (FlowFlagInserted) this.flagInserted.get(hh);
		boolean inserted = ffi.getInserted();
		if (this.isLegalTraffic(hTraffic)) {
			result.setHTraffic(hTraffic);
			result.setInserted(inserted);
			if (inserted)// 对于每个小时客流，仅作一次插入，其余为更新
			{
				ffi.setInserted(false);
			}
			result.setIsValid(true);
		}
		return result;

	}

	public FlowResult calculateFlowHour(int hh, int hTotalTraffic,
			FlowHour fhPre) {
		FlowResult result = new FlowResult();
		// 如果当前小时客流非法不做处理
		if (!this.setFlowHourUnit(hh, hTotalTraffic, fhPre)) {
			return result;
		}

		int hTraffic = ((FlowHourUnit) this.flowHour.get(hh)).getTraffic();
		FlowFlagInserted ffi = (FlowFlagInserted) this.flagInserted.get(hh);
		boolean inserted = ffi.getInserted();
		if (this.isLegalTraffic(hTraffic)) {
			result.setHTraffic(hTraffic);
			result.setInserted(inserted);
			if (inserted)// 对于每个小时客流，仅作一次插入，其余为更新
			{
				ffi.setInserted(false);
			}
			result.setIsValid(true);
		}
		return result;

	}

	private boolean setFlowHourUnit(int hh, int hTotalTraffic) {
		// 获取当前小时对应的总客流对象
		FlowHourUnit fhu = (FlowHourUnit) this.flowHourTotal.get(hh);
		// 设置当前小时对应的总客流

		fhu.setTraffic(hTotalTraffic);
		int hTraffic = this.getHourTraffic(hh, hTotalTraffic);
		if (this.isLegalTraffic(hTraffic)) {
			FlowHourUnit fhu1 = (FlowHourUnit) this.flowHour.get(hh);
			fhu1.setTraffic(hTraffic);
			return true;
		}
		return false;

	}

	private boolean setFlowHourUnit(int hh, int hTotalTraffic, FlowHour fhPre) {
		// 获取当前小时对应的总客流对象
		FlowHourUnit fhu = (FlowHourUnit) this.flowHourTotal.get(hh);
		// 设置当前小时对应的总客流

		fhu.setTraffic(hTotalTraffic);

		int hTraffic = this.getHourTraffic(hh, hTotalTraffic, fhPre);
		if (this.isLegalTraffic(hTraffic)) {
			FlowHourUnit fhu1 = (FlowHourUnit) this.flowHour.get(hh);
			fhu1.setTraffic(hTraffic);
			return true;
		}
		return false;

	}

	private int getHourTraffic(int hh, int hTotalTraffic) {
		int hTraffic;
		int hPreTotalTraffic;
		if (hh == 0) {
			hTraffic = hTotalTraffic;
		} else {
			hPreTotalTraffic = ((FlowHourUnit) this.flowHourTotal.get((hh - 1)))
					.getTraffic();
			hTraffic = hTotalTraffic - hPreTotalTraffic;
		}
		return hTraffic;

	}

	// 求出与运营时间最接近的5分钟时间，如运营日是0200则结果应是0200 ;运营日是0216则结果应是0220;运营日是0256则结果应是0300

	public int getMinFiveFirstInSquadTime() {
		String squadHour = FrameCodeConstant.SQUAD_TIME.substring(0, 2);
		return Integer.parseInt(squadHour);

	}

	private boolean isFirstInSquadTime(int hh) {
		int first = this.getMinFiveFirstInSquadTime();
		if (hh == first) {
			return true;
		}
		return false;
	}

	private int getHourTraffic(int hh, int hTotalTraffic, FlowHour fhPre) {
		int hTraffic;
		int hPreTotalTraffic;
		if (this.isFirstInSquadTime(hh))// 运营时间第一个小时客流与总客流相同)
		{
			hTraffic = hTotalTraffic;
		} else {
			if (hh == 0) {// 自然日的第一个小时客流需使用上一个自然日的小时客流计算
				hPreTotalTraffic = ((FlowHourUnit) fhPre.flowHourTotal.get(23))
						.getTraffic();
				hTraffic = hTotalTraffic - hPreTotalTraffic;

			} else {// 当小时客流＝当前总客流－上一个小时总客流
				hPreTotalTraffic = ((FlowHourUnit) this.flowHourTotal
						.get((hh - 1))).getTraffic();
				hTraffic = hTotalTraffic - hPreTotalTraffic;
			}
		}
		return hTraffic;

	}

	private boolean isLegalTraffic(int traffic) {
		if (traffic <= 0) {
			// logger.error("isLegalTraffic is false traffic="+traffic);
			return false;
		}
		return true;
	}
}
