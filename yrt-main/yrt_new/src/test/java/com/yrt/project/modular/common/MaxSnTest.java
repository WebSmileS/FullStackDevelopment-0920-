package com.yrt.project.modular.common;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.yrt.project.api.common.service.MaxSnService;
import com.yrt.project.modular.common.mapper.MaxSnMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class MaxSnTest {

	private static final Logger logger = LoggerFactory.getLogger(MaxSnTest.class);
	
	private static final long ORG_ID = 10000L;

	@Autowired
	private MaxSnMapper dao;
	
	@Autowired
	private MaxSnService service;
	
//	@Test
	// @Transactional(propagation=Propagation.NOT_SUPPORTED)
	public void testDao() {
		String tableName = "voucher_sn";
		Map<String, Object> keys = new HashMap<>();
		keys.put("system_type", SystemType.HOSPITAL.value());
		keys.put("belong", ORG_ID);// org
		keys.put("voucher_type", 20); // VOUCHER_TYPE_INSPECTION=12
		keys.put("sn_date", new Date()); // dbserver convert it to date(without time part)

		long sn1 = dao.getMaxSn(tableName, keys);

		long sn2 = dao.getMaxSn(tableName, keys, 10);

		logger.info("sn1: {}, sn2: {}", sn1, sn2);

		assertEquals(sn1, sn2 - 10);
	}
	
//	@Test
	public void testService() {
		String tableName = "voucher_sn";
		Map<String, Object> keys = new HashMap<>();
		keys.put("system_type", SystemType.HOSPITAL.value());
		keys.put("belong", ORG_ID);// org
		keys.put("voucher_type", 20); // VOUCHER_TYPE_INSPECTION=12
		keys.put("sn_date", new Date()); // dbserver convert it to date(without time part)

		long sn1 = service.getMaxSn(tableName, keys);

		List<Long> list = service.getMaxSn(tableName, keys, 10);

		logger.info("sn1: {}, SNs: {}", sn1, list);

		assertEquals(sn1, list.get(0)-1);
	}
	
	@Test
	public void testService1() {
		String tableName = "dealer_warehouse_model_inner_sn_max";
		Map<String, Object> keys = new HashMap<>();
		keys.put("dealer_inner_sn", 56);

		long sn1 = service.getMaxSn(tableName, keys);

		logger.info("sn1: {}", sn1);

		assertEquals(sn1, 13);
	}
}
