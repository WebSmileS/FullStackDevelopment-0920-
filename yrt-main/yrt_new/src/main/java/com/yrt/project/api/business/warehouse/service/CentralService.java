package com.yrt.project.api.business.warehouse.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.GrnNoticeStatus;
import com.yrt.common.dict.LogisticsMode;
import com.yrt.common.dict.LogisticsStatus;
import com.yrt.common.dict.OdoNoticeStatus;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.warehouse.vo.central.AddNoticeForm;
import com.yrt.project.api.business.warehouse.vo.central.LogisticsForm;
import com.yrt.project.api.business.warehouse.vo.central.NoticeDetailForm;
import com.yrt.project.api.business.warehouse.vo.central.SearchLogisticsForm;
import com.yrt.project.api.business.warehouse.vo.central.SearchNoticeForm;
import com.yrt.project.api.business.warehouse.vo.central.SearchNoticeProductForm;
import com.yrt.project.api.business.warehouse.vo.central.SearchWarehouseProductForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.common.mapper.DDCommMapper;
import com.yrt.project.modular.warehouse.domain.GrnNotice;
import com.yrt.project.modular.warehouse.domain.InventoryProduct;
import com.yrt.project.modular.warehouse.domain.Lease;
import com.yrt.project.modular.warehouse.domain.OdoLogisticsRecord;
import com.yrt.project.modular.warehouse.domain.OdoNotice;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import com.yrt.project.modular.warehouse.mapper.DealerInventoryMapper;
import com.yrt.project.modular.warehouse.mapper.DealerWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.GrnNoticeMapper;
import com.yrt.project.modular.warehouse.mapper.LeaseMapper;
import com.yrt.project.modular.warehouse.mapper.LogisticsMapper;
import com.yrt.project.modular.warehouse.mapper.OdoNoticeMapper;

@Service
public class CentralService {

	@Autowired LeaseMapper leaseMapper;
	@Autowired GrnNoticeMapper grnMapper;
	@Autowired OdoNoticeMapper odoMapper;
	@Autowired LogisticsMapper logisticsMapper;
	
	@Autowired DealerInventoryMapper dInventoryMapper;
	@Autowired DealerWarehouseMapper dWarehouseMapper;
	@Autowired DDCommMapper ddcommMapper;
	
	@Autowired VoucherNumberService numberUtils;
	
	public List<Lease> getLeaseList(Long orgId, String leaseName){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("dealer_name", leaseName);
		return leaseMapper.selectLeaseList(params);
	}
	
	@Transactional
	public Lease addLease(Long orgId, Long dealer_inner_sn) {
		Map<String,Object> params = new HashMap<>();
		Long id = CompressedIdGenerator.generateId();
		params.put("id", id);
		params.put("org_id", orgId);
		params.put("dealer_inner_sn", dealer_inner_sn);
		Lease old = leaseMapper.existLease(params);
		if(old == null) {
			leaseMapper.insertLease(params);
			return leaseMapper.selectLeaseById(id);
		}
		return old;
	}
	
	@Transactional
	public void deleteLease(List<Long> ids) {
		for (Long id : ids) {
			leaseMapper.deleteLease(id);
		}
	}
	
	public Lease isLease(Long dealerId) {
		Map<String,Object> params = new HashMap<>();
		params.put("dealer_inner_sn", dealerId);
		Lease lease = leaseMapper.existLease(params);
		
		return lease;
	}
	
	public List<InventoryProduct> selectInventoryByLevel(Integer level, SearchWarehouseProductForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("product_name", form.getProduct_name());
		params.put("specification", form.getSpecification());
		params.put("batch_number", form.getBatch_number());
		params.put("selectVendor", 1);
		params.put("selectDealer", 1);
		switch (level) {
		case 1:
			return dInventoryMapper.selectInventoryProductLevel1(params);
		case 2:
			return dInventoryMapper.selectInventoryProductLevel2(params);
		case 3:
			return dInventoryMapper.selectInventoryProductLevel3(params);
		}
		return new ArrayList<>();
	}
	
	public List<WarehouseModel> selectTopLevelWarehouseList(Long oid, Long status) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("status", status);
		List<WarehouseModel> list = dWarehouseMapper.selectTopLevelWarehouseList(params);
		return list;
	}
	
	//入库通知
	public List<GrnNotice> searchGrnNoticeList(Long orgId, SearchNoticeForm form, boolean isHistory){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("dealer_name", form.getDealer_name());
		params.put("company_name", form.getCompany_name());
		params.put("product_name", form.getProduct_name());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		if(isHistory) {
			params.put("status", "1,2,3");//1-已确认 2-作废 3-完成
		}else {
			params.put("status", "0");//0-待确认
		}
		return grnMapper.selectGrnNoticeList(params);
	}
	
	@Transactional
	public List<GrnNotice> insertGrnNotice(AddNoticeForm form, ContextUserInfo curUser){
		List<GrnNotice> res = new ArrayList<>();
		if(Utils.isEmpty(form.getDetails())) {
			throw new ValidateException("通知明细不能为空!");
		}
		Long notice_id = CompressedIdGenerator.generateId();
		for (NoticeDetailForm detail : form.getDetails()) {
			GrnNotice params = new GrnNotice();
			params.setId(CompressedIdGenerator.generateId());
			params.setOrg_id(0L);//平台是唯一代储代配机构
			params.setNotice_id(notice_id);
			params.setDealer_inner_sn(form.getDealer_inner_sn());
			params.setDealer_name(form.getDealer_name());
			params.setSales_company_name(form.getCompany_name());
			params.setGrn_inner_sn(form.getRecord_inner_sn());
			params.setSn(form.getSn());
			params.setWarehouse_name(form.getWarehouse_name());
			params.setBills_type(form.getBills_type());
			params.setProduct_vendor_inner_sn(detail.getProduct_vendor_inner_sn());
			params.setProduct_inner_sn(detail.getProduct_inner_sn());
			params.setSpecification_inner_sn(detail.getSpecification_inner_sn());
			params.setUnit_inner_sn(detail.getUnit_inner_sn());
			params.setMdrf_inner_sn(detail.getMdrf_inner_sn());
			params.setProduct_vendor_name(detail.getProduct_vendor_name());
			params.setProduct_name(detail.getProduct_name());
			params.setSpecification(detail.getSpecification());
			params.setUnit(detail.getUnit());
			params.setMdrf_sn(detail.getMdrf_sn());
			params.setBatch_number(detail.getBatch_number());
			params.setUnit_price(detail.getUnit_price());
			params.setQuantity(detail.getQuantity());
			params.setRate(detail.getRate());
			params.setProduce_date(detail.getProduce_date());
			params.setOverdue_date(detail.getOverdue_date());
			params.setProduct_type(detail.getProduct_type());
			params.setCreated_by(Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
			params.setCreated_time(new Date());
			if(form.getStatus().equals(1)) {
				params.setStatus(GrnNoticeStatus.RECEIVED);
				params.setConfirm_by(Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
				params.setConfirm_time(new Date());
			}else {
				params.setStatus(GrnNoticeStatus.TO_BE_RECEIVED);
			}
			grnMapper.insertGrnNotice(params);
			res.add(params);
		}
		return res;
	}
	
	@Transactional
	public void updateGrnNotice(Long id, Integer status, ContextUserInfo curUser){
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("status", status.intValue());
		if(status.equals(1)) {
			params.put("confirm_by", Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
			params.put("confirm_time", new Date());
		}
		grnMapper.updateGrnNotice(params);
	}
	
	//出库通知
	public List<OdoNotice> searchOdoNoticeList(Long orgId, SearchNoticeForm form, boolean isHistory){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("dealer_name", form.getDealer_name());
		params.put("company_name", form.getCompany_name());
		params.put("product_name", form.getProduct_name());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		if(isHistory) {
			params.put("status", "1,2,3");//1-已确认 2-作废 3-完成
		}else {
			params.put("status", "0");//0-待确认
		}
		return odoMapper.selectOdoNoticeList(params);
	}
	
	@Transactional
	public List<OdoNotice> insertOdoNotice(AddNoticeForm form, ContextUserInfo curUser){
		List<OdoNotice> res = new ArrayList<>();
		if(Utils.isEmpty(form.getDetails())) {
			throw new ValidateException("通知明细不能为空!");
		}
		Long notice_id = CompressedIdGenerator.generateId();
		for (NoticeDetailForm detail : form.getDetails()) {
			OdoNotice params = new OdoNotice();
			params.setId(CompressedIdGenerator.generateId());
			params.setOrg_id(0L);//平台是唯一代储代配机构
			params.setNotice_id(notice_id);
			params.setDealer_inner_sn(form.getDealer_inner_sn());
			params.setDealer_name(form.getDealer_name());
			params.setPurchasing_company_name(form.getCompany_name());
			params.setOdo_inner_sn(form.getRecord_inner_sn());
			params.setSn(form.getSn());
			params.setWarehouse_name(form.getWarehouse_name());
			params.setBills_type(form.getBills_type());
			params.setProduct_vendor_inner_sn(detail.getProduct_vendor_inner_sn());
			params.setProduct_inner_sn(detail.getProduct_inner_sn());
			params.setSpecification_inner_sn(detail.getSpecification_inner_sn());
			params.setUnit_inner_sn(detail.getUnit_inner_sn());
			params.setMdrf_inner_sn(detail.getMdrf_inner_sn());
			params.setProduct_vendor_name(detail.getProduct_vendor_name());
			params.setProduct_name(detail.getProduct_name());
			params.setSpecification(detail.getSpecification());
			params.setUnit(detail.getUnit());
			params.setMdrf_sn(detail.getMdrf_sn());
			params.setBatch_number(detail.getBatch_number());
			params.setUnit_price(detail.getUnit_price());
			params.setQuantity(detail.getQuantity());
			params.setRate(detail.getRate());
			params.setProduce_date(detail.getProduce_date());
			params.setOverdue_date(detail.getOverdue_date());
			params.setProduct_type(detail.getProduct_type());
			params.setCreated_by(Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
			params.setCreated_time(new Date());
			if(form.getStatus().equals(1)) {
				params.setStatus(OdoNoticeStatus.DELIVER);
				params.setConfirm_by(Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
				params.setConfirm_time(new Date());
			}else {
				params.setStatus(OdoNoticeStatus.TO_BE_DELIVER);
			}
			odoMapper.insertOdoNotice(params);
			res.add(params);
		}
		return res;
	}
	
	@Transactional
	public void updateOdoNotice(Long id, Integer status, ContextUserInfo curUser){
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("status", status.intValue());
		if(status.equals(1)) {
			params.put("confirm_by", Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
			params.put("confirm_time", new Date());
		}
		odoMapper.updateOdoNotice(params);
	}
	
	//物流信息
	public List<OdoLogisticsRecord> getLogisticsRecordList(Long orgId, SearchLogisticsForm form, boolean isHistory){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		if(isHistory) {
			params.put("status", 1);//1-已确认
		}else {
			params.put("status", 0);//0-草稿
		}
		params.put("logistics_mode", form.getLogistics_mode());
		params.put("logistics_sn", form.getLogistics_sn());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		return logisticsMapper.selectLogisticsRecordList(params);
	}
	
	@Transactional
	public OdoLogisticsRecord insertOdoLogisticsRecord(LogisticsForm form, ContextUserInfo curUser) {
		if(LogisticsMode.THIRD.equals(form.getLogistics_mode())) {
			if(Utils.isEmpty(form.getLogistics_sn()) || Utils.isEmpty(form.getThird_logistics_company())) {
				throw new ValidateException("第三方物流方式时，物流公司和物流单号不能为空！");
			}
		}
		if(Utils.isEmpty(form.getDetail())) {
			throw new ValidateException("物流明细不能为空!");
		}
		Long logistics_record_id = CompressedIdGenerator.generateId();
		Map<String,Object> params = new HashMap<>();
		params.put("id", logistics_record_id);
		params.put("org_id", curUser.getOrgId());
		params.put("logistics_mode", form.getLogistics_mode());
		params.put("third_logistics_company", form.getThird_logistics_company());
		if(LogisticsMode.SELF.equals(form.getLogistics_mode())) {
			params.put("logistics_sn", numberUtils.getMaxSn(VoucherType.LOGISTICS, curUser.getSystemType(), curUser.getOrgId()));
		}else {
			params.put("logistics_sn", form.getLogistics_sn());
		}
		params.put("status", form.getStatus());
		params.put("created_by", Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
		params.put("created_time", new Date());
		if(LogisticsStatus.CONFIRMED.equals(form.getStatus())) {
			params.put("confirm_by", Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
			params.put("confirm_time", new Date());
		}
		logisticsMapper.insertLogisticsRecord(params);
		for (Long odo_notice_id : form.getDetail()) {
			checkOdoNoticeStatus(odo_notice_id);
			Map<String,Object> params1 = new HashMap<>();
			params1.put("id", CompressedIdGenerator.generateId());
			params1.put("odo_notice_id", odo_notice_id);
			params1.put("logistics_record_id", logistics_record_id);
			logisticsMapper.insertLogisticsRefOdoNotice(params1);
		}
		return logisticsMapper.selectLogisticsRecordById(logistics_record_id);
	}
	
	@Transactional
	public void updateOdoLogisticsRecord(LogisticsForm form, ContextUserInfo curUser) {
		Long logistics_record_id = form.getId();
		OdoLogisticsRecord old = logisticsMapper.selectLogisticsRecordById(logistics_record_id);
		if(Utils.isEmpty(old)) {
			throw new ValidateException("错误的物流信息ID!");
		}
		if(LogisticsMode.THIRD.equals(form.getLogistics_mode())) {
			if(Utils.isEmpty(form.getLogistics_sn()) || Utils.isEmpty(form.getThird_logistics_company())) {
				throw new ValidateException("第三方物流方式时，物流公司和物流单号不能为空！");
			}
		}
		if(Utils.isEmpty(form.getDetail())) {
			throw new ValidateException("物流明细不能为空!");
		}
		
		Map<String,Object> params = new HashMap<>();
		params.put("logistics_record_id", logistics_record_id);
		logisticsMapper.deleteLogisticsRefOdoNotice(params);
		
		params.clear();
		params.put("id", logistics_record_id);
		params.put("logistics_mode", form.getLogistics_mode());
		params.put("third_logistics_company", form.getThird_logistics_company());
		if(LogisticsMode.SELF.equals(form.getLogistics_mode())) {
			if(LogisticsMode.SELF.equals(old.getLogistics_mode())) {
				params.put("logistics_sn", old.getLogistics_sn());
			}else {
				params.put("logistics_sn", numberUtils.getMaxSn(VoucherType.LOGISTICS, curUser.getSystemType(), curUser.getOrgId()));
			}
		}else {
			params.put("logistics_sn", form.getLogistics_sn());
		}
		params.put("status", form.getStatus());
		if(LogisticsStatus.CONFIRMED.equals(form.getStatus())) {
			params.put("confirm_by", Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
			params.put("confirm_time", new Date());
		}
		logisticsMapper.updateLogisticsRecord(params);
		for (Long odo_notice_id : form.getDetail()) {
			checkOdoNoticeStatus(odo_notice_id);
			Map<String,Object> params1 = new HashMap<>();
			params1.put("id", CompressedIdGenerator.generateId());
			params1.put("odo_notice_id", odo_notice_id);
			params1.put("logistics_record_id", logistics_record_id);
			logisticsMapper.insertLogisticsRefOdoNotice(params1);
		}
	}
	
	private void checkOdoNoticeStatus(Long id) {
		OdoNotice info = odoMapper.selectOdoNoticeById(id);
		if(info == null) {
			throw new ValidateException("错误的出库通知ID！");
		}else if(OdoNoticeStatus.CANCEL.equals(info.getStatus())){
			throw new ValidateException("出库通知ID已被取消，不能编辑物流单！");
		}
	}
	
	@Transactional
	public void confirmLogistics(Long id, ContextUserInfo curUser) {
		OdoLogisticsRecord old = logisticsMapper.selectLogisticsRecordById(id);
		if(Utils.isEmpty(old)) {
			throw new ValidateException("错误的物流信息ID!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("status", 1);
		params.put("confirm_by", Utils.isEmpty(curUser.getEmployee())?"管理员":curUser.getEmployee().getName());
		params.put("confirm_time", new Date());
		logisticsMapper.updateLogisticsRecord(params);
		params.clear();
		params.put("logistics_record_id", id);
		List<OdoNotice> list = logisticsMapper.selectLogisticsRefOdoNotice(params);
		for (OdoNotice odoNotice : list) {
			updateOdoNotice(odoNotice.getId(), 3,  curUser);
		}
	}
	
	public OdoLogisticsRecord getLogisticsRecordInfo(Long id) {
		OdoLogisticsRecord info = logisticsMapper.selectLogisticsRecordById(id);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的物流信息ID!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("logistics_record_id", id);
		info.setDetails(logisticsMapper.selectLogisticsRefOdoNotice(params));
		return info;
	}

	public List<ResponseProductInfo> getGrnNoticeVendorProductList(SearchNoticeProductForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("product_name", form.getProduct_name());
		params.put("vendor_inner_sn", form.getProdict_vendor_inner_sn());
		params.put("specification", form.getSpecification());
		params.put("specification_vendor_sn", form.getSpecification_vendor_sn());
		return ddcommMapper.selectVendorProductList(params);
	}

	public List<ResponseProductInfo> getOdoNoticeVendorProductList(SearchNoticeProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getDealer_inner_sn());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("product_name", form.getProduct_name());
		params.put("vendor_inner_sn", form.getProdict_vendor_inner_sn());
		params.put("specification", form.getSpecification());
		params.put("specification_vendor_sn", form.getSpecification_vendor_sn());
		return ddcommMapper.selectVendorInventoryProductList(params);
	}
}
