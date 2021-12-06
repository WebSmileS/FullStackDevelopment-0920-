package com.yrt.project.api.business.warehouse.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.constant.Constants;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.InventoryException;
import com.yrt.common.exception.MoreThanInventoryException;
import com.yrt.common.utils.Utils;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.domain.ProductUnitConvertKey;
import com.yrt.project.modular.warehouse.mapper.InventoryLockMapper;
import com.yrt.project.modular.warehouse.mapper.InventoryMinUnitMapper;

/**
 * 库存锁定(内置库存检查).
 * <p>
 * <strong>Notes:</strong> 本Service的 输入输出【数量】
 * 为最终用户所见的<strong>小数</strong>，而<strong>不是</strong>数据库存储的化整数.
 */
@Service
public class InventoryLockService {

	@Autowired
	private InventoryLockMapper locker;

	@Autowired
	private InventoryMinUnitMapper muInventory;

	/**
	 * 库存锁定(指定单位), 库存余量不足 抛 运行时错误。
	 * 
	 * <p>
	 * 本方法会检查库存余量, client不必预先查库存余量(避免重复计算).
	 * @param lockKey
	 *            库存锁定键
	 * @param queryKey
	 *            库存余量校验查询 【当前锁定量/库存量】的条件 (可以与 lockKey不同--
	 *            warehouse/batch_number可为空)。NULL与lockKey相同.
	 * @param unitId
	 * @param quantity
	 *            以unitId为单位的数量(最终用户所见的<strong>小数</strong>; <strong>正数</strong>)
	 */
	@Transactional
	public void lockInventory(InventoryKey lockKey, InventoryKey queryKey, Long unitId, BigDecimal quantity) throws InventoryException {
		InventoryKey specLockKey = InventoryKey.of(lockKey);
		BigDecimal intQuantity = quantity.multiply(Constants.QUANTITY_INT_SCALE);
		if (intQuantity.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		Utils.assertTrue(intQuantity.compareTo(BigDecimal.ZERO) > 0, "数量不能为负");
		if (queryKey == null) {
			queryKey = InventoryKey.of(specLockKey);
		}

		BigDecimal requestQuantity = locker.getUnitToMinUnitScale(ProductUnitConvertKey.of(specLockKey.getPrdType(),
				specLockKey.getVendorId(), specLockKey.getProductId(), specLockKey.getSpecificationId(), unitId))
				.multiply(intQuantity); // convert to min-unit

		BigDecimal curLocked = locker.getLockedQuantity(queryKey);//获取当前仓库指定批号的锁定量
		//2020-08-21 只有PRE_LOCK的批号代表锁定库存总数
		if(Constants.PRE_LOCK_BATCH_NUMBER.equalsIgnoreCase(queryKey.getBatchNumber())) {
			queryKey.setBatchNumber(null);
			if(!queryKey.getOrgType().equals(SystemType.HOSPITAL)) {
				specLockKey.setWarehouseId(-1L);
			}
		}
		BigDecimal curInventory = muInventory.getInventoryQuantity(queryKey);
		BigDecimal r = curInventory.subtract(curLocked);// INT mode
		if(r.longValue() < requestQuantity.longValue()) {
			throw new InventoryException(quantity, requestQuantity.divide(Constants.QUANTITY_INT_SCALE), r);
		}

		locker.lockInventory(specLockKey, requestQuantity);
	}

	/**
	 * 库存锁定(最小单位), 库存余量不足 抛 运行时错误。
	 * <p>
	 * 本方法会检查库存余量, client不必预先查库存余量(避免重复计算).
	 * @param lockKey
	 *            库存锁定键
	 * @param queryKey
	 *            库存余量校验查询 【当前锁定量/库存量】的条件 (可以与 lockKey不同--
	 *            warehouse/batch_number可为空)。
	 * @param minUnitQuantity
	 *            最小单位表示的数量(最终用户所见的<strong>小数</strong>; <strong>正数</strong>)
	 */
	@Transactional
	public void lockInventory(InventoryKey lockKey, InventoryKey queryKey, BigDecimal minUnitQuantity) throws InventoryException {
		InventoryKey specLockKey = InventoryKey.of(lockKey);
		BigDecimal intQuantity = minUnitQuantity.multiply(Constants.QUANTITY_INT_SCALE);
		if (intQuantity.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		Utils.assertTrue(intQuantity.compareTo(BigDecimal.ZERO) > 0, "数量不能为负");
		if (queryKey == null) {
			queryKey = specLockKey;
		}
		//2020-08-21 只有PRE_LOCK的批号代表锁定库存总数
		if(Constants.PRE_LOCK_BATCH_NUMBER.equalsIgnoreCase(queryKey.getBatchNumber())) {
			queryKey.setBatchNumber(null);
			if(!queryKey.getOrgType().equals(SystemType.HOSPITAL)) {
				specLockKey.setWarehouseId(-1L);
			}
		}
		BigDecimal requestQuantity = intQuantity; // no convert

		BigDecimal r = muInventory.getInventoryQuantity(queryKey).subtract(locker.getLockedQuantity(queryKey));// INT
																												// mode
		if(r.longValue() < requestQuantity.longValue()) {
			throw new InventoryException(minUnitQuantity, requestQuantity.divide(Constants.QUANTITY_INT_SCALE), r);
		}
//		Utils.assertTrue(r.longValue() >= requestQuantity.longValue(),
//				"库存不足: [请求锁定量(最小单位): " + minUnitQuantity + ", 库存余量(最小单位): " + r.divide(Constants.QUANTITY_INT_SCALE) + "]");

		locker.lockInventory(specLockKey, requestQuantity);
	}

	/**
	 * 解锁库存(按指定单位)
	 * 
	 * @param key
	 * @param unitId
	 * @param quantity
	 *            以unitId为单位 的数量(最终用户所见的<strong>小数</strong>; <strong>正数</strong>)
	 */
	@Transactional
	public void unlockInventory(InventoryKey key, Long unitId, BigDecimal quantity) {
		InventoryKey specKey = InventoryKey.of(key);
		BigDecimal intQuantity = quantity.multiply(Constants.QUANTITY_INT_SCALE);
		if (intQuantity.longValue() == 0) {
			return;
		}
		if(Constants.PRE_LOCK_BATCH_NUMBER.equalsIgnoreCase(key.getBatchNumber())) {
			if(!specKey.getOrgType().equals(SystemType.HOSPITAL)) {
				specKey.setWarehouseId(-1L);
			}
		}
		Utils.assertTrue(intQuantity.longValue() > 0, "数量不能为负");
		locker.unlockInventory(specKey, unitId, intQuantity);
	}

	/**
	 * 解锁库存(最小单位)
	 * @param key
	 * @param minUnitQuantity
	 *            最小单位表示的数量(最终用户所见的<strong>小数</strong>; <strong>正数</strong>)
	 */
	@Transactional
	public void unlockInventory(InventoryKey key, BigDecimal minUnitQuantity) {
		unlockInventory(key, minUnitQuantity, false);
	}
	
	/**
	 * 解锁库存(最小单位)
	 * @param key
	 * @param minUnitQuantity
	 *            最小单位表示的数量(最终用户所见的<strong>小数</strong>; <strong>正数</strong>)
	 */
	@Transactional
	public void unlockInventory(InventoryKey key, BigDecimal minUnitQuantity, Boolean isRed) {
		InventoryKey specKey = InventoryKey.of(key);
		BigDecimal intQuantity = minUnitQuantity.multiply(Constants.QUANTITY_INT_SCALE);
		if (intQuantity.longValue() == 0) {
			return;
		}
		if(Constants.PRE_LOCK_BATCH_NUMBER.equalsIgnoreCase(specKey.getBatchNumber())) {
			if(!specKey.getOrgType().equals(SystemType.HOSPITAL)) {
				specKey.setWarehouseId(-1L);
			}
		}
		if(!isRed) {
			Utils.assertTrue(intQuantity.longValue() > 0, "数量不能为负");
		}
		locker.unlockInventory(specKey, intQuantity);
	}

	/**
	 * 当前锁定量(最小单位)
	 * 
	 * @param key
	 * @return 最小单位锁定量(小数)
	 */
	public BigDecimal getLockedInventory(InventoryKey key) {
		if(Constants.PRE_LOCK_BATCH_NUMBER.equalsIgnoreCase(key.getBatchNumber())) {
			if(!key.getOrgType().equals(SystemType.HOSPITAL)) {
				key.setWarehouseId(-1L);
			}
		}
		return locker.getLockedQuantity(key).divide(Constants.QUANTITY_INT_SCALE);
	}

	/**
	 * 当前库存量(最小单位)
	 * 
	 * @param key
	 * @return 最小单位库存量(小数)
	 */
	private BigDecimal getInventoryQuantity(InventoryKey key) {
		if(Constants.PRE_LOCK_BATCH_NUMBER.equalsIgnoreCase(key.getBatchNumber())) {
			if(!key.getOrgType().equals(SystemType.HOSPITAL)) {
				key.setWarehouseId(-1L);
			}
		}
		return muInventory.getInventoryQuantity(key).divide(Constants.QUANTITY_INT_SCALE);
	}
	
	/**
	 * 当前 可用库存余量(最小单位)
	 * <p>等于: {@link #getInventoryQuantity(InventoryKey)} - {@link #getLockedInventory(InventoryKey)}
	 * @param key
	 * @return 最小单位库存量(小数)
	 */
	public BigDecimal getAvailableInventoryQuantity(InventoryKey key) {
		if(Constants.PRE_LOCK_BATCH_NUMBER.equalsIgnoreCase(key.getBatchNumber())) {
			if(!key.getOrgType().equals(SystemType.HOSPITAL)) {
				key.setWarehouseId(-1L);
			}
		}
		return getInventoryQuantity(key).subtract(getLockedInventory(key));
	}

	/**
	 * 指定产品全局总可用量>=当前库存量返回当前库存量，否则返回总可用量(最小单位)
	 * <p>等于: {@link #getInventoryQuantity(InventoryKey)} - {@link #getLockedInventory(InventoryKey)}
	 * @param key
	 * @return 最小单位库存量(小数)
	 */
	public BigDecimal getInventoryTotalBannce(InventoryKey key) {
		InventoryKey queryKey = InventoryKey.of(key);
		queryKey.setBatchNumber(null);
		BigDecimal curInventoryQuantity = getInventoryQuantity(queryKey);//当前库存总量
		BigDecimal lockedQuantity = getLockedInventory(key);//总锁定量
		BigDecimal surplus;
		if(!queryKey.getOrgType().equals(SystemType.HOSPITAL)) {
			queryKey.setWarehouseId(null);
			BigDecimal total = getInventoryQuantity(queryKey);//总库存
			surplus = total.subtract(lockedQuantity);//总库存-总锁定量=总可用量
		}else {
			surplus = curInventoryQuantity.subtract(lockedQuantity);//当前库存总量-当前库存锁定量=当前库存可用量
		}
		//总可用量>=当前库存量返回当前库存量，否则返回总可用量
		if(surplus.compareTo(curInventoryQuantity) >= 0) {
			return curInventoryQuantity;
		}
		return surplus;
	}
	
	public BigDecimal getAvailablelAllInventory(InventoryKey key) {
		InventoryKey queryKey = InventoryKey.of(key);
		queryKey.setBatchNumber(null);
		BigDecimal curInventoryQuantity = getInventoryQuantity(queryKey);//当前库存总量
		BigDecimal lockedQuantity = getLockedInventory(key);//总锁定量
		BigDecimal surplus = curInventoryQuantity.subtract(lockedQuantity);
		if(queryKey.getOrgType().equals(SystemType.HOSPITAL)) {
			surplus = curInventoryQuantity.subtract(lockedQuantity);//当前库存总量-当前库存锁定量=当前库存可用量
		}
		return surplus;
	}
	
	/**
	 * 指定产品和批号的总可用量>=当前库存量返回当前库存量，否则返回总可用量(最小单位)
	 * @param key
	 * @return 最小单位库存量(小数)
	 */
	public BigDecimal getInventoryBatchAndWarehouseBannce(InventoryKey key, BigDecimal initQuantity) {
		InventoryKey queryKey = InventoryKey.of(key);
		BigDecimal curInventoryQuantity = getInventoryQuantity(key);//当前仓库库存量
		BigDecimal lockedQuantity = getLockedInventory(key);//当前仓库锁定量
		BigDecimal quantity = curInventoryQuantity.subtract(lockedQuantity);//当前仓库可用量
		BigDecimal surplus = BigDecimal.ZERO;
		if(!queryKey.getOrgType().equals(SystemType.HOSPITAL)) {
			queryKey.setWarehouseId(null);
			BigDecimal total = getInventoryQuantity(queryKey);//总库存
			surplus = total.subtract(lockedQuantity);//总库存-总锁定量=总可用量
		}else {
			surplus = curInventoryQuantity.subtract(lockedQuantity);//当前库存总量-当前库存锁定量=当前库存可用量
		}
		//总可用量>=当前库存量返回当前库存量，否则返回总可用量
		if(surplus.compareTo(quantity) >= 0) {
			return quantity = initQuantity == null?quantity:quantity.add(initQuantity);
		}
		return surplus = initQuantity == null?quantity:quantity.add(initQuantity);
	}
	
	public void checkLargerThanInventory(InventoryKey key, Long unitId, BigDecimal toBeLockuantity) throws MoreThanInventoryException {
		InventoryKey queryKey = InventoryKey.of(key);
		queryKey.setWarehouseId(null);
		BigDecimal intQuantity = toBeLockuantity.multiply(Constants.QUANTITY_INT_SCALE);
		queryKey.setBatchNumber(Constants.PRE_LOCK_BATCH_NUMBER);
		BigDecimal inventoryBannce = getAvailablelAllInventory(queryKey).multiply(Constants.QUANTITY_INT_SCALE);//库存余量(库存总量-总锁定量)
		BigDecimal requestQuantity = locker.getUnitToMinUnitScale(ProductUnitConvertKey.of(queryKey.getPrdType(),
				queryKey.getVendorId(), queryKey.getProductId(), queryKey.getSpecificationId(), unitId))
				.multiply(intQuantity); // convert to min-unit
		if(inventoryBannce.compareTo(BigDecimal.ZERO) < 0) {//如果库存余量小于0则说明预锁定超过真实库存量
			throw new MoreThanInventoryException(toBeLockuantity, requestQuantity.divide(Constants.QUANTITY_INT_SCALE), inventoryBannce.abs());
		}
	}
}
