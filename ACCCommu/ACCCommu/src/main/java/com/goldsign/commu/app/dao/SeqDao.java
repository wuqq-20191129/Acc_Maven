package com.goldsign.commu.app.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.goldsign.commu.frame.exception.BaseException;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 查询序列号
 * 
 * @author zhangjh
 */
public class SeqDao {
	// 初始化
	private static final SeqDao SEQ_DAO = new SeqDao();
	private static Logger logger = Logger.getLogger(SeqDao.class.getName());

	/**
	 * 私有构造方法
	 */
	private SeqDao() {
	}

	/**
	 * 获取单例对象
	 * 
	 * @return
	 */
	public static SeqDao getInstance() {
		return SEQ_DAO;
	}

	/**
	 * 根据序列标签查询出序列号
	 * 
	 * @param seqLab
	 *            序列标签
	 * @return 序列号
	 * @throws SQLException
	 *             SQLException
	 */
	public synchronized long selectNextSeq(String seqLab, DbHelper helper)
			throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append(" select ").append(seqLab).append(".nextval seqno")
				.append(" from dual");
		long seqNo = -1;
		boolean result = helper.getFirstDocument(sb.toString());
		if (result) {
			seqNo = helper.getItemLongValue("seqno");
		}
		return seqNo;

	}

	/**
	 * 序列号不足seqLen，就在前面补足0
	 * 
	 * @param seqLen
	 *            序列号的长度
	 * @param helper
	 * @return
	 * @throws SQLException
	 * @throws BaseException
	 */
	public synchronized String qrSeqNo(int seqLen, DbHelper helper)
			throws SQLException, BaseException {
		String seqNo;
		try {
			seqNo = String
					.valueOf(selectNextSeq(FrameDBConstant.COM_COMMU_P+"seq_cm_tk_interface", helper));
		} catch (SQLException e) {
			logger.error("查询序列号出现异常", e);
			throw e;
		} finally {
			if (null != helper) {
				helper.closeConnection();
			}
		}
		if (null == seqNo || "".equals(seqNo.trim())) {
			logger.error("查询序列号出现异常,seqNo为null或者\"\"");
			throw new BaseException("查询序列号出现异常,seqNo为null或者\"\"");
		}
		// int length = seqNo.trim().length();
		// 递归补0
		return dealSeq(seqNo, seqLen);
	}

	/**
	 * 
	 * @param seq
	 * @param length
	 * @return
	 */
	private String dealSeq(String seq, int length) {
		if (seq.length() >= length) {
			return seq;
		} else {
			return "0" + dealSeq(seq, length - 1);
		}
	}

        /**
	 * 票库通讯接口文件名序号查询
         * acc_commu.CM_SEQ_TK_FILE
	 */
	public synchronized String tkFileSeq(String fileType, DbHelper cmDbHelper)
                    throws SQLException, BaseException {
		String seq = "001";
		boolean result = false;
		String QR_SQL = "select decode(alter_day,to_char(sysdate,'yyyymmdd'),seq,1) seq from "+FrameDBConstant.COM_COMMU_P+"CM_SEQ_TK_FILE where file_type=?";

		//String QR_SQL = "select decode(alter_day,to_char(sysdate,'yyyymmdd'),seq+1,1) seq from "+FrameDBConstant.COM_COMMU_P+"CM_SEQ_TK_FILE where file_type=?";
                Object[] values = {fileType};
		
                try {
			result = cmDbHelper.getFirstDocument(QR_SQL, values);
			if (result) {
                            int s = cmDbHelper.getItemIntValue("seq");
                            seq = String.format("%03d",s);
                            logger.info("tkFileSeq 查询到seq==="+s);
                            updateTkFileSeq(s,fileType,cmDbHelper);
				logger.info("tkFileSeq中updateTkFileSeq 更新seq==="+s);
			}

		} catch (SQLException e) {
			logger.error("查询序列号出现异常", e);
			throw e;
		} finally {
			PubUtil.finalProcess(cmDbHelper);
		}
		return seq;
	}

	/**
	 * 更改票库通讯接口文件名序号
	 */
	public synchronized int updateTkFileSeq(int s, String fileType, DbHelper cmDbHelper) throws SQLException {
		int result = 0;
                String UPDATE_SQL = " update "+FrameDBConstant.COM_COMMU_P+"CM_SEQ_TK_FILE set seq=?, alter_day=to_char(sysdate,'yyyymmdd') where file_type=? ";
                
		try {
			Object[] params = { s, fileType};
			result = cmDbHelper.executeUpdate(UPDATE_SQL, params);

		} catch (SQLException e) {
			logger.error("更改票库通讯接口文件名序号：", e);
                        throw e;
		} 
                
                logger.info("更改票库通讯接口文件名序号:" + s);
                return result;
	}
}
