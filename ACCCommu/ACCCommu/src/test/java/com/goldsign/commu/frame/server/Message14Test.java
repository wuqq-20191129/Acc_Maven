package com.goldsign.commu.frame.server;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-06
 * @Time: 17:08
 */
public class Message14Test extends BaseMessage implements Runnable, DataImpl {
    public static void main(String[] args) {
        Thread t = new Thread(new Message14Test(), "Message14Test");
        t.start();
    }

    @Override
    public byte[] data() {
        byte[] data = {49, 52, 32, 24, 9, 25, 0, 0, 7, 48, 49, 49, 57, 32, 24, 9, 25, 0, 0, 0, 0};
        return data;
    }

    @Override
    public void run() {
        sendDataNoResponseData(255, data(), "14");
    }
}
