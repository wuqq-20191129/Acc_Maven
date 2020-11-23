package com.goldsign.commu.frame.vo;

/**
 * 文件对象
 * 
 * @author zhangjh
 * 
 */
public class RecvFileVo {
	/**
	 * 流水号
	 */
	private int waterNo;
	/**
	 * 文件名
	 */
	private String fileName;
	/**
	 * 文件类型
	 */
	private String fileType;
	/**
	 * 新文件名
	 */
	private String newFileName;
	/**
	 * 文件路径
	 */
	private String filePath="ticket";
	/***
	 * 正在处理的文件存放路径
	 */
	private String handlePath="handling";
	/***
	 * 成功处理的文件存放路径
	 */
	private String hisPath="his";
	/**
	 * 错误文件的存放路径
	 */
	private String errorPath="error";
	/**
	 * 文件状态 <br/>
	 * 0：未处理<br/>
	 * 1：正在处理 <br/>
	 * 2：文件格式有问题<br/>
	 * 3：文件数据入库异常<br/>
	 * 4:已处理<br/>
	 */
	private int status;

	/**
	 * 
	 */
	private String remark;

	public RecvFileVo() {
		super();
	}

	public RecvFileVo(int waterNo, String fileName, String newFileName,
			int status, String remark) {
		super();
		this.waterNo = waterNo;
		this.fileName = fileName;
		this.newFileName = newFileName;
		this.status = status;
		this.remark = remark;
	}

	/**
	 * 文件标识，st:清算 tk:票务 cm:通信
	 * 
	 * @return
	 */
	private String flag;

	public int getWaterNo() {
		return waterNo;
	}

	public void setWaterNo(int waterNo) {
		this.waterNo = waterNo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getHisPath() {
		return hisPath;
	}

	public void setHisPath(String hisPath) {
		this.hisPath = hisPath;
	}

	public String getErrorPath() {
		return errorPath;
	}

	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getHandlePath() {
		return handlePath;
	}

	public void setHandlePath(String handlePath) {
		this.handlePath = handlePath;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
