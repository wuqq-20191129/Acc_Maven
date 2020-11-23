package com.goldsign.commu.frame.server;

import com.goldsign.commu.app.dao.ExternalIpDao;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.*;
import com.goldsign.commu.frame.dao.*;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.exception.ConfigFileException;
import com.goldsign.commu.frame.listener.CommuConnectionPoolListener;
import com.goldsign.commu.frame.manager.CommuThreadManager;
import com.goldsign.commu.frame.thread.*;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.xml.CommuXMLHandler;
import com.goldsign.lib.db.util.DbcpHelper;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author hejj
 */
public class CommuServer {

    private final String LOGFILE = "Log4jConfig.xml";
    private final String CONFIGFILE = "CommuConfig.xml";
    private final String LogFileName = "commu.log";

    public static ServerSocket serverSocket;
    @SuppressWarnings("rawtypes")
    public static HashMap connectedSockets = new HashMap();
    public static int connectID = 0;
    private boolean stoped = false;
    private static Logger logger = Logger
            .getLogger(CommuServer.class.getName());
    private CommuThreadManager ctm = new CommuThreadManager();
    /**
     * 日志记录使用
     */
    private long hdlStartTime; // 处理的起始时间
    private long hdlEndTime;// 处理的结束时间

    public static void main(String[] args) {
        CommuServer commuServer = new CommuServer();
        Runtime.getRuntime().addShutdownHook(new CommuStopHook(commuServer));
        commuServer.start();
    }

    public void start() {
        try {
            hdlStartTime = System.currentTimeMillis();

            logger.info("通讯服务器正在启动 ...");

            // 从配置文件及数据库取得数据放入ApplicationConstant对应缓存变量中.
            initConfig();

            // 检查配置是否设置合理
            checkConfigure();

            // 从数据库的参数下发表中取得需下发的参数,从参数表中取得参数数据形成文件
            if (FrameCodeConstant.startParameterDistribute) {
                startParameterDistribute();
                logger.info("参数下发监听已启动 ...");
            }

            // 启动图形界面
            /*
             * if(startMonitor){ startMonitor(); }
             */
            // 启动消息处理线程
            ctm.startHandleThreads();

            // 启动缓存清理线程
            startBufferClearThread();

            // 启动消息队列线程
            startMessageQueueThread();

            // 启动参数自动下发线程
            // startParaAutoDownloadThread();
            // 启动连接池监听线程
            if (FrameCodeConstant.startConnectionPoolListener) {
                startConnectionPoolListener();
                logger.info("连接池监听正在启动 ...");
            }

            // 启动日志级别线程
            startLogLevelMonitor();
            logger.info("日志级别监听已启动 ...");

            // 启动设备参数版本查询线程
            startDevParaVerQuery();
            logger.info("设备参数版本查询线程已启动 ...");

            // 启动线程池监控
            startThreadMonitor();

            // 启动票务接口线程
            startTkThread();
            logger.info("票务接口线程已启动 ...");
//            // 启动一卡通接口线程
             startBusThread();
            logger.info("一卡通接口线程已启动 ...");
            //手机支付信息下发
//            startMobileInfoThread();
//            logger.info("手机支付平台信息下发线程已启动 ...");
            //add by zhongziqi tcc处理接口启动
//            if (FrameCodeConstant.TCC_INTERFACE_USE_KEY.equals(FrameCodeConstant.TCC_INTERFACE_CONTROL)) {
            startTccInterface();
            logger.info("TCC接口线程已启动 ...使用标识：" + FrameCodeConstant.TCC_INTERFACE_CONTROL);
//            }else{

//            }
            //定时刷新缓存变量
            startRefreshCacheThread();
            logger.info("定时刷新缓存变量线程已启动 ...");
            hdlEndTime = System.currentTimeMillis();
            // 记录日志
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_START, "",
                            hdlStartTime, hdlEndTime,
                    FrameLogConstant.RESULT_HDL_SUCESS, Thread.currentThread().getName(),
                            FrameLogConstant.LOG_LEVEL_INFO, "通讯启动");
            // 启动线路消息接收SOCKET服务器
            if (FrameCodeConstant.startSocketListener) {
                startSocketListener();
            }
        } catch (ConfigFileException e) {
            logger.error("配置文件读入失败 - ", e);
        } catch (BindException e) {
            logger.error("端口 " + FrameCodeConstant.port + "　已经被服务占用！", e);
            // 记录日志
            hdlEndTime = System.currentTimeMillis();
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_START, "",
                    hdlStartTime, hdlEndTime, FrameLogConstant.RESULT_HDL_FAIL,
                    Thread.currentThread().getName(),
                    FrameLogConstant.LOG_LEVEL_ERROR_SYS, e.getMessage());

        } catch (Exception e) {
            logger.error("Error -", e);
            hdlEndTime = System.currentTimeMillis();
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_START, "",
                    hdlStartTime, hdlEndTime, FrameLogConstant.RESULT_HDL_FAIL,
                    Thread.currentThread().getName(),
                    FrameLogConstant.LOG_LEVEL_ERROR_SYS, e.getMessage());

        } finally {
            // 如是连接池问题引起socket关闭,则无需关闭程序
            if (!CommuConnectionPoolListener.isSqlConExceptionClose
                    .booleanValue()) {
                logger.info("程序结束处理 ...");
                stop();
            }
        }
    }

    private void startTccInterface() {
//        Executor executor = Executors.newFixedThreadPool(1);
//        executor.execute(new TccInterface());
        Thread tcc = new Thread(new TccInterface(), "tcc-thread");
        tcc.start();
    }

    private void startBusThread() {
        Thread thread = new Thread(new BusDataImportThread());
        //增加检测未捕获异常处理
        thread.setUncaughtExceptionHandler(new ExceptionHandler());
        thread.start();

    }

    private void startMobileInfoThread() {
        MobileInfoThread mb = new MobileInfoThread();
        Thread t = new Thread(mb, "MobileInfo");
        t.start();
    }

    private void startRefreshCacheThread() {
        RefreshCacheThread rc = new RefreshCacheThread();
        Thread t = new Thread(rc, "RefreshCache");
        t.start();
    }

    private void startTkThread() {

        // 解析文件
        ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
        TKInterfaceThread task = new TKInterfaceThread();
        /*
         * Thread thread = new Thread(task, "票务文件下发-解析线程："); // thread.start();
         */
        service.scheduleAtFixedRate(task, 1000L, 3000L,
                TimeUnit.MILLISECONDS);

        // // 文件数据批量入库
        ScheduledExecutorService service2 = Executors.newScheduledThreadPool(5);
        for (int i = 0; i < 5; i++) {
            TKFileDataInDBThread task2 = new TKFileDataInDBThread();
            service2.scheduleAtFixedRate(task2, 1000L, 3000L,
                    TimeUnit.MILLISECONDS);
        }

    }

    /**
     * 检查配置信息是否合理
     */
    private void checkConfigure() {
        if (FrameCodeConstant.readOneMessageTimeOut != 3000) {
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_CONFIG, "",
                    System.currentTimeMillis(), System.currentTimeMillis(),
                    FrameLogConstant.RESULT_HDL_WARN, Thread.currentThread()
                            .getName(), FrameLogConstant.LOG_LEVEL_WARN,
                    "接口规范定义读消息超时为3秒");
        }

        if (FrameCodeConstant.ftpTimeout > 10000) {
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_CONFIG, "",
                    System.currentTimeMillis(), System.currentTimeMillis(),
                    FrameLogConstant.RESULT_HDL_WARN, Thread.currentThread()
                            .getName(), FrameLogConstant.LOG_LEVEL_WARN,
                    "FTP连接超时大于10秒，可能会影响性能");
        }

        if (FrameCodeConstant.maxThreadNumber > 30) {
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_CONFIG, "",
                    System.currentTimeMillis(), System.currentTimeMillis(),
                    FrameLogConstant.RESULT_HDL_WARN, Thread.currentThread()
                            .getName(), FrameLogConstant.LOG_LEVEL_WARN,
                    "线程总数量大于30，可能会影响性能");
        }

    }

    public void stop() {
        if (!stoped) {
            stoped = !stoped;
            logger.info("通讯服务器正在准备停止...");
            logger.info("通讯服务器将在1分钟后停止...");

            try {
                serverSocket.close();
                Thread.sleep(60000);
            } catch (Exception e) {
            }
            /*
             * try { FrameCodeConstant.COMMU_DBCPHELPER.close(); } catch
             * (Exception e) {
             *
             * }
             */
            try {
                // FrameCodeConstant.OTHER_DBCPHELPER.close();
                logger.info("关闭连接池OP_DBCPHELPER...");
                FrameDBConstant.OP_DBCPHELPER.close();
            } catch (Exception e) {
                logger.info("关闭连接池OP_DBCPHELPER发生异常", e);
            }
            try {
                // FrameDBConstant.FLOW_DBCPHELPER.close();
                logger.info("关闭连接池CM_DBCPHELPER...");
                FrameDBConstant.CM_DBCPHELPER.close();
            } catch (Exception e) {
                logger.info("关闭连接池CM_DBCPHELPER发生异常", e);
            }
            try {
                logger.info("关闭连接池TK_DBCPHELPER...");
                FrameDBConstant.TK_DBCPHELPER.close();
            } catch (Exception e) {
                logger.info("关闭连接池TK_DBCPHELPER发生异常", e);
            }
            // try {
            // // FrameDBConstant.FLOWOTHER_DBCPHELPER.close();
            // logger.info("关闭连接池FLOWSTATUS_DBCPHELPER...");
            // FrameDBConstant.FLOWSTATUS_DBCPHELPER.close();
            // } catch (Exception e) {
            // logger.info("关闭连接池FLOWSTATUS_DBCPHELPER发生异常", e);
            // }
            //
            // if (FrameDBConstant.isWriteEmergentTraffic) {
            // try {
            // // FrameDBConstant.FLOWOTHER_DBCPHELPER.close();
            // logger.info("关闭连接池EMERGENT_DBCPHELPER...");
            // FrameDBConstant.EMERGENT_DBCPHELPER.close();
            // } catch (Exception e) {
            // logger.info("关闭连接池EMERGENT_DBCPHELPER发生异常", e);
            // }
            // }
            logger.info("处理线程池所有线程...");
            CommuThreadManager.stopHandleThread();
            /*
             * try { FrameDBConstant.COMMU_DBHELPER.closeConnection(); } catch
             * (Exception e) {
             *
             * }
             */
            /*
             * try{ FrameCodeConstant.OTHER_DBHELPER.closeConnection(); }
             * catch(Exception e){ logger.error("Server stop error! - " + e); }
             */
            logger.info("通讯服务器已停止！");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
        }
    }

    private void startParameterDistribute() {
        ParameterDistributeThread pd = new ParameterDistributeThread();
        Thread t = new Thread(pd, "Param");
        t.start();
    }

    private void startConnectionPoolListener() {
        Hashtable ht;
        CommuConnectionPoolListener ccpl;

        ht = (Hashtable) FrameCodeConstant.commuConfig
                .get(CommuXMLHandler.OP_CONNECTION_POOL_TAG);
        ccpl = new CommuConnectionPoolListener(ht, "OP运营库",
                FrameDBConstant.FLAG_OP);
        ccpl.start();

        ht = (Hashtable) FrameCodeConstant.commuConfig
                .get(CommuXMLHandler.CM_CONNECTION_POOL_TAG);
        ccpl = new CommuConnectionPoolListener(ht, "CM通讯库",
                FrameDBConstant.FLAG_CM);
        ccpl.start();

        ht = (Hashtable) FrameCodeConstant.commuConfig
                .get(CommuXMLHandler.TK_CONNECTION_POOL_TAG);
        ccpl = new CommuConnectionPoolListener(ht, "TK票务库",
                FrameDBConstant.FLAG_TK);
        ccpl.start();

        // if (FrameDBConstant.isWriteEmergentTraffic) {
        // ht = (Hashtable) FrameCodeConstant.commuConfig
        // .get(CommuXMLHandler.EmergentFlowConnectionPool_TAG);
        // ccpl = new CommuConnectionPoolListener(ht, "emergent",
        // FrameDBConstant.FLAG_EMERGENT);
        // ccpl.start();
        // }
    }

    private void startMessageQueueThread() {
        MessageQueueThread t = new MessageQueueThread();
        t.start();
        logger.info("消息队列管理线程已启动!");
    }

    private void startThreadMonitor() {
        CommuThreadPoolMonitor t = new CommuThreadPoolMonitor();
        t.start();
        logger.info("线程池监控已启动 !");
    }

    private void startBufferClearThread() {
        // testBuffer();
        CommuBufferClearThread t = new CommuBufferClearThread();
        t.start();
        logger.info("缓存清理线程已启动 !");
    }

    private void startParaAutoDownloadThread() {
        // testBuffer();
        ParaAutoDWThread t = new ParaAutoDWThread();
        t.start();
        logger.info("参数定时自动下发线程已启动 !");
    }

    private void initConfig() throws ConfigFileException {
        Hashtable<String, Object> commuConfig;
        // init log4j
        DOMConfigurator.configureAndWatch(LOGFILE);
        logger.info("日志配置设置成功!");
        // set SystemStartDateTime;
        Date ssdt = new Date(System.currentTimeMillis());
        PubUtil.SystemStartDateTime = DateHelper.dateToString(ssdt);
        try {
            System.out.println(System.getProperty("user.dir"));
            CommuXMLHandler commuXMLHandler = new CommuXMLHandler();
            commuConfig = commuXMLHandler.parseConfigFile(CONFIGFILE);
            FrameCodeConstant.commuConfig = commuConfig;
            logger.info("成功解析配置文件：" + CONFIGFILE);

            // 备份日志
            initForBackFile(commuConfig);

            // 初始化socket配置
            initSocket(commuConfig);
            logger.info("成功初始化socket配置!");

            // 初始化客流配置
            initTraffic(commuConfig);
            logger.info("成功初始化客流配置!");

            // 线程监控配置
            initThreadMonitor(commuConfig);
            logger.info("成功初始化线程监控配置!");

            // 初始化参数下发
            initParamDistribute(commuConfig);
            logger.info("成功初始化初始化参数下发配置!");

            // 初始化应用相关配置
            initApp(commuConfig);
            logger.info("成功初始化应用相关配置!");

            // 初始化FTP配置
            initFtp(commuConfig);
            logger.info("成功初始化FTP配置!");

            // 获取控制是否写应急指挥中心客流标志
            FrameDBConstant.isWriteEmergentTraffic = Boolean.valueOf(
                    (String) commuConfig
                            .get(CommuXMLHandler.IsWriteEmergentTraffic_TAG))
                    .booleanValue();

            // 初始化数据库连接池
            initFromConnectionPool(commuConfig);
            logger.info("成功初始化数据库连接池!");

            // 初始化线程池配置
            initThreadPool(commuConfig);
            logger.info("成功初始化线程池配置!");

            // 初始化线程池监控
            initThreadPoolMonitor(commuConfig);
            logger.info("成功初始化线程池监控配置!");

            // 设备参数版本查询
            initDevParamVer(commuConfig);
            logger.info("成功初始化设备参数版本查询配置!");

            // 参数自动下发
            initParamAutoDown(commuConfig);
            logger.info("成功初始化参数自动下发配置!");

            // 初始化启动选项控制
            initFromStartOption(commuConfig);
            logger.info("成功初始化启动选项控制!");

            // Initialize ftp paths
            FrameCodeConstant.FTP_PATHS = (Hashtable<String, String>) commuConfig
                    .get(CommuXMLHandler.FtpPaths_TAG);
            logger.info("成功初始化FTP路径!");
            // Initialize messages
//            FrameCodeConstant.MESSAGE_CLASSES = (Hashtable<String, String>) commuConfig
//                    .get(CommuXMLHandler.Messages_TAG);

            // 初始化数据库数据
            initFromDB();

        } catch (Exception e) {
            logger.error("初始化 " + CONFIGFILE + " 错误! ", e);
            throw new ConfigFileException("初始化 " + CONFIGFILE + " 错误! " + e);
        }

    }

    @SuppressWarnings("rawtypes")
    private void initForBackFile(Hashtable commuConfig)
            throws ConfigFileException {
        String value = (String) commuConfig.get(CommuXMLHandler.BackUpLog_TAG);
        if (value == null || value.length() == 0) {
            throw new ConfigFileException("需配置Tag <"
                    + CommuXMLHandler.BackUpLog_TAG + "> 日志备份文件!");
        } else {
            String oldLog = DateHelper.datetimeToString(new Date()) + ".log";
            File oldLogFile = new File(value, oldLog);
            File logFile = new File(LogFileName);
            if (logFile.exists()) {
                logFile.renameTo(oldLogFile);
                logger.info("日志备份");
            }

        }
    }

    @SuppressWarnings("rawtypes")
    private void initTraffic(Hashtable commuConfig) throws ConfigFileException {
        String value = (String) commuConfig
                .get(CommuXMLHandler.TrafficDelayMaxDay_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.trafficDelayMaxDay = Integer.parseInt(value);
        }
    }

    @SuppressWarnings("rawtypes")
    private void initThreadMonitor(Hashtable commuConfig)
            throws ConfigFileException {
        String value = (String) commuConfig
                .get(CommuXMLHandler.ThreadReleaseResourceWaitCount_TAG);
        if (value != null && value.length() != 0) {
            FrameThreadPoolConstant.ThreadReleaseResourceWaitCount = Integer
                    .parseInt(value);
        }
    }

    @SuppressWarnings("rawtypes")
    private void initSocket(Hashtable commuConfig) throws ConfigFileException {
        // Socket config
        String value = (String) commuConfig.get(CommuXMLHandler.Port_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.port = Integer.parseInt(value);
        }

        value = (String) commuConfig
                .get(CommuXMLHandler. ReadOneByteTimeOut_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.readOneByteTimeOut = Integer.parseInt(value);
        }

        value = (String) commuConfig
                .get(CommuXMLHandler.ReadOneMessageTimeOut_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.readOneMessageTimeOut = Integer.parseInt(value);
        }

        value = (String) commuConfig
                .get(CommuXMLHandler.MessageQueueThreadSleepTime_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.messageQueueThreadSleepTime = Integer
                    .parseInt(value);

        }
        value = (String) commuConfig
                .get(CommuXMLHandler.ConsolePrintFrequency_TAG);
        if (value != null && value.length() != 0 && !value.equals("0")) {
            FrameCodeConstant.consolePrintFrequency = Integer.parseInt(value);

        }

    }

    @SuppressWarnings("rawtypes")
    private void initParamDistribute(Hashtable commuConfig)
            throws ConfigFileException {
        // Parameter distribute config
        String value = (String) commuConfig
                .get(CommuXMLHandler.ParmDstrbInterval_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.parmDstrbInterval = Integer.parseInt(value);
        }

        value = (String) commuConfig.get(CommuXMLHandler.ParmDstrbPath_TAG);
        if (value == null || value.length() == 0) {
            throw new ConfigFileException("Tag <"
                    + CommuXMLHandler.ParmDstrbPath_TAG
                    + "> and setting are required in the config file!");
        }
        FrameCodeConstant.parmDstrbPath = value;

        value = (String) commuConfig.get(CommuXMLHandler.ParmEncodePath_TAG);
        if (value == null || value.length() == 0) {
            // throw new
            // CommuException("Tag <"+ParmEncodePath_TAG+"> and setting are required in the config file!");
        }
        FrameCodeConstant.parmEncodePath = value;

        // Parameter synch types config
        value = (String) commuConfig.get(CommuXMLHandler.ParmSynchType_TAG);
        if (value == null || value.length() == 0) {
            // throw new
            // CommuException("Tag <"+ParmSynchType_TAG+"> and setting are required in the config file!");
        } else {
            FrameCodeConstant.parmSynchType = value;
        }

    }

    @SuppressWarnings("rawtypes")
    private void initApp(Hashtable commuConfig) throws ConfigFileException {
        // APP config
        String value = (String) commuConfig
                .get(CommuXMLHandler.MessageClassPrefix_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.messageClassPrefix = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.SendQueryWait_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.sendQueryWait = Integer.parseInt(value);
        }

        value = (String) commuConfig.get(CommuXMLHandler.MonitorRefresh_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.monitorRefresh = Integer.parseInt(value);
        }

        value = (String) commuConfig.get(CommuXMLHandler.ReadWithThread_TAG);
        if (value != null && value.length() != 0) {
            if (value.equals("false")) {
                FrameCodeConstant.readWithThread = false;
            } else {
                FrameCodeConstant.readWithThread = true;
            }
        } else {
            FrameCodeConstant.readWithThread = true;
        }

    }

    @SuppressWarnings("rawtypes")
    private void initThreadPoolMonitor(Hashtable commuConfig) {
        Hashtable ht = (Hashtable) commuConfig
                .get(CommuXMLHandler.ThreadPoolMonitor_TAG);
        FrameThreadPoolConstant.TPMonitorThreadSleepTime = Integer
                .parseInt((String) ht.get("TPMonitorThreadSleepTime"));
        FrameThreadPoolConstant.ThreadMsgAnalyzeClassPrefix = (String) ht
                .get("ThreadMsgAnalyzeClassPrefix");
        FrameThreadPoolConstant.ThreadMsgHandUpMaxNumberAllow = Integer
                .parseInt((String) ht.get("ThreadMsgHandUpMaxNumberAllow"));

    }

    @SuppressWarnings("rawtypes")
    private void initDevParamVer(Hashtable commuConfig) {
        Hashtable ht = (Hashtable) commuConfig
                .get(CommuXMLHandler.DevParamVer_TAG);
        FrameParameterConstant.devParaVerThreadSleepTime = Integer
                .parseInt((String) ht.get("DevParaVerThreadSleepTime"));

    }

    @SuppressWarnings("rawtypes")
    private void initParamAutoDown(Hashtable commuConfig) {
        Hashtable ht = (Hashtable) commuConfig
                .get(CommuXMLHandler.ParamAutoDown_TAG);
        FrameParameterConstant.paraDownloadThreadSleepTime = Integer
                .parseInt((String) ht.get("ParaDownloadThreadSleepTime"));

    }

    @SuppressWarnings("rawtypes")
    private void initFtp(Hashtable commuConfig) throws ConfigFileException {
        // EncryptionUtil encryption = new EncryptionUtil();
        // Ftp config
        String value = (String) commuConfig
                .get(CommuXMLHandler.FtpUserName_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.ftpUserName = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.FtpUserPassword_TAG);
        if (value != null) { // ftp password can be ""
            FrameCodeConstant.ftpUserPassword = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.FtpPort_TAG);
        if (value != null) { // ftp port can be ""
            FrameCodeConstant.ftpPort = Integer.parseInt(value);
        }
        value = (String) commuConfig.get(CommuXMLHandler.FtpTimeout_TAG);
        if (value != null) { // ftp 超时时间
            FrameCodeConstant.ftpTimeout = new Integer(value).intValue();
        }

        value = (String) commuConfig.get(CommuXMLHandler.FtpSocketTimeout_TAG);
        if (value != null) { // ftp socket超时时间
            FrameCodeConstant.ftpSocketTimeout = new Integer(value).intValue();
        }

        value = (String) commuConfig.get(CommuXMLHandler.FtpRetryWaiting_TAG);
        if (value != null) { // ftp 重试等待时间
            FrameCodeConstant.ftpRetryWaiting = new Integer(value).intValue();
        }

        value = (String) commuConfig.get(CommuXMLHandler.FtpRetryTimes_TAG);
        if (value != null) { // ftp 重试次数
            FrameCodeConstant.ftpRetryTime = new Integer(value).intValue();
        }

        value = (String) commuConfig.get(CommuXMLHandler.FtpLocalDir_TAG);
        logger.info("FtpLocalDir:" + value);
        if (value == null || value.length() == 0) {
            throw new ConfigFileException("Tag <"
                    + CommuXMLHandler.FtpLocalDir_TAG
                    + "> and setting are required in the config file!");
        }
        File localDir = new File(value);
        if (!localDir.exists()) {
            throw new ConfigFileException("设置FptFtp本地目录错误，本地目录不存在");
        }
        FrameCodeConstant.ftpLocalDir = value;

        value = (String) commuConfig.get(CommuXMLHandler.BusFtpLocalDir_TAG);
        logger.info("BusFtpLocalDir:" + value);
        if (value == null || value.length() == 0) {
            throw new ConfigFileException("Tag <"
                    + CommuXMLHandler.BusFtpLocalDir_TAG
                    + "> and setting are required in the config file!");
        }
        File buslocalDir = new File(value);
        if (!buslocalDir.exists()) {
            throw new ConfigFileException("设置BusFtpLocalDir本地目录错误，本地目录不存在");
        }
        FrameCodeConstant.busFtpLocalDir = value;

        value = (String) commuConfig.get(CommuXMLHandler.OP_URL_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.OP_URL = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.OP_PORT_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.OP_PORT = new Integer(value).intValue();
        }

        value = (String) commuConfig.get(CommuXMLHandler.OP_USERNAME_TAG);
        if (value != null && value.length() != 0) {
            // FrameCodeConstant.OP_USERNAME = encryption.biDecrypt(
            // CommuXMLHandler.ENCRYPT_KEY, value);
            FrameCodeConstant.OP_USERNAME = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.OP_PASSWORD_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.OP_PASSWORD = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.BusFtpURL_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.busFtpURL = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.BusFtpPort_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.busFtpPort = new Integer(value).intValue();
        }

        value = (String) commuConfig.get(CommuXMLHandler.BusFtpUserName_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.busFtpUserName = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.BusFtpPassWord_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.busFtpPassWord = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.BusFtpUploadUser_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.busFtpUploadUser = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.BusFtpUploadPW_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.busFtpUploadPW = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.BusFtpUploadPath_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.BusFtpUploadPath = value;
        }

        value = (String) commuConfig.get(CommuXMLHandler.BusFtpDownloadPath_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.BusFtpDownloadPath = value;
        }

    }

    @SuppressWarnings("rawtypes")
    private void initThreadPool(Hashtable commuConfig) {
        // 获得线程池的配置属性

        Hashtable ht = (Hashtable) commuConfig
                .get(CommuXMLHandler.CommunicationThreadPool_TAG);
        FrameCodeConstant.maxThreadNumber = Integer.parseInt((String) ht
                .get("MaxThreadNumber"));
        FrameCodeConstant.maxSearchNum = Integer.parseInt((String) ht
                .get("MaxSearchNum"));
        FrameCodeConstant.threadSleepTime = Integer.parseInt((String) ht
                .get("ThreadSleepTime"));
        FrameCodeConstant.threadPriority = Thread.NORM_PRIORITY
                + Integer.parseInt((String) ht.get("ThreadPriorityAdd"));
        FrameCodeConstant.unHanledMsgLogDir = (String) ht
                .get("UnHanledMsgLogDir");

        FrameCodeConstant.threadBufferCapacity = Integer.parseInt((String) ht
                .get("ThreadBufferCapacity"));
        FrameCodeConstant.threadBufferIncrement = Integer.parseInt((String) ht
                .get("ThreadBufferIncrement"));
        FrameCodeConstant.priorityThreadBufferCapacity = Integer
                .parseInt((String) ht.get("PriorityThreadBufferCapacity"));
        FrameCodeConstant.priorityThreadBufferIncrement = Integer
                .parseInt((String) ht.get("PriorityThreadBufferIncrement"));

        FrameCodeConstant.readThreadPriorityAdd = Integer.parseInt((String) ht
                .get("ReadThreadPriorityAdd"));

        FrameCodeConstant.consolePrint = Boolean.valueOf(
                (String) commuConfig.get("ConsolePrint")).booleanValue();
        FrameCodeConstant.getMessageFrequency = Integer
                .parseInt((String) commuConfig.get("GetMessageFrequency"));

        // 获得连接池监听线程配置属性
        ht = (Hashtable) commuConfig
                .get(CommuXMLHandler.ConnectionPoolListenerThread_TAG);
        FrameDBConstant.CPListenerThreadSleepTime = Integer
                .parseInt((String) ht
                        .get(CommuXMLHandler.CPListenerThreadSleepTime_TAG));
        FrameDBConstant.TestSqlForOP = (String) ht
                .get(CommuXMLHandler.TestSqlForOP_TAG);
        FrameDBConstant.TestSqlForCM = (String) ht
                .get(CommuXMLHandler.TestSqlForCM_TAG);
        FrameDBConstant.TestSqlForTK = (String) ht
                .get(CommuXMLHandler.TestSqlForTK_TAG);

        FrameDBConstant.TestSqlForEmergent = (String) ht
                .get(CommuXMLHandler.TestSqlForEmergent_TAG);

        FrameDBConstant.SqlMsgHandleSleepTime = Integer.parseInt((String) ht
                .get(CommuXMLHandler.SqlMsghandleSleepTime_TAG));

    }

    @SuppressWarnings("rawtypes")
    private void initFromStartOption(Hashtable commuConfig)
            throws ConfigFileException {
        // 获得控制启动组件配置属性
        String value = (String) commuConfig
                .get(CommuXMLHandler.StartParameterDistribute_TAG);
        FrameCodeConstant.startParameterDistribute = Boolean.valueOf(value)
                .booleanValue();
        value = (String) commuConfig
                .get(CommuXMLHandler.StartSocketListener_TAG);
        FrameCodeConstant.startSocketListener = Boolean.valueOf(value)
                .booleanValue();
        value = (String) commuConfig
                .get(CommuXMLHandler.StartConnectionPoolListener_TAG);
        FrameCodeConstant.startConnectionPoolListener = Boolean.valueOf(value)
                .booleanValue();

    }

    @SuppressWarnings("rawtypes")
    private void initFromConnectionPool(Hashtable commuConfig)
            throws ConfigFileException {

        try {
            Hashtable ht = (Hashtable) commuConfig
                    .get(CommuXMLHandler.OP_CONNECTION_POOL_TAG);
            FrameDBConstant.OP_DBCPHELPER = new DbcpHelper(
                    (String) ht.get("Driver"), (String) ht.get("URL"),
                    (String) ht.get("Database"), (String) ht.get("User"),
                    (String) ht.get("Password"), Integer.parseInt((String) ht
                    .get("MaxActive")), Integer.parseInt((String) ht
                    .get("MaxIdle")), Integer.parseInt((String) ht
                    .get("MaxWait")));

            logger.info("运营库连接池OpConnectionPool创建!");
            ht = (Hashtable) commuConfig
                    .get(CommuXMLHandler.CM_CONNECTION_POOL_TAG);

            FrameDBConstant.CM_DBCPHELPER = new DbcpHelper(
                    (String) ht.get("Driver"), (String) ht.get("URL"),
                    (String) ht.get("Database"), (String) ht.get("User"),
                    (String) ht.get("Password"), Integer.parseInt((String) ht
                    .get("MaxActive")), Integer.parseInt((String) ht
                    .get("MaxIdle")), Integer.parseInt((String) ht
                    .get("MaxWait")));

            logger.info("通讯库连接池CmConnectionPool创建!");
            ht = (Hashtable) commuConfig
                    .get(CommuXMLHandler.TK_CONNECTION_POOL_TAG);
            FrameDBConstant.TK_DBCPHELPER = new DbcpHelper(
                    (String) ht.get("Driver"), (String) ht.get("URL"),
                    (String) ht.get("Database"), (String) ht.get("User"),
                    (String) ht.get("Password"), Integer.parseInt((String) ht
                    .get("MaxActive")), Integer.parseInt((String) ht
                    .get("MaxIdle")), Integer.parseInt((String) ht
                    .get("MaxWait")));
            logger.info("票务库连接池TkConnectionPool创建!");
            //
            // if (MessageUtil.isNeeedEmergentTraffic()) {
            // ht = (Hashtable) commuConfig
            // .get(CommuXMLHandler.EmergentFlowConnectionPool_TAG);
            // FrameDBConstant.EMERGENT_DBCPHELPER = new DbcpHelper(
            // (String) ht.get("Driver"), (String) ht.get("URL"),
            // (String) ht.get("Database"), (String) ht.get("User"),
            // (String) ht.get("Password"),
            // Integer.parseInt((String) ht.get("MaxActive")),
            // Integer.parseInt((String) ht.get("MaxIdle")),
            // Integer.parseInt((String) ht.get("MaxWait")));
            // logger.info("应急指挥中心连接池创建!");
            //
            // }
        } catch (ClassNotFoundException ex) {
            throw new ConfigFileException(ex.getMessage());
        }

    }

    private void initFromDB() throws ConfigFileException {
        try {
            // 从数据库取得数据
            LccLineCodeDao lccLineCodeDAO = new LccLineCodeDao();
            lccLineCodeDAO.getAllLccDetail();
            FrameCodeConstant.ALL_LCC_IP = lccLineCodeDAO.getAllLccIpCod();//参数下发使用

            SquadTimeDao stDao = new SquadTimeDao();
            FrameCodeConstant.SQUAD_TIME = stDao.getSquadTime();
            logger.info("运营时间:" + FrameCodeConstant.SQUAD_TIME);

            DeviceDao deviceDAO = new DeviceDao();
            FrameCodeConstant.ALL_BOM_IP = deviceDAO.getAllBomIp();
            FrameCodeConstant.DEV_STATUS_MAPPING = deviceDAO.getStatusMapping();

            //手机支付业务 add by lindaquan in 20160118
            ConfigBaseDao cbDao = new ConfigBaseDao();
            FrameCodeConstant.MOBILE_CONTROL = cbDao.getConfigValue(FrameCodeConstant.MOBILE_CONTROL_KEY);
            FrameCodeConstant.MOBILE_INTERVAL = Integer.parseInt(cbDao.getConfigValue(FrameCodeConstant.MOBILE_INTERVAL_KEY));
//            if (FrameCodeConstant.MOBILE_CONTROL.equals("1")) {
//                MBCodeDao mBCodeDao = new MBCodeDao();
//                FrameCodeConstant.MOBILE_IP = mBCodeDao.getMobileIp();
//            }
            //tcc业务 zhongziqi 20190617
            FrameCodeConstant.TCC_INTERFACE_CONTROL = cbDao.getCmCfgSysValue(FrameCodeConstant.TCC_CONTROL_KEY);
//            System.out.println("FrameCodeConstant.TCC_INTERFACE_CONTROL="+FrameCodeConstant.TCC_INTERFACE_CONTROL);
            if (FrameCodeConstant.TCC_INTERFACE_USE_KEY.equals(FrameCodeConstant.TCC_INTERFACE_CONTROL)) {
                FrameCodeConstant.TCC_IP = new ExternalIpDao().getExternalIp(FrameCodeConstant.TCC_IP_TYPE);
                FrameCodeConstant.EXTERNAL_IP.put("TCC", FrameCodeConstant.TCC_IP);
            }
            //客流统计模式
            FrameCodeConstant.USER_MODE = cbDao.getCmCfgSysValue(FrameCodeConstant.MODE_CONTROL_KEY);

            //blacklist time control wuqq 20201009
            FrameCodeConstant.BLACKDISTRI_BEGIN=cbDao.getCmCfgSysValue(FrameCodeConstant.BLACK_DISTRIBUTE_BEGINKEY);
            FrameCodeConstant.BLACKDISTRI_END=cbDao.getCmCfgSysValue(FrameCodeConstant.BLACK_DISTRIBUTE_ENDKEY);

            logger.info("黑名单下发时间："+FrameCodeConstant.BLACKDISTRI_BEGIN+" - "+FrameCodeConstant.BLACKDISTRI_END);

            //一卡通下载延时间隔
            FrameCodeConstant.busFtpDelayInterval = Integer.parseInt(cbDao.getConfigValue(FrameCodeConstant.busFtpDelayInterval_key));
            //一卡通下载间隔
            FrameCodeConstant.busFtpInterval = Integer.parseInt(cbDao.getConfigValue(FrameCodeConstant.busFtpInterval_key));

            //缓存值刷新时间间隔
            FrameCodeConstant.CACHE_REFRESH_INTERVAL = Integer.parseInt(
                    cbDao.getCmCfgSysValue(FrameCodeConstant.CACHE_REFRESH_INTERVAL_KEY));

            CommuLogLevelThread th = new CommuLogLevelThread();
            th.setCurrentLogLevel();

            ComThreadPoolMonitorDao ctpmDao = new ComThreadPoolMonitorDao();
            ctpmDao.setMsgMaxHdlTime();

            // logger.info("获取消息最大处理时间"
            // + FrameThreadPoolConstant.ThreadMsgHandleMaxTime);
            logger.info("缺省消息最大处理时间"
                    + FrameThreadPoolConstant.ThreadMsgHandleMaxTimeDefault);

            int n = ctpmDao.dumpThreadStatus();
            logger.info("转存线程池状态表数据，共:" + n + "记录");

        } catch (Exception e) {
            logger.info("数据库数据初始化 " + CONFIGFILE + " 错误! ", e);
            throw new ConfigFileException("数据库数据初始化 " + CONFIGFILE + " 错误! "
                    + e);
        }

    }

    private void startLogLevelMonitor() {
        CommuLogLevelThread t = new CommuLogLevelThread();
        t.start();
    }

    private void startDevParaVerQuery() {
        DevParaVerThread t = new DevParaVerThread();
        t.start();
    }

    /**
     * socket监听开始
     *
     * @throws BindException
     * @throws IOException
     */
    public void startSocketListener() throws BindException, IOException {
        serverSocket = new ServerSocket(FrameCodeConstant.port);
        // FrameCodeConstant.serverSocket = serverSocket;
        CommuServer.connectID = 0;

        logger.info("服务器正常启动！正在监听端口 : " + FrameCodeConstant.port);
        while (true) {
            hdlStartTime = System.currentTimeMillis();

            CommuServer.connectID++;

            Socket socket = serverSocket.accept();

            String ip = socket.getInetAddress().getHostAddress();
            int port = socket.getPort();
            logger.info("接受新的连接请求，来自 " + ip + ":" + port);
            try {
                int checkResult = checkConnection(ip);
                if (checkResult == 0) {
                    // 从消息队列及缓存删除未处理非及时退款消息

                    // removeNonInstantMsgFromQueue(ip);
                    FrameCodeConstant.all_connecting_ip.put(ip, "1");
                    String id = ip;
                    logger.info("开始处理新连接!");
                    CommuConnection commuConnection = new CommuConnection(
                            socket, id, CommuServer.connectID);

                    CommuServer.addConnection(CommuServer.connectID,
                            commuConnection);
                    // addConnection(ip, commuConnection);

                    Thread t = new Thread(commuConnection, id);
                    t.setPriority(Thread.NORM_PRIORITY
                            + FrameCodeConstant.readThreadPriorityAdd);
                    t.start();
                    LogUtil.writeConnectLog(
                            new Date(System.currentTimeMillis()), ip, "0", "");
                } else {
                    if (checkResult == 1) {
                        logger.info("连接不合法！地址：" + ip);
                        //add by zhongzq 20181219 非法地址需要主动关闭
                        try {
                            socket.close();
                            logger.warn(ip + "连接已关闭");
                        } catch (Exception e) {
                            logger.error(ip + " - close rror! - ", e);
                        }

                        throw new CommuException("连接不合法！地址：" + ip);
                    }
                    if (checkResult == 2) {
                        logger.info("连接已经存在！地址：" + ip);
                        throw new CommuException("连接已经存在！地址：" + ip);
                    }
                }
            } catch (CommuException e) {
                LogUtil.writeConnectLog(new Date(System.currentTimeMillis()),
                        ip, "1", "");
                logger.info("创建连接失败 - ", e);

                hdlEndTime = System.currentTimeMillis();
                // 记录日志
                LogDbUtil.logForDbDetail(
                        FrameLogConstant.MESSAGE_ID_CONNECTION, "",
                        hdlStartTime, hdlEndTime,
                        FrameLogConstant.RESULT_HDL_FAIL, Thread
                                .currentThread().getName(),
                        FrameLogConstant.LOG_LEVEL_ERROR, e.getMessage());
            }
            /*
             * catch (Exception e) { logger.error("catch one exception",e)();
             *
             * }
             */
        }
    }

    // private void removeNonInstantMsgFromQueue(String ip) {
    // ComMessageQueueDao dao = new ComMessageQueueDao(false);
    // String messageType = "17";
    // //从数据库消息队列中删除
    //
    // dao.removeMsgFromQueue(ip, messageType);
    // //从缓存中删除
    // MessageQueueManager.removeMessageQueueFromBuffer(ip, messageType);
    // }
    public static void removeConnection(int connID) {
        Integer iConnID = new Integer(connID);
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CONNECTION) {
            if (connectedSockets.containsKey(iConnID)) {
                connectedSockets.remove(iConnID);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void addConnection(int connID, CommuConnection con) {
        Integer iConnID = new Integer(connID);
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CONNECTION) {
            if (connectedSockets.containsKey(iConnID)) {
                connectedSockets.remove(iConnID);
            }
            connectedSockets.put(iConnID, con);
        }
    }

    @SuppressWarnings("unchecked")
    public static void addConnection(String ip, CommuConnection con) {

        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CONNECTION) {
            if (connectedSockets.containsKey(ip)) {
                connectedSockets.remove(ip);
            }
            connectedSockets.put(ip, con);
        }
    }

    private int checkConnection(String ip) {
        if (!(FrameCodeConstant.ALL_LCC_IP.containsValue(ip))
                && !(FrameCodeConstant.ALL_BOM_IP.containsValue(ip))
                && !(FrameCodeConstant.EXTERNAL_IP.containsValue(ip))
        ) {
            return 1;
        }
        return 0;
    }

}
