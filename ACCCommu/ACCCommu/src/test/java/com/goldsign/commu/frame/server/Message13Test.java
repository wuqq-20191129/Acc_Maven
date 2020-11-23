package com.goldsign.commu.frame.server;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-05
 * @Time: 16:35
 */
public class Message13Test extends BaseMessage implements Runnable, DataImpl {
    public static void main(String[] args) {
        Thread t = new Thread(new Message13Test(), "Message13Test");
        t.start();
    }

    @Override
    public void run() {
        sendDataNoResponseData(255, data(), "13");
    }

    @Override
    public byte[] data() {
//        byte[] data = {49, 51, 32, 24, 9, 25, 0, 0, 7, 48, 49, 49, 57, 32, 24, 9, 25, 0, 0, 0, 0};
        byte[] data = {49, 51, 32, 24, 18, 19, 9, 54, 0, 48, 49, 49, 49, 32, 24, 18, 19, 9, 54, 0, 4, 6, 0, 7, 0, 0, 0, 6, 1, 1, 0, 0, 0, 6, 6, 1, 0, 0, 0, 1, 0, 4, 0, 0, 0};
        return data;
    }
}
