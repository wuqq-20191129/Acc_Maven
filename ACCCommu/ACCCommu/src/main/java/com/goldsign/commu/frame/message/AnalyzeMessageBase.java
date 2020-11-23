package com.goldsign.commu.frame.message;

import com.goldsign.commu.frame.vo.TMThreadMsg;

/**
 * 
 * @author hejj
 */
public abstract class AnalyzeMessageBase {
	// private String msgKeyType = "1";// 按消息代码+线路
	public final static String msgKeyType_line = "1";// 按消息代码+线路
	public final static String msgKeyType_line_station = "2";// 按消息代码+线路+车站代码

	public abstract TMThreadMsg getMsgKeyInfo(byte[] msg);

}
