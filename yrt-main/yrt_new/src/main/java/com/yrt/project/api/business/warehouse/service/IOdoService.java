package com.yrt.project.api.business.warehouse.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.ProductSnType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.project.api.business.invoice.vo.SearchInvoiceProductForm;
import com.yrt.project.api.business.sale.vo.SearchReturnProductForm;
import com.yrt.project.api.business.warehouse.vo.ProductHealthCareSnForm;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.EditOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoListIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchBatchNumForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoProductForm;
import com.yrt.project.api.vo.BaseQuantities;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.order.domain.SaleRecordDetail;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.product.domain.ProductSn;
import com.yrt.project.modular.warehouse.domain.BatchNumberDetail;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.domain.Odo;
import com.yrt.project.modular.warehouse.domain.TransferSaleRecord;

public interface IOdoService {

	Odo insertOdo(OrgEmployee makingPeople, SystemType systemType, @Valid AddOdoForm form);

	/**
	 * 新增入库单，不检查上游单据数量及锁定库存
	 * @param makingPeople
	 * @param systemType
	 * @param form
	 * @param sn
	 * @param toBeSpecialLock 记录需要锁定的指定批号的产品相关信息
	 * @param toBePreLock 记录需要预锁定的产品相关信息
	 * @return
	 */
	Odo insertOdo(OrgEmployee makingPeople, SystemType systemType, @Valid AddOdoForm form, String sn, Map<InventoryKey, BaseQuantities> toBeSpecialLock, Map<InventoryKey, BaseQuantities> toBePreLock);

	/**
	 * 新增入库单，检查上游单据数量及锁定库存
	 * @param makingPeople
	 * @param systemType
	 * @param form
	 * @param sn
	 * @param isReturn
	 * @return
	 */
	Odo insertOdoAndCheckOverVoucher(OrgEmployee makingPeople, SystemType systemType, @Valid AddOdoForm form, String sn, boolean isReturn);
	
	Odo updateOdo(@Valid EditOdoForm form, OrgEmployee makingPeople);
	Odo updateOdo(@Valid EditOdoForm form, OrgEmployee makingPeople, boolean isReturn);
	
	int deleteOdo(OdoIDForm form);
	int deleteOdo(OdoIDForm form, boolean isReturn);
	
	List<Odo> searchOdoList(SearchOdoForm form, List<Long> warehouseIds);
	
	Odo selectOdoInfo(OdoIDForm form);

	String putout(OdoListIDForm form);
	String putout(OdoListIDForm form, ContextUserInfo curUser, boolean isReturn);

	Object getWarehouseEmployeeList(Long oid, Long wid);
	
	BigDecimal calculateQuantityByProductMinUnit(Long vid, Long pid, Long psid, Long uid, int product_type, BigDecimal quantity);

	List<Map<String,Object>> selectRelatedOrg4DropDownList(SystemType systemType, Long oid, String name);

	List<ResponseProductInfo> selectVendorProductList(SearchOdoProductForm form, List<Long> warehouseIds);
	List<Map<String,Object>> selectVendorProductList4Phone(Map<String,Object> params);

	List<Map<String,Object>> selectDealerProductList(SearchOdoProductForm form);
	List<Map<String,Object>> selectDealerProductList4Phone(Map<String,Object> params);

	List<BatchNumberDetail> selectProductBatchNumList(SearchBatchNumForm form, InventoryKey key);

	List<TransferSaleRecord> getTransferSaleRecordList(SystemType systemType, long orgId, String key,String key1,String organizationName);
	List<SaleRecordDetail> getTransferSaleRecordDetailList(SystemType systemType, long orgId, Long sid, Long detailid, BillsType billsType);
	List<SaleRecordDetail> getTransferGroupSaleRecordDetailList(SystemType systemType, long orgId, Long sid, InventoryKey queryKey, BillsType billsType);
	
	List<HospitalGrantVoucher> getTransferGrantList(SystemType systemType, long orgId, String key, String key1);
	List<HospitalGrantVoucherDetail> getTransferGrantDetailList(SystemType systemType, long orgId, Long gvid, Long detailid);
	List<HospitalGrantVoucherDetail> getTransferGroupGrantDetailList(SystemType systemType, long orgId, Long gvid, InventoryKey queryKey);

	Odo odoRed(@Valid OdoIDForm form, ContextUserInfo curUser);

	void unReview(@Valid OdoIDForm form);

	void printed(OdoIDForm form);
	
	List<Map<String,Object>> getOdoProductList4Return(SearchReturnProductForm form, List<Long> warehouseIds);
	
	List<ProductSn> getProductHealthCareSnList(Long oid, ProductHealthCareSnForm form, ProductSnType healthCareSn);
	List<Map<String, Object>> getOdoProductList4Invoice(SearchInvoiceProductForm form, List<Long> warehouseIds);
}
