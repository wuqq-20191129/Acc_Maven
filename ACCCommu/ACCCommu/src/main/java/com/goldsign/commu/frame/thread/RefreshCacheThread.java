package com.goldsign.commu.frame.thread;

import com.goldsign.commu.app.dao.ExternalIpDao;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.dao.ConfigBaseDao;
import com.goldsign.commu.frame.dao.DeviceDao;
import com.goldsign.commu.frame.dao.LccLineCodeDao;
import com.goldsign.commu.frame.dao.MBCodeDao;
import com.goldsign.commu.frame.dao.SquadTimeDao;
import com.goldsign.commu.frame.exception.ConfigFileException;
import org.apache.log4j.Logger;


/**
 * 定时刷新缓存变量
 * 20160427
 * @author lindaquan
 */
public class RefreshCacheThread implements Runnable {

    private static Logger logger = Logger.getLogger(RefreshCacheThread.class.getName());

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(FrameCodeConstant.CACHE_REFRESH_INTERVAL);
                refreshData();
            } catch (Exception e) {
                logger.error("RefreshCacheThread.run sleep error - " + e);
            }
        }
    }

    private void refreshData() throws ConfigFileException {
        try {
            // 从数据库取得数据
            LccLineCodeDao lccLineCodeDAO = new LccLineCodeDao();
            FrameCodeConstant.ALL_LCC_IP = lccLineCodeDAO.getAllLccIpCod();
            lccLineCodeDAO.getAllLccDetail();

            SquadTimeDao stDao = new SquadTimeDao();
            FrameCodeConstant.SQUAD_TIME = stDao.getSquadTime();
            logger.info("运营时间:" + FrameCodeConstant.SQUAD_TIME);

            DeviceDao deviceDAO = new DeviceDao();
            FrameCodeConstant.ALL_BOM_IP = deviceDAO.getAllBomIp();
            FrameCodeConstant.DEV_STATUS_MAPPING = deviceDAO.getStatusMapping();

            //手机支付业务
            ConfigBaseDao cbDao = new ConfigBaseDao();
            FrameCodeConstant.MOBILE_CONTROL = cbDao.getConfigValue(FrameCodeConstant.MOBILE_CONTROL_KEY);
            FrameCodeConstant.MOBILE_INTERVAL = Integer.parseInt(cbDao.getConfigValue(FrameCodeConstant.MOBILE_INTERVAL_KEY));
//            if(FrameCodeConstant.MOBILE_CONTROL.equals("1")){
//                MBCodeDao mBCodeDao = new MBCodeDao();
//                FrameCodeConstant.MOBILE_IP = mBCodeDao.getMobileIp();
//            }

            //tcc业务 zhongziqi 20190617
            FrameCodeConstant.TCC_INTERFACE_CONTROL = cbDao.getCmCfgSysValue(FrameCodeConstant.TCC_CONTROL_KEY);
            if (FrameCodeConstant.TCC_INTERFACE_USE_KEY.equals(FrameCodeConstant.TCC_INTERFACE_CONTROL)) {
                FrameCodeConstant.TCC_IP = new ExternalIpDao().getExternalIp(FrameCodeConstant.TCC_IP_TYPE);
                FrameCodeConstant.EXTERNAL_IP.put("TCC", FrameCodeConstant.TCC_IP);
            } else {
                FrameCodeConstant.EXTERNAL_IP.put("TCC", "127.0.0.1");
            }
            FrameCodeConstant.USER_MODE = cbDao.getCmCfgSysValue(FrameCodeConstant.MODE_CONTROL_KEY);

            //blacklist time control wuqq 20201009
            FrameCodeConstant.BLACKDISTRI_BEGIN=cbDao.getCmCfgSysValue(FrameCodeConstant.BLACK_DISTRIBUTE_BEGINKEY);
            FrameCodeConstant.BLACKDISTRI_END=cbDao.getCmCfgSysValue(FrameCodeConstant.BLACK_DISTRIBUTE_ENDKEY);

            logger.info("黑名单下发时间："+FrameCodeConstant.BLACKDISTRI_BEGIN+" - "+FrameCodeConstant.BLACKDISTRI_END);
            //一卡通下载延时间隔
            FrameCodeConstant.busFtpDelayInterval = Integer.parseInt(cbDao.getConfigValue(FrameCodeConstant.busFtpDelayInterval_key));
            //一卡通下载间隔
            FrameCodeConstant.busFtpInterval = Integer.parseInt(cbDao.getConfigValue(FrameCodeConstant.busFtpInterval_key));
            //缓存值刷新时间间隔
            FrameCodeConstant.CACHE_REFRESH_INTERVAL = Integer.parseInt(
            cbDao.getCmCfgSysValue(FrameCodeConstant.CACHE_REFRESH_INTERVAL_KEY));
        } catch (Exception e) {
                logger.info("定时刷新缓存变量错误! ", e);
                throw new ConfigFileException("定时刷新缓存变量错误!" + e);
        }
    }


}
