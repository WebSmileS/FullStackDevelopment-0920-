package com.yrt.project.api.business.order.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.github.pagehelper.PageHelper;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.PurchasingRecordStatus;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.order.vo.order.SearchPurchaseRecordForm;
import com.yrt.project.api.business.order.vo.purchasingRecord.SearchPurchasingRecordProductForm;
import com.yrt.project.api.business.warehouse.service.ArrivalRecordService;
import com.yrt.project.api.business.warehouse.service.IGrnService;
import com.yrt.project.api.business.warehouse.service.InspectionRecordService;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.order.domain.OrderDetail;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.order.domain.PurchasingRecord;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.order.mapper.PurchasingRecordMapper;
import com.yrt.project.modular.warehouse.domain.CheckPurchasingRecord;

@Service
public class PurchasingRecordService {
	
	@Autowired PurchasingRecordMapper mapper;
	@Autowired SysParameterService paramService;
	
	@Autowired ArrivalRecordService arrivalService;
	@Autowired InspectionRecordService inspectionService;
	@Autowired IGrnService grnService;
	
	public boolean isExistPurchasingRecord(Long org_id, Long organization_inner_sn, BillsType billsType, RefVoucherType voucherType, Long voucher_inner_sn_c, Long voucher_detail_inner_sn_c) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", org_id);
		params.put("organization_inner_sn", organization_inner_sn);
		params.put("bills_type", billsType);
		params.put("voucher_type_c", voucherType);
		params.put("voucher_inner_sn_c", voucher_inner_sn_c);
		params.put("voucher_detail_inner_sn_c", voucher_detail_inner_sn_c);
		return mapper.existPurchasingRecord(params);
	}
	
	public List<PurchasingRecord> searchPurchasingRecordList(SearchPurchaseRecordForm form, SystemType systemType, boolean isSingle) {
		if(Utils.isEmpty(form.getStart_date()) || Utils.isEmpty(form.getEnd_date())){
			throw new ValidateException("日期区间必填!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getPartA());
		params.put("system_type", systemType.value());
		params.put("organization_name", form.getPartB_name());
		params.put("sn", form.getOrder_sn());
		params.put("product_vendor_name", form.getProduct_vendor_name());
		params.put("product_name", form.getProduct_name());
		params.put("buyer_name", form.getBuyer_name());
		params.put("begin_date", form.getStart_date());
		params.put("end_date", form.getEnd_date());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("status", form.getStatus());
		
		return mapper.selectPurchasingRecordList(params);
	}
	
	public PurchasingRecord getPurchasingRecordInfo(Long id) {
		return mapper.selectPurchasingRecordInfo(id);
	}
	
	@Transactional
	public void insertPurchasingRecord(PurchaseOrder order, SystemType systemType, String created_by) {
		List<OrderDetail> details = order.getDetails();
		for (OrderDetail detail : details) {
			PurchasingRecord record = Utils.clone(detail, PurchasingRecord.class);
			record.setUnit(detail.getUnit_name());
			record.setId(CompressedIdGenerator.generateId());
			record.setBills_type(order.getBillsType());
			record.setSystem_type(systemType.value());
			record.setOrg_id(order.getPartA());
			record.setOrganization_inner_sn(order.getPartB());
			record.setOrganization_name(order.getPartB_name());
			record.setSn(order.getPo_sn());
			record.setCreated_time(order.getCreate_time());
			record.setCreated_by(created_by);
			record.setPurchasing_date(order.getCreate_time());
			record.setVoucher_type_c(RefVoucherType.ORDER_PO.value());
			record.setVoucher_inner_sn_c(order.getPo_so_inner_sn());
			record.setVoucher_detail_inner_sn_c(detail.getDetail_inner_sn());
			record.setWarehouse_inner_sn(order.getWarehouse_inner_sn());
			record.setWarehouse_name(order.getWarehouse_name());
			record.setBuyer_inner_sn(order.getBuyer_inner_sn());
			record.setBuyer_name(order.getBuyer_name());
			record.setRecord_status(calculateRecordStatus(order.getStatus().intValue(), detail.getStatus().intValue()));
			mapper.insertPurchasingRecord(record);
		}
	}
	
	@Transactional
	public void updatePurchasingRecordArrivalQuantity(Long id, BigDecimal arrival_quantity, String update_by,
			boolean rollback, List<Long> rollbackDetailIdList) {
		if (rollback) {
			for (Long detailId : rollbackDetailIdList) {
				if (inspectionService.isExistInspectionRecord(id, RefVoucherType.PO_RECORDS, detailId)) {
					throw new ValidateException("已生成验货单，请先作废对应的验货单!!");
				}
			}
		}
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		params.put("arrival_quantity", rollback ? arrival_quantity.multiply(new BigDecimal(-1)) : arrival_quantity);
		updatePurchasingRecord(params, update_by);
	}
	
	@Transactional
	public void updatePurchasingRecordInspectionQuantity(Long id, BigDecimal qualified_quantity,
			BigDecimal unqualified_quantity, String update_by, boolean rollback, List<Long> rollbackDetailIdList) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		PurchasingRecord info = mapper.selectPurchasingRecordInfo(id);
		if (rollback) {
			for (Long detailId : rollbackDetailIdList) {
				if(grnService.isExistGrn(id, info.getOrg_id(), info.getOrganization_inner_sn(),
						RefVoucherType.PO_RECORDS, info.getBills_type(), detailId)) {
					throw new ValidateException("已生成采购入库单，请先作废对应的采购入库单!!");
				}
			}
		}
		params.put("qualified_quantity",
				rollback ? qualified_quantity.multiply(new BigDecimal(-1)) : qualified_quantity);
		params.put("unqualified_quantity",
				rollback ? unqualified_quantity.multiply(new BigDecimal(-1)) : unqualified_quantity);
		updatePurchasingRecord(params, update_by);
	}
	
	@Autowired IOrderService orderService;
	
	@Transactional
	public void updatePurchasingRecordGrnQuantity(Long id, BigDecimal grn_quantity, String update_by, boolean rollback) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		PurchasingRecord record = mapper.selectPurchasingRecordInfo(id);
		BigDecimal new_grn_quantity = grn_quantity;
		if(rollback) {
			new_grn_quantity = record.getGrn_quantity().subtract(grn_quantity);
		}
		params.put("grn_quantity", new_grn_quantity);
		
		updatePurchasingRecord(params, update_by);
		PurchasingRecord info = mapper.selectPurchasingRecordInfo(id);
		if(BigDecimal.ZERO.compareTo(info.getQualified_quantity()) < 0) {
			if(info.getGrn_quantity().compareTo(info.getQualified_quantity()) >= 0) {
				updatePurchasingRecordStatus(id, PurchasingRecordStatus.FINISHED, update_by);
			}else {
				updatePurchasingRecordStatus(id, PurchasingRecordStatus.EXECUTING, update_by);
			}
		}else {
			if(info.getGrn_quantity().compareTo(info.getQuantity()) >= 0) {
				updatePurchasingRecordStatus(id, PurchasingRecordStatus.FINISHED, update_by);
			}else {
				updatePurchasingRecordStatus(id, PurchasingRecordStatus.EXECUTING, update_by);
			}
		}
		orderService.updateTransferDetail(record.getOrg_id(), record.getOrganization_inner_sn(),
				record.getVoucher_inner_sn_c(), record.getVoucher_detail_inner_sn_c(), 
				record.getUnit_inner_sn(), grn_quantity, record.getBills_type(), rollback, "更新入库数量调单");
	}
	
	@Transactional
	public void updatePurchasingRecordStatus(Long id, PurchasingRecordStatus newStatus, String update_by) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("record_status", newStatus.value());
		PurchasingRecord record = mapper.selectPurchasingRecordInfo(id);
		if(Utils.isEmpty(id)) {
			throw new ValidateException("采购记录ID不能为空!!");
		}
		if(Utils.isEmpty(record)) {
			throw new ValidateException("错误的采购记录ID!!");
		}
		updatePurchasingRecordStatus(record.getId(), record.getRecord_status(), newStatus, update_by);
	}
	
	@Transactional
	public void updatePurchasingRecordStatus(Long orgId, Long organization_inner_sn, BillsType billsType, RefVoucherType voucher_type_c,
			Long voucher_inner_sn_c, Long voucher_detail_inner_sn_c, PurchasingRecordStatus newStatus, String update_by) {
		if (!RefVoucherType.ORDER_PO.equals(voucher_type_c) && !RefVoucherType.ODO.equals(voucher_type_c)) {
			throw new ValidateException("错误的调单类型!");
		}
		if (Utils.isEmpty(voucher_inner_sn_c)) {
			throw new ValidateException("调单ID必填!");
		}
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("organization_inner_sn", organization_inner_sn);
		params.put("bills_type", billsType.value());
		params.put("voucher_type_c", voucher_type_c.value());
		params.put("voucher_inner_sn_c", voucher_inner_sn_c);
		params.put("voucher_detail_inner_sn_c", voucher_detail_inner_sn_c);
		List<PurchasingRecord> list = mapper.selectPurchasingRecordListByVoucher(params);
		for (PurchasingRecord record : list) {
			updatePurchasingRecordStatus(record.getId(), record.getRecord_status(), newStatus, update_by);
		}
	}
	
	@Transactional
	void updatePurchasingRecordStatus(Long id, PurchasingRecordStatus oldStatus, PurchasingRecordStatus newStatus, String update_by) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("record_status", newStatus.value());
//		if(newStatus.value() > oldStatus.value()
//				|| (PurchasingRecordStatus.CANCEL.equals(oldStatus) 
//						&& PurchasingRecordStatus.EXECUTING.equals(newStatus))) {//状态只能从小到大，不能反向执行，反终止除外
			updatePurchasingRecord(params, update_by);
//		}else {
//			throw new ValidateException("采购记录的状态错误!!");
//		}
	}
	
	@Transactional
	public void updatePurchasingRecordMDRF(Long id, Long mdrf_inner_sn, String mdrf_sn, String health_care_sn, String update_by) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("mdrf_inner_sn", Utils.isEmpty(mdrf_inner_sn)?null:mdrf_inner_sn);
		params.put("mdrf_sn", Utils.isEmpty(mdrf_sn)?null:mdrf_sn);
		params.put("health_care_sn", Utils.isEmpty(health_care_sn)?null:health_care_sn);
		updatePurchasingRecord(params, update_by);
	}
	
	@Transactional
	void updatePurchasingRecord(Map<String,Object> params, String update_by) {
		params.put("update_by", update_by);
		params.put("update_time", new Date());
		mapper.updatePurchasingRecord(params);
	}
	
	@Transactional
	public void deletePurchasingRecord(Long id, Long orgId, Long organization_inner_sn, BillsType billsType, RefVoucherType voucher_type_c, Long voucher_inner_sn_c, boolean isSale) {
		if(isSale) {
			billsType = convertBillsType(billsType);
		}
		Map<String,Object> params = new HashMap<>();
		if(Utils.isNotEmpty(id)) {
			params.put("id", id);
		}else {
			if(!RefVoucherType.ORDER_PO.equals(voucher_type_c) && !RefVoucherType.ODO.equals(voucher_type_c)){
				throw new ValidateException("错误的调单类型!");
			}
			if(Utils.isEmpty(voucher_inner_sn_c) ){
				throw new ValidateException("调单ID必填!");
			}
			if(arrivalService.isExistArrivalRecord(voucher_inner_sn_c, voucher_type_c)
					||inspectionService.isExistInspectionRecord(voucher_inner_sn_c, voucher_type_c, null)
					||grnService.isExistGrn(orgId,organization_inner_sn, voucher_inner_sn_c, voucher_type_c, billsType, null)) {
				throw new ValidateException("不能删除！已经生成收货单、验货单和入库单!");
			}
			params.put("org_id", orgId);
			params.put("bills_type", billsType.value());
			params.put("organization_inner_sn", organization_inner_sn);
			params.put("voucher_type_c", voucher_type_c.value());
			params.put("voucher_inner_sn_c", voucher_inner_sn_c);
		}
		mapper.deletePurchasingRecord(params);
	}
	/**
	 * 强行终止(因为采购订单上已经有终止功能，暂时不用这个)
	 * @param id
	 * @param kill_by
	 * @return
	 */
	public int kill(Long id, String kill_by) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("record_status", PurchasingRecordStatus.KILL.value());
		updatePurchasingRecord(params, kill_by);
		return 1;
	}
	
	public BillsType convertBillsType(BillsType billsType) {
		switch (billsType) {
		case DD:
			return BillsType.DD;
		case DV:
			return BillsType.VD;
		case DH:
			return BillsType.HD;
		case HH:
			return BillsType.HH;
		case HD:
			return BillsType.DH;
		case HV:
			return BillsType.VH;
		case VD:
			return BillsType.DV;
		case VH:
			return BillsType.HV;
		case VV:
			return BillsType.VV;
		default:
			break;
		}
		return null;
	}

	private PurchasingRecordStatus calculateRecordStatus(int p_status, int d_status) {
		if(p_status == 4) {//如果订单状态已终止则全部明细都是终止
			return PurchasingRecordStatus.CANCEL;
		}else {
			if(d_status == 3) {//如果订单明细已终止，则明细就是终止状态
				return PurchasingRecordStatus.CANCEL;
			}else {
				if(p_status == 0 || p_status == 1 ) {//如果订单状态是草稿或者正式，则明细应该是未审核
					return PurchasingRecordStatus.TO_BE_VERIFY;
				}else if(p_status == 2){
					return PurchasingRecordStatus.TO_BE_CONFIRM;
				}else {
					return PurchasingRecordStatus.EXECUTING;
				}
			}
		}
	}
	
	public List<PurchasingRecordProduct> getControlUsablePurchasingRecordList(@Validated SearchPurchasingRecordProductForm form,
			ContextUserInfo curUser, VoucherType type, List<Long> warehouseIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType());
		params.put("bills_type", form.getBills_type());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());

		params.put("product_vendor_name", form.getProduct_vendor_name());
		params.put("product_name", form.getProduct_name());
		params.put("specification", form.getSpecification());
		params.put("sn", form.getSn());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("warehouseIds", warehouseIds);
		
		return getControlUsablePurchasingRecordList(params);
	}
	
	public List<PurchasingRecordProduct> getControlUsablePurchasingRecordList(Map<String, Object> params){
		return mapper.selectControlUsablePurchasingRecordList(params);
	}
	
	public List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(@Validated SearchPurchasingRecordProductForm form,
			ContextUserInfo curUser, VoucherType type, List<Long> warehouseIds) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType());
		params.put("bills_type", form.getBills_type());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("voucherType", type.value());

		params.put("product_vendor_name", form.getProduct_vendor_name());
		params.put("product_name", form.getProduct_name());
		params.put("specification", form.getSpecification());
		params.put("sn", form.getSn());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("warehouseIds", warehouseIds);
		
		return getUsablePurchasingRecordProductList(params, curUser, type);
	}
	
	public List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(Map<String, Object> params,
			ContextUserInfo curUser, VoucherType type) {
		startPage();
		
		List<PurchasingRecordProduct> productList = mapper.selectNotControlUsablePurchasingRecordList(params);
		if(VoucherType.INSPECTION.equals(type) || VoucherType.PUT_IN.equals(type)) {
			for (PurchasingRecordProduct product : productList) {
				if(VoucherType.INSPECTION.equals(type)) {
					if(arrivalService.isExistArrivalRecord(product.getId(), RefVoucherType.PO_RECORDS)) {
						packageProduct(product);
					}
				}
				if(VoucherType.PUT_IN.equals(type)) {
					if(inspectionService.isExistInspectionRecord(product.getId(), RefVoucherType.PO_RECORDS, null)) {
						CheckPurchasingRecord record = inspectionService.getExistInspectionRecord(product.getId(), product.getBatch_number());
						if(!record.isExist()) {//有验货单,但还没有验货
							product.setOrder_quantity(BigDecimal.ZERO);
						}else {
							product.setOrder_quantity(record.getQualified_quantity());
							product.setWarehouse_inner_sn(record.getWarehouse_inner_sn());
							product.setWarehouse_name(record.getWarehouse_name());
							product.setMdrf_inner_sn(record.getMdrf_inner_sn());
							product.setMdrf_sn(record.getMdrf_sn());
							product.setProduce_date(record.getProduce_date());
							product.setOverdue_date(record.getOverdue_date());
							product.setInspection_detail_id(record.getInspection_detail_id());
						}
					}else {
						if(arrivalService.isExistArrivalRecord(product.getId(), RefVoucherType.PO_RECORDS)) {
							packageProduct(product);

						}
					}
				}
			}
		}
		return productList;
	}

	private void packageProduct(PurchasingRecordProduct product) {
		List<CheckPurchasingRecord> records = arrivalService.getExistArrivalRecord(product.getId(), product.getBatch_number());
		records = records.stream().filter(CheckPurchasingRecord::isExist).collect(Collectors.toList());
		if(records==null || records.isEmpty()) {//有收货单，但还没有收货
			product.setOrder_quantity(BigDecimal.ZERO);
		} else {
			BigDecimal quantityCount = records.stream().map(e->e.getArrival_quantity()).reduce(BigDecimal.ZERO,BigDecimal::add);
			product.setOrder_quantity(quantityCount);
			product.setWarehouse_inner_sn(records.get(0).getWarehouse_inner_sn());
			product.setWarehouse_name(records.get(0).getWarehouse_name());
			product.setArrival_detail_id(records.get(0).getArrival_detail_id());
		}
	}

	/*
	 * @Autowired UDIService udiService; public UsableProductInfo
	 * scanProductByBarcode(@Validated ScanBarcodeForm form, ContextUserInfo
	 * curUser, VoucherType voucherType) { UsableProductInfo result = new
	 * UsableProductInfo(); try { //解析条码 UDIDInfo udiInfo =
	 * udiService.parseBarcode(form.getBarcode()); if(udiInfo.getUdi_barcode() ==
	 * null) {//不包含产品信息 result.setOnlyOtherInfo(true); }else {//包含产品信息,则查询是否存在可用产品信息
	 * Map<String, Object> params = new HashMap<>(); params.put("org_id",
	 * curUser.getOrgId()); params.put("system_type",
	 * curUser.getSystemType().value()); params.put("bills_type",
	 * form.getBills_type().value()); params.put("organization_inner_sn",
	 * form.getOrganization_inner_sn()); params.put("warehouse_inner_sn",
	 * form.getWarehouse_inner_sn()); params.put("voucherType", voucherType);
	 * 
	 * params.put("product_vendor_inner_sn", udiInfo.getProduct_vendor_inner_sn());
	 * params.put("product_inner_sn", udiInfo.getProduct_inner_sn());
	 * params.put("specification_inner_sn", udiInfo.getSpecification_inner_sn());
	 * List<PurchasingRecordProduct> records =
	 * getUsablePurchasingRecordProductList(params, curUser, voucherType);
	 * if(StringUtils.isNotEmpty(records)) {
	 * if(VoucherType.INSPECTION.equals(voucherType) ||
	 * VoucherType.PUT_IN.equals(voucherType)) { for (PurchasingRecordProduct record
	 * : records) { record.setBatch_number(udiInfo.getBatch_number());
	 * record.setProduce_date(udiInfo.getProduce_date());
	 * record.setOverdue_date(udiInfo.getOverdue_date()); } }
	 * result.setUsableProductList(records); }else {
	 * result.setExistUsabelProduct(false); } } result.setProductBaseInfo(udiInfo);
	 * }catch(NotFoundUDIException e) { result.setExistUDI(false);
	 * result.setMsg(e.getMessage()); } return result; }
	 */
	
	protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }else {
        	PageHelper.clearPage();
        }
    }
}
