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
import com.yrt.common.dict.InspectionStatus;
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
import com.yrt.project.api.business.purchase.PurchaseGrnService;
import com.yrt.project.api.business.warehouse.vo.ScanBarcodeForm;
import com.yrt.project.api.business.warehouse.vo.UsableProductInfo;
import com.yrt.project.api.business.warehouse.vo.inspection.InspectionRecordDetailForm;
import com.yrt.project.api.business.warehouse.vo.inspection.InspectionRecordForm;
import com.yrt.project.api.business.warehouse.vo.inspection.SearchInspectionRecordForm;
import com.yrt.project.api.business.warehouse.vo.receipt.ArrivalRecordDetailForm;
import com.yrt.project.api.business.warehouse.vo.receipt.ArrivalRecordForm;
import com.yrt.project.api.common.service.UDIService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.common.vo.UDIDInfo;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.gsp.domain.GspSetup;
import com.yrt.project.modular.order.domain.PurchasingRecord;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.warehouse.domain.ArrivalRecord;
import com.yrt.project.modular.warehouse.domain.ArrivalRecordDetail;
import com.yrt.project.modular.warehouse.domain.CheckPurchasingRecord;
import com.yrt.project.modular.warehouse.domain.InspectionRecord;
import com.yrt.project.modular.warehouse.domain.InspectionRecordDetail;
import com.yrt.project.modular.warehouse.mapper.InspectionRecordMapper;

@Service
public class InspectionRecordService {

	@Autowired InspectionRecordMapper mapper;
	@Autowired VoucherNumberService numberUtils;
	
	@Autowired SysParameterService paramService;
	@Autowired PurchasingRecordService recordService;
	@Autowired ArrivalRecordService arrivalService;
	@Autowired PurchaseGrnService grnService;

	@Transactional
	public void autoCreateInspection(ArrivalRecord entity, ContextUserInfo curUser) {
		List<InspectionRecordDetailForm> inspectionDetails = new ArrayList<>();
		for (ArrivalRecordDetail detail : entity.getDetails()) {
			InspectionRecordDetailForm inspectionDetail = new InspectionRecordDetailForm();
			inspectionDetail.setProduct_vendor_inner_sn(detail.getProduct_vendor_inner_sn());
			inspectionDetail.setProduct_vendor_name(detail.getProduct_vendor_name());
			inspectionDetail.setProduct_inner_sn(detail.getProduct_inner_sn());
			inspectionDetail.setProduct_name(detail.getProduct_name());
			inspectionDetail.setSpecification(detail.getSpecification());
			inspectionDetail.setSpecification_inner_sn(detail.getSpecification_inner_sn());
			inspectionDetail.setUnit_inner_sn(detail.getUnit_inner_sn());
			inspectionDetail.setUnit(detail.getUnit());
			inspectionDetail.setBatch_number(detail.getBatch_number());
			inspectionDetail.setArrival_quantity(detail.getArrival_quantity());//收货数量
			inspectionDetail.setQualified_quantity(detail.getArrival_quantity());//合格数量=收货数量
			inspectionDetail.setUnqualified_quantity(BigDecimal.ZERO);//没有不合格产品
			inspectionDetail.setProduct_type(detail.getProduct_type());
			inspectionDetail.setVoucher_inner_sn_c(detail.getVoucher_inner_sn_c());
			inspectionDetail.setVoucher_type_c(detail.getVoucher_type_c());
			inspectionDetail.setArrival_detail_id(detail.getId());
			inspectionDetails.add(inspectionDetail);
		}
		InspectionRecordForm inspection = new InspectionRecordForm();
		inspection.setBills_type(entity.getBills_type());
		inspection.setOrganization_inner_sn(entity.getOrganization_inner_sn());
		inspection.setOrganization_name(entity.getOrganization_name());
		inspection.setWarehouse_inner_sn(entity.getWarehouse_inner_sn());
		inspection.setWarehouse_name(entity.getWarehouse_name());
		inspection.setDescription(entity.getDescription());
		inspection.setInspection_status(InspectionStatus.NOT_INSPECTED);
		inspection.setDetails(inspectionDetails);
		insertInspectionRecord(inspection, curUser, true, curUser.getEmployee().getName());
	}
	
	@Transactional
	public InspectionRecord insertInspectionRecord(@Validated InspectionRecordForm form, ContextUserInfo curUser, boolean isAutoCreate, String created_by) {
		String setupStr = paramService.getGSPSetup(curUser.getSystemType(), curUser.getOrgId());
		GspSetup gsp = JSONObject.parseObject(setupStr, GspSetup.class);
		boolean isControl = gsp.getProcurementProcess().isStrictControl();
		boolean updatePurchasingRecord = false;
		List<InspectionRecordDetail> recordDetails = new ArrayList<>();
		String sn = numberUtils.getMaxSn(VoucherType.INSPECTION, curUser.getSystemType(), curUser.getOrgId());
		InspectionRecord entity = Utils.clone(form, InspectionRecord.class);
		entity.setId(CompressedIdGenerator.generateId());
		entity.setOrg_id(curUser.getOrgId());
		entity.setSystem_type(curUser.getSystemType().value());
		entity.setSn(sn);
		entity.setCreated_by(isAutoCreate?created_by:curUser.getEmployee().getName());
		entity.setCreated_time(new Date());
		if(InspectionStatus.INSPECTED.equals(form.getInspection_status())) {
			entity.setInspection_by(isAutoCreate?created_by:curUser.getEmployee().getName());
			entity.setInspection_time(new Date());
			updatePurchasingRecord = true;
		}
		mapper.insertInspectionRecord(entity);
		List<InspectionRecordDetailForm> details = form.getDetails();
		saveRecordDetail(entity.getId(), details, recordDetails, updatePurchasingRecord, isAutoCreate?created_by:curUser.getEmployee().getName(), isAutoCreate);
		entity.setDetails(recordDetails);
		//TODO:自动创建收货单
		if(InspectionStatus.INSPECTED.equals(form.getInspection_status()) && !isControl) {
			ArrivalRecordForm arrivalRecord = Utils.clone(form, ArrivalRecordForm.class);
			arrivalRecord.setArrival_status(ArrivalStatus.RECEIVED);
			List<ArrivalRecordDetailForm> arrivalDetails = new ArrayList<>();
			for (InspectionRecordDetailForm detail : details) {
				if(RefVoucherType.PO_RECORDS.equals(detail.getVoucher_type_c())) {
					if(!arrivalService.isExistArrivalRecord(detail.getVoucher_inner_sn_c(), RefVoucherType.PO_RECORDS)) {//没有生成过收货单
						ArrivalRecordDetailForm detailForm = Utils.clone(detail, ArrivalRecordDetailForm.class);
						detailForm.setArrival_quantity(detail.getQualified_quantity().add(detail.getUnqualified_quantity()));//合格数量+不合格数量=收货数量
						detailForm.setOrder_quantity(detail.getArrival_quantity());//订单数量=验货明细的收货数量
						detailForm.setInspection_detail_id(detail.getId());
						arrivalDetails.add(detailForm);
					}
				}
			}
			if(Utils.isNotEmpty(arrivalDetails)) {
				arrivalRecord.setDetails(arrivalDetails);
				arrivalService.insertArrivalRecord(arrivalRecord, curUser, true, gsp.getProcurementProcess().getEmployment(arrivalRecord.getWarehouse_inner_sn())[0]);
			}
		}
		if (isAutoCreate && !isControl) {// 不是GSP流程时，自动生成验货单需要回写验货单明细ID到入库单明细中
			for (InspectionRecordDetailForm detail : details) {
				grnService.updatePurchaseGrnDetailInspectionDetailId(curUser.getOrgId(), detail.getGrn_id(),
						detail.getGrn_detail_id(), detail.getId(), form.getBills_type());
			}
		}
		if(isControl && updatePurchasingRecord) {
			grnService.autoCreatePurchaseGrn(entity, curUser);
		}
		return entity;
	}

	@Transactional
	private void saveRecordDetail(Long inspectionRecordId, List<InspectionRecordDetailForm> details,
			List<InspectionRecordDetail> recordDetails, boolean updatePurchasingRecord, String update_by, boolean isAutoCreate) {
		Map<Long, BigDecimal[]> voucherMap = new HashMap<>();
		List<Long> arrivalIdList = new ArrayList<>();
		for (InspectionRecordDetailForm detailForm : details) {
			Long arrivalDetailId = detailForm.getArrival_detail_id();
			Long voucherId = detailForm.getVoucher_inner_sn_c();
			BigDecimal formQualifiedQuantity = detailForm.getQualified_quantity() == null?BigDecimal.ZERO:detailForm.getQualified_quantity();
			BigDecimal formUnqualifiedQuantity = detailForm.getUnqualified_quantity() == null?BigDecimal.ZERO:detailForm.getUnqualified_quantity();
			InspectionRecordDetail detail = Utils.clone(detailForm, InspectionRecordDetail.class);
			Long inspectionDetailId = CompressedIdGenerator.generateId();
			detail.setId(inspectionDetailId);
			detail.setInspection_record_id(inspectionRecordId);
			detail.setArrival_detail_id(arrivalDetailId);
			detailForm.setId(inspectionDetailId);
			if(RefVoucherType.PO_RECORDS.equals(detailForm.getVoucher_type_c())) {
				if(voucherMap.containsKey(voucherId)) {
					BigDecimal[] values = voucherMap.get(voucherId);
					BigDecimal qualifedQuantity = values[0].add(formQualifiedQuantity);
					BigDecimal unqualifedQuantity = values[1].add(formUnqualifiedQuantity);
					BigDecimal[] newValues = new BigDecimal[] {qualifedQuantity, unqualifedQuantity};
					voucherMap.put(voucherId, newValues);
				}else {
					BigDecimal[] values = new BigDecimal[] {formQualifiedQuantity, formUnqualifiedQuantity};
					voucherMap.put(voucherId, values);
				}
				if(arrivalDetailId != null && !arrivalIdList.contains(arrivalDetailId)) {
					arrivalIdList.add(arrivalDetailId);
				}
			}
			mapper.insertInspectionRecordDetail(detail);
			if(recordDetails != null) {
				recordDetails.add(detail);
			}
		}
		//TODO: 更新验货数量到对应的收货单
		arrivalIdList.forEach((key) -> {
			BigDecimal value = mapper.sumInspectionRecordDetailByArrival(key);
			arrivalService.updateArrivalRecordDetailFinishQuantity(key, value, false);
		});
		//TODO: 更新验货数量到采购记录
		if (updatePurchasingRecord) {
			voucherMap.forEach((key, value) -> {
				PurchasingRecord record = recordService.getPurchasingRecordInfo(key);
				BigDecimal arrivalQuantity = BigDecimal.ZERO.compareTo(record.getArrival_quantity()) == 0
						? record.getQuantity()
						: record.getArrival_quantity();
				BigDecimal newQuantity = value[0].add(value[1]);
				BigDecimal afterQuantity = record.getQualified_quantity().add(record.getUnqualified_quantity())
						.add(newQuantity);//原合格数+原不合格数+新更新的合格数+新更新的不合格数
				if (!isAutoCreate && afterQuantity.compareTo(arrivalQuantity) > 0)
					throw new ValidateException(String.format("合格数量+不合格数量不能大于(%s)收货数量!",arrivalQuantity));
				recordService.updatePurchasingRecordInspectionQuantity(key, value[0], value[1], update_by, false, null);
			});
		}
	}

	@Transactional
	public InspectionRecord updateInspectionRecord(@Validated InspectionRecordForm form, ContextUserInfo curUser) {
		checkStatus(form.getId(), form.getInspection_status(), false, false);
		String setupStr = paramService.getGSPSetup(curUser.getSystemType(), curUser.getOrgId());
		GspSetup gsp = JSONObject.parseObject(setupStr, GspSetup.class);
		boolean isControl = gsp.getProcurementProcess().isStrictControl();
		boolean updatePurchasingRecord = false;
		Map<String,Object> params = new HashMap<>();
		params.put("id", form.getId());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("organization_name", form.getOrganization_name());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("warehouse_name", form.getWarehouse_name());
		params.put("description", form.getDescription());
		params.put("inspection_status", form.getInspection_status());
		if(InspectionStatus.INSPECTED.equals(form.getInspection_status())) {
			params.put("inspection_by", curUser.getEmployee().getName());
			params.put("inspection_time", new Date());
			updatePurchasingRecord = true;
		}
		mapper.updateInspectionRecord(params);
		params.clear();
		params.put("inspection_record_id", form.getId());
		mapper.deleteInspectionRecordDetail(params);
		List<InspectionRecordDetailForm> details = form.getDetails();
		saveRecordDetail(form.getId(), details, null, updatePurchasingRecord, curUser.getEmployee().getName(), false);
		//TODO:自动创建收货单
		if(InspectionStatus.INSPECTED.equals(form.getInspection_status()) 
				&&!isControl) {
			ArrivalRecordForm arrivalRecord = Utils.clone(form, ArrivalRecordForm.class);
			arrivalRecord.setArrival_status(ArrivalStatus.RECEIVED);
			List<ArrivalRecordDetailForm> arrivalDetails = new ArrayList<>();
			for (InspectionRecordDetailForm detail : details) {
				if(RefVoucherType.PO_RECORDS.equals(detail.getVoucher_type_c())) {
					if(!arrivalService.isExistArrivalRecord(detail.getVoucher_inner_sn_c(), RefVoucherType.PO_RECORDS)) {//没有生成过收货单
						ArrivalRecordDetailForm detailForm = Utils.clone(detail, ArrivalRecordDetailForm.class);
						detailForm.setArrival_quantity(detail.getQualified_quantity().add(detail.getUnqualified_quantity()));//合格数量+不合格数量=收货数量
						detailForm.setOrder_quantity(detail.getArrival_quantity());//订单数量=验货明细的收货数量
						arrivalDetails.add(detailForm);
					}
				}
			}
			if(Utils.isNotEmpty(arrivalDetails)) {
				arrivalRecord.setDetails(arrivalDetails);
				arrivalService.insertArrivalRecord(arrivalRecord, curUser, true, gsp.getProcurementProcess().getEmployment(arrivalRecord.getWarehouse_inner_sn())[0]);
			}
		}
		InspectionRecord entity = getInspectionRecordInfo(form.getId());
		if(isControl && updatePurchasingRecord) {
			grnService.autoCreatePurchaseGrn(entity, curUser);
		}
		return entity;
	}

	@Transactional
	public void updateInspectionRecordDetailFinishQuantity(Long inspectionDetailId, BigDecimal finishQuantity, boolean rollback) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", inspectionDetailId);
		InspectionRecordDetail detail = mapper.selectInspectionRecordDetailList(params).get(0);
		finishQuantity = finishQuantity == null?BigDecimal.ZERO:finishQuantity;
		BigDecimal newQuantity = BigDecimal.ZERO;
		if(rollback) {
			newQuantity = detail.getFinish_quantity().subtract(finishQuantity);
		}else {
			newQuantity = finishQuantity;
		}
		params.put("finish_quantity", newQuantity);
		mapper.updateInspectionRecordDetail(params);
	}
	
	@Transactional
	public void updateInspectionRecordDetailArrivalDetailId(Long inspectionDetailId, Long arrivalDetailId) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", inspectionDetailId);
		params.put("arrival_detail_id", arrivalDetailId);
		mapper.updateInspectionRecordDetail(params);
	}
	
	@Transactional
	public int deleteInspectionRecord(Long id) {
		checkStatus(id, null, false, true);
		Map<String,Object> params = new HashMap<>();
		params.put("inspection_record_id", id);
		Map<Long, BigDecimal> arrivalMap = new HashMap<>();
		List<InspectionRecordDetail> details = mapper.selectInspectionRecordDetailList(params);
		for (InspectionRecordDetail detail : details) {
			Long arrivalDetailId = detail.getArrival_detail_id();
			BigDecimal formQualifiedQuantity = detail.getQualified_quantity() == null?BigDecimal.ZERO:detail.getQualified_quantity();
			BigDecimal formUnqualifiedQuantity = detail.getUnqualified_quantity() == null?BigDecimal.ZERO:detail.getUnqualified_quantity();
			if(arrivalMap.containsKey(arrivalDetailId)) {
				BigDecimal value = arrivalMap.get(arrivalDetailId);
				BigDecimal newValue = value.add(formQualifiedQuantity.add(formUnqualifiedQuantity));
				arrivalMap.put(arrivalDetailId, newValue);
			}else {
				if(arrivalDetailId != null) {
					arrivalMap.put(arrivalDetailId, formQualifiedQuantity.add(formUnqualifiedQuantity));
				}
			}
		}
		//TODO: 更新验货数量到对应的收货单
		arrivalMap.forEach((key,value) -> {
			arrivalService.updateArrivalRecordDetailFinishQuantity(key, value, true);
		});
		mapper.deleteInspectionRecordDetail(params);
		
		params.put("id", id);
		mapper.deleteInspectionRecord(params);
		return 1;
	}

	@Transactional
	public int approvalInspectionRecord(Long id, ContextUserInfo curUser) {
		checkStatus(id, InspectionStatus.REVIEWED, false, false);
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("inspection_status", InspectionStatus.REVIEWED);
		params.put("confirm_by", curUser.getEmployee().getName());
		params.put("confirm_time", new Date());
		mapper.updateInspectionRecord(params);
		return 1;
	}

	@Transactional
	public int cancelInspectionRecord(Long id, String reason, ContextUserInfo curUser) {
		checkStatus(id, null, true, false);
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("is_cancel", 1);
		params.put("cancel_reason", reason);
		params.put("cancel_by", curUser.getEmployee().getName());
		params.put("cancel_time", new Date());
		mapper.updateInspectionRecord(params);
		//TODO: 回滚已经更新验货数量的采购记录
		Map<Long, BigDecimal[]> voucherMap = new HashMap<>();
		params.clear();
		params.put("inspection_record_id", id);
		List<InspectionRecordDetail> details = mapper.selectInspectionRecordDetailList(params);
		List<Long> inspectionDetailIdList = new ArrayList<>();
		for (InspectionRecordDetail detail : details) {
			Long voucherId = detail.getVoucher_inner_sn_c();
			if(RefVoucherType.PO_RECORDS.equals(detail.getVoucher_type_c())) {
				if(voucherMap.containsKey(voucherId)) {
					BigDecimal[] values = voucherMap.get(voucherId);
					BigDecimal qualifedQuantity = values[0].add(detail.getQualified_quantity());
					BigDecimal unqualifedQuantity = values[1].add(detail.getUnqualified_quantity());
					BigDecimal[] newValues = new BigDecimal[] {qualifedQuantity, unqualifedQuantity};
					voucherMap.put(voucherId, newValues);
				}else {
					BigDecimal[] values = new BigDecimal[] {detail.getQualified_quantity(), detail.getUnqualified_quantity()};
					voucherMap.put(voucherId, values);
				}
			}
			inspectionDetailIdList.add(detail.getId());
		}
		voucherMap.forEach((key,value)->{
			recordService.updatePurchasingRecordInspectionQuantity(key, value[0], value[1], curUser.getEmployee().getName(), true, inspectionDetailIdList);
		});
		return 1;
	}

	public List<InspectionRecord> searchInspectionRecordList(SearchInspectionRecordForm form, ContextUserInfo curUser, List<Long> warehouseIds) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType().value());
		params.put("bills_type", form.getBills_type());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("sn", form.getSn());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		params.put("warehouseIds", warehouseIds);
		params.put("inspection_status", form.isQualityControl()?1:0);
		if(form.isHistory()) {
			params.put("history", 1);
			params.put("inspection_status", form.isQualityControl()?"2":"1,2");
		}else {
			params.put("history", 0);
		}
		return mapper.selectInspectionRecordList(params);
	}
	
	public InspectionRecord getInspectionRecordInfo(Long id) {
		InspectionRecord entity = mapper.selectInspectionRecordInfo(id);
		if(Utils.isEmpty(entity)) {
			throw new ValidateException("错误的验货单ID!!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("inspection_record_id", id);
		List<InspectionRecordDetail> details = mapper.selectInspectionRecordDetailList(params);
		entity.setDetails(details);
		return entity;
	}

	private void checkStatus(Long id, InspectionStatus newStatus, boolean isCancel, boolean isDelete) {
		if(Utils.isEmpty(id)) {
			throw new ValidateException("验货单ID不能为空!!");
		}
		InspectionRecord info = mapper.selectInspectionRecordInfo(id);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的验货单ID!!");
		}
		if(isCancel || isDelete) {
			if(isCancel) {
				if(!InspectionStatus.INSPECTED.equals(info.getInspection_status())) {
					throw new ValidateException("已验货状态的验货单才能作废!!");
				}
			}else {
				if(!InspectionStatus.NOT_INSPECTED.equals(info.getInspection_status())) {
					throw new ValidateException("未验货状态的验货单才能删除!!");
				}
			}
		}else {
			if(InspectionStatus.REVIEWED.equals(newStatus) && !InspectionStatus.INSPECTED.equals(info.getInspection_status())) {
				throw new ValidateException("已验货状态的验货单才能复核!!");
			}
			if(newStatus.value() < info.getInspection_status().value()) {
				throw new ValidateException("验货单的状态有错误!!");
			}
		}
	}

	public List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(SearchPurchasingRecordProductForm form,
			ContextUserInfo curUser, VoucherType voucherType, List<Long> warehouseIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType());
		params.put("bills_type", form.getBills_type());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("voucherType", voucherType.value());

		params.put("product_vendor_name", form.getProduct_vendor_name());
		params.put("product_name", form.getProduct_name());
		params.put("specification", form.getSpecification());
		params.put("sn", form.getSn());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("warehouseIds", warehouseIds);
		
		return getUsablePurchasingRecordProductList(params, curUser, VoucherType.INSPECTION);
	}
	
	private List<PurchasingRecordProduct> getUsablePurchasingRecordProductList(Map<String, Object> params,
			ContextUserInfo curUser, VoucherType inspectionRecord){
		String setupStr = paramService.getGSPSetup(curUser.getSystemType(), curUser.getOrgId());
		GspSetup gsp = JSONObject.parseObject(setupStr, GspSetup.class);
		boolean isControl = gsp.getProcurementProcess().isStrictControl();
		startPage();
		if(isControl) {
			return mapper.selectControlUsablePurchasingRecordList(params);
		}else {
			return recordService.getUsablePurchasingRecordProductList(params, curUser, inspectionRecord);
		}
	}
	
	public CheckPurchasingRecord getExistInspectionRecord(Long voucher_inner_sn_c, String batch_number) {
		Map<String,Object> params = new HashMap<>();
		params.put("voucher_inner_sn_c", voucher_inner_sn_c);
		params.put("batch_number", batch_number);
		CheckPurchasingRecord record = mapper.selectExistInspectionRecord(params);
		return StringUtils.isNull(record)?new CheckPurchasingRecord():record;
	}
	
	public boolean isExistInspectionRecord(Long voucher_inner_sn_c, RefVoucherType voucher_type_c, Long arrivalDetailId) {
		Map<String,Object> params = new HashMap<>();
		params.put("voucher_inner_sn_c", voucher_inner_sn_c);
		params.put("voucher_type_c", voucher_type_c);
		params.put("arrival_detail_id", arrivalDetailId);
		Boolean res = mapper.isExistInspectionRecord(params);
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
				params.put("voucherType", VoucherType.INSPECTION.value());

				params.put("product_vendor_inner_sn", udiInfo.getProduct_vendor_inner_sn());
				params.put("product_inner_sn", udiInfo.getProduct_inner_sn());
				params.put("specification_inner_sn", udiInfo.getSpecification_inner_sn());
				params.put("warehouseIds", warehouseIds);
				List<PurchasingRecordProduct> records = getUsablePurchasingRecordProductList(params, curUser, VoucherType.INSPECTION);
				if(StringUtils.isNotEmpty(records)) {
//					for (PurchasingRecordProduct record : records) {
//						record.setBatch_number(udiInfo.getBatch_number());
//						record.setProduce_date(udiInfo.getProduce_date());
//						record.setOverdue_date(udiInfo.getOverdue_date());
//					}
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
