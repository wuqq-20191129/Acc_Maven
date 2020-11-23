package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.FlowHourOrg;
import com.goldsign.commu.app.vo.PassageFlowVo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.MessageUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * @author zhangjh
 */
public class TrafficLccDao {

    private static Logger logger = Logger.getLogger(TrafficLccDao.class
            .getName());

    public TrafficLccDao() {
        super();
    }

    public Vector<FlowHourOrg> getFlowHourMinFiveTotal(String date, String flag)
            throws Exception {
        Vector<FlowHourOrg> bufferFlowHourMinFive = new Vector<FlowHourOrg>();
        String sql = "select line_id,station_id,traffic_datetime,card_main_type,card_sub_type,flag,traffic from " + FrameDBConstant.COM_COMMU_P + "cm_traffic_afc_min_total where substr(traffic_datetime,1,8)=? and flag=?";
        DbHelper dbHelper = null;
        String[] values = {date, flag};
        boolean result;
        FlowHourOrg vo;

        try {
            dbHelper = new DbHelper("",
                    FrameDBConstant.CM_DBCPHELPER.getConnection());
            result = dbHelper.getFirstDocument(sql, values);
            while (result) {
                vo = new FlowHourOrg();
                vo.setLineId(dbHelper.getItemValue("line_id"));
                vo.setStationId(dbHelper.getItemValue("station_id"));
                vo.setCardMainType(dbHelper.getItemValue("card_main_type"));
                vo.setCardSubType(dbHelper.getItemValue("card_sub_type"));
                vo.setTrafficDatetime(dbHelper.getItemValue("traffic_datetime"));
                vo.setFlag(dbHelper.getItemValue("flag"));
                vo.setTraffic(dbHelper.getItemIntValue("traffic"));
                bufferFlowHourMinFive.add(vo);
                result = dbHelper.getNextDocument();
            }
        } catch (Exception e) {
            PubUtil.handleException(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }

        return bufferFlowHourMinFive;

    }

    /**
     * 获取某一天的客流消息
     *
     * @param dateKey
     * @param flag
     * @return
     * @throws Exception
     */
    public Vector<FlowHourOrg> getFlowHourMinFive(String dateKey, String flag)
            throws Exception {
        Vector<FlowHourOrg> bufferFlowHourMinFive = new Vector<FlowHourOrg>();
        String sql = "select line_id,station_id,traffic_datetime,card_main_type,card_sub_type,flag,traffic from " + FrameDBConstant.COM_COMMU_P + "cm_traffic_afc_min where substr(traffic_datetime,1,8)=? and flag=?";
        DbHelper dbHelper = null;
        String[] values = {dateKey, flag};
        boolean result;
        FlowHourOrg vo;

        try {
            dbHelper = new DbHelper("",
                    FrameDBConstant.CM_DBCPHELPER.getConnection());
            result = dbHelper.getFirstDocument(sql, values);
            while (result) {
                vo = new FlowHourOrg();
                vo.setLineId(dbHelper.getItemValue("line_id"));
                vo.setStationId(dbHelper.getItemValue("station_id"));
                vo.setCardMainType(dbHelper.getItemValue("card_main_type"));
                vo.setCardSubType(dbHelper.getItemValue("card_sub_type"));
                vo.setTrafficDatetime(dbHelper.getItemValue("traffic_datetime"));
                vo.setFlag(dbHelper.getItemValue("flag"));
                vo.setTraffic(dbHelper.getItemIntValue("traffic"));
                bufferFlowHourMinFive.add(vo);
                result = dbHelper.getNextDocument();
            }
        } catch (Exception e) {
            PubUtil.handleException(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }

        return bufferFlowHourMinFive;

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
     * @return
     * @throws Exception
     */
    public int writeTrafficForMin(String lineId, String stationId,
                                  String cardMainType, String cardSubType, String flag,
                                  String trafficDate, int hTraffic) throws Exception {
        String sql;
        String time = MessageUtil.getDateTimeForMinFive(trafficDate);
        Object[] values = {lineId, stationId, cardMainType, cardSubType, flag,
                time, new Integer(hTraffic)};
        Object[] values1 = {new Integer(hTraffic), lineId, stationId,
                cardMainType, cardSubType, flag, time};
        int ret = 0;
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("TrafficLccDao",
                    FrameDBConstant.CM_DBCPHELPER.getConnection());

            sql = "update " + FrameDBConstant.COM_COMMU_P + "cm_traffic_afc_min set traffic=? where line_id =? and station_id =? and card_main_type =? and card_sub_type =? and flag =? and traffic_datetime =?";
            ret = dbHelper.executeUpdate(sql, values1);
            if (ret == 0) {
                sql = "insert into " + FrameDBConstant.COM_COMMU_P + "cm_traffic_afc_min(line_id,station_id,card_main_type,card_sub_type,flag,traffic_datetime,traffic) values(?,?,?,?,?,?,?)";
                ret = dbHelper.executeUpdate(sql, values);
            }
        } catch (Exception e) {
            PubUtil.handleException(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);

        }
        return ret;

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
     * @return
     * @throws Exception
     */
    public int writeTrafficForMinTotal(String lineId, String stationId,
                                       String cardMainType, String cardSubType, String flag,
                                       String trafficDate, int hTraffic) throws Exception {
        String sql;
        String time = MessageUtil.getDateTimeForMinFive(trafficDate);
        Object[] values = {lineId, stationId, cardMainType, cardSubType, flag,
                time, new Integer(hTraffic)};
        Object[] values1 = {new Integer(hTraffic), lineId, stationId,
                cardMainType, cardSubType, flag, time};
        int ret = 0;
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("TrafficLccDao",
                    FrameDBConstant.CM_DBCPHELPER.getConnection());

            sql = "update " + FrameDBConstant.COM_COMMU_P + "cm_traffic_afc_min_total set traffic=? where line_id =? and station_id =? and card_main_type =? and card_sub_type =? and flag =? and traffic_datetime =?";
            ret = dbHelper.executeUpdate(sql, values1);
            if (ret == 0) {
                sql = "insert into " + FrameDBConstant.COM_COMMU_P + "cm_traffic_afc_min_total(line_id,station_id,card_main_type,card_sub_type,flag,traffic_datetime,traffic) values(?,?,?,?,?,?,?)";
                ret = dbHelper.executeUpdate(sql, values);
            }
        } catch (Exception e) {
            PubUtil.handleException(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);

        }
        return ret;

    }

    /**
     * 根据分钟客流计算总客流
     *
     * @param condition
     * @throws Exception
     */
    public void countTrafficTotal(ArrayList<PassageFlowVo> condition) throws Exception {
        if (condition == null || condition.size() == 0) {
            return;
        }
        DbHelper dbHelper = null;
        String sql = "";
        boolean result = false;
        try {
            dbHelper = new DbHelper("TrafficLccDao",
                    FrameDBConstant.CM_DBCPHELPER.getConnection());
            sql = "  select nvl(sum(t.traffic),0) traffic from " + FrameDBConstant.COM_COMMU_P + "cm_traffic_afc_min t " +
                    " where t.line_id = ? and t.station_id = ?  and t.card_main_type = ? and t.card_sub_type = ?" +
                    "  and t.flag = ? and t.traffic_datetime >= ? and t.traffic_datetime <= ?";
            for (PassageFlowVo vo : condition) {
                Object[] values = {vo.getLineId(), vo.getStationId(), vo.getCardMainType(), vo.getCardSubType(), vo.getFlag(), vo.getTotalBeginTime(), vo.getTotalEndTime()};
//                result = dbHelper.executeUpdate(sql, values);
                result = dbHelper.getFirstDocument(sql, values);
                while (result) {
                    vo.setTrafficNum(dbHelper.getItemIntValue("traffic"));
                    result = dbHelper.getNextDocument();
                }
                logger.info("TotalBeginTime=" + vo.getTotalBeginTime() + ",TotalEndTime" + vo.getTotalEndTime() + ",TrafficNum=" + vo.getTrafficNum());
                dbHelper.close();
            }
        } catch (Exception e) {
            PubUtil.handleException(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);

        }
    }
}
