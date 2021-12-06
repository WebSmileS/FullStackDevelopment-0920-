package com.yrt.project.modular.warehouse;

import static org.junit.Assert.assertEquals;

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

import com.yrt.common.dict.PrdType;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.domain.ProductUnitConvertKey;
import com.yrt.project.modular.warehouse.mapper.InventoryLockMapper;

import lombok.Data;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class InventoryLockTest {

	private static final Logger logger = LoggerFactory.getLogger(InventoryLockTest.class);

	@Autowired
	private InventoryLockMapper dao;

	private final long hospitalOrgId = 1;
	private final long dealerOrgId = 2;

	// vendor产品
	private final long prdVendor = 1;
	private final long prdId = 1;
	private final long prdSpec = 1;

	private final long prdUnit = 6;// unit: scale=30
	private final BigDecimal prdScale = BigDecimal.valueOf(30);

	// dealer 自建
	private final long dprdVendor = dealerOrgId;
	private final long dprdId = 2;
	private final long dprdSpec = 1;

	private final long dprdUnit = 2;// unit: scale=96
	private final BigDecimal dprdScale = BigDecimal.valueOf(96);

	private final String batchNumber = "test_batch"; // ""

	private InventoryKey keyDealerP = InventoryKey.ofProductForDealer(dealerOrgId, null, prdVendor, prdId, prdSpec,
			batchNumber);
	private InventoryKey keyDealerDp = InventoryKey.ofDealerProductForDealer(dealerOrgId, null, dprdVendor, dprdId,
			dprdSpec, batchNumber);

	private InventoryKey keyHospitalP = InventoryKey.ofProductForHospital(hospitalOrgId, 1L, // warehouse
			prdVendor, prdId, prdSpec, batchNumber);

	private InventoryKey keyHospitalDp = InventoryKey.ofDealerProductForHospital(hospitalOrgId, 1L, // warehouse
			dprdVendor, dprdId, dprdSpec, batchNumber);

	ProductUnitConvertKey unitKeyP = ProductUnitConvertKey.of(PrdType.VENDOR_PRD, prdVendor, prdId, prdSpec, prdUnit);// scale=30
	ProductUnitConvertKey unitKeyDP = ProductUnitConvertKey.of(PrdType.DELEAR_PRD, dprdVendor, dprdId, dprdSpec,
			dprdUnit);// scale=96

	/**
	 * test success or not: dependency current dev database data.
	 */
	@Test
	// @Transactional(propagation=Propagation.NOT_SUPPORTED) //@Rollback(false)
	public void lock() {
		LockKeyAndUnit[] keys = new LockKeyAndUnit[] { //
				LockKeyAndUnit.of(keyDealerP, prdUnit, prdScale), //
				LockKeyAndUnit.of(keyDealerDp, dprdUnit, dprdScale), //
				LockKeyAndUnit.of(keyHospitalP, prdUnit, prdScale), //
				LockKeyAndUnit.of(keyHospitalDp, dprdUnit, dprdScale) //
		};
		for (LockKeyAndUnit ku : keys) {
			InventoryKey key = ku.key;

			//
			BigDecimal initLocked = dao.getLockedQuantity(key);

			BigDecimal quantity = BigDecimal.valueOf(10);

			// without unit convert
			dao.lockInventory(key, quantity);
			BigDecimal lockedQuantity = dao.getLockedQuantity(key);

			logger.info("doLock11: {}, quantity={}, curLocked={}", key, quantity, lockedQuantity);
			assertEquals(quantity, lockedQuantity.subtract(initLocked));

			dao.lockInventory(key, quantity); // two
			lockedQuantity = dao.getLockedQuantity(key);
			logger.info("doLock12: {}, quantity={}, curLocked={}", key, quantity, lockedQuantity);
			assertEquals(quantity.multiply(BigDecimal.valueOf(2)), lockedQuantity.subtract(initLocked));// twice

			// restore
			dao.unlockInventory(key, lockedQuantity.subtract(initLocked)); // restore
			lockedQuantity = dao.getLockedQuantity(key);
			assertEquals(initLocked, lockedQuantity);

			// with unit convert
			dao.lockInventory(key, ku.unitId, quantity);
			lockedQuantity = dao.getLockedQuantity(key);
			logger.info("doLock21: {}, quantity={}, curLocked={}", key, quantity, lockedQuantity);
			assertEquals(quantity.multiply(ku.scale), lockedQuantity.subtract(initLocked));

			dao.unlockInventory(key, lockedQuantity.subtract(initLocked)); // restore
			lockedQuantity = dao.getLockedQuantity(key);
			assertEquals(initLocked, lockedQuantity);
		}
	}

	@Test
	public void getLocked() {

		logger.info("invLocked: {}, ret={}", keyDealerP, dao.getLockedQuantity(keyDealerP));
		logger.info("invLocked: {}, ret={}", keyDealerDp, dao.getLockedQuantity(keyDealerDp));

		//selectLockedQuantity: maybe null
		logger.info("invLocked: {}, ret={}", keyHospitalP, dao.selectLockedQuantity(keyHospitalP));
		logger.info("invLocked: {}, ret={}", keyHospitalDp, dao.selectLockedQuantity(keyHospitalDp));
	}

	@Test
	public void getUnitScale() {
		logger.info("unitScale: {}, ret={}", unitKeyP, dao.getUnitToMinUnitScale(unitKeyP));
		logger.info("unitScale: {}, ret={}", unitKeyDP, dao.getUnitToMinUnitScale(unitKeyDP));
	}

	@Data(staticConstructor = "of")
	static class LockKeyAndUnit {
		final InventoryKey key;
		final long unitId;
		final BigDecimal scale;
	}

}
