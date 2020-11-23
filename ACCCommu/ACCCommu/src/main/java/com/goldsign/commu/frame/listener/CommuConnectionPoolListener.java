package com.goldsign.commu.frame.listener;

import com.goldsign.commu.frame.buffer.CommuExceptionBuffer;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameSynchronizeConstant;
import com.goldsign.commu.frame.manager.CommuThreadManager;
import com.goldsign.commu.frame.thread.CommuMessageHandleThread;
import com.goldsign.commu.frame.thread.CommuServerThread;
import com.goldsign.commu.frame.util.SocketUtil;
import com.goldsign.commu.frame.vo.CommuHandledMessage;

import java.sql.SQLException;
import java.util.Hashtable;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Vector;

import com.goldsign.lib.db.util.DbHelper;
import com.goldsign.lib.db.util.DbcpHelper;
import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class CommuConnectionPoolListener extends Thread {

    private static Logger logger = Logger
            .getLogger(CommuConnectionPoolListener.class.getName());
    private int count = 0;
    // private static DbcpHelper test_cp = null;
    private Hashtable test_config = null;
    private String msgFlag = "";
    private int flag = -1;
    private String testSql = "select 1 from dual";
    public static Boolean isConnectForOPConnection = false;
    public static Boolean isConnectForCMConnection = false;
    public static Boolean isConnectForTKConnection = false;
    public static Boolean isConnectForEmergentConnection = false;
    public static Boolean isSqlConExceptionClose = false;

    public CommuConnectionPoolListener() {
    }

    public CommuConnectionPoolListener(Hashtable config, String msgFlag,
            int flag) {
        // this.test_cp = cp;
        this.test_config = config;
        this.msgFlag = msgFlag;
        this.flag = flag;
        switch (this.flag) {
            case 0: {
                this.testSql = FrameDBConstant.TestSqlForOP;
                break;
            }
            case 1: {
                this.testSql = FrameDBConstant.TestSqlForCM;
                break;
            }
            case 2: {
                this.testSql = FrameDBConstant.TestSqlForTK;
                break;
            }
            case 3: {
                this.testSql = FrameDBConstant.TestSqlForEmergent;
                break;
            }

        }
    }

    //
    @Override
    public void run() {
        logger.info(msgFlag + "连接池监听线程已启动...");
        while (true) {
            try {

                this.testConnectionPools();
                /*
                 * if (this.isConnectForOPConnection.booleanValue() &&
                 * this.isConnectForCMConnection.booleanValue() &&
                 * this.isConnectForTKConnection.booleanValue()) {
                 */
                // 如是应急中心数据源，不作处理，其他数据源才作处理

                if (this.isOkForAllConnect()) {
                    // if (this.isRestartedServerSocket.booleanValue()) {
                    if (SocketUtil.isNeedRestartServerSocket()) {
                        // 准备重新启动服务器端口，禁止多次重新启动服务器端口

                        SocketUtil.setRestartedStatus(false);
                        /*
                         * synchronized (this.isRestartedServerSocket) {
                         * this.isRestartedServerSocket = new Boolean(true);
                         * 
                         * }
                         */

                        this.restartServerSocket();

                    }
                }

                CommuConnectionPoolListener
                        .sleep(FrameDBConstant.CPListenerThreadSleepTime);
            } catch (InterruptedException e) {
                logger.warn("连接池监听线程将终止...");
            }

        }
    }

    private boolean isOkForAllConnect() {
        synchronized (FrameSynchronizeConstant.CONTROL_CP_OP) {
            synchronized (FrameSynchronizeConstant.CONTROL_CP_CM) {
                synchronized (FrameSynchronizeConstant.CONTROL_CP_TK) {
                    if (CommuConnectionPoolListener.isConnectForOPConnection
                            .booleanValue()
                            && CommuConnectionPoolListener.isConnectForCMConnection
                            .booleanValue()
                            && CommuConnectionPoolListener.isConnectForTKConnection
                            .booleanValue()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

    }

    private boolean isEmergent() {
        if (this.flag == FrameDBConstant.FLAG_EMERGENT) {
            return true;
        }
        return false;
    }

    private void disableEmergent() {
        if (FrameDBConstant.isWriteEmergentTrafficForDb) {
            // logger.info("应急指挥中心数据库异常,应急指挥中心数据库标志由true改为false");
            logger.info("应急指挥中心数据库异常,应急指挥中心数据库标志由true改为false");
            FrameDBConstant.isWriteEmergentTrafficForDb = false;
        }
    }

    private void enableEmergent() {
        if (!FrameDBConstant.isWriteEmergentTrafficForDb) {
            // logger.info("应急指挥中心数据库正常,应急指挥中心客流数据库标志由false改为true");
            logger.info("应急指挥中心数据库正常,应急指挥中心客流数据库标志由false改为true");
            FrameDBConstant.isWriteEmergentTrafficForDb = true;
        }
    }

    private void testConnectionPools() {
        // 测试连接
        if (!this.testConnection()) {
            if (this.isEmergent()) {// 应急指挥中心数据库
                this.disableEmergent();// 数据库状态标志改为false
            }
            // 设置连接不可用

            this.setConnectStatus(flag, false);
            // 关闭监听服务器,除应急指挥中心数据库连接异常外

            if (!this.isEmergent()) {

                synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CLOSE) {
                    if (!SocketUtil.isClosedServerSocket.booleanValue()) {
                        SocketUtil.closeServerSocketForCPListener(this.msgFlag);
                        SocketUtil.isClosedServerSocket = true;
                    }
                }
            }
            logger.info("尝试重建" + msgFlag + "连接池" + (++count) + "次.....");
            return;
        }
        // 连接可用,正常运行测试连接无问题

        if (count == 0) {
            this.setConnectStatus(flag, true);

            return;
        }
        if (this.isEmergent())// 应急指挥中心数据库
        {
            this.enableEmergent();// 数据库状态标志改为true
        }

        logger.info(msgFlag + "连接中断后已可用.....");
        // 重建连接池

        if (this.resetConnectonPool()) {
            // 设置连接池可用标志

            this.setConnectStatus(this.flag, true);
            // 设置可以重新启动服务器SOCK端口
            if (!this.isEmergent()) {
                SocketUtil.setRestartedStatus(true);
            }
            /*
             * switch (this.flag) { case 0: { this.isConnectForOPConnection =
             * new Boolean(true); break; } case 1: {
             * this.isConnectForCMConnection = new Boolean(true); break; } case
             * 2: { this.isConnectForTKConnection = new Boolean(true); break;
             * }
             * 
             * }
             */
        }

    }

    private void setConnectStatus(int flag, boolean status) {
        switch (this.flag) {
            case 0: {
                synchronized (FrameSynchronizeConstant.CONTROL_CP_OP) {
                    CommuConnectionPoolListener.isConnectForOPConnection = status;
                }
                break;
            }
            case 1: {
                synchronized (FrameSynchronizeConstant.CONTROL_CP_CM) {
                    CommuConnectionPoolListener.isConnectForCMConnection = status;
                }
                break;
            }
            case 2: {
                synchronized (FrameSynchronizeConstant.CONTROL_CP_TK) {
                    CommuConnectionPoolListener.isConnectForTKConnection = status;
                }

                break;
            }
            case 3: {
                synchronized (FrameSynchronizeConstant.CONTROL_CP_EMERGENT) {
                    CommuConnectionPoolListener.isConnectForEmergentConnection = status;
                }

                break;
            }
        }
    }

    private boolean testConnectionPool(DbcpHelper dbcp) {
        boolean result;
        DbHelper dbHelper = null;
        try {

            dbHelper = new DbHelper("data", dbcp.getConnection());

            result = dbHelper.getFirstDocument(testSql);

            return result;

        } catch (SQLException e) {
            logger.error(msgFlag + "数据库异常,错误代码:" + e.getErrorCode() + " 消息:", e);
            return false;
        } finally {
            try {
                if (dbHelper != null) {
                    dbHelper.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    private boolean testConnection() {
        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            DriverManager.setLoginTimeout(10);
            // logger.info("url:"+ (String) test_config.get("URL") + "/" +
            // (String) test_config.get("Database")+"user:"+(String)
            // test_config.get("User")+"pasword:"+(String)
            // test_config.get("Password"));
            String url = (String) test_config.get("URL");
            String user = (String) test_config.get("User");
            String password = (String) test_config.get("Password");
//            logger.info("url:" + url + ",user:" + user + ",password:" + password);
            con = DriverManager.getConnection(
                    url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(this.testSql);
            if (rs.next()) {
                return true;
            }

        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException e) {
            logger.info(msgFlag + "数据库异常,错误代码:" + e.getErrorCode() + " 消息:", e);
            return false;

        } finally {
            try {
                if (con != null) {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    con.close();
                }

            } catch (SQLException e) {
                return false;

            }
        }
        return false;
    }

    private boolean resetConnectonPool() {
        DbcpHelper cp;

        try {
            cp = new DbcpHelper((String) test_config.get("Driver"),
                    (String) test_config.get("URL"),
                    (String) test_config.get("Database"),
                    (String) test_config.get("User"),
                    (String) test_config.get("Password"),
                    Integer.parseInt((String) test_config.get("MaxActive")),
                    Integer.parseInt((String) test_config.get("MaxIdle")),
                    Integer.parseInt((String) test_config.get("MaxWait")));

            if (!this.testConnectionPool(cp)) {
                logger.info("重建的" + msgFlag + "连接池不可用.....");
                return false;
            }

            switch (this.flag) {
                case 0: {
                    synchronized (FrameSynchronizeConstant.CONTROL_CP_OP) {
                        if (FrameDBConstant.OP_DBCPHELPER != null) {
                            FrameDBConstant.OP_DBCPHELPER.close();
                        }
                        // ApplicationConstant.OP_DBCPHELPER = null;
                        FrameDBConstant.OP_DBCPHELPER = cp;
                    }
                    break;
                }
                case 1: {
                    synchronized (FrameSynchronizeConstant.CONTROL_CP_CM) {
                        if (FrameDBConstant.CM_DBCPHELPER != null) {
                            FrameDBConstant.CM_DBCPHELPER.close();
                        }
                        // ApplicationConstant.CM_DBCPHELPER = null;
                        FrameDBConstant.CM_DBCPHELPER = cp;
                    }
                    break;

                }
                case 2: {
                    synchronized (FrameSynchronizeConstant.CONTROL_CP_TK) {
                        if (FrameDBConstant.TK_DBCPHELPER != null) {
                            FrameDBConstant.TK_DBCPHELPER.close();
                        }
                        // ApplicationConstant.TK_DBCPHELPER = null;
                        FrameDBConstant.TK_DBCPHELPER = cp;
                    }
                    break;

                }
//                case 3: {
//                    synchronized (FrameSynchronizeConstant.CONTROL_CP_EMERGENT) {
//                        if (FrameDBConstant.EMERGENT_DBCPHELPER != null) {
//                            FrameDBConstant.EMERGENT_DBCPHELPER.close();
//                        }
//                        // ApplicationConstant.FLOWSTATUS_DBCPHELPER = null;
//                        FrameDBConstant.EMERGENT_DBCPHELPER = cp;
//                    }
//                    break;
//
//                }
            }

            logger.info("重设" + msgFlag + "连接池.....");
            this.count = 0;
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException e) {
            return false;
        }

    }

    private boolean restartServerSocket() {
        CommuServerThread server = new CommuServerThread();
        logger.info("数据库连接恢复,重新启动监听服务器");
        try {
            this.handleSqlBufferMsgs();
            server.start();
            // 恢复监听器监听功能

            this.restoreControl();
            return true;
        } catch (InterruptedException e) {
            return false;
        }

    }

    private void restoreControl() {
        logger.info("恢复控制参数");
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_START) {
            SocketUtil.isClosedServerSocket = false;
        }
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_RESTART) {
            SocketUtil.isRestartedServerSocket = false;
        }

        synchronized (FrameSynchronizeConstant.CONTROL_CP_OP) {
            isConnectForOPConnection = false;
        }
        synchronized (FrameSynchronizeConstant.CONTROL_CP_CM) {
            isConnectForCMConnection = false;
        }

        synchronized (FrameSynchronizeConstant.CONTROL_CP_TK) {
            isConnectForTKConnection = false;
        }
        /*
         * synchronized (isSqlConExceptionClose) { isSqlConExceptionClose = new
         * Boolean(false); }
         */

    }

    private void handleSqlBufferMsgs() throws InterruptedException {
        Vector unHandleSqlMsgs = CommuExceptionBuffer.getSqlUnHandleMsgs();
        if (unHandleSqlMsgs == null || unHandleSqlMsgs.isEmpty()) {
            return;
        }
        CommuHandledMessage chm;
        CommuConnection con = new CommuConnection();
        logger.info("由于Sql连接异常需重新处理的消息记录为" + unHandleSqlMsgs.size() + "条");
        for (int i = 0; i < unHandleSqlMsgs.size(); i++) {
            // logger.info("处理Sql连接异常消息缓存记录第" + i + "条");
            chm = (CommuHandledMessage) unHandleSqlMsgs.get(i);
            con.processReaderResult(chm.getIp(), chm.getReadResult());
            CommuConnectionPoolListener
                    .sleep(FrameDBConstant.SqlMsgHandleSleepTime);

        }
        logger.info("成功重新处理由于Sql连接异常的消息记录为" + unHandleSqlMsgs.size() + "条");
    }

    public static void dumpThreadUnHandledMsgs() {
        CommuMessageHandleThread t;
        Vector msgs;
        CommuHandledMessage msg;
        for (int i = 0; i < CommuThreadManager.handleThreads.size(); i++) {
            t = (CommuMessageHandleThread) CommuThreadManager.handleThreads
                    .get(i);
            if (t == null) {
                logger.info("第" + i + "个线程已是NULL值");
                continue;
            }
            // 由于数据库连接异常关闭时,将线程未处理的消息放入待处理缓存

            msgs = t.getAllMsgs();

            logger.info("线程" + t.getName() + "未处理消息" + msgs.size());
            for (int j = 0; j < msgs.size(); j++) {
                // logger.info("class name:"+msgs.get(j).getClass().getName());

                msg = (CommuHandledMessage) msgs.get(j);
                CommuExceptionBuffer.setSqlUnHandleMsgs(msg.getIp(),
                        (byte[]) (msg.getReadResult().get(1)));
            }

        }

    }
}
