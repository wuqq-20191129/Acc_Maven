package com.goldsign.commu.app.message;

import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.Message16Dao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.vo.Message16Vo;
import com.goldsign.commu.frame.vo.Message17Vo;
import com.goldsign.commu.frame.vo.MessageQueue;
import com.goldsign.lib.db.util.DbHelper;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * 非即时退款
 *
 * @author zhangjh
 */
public class Message16 extends MessageBase {

    private final String BomDevTypeId = "03";
    private static Logger logger = Logger.getLogger(Message16.class.getName());
    // private static final byte[] SYNCONTROL16 = new byte[0];
    public final static String HDL_REFUNDED = "1";// 已办理退款
    public final static String HDL_NOTDO = "2";// 车票未处理
    public final static String HDL_REFUNDEDRESULT = "3";// ICCS完成车票退款结果
    public final static String HDL_BLACKLIST = "4";// 黑名单票卡，不能办理退款，但已办理即时退款
    public final static String HDL_INPUTERROR = "5";// 凭证号或卡号错误
    public final static String HDL_CANCEL = "6";// 退款申请已撤消
    public final static String HDL_PERMIT = "7";// 退款申请得到许可
    public final static String ACTION_QUERY = "1";// 查询退款处理情况
    public final static String ACTION_RETURN = "2";// 申请退款
    public final static String ACTION_RETURNSUCESS = "3";// 退款完成通知
    public final static String TYPE_MESSAGE_SEND = "17";// 发送出的17消息
    public final static String TYPE_MESSAGE_RECEIVE = "16";// 接收到的16消息
    private String applyBill;
    public final static String LOCK = "1";// 加锁
    public final static String UNLOCK = "0";// 解锁

    private boolean isOpQuery(String op) {
        return Message16.ACTION_QUERY.equals(op);
    }

    private boolean isOpApply(String op) {
        return Message16.ACTION_RETURN.equals(op);
    }

    private boolean isOpFinish(String op) {
        return Message16.ACTION_RETURNSUCESS.equals(op);
    }

    @Override
    public void run() throws Exception {
        String result = FrameLogConstant.RESULT_HDL_SUCESS;
        this.level = FrameLogConstant.LOG_LEVEL_INFO;
        try {
            this.hdlStartTime = System.currentTimeMillis();
            logger.info("--处理16消息开始--");
            this.process();
            logger.info("--处理16消息结束--");
            this.hdlEndTime = System.currentTimeMillis();
        } catch (Exception e) {
            result = FrameLogConstant.RESULT_HDL_FAIL;
            this.hdlEndTime = System.currentTimeMillis();
            this.level = FrameLogConstant.LOG_LEVEL_ERROR;
            this.remark = e.getMessage();
            throw e;
        } finally {// 记录处理日志
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_NON_RETURN,
                    this.messageFrom, this.hdlStartTime, this.hdlEndTime,
                    result, this.threadNum, this.level, this.remark, this.getCmDbHelper());
        }
    }

    public void process() throws Exception {
        // logger.error("MessageRun" + thisClassName + " started!");
        // synchronized (SYNCONTROL16) {
        // this.processMessage16();
        this.processMessage16ForNew();
        // }
    }

    public void processMessage16ForNew() throws Exception {

        Message16Vo vo;
        int dbId;
        try {
            // 获取客户端的消息
            vo = this.getMessage16Vo();
            // 获取连接的标识，并将标识放在连接对象中
            dbId = this.getId(this.getOpDbHelper());

            // 凭证ID放在消息处理对象中
            this.setApplyBill(vo.getApplyBill());

            // 查询
            if (this.isOpQuery(vo.getAction())) {
                logger.info("----------------------------非即时退款查询开始---------------------------");
                this.opQuery(vo, dbId, this.getOpDbHelper());
            } // bom申请退款
            else if (this.isOpApply(vo.getAction())) {
                logger.info("----------------------------非即时退款开始----------------------------");
                this.opApply(vo, dbId, this.getOpDbHelper());
                // 如果数据库处理完bom申请退款，而客户端连接中断了，需解除申请退款锁
                this.setUnlockFlag(true);
            } // BOM发送的退款结果
            else if (this.isOpFinish(vo.getAction())) {
                logger.info("----------------------------非即时退款结束通知-----------------------");
                this.opFinish(vo, dbId, this.getOpDbHelper());
                this.setUnlockFlag(false);
            }

        } catch (Exception e) {
            logger.error(thisClassName + " error! messageSequ:" + messageSequ
                    + ". ", e);
            throw e;
        }

    }

    private MessageQueue getMessageQueue(Message16Vo vo, Message17Vo vo17) {

        byte[] msg17 = new ConstructMessage17().constructMessage(
                vo.getCurrentBom(), vo.getApplyBill(), vo17.getCardNo(),
                vo17.getFlag(), vo17.getDeposit(), vo17.getBalance(),
                vo17.getPenalty(), vo17.getPenaltyReason());
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
        messageQueue.setIpAddress(this.messageFrom);
        messageQueue.setMessage(msg17);
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

    /**
     *
     * @param vo 非即退请求消息
     * @param id 数据库连接的标识
     * @param dbHelper
     * @throws Exception
     */
    private void opQuery(Message16Vo vo, int id, DbHelper dbHelper)
            throws Exception {

        Message16Dao dao = new Message16Dao();
        // 获取申请单处理相关信息
        Message17Vo vo17 = dao.getNonReturnInfogFromSp(vo, id, dbHelper);
        // 构造17消息
        MessageQueue mq = this.getMessageQueue(vo, vo17);
        // 消息放入连接的消息队列
        this.sendMsgToConnectionQueue(mq);
    }

    private void opApply(Message16Vo vo, int id, DbHelper dbHelper)
            throws Exception {
        Message16Dao dao = new Message16Dao();
        // 获取申请单处理相关信息
        Message17Vo vo17 = dao.getNonReturnInfogFromSp(vo, id, dbHelper);
        // 构造17消息
        MessageQueue mq = this.getMessageQueue(vo, vo17);
        // 消息放入连接的消息队列

        this.sendMsgToConnectionQueue(mq);
    }

    private void opFinish(Message16Vo vo, int id, DbHelper dbHelper)
            throws Exception {
        Message16Dao dao = new Message16Dao();
        // 获取申请单处理相关信息

        Message17Vo vo17 = dao.getNonReturnInfogFromSp(vo, id, dbHelper);

    }

    /**
     * 获取会话标识
     *
     * @param dbHelper 数据库访问辅助类
     * @return 会话标识
     * @throws Exception Exception
     */
    private int getId(DbHelper dbHelper) throws Exception {
        CommuConnection con = this.bridge.getConnection();

        int id = con.getDbId();
        if (id == -1) {
            Message16Dao dao = new Message16Dao();
            id = dao.getId(dbHelper);
            con.setDbId(id);
        }
        return id;
    }

    private void setUnlockFlag(boolean isUnlock) {
        this.bridge.getConnection().setIsNeedUnlock(isUnlock);
    }

    private Message16Vo getMessage16Vo() throws CommuException {
        Message16Vo vo = new Message16Vo();
        // 消息时间
        String currentTod = getBcdString(2, 7);

        // 当前BOM代码
        String currentBom = getCharString(9, 9);
        String clineId = currentBom.substring(0, 2);
        String cstationId = currentBom.substring(2, 4);
        String cdevTypeId = currentBom.substring(4, 6);
        String cdeviceId = currentBom.substring(6);

        // 凭证ID
        String applyBill_1 = getCharString(18, 25);
        String tkTime = applyBill_1.substring(0, 8); // 日期：YYYYMMDD
        String shiftId = applyBill_1.substring(9, 11); // 班次号
        String lineId = applyBill_1.substring(12, 14); // 线路
        String stationId = applyBill_1.substring(14, 16); // 车站
        String devTypeId = this.BomDevTypeId; // BOM设备类型
        String deviceId = applyBill_1.substring(17, 20); // 设备代码
        String receiptId = applyBill_1.substring(21);// 交易序列号

        // String
        // applyBillForDb=tkTime+shiftId+lineId+stationId+deviceId+receiptId;
        // vo.setApplyBillForDb(applyBillForDb);
        // 处理方式
        String action = getCharString(43, 1);

        print(currentTod, currentBom, clineId, cstationId, cdevTypeId,
                cdeviceId, applyBill_1, tkTime, shiftId, lineId, stationId,
                devTypeId, deviceId, receiptId, action);

        // 当前BOM信息
        vo.setCurrentTod(currentTod);
        vo.setCurrentBom(currentBom);
        vo.setClineId(clineId);
        vo.setCstationId(cstationId);
        vo.setCdevTypeId(cdevTypeId);
        vo.setCdeviceId(cdeviceId);

        // 申请BOM信息
        vo.setApplyBill(applyBill_1);
        vo.setTkTime(tkTime);
        vo.setShiftId(shiftId);
        vo.setLineId(lineId);
        vo.setStationId(stationId);
        vo.setDevTypeId(devTypeId);
        vo.setDeviceId(deviceId);
        vo.setReceiptId(receiptId);

        // 操作信息
        vo.setAction(action);
        return vo;
    }

    /**
     * @return the applyBill
     */
    public String getApplyBill() {
        return applyBill;
    }

    /**
     * @param applyBill the applyBill to set
     */
    public void setApplyBill(String applyBill) {
        this.applyBill = applyBill;
    }

    /**
     * 打印日志
     *
     * @param currentTod
     * @param currentBom
     * @param clineId
     * @param cstationId
     * @param cdevTypeId
     * @param cdeviceId
     * @param applyBom
     * @param tkTime
     * @param shiftId
     * @param lineId
     * @param stationId
     * @param devTypeId
     * @param deviceId
     * @param receiptId
     * @param action
     */
    private void print(String currentTod, String currentBom, String clineId,
            String cstationId, String cdevTypeId, String cdeviceId,
            String applyBom, String tkTime, String shiftId, String lineId,
            String stationId, String devTypeId, String deviceId,
            String receiptId, String action) {
        logger.info("currentTod:" + currentTod);
        logger.info("currentBom:" + currentBom);
        logger.info("clineId:" + clineId);
        logger.info("cstationId:" + cstationId);
        logger.info("cdevTypeId:" + cdevTypeId);
        logger.info("cdeviceId:" + cdeviceId);
        logger.info("applyBill_1:" + applyBom);
        logger.info("tkTime:" + tkTime);
        logger.info("shiftId:" + shiftId);
        logger.info("lineId:" + lineId);
        logger.info("stationId:" + stationId);
        logger.info("devTypeId:" + devTypeId);
        logger.info("deviceId:" + deviceId);
        logger.info("receiptId:" + receiptId);
        logger.info("action:" + action);

    }
}
