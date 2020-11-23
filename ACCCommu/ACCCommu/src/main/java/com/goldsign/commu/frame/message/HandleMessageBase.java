package com.goldsign.commu.frame.message;

import java.util.List;

import com.goldsign.commu.frame.vo.SynchronizedControl;

/**
 * 
 * @author zhangjh
 */
public class HandleMessageBase {
	/**
	 * 流水号
	 */
	private int waterNo;

	/**
	 * 原文件文件路径
	 */
	private String path;
	/**
	 * 原文件名
	 */
	private String fileName;
	/**
	 * 新文件名
	 */
	private String newFileName;

	/**
	 * 文件类型
	 */
	private String tradType;
	/***
	 * 正在处理的文件存放路径
	 */
	private String handlePath;
	/**
	 * 成功处理的文件存放路径
	 */
	private String pathHis;
	/**
	 * 错误文件的存放路径
	 */
	private String pathError;
	/**
	 * 文件状态： <br/>
	 * 0：文件未处理<br/>
	 * 1:文件正在处理<br/>
	 * 2:文件有误<br/>
	 * 3:文件数据入库异常<br/>
	 * 4:文件已处理
	 * 
	 */
	private int finished = 1;
	/**
	 * 文件对应的文件内容
	 */
	private List<Object> content;

	private final SynchronizedControl syn = new SynchronizedControl();

	public HandleMessageBase() {
	}

	public HandleMessageBase(String path, String pathHis, String pathError,
			String fileName, int waterNo) {
		this.path = path;
		this.fileName = fileName;
		this.pathHis = pathHis;
		this.pathError = pathError;
		this.waterNo = waterNo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTradType() {
		return tradType;
	}

	public void setTradType(String tradType) {
		this.tradType = tradType;
	}

	public String getPathHis() {
		return pathHis;
	}

	public void setPathHis(String pathHis) {
		this.pathHis = pathHis;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	public String getPathError() {
		return pathError;
	}

	public void setPathError(String pathError) {
		this.pathError = pathError;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	public SynchronizedControl getSyn() {
		return syn;
	}

	public List<Object> getContent() {
		return content;
	}

	public void setContent(List<Object> content) {
		this.content = content;
	}

	public int getWaterNo() {
		return waterNo;
	}

	public void setWaterNo(int waterNo) {
		this.waterNo = waterNo;
	}

    /**
     * @return the handlePath
     */
    public String getHandlePath() {
        return handlePath;
    }

    /**
     * @param handlePath the handlePath to set
     */
    public void setHandlePath(String handlePath) {
        this.handlePath = handlePath;
    }

	

}
