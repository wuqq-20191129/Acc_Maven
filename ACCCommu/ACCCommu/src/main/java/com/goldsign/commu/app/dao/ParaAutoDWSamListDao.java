package com.goldsign.commu.app.dao;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameParameterConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 *
 * @author hejj
 */
public class ParaAutoDWSamListDao {

    private static Logger logger = Logger.getLogger(ParaAutoDWSamListDao.class
            .getName());

    private String getMsgText(int retCode) {
        if (retCode == 1) {
            return "不存在当前版本或未来版本";
        }
        if (retCode == 2) {
            return "不存在需下发的当前版本或未来版本";
        }
        if (retCode == 3) {
            return "没有设置最小应满足的新增数量";
        }
        if (retCode == 4) {
            return "没有满足最小新增数量的版本";
        }
        if (retCode == 5) {
            return "获取日志流水号异常";
        }
        if (retCode == 6) {
            return "初始化日志记录异常";
        }
        if (retCode == 7) {
            return "增加下发的基本信息异常";
        }
        if (retCode == 8) {
            return "增加下发的参数信息异常";
        }
        if (retCode == 9) {
            return "不存在当前版本或未来版本";
        }
        if (retCode == 1) {
            return "增加下发的线路信息异常";
        }
        return "没有对应的消息文本";

    }

    public void download() throws Exception {
        boolean result;
        DbHelper dbHelper = null;
        int num = 0;
        String[] values = {FrameParameterConstant.parmTypeSamlist};

        try {
            dbHelper = new DbHelper("dataDbUtil",
                    FrameDBConstant.OP_DBCPHELPER.getConnection());

            String sqlStr = "exec "+FrameDBConstant.COM_COMMU_P+"Up_com_para_auto_dw ?";

            result = dbHelper.getFirstDocument(sqlStr, values);
            if (result) {
                num = dbHelper.getItemIntValue("result");
            }
            if (num != 0) {
                logger.error("自动下发返回消息：" + this.getMsgText(num));
            }

        } catch (Exception e) {
            PubUtil.handleException(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }

    }
}
