package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.DeviceEvent;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.lib.db.util.DbHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
public class Message11Dao {

    private static Logger logger = Logger.getLogger(Message11Dao.class
            .getName());
    private static String sqlStr = "insert into "+FrameDBConstant.COM_COMMU_P+"cm_dev_event(event_datetime,line_id,station_id,dev_type_id,device_id,event_id,event_argument) values(?,?,?,?,?,?,?)";

    public static void recordDeviceEvent(DbHelper otherDbHelper,
            Vector<DeviceEvent> eventV) throws Exception {
        // logger.info(DateHelper.datetimeToString(new Date()) +
        // "  com_dev_event增加记录数 " + eventV.size());

        int num = eventV.size();
        try {
            otherDbHelper.setAutoCommit(false);
            List<Object> values = new ArrayList<Object>();
            for (int i = 0; i < num; i++) {

                DeviceEvent de = eventV.get(i);
                values.clear();
                values.add(de.getEventDatetime());
                values.add(de.getLineId());
                values.add(de.getStationId());
                values.add(de.getDevTypeId());
                values.add(de.getDeviceId());
                values.add(de.getEventId());
                logger.info("de.getEventArgument():" + CharUtil.IsoToGbk(de.getEventArgument()));
                values.add(CharUtil.IsoToGbk(de.getEventArgument()));
                otherDbHelper.executeUpdate(sqlStr, values.toArray());
            }
            otherDbHelper.commitTran();
            otherDbHelper.setAutoCommit(true);
        } catch (SQLException e) {
            otherDbHelper.rollbackTran();
            otherDbHelper.setAutoCommit(true);
            logger.error("数据库异常,错误代码" + e.getErrorCode() + " 消息", e);
            throw e;
        } catch (Exception e) {
            otherDbHelper.rollbackTran();
            otherDbHelper.setAutoCommit(true);
        }

    }
}
