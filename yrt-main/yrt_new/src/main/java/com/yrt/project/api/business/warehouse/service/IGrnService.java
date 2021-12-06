package com.yrt.project.api.business.warehouse.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.project.api.business.invoice.vo.SearchInvoiceProductForm;
import com.yrt.project.api.business.order.vo.purchasingRecord.SearchPurchasingRecordProductForm;
import com.yrt.project.api.business.sale.vo.SearchReturnProductForm;
import com.yrt.project.api.business.warehouse.vo.ScanBarcodeForm;
import com.yrt.project.api.business.warehouse.vo.UsableProductInfo;
import com.yrt.project.api.business.warehouse.vo.grn.AddGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.AppGrnSearchForm;
import com.yrt.project.api.business.warehouse.vo.grn.EditGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIDForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIds;
import com.yrt.project.api.business.warehouse.vo.grn.SearchGrnForm;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Grn;
import com.yrt.project.modular.warehouse.domain.TransferVoucher;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;

public interface IGrnService {

	public Grn insertGrn(AddGrnForm form, String sn, OrgEmployee makingPeople);
	
	public Grn updateGrn(EditGrnForm form, OrgEmployee makingPeople);
	
	public int deleteGrn(GrnIDForm form, OrgEmployee makingPeople);
	
	public List<Grn> searchGrnList(SearchGrnForm form, List<Long> warehouseIds);
	public List<Grn> searchGrnList4Phone(AppGrnSearchForm form);
	
	public Grn selectGrnInfo(GrnIDForm form);

	public Map<String,Object> putin(GrnIds form, ContextUserInfo curUser);
	public Map<String,Object> putin(GrnIds form, ContextUserInfo curUser, Boolean isReturn);

	/**
	 * 采购退货出库
	 * @param form
	 * @param curUser
	 * @return
	 */
	Map<String,Object> purchaseReturnPutIn(GrnIds form, ContextUserInfo curUser);

	public Object getWarehouseEmployeeList(Long oid, Long wid);

//	public List<Map<String,Object>> selectRelatedOrg4DropDownList(SystemType systemType, Long oid, String name, Boolean all);

//	public List<Map<String,Object>> selectVendorProductList(GrnSearchProductForm form);

//	public List<Map<String,Object>> selectDealerProductList(GrnSearchProductForm form);

	/* 取消储位功能，故取消条码相关功能
	 * public List<InventoryProduct> getStorageLocationInfo(Long oid, Long wid, Long begin_inventory, Long end_inventory);

	 * public List<ProductBarcode> getBarcode(SearchBarcodeForm form);
	 * 
	 * public List<Map<String,String>> createBarcode(CreateBarcodeForm form);
	 * 
	 * public int saveBarcode(SystemType systemType, SaveBarcodeForm form);
	 * 
	 * public ProductBarcode scanBarcode(ScanBarcodeForm form);
	 */

	public List<TransferVoucher> getTransferVoucherList(SystemType systemType, long orgId, String sn, String organizationName, String warehouse_name, RefVoucherType vType,String key);

	public List<TransferVoucherDetail> getTransferVoucherDetailList(long orgId, Long odoId, Long organization_inner_sn, BillsType grn_type, RefVoucherType vType);

	public List<Grn> searchGrnListGlobal(SearchGrnForm form);

	public Grn grnRed(@Valid GrnIDForm form, ContextUserInfo curUser, Boolean isReturn);

	public void unReview(@Valid GrnIDForm form);
	
	public BigDecimal getSumGrnDetailRefPurchaseOrder(Long org_id, Long organization_inner_sn, Long orderId, Long orderDetailId, BillsType type);
	
	public List<Map<String,Object>> getGrnProductList4Return(SearchReturnProductForm form, List<Long> warehouseIds);

	public List<Map<String, Object>> getGrnProductList4Invoice(SearchInvoiceProductForm form, List<Long> warehouseIds);

	public boolean isExistGrn(Long org_id, Long organization_inner_sn, Long voucher_inner_sn_c, RefVoucherType voucher_type_c, BillsType billsType, Long rollbackDetailId);

	public List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(SearchPurchasingRecordProductForm form,
			ContextUserInfo curUser, VoucherType voucherType, List<Long> warehouseIds);
	public List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(Map<String, Object> params,
			ContextUserInfo curUser, VoucherType voucherType, BillsType billsType);

	public UsableProductInfo scanProductByBarcode(ScanBarcodeForm form, ContextUserInfo curUser, List<Long> warehouseIds);

	public void updatePurchaseGrnDetailInspectionDetailId(Long org_id, Long grn_id, Long grn_detail_id, Long inspectionDetailId,
			BillsType billsType);
}
