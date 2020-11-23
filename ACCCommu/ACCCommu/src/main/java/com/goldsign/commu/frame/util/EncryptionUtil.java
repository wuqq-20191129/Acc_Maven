package com.goldsign.commu.frame.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class EncryptionUtil {

	/**
	 * uniEncrypt(String aPSW) is a uni-directional encryption from a string to
	 * another one. the encrypted string can not be decrypted to original any
	 * more.
	 */
	private static Logger logger = Logger.getLogger(EncryptionUtil.class
			.getName());

	public String uniEncrypt(String aPSW) {
		String compareEncryptPassword = "";
		if (aPSW == null) {
			return null;
		}

		byte[] buf = null;

		try {
			buf = aPSW.getBytes("ISO8859-1");

		} catch (UnsupportedEncodingException e) {
			buf = aPSW.getBytes();
		}

		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Error No:6093|PSWHistory uniEncrypt(String aPSW) err:"
					+ e);
		}
		messageDigest.reset();
		messageDigest.update(buf);
		byte[] encryptText = messageDigest.digest();

		for (int c = 0; c < encryptText.length; c++) {
			compareEncryptPassword += encryptText[c];
		}
		return compareEncryptPassword;
	}

	/**
	 * biEncrypt(String aKey,String aWord) is a bi-directional encryption
	 * function for Encrypt a string with specific word. encrypted string can be
	 * once again decrypted to original with the same word using
	 * biDecrypt(String aKey,String aWord).
	 */
	public String biEncrypt(String aKey, String aWord) {
		int len = aKey.length();
		int ikey = 0, iPos = 0, temp = 0;
		String sEncrypt = "";

		for (int x = 1; x <= aWord.length(); x++) {
			iPos = x % len - len * (((x % len) == 0) ? -1 : 0);
			ikey = aKey.charAt(iPos - 1);
			temp = aWord.charAt(x - 1) ^ ikey;
			sEncrypt += toHex(temp);
		}
		return sEncrypt;
	}

	/**
	 * biDecrypt(String aKey, String aWord) can decrypted the string which is
	 * encrypted by biEncrypt(String aKey, String aWord) to its original
	 */
	public String biDecrypt(String aKey, String aWord) {
		int len = aKey.length();
		int ikey = 0, iPos = 0, iAsc = 0;
		String sDecrypt = "", sTmp = "";
		char cTmp;

		for (int x = 0; x < aWord.length() - 1; x += 2) {
			iAsc = toDec(aWord.charAt(x)) * 16 + toDec(aWord.charAt(x + 1));
			cTmp = (char) iAsc;
			sTmp += cTmp;
		}

		for (int x = 1; x <= sTmp.length(); x++) {
			iPos = x % len - len * (((x % len) == 0) ? -1 : 0);
			ikey = aKey.charAt(iPos - 1);
			iAsc = sTmp.charAt(x - 1) ^ ikey;
			cTmp = (char) iAsc;
			sDecrypt += cTmp;
		}

		return sDecrypt;
	}

	/**
	 * This member function is to transform a Hex String into a byte array.
	 * 
	 * You can use the toHexString() to transform a byte[] into a Hex String
	 * first and then use this member function to transform back to a byte[].
	 * 
	 * @param aHexString
	 *            - The Hex String of the original byte[]
	 * 
	 * @return byte[] - The encrypted byte array
	 * 
	 */
	public byte[] toByteArray(String aHexString) {

		int length = aHexString.length();
		byte[] result = new byte[length / 2]; // two characters present one byte
		int y = 0;

		for (int i = 0; i < length; i += 2) {
			String tempString;
			if (i + 2 > length) {
				tempString = aHexString.substring(i);
			} else {
				tempString = aHexString.substring(i, i + 2);
			}

			result[y++] = (Integer.valueOf(tempString, 16)).byteValue();
		}

		return result;
	}

	/**
	 * This member function is to transform a byte array into a Hex String
	 * 
	 * You can use this memeber function to transform a byte[] into a Hex String
	 * first and then use the toByteArray to transform back to a byte[].
	 * 
	 * @param aByteArray
	 *            - The original byte[]
	 * 
	 * @return String - The Hex String of the byte[]
	 * 
	 */
	public String toHexString(byte[] aByteArray) {

		StringBuffer result = new StringBuffer();
		String tempHexString;
		int tempInt;

		for (int i = 0; i < aByteArray.length; i++) {

			tempInt = aByteArray[i];
			if (tempInt < 0) {
				tempInt = tempInt + 256; // convert a byte to its unsigned value
			}
			tempHexString = Integer.toHexString(tempInt).toUpperCase();

			// add the leading "0"
			if (tempHexString.length() == 1) {
				tempHexString = "0" + tempHexString;
			}

			result.append(tempHexString);
		}

		return result.toString();
	}

	private int toDec(char aHex) {
		int iDec = 0;
		if (aHex <= 57) {
			iDec = aHex - 48;
		} else {
			iDec = java.lang.Character.toUpperCase(aHex) - 55;
		}
		return iDec;
	}

	private String toHex(int aDec) {
		String hexChars = "0123456789ABCDEF";
		if (aDec > 255) {
			return null;
		}
		int i = aDec % 16;
		int j = (aDec - i) / 16;
		String result = "";
		result += hexChars.charAt(j);
		result += hexChars.charAt(i);
		return result;
	}
        
        public static void main(String[] args) {
                EncryptionUtil encryption = new EncryptionUtil();
                System.out.println(encryption.biEncrypt("GOLDSIGN","w_acc_cm"));
        }
}
