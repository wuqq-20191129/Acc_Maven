package com.goldsign.commu.app.message;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.vo.DevParaVerAddressVo;
import com.goldsign.commu.frame.vo.DevParaVerVo;

/**
 *
 * @author hejj
 */
public class ConstructMessage04 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage04.class
            .getName());

    public ConstructMessage04() {
        super();
        this.messageType = "04";
        this.messageRemark = "车站设备参数版本查询";
    }

    /**
     * 组装消息并发送
     *
     * @param v 消息数组
     * @throws JmsException
     */
    public void constructMessageAndSend(Vector<DevParaVerVo> v) {
        for (DevParaVerVo vo : v) {
            constructMessageAndSendForOne(vo);
        }
    }

    public void constructMessageAndSendForOne(DevParaVerVo vo) {
        Vector addresses = vo.getAddressSend();
        if (addresses == null || addresses.isEmpty()) {
            return;
        }
        DevParaVerAddressVo avo;
        byte[] msg;
        for (int i = 0; i < addresses.size(); i++) {
            avo = (DevParaVerAddressVo) addresses.get(i);
            msg = constructMessage(avo.getLineId(), vo);
            sendToJms(avo.getIp(), msg, vo.getLineId(), vo.getStationId());
        }

    }

    public byte[] constructMessage(String lineId, DevParaVerVo vo) {
        logger.info("--构建04消息开始--");
        
        try {
            initMessage();
            AddStringToMessage("04", 2);
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            String lineStation = lineId + vo.getStationId();
            AddStringToMessage(lineStation, 4);
            AddStringToMessage(vo.getDevTypeId(), 2);
            AddStringToMessage(vo.getDeviceId(), 3);

            byte[] msg = trimMessage();
            logger.info("--成功构造线路" + lineId + "的04消息--");
            return msg;
        } catch (Exception e) {
            logger.error("构造线路" + lineId + "的04消息失败! " + e);
            return new byte[0];
        }
    }
}
