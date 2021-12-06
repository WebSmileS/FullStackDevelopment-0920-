package com.yrt.common.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.yrt.common.constant.MessageConstant;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageUtilsTests {

	@Test
	public void sendMessage() {
		String phoneMsg="#useraccount#=admin&#password#=a1234567";
		MessageUtils.sendMessage("电话号码", MessageConstant.TEMP_ADUIT_PASS, phoneMsg);
	}

}