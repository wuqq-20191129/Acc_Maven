package com.goldsign.commu.app.parser;

import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.app.vo.InfoStationHandin;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.parser.FileRecordParserBase;
import com.goldsign.commu.frame.util.CharUtil;

/**
 * 解析车站上交文件
 * 
 * @author zhangjh
 * 
 */
public class FileRecordParserTHD extends FileRecordParserBase {

	@Override
	public Object parse(String line, FileRecordAddVo lineAdd)
			throws RecordParseException {
		InfoStationHandin r = new InfoStationHandin();
		char[] b = null;
		int offset = 0;

		try {
			// b=getLineByteFromFileTest(line);
			b = getLineCharFromFile(line);

			int len = FrameTicketConstant.LEN_LINE_STATION;
			r.setDeptId(getCharString(b, offset, len));// 站点代码
			offset += len;

			len = FrameTicketConstant.LEN_CARD_TYPE;
			r.setTicketTypeId(getBcdString(b, offset, len));// 票卡类型
			offset += len;

			len = FrameTicketConstant.LEN_VALUE;
			r.setValue(getLong(b, offset));// 面值
			offset += len;

			len = FrameTicketConstant.LEN_IS_ABANDON;
			r.setIsAbandon(getCharString(b, offset, len));// 是否弃票
			offset += len;

			len = FrameTicketConstant.LEN_HANDIN_TYPE_ID;
			r.setHandinTypeId(getCharString(b, offset, len));// 上交类型
			offset += len;

			len = FrameTicketConstant.LEN_QUANTITY;
			r.setQuantity(getLong(b, offset));// 数量
			offset += len;

			len = FrameTicketConstant.LEN_ID_START;
			r.setIdStart(getCharString(b, offset, len));// 起始逻辑卡号
			offset += len;

			len = FrameTicketConstant.LEN_ID_END;
			r.setIdEnd(getCharString(b, offset, len));// 终止逻辑卡号
			offset += len;

			len = FrameTicketConstant.LEN_REMARK;
			String remark = getCharString(b, offset, len);
			r.setRemark(CharUtil.IsoToGbk(remark));// 备注
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
