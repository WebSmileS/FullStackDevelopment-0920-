package com.yrt.project.api.business.warehouse.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.yrt.common.dict.ArrivalStatus;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.NotFoundUDIException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.order.service.PurchasingRecordService;
import com.yrt.project.api.business.order.vo.purchasingRecord.SearchPurchasingRecordProductForm;
import com.yrt.project.api.business.warehouse.vo.ScanBarcodeForm;
import com.yrt.project.api.business.warehouse.vo.UsableProductInfo;
import com.yrt.project.api.business.warehouse.vo.receipt.ArrivalRecordDetailForm;
import com.yrt.project.api.business.warehouse.vo.receipt.ArrivalRecordForm;
import com.yrt.project.api.business.warehouse.vo.receipt.SearchArrivalRecordForm;
import com.yrt.project.api.common.service.UDIService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.common.vo.UDIDInfo;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.gsp.domain.GspSetup;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.warehouse.domain.ArrivalRecord;
import com.yrt.project.modular.warehouse.domain.ArrivalRecordDetail;
import com.yrt.project.modular.warehouse.domain.CheckPurchasingRecord;
import com.yrt.project.modular.warehouse.mapper.ArrivalRecordMapper;

@Service
public class ArrivalRecordService {
	
	@Autowired ArrivalRecordMapper mapper;
	@Autowired VoucherNumberService numberUtils;
	
	@Autowired SysParameterService paramService;
	@Autowired PurchasingRecordService recordService;
	@Autowired InspectionRecordService inspectionService;

	@Transactional
	public ArrivalRecord insertArrivalRecord(@Validated ArrivalRecordForm form, ContextUserInfo curUser, boolean isAutoCreate, String created_by) {
		boolean updatePurchasingRecord = false;
		List<ArrivalRecordDetail> recordDetails = new ArrayList<>();
		String sn = numberUtils.getMaxSn(VoucherType.ARRIVAL_RECORD, curUser.getSystemType(), curUser.getOrgId());
		ArrivalRecord entity = Utils.clone(form, ArrivalRecord.class);
		entity.setId(CompressedIdGenerator.generateId());
		entity.setOrg_id(curUser.getOrgId());
		entity.setSystem_type(curUser.getSystemType().value());
		entity.setSn(sn);
		entity.setCreated_by(isAutoCreate?created_by:curUser.getEmployee().getName());
		entity.setCreated_time(new Date());
		if(ArrivalStatus.RECEIVED.equals(form.getArrival_status())) {
			entity.setArrival_by(isAutoCreate?created_by:curUser.getEmployee().getName());
			entity.setArrival_time(new Date());
			updatePurchasingRecord = true;
		}
		mapper.insertArrivalRecord(entity);
		List<ArrivalRecordDetailForm> details = form.getDetails();
		saveRecordDetail(entity.getId(), details, recordDetails, updatePurchasingRecord, isAutoCreate?created_by:curUser.getEmployee().getName());
		entity.setDetails(recordDetails);
		String setupStr = paramService.getGSPSetup(curUser.getSystemType(), curUser.getOrgId());
		GspSetup gsp = JSONObject.parseObject(setupStr, GspSetup.class);
		boolean isControl = gsp.getProcurementProcess().isStrictControl();
		if(isControl && updatePurchasingRecord && !isAutoCreate) {
			inspectionService.autoCreateInspection(entity, curUser);
		}
		if(isAutoCreate && !isControl) {//不是GSP流程时，自动生成收货单需要回写收货单明细ID到验货单明细中
			for (ArrivalRecordDetailForm detail : details) {
				inspectionService.updateInspectionRecordDetailArrivalDetailId(detail.getInspection_detail_id(), detail.getId());
			}
		}
		return entity;
	}

	@Transactional
	void saveRecordDetail(Long arrivalRecordId, List<ArrivalRecordDetailForm> details,
						  List<ArrivalRecordDetail> recordDetails, boolean updatePurchasingRecord, String update_by) {
		Map<Long, BigDecimal> voucherMap = new HashMap<>();
		for (ArrivalRecordDetailForm detailForm : details) {
			Long voucherId = detailForm.getVoucher_inner_sn_c();
			if(BigDecimal.ZERO.compareTo(detailForm.getArrival_quantity()) >= 0) throw new ValidateException("收货数量必须大于0");
			if(BigDecimal.ZERO.compareTo(detailForm.getOrder_quantity()) >= 0) throw new ValidateException("订单数量必须大于0");
			if(RefVoucherType.PO_RECORDS.equals(detailForm.getVoucher_type_c())) {
				if(voucherMap.containsKey(voucherId)) {
					BigDecimal value = voucherMap.get(voucherId).add(detailForm.getArrival_quantity());
					voucherMap.put(voucherId, value);
				}else {
					voucherMap.put(voucherId, detailForm.getArrival_quantity());
				}
			}
			Long arrivalDetailId = CompressedIdGenerator.generateId();
			detailForm.setId(arrivalRecordId);
			ArrivalRecordDetail detail = Utils.clone(detailForm, ArrivalRecordDetail.class);
			detail.setId(arrivalDetailId);
			detail.setArrival_record_id(arrivalRecordId);
			mapper.insertArrivalRecordDetail(detail);
			if(recordDetails != null) {
				recordDetails.add(detail);
			}
		}
		//TODO: 更新收货数量到采购记录
		if(updatePurchasingRecord) {
			voucherMap.forEach((key,value)->{
				recordService.updatePurchasingRecordArrivalQuantity(key, value, update_by, false, null);
			});
		}
	}

	@Transactional
	public ArrivalRecord updateArrivalRecord(@Validated ArrivalRecordForm form, ContextUserInfo curUser) {
		checkStatus(form.getId(), form.getArrival_status(), false, false);
		boolean updatePurchasingRecord = false;
		Map<String,Object> params = new HashMap<>();
		params.put("id", form.getId());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("organization_name", form.getOrganization_name());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("warehouse_name", form.getWarehouse_name());
		params.put("deliverymen_name", form.getDeliverymen_name());
		params.put("description", form.getDescription());
		params.put("arrival_status", form.getArrival_status());
		if(ArrivalStatus.RECEIVED.equals(form.getArrival_status())) {
			params.put("arrival_by", curUser.getEmployee().getName());
			params.put("arrival_time", new Date());
			updatePurchasingRecord = true;
		}
		mapper.updateArrivalRecord(params);
		params.clear();
		params.put("arrival_record_id", form.getId());
		mapper.deleteArrivalRecordDetail(params);
		List<ArrivalRecordDetailForm> details = form.getDetails();
		saveRecordDetail(form.getId(), details, null, updatePurchasingRecord, curUser.getEmployee().getName());
		
		ArrivalRecord entity = getArrivalRecordInfo(form.getId());
		String setupStr = paramService.getGSPSetup(curUser.getSystemType(), curUser.getOrgId());
		GspSetup gsp = JSONObject.parseObject(setupStr, GspSetup.class);
		boolean isControl = gsp.getProcurementProcess().isStrictControl();
		if(isControl && updatePurchasingRecord) {
			inspectionService.autoCreateInspection(entity, curUser);
		}
		return entity;
	}
	
	@Transactional
	public void updateArrivalRecordDetailFinishQuantity(Long arrivalDetailId, BigDecimal finishQuantity, boolean rollback) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", arrivalDetailId);
		ArrivalRecordDetail detail = mapper.selectArrivalRecordDetailList(params).get(0);
		finishQuantity = finishQuantity == null?BigDecimal.ZERO:finishQuantity;
		BigDecimal newQuantity = BigDecimal.ZERO;
		if(rollback) {
			newQuantity = detail.getFinish_quantity().subtract(finishQuantity);
		}else {
			newQuantity = finishQuantity;
		}
		params.put("finish_quantity", newQuantity);
		mapper.updateArrivalRecordDetail(params);
	}

	@Transactional
	public int deleteArrivalRecord(Long id) {
		checkStatus(id, null, false, true);
		Map<String,Object> params = new HashMap<>();
		params.put("arrival_record_id", id);
		mapper.deleteArrivalRecordDetail(params);
		params.put("id", id);
		mapper.deleteArrivalRecord(params);
		return 1;
	}

	@Transactional
	public int approvalArrivalRecord(Long id, ContextUserInfo curUser) {
		checkStatus(id, ArrivalStatus.REVIEWED, false, false);
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("arrival_status", ArrivalStatus.REVIEWED);
		params.put("confirm_by", curUser.getEmployee().getName());
		params.put("confirm_time", new Date());
		mapper.updateArrivalRecord(params);
		return 1;
	}

	@Transactional
	public int cancelArrivalRecord(Long id, String reason, ContextUserInfo curUser) {
		checkStatus(id, null, true, false);
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("is_cancel", 1);
		params.put("cancel_reason", reason);
		params.put("cancel_by", curUser.getEmployee().getName());
		params.put("cancel_time", new Date());
		mapper.updateArrivalRecord(params);
		//TODO: 回滚已经更新收货数量的采购记录
		Map<Long, BigDecimal> voucherMap = new HashMap<>();
		params.clear();
		params.put("arrival_record_id", id);
		List<ArrivalRecordDetail> details = mapper.selectArrivalRecordDetailList(params);
		List<Long> arrivalDetailIdList = new ArrayList<>();
		for (ArrivalRecordDetail detail : details) {
			Long voucherId = detail.getVoucher_inner_sn_c();
			if(RefVoucherType.PO_RECORDS.equals(detail.getVoucher_type_c())) {
				if(voucherMap.containsKey(voucherId)) {
					BigDecimal value = voucherMap.get(voucherId).add(detail.getArrival_quantity());
					voucherMap.put(voucherId, value);
				}else {
					voucherMap.put(voucherId, detail.getArrival_quantity());
				}
			}
			arrivalDetailIdList.add(detail.getId());
		}
		voucherMap.forEach((key,value)->{
			recordService.updatePurchasingRecordArrivalQuantity(key, value, curUser.getEmployee().getName(), true, arrivalDetailIdList);
		});
		return 1;
	}

	public List<ArrivalRecord> searchArrivalRecordList(SearchArrivalRecordForm form, ContextUserInfo curUser, List<Long> warehouseIds) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType().value());
		params.put("bills_type", form.getBills_type());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("deliverymen_name", form.getDeliverymen_name());
		params.put("sn", form.getSn());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		params.put("arrival_status", form.isQualityControl()?1:0);
		params.put("warehouseIds", warehouseIds);
		if(form.isHistory()) {
			params.put("history", 1);
			params.put("arrival_status", form.isQualityControl()?"2":"1,2");
		}else {
			params.put("history", 0);
		}
		return mapper.selectArrivalRecordList(params);
	}
	
	public ArrivalRecord getArrivalRecordInfo(Long id) {
		ArrivalRecord entity = mapper.selectArrivalRecordInfo(id);
		if(Utils.isEmpty(entity)) {
			throw new ValidateException("错误的收货单ID!!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("arrival_record_id", id);
		List<ArrivalRecordDetail> details = mapper.selectArrivalRecordDetailList(params);
		entity.setDetails(details);
		return entity;
	}

	private void checkStatus(Long id, ArrivalStatus newStatus, boolean isCancel, boolean isDelete) {
		if(Utils.isEmpty(id)) {
			throw new ValidateException("收货单ID不能为空!!");
		}
		ArrivalRecord info = mapper.selectArrivalRecordInfo(id);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的收货单ID!!");
		}
		if(isCancel || isDelete) {
			if(isCancel) {
				if(!ArrivalStatus.RECEIVED.equals(info.getArrival_status())) {
					throw new ValidateException("已收货状态的收货单才能作废!!");
				}
			}else {
				if(!ArrivalStatus.NOT_RECEIVED.equals(info.getArrival_status())) {
					throw new ValidateException("未收货状态的收货单才能删除!!");
				}
			}
		}else {
			if(ArrivalStatus.REVIEWED.equals(newStatus) && !ArrivalStatus.RECEIVED.equals(info.getArrival_status())) {
				throw new ValidateException("已收货状态的收货单才能复核!!");
			}
			if(newStatus.value() < info.getArrival_status().value()) {
				throw new ValidateException("收货单的状态有错误!!");
			}
		}
	}

	public List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(SearchPurchasingRecordProductForm form,
			ContextUserInfo curUser, VoucherType voucherType, List<Long> warehouseIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType());
		params.put("bills_type", form.getBills_type());
		params.put("voucherType", voucherType.value());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());

		params.put("product_vendor_name", form.getProduct_vendor_name());
		params.put("product_name", form.getProduct_name());
		params.put("specification", form.getSpecification());
		params.put("sn", form.getSn());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("warehouseIds", warehouseIds);
		return getUsablePurchasingRecordProductList(params, curUser, voucherType);
	}
	
	private List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(Map<String, Object> params,
			ContextUserInfo curUser, VoucherType voucherType) {
		String setupStr = paramService.getGSPSetup(curUser.getSystemType(), curUser.getOrgId());
		GspSetup gsp = JSONObject.parseObject(setupStr, GspSetup.class);
		boolean isControl = gsp.getProcurementProcess().isStrictControl();
		startPage();
		if(isControl) {
			return mapper.selectControlUsablePurchasingRecordList(params);
		}else{
			return recordService.getUsablePurchasingRecordProductList(params, curUser, voucherType);
		}
	}
	
	public List<CheckPurchasingRecord> getExistArrivalRecord(Long voucher_inner_sn_c, String batch_number) {
		Map<String,Object> params = new HashMap<>();
		params.put("voucher_inner_sn_c", voucher_inner_sn_c);
		params.put("batch_number", batch_number);
		List<CheckPurchasingRecord> records = mapper.selectExistArrivalRecord(params);
		return records;
	}
	
	public boolean isExistArrivalRecord(Long voucher_inner_sn_c, RefVoucherType voucher_type_c) {
		Map<String,Object> params = new HashMap<>();
		params.put("voucher_inner_sn_c", voucher_inner_sn_c);
		params.put("voucher_type_c", voucher_type_c.value());
		Boolean res = mapper.isExistArrivalRecord(params);
		return res == null?false:res;
	}

	@Autowired UDIService udiService;
	public UsableProductInfo scanProductByBarcode(@Validated ScanBarcodeForm form, ContextUserInfo curUser, List<Long> warehouseIds) {
		UsableProductInfo result = new UsableProductInfo();
		try {
			//解析条码
			UDIDInfo udiInfo = udiService.parseBarcode(form.getBarcode(), true);
			if(udiInfo.getUdi_barcode() == null) {//不包含产品信息
				result.setOnlyOtherInfo(true);
				result.setExistUsabelProduct(false);
			}else {//包含产品信息,则查询是否存在可用产品信息
				Map<String, Object> params = new HashMap<>();
				params.put("org_id", curUser.getOrgId());
				params.put("system_type", curUser.getSystemType());
				params.put("bills_type", form.getBills_type());
				params.put("organization_inner_sn", form.getOrganization_inner_sn());
				params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
				params.put("voucherType", VoucherType.ARRIVAL_RECORD.value());

				params.put("product_vendor_inner_sn", udiInfo.getProduct_vendor_inner_sn());
				params.put("product_inner_sn", udiInfo.getProduct_inner_sn());
				params.put("specification_inner_sn", udiInfo.getSpecification_inner_sn());
				params.put("warehouseIds", warehouseIds);
				List<PurchasingRecordProduct> records = getUsablePurchasingRecordProductList(params, curUser, VoucherType.ARRIVAL_RECORD);
				if(StringUtils.isNotEmpty(records)) {
					result.setUsableProductList(records);
				}else {
					result.setExistUsabelProduct(false);
				}
			}
			result.setProductBaseInfo(udiInfo);
		}catch(NotFoundUDIException e) {
			result.setExistUDI(false);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	/**
     * 设置请求分页数据
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
