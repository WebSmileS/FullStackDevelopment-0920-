package com.yrt;

import com.yrt.project.api.common.service.VoucherNumberService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YrtApplicationTests {

	@Autowired
	private VoucherNumberService numberUtils;

	@Test
	public void contextLoads() {
		VoucherNumberService.OrderNumberPair pair = numberUtils.getOrderNumberPair(1, 2, 2, 4);
		System.out.println(pair.getPo().get());
	}

}
