package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.Message21Vo;
import com.goldsign.commu.frame.vo.Message22Vo;
import com.goldsign.lib.db.util.DbHelper;
import org.apache.log4j.Logger;

/**
 * 挂失/解挂申请/挂失补卡申请 DAO
 *
 * @author lindaquan
 * @creatTime 20170717
 * @version 1.00
 */
public class Message21Dao {

    private static Logger logger = Logger.getLogger(Message21Dao.class.getName());
    
    /**
     * 查询挂失/解挂申请/挂失补卡申请信息
     *
     * @param vo 查询信息
     * @param id 连接ID
     * @param dbHelper
     * @throws Exception
     */
    public Message22Vo getReportLossInfoFromSp(Message21Vo vo, DbHelper dbHelper) throws Exception {
        Message22Vo vo22 = new Message22Vo();
        int[] pInIndexes = {1, 2, 3, 4, 5, 6, 7, 8};
        Object[] pInStmtValues = {vo.getBusnissType(), vo.getIDCardType(), vo.getIDNumber(),
            vo.getApplyBill(), vo.getAction(), vo.getCurrentBom(), vo.getCardType(), vo.getCardLogicalID()};

        // 存储过程输出参数索引列表
        int[] pOutIndexes = {9};
        // 存储过程输出参数值类型
        int[] pOutTypes = {DbHelper.PARAM_OUT_TYPE_VACHAR};
        String sql = "call " + FrameDBConstant.COM_ST_P + "up_cm_report_loss(?,?,?,?,?,?,?,?,?)";

        // 执行存储过程
        dbHelper.runStoreProcForOracle(sql, pInIndexes, pInStmtValues,
                pOutIndexes, pOutTypes);

        vo22.setBusnissType(vo.getBusnissType());
        vo22.setResultCode(dbHelper.getOutParamStringValue(9));

        return vo22;
    }

}
