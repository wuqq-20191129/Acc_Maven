package com.goldsign.commu.app.parser;

import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.app.vo.InfoStationSale;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.parser.FileRecordParserBase;

/**
 * 解析售存文件
 * 
 * @author zhangjh
 * 
 */
public class FileRecordParserTST extends FileRecordParserBase {

	@Override
	public Object parse(String line, FileRecordAddVo lineAdd)
			throws RecordParseException {
		InfoStationSale r = new InfoStationSale();
		char[] b = null;
		int offset = 0;

		try {
			int len = FrameTicketConstant.LEN_LINE_STATION;
			b = getLineCharFromFile(line);
			r.setDeptId(getCharString(b, offset, len));// 站点代码
			offset += len;

			len = FrameTicketConstant.LEN_CARD_TYPE;
			r.setTicketTypeId(getBcdString(b, offset, len));// 票卡类型
			offset += len;

			len = FrameTicketConstant.LEN_VALUE;
			int value = getLong(b, offset);
			r.setValue(value);// 面值
			offset += len;

			len = FrameTicketConstant.LEN_QUANTITY_SALE;
			int quantitySale = getLong(b, offset);
			r.setQuantitySale(quantitySale);// 发售数量
			offset += len;

			len = FrameTicketConstant.LEN_QUANTITY_REC;
			int quantityRec = getLong(b, offset);
			r.setQuantityRec(quantityRec);// 闸机单程票回收数量
			offset += len;

			len = FrameTicketConstant.LEN_BALANCE;
			int balance = getLong(b, offset);
			r.setBalance(balance);// 车票本日结存数
			offset += len;

			len = FrameTicketConstant.LEN_REPORT_DATE;
			r.setReportDate(getBcdString(b, offset, 4));// 报表日期

		} catch (Exception ex) {
			throw new RecordParseException("解析文件记录出现异常.");
		}
		return r;

	}

	@Override
	public void handleMessage(HandleMessageBase msg) {

	}

}
