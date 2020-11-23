/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameParameterConstant;
import com.goldsign.commu.frame.dao.DevParaVerDao;
import com.goldsign.commu.app.message.ConstructMessage04;
import com.goldsign.commu.frame.vo.DevParaVerAddressVo;
import com.goldsign.commu.frame.vo.DevParaVerVo;
import org.apache.log4j.Logger;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * 设备参数版本查询线程
 *
 * @author hejj
 */
public class DevParaVerThread extends Thread {

    private static Logger logger = Logger.getLogger(DevParaVerThread.class
            .getName());

    @Override
    public void run() {
        Vector<DevParaVerVo> v;

        while (true) {
            try {
                v = queryUnhandleRequest();
                if (!v.isEmpty()) {
                    constuctMsgAndSend(v);
                }

                threadSleep();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }

    }

    /**
     * 全线路
     *
     * @param vo
     */
    private void setLineAll(DevParaVerVo vo) {
        Set keys = FrameCodeConstant.ALL_LCC_IP.keySet();
        Iterator it = keys.iterator();
        String value, key;
        Vector<DevParaVerAddressVo> v = new Vector<DevParaVerAddressVo>();
        DevParaVerAddressVo av;
        while (it.hasNext()) {
            key = (String) it.next();
            value = (String) FrameCodeConstant.ALL_LCC_IP.get(key);
            av = new DevParaVerAddressVo();
            av.setLineId(key);
            av.setIp(value);
            v.add(av);

        }
        vo.setAddressSend(v);

    }

    private void setLineSingle(DevParaVerVo vo) {
        Set keys = FrameCodeConstant.ALL_LCC_IP.keySet();
        Iterator it = keys.iterator();
        String value, key;
        Vector<DevParaVerAddressVo> v = new Vector<DevParaVerAddressVo>();
        DevParaVerAddressVo av;
        while (it.hasNext()) {
            key = (String) it.next();
            value = (String) FrameCodeConstant.ALL_LCC_IP.get(key);
            if (key.equals(vo.getLineId())) {
                av = new DevParaVerAddressVo();
                av.setLineId(key);
                av.setIp(value);
                v.add(av);
                break;
            }

        }
        vo.setAddressSend(v);

    }

    /**
     * 是否是LCC设备
     *
     * @param vo
     * @return
     */
    private boolean isQueryLCC(DevParaVerVo vo) {
        // LCC设备类型
        return FrameParameterConstant.devTypeLcc.equals(vo.getDevTypeId());
    }

    private void setAddress(Vector<DevParaVerVo> v) {
        String lineId;
        for (DevParaVerVo vo : v) {
            // 处理设备类型为LCC,线路取lcc_line_id字段值、车站取00
            if (isQueryLCC(vo)) {
                logger.info("选中的是LCC设备类型");
                lineId = vo.getLccLineId();
                vo.setLineId(lineId);
                vo.setStationId(FrameParameterConstant.devParaVerAllStation);
            }

            logger.info("lineId:" + vo.getLineId());
            logger.info("stationId:" + vo.getStationId());

            // 全线路代码
            if (vo.getLineId().equals(FrameParameterConstant.devParaVerAllLine)) {
                setLineAll(vo);
            } else {
                setLineSingle(vo);
            }
        }

    }

    /**
     * 查询设备参数
     *
     * @return
     */
    private Vector<DevParaVerVo> queryUnhandleRequest() {
        DevParaVerDao dao = new DevParaVerDao();
        Vector<DevParaVerVo> v = new Vector<DevParaVerVo>();
        try {
            v = dao.queryUnhandleRequest();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return v;
    }

    /**
     * 构建04消息并发送
     *
     * @param v 设备参数列表
     * @throws JmsException JmsException
     */
    private void constuctMsgAndSend(Vector<DevParaVerVo> v) {
        setAddress(v);
        ConstructMessage04 cm = new ConstructMessage04();
        cm.constructMessageAndSend(v);
    }

    private void threadSleep() {

        try {
            sleep(FrameParameterConstant.devParaVerThreadSleepTime);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

    }
}
