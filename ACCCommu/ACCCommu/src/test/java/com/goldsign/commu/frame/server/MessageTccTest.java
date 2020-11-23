package com.goldsign.commu.frame.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-08
 * @Time: 17:10
 */
public class MessageTccTest extends BaseMessage implements Runnable {
    public static void main(String[] args) {
        new Thread(new MessageTccTest()).start();
    }

    @Override
    public void run() {
        Message08Test message08Test = new Message08Test();
        Message09Test message09Test = new Message09Test();
        Message10Test message10Test = new Message10Test();
        Message13Test message13Test = new Message13Test();
        Message14Test message14Test = new Message14Test();
        BlockingQueue<byte[]> blockingQueue = new LinkedBlockingQueue<>();
        blockingQueue.offer(message13Test.data());
        blockingQueue.offer(message14Test.data());
        blockingQueue.offer(message08Test.data());
        blockingQueue.offer(message09Test.data());
        blockingQueue.offer(message10Test.data());

        sendMultiDataNoResponse(1200, blockingQueue, "TCC");
    }
}
