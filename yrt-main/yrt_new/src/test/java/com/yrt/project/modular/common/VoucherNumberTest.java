package com.yrt.project.modular.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.common.service.VoucherNumberService.OrderNumberPair;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class VoucherNumberTest {

	private static final Logger logger = LoggerFactory.getLogger(VoucherNumberTest.class);

	@Autowired
	private VoucherNumberService service;

	@Test
	public void test() {
		logger.info("ht1: {}", service.getMaxSn(VoucherType.CONTRACT, SystemType.DEALER, 1L));
		logger.info("ht2: {}", service.getMaxSn(VoucherType.CONTRACT, SystemType.DEALER, 1L));
		logger.info("cg1: {}", service.getMaxSn(VoucherType.ORDER_PO, SystemType.DEALER, 2L));
		logger.info("cg2: {}", service.getMaxSn(VoucherType.ORDER_PO, SystemType.DEALER, 2L));
		
		OrderNumberPair pair = service.getOrderNumberPair(1, 1, 2, 2);
		logger.info("po: {}", pair.getPo().get());
		logger.info("so1: {}", pair.getSo().get());
		logger.info("so2: {}", pair.getSo().get());
	}
}
