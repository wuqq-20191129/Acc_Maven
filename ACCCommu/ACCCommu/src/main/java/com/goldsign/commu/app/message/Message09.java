package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.Message08Dao;
import com.goldsign.commu.frame.buffer.TccBuffer;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * 单个设备状态信息
 *
 * @author zhangjh
 */
public class Message09 extends Message08 {

    private static Logger logger = Logger.getLogger(Message09.class.getName());

    @Override
    public void run() throws Exception {
        String result = FrameLogConstant.RESULT_HDL_SUCESS;
        this.level = FrameLogConstant.LOG_LEVEL_INFO;
        try {
            this.hdlStartTime = System.currentTimeMillis();
            logger.info("--处理09消息开始--");
            this.process();
            logger.info("--处理09消息结束--");
            this.hdlEndTime = System.currentTimeMillis();
        } catch (Exception e) {
            result = FrameLogConstant.RESULT_HDL_FAIL;
            this.hdlEndTime = System.currentTimeMillis();
            this.level = FrameLogConstant.LOG_LEVEL_ERROR;
            this.remark = e.getMessage();
            throw e;
        } finally {// 记录处理日志
            LogDbUtil.logForDbDetail(
                    FrameLogConstant.MESSAGE_ID_DEV_STATUS_EACH,
                    this.messageFrom, this.hdlStartTime, this.hdlEndTime,
                    result, this.threadNum, this.level, this.remark, this.getCmDbHelper());
        }
    }

    @Override
    public void process() throws Exception {

        try {
            String currentTod = getBcdString(2, 7);
            statusDateTime = DateHelper.dateStrToSqlTimestamp(currentTod);
            statusDateTimeStr = currentTod;
            getDeviceStatus(1, 9, false);
            Message08Dao.updateCurrentStatus(getCmDbHelper(), currentStatusV);
            Message08Dao.insertHisStatus(getCmDbHelper(), hisStatusV);
            //add by zhongzq 用于tcc转发 20190605 生产者
            if (FrameCodeConstant.TCC_INTERFACE_USE_KEY.equals(FrameCodeConstant.TCC_INTERFACE_CONTROL)) {
                TccBuffer.TCC_MESSAGE_QUEUE.offer(this.data);
                Object[] paras = {"09", messageSequ};
                getCmDbHelper().executeUpdate(TccBuffer.logSql, paras);

            }
        } catch (SQLException e) {
            logger.error("数据库异常,错误代码" + e.getErrorCode() + " 消息", e);
            throw e;
        } catch (Exception e) {
            logger.error("处理消息08出错：", e);
            throw e;
        }

    }
}
