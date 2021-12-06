package com.yrt.project.api.business.applyvoucher.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.yrt.project.modular.premium.service.PremiumRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.constant.Constants;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.InventoryException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.applyvoucher.service.IHospitalGrantService;
import com.yrt.project.api.business.applyvoucher.vo.AppGrantConfirmDetail;
import com.yrt.project.api.business.applyvoucher.vo.AppGrantConfirmForm;
import com.yrt.project.api.business.applyvoucher.vo.GrantSendDetailForm;
import com.yrt.project.api.business.applyvoucher.vo.GrantSendForm;
import com.yrt.project.api.business.applyvoucher.vo.SearchApplyDetailForm;
import com.yrt.project.api.business.applyvoucher.vo.SearchApplyVoucherForm;
import com.yrt.project.api.business.dealer.service.IDealerProductUnitService;
import com.yrt.project.api.business.warehouse.service.AllotVoucherService;
import com.yrt.project.api.business.warehouse.service.IGrnService;
import com.yrt.project.api.business.warehouse.service.InventoryLockService;
import com.yrt.project.api.business.warehouse.vo.allot.SearchAllotVoucherForm;
import com.yrt.project.api.business.warehouse.vo.grn.AddGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnDetailForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIds;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.platform.service.IProductUnitService;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalApplyVoucherDetail;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.applyvoucher.mapper.HospitalApplyVoucherMapper;
import com.yrt.project.modular.applyvoucher.mapper.HospitalGrantVoucherMapper;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.OrgMapper;
import com.yrt.project.modular.plan.mapper.HospitalPlanMapper;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.warehouse.domain.AllotVoucher;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;
import com.yrt.project.modular.warehouse.domain.Grn;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.mapper.HospitalAllotVoucherMapper;

@Service
public class HospitalGrantServiceImpl implements IHospitalGrantService {

	@Autowired private HospitalApplyVoucherMapper aMapper;
	@Autowired private HospitalGrantVoucherMapper gMapper;
	
	@Autowired HospitalMapper hMapper;
	
	@Autowired private HospitalPlanMapper hpMapper;
	@Autowired private VoucherNumberService numberUtils;
	@Autowired InventoryLockService inventoryLocker;
	@Autowired
	PremiumRuleService premiumRuleService;

	@Override
	public List<HospitalApplyVoucher> searchHospitalApplyVoucherList(SearchApplyVoucherForm form, OrgEmployee makingPeople) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", form.getHospital_inner_sn());
		params.put("status", form.getStatus());
		params.put("voucher_sn", form.getVoucher_sn());
		params.put("proposer_name", form.getProposer_name());
		params.put("department_inner_sn", form.getDepartment_inner_sn());

		//params.put("uid", makingPeople.getEmployee_inner_sn());//只能查询登录人自己部门的申领单

		return aMapper.searchHospitalApplyVoucherList(params);
	}
	
	@Override
	public List<HospitalApplyVoucher> searchHospitalApplyVoucherList(Long oid, String key, OrgEmployee makingPeople){
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("status", 1);
		params.put("voucher_sn", key);
		params.put("proposer_name", key);
		params.put("department_name", key);

		params.put("uid", makingPeople.getEmployee_inner_sn());//只能查询登录人自己部门的申领单

		return aMapper.appSearchHospitalApplyVoucherList(params);
	}

	@Override
	public List<HospitalApplyVoucherDetail> selectGrantingVoucherDetailListByWarehouse(SearchApplyDetailForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", form.getHospital_inner_sn());
		params.put("av_inner_sn", form.getOrder_id());
		params.put("status", "0,1");
		if(form.getOnlyUnGrant()) {
			params.put("onlyUnGrant", 1);
		}
		return aMapper.searchHospitalApplyVoucherDetailList(params);//TODO: 需要用存储过程代替
	}



	@Override
	public List<Map<String, Object>> selectGrantList(Long oid, Long aid, Long wid) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn_i", oid);
		params.put("av_inner_sn_i", aid);
		params.put("warehouse_inner_sn_i", wid);
		List<Map<String, Object>> list = gMapper.selectGrantList(params);


		params.put("hospital_inner_sn", oid);
		params.put("av_inner_sn", aid);
		List<HospitalApplyVoucher> avs = aMapper.searchHospitalApplyVoucherList(params);
		if (avs.stream().anyMatch(a->Objects.nonNull(a.getWarehouse_inner_sn()))){
			//申领单有仓库，发放时候生成了调拨单的，需要关联调拨明细修改已批准发放数量(最小包装单位)

			//查出申领单关联的调拨明细
			SearchAllotVoucherForm xForm = new SearchAllotVoucherForm();
			xForm.setOrg_id(oid);
			xForm.setTab_id(-1);
			xForm.setApply_voucher_inner_sn(aid);
			List<AllotVoucherDetail> avDetails = avService.searchAllotVoucherList(xForm, false);//已经生成的调拨单

			if (!avDetails.isEmpty()){
				Map<String, List<AllotVoucherDetail>> avMap = avDetails.stream()
						.peek(x->{
							Map<String,Object> m=new HashMap<>();
							m.put("vendor_inner_sn_i", x.getVd_inner_sn());
							m.put("product_inner_sn_i", x.getProduct_inner_sn());
							m.put("specification_inner_sn_i", x.getSpecification_inner_sn());
							m.put("unit_inner_sn_i", x.getUnit_inner_sn());

							BigDecimal bs;
							if (1==x.getProduct_type()){
								bs= pMapper.calculate_product_min_unit(m);

							}else {
								bs = dpMapper.calculate_product_min_unit(m);
							}
							//最小包装单位数量
							x.setAllot_quantity(bs.multiply(x.getAllot_quantity()));
						})
						.collect(Collectors.groupingBy(x -> x.getVd_inner_sn() + "-" + x.getProduct_inner_sn() + "-" + x.getSpecification_inner_sn() + "-" + x.getProduct_type()));

				for (Map<String,Object> map:list){
					String key=map.get("product_vendor_inner_sn")+"-"+map.get("product_inner_sn")+"-"+map.get("specification_inner_sn")+"-"+map.get("product_type");
					List<AllotVoucherDetail> details = avMap.get(key);
					map.put("approved_quantity",Objects.isNull(details)?BigDecimal.ZERO:details.stream().map(AllotVoucherDetail::getAllot_quantity).reduce(BigDecimal.ZERO, BigDecimal::add));
				}

			}
		}
		return list;
	}
	
	//申领页面使用
	@Override
	public List<HospitalGrantVoucher> selectGrantVoucherlList(Long oid, Long aid, Boolean isAll, String key) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("av_inner_sn", aid);
		params.put("key", key);
		if(isAll) {
			params.put("status", "0,1,2");
		}else {
			params.put("status", "0");
		}
        List<HospitalGrantVoucher> list = gMapper.selectGrantVoucherList(params);
		for (HospitalGrantVoucher x : list){
            Map<String, Object> map = new HashMap<>();
            map.put("hospital_inner_sn", oid);
            map.put("gv_inner_sn", x.getGv_inner_sn());
            List<HospitalGrantVoucherDetail> details = gMapper.searchGrantDetailList(map);

			// 设置溢价比例和售价
            premiumRuleService.setPercentAndSalesPrice4(details);
            x.setDetails(details);
        }
		return list;
//		return gMapper.selectGrantVoucherList(params);
	}
	
	//获取发放单列表(手机使用)
	@Override
	public List<HospitalGrantVoucher> getHospitalGrandVoucherList4Phone(Long oid, String key, Integer status, OrgEmployee makingPeople) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("gv_sn", key);
		params.put("warehouse_name", key);
		params.put("department_name", key);

		params.put("uid", makingPeople.getEmployee_inner_sn());//只能查询登录人自己部门的申领单

		if(status == 0) {
			params.put("status", "0,1,2");
		}else if(status == 1 ){
			params.put("status", "0,1");
		}else {
			params.put("status", "2");
		}
		return gMapper.appSelectGrantVoucherList(params);
	}

	@Override
	public List<HospitalGrantVoucherDetail> selectGrantDetailList(Long oid, Long gid, String status) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("gv_inner_sn", gid);
		if(StringUtils.isNotEmpty(status)) {
			params.put("status", status);
		}
		return gMapper.searchGrantDetailList(params);
	}
	
	@Override
	public List<HospitalGrantVoucherDetail> selectGrantDetailList4Phone(Long oid, Long gid, Integer all) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("gv_inner_sn", gid);
		if(all == 0) {
			params.put("all", all);
		}
		return gMapper.searchGrantDetailList(params);
	}

	@Autowired
	ProductUnitMapper pMapper;
	@Autowired
	DealerProductUnitMapper dpMapper;

	@Override
	@Transactional
	public int voucherSend(GrantSendForm form, OrgEmployee makingPeople) {
		//先查申领单，判断有无仓库，有:生成调拨单 无:继续原先下面逻辑
		Map<String,Object> map=new HashMap<>();
		map.put("hospital_inner_sn",form.getHospital_inner_sn());
		map.put("av_inner_sn",form.getAv_inner_sn());
		map.put("status","1,2");	//申领、部分发放状态的申领单才能进行发放
		List<HospitalApplyVoucher> list = aMapper.searchHospitalApplyVoucherList(map);
		if (list.isEmpty()) throw new DBException(MessageUtils.message("db.add.fail","发放","申领单不存在了"));
//		HospitalApplyVoucher av = list.get(0);

		List<GrantSendDetailForm> details = form.getDetails();
		for (GrantSendDetailForm detail : details) {
			InventoryKey lockKey = InventoryKey.of(form.getHospital_inner_sn(), form.getWarehouse_inner_sn(), detail.getProduct_vendor_inner_sn(),
					detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
					detail.getBatch_number(), BillsType.HH);
			try {
//				lockInventory(lockKey, BigDecimal.ZERO, detail.getGrant_quantity(), detail.getUnit_inner_sn(), true);//锁定指定批号库存量
				lockKey.setBatchNumber(Constants.PRE_LOCK_BATCH_NUMBER);
				lockInventory(lockKey, BigDecimal.ZERO, detail.getGrant_quantity(), detail.getUnit_inner_sn(), true);//锁定全局库存量
			} catch (InventoryException ex) {
				String message = "库存不足: " + detail.getProduct_name() + ">>" + detail.getSpecification() + ">>" + detail.getUnit() + "\\n" 
						+ "批号:" + "'" + detail.getBatch_number() + "'" + "请求锁定量: " + ex.getRequestQuantity().longValue() + " 请求最小单位锁定量: " + ex.getRequestMinUnitQuantity().longValue() + "\\n"
						+ "库存余量(最小单位):" + ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE);
				throw new RuntimeException(message);
			}
		}
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn_i", form.getHospital_inner_sn());
		params.put("av_inner_sn_i", form.getAv_inner_sn());
		params.put("department_inner_sn_i", form.getDepartment_inner_sn());
		params.put("approver_inner_sn_i", makingPeople.getEmployee_inner_sn());
		params.put("gv_sn_i", numberUtils.getMaxSn(VoucherType.GRANT, SystemType.HOSPITAL, form.getHospital_inner_sn())); //.getGrantMaxSn(form.getHospital_inner_sn()));
//		if(StringUtils.isEmpty(form.getGv_sn())) {
//		}else {
//			params.put("gv_sn_i", form.getGv_sn());
//		}
		params.put("description_i", form.getDescription());
		params.put("department_name_i", form.getDepartment_name());
		params.put("approver_name_i", makingPeople.getName());
		params.put("warehouse_inner_sn_i", form.getWarehouse_inner_sn());
		params.put("warehouse_name_i", form.getWarehouse_name());
		params.put("data_i", JSONObject.toJSONString(form.getDetails()));

//		if (Objects.nonNull(av.getWarehouse_inner_sn())&&StringUtils.isNotEmpty(av.getWarehouse_inner_sn_name())){
//			//有仓库，新增调拨单
//			//将GrantSendForm form参数接收的List<GrantSendDetailForm> details发放明细转为List<AddOrEditProductDetailForm>
//			List<AddOrEditProductDetailForm> forms = details.stream().map(x -> new AddOrEditProductDetailForm(null, x.getProduct_vendor_inner_sn(), x.getProduct_vendor_name(), x.getProduct_type(),
//					x.getProduct_inner_sn(), x.getProduct_name(), x.getSpecification_inner_sn(), x.getSpecification(),
//					x.getUnit_inner_sn(), x.getUnit(), x.getSort_number(), x.getBatch_number(), x.getGrant_quantity(), BigDecimal.ZERO,null,null,
//					AllotDetailStatus.CONFIRM)).collect(Collectors.toList());
//
//			AddAllotVoucherForm avForm = new AddAllotVoucherForm();
//			avForm.setOrg_id(form.getHospital_inner_sn());
//			avForm.setApply_voucher_inner_sn(String.valueOf(form.getAv_inner_sn()));
//			avForm.setSource_warehouse_inner_sn(form.getWarehouse_inner_sn());
//			avForm.setTarget_warehouse_inner_sn(av.getWarehouse_inner_sn());
//			avForm.setDescription(form.getDescription());
//			avForm.setDetails(forms);
//			AllotVoucher res = service.addAllotVoucher(avForm);
//
//			//修改申领单状态。如果关联的调拨单数量达到发放单，则改为完全发放，否则为部分发放
//
//			//通过申领单，查到关联的调拨主单
//			Long apply_voucher_inner = form.getAv_inner_sn();
//			SearchAllotVoucherForm xForm = new SearchAllotVoucherForm();
//			xForm.setOrg_id(form.getHospital_inner_sn());
//			xForm.setTab_id(0);
//			xForm.setApply_voucher_inner_sn(apply_voucher_inner);
//			List<AllotVoucherDetail> avDetails = avService.searchAllotVoucherList(xForm, false);
//
//			Map<String, List<AllotVoucherDetail>> avMap = avDetails.stream()
//					.peek(x->{
//						Map<String,Object> m=new HashMap<>();
//						m.put("vendor_inner_sn_i", x.getVd_inner_sn());
//						m.put("product_inner_sn_i", x.getProduct_inner_sn());
//						m.put("specification_inner_sn_i", x.getSpecification_inner_sn());
//						m.put("unit_inner_sn_i", x.getUnit_inner_sn());
//
//						BigDecimal bs;
//						if (1==x.getProduct_type()){
//							bs= pMapper.calculate_product_min_unit(m);
//
//						}else {
//							bs = dpMapper.calculate_product_min_unit(m);
//						}
//
//						//最小包装单位数量
//						x.setAllot_quantity(bs.multiply(x.getAllot_quantity()));
//					})
//					.collect(Collectors.groupingBy(x ->   x.getVd_inner_sn() +"-"+ x.getProduct_inner_sn()+ "-" + x.getSpecification_inner_sn()+"-"+x.getProduct_type()));
//
//			List<GrantSendDetailForm> detailForms = form.getDetails();
//			Map<String, List<GrantSendDetailForm>> applyMap = detailForms.stream()
//					.peek(x->{
//						Map<String,Object> m=new HashMap<>();
//						m.put("vendor_inner_sn_i", x.getProduct_vendor_inner_sn());
//						m.put("product_inner_sn_i", x.getProduct_inner_sn());
//						m.put("specification_inner_sn_i", x.getSpecification_inner_sn());
//						m.put("unit_inner_sn_i", x.getUnit_inner_sn());
//
//						BigDecimal bs;
//						if (1==x.getProduct_type()){
//							bs= pMapper.calculate_product_min_unit(m);
//
//						}else {
//							bs = dpMapper.calculate_product_min_unit(m);
//						}
//
//						//最小包装单位数量
//						x.setGrant_quantity(bs.multiply(x.getGrant_quantity()));
//					})
//					.collect(Collectors.groupingBy(x -> x.getProduct_vendor_inner_sn() + "-" + x.getProduct_inner_sn() + "-" + x.getSpecification_inner_sn()+"-"+x.getProduct_type()));
//
//
//			if (avMap.size()<applyMap.size()){
//				//改为部分申领,	"状态 0-草稿 1-申领 2-部分发放 3-全部发放(自然终止) 4-申领终止(人为终止)")
//				params.put("status",2);
//				havMapper.updateApplyStatus(params);
//			}
//
//			boolean flag=true;
//			for (Map.Entry<String, List<AllotVoucherDetail>> avEntry:avMap.entrySet()){
//				for (Map.Entry<String, List<GrantSendDetailForm>> applyEntry:applyMap.entrySet()){
//					if (avEntry.getKey().equals(applyEntry.getKey())){
//						//比较最小单位数量,一旦调拨小于申领数量，则flag=false
//						List<AllotVoucherDetail> list1 = avEntry.getValue();
//						List<GrantSendDetailForm> list2 = applyEntry.getValue();
//
//						BigDecimal reduce1 = list1.stream().map(AllotVoucherDetail::getAllot_quantity).reduce(BigDecimal.ZERO, BigDecimal::add);
//						BigDecimal reduce2 = list2.stream().map(GrantSendDetailForm::getGrant_quantity).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//						if (reduce1.compareTo(reduce2)<0) flag=false;
//					}
//				}
//			}
//
//			if (flag){
//				//修改申领单为完全申领
//				params.put("status",3);
//				havMapper.updateApplyStatus(params);
//			}
//			return Objects.isNull(res)?0:1;
//		}

		gMapper.insertGrant(params);
		if(Integer.valueOf(params.get("result_o").toString()) != 0) {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","发放",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","发放",params.get("message_string_o")));
			}
		}else {
			Integer hospital_av_status = Integer.valueOf(params.get("hospital_av_status_o").toString());
			return hospital_av_status;
		}
	}
	
	@Transactional
	void lockInventory(InventoryKey lockKey, BigDecimal old_quantity, BigDecimal new_quantity, Long unitid, boolean isNew) throws InventoryException {
		InventoryKey queryKey = InventoryKey.of(lockKey);
		queryKey.setBatchNumber(Constants.PRE_LOCK_BATCH_NUMBER);
		if (isNew) {
			inventoryLocker.lockInventory(lockKey, queryKey, unitid, new_quantity);
		} else {
			if (old_quantity.compareTo(new_quantity) == 0) {
				return;
			}
			inventoryLocker.unlockInventory(lockKey, unitid, old_quantity);
			inventoryLocker.lockInventory(lockKey, queryKey, unitid, new_quantity);
		}
	}

	@Override
	@Transactional
	public int createPlan(GrantSendForm form, OrgEmployee makingPeople) {
		Map<String,Object> params = new HashMap<>();
		params.put("hospital_inner_sn_i", form.getHospital_inner_sn());
		params.put("creator_inner_sn_i", makingPeople.getEmployee_inner_sn());
		params.put("making_people_name_i", makingPeople.getName());
		params.put("department_inner_sn_i", form.getDepartment_inner_sn());
		params.put("department_name_i", form.getDepartment_name());
		params.put("create_time_i", new Date());
		params.put("status_i", form.getStatus());
		params.put("ppl_sn_i", numberUtils.getMaxSn(VoucherType.PURCHASE_PLAN, ServletUtils.getCurUserSystemType(), form.getHospital_inner_sn()));//.getPlanMaxSn(ServletUtils.getCurUserSystemType(), form.getHospital_inner_sn()));
		params.put("description_i", form.getDescription());
		if(form.getStatus() == 2) {
			params.put("audit_time_i", new Date());
			params.put("auditor_inner_sn_i", makingPeople.getEmployee_inner_sn());
			params.put("auditor_name_i", makingPeople.getName());
		}else {
			params.put("audit_time_i", null);
			params.put("auditor_name_i", "");
		}
		
		hpMapper.h_add_hospital_ppl(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long pid = Long.valueOf(params.get("ppl_inner_sn_o").toString());
			List<GrantSendDetailForm> details = form.getDetails();
			for (GrantSendDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("hospital_inner_sn_i", form.getHospital_inner_sn());
				params1.put("ppl_inner_sn_i", pid);
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getGrant_quantity());
				params1.put("status_i", 0);//新增的时候只可能是待处理
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("batch_number_i", detail.getBatch_number());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit());
				if(detail.getProduct_type() == 1) {//厂商产品
					params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
					params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
					hpMapper.h_add_hospital_ppl_product_detail(params1);
				}else {//自建产品
					params1.put("product_dealer_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getProduct_vendor_name());
					hpMapper.h_add_hospital_ppl_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","采购计划明细",params1.get("message_string_o")));
				}else {
					Map<String,Object> params2 = new HashMap<>();
					params2.put("hospital_inner_sn", form.getHospital_inner_sn());
					params2.put("av_inner_sn", form.getAv_inner_sn());
					params2.put("detail_inner_sn", detail.getDetail_inner_sn());
					params2.put("status", 2);
					if(detail.getProduct_type() == 1) {//厂商产品
						aMapper.updateHospitalApplyVoucherProduct(params2);
					}else {//自建产品
						aMapper.updateHospitalApplyVoucherDealerProduct(params2);
					}
				}
			}
			return 1;
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","采购计划",params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public int stopHospitalApplyVoucher(Long oid, Long aid) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("av_inner_sn", aid);
		params.put("status", 3);//3-申领终止(人为终止)
		
		return aMapper.updateHospitalApplyVoucher(params);
	}

	@Override
	@Transactional
	public int confirmSendVoucher(Long oid, Long gid, ContextUserInfo curUser) {
		OrgEmployee makingPeople = curUser.getEmployee();
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("gv_inner_sn", gid);
		params.put("status", 2);//2-已领取
		params.put("receiver_inner_sn", makingPeople.getEmployee_inner_sn());//领取人ID
		params.put("receiver_name", makingPeople.getName());//领取人名称
		
		return gMapper.updateGrant(params);
	}

	@Autowired HospitalAllotVoucherMapper havMapper;
	@Autowired IGrnService grnService;
	@Autowired OrgMapper orgMapper;
	@Autowired AllotVoucherService avService;

	@Override
	@Transactional
	public int confirmSendVoucher4Phone(AppGrantConfirmForm form, ContextUserInfo curUser) {
		OrgEmployee makingPeople = curUser.getEmployee();
		String x = makingPeople.getDepartment_inner_sn();
		String s = makingPeople.getDepartment_name();

		Long oid = form.getOid();	//机构id
		Long gid = form.getGid();	//发放单id	//调拨单id

		AllotVoucher av = havMapper.searchByAvInnerSn(gid);
		if (Objects.nonNull(av)){
			//如果是领取调拨单，,生成入库单，然后调入库动作
			//加个限制：如果这个调拨单已经做过入库调单，就提示不让直接领取请调单完成
//			Map<String,Object> params=new HashMap<>(3);
//			params.put("org_id",oid);
//			params.put("av_inner_sn",gid);
//			List<Long> grnIds = havMapper.findGrnIdsByAvInnerSn(params);
//			if (!grnIds.isEmpty()) throw new BusinessException("该调拨单已经做过入库调单，无法直接领取请调单入库完成!");
			AddGrnForm grnForm = new AddGrnForm();
			grnForm.setOrg_id(oid);
			grnForm.setOrganization_inner_sn(oid);
			grnForm.setOrganization_name(hMapper.selectOrgById(oid).getName());
			grnForm.setWarehouse_inner_sn(av.getTarget_warehouse_inner_sn());
			grnForm.setWarehouse_name(av.getTarget_warehouse_name());
			Long uid = makingPeople.getEmployee_inner_sn();
			grnForm.setHandler_inner_sn(uid);
			grnForm.setHandler_name(makingPeople.getName());
			grnForm.setType_inner_sn(2L);
			grnForm.setType_name("调拨入库");
			grnForm.setDepartment_inner_sn(Long.valueOf(x.split(",")[0]));
			grnForm.setDepartment_name(s.split(",")[0]);
			grnForm.setStatus(1);
			grnForm.setBillsType(BillsType.HH);
			List<GrnDetailForm> detailForms=new ArrayList<>();
			//准备好明细数据
			for (AppGrantConfirmDetail detail:form.getDetails()){

			//加个限制：如果这个调拨明细单已经做过入库调单，就提示不让直接领取请调单完成(修改限制：调拨单做过入库调单变为调拨明细单做过入库调单)
			Map<String,Object> params=new HashMap<>(3);
			params.put("org_id",oid);
			params.put("detail_inner_sn",detail.getDetail_inner_sn());
			List<Long> grnIds = havMapper.findGrnIdsByAvInnerSn(params);
			if (!grnIds.isEmpty()) throw new BusinessException("该调拨单已经做过入库调单，无法直接领取请调单入库完成!");

			GrnDetailForm detailForm=convertGrantConfirmDetail2GrnDetailForm(detail,oid);
			detailForms.add(detailForm);
			}
			grnForm.setDetails(detailForms);
			String sn = numberUtils.getMaxSn(VoucherType.PUT_IN, ServletUtils.getCurUserSystemType(), oid);
			Grn grn = grnService.insertGrn(grnForm, sn, makingPeople);

			GrnIds ids = new GrnIds();
			ids.setOrg_id(oid);
			ids.setGrn_inner_sn(grn.getGrn_inner_sn());
			ids.setVoucher_inner_sn(grn.getVoucher_inner_sn_c());
			ids.setWarehouse_inner_sn(av.getTarget_warehouse_inner_sn());
			ids.setWarehouse_name(av.getTarget_warehouse_name());
			ids.setBillsType(BillsType.HH);
			grnService.putin(ids, curUser);

			return 2;
		}


		List<AppGrantConfirmDetail> details = form.getDetails();	//发放明细列表
		for (AppGrantConfirmDetail detail : details) {
			Map<String, Object> params = new HashMap<>();
			params.put("hospital_inner_sn", oid);
			params.put("gv_inner_sn", gid);		//发放单id
			params.put("detail_inner_sn", detail.getDetail_inner_sn());		//明细单id
			params.put("get_quantity", detail.getGet_quantity().multiply(new BigDecimal(1000)));	//发放数量
			params.put("status", 2);//2-已领取
			if(detail.getProduct_type() == 1) {
				gMapper.updateGrantDetail(params);
			}else {
				gMapper.updateGrantDetailDealer(params);
			}
		}
		//检查发放单明细是否完全领取
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("gv_inner_sn", gid);
		params.put("status", 2);//2-已领取
		params.put("receiver_inner_sn", makingPeople.getEmployee_inner_sn());//领取人ID
		params.put("receiver_name", makingPeople.getName());//领取人名称
		params.put("checkStatus", "0,1");//检查是否还存在没有领取的明细
		if(StringUtils.isEmpty(gMapper.checkGrantFinish(params))){
			gMapper.updateGrant(params);
			return 2;//2-已领取
		}else {
			return 0;//0-可发放
		}
	}

	private GrnDetailForm convertGrantConfirmDetail2GrnDetailForm(AppGrantConfirmDetail detail,Long oid) {
		SearchAllotVoucherForm avForm = new SearchAllotVoucherForm();
		avForm.setOrg_id(oid);
		avForm.setTab_id(0);
		avForm.setDetail_inner_sn(String.valueOf(detail.getDetail_inner_sn()));
		List<AllotVoucherDetail> allotDetail = avService.searchAllotVoucherList(avForm, false);
		AllotVoucherDetail avDetail = allotDetail.get(0);

		GrnDetailForm form = new GrnDetailForm();
		form.setProduct_vendor_inner_sn(avDetail.getVd_inner_sn());
		form.setProduct_vendor_name(avDetail.getVd_name());
		form.setProduct_inner_sn(avDetail.getProduct_inner_sn());
		form.setProduct_name(avDetail.getProduct_name());
		form.setSpecification_inner_sn(avDetail.getSpecification_inner_sn());
		form.setSpecification(avDetail.getSpecification());
		form.setUnit_inner_sn(avDetail.getUnit_inner_sn());
		form.setUnit_name(avDetail.getUnit_name());
		form.setUnit_price(avDetail.getUnit_price());
		form.setQuantity(detail.getGet_quantity());
		form.setProduce_date(avDetail.getProduce_date());
		form.setOverdue_date(avDetail.getOverdue_date());
		form.setSort_number(avDetail.getSort_number());
		form.setBatch_number(avDetail.getBatch_number());
		form.setProduct_type(detail.getProduct_type());
		form.setVoucher_type_c(RefVoucherType.ALLOT);
		form.setVoucher_inner_sn_c(avDetail.getAv_inner_sn());
		form.setVoucher_detail_inner_sn_c(detail.getDetail_inner_sn());
		form.setType(0);
		form.setSort_number(Objects.isNull(avDetail.getSort_number())?0:avDetail.getSort_number());
		form.setMdrf_sn("1");
		form.setAction(1);
		return form;
	}

	@Override
	@Transactional
	public void rollbackVoucherStatus(Long oid, Long gid, Long detailId, int product_type, int status) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("gv_inner_sn", gid);
		params.put("detail_inner_sn", detailId);

		/**
		 * fix bug 4349
		 */
		List<HospitalGrantVoucherDetail> details = gMapper.searchGrantDetailList(params);
		HospitalGrantVoucherDetail detail = details.get(0);
		if (2==detail.getStatus()) status=2;

		params.put("status", status);
		if(product_type == 1) {
			gMapper.updateGrantDetail(params);
		}else {
			gMapper.updateGrantDetailDealer(params);
		}
		params.put("checkStatus", 0);//检查是否还存在没有生成出库单的明细
		//检查发放单明细只要有一条明细没有生成出库单就回滚主表状态为0-可发放
		if(StringUtils.isNotEmpty(gMapper.checkGrantFinish(params))){
			params.put("status", 0);
			gMapper.updateGrant(params);
		}else {
			/*
			 * HospitalGrantVoucher info = gMapper.selectGrantInfo(oid, gid);
			 * if(info.getStatus().equals(1)) { throw new DBException(""); }
			 */
			params.put("status", 1);//1-出库单已完全生成
			gMapper.updateGrant(params);
		}
	}

	@Autowired IProductUnitService puMapper;
	@Autowired IDealerProductUnitService dpuMapper;

	@Override
	@Transactional
	public BigDecimal updateTransferDetail(SystemType curSystemType, Long curOrgId, Long voucher_inner_sn, Long voucher_details_inner_sn,
			BigDecimal lockedQuantity, BillsType billsType, boolean isDelete) {
		//获取单据明细中的批准发放量
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", curOrgId);
		params.put("gv_inner_sn", voucher_inner_sn);
		params.put("detail_inner_sn", voucher_details_inner_sn);
		HospitalGrantVoucherDetail detail = gMapper.searchGrantDetailList(params).get(0);
		BigDecimal toBeUnlockQuantity = BigDecimal.ZERO;
		if (detail.getProduct_type().intValue() == 1) {
			toBeUnlockQuantity = detail.getGrant_quantity().multiply(
					puMapper.calculateProductMinUnit(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
							detail.getSpecification_inner_sn(), detail.getUnit_inner_sn())).multiply(new BigDecimal(1000));
		} else {
			toBeUnlockQuantity = detail.getGrant_quantity().multiply(
					dpuMapper.calculateProductMinUnit(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
							detail.getSpecification_inner_sn(), detail.getUnit_inner_sn())).multiply(new BigDecimal(1000));
		}
		//更新库存锁定数量
		int status = 0;
		BigDecimal ret = BigDecimal.ZERO;
		if(lockedQuantity.compareTo(toBeUnlockQuantity) >= 0 && !isDelete) {
			status = 1;
			ret = lockedQuantity.subtract(toBeUnlockQuantity);
		}
		//更新单据状态
		rollbackVoucherStatus(curOrgId, voucher_inner_sn, voucher_details_inner_sn, detail.getProduct_type(), status);
		return ret;
	}
}
