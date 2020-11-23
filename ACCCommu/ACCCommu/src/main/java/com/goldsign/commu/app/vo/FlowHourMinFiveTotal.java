package com.goldsign.commu.app.vo;

import java.util.TreeMap;

public class FlowHourMinFiveTotal {

	private String dayKey;
	private TreeMap<String, FlowHourMinFiveUnit> flowHourMinTotal;

	public String getDayKey() {
		return dayKey;
	}

	public void setDayKey(String dayKey) {
		this.dayKey = dayKey;
	}

	public TreeMap<String, FlowHourMinFiveUnit> getFlowHourMinTotal() {
		return flowHourMinTotal;
	}

	public void setFlowHourMinTotal(TreeMap<String, FlowHourMinFiveUnit> flowHourMinTotal) {
		this.flowHourMinTotal = flowHourMinTotal;
	}

	public FlowHourMinFiveTotal() {
		super();
	}
}
