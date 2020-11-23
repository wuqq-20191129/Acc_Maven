package com.goldsign.commu.frame.server;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-07
 * @Time: 17:54
 */
public class CountDownLatchTest {
    public static LinkedBlockingQueue concurrentLinkedQueue = new LinkedBlockingQueue();

    public static void main(String[] args) {


        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
//                        CountDownLatchTest.concurrentLinkedQueue.put("text" + i);
                        CountDownLatchTest.concurrentLinkedQueue.offer("text" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 100; i++) {
//                    try {
//                        CountDownLatchTest.concurrentLinkedQueue.put("queue" + i);
                        CountDownLatchTest.concurrentLinkedQueue.offer("queue" + i);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
//                    try {
//                        CountDownLatchTest.concurrentLinkedQueue.put("2text" + i);
                        CountDownLatchTest.concurrentLinkedQueue.offer("2text" + i);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 100; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
//                        CountDownLatchTest.concurrentLinkedQueue.put("2queue" + i);
                        CountDownLatchTest.concurrentLinkedQueue.offer("2queue" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t1.start();
        t2.start();
        while (true) {
            while (!CountDownLatchTest.concurrentLinkedQueue.isEmpty()) {
//                try {
//                    System.out.println("CountDownLatchTest.concurrentLinkedQueue.poll() = " + CountDownLatchTest.concurrentLinkedQueue.take());
                    System.out.println("CountDownLatchTest.concurrentLinkedQueue.poll() = " + CountDownLatchTest.concurrentLinkedQueue.poll());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            if (CountDownLatchTest.concurrentLinkedQueue.isEmpty()) {
                try {
                    System.out.println("isEmpty" );
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
