package com.goldsign.commu.app.message;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.DateHelper;

/**
 *
 * @author zhangjh
 */
public class ConstructMessage12 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage12.class
            .getName());

    public ConstructMessage12() {
        super();
        this.messageType = "12";
        this.messageRemark = "文件通知";
    }

    public byte[] constructMessage(Vector fileNameV) {
        logger.info("--构建12消息开始-- ");
        
        try {
            initMessage();
            AddStringToMessage("12", 2);
            AddBcdToMessage(DateHelper.currentTodToString(), 7);
            AddStringToMessage("0000", 4);
            AddIntToMessage(fileNameV.size(), 1);
            Iterator it = fileNameV.iterator();
            while (it.hasNext()) {
                String fileName = (String) (it.next());
                AddStringToMessage(fileName, 24);
            }
            byte[] msg = trimMessage();
            logger.info("--成功构建12消息--");
            return msg;
        } catch (Exception e) {
            logger.error("构建12消息失败! " + e);
            return new byte[0];
        }
    }

    public void constructAndSend(Vector ip, Vector fileNameV) {
        sendToJms(ip, constructMessage(fileNameV));
    }

    public void constructAndSend(String ip, String fileName) {
        Vector<String> fileNameV = new Vector<String>();
        fileNameV.add(fileName);
        sendToJms(ip, constructMessage(fileNameV));
    }

    public void constructAndSend(String ip, String fileName, String line,
            String stationId) {
        Vector<String> fileNameV = new Vector<String>();
        fileNameV.add(fileName);
        sendToJms(ip, constructMessage(fileNameV), line, stationId);
    }
}
