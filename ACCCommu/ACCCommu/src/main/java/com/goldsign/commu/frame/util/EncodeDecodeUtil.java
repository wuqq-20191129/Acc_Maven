package com.goldsign.commu.frame.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class EncodeDecodeUtil {

	private final byte[] TEMP = { 'P', 'A', 'R', 'M' };
	private final String Algorithm = "DESede";
	private static Logger logger = Logger.getLogger(EncodeDecodeUtil.class
			.getName());

	public static void main(String[] args) {
		if (args.length == 2) {
			EncodeDecodeUtil ed = new EncodeDecodeUtil();
			if (args[0].equals("-e")) {
				File of = new File(args[1]);
				File ef = new File(args[1] + ".e");
				if (ed.encode(of, ef)) {
					// logger.info("Encode finished!");
				}
			}
			if (args[0].equals("-d")) {
				File ef = new File(args[1]);
				File df = new File(args[1] + ".d");
				if (ed.decode(ef, df)) {
					// logger.info("Decode finished!");
				}
			}
		} else {
			logger.info("Error - Args should be option and file name.");
		}
	}

	public boolean encode(File of, File ef) {
		boolean result = false;

		try {

			byte[] fileBytes = getBytesFromFile(of);
			if (fileBytes == null) {
				throw new Exception("");
			}

			byte[] key = getKey();
			// byte[] key =
			// {0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x41,0x42,0x43,0x44,0x45,0x46,0x47,0x48,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38};

			if (key == null) {
				throw new Exception("");
			}
			// logger.info("EncodeDecode - key:"+byteToHex(key));

			byte[] encoded = encodeBytes(key, fileBytes);
			if (encoded == null) {
				throw new Exception("");
			}
			// logger.info("EncodeDecode - encoded:"+byteToHex(encoded));

			FileOutputStream out = new FileOutputStream(ef);

			// write temp bytes
			out.write(TEMP);

			// write key 16 bytes
			for (int i = 0; i < 16; i++) {
				out.write(key[i]);
			}

			// write encoded bytes
			for (int i = 0; i < encoded.length; i++) {
				out.write(encoded[i]);
			}
			out.close();
			result = true;
		} catch (Exception e) {
			logger.error("Encode error! " + e);
			logger.error("catch one exception",e);
		}

		return result;
	}

	public boolean decode(File ef, File df) {
		try {
			FileInputStream fis = new FileInputStream(ef);
			byte[] buff;
			int readed;
			byte[] result;

			buff = new byte[4];
			readed = fis.read(buff);
			if (readed != 4) {
				throw new Exception("Temp bytes error!");
			}
			// logger.info("Temp bytes are:"+byteToHex(buff));

			buff = new byte[16];
			readed = fis.read(buff);
			if (readed != 16) {
				throw new Exception("Keys bytes error!");
			}
			// logger.info("16 Keys are:"+byteToHex(buff));
			byte[] key = new byte[24];
			System.arraycopy(buff, 0, key, 0, 16);
			System.arraycopy(buff, 0, key, 16, 8);
			// logger.info("EncodeDecode - key:"+byteToHex(key));

			buff = new byte[1024];
			int len = 0;
			byte[] temp;
			result = new byte[0];
			while ((readed = fis.read(buff)) > 0) {
				temp = result;
				len = len + readed;
				// logger.info("readed:"+readed);
				result = new byte[len];
				System.arraycopy(temp, 0, result, 0, temp.length);
				System.arraycopy(buff, 0, result, temp.length, readed);
				// logger.info("readed bytes:"+byteToHex(result));
			}

			// logger.info("Encoded bytes are:"+byteToHex(result));

			byte[] decodedBytes = decodeBytes(key, result);
			// logger.info("Decoded bytes are:"+byteToHex(decodedBytes));

			if (decodedBytes != null) {
				FileOutputStream out = new FileOutputStream(df);
				for (int i = 0; i < decodedBytes.length; i++) {
					out.write(decodedBytes[i]);
				}
				out.close();
			}
			return true;
		} catch (Exception e) {
			logger.error("Decode error! " + e);
			logger.error("catch one exception",e);
			return false;
		}
	}

	public byte[] decodeBytes(byte[] keybyte, byte[] src) {
		try {
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			Cipher ci = Cipher.getInstance(Algorithm);
			ci.init(Cipher.DECRYPT_MODE, deskey);
			return ci.doFinal(src);
		} catch (Exception e) {
			logger.error("Decode error! " + e);
			return null;
		}
	}

	private byte[] getBytesFromFile(File f) {
		try {
			FileReader fr = new FileReader(f);
			StringBuffer sb = new StringBuffer();
			char[] buff = new char[1024];
			int readed;
			while ((readed = fr.read(buff)) > 0) {
				sb.append(buff, 0, readed);
			}

			return sb.toString().getBytes();
		} catch (Exception e) {
			logger.error("Error - getBytesFromFile error! " + e);
			return null;
		}
	}

	private byte[] getKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(Algorithm);
			keyGenerator.init(112);
			SecretKey key = keyGenerator.generateKey();
			return key.getEncoded();
		} catch (Exception e) {
			logger.error("Error - getKey error! " + e);
			return null;
		}
	}

	private byte[] encodeBytes(byte[] keybyte, byte[] b) {
		try {
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(b);
		} catch (Exception e) {
			logger.error("Error - encodeBytes error! " + e);
		}
		return null;
	}

	// For example, byte[] {0x01,0x23,0x45,0x67} will be changed to String
	// "01,23,45,67"
	private String byteToHex(byte[] b) {
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
}
