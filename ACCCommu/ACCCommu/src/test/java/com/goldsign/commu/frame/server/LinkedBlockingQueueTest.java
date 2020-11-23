package com.goldsign.commu.frame.server;

import com.goldsign.commu.app.vo.FlowHourMinFiveUnit;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import org.junit.experimental.theories.Theories;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-04
 * @Time: 16:07
 */
public class LinkedBlockingQueueTest {
    public static ConcurrentLinkedQueue<Object> linkedBlockingQueue= new ConcurrentLinkedQueue<>();
    public static void main(String[] args) {
//        Object[] v ={1,2};
//        linkedBlockingQueue.offer("1");
//        linkedBlockingQueue.offer("2");
//        linkedBlockingQueue.offer("3");
//        linkedBlockingQueue.offer("4");
//        linkedBlockingQueue.offer("5");
//        linkedBlockingQueue.offer("6");
//        linkedBlockingQueue.offer(v);
//
//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i <100000 ; i++) {
//                    if (!linkedBlockingQueue.offer(i+","+Thread.currentThread().getName())) {
//                        System.out.println("t1,i = " + i + "========================================================");
//                    }
//                }
//            }
//        },"t1");
//        Thread t2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 100000 ; i++) {
//                    if(!linkedBlockingQueue.offer((i+1000000)+","+Thread.currentThread().getName())){
//                        System.out.println("t2,i = " + i+"========================================================");
//                    }
//                }
//            }
//        }, "t2");
//        Thread t3 = new Thread(()->{
//            for (int i = 0; i < 100000 ; i++) {
//                if(!linkedBlockingQueue.offer((i+2000000)+","+Thread.currentThread().getName())){
//                    System.out.println("t3,i = " + i+"========================================================");
//                }
//            }
//        },"t3");
//        t1.start();
//        t2.start();
//        t3.start();
//        int count = 0;
//        while(true){
//            if (linkedBlockingQueue.isEmpty()){
//                try {
//                    System.out.println("isEmpty");
//                    TimeUnit.SECONDS.sleep(5);
//                    continue;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }else {
////                System.out.println("linkedBlockingQueue.poll() = " + linkedBlockingQueue.poll());
//                linkedBlockingQueue.poll();
//                System.out.println("count = " + ++count);
//            }
//
//
//        }
        TreeMap<String, FlowHourMinFiveUnit> fhmfTotal = new TreeMap<String, FlowHourMinFiveUnit>();
        SortedMap<String, FlowHourMinFiveUnit> fhmfTotalHead = fhmfTotal
                .tailMap(FrameCodeConstant.SQUAD_TIME);
        Collection<FlowHourMinFiveUnit> c  =  fhmfTotalHead.values();
        Vector<FlowHourMinFiveUnit> v =  new Vector<>(c);
        System.out.println(v.size());
    }
    
}
