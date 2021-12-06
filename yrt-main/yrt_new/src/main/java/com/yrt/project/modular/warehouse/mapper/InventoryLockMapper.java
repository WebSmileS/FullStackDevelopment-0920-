package com.yrt.project.modular.warehouse.mapper;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.yrt.common.dict.PrdType;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.domain.InventoryLockParam;
import com.yrt.project.modular.warehouse.domain.ProductUnitConvertKey;

/**
 * 库存锁定 操作: 
 * <ol>
 * <li>{@link #lockInventory(InventoryKey, BigDecimal)}</li>
 * <li>{@link #lockInventory(InventoryKey, Long, BigDecimal)}</li>
 * <li>{@link #unlockInventory(InventoryKey, BigDecimal)}</li>
 * <li>{@link #unlockInventory(InventoryKey, Long, BigDecimal)}</li>
 * <li>{@link #getLockedQuantity(InventoryKey)}</li>
 * <li>{@link #getUnitToMinUnitScale(ProductUnitConvertKey)}</li>
 * </ol>
 * 
 * <p>
 * 单位转换 本属Product模块，在这里提供仅为方便起见。
 * <p>
 * <em>采用insertOrUpdate 消除 select/insert 的并发冲突风险.</em>
 * 
 * <p>
 * <strong>Notes:</strong> <em>quantity 内置单位转换的方法都有明确的说明（但都不包含 小数化整数
 * 的转换--*1000或10000） </em>
 */
@Repository
public interface InventoryLockMapper {

	/**
	 * 锁定库存(无单位转换). 
	 * @param key
	 * @param minUnitQuantity
	 *            最小单位数量.
	 */
	default void lockInventory(InventoryKey key, BigDecimal minUnitQuantity) {
		this.insertOrUpdateInventoryLock(InventoryLockParam.of(key, minUnitQuantity));
	}

	/**
	 * 锁定库存(内置单位转换: unitId->minUnit)
	 * 
	 * @param key
	 * @param unitId
	 * @param quantity
	 *            unitId所对应的单位.
	 * 
	 */
	default void lockInventory(InventoryKey key, Long unitId, BigDecimal quantity) {
		ProductUnitConvertKey unitKey = ProductUnitConvertKey.of(key.getPrdType(), key.getVendorId(),
				key.getProductId(), key.getSpecificationId(), unitId);
		BigDecimal scale = this.selectCaculateMinUnitFunction(unitKey);
		BigDecimal minUnitQuantity = scale.multiply(quantity);
		LoggerFactory.getLogger(InventoryLockMapper.class).info(
				"lockInventory(withUnitConvert): scale={}, quantity={}, minUnitQuantity={}, key={}\n\t unitKey={}",
				scale, quantity, minUnitQuantity, key, unitKey);
		this.insertOrUpdateInventoryLock(InventoryLockParam.of(key, minUnitQuantity));
	}

	/**
	 * 解锁 库存锁定(无单位转换)
	 * 
	 * @param key
	 * @param quantity
	 *            最小单位数量.
	 */
	default void unlockInventory(InventoryKey key, BigDecimal minUnitQuantity) {
		this.lockInventory(key, minUnitQuantity.negate());
	}

	/**
	 * 解锁 库存锁定(内置单位转换: unitId->minUnit)
	 * 
	 * @param key
	 * @param unitId
	 * @param quantity
	 *            unitId所对应的单位.
	 */
	default void unlockInventory(InventoryKey key, Long unitId, BigDecimal quantity) {
		this.lockInventory(key, unitId, quantity.negate());
	}

	/**
	 * 取锁定量(不会返回空, 数据库为空时返回0)
	 * 
	 * @param key batch_number可选, warehouse_inner_sn可选(for dealer无意义)
	 * @return 锁定量(以最小单位计)
	 */
	default @NotNull BigDecimal getLockedQuantity(InventoryKey key) {
		BigDecimal ret = this.selectLockedQuantity(key);
		return ret == null ? BigDecimal.ZERO : ret;
	}

	/**
	 * 到最小单位的转换因子(倍率)
	 * 
	 * @param unitKey
	 * @return
	 */
	default BigDecimal getUnitToMinUnitScale(ProductUnitConvertKey unitKey) {
		BigDecimal ret = this.selectCaculateMinUnitFunction(unitKey);
		return ret;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// mybatis sql semantic methods

	@InsertProvider(type = SqlProvider.class, method = "insertOrUpdateInventoryLock")
	public int insertOrUpdateInventoryLock(InventoryLockParam param);

	/**
	 * @param param
	 * @return 锁定记录不存在返回null
	 */
	@SelectProvider(type = SqlProvider.class, method = "selectLockedQuantity")
	public @Nullable BigDecimal selectLockedQuantity(InventoryKey param);

	@SelectProvider(type = SqlProvider.class, method = "selectCaculateMinUnitFunction")
	public BigDecimal selectCaculateMinUnitFunction(ProductUnitConvertKey param);

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	static class SqlProvider {
		public String insertOrUpdateInventoryLock(InventoryLockParam param) {
			// INSERT INTO hospital_product_inventory_lock_warehouse(..., quantity) VALUES
			// (..., #{quantity})
			// ON
			// DUPLICATE KEY UPDATE quantity = quantity + (#{quantity})

			SQL sql = new SQL();
			sql.INSERT_INTO(lockTableName(param));
			sql.VALUES(orgColumn(param), "#{orgId}");
			sql.VALUES("warehouse_inner_sn", "#{warehouseId}");
			/*2020-09-10 所有机构都存在仓库字段
			 * if (param.getOrgType() == SystemType.HOSPITAL) { }
			 */
			sql.VALUES(vendorColumn(param), "#{vendorId}");
			sql.VALUES("product_inner_sn", "#{productId} ");
			sql.VALUES("specification_inner_sn", "#{specificationId} ");
			sql.VALUES("batch_number", "#{batchNumber} ");
			sql.VALUES("quantity", "GREATEST(0, #{quantity}) "); //GREATEST 锁定库存量不能为负
			return sql.toString() + " ON DUPLICATE KEY UPDATE quantity = GREATEST(0, quantity + #{quantity})";
		}

		public String selectLockedQuantity(InventoryKey param) {
			// select quantity from hospital_product_inventory_lock_warehouse where ...

			SQL sql = new SQL();
			sql.SELECT("SUM(quantity)").FROM(lockTableName(param));

			sql.WHERE(orgColumn(param) + " = #{orgId}");
			/*2020-09-10 所有机构都存在仓库字段
			 * if (param.getOrgType() == SystemType.HOSPITAL &&
			 * param.getWarehouseId()!=null) {
			 * sql.WHERE("warehouse_inner_sn = #{warehouseId}"); }
			 */
			if (param.getWarehouseId()!=null) {
				sql.WHERE("warehouse_inner_sn = #{warehouseId}");
			}
			sql.WHERE(vendorColumn(param) + " = #{vendorId} ");
			sql.WHERE("product_inner_sn = #{productId} ");
			sql.WHERE("specification_inner_sn = #{specificationId} ");
			if (param.getBatchNumber() != null) {
				sql.WHERE("batch_number = #{batchNumber} ");
			}
			return sql.toString();
		}

		private String lockTableName(InventoryKey param) {
			switch (param.getOrgType()) {
			case HOSPITAL:
				return param.getPrdType() == PrdType.VENDOR_PRD ? "hospital_product_inventory_lock_warehouse"
						: "hospital_dealer_product_inventory_lock_warehouse";
			case DEALER:
				return param.getPrdType() == PrdType.VENDOR_PRD ? "dealer_product_inventory_lock"
						: "dealer_dealer_product_inventory_lock";
			case VENDOR:
				return param.getPrdType() == PrdType.VENDOR_PRD ? "vendor_product_inventory_lock"
						: "vendor_dealer_product_inventory_lock";
			default:
				throw new RuntimeException("Unknow orgType");
			}
		}

		private String orgColumn(InventoryKey param) {
			switch (param.getOrgType()) {
			case HOSPITAL:
				return "hospital_inner_sn";
			case DEALER:
				return "dealer_inner_sn";
			case VENDOR:
				return "vendor_inner_sn";
			default:
				throw new RuntimeException("Unknow orgType");
			}
		}

		private String vendorColumn(InventoryKey param) {
			return param.getPrdType() == PrdType.VENDOR_PRD ? "product_vendor_inner_sn" : "product_dealer_inner_sn";
		}

		public String selectCaculateMinUnitFunction(ProductUnitConvertKey param) {
			// %s=function
			String sql = "SELECT %s( #{vendorId}, #{productId}, #{specificationId}, #{unitId} ) FROM dual";
			String fun = param.getPrdType() == PrdType.VENDOR_PRD ? "c_calculate_product_min_unit"
					: "c_calculate_dealer_product_min_unit";
			return String.format(sql, fun);
		}

	}

}
