/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.zip.CRC32;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class CrcUtil {

    private static Logger logger = Logger.getLogger(CrcUtil.class.getName());
    protected final static char[] CRLF = {0x0d, 0x0a};
    protected final static String PADDING = "0";
    public final static int CRC_LEN = 8;

    public static long getCRC32Value(byte[] b) {
        CRC32 crc = new CRC32();
        crc.update(b);

        return crc.getValue();
    }

    public static void writeLineToBuf(StringBuffer buf, String lineNoCrLf) {
        buf.append(lineNoCrLf);
        buf.append(CrcUtil.CRLF);
    }

    public static void delete_printByteBuf(byte[] sb) {

        for (int i = 0; i < sb.length; i++) {
            if ((int) ((char) sb[i]) < 10) {
                System.out.print("0" + Integer.toHexString((int) ((char) sb[i])) + " ");
            } else {
                System.out.print(Integer.toHexString((int) ((char) sb[i])) + " ");
            }
            if (i != 0 && (i + 1) % 16 == 0) {
                System.out.print("\r\n");
            }
        }

    }

    public static String getCRC32Value(StringBuffer buf, int len) throws UnsupportedEncodingException {
        byte[] b;
        b = buf.toString().getBytes("US-ASCII");

        long iCrc = getCRC32Value(b);

        String crc = Long.toHexString(iCrc);
        for (int i = crc.length(); i < len; i++) {
            crc = PADDING + crc;
        }
        return crc;


    }

    /**
     * 解决JAVABYTE是有符号数，而C语言等BYTE表示无符号数计算CRC码不一致问题
     *
     * @param buf
     * @param len
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getCRC32ValueByChar(StringBuffer buf, int len) throws UnsupportedEncodingException {
        int n;
        CRC32 crc32 = new CRC32();
        for (int i = 0; i < buf.length(); i++) {
            n = (int) buf.charAt(i);
            if (n > 127) {
                System.out.print(n + " ");
            }
            crc32.update(n);
        }

        long iCrc = crc32.getValue();

        String crc = Long.toHexString(iCrc);
        for (int i = crc.length(); i < len; i++) {
            crc = PADDING + crc;
        }
        return crc;


    }

    public static String getCRC32ValueByChar(char[] buf, int bufLen, int len) throws UnsupportedEncodingException {
        int n;
        CRC32 crc32 = new CRC32();
        for (int i = 0; i < bufLen; i++) {
            n = (int) buf[i];
            if (n > 127) {
                System.out.print(n + " ");
            }
            crc32.update(n);
        }

        long iCrc = crc32.getValue();

        String crc = Long.toHexString(iCrc);
        for (int i = crc.length(); i < len; i++) {
            crc = PADDING + crc;
        }
        return crc;


    }

    public static long getCRC32Value(StringBuffer buf) {
        byte[] b = buf.toString().getBytes();
        long iCrc = getCRC32Value(b);
        return iCrc;


    }

    public static void main(String[] args) {

        try {
            String a = "123456";
            StringBuffer sb = new StringBuffer();
            sb.append(a);
            String crc = CrcUtil.getCRC32Value(sb, 8);
            System.out.println("CRC:" + crc);
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(CrcUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
