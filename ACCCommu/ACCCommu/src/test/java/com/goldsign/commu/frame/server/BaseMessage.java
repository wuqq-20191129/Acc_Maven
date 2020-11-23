package com.goldsign.commu.frame.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-05-28
 * @Time: 20:18
 */
public class BaseMessage {
    public static int port = 5001;
//            public static String ipAddress = "210.21.94.85";
    //    public static final String ipAddress = "10.99.11.81";
//    public static final String ipAddress = "172.20.19.14";
    public static final String ipAddress = "172.20.18.111";
//    public static final String ipAddress = "127.0.0.1";
    public static final byte STX_B = (byte) 0xEB;
    public static final byte ETX = 0x03;
    public static final byte QRY = 0x01;
    public static final byte DTA = 0x03;
    public static final byte[] HEADER = {STX_B, DTA, 0};
    public static final byte[] QUERY = {STX_B, QRY, 0, 0, 0, ETX};////开始标识 数据类型 序列号 数据长度（两位） 结尾
    public int SEND_INDEX = 0;

    public int getSendIndex() {
        return SEND_INDEX;
    }

    public void setSendIndex(int sendIndex) {
        SEND_INDEX = sendIndex;
    }

    /**
     * 发送单条数据
     *
     * @param commuNum    不能大于256
     * @param sendData    具体消息数据
     * @param messageType 消息类型
     */
    protected void sendDataNoResponseData(int commuNum, byte[] sendData, String messageType) {
        if (commuNum > 256) {
            System.out.println("commuNum = " + commuNum + " more than 256");
            return;
        }
        Socket socket = null;
        try {
            socket = new Socket(ipAddress, port);
            System.out.println("socket.hashCode:" + socket.hashCode());
            System.out.println("===================" + messageType + "消息===================");
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            OutputStream out = socket.getOutputStream();
            boolean isSend = false;
            for (int i = 0; i < commuNum; i++) {
                StringBuffer sb = new StringBuffer();
                sb.append("socket.i=" + i + ":");

                if (i == 0 && !isSend) {
                    sendData(sendData, out, i);
                    isSend = true;
                } else {
                    sendQuery(out, i);
                }
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

    /**
     * 多消息发送
     *
     * @param commuNum    通信次数 无限制 大于0即可
     * @param dataQueue   无限制 实例即可 有数据发数据 无数据发无数据包
     * @param messageType 随便起名
     */
    protected void sendMultiDataNoResponse(int commuNum, BlockingQueue<byte[]> dataQueue, String messageType) {
        Socket socket = null;
        try {
            socket = new Socket(ipAddress, port);
            System.out.println("socket.hashCode:" + socket.hashCode());
            System.out.println("===================" + messageType + "消息===================");
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            OutputStream out = socket.getOutputStream();
            int serialNum = 0;
            while (commuNum > 0) {

                if (commuNum >= 256) {
                    serialNum = 256;
                    commuNum = commuNum - 256;
                    System.out.println(commuNum);
                } else {
                    serialNum = commuNum;
                    System.out.println(commuNum);
                    commuNum = 0;
                }
                TimeUnit.SECONDS.sleep(1);
                for (int i = 0; i < serialNum; i++) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("socket.i=" + i + ":");

                    if (!dataQueue.isEmpty()) {
                        sendData(dataQueue.poll(), out, i);
                    } else {
                        sendQuery(out, i);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                    }
                    sb.append(Integer.toHexString(in.read() & 0xff));//开关标识
                    int dataType = in.read();
                    sb.append("," + dataType);//数据类型
                    sb.append("," + in.read());//序列号
                    int low = in.read();
                    int high = in.read();
                    int dataLength = low + 256 * high;//数据长度两位
                    sb.append("," + dataLength);
                    if (dataType == 3) {
                        byte[] data = new byte[dataLength];
                        in.read(data, 0, dataLength);
                    }
                    sb.append("," + in.read());
                    System.out.println(sb.toString()+"high:"+high+",low="+low);
                }
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


    public static void sendQuery(OutputStream out, int serialNo) throws IOException {
        BaseMessage.QUERY[2] = (byte) serialNo;
        out.write(BaseMessage.QUERY);

    }

    public void sendData(byte[] b, OutputStream out, int serialNo) throws IOException {
        HEADER[2] = (byte) serialNo;
        out.write(HEADER);
        out.write((byte) ((b.length) % 256));
        out.write((byte) ((b.length) / 256));
        out.write(b);
        out.write(ETX);
//        System.out.println("send:"+HEADER.toString()+"，"+(b.length% 256)+","+(b.length / 256)+","+b.toString()+","+ETX);
    }


    protected byte[] longStringToByteArray(long input, int len) {
        String des = Integer.toHexString((int) input);
        if (des.length() >= 8) {
            des = des.substring(0, 7);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len * 2 - des.length(); i++) {
                sb.append("0");
            }
            sb.append(des);
            des = sb.toString();
        }

        byte[] rst = new byte[4];
        rst[0] = (byte) (Integer.valueOf(des.substring(6), 16).intValue());
        rst[1] = (byte) (Integer.valueOf(des.substring(4, 6), 16).intValue());
        rst[2] = (byte) (Integer.valueOf(des.substring(2, 4), 16).intValue());
        rst[3] = (byte) (Integer.valueOf(des.substring(0, 2), 16).intValue());
        return rst;
    }

    // when transform 2 decimal number for example 98,run this method to get
    // 0x98
    private byte bcd2ToByte1(int i) {
        return (byte) (i / 10 * 16 + i % 10);
    }

    // run this method example : transform "123456789" to
    // {0x01,0x23,0x45,0x67,0x89}
    protected byte[] bcdStringToByteArray(String str) {
        try {
            int len = str.length();
            if (str.length() == 0) {
                return null;
            }
            if (len % 2 == 1) {
                str = "0" + str;
                len = len + 1;
            }
            if (len / 2 > 65536) {
                throw new Exception("Transform string to BCD length > "
                        + 65536);
            }
            byte[] tmp = new byte[65536];
            int p = 0;
            for (int i = 0; i < len; i = i + 2) {
                int value = Integer.parseInt(str.substring(i, i + 2));
                byte[] b = {bcd2ToByte1(value)};
                System.arraycopy(b, 0, tmp, p, 1);
                p = p + 1;
            }

            byte[] bb = new byte[p];
            System.arraycopy(tmp, 0, bb, 0, p);
            return bb;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // when transform one byte for example (byte)0x98,run this method to get
    // "98";
    public String  byte1ToBcd2(byte b) {
        int i = 0;
        if (b < 0) {
            i = 256 + b;
        } else {
            i = b;
        }
        return (new Integer(i / 16)).toString()
                + (new Integer(i % 16)).toString();
    }

    public String getBcdString(byte[] data, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < length; i++) {
                // logger.info("bcd之前："+data[offset + i]);
                sb.append(byte1ToBcd2(data[offset + i]));
                // logger.info("bcd之后："+byte1ToBcd2(data[offset + i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public char byteToChar(byte b) {
        return (char) b;
    }

    public String getCharString(byte[] data, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < length; i++) {
                sb.append(byteToChar(data[offset + i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getCharString(char[] data, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(data[offset + i]);
        }
        return sb.toString();
    }

    // when transform one byte for example (byte)0x98,run this method to get
    // 104;
    public int byteToInt(byte b) {
        int i = 0;
        if (b < 0) {
            i = 256 + b;
        } else {
            i = b;
        }
        return i;
    }

    public int getInt(byte[] data, int offset) {
        return byteToInt(data[offset]);
    }

    // when transform one short(two bytes) for example 0x12(low),0x34(high),run
    // this method to get 13330
    public int getShort(byte[] data, int offset) {
        int low = byteToInt(data[offset]);
        int high = byteToInt(data[offset + 1]);
        return high * 16 * 16 + low;
    }

    // when transform one long(two shorts) for example 0x12,0x34,0x56,0x78
    public int getLong(byte[] data, int offset) {
        int low = getShort(data, offset);
        int high = getShort(data, offset + 2);
        return high * 16 * 16 * 16 * 16 + low;
    }
}
