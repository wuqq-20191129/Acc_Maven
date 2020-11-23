package com.goldsign.commu.app.message;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.DateHelper;

/**
 *
 * @author hejj
 */
public class ConstructMessage17 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage17.class
            .getName());
    
    public ConstructMessage17() {
        super();
        this.messageType = "17";
        this.messageRemark = "非即时退款结果信息";
    }

    public byte[] constructMessage(String returnBOM, String ID, String cardNo,
            String flag, String deposit, String balance, String penalty,
            String penalty_reason) {
        logger.info("--构建17消息开始--");
        
        try {
            // logger.info("ConstructMessage17 Begin.");
            initMessage();
            AddStringToMessage("17", 2);
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            AddStringToMessage(returnBOM, 9);
            AddStringToMessage(ID, 25);
            AddStringToMessage(cardNo, 16);
            logger.debug("XXXX+cardNo:"+cardNo);
            AddStringToMessage(flag, 1);
            AddStringToMessage(deposit, 5);
            AddStringToMessage(balance, 7);
            AddStringToMessage(penalty, 5);
            // Add penalty_reason
            AddBcdToMessage(penalty_reason, 1);

            byte[] msg = trimMessage();
            logger.info("--成功构建17消息--");
            // logger.info("ConstructMessage17 OK.");
            return msg;
        } catch (Exception e) {
            logger.error("构建17消息失败! " + e);
            return new byte[0];
        }
    }

    public void constructAndSend(String ip, String returnBOM, String ID,
            String cardNo, String flag, String deposit, String balance,
            String penalty, String penalty_reason) {
        sendToJms(
                ip,
                constructMessage(returnBOM, ID, cardNo, flag, deposit, balance,
                penalty, penalty_reason), "2");
    }
}
