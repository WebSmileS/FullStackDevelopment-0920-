package com.yrt.project.warehouse;

import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.RefVoucherType;
import com.yrt.project.api.business.warehouse.GrnController;
import com.yrt.project.api.business.warehouse.service.ArrivalRecordService;
import com.yrt.project.api.business.warehouse.service.IGrnService;
import com.yrt.project.modular.warehouse.InventoryLockTest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@WebMvcTest(GrnController.class)
public class GrnServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(GrnServiceTest.class);
	
	@Autowired private MockMvc mvc;

	@Autowired
	private IGrnService service;

	@Test
	public void putin() {
		given(service.putin(null, null, null));
//		mvc.perform(put("http://localhost/v1/business/grn/putin"));
		
	}

}
