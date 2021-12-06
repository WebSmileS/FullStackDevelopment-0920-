package com.yrt.project.warehouse;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.RefVoucherType;
import com.yrt.project.api.business.warehouse.service.ArrivalRecordService;
import com.yrt.project.modular.warehouse.InventoryLockTest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class ArrivalRecordServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(InventoryLockTest.class);

	@Autowired
	private ArrivalRecordService service;

	@Test
	public void test1() {
		assertFalse(service.isExistArrivalRecord(1L, RefVoucherType.GRANT));
		
	}

}
