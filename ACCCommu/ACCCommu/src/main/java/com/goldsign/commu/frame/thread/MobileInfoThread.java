package com.goldsign.commu.frame.thread;

import com.goldsign.commu.app.message.ConstructMessage12;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.MBInfoDao;
import com.goldsign.commu.frame.exception.ParameterException;
import com.goldsign.commu.frame.mobile.MobileInfo;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.vo.MBInfoVo;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;


/**
 * 手机支付信息下发线程 1,读取信息 2,生成信息文件 2,以02消息下发到手机支付平台
 *
 * @author lindaquan
 */
public class MobileInfoThread implements Runnable {

    private final static String INF = "INF";
    private final static String INFO_CODE = "9900";
    private final static String SUCCESS = "2";
    private final static String ERROR = "1";
    private final static String LINE = "80";
    private String fileName = "";
    private static Logger logger = Logger.getLogger(MobileInfoThread.class.getName());
    /**
     * 日志记录使用
     */
    private long hdlStartTime; // 处理的起始时间
    private long hdlEndTime;// 处理的结束时间

    @Override
    public void run() {
        while (true) {
            try {
                this.hdlEndTime = System.currentTimeMillis();

                MBInfoDao dao = new MBInfoDao();
                //查询已经审核未下发的发布信息内容
                Vector<MBInfoVo> mBInfos = dao.getMBInfo();
                if (mBInfos != null && mBInfos.size()>0) {
                    // 生成下发消息文件
                    boolean result = genMBInfoFiles(mBInfos, dao);
                    if (result) {
                        result = sendMsg();// 通知手机支付平台有信息文件需下载
                    }
                    if(result){
                        //更新下发消息状态情况
                        updateInfoFlag(mBInfos, dao, SUCCESS);
                        this.hdlEndTime = System.currentTimeMillis();
                        // 记录日志
                        LogDbUtil.logForDbDetail(
                                FrameLogConstant.MESSAGE_ID_FILE_NOTICE, "",
                                this.hdlStartTime, this.hdlEndTime,
                                FrameLogConstant.RESULT_HDL_SUCESS, Thread.currentThread().getName(),
                                FrameLogConstant.LOG_LEVEL_INFO, "文件通知");
                    }else{
                        updateInfoFlag(mBInfos, dao, ERROR);
                        // 记录日志
                        LogDbUtil.logForDbDetail(
                                FrameLogConstant.MESSAGE_ID_FILE_NOTICE, "",
                                this.hdlStartTime, this.hdlEndTime,
                                FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread().getName(),
                                FrameLogConstant.LOG_LEVEL_ERROR, "文件通知");
                    }
                }
            } catch (Exception e) {
                logger.error("MobileInfoThread.run error - " + e);
                this.hdlEndTime = System.currentTimeMillis();
                // 记录日志
                LogDbUtil.logForDbDetail(
                        FrameLogConstant.MESSAGE_ID_FILE_NOTICE, "",
                        this.hdlStartTime, this.hdlEndTime,
                        FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread().getName(),
                        FrameLogConstant.LOG_LEVEL_ERROR, e.getMessage());
            } finally {
                try {
                    Thread.sleep(FrameCodeConstant.MOBILE_INTERVAL);
                } catch (Exception e) {
                    logger.error("MobileInfoThread.run sleep error - " + e);
                }
            }
        }
    }

    /*
     * 生成下发消息文件
     */
    private boolean genMBInfoFiles(Vector<MBInfoVo> mBInfos, MBInfoDao dao)
            throws ParameterException {
        boolean result = true;
        String curDay = DateHelper.dateOnlyToString(new Date());
        this.fileName = INF+INFO_CODE + "." + curDay;
        int seqNo = dao.getMaxSeq(curDay);

        try {
            //生成所有下发消息文件
            seqNo++;
            String str = String.format("%3d", seqNo).replace(" ", "0");
            fileName = fileName + "." + str;
            MobileInfo info = new MobileInfo();
            info.init(mBInfos, fileName);
            result = info.formInfoFile();
        } catch (ParameterException e) {
            logger.error("生成下发消息文件失败!", e);
            return false;
        } catch (Exception e) {
            logger.error("生成下发消息文件失败!" + e);
            return false;
        }
        return result;
    }

    /*
     * 发送文件通知消息12
    */
    protected boolean sendMsg() {
        boolean result = true;
        logger.info("获取线路对应的ip,线路[" + LINE + "]");
        if(!FrameCodeConstant.MOBILE_IP.containsKey(LINE)){
            logger.error("没有找到线路[" + LINE + "]的ip");
            return false;
        }
        String ip = (String) ((FrameCodeConstant.MOBILE_IP).get(LINE));
        logger.info("查找到新路:" + LINE + "对应的ip：" + ip);
        try {
                logger.info("开始发送12消息到线路[" + LINE + "],ip[" + ip + "]");
                ConstructMessage12 msg12 = new ConstructMessage12();
                msg12.constructAndSend(ip, fileName, LINE, "00");
                logger.info("成功发送12消息到线路[" + LINE + "],ip[" + ip + "]");
        } catch (Exception e) {
                result = false;
                logger.error(e.getMessage());
        } finally{
            return result;
        }
//        return result;
    }

    /*
     * 更新发布信息下发状态
     */
    private void updateInfoFlag(Vector<MBInfoVo> mBInfos, MBInfoDao dao, String flag) {
        boolean result = false;
        int n = 0;
        for(MBInfoVo ifInfoVo : mBInfos){
            result = dao.updateMBInfo(ifInfoVo.getInfoNo(), flag, fileName);
            if(result){
                n++;
            }
        }
        logger.info("手机支付平台有信息文件下发[" + mBInfos.size() + "]条记录,成功更新[" + n + "]条记录");
    }
}
