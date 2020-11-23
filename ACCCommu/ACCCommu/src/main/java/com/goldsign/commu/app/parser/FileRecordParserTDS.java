package com.goldsign.commu.app.parser;

import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.app.vo.InfoDistributeTk;
import com.goldsign.commu.frame.constant.FrameTicketConstant;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.parser.FileRecordParserBase;

/**
 * 解析配票文件
 * 
 * @author zhangjh
 * 
 */
public class FileRecordParserTDS extends FileRecordParserBase {

	@Override
	public Object parse(String line, FileRecordAddVo lineAdd)
			throws RecordParseException {
		InfoDistributeTk r = new InfoDistributeTk();
		char[] b = null;
		int offset = 0;

		try {
			b = this.getLineCharFromFile(line);

			int len = FrameTicketConstant.LEN_LINE;
			r.setDeptId(getCharString(b, offset, len));// 站点代码
			offset += len;

			len = FrameTicketConstant.LEN_CARD_TYPE;
			r.setTicketTypeId(getBcdString(b, offset, len));// 票卡类型
			offset += len;

			len = FrameTicketConstant.LEN_VALUE;
			r.setValue(getLong(b, offset));// 面值
			offset += len;

			len = FrameTicketConstant.LEN_EFFECTIVE;
			r.setValidDate(getBcdString(b, offset, len));// 有效期
			offset += len;

			len = FrameTicketConstant.LEN_MODEL;
			r.setModel(getCharString(b, offset, len));// 限制使用模式
			offset += len;

			len = FrameTicketConstant.LEN_IN_STATION;
			r.setEntryStation(getBcdString(b, offset, len));// 限制进站代码
			offset += len;

			len = FrameTicketConstant.LEN_EXIT_STATION;
			r.setExitStation(getBcdString(b, offset, len));// 限制出站代码
			offset += len;

			len = FrameTicketConstant.LEN_QUANTITY;
			r.setQuantity(getLong(b, offset));// 数量
			offset += len;

			len = FrameTicketConstant.LEN_DIST_DATE;
			r.setDistDate(getBcdString(b, offset, len));// 配票时间

		} catch (Exception ex) {
			throw new RecordParseException("解析文件记录出现异常.");
		}
		return r;

	}

	@Override
	public void handleMessage(HandleMessageBase msg) {

	}

}
