package com.goldsign.commu.frame.server;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-06
 * @Time: 17:08
 */
public class Message09Test extends BaseMessage implements Runnable, DataImpl {
    public static void main(String[] args) {
        Thread t = new Thread(new Message09Test(), "Message09Test");
        t.start();
    }

    @Override
    public byte[] data() {
        byte[] data = {48, 57, 32, 24, 9, 32, 5, 88, 57, 48, 49, 49, 51, 48, 49, 48, 48, 49, 0, 0, 0, 1, 0, 4};
        return data;
    }

    @Override
    public void run() {
        sendDataNoResponseData(255, data(), "09");
    }
}
