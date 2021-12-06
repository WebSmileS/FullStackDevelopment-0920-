package com.yrt.project.modular.warehouse.domain;

import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.PrdType;
import com.yrt.common.dict.SystemType;

import lombok.Data;

/**
 * 库存key(库存锁/最小单位库存量)
 */
@Data
public class InventoryKey {

	/**
	 * 医院库存： 产品
	 * 
	 * @param hospital_inner_sn
	 * @param warehouse_inner_sn
	 * @param product_vendor_inner_sn
	 * @param product_inner_sn
	 * @param specification_inner_sn
	 * @param batch_number
	 * @return
	 */
	public static InventoryKey ofProductForHospital(Long hospital_inner_sn, Long warehouse_inner_sn,
			Long product_vendor_inner_sn, Long product_inner_sn, Long specification_inner_sn, String batch_number) {
		return of(SystemType.HOSPITAL, PrdType.VENDOR_PRD, // 产品
				hospital_inner_sn, warehouse_inner_sn, product_vendor_inner_sn, product_inner_sn,
				specification_inner_sn, batch_number);
	}

	/**
	 * 医院库存： 经销商自建产品
	 * 
	 * @param hospital_inner_sn
	 * @param warehouse_inner_sn
	 * @param product_dealer_inner_sn
	 * @param product_inner_sn
	 * @param specification_inner_sn
	 * @param batch_number
	 * @return
	 */
	public static InventoryKey ofDealerProductForHospital(Long hospital_inner_sn, Long warehouse_inner_sn,
			Long product_dealer_inner_sn, Long product_inner_sn, Long specification_inner_sn, String batch_number) {
		return of(SystemType.HOSPITAL, PrdType.DELEAR_PRD, // 经销商自建
				hospital_inner_sn, warehouse_inner_sn, product_dealer_inner_sn, product_inner_sn,
				specification_inner_sn, batch_number);
	}

	/**
	 * 经销商库存：产品
	 * 
	 * @param dealer_inner_sn
	 * @param product_vendor_inner_sn
	 * @param product_inner_sn
	 * @param specification_inner_sn
	 * @param batch_number
	 * @return
	 */
	public static InventoryKey ofProductForDealer(Long dealer_inner_sn, Long warehouse_inner_sn,
			Long product_vendor_inner_sn, Long product_inner_sn, Long specification_inner_sn, String batch_number) {
		return of(SystemType.DEALER, PrdType.VENDOR_PRD, // 产品
				dealer_inner_sn, warehouse_inner_sn, // warehouse
				product_vendor_inner_sn, product_inner_sn, specification_inner_sn, batch_number);
	}

	/**
	 * 经销商库存： 经销商自建产品
	 * 
	 * @param dealer_inner_sn
	 * @param product_dealer_inner_sn
	 * @param product_inner_sn
	 * @param specification_inner_sn
	 * @param batch_number
	 * @return
	 */
	public static InventoryKey ofDealerProductForDealer(Long dealer_inner_sn, Long warehouse_inner_sn,
			Long product_dealer_inner_sn, Long product_inner_sn, Long specification_inner_sn, String batch_number) {
		return of(SystemType.DEALER, PrdType.DELEAR_PRD, // 经销商自建
				dealer_inner_sn, warehouse_inner_sn, // warehouse
				product_dealer_inner_sn, product_inner_sn, specification_inner_sn, batch_number);

	}
	
	/**
	 * 厂商库存：产品
	 * 
	 * @param vendor_inner_sn
	 * @param product_vendor_inner_sn
	 * @param product_inner_sn
	 * @param specification_inner_sn
	 * @param batch_number
	 * @return
	 */
	public static InventoryKey ofProductForVendor(Long vendor_inner_sn,  Long warehouse_inner_sn,
			Long product_vendor_inner_sn, Long product_inner_sn, Long specification_inner_sn, String batch_number) {
		return of(SystemType.VENDOR, PrdType.VENDOR_PRD, // 产品
				vendor_inner_sn, warehouse_inner_sn, // warehouse
				product_vendor_inner_sn, product_inner_sn, specification_inner_sn, batch_number);
	}
	
	public static InventoryKey of(SystemType orgType, PrdType prdType, Long orgId, Long warehouseId, Long vendorId, Long productId, Long specificationId, String batchNumber) {
		InventoryKey ret = new InventoryKey();
		ret.setOrgType(orgType);
		ret.setPrdType(prdType);
		ret.setOrgId(orgId);
		ret.setWarehouseId(warehouseId);
		ret.setVendorId(vendorId);
		ret.setProductId(productId);
		ret.setSpecificationId(specificationId);
		ret.setBatchNumber(batchNumber);
		return ret;
	}
	
	public static InventoryKey of(Long oid, Long wid, Long pvid, Long pid, Long psid, int productType, String batchNumber, BillsType billsType) {
		InventoryKey lockKey = null;
		switch (billsType) {
		case DD:
		case DV:
		case DH:
			if (productType == 1) {
				lockKey = InventoryKey.ofProductForDealer(oid, wid, pvid, //
						pid, //
						psid, //
						batchNumber);
			} else {
				lockKey = InventoryKey.ofDealerProductForDealer(oid, wid, pvid, //
						pid, //
						psid, //
						batchNumber);
			}
			break;
		case HH:
		case HD:
		case HV:
			if (productType == 1) {
				lockKey = InventoryKey.ofProductForHospital(oid, wid, pvid, //
						pid, //
						psid, //
						batchNumber);
			} else {
				lockKey = InventoryKey.ofDealerProductForHospital(oid, wid, pvid, //
						pid, //
						psid, //
						batchNumber);
			}
			break;
		case VD:
		case VH:
		case VV:
			if (productType == 1) {
				lockKey = InventoryKey.ofProductForVendor(oid, wid, pvid, //
						pid, //
						psid, //
						batchNumber);
			}
			break;
		}
		return lockKey;
	}
	
	/**
	 * copy
	 * @param src
	 * @return
	 */
	public static InventoryKey of(InventoryKey src) {
		return of(src.getOrgType(), src.getPrdType(),src.getOrgId(), src.getWarehouseId(), src.getVendorId(), src.getProductId(), src.getSpecificationId(), src.getBatchNumber());
	}

	/**
	 * 库存所有者机构类型(SystemType) : 只有HOSPITAL和DEALER有库存.
	 */
	private SystemType orgType;

	/**
	 * 产品类型(产品/自建)
	 * @see #vendorId
	 */
	private PrdType prdType;

	/**
	 * 库存所有者 机构id(hospital_inner_sn/dealer_inner_sn, 由 {@link #orgType} 决定)
	 */
	private Long orgId;

	
	/**
	 * 仓库inner_sn
	 */
	@Nullable
	private Long warehouseId;

	/**
	 * 产品提供商id(vendor_inner_sn/dealer_inner_sn, 由 {@link #prdType} 决定)
	 */
	private Long vendorId;

	/**
	 * 产品inner_sn
	 */
	private Long productId;

	/**
	 * 规格型号inner_sn
	 */
	private Long specificationId;

	
	/**
	 * 批号
	 */
	@NotNull
	private String batchNumber;

}
