package com.yrt.project.api.business.plan.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.bean.BeanUtils;
import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.framework.web.page.PageDomain;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.applyvoucher.vo.AppApplayProductSearchForm;
import com.yrt.project.api.business.order.service.IOrderService;
import com.yrt.project.api.business.order.vo.order.AddPurchaseOrderForm;
import com.yrt.project.api.business.order.vo.order.OrderDetailForm;
import com.yrt.project.api.business.plan.service.IPlanService;
import com.yrt.project.api.business.plan.vo.AddPlanForm;
import com.yrt.project.api.business.plan.vo.AppPlanSearchForm;
import com.yrt.project.api.business.plan.vo.BuildOrderDetailForm;
import com.yrt.project.api.business.plan.vo.BuildOrderForm;
import com.yrt.project.api.business.plan.vo.EditPlanForm;
import com.yrt.project.api.business.plan.vo.IDForm;
import com.yrt.project.api.business.plan.vo.PlanDetailForm;
import com.yrt.project.api.business.plan.vo.PlanDetailIDForm;
import com.yrt.project.api.business.plan.vo.PlanSearchForm;
import com.yrt.project.api.business.plan.vo.PlanSearchProductForm;
import com.yrt.project.api.business.plan.vo.SearchBatchNumForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.order.domain.OrderDetail;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.plan.domain.BuildOrderData;
import com.yrt.project.modular.plan.domain.BuildOrderData4Out;
import com.yrt.project.modular.plan.domain.Plan;
import com.yrt.project.modular.plan.domain.PlanDetail;
import com.yrt.project.modular.plan.domain.SupplierData4Out;
import com.yrt.project.modular.plan.mapper.DealerPlanMapper;
import com.yrt.project.modular.plan.mapper.HospitalPlanMapper;
import com.yrt.project.modular.plan.mapper.PlanMapper;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;

@Service
public class PlanServiceImpl implements IPlanService {
	
	@Autowired private DealerPlanMapper dpMapper;
	@Autowired DealerMapper dMapper;
	@Autowired private HospitalPlanMapper hpMapper;
	@Autowired HospitalMapper hMapper;
	
	@Autowired private IOrderService orderService;
	@Autowired private VoucherNumberService numberUtils;
	
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
        }
    }

	private Long getCurUserEmployeeId(boolean isDealer) {
		OrgEmployee curEmp = null;
		if(isDealer) {
			curEmp = dMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		}else {
			curEmp = hMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		}
		Long id = null;
		if(curEmp != null) {
			id = curEmp.getEmployee_inner_sn();
		}
		return id;
	}
	
	private String getCurUserEmployeeName(boolean isDealer) {
		OrgEmployee curEmp = null;
		if(isDealer) {
			curEmp = dMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		}else {
			curEmp = hMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		}
		String curEmpName = "";
		if(curEmp != null) {
			curEmpName = curEmp.getName();
		}
		return curEmpName;
	}
	
	private PlanMapper getPlanMapper() {
		SystemType type = ServletUtils.getCurUserSystemType();
		switch (type) {
		case HOSPITAL:
			return hpMapper;
		case DEALER:
			return dpMapper;
		default:
		}
		return null;
	}
	
	@Override
	@Transactional
	public Plan insertDealerPlan(AddPlanForm form) {
		String status = form.getStatus();
		Map<String,Object> params = new HashMap<>();
		params.put("dealer_inner_sn_i", form.getOrg_id());
		params.put("auditor_inner_sn_i", null);
		params.put("auditor_name_i", "");
		params.put("creator_inner_sn_i", getCurUserEmployeeId(true));
		params.put("making_people_name_i", getCurUserEmployeeName(true));
		params.put("department_inner_sn_i", form.getDepartment_inner_sn());
		params.put("department_name_i", form.getDepartment_name());
		if("1".equalsIgnoreCase(status)) {
			params.put("create_time_i", new Date());
		}else {
			params.put("create_time_i", null);
		}
		params.put("audit_time_i", null);
		params.put("status_i", form.getStatus());
		if(StringUtils.isEmpty(form.getPpl_sn())) {
			params.put("ppl_sn_i", numberUtils.getMaxSn(VoucherType.PURCHASE_PLAN, SystemType.DEALER, form.getOrg_id())); //.getPlanMaxSn(2, form.getOrg_id()));
		}else {
			params.put("ppl_sn_i", form.getPpl_sn());
		}
		params.put("description_i", form.getDescription());
		dpMapper.d_add_dealer_ppl(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long pid = Long.valueOf(params.get("ppl_inner_sn_o").toString());
			List<PlanDetailForm> details = form.getDetailList();
			for (PlanDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("dealer_inner_sn_i", form.getOrg_id());
				params1.put("ppl_inner_sn_i", pid);
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("status_i", 0);//新增的时候只可能是待处理
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("batch_number_i", detail.getBatch_number());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				if(detail.getProduct_type() == 1) {//厂商产品
					params1.put("product_vendor_name_i", detail.getVendor_name());
					params1.put("product_vendor_inner_sn_i", detail.getVendor_inner_sn());
					dpMapper.d_add_dealer_ppl_product_detail(params1);
				}else {//自建产品
					params1.put("product_dealer_inner_sn_i", detail.getVendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getVendor_name());
					dpMapper.d_add_dealer_ppl_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","采购计划明细",params1.get("message_string_o")));
				}
			}
			IDForm form1 = new IDForm();
			form1.setOrg_id(form.getOrg_id());
			form1.setPpl_inner_sn(pid);
			return selectPlanInfo(form1);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","采购计划",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","采购计划",params.get("message_string_o")));
			}
		}
	}
	
	@Override
	@Transactional
	public Plan insertHospitalPlan(AddPlanForm form) {
		String status = form.getStatus();
		Map<String,Object> params = new HashMap<>();
		params.put("hospital_inner_sn_i", form.getOrg_id());
		params.put("auditor_inner_sn_i", null);
		params.put("auditor_name_i", "");
		params.put("creator_inner_sn_i", getCurUserEmployeeId(false));
		params.put("making_people_name_i", getCurUserEmployeeName(false));
		params.put("department_inner_sn_i", form.getDepartment_inner_sn());
		params.put("department_name_i", form.getDepartment_name());
		if("1".equalsIgnoreCase(status)) {
			params.put("create_time_i", new Date());
		}else {
			params.put("create_time_i", null);
		}
		params.put("audit_time_i", null);
		params.put("status_i", form.getStatus());
		if(StringUtils.isEmpty(form.getPpl_sn())) {
			params.put("ppl_sn_i", numberUtils.getMaxSn(VoucherType.PURCHASE_PLAN, SystemType.HOSPITAL, form.getOrg_id())); //.getPlanMaxSn(1, form.getOrg_id()));
		}else {
			params.put("ppl_sn_i", form.getPpl_sn());
		}
		params.put("description_i", form.getDescription());
		hpMapper.h_add_hospital_ppl(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long pid = Long.valueOf(params.get("ppl_inner_sn_o").toString());
			List<PlanDetailForm> details = form.getDetailList();
			for (PlanDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("hospital_inner_sn_i", form.getOrg_id());
				params1.put("ppl_inner_sn_i", pid);
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("status_i", 0);//新增的时候只可能是待处理
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("batch_number_i", detail.getBatch_number());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				if(detail.getProduct_type() == 1) {//厂商产品
					params1.put("product_vendor_name_i", detail.getVendor_name());
					params1.put("product_vendor_inner_sn_i", detail.getVendor_inner_sn());
					hpMapper.h_add_hospital_ppl_product_detail(params1);
				}else {//自建产品
					params1.put("product_dealer_inner_sn_i", detail.getVendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getVendor_name());
					hpMapper.h_add_hospital_ppl_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","采购计划明细",params1.get("message_string_o")));
				}
			}
			IDForm form1 = new IDForm();
			form1.setOrg_id(form.getOrg_id());
			form1.setPpl_inner_sn(pid);
			return selectPlanInfo(form1);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","采购计划",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","采购计划",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public int updateDealerPlan(EditPlanForm form) {
		int result = 1;
		Map<String,Object> info = form.getInfo();
		int status = Integer.valueOf(form.getStatus());
		switch (status) {
		case 1:
			info.put("create_time", new Date());
			break;
		case 2:
			info.put("auditor_inner_sn", getCurUserEmployeeId(true));
			info.put("auditor_name", getCurUserEmployeeName(true));
			info.put("audit_time", new Date());
			break;
		}
		info.put("org_id", form.getOrg_id());
		info.put("ppl_inner_sn", form.getPpl_inner_sn());
		info.put("status", status);
		try {
			result = result & dpMapper.updatePlan(info);
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","采购计划","采购计划编码重复"));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","采购计划",e.getMessage()));
			}
		}
		if(result > 0) {
			Map<String,Object> tmp = new HashMap<>();
			tmp.put("org_id", form.getOrg_id());
			tmp.put("ppl_inner_sn", form.getPpl_inner_sn());
			dpMapper.deletePlanDealerDetail(tmp);
			dpMapper.deletePlanVendorDetail(tmp);
			List<PlanDetailForm> details = form.getDetailList();
			for (PlanDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("dealer_inner_sn_i", form.getOrg_id());
				params1.put("ppl_inner_sn_i", form.getPpl_inner_sn());
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("status_i", 0);//新增的时候只可能是待处理
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("batch_number_i", detail.getBatch_number());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				if(detail.getProduct_type() == 1) {//厂商产品
					params1.put("product_vendor_name_i", detail.getVendor_name());
					params1.put("product_vendor_inner_sn_i", detail.getVendor_inner_sn());
					dpMapper.d_add_dealer_ppl_product_detail(params1);
				}else {//自建产品
					params1.put("product_dealer_inner_sn_i", detail.getVendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getVendor_name());
					dpMapper.d_add_dealer_ppl_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.update.fail","采购计划明细",params1.get("message_string_o")));
				}
			}
		}else {
			throw new DBException(MessageUtils.message("db.update.fail","采购计划"));
		}
		return result;
	}
	
	@Override
	@Transactional
	public int updateHospitalPlan(EditPlanForm form) {
		int result = 1;
		Map<String,Object> info = form.getInfo();
		int status = Integer.valueOf(form.getStatus());
		switch (status) {
		case 1:
			info.put("create_time", new Date());
			break;
		case 2:
			info.put("auditor_inner_sn", getCurUserEmployeeId(false));
			info.put("auditor_name", getCurUserEmployeeName(false));
			info.put("audit_time", new Date());
			break;
		}
		info.put("org_id", form.getOrg_id());
		info.put("ppl_inner_sn", form.getPpl_inner_sn());
		info.put("status", status);
		try {
			result = result & hpMapper.updatePlan(info);
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","采购计划","采购计划编码重复"));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","采购计划",e.getMessage()));
			}
		}
		if(result > 0) {
			Map<String,Object> tmp = new HashMap<>();
			tmp.put("org_id", form.getOrg_id());
			tmp.put("ppl_inner_sn", form.getPpl_inner_sn());
			hpMapper.deletePlanDealerDetail(tmp);
			hpMapper.deletePlanVendorDetail(tmp);
			List<PlanDetailForm> details = form.getDetailList();
			for (PlanDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("hospital_inner_sn_i", form.getOrg_id());
				params1.put("ppl_inner_sn_i", form.getPpl_inner_sn());
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("status_i", 0);//新增的时候只可能是待处理
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("batch_number_i", detail.getBatch_number());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				if(detail.getProduct_type() == 1) {//厂商产品
					params1.put("product_vendor_name_i", detail.getVendor_name());
					params1.put("product_vendor_inner_sn_i", detail.getVendor_inner_sn());
					hpMapper.h_add_hospital_ppl_product_detail(params1);
				}else {//自建产品
					params1.put("product_dealer_inner_sn_i", detail.getVendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getVendor_name());
					hpMapper.h_add_hospital_ppl_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.update.fail","采购计划明细",params1.get("message_string_o")));
				}
			}
		}else {
			throw new DBException(MessageUtils.message("db.update.fail","采购计划"));
		}
		return result;
	}

	@Override
	@Transactional
	public int deletePlan(List<IDForm> list) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		for (IDForm form : list) {
			Map<String,Object> params = new HashMap<>();
			params.put("org_id", form.getOrg_id());
			params.put("ppl_inner_sn", form.getPpl_inner_sn());
			Plan plan = null;
			switch (systemType) {
			case DEALER:
				plan = dpMapper.selectPlanInfo(params);
				if(plan != null && (plan.getStatus().intValue() == 0 || plan.getStatus().intValue() == 1)) {
					dpMapper.deletePlanDealerDetail(params);
					dpMapper.deletePlanVendorDetail(params);
					dpMapper.deletePlan(params);
				}else {
					throw new DBException("只能删除草稿或者正式状态的采购计划!");
				}
				break;
			case HOSPITAL:
				plan = hpMapper.selectPlanInfo(params);
				if(plan != null && (plan.getStatus().intValue() == 0 || plan.getStatus().intValue() == 1)) {
					hpMapper.deletePlanDealerDetail(params);
					hpMapper.deletePlanVendorDetail(params);
					hpMapper.deletePlan(params);
				}else {
					throw new DBException("只能删除草稿或者正式状态的采购计划!");
				}
			default:
			}
		}
		
		return 1;
	}

	@Override
	public Plan selectPlanInfo(IDForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("ppl_inner_sn", form.getPpl_inner_sn());
		Plan plan = new Plan();
		List<PlanDetail> details = null;
		switch (systemType) {
		case DEALER:
			plan = dpMapper.selectPlanInfo(params);
			details = dpMapper.selectPlanDetailList(params);
			break;
		case HOSPITAL:
			plan = hpMapper.selectPlanInfo(params);
			details = hpMapper.selectPlanDetailList(params);
			break;
		default:
		}
		plan.setDetailList(details);
		return plan;
	}

	@Override
	public List<Plan> searchPlanList(Long oid, PlanSearchForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("status", form.getStatus());
		params.put("creator_name", form.getCreator_name());
		params.put("ppl_sn", form.getPpl_sn());
		params.put("department_inner_sn", form.getDepartment_inner_sn());
		switch (systemType) {
		case DEALER:
			params.put("creator_inner_sn", getCurUserEmployeeId(true));
			startPage();
			List<Plan> dpList = dpMapper.searchPlanList(params);
			return dpList;
		case HOSPITAL:
			params.put("creator_inner_sn", getCurUserEmployeeId(false));
			startPage();
			List<Plan> hpList = hpMapper.searchPlanList(params);
			return hpList;
		default:
		}
		return new ArrayList<>();
	}
	
	@Override
	public List<Plan> searchPlanList4Phone(AppPlanSearchForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		PlanMapper mapper = getPlanMapper();
		if(mapper == null) {
			throw new DBException("当前机构不支持采购计划功能!");
		}
		String status = null;
		switch (form.getStatus()) {
		case 0:
			status = "2";//张总要求手机只显示审核状态采购计划
			break;
		case 1:
			status = "3,4";
			break;
		case 2:
			status = null;
			break;
		}
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("status", status);
		params.put("key", form.getKey());
		switch (systemType) {
		case DEALER:
			params.put("creator_inner_sn", getCurUserEmployeeId(true));
			break;
		case HOSPITAL:
			params.put("creator_inner_sn", getCurUserEmployeeId(false));
			break;
		default:
		}
		startPage();
		return mapper.searchPlanList(params);
	}

	@Override
	@Transactional
	public int stopPlan(List<IDForm> list) {
		int result = 1;
		SystemType systemType = ServletUtils.getCurUserSystemType();
		for (IDForm form : list) {
			Map<String,Object> params = new HashMap<>();
			params.put("org_id", form.getOrg_id());
			params.put("ppl_inner_sn", form.getPpl_inner_sn());
			params.put("status", 3);
			switch (systemType) {
			case DEALER:
				result = result & dpMapper.updatePlan(params);
				break;
			case HOSPITAL:
				result = result & hpMapper.updatePlan(params);
				break;
			default:
			}
		}
		return result;
	}

	@Override
	@Transactional
	public int deletePlanDetail(PlanDetailIDForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("ppl_inner_sn", form.getPpl_inner_sn());
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		switch (systemType) {
		case DEALER:
			if(form.getProduct_type() == 1) {
				return dpMapper.deletePlanVendorDetail(params);
			}else {
				return dpMapper.deletePlanDealerDetail(params);
			}
		case HOSPITAL:
			if(form.getProduct_type() == 1) {
				return hpMapper.deletePlanVendorDetail(params);
			}else {
				return hpMapper.deletePlanDealerDetail(params);
			}
		default:
		}
		return 0;
	}

	@Override
	@Transactional
	public int stopPlanDetail(PlanDetailIDForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("ppl_inner_sn", form.getPpl_inner_sn());
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		params.put("status", 1);
		switch (systemType) {
		case DEALER:
			if(form.getProduct_type() == 1) {
				return dpMapper.updatePlanVendorDetail(params);
			}else {
				return dpMapper.updatePlanDealerDetail(params);
			}
		case HOSPITAL:
			if(form.getProduct_type() == 1) {
				return hpMapper.updatePlanVendorDetail(params);
			}else {
				return hpMapper.updatePlanDealerDetail(params);
			}
		default:
		}
		return 0;
	}
	
	@Override
	@Transactional
	public int unStopPlanDetail(PlanDetailIDForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("ppl_inner_sn", form.getPpl_inner_sn());
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		params.put("status", 0);
		switch (systemType) {
		case DEALER:
			if(form.getProduct_type() == 1) {
				return dpMapper.updatePlanVendorDetail(params);
			}else {
				return dpMapper.updatePlanDealerDetail(params);
			}
		case HOSPITAL:
			if(form.getProduct_type() == 1) {
				return hpMapper.updatePlanVendorDetail(params);
			}else {
				return hpMapper.updatePlanDealerDetail(params);
			}
		default:
		}
		return 0;
	}

	@Override
	public List<ResponseProductInfo> selectVendorProductList(Long oid, PlanSearchProductForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("product_vendor_id", form.getProduct_vendor_id());
		params.put("product_name", form.getProduct_name());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("specification_vendor_sn", form.getSpecification_vendor_sn());
		if(StringUtils.isNotEmpty(form.getSpecification())) {
			params.put("specification", form.getSpecification());
		}
		switch (systemType) {
		case DEALER:
			return dpMapper.selectVendorProductList(params);
		case HOSPITAL:
			return hpMapper.selectVendorProductList(params);
		default:
		}
		return new ArrayList<>();
	}
	
	@Override
	public List<Map<String, Object>> selectVendorProductList4Phone(AppApplayProductSearchForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOid());
		params.put("key", form.getKey());
		switch (systemType) {
		case DEALER:
			return dpMapper.selectVendorProductList4Phone(params);
		case HOSPITAL:
			return hpMapper.selectVendorProductList4Phone(params);
		default:
		}
		return new ArrayList<>();
	}

	@Override
	public List<ResponseProductInfo> selectDealerProductList(Long oid, PlanSearchProductForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("product_vendor_id", form.getProduct_vendor_id());
		params.put("product_name", form.getProduct_name());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("specification_vendor_sn", form.getSpecification_vendor_sn());
		switch (systemType) {
		case DEALER:
			return dpMapper.selectDealerProductList(params);
		case HOSPITAL:
			return hpMapper.selectDealerProductList(params);
		default:
		}
		return new ArrayList<>();
	}

	@Override
	public List<Map<String,Object>> selectDealerProductList4Phone(AppApplayProductSearchForm form){
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOid());
		params.put("key", form.getKey());
		switch (systemType) {
		case DEALER:
			return dpMapper.selectDealerProductList4Phone(params);
		case HOSPITAL:
			return hpMapper.selectDealerProductList4Phone(params);
		default:
		}
		return new ArrayList<>();
	}
	
	@Override
	public Map<String,Object> createOrderData(List<IDForm> list, Long oid) {
		Map<String,Object> result = new HashMap<>();
		List<BuildOrderData> data = new ArrayList<>();
		List<Map<String, Object>> list1 = new ArrayList<>();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		for (IDForm form : list) {
			Map<String,Object> params = new HashMap<>();
			params.put("org_id", form.getOrg_id());
			params.put("ppl_inner_sn", form.getPpl_inner_sn());
			switch (systemType) {
			case DEALER:
				List<BuildOrderData> dpBuildData = dpMapper.selectPlanProductDetailList(params);
				for(BuildOrderData bo : dpBuildData) {
					if(StringUtils.isNotNull(bo.getStatus()) && bo.getStatus().equals("0")) {
						data.add(bo);
					}
				}
				data.addAll(dpMapper.selectPlanDearlerProductDetailList(params));
				list1.addAll(dpMapper.selectPlanDetailIDList(params));
				break;
			case HOSPITAL:
				List<BuildOrderData> hpBuildData = hpMapper.selectPlanProductDetailList(params);
				for(BuildOrderData bo : hpBuildData) {
					if(StringUtils.isNotNull(bo.getStatus()) && bo.getStatus().equals("0")) {
						data.add(bo);
					}
				}
				
				data.addAll(hpMapper.selectPlanDearlerProductDetailList(params));
				list1.addAll(hpMapper.selectPlanDetailIDList(params));
				break;
			default:
			}
		}
		result.put("buildData", mergeData(data, oid, systemType));
		result.put("detail", list1);
		return result;
	}
	
	@Override
	public Map<String,Object> planDetailCreateOrderData(List<PlanDetailIDForm> list, Long oid) {
		Map<String,Object> result = new HashMap<>();
		List<BuildOrderData> data = new ArrayList<>();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		for (PlanDetailIDForm form : list) {
			Map<String,Object> params = new HashMap<>();
			params.put("org_id", form.getOrg_id());
			params.put("ppl_inner_sn", form.getPpl_inner_sn());
			params.put("detail_inner_sn", form.getDetail_inner_sn());
			switch (systemType) {
			case DEALER:
				if(form.getProduct_type() == 1) {
					data.addAll(dpMapper.selectPlanProductDetailList(params));
				}else {
					data.addAll(dpMapper.selectPlanDearlerProductDetailList(params));
				}
				break;
			case HOSPITAL:
				if(form.getProduct_type() == 1) {
					data.addAll(hpMapper.selectPlanProductDetailList(params));
				}else {
					data.addAll(hpMapper.selectPlanDearlerProductDetailList(params));
				}
				break;
			default:
			}
		}
		result.put("buildData", mergeData(data, oid, systemType));
		result.put("detail", list);
		return result;
	}
	
	private List<BuildOrderData4Out> mergeData(List<BuildOrderData> data, Long org_id, SystemType systemType){
		List<BuildOrderData4Out> result = new ArrayList<>();
		Map<String, List<BuildOrderData>> group = new HashMap<>();
		//合并相同的产品(厂商+产品+规格+批号)到同一个map中
		for (BuildOrderData detail : data) {
			String key = detail.getCustomID();
			if(group.containsKey(key)) {
				List<BuildOrderData> values = group.get(key);
				values.add(detail);
			}else {
				List<BuildOrderData> values = new ArrayList<>();
				values.add(detail);
				group.put(key, values);
			}
		}
		//相同的产品累计总订单数量
		Iterator<String> keys = group.keySet().iterator();
		while(keys.hasNext()) {
			List<BuildOrderData> values = group.get(keys.next());
			BuildOrderData4Out OrderData = new BuildOrderData4Out();
			BigDecimal totle_quantity = new BigDecimal(0);
			for (BuildOrderData buildOrderData : values) {
				totle_quantity = totle_quantity.add(buildOrderData.getQuantity());
				BeanUtils.copyBeanProp(OrderData, buildOrderData);
			}
			OrderData.setQuantity(totle_quantity);
			result.add(OrderData);
		}
		//获取合同供应商列表
		for (BuildOrderData4Out out : result) {
			List<SupplierData4Out> suplierList = new ArrayList<>();
			Map<String,Object> params = new HashMap<>();
			params.put("org_id", org_id);
			params.put("vendor_inner_sn", out.getProduct_vendor_inner_sn());
			params.put("product_inner_sn", out.getProduct_inner_sn());
			params.put("specification_inner_sn", out.getSpecification_inner_sn());
			switch (systemType) {
			case DEALER:
				if(out.getProduct_type() == 1) {
					suplierList = dpMapper.selectSupplierInfo(params);
				}else {
					suplierList = dpMapper.selectSupplierInfo1(params);
				}
				break;
			case HOSPITAL:
				if(out.getProduct_type() == 1) {
					suplierList = hpMapper.selectSupplierInfo(params);
				}else {
					suplierList = hpMapper.selectSupplierInfo1(params);
				}
				break;
			default:
			}
			out.setSupplierList(suplierList);
		}
		return result;
	}
	
	@Transactional
	private List<PurchaseOrder> addPurchaseOrder(List<BuildOrderDetailForm> list, Long orgID, SystemType systemType, ContextUserInfo curUser) {
		List<PurchaseOrder> results = new ArrayList<>(); 
		
		// groups: 按供应商分组(且，同一供应商相同产品不在同一组)===
		List<List<BuildOrderDetailForm>> groups = list.stream() //
			.collect(Collectors.groupingBy(BuildOrderDetailForm::getCustomID)) // group by: customID
			.values().stream().flatMap(sub -> {
				// group by: product_vendor_inner_sn/product_inner_sn/specification_inner_sn
				Map<String, List<BuildOrderDetailForm>> index = sub.stream().collect( //
						Collectors.groupingBy((d) -> d.getProduct_vendor_inner_sn() + "-" + d.getProduct_inner_sn()
								+ "-" + d.getSpecification_inner_sn()) //
				);
				List<List<BuildOrderDetailForm>> g = new ArrayList<>();
				for (;;) {
					List<BuildOrderDetailForm> t = index.values().stream().filter(e -> !e.isEmpty())
							.map(e -> e.remove(0)).collect(Collectors.toList());
					if (t.isEmpty()) {
						break;
					}
					g.add(t);
				}
				return g.stream();
			}) //
			.collect(Collectors.toList());
		// ====================================================
				
		//按照不同的供应商和供应商类型生成采购订单
		int i = 1;
		for(List<BuildOrderDetailForm> values : groups) {
			AddPurchaseOrderForm form1 = new AddPurchaseOrderForm();
			List<OrderDetailForm> details = form1.getDetails();
			form1.setPartA_id(orgID);
			for (BuildOrderDetailForm detail : values) {
				form1.setPartB_id(detail.getSupplierID());
				switch (systemType) {
				case HOSPITAL:
					if(detail.getSupplierType() == 0) {
						form1.setBillsType(BillsType.HD);
					}else {
						form1.setBillsType(BillsType.HV);
					}
					break;
				case DEALER:
					if(detail.getSupplierType() == 0) {
						form1.setBillsType(BillsType.DD);
					}else {
						form1.setBillsType(BillsType.DV);
					}
					break;
				default:
					continue;
				}
				form1.setStatus(1);
				List<WarehouseModel> wList = curUser.getWarehouseModelList();
				if(StringUtils.isEmpty(wList)) {
					throw new ValidateException(String.format("员工%s没有仓库权限，请联系管理员配置仓库!", curUser.getEmployee().getName()));
				}
				form1.setWarehouse_inner_sn(wList.get(0).getWarehouse_model_inner_sn());
				form1.setWarehouse_name(wList.get(0).getName());
				OrderDetailForm orderDetail = new OrderDetailForm();
				BeanUtils.copyBeanProp(orderDetail, detail);
				details.add(orderDetail);
			}
			SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
			form1.setPo_sn("CG-"+s.format(new Date())+i);
			PurchaseOrder info = orderService.addPurchaseOrder(form1, curUser);
			i++;
			if(info == null) {
				throw new DBException("生成采购订单出错!");
			}else {
				results.add(info);
			}
		}
		
		return results;
	}
	
	@Override
	@Transactional
	public List<PurchaseOrder> buildOrder(BuildOrderForm form, ContextUserInfo curUser) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		List<PurchaseOrder> results = addPurchaseOrder(form.getBuildData(), form.getOrg_id(), systemType, curUser);
		if( results.size() > 0) {
			//成功后更改原采购计划单据的状态
			List<PlanDetailIDForm> details = form.getDetail();
			for (PlanDetailIDForm detail : details) {
				Map<String,Object> params = new HashMap<>();
				params.put("org_id", detail.getOrg_id());
				params.put("ppl_inner_sn", detail.getPpl_inner_sn());
				params.put("detail_inner_sn", detail.getDetail_inner_sn());
				params.put("status", 2);
				switch (systemType) {
				case DEALER:
					if(detail.getProduct_type() == 1) {
						dpMapper.updatePlanVendorDetail(params);
					}else {
						dpMapper.updatePlanDealerDetail(params);
					}
					//检查是否所有的明细单据都已经生成订单
					params.put("status", 0);
					List<PlanDetail> dd = dpMapper.selectPlanDetailList(params);
					if(dd.size() == 0) {
						params.put("status", 4);
						dpMapper.updatePlan(params);
					}
					break;
				case HOSPITAL:
					if(detail.getProduct_type() == 1) {
						hpMapper.updatePlanVendorDetail(params);
					}else {
						hpMapper.updatePlanDealerDetail(params);
					}
					//检查是否所有的明细单据都已经生成订单
					params.put("status", 0);
					List<PlanDetail> hd = hpMapper.selectPlanDetailList(params);
					if(hd.size() == 0) {
						params.put("status", 4);
						hpMapper.updatePlan(params);
					}
					break;
				default:
				}
				//改result的状态
				for(PurchaseOrder p : results) {
					p.setStatus(4);
					for(OrderDetail o : p.getDetails()) {
						o.setStatus(2);
					}
				}
			}
			
		}
		return results;
	}

	@Override
	public List<Map<String, Object>> selectContractVendorProductList4Phone(AppApplayProductSearchForm form) {
		PlanMapper mapper = getPlanMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOid());
		params.put("key", form.getKey());
		return mapper.selectContractVendorProductList4Phone(params);
	}

	@Override
	public List<Map<String, Object>> selectContractDealerProductList4Phone(AppApplayProductSearchForm form) {
		PlanMapper mapper = getPlanMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOid());
		params.put("key", form.getKey());
		return mapper.selectContractDealerProductList4Phone(params);
	}

	@Override
	public List<String> selectProductBatchNumList(SearchBatchNumForm form) {
		PlanMapper mapper = getPlanMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("product_vendor_inner_sn", form.getProduct_vendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		if(form.getProduct_type().equals(1)) {
			return mapper.selectProductBatchNumList(params);
		}
		return mapper.selectDealerProductBatchNumList(params);
	}

}
