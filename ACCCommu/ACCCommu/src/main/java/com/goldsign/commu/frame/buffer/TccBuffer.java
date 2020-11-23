package com.goldsign.commu.frame.buffer;

import com.goldsign.commu.frame.constant.FrameDBConstant;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-04
 * @Time: 13:00
 */
public class TccBuffer {
    public static ConcurrentLinkedQueue<byte[]> TCC_MESSAGE_QUEUE = new ConcurrentLinkedQueue<>();

    public static final String logSqlPassageFlow = "insert into " + FrameDBConstant.COM_COMMU_P + "cm_tcc_transfer_log(message_code,message_seq,line_id,station_id,insert_time) values(?,?,?,?,sysdate)";
    public static final String logSql = "insert into " + FrameDBConstant.COM_COMMU_P + "cm_tcc_transfer_log(message_code,message_seq,insert_time) values(?,?,sysdate)";

    public static HashMap<String, String> MESSAGE_NAME_MAPPING = new HashMap<>();

    static {
        MESSAGE_NAME_MAPPING.put("08", "全部设备状态信息");
        MESSAGE_NAME_MAPPING.put("09", "单个设备状态信息");
        MESSAGE_NAME_MAPPING.put("10", "设备状态变化信息");
        MESSAGE_NAME_MAPPING.put("13", "进站累计客流");
        MESSAGE_NAME_MAPPING.put("14", "出站累计客流");
        MESSAGE_NAME_MAPPING.put("22", "文件通知");
        MESSAGE_NAME_MAPPING.put("23", "5分钟进站客流");
        MESSAGE_NAME_MAPPING.put("24", "5分钟出站客流");
    }
}
