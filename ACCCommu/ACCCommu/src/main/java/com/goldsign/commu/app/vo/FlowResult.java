package com.goldsign.commu.app.vo;

public class FlowResult {

	private int hTraffic = 0;
	private boolean inserted = true;
	private boolean isValid = false;

	public FlowResult() {
		super();
	}

	public int getHTraffic() {
		return this.hTraffic;
	}

	public void setHTraffic(int hTraffic) {
		this.hTraffic = hTraffic;
	}

	public boolean getInserted() {
		return this.inserted;
	}

	public void setInserted(boolean inserted) {
		this.inserted = inserted;
	}

	public boolean getIsValid() {
		return this.isValid;
	}

	public void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}

}
