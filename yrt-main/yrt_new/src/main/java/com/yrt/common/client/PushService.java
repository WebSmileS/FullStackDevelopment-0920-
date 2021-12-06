package com.yrt.common.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yrt.framework.config.YrtConfig;

@Service
public class PushService {

//	private static String PUSH_ADDRESS = "http://192.168.50.124/v1/notify";
//	private static String NTFY_BODY_DELIVERY = "%s已经发货，编号: %s";
//	private static String NTFY_BODY_APPLY    = "%s有新的耗材申领请求，申领单编号: %s";
//	private static String NTFY_BODY_ORDER    = "%s向您发送了新的订单，请及时处理，订单号: %s";
	
	@Autowired HttpAPIService service;
	@Autowired YrtConfig config;
	
	public void sendPutoutMessage(Long oid, int systemType, String orgName, String sn) {
		PushBody body = PushBody.of(oid, systemType, orgName, sn);
		body.setType(1);
		String json = JSONObject.toJSONString(body);
		send(json);
	}
	
	public void sendApplyMessage(Long oid, int systemType, String orgName, String sn) {
		PushBody body = PushBody.of(oid, systemType, orgName, sn);
		body.setType(2);
		String json = JSONObject.toJSONString(body);
		send(json);
	}

	public void sendOrderMessage(Long oid, int systemType, String orgName, String sn) {
		PushBody body = PushBody.of(oid, systemType, orgName, sn);
		body.setType(3);
		String json = JSONObject.toJSONString(body);
		send(json);
	}
	
	public void sendRegOrgMessage(int systemType, String orgName) {
		PushBody body = new PushBody();
		body.setOID(0L);//平台ID
		body.setSystemType(0);
		body.setObjectID(String.valueOf(systemType));
		body.addParams(orgName);
		body.setType(4);
		String json = JSONObject.toJSONString(body);
		send(json);
	}
	
	private void send(String json) {
		try {
			service.doPostWithJson(config.getPushAddress(), json);
		} catch (Exception e) {
			e.printStackTrace();
//			throw new BusinessException("推送消息出错，原因：" + e.getMessage());
		}
	}

	public void sendAllotMessage(Long hospital_inner_sn, int systemType, String sn) {
		PushBody body = PushBody.of(hospital_inner_sn, systemType, sn);
		body.setType(2);
		String json = JSONObject.toJSONString(body);
		send(json);
	}
}
