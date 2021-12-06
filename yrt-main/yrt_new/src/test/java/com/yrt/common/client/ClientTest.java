package com.yrt.common.client;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.dict.SystemType;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ClientTest {

	@Autowired HttpAPIService service;
	@Autowired PushService pushService;
	
//	@Test
	public void send() {
		String str = null;
		try {
			str = service.doGet("http://www.baidu.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(str);
	}
	
	@Test
	public void sendApplyTest() {
		pushService.sendApplyMessage(2L, SystemType.HOSPITAL.value(), "内蒙古第一人民医院", "SL202009170008");
	}
	
	@Test
	public void sendOrderTest() {
		pushService.sendOrderMessage(2L, SystemType.DEALER.value(), "内蒙古第一人民医院", "XS202004140007");
	}
	
	@Test
	public void sendPutoutTest() {
		pushService.sendPutoutMessage(2L, SystemType.HOSPITAL.value(), "内蒙古第一人民医院", "CK202009180002");
	}
}
