package com.goldsign.commu.app.parser;

import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.app.vo.InfoPreTicketSale;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.parser.FileRecordParserBase;

/**
 * 解析预制票数据文件
 * 
 * @author lindq
 * 
 */
public class FileRecordParserPSD extends FileRecordParserBase {

	@Override
	public Object parse(String line, FileRecordAddVo lineAdd)
			throws RecordParseException {
		          InfoPreTicketSale r = new InfoPreTicketSale();
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
			r.setQuantity(quantitySale);// 发售数量
			offset += len;

			len = FrameTicketConstant.LEN_ID_START;
			b = getLineCharFromFile(line);
			r.setLogicalBegin(getCharString(b, offset, len));// 起始逻辑卡号
			offset += len;
                        
                        len = FrameTicketConstant.LEN_ID_END;
			b = getLineCharFromFile(line);
			r.setLogicalEnd(getCharString(b, offset, len));// 终止逻辑卡号
			offset += len;
                        
                        len = FrameTicketConstant.LEN_DIST_DATE;
			r.setSaleTime(getBcdString(b, offset, len));// 发售时间
			offset += len;
                        
                        len = FrameTicketConstant.LEN_OPERATOR_ID;
			b = getLineCharFromFile(line);
			r.setOperatorId(getCharString(b, offset, len));// 操作员ID
			offset += len;

			len = FrameTicketConstant.LEN_REPORT_DATE;
			r.setReportDate(getBcdString(b, offset, 4));// 报表日期

		} catch (Exception ex) {
			throw new RecordParseException("解析文件记录出现异常."+ex.getMessage());
		}
		return r;

	}

	@Override
	public void handleMessage(HandleMessageBase msg) {

	}

}
