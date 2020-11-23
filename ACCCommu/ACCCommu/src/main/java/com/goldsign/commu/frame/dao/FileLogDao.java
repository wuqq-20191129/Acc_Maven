package com.goldsign.commu.frame.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.handler.HanderTk;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.RecvFileVo;
import com.goldsign.lib.db.util.DbHelper;

/**
 * 接收文件的信息入库、更新。
 * 
 * @author zhangjh
 */
public class FileLogDao {

	private static Logger logger = Logger.getLogger(FileLogDao.class.getName());
	private final static String SQL_LABLE = ""+FrameDBConstant.COM_COMMU_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"cm_file_recv";
	private final static String UPDATE_SQL = " update "+FrameDBConstant.COM_COMMU_P+"cm_file_recv set new_filename=?,status=?,remark=?,update_time=? where water_no=?";
	private final static String INSERT_SQL = " insert into "+FrameDBConstant.COM_COMMU_P+"cm_file_recv(water_no,file_name,file_type,file_path,his_path,handle_path,err_path,status,flag,insert_time) values(?,?,?,?,?,?,?,?,?,?)";
	private final static String QR_SQL = " select water_no,file_name,file_type,file_path,his_path,handle_path,err_path from "+FrameDBConstant.COM_COMMU_P+"cm_file_recv  where flag='tk' and status=0";
	private final static String UPDATE_SQL2 = " update "+FrameDBConstant.COM_COMMU_P+"cm_file_recv set status=?,remark=? where water_no=?";

	//20200416 增加FTP 文件 收取时查重
	private final static String QR_SQL1 = " select file_name from  "+FrameDBConstant.COM_COMMU_P+"cm_file_recv where status in ('0','1','4') and file_name=?";

	// public int insert(FileLogVo vo) throws Exception {
	// String sql =
	// "insert into st_log_file_passed(file_name,file_type,balance_water_no,gen_time) "
	// + "values(?,?,?,sysdate) ";
	// DbHelper dbHelper = null;
	// int result = 0;
	// Object[] values = {vo.getFileName(), vo.getFileType(),
	// vo.getBalanceWaterNo()};
	// try {
	// dbHelper = new DbHelper("",
	// FrameDBConstant.OP_DBCPHELPER.getConnection());
	// result = dbHelper.executeUpdate(sql, values);
	//
	// } catch (Exception e) {
	// PubUtil.handleException(e, logger);
	// } finally {
	// PubUtil.finalProcess(dbHelper);
	// }
	// return result;
	//
	//
	// }

	/**
	 * 更新文件状态
	 * 
	 * @param vo
	 */
	public void update(RecvFileVo vo) {
		DbHelper dbHelper = null;
		int result = 0;
		// 入库时间
		String update_time = DateHelper.dateToStr(new Date(),
				DateHelper.yyyy_MM_dd_HH_mm_ss);
		int status = vo.getStatus();
		String remark = HanderTk.FILE_STATUS.get(status);
		Object[] values = { vo.getNewFileName(), status, remark, update_time,
				vo.getWaterNo() };

		try {

			dbHelper = new DbHelper("FileLogDao",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			result = dbHelper.executeUpdate(UPDATE_SQL, values);

		} catch (Exception e) {
			logger.error("更新cm_file_recv表出错:", e);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.debug("更新cm_file_recv表[" + result + "]条数据,water_no="
				+ vo.getWaterNo());

	}

	/**
	 * 接收的文件信息入库
	 * 
	 * @param vo
	 */
	public void insert(RecvFileVo vo) {

		DbHelper dbHelper = null;
		int result = 0;

		try {
			dbHelper = new DbHelper("FileLogDao",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			// 取流水号，加了同步锁
			int waterNo = (int) SeqDao.getInstance().selectNextSeq(SQL_LABLE,
					dbHelper);
			vo.setWaterNo(waterNo);

			// 入库时间
			String insert_time = DateHelper.dateToStr(new Date(),
					DateHelper.yyyy_MM_dd_HH_mm_ss);

			Object[] values = { vo.getWaterNo(), vo.getFileName(),
					vo.getFileType(),
					vo.getFilePath(),
					vo.getHisPath(), vo.getHandlePath(), vo.getErrorPath(),
					vo.getStatus(), vo.getFlag(), insert_time };
			result = dbHelper.executeUpdate(INSERT_SQL, values);

		} catch (Exception e) {
			logger.error("数据插入cm_file_recv表出错:", e);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.debug("插入cm_file_recv表[" + result + "]条数据,water_no="
				+ vo.getWaterNo());

	}

	/**
	 * 查询需要处理的文件
	 * 
	 * @param vo
	 */
	public List<RecvFileVo> query() {
		logger.debug("查询需要处理的文件开始");
		DbHelper dbHelper = null;
		boolean result = false;
		List<RecvFileVo> list = new ArrayList<RecvFileVo>();
		try {
			dbHelper = new DbHelper("FileLogDao",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			result = dbHelper.getFirstDocument(QR_SQL);
			RecvFileVo vo = null;
			while (result) {
				vo = new RecvFileVo();
				int waterNo = dbHelper.getItemIntegerValue("water_no");
				String fileName = dbHelper.getItemValue("file_name");
				String fileType = dbHelper.getItemValue("file_type");
				String filePath = dbHelper.getItemValue("file_path");
				String hisPath = dbHelper.getItemValue("his_path");
				String errorPath = dbHelper.getItemValue("err_path");
				String handlePath = dbHelper.getItemValue("handle_path");

				vo.setWaterNo(waterNo);
				vo.setFileName(fileName);
				vo.setFileType(fileType);
				vo.setFilePath(filePath);
				vo.setHisPath(hisPath);
				vo.setHandlePath(handlePath);
				vo.setErrorPath(errorPath);
				list.add(vo);
				result = dbHelper.getNextDocument();
			}
		} catch (Exception e) {
			logger.error("查询cm_file_recv表出错:", e);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}
		logger.debug("查询cm_file_recv表[" + list.size() + "]条数据.");
		logger.debug("查询需要处理的文件结束");
		return list;
	}

	/**
	 * 
	 * 判断文件是否存在 <br/>
	 * 如果存在，将状态改为1（正在执行）,这批文件等待解析入库 <br/>
	 * 如果不存在，将状态改为5(文件有误)
	 * 
	 * @param list
	 */
	public List<RecvFileVo> update(List<RecvFileVo> list) {

		DbHelper dbHelper = null;
		int result = 0;
		List<RecvFileVo> list2 = new ArrayList<RecvFileVo>();
		try {
			dbHelper = new DbHelper("FileLogDao",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			for (RecvFileVo vo : list) {
				String fileName = vo.getFileName();
				// String filePath = vo.getFilePath();
				String fileFullFath = vo.getFilePath() + "/" + fileName;
				File file = new File(fileFullFath);
				int status;

				if (file.exists()) {
					status = FrameTicketConstant.FILE_IS_HANDLEING;
					list2.add(vo);
				} else {
					status = FrameTicketConstant.FILE_NOT_EXIT;
				}
				String remark = HanderTk.FILE_STATUS.get(status);
				Object[] values = { status, remark, vo.getWaterNo() };
				result += dbHelper.executeUpdate(UPDATE_SQL2, values);
			}
			logger.debug("批量更新cm_file_recv表状态，共[" + result + "]条数据.");

		} catch (Exception e) {
			logger.error("批量更新cm_file_recv表状态为正在处理出错:", e);
		} finally {
			PubUtil.finalProcess(dbHelper);
		}

		if (!list2.isEmpty()) {
			logger.debug("等待解析的文件有：");
			for (RecvFileVo vo : list2) {
				logger.info(vo.getFileName());
			}
		}

		return list2;

	}

	/**
	 *
	 * 判断文件是否已正常处理 <br/>
	 * 是，将状态改为6（文件存在正常处理记录）,停止数据入库，只插入收取记录 <br/>
	 * 否 ，继续处理
	 *
	 * @param file_name
	 */
	public boolean isRepeat(String file_name){
		boolean isRepeat = false;
		boolean result = false;
		DbHelper dbHelper = null;
		try{
			dbHelper = new DbHelper("FileLogDao",
					FrameDBConstant.CM_DBCPHELPER.getConnection());
			Object[] values={file_name};
			result = dbHelper.getFirstDocument(QR_SQL1,values);
			if(result){
				isRepeat=true;
			}
		}catch (Exception e){
			logger.error("查询cm_file_recv表出错",e);
		}finally {
			PubUtil.finalProcess(dbHelper);
		}
		return isRepeat;
	}
}
