package com.goldsign.commu.app.message;

import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.vo.BusinessInfo;
import com.goldsign.commu.frame.vo.CardInfo;
import com.goldsign.commu.frame.vo.Message23Vo;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * 逻辑卡号查询
 * @author lind
 */
public class ConstructMessage24 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage24.class
            .getName());
    
    
    public ConstructMessage24() {
        super();
        this.messageType = "24";
        this.messageRemark = "逻辑卡号查询";
    }

    public byte[] constructMessage(Message23Vo vo) {
        logger.info("--构建24消息开始--");
        
        try {
            initMessage();
            AddStringToMessage("24", 2);
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            AddStringToMessage(vo.getCurrentBom(), 9);
            List<CardInfo> CIList = vo.getCardInfos();
            AddIntToMessage((CIList==null?0:CIList.size()), 1);
            if(CIList!=null && CIList.size()>0){
                for (Iterator<CardInfo> iterator = CIList.iterator(); iterator.hasNext();) {
                    CardInfo next = iterator.next();
                    AddBcdToMessage(next.getCardType(),2);
                    AddStringToMessage(next.getLogicalNo(),16);
                }
            }
            List<BusinessInfo> BIList = vo.getBusinessInfos();
            AddIntToMessage((BIList==null?0:BIList.size()), 1);
            if(BIList!=null && BIList.size()>0){
                for (Iterator<BusinessInfo> iterator = BIList.iterator(); iterator.hasNext();) {
                    BusinessInfo next = iterator.next();
                    AddStringToMessage(next.getBusyType(),2);
                    AddStringToMessage(next.getApplyBill(),25);
                }
            }

            byte[] msg = trimMessage();
            logger.info("--成功构建24消息--");
            return msg;
        } catch (Exception e) {
            logger.error("构建24消息失败! " + e);
            return new byte[0];
        }
    }

    public void constructAndSend(String ip, Message23Vo vo) {
        sendToJms(ip, constructMessage(vo), "2");
    }
}
