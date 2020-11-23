package com.goldsign.commu.frame.server;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-06
 * @Time: 17:08
 */
public class Message10Test extends BaseMessage implements Runnable, DataImpl {
    public static void main(String[] args) {
        Thread t = new Thread(new Message10Test(), "Message10Test");
        t.start();
    }

    @Override
    public byte[] data() {
        byte[] data = {49, 48, 32, 24, 9, 25, 35, 89, 66, 48, 49, 49, 52, 48, 50, 49, 48, 53, 0, 0, 0, 2, 0, 2, 32, 24, 9, 25, 35, 89, 66, 4, 33, 32, 24, 9, 25, 35, 89, 66};
        return data;
    }

    @Override
    public void run() {
        sendDataNoResponseData(255, data(), "10");
    }
}
