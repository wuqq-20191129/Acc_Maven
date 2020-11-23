package com.goldsign.commu.app.message;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.DateHelper;

/**
 * 降级模式通知
 *
 * @author zhangjh
 */
public class ConstructMessage06 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage06.class
            .getName());
    
    public ConstructMessage06() {
        super();
        this.messageType = "06";
        this.messageRemark = "降级模式通知";
    }

    public byte[] constructMessage(String stationId, String command, String flag) {
        logger.info("--构建06消息开始--");
        
//		logger.info("stationid:" + stationId);
//		logger.info("command:" + command);
//		logger.info("flag:" + flag);
        try {
            initMessage();
            AddStringToMessage("06", 2);
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            AddStringToMessage(stationId, 4);
            AddBcdToMessage(command, 2);
            AddBcdToMessage(flag, 1);
            byte[] msg = trimMessage();
            logger.info("--成功构建06消息--");
            return msg;
        } catch (Exception e) {
            logger.error("构建06消息失败! " + e);
            return new byte[0];
        }
    }

    public void constructAndSend(Vector<String> ip, String stationId,
            String command, String flag) {
        sendToJms(ip, constructMessage(stationId, command, flag));
        logger.info("--成功发送06消息--");
    }
}
