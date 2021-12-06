package com.yrt.project.api.business.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.constant.Constants;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.InventoryException;
import com.yrt.common.utils.JsonUtil4Db;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.dealer.service.IDealerProductUnitService;
import com.yrt.project.api.business.order.service.IOrderService;
import com.yrt.project.api.business.order.service.ISaleRecordService;
import com.yrt.project.api.business.order.vo.order.BuildSaleOrderDetailForm;
import com.yrt.project.api.business.order.vo.order.BuildSaleOrderForm;
import com.yrt.project.api.business.order.vo.record.AddSaleRecordForm;
import com.yrt.project.api.business.order.vo.record.AppSaleRecordSearchForm;
import com.yrt.project.api.business.order.vo.record.EditSaleRecordDetailForm;
import com.yrt.project.api.business.order.vo.record.EditSaleRecordForm;
import com.yrt.project.api.business.order.vo.record.SaleRecordDetailForm;
import com.yrt.project.api.business.order.vo.record.SaleRecordIDForm;
import com.yrt.project.api.business.order.vo.record.SearchSaleRecordDetailForm;
import com.yrt.project.api.business.order.vo.record.SearchSaleRecordForm;
import com.yrt.project.api.business.order.vo.record.SearchSaleRecordForm2;
import com.yrt.project.api.business.warehouse.service.InventoryLockService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.platform.service.IProductUnitService;
import com.yrt.project.modular.order.domain.SaleRecord;
import com.yrt.project.modular.order.domain.SaleRecordDetail;
import com.yrt.project.modular.order.domain.Sl;
import com.yrt.project.modular.order.domain.SlDetail;
import com.yrt.project.modular.order.mapper.DealerDealerSaleRecordMapper;
import com.yrt.project.modular.order.mapper.DealerHospitalSaleRecordMapper;
import com.yrt.project.modular.order.mapper.SaleRecordMapper;
import com.yrt.project.modular.order.mapper.VendorDealerSaleRecordMapper;
import com.yrt.project.modular.order.mapper.VendorHospitalSaleRecordMapper;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.warehouse.domain.InventoryKey;

@Service
public class SaleRecordServiceImpl implements ISaleRecordService {

	@Autowired
	VendorHospitalSaleRecordMapper vhsMapper;
	@Autowired
	DealerHospitalSaleRecordMapper dhsMapper;
	@Autowired
	VendorDealerSaleRecordMapper vdsMapper;
	@Autowired
	DealerDealerSaleRecordMapper ddsMapper;

	@Autowired
	DealerMapper dMapper;
	@Autowired
	HospitalMapper hMapper;
	@Autowired
	VendorMapper vMapper;

	@Autowired
	VoucherNumberService numberUtils;
	@Autowired
	InventoryLockService inventoryLocker;

	@Autowired
	IOrderService orderService;

	private String getCurUserEmployeeName() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		OrgEmployee curEmp = new OrgEmployee();
		switch (systemType) {
		case DEALER:
			curEmp = dMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
			break;
		case HOSPITAL:
			curEmp = hMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
			break;
		case VENDOR:
			curEmp = vMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
			break;
		default:
		}
		String curEmpName = "";
		if (curEmp != null) {
			curEmpName = curEmp.getName();
		}
		return curEmpName;
	}

	private Long getCurUserEmployeeId() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		OrgEmployee curEmp = new OrgEmployee();
		switch (systemType) {
		case DEALER:
			curEmp = dMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
			break;
		case HOSPITAL:
			curEmp = hMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
			break;
		case VENDOR:
			curEmp = vMapper.selectOrgEmployeeInfoByUserId(ServletUtils.getCurUserID());
			break;
		default:
		}
		Long id = null;
		if (curEmp != null) {
			id = curEmp.getEmployee_inner_sn();
		}
		return id;
	}

	private SaleRecordMapper getSRMapper(BillsType order_type) {
		switch (order_type) {
		case DD:
			return ddsMapper;
		case DV:
		case VD:
			return vdsMapper;
		case DH:
		case HD:
			return dhsMapper;
		case HV:
		case VH:
			return vhsMapper;
		default:
			break;
		}
		throw new DBException("当前机构不支持该功能!");
	}

	@Override
	public List<SaleRecord> selectSaleRecordList(SearchSaleRecordForm form) {
		SaleRecordMapper mapper = getSRMapper(form.getBillsType());
		Map<String, Object> params = new HashMap<>();
		params.put("partA", form.getPartA());
		params.put("partB", form.getPartB());
		params.put("po_so_inner_sn", form.getPo_so_inner_sn());
		return mapper.selectSaleRecordList(params);
	}

	@Override
	public List<Sl> searchSaleRecordList4Phone(AppSaleRecordSearchForm form) {
		SystemType system_type = ServletUtils.getCurUserSystemType();
		SaleRecordMapper mapper = null;
		if (system_type.equals(SystemType.DEALER)) {
			mapper = getSRMapper(BillsType.DD);
		} else if (system_type.equals(SystemType.VENDOR)) {
			mapper = getSRMapper(BillsType.DV);
		} else {
			throw new DBException("医院不支持这个功能!");
		}
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOid());
		params.put("key", form.getKey());
		return mapper.searchSaleRecord(params);
	}

	@Override
	public List<SaleRecordDetail> selectSaleRecordDetailList(SearchSaleRecordDetailForm form) {
		SaleRecordMapper mapper = getSRMapper(form.getBillsType());
		Map<String, Object> params = new HashMap<>();
		params.put("sl_inner_sn", form.getSl_inner_sn());
		params.put("org_id", form.getOrg_id());
//		if(!form.getIsAll()) {
//			params.put("status", 0);//只显示0-待生成出库单
//		}
		return mapper.selectSaleRecordDetailList(params);
	}

	@Transactional
	private void lockInventory(InventoryKey lockKey, BigDecimal old_quantity, BigDecimal new_quantity, Long unitid, boolean isNew) throws InventoryException {
		InventoryKey queryKey = InventoryKey.of(lockKey);
//		queryKey.setBatchNumber(null);
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
	
	@Transactional
	private void unLockInventory(InventoryKey lockKey, BigDecimal quantity, Long unitid) {
		inventoryLocker.unlockInventory(lockKey, unitid, quantity);
	}
	
	@Override
	@Transactional
	public Sl addSaleRecord(OrgEmployee makingPeople, SystemType systemType, @Valid AddSaleRecordForm form) {
		SaleRecordMapper mapper = getSRMapper(form.getBillsType());
		//按总库存量锁定库存，不关心具体批号
		List<SaleRecordDetailForm> details = form.getDetails();
		for (SaleRecordDetailForm detail : details) {
			InventoryKey lockKey = InventoryKey.of(form.getOrg_id(), null, detail.getProduct_vendor_inner_sn(),
					detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
					Constants.PRE_LOCK_BATCH_NUMBER, form.getBillsType());
			try {
				lockInventory(lockKey, BigDecimal.ZERO, detail.getQuantity(), detail.getUnit_inner_sn(), true);
			} catch (InventoryException ex) {
				String message = "库存不足: " + detail.getProduct_name() + ">>" + detail.getSpecification() + ">>" + detail.getUnit() + "\\n" 
						+ "批号:" + "'" + detail.getBatch_number() + "'" + "请求锁定量: " + ex.getRequestQuantity().longValue() + " 请求最小单位锁定量: " + ex.getRequestMinUnitQuantity().longValue() + "\\n"
						+ "库存余量(最小单位):" + ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE);
				throw new RuntimeException(message);
			}
		}
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("organization_inner_sn_i", form.getOrganization_inner_sn());
		params.put("po_so_inner_sn_i", null);
		params.put("warehouse_inner_sn_i", 1);// 销售单与仓库无关，数据库实际用不到，但存储过程有这个值故随便给一个就行
		params.put("seller_inner_sn_i", form.getSeller_inner_sn());
		params.put("making_people_inner_sn_i", makingPeople.getEmployee_inner_sn());
		params.put("department_inner_sn_i", form.getDepartment_id());
		params.put("status_i", 1);// 只能是正式状态
		params.put("collection_date_i", form.getCollection_date());
		params.put("billing_time_i", new Date());
		params.put("sl_sn_i", numberUtils.getMaxSn(VoucherType.SALE_RECORD, systemType, form.getOrg_id()));// .getSaleMaxSn(systemType,
																											// form.getOrg_id()));
		params.put("audit_time_i", null);
		params.put("auditor_name_i", "");
		params.put("purchaser_linkman_i", form.getPurchaser_linkman());
		params.put("purchaser_phone_i", form.getPurchaser_phone());
		params.put("seller_linkman_i", form.getSeller_linkman());
		params.put("seller_phone_i", form.getSeller_phone());
		params.put("organization_name_i", form.getOrganization_name());
		params.put("seller_name_i", form.getSeller_name());
		params.put("making_people_name_i", makingPeople.getName());
		params.put("department_name_i", form.getDepartment_name());
		params.put("description_i", form.getDescription());
		params.put("data_i", JsonUtil4Db.toJson(details));
		mapper.insertSaleOrder(params);
		if (Integer.valueOf(params.get("result_o").toString()) != 0) {
			throw new DBException(MessageUtils.message("db.add.fail", "销售单", params.get("message_string_o")));
		} else {
			Long sl_inner_sn_o = Long.parseLong(params.get("sl_inner_sn_o").toString());
			Map<String, Object> params1 = new HashMap<>();
			params1.put("org_id", form.getOrg_id());
			params1.put("sl_inner_sn", sl_inner_sn_o);
			return mapper.selectSaleRecordInfo(params1);
		}
	}

	@Override
	@Transactional
	public Long buildSaleOrder(BuildSaleOrderForm form, SystemType systemType) {
		SaleRecordMapper mapper = getSRMapper(form.getBillsType());
		//按总库存量锁定库存，不关心具体批号
		List<BuildSaleOrderDetailForm> details = form.getDetails();
		for (BuildSaleOrderDetailForm detail : details) {
			InventoryKey lockKey = InventoryKey.of(form.getOrg_id(), null, detail.getProduct_vendor_inner_sn(),
					detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
					Constants.PRE_LOCK_BATCH_NUMBER, form.getBillsType());
			try {
				lockInventory(lockKey, BigDecimal.ZERO, detail.getQuantity(), detail.getUnit_inner_sn(), true);
			} catch (InventoryException ex) {
				String message = "库存不足: " + detail.getProduct_name() + ">>" + detail.getSpecification() + ">>" + detail.getUnit() + "\\n" 
						+ "批号:" + "'" + detail.getBatch_number() + "'" + "请求锁定量: " + ex.getRequestQuantity().longValue() + " 请求最小单位锁定量: " + ex.getRequestMinUnitQuantity().longValue() + "\\n"
						+ "库存余量(最小单位):" + ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE);
				throw new RuntimeException(message);
			}
		}
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("organization_inner_sn_i", form.getOrganization_inner_sn());
		params.put("po_so_inner_sn_i", form.getPo_so_inner_sn());
		params.put("warehouse_inner_sn_i", form.getWarehouse_inner_sn());
		params.put("seller_inner_sn_i", form.getSeller_inner_sn());
		params.put("making_people_inner_sn_i", getCurUserEmployeeId());
		params.put("department_inner_sn_i", form.getDepartment_id());
		params.put("status_i", form.getStatus());
//		params.put("delivery_date_i", form.getDelivery_date());
		params.put("collection_date_i", form.getCollection_date());
		params.put("billing_time_i", new Date());
		params.put("sl_sn_i", numberUtils.getMaxSn(VoucherType.SALE_RECORD, systemType, form.getOrg_id()));
		int status = form.getStatus();
		params.put("status", status);
		if (status == 2) {
			params.put("auditor_inner_sn_i", getCurUserEmployeeId());
			params.put("auditor_name_i", getCurUserEmployeeName());
		} else {
			params.put("auditor_inner_sn_i", null);
			params.put("auditor_name_i", "");
		}
		params.put("purchaser_linkman_i", form.getPurchaser_linkman());
		params.put("purchaser_phone_i", form.getPurchaser_phone());
		params.put("seller_linkman_i", form.getSeller_linkman());
		params.put("seller_phone_i", form.getSeller_phone());
//		params.put("delivery_address_i", form.getDelivery_address());
		params.put("organization_name_i", form.getOrganization_name());
		params.put("seller_name_i", form.getSeller_name());
		params.put("making_people_name_i", getCurUserEmployeeName());
		params.put("department_name_i", form.getDepartment_name());
		params.put("description_i", form.getDescription());
		params.put("data_i", JsonUtil4Db.toJson(form.getDetails()));
		mapper.insertSaleOrder(params);
		if (Integer.valueOf(params.get("result_o").toString()) != 0) {
			throw new DBException(MessageUtils.message("db.add.fail", "销售单", params.get("message_string_o")));
		} else {
			Long sl_inner_sn_o = Long.parseLong(params.get("sl_inner_sn_o").toString());
			// TODO: 也许还有其他处理
			return sl_inner_sn_o;
		}
	}

	@Override
	@Transactional
	public int editSalerecord(@Valid EditSaleRecordForm form) {
		SaleRecordMapper mapper = getSRMapper(form.getBillsType());
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("sl_inner_sn", form.getSl_inner_sn());
		params.put("seller_inner_sn", form.getSeller_inner_sn());
		params.put("department_inner_sn", form.getDepartment_inner_sn());
		params.put("collection_date", form.getCollection_date());
		params.put("purchaser_linkman", form.getPurchaser_linkman());
		params.put("purchaser_phone", form.getPurchaser_phone());
		params.put("seller_linkman", form.getSeller_linkman());
		params.put("seller_phone", form.getSeller_phone());
		params.put("seller_name", form.getSeller_name());
		params.put("department_name", form.getDepartment_name());
		params.put("description", form.getDescription());
		int status = form.getStatus();
		params.put("status", status);
		if (status == 2) {
			params.put("auditor_inner_sn", getCurUserEmployeeId());
			params.put("auditor_name", getCurUserEmployeeName());
		} else {
			params.put("auditor_inner_sn", null);
			params.put("auditor_name", "");
		}
		List<EditSaleRecordDetailForm> details = form.getDetails();
		for (EditSaleRecordDetailForm detailForm : details) {
			int action = detailForm.getAction();
			switch (action) {
			case 0:// 0-删除
				deleteSaleRecordDetail(mapper, detailForm, form.getOrg_id(), form.getOrganization_inner_sn(),
						form.getSl_inner_sn(), form.getBillsType());
				break;
			case 1:// 1-新增
				insertSaleRecordDetail(mapper, detailForm, form.getOrg_id(), form.getOrganization_inner_sn(),
						form.getSl_inner_sn(), form.getBillsType());
				break;
			case 2:// 2-修改
				updateSaleRecordDetail(mapper, detailForm, form.getOrg_id(), form.getOrganization_inner_sn(),
						form.getSl_inner_sn(), form.getBillsType());
				break;
			}
		}
		return mapper.updateSaleRecord(params);
	}

	@Transactional
	private void insertSaleRecordDetail(SaleRecordMapper mapper, EditSaleRecordDetailForm detailForm, Long org_id,
			Long organization_inner_sn, Long sl_inner_sn, BillsType billsType) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", org_id);
		params.put("sl_inner_sn_i", sl_inner_sn);
		params.put("product_vendor_inner_sn_i", detailForm.getProduct_vendor_inner_sn());
		params.put("product_inner_sn_i", detailForm.getProduct_inner_sn());
		params.put("specification_inner_sn_i", detailForm.getSpecification_inner_sn());
		params.put("unit_inner_sn_i", detailForm.getUnit_inner_sn());
		params.put("quantity_i", detailForm.getQuantity());
		params.put("unit_price_i", detailForm.getUnit_price());
		params.put("discount_i", detailForm.getDiscount());
		params.put("rate_i", detailForm.getRate());
		params.put("sort_number_i", detailForm.getSort_number());
		params.put("product_vendor_name_i", detailForm.getProduct_vendor_name());
		params.put("product_name_i", detailForm.getProduct_name());
		params.put("specification_i", detailForm.getSpecification());
		params.put("unit_i", detailForm.getUnit());
		params.put("organization_inner_sn", organization_inner_sn);
		params.put("po_so_inner_sn_i", null);
		params.put("detail_inner_sn_i", null);
		params.put("batch_number_i", detailForm.getBatch_number());
		params.put("voucher_type_c_i", RefVoucherType.NONE.value());
		params.put("health_care_sn_i", StringUtils.isEmpty(detailForm.getHealth_care_sn())?"":detailForm.getHealth_care_sn());
		params.put("sun_sn_i", StringUtils.isEmpty(detailForm.getSun_sn())?"":detailForm.getSun_sn());
		if (detailForm.getProduct_type() == 1) {
			params.put("mdrf_inner_sn_i", detailForm.getMdrf_inner_sn());
			params.put("mdrf_sn_i", detailForm.getMdrf_sn());
			mapper.insertVendorSaleRecordDetail(params);
		} else {
			mapper.insertDealerSaleRecordDetail(params);
		}
		InventoryKey lockKey = InventoryKey.of(org_id, null, detailForm.getProduct_vendor_inner_sn(),
				detailForm.getProduct_inner_sn(), detailForm.getSpecification_inner_sn(), detailForm.getProduct_type(),
				Constants.PRE_LOCK_BATCH_NUMBER, billsType);

		try {
			lockInventory(lockKey, BigDecimal.ZERO, detailForm.getQuantity(), detailForm.getUnit_inner_sn(), true);
		} catch (InventoryException ex) {
			String message = "库存不足: " + detailForm.getProduct_name() + ">>" + detailForm.getSpecification() + ">>" + detailForm.getUnit() + "\\n" 
					+ "批号:" + "'" + detailForm.getBatch_number() + "'" + "请求锁定量: " + ex.getRequestQuantity().longValue() + " 请求最小单位锁定量: " + ex.getRequestMinUnitQuantity().longValue() + "\\n"
					+ "库存余量(最小单位):" + ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE);
			throw new RuntimeException(message);
		}
	}

	@Transactional
	private void updateSaleRecordDetail(SaleRecordMapper mapper, EditSaleRecordDetailForm detailForm, Long org_id,
			Long organization_inner_sn, Long sl_inner_sn, BillsType billsType) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", org_id);
		params.put("sl_inner_sn", sl_inner_sn);
		params.put("detail_inner_sn", detailForm.getDetail_inner_sn());
		params.put("unit_inner_sn", detailForm.getUnit_inner_sn());
		params.put("unit", detailForm.getUnit());
		params.put("quantity", detailForm.getQuantity().multiply(new BigDecimal(1000)));
		params.put("unit_price", detailForm.getUnit_price().multiply(new BigDecimal(10000)));
		params.put("discount", detailForm.getDiscount());
		params.put("rate", detailForm.getRate());
		params.put("batch_number", detailForm.getBatch_number());
		params.put("health_care_sn", detailForm.getHealth_care_sn());
		params.put("sun_sn", detailForm.getSun_sn());
		if (detailForm.getProduct_type() == 1) {
			params.put("mdrf_inner_sn", detailForm.getMdrf_inner_sn());
			params.put("mdrf_sn", detailForm.getMdrf_sn());
			mapper.updateVendorSaleRecordDetail(params);
		} else {
			mapper.updateDealerSaleRecordDetail(params);
		}
		// 修改库存锁定
		if (billsType.equals(BillsType.DD) || billsType.equals(BillsType.HD) || billsType.equals(BillsType.DH)
				|| billsType.equals(BillsType.VD) || billsType.equals(BillsType.VH)) {
			InventoryKey lockKey = InventoryKey.of(org_id, null, detailForm.getProduct_vendor_inner_sn(),
					detailForm.getProduct_inner_sn(), detailForm.getSpecification_inner_sn(), detailForm.getProduct_type(),
					Constants.PRE_LOCK_BATCH_NUMBER, billsType);
			try {
				lockInventory(lockKey, detailForm.getQuantity_old(), detailForm.getQuantity(), detailForm.getUnit_inner_sn(), false);
			} catch (InventoryException ex) {
				String message = "库存不足: " + detailForm.getProduct_name() + ">>" + detailForm.getSpecification() + ">>" + detailForm.getUnit() + "\\n" 
						+ "批号:" + "'" + detailForm.getBatch_number() + "'" + "请求锁定量: " + ex.getRequestQuantity().longValue() + " 请求最小单位锁定量: " + ex.getRequestMinUnitQuantity().longValue() + "\\n"
						+ "库存余量(最小单位):" + ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE);
				throw new RuntimeException(message);
			}
		}
	}

	@Transactional
	private void deleteSaleRecordDetail(SaleRecordMapper mapper, EditSaleRecordDetailForm detailForm, Long org_id,
			Long organization_inner_sn, Long sl_inner_sn, BillsType billsType) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", org_id);
		params.put("sl_inner_sn", sl_inner_sn);
		params.put("detail_inner_sn", detailForm.getDetail_inner_sn());
		// 删除库存锁定
		if (billsType.equals(BillsType.DD) || billsType.equals(BillsType.HD) || billsType.equals(BillsType.DH)
				|| billsType.equals(BillsType.VD) || billsType.equals(BillsType.VH)) {
			List<SaleRecordDetail> details = mapper.selectSaleRecordDetailList(params);
			for (SaleRecordDetail detail : details) {
				InventoryKey lockKey = InventoryKey.of(org_id, null, detail.getProduct_vendor_inner_sn(),
						detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
						Constants.PRE_LOCK_BATCH_NUMBER, billsType);
				unLockInventory(lockKey, detail.getQuantity(), detail.getUnit_inner_sn());
			}
		}
		// 删除销售单明细
		if (detailForm.getProduct_type() == 1) {
			mapper.deleteVendorSaleRecordDetail(params);
		} else {
			mapper.deleteDealerSaleRecordDetail(params);
		}
	}

	@Override
	@Transactional
	public int deleteSaleRecord(SaleRecordIDForm form) {
		// 0-DealerDealer 1-DealerVendor 4-hospitalDealer 5-hospitalVendor
		BillsType billsType = form.getBillsType();
		SaleRecordMapper mapper = getSRMapper(billsType);
		Map<String, Object> params = new HashMap<>();
		params.put("sl_inner_sn", form.getSl_inner_sn());
		params.put("org_id", form.getOrg_id());
		Sl info = mapper.selectSaleRecordInfo(params);
		// 删除库存锁定
		if (billsType.equals(BillsType.DD) || billsType.equals(BillsType.HD) || billsType.equals(BillsType.DH) 
				|| billsType.equals(BillsType.VD) || billsType.equals(BillsType.VH)) {
			List<SlDetail> details = mapper.searchSaleRecordDetailList(params);
			for (SlDetail detail : details) {
				InventoryKey lockKey = InventoryKey.of(form.getOrg_id(), null, detail.getProduct_vendor_inner_sn(),
						detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
						Constants.PRE_LOCK_BATCH_NUMBER, form.getBillsType());
				unLockInventory(lockKey, detail.getQuantity(), detail.getUnit_inner_sn());
			}
		}
		List<SlDetail> details = mapper.searchSaleRecordDetailList(params);
		for (SlDetail detail : details) {
			if (detail.getVoucher_type_c() == RefVoucherType.ORDER_SO) {
				orderService.updateTransferDetail(info.getOrganization_inner_sn(), form.getOrg_id(),
						detail.getVoucher_inner_sn_c(), detail.getVoucher_detail_inner_sn_c(),
						detail.getUnit_inner_sn(), detail.getQuantity(), billsType, true, "删除销售单调单");
			}
		}
		// 删除销售单明细
		mapper.deleteVendorSaleRecordDetail(params);
		if (billsType.equals(BillsType.DD) || billsType.equals(BillsType.HD) || billsType.equals(BillsType.DH)) {
			mapper.deleteDealerSaleRecordDetail(params);
		}
		// 删除销售单
		return mapper.deleteSaleRecord(params);
	}

	@Override
	public List<Sl> searchSaleRecordList(SearchSaleRecordForm2 form) {
		SystemType system_type = ServletUtils.getCurUserSystemType();
		SaleRecordMapper mapper = null;
		if (system_type.equals(SystemType.DEALER)) {
			mapper = getSRMapper(BillsType.DD);
		} else if (system_type.equals(SystemType.VENDOR)) {
			mapper = getSRMapper(BillsType.DV);
		} else {
			throw new DBException("医院不支持这个功能!");
		}
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("organization_name", form.getOrganization_name());
		params.put("sl_sn", form.getSl_sn());
		params.put("status", form.getStatus());
		params.put("seller_name", form.getSeller_name());
		return mapper.searchSaleRecord(params);
	}

	@Override
	public Sl selectSaleRecordInfo(SaleRecordIDForm form) {
		BillsType order_type = form.getBillsType();
		SaleRecordMapper mapper = getSRMapper(order_type);
		Map<String, Object> params = new HashMap<>();
		params.put("sl_inner_sn", form.getSl_inner_sn());
		params.put("org_id", form.getOrg_id());
		Sl record = mapper.selectSaleRecordInfo(params);
		if (record != null) {
			List<SlDetail> details = mapper.searchSaleRecordDetailList(params);
			record.setDetails(details);
			if (StringUtils.isNotEmpty(details)) {
				record.setVoucher_inner_sn_c(details.get(0).getVoucher_inner_sn_c());
			}
		}
		return record;
	}

	@Override
	public void rollbackVoucherStatus(Long oid, Long sid, Long detailId, BillsType billsType, int product_type,
			int status) {
		SaleRecordMapper srMapper = getSRMapper(billsType);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("sl_inner_sn", sid);
		params.put("detail_inner_sn", detailId);
		params.put("status", status);
		if (product_type == 1) {
			srMapper.updateVendorSaleRecordDetail(params);
		} else {
			srMapper.updateDealerSaleRecordDetail(params);
		}
		// 检查销售单明细只要有一条明细没有生成出库单就回滚主表状态为0-待生成出库单
		params.put("checkStatus", 0);// 检查是否还存在没有生成出库单的明细
		if (StringUtils.isNotEmpty(srMapper.checkSaleRecordFinish(params))) {
			params.put("status", 2);// 2-已审核
			srMapper.updateSaleRecord(params);
		} else {
			params.put("status", 4);// 4-出库单已完全生成
			srMapper.updateSaleRecord(params);
		}
	}

	@Autowired IProductUnitService puMapper;
	@Autowired IDealerProductUnitService dpuMapper;
	
	@Override
	@Transactional
	public BigDecimal updateTransferDetail(SystemType curSystemType, Long curOrgId, Long voucher_inner_sn,
			Long voucher_details_inner_sn, BigDecimal lockedQuantity, BillsType billsType, boolean isDelete) {
		SaleRecordMapper mapper = getSRMapper(billsType);
		// 获取单据明细中的批准发放量
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", curOrgId);
		params.put("sl_inner_sn", voucher_inner_sn);
		params.put("detail_inner_sn", voucher_details_inner_sn);

		//////////临时解决bug出库报错
		List<SlDetail> list = mapper.searchSaleRecordDetailList(params);
		if (list.size()==0) return BigDecimal.ZERO;

		SlDetail detail = list.get(0);
		BigDecimal toBeUnlockQuantity = BigDecimal.ZERO;
		if (detail.getProduct_type().intValue() == 1) {
			toBeUnlockQuantity = detail.getQuantity().multiply(
					puMapper.calculateProductMinUnit(detail.getProduct_vendor_inner_sn(), detail.getProduct_inner_sn(),
							detail.getSpecification_inner_sn(), detail.getUnit_inner_sn())).multiply(new BigDecimal(1000));
		} else {
			toBeUnlockQuantity = detail.getQuantity().multiply(
					dpuMapper.calculateProductMinUnit(detail.getProduct_vendor_inner_sn(), detail.getProduct_inner_sn(),
							detail.getSpecification_inner_sn(), detail.getUnit_inner_sn())).multiply(new BigDecimal(1000));
		}
		int status = 0;
		BigDecimal ret = BigDecimal.ZERO;
		if(lockedQuantity.compareTo(toBeUnlockQuantity) >= 0 && !isDelete) {
			status = 1;
			ret = lockedQuantity.subtract(toBeUnlockQuantity);
		}
		// 更新单据状态
		rollbackVoucherStatus(curOrgId, voucher_inner_sn, voucher_details_inner_sn, billsType, detail.getProduct_type(),
				status);
		return ret;
	}
}
