package com.goldsign.commu.frame.thread;

import com.goldsign.commu.app.dao.BusDataDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.goldsign.commu.app.vo.BusDataUpLoadVo;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameCodeConstant;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取一卡通中心的结算结果文件 上传一卡通地铁交易文件到一卡通中心
 *
 * @author lindq
 */
public class BusDataImportThread implements Runnable {

    private static String FILE_TRX = "trx";// 交易文件标识
    private static String FILE_SETTLE = "settle";// 结算结果文件标识
    private static String FILE_OCT = "XF";// 一卡通通讯包标识
    //    private static String DIRECTORY_TRX = "";
//    private static String DIRECTORY_SETTLE = "";
    private static Logger logger = Logger.getLogger(BusDataImportThread.class);
    private static String CONTROL_FILE_NAME = "control.txt";

    @Override
    public void run() {
        while (true) {
            logger.info("------------------------与一卡通中心文件交互执行begin-------------------------");

            try {
                BusDataDao bdd = new BusDataDao();

                //查询非取清算文件
                Map<String, String> map = bdd.busDataMap();
                logger.debug("============="+map.toString());
                // 取所有一卡通结算结果文件(包含滞留的)
                handle(map, FILE_SETTLE, FrameCodeConstant.BusFtpDownloadPath);
                //上传操作
                uploadHandle(bdd);


            } catch (Exception e1) {
                logger.error("出错", e1);
            }
            logger.info("------------------------与一卡通中心文件交互执行end-------------------------");
            try {
                Thread.sleep(FrameCodeConstant.busFtpInterval);// 休眠5分钟
            } catch (InterruptedException e) {
                logger.error("睡眠被打断", e);
            }
        }

    }

    private void uploadHandle(BusDataDao bdd) throws SQLException {
        // 获取清算流水号
        List uploadListNo = bdd.getUploadList();
        if (uploadListNo.size() > 0) {
            for (int i = 0; i < uploadListNo.size(); i++) {
                String banlanceWaterNo = (String) uploadListNo.get(i);
                // 没有获取到清算流水号
                if ("".equals(banlanceWaterNo.trim())) {
                    logger.warn("--数据异常，无清算流水号.");
                    continue;
                }
                logger.debug("--查询到的清算流水号:" + banlanceWaterNo);
                // 上传交易文件 按目录
                upload(banlanceWaterNo, FILE_TRX, FrameCodeConstant.BusFtpUploadPath);
            }
        }
    }


    /**
     * 取一卡通结算结果文件
     *
     * @param fileType         文件类型
     * @param fileSubDirectory 文件存放的子目录
     */
    private void handle(Map<String, String> map, String fileType, String fileSubDirectory) {

        if (null == map || map.size() == 0) {
            return;
        }
        try {
            // 根据清算流水号获取文件
            Map<String, String> retMap = new HashMap<String, String>();
            int n = 0;
            boolean result = getFile(map, retMap, fileType, fileSubDirectory);
            logger.info("下载成功数量:" + retMap.size());
            // 如果获取文件成功，记录结果到表中
            if (result && retMap.size() > 0) {
                BusDataDao bdd = new BusDataDao();
                //更新获取一卡通清算文件的记录为1"正在进行"
                n = bdd.updateBusDataLog(retMap);
                logger.warn("下载数量:" + retMap.size() + ",st_log_oct_export_import更新记录数量:" + n);

                n = bdd.writeDB(retMap, fileType);
                logger.warn("下载数量:" + retMap.size() + ",cm_log_busdata_file更新记录数量:" + n);
            }
        } catch (Exception e) {
            logger.error("--处理" + fileType + "类型目录一卡通结算文件过程中出错：", e);
        }
    }

    private boolean getFile(Map<String, String> map, Map<String, String> retMap,
                            String fileType, String fileSubDirectory) {
        String username = FrameCodeConstant.busFtpUserName;
        String password = FrameCodeConstant.busFtpPassWord;
        String localPath = FrameCodeConstant.busFtpLocalDir + "/import/" + fileType
                + "/";

        // 初始表示下载失败
        boolean result = false;
        // 创建FTPClient对象
        FTPClient ftp = new FTPClient();
        try {
            ftpLogin(ftp, fileSubDirectory, username, password);

            /*
            间隔时间内遍历两次文件，对比文件数量、大小，相同时判断为文件写完成状态，可以下载
             */
            Map<String, FTPFile> fileMap1 = new HashMap<String, FTPFile>();
            Map<String, FTPFile> fileMap2 = new HashMap<String, FTPFile>();
            // 列出该目录下所有文件1,第一次查询文件大小
            FTPFile[] fs1 = ftp.listFiles();
//            logger.info("--列出该目录下所有文件1:" + fs1.length);
            if (null == fs1 || fs1.length == 0) {
                return result;
            }
            // 遍历文件夹存在当前流水号文件名
            for (FTPFile ff : fs1) {
                if (map.containsKey(ff.getName())) {
                    logger.debug("--枚举一卡通结算文件1:" + ff.getName());
                    fileMap1.put(ff.getName(), ff);
                }
            }

            //并对比第一次遍历结果
            if (0 < fileMap1.size()) {
                try {
                    Thread.sleep(FrameCodeConstant.busFtpDelayInterval);
                } catch (InterruptedException ex) {
                    logger.error(ex.getMessage());
                }

                // 列出该目录下所有文件2,第二次查询文件大小
                FTPFile[] fs2 = ftp.listFiles();
                if (null == fs2 || fs2.length == 0) {
                    return result;
                }
                // 遍历文件夹存在当前流水号文件名,并对比第一次遍历结果
                for (FTPFile ff : fs2) {
                    if (fileMap1.containsKey(ff.getName())) {
                        if (fileMap1.get(ff.getName()).getSize() == ff.getSize()) {
                            logger.debug("--枚举一卡通结算文件2:" + ff.getName());
                            fileMap2.put(ff.getName(), ff);
                        }
                    }
                }
            }

            //最后文件大小一样,未下载的,统一下载
            if (fileMap2.size() > 0) {
                File f = new File(localPath + CONTROL_FILE_NAME);
                if (f.exists()) {
                    f.delete();
                }
                //遍历未下载文件
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (fileMap1.containsKey(entry.getKey())) {
//                        String tempPath = localPath + entry.getValue();
                        logger.info("--下载文件到本地目录：" + localPath);
                        File localDir = new File(localPath);
                        if (!localDir.exists()) {
                            localDir.mkdirs();
                        }
                        FTPFile value = fileMap2.get(entry.getKey());
                        //下载成功
                        if (downFileUnit(localPath, ftp, value)) {
                            logger.info("--下载成功文件：" + entry.getKey());
                            retMap.put(entry.getKey(), entry.getValue());
                            result = true;
                        }
                    }
                }
                //上传成功时创建control文件
                if (result) {
                    if (f.createNewFile()) {
                        logger.info("--成功创建文件：" + f.getName());
                    }
                }
            }

        } catch (IOException e) {
            logger.error("--FTP取一卡通结算文件出错:", e);
        } finally {
            if (ftp.isConnected()) {
                try {
                    // 退出ftp
                    ftp.logout();
                    ftp.disconnect();
                } catch (IOException ioe) {
                    logger.error("--FTP取一卡通结算文件出错:", ioe);
                }
            }
        }
        return result;

    }

    private boolean downFileUnit(String localPath, FTPClient ftp, FTPFile ff)
            throws FileNotFoundException, IOException {
        OutputStream out = null;
        boolean result = false;
        // 根据绝对路径初始化文件
        File localFile = new File(localPath + "/" + ff.getName());
        // 输出流
        try {
            out = new FileOutputStream(localFile);
            // 下载文件
            result = ftp.retrieveFile(ff.getName(), out);
            logger.info("下载：" + ff.getName() + "," + result);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            this.closeOut(out);
            return result;
        }
    }

    private void closeOut(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    上传地铁交易、对账结果文件到一卡通ftp服务器
     */
    private void upload(String banlanceWaterNo, String fileType, String fileSubDirectory) {
        try {

            // 判断清算流水号对应的文件是否上传
            BusDataDao bdd = new BusDataDao();
            Map uploadFileList = bdd.getUploadFiles(banlanceWaterNo);
            if(uploadFileList.size()==0){
                logger.info("没有需要上传的文件，流水目录："+banlanceWaterNo);
                return;
            }
            boolean isExited = bdd.judgeWaterNoExited(banlanceWaterNo, fileType);
            // 流水号对应的文件已经上传过，清算重新生成重新上传 modify byzhongziqi 20180929
            if (isExited) {
                logger.info("--清算流水号对应的一卡通地铁消费文件已经上传过,此次为重新上传重传流水号：" + banlanceWaterNo);
//                return;
            }
            logger.warn("--准备FTP登录一卡通中心上传文件.");

            // 根据清算流水号上传文件
//            boolean result = uploadFile(banlanceWaterNo, fileType, fileSubDirectory);
            boolean result = uploadFile(banlanceWaterNo, uploadFileList, fileType, fileSubDirectory);

            // 如果获取文件成功，记录结果到表中
            if (result) {
                bdd.updateBusDataLogForFinished(uploadFileList);
                Map<String, String> map = new HashMap<String, String>() {{
                    put(fileType, banlanceWaterNo);
                }};
                bdd.writeDB(map, fileType);
            }
        } catch (Exception e) {
            logger.error("--处理" + fileType + "类型目录文件过程中出错：", e);
        }
    }

    //add by zhongziqi 指定目录 指定文件上传
    private boolean uploadFile(String balanceWaterNo, Map uploadFileList, String fileType, String fileSubDirectory) {
        String username = FrameCodeConstant.busFtpUploadUser;
        String password = FrameCodeConstant.busFtpUploadPW;
        String localPath = FrameCodeConstant.busFtpLocalDir + "/export/" + fileType
                + "/" + balanceWaterNo;

        // 初始表示上传失败
        boolean result = false;
        // 判断清算文件
        File csDir = new File(localPath);
        if (!csDir.exists()) {
            logger.info("--------地铁公交交易、清算文件目录未生成------------");
            return result;
        }
        File[] fs = csDir.listFiles();
        if (null == fs || fs.length == 0) {
            logger.info("--------地铁公交交易、清算文件未生成------------");
            return result;
        }
        //判断control文件，存在时可取文件
        for (File f : fs) {
            if (CONTROL_FILE_NAME.equals(f.getName())) {
                result = true;
                break;
            }
        }

        //上传文件
        if (result) {
            // 创建FTPClient对象
            FTPClient ftp = new FTPClient();
            try {
                ftpLogin(ftp, fileSubDirectory, username, password);

                for (File f : fs) {
                    if (FILE_OCT.equals(f.getName().substring(0, 2))) {
                        for (int i = 0; i < uploadFileList.size(); i++) {
                            BusDataUpLoadVo vo = (BusDataUpLoadVo) uploadFileList.get(i);
                            if(f.getName().equals(vo.getFileName())) {
                                uploadFileUnit(ftp, f);

                            }
                        }

                    }
                }

            } catch (IOException e) {
                logger.error("--FTP上传地铁公交交易、清算文件出错:", e);
            } finally {
                if (ftp.isConnected()) {
                    try {
                        // 退出ftp
                        ftp.logout();
                        ftp.disconnect();
                    } catch (IOException ioe) {
                        logger.error("--FTP上传地铁公交交易、清算文件出错:", ioe);
                    }
                }
            }
        }

        return result;
    }

//    private boolean uploadFile(String banlanceWaterNo, String fileType,
//                               String fileSubDirectory) {
//        String username = FrameCodeConstant.busFtpUploadUser;
//        String password = FrameCodeConstant.busFtpUploadPW;
//        String localPath = FrameCodeConstant.busFtpLocalDir + "/export/" + fileType
//                + "/" + banlanceWaterNo;
//
//        // 初始表示上传失败
//        boolean result = false;
//        // 判断清算文件
//        File csDir = new File(localPath);
//        if (!csDir.exists()) {
//            logger.info("--------地铁公交交易、清算文件目录未生成------------");
//            return result;
//        }
//        File[] fs = csDir.listFiles();
//        if (null == fs || fs.length == 0) {
//            logger.info("--------地铁公交交易、清算文件未生成------------");
//            return result;
//        }
//        //判断control文件，存在时可取文件
//        for (File f : fs) {
//            if (CONTROL_FILE_NAME.equals(f.getName())) {
//                result = true;
//                break;
//            }
//        }
//
//        //上传文件
//        if (result) {
//            // 创建FTPClient对象
//            FTPClient ftp = new FTPClient();
//            try {
//                ftpLogin(ftp, fileSubDirectory, username, password);
//
//                for (File f : fs) {
//                    if (FILE_OCT.equals(f.getName().substring(0, 2))) {
//                        uploadFileUnit(ftp, f);
//                    }
//                }
//
//            } catch (IOException e) {
//                logger.error("--FTP上传地铁公交交易、清算文件出错:", e);
//            } finally {
//                if (ftp.isConnected()) {
//                    try {
//                        // 退出ftp
//                        ftp.logout();
//                        ftp.disconnect();
//                    } catch (IOException ioe) {
//                        logger.error("--FTP上传地铁公交交易、清算文件出错:", ioe);
//                    }
//                }
//            }
//        }
//
//        return result;
//    }

    private void uploadFileUnit(FTPClient ftp, File f)
            throws FileNotFoundException, IOException {
        // 输入流
        InputStream in = null;

        try {
            in = new FileInputStream(f);
            boolean result = true;
            // 上传文件
            result = ftp.storeFile(f.getName(), in);
            logger.info("--上传：" + f.getName() + "," + result);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            this.closeIn(in);
        }
    }

    private void closeIn(InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean ftpLogin(FTPClient ftp, String fileSubDirectory,
                             String username, String password) throws IOException {
        boolean result = true;
        String url = FrameCodeConstant.busFtpURL;
        int port = FrameCodeConstant.busFtpPort;
        String remotePath = fileSubDirectory;

        int reply;
        // 连接FTP服务器
        // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
        ftp.connect(url, port);
        // 登录ftp
        ftp.login(username, password);
        logger.info("--下载FTP成功登录一卡通中心." + url + ":" + port);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }
        // 转到指定下载目录
        if (!remotePath.isEmpty() && remotePath.length() > 0) {
            logger.info("--切换到一卡通中心目录：" + remotePath);
            boolean isRemotePathExited = ftp.changeWorkingDirectory(remotePath);
            if (!isRemotePathExited) {
                logger.info("--远程目录:" + remotePath + "不存在");
                ftp.disconnect();
                return false;
            }
        }
        // 设置文件类型（二进制）
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        // 设置传文件过程超时
        ftp.setDataTimeout(FrameCodeConstant.ftpTimeout);
        ftp.enterLocalPassiveMode();
//            ftp.setBufferSize(1024);
//            ftp.setControlEncoding("GBK");
        return result;
    }
}
