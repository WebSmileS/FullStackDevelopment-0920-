package com.yrt.project.api.business.warehouse.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.PrdType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.warehouse.vo.WarehouseSearchProductForm;
import com.yrt.project.modular.product.domain.CompleteProduct;
import com.yrt.project.modular.warehouse.domain.InventoryGrnParam;
import com.yrt.project.modular.warehouse.domain.InventoryMinUnitProduct;
import com.yrt.project.modular.warehouse.domain.InventoryProduct;
import com.yrt.project.modular.warehouse.mapper.DealerInventoryMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalInventoryMapper;
import com.yrt.project.modular.warehouse.mapper.InventoryMapper;
import com.yrt.project.modular.warehouse.mapper.VendorInventoryMapper;

@Service
public class InventoryServiceImpl {

	@Autowired DealerInventoryMapper dMapper;
	@Autowired HospitalInventoryMapper hMapper;
	@Autowired VendorInventoryMapper vMapper;
	
//	@Autowired DealerProductBarcodeMapper dbMapper;
//	@Autowired HospitalProductBarcodeMapper hbMapper;
//	@Autowired VendorProductBarcodeMapper vbMapper;
	
	//根据传入的机构类型选择条码数据库
	/* 取消储位功能，故取消条码相关功能
	 * private ProductBarcodeMapper getBarcodeMapperByOrgType(SystemType type) {
	 * switch (type) { case HOSPITAL: return hbMapper; case DEALER: return dbMapper;
	 * case VENDOR: return vbMapper; default: } throw new
	 * DBException("当前机构不支持该功能!"); } //根据当前登录人的机构类型选择条码数据库 private
	 * ProductBarcodeMapper getBarcodeMapperByOrgType() { SystemType type =
	 * ServletUtils.getCurUserSystemType(); return getBarcodeMapperByOrgType(type);
	 * }
	 */
	
	private InventoryMapper getMapper(SystemType systemType) {
		switch (systemType) {
		case HOSPITAL:
			return hMapper;
		case DEALER:
			return dMapper;
		case VENDOR:
			return vMapper;
		default:
		}
		throw new DBException("当前机构不支持该功能!");
	}
	
	//临时接口
	public String getFirstProductName(Long org_id, SystemType systemType) {
		InventoryMapper mapper = getMapper(systemType);
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", org_id);
		return mapper.selectFirstProductName(params);
	}
	
	public List<InventoryProduct> selectInventoryByLevel(Integer level, WarehouseSearchProductForm form, SystemType systemType, List<Long> warehouseIds) {
		InventoryMapper mapper = getMapper(systemType);
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("warehouseIds", warehouseIds);
		params.put("product_vendor_inner_sn", form.getProduct_vendor_inner_sn());
		params.put("product_dealer_inner_sn", form.getProduct_dealer_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		if(StringUtils.isNotEmpty(form.getKey())) {
			params.put("key", form.getKey());
		}else {
			params.put("product_name", form.getProduct_name());
			params.put("specification", form.getSpecification());
			params.put("batch_number", form.getBatch_number());
		}
		if(form.getProduct_vendor_inner_sn() == null && form.getProduct_dealer_inner_sn() == null) {
			params.put("selectVendor", 1);
			params.put("selectDealer", 1);
		}else {
			if(form.getProduct_vendor_inner_sn() == null) {
				params.put("selectDealer", 1);
			}else {
				params.put("selectVendor", 1);
			}
		}

		params.put("orderByColumn",form.getOrderByColumn());

		switch (level) {
		case 1:
			return mapper.selectInventoryProductLevel1(params);
		case 2:
			return mapper.selectInventoryProductLevel2(params);
		case 3:
			return mapper.selectInventoryProductLevel3(params);
//		case 4:
//			return mapper.selectInventoryProductLevel4(params);
		}
		return new ArrayList<>();
	}

	/* 取消储位功能，故取消条码相关功能
	 * @Override public Map<String, Object> scanBarcode(ScanBarcodeForm form) {
	 * String barcode = form.getBarcode(); if(barcode.length()==31) { try {
	 * SystemType orgType = BaseIntEnum.valueOf(SystemType.class,
	 * Integer.parseInt(barcode.substring(0, 2))); int orgId =
	 * Integer.parseInt(barcode.substring(2,12)); String code =
	 * barcode.substring(12); ProductBarcodeMapper bMapper =
	 * getBarcodeMapperByOrgType(orgType); if(bMapper == null) { throw new
	 * DBException("当前机构不支持扫码查库存功能!"); } Map<String,Object> params = new
	 * HashMap<>(); params.put("org_id", orgId); params.put("barcode_sn", code);
	 * List<ProductBarcode> codes = bMapper.selectBarcode4Grn(params);
	 * if(StringUtils.isNotEmpty(codes)) { ProductBarcode productBarcode =
	 * codes.get(0); Map<String,Object> result = new HashMap<>();
	 * result.put("product_name", productBarcode.getProduct_name());
	 * result.put("specification", productBarcode.getSpecification()); return
	 * result; }else { throw new DBException("不能识别的条码!"); } } catch
	 * (NumberFormatException e) { throw new DBException("不能识别的条码!"); } }else {
	 * throw new DBException("不能识别的条码!"); } }
	 * 
	 * @Override public List<InventoryProduct4Phone>
	 * getUnPlaceInventoryProduct4Phone(SearchUnPlaceProductForm form) { SystemType
	 * systemType = ServletUtils.getCurUserSystemType(); InventoryMapper mapper =
	 * getMapper(); Map<String,Object> params = new HashMap<>();
	 * params.put("org_id", form.getOrg_id()); params.put("warehouse_inner_sn",
	 * form.getWarehouse_inner_sn()); if(StringUtils.isNotEmpty(form.getBarcode()))
	 * { String barcode = form.getBarcode(); if(barcode.length()==31) { try {
	 * SystemType orgType = BaseIntEnum.valueOf(SystemType.class,
	 * Integer.parseInt(barcode.substring(0, 2))); long orgId =
	 * Long.parseLong(barcode.substring(2,12)); String code = barcode.substring(12);
	 * if(!systemType.equals(orgType) || orgId != form.getOrg_id().longValue()) {
	 * throw new DBException("其他机构的条码!"); }else { ProductBarcodeMapper bMapper =
	 * getBarcodeMapperByOrgType(); Map<String,Object> params1 = new HashMap<>();
	 * params1.put("org_id", orgId); params1.put("barcode_sn", code);
	 * List<ProductBarcode> codes = bMapper.selectBarcode4Grn(params1);
	 * if(StringUtils.isNotEmpty(codes)) { ProductBarcode info = codes.get(0);
	 * params.put("product_vendor_inner_sn", info.getProduct_vendor_inner_sn());
	 * params.put("product_inner_sn", info.getProduct_inner_sn());
	 * params.put("specification_inner_sn", info.getSpecification_inner_sn());
	 * params.put("batch_number", info.getBatch_number()); }else { throw new
	 * DBException("不能识别的条码!"); } } } catch (NumberFormatException e) { throw new
	 * DBException("不能识别的条码!"); } }else { throw new DBException("不能识别的条码!"); } }else
	 * { params.put("key", form.getKey()); } return
	 * mapper.selectUnPlaceInventoryProduct4Phone(params); }
	 */

	@Transactional
	public void updateInventoryQuantity(InventoryGrnParam param, SystemType systemType, boolean isRed, boolean isReturn) throws ValidateException{
		InventoryMapper mapper = getMapper(systemType);
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", param.getOrgId());
		params.put("warehouse_inner_sn", param.getWarehouseId());
		params.put("product_vendor_inner_sn", param.getVendorId());
		params.put("product_inner_sn", param.getProductId());
		params.put("specification_inner_sn", param.getSpecificationId());
		params.put("batch_number", param.getBatchNumber());
		params.put("quantity", param.getMinUnitQuantity());
		InventoryMinUnitProduct ret = mapper.getInventoryMinUnitProduct(param);

		BigDecimal parMinUnitQuantity = param.getMinUnitQuantity().abs().divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
		BigDecimal parAveragePrice = param.getAveragePrice().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP);

		if(ret == null && isReturn) {
			param.setMinUnitQuantity(parMinUnitQuantity);
			param.setAveragePrice(parAveragePrice);
			insertOrUpdateInventoryProduct(param, systemType, isRed, false);
		}else {
			BigDecimal newQuantity = ret.getQuantity().multiply(new BigDecimal(1000)).subtract(param.getMinUnitQuantity());
//			// 这个方法除了入库会调用，出库也会调用，而出库是不会影响平均价格的，所以只有入库才需要设置平均价
//			if (isReturn){
//				// 计算平均价
//				BigDecimal retSumPrice = ret.getAverage_price().multiply(ret.getQuantity());
//				BigDecimal parSumPrice = parAveragePrice.abs().multiply(parMinUnitQuantity);
//				BigDecimal averagePrice = retSumPrice.add(parSumPrice).divide(ret.getQuantity().add(parMinUnitQuantity), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(10000));
//				params.put("average_price", averagePrice);
//			}
			// 由于销售退货单的价格不应该影响库存的价格，所以注释掉设置平均价过程
			if(newQuantity.compareTo(BigDecimal.ZERO) < 0) {
				StringBuffer message = new StringBuffer();
				message.append(param.getProductName()).append(">>")
				.append(param.getSpecification()).append(">>").append(param.getMinUnitName()).append("\\n")
				.append("的库存数量不足,可能有入库单被红冲,不能出库!");
				throw new ValidateException(message.toString());
			}else if(newQuantity.compareTo(BigDecimal.ZERO) == 0) {
				params.put("average_price", BigDecimal.ZERO);
			}
			if(ret.getQuantity().compareTo(BigDecimal.ZERO) == 0){
				params.put("average_price", param.getAveragePrice());
			}
			if(param.getPrdType().equals(PrdType.DELEAR_PRD)) {
				mapper.updateDealerMinUnitInventory(params);
			}else {
				mapper.updateMinUnitInventory(params);
			}
		}
	}

	/**
	 * 采购退货减库存
	 * @param param
	 * @param systemType
	 */
	@Transactional
	public void subInventoryProduct(InventoryGrnParam param, SystemType systemType) {
		// 退货数量，负数
		BigDecimal returnQuantity = param.getMinUnitQuantity();

		// 退货成本单价，正数
		BigDecimal returnAveragePrice = param.getAveragePrice();
		InventoryMapper mapper = getMapper(systemType);
		InventoryMinUnitProduct product = mapper.getInventoryMinUnitProduct(param);
		if (product==null){
			throw new ValidateException("库存无此产品，不允许退货");
		}

		// 库存总金额
		BigDecimal inventoryPriceSum = product.getQuantity().multiply(product.getAverage_price());

		// 当前退货金额
		BigDecimal returnPriceSum = returnQuantity.abs().multiply(returnAveragePrice);

		// 新的总金额
		BigDecimal newPriceSum = inventoryPriceSum.subtract(returnPriceSum);

		// 新的总数量
		BigDecimal newQuantity = product.getQuantity().subtract(returnQuantity.abs());

		//最后的库存量检查，不能小于0
		if(newQuantity.compareTo(BigDecimal.ZERO) < 0) {
			StringBuffer message = new StringBuffer();
			message.append(param.getProductName()).append(">>")
					.append(param.getSpecification()).append(">>").append(param.getMinUnitName()).append("\\n")
					.append("的库存数量已经不足，不能退货!");
			throw new ValidateException(message.toString());
		}

		//最后总金额不能小于0
		if(newPriceSum.compareTo(BigDecimal.ZERO) < 0) {
			String message = String.format("本次退货总金额[%s]大于库存可退货总金额[%s]，不能退货",returnPriceSum,inventoryPriceSum);
			throw new ValidateException(message);
		}

		BigDecimal newAveragePrice = BigDecimal.ZERO;
		if(newPriceSum.compareTo(BigDecimal.ZERO) != 0 && newQuantity.compareTo(BigDecimal.ZERO) != 0) {
			newAveragePrice = newPriceSum.divide(newQuantity, 4, RoundingMode.HALF_UP);
		}
		param.setMinUnitQuantity(newQuantity.multiply(new BigDecimal(1000)));
		param.setAveragePrice(newAveragePrice.multiply(new BigDecimal(10000)));
		mapper.insertOrUpdateInventory(param);
	}



	@Transactional
	public void insertOrUpdateInventoryProduct(InventoryGrnParam param, SystemType systemType, Boolean isRed, Boolean isReturn) {
		BigDecimal newQuantity = param.getMinUnitQuantity();
		BigDecimal newAverage_price = param.getAveragePrice();
		InventoryMapper mapper = getMapper(systemType);
		InventoryMinUnitProduct product = mapper.getInventoryMinUnitProduct(param);
		if(product != null) {
			BigDecimal total = product.getQuantity().multiply(product.getAverage_price());
			BigDecimal newTotal = total.add(param.getMinUnitQuantity().multiply(param.getAveragePrice()));
			newQuantity = product.getQuantity().add(param.getMinUnitQuantity());
			//最后的库存量检查，不能小于0
			if(newQuantity.compareTo(BigDecimal.ZERO) < 0) {
				StringBuffer message = new StringBuffer();
				message.append(param.getProductName()).append(">>")
						.append(param.getSpecification()).append(">>").append(param.getMinUnitName()).append("\\n")
						.append("的库存数量已经不足，不能红冲或退货!");
				throw new ValidateException(message.toString());
			}
			if(newTotal.compareTo(BigDecimal.ZERO) != 0 && newQuantity.compareTo(BigDecimal.ZERO) != 0) {
				newAverage_price = newTotal.divide(newQuantity, 4, RoundingMode.HALF_UP);
			}else {
				if(newTotal.compareTo(BigDecimal.ZERO) == 0 || newQuantity.compareTo(BigDecimal.ZERO) == 0) {
					newAverage_price = BigDecimal.ZERO;
				}
			}
		}else if(isReturn){
			if(newQuantity.compareTo(BigDecimal.ZERO) < 0) {
				StringBuffer message = new StringBuffer();
				message.append(param.getProductName()).append(">>")
						.append(param.getSpecification()).append(">>").append(param.getMinUnitName()).append("\\n")
						.append("的库存数量已经不足，不能退货!");
				throw new ValidateException(message.toString());
			}
		}
		param.setMinUnitQuantity(newQuantity.multiply(new BigDecimal(1000)));
		param.setAveragePrice(newAverage_price.multiply(new BigDecimal(10000)));
		mapper.insertOrUpdateInventory(param);
	}
	
	@Transactional
	public void updateInventoryProductInfo(CompleteProduct product) {
		dMapper.updateProductInfo(product);
		hMapper.updateProductInfo(product);
		vMapper.updateProductInfo(product);
	}
}
