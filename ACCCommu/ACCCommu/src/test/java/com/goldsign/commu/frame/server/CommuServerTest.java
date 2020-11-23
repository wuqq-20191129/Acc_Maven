package com.goldsign.commu.frame.server;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.*;

public class CommuServerTest {
    public static final int port = 5001;
    public static final String ipAddress = "172.20.19.14";//广州
//    public static final String ipAddress = "127.0.0.1";
//    public static final String ipAddress = "10.99.11.81";

    public static final byte STX_B = (byte) 0xEB;
    public static final byte ETX = 0x03;
    public static final byte QRY = 0x01;
    public static final byte DTA = 0x03;
    public static final byte[] HEADER = {STX_B, DTA, 0};
    public static final byte[] QUERY = {STX_B, QRY, 0, 0, 0, ETX};////开始标识 数据类型 序列号 数据长度（两位） 结尾

    //    @Test
    public void startSocketListener() {
        Socket socket = null;
        try {
            socket = new Socket(ipAddress, port);
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            OutputStream out = socket.getOutputStream();
            for (int i = 0; i < 5; i++) {
                sendQuery(out, i);
//                alert
                System.out.println("i = " + i);
                for (int j = 0; j < 10000; j++) {

                }
            }
        } catch (Exception e) {
            System.out.println("e = " + e);
        }
    }

    public static void sendQuery(OutputStream out, int serialNo) throws IOException {
        QUERY[2] = (byte) serialNo;
        out.write(QUERY);

    }
}