package com.yrt.project.api.business.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.page.PageMethod;
import com.yrt.common.client.PushService;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.PurchasingRecordStatus;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.dealer.service.IDealerProductUnitService;
import com.yrt.project.api.business.order.service.IDealerDealerOrderService;
import com.yrt.project.api.business.order.service.IDealerVendorOrderService;
import com.yrt.project.api.business.order.service.IHospitalDealerOrderService;
import com.yrt.project.api.business.order.service.IHospitalVendorOrderService;
import com.yrt.project.api.business.order.service.IOrderService;
import com.yrt.project.api.business.order.service.PurchasingRecordService;
import com.yrt.project.api.business.order.vo.order.AddPurchaseOrderForm;
import com.yrt.project.api.business.order.vo.order.AddSaleOrderForm;
import com.yrt.project.api.business.order.vo.order.ConfirmIDForm;
import com.yrt.project.api.business.order.vo.order.DetailCIDForm;
import com.yrt.project.api.business.order.vo.order.EditPurchaseOrderForm;
import com.yrt.project.api.business.order.vo.order.EditSaleOrderForm;
import com.yrt.project.api.business.order.vo.order.OrderIDForm;
import com.yrt.project.api.business.order.vo.order.OrderSendForm;
import com.yrt.project.api.business.order.vo.order.PurchaseHistorySearchForm;
import com.yrt.project.api.business.order.vo.order.PurchaseSearchForm;
import com.yrt.project.api.business.order.vo.order.SaleDetailSearchForm;
import com.yrt.project.api.business.order.vo.order.SaleHistorySearchForm;
import com.yrt.project.api.business.order.vo.order.SaleSearchForm;
import com.yrt.project.api.business.order.vo.order.SearchOrderPriceForm;
import com.yrt.project.api.business.order.vo.order.SendDetailForm;
import com.yrt.project.api.business.order.vo.order.SearchPurchaseRecordForm;
import com.yrt.project.api.business.warehouse.service.IGrnService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.platform.service.IProductUnitService;
import com.yrt.project.modular.contract.domain.ContractProduct;
import com.yrt.project.modular.order.domain.OrderDetail;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.order.domain.PurchasingRecord;
import com.yrt.project.modular.order.domain.SaleOrder;
import com.yrt.project.modular.order.mapper.DealerDealerOrderMapper;
import com.yrt.project.modular.order.mapper.DealerVendorOrderMapper;
import com.yrt.project.modular.order.mapper.HospitalDealerOrderMapper;
import com.yrt.project.modular.order.mapper.HospitalVendorOrderMapper;
import com.yrt.project.modular.order.mapper.OrderMapper;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.plan.mapper.DealerPlanMapper;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;

@Service
public class OrderServiceImpl implements IOrderService {
	
	@Autowired IDealerDealerOrderService ddService;
	@Autowired IDealerVendorOrderService dvService;
	@Autowired IHospitalDealerOrderService hdService;
	@Autowired IHospitalVendorOrderService hvService;
	
	@Autowired DealerDealerOrderMapper ddMapper;
	@Autowired DealerVendorOrderMapper dvMapper;
	@Autowired HospitalDealerOrderMapper hdMapper;
	@Autowired HospitalVendorOrderMapper hvMapper;

//	@Autowired DealerMapper dMapper;
	@Autowired private DealerPlanMapper planMapper;
	
	@Autowired private VoucherNumberService numberUtils;
	@Autowired PushService pushService;
	@Autowired IGrnService grnService;
	@Autowired PurchasingRecordService recordService;
	
	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	private int getOrganization_type(BillsType billsType) {
		switch (billsType) {
		case DD:
			return 2;
		case DV:
			return 3;
		case DH:
			return 1;
		case HH:
			return 1;
		case HD:
			return 2;
		case HV:
			return 3;
		case VD:
			return 2;
		case VH:
			return 1;
		case VV:
			return 3;
		}
		throw new ValidateException("错误的业务类型!");
	}
	
	private BillsType convertBillsType(BillsType billsType) {
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
	
	private SystemType getOrgSytem_type(BillsType billsType) {
		switch (billsType) {
		case DD:
			return SystemType.DEALER;
		case DV:
			return SystemType.DEALER;
		case DH:
			return SystemType.DEALER;
		case HH:
			return SystemType.HOSPITAL;
		case HD:
			return SystemType.HOSPITAL;
		case HV:
			return SystemType.HOSPITAL;
		case VD:
			return SystemType.VENDOR;
		case VH:
			return SystemType.VENDOR;
		case VV:
			return SystemType.VENDOR;
		}
		throw new ValidateException("错误的业务类型!");
	}
	
	private OrderMapper getMapper(BillsType type) {
		switch (type) {
		case DD://DealerDealer
			return ddMapper;
		case DV://DealerVendor
		case VD:
			return dvMapper;
		case DH:
		case HD://hospitalDealer
			return hdMapper;
		case HV://hospitalVendor
		case VH:
			return hvMapper;
		default:
			break;
		}
		throw new RuntimeException("错误的单据类型!");
	}
	
	@Override
	@Transactional
	public PurchaseOrder addPurchaseOrder(AddPurchaseOrderForm form, ContextUserInfo curUser) {
		BillsType type = form.getBillsType();
		PurchaseOrder order = null;
		switch (type) {
		case DD://DealerDealer
			order = ddService.addPurchaseOrder(form, 2, 2);
			break;
		case DV://DealerVendor
			order =  dvService.addPurchaseOrder(form, 2, 3);
			break;
		case HD://hospitalDealer
			order =  hdService.addPurchaseOrder(form, 1, 2);
			break;
		case HV://hospitalVendor
			order =  hvService.addPurchaseOrder(form, 1, 3);
			break;
		default:
			throw new RuntimeException("错误的单据类型!");
		}
//		if(order.getStatus().equals(2)) {
//			pushService.sendOrderMessage(order.getPartB(), getOrganization_type(type), order.getPartA_name(), order.getSo_sn());
//		}
//		updatePurchasingRecordStatus(order.getPartA(), order.getPartB(), order.getPo_so_inner_sn(), null, type,
//				PurchasingRecordStatus.TO_BE_VERIFY, curUser.getEmployee().getName());
		return order;
	}

	@Override
	@Transactional
	public SaleOrder addSaleOrder(AddSaleOrderForm form, ContextUserInfo curUser) {
		BillsType type = form.getBillsType();
		SaleOrder order = null;
		switch (type) {
		case DD://DealerDealer
			order =  ddService.addSaleOrder(form, 2, 2);
			break;
		case VD://DealerVendor
			order =  dvService.addSaleOrder(form, 2, 3);
			break;
		case DH:
			order =  hdService.addSaleOrder(form, 1, 2);
			break;
		case VH://hospitalVendor
			order =  hvService.addSaleOrder(form, 1, 3);
			break;
		default:
			throw new RuntimeException("错误的单据类型!");
		}
//		if(order.getStatus().equals(2)) {
//			pushService.sendOrderMessage(order.getPartA(), getOrganization_type(type), order.getPartB_name(), order.getPo_sn());
//		}
//		updatePurchasingRecordStatus(order.getPartA(), order.getPartB(), order.getPo_so_inner_sn(), null, type,
//				PurchasingRecordStatus.TO_BE_VERIFY, curUser.getEmployee().getName());
		return order;
	}

	@Override
	@Transactional
	public PurchaseOrder editPurchaseOrder(EditPurchaseOrderForm form, ContextUserInfo curUser) {
		BillsType type = form.getBillsType();
		PurchaseOrder order = null;
		switch (type) {
		case DD://DealerDealer
			order =  ddService.editPurchaseOrder(form, curUser);
			break;
		case DV://DealerVendor
			order =  dvService.editPurchaseOrder(form, curUser);
			break;
		case HD://hospitalDealer
			order =  hdService.editPurchaseOrder(form, curUser);
			break;
		case HV://hospitalVendor
			order =  hvService.editPurchaseOrder(form, curUser);
			break;
		default:
			throw new RuntimeException("错误的单据类型!");
		}
		recordService.deletePurchasingRecord(null, order.getPartA(), order.getPartB(), type, RefVoucherType.ORDER_PO, order.getPo_so_inner_sn(), false);
		PurchasingRecordStatus recordStatus = PurchasingRecordStatus.TO_BE_VERIFY;
		switch (order.getStatus()) {
		case 0:
		case 1:
			break;
		case 2:
			recordStatus = PurchasingRecordStatus.TO_BE_CONFIRM;
			break;
		case 3:
			recordStatus = PurchasingRecordStatus.EXECUTING;
			break;
		default:
			break;
		}
		if(order.getStatus().equals(2)) {
			pushService.sendOrderMessage(order.getPartB(), getOrganization_type(type), order.getPartA_name(), order.getSo_sn());
		}
		if(PurchasingRecordStatus.EXECUTING.equals(recordStatus)) {
			updatePurchasingRecordStatus(getMapper(type), order.getPartA(), order.getPartB(), order.getPo_so_inner_sn(), null,
					recordStatus, curUser.getEmployee().getName());
		}
		return order;
	}

	@Override
	@Transactional
	public SaleOrder editSaleOrder(EditSaleOrderForm form, ContextUserInfo curUser) {
		BillsType type = form.getBillsType();
		SaleOrder order = null;
		switch (type) {
		case DD://DealerDealer
			order =  ddService.editSaleOrder(form);
			break;
		case DV://DealerVendor
		case VD:
			order =  dvService.editSaleOrder(form);
			break;
		case HD://hospitalDealer
		case DH:
			order =  hdService.editSaleOrder(form);
			break;
		case HV://hospitalVendor
		case VH:
			order =  hvService.editSaleOrder(form);
			break;
		default:
			throw new RuntimeException("错误的单据类型!");
		}
		recordService.deletePurchasingRecord(null, order.getPartA(), order.getPartB(), type, RefVoucherType.ORDER_PO, order.getPo_so_inner_sn(), true);
		PurchasingRecordStatus recordStatus = PurchasingRecordStatus.TO_BE_VERIFY;
		switch (order.getStatus()) {
		case 0:
		case 1:
			break;
		case 2:
			recordStatus = PurchasingRecordStatus.TO_BE_CONFIRM;
			break;
		case 3:
			recordStatus = PurchasingRecordStatus.EXECUTING;
			break;
		default:
			break;
		}
		if(order.getStatus().equals(2)) {
			pushService.sendOrderMessage(order.getPartA(), getOrganization_type(type), order.getPartB_name(), order.getPo_sn());
		}
		if(PurchasingRecordStatus.EXECUTING.equals(recordStatus)) {
			boolean isSale = order.getCreate_organization_type().intValue()== 1?true:false;
			if(isSale) {
				type = convertBillsType(type);
			}
			updatePurchasingRecordStatus(getMapper(type), order.getPartA(), order.getPartB(), order.getPo_so_inner_sn(), null,
					recordStatus, curUser.getEmployee().getName());
		}
		return order;
	}

	@Override
	@Transactional
	public int deleteOrder(List<OrderIDForm> list) {
		for (OrderIDForm form : list) {
			BillsType type = form.getBillsType();
			OrderMapper mapper = getMapper(type);
			Map<String,Object> tmp = new HashMap<>();
			tmp.put("partA", form.getPartA_id());
			tmp.put("partB", form.getPartB_id());
			tmp.put("po_so_inner_sn", form.getPo_so_inner_sn());
			SaleOrder order = mapper.selectSaleOrderInfo(tmp);
			if(order.getStatus().compareTo(0) != 0 && order.getStatus().compareTo(1) != 0) {
				throw new RuntimeException("只能删除草稿或者正式状态的订单!");
			}
			mapper.deleteVendorOrderDetail(tmp);
			if(type.equals(BillsType.DD) || type.equals(BillsType.HD) || type.equals(BillsType.DH)) {
				mapper.deleteDealerOrderDetail(tmp);
			}
			mapper.deleteOrder(tmp);
			boolean isSale = order.getCreate_organization_type().intValue()== 1?true:false;
			recordService.deletePurchasingRecord(null, form.getPartA_id(), form.getPartB_id(), type, RefVoucherType.ORDER_PO, form.getPo_so_inner_sn(), isSale);
		}
		return 1;
	}

	@Override
	@Transactional
	public int stopOrder(List<OrderIDForm> list, ContextUserInfo curUser) {
		for (OrderIDForm form : list) {
			BillsType type = form.getBillsType();
			OrderMapper mapper = getMapper(type);
			Map<String,Object> tmp = new HashMap<>();
			tmp.put("partA", form.getPartA_id());
			tmp.put("partB", form.getPartB_id());
			tmp.put("po_so_inner_sn", form.getPo_so_inner_sn());
			tmp.put("status", 4);
			mapper.updateOrder(tmp);
			updatePurchasingRecordStatus(getMapper(type), form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), null,
					PurchasingRecordStatus.CANCEL, curUser.getEmployee().getName());
		}
		return 1;
	}
	
	@Override
	@Transactional
	public int confirmOrder(List<OrderIDForm> list, ContextUserInfo curUser) {
		for (OrderIDForm form : list) {
			BillsType type = form.getBillsType();
			OrderMapper mapper = getMapper(type);
			Map<String, Object> params = new HashMap<>();
			params.put("partA", form.getPartA_id());
			params.put("partB", form.getPartB_id());
			params.put("po_so_inner_sn", form.getPo_so_inner_sn());
			if(!isSale(form.getPartA_id(), form.getBillsType())) {
				validateWarehouse(form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), mapper, curUser);
			}
			params.put("status", 3);
			mapper.updateOrder(params);
			updatePurchasingRecordStatus(getMapper(type), form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), null,
					PurchasingRecordStatus.EXECUTING, curUser.getEmployee().getName());
		}
		return 1;
	}
	
	private void validateWarehouse(Long partA_id, Long partB_id, Long po_so_inner_sn, OrderMapper mapper, ContextUserInfo curUser) {
		Map<String,Object> tmp = new HashMap<>();
		tmp.put("partA", partA_id);
		tmp.put("partB", partB_id);
		tmp.put("po_so_inner_sn", po_so_inner_sn);
		PurchaseOrder order = mapper.selectPurchaseOrderInfo(tmp);
		List<WarehouseModel> wList = curUser.getWarehouseModelList();
		if(StringUtils.isEmpty(wList)) {
			throw new ValidateException(String.format("当前员工%s没有仓库'%s'的权限，请更换仓库！", curUser.getEmployee().getName(), order.getWarehouse_name()));
		}else {
			boolean isNotExist = true;
			for (WarehouseModel w : wList) {
				if(order.getWarehouse_inner_sn().compareTo(w.getWarehouse_model_inner_sn()) == 0) {
					isNotExist = false;
					break;
				}
			}
			if(isNotExist) throw new ValidateException(String.format("当前员工%s没有仓库'%s'的权限，请更换仓库！", curUser.getEmployee().getName(), order.getWarehouse_name()));
		}
	}

	@Transactional
	void updatePurchasingRecordStatus(OrderMapper mapper, Long partAId, Long partBId, Long po_so_inner_sn, Long detail_inner_sn,
			PurchasingRecordStatus newStatus, String update_by) {
		Map<String, Object> params = new HashMap<>();
		params.put("partA", partAId);
		params.put("partB", partBId);
		params.put("po_so_inner_sn", po_so_inner_sn);
		PurchaseOrder order = mapper.selectPurchaseOrderInfo(params);
		if (recordService.isExistPurchasingRecord(order.getPartA(), order.getPartB(), order.getBillsType(), RefVoucherType.ORDER_PO,
				order.getPo_so_inner_sn(), detail_inner_sn)) {
			recordService.updatePurchasingRecordStatus(order.getPartA(), partBId, order.getBillsType(), RefVoucherType.ORDER_PO,
					order.getPo_so_inner_sn(), detail_inner_sn, newStatus, update_by);
		} else {
			List<OrderDetail> details = mapper.selectOrderDetailList(params);
			order.setDetails(details);
			recordService.insertPurchasingRecord(order, getOrgSytem_type(order.getBillsType()), order.getCreator_name());
		}
	}
	
	private boolean isSale(Long partAId, BillsType type) {
		switch (type) {
		case DD:
		case DH:
		case DV:
			if(ServletUtils.getCurUserOrgID().compareTo(partAId) == 0
					&& ServletUtils.getCurUserSystemType().equals(SystemType.DEALER)) return false;
			break;
		case HH:
		case HD:
		case HV:
			if(ServletUtils.getCurUserOrgID().compareTo(partAId) == 0
					&& ServletUtils.getCurUserSystemType().equals(SystemType.HOSPITAL)) return false;
			break;
		case VD:
		case VH:
		case VV:
			if(ServletUtils.getCurUserOrgID().compareTo(partAId) == 0
					&& ServletUtils.getCurUserSystemType().equals(SystemType.VENDOR)) return false;
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public SaleOrder selectSaleOrderInfo(OrderIDForm form) {
		BillsType type = form.getBillsType();
		OrderMapper mapper = getMapper(type);
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

	
	public List<SaleOrder> selectSaleOrderList(SaleSearchForm form, Long curEmployeeId) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_name());
		params.put("partB", form.getPartB_id());
		params.put("so_sn", form.getOrder_sn());
		if(StringUtils.isEmpty(form.getStatus())){
			params.put("status", "0,1,2,3");
			params.put("otherStatus", "2,3");
		}else {
			String status = form.getStatus().trim();
			params.put("status", status);
			if(status.equals("2") || status.equals("3")) {
				params.put("otherStatus", status);
			}
		}
		params.put("uid", curEmployeeId);
		switch (systemType) {
		case HOSPITAL:
			return new ArrayList<>();
		case DEALER:
			return ddMapper.searchSaleOrderList(params);
		case VENDOR:
			return dvMapper.searchSaleOrderList(params);
		default:
		}
		return new ArrayList<>();
	}
	
	@Override
	public List<Map<String,Object>> selectSaleOrderDetailList(@Valid SaleDetailSearchForm form){
		BillsType type = form.getBillsType();
		OrderMapper mapper = getMapper(type);
		Map<String,Object> params = new HashMap<>();
		params.put("partA_i", form.getPartA_id());
		params.put("partB_i", form.getOrg_id());
		params.put("po_so_inner_sn_i", form.getPo_so_inner_sn());
		params.put("data_i", JSONObject.toJSONString(form.getWarehouse_ids()));
		switch (type) {
		case DD://DealerDealer
		case HD://hospitalDealer
		case DH:
			params.put("data_i", JSONObject.toJSONString(form.getWarehouse_ids()));
			break;
		case DV://DealerVendor
		case VD:
		case HV://hospitalVendor
			params.put("warehouse_inner_sn_i", 1);//厂商没有仓库，但接口需要...所以随便传个值到数据库
			break;
		default:
				break;
		}
		try {
			return mapper.selectSaleOrderDetailList(params);
		}catch(Exception e) {
			log.error(PageMethod.getLocalPage().toString());
		}
		return new ArrayList<>();
	}

	@Override
	public PurchaseOrder selectPurchaseOrderInfo(OrderIDForm form, OrgEmployee emp, boolean includeDetail) {
		BillsType type = form.getBillsType();
		OrderMapper mapper = getMapper(type);
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_id());
		params.put("partB", form.getPartB_id());
		params.put("po_so_inner_sn", form.getPo_so_inner_sn());
		PurchaseOrder order = mapper.selectPurchaseOrderInfo(params);
		 
		if(includeDetail && order != null) {
			List<OrderDetail> details = mapper.selectOrderDetailList(params);
			order.setDetails(details);
		}
		return order;
	}

	@Override
	public List<PurchaseOrder> selectPurchaseOrderList(PurchaseSearchForm form, Long curEmployeeId) {
		BillsType type = form.getBillsType();
		OrderMapper mapper = getMapper(type);
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_id());
		params.put("partB", form.getPartB_name());
		params.put("po_sn", form.getOrder_sn());
		params.put("purchase_department_inner_sn", form.getDepartment_id());
		params.put("buyer_inner_sn", form.getBuyer_id());
		params.put("status", "0,1,2,3");
		params.put("uid", curEmployeeId);

		params.put("start_date",form.getStart_date());
		params.put("end_date",form.getEnd_date());

		return mapper.searchPurchaseOrderList(params);
	}

	@Override
	@Transactional
	public int deleteDetailOrder(DetailCIDForm form) {
		BillsType type = form.getBillsType();
		OrderMapper mapper = getMapper(type);
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_id());
		params.put("partB", form.getPartB_id());
		params.put("po_so_inner_sn", form.getPo_so_inner_sn());
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		if(form.getProduct_type() == 1) {
			return mapper.deleteVendorOrderDetail(params);
		}else {
			return mapper.deleteDealerOrderDetail(params);
		}
	}
	
	@Override
	@Transactional
	public int stopOrderDetail(DetailCIDForm form, ContextUserInfo curUser) {
		BillsType type = form.getBillsType();
		OrderMapper mapper = getMapper(type);
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_id());
		params.put("partB", form.getPartB_id());
		params.put("po_so_inner_sn", form.getPo_so_inner_sn());
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		params.put("status", 3);
		if(form.getProduct_type() == 1) {
			mapper.updateVendorOrderDetail(params);
		}else {
			mapper.updateDealerOrderDetail(params);
		}
		updateOrderStatus(mapper, form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), true);
		updatePurchasingRecordStatus(getMapper(type), form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), form.getDetail_inner_sn(),
				PurchasingRecordStatus.CANCEL, curUser.getEmployee().getName());
		return 1;
	}
	
	@Override
	@Transactional
	public int unStopOrderDetail(DetailCIDForm form, ContextUserInfo curUser) {
		BillsType type = form.getBillsType();
		OrderMapper mapper = getMapper(type);
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_id());
		params.put("partB", form.getPartB_id());
		params.put("po_so_inner_sn", form.getPo_so_inner_sn());
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		params.put("status", 0);
		if(form.getProduct_type() == 1) {
			mapper.updateVendorOrderDetail(params);
		}else {
			mapper.updateDealerOrderDetail(params);
		}
		updateOrderStatus(mapper, form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), false);
		updatePurchasingRecordStatus(getMapper(type), form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), form.getDetail_inner_sn(),
				PurchasingRecordStatus.EXECUTING, curUser.getEmployee().getName());
		return 1;
	}

	@Override
	@Transactional
	public int createPlan(@Valid OrderSendForm form, OrgEmployee emp) {
		Map<String,Object> params = new HashMap<>();
		params.put("dealer_inner_sn_i", form.getOrg_id());
		params.put("auditor_inner_sn_i", null);
		params.put("auditor_name_i", "");
		params.put("creator_inner_sn_i", emp.getEmployee_inner_sn());
		params.put("making_people_name_i", emp.getName());
		params.put("department_inner_sn_i", form.getDepartment_inner_sn());
		params.put("department_name_i", form.getDepartment_name());
		params.put("create_time_i", new Date());
		params.put("audit_time_i", null);
		params.put("status_i", 1);
		if(StringUtils.isEmpty(form.getPo_sn())) {
			params.put("ppl_sn_i", numberUtils.getMaxSn(VoucherType.PURCHASE_PLAN, ServletUtils.getCurUserSystemType(), form.getOrg_id()));//.getPlanMaxSn(ServletUtils.getCurUserSystemType(), form.getOrg_id()));
		}else {
			params.put("ppl_sn_i", form.getPo_sn());
		}
		params.put("description_i", form.getDescription());
		planMapper.d_add_dealer_ppl(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long pid = Long.valueOf(params.get("ppl_inner_sn_o").toString());
			List<SendDetailForm> details = form.getDetails();
			for (SendDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("dealer_inner_sn_i", form.getOrg_id());
				params1.put("ppl_inner_sn_i", pid);
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("status_i", 0);//新增的时候只可能是待处理
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("batch_number_i", detail.getBatch_number());
				params1.put("unit_i", detail.getUnit());
				if(detail.getProduct_type() == 1) {//厂商产品
					params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
					params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
					planMapper.d_add_dealer_ppl_product_detail(params1);
				}else {//自建产品
					params1.put("product_dealer_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getProduct_vendor_name());
					planMapper.d_add_dealer_ppl_dealer_product_detail(params1);
				}
				if(Integer.valueOf(params1.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","采购计划明细",params1.get("message_string_o")));
				}else {
					BillsType type = form.getBillsType();
					OrderMapper mapper = getMapper(type);
					Map<String,Object> params2 = new HashMap<>();
					params2.put("partA", form.getOrganization_inner_sn());
					params2.put("partB", form.getOrg_id());
					params2.put("po_so_inner_sn", form.getPo_so_inner_sn());
					params2.put("detail_inner_sn", detail.getDetail_inner_sn());
					params2.put("status", 2);
					if(detail.getProduct_type() == 1) {
						mapper.updateVendorOrderDetail(params2);
					}else {
						mapper.updateDealerOrderDetail(params2);
					}
					updateOrderStatus(mapper, form.getOrganization_inner_sn(), form.getOrg_id(), form.getPo_so_inner_sn(), false);
				}
			}
			return 1;
		}else {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","采购计划",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","采购计划",params.get("message_string_o")));
			}
		}
	}

	@Override
	public List<SaleOrder> selectHistorySaleOrderList(SaleHistorySearchForm form) {
		SystemType type = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_name());
		params.put("partB", form.getPartB_id());
		params.put("so_sn", form.getOrder_sn());
		if(StringUtils.isEmpty(form.getStatus())){
			params.put("status", "4,5");
		}else {
			int status = Integer.valueOf(form.getStatus());
			if(status < 4 || status > 5) {
				params.put("status", "4,5");
			}else {
				params.put("status", status);
			}
		}
		switch (type) {
		case HOSPITAL://hospital
			return new ArrayList<>();
		case DEALER://Dealer
			return ddMapper.searchSaleHistoryOrderList(params);
		case VENDOR://Vendor
			return dvMapper.searchSaleHistoryOrderList(params);
		default:
			break;
		}
		return new ArrayList<>();
	}

	@Override
	public List<PurchaseOrder> selectHistoryPurchaseOrderList(PurchaseHistorySearchForm form) {
		SystemType type = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("partA", form.getPartA_id());
		params.put("partB", form.getPartB_name());
		params.put("po_sn", form.getOrder_sn());
		params.put("purchase_department_inner_sn", form.getDepartment_id());
		params.put("buyer_inner_sn", form.getBuyer_id());

		params.put("start_date",form.getStart_date());
		params.put("end_date",form.getEnd_date());

		switch (type) {
		case HOSPITAL://hospital
			return hdMapper.searchPurchaseHistoryOrderList(params);
		case DEALER://Dealer
			return ddMapper.searchPurchaseHistoryOrderList(params);
		case VENDOR://Vendor
			return new ArrayList<>();
		default:
			break;
		}
		return new ArrayList<>();
	}

	@Override
	@Transactional
	public int confirmSaleOrder(ConfirmIDForm form, ContextUserInfo curUser) {
		BillsType type = form.getBillsType();
		OrderMapper mapper = getMapper(type);
		Map<String,Object> tmp = new HashMap<>();
		tmp.put("partA", form.getPartA_id());
		tmp.put("partB", form.getPartB_id());
		tmp.put("po_so_inner_sn", form.getPo_so_inner_sn());
		tmp.put("status", 3);
//		tmp.put("so_sn", form.getSo_sn());
		mapper.updateOrder(tmp);
		updatePurchasingRecordStatus(getMapper(type), form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), null,
				PurchasingRecordStatus.EXECUTING, curUser.getEmployee().getName());
		return 1;
	}

	@Override
	@Transactional
	public int confirmPurchaseOrder(ConfirmIDForm form, ContextUserInfo curUser) {
		BillsType type = form.getBillsType();
		OrderMapper mapper = getMapper(type);
		Map<String,Object> tmp = new HashMap<>();
		tmp.put("partA", form.getPartA_id());
		tmp.put("partB", form.getPartB_id());
		tmp.put("po_so_inner_sn", form.getPo_so_inner_sn());
		validateWarehouse(form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), mapper, curUser);
		tmp.put("status", 3);
//		tmp.put("po_sn", form.getSo_sn());
		mapper.updateOrder(tmp);
		updatePurchasingRecordStatus(getMapper(type), form.getPartA_id(), form.getPartB_id(), form.getPo_so_inner_sn(), null,
				PurchasingRecordStatus.EXECUTING, curUser.getEmployee().getName());
		return 1;
	}

	@Override
	public List<SaleOrder> selectSaleOrderList4Phone(Long oid, String key, Integer status, Long curEmployeeId) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("partB", oid);
		params.put("key", key);
		params.put("uid", curEmployeeId);
		
		switch (systemType) {
		case HOSPITAL:
			return new ArrayList<>();
		case DEALER:
			if(status == -1){
				params.put("status", "0,1,2,3,4,5,6");
				params.put("status1", "2,3,4,5");
			}else {
				params.put("status", status);
				if(status.intValue() != 2 && status.intValue() != 3 && status.intValue() != 4 && status.intValue() != 5) {
					return ddMapper.searchSaleOrderList4Phone1(params);
				}
				params.put("status1", status);
			}
			return ddMapper.searchSaleOrderList4Phone(params);
		case VENDOR:
			if(status == -1){
				params.put("status", "0,1,2,3,4,5,6");
				params.put("status1", "2,3,4,5");
			}else {
				params.put("status", status);
				if(status.intValue() != 2 && status.intValue() != 3 && status.intValue() != 4 && status.intValue() != 5) {
					return dvMapper.searchSaleOrderList4Phone1(params);
				}
				params.put("status1", status);
			}
			return dvMapper.searchSaleOrderList4Phone(params);
		default:
		}
		return new ArrayList<>();
	}

	@Override
	public List<PurchaseOrder> selectPurchaseOrderList4Phone(Long oid, String key, Integer status, Long curEmployeeId) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		Map<String,Object> params = new HashMap<>();
		params.put("partA", oid);
		params.put("partB", key);
		params.put("po_sn", key);
		params.put("purchase_department", key);
		params.put("buyer_name", key);
		params.put("uid", curEmployeeId);
		switch (systemType) {
		case HOSPITAL:
			if(status == -1){
				params.put("status", "0,1,2,3,4,5,6");
				params.put("status1", "2,3,4,5");
			}else {
				params.put("status", status);
				if(status.intValue() != 2 && status.intValue() != 3 && status.intValue() != 4 && status.intValue() != 5) {
					return hdMapper.searchPurchaseOrderList4Phone1(params);
				}
				params.put("status1", status);
			}
			return hdMapper.searchPurchaseOrderList4Phone(params);
		case DEALER:
			if(status == -1){
				params.put("status", "0,1,2,3,4,5,6");
				params.put("status1", "2,3,4,5");
			}else {
				params.put("status", status);
				if(status.intValue() != 2 && status.intValue() != 3 && status.intValue() != 4 && status.intValue() != 5) {
					return ddMapper.searchPurchaseOrderList4Phone1(params);
				}
				params.put("status1", status);
			}
			return ddMapper.searchPurchaseOrderList4Phone(params);
		case VENDOR:
			return new ArrayList<>();
		default:
		}
		return new ArrayList<>();
	}

	@Autowired IProductUnitService puMapper;
	@Autowired IDealerProductUnitService dpuMapper;
	
	@Override
	@Transactional
	public void updateTransferDetail(Long curOrgId, Long organization_inner_sn, Long voucher_inner_sn,
			Long voucher_details_inner_sn, Long unitId, BigDecimal useQuantity, BillsType billsType, boolean isDelete, String update_by) {
		OrderMapper mapper = getMapper(billsType);
		Map<String, Object> params = new HashMap<>();
		params.put("partA", curOrgId);
		params.put("partB", organization_inner_sn);
		params.put("po_so_inner_sn", voucher_inner_sn);
		params.put("detail_inner_sn", voucher_details_inner_sn);
		OrderDetail detail = mapper.selectOrderDetailList(params).get(0);
		BigDecimal srcQuantity = BigDecimal.ZERO;
		if(isDelete) {
			params.put("status", 0);//0-处理中
			updatePurchasingRecordStatus(mapper, curOrgId, organization_inner_sn, voucher_inner_sn, voucher_details_inner_sn,
					PurchasingRecordStatus.EXECUTING, update_by);
		}else {
			//计算调单数量是否超过订单数量
			if (detail.getProduct_type().intValue() == 1) {
				srcQuantity = detail.getQuantity().multiply(
						puMapper.calculateProductMinUnit(detail.getProduct_vendor_inner_sn(), detail.getProduct_inner_sn(),
								detail.getSpecification_inner_sn(), detail.getUnit_inner_sn()));
				useQuantity = useQuantity.multiply(puMapper.calculateProductMinUnit(detail.getProduct_vendor_inner_sn(),
						detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), unitId));
			} else {
				srcQuantity = detail.getQuantity().multiply(
						dpuMapper.calculateProductMinUnit(detail.getProduct_vendor_inner_sn(), detail.getProduct_inner_sn(),
								detail.getSpecification_inner_sn(), detail.getUnit_inner_sn()));
				useQuantity = useQuantity.multiply(dpuMapper.calculateProductMinUnit(detail.getProduct_vendor_inner_sn(),
						detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), unitId));
			}
			if(useQuantity.compareTo(srcQuantity) >= 0) {
				params.put("status", 1);//1-已完全处理
				updatePurchasingRecordStatus(mapper, curOrgId, organization_inner_sn, voucher_inner_sn, voucher_details_inner_sn,
						PurchasingRecordStatus.FINISHED, update_by);
			}else {
				params.put("status", 0);//0-处理中
				updatePurchasingRecordStatus(mapper, curOrgId, organization_inner_sn, voucher_inner_sn, voucher_details_inner_sn,
						PurchasingRecordStatus.EXECUTING, update_by);
			}
		}
		if (detail.getProduct_type() == 1) {
			mapper.updateVendorOrderDetail(params);
		} else {
			mapper.updateDealerOrderDetail(params);
		}
		updateOrderStatus(mapper, curOrgId, organization_inner_sn, voucher_inner_sn, false);
	}

	@Transactional
	public void updateOrderStatus(OrderMapper mapper, Long partA, Long partB, Long po_so_inner_sn, boolean isStop) {
		Map<String,Object> params = new HashMap<>();
		params.put("partA", partA);
		params.put("partB", partB);
		params.put("po_so_inner_sn", po_so_inner_sn);
		SaleOrder info = mapper.selectSaleOrderInfo(params);
		List<OrderDetail> details = mapper.selectOrderDetailList(params);
		if(info.getStatus().equals(4) && details.size() > 1) {//主单据处于4-已终止状态后，不能再修改
			return;
		}
		params.put("checkStatus", "0,2");//如果明细中没有0-处理中 2-已计划采购的状态，修改主表状态为已完结
		if(StringUtils.isEmpty(mapper.checkExistOrderDetail(params))){
			if(isStop && details.size() == 1) {
				params.put("status", 4);//4-已终止，全部明细都被终止则主单终止
			}else {
				params.put("status", 5);//5-已完结
			}
		}else {
			params.put("status", 3);//3-确认
		}
		mapper.updateOrder(params);
	}

	@Override
	public List<PurchasingRecord> selectPurchaseRecordList(SearchPurchaseRecordForm form, SystemType systemType){
		return recordService.searchPurchasingRecordList(form, systemType, false);
	}

	@Override
	public List<BigDecimal> getOrderPrice(@Valid SearchOrderPriceForm form, Long orgId) {
		List<ContractProduct> list = null;
		
		OrderMapper mapper = getMapper(form.getBillsType());
		Map<String, Object> params1 = new HashMap<>();
		params1.put("org_id", form.getSaleUse()?form.getOrganization_inner_sn():orgId);
		params1.put("organization_inner_sn", form.getSaleUse()?orgId:form.getOrganization_inner_sn());
		params1.put("product_vendor_inner_sn", form.getProduct_vendor_inner_sn());
		params1.put("product_inner_sn", form.getProduct_inner_sn());
		params1.put("specification_inner_sn", form.getSpecification_inner_sn());
		if(form.getProduct_type() == 1) {
			list = mapper.selectOrderProductDetail(params1);
		}else {
			list = mapper.selectOrderDealerProductDetail(params1);
		}
		List<BigDecimal> result = new ArrayList<>();
		for (ContractProduct product : list) {
			BigDecimal curPrice = BigDecimal.ZERO;
			if(product.getProduct_type().equals(1)) {
				BigDecimal tmp = puMapper.calculateProductMinUnit(product.getProduct_vendor_inner_sn(),
						product.getProduct_inner_sn(), product.getSpecification_inner_sn(), product.getUnit_inner_sn());
				BigDecimal minPrice = product.getPrice().divide(tmp, 4, BigDecimal.ROUND_HALF_UP);//合同的包装单位的价格转换为最小包装单位的价格
				if(StringUtils.isNotNull(minPrice)) {
					curPrice = minPrice.multiply(puMapper.calculateProductMinUnit(product.getProduct_vendor_inner_sn(),
							product.getProduct_inner_sn(), product.getSpecification_inner_sn(), form.getUnit_inner_sn()));//最小包装单位价格转换为参数单位的价格
				}
			}else {
				BigDecimal tmp = dpuMapper.calculateProductMinUnit(product.getProduct_vendor_inner_sn(),
						product.getProduct_inner_sn(), product.getSpecification_inner_sn(), product.getUnit_inner_sn());
				BigDecimal minPrice = product.getPrice().divide(tmp, 4, BigDecimal.ROUND_HALF_UP);
				if(StringUtils.isNotNull(minPrice)) {
					curPrice = minPrice.multiply(dpuMapper.calculateProductMinUnit(product.getProduct_vendor_inner_sn(),
							product.getProduct_inner_sn(), product.getSpecification_inner_sn(), form.getUnit_inner_sn()));//最小包装单位价格转换为参数单位的价格
				}
			}
			result.add(curPrice);
		}
		return result;
	}

}
