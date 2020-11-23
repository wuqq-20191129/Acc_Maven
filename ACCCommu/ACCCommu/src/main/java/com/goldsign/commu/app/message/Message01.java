package com.goldsign.commu.app.message;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.Message01Dao;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.vo.ParaGenDtl;

/**
 * 参数版本报告 LCC->ACC
 *
 * @author zhangjh
 */
public class Message01 extends MessageBase {

    private static Logger logger = Logger.getLogger(Message01.class.getName());

    // private static final byte[] SYNCONTROL = new byte[0];
    @Override
    public void run() throws Exception {
        String result = FrameLogConstant.RESULT_HDL_SUCESS;
        this.level = FrameLogConstant.LOG_LEVEL_INFO;
        try {
            this.hdlStartTime = System.currentTimeMillis();
            logger.info("--处理01消息开始--");
            this.process();
            logger.info("--处理01消息结束--");
            this.hdlEndTime = System.currentTimeMillis();
        } catch (Exception e) {
            result = FrameLogConstant.RESULT_HDL_FAIL;
            this.hdlEndTime = System.currentTimeMillis();
            this.level = FrameLogConstant.LOG_LEVEL_ERROR;
            this.remark = e.getMessage();
            throw e;
        } finally {// 记录处理日志
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_PARAM_SYN,
                    this.messageFrom, this.hdlStartTime, this.hdlEndTime,
                    result, this.threadNum, this.level, this.remark, this.getCmDbHelper());
        }
    }

    public void process() throws Exception {

        logger.info(thisClassName + " started!");
        // synchronized (SYNCONTROL) {
        try {
            // 消息生成时间
            getBcdString(2, 7);
            // 站点
            String station = getCharString(9, 4);
            //
            String lineId = station.substring(0, 2);
            String stationId = station.substring(2);
            // 重复次数
            int totalRepeatCount = getInt(13);
            int j = 14;
            Map<String, String> lccVer = new HashMap<String, String>();
            for (int i = 0; i < totalRepeatCount; i++) {
                // 参数类型
                String paraCode = getBcdString(j, 2);
                j = j + 2;
                // 参数当前版本号
                String paraCurrentVer = getBcdString(j, 5);
                j = j + 5;
                // 参数未来版本号
                String paraFutureVer = getBcdString(j, 5);
                j = j + 5;
                lccVer.put(paraCode + "0", paraCurrentVer);
                logger.debug("Version compare with " + station
                        + " - lcc version:" + paraCode + " 0 " + paraCurrentVer);
                lccVer.put(paraCode + "1", paraFutureVer);
                logger.debug("Version compare with " + station
                        + " - lcc version:" + paraCode + " 1 " + paraFutureVer);
            }

            Vector<ParaGenDtl> paraGenDtlV = new Vector<ParaGenDtl>();

            Map<String, String> accVer = Message01Dao.queryAccVer(getOpDbHelper(),lineId);
            for (Map.Entry entry : accVer.entrySet()) {
                String key = (String) entry.getKey();
                String accNum = (String) entry.getValue(); // 版本号
                String parmTypeId = key.substring(0, 4);// 参数类型
                String verType = key.substring(4);// 版本类型
                logger.info("Version compare with " + station
                        + " - acc version:" + parmTypeId + " " + verType + " " + accNum);
                if (lccVer.containsKey(key)) {// LCC已有参数
                    String lccNum = (String) lccVer.get(key);
                    if (!accNum.equals(lccNum)) {
                        if (verType.equals("0")) {
                            // 当前版本
                            String accNum1 = (String) accVer.get(parmTypeId
                                    + "1");
                            if (accNum1 == null) {
                                accNum1 = "0000000000";
                            }
                            String lccNum1 = (String) lccVer.get(parmTypeId
                                    + "1");
                            // LCC存在当前版本且不等于ACC未来版本，且LCC的未来版本不等于ACC的当前版本
                            if (!(accNum1.equals(lccNum)
                                    && (!lccNum.equals("0000000000")) || accNum
                                    .equals(lccNum1))) {
                                paraGenDtlV.add(getDownloadPara(parmTypeId,
                                        accNum, station));
                            }
                        } else {
                            // 未来版本
                            String accNum0 = (String) accVer.get(parmTypeId
                                    + "0");
                            if (accNum0 == null) {
                                accNum0 = "0000000000";
                            }
                            String lccNum0 = (String) lccVer.get(parmTypeId
                                    + "0");
                            // LCC的当前版本不等于ACC的未来版本，并且LCC的未来版本不等于ACC的当前版本
                            if (!(accNum.equals(lccNum0) || accNum0
                                    .equals(lccNum))) {
                                paraGenDtlV.add(getDownloadPara(parmTypeId,
                                        accNum, station));
                            }
                        }
                    }
                } else {// LCC没有该参数
                    paraGenDtlV
                            .add(getDownloadPara(parmTypeId, accNum, station));
                }
            }

            ConstructMessage02 msg = new ConstructMessage02();
            String ip = (String) (FrameCodeConstant.ALL_LCC_IP.get(lineId));
            msg.constructAndSend(ip, paraGenDtlV, lineId, stationId);
        } catch (SQLException e) {
            logger.error("数据库异常,错误代码" + e.getErrorCode() + " 消息", e);
            throw e;
        } catch (Exception e) {
            logger.error(thisClassName + " error! ", e);
            throw e;
        }
        // logger.error(thisClassName+" ended!");
        // }
    }

    private ParaGenDtl getDownloadPara(String type, String ver, String station) {
        logger.info("Version compare with " + station
                + " - parameter that LCC need to download : " + type + ver
                + " . ");
        // String lineId=station.substring(0,2);
        // String stationId=station.substring(2);
        ParaGenDtl p = new ParaGenDtl();
        p.setParmTypeId(type);
        p.setVerNum(ver);

        return p;
    }
}
