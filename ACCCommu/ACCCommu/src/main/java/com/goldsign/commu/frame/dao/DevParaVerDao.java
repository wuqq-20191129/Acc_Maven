package com.goldsign.commu.frame.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameParameterConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.DevParaVerInfoVo;
import com.goldsign.commu.frame.vo.DevParaVerInfosVo;
import com.goldsign.commu.frame.vo.DevParaVerVo;
import com.goldsign.lib.db.util.DbHelper;

/**
 * @author hejj
 */
public class DevParaVerDao {

    private static Logger logger = Logger.getLogger(DevParaVerDao.class
            .getName());

    /**
     * SQL标签
     */
    private static String sqlStr1 = "update " + FrameDBConstant.COM_COMMU_P + "cm_dev_para_ver_cur set version_no=? ,report_date=? "
            + "where line_id=? and station_id=? and dev_type_id=? and device_id =? "
            + "and parm_type_id=? and record_flag=? ";
    /**
     * SQL标签
     */
    private static String sqlStr2 = "insert into " + FrameDBConstant.COM_COMMU_P + "cm_dev_para_ver_his(water_no,line_id,station_id,dev_type_id,device_id,"
            + "parm_type_id,record_flag,version_no,report_date,remark) values(" + FrameDBConstant.COM_COMMU_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "cm_dev_para_ver_his.nextval,?,?,?,?,?,?,?,?,?) ";
    /**
     * SQL标签
     */
    private static String sqlStr3 = "select water_no,lcc_line_id,line_id,station_id,dev_type_id,device_id,status,"
            + "query_date,send_date,operator_id,"
            + "remark from " + FrameDBConstant.COM_ST_P + "op_cm_dev_para_ver_qry where status=? ";
    /**
     * SQL标签
     */
    private static String sqlStr4 = "update " + FrameDBConstant.COM_ST_P + "op_cm_dev_para_ver_qry set status=? ,send_date=? ,remark=? where status=? ";
    /**
     * SQL标签
     */
    private static String sqlStr11 = "insert into " + FrameDBConstant.COM_COMMU_P + "cm_dev_para_ver_cur(line_id,station_id,dev_type_id,device_id,"
            + "parm_type_id,record_flag,version_no,report_date,remark) values(?,?,?,?,?,?,?,?,?) ";

    //noted by zhongzq 20190531 报文数量量过大导致游标超过最大值 弃用
//	public int writeInfo(DbHelper dbHelper, DevParaVerInfosVo vo)
//			throws Exception {
//		DevParaVerInfoVo ivo;
//		int n = 0;
//		int size = vo.getParams().size();
//		 logger.info("参数重复次数N1:[" + size + "]");
//		for (int i = 0; i < size; i++) {
//			ivo = vo.getParams().get(i);
//			ivo.setLineId(vo.getLineId());
//			ivo.setStationId(vo.getStationId());
//			ivo.setDevTypeId(vo.getDevTypeId());
//			ivo.setDeviceId(vo.getDeviceId());
//			ivo.setReportDate(vo.getReportDate());
//			n += insertOrUpdateCur(dbHelper, ivo);
//			insertHis(dbHelper, ivo);
//		}
//		return n;
//
//	}
    //add by zhongzq 20190531 解决报文数量量过大导致游标超过最大值问题
    public int writeInfoForBatch(DbHelper dbHelper, DevParaVerInfosVo vo)
            throws Exception {
        DevParaVerInfoVo ivo;
        int n = 0;
        int size = vo.getParams().size();
//        logger.info("参数重复次数N1:[" + size + "]");
        ArrayList hisValues = new ArrayList();
        ArrayList insertValues = new ArrayList();
        for (int i = 0; i < size; i++) {
            ivo = vo.getParams().get(i);
            ivo.setLineId(vo.getLineId());
            ivo.setStationId(vo.getStationId());
            ivo.setDevTypeId(vo.getDevTypeId());
            ivo.setDeviceId(vo.getDeviceId());
            ivo.setReportDate(vo.getReportDate());
//            n += updateCurAndGrepInsert(dbHelper, ivo, insertValues);
            n += insertOrUpdateCur(dbHelper, ivo);
            addHisValues(hisValues, ivo);
        }
        logger.info(" 更新cm_dev_para_ver_cur表[" + n + "]条数据,插入[" + (hisValues.size() - n) + "]条数据");
//        insertCurForBatch(dbHelper, insertValues);
        insertHisForBatch(dbHelper, hisValues);
        return n;

    }

    private void insertCurForBatch(DbHelper dbHelper, ArrayList insertValues) throws Exception {
        if (insertValues == null || insertValues.size() == 0) {
            return;
        }
        logger.info(" 需要插入cm_dev_para_ver_cur表[" + insertValues.size() + "]条数据！");
        Object[] value = null;
        dbHelper.prepareStatement(sqlStr11);
        int batchSize = 100;
        int n = 0;

        for (int i = 0; i < insertValues.size(); i++) {
            value = (Object[]) insertValues.get(i);
            dbHelper.addBatch(value);
            if (i % batchSize == 0 && i != 0) {
                n = n + dbHelper.executeBatch();
                logger.info(" 批量插入[" + batchSize + "]条数据！+返回值=" + n);
            }
        }
        n = n + dbHelper.executeBatch();
        logger.info(" 插入cm_dev_para_ver_cur表[" + insertValues.size() + "]条数据！最后一批返回值=" + n);
    }

    private void addHisValues(ArrayList hisValues, DevParaVerInfoVo vo) throws ParseException {
        Object[] values = {vo.getLineId(), vo.getStationId(),
                vo.getDevTypeId(), vo.getDeviceId(), vo.getParmTypeId(),
                vo.getRecordFlag(), vo.getVersionNo(),
                DateHelper.dateStrToSqlTimestamp1(vo.getReportDate()), ""};
        hisValues.add(values);
    }

    public int insertOrUpdateCur(DbHelper dbHelper, DevParaVerInfoVo vo)
            throws Exception {

        Object[] values = {vo.getVersionNo(),
                DateHelper.dateStrToSqlTimestamp1(vo.getReportDate()), vo.getLineId(),
                vo.getStationId(), vo.getDevTypeId(), vo.getDeviceId(),
                vo.getParmTypeId(), vo.getRecordFlag()};
        int n = dbHelper.executeUpdate(sqlStr1, values);
        //zhongziqi 为了解决游标耗尽问题，DbHelper封装的executeUpdate方法并没有手工释放PreparedStatement
        dbHelper.close();
//        logger.warn(" 更新cm_dev_para_ver_cur表[" + n + "]条数据！");
        if (n == 0) {
            Object[] values_1 = {vo.getLineId(), vo.getStationId(),
                    vo.getDevTypeId(), vo.getDeviceId(), vo.getParmTypeId(),
                    vo.getRecordFlag(), vo.getVersionNo(),
                    DateHelper.dateStrToSqlTimestamp1(vo.getReportDate()), ""};
            n = dbHelper.executeUpdate(sqlStr11, values_1);
            dbHelper.close();
//            logger.warn(" 插入cm_dev_para_ver_cur表[" + n + "]条数据！");
        }
        return n;
    }

    public int updateCurAndGrepInsert(DbHelper dbHelper, DevParaVerInfoVo vo, ArrayList insertValues)
            throws Exception {

        Object[] values = {vo.getVersionNo(),
                DateHelper.dateStrToSqlTimestamp1(vo.getReportDate()), vo.getLineId(),
                vo.getStationId(), vo.getDevTypeId(), vo.getDeviceId(),
                vo.getParmTypeId(), vo.getRecordFlag()};
        int n = dbHelper.executeUpdate(sqlStr1, values);
        //为了解决游标耗尽问题，DbHelper封装的executeUpdate方法并没有手工释放PreparedStatement
        dbHelper.close();
//        logger.debug(" 更新cm_dev_para_ver_cur表[" + n + "]条数据！");
        if (n == 0) {
            logger.info(vo.getLineId() + vo.getStationId() + vo.getDevTypeId() + vo.getDeviceId() + vo.getParmTypeId() + vo.getRecordFlag());
            Object[] values_1 = {vo.getLineId(), vo.getStationId(),
                    vo.getDevTypeId(), vo.getDeviceId(), vo.getParmTypeId(),
                    vo.getRecordFlag(), vo.getVersionNo(),
                    DateHelper.dateStrToSqlTimestamp1(vo.getReportDate()), ""};
            insertValues.add(values_1);
            n = dbHelper.executeUpdate(sqlStr11, values_1);
//			logger.warn(" 插入cm_dev_para_ver_cur表[" + n + "]条数据！");
        }
        return n;
    }


    public int insertHis(DbHelper dbHelper, DevParaVerInfoVo vo)
            throws Exception {

        Object[] values = {vo.getLineId(), vo.getStationId(),
                vo.getDevTypeId(), vo.getDeviceId(), vo.getParmTypeId(),
                vo.getRecordFlag(), vo.getVersionNo(),
                DateHelper.dateStrToSqlTimestamp1(vo.getReportDate()), ""};
        int n = dbHelper.executeUpdate(sqlStr2, values);
        logger.warn(" 插入cm_dev_para_ver_his表[" + n + "]条数据！");
        return n;
    }

    public void insertHisForBatch(DbHelper dbHelper, ArrayList hisValues)
            throws Exception {
        if (hisValues == null || hisValues.size() == 0) {
            return;
        }
        dbHelper.prepareStatement(sqlStr2);
        int batchSize = 100;
        int n = 0;
        for (int i = 0; i < hisValues.size(); i++) {
            Object[] value = (Object[]) hisValues.get(i);
            dbHelper.addBatch(value);
            if (i % batchSize == 0 && i != 0) {
                n = n + dbHelper.executeBatch();
                logger.warn(" 批量插入[" + batchSize + "]条数据！+返回值=" + n);
            }
        }
        n = n + dbHelper.executeBatch();
        logger.warn(" 插入cm_dev_para_ver_his表[" + hisValues.size() + "]条数据！最后一批返回值=" + n);
        return;
    }

    //    public int insertHisForBatch(DbHelper dbHelper, ArrayList hisValues)
//            throws Exception {
//
//        Object[] values = { vo.getLineId(), vo.getStationId(),
//                vo.getDevTypeId(), vo.getDeviceId(), vo.getParmTypeId(),
//                vo.getRecordFlag(), vo.getVersionNo(),
//                DateHelper.dateStrToSqlTimestamp1(vo.getReportDate()), "" };
//        int n = dbHelper.executeUpdate(sqlStr2, values);
//        logger.warn(" 插入cm_dev_para_ver_his表[" + n + "]条数据！");
//        return n;
//    }
    public Vector<DevParaVerVo> queryUnhandleRequestForGet(DbHelper dbHelper)
            throws Exception {
        boolean result;

        Vector<DevParaVerVo> v = new Vector<DevParaVerVo>();
        DevParaVerVo vo;
        // 请求待处理
        Object[] values = {FrameParameterConstant.devParaVerStatusUnhandled};

        result = dbHelper.getFirstDocument(sqlStr3, values);
        while (result) {
            vo = getResultRecord(dbHelper);
            v.add(vo);
            result = dbHelper.getNextDocument();
        }
        // logger.warn(" 查询op_cm_dev_para_ver_qry表[" + v.size() + "]条数据！");
        return v;
    }

    public int queryUnhandleRequestForUpdate(DbHelper dbHelper)
            throws Exception {

        Object[] values = {FrameParameterConstant.devParaVerStatusInQueue,
                DateHelper.getCurrentSqlTimestamp(), "查询请求放入消息队列",
                // CharUtil.GbkToIso("查询请求放入消息队列"),
                FrameParameterConstant.devParaVerStatusUnhandled};

        int n = dbHelper.executeUpdate(sqlStr4, values);
        // logger.warn(" 更新op_cm_dev_para_ver_qry表[" + n + "]条数据！");
        return n;
    }

    private DevParaVerVo getResultRecord(DbHelper dbHelper) throws Exception {
        DevParaVerVo vo = new DevParaVerVo();
        vo.setWaterNo(dbHelper.getItemValue("water_no"));
        vo.setLineId(dbHelper.getItemValue("line_id"));
        vo.setStationId(dbHelper.getItemValue("station_id"));
        vo.setDevTypeId(dbHelper.getItemValue("dev_type_id"));
        vo.setDeviceId(dbHelper.getItemValue("device_id"));
        vo.setStatus(dbHelper.getItemValue("status"));
        vo.setOperatorId(dbHelper.getItemValue("operator_id"));
        vo.setQueryDate(dbHelper.getItemValue("query_date"));
        vo.setSend_date(dbHelper.getItemValue("send_date"));
        vo.setRemark(dbHelper.getItemValue("remark"));
        vo.setLccLineId(dbHelper.getItemValue("lcc_line_id"));

        return vo;

    }

    public Vector<DevParaVerVo> queryUnhandleRequest() throws Exception {

        DbHelper dbHelper = null;
        Vector<DevParaVerVo> v = null;

        try {
            dbHelper = new DbHelper("dataDbUtil",
                    FrameDBConstant.OP_DBCPHELPER.getConnection());
            dbHelper.setAutoCommit(false);
            v = queryUnhandleRequestForGet(dbHelper);

            queryUnhandleRequestForUpdate(dbHelper);

            dbHelper.commitTran();
            // dbHelper.setAutoCommit(true);
        } catch (Exception e) {
            PubUtil.handleExceptionForTran(e, logger, dbHelper);
        } finally {
            PubUtil.finalProcessForTran(dbHelper);
        }

        return v;
    }
}
