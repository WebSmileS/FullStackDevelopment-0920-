package com.yrt.project.api.business.order.service.impl;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.order.service.IDealerDealerOrderService;
import com.yrt.project.api.business.order.vo.order.*;
import com.yrt.project.api.business.warehouse.service.IWarehouseService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.common.service.VoucherNumberService.OrderNumberPair;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.order.domain.OrderDetail;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.order.domain.SaleOrder;
import com.yrt.project.modular.order.mapper.DealerDealerOrderMapper;
import com.yrt.project.modular.organization.domain.OrgDept;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DealerDealerOrderServiceImpl implements IDealerDealerOrderService {

	@Autowired DealerDealerOrderMapper mapper;
	@Autowired DealerMapper dMapper;
	
	@Autowired private SysParameterService paramService;
	@Autowired private VoucherNumberService numberUtils;
	
	private String getCurUserEmployeeName() {
		OrgEmployee curEmp = dMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		String curEmpName = "";
		if(curEmp != null) {
			curEmpName = curEmp.getName();
		}
		return curEmpName;
	}
	
	private Long getCurUserEmployeeId() {
		OrgEmployee curEmp = dMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
		Long id = null;
		if(curEmp != null) {
			id = curEmp.getEmployee_inner_sn();
		}
		return id;
	}
	
	@Override
	@Transactional
	public PurchaseOrder addPurchaseOrder(AddPurchaseOrderForm form, int system_type1, int system_type2) {
		Integer status  = form.getStatus();
		if(StringUtils.isNull(form.getWarehouse_inner_sn()) || StringUtils.isEmpty(form.getWarehouse_name())) {
			throw new ValidateException("????????????????????????!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("dealer_a_inner_sn_i", form.getPartA_id());
		params.put("dealer_b_inner_sn_i", form.getPartB_id());
		if(form.getBuyer_id() != null) {
			params.put("buyer_inner_sn_i", form.getBuyer_id());
			Map<String,Object> emp = new HashMap<>();
			emp.put("id", form.getPartA_id());
			emp.put("employee_inner_sn", form.getBuyer_id());
			params.put("buyer_name_i", dMapper.selectOrgEmployeeInfoById(emp).getName());
		}else {
			params.put("buyer_name_i", "");
		}
		if(form.getDepartment_id() != null) {
			params.put("purchase_department_inner_sn_i", form.getDepartment_id());
			OrgDept dept = new OrgDept();
			dept.setId(form.getPartA_id());
			dept.setDepartment_inner_sn(form.getDepartment_id());
			params.put("purchase_department_i", dMapper.selectOrgDeptInfoById(dept).getName());
		}else {
			params.put("purchase_department_i", "");
		}
		params.put("dealer_a_creator_inner_sn_i", getCurUserEmployeeId());
		params.put("dealer_a_creator_name_i", getCurUserEmployeeName());
		params.put("dealer_a_auditor_inner_sn_i", null);
		params.put("dealer_a_auditor_name_i", "");
		params.put("dealer_b_creator_inner_sn_i", null);
		params.put("dealer_b_creator_name_i", "");
		params.put("dealer_b_auditor_inner_sn_i", null);
		params.put("dealer_b_auditor_name_i", "");
		params.put("dealer_a_warehouse_inner_sn_i", form.getWarehouse_inner_sn());
		params.put("dealer_a_warehouse_name_i", form.getWarehouse_name());
		params.put("delivery_address_i", form.getDelivery_address());
		//String[] sns = numberUtils..getOrderMaxSn(system_type1, form.getPartA_id(), system_type2, form.getPartB_id());
		OrderNumberPair pair = numberUtils.getOrderNumberPair(system_type1, form.getPartA_id(), system_type2, form.getPartB_id());
		if(StringUtils.isEmpty(form.getPo_sn())){
			params.put("dealer_a_po_sn_i", pair.getPo().get());
		}else {
			params.put("dealer_a_po_sn_i", form.getPo_sn());
		}
		params.put("dealer_b_so_sn_i", pair.getSo().get());
		params.put("expect_aog_date_i", form.getExpect_aog_date());
		params.put("status_i", form.getStatus());
		params.put("create_organization_type_i", 0);
		if(status == 1) {
			params.put("create_time_i", new Date());
		}else {
			params.put("create_time_i", null);
		}
		params.put("audit_time_i", null);
		params.put("confirm_time_i", null);
		params.put("description_i", form.getDescription());
		params.put("dealer_a_affirmant_inner_sn_i", null);
		params.put("dealer_a_affirmant_name_i", "");
		params.put("dealer_b_affirmant_inner_sn_i", null);
		params.put("dealer_b_affirmant_name_i", "");
		mapper.dd_add_dealer_po_dealer_so(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long poid = Long.valueOf(params.get("po_so_inner_sn_o").toString());
			List<OrderDetailForm> details = form.getDetails();
			for (OrderDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("dealer_a_inner_sn_i", form.getPartA_id());
				params1.put("dealer_b_inner_sn_i", form.getPartB_id());
				params1.put("po_so_inner_sn_i", poid);
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("unit_price_i", detail.getUnit_price());
				params1.put("discount_i", detail.getDiscount());
				params1.put("rate_i", detail.getRate());
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("status_i", 0);
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				params1.put("batch_number_i", detail.getBatch_number());
				if(detail.getProduct_type() == 1) {//????????????
					params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
					mapper.dd_add_dealer_po_dealer_so_product_detail(params1);
				}else {
					params1.put("product_dealer_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getProduct_vendor_name());
					mapper.dd_add_dealer_po_dealer_so_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","??????????????????",params1.get("message_string_o")));
				}
			}
			OrderIDForm form1 = new OrderIDForm();
			form1.setPartA_id(form.getPartA_id());
			form1.setPartB_id(form.getPartB_id());
			form1.setPo_so_inner_sn(poid);
			return selectPurchaseOrderInfo(form1);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","????????????",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","????????????",params.get("message_string_o")));
			}
		}
	}

	@Autowired IWarehouseService wService;
	@Override
	@Transactional
	public SaleOrder addSaleOrder(AddSaleOrderForm form, int system_type1, int system_type2) {
		WarehouseModel warehouse = wService.selectTopLevelWarehouseList(form.getPartA_id(), SystemType.DEALER, 1L).get(0);
		Integer status  = form.getStatus();
		Map<String,Object> params = new HashMap<>();
		params.put("dealer_a_inner_sn_i", form.getPartA_id());
		params.put("dealer_b_inner_sn_i", form.getPartB_id());
		params.put("buyer_inner_sn_i", null);
		params.put("buyer_name_i", "");
		params.put("purchase_department_inner_sn_i", null);
		params.put("purchase_department_i", "");
		params.put("dealer_a_creator_inner_sn_i", null);
		params.put("dealer_a_creator_name_i", "");
		params.put("dealer_a_auditor_inner_sn_i", null);
		params.put("dealer_a_auditor_name_i", "");
		params.put("dealer_b_creator_inner_sn_i", getCurUserEmployeeId());
		params.put("dealer_b_creator_name_i", getCurUserEmployeeName());
		params.put("dealer_b_auditor_inner_sn_i", null);
		params.put("dealer_b_auditor_name_i", "");
		params.put("dealer_a_warehouse_inner_sn_i", warehouse.getWarehouse_model_inner_sn());
		params.put("dealer_a_warehouse_name_i", warehouse.getName());
		params.put("delivery_address_i", "");
		//String[] sns = numberUtils.getOrderMaxSn(system_type1, form.getPartA_id(), system_type2, form.getPartB_id());
		OrderNumberPair pair = numberUtils.getOrderNumberPair(system_type1, form.getPartA_id(), system_type2, form.getPartB_id());
		params.put("dealer_a_po_sn_i", pair.getPo().get());
		if(StringUtils.isEmpty(form.getPo_sn())) {
			params.put("dealer_b_so_sn_i", pair.getSo().get());
		}else {
			params.put("dealer_b_so_sn_i", form.getPo_sn());
		}
		params.put("expect_aog_date_i", form.getExpect_aog_date());
		params.put("status_i", form.getStatus());
		params.put("create_organization_type_i", 1);
		if(status == 1) {
			params.put("create_time_i", new Date());
		}else {
			params.put("create_time_i", null);
		}
		params.put("audit_time_i", null);
		params.put("confirm_time_i", null);
		params.put("description_i", form.getDescription());
		params.put("dealer_a_affirmant_inner_sn_i", null);
		params.put("dealer_a_affirmant_name_i", "");
		params.put("dealer_b_affirmant_inner_sn_i", null);
		params.put("dealer_b_affirmant_name_i", "");
		mapper.dd_add_dealer_po_dealer_so(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long poid = Long.valueOf(params.get("po_so_inner_sn_o").toString());
			List<OrderDetailForm> details = form.getDetails();
			for (OrderDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("dealer_a_inner_sn_i", form.getPartA_id());
				params1.put("dealer_b_inner_sn_i", form.getPartB_id());
				params1.put("po_so_inner_sn_i", poid);
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("unit_price_i", detail.getUnit_price());
				params1.put("discount_i", detail.getDiscount());
				params1.put("rate_i", detail.getRate());
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("status_i", 0);
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				params1.put("batch_number_i", detail.getBatch_number());
				if(detail.getProduct_type() == 1) {//????????????
					params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
					mapper.dd_add_dealer_po_dealer_so_product_detail(params1);
				}else {
					params1.put("product_dealer_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getProduct_vendor_name());
					mapper.dd_add_dealer_po_dealer_so_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","??????????????????",params1.get("message_string_o")));
				}
			}
			OrderIDForm form1 = new OrderIDForm();
			form1.setPartA_id(form.getPartA_id());
			form1.setPartB_id(form.getPartB_id());
			form1.setPo_so_inner_sn(poid);
			return selectSaleOrderInfo(form1);
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","??????????????????",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","??????????????????",params.get("message_string_o")));
			}
		}
	}

	@Override
	@Transactional
	public PurchaseOrder editPurchaseOrder(EditPurchaseOrderForm form, ContextUserInfo info) {
		int result = 1;
		int status  = form.getStatus().intValue();
		Map<String,Object> params = form.getInfo();
		switch (status) {
		case 1:
			params.put("dealer_a_creator_inner_sn", getCurUserEmployeeId());
			params.put("dealer_a_creator_name", getCurUserEmployeeName());
			params.put("create_time", new Date());
			break;
		case 2:
			params.put("dealer_a_auditor_inner_sn", getCurUserEmployeeId());
			params.put("dealer_a_auditor_name", getCurUserEmployeeName());
			params.put("audit_time", new Date());
			if(paramService.isSingle(info.getSystemType(), info.getOrgId())) {
				params.put("status", 3);
			}
			break;
		case 3:
			params.put("dealer_a_affirmant_inner_sn", getCurUserEmployeeId());
			params.put("dealer_a_affirmant_name", getCurUserEmployeeName());
			params.put("confirm_time", new Date());
			break;
		default:
			break;
		}
		result = result & mapper.updateOrder(params);
		if(result > 0) {
			Map<String,Object> tmp = new HashMap<>();
			tmp.put("partA", form.getPartA_id());
			tmp.put("partB", form.getPartB_id());
			tmp.put("po_so_inner_sn", form.getPo_so_inner_sn());
			mapper.deleteVendorOrderDetail(tmp);
			mapper.deleteDealerOrderDetail(tmp);
			List<OrderDetailForm> details = form.getDetails();
			for (OrderDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("dealer_a_inner_sn_i", form.getPartA_id());
				params1.put("dealer_b_inner_sn_i", form.getPartB_id());
				params1.put("po_so_inner_sn_i", form.getPo_so_inner_sn());
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("unit_price_i", detail.getUnit_price());
				params1.put("discount_i", detail.getDiscount());
				params1.put("rate_i", detail.getRate());
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("status_i", 0);
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				params1.put("batch_number_i", detail.getBatch_number());
				if(detail.getProduct_type() == 1) {//????????????
					params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
					mapper.dd_add_dealer_po_dealer_so_product_detail(params1);
				}else {
					params1.put("product_dealer_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getProduct_vendor_name());
					mapper.dd_add_dealer_po_dealer_so_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.update.fail","??????????????????",params1.get("message_string_o")));
				}
			}
		}else {
			throw new DBException(MessageUtils.message("db.update.fail","????????????"));
		}
		OrderIDForm form1 = new OrderIDForm();
		form1.setPartA_id(form.getPartA_id());
		form1.setPartB_id(form.getPartB_id());
		form1.setPo_so_inner_sn(form.getPo_so_inner_sn());
		return selectPurchaseOrderInfo(form1);
	}

	@Override
	@Transactional
	public SaleOrder editSaleOrder(EditSaleOrderForm form) {
		int result = 1;
		Integer status  = form.getStatus();
		Map<String,Object> params = form.getInfo();
		if( status == 1) {
			params.put("dealer_b_creator_inner_sn", getCurUserEmployeeId());
			params.put("dealer_b_creator_name", getCurUserEmployeeName());
			params.put("create_time", new Date());
		}
		if( status == 2) {
			params.put("dealer_b_auditor_inner_sn", getCurUserEmployeeId());
			params.put("dealer_b_auditor_name", getCurUserEmployeeName());
			params.put("audit_time", new Date());
		}
		if( status == 3) {
			params.put("dealer_b_affirmant_inner_sn", getCurUserEmployeeId());
			params.put("dealer_b_affirmant_name", getCurUserEmployeeName());
			params.put("confirm_time", new Date());
		}
		result = result & mapper.updateOrder(params);
		if(result > 0) {
			// ???????????????????????????
			Map<String,Object> tmp = new HashMap<>();
			tmp.put("partA", form.getPartA_id());
			tmp.put("partB", form.getPartB_id());
			tmp.put("po_so_inner_sn", form.getPo_so_inner_sn());
			mapper.deleteVendorOrderDetail(tmp);
			mapper.deleteDealerOrderDetail(tmp);
			List<OrderDetailForm> details = form.getDetails();
			for (OrderDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("dealer_a_inner_sn_i", form.getPartA_id());
				params1.put("dealer_b_inner_sn_i", form.getPartB_id());
				params1.put("po_so_inner_sn_i", form.getPo_so_inner_sn());
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("unit_price_i", detail.getUnit_price());
				params1.put("discount_i", detail.getDiscount());
				params1.put("rate_i", detail.getRate());
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("status_i", 0);
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				params1.put("batch_number_i", detail.getBatch_number());
				if(detail.getProduct_type() == 1) {//????????????
					params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
					mapper.dd_add_dealer_po_dealer_so_product_detail(params1);
				}else {
					params1.put("product_dealer_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getProduct_vendor_name());
					mapper.dd_add_dealer_po_dealer_so_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.update.fail","??????????????????",params1.get("message_string_o")));
				}
			}
		}else {
			throw new DBException(MessageUtils.message("db.update.fail","????????????"));
		}
		OrderIDForm form1 = new OrderIDForm();
		form1.setPartA_id(form.getPartA_id());
		form1.setPartB_id(form.getPartB_id());
		form1.setPo_so_inner_sn(form.getPo_so_inner_sn());
		return selectSaleOrderInfo(form1);
	}
	
	@Override
	public SaleOrder selectSaleOrderInfo(OrderIDForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_id());
		params.put("partB", form.getPartB_id());
		params.put("po_so_inner_sn", form.getPo_so_inner_sn());
		SaleOrder order = mapper.selectSaleOrderInfo(params);
		if(order != null) {
			List<OrderDetail> details = mapper.selectOrderDetailList(params);
			order.setDetails(details);
		}
		return order;
	}
	
	@Override
	public PurchaseOrder selectPurchaseOrderInfo(OrderIDForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_id());
		params.put("partB", form.getPartB_id());
		params.put("po_so_inner_sn", form.getPo_so_inner_sn());
		PurchaseOrder order = mapper.selectPurchaseOrderInfo(params);
		if(order != null) {
			List<OrderDetail> details = mapper.selectOrderDetailList(params);
			order.setDetails(details);
		}
		return order;
	}
}
