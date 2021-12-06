package com.yrt.project.warehouse;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.exception.InventoryException;
import com.yrt.project.api.business.warehouse.service.InventoryLockService;
import com.yrt.project.modular.warehouse.InventoryLockTest;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.mapper.InventoryLockMapper;
import com.yrt.project.modular.warehouse.mapper.InventoryMinUnitMapper;

import lombok.Data;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class InventoryLockServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(InventoryLockTest.class);

	@Autowired
	private InventoryLockService service;

	@SuppressWarnings("unused")
	@Autowired
	private InventoryLockMapper lockDao;

	@SuppressWarnings("unused")
	@Autowired
	private InventoryMinUnitMapper muInventory;

	private final long hospitalOrgId = 2;
	private final long warehouseId = 1;
	private final long dealerOrgId = 2;

	// vendor产品
	private final long prdVendor = 2;
	private final long prdId = 5;
	private final long prdSpec = 3;

	// dealer 自建
	private final long dprdVendor = dealerOrgId;
	private final long dprdId = 2;
	private final long dprdSpec = 1;

	private final String batchNumber = "2020042201"; // virtual "PRE_LOCK"; //"2020042201"; ""

	private InventoryKey keyDealerP = InventoryKey.ofProductForDealer(dealerOrgId, null, prdVendor, prdId, prdSpec,
			batchNumber);
	private InventoryKey keyDealerDp = InventoryKey.ofDealerProductForDealer(dealerOrgId, null, dprdVendor, dprdId,
			dprdSpec, batchNumber);

	private InventoryKey keyHospitalP = InventoryKey.ofProductForHospital(hospitalOrgId, warehouseId, prdVendor,
			prdId, prdSpec, batchNumber);

	private InventoryKey keyHospitalDp = InventoryKey.ofDealerProductForHospital(hospitalOrgId, warehouseId,
			dprdVendor, dprdId, dprdSpec, batchNumber);

	/**
	 * test with same lock-key/query-key. (Virutal batchNumber "PRE_LOCK" always
	 * faile)
	 * <p>
	 * test success or not: dependency current dev database data.
	 */
	@Test
	public void test1() {
		InventoryKey[] keys = new InventoryKey[] { //
				keyDealerP, //
				keyDealerDp, keyHospitalP, keyHospitalDp };

		BigDecimal quantity = BigDecimal.ONE;
		for (InventoryKey key : keys) {
			BigDecimal available = service.getAvailableInventoryQuantity(key);

			BigDecimal requestInc = quantity;

			logger.info("available={}, request={}, key={}", available, requestInc, key);
			try {
				service.lockInventory(key, null, quantity);
				BigDecimal newAvailable = service.getAvailableInventoryQuantity(key);
				logger.info("lock success1:: dec={}, requestInc={}", available.subtract(newAvailable), requestInc);
			} catch (Exception e) {
				logger.info("No enough available inventory: {}", e.getMessage());
			} catch (InventoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * test with different lock-key/query-key(query key ignore warehouse &
	 * batchNumber). (Virutal batchNumber "PRE_LOCK" may lock success
	 * <p>
	 * test success or not: dependency current dev database data.
	 */
	@Test
	public void test2() {
		InventoryKey[] keys = new InventoryKey[] { //
				keyDealerP, //
				keyDealerDp, keyHospitalP, keyHospitalDp };

		BigDecimal quantity = BigDecimal.ONE;
		for (InventoryKey key : keys) {
			// 不区分仓库和批号
			InventoryKey queryKey = InventoryKey.of(key.getOrgType(), key.getPrdType(), key.getOrgId(), //
					null, // warehouse
					key.getVendorId(), key.getProductId(), key.getSpecificationId(), //
					null // batch_number
			);
			BigDecimal available = service.getAvailableInventoryQuantity(queryKey);

			BigDecimal requestInc = quantity;

			logger.info("available={}, request={}, key={}", available, requestInc, key);
			try {
				try {
					service.lockInventory(key, queryKey, quantity);
				} catch (InventoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BigDecimal newAvailable = service.getAvailableInventoryQuantity(queryKey);
				logger.info("lock success2:: dec={}, requestInc={}", available.subtract(newAvailable), requestInc);
			} catch (Exception e) {
				logger.info("No enough available inventory: {}", e.getMessage());
			}
		}
	}

	@Data(staticConstructor = "of")
	static class LockKeyAndUnit {
		final InventoryKey key;
		final long unitId;
		final BigDecimal scale;
	}

}
