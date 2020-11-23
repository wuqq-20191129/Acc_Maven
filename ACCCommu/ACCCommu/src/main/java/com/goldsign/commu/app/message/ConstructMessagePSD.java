package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.TkPSDDao;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.vo.InfoPreTicketSale;
import com.goldsign.commu.app.vo.InfoTkBase;
import com.goldsign.commu.frame.constant.FrameCharConstant;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.BaseException;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.thread.TKInterfaceThread;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.util.DateHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * 预制票数据交易文件
 *
 * @author lindq
 *
 */
public class ConstructMessagePSD extends ConstructMessageTK {

    private static final int DATA_LENGTH = 126;
    private static Logger logger = Logger.getLogger(ConstructMessagePSD.class.getName());
    
    private static int[] data_type_head = { T_STR, T_BCD, T_BCD, T_INT, T_INT };
    private static int[] data_len_head = { FrameTicketConstant.LEN_LINE_STATION,
                    FrameTicketConstant.LEN_BEGIN_TIME,
                    FrameTicketConstant.LEN_END_TIME, FrameTicketConstant.LEN_SEQ,
                    FrameTicketConstant.LEN_RECORDS };
    
    private static int[] data_type = {T_STR,T_STR,T_STR,T_STR,T_STR,T_BCD,T_STR,T_STR,T_INT,T_STR,
                                      T_STR,T_INT,T_BCD,T_INT,T_INT,T_INT,T_BCD,T_STR,T_STR,T_BCD,
                                      T_INT, T_STR, T_INT};
    private static int[] data_len = {2,2,4,2,3,2,20,20,1,12,
                                     16,4,1,2,4,4,7,4,6,1,
                                     4,1,4};

    /**
     * 任务完成状态,true:已完成，false：未完成 ,对finish_flag的取值、赋值必须同步
     */
    public void handle() {

        String fileName = "";
        try {
            if (!TKInterfaceThread.isFinish_flag_psd()) {
                logger.info("上一次预制票数据交易文件的生成还没有完成.");
                return;
            }
            TKInterfaceThread.setFinish_flag_psd(false);// 设置为正在运行

            file_flag = "PSD";
            // 查询数据
            List<InfoPreTicketSale> list = query();
            if (list.isEmpty()) {
                logger.debug("未查询到预制票数据...");
                return;
            }
            // 生成文件
            fileName = createFile(FrameCodeConstant.ACC_LINE_CODE, list);
            new TkPSDDao().updateStatus(fileName, list);
        } catch (Exception e) {
            logger.error("生成预制票数据交易文件出现异常", e);
        } finally {
            // logger.info("----------设置状态为结束");
            TKInterfaceThread.setFinish_flag_psd(true);// 设置为结束运行
        }
        logger.info("完成生成预制票数据交易文件."+fileName);

    }


    protected void dealMsgPsd(char[] msg, String line, List<InfoPreTicketSale> list,
            int offset) throws MessageException, IOException {

        String deposit_type="00";
        int deposite_amount=0;
        int card_cost=0;

        for (InfoPreTicketSale tk : list) {
            
            String deptId = tk.getDeptId();//站点代码
            String ticketTypeId = tk.getTicketTypeId();//票卡类型
            int value = tk.getValue();//面值
            String operatorId = tk.getOperatorId();//操作员
            String logicalBegin = tk.getLogicalBegin();//开始逻辑卡号
            String logicalEnd = tk.getLogicalEnd();//结束逻辑卡号
            String saleTime = tk.getSaleTime();//发售时间
            String tkFileName = tk.getTkFileName();//原文件名
            int quantity = tk.getQuantity();//数量

            //修改押金类型，押金金额，次票发售金额
    //        Map<String,Object> map =new HashMap<String, Object>();
            List<Object> queryList =new ArrayList<Object>();

            queryList= new TkPSDDao().queryForDeposit(ticketTypeId);
//            String deposit_type= (String) map.get("deposit_type");
//            int deposite_amount= (int) map.get("deposite_amount");
//            int card_cost = (int)map.get("card_cost");
            if(!queryList.isEmpty()) {
                deposit_type = (String) queryList.get(0);
                deposite_amount = (int) queryList.get(1);
                card_cost = (int) queryList.get(2);
            }
            // 数据
            String logicalNo = logicalBegin;
            int seqNo = Integer.parseInt(logicalNo.substring(7, 16));
            for(int i=0; i<quantity; i++){
                if(i>0){
                    logicalNo = logicalNo.substring(0, 7) + CharUtil.addCharsBefore(""+seqNo, 9, "0");
                }
                if("0000000000000000".equals(logicalBegin)){
                    logicalNo = logicalBegin;
                }
                
//                Object[] datas = {"63", "10", deptId, CharUtil.addCharsAfter("", 2, " "), CharUtil.addCharsAfter("", 3, " "), ticketTypeId, CharUtil.addCharsBefore(logicalNo, 20, " "),
//                    CharUtil.addCharsAfter("", 20, "0"), 101, CharUtil.addCharsAfter("", 12, "0"), CharUtil.addCharsAfter("", 16, "0"), 0, "00", 0, value, 1,
//                    CharUtil.addCharsAfter(DateHelper.dateStrToStr(saleTime), 14, "0"),
//                    CharUtil.addCharsAfter("", 4, "0"), CharUtil.addCharsAfter(operatorId, 6, " "), "00", 0, "0", 0};
                Object[] datas = {"63", "10", deptId, CharUtil.addCharsAfter("03", 2, " "), CharUtil.addCharsAfter("000", 3, " "), ticketTypeId, CharUtil.addCharsBefore(logicalNo, 20, " "),
                        CharUtil.addCharsAfter("", 20, "0"), 101, CharUtil.addCharsAfter("", 12, "0"), CharUtil.addCharsAfter("", 16, "0"), 0, deposit_type, deposite_amount, value, 1,
                        CharUtil.addCharsAfter(DateHelper.dateStrToStr(saleTime), 14, "0"),
                        CharUtil.addCharsAfter("", 4, "0"), CharUtil.addCharsAfter(operatorId, 6, " "), "00", 0, "0", card_cost};
                // 文件记录
                char[] cs = getLine(datas, data_len, data_type);
                addCharArrayToBuffer(msg, cs, offset);
                offset += (DATA_LENGTH + CRLF_LENGTH);
                seqNo++;
            }
        }

    }

    /**
     * 查询数据
     *
     * @return
     */
    private List<InfoPreTicketSale> query() {
        List<InfoPreTicketSale> list = new TkPSDDao().query();
        return list;
    }

    private String  createFile(String line, List<InfoPreTicketSale> list)
            throws SQLException, BaseException, MessageException {
        // 文件序列号
        String seqNo = getSeq();
        Date now = new Date();
        String nowDateStr = DateHelper.dateToStr(now, DateHelper.yyyyMMdd);

        // 文件名
        String fileName = "TRX" + line + FrameCharConstant.POINT
                + nowDateStr + FrameCharConstant.POINT + seqNo;
        File newFile = new File(FrameCodeConstant.ftpLocalDir + "/" + fileName);

        // 同名文件存在
        if (newFile.exists()) {
            Date nowDate = new Date();
            // 移动到his目录，并在文件名后加上日期
            File dest = new File(FrameCodeConstant.ftpLocalDir + "/" + fileName + "."
                    + DateHelper.dateToString(nowDate));
            newFile.renameTo(dest);
        }

        try {
            // 打开时间
            String beginTime = DateHelper.dateToStr(now, DateHelper.yyyyMMdd) + "000000";
            String endTime = DateHelper.dateToStr(now, DateHelper.yyyyMMdd) + "235959";
            int offset = 0;
            // 文件中数据的记录数
            int size = caculateDataSize(list);// 记录数
            int headLen = FrameTicketConstant.HEAD_LENGTH_TRX + CRLF_LENGTH; // 长度+回车换行符

            // 计算文件内容字符大小
            int dataLen = caculateDateLen(size);
            int crcLen = FrameTicketConstant.CRC_LENGTH; // CRC不加回车换行
            // 总字符数
            char[] msg = new char[headLen + dataLen + crcLen];

            dealHeadPSD(msg, line, beginTime, endTime, seqNo, size, offset);

            offset += headLen;
            dealMsgPsd(msg, line, list, offset);
            offset += dataLen;
            dealCrc(msg, offset);
            writeFile(msg, FrameCodeConstant.ftpLocalDir, fileName);
            // // 关闭时间
            // String endtime = DateHelper.dateToStr(now,
            // DateHelper.yyyyMMddHHmmss);

        } catch (FileNotFoundException e1) {
            logger.error("文件未找到：" + fileName);
        } catch (IOException e) {
            logger.error("生成：" + fileName + "发生错误.");
        } finally {
        }

        return fileName;
    }

    @Override
    protected int caculateDateLen(int size) {
        // 换行符用三个0X0D0X0A;
        return (DATA_LENGTH + CRLF_LENGTH) * size;
    }

    /*
    计算发售总数量
    */
    private int caculateDataSize(List<InfoPreTicketSale> list) {
        int n = 0;
        for(InfoPreTicketSale infoPreTicketSale:list){
            n = n+infoPreTicketSale.getQuantity();
        }
        return n;
    }
    
    /**
	 * 写文件头
	 * @param msg
	 * @param line
	 * @param begintime
	 * @param endtime
	 * @param seqNo
	 * @param size
	 * @param offset
	 * @return
	 * @throws MessageException
	 * @throws IOException
	 */
	private void dealHeadPSD(char[] msg, String line, String beginTime,
			String endTime, String seqNo, int size, int offset)
			throws MessageException, IOException {
		Object[] data_head = { line, beginTime, endTime, seqNo, size };
		// 文件头
		char[] cs = this.getLine(data_head, data_len_head, data_type_head);
		addCharArrayToBuffer(msg, cs, offset);
	}
        

    @Override
    protected void dealMsg(char[] msg, String line, List<InfoTkBase> list, int offset) throws MessageException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
