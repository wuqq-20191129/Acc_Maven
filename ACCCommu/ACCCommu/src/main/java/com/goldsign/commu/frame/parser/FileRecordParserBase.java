package com.goldsign.commu.frame.parser;

import java.util.HashMap;
import java.util.Vector;

import com.goldsign.commu.app.vo.FileRecordAddVo;
import com.goldsign.commu.frame.constant.FrameFileHandledConstant;
import com.goldsign.commu.frame.exception.RecordParseException;
import com.goldsign.commu.frame.handler.HandleBase;
import com.goldsign.commu.frame.vo.FileRecordBase;

/**
 *
 * @author zhangjh
 */
public abstract class FileRecordParserBase extends HandleBase {

    public abstract Object parse(String line, FileRecordAddVo lineAdd) throws RecordParseException;

    protected void addCommonInfo(FileRecordBase r, FileRecordAddVo lineAdd) {
        //获取附加信息
        r.setBalanceWaterNo(lineAdd.getWaterNo());
        r.setFileName(lineAdd.getFileName());
        r.setCheckFlag(FrameFileHandledConstant.RECORD_UNCHECKED);
    }

    protected void addSubRecords(HashMap subRecords, String tradeType, String tradeTypeSufix, Vector subRecordUnit) {
        String tradeTypeAll = tradeType + tradeTypeSufix;
        Vector v;
        if (subRecords.containsKey(tradeTypeAll)) {
            v = (Vector) subRecords.get(tradeTypeAll);
            v.addAll(subRecordUnit);
            return;
        }
        subRecords.put(tradeTypeAll, subRecordUnit);
    }
}