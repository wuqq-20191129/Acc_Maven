package com.goldsign.commu.app.dao;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.MessageUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 *
 * @author zhangjh
 */
public class EmergentTrafficLccDao {

    private static Logger logger = Logger.getLogger(EmergentTrafficLccDao.class
            .getName());

    public EmergentTrafficLccDao() {
        super();
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
     * @return
     * @throws Exception
     */
    public int writeTrafficForMin(String lineId, String stationId,
            String cardMainType, String cardSubType, String flag,
            String trafficDate, int hTraffic) throws Exception{
        String sql;
        String time = MessageUtil.getDateTimeForMinFive(trafficDate);
        Object[] values = {lineId, stationId, cardMainType, cardSubType, flag,
            time, Integer.valueOf(hTraffic)};
        Object[] values1 = {new Integer(hTraffic), lineId, stationId,
            cardMainType, cardSubType, flag, time};
        int ret = 0;
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("EmergentTrafficLccDao",
                    FrameDBConstant.OP_DBCPHELPER.getConnection());

            sql = "update "+FrameDBConstant.COM_ST_P+"op_com_traffic_lcc_min set traffic=? where line_id =? and station_id =? and card_main_type =? and card_sub_type =? and flag =? and traffic_datetime =?";
            ret = dbHelper.executeUpdate(sql, values1);
            if (ret == 0) {
                sql = "insert into "+FrameDBConstant.COM_ST_P+"op_com_traffic_lcc_min(line_id,station_id,card_main_type,card_sub_type,flag,traffic_datetime,traffic) values(?,?,?,?,?,?,?)";
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
     * 写应急指挥中心客流分钟总客流
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
            time,  Integer.valueOf(hTraffic)};
        Object[] values1 = {new Integer(hTraffic), lineId, stationId,
            cardMainType, cardSubType, flag, time};
        int ret = 0;
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("EmergentTrafficLccDao",
                    FrameDBConstant.OP_DBCPHELPER.getConnection());

            sql = "update "+FrameDBConstant.COM_ST_P+"op_com_traffic_lcc_min_total set traffic=? where line_id =? and station_id =? and card_main_type =? and card_sub_type =? and flag =? and traffic_datetime =?";
            ret = dbHelper.executeUpdate(sql, values1);
            if (ret == 0) {
                sql = "insert into "+FrameDBConstant.COM_ST_P+"op_com_traffic_lcc_min_total(line_id,station_id,card_main_type,card_sub_type,flag,traffic_datetime,traffic) values(?,?,?,?,?,?,?)";
                ret = dbHelper.executeUpdate(sql, values);
            }
        } catch (Exception e) {
            PubUtil.handleException(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);

        }
        return ret;

    }
}
