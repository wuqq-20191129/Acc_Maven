package com.goldsign.commu.app.message;

import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.DateHelper;
import org.apache.log4j.Logger;


/**
 * 挂失/解挂申请审核
 *
 * @author zhangjh
 */
public class ConstructMessage22 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage22.class
            .getName());
    
    
    public ConstructMessage22() {
        super();
        this.messageType = "22";
        this.messageRemark = "挂失解挂补卡审核";
    }

    public byte[] constructMessage(String bussType, String dealResult) {
        logger.info("--构建22消息开始--");
        
        try {
            initMessage();
            AddStringToMessage("22", 2);
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            AddStringToMessage(bussType, 1);
            AddStringToMessage(dealResult, 2);

            byte[] msg = trimMessage();
            logger.info("--成功构建22消息--");
            return msg;
        } catch (Exception e) {
            logger.error("构建22消息失败! " + e);
            return new byte[0];
        }
    }

    public void constructAndSend(String ip, String bussType, String dealResult) {
        sendToJms(ip, constructMessage(bussType, dealResult), "2");
    }
}
