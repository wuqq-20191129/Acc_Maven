package com.goldsign.commu.app.message;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.vo.ParaGenDtl;
import com.goldsign.commu.frame.vo.ParaInformDtl;

/**
 * 
 * @author zhangjh
 */
public class ConstructMessage02 extends ConstructMessageBase {

	private final String IsParaInformMsg = "1";
	private static Logger logger = Logger.getLogger(ConstructMessage02.class
			.getName());
        
        public ConstructMessage02() {
            super();
            this.messageType = "02";
            this.messageRemark = "参数版本差异";
        }

	public byte[] constructMessage(Vector<ParaGenDtl> paraGenDtlV) {
		logger.info("--构建02消息开始--");
                
		try {
			initMessage();
			AddStringToMessage("02", 2);
			AddBcdToMessage(DateHelper.currentTodToString(), 7);
			AddIntToMessage(paraGenDtlV.size(), 1);
			Iterator it = paraGenDtlV.iterator();
			while (it.hasNext()) {
				ParaGenDtl paraGenDtl = (ParaGenDtl) (it.next());
				AddBcdToMessage(paraGenDtl.getParmTypeId(), 2);
				AddBcdToMessage(paraGenDtl.getVerNum(), 5);
			}
			byte[] msg = trimMessage();
			logger.info("--成功构建02消息--,消息长度是： " + msg.length);
			return msg;
		} catch (Exception e) {
			logger.error("构建02消息失败! ", e);
			return new byte[0];
		}
	}

	/*
	 * 
	 */
	public void constructAndSend(Vector<ParaInformDtl> paraInfoDtlV,
			Vector<ParaGenDtl> paraGenDtlV) {
		byte[] msg = constructMessage(paraGenDtlV);
		Iterator it = paraInfoDtlV.iterator();
		while (it.hasNext()) {
			ParaInformDtl paraInfoDtl = (ParaInformDtl) (it.next());
			String ip = (String) (FrameCodeConstant.ALL_LCC_IP).get(paraInfoDtl
					.getLineId());
			if (ip != null) {
				logger.info("发送的02消息：" + Arrays.toString(msg));
				pushQueue(ip, msg, IsParaInformMsg, paraInfoDtl.getWaterNo(),
						paraInfoDtl.getLineId(), paraInfoDtl.getStationId());
			}
			logger.info("成功发送02消息到" + ip);
		}
	}

	public void constructAndSend(String ip, Vector<ParaGenDtl> paraGenDtlV) {
		if (ip != null) {
			sendToJms(ip, constructMessage(paraGenDtlV));
		}
		logger.info("成功发送02消息到" + ip);
	}

	public void constructAndSend(String ip, Vector<ParaGenDtl> paraGenDtlV,
			String lineId, String stationId) {
		if (ip != null) {
			sendToJms(ip, constructMessage(paraGenDtlV), lineId, stationId);
		}
		logger.info("成功发送02消息到" + ip);
	}
}
