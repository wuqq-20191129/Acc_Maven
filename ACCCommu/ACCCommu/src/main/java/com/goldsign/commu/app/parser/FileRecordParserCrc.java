/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.parser;

import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.message.HandleMessageBase;
import com.goldsign.commu.frame.parser.FileRecordParserBase;
import com.goldsign.commu.frame.vo.FileRecordCrc;

/**
 * 
 * @author Administrator
 */
public class FileRecordParserCrc extends FileRecordParserBase {

	@Override
	public Object parse(String line, FileRecordAddVo lineAdd)
			throws RecordParseException {
		FileRecordCrc r = new FileRecordCrc();
		// byte[] b ;//line.getBytes();
		char[] b;
		int offset = 0;
		int len = 4;
		try {
			b = this.getLineCharFromFile(line);

			r.setCrcFlag(this.getCharString(b, offset, len));// CRC:标识
			offset += 4;
			len = 8;
			r.setCrc(this.getCharString(b, offset, len));// CRC值

		} catch (Exception ex) {
			throw new RecordParseException(ex.getMessage());
		}

		return r;

	}

	@Override
	public void handleMessage(HandleMessageBase msg) {
	}
}