package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import com.yrt.common.dict.PrdType;
import com.yrt.project.modular.product.domain.CompleteProduct;
import com.yrt.project.modular.warehouse.domain.InventoryGrnParam;
import com.yrt.project.modular.warehouse.domain.InventoryMinUnitProduct;
import com.yrt.project.modular.warehouse.domain.InventoryProduct;

@Repository
public interface InventoryMapper {

	//临时接口
	public String selectFirstProductName(Map<String,Object> params);
	
	//库存管理主页面四层查询
	public List<InventoryProduct> selectInventoryProductLevel1(Map<String,Object> params);
	
	public List<InventoryProduct> selectInventoryProductLevel2(Map<String,Object> params);
	
	public List<InventoryProduct> selectInventoryProductLevel3(Map<String,Object> params);
	
	default InventoryMinUnitProduct getInventoryMinUnitProduct(InventoryGrnParam param) {
		InventoryMinUnitProduct ret = null;
		if(param.getPrdType().equals(PrdType.VENDOR_PRD)) {
			ret = selectInventoryMinUnitProduct(param);
		}else {
			ret = selectInventoryMinUnitDealerProduct(param);
		}
		return ret;
	}
	public InventoryMinUnitProduct selectInventoryMinUnitProduct(InventoryGrnParam param);
	public InventoryMinUnitProduct selectInventoryMinUnitDealerProduct(InventoryGrnParam param);
	
	public void updateMinUnitInventory(Map<String, Object> params);
	
	public void updateDealerMinUnitInventory(Map<String, Object> params);
	
	public void updateProductInfo(CompleteProduct params);
	
	@InsertProvider(type = SqlProvider.class, method = "insertOrUpdateInventory")
	public void insertOrUpdateInventory(InventoryGrnParam param);
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// mybatis sql semantic methods
	static class SqlProvider {
		public String insertOrUpdateInventory(InventoryGrnParam param) {
			SQL sql = new SQL();
			sql.INSERT_INTO(getTableName(param));
			sql.VALUES(orgColumn(param), "#{orgId}");
			sql.VALUES("warehouse_inner_sn", "#{warehouseId}");
			sql.VALUES(vendorColumn(param, false), "#{vendorId}");
			sql.VALUES("product_inner_sn", "#{productId} ");
			sql.VALUES("specification_inner_sn", "#{specificationId} ");
			sql.VALUES("unit_inner_sn", "#{unitId} ");
			sql.VALUES("batch_number", "#{batchNumber} ");
			sql.VALUES(vendorColumn(param, true), "#{productVendorName} ");
			sql.VALUES("product_name", "#{productName} ");
			sql.VALUES("specification", "#{specification} ");
			sql.VALUES("unit", "#{minUnitName} ");
			sql.VALUES("produce_date", "#{productDate} ");
			sql.VALUES("overdue_date", "#{overdueDate} ");
			sql.VALUES("quantity", "#{minUnitQuantity} ");
			sql.VALUES("average_price", "#{averagePrice} ");
			
			return sql.toString() + " ON DUPLICATE KEY UPDATE " + " product_name = #{productName},  "
					+ vendorColumn(param, true)
					+ " = #{productVendorName}, specification = #{specification},  quantity = #{minUnitQuantity}, average_price = #{averagePrice}, "
					+ "unit_inner_sn = #{unitId}, unit = #{minUnitName}, produce_date = #{productDate}, overdue_date = #{overdueDate}";
		}
		
		private String getTableName(InventoryGrnParam param) {
			switch (param.getOrgType()) {
			case HOSPITAL:
				return param.getPrdType() == PrdType.VENDOR_PRD ? "hospital_product_min_unit_inventory"
						: "hospital_dealer_product_min_unit_inventory";
			case DEALER:
				return param.getPrdType() == PrdType.VENDOR_PRD ? "dealer_product_min_unit_inventory"
						: "dealer_dealer_product_min_unit_inventory";
			case VENDOR:
				return param.getPrdType() == PrdType.VENDOR_PRD ? "vendor_product_min_unit_inventory"
						: "vendor_product_min_unit_inventory";
			default:
				throw new RuntimeException("Unknow orgType");
			}
		}

		private String orgColumn(InventoryGrnParam param) {
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

		private String vendorColumn(InventoryGrnParam param, boolean isName) {
			if(isName) {
				return param.getPrdType() == PrdType.VENDOR_PRD ? "product_vendor_name" : "product_dealer_name";
			}
			return param.getPrdType() == PrdType.VENDOR_PRD ? "product_vendor_inner_sn" : "product_dealer_inner_sn";
		}
	}
}
