/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.BusDataUpLoadVo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * @datetime 2018-7-3 11:49:37
 * @author lind
 * 一卡通接口数据
 */
public class BusDataDao {

    private static Logger logger = Logger.getLogger(BusDataDao.class.getName());

    public boolean judgeWaterNoExited(String banlanceWaterNo, String fileType)
            throws SQLException {
        boolean flag = false;
        List<String> values = new ArrayList<String>();
        values.add(banlanceWaterNo);
        values.add(fileType);
        logger.info("banlanceWaterNo:" + banlanceWaterNo + ",fileType:" + fileType);

        DbHelper dbHelper = null;
        int counts = 0;
        try {
            dbHelper = new DbHelper("BusDataDao",
                    FrameDBConstant.CM_DBCPHELPER.getConnection());

            String sql = "select count(*) counts from " + FrameDBConstant.COM_COMMU_P + "cm_log_busdata_file where balance_water_no=? and file_type=?";
            boolean result = dbHelper.getFirstDocument(sql, values.toArray());

            if (result) {
                counts = dbHelper.getItemIntValue("counts");
                if (counts > 0) {
                    flag = true;
                }
            }
            logger.debug("counts:" + counts);

        } catch (SQLException e) {
            logger.info("--查询一卡通接口文件记录出错：", e);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return flag;
    }

    //abandon     modify by zhongziqi 20180928
//    public String query() throws SQLException {
//        String balanceWaterNo = "";
//        boolean result = false;
//        DbHelper dbHelper = null;
//        try {
//            dbHelper = new DbHelper("BusDataDao", FrameDBConstant.OP_DBCPHELPER.getConnection());
//
//            String sql = "select balance_water_no from " + FrameDBConstant.COM_ST_P + "st_sys_flow_current where rownum=1";
//            result = dbHelper.getFirstDocument(sql);
//            if (result) {
//                balanceWaterNo = dbHelper.getItemValue("balance_water_no");
//            }
//
//        } catch (SQLException e) {
//            logger.error("--查询清算流水号出现异常：", e);
//        } finally {
//            PubUtil.finalProcess(dbHelper);
//        }
//        return balanceWaterNo;
//    }


    /*
    查询未获取一卡通清算文件的记录
    */
    public Map<String, String> busDataMap() throws SQLException {
        boolean result = false;
        Map<String, String> map = new HashMap<String, String>();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("BusDataDao", FrameDBConstant.OP_DBCPHELPER.getConnection());

            String sql = "select distinct export_file_name, import_file_name, import_flag, balance_water_no from "
                    + FrameDBConstant.COM_ST_P + "st_log_oct_export_import where import_flag='0'";

            result = dbHelper.getFirstDocument(sql);
            while (result) {
                map.put(dbHelper.getItemValue("export_file_name"), dbHelper.getItemValue("balance_water_no"));
                result = dbHelper.getNextDocument();
            }

        } catch (SQLException e) {
            logger.error("--查询w_st_log_oct_export_import异常：", e);
            throw e;
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return map;
    }


    /*
    更新获取一卡通清算文件的记录为8"正在进行"
    */
    public int updateBusDataLog(Map<String, String> map) {
        int result = 0;
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("BusDataDao", FrameDBConstant.OP_DBCPHELPER.getConnection());
            String sql = "update " + FrameDBConstant.COM_ST_P + "st_log_oct_export_import set import_flag='8' where export_file_name=? and import_flag='0'";

            dbHelper.setAutoCommit(false);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String[] values = {entry.getKey()};
                result += dbHelper.executeUpdate(sql, values);
            }

            dbHelper.commitTran();
            dbHelper.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error("--查询w_st_log_oct_export_import异常：", e);
            throw e;
        } finally {
            PubUtil.finalProcess(dbHelper);
            return result;
        }
    }


    public int writeDB(Map<String, String> map, String fileType)
            throws SQLException {
        int result = 0;
        DbHelper dbHelper = null;
        Map<String, String> mapTmp = new HashMap<>();
        try {
            dbHelper = new DbHelper("BusDataImportThread", FrameDBConstant.CM_DBCPHELPER.getConnection());
            String sql = "insert into " + FrameDBConstant.COM_COMMU_P + "cm_log_busdata_file (balance_water_no, file_type, insert_time) values (?,?, sysdate)";

            dbHelper.setAutoCommit(false);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (!mapTmp.containsKey(entry.getValue())) {
                    String[] values = {entry.getValue(), fileType};
                    try {
                        result += dbHelper.executeUpdate(sql, values);
                        mapTmp.put(entry.getValue(), fileType);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        continue;
                    }
                }
            }

            dbHelper.commitTran();
            dbHelper.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error("--入库一卡通结算文件记录出错：", e);
            throw e;
        } finally {
            PubUtil.finalProcess(dbHelper);
            return result;
        }
    }
    //add by zhongzq 20180929
    public List getUploadList() throws SQLException {
        boolean result = false;
        List balanceWaterList = new ArrayList();
        Map<String, String> map = new HashMap<String, String>();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("BusDataDao", FrameDBConstant.OP_DBCPHELPER.getConnection());

            java.lang.String sql = "select  distinct balance_water_no  from "
                    + FrameDBConstant.COM_ST_P + "st_log_oct_export_import where export_flag='8'  order by balance_water_no";

            result = dbHelper.getFirstDocument(sql);
            while (result) {
                String no = dbHelper.getItemValue("balance_water_no");
                if("".equals(no.trim())){
                    logger.warn("存在空流水号");
                    continue;
                }
                balanceWaterList.add(dbHelper.getItemValue("balance_water_no"));
                result = dbHelper.getNextDocument();
            }

        } catch (SQLException e) {
            logger.error("--查询w_st_log_oct_export_import异常：", e);
            throw e;
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return balanceWaterList;
    }

    public Map getUploadFiles(String balanceWaterNo) throws SQLException {
        boolean result = false;
        Map fileList = new HashMap();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("BusDataDao", FrameDBConstant.OP_DBCPHELPER.getConnection());

            String sql = "select  export_file_name,balance_water_no,balance_water_no_sub from "
                    + FrameDBConstant.COM_ST_P + "st_log_oct_export_import where export_flag='8' and balance_water_no = '"+balanceWaterNo+"' order by balance_water_no,balance_water_no_sub";
            result = dbHelper.getFirstDocument(sql);
            int i = 0;
            while (result) {
                BusDataUpLoadVo vo = new BusDataUpLoadVo();
                vo.setFileName(dbHelper.getItemValue("export_file_name"));
                vo.setBalanceWaterNo(dbHelper.getItemValue("balance_water_no"));
                vo.setBalanceWaterNoSub(dbHelper.getItemValue("balance_water_no_sub"));
                fileList.put(i,vo);
                i++;
                result = dbHelper.getNextDocument();
            }

        } catch (SQLException e) {
            logger.error("--查询w_st_log_oct_export_import异常：", e);
            throw e;
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return fileList;
    }

    public int updateBusDataLogForFinished(Map map) {
        int result = 0;
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("BusDataDao", FrameDBConstant.OP_DBCPHELPER.getConnection());
            String sql = "update " + FrameDBConstant.COM_ST_P + "st_log_oct_export_import set export_flag='1' where export_file_name= ? and balance_water_no = ? and balance_water_no_sub = ? and export_flag='8'";

            dbHelper.setAutoCommit(false);
            for (int i = 0; i < map.size(); i++) {
                BusDataUpLoadVo vo = (BusDataUpLoadVo) map.get(i);
                String[] values = {vo.getFileName(),vo.getBalanceWaterNo(),vo.getBalanceWaterNoSub()};
                result += dbHelper.executeUpdate(sql, values);
            }

            dbHelper.commitTran();
            dbHelper.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error("--查询w_st_log_oct_export_import异常：", e);
            throw e;
        } finally {
            PubUtil.finalProcess(dbHelper);
            return result;
        }
    }
}