package com.goldsign.commu.frame.connection;
import com.goldsign.commu.frame.exception.MessageException;
import java.io.*;
import java.util.Vector;
import org.apache.log4j.Logger;
/**
 * 
 * @author hejj
 */
public class ConnectionReader extends Thread {

	private BufferedInputStream in;
	private PipedOutputStream pipeout;
	private int serialNoShouldBe;
	private int serialNo;
	private boolean stopReader = false;
	private String resultCode = "";
	private int fromClient = -1;
	private final byte STX_B = (byte) 0xEB;
	private final byte ETX = 0x03;
	private final byte SERO = 0;
	private final byte QRY = 0x01;
	private final byte NDT = 0x02;
	private final byte DTA = 0x04;
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ConnectionReader.class
			.getName());

	/**
	 * 01xx message 0100 Received DATA message. 0101 Received NODATA message.
	 * 
	 * 10xx Socket,IO 1001 Socket error! 1002 IO error!
	 * 
	 * 110x message（receive）
	 * 
	 * 1101 Message start flag error! 1102 Message type error! 1103 Message
	 * serial NO error! 1104 Message end flag error! 1105 Message read timeout!
	 * 
	 * 
	 * 115x message（send）
	 * 
	 * 1151 Message length longer than 65536 bytes. 1152 Message length is 0.
	 * 1153 Message is null.
	 * 
	 * 120x 网络不正常
	 * 
	 * 1201 不能读指定长度字节
	 * 
	 */
	public ConnectionReader(BufferedInputStream bin, PipedOutputStream pout,
			int aSerialNoShouldBe) {
		in = bin;
		pipeout = pout;
		serialNoShouldBe = aSerialNoShouldBe;
	}

	@Override
	public void run() {
		Vector readerResult = new Vector();
		int dataLength;
		byte[] data = null;

		try {
			// read STX
			readOneByte();
			// logger.info(" - Got from client:"+fromClient);
			if ((byte) fromClient == -1) {
				logger.info(" - Message read error!");
				resultCode = "1002";
				throw new MessageException(resultCode);
			}
			if ((byte) fromClient != STX_B) {
				logger.info(" - Message start flag error!");
				resultCode = "1101";
				throw new MessageException(resultCode);
			}

			// read message type
			readOneByte();
			// logger.info(" - Got from client:"+fromClient);
			if (fromClient != NDT && fromClient != DTA && fromClient != QRY) {
				logger.info(" - Message type error!");
				resultCode = "1102";
				throw new MessageException(resultCode);
			}

			// read serial NO
			readOneByte();
			// logger.info(" - Got from client:"+fromClient);
			if (serialNoShouldBe != -1) { // for client test,serialNoShouldBe =
											// -1 means ignore serial no check
				if (fromClient != serialNoShouldBe) {
					logger.info(" - Message serial NO error!");
					resultCode = "1103";
					throw new MessageException(resultCode);
				}
			}
			serialNo = fromClient;

			// read data length
			readOneByte();
			dataLength = fromClient + in.read() * 256;
			// logger.info(" - Got from client:"+dataLength);

			// read data
			data = new byte[dataLength];
			this.readBytes(data, 0, data.length);
			/*
			 * for(int i=0;i<dataLength;i++){ readOneByte();
			 * data[i]=(byte)(fromClient); }
			 */
			// logger.info(" - Got from client:DATA");

			// read ETX
			readOneByte();
			if (fromClient != ETX) {
				logger.info(" - Message end flag error!");
				resultCode = "1104";
				throw new MessageException(resultCode);
			} else {
				// logger.info(" - Got from client:"+fromClient);
				if (dataLength == 0) {
					resultCode = "0101";
				} else {
					resultCode = "0100";
				}
			}
		} catch (IOException e) {
			resultCode = "1002";
		} catch (MessageException e) {
		} finally {
			readerResult.add(resultCode);
			readerResult.add(data);
			readerResult.add(new Integer(serialNo));
			try {
				ObjectOutputStream oout = new ObjectOutputStream(pipeout);
				oout.writeObject(readerResult);
				// logger.info("Reader return result.");
			} catch (IOException e) {
				resultCode = "1002";
			}
		}
	}

	public void stopReader() {
		stopReader = true;
	}

	private void readBytes(byte[] buff, int off, int len) throws IOException,
			MessageException {
		int readTotal = 0;
		if (!stopReader) {
			readTotal = readTotal = in.read(buff, off, len);
			if (readTotal != len) {
				resultCode = "1201";
				throw new MessageException(resultCode);
			}

		} else {
			resultCode = "1105";
			throw new MessageException(resultCode);
		}
	}

	private void readOneByte() throws IOException, MessageException {
		if (!stopReader) {
			fromClient = in.read();
		} else {
			resultCode = "1105";
			throw new MessageException(resultCode);
		}
	}
}
