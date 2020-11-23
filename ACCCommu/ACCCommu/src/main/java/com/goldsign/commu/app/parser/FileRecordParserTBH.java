package com.goldsign.commu.app.parser;

import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.app.vo.InfoIncomedepHandin;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.parser.FileRecordParserBase;

/**
 * 解析收益租上交文件
 * 
 * @author zhangjh
 * 
 */
public class FileRecordParserTBH extends FileRecordParserBase {

	@Override
	public Object parse(String line, FileRecordAddVo lineAdd)
			throws RecordParseException {
		InfoIncomedepHandin r = new InfoIncomedepHandin();
		char[] b = null;
		int offset = 0;
		try {
			b = getLineCharFromFile(line);

			int len = FrameTicketConstant.LEN_LINE;
			r.setDeptId(getCharString(b, offset, len));// 站点代码
			offset += len;

			len = FrameTicketConstant.LEN_CARD_TYPE;
			r.setTicketTypeId(getBcdString(b, offset, len));// 票卡类型
			offset += len;

			len = FrameTicketConstant.LEN_VALUE;
			r.setValue(getLong(b, offset));// 面值
			offset += len;

			len = FrameTicketConstant.LEN_HANDIN_TYPE_ID;
			r.setHandinTypeId(getCharString(b, offset, len));// 上交类型
			offset += len;

			len = FrameTicketConstant.LEN_QUANTITY;
			r.setQuantity(getLong(b, offset));// 数量
			offset += len;

			len = FrameTicketConstant.LEN_REPORT_DATE;
			r.setReportDate(getBcdString(b, offset, len));// 报表日期

		} catch (Exception ex) {
			throw new RecordParseException("解析文件记录出现异常.");
		}
		return r;

	}

	@Override
	public void handleMessage(HandleMessageBase msg) {
		// TODO Auto-generated method stub

	}

}
