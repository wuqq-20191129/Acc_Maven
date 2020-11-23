package com.goldsign.commu.frame.constant;

/**
 * 票务接口相关常量
 * 
 * @author zhangjh
 * 
 */
public class FrameTicketConstant {
	public static final int HEAD_LENGTH = 22;// 配置数据文件头长度
        public static final int HEAD_LENGTH_TRX = 24;// 交易文件头长度
	public static final int CRC_LENGTH = 12;// CRC长度,类似：CRC:", "037067c5
	// 文件头部信息
	public static final int LEN_LINE = 2;// 线路长度
	public static final int LEN_BEGIN_TIME = 7;// 开始时间长度
	public static final int LEN_END_TIME = 7;// 结束时间长度
	public static final int LEN_SEQ = 2;// 流水号长度
	public static final int LEN_RECORDS = 4;// 记录数长度

	public static final int LEN_LINE_STATION = 4;// 车站长度
	public static final int LEN_CARD_TYPE = 2;// 票卡类型长度
	public static final int LEN_VALUE = 4;// 面值长度
	public static final int LEN_HANDIN_TYPE_ID = 2;// 上交类型
	public static final int LEN_QUANTITY = 4;// 数量
	public static final int LEN_REPORT_DATE = 4;// 报表日期

	public static final int LEN_IS_ABANDON = 1;// 是否弃票
	public static final int LEN_ID_START = 20;// 起始逻辑卡号
	public static final int LEN_ID_END = 20;// 终止逻辑卡号
	public static final int LEN_REMARK = 250;// 备注

	public static final int LEN_QUANTITY_SALE = 4;// 发售数量
	public static final int LEN_QUANTITY_REC = 4;// 闸机单程票回收数量
	public static final int LEN_BALANCE = 4;// 车票本日结存数

	public static final int LEN_EFFECTIVE = 4;// 有效期
	public static final int LEN_MODEL = 3;// 限制使用模式
	public static final int LEN_IN_STATION = 2;// 限制进站代码
	public static final int LEN_EXIT_STATION = 2;// 限制出站代码
	public static final int LEN_DIST_DATE = 7;// 配票时间
        
        public static final int LEN_SALE_DATE = 7;// 发售时间
        public static final int LEN_OPERATOR_ID = 6;// 操作员ID

	public final static String CLASS_HANDLE_DAO_PREFIX = "com.goldsign.commu.app.dao.Tk";// 数据库处理类前缀
	public static String CLASS_HANDLE_FILE_PREFIX = "com.goldsign.commu.frame.handler.Hander";// 文件解析类前缀

	// 文件状态
	public static int FILE_NOT_HANDLE = 0;// 文件未处理
	public static int FILE_IS_HANDLEING = 1;// 文件正在处理
	public static int FILE_IS_ERROR = 2;// 文件有误
	public static int FILE_IN_DB_ERROR = 3;// 文件数据入库异常
	public static int FILE_IS_HANDLED = 4;// 文件已处理
	public static int FILE_NOT_EXIT = 5;// 文件不存在
}
