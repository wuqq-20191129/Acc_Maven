/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

/**
 *
 * @author hejj
 */
public class CharUtil {

    public static String GbkToIso(String str) {
        if (str == null) {
            return str;
        }
        try {
            return new String(str.getBytes("GB18030"), "8859_1");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static String IsoToGbk(String str) {
        if (str == null) {
            return str;
        }
        try {
            // logger.info("IsoToGbk:"+new
            // String(str.getBytes("8859_1"),"GB18030"));
            return new String(str.getBytes("8859_1"), "GB18030");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static String IsoToGbk2(String str) {
        if (str == null) {
            return str;
        }
        try {
            // logger.info("IsoToGbk:"+new
            // String(str.getBytes("8859_1"),"GB18030"));
            return new String(str.getBytes("ISO-8859-1"), "GB18030");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    /**
     * For example, byte[] {0x01,0x23,0x45,0x67} will be changed to String
     * "01,23,45,67"
     */
    public static String byteToHex(byte[] b) {
        String result = "";
        String tmp = "";

        for (int n = 0; n < b.length; n++) {
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                result = result + "0" + tmp;
            } else {
                result = result + tmp;
            }
            if (n < b.length - 1) {
                result = result + ",";
            }
        }
        return result.toUpperCase();
    }

    public static long getCRC32Value(byte[] b) {
        CRC32 crc = new CRC32();
        crc.update(b);

        return crc.getValue();
    }

    /**
     * 后面加字符
     *
     * @param str
     * @param totalLen
     * @param chars
     * @return
     */
    public static String addCharsAfter(String str, int totalLen, String chars) {
        if (null == str) {
            str = "";
        }
        String strRet = str;
        int strLen = str.getBytes().length;
        int len = totalLen - strLen;
        for (int i = 0; i < len; i++) {
            strRet = strRet + chars;
        }
        if (len < 0) {
            strRet = strRet.substring(0, strLen);
        }

        return strRet;
    }

    /**
     * 前面加字符
     *
     * @param str
     * @param totalLen
     * @param chars
     * @return
     */
    public static String addCharsBefore(String str, int totalLen, String chars) {
        if (null == str) {
            str = "";
        }
        String strRet = str;
        int strLen = str.getBytes().length;
        int len = totalLen - strLen;
        for (int i = 0; i < len; i++) {
            strRet = chars + strRet;
        }
        if (len < 0) {
            strRet = strRet.substring(0, strLen);
        }

        return strRet;
    }

}
