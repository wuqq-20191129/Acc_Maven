package com.goldsign.commu.app.vo;

public class FlowHourMinFiveUnit {

	private int traffic = 0;
	private String hourMinFive = "";
	private String dateKey = "";

        public FlowHourMinFiveUnit(int traffic, String hourMinFive, String dateKey){
            this.traffic = traffic;
            this.hourMinFive = hourMinFive;
            this.dateKey = dateKey;
        }
        
	public String getDateKey() {
		return dateKey;
	}

	public void setDateKey(String dateKey) {
		this.dateKey = dateKey;
	}

	public String getHourMinFive() {
		return hourMinFive;
	}

	public void setHourMinFive(String hourMinFive) {
		this.hourMinFive = hourMinFive;
	}

	public int getTraffic() {
		return traffic;
	}

	public void setTraffic(int traffic) {
		this.traffic = traffic;
	}

	public FlowHourMinFiveUnit() {
		super();
	}

    @Override
    public String toString() {
        return "FlowHourMinFiveUnit{" + "traffic=" + traffic + ", hourMinFive=" + hourMinFive + ", dateKey=" + dateKey + '}';
    }
        
        
}
