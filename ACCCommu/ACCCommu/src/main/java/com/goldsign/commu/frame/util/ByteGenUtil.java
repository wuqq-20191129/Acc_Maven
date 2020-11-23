package com.goldsign.commu.frame.util;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * 
 * @author hejj
 */
public class ByteGenUtil {

	private static String ENCODE_CHARTSET = "gbk";

	public static byte bcdToByte(String bcd) {
		byte b = Byte.parseByte(bcd, 16);
		return b;
	}

	public static char bcdToChar(String bcd) {
		char c = (char) Integer.parseInt(bcd, 16);

		return c;
	}

	public static byte hexToByte(String hex) {
		byte b = Byte.parseByte(hex, 16);

		return b;
	}

	public static char hexToChar(String hex) {
		char c = (char) Integer.parseInt(hex, 16);

		return c;
	}

	public static void reflect() throws ClassNotFoundException {
		Class c = Class.forName("");
	}

	public static byte[] intToByte(int n, int byteNum) {
		String strHex = Integer.toHexString(n);
		int len = strHex.length();
		int byteTotal = byteNum * 2;
		String byteHex;
		byte[] bs = new byte[byteNum];

		while (len - byteTotal < 0) {
			strHex = "0" + strHex;
			len = strHex.length();
		}
		for (int i = 0; i < byteNum; i++) {
			byteHex = strHex.substring(len - 2, len);
			bs[i] = hexToByte(byteHex);
			len = len - 2;
		}
		return bs;
	}

	public static char[] intToChar(int n, int byteNum) {
		String strHex = Integer.toHexString(n);
		int len = strHex.length();
		int byteTotal = byteNum * 2;
		String byteHex;
		char[] bs = new char[byteNum];

		while (len - byteTotal < 0) {
			strHex = "0" + strHex;
			len = strHex.length();
		}
		for (int i = 0; i < byteNum; i++) {
			byteHex = strHex.substring(len - 2, len);
			bs[i] = hexToChar(byteHex);
			len = len - 2;
		}
		return bs;
	}

	public static byte[] intOneToByte(int n) {
		return intToByte(n, 1);
	}

	public static char[] intOneToChar(int n) {
		return intToChar(n, 1);
	}

	public static byte[] intTwoToByte(int n) {
		return intToByte(n, 2);
	}

	public static char[] intTwoToChar(int n) {
		return intToChar(n, 2);
	}

	public static byte[] stringToByte(String str, int len)
			throws UnsupportedEncodingException {
		byte[] bs = str.getBytes(ENCODE_CHARTSET);
		int blen = bs.length;
		byte[] bsr = new byte[len];
		for (int i = 0; i < len; i++) {
			if (i < blen) {
				bsr[i] = bs[i];
			} else {
				bsr[i] = 0x20;
			}
		}
		return bsr;
	}

	public static char[] convertByteArrayToCharArray(byte[] bs) {
		char[] cs = new char[bs.length];
		for (int i = 0; i < cs.length; i++) {
			cs[i] = (char) bs[i];
		}
		return cs;
	}

	public static char[] stringToChar(String str, int len)
			throws UnsupportedEncodingException {
		// char[] bs = str.toCharArray();
		char[] bs = convertByteArrayToCharArray(str.getBytes("gbk"));

		int blen = bs.length;
		char[] bsr = new char[len];
		for (int i = 0; i < len; i++) {
			if (i < blen) {
				bsr[i] = bs[i];
			} else {
				bsr[i] = 0x20;
			}
		}
		return bsr;
	}

	public static byte[] stringBcdToByte(String str)
			throws UnsupportedEncodingException {
		int len = str.length();
		if (len % 2 != 0) {
			str = "0" + str;
		}
		int blen = len / 2;
		len = str.length();
		byte[] bs = new byte[blen];

		String strHex;
		for (int i = 0; i < blen; i++) {
			strHex = str.substring(len - 2, len);
			bs[i] = bcdToByte(strHex);
			len = len - 2;

		}
		return bs;
	}

	public static char[] stringBcdToChar(String str, int dlen) {
		int len = str.length();
		if (len % 2 != 0) {
			str = "0" + str;
		}
		len = str.length();
		int blen = len / 2;

		char[] bs = new char[dlen];

		String strHex;
		len = 0;
		for (int i = 0; i < blen; i++) {
			strHex = str.substring(len, len + 2);
			bs[i] = bcdToChar(strHex);
			len = len + 2;

		}
		for (int i = 0; i < (dlen - blen); i++) {
			bs[blen + i] = 0x20;
		}
		return bs;
	}

	public static byte[] intFourToByte(int n) {
		return intToByte(n, 4);
	}

	public static char[] intFourToChar(int n) {
		return intToChar(n, 4);
	}

	public static int charToInt(char c) {
		return (int) c;
	}

	public static int charsToInt(char[] cs) {
		int mul = 1;
		int n = 0;
		for (char c : cs) {
			n += c * mul;
			mul = mul * 16 * 16;
		}
		return n;
	}

	public static int charTwoToInt(char[] cs) {
		return charsToInt(cs);
	}

	public static int charFourToInt(char[] cs) {
		return charsToInt(cs);
	}

	public static int charOneToInt(char c) {
		return charToInt(c);
	}

	public static void regularMatch() {
		char[] a = { 0x31, 0x32, 0x0d, 0x0a, 0x33, 0x34 };
		char[] b = { 0x0d, 0x0a };
		String s = "abbb123";
		String[] ss = s.split("");
		boolean result = s.matches("ab{3}\\d?");
		// result=s.matches("a");
		// result =Pattern.matches("a*b", "aaaab");
		System.out.println(result);

	}

	public static void main(String[] args) {
		try {
			String a = "何建军";
			// byte[] b = ByteGenUtil.bcdToByte(a);
			int n = 100;
			String bcd = "78975";
			char c = 244;
			char[] cs = { 244, 200, 200, 4 };
			/*
			 * b = ByteGenUtil.intOneToByte(n); System.out.println(b);; b =
			 * ByteGenUtil.intTwoToByte(n); System.out.println(b);; b =
			 * ByteGenUtil.intFourToByte(n); System.out.println(b);;
			 */

			// ByteGenUtil.stringToByte(a, 12);
			// ByteGenUtil
			// char[] cs =ByteGenUtil.stringBcdToChar(bcd);
			// cs =ByteGenUtil.intFourToChar(n);
			// cs =ByteGenUtil.intTwoToChar(n);
			// cs =ByteGenUtil.intOneToChar(n);
			// ByteGenUtil.printChars(cs);
			n = ByteGenUtil.charOneToInt(c);
			n = ByteGenUtil.charTwoToInt(cs);
			ByteGenUtil.regularMatch();

			System.out.println(n);
			;
		} catch (Exception ex) {
			Logger.getLogger(ByteGenUtil.class.getName()).log(Level.SEVERE,
					null, ex);
		}

	}

	public static void printChars(char[] cs) {
		int n;
		String s;
		for (int i = 0; i < cs.length; i++) {
			n = (int) cs[i];
			s = Integer.toHexString(n);
			System.out.print(s + " ");
		}
	}
}
