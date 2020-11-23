package com.goldsign.commu.frame.messagevo;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-05-31
 * @Time: 9:56
 */
public class BaseMessageVo {
    private String messageType;
    private String messageGenTime;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageGenTime() {
        return messageGenTime;
    }

    public void setMessageGenTime(String messageGenTime) {
        this.messageGenTime = messageGenTime;
    }
}
