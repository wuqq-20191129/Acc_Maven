package com.goldsign.commu.app.message;

import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.Message23Dao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.vo.Message23Vo;
import com.goldsign.commu.frame.vo.MessageQueue;
import com.goldsign.lib.db.util.DbHelper;
import java.util.Date;
import org.apache.log4j.Logger;


/**
 * 证件号查询业务信息 LC->ACC
 */
public class Message23 extends MessageBase {
    private static Logger logger = Logger.getLogger(Message23.class.getName());

    @Override
    public void run() throws Exception {
            String result = FrameLogConstant.RESULT_HDL_SUCESS;
            this.level = FrameLogConstant.LOG_LEVEL_INFO;
            this.hdlStartTime = System.currentTimeMillis();
            try {
                    logger.info("--处理23证件号查询业务消息开始--");
                    this.process();
                    logger.info("--处理23证件号查询业务消息结束--");
            } catch (Exception e) {
                    result = FrameLogConstant.RESULT_HDL_FAIL;
                    this.level = FrameLogConstant.LOG_LEVEL_ERROR;
                    this.remark = e.getMessage();
                    throw e;
            } finally {// 记录处理日志
                    this.hdlEndTime = System.currentTimeMillis();
                    LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_CARD_INFO,
                                    this.messageFrom, this.hdlStartTime, this.hdlEndTime,
                                    result, this.threadNum, this.level, this.remark,
                                    this.getCmDbHelper());
            }
    }


    public void process() throws Exception {
        Message23Vo vo;
        try {
            // 获取客户端的消息
            vo = this.processMessage();

            //查询
            this.opQuery(vo, this.getOpDbHelper());
        } catch (Exception e) {
            logger.error(thisClassName + " error! messageSequ:" + messageSequ  + ". ", e);
            throw e;
        }

    }

    /*
     * 查询
     */
    private void opQuery(Message23Vo vo, DbHelper dbHelper) throws Exception {
        Message23Dao dao = new Message23Dao();
        dao.cardInfos(vo, dbHelper);
        dao.businessInfos(vo, dbHelper);
        // 构造24消息
        MessageQueue mq = this.getMessageQueue(vo);
        // 消息放入连接的消息队列
        this.sendMsgToConnectionQueue(mq);
    }

    
    private MessageQueue getMessageQueue(Message23Vo vo) {

        byte[] msg = new ConstructMessage24().constructMessage(vo);
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
        messageQueue.setIpAddress(this.messageFrom);
        messageQueue.setMessage(msg);
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

    private Message23Vo processMessage() throws CommuException  {
        Message23Vo vo = new Message23Vo();
        
        vo.setCurrentTod(getBcdString(2, 7));
        logger.info("生成时间currentTod:" + vo.getCurrentTod());
        String currentBom = getCharString(9, 9);
        vo.setCurrentBom(currentBom);
        vo.setIDCardType(getCharString(18, 1));
        vo.setIDNumber(getCharString(19, 18));
        return vo;
    }

}
