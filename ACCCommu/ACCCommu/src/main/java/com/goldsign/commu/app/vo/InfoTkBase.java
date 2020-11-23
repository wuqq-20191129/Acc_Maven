package com.goldsign.commu.app.vo;

public class InfoTkBase {

    /**
     * 流水号
     */
    private int waterNo;
    /**
     * 站点代码
     */
    protected String deptId;
    /**
     * 票卡类型
     */
    protected String ticketTypeId;
    /**
     * 面值
     */
    protected int value;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(String ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getWaterNo() {
        return waterNo;
    }

    public void setWaterNo(int waterNo) {
        this.waterNo = waterNo;
    }

}
