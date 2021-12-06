package com.yrt.project.modular.warehouse.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.lang.Nullable;

import com.yrt.common.dict.PrdType;
import com.yrt.project.modular.warehouse.domain.InventoryKey;

/**
 * 最小库存 (主要为 库存锁定 前 查询 )min_unit_inventory
 */
public interface InventoryMinUnitMapper {

	/**
	 * 以 key 为条件, 对quantity求和.
	 * @param key warehouse_inner_sn/batch_number 可为空
	 * @return 最小单位库存量(没有/1000)，无库存返回0
	 */
	default BigDecimal getInventoryQuantity(InventoryKey key) {
		BigDecimal ret = selectMinUnitQuantity(key);
		return ret == null ? BigDecimal.ZERO : ret;
	}
	
	/**
	 * @param param warehouse_inner_sn/batch_number 可选
	 * @return 记录不存在返回null
	 */
	@SelectProvider(type = SqlProvider.class, method = "selectMinUnitQuantity")
	public @Nullable BigDecimal selectMinUnitQuantity(InventoryKey param);
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	static class SqlProvider {

		public String selectMinUnitQuantity(InventoryKey param) {
			// select SUM(quantity) from hospital_product_min_unit_inventory where ...

			SQL sql = new SQL();
			sql.SELECT("SUM(quantity)").FROM(tableName(param));

			sql.WHERE(orgColumn(param) + " = #{orgId}");

			if (param.getWarehouseId() != null) {
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
		
		private String tableName(InventoryKey param) {
			switch (param.getOrgType()) {
			case HOSPITAL:
				return param.getPrdType() == PrdType.VENDOR_PRD ? "hospital_product_min_unit_inventory"
						: "hospital_dealer_product_min_unit_inventory";
			case DEALER:
				return param.getPrdType() == PrdType.VENDOR_PRD ? "dealer_product_min_unit_inventory"
						: "dealer_dealer_product_min_unit_inventory";
			case VENDOR:
				return param.getPrdType() == PrdType.VENDOR_PRD ? "vendor_product_min_unit_inventory"
						: "vendor_dealer_product_min_unit_inventory";
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
	}
}
