package com.goldsign.commu.frame.constant;

import java.util.Hashtable;

/**
 *
 * @author hejj
 */
public class FrameCodeConstant {

    public static boolean consolePrint = true;
    /**
     * 线程是否启动控制***********************************************************
     */
    public static int consolePrintFrequency = 1;
    public static boolean startParameterDistribute = true;
    public static boolean startSocketListener = true;
    public static boolean startConnectionPoolListener = true;
    /**
     * ***********************************************************
     */
    /**
     * ***********************************************************
     */
    public static String messageClassPrefix = "com.goldsign.commu.app.message.";
    public static int sendQueryWait = 1000;
    public static int monitorRefresh = 10000;
    public static int getMessageFrequency = 200;
    public static boolean readWithThread = false;
    /**
     * ***********************************************************
     */
    /**
     * Socket config***********************************************************
     */
    public static int port = 5001;
    public static int readOneByteTimeOut = 3000;
    public static int readOneMessageTimeOut = 3000;
    public static int threadSleepTime = 100;
    public static int threadPriority = 5;
    public static int maxThreadNumber = 5;
    public static int maxSearchNum = 2;
    public static int readThreadPriorityAdd = 0;
    public static String unHanledMsgLogDir = "";
    public static int threadBufferCapacity = 1000;
    public static int threadBufferIncrement = 1000;
    public static int priorityThreadBufferCapacity = 1000;
    public static int priorityThreadBufferIncrement = 1000;
    /**
     * ***********************************************************
     */
    /**
     * 消息的队列类型***********************************************************
     */
    public static String threadMsgQueueTypePri = "1";// 优先队列
    public static String threadMsgQueueTypeOrd = "2";// 普通队列
    /**
     * ***********************************************************
     */
    /**
     * 进出站客流消息***********************************************************
     */
    public static int trafficDelayMaxDay = 1;// 最大滞留天数 -1:不限制
    public final static int trafficDelayUnLimit = -1;// 不限制
    /**
     * ***********************************************************
     */
    /**
     * 缓存的基本数据***********************************************************
     */
    public static Hashtable<String, String> ALL_LCC_IP;
    public static Hashtable<String, String> ALL_LCC_IP_NAME = new Hashtable<String, String>();
    public static Hashtable<String, String> ALL_BOM_IP;
    public static Hashtable<String, String> all_connecting_ip = new Hashtable<String, String>();
    public static Hashtable<String, String> FTP_PATHS;
    public static Hashtable<String, String> MESSAGE_CLASSES;
    public static Hashtable<String, String> DEV_STATUS_MAPPING;
    public static String ACC_LINE_CODE = "9900";
    //add by lindaquan in 20160118
    public static Hashtable<String, String> MOBILE_IP;//手机支付平台IP
    public static String MOBILE_CONTROL_KEY = "business.mobile.control";
    public static String MOBILE_CONTROL = "0";//手机支付业务处理控制。1：启用 0：不启用。
    public static String MOBILE_INTERVAL_KEY = "business.mobile.interval";
    public static int MOBILE_INTERVAL = 60000;//手机支付平台消息下发时间间隔
    // add by lindaquan in 20160427
    public static String CACHE_REFRESH_INTERVAL_KEY = "commu.cache.interval";
    public static int CACHE_REFRESH_INTERVAL = 1800000;//缓存值刷新时间间隔
    /**
     * Ftp config
     */
    public static String ftpUserName = "anonymous";
    public static String ftpUserPassword = "";
    public static int ftpPort = 21;
    public static String ftpLocalDir = "";
    public static int ftpTimeout = 10000;
    public static int ftpSocketTimeout = 10000; 
    public static int ftpRetryTime = 3;
    public static int ftpRetryWaiting = 10000;
    public static String ftpTmpFilePrefix = "TMP.";
    /**
     * ***********************************************************
     */
    public static Hashtable commuConfig = null;
    public static int messageQueueThreadSleepTime = 60000;
    /**
     * Parameter distribute config
     */
    public static int parmDstrbInterval = 60000;
    public static String parmDstrbPath = "";
    public static String parmEncodePath = "";
    public static String parmSynchType = "'0101','0201','0202','0203','0204','0205','0301','0302','0303','0304','0305','0400','0501','0601','0602','0603','0604','0801','0802','0900','0901','0902','0903'";
    public static long BUFFERCLEAR_SLEEPTIME = 300000;// 300000;//缓存清理的循环时间间隔
    public static String BUFFERCLEAR_RUN_TIME = "0300";// 缓存清理的时间,多个时间点使用＃号分隔
    public static String SQUAD_TIME = "0230";// 运营开始时间
    /**
     * 版本标志***********************************************************
     */
    public static String RECORD_FLAG_CURRENT = "0";// 当前版本
    public static String RECORD_FLAG_FUTURE = "1";// 未来版本
    /**
     * 系统标识***********************************************************
     */
    public static String SYSTEM_COMMU = "2";// 通讯系统
    /**
     * ***********************************************************
     */
    public static boolean isWriteEmergentTraffic = false;// 控制是否写应急指挥中心客流
    public static boolean isWriteEmergentTrafficForDb = false;
    public final static String FLAG_ENTRY = "0";// 进站标志
    public final static String FLAG_Exit = "1";// 出战标志
    public final static String[] FIVE_HOUR_MINS = {"00", "05", "10", "15",
        "20", "25", "30", "35", "40", "45", "50", "55"};
    /**
     * 运营管理系统的FTP配置开始
     */
    public static String OP_URL; // IP
    public static int OP_PORT;// 端口
    public static String OP_USERNAME;// 用户名
    public static String OP_PASSWORD;// 密码
    /**
     * 一卡通中心的FTP配置开始
     */
    public static String busFtpURL; // IP
    public static int busFtpPort;// 端口
    public static String busFtpUserName;// 下载用户名
    public static String busFtpPassWord;// 下载密码
    public static String busFtpLocalDir;// 公交数据存放的本地路径
    public static String busFtpUploadUser;// 上传用户名
    public static String busFtpUploadPW;// 上传密码
    public static String BusFtpUploadPath="";// 上传目录
    public static String BusFtpDownloadPath="";// 下载目录
    public static int busFtpDelayInterval = 5000;//延时下载时间
    public static String busFtpDelayInterval_key = "busftp.delay.interval";//配置表key值
    public static int busFtpInterval = 300000;//一卡通下载间隔时间
    public static String busFtpInterval_key = "busftp.interval";//配置表key值
    //add by zhongzq 默认1为启用
    public static String TCC_INTERFACE_CONTROL = "0";
    public static final String TCC_INTERFACE_USE_KEY = "1";
    public static final String TCC_CONTROL_KEY = "business.tcc.control";
    public static final String TCC_IP_TYPE = "1";
    //外部ip
    public static Hashtable<String, String> EXTERNAL_IP = new Hashtable<String, String>();
    public static String TCC_IP = "127.0.0.1";
    public static final String MODE_CONTROL_KEY = "business.passageflow.handlmode.control";
    public static final String MODE_STANDARD = "1";
    public static String USER_MODE = "0";

    //wuqq 20201009 黑名单下发时间控制
    public static String BLACKDISTRI_BEGIN = "0330";// 黑名单下发开始时间
    public static String BLACKDISTRI_END = "0430";// 黑名单下发结束时间
    public static final String BLACK_DISTRIBUTE_BEGINKEY = "blacklist.distribute.begin";
    public static final String BLACK_DISTRIBUTE_ENDKEY = "blacklist.distribute.end";

}
