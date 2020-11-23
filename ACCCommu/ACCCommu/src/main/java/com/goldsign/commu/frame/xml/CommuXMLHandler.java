package com.goldsign.commu.frame.xml;

import com.goldsign.commu.frame.util.EncryptionUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
import java.io.CharArrayWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 
 * @author hejj
 */
public class CommuXMLHandler extends DefaultHandler {
	public static final String ENCRYPT_KEY = "GOLDSIGN";
	protected final String NOT_ENCRYPT_PREFIX = "@@";
	private boolean needWriteFile = false;
	private boolean encrypt = false;
	
	private static final String Config_TAG = "Config";
	private static final String Common_TAG = "Common";
	public static final String FtpPaths_TAG = "FtpPaths";
	private static final String FtpPath_TAG = "FtpPath";
	public static final String Messages_TAG = "Messages";
	private static final String Message_TAG = "Message";

	/*
	 * public static final String CommuDb_TAG = "CommuDb"; public static final
	 * String OtherDb_TAG = "OtherDb"; public static final String FlowDb_TAG =
	 * "FlowDb"; public static final String FlowOtherDb_TAG = "FlowOtherDb";
	 * public static final String CommuCp_TAG = "CommuCp"; public static final
	 * String OtherCp_TAG = "OtherCp"; public static final String FlowCp_TAG =
	 * "FlowCp"; public static final String FlowOtherCp_TAG = "FlowOtherCp";
	 */
	public static final String OP_CONNECTION_POOL_TAG = "OpConnectionPool";
	public static final String CM_CONNECTION_POOL_TAG = "CmConnectionPool";
	public static final String TK_CONNECTION_POOL_TAG = "TkConnectionPool";
	public static final String CommunicationThreadPool_TAG = "CommunicationThreadPool";
	public static final String MaxThreadNumber_TAG = "MaxThreadNumber";
	public static final String ThreadSleepTime_TAG = "ThreadSleepTime";
	public static final String ThreadPriorityAdd_TAG = "ThreadPriorityAdd";
	public static final String MaxSearchNum_TAG = "MaxSearchNum";
	public static final String ConsolePrint_TAG = "ConsolePrint";
	public static final String ThreadBufferCapacity_TAG = "ThreadBufferCapacity";
	public static final String ThreadBufferIncrement_TAG = "ThreadBufferIncrement";
	public static final String PriorityThreadBufferCapacity_TAG = "PriorityThreadBufferCapacity";
	public static final String PriorityThreadBufferIncrement_TAG = "PriorityThreadBufferIncrement";
	public static final String GetMessageFrequency_TAG = "GetMessageFrequency";
	public static final String ReadThreadPriorityAdd_TAG = "ReadThreadPriorityAdd";
	public static final String ConnectionPoolListenerThread_TAG = "ConnectionPoolListenerThread";
	public static final String CPListenerThreadSleepTime_TAG = "CPListenerThreadSleepTime";
	public static final String TestSqlForOP_TAG = "TestSqlForData";
	public static final String TestSqlForCM_TAG = "TestSqlForLog";
	public static final String TestSqlForTK_TAG = "TestSqlForFlow";
	public static final String TestSqlForEmergent_TAG = "TestSqlForEmergent";
	public static final String SqlMsghandleSleepTime_TAG = "SqlMsghandleSleepTime";
	public static final String StartParameterDistribute_TAG = "StartParameterDistribute";
	public static final String StartSocketListener_TAG = "StartSocketListener";
	public static final String StartConnectionPoolListener_TAG = "StartConnectionPoolListener";
	public static final String MessageQueueThreadSleepTime_TAG = "MessageQueueThreadSleepTime";
	public static final String ConsolePrintFrequency_TAG = "ConsolePrintFrequency";
	public static final String ThreadReleaseResourceWaitCount_TAG = "ThreadReleaseResourceWaitCount";
	private static final String encrypt_ATTR = "encrypt";
	private static final String id_ATTR = "id";
	private static final String value_ATTR = "value";
	public static final String OP_URL_TAG = "OP_URL";
	public static final String OP_PORT_TAG = "OP_PORT";
	public static final String OP_USERNAME_TAG = "OP_USERNAME";
	public static final String OP_PASSWORD_TAG = "OP_PASSWORD";
	public static final String BusFtpURL_TAG = "BusFtpURL";
	public static final String BusFtpPort_TAG = "BusFtpPort";
	public static final String BusFtpUserName_TAG = "BusFtpUserName";
	public static final String BusFtpPassWord_TAG = "BusFtpPassWord";
	public static final String BusFtpLocalDir_TAG = "BusFtpLocalDir";
        public static final String BusFtpUploadUser_TAG = "BusFtpUploadUser";
	public static final String BusFtpUploadPW_TAG = "BusFtpUploadPW";
        public static final String BusFtpUploadPath_TAG = "BusFtpUploadPath";
	public static final String BusFtpDownloadPath_TAG = "BusFtpDownloadPath";

	public static final String BackUpLog_TAG = "BackUpLog";
	public static final String Port_TAG = "Port";
	public static final String ReadOneByteTimeOut_TAG = "ReadOneByteTimeOut";
	public static final String ReadOneMessageTimeOut_TAG = "ReadOneMessageTimeOut";
	public static final String FtpUserName_TAG = "FtpUserName";
	public static final String FtpUserPassword_TAG = "FtpUserPassword";
	public static final String FtpPort_TAG = "FtpPort";
	public static final String FtpTimeout_TAG = "FtpTimeout";
	public static final String FtpSocketTimeout_TAG = "FtpSocketTimeout";
	public static final String FtpRetryWaiting_TAG = "FtpRetryWaiting";
	public static final String FtpRetryTimes_TAG = "FtpRetryTimes";
	public static final String FtpLocalDir_TAG = "FtpLocalDir";
	public static final String ParmDstrbInterval_TAG = "ParmDstrbInterval";
	public static final String ParmDstrbPath_TAG = "ParmDstrbPath";
	public static final String ParmEncodePath_TAG = "ParmEncodePath";
	public static final String ParmSynchType_TAG = "ParmSynchType";
	public static final String MessageClassPrefix_TAG = "MessageClassPrefix";
	public static final String SendQueryWait_TAG = "SendQueryWait";
	public static final String MonitorRefresh_TAG = "MonitorRefresh";
	public static final String ReadWithThread_TAG = "ReadWithThread";
	public static final String TrafficDelayMaxDay_TAG = "TrafficDelayMaxDay";
	private Hashtable<String,Object> config = new Hashtable<String,Object>();
	private Hashtable<String,String> ftpPaths = new Hashtable<String,String>();

	/*
	 * private Hashtable commuDb = new Hashtable(); private Hashtable otherDb =
	 * new Hashtable(); private Hashtable flowDb = new Hashtable(); private
	 * Hashtable flowOtherDb = new Hashtable(); private Hashtable commuCp = new
	 * Hashtable(); private Hashtable otherCp = new Hashtable(); private
	 * Hashtable flowCp = new Hashtable(); private Hashtable flowOtherCp = new
	 * Hashtable();
	 */
	private Hashtable<String,String> opConnectionPool = new Hashtable<String,String>();
	private Hashtable<String,String> cmConnectionPool = new Hashtable<String,String>();
	private Hashtable<String,String> tkConnectionPool = new Hashtable<String,String>();
	private Hashtable<String,String> connectionPoolListenerThread = new Hashtable<String,String>();
	private Hashtable<String,String> communicationThreadPool = new Hashtable<String,String>();
	private Hashtable<String,String> messages = new Hashtable<String,String>();
	private CharArrayWriter contents = new CharArrayWriter();
	private boolean inCommon = false;
	/*
	 * 应急指挥中心使用
	 */
	private Hashtable<String,String> emergentFlowConnectionPool = new Hashtable<String,String>();// 连接配置变量
	public static final String IsWriteEmergentTraffic_TAG = "IsWriteEmergentTraffic";// 控制是否启用配置文件标签
	public static final String EmergentFlowConnectionPool_TAG = "EmergentFlowConnectionPool";// 连接配置标签
	private boolean inEmergentFlowConnectionPool = false;// 连接配置属性控制

	/*
	 * private boolean inCommuDb = false; private boolean inOtherDb = false;
	 * private boolean inFlowDb = false; private boolean inFlowOtherDb = false;
	 * private boolean inCommuCp = false; private boolean inOtherCp = false;
	 * private boolean inFlowCp = false; private boolean inFlowOtherCp = false;
	 */
	private boolean inOPConnectionPool = false;
	private boolean inCMConnectionPool = false;
	private boolean inTKConnectionPool = false;
	private boolean inCommunicationThreadPool = false;
	private boolean inConnectionPoolListenerThread = false;
	private boolean inJmsExceptionListenerThread = false;

	/*
	 * 线程池监控使用
	 */
	private Hashtable<String,String> ThreadPoolMonitor = new Hashtable<String,String>();
	public static final String ThreadPoolMonitor_TAG = "ThreadPoolMonitor";
	public static final String TPMonitorThreadSleepTime_TAG = "TPMonitorThreadSleepTime";
	public static final String ThreadMsgHandUpMaxNumberAllow_TAG = "ThreadMsgHandUpMaxNumberAllow";
	public static final String ThreadMsgAnalyzeClassPrefix_TAG = "ThreadMsgAnalyzeClassPrefix";
	private boolean inThreadPoolMonitor = false;
	/*
	 * 设备参数版本查询
	 */
	private Hashtable<String,String> DevParamVer = new Hashtable<String,String>();
	public static final String DevParamVer_TAG = "DevParamVer";
	public static final String DevParaVerThreadSleepTime_TAG = "DevParaVerThreadSleepTime";
	private boolean inDevParamVer = false;

	/*
	 * 参数自动下发
	 */
	private Hashtable<String,String> ParamAutoDown = new Hashtable<String,String>();
	public static final String ParamAutoDown_TAG = "ParamAutoDown";
	public static final String ParaDownloadThreadSleepTime_TAG = "ParaDownloadThreadSleepTime";
	private boolean inParamAutoDown = false;

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException {
		contents.reset();

		if (attr.getValue(encrypt_ATTR) != null) {
			if (attr.getValue(encrypt_ATTR).equals("true")) {
				this.encrypt = true;
			}
		}
		if (localName.equals(Common_TAG)) {
			this.inCommon = true;
		}
		if (localName.equals(FtpPath_TAG)) {
			ftpPaths.put(attr.getValue(id_ATTR), attr.getValue(value_ATTR));
		}
		if (localName.equals(Message_TAG)) {
			messages.put(attr.getValue(id_ATTR), attr.getValue(value_ATTR));
		}
		/*
		 * 
		 * if (localName.equals(CommuDb_TAG)) { this.inCommuDb = true; } if
		 * (localName.equals(OtherDb_TAG)) { this.inOtherDb = true; } if
		 * (localName.equals(FlowDb_TAG)) { this.inFlowDb = true; } if
		 * (localName.equals(FlowOtherDb_TAG)) { this.inFlowOtherDb = true; } if
		 * (localName.equals(CommuCp_TAG)) { this.inCommuCp = true; } if
		 * (localName.equals(OtherCp_TAG)) { this.inOtherCp = true; } if
		 * (localName.equals(FlowCp_TAG)) { this.inFlowCp = true; } if
		 * (localName.equals(FlowOtherCp_TAG)) { this.inFlowOtherCp = true; }
		 */

		if (localName.equals(CommuXMLHandler.OP_CONNECTION_POOL_TAG)) {
			this.inOPConnectionPool = true;
		}
		if (localName.equals(CommuXMLHandler.CM_CONNECTION_POOL_TAG)) {
			this.inCMConnectionPool = true;
		}
		if (localName.equals(CommuXMLHandler.TK_CONNECTION_POOL_TAG)) {
			this.inTKConnectionPool = true;
		}
		if (localName.equals(CommuXMLHandler.CommunicationThreadPool_TAG)) {
			this.inCommunicationThreadPool = true;
		}
		if (localName.equals(CommuXMLHandler.ConnectionPoolListenerThread_TAG)) {
			this.inConnectionPoolListenerThread = true;
		}

		// 应急指挥中心

		if (localName.equals(CommuXMLHandler.EmergentFlowConnectionPool_TAG)) {
			this.inEmergentFlowConnectionPool = true;
		}
		// 线程池监控

		if (localName.equals(CommuXMLHandler.ThreadPoolMonitor_TAG)) {
			this.inThreadPoolMonitor = true;
		}
		// 备参数版本

		if (localName.equals(CommuXMLHandler.DevParamVer_TAG)) {
			this.inDevParamVer = true;
		}
		// 参数自动下发
		if (localName.equals(CommuXMLHandler.ParamAutoDown_TAG)) {
			this.inParamAutoDown = true;
		}

	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		String value;

		value = contents.toString().trim();
		/*
		 * if set <tag></tag>,value will be "" if (value.equals("")){ value =
		 * null; //value can not be null,otherwise NullPointerException when
		 * Hashtable.put(key,value) }
		 */

		if (inCommon) {
			if (localName.equals(Common_TAG)) {
				inCommon = false;
			} else {
				if (encrypt) {
					config.put(localName, getUserIdPwd(value));
					encrypt = false;
				} else {
					config.put(localName, value);
				}
			}
		}
		/*
		 * if(inCommuDb){ if(localName.equals(CommuDb_TAG)){ inCommuDb = false;
		 * } else{ if(encrypt){ commuDb.put(localName,getUserIdPwd(value));
		 * encrypt = false; } else{ commuDb.put(localName, value); } } }
		 * 
		 * if(inOtherDb){ if(localName.equals(OtherDb_TAG)){ inOtherDb = false;
		 * } else{ if(encrypt){ otherDb.put(localName,getUserIdPwd(value));
		 * encrypt = false; } else{ otherDb.put(localName, value); } } }
		 * if(inFlowDb){ if(localName.equals(FlowDb_TAG)){ inFlowDb = false; }
		 * else{ if(encrypt){ floDb.put(localName,getUserIdPwd(value)); encrypt
		 * = false; } else{ flowDb.put(localName, value); } } }
		 * if(inFlowOtherDb){ if(localName.equals(FlowOtherDb_TAG)){
		 * inFlowOtherDb = false; } else{ if(encrypt){
		 * flowOtherDb.put(localName,getUserIdPwd(value)); encrypt = false; }
		 * else{ flowOtherDb.put(localName, value); } } }
		 * 
		 * if(inCommuCp){ if(localName.equals(CommuCp_TAG)){ inCommuCp = false;
		 * } else{ if(encrypt){ commuCp.put(localName,getUserIdPwd(value));
		 * encrypt = false; } else{ commuCp.put(localName, value); } } }
		 * 
		 * if(inOtherCp){ if(localName.equals(OtherCp_TAG)){ inOtherCp = false;
		 * } else{ if(encrypt){ otherCp.put(localName,getUserIdPwd(value));
		 * encrypt = false; } else{ otherCp.put(localName, value); } } }
		 * if(inFlowCp){ if(localName.equals(FlowCp_TAG)){ inFlowCp = false; }
		 * else{ if(encrypt){ flowCp.put(localName,getUserIdPwd(value)); encrypt
		 * = false; } else{ flowCp.put(localName, value); } } }
		 * 
		 * if(inFlowOtherCp){ if(localName.equals(FlowOtherCp_TAG)){
		 * inFlowOtherCp = false; } else{ if(encrypt){
		 * flowOtherCp.put(localName,getUserIdPwd(value)); encrypt = false; }
		 * else{ flowOtherCp.put(localName, value); } } }
		 */

		if (this.inOPConnectionPool) {
			if (localName.equals(CommuXMLHandler.OP_CONNECTION_POOL_TAG)) {
				this.inOPConnectionPool = false;
			} else {
				if (encrypt) {
					this.opConnectionPool.put(localName, getUserIdPwd(value));
					encrypt = false;
				} else {
					this.opConnectionPool.put(localName, value);
				}
			}
		}

		if (this.inCMConnectionPool) {
			if (localName.equals(CommuXMLHandler.CM_CONNECTION_POOL_TAG)) {
				this.inCMConnectionPool = false;
			} else {
				if (encrypt) {
					this.cmConnectionPool.put(localName, getUserIdPwd(value));
					encrypt = false;
				} else {
					this.cmConnectionPool.put(localName, value);
				}
			}
		}

		if (this.inTKConnectionPool) {
			if (localName.equals(CommuXMLHandler.TK_CONNECTION_POOL_TAG)) {
				this.inTKConnectionPool = false;
			} else {
				if (encrypt) {
					this.tkConnectionPool.put(localName, getUserIdPwd(value));
					encrypt = false;
				} else {
					this.tkConnectionPool.put(localName, value);
				}
			}
		}

		if (this.inEmergentFlowConnectionPool) {
			if (localName
					.equals(CommuXMLHandler.EmergentFlowConnectionPool_TAG)) {
				this.inEmergentFlowConnectionPool = false;
			} else {
				if (encrypt) {
					this.emergentFlowConnectionPool.put(localName,
							getUserIdPwd(value));
					encrypt = false;
				} else {
					this.emergentFlowConnectionPool.put(localName, value);
				}
			}
		}

		if (this.inCommunicationThreadPool) {
			if (localName.equals(CommuXMLHandler.CommunicationThreadPool_TAG)) {
				this.inCommunicationThreadPool = false;
			} else {
				this.communicationThreadPool.put(localName, value);
			}
		}

		if (this.inConnectionPoolListenerThread) {
			if (localName
					.equals(CommuXMLHandler.ConnectionPoolListenerThread_TAG)) {
				this.inConnectionPoolListenerThread = false;
			} else {
				this.connectionPoolListenerThread.put(localName, value);
			}
		}

		// 线程池监控

		if (this.inThreadPoolMonitor) {
			if (localName.equals(CommuXMLHandler.ThreadPoolMonitor_TAG)) {
				this.inThreadPoolMonitor = false;
			} else {
				this.ThreadPoolMonitor.put(localName, value);
			}

		}
		// 设备参数版本
		if (this.inDevParamVer) {
			if (localName.equals(CommuXMLHandler.DevParamVer_TAG)) {
				this.inDevParamVer = false;
			} else {
				this.DevParamVer.put(localName, value);
			}

		}
		// 参数自动下发
		if (this.inParamAutoDown) {
			if (localName.equals(CommuXMLHandler.ParamAutoDown_TAG)) {
				this.inParamAutoDown = false;
			} else {
				this.ParamAutoDown.put(localName, value);
			}

		}

		if (localName.equals(Config_TAG)) {
			config.put(FtpPaths_TAG, ftpPaths);
			config.put(Messages_TAG, messages);

			/*
			 * config.put(CommuDb_TAG,commuDb); config.put(OtherDb_TAG,otherDb);
			 * config.put(FlowDb_TAG,flowDb);
			 * config.put(FlowOtherDb_TAG,flowOtherDb);
			 * config.put(CommuCp_TAG,commuCp); config.put(OtherCp_TAG,otherCp);
			 * config.put(FlowCp_TAG,flowCp);
			 * config.put(FlowOtherCp_TAG,flowOtherCp);
			 */
			config.put(CommuXMLHandler.OP_CONNECTION_POOL_TAG,
					this.opConnectionPool);
			config.put(CommuXMLHandler.CM_CONNECTION_POOL_TAG,
					this.cmConnectionPool);
			config.put(CommuXMLHandler.TK_CONNECTION_POOL_TAG,
					this.tkConnectionPool);
			config.put(CommuXMLHandler.CommunicationThreadPool_TAG,
					this.communicationThreadPool);
			config.put(CommuXMLHandler.ConnectionPoolListenerThread_TAG,
					this.connectionPoolListenerThread);
			// 应急指挥中心

			config.put(CommuXMLHandler.EmergentFlowConnectionPool_TAG,
					this.emergentFlowConnectionPool);
			// 线程池监控

			config.put(CommuXMLHandler.ThreadPoolMonitor_TAG,
					this.ThreadPoolMonitor);
			// 设备参数版本
			config.put(CommuXMLHandler.DevParamVer_TAG, this.DevParamVer);
			// 参数自动下发
			config.put(CommuXMLHandler.ParamAutoDown_TAG, this.ParamAutoDown);

		}

	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		contents.write(ch, start, length);
	}

	public Hashtable<String,Object> parseConfigFile(String aFileName) throws SAXException,
			FileNotFoundException, IOException {
		XMLReader xr;

		xr = XMLReaderFactory
				.createXMLReader("org.apache.xerces.parsers.SAXParser");
		xr.setContentHandler(this);
		xr.parse(new InputSource(new FileReader(aFileName)));

		// Save the encrypted pasword to the config file
		if (needWriteFile) {
			encryptUserPwdInFile(aFileName);
		}

		return config;
	}

	protected void encryptUserPwdInFile(String aFileName)
			throws FileNotFoundException, IOException {
		int pos, posEnd, posCom1, posCom2;
		String value;
		StringBuffer fileContent = new StringBuffer();
		EncryptionUtil encryption = new EncryptionUtil();

		// Read the config file
		BufferedReader br = new BufferedReader(new FileReader(aFileName));
		String line = br.readLine();
		while (line != null) {
			// Skip the comment line
			posCom1 = line.indexOf("<!--");
			posCom2 = line.indexOf("-->");

			pos = line.indexOf(NOT_ENCRYPT_PREFIX);
			// Encrypt the userId or password
			if (pos != -1 && posCom1 == -1 && posCom2 == -1) {
				StringBuffer lineBuf = new StringBuffer(line);

				// Search the end position
				posEnd = line.indexOf("<", pos);

				// Encrypt and replace
				value = line.substring(pos + NOT_ENCRYPT_PREFIX.length(),
						posEnd).trim();
				value = encryption.biEncrypt(ENCRYPT_KEY, value);
				line = lineBuf.replace(pos, posEnd, value).toString();

			}

			fileContent.append("\r\n").append(line);

			line = br.readLine();
		}

		// Write new content to the config file
		FileWriter fw = new FileWriter(aFileName);
		fw.write(fileContent.toString().substring(2));
		fw.flush();
		fw.close();
	}

	protected String getUserIdPwd(String aUserIdPwd) {
		String result = "";
		EncryptionUtil encryption = new EncryptionUtil();

		if (aUserIdPwd.startsWith(NOT_ENCRYPT_PREFIX)) {
			result = aUserIdPwd.substring(NOT_ENCRYPT_PREFIX.length());
			needWriteFile = true;
		} else {
			result = encryption.biDecrypt(ENCRYPT_KEY, aUserIdPwd);
		}

		return result;
	}

}
