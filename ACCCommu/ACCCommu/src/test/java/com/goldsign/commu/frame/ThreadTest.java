package com.goldsign.commu.frame;

/**
 * @author:
 * @params:
 * @Date:{9:55} {2020/6/3}
 **/
public class ThreadTest {
    static class MyThread extends Thread{
        private static  ThreadLocal<Integer> threadLocal =new ThreadLocal<Integer>();
        @Override
        public void run() {
            super.run();
            for(int i=0;i<3;i++){
                threadLocal.set(i);
                System.out.println(getName()+" threadLocal.get=="+threadLocal.get());

            }
        }
    }
    public static void main(String args[]){
        MyThread thread1 = new MyThread();
        MyThread thread2 = new MyThread();
        thread1.setName("name1");
        thread2.setName("name2");
        thread1.start();
        thread2.start();

    }
}