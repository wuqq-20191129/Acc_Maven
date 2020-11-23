package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.BusinessInfo;
import com.goldsign.commu.frame.vo.CardInfo;
import com.goldsign.commu.frame.vo.Message23Vo;
import com.goldsign.lib.db.util.DbHelper;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 证件号查询业务信息 DAO
 *
 * @author lindaquan
 * @creatTime 20180410
 */
public class Message23Dao {
    private static Logger logger = Logger.getLogger(Message23Dao.class.getName());

    public Message23Dao() throws Exception {
        super();
    }

    /**
     * 逻辑卡号查询
     * @param vo
     * @param dbHelper
     * @throws java.lang.Exception
     */
    public void cardInfos(Message23Vo vo, DbHelper dbHelper) throws Exception {
        boolean result;
        List<CardInfo> cardInfos = new ArrayList<>();
        Object[] values = {vo.getIDCardType(),vo.getIDNumber()};
        String sql = "select a.card_main_id,a.card_sub_id,a.order_no,a.req_no,b.logical_id "
                + "from " + FrameDBConstant.COM_ST_P + "st_list_sign_card a, " + FrameDBConstant.COM_TK_P + "IC_ES_INITI_INFO b "
                + "where a.order_no=b.order_no(+) and a.req_no=b.req_no(+) and a.identity_type=? and a.identity_id=?";

        result = dbHelper.getFirstDocument(sql, values);
        while (result) {
            CardInfo cardInfo = new CardInfo();
            cardInfo.setCardType(dbHelper.getItemValue("card_main_id") + dbHelper.getItemValue("card_sub_id"));
            cardInfo.setLogicalNo(dbHelper.getItemValue("logical_id"));
            cardInfos.add(cardInfo);
            result = dbHelper.getNextDocument();
        }
        vo.setCardInfos(cardInfos);
    }
    
    /**
     * 待处理非即时退款凭证号查询 
     * @param vo
     * @param dbHelper
     * @throws java.lang.Exception
     */
    public void businessInfos(Message23Vo vo, DbHelper dbHelper) throws Exception {
        boolean result;
        List<BusinessInfo> businessInfos = new ArrayList<>();
        Object[] values = {vo.getIDCardType(),vo.getIDNumber(),vo.getIDCardType(),vo.getIDNumber()};
        String sql = "select '02' business_type, a.apply_bill from " + FrameDBConstant.COM_ST_P + "ST_LIST_REPORT_LOSS a where a.identify_type=? and a.identity_id=? union all"
                + " select '01' business_type, b.business_receipt_id apply_bill from " + FrameDBConstant.COM_ST_P + "ST_LIST_NON_RTN b"
                + " where b.identity_type=? and b.identity_id=? and b.hdl_flag in('2','3')";//非即退只返回 2：车票未处理；3：车票已有退款结果；

        result = dbHelper.getFirstDocument(sql, values);
        while (result) {
            BusinessInfo businessInfo = new BusinessInfo();
            businessInfo.setBusyType(dbHelper.getItemValue("business_type"));
            businessInfo.setApplyBill(dbHelper.getItemValue("apply_bill"));
            businessInfos.add(businessInfo);
            result = dbHelper.getNextDocument();
        }
        vo.setBusinessInfos(businessInfos);
    }

}
