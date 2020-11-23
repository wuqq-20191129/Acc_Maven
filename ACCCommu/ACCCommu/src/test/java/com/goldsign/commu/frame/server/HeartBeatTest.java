package com.goldsign.commu.frame.server;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-05-31
 * @Time: 17:07
 */
public class HeartBeatTest extends BaseMessage implements Runnable {
    public static void main(String[] args) {
        Thread t = new Thread(new Message05Test(), "172.20.16.95");
//        Thread t = new Thread(new Message05Test(), "172.20.18.36");
        t.run();
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(ipAddress, port);
            System.out.println("socket.hashCode:" + socket.hashCode());
            System.out.println("===================05消息===================");
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            OutputStream out = socket.getOutputStream();

            for (int i = 0; i < Long.MAX_VALUE; i++) {
                StringBuffer sb = new StringBuffer();
                sb.append("socket.i=" + i + ":");
                sendQuery(out, i);
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                }

                sb.append(Integer.toHexString(in.read() & 0xff));//开关标识
                int dataType = in.read();
                sb.append("," + dataType);//数据类型
                sb.append("," + in.read());//序列号
                int dataLength = in.read() + 256 * in.read();//数据长度两位
                sb.append("," + dataLength);
                if (dataType == 3) {
                    byte[] data = new byte[dataLength];
                    in.read(data, 0, dataLength);
                }
                sb.append("," + in.read());
                System.out.println(sb.toString());

            }

            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
