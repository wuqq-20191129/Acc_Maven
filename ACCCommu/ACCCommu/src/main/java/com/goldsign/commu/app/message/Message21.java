package com.goldsign.commu.app.message;

import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.Message21Dao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.vo.Message21Vo;
import com.goldsign.commu.frame.vo.Message22Vo;
import com.goldsign.commu.frame.vo.MessageQueue;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Date;
import org.apache.log4j.Logger;


/**
 * 挂失/解挂申请/挂失补卡申请 LCC->ACC
 */
public class Message21 extends MessageBase {
    private final String BomDevTypeId = "03";
    private static Logger logger = Logger.getLogger(Message21.class.getName());
    public final static String ACTION_QUERY = "1";// 查询
    public final static String ACTION_SUCESS = "2";// 办理成功通知
    public final static String LOCK = "1";// 加锁
    public final static String UNLOCK = "0";// 解锁
    private String applyBill;//会话标识
    private String SQL_LABLE = "seq_"+FrameDBConstant.TABLE_PREFIX+"st_flow_report_loss_id";

    @Override
    public void run() throws Exception {
            String result = FrameLogConstant.RESULT_HDL_SUCESS;
            this.level = FrameLogConstant.LOG_LEVEL_INFO;
            this.hdlStartTime = System.currentTimeMillis();
            try {
                    logger.info("--处理21消息开始--");
                    this.process();
                    logger.info("--处理21消息结束--");
            } catch (Exception e) {
                    result = FrameLogConstant.RESULT_HDL_FAIL;
                    this.level = FrameLogConstant.LOG_LEVEL_ERROR;
                    this.remark = e.getMessage();
                    throw e;
            } finally {// 记录处理日志
                    this.hdlEndTime = System.currentTimeMillis();
                    LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_LOSS_REPORT,
                                    this.messageFrom, this.hdlStartTime, this.hdlEndTime,
                                    result, this.threadNum, this.level, this.remark,
                                    this.getCmDbHelper());
            }
    }


    public void process() throws Exception {
        Message21Vo vo;
        try {
            // 获取客户端的消息
            vo = this.processMessage21();

            // 凭证ID放在消息处理对象中
            this.setApplyBill(vo.getApplyBill());

            //查询
            if(vo.getAction().equals(ACTION_QUERY)){
                logger.info("----------------------------挂失查询开始---------------------------");
                this.opQuery(vo, this.getOpDbHelper());
                logger.info("----------------------------挂失查询结束---------------------------");
            }
            //办理成功通知
            else if(vo.getAction().equals(ACTION_SUCESS)){
                logger.info("----------------------------办理成功通知开始-----------------------");
                this.opFinish(vo, this.getOpDbHelper());
                logger.info("----------------------------办理成功通知结束-----------------------");
            }
        } catch (Exception e) {
            logger.error(thisClassName + " error! messageSequ:" + messageSequ  + ". ", e);
            throw e;
        }

    }

    /*
     * 查询
     */
    private void opQuery(Message21Vo vo, DbHelper dbHelper) throws Exception, SQLException {
        Message21Dao dao = new Message21Dao();
        // 获取申请单处理相关信息
        Message22Vo vo22 = dao.getReportLossInfoFromSp(vo, dbHelper);
        // 构造22消息
        MessageQueue mq = this.getMessageQueue(vo22);
        // 消息放入连接的消息队列
        this.sendMsgToConnectionQueue(mq);
    }

    /*
     * 办理成功通知
     */
    private void opFinish(Message21Vo vo, DbHelper dbHelper) throws Exception {
        Message21Dao dao = new Message21Dao();
        // 更新相关信息
        dao.getReportLossInfoFromSp(vo, dbHelper);
    }
    
    private MessageQueue getMessageQueue(Message22Vo vo22) {

        byte[] msg22 = new ConstructMessage22().constructMessage(vo22.getBusnissType(), vo22.getResultCode());
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
        messageQueue.setIpAddress(this.messageFrom);
        messageQueue.setMessage(msg22);
        messageQueue.setIsParaInformMsg("0");
        messageQueue.setParaInformWaterNo(0);
        messageQueue.setLineId("");
        messageQueue.setStationId("");
        return messageQueue;

    }

    private void sendMsgToConnectionQueue(MessageQueue mq) {
        CommuConnection con = this.bridge.getConnection();
        con.setMessageInConnectionQueue(mq);
    }

    private Message21Vo processMessage21() throws CommuException  {
        Message21Vo vo = new Message21Vo();
        
        vo.setCurrentTod(getBcdString(2, 7));
        logger.info("生成时间currentTod:" + vo.getCurrentTod());
        String currentBom = getCharString(9, 9);
        vo.setCurrentBom(currentBom);
        vo.setClineId(currentBom.substring(0, 2));
        vo.setCstationId(currentBom.substring(2, 4));
        vo.setCdevTypeId(currentBom.substring(4, 6));
        vo.setCdeviceId(currentBom.substring(6));

        vo.setBusnissType(getCharString(18, 1));
        vo.setIDCardType(getCharString(19, 1));
        vo.setIDNumber(getCharString(20, 18));
        vo.setCardType(getBcdString(38, 2));//票卡类型
        vo.setCardLogicalID(getCharString(40, 16));//逻辑卡号
        
        // 凭证ID
        String applyBill_1 = getCharString(56, 25);
        vo.setApplyBill(applyBill_1);
        vo.setTkTime(applyBill_1.substring(0, 8)); // 日期：YYYYMMDD
        vo.setShiftId(applyBill_1.substring(9, 11)); // 班次号
        vo.setLineId(applyBill_1.substring(12, 14)); // 线路
        vo.setStationId(applyBill_1.substring(14, 16)); // 车站
        vo.setDevTypeId(this.BomDevTypeId); // BOM设备类型
        vo.setDeviceId(applyBill_1.substring(17, 20)); // 设备代码
        vo.setDealId(applyBill_1.substring(21));// 交易序列号
        // 处理方式
        vo.setAction(getCharString(81, 1));
        
//        vo.toString();
        return vo;
    }

    
    public String getApplyBill() {
        return applyBill;
    }

    public void setApplyBill(String applyBill) {
        this.applyBill = applyBill;
    }

}
