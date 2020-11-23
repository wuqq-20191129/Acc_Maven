package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;
import org.apache.log4j.Logger;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-06-18
 * @Time: 12:12
 */
public class ExternalIpDao {
    private static Logger logger = Logger.getLogger(ExternalIpDao.class.getName());

    public String getExternalIp(String type) throws Exception {
        boolean result = false;
        DbHelper dbHelper = null;
        String ip = "127.0.0.1";
        Object[] values = {type};

        try {
            dbHelper = new DbHelper("ExternalIpDao",
                    FrameDBConstant.CM_DBCPHELPER.getConnection());
            String sqlStr = "select ip from " + FrameDBConstant.COM_COMMU_P + "cm_external_ip where type=?";
            result = dbHelper.getFirstDocument(sqlStr, values);
            if (!result) {
                throw new Exception("获取" + type + "控制失败，记录不存在");
            } else {
                ip = dbHelper.getItemValue("ip");
            }

        } catch (Exception e) {
            PubUtil.handleException(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }

        return ip;
    }
}
