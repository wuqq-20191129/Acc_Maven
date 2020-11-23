package com.goldsign.commu.app.parser;

import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.parser.FileRecordParserBase;
import com.goldsign.commu.frame.vo.FileRecordHead;

public class FileRecordParserHead extends FileRecordParserBase {
	@Override
	public Object parse(String line, FileRecordAddVo lineAdd)
			throws RecordParseException {
		FileRecordHead r = new FileRecordHead();
		char[] b = null;
		int offset = 0;
		int len = 2;
		try {
			b = this.getLineCharFromFile(line);
			r.setLineId(this.getCharString(b, offset, len));// 线路代码
			offset += 2;
			
			len = 7;
			r.setOpenTime(this.getBcdString(b, offset, len));// 文件打开时间
			offset += 7;

			len = 7;
			r.setCloseTime(this.getBcdString(b, offset, len));// 文件关闭时间
			offset += 7;

			r.setSeq(this.getShort(b, offset));// 文件序列号
			offset += 2;

			r.setRowCount(this.getLong(b, offset));// 文件记录数

		} catch (Exception ex) {
			throw new RecordParseException("解析文件头出现异常.");
		}
		return r;

	}

	@Override
	public void handleMessage(HandleMessageBase msg) {
	}
}
