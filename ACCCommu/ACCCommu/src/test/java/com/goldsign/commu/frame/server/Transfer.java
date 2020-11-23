package com.goldsign.commu.frame.server;

import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.message.MessageBase;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-23
 * @Time: 18:53
 */
public class Transfer {
    public static void main(String[] args) throws MessageException {
        byte[] data = new Transfer().AddLongToMessage(1200,4);
        System.out.println(new Transfer().getLong(0,data));
        byte[] test = {-80,4,0,0};
        System.out.println();
        int i = Integer.valueOf("000004B0",16);
        System.out.println("i = " + i);
//        int j = Integer.valueOf("B0040000",16);
//        System.out.println("j = " + j);
//        long=0XB00400L;

        byte[] test1 = {49,51,32,25,6,35,34,66,0,48,49,49,48,32,25, 6,35, 0,73, 0, 2, 1, 0,10, 0,0,0,1,1,10,0,0,0};
        byte[] test2 ={48,49,49,48,50,48,49,57,48,54,50,51,48,49,48,53,0,0,0,0, 0,0,0,0,0,0,0,0,0};

        StringBuilder builder = new StringBuilder();
        builder.append("test1:");
        for (byte b:test1){
            builder.append(b);
            builder.append(",");
        }
        builder.append("test2:");
        for (byte b:test2){
            builder.append(b);
            builder.append(",");
        }
        System.out.println("builder.toString() = " + builder.toString());
        System.arraycopy(test1, 20, test2, 16, test1.length-20);
    }

    public int getShort(int offset, byte[] data) {
        int low = byteToInt(data[offset]);
        int high = byteToInt(data[offset + 1]);
        return high * 16 * 16 + low;
    }

    // when transform one long(two shorts) for example 0x12,0x34,0x56,0x78
    public int getLong(int offset, byte[] data) {
        int low = getShort(offset, data);
        int high = getShort(offset + 2, data);
        return high * 16 * 16 * 16 * 16 + low;
    }

    public int byteToInt(byte b) {
        int i = 0;
        if (b < 0) {
            i = 256 + b;
        } else {
            i = b;
        }
        return i;
    }
    protected byte[] AddLongToMessage(long input, int len)
            throws MessageException {
        String des = Integer.toHexString((int) input);
        if (des.length() >= 8) {
            des = des.substring(0, 7);
        } else {
            StringBuilder sb = new StringBuilder();
            //len 4*2
            for (int i = 0; i < len * 2 - des.length(); i++) {
                sb.append("0");
            }
            sb.append(des);
            des = sb.toString();
        }
//        String des = Integer.toHexString((int) input);
        //000000
        byte[] b = longStringToByteArray(des);
        if (b == null) {
            throw new MessageException("Long transform error!");
        }
        return b;
//        AddByteArrayToMessage(b, len);
    }
    private byte[] longStringToByteArray(String des) {
        byte[] rst = new byte[4];
        rst[0] = (byte) (Integer.valueOf(des.substring(6), 16).intValue());
        rst[1] = (byte) (Integer.valueOf(des.substring(4, 6), 16).intValue());
        rst[2] = (byte) (Integer.valueOf(des.substring(2, 4), 16).intValue());
        rst[3] = (byte) (Integer.valueOf(des.substring(0, 2), 16).intValue());
        return rst;
    }
//    protected void AddByteArrayToMessage(byte[] b, int len) {
//        if (len > b.length) {
//            System.arraycopy(b, 0, message, pointer, b.length);
//            pointer = pointer + b.length;
//            // char space = ' ';
//            byte[] space = {0x20};
//            for (int i = (len - b.length); i > 0; i--) {
//                System.arraycopy(space, 0, message, pointer, 1);
//                pointer = pointer + 1;
//            }
//        } else {
//            System.arraycopy(b, 0, message, pointer, len);
//            pointer = pointer + len;
//        }
//    }
}
