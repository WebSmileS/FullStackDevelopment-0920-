package com.yrt.project.api.business.warehouse.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.yrt.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.client.PushService;
import com.yrt.common.constant.Constants;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.ProductSnType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.InventoryException;
import com.yrt.common.exception.MoreThanInventoryException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.bean.BeanUtils;
import com.yrt.project.api.business.applyvoucher.service.IHospitalGrantService;
import com.yrt.project.api.business.invoice.vo.SearchInvoiceProductForm;
import com.yrt.project.api.business.order.service.ISaleRecordService;
import com.yrt.project.api.business.productSn.service.ProductSnService;
import com.yrt.project.api.business.productSn.vo.ProductSnForm;
import com.yrt.project.api.business.sale.vo.SearchReturnProductForm;
import com.yrt.project.api.business.warehouse.service.AllotVoucherService;
import com.yrt.project.api.business.warehouse.service.CentralService;
import com.yrt.project.api.business.warehouse.service.IOdoService;
import com.yrt.project.api.business.warehouse.service.InventoryLockService;
import com.yrt.project.api.business.warehouse.vo.ProductHealthCareSnForm;
import com.yrt.project.api.business.warehouse.vo.allot.SearchAllotVoucherForm;
import com.yrt.project.api.business.warehouse.vo.central.AddNoticeForm;
import com.yrt.project.api.business.warehouse.vo.central.NoticeDetailForm;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.EditOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoDetailForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIds;
import com.yrt.project.api.business.warehouse.vo.odo.OdoListIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchBatchNumForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoProductForm;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.api.common.service.VoucherLockService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.api.external.warehouse.SyncWarehouseService;
import com.yrt.project.api.vo.BaseQuantities;
import com.yrt.project.api.vo.Quantities;
import com.yrt.project.api.vo.VoucherKey;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.order.domain.SaleRecordDetail;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.product.domain.ProductSn;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.warehouse.domain.AllotVoucher;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;
import com.yrt.project.modular.warehouse.domain.BatchNumberDetail;
import com.yrt.project.modular.warehouse.domain.InventoryGrnParam;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.domain.Odo;
import com.yrt.project.modular.warehouse.domain.Odo4External;
import com.yrt.project.modular.warehouse.domain.OdoDetail;
import com.yrt.project.modular.warehouse.domain.OdoDetail4External;
import com.yrt.project.modular.warehouse.domain.TransferSaleRecord;
import com.yrt.project.modular.warehouse.domain.VoucherDetailQuantity;
import com.yrt.project.modular.warehouse.mapper.AllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.DealerAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.DealerDealerOdoMapper;
import com.yrt.project.modular.warehouse.mapper.DealerHospitalOdoMapper;
import com.yrt.project.modular.warehouse.mapper.DealerVendorOdoMapper;
import com.yrt.project.modular.warehouse.mapper.DealerWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalDealerOdoMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalHospitalOdoMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalVendorOdoMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.OdoMapper;
import com.yrt.project.modular.warehouse.mapper.VendorDealerOdoMapper;
import com.yrt.project.modular.warehouse.mapper.VendorHospitalOdoMapper;
import com.yrt.project.modular.warehouse.mapper.VendorWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.WareHouseMapper;

@Service
public class OdoServiceImpl implements IOdoService {

//	private static Logger logger = LoggerFactory.getLogger(OdoServiceImpl.class);

	@Autowired
	IHospitalGrantService hgService;
	@Autowired
	ISaleRecordService srService;
	@Autowired
	InventoryLockService inventoryLocker;

	@Autowired
	private DealerDealerOdoMapper ddMapper;
	@Autowired
	private DealerHospitalOdoMapper dhMapper;
	@Autowired
	private DealerVendorOdoMapper dvMapper;
	@Autowired
	private HospitalHospitalOdoMapper hhMapper;
	@Autowired
	private HospitalDealerOdoMapper hdMapper;
	@Autowired
	private HospitalVendorOdoMapper hvMapper;
	@Autowired
	private VendorDealerOdoMapper vdMapper;
	@Autowired
	private VendorHospitalOdoMapper vhMapper;

	@Autowired
	VendorMapper vMapper;
	@Autowired
	HospitalMapper hMapper;
	@Autowired
	DealerMapper dMapper;

	@Autowired
	DealerWarehouseMapper dwMapper;
	@Autowired
	HospitalWarehouseMapper hwMapper;
	@Autowired
	VendorWarehouseMapper vwMapper;

	@Autowired VoucherNumberService numberUtils;
	@Autowired PushService pushService;
	@Autowired SyncWarehouseService syncService;
	@Autowired InventoryServiceImpl inventoryService;
	@Autowired CommService commService;
	@Autowired ProductSnService productSnService;
	@Autowired CentralService centralService;

	private WareHouseMapper getWarehouseMapper() {
		SystemType type = ServletUtils.getCurUserSystemType();
		switch (type) {
			case HOSPITAL:
				return hwMapper;
			case DEALER:
				return dwMapper;
			case VENDOR:
				return vwMapper;
			default:
		}
		return null;
	}

	/**
	 * ??????????????? 0-DealerDealer 1-DealerVendor 2-DealerHospital 3-HospitalHospital
	 * 4-HospitalDealer 5-HospitalVendor
	 */
	private OdoMapper getMapper(BillsType billsType) {
		switch (billsType) {
			case DD:
				return ddMapper;
			case DV:
				return dvMapper;
			case DH:
				return dhMapper;
			case HH:
				return hhMapper;
			case HD:
				return hdMapper;
			case HV:
				return hvMapper;
			case VD:
				return vdMapper;
			case VH:
				return vhMapper;
			case VV:
		}
		throw new DBException("??????????????????????????????!");
	}

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
		return -1;
	}

	@Transactional
	void insertOdoDetail(OdoMapper mapper, @Valid OdoDetailForm form, Long oid, Long wid, Long odoid,
						 BillsType billsType, Map<InventoryKey, BaseQuantities> toBeSpecialLock, Map<InventoryKey, BaseQuantities> toBePreLock) {
		if (form.getVoucher_type_c().compareTo(RefVoucherType.NONE) != 0
				&& (form.getVoucher_inner_sn_c() == null || form.getVoucher_detail_inner_sn_c() == null)) {
			throw new DBException("??????????????????????????????????????????!");
		}

		//?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? 2021-05-21???????????????????????????????????????
		// ??????????????????2???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????*?????????
		// ????????????????????????????????????????????????????????????????????????????????????*????????????*???????????????????????????controller??????????????????????????????????????????????????????*???????????????????????????????????????????????????*
//		BigDecimal measure = commService.getProductUnitMeasure(form.getProduct_vendor_inner_sn(), form.getProduct_inner_sn(),
//				form.getSpecification_inner_sn(), form.getUnit_inner_sn(), form.getProduct_type().intValue());
//		BigDecimal buying_price = form.getBuying_price() == null?BigDecimal.ZERO:form.getBuying_price().multiply(measure);
		// ??????????????????controller????????????????????????????????????????????????????????????????????????  20210914

		BigDecimal buying_price = form.getBuying_price() == null?BigDecimal.ZERO:form.getBuying_price();


		Map<String, Object> params = new HashMap<>();
		params.put("odo_inner_sn_i", odoid);
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("specification_inner_sn_i", form.getSpecification_inner_sn());
		params.put("unit_inner_sn_i", form.getUnit_inner_sn());
		params.put("unit_price_i", form.getUnit_price());
		params.put("buying_price_i", buying_price);
		params.put("quantity_i", form.getQuantity());
		params.put("produce_date_i", form.getProduce_date());
		params.put("overdue_date_i", form.getOverdue_date());
		params.put("type_i", form.getType());
		params.put("sort_number_i", form.getSort_number());
		params.put("batch_number_i", form.getBatch_number());
		params.put("product_name_i", form.getProduct_name());
		params.put("specification_i", form.getSpecification());
		params.put("unit_i", form.getUnit_name());
		params.put("voucher_type_c_i", form.getVoucher_type_c().value());
		params.put("voucher_inner_sn_c_i", form.getVoucher_inner_sn_c());
		params.put("voucher_detail_inner_sn_c_i", form.getVoucher_detail_inner_sn_c());
		//TODO: 2021-05-10 ??????????????????????????????????????????????????????????????????????????????????????????
		String healthCareSn = form.getHealth_care_sn();
		if(!BillsType.HD.equals(billsType) && !BillsType.HH.equals(billsType) && !BillsType.HV.equals(billsType)) {
			if(StringUtils.isNotEmpty(healthCareSn)) {
				ProductSnForm snForm = new ProductSnForm();
				snForm.setVendor_inner_sn(form.getProduct_vendor_inner_sn());
				snForm.setProduct_inner_sn(form.getProduct_inner_sn());
				snForm.setSpecification_inner_sn(form.getSpecification_inner_sn());
				snForm.setSn(healthCareSn);
				snForm.setType(ProductSnType.HEALTH_CARE_SN);
				productSnService.addOrEditProductSn(oid, snForm);
			}
		}
		params.put("health_care_sn_i", StringUtils.isEmpty(healthCareSn)?"":healthCareSn);
		params.put("org_id_i", oid);
		params.put("rate_i", StringUtils.isNull(form.getRate())?BigDecimal.ZERO:form.getRate());
		if (form.getProduct_type() == 1) {// ????????????
			params.put("product_vendor_inner_sn_i", form.getProduct_vendor_inner_sn());
			params.put("product_vendor_name_i", form.getProduct_vendor_name());
			params.put("mdrf_inner_sn_i", form.getMdrf_inner_sn());
			params.put("mdrf_sn_i", form.getMdrf_sn());
			mapper.insertOdoProductDetail(params);
		} else {
			params.put("product_dealer_inner_sn_i", form.getProduct_vendor_inner_sn());
			params.put("product_dealer_name_i", form.getProduct_vendor_name());
			mapper.insertOdoDealerProductDetail(params);

		}
		if (Integer.valueOf(params.get("result_o").toString()) != 0) {
			throw new DBException(MessageUtils.message("db.add.fail", "???????????????", params.get("message_string_o")));
		}
		//???????????????????????????????????????
		if(form.getVoucher_type_c().equals(RefVoucherType.NONE)) {//???????????????????????????????????????????????????????????????????????????
			InventoryKey preLockKey = InventoryKey.of(oid, wid, form.getProduct_vendor_inner_sn(),
					form.getProduct_inner_sn(), form.getSpecification_inner_sn(), form.getProduct_type(),
					Constants.PRE_LOCK_BATCH_NUMBER, billsType);
			InventoryKey specialLockKey = InventoryKey.of(preLockKey);
			specialLockKey.setBatchNumber(form.getBatch_number());
			if(toBePreLock.containsKey(preLockKey)) {
				BaseQuantities base = toBePreLock.get(preLockKey);
				base.addLockedQuantity(form.getQuantity());
			}else {
				toBePreLock.put(preLockKey, BaseQuantities.of(form.getProduct_name(), form.getSpecification(), form.getUnit_name(), form.getUnit_inner_sn(), form.getQuantity()));
			}
			toBeSpecialLock.put(specialLockKey, BaseQuantities.of(form.getProduct_name(), form.getSpecification(), form.getUnit_name(), form.getUnit_inner_sn(), form.getQuantity()));
		}else {//?????????????????????????????????????????????????????????
			InventoryKey specialLockKey = InventoryKey.of(oid, wid, form.getProduct_vendor_inner_sn(),
					form.getProduct_inner_sn(), form.getSpecification_inner_sn(), form.getProduct_type(),
					form.getBatch_number(), billsType);
			toBeSpecialLock.put(specialLockKey, BaseQuantities.of(form.getProduct_name(), form.getSpecification(), form.getUnit_name(), form.getUnit_inner_sn(), form.getQuantity()));
		}
	}

	@Transactional
	void lockInventory(InventoryKey lockKey, BigDecimal quantity, Long unitId) throws InventoryException {
		InventoryKey queryKey = InventoryKey.of(lockKey);
		inventoryLocker.lockInventory(lockKey, queryKey, unitId, quantity);
	}

	@Transactional
	void unLockInventory(InventoryKey lockKey, BigDecimal quantity, Long unitid) {
		inventoryLocker.unlockInventory(lockKey, unitid, quantity);
	}

	@Override
	@Transactional
	public Odo insertOdo(OrgEmployee makingPeople, SystemType systemType, @Valid AddOdoForm form) {
		String sn = numberUtils.getMaxSn(VoucherType.PUT_OUT, ServletUtils.getCurUserSystemType(), form.getOrg_id());
		return insertOdoAndCheckOverVoucher(makingPeople, systemType, form, sn, false);
	}

	@Override
	@Transactional
	public Odo insertOdo(OrgEmployee makingPeople, SystemType systemType, @Valid AddOdoForm form, String sn, Map<InventoryKey, BaseQuantities> toBeSpecialLock, Map<InventoryKey, BaseQuantities> toBePreLock) {
		BillsType billsType = form.getBillsType();
		if (billsType.equals(BillsType.HH)) {
			if (form.getDepartment_inner_sn() == null) {
				throw new DBException(MessageUtils.message("db.add.fail", "?????????", "??????????????????"));
			}
		}
		OdoMapper mapper = getMapper(billsType);
		Integer status = form.getStatus();
		Map<String, Object> params = new HashMap<>();
		params.put("organization_inner_sn_i", form.getOrganization_inner_sn());
		params.put("organization_name_i", form.getOrganization_name());
		params.put("warehouse_inner_sn_i", form.getWarehouse_inner_sn());
		params.put("warehouse_name_i", form.getWarehouse_name());
		params.put("handler_inner_sn_i", form.getHandler_inner_sn());
		params.put("handler_name_i", form.getHandler_name());
		params.put("making_people_inner_sn_i",makingPeople.getEmployee_inner_sn());
		params.put("making_people_name_i", makingPeople.getName());
		params.put("auditor_name_i", "");
		if (status == 1) {
			params.put("auditor_inner_sn_i", makingPeople.getEmployee_inner_sn());
			params.put("auditor_name_i", makingPeople.getName() );
		}
		params.put("warehouse_operator_inner_sn_i", form.getWarehouse_operator_inner_sn());
		params.put("warehouse_operator_name_i", form.getWarehouse_operator_name());
		params.put("type_inner_sn_i", form.getType_inner_sn());
		params.put("type_name_i", form.getType_name());
		params.put("department_inner_sn_i", form.getDepartment_inner_sn());
		params.put("department_name_i", form.getDepartment_name());
		params.put("status_i", status);
		params.put("out_time_i", form.getOdoTime());
		if (StringUtils.isEmpty(form.getOdo_sn())) {
			params.put("odo_sn_i",sn);
		} else {
			params.put("odo_sn_i", form.getOdo_sn());
		}
		params.put("description_i", form.getDescription());
		params.put("org_id_i", form.getOrg_id());
		params.put("use_department_i", form.getUse_department());
		params.put("printed_i", 0);
		mapper.insertOdo(params);
		Long odoid = Long.valueOf(params.get("odo_inner_sn_o").toString());
		if (Integer.valueOf(params.get("result_o").toString()) != 0) {
			throw new DBException(MessageUtils.message("db.add.fail", "?????????", params.get("message_string_o")));
		}
		// ????????????
		List<OdoDetailForm> details = form.getDetails();
		for (OdoDetailForm detail : details) {
			insertOdoDetail(mapper, detail, form.getOrg_id(), form.getWarehouse_inner_sn(), odoid,
					billsType, toBeSpecialLock, toBePreLock);
		}

		OdoIDForm f1 = new OdoIDForm();
		f1.setOrg_id(form.getOrg_id());
		f1.setOdo_inner_sn(odoid);
		f1.setBillsType(form.getBillsType());
		return selectOdoInfo(f1);
	}

	@Override
	@Transactional
	public Odo insertOdoAndCheckOverVoucher(OrgEmployee makingPeople, SystemType systemType, @Valid AddOdoForm form, String sn, boolean isReturn) {
		BillsType billsType = form.getBillsType();
		// ??????????????????????????????????????????????????????
		Map<InventoryKey, BaseQuantities> toBeSpecialLock = new HashMap<>();
		//??????????????????????????????????????????
		Map<InventoryKey, BaseQuantities> toBePreLock = new HashMap<>();
		Odo odo = insertOdo(makingPeople, systemType, form, sn, toBeSpecialLock, toBePreLock);
		if (!isReturn) {
			// ??????????????????????????????????????????????????????
			checkOverVoucher(form, billsType, toBeSpecialLock, toBePreLock, odo);
		}
		return odo;
	}

	private void checkOverVoucher(AddOdoForm form, BillsType billsType, Map<InventoryKey, BaseQuantities> toBeSpecialLock, Map<InventoryKey, BaseQuantities> toBePreLock, Odo odo) {
		// ????????????????????????????????????
		Map<VoucherKey, Quantities> toBeUnLockMap = new HashMap<>();
		// ????????????????????????????????????????????????????????????????????????????????????????????????
		checkOverVoucherDetail(form.getOrg_id(), odo.getOdo_inner_sn(), billsType);
		// ????????????????????????????????????????????????????????????????????????????????????
		setToBeUnLockVoucherData(form.getOrg_id(), form.getWarehouse_inner_sn(), odo.getOdo_inner_sn(), billsType, toBeUnLockMap);
		for (Map.Entry<InventoryKey, BaseQuantities> entry : toBePreLock.entrySet()) {
			InventoryKey key = entry.getKey();
			BaseQuantities value = entry.getValue();
			try {
				lockInventory(key, value.getLockedQuantity(), value.getUnitId());
			} catch (InventoryException ex) {
				StringBuffer message = new StringBuffer();
				if (key.getBatchNumber().equalsIgnoreCase(Constants.PRE_LOCK_BATCH_NUMBER)) {
					message.append("????????????:[").append(value.getProductName()).append(">>")
							.append(value.getSpecification()).append(">>").append(value.getUnitName()).append("\\n")
							.append("??????????????????: ").append(ex.getRequestQuantity().longValue()).append(" ??????????????????????????????: ")
							.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
							.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
				} else {
					message.append("????????????:[").append(value.getProductName()).append(">>")
							.append(value.getSpecification()).append(">>").append(value.getUnitName()).append("\\n")
							.append("??????: '").append(key.getBatchNumber()).append("' ")
							.append("???????????????: ").append(ex.getRequestQuantity().longValue()).append(" ???????????????????????????: ")
							.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
							.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
				}
				throw new ValidateException(message.toString());
			}
		}
		for (Map.Entry<InventoryKey, BaseQuantities> entry : toBeSpecialLock.entrySet()) {
			InventoryKey key = entry.getKey();
			BaseQuantities value = entry.getValue();
			try {
				inventoryLocker.checkLargerThanInventory(key, value.getUnitId(), value.getLockedQuantity());
				lockInventory(key, value.getLockedQuantity(), value.getUnitId());
			} catch (InventoryException ex) {
				StringBuffer message = new StringBuffer();
				if (key.getBatchNumber().equalsIgnoreCase(Constants.PRE_LOCK_BATCH_NUMBER)) {
					message.append("????????????:[").append(value.getProductName()).append(">>")
							.append(value.getSpecification()).append(">>").append(value.getUnitName()).append("\\n")
							.append("??????????????????: ").append(ex.getRequestQuantity().longValue()).append(" ??????????????????????????????: ")
							.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
							.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
				} else {
					message.append("????????????:[").append(value.getProductName()).append(">>")
							.append(value.getSpecification()).append(">>").append(value.getUnitName()).append("\\n")
							.append("??????: '").append(key.getBatchNumber()).append("' ")
							.append("???????????????: ").append(ex.getRequestQuantity().longValue()).append(" ???????????????????????????: ")
							.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
							.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
				}
				throw new ValidateException(message.toString());
			} catch (MoreThanInventoryException ex) {
				String message = "????????????:[" + value.getProductName() + ">>" + value.getSpecification() + ">>" + value.getUnitName() + "\\n"
						+ "??????:" + "'" + key.getBatchNumber() + "'" + " ???????????????: " + ex.getRequestQuantity().longValue() + " ???????????????????????????: " + ex.getRequestMinUnitQuantity().longValue() + "\\n"
						+ "????????????(????????????):" + ex.getMoreThenQuantity().divide(Constants.QUANTITY_INT_SCALE);
				throw new ValidateException(message);
			}
		}
		for (Map.Entry<VoucherKey, Quantities> entry : toBeUnLockMap.entrySet()) {
			VoucherKey key = entry.getKey();
			Quantities value = entry.getValue();
			BigDecimal fixQuantity = BigDecimal.ZERO;
			RefVoucherType type = value.getVoucher_type_c();
			BigDecimal totQuantity = BigDecimal.ZERO;
			OdoMapper mapper = getMapper(billsType);
			List<VoucherDetailQuantity> list = mapper.getVoucherDetailQuantity(form.getOrg_id(), type, value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn());
			if (StringUtils.isNotEmpty(list)) totQuantity = list.get(0).getTotalQuantity();
			switch (type) {
				case GRANT:
					fixQuantity = hgService.updateTransferDetail(ServletUtils.getCurUserSystemType(), form.getOrg_id(),
							value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn(), totQuantity, billsType, false);
					break;
				case SALE_RECORD:
					fixQuantity = srService.updateTransferDetail(ServletUtils.getCurUserSystemType(), form.getOrg_id(),
							value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn(), totQuantity, billsType, false);
					break;
				default:
			}
			//??????????????????????????????????????????????????????????????????????????????
			if (fixQuantity.compareTo(BigDecimal.ZERO) > 0) {
				InventoryKey preLockKey = InventoryKey.of(form.getOrg_id(), form.getWarehouse_inner_sn(), value.getVendorId(),
						value.getProductId(), value.getSpecificationId(), value.getProductType(),
						Constants.PRE_LOCK_BATCH_NUMBER, billsType);
				try {
					inventoryLocker.lockInventory(preLockKey, preLockKey, fixQuantity);
				} catch (InventoryException e) {
					String message = "????????????:[" + value.getProductName() + ">>" + value.getSpecification() + ">>" + value.getUnitName() + "\\n"
							+ "??????:" + "'" + key.getBatchNumber() + "'" + "???????????????: " + e.getRequestQuantity().longValue() + " ???????????????????????????: " + e.getRequestMinUnitQuantity().longValue() + "\\n"
							+ "????????????(????????????):" + e.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE);
					throw new ValidateException(message);
				}
			}
		}
	}

	private void setToBeUnLockVoucherData(Long curOrgId, Long wid, Long odoId, BillsType billsType,
										  Map<VoucherKey, Quantities> toBeUnLockMap) {
		OdoMapper mapper = getMapper(billsType);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", curOrgId);
		params.put("odo_inner_sn", odoId);
		List<OdoDetail> details = mapper.selectOdoProductDetails(params);
		for (OdoDetail detail : details) {

			if (detail.getVoucher_type_c() != RefVoucherType.NONE && detail.getVoucher_inner_sn_c() != null
					&& detail.getVoucher_detail_inner_sn_c() != null) {
				VoucherKey lockKey = new VoucherKey();
				lockKey.voucherType = detail.getVoucher_type_c();
				lockKey.voucherId = detail.getVoucher_inner_sn_c();
				lockKey.voucherDetailId = detail.getVoucher_detail_inner_sn_c();
				lockKey.batchNumber = detail.getBatch_number();

				if (toBeUnLockMap.containsKey(lockKey)) {
					Quantities value = toBeUnLockMap.get(lockKey);
					value.addLockedQuantity(detail.getQuantity());
				} else {
					Quantities value = new Quantities();
					value.setBillsType(billsType);
					value.setVoucher_type_c(detail.getVoucher_type_c());
					value.setVoucher_inner_sn(detail.getVoucher_inner_sn_c());
					value.setVoucher_details_inner_sn(detail.getVoucher_detail_inner_sn_c());
					value.setVendorId(detail.getVendor_inner_sn());
					value.setProductId(detail.getProduct_inner_sn());
					value.setProductName(detail.getProduct_name());
					value.setSpecificationId(detail.getSpecification_inner_sn());
					value.setSpecification(detail.getSpecification());
					value.setUnitId(detail.getUnit_inner_sn());
					value.setUnitName(detail.getUnit_name());
					value.setProductType(detail.getProduct_type());
					value.addLockedQuantity(detail.getQuantity());
					toBeUnLockMap.put(lockKey, value);
				}
			}
		}
	}

	private void checkStatus(Integer old_status) {
		if(old_status.compareTo(1) >= 0 ){
			throw new ValidateException("??????????????????????????????????????????!");
		}
	}

	@Override
	@Transactional
	public Odo updateOdo(@Valid EditOdoForm form, OrgEmployee makingPeople) {
		return updateOdo(form, makingPeople, false);
	}

	@Override
	@Transactional
	public Odo updateOdo(@Valid EditOdoForm form, OrgEmployee makingPeople, boolean isReturn) {
		OdoIDForm f2 = new OdoIDForm();
		f2.setOrg_id(form.getOrg_id());
		f2.setOdo_inner_sn(form.getOdo_inner_sn());
		f2.setBillsType(form.getBillsType());
		Odo old_info = selectOdoInfo(f2);
		//????????????
		checkStatus(old_info.getStatus());

		BillsType billsType = form.getBillsType();
		OdoMapper mapper = getMapper(billsType);

		//?????????????????????????????????????????????bug#4147
		if (RefVoucherType.ALLOT.equals(old_info.getVoucher_type_c())){
			Long av_inner_sn = old_info.getVoucher_inner_sn_c();
			//????????????????????????form???quantity??????
			AllotVoucher av = avService.searchAllotVoucherDetails(av_inner_sn);
			List<AllotVoucherDetail> details = av.getDetails();
			List<OdoDetailForm> detailForms = form.getDetails();
			for (OdoDetailForm detailForm :detailForms){
				Long detail_inner_sn = detailForm.getVoucher_detail_inner_sn_c();
				Optional<AllotVoucherDetail> optional = details.stream().filter(x -> x.getDetail_inner_sn().equals(detail_inner_sn)).findAny();
				String batchNumber =StringUtils.isEmpty(detailForm.getBatch_number())?"?????????": detailForm.getBatch_number();
				if (!optional.isPresent()) throw new BusinessException("??????????????????????????????[??????:"+av.getAv_sn()+",??????:"+detailForm.getProduct_name()+",??????:"+batchNumber+"]???????????????????????????????????????????????????!");
				AllotVoucherDetail detail = optional.get();
				if (detailForm.getQuantity().compareTo(detail.getAllot_quantity())>0){
					String message = "??????????????????:[" + detail.getProduct_name() + ">>" +
							detail.getSpecification() + ">>" + detail.getUnit_name() + "\\n" +
							"??????: '" + detail.getBatch_number() + "' " +
							"??????????????????: " + detail.getAllot_quantity() + " ??????????????????: " +
							detailForm.getQuantity() + "\\n";
					throw new ValidateException(message);
				}
			}
		}


		Odo newOdo = new Odo();
		int result = 1;
		Integer status = form.getStatus();
		Map<String, Object> params = form.getInfo();
		if (status == 1) {
			params.put("auditor_inner_sn", makingPeople.getEmployee_inner_sn());
			params.put("auditor_name", makingPeople.getName());
		}
		if(params.isEmpty() && StringUtils.isEmpty(form.getDetails())) {
			return old_info;
		}
		// ?????????????????????????????????billsType
		if (billsType != null && form.getBillsType_old() != null) {
			// ????????????billsType???????????????
			if (billsType.equals(form.getBillsType_old())
					&& form.getOrganization_inner_sn().compareTo(form.getOrganization_inner_sn_old()) == 0) {
				try {
					// ??????????????????????????????????????????????????????
					deleteOldOdoDetails(mapper, billsType, form.getOrg_id(), form.getOdo_inner_sn(), isReturn);
					result = result & mapper.updateOdo(params);
				} catch (Exception e) {
					if (e instanceof DuplicateKeyException) {
						throw new DuplicateException(MessageUtils.message("db.update.fail", "?????????", "?????????????????????"));
					} else {
						throw new DBException(MessageUtils.message("db.update.fail", "?????????", e.getMessage()));
					}
				}
				Map<VoucherKey, Quantities> toBeUnLockMap = new HashMap<>();// ????????????????????????????????????
				Map<VoucherKey, Quantities> sumDelMap = new HashMap<>();// ????????????????????????????????????????????????
				Map<InventoryKey, BaseQuantities> toBeSpecialLock = new HashMap<>();//??????????????????????????????????????????????????????
				Map<InventoryKey, BaseQuantities> toBePreLock = new HashMap<>();//??????????????????????????????????????????

				List<OdoDetailForm> details = form.getDetails();

				for (OdoDetailForm detailForm : details) {
					insertOdoDetail(mapper, detailForm, form.getOrg_id(), form.getWarehouse_inner_sn(),
							form.getOdo_inner_sn(), billsType, toBeSpecialLock, toBePreLock);
				}
				if(!isReturn) {//???????????????????????????????????????????????????
					//????????????????????????????????????????????????????????????????????????????????????????????????
					checkOverVoucherDetail(form.getOrg_id(), form.getOdo_inner_sn(), billsType);
					// TODO ????????????????????????????????????????????????????????????????????????????????????
					setToBeUnLockVoucherData(form.getOrg_id(), form.getWarehouse_inner_sn(), form.getOdo_inner_sn(), billsType,
							toBeUnLockMap);
					for(Map.Entry<InventoryKey, BaseQuantities> entry: toBePreLock.entrySet()){
						InventoryKey key = entry.getKey();
						BaseQuantities value = entry.getValue();
						try {
							lockInventory(key,value.getLockedQuantity(), value.getUnitId());
						} catch (InventoryException ex) {
							StringBuffer message = new StringBuffer();
							if(key.getBatchNumber().equalsIgnoreCase(Constants.PRE_LOCK_BATCH_NUMBER)) {
								message.append("????????????:[").append(value.getProductName()).append(">>")
										.append(value.getSpecification()).append(">>").append(value.getUnitName()).append("\\n")
										.append("??????????????????: ").append(ex.getRequestQuantity().longValue()).append(" ??????????????????????????????: ")
										.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
										.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
							}else {
								message.append("????????????:[").append(value.getProductName()).append(">>")
										.append(value.getSpecification()).append(">>").append(value.getUnitName()).append("\\n")
										.append("??????: '").append(key.getBatchNumber()).append("' ")
										.append("???????????????: ").append(ex.getRequestQuantity().longValue()).append(" ???????????????????????????: ")
										.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
										.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
							}
							throw new ValidateException(message.toString());
						}
					}
					for(Map.Entry<InventoryKey, BaseQuantities> entry: toBeSpecialLock.entrySet()){
						InventoryKey key = entry.getKey();
						BaseQuantities value = entry.getValue();
						try {
							inventoryLocker.checkLargerThanInventory(key, value.getUnitId(), value.getLockedQuantity());
							lockInventory(key,value.getLockedQuantity(), value.getUnitId());
						} catch (InventoryException ex) {
							StringBuffer message = new StringBuffer();
							if(key.getBatchNumber().equalsIgnoreCase(Constants.PRE_LOCK_BATCH_NUMBER)) {
								message.append("????????????:[").append(value.getProductName()).append(">>")
										.append(value.getSpecification()).append(">>").append(value.getUnitName()).append("\\n")
										.append("??????????????????: ").append(ex.getRequestQuantity().longValue()).append(" ??????????????????????????????: ")
										.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
										.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
							}else {
								message.append("????????????:[").append(value.getProductName()).append(">>")
										.append(value.getSpecification()).append(">>").append(value.getUnitName()).append("\\n")
										.append("??????: '").append(key.getBatchNumber()).append("' ")
										.append("???????????????: ").append(ex.getRequestQuantity().longValue()).append(" ???????????????????????????: ")
										.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
										.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
							}
							throw new ValidateException(message.toString());
						} catch (MoreThanInventoryException ex) {
							String message = "????????????:[" + value.getProductName() + ">>" + value.getSpecification() + ">>" + value.getUnitName() + "\\n"
									+ "??????:" + "'" + key.getBatchNumber() + "'" + " ???????????????: " + ex.getRequestQuantity().longValue() + " ???????????????????????????: " + ex.getRequestMinUnitQuantity().longValue() + "\\n"
									+ "????????????(????????????):" + ex.getMoreThenQuantity().divide(Constants.QUANTITY_INT_SCALE);
							throw new ValidateException(message);
						}
					}
					// ??????????????????????????????????????????
					for (Quantities value : sumDelMap.values()) {
						BigDecimal fixQuantity = BigDecimal.ZERO;
						RefVoucherType type = value.getVoucher_type_c();
						BigDecimal totQuantity = BigDecimal.ZERO;
						List<VoucherDetailQuantity> list = mapper.getVoucherDetailQuantity(form.getOrg_id(), type, value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn());
						if(StringUtils.isNotEmpty(list)) totQuantity = list.get(0).getTotalQuantity();
						switch (type) {
							case GRANT:
								fixQuantity = hgService.updateTransferDetail(ServletUtils.getCurUserSystemType(), form.getOrg_id(),
										value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn(), totQuantity, billsType, true);
								break;
							case SALE_RECORD:
								fixQuantity = srService.updateTransferDetail(ServletUtils.getCurUserSystemType(), form.getOrg_id(),
										value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn(), totQuantity, billsType, true);
								break;
							default:
						}
						//??????????????????????????????????????????????????????????????????????????????
						if(fixQuantity.compareTo(BigDecimal.ZERO) > 0) {
							InventoryKey preLockKey = InventoryKey.of(form.getOrg_id(), form.getWarehouse_inner_sn(), value.getVendorId(),
									value.getProductId(), value.getSpecificationId(), value.getProductType(),
									Constants.PRE_LOCK_BATCH_NUMBER, billsType);
							inventoryLocker.unlockInventory(preLockKey, fixQuantity);
						}
					}
					// ??????toBeUnLockMap???????????????????????????????????????
					for(Map.Entry<VoucherKey, Quantities> entry: toBeUnLockMap.entrySet()){
						VoucherKey key = entry.getKey();
						Quantities value = entry.getValue();
						BigDecimal fixQuantity = BigDecimal.ZERO;
						RefVoucherType type = value.getVoucher_type_c();
						BigDecimal totQuantity = BigDecimal.ZERO;
						List<VoucherDetailQuantity> list = mapper.getVoucherDetailQuantity(form.getOrg_id(), type, value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn());
						if(StringUtils.isNotEmpty(list)) totQuantity = list.get(0).getTotalQuantity();
						switch (type) {
							case GRANT:
								fixQuantity = hgService.updateTransferDetail(ServletUtils.getCurUserSystemType(), form.getOrg_id(),
										value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn(), totQuantity, billsType, false);
								break;
							case SALE_RECORD:
								fixQuantity = srService.updateTransferDetail(ServletUtils.getCurUserSystemType(), form.getOrg_id(),
										value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn(), totQuantity, billsType, false);
								break;
							default:
						}
						//??????????????????????????????????????????????????????????????????????????????
						if(fixQuantity.compareTo(BigDecimal.ZERO) > 0) {
							InventoryKey preLockKey = InventoryKey.of(form.getOrg_id(), form.getWarehouse_inner_sn(), value.getVendorId(),
									value.getProductId(), value.getSpecificationId(), value.getProductType(),
									Constants.PRE_LOCK_BATCH_NUMBER, billsType);
							try {
								inventoryLocker.lockInventory(preLockKey, preLockKey, fixQuantity);
							} catch (InventoryException e) {
								String message = "????????????:[" + value.getProductName() + ">>" + value.getSpecification() + ">>" + value.getUnitName() + "\\n"
										+ "??????:" + "'" + key.getBatchNumber() + "'" + "???????????????: " + e.getRequestQuantity().longValue() + " ???????????????????????????: " + e.getRequestMinUnitQuantity().longValue() + "\\n"
										+ "????????????(????????????):" + e.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE);
								throw new ValidateException(message);
							}
						}
					}
				}

				OdoIDForm f1 = new OdoIDForm();
				f1.setOrg_id(form.getOrg_id());
				f1.setOdo_inner_sn(form.getOdo_inner_sn());
				f1.setBillsType(form.getBillsType());
				return selectOdoInfo(f1);
			} else {// ??????billsType????????????
				// ??????form
				OdoIDForm delForm = new OdoIDForm(form.getOrg_id(), form.getOdo_inner_sn(), form.getBillsType_old());
				result = result & deleteOdo(delForm);
				// ??????form
				AddOdoForm addForm = new AddOdoForm();
				BeanUtils.copyBeanProp(addForm, form);
				newOdo = insertOdo(null, ServletUtils.getCurUserSystemType(), addForm);
			}
		}
		return newOdo;
	}

	@Transactional
	void deleteOldOdoDetails(OdoMapper mapper, BillsType billsType, Long orgId, Long odo_inner_sn, boolean isReturn) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("odo_inner_sn", odo_inner_sn);
		Odo info = mapper.selectOdoInfo(params);
		if(info.getStatus().equals(1)) {//?????????????????????????????????
			throw new DBException("??????????????????????????????????????????????????????!");
		}
		List<OdoDetail> details = mapper.selectOdoProductDetails(params);
		for (OdoDetail detail : details) {
			RefVoucherType type = detail.getVoucher_type_c();
			if(!isReturn) {//???????????????????????????????????????????????????
				// ???????????????????????????
				if(type.equals(RefVoucherType.NONE)) {//??????????????????????????????????????????????????????????????????????????????
					InventoryKey preLockKey = InventoryKey.of(orgId, info.getWarehouse_inner_sn(), detail.getVendor_inner_sn(),
							detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
							Constants.PRE_LOCK_BATCH_NUMBER, billsType);
					unLockInventory(preLockKey, detail.getQuantity(), detail.getUnit_inner_sn());
					InventoryKey specialLockKey = InventoryKey.of(preLockKey);
					specialLockKey.setBatchNumber(detail.getBatch_number());
					unLockInventory(specialLockKey, detail.getQuantity(), detail.getUnit_inner_sn());
				}else {//????????????????????????????????????????????????????????????
					InventoryKey lockKey = InventoryKey.of(orgId, info.getWarehouse_inner_sn(), detail.getVendor_inner_sn(),
							detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
							detail.getBatch_number(), billsType);
					unLockInventory(lockKey, detail.getQuantity(), detail.getUnit_inner_sn());
				}
			}
		}
		if(!isReturn) {//???????????????????????????????????????????????????
			// TODO ????????????????????????????????????????????????????????????????????????????????????
			Map<VoucherKey, Quantities> toBeUnLockMap = new HashMap<>();// ????????????????????????????????????
			setToBeUnLockVoucherData(orgId, info.getWarehouse_inner_sn(), odo_inner_sn, billsType,
					toBeUnLockMap);
			// ???????????????????????????
			for (Quantities value : toBeUnLockMap.values()) {
				BigDecimal fixQuantity = BigDecimal.ZERO;
				RefVoucherType type = value.getVoucher_type_c();
				switch (type) {
					case GRANT:
						hgService.updateTransferDetail(ServletUtils.getCurUserSystemType(), orgId,
								value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn(), value.getLockedQuantity(), billsType, true);
						break;
					case SALE_RECORD:
						srService.updateTransferDetail(ServletUtils.getCurUserSystemType(), orgId,
								value.getVoucher_inner_sn(), value.getVoucher_details_inner_sn(), value.getLockedQuantity(), billsType, true);
						break;
					default:
				}
				//??????????????????????????????????????????????????????????????????????????????
				if(fixQuantity.compareTo(BigDecimal.ZERO) > 0) {
					InventoryKey preLockKey = InventoryKey.of(orgId, info.getWarehouse_inner_sn(), value.getVendorId(),
							value.getProductId(), value.getSpecificationId(), value.getProductType(),
							Constants.PRE_LOCK_BATCH_NUMBER, billsType);
					inventoryLocker.unlockInventory(preLockKey, fixQuantity);
				}
			}
		}

		// ??????????????????????????????
		mapper.deleteOdoProductDetail(params);
		if(billsType.equals(BillsType.DD) || billsType.equals(BillsType.DH) || billsType.equals(BillsType.HD)
				|| billsType.equals(BillsType.HH)) {
			mapper.deleteOdoDealerProductDetail(params);
		}
	}

	@Override
	@Transactional
	public int deleteOdo(OdoIDForm form) {
		return deleteOdo(form, false);
	}

	@Override
	@Transactional
	public int deleteOdo(OdoIDForm form, boolean isReturn) {
		BillsType billsType = form.getBillsType();
		OdoMapper mapper = getMapper(billsType);
		deleteOldOdoDetails(mapper, billsType, form.getOrg_id(), form.getOdo_inner_sn(), isReturn);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("odo_inner_sn", form.getOdo_inner_sn());
		return mapper.deleteOdo(params);
	}

	@Override
	public List<Odo> searchOdoList(SearchOdoForm form, List<Long> warehouseIds) {
		OdoMapper mapper = null;
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
			case HOSPITAL:
				mapper = getMapper(BillsType.HH);
				break;
			case DEALER:
				mapper = getMapper(BillsType.DD);
				break;
			case VENDOR:
				mapper = getMapper(BillsType.VD);
				break;
			default:
		}
		Map<String, Object> params = form.getInfo();

		params.put("warehouseIds", warehouseIds);
		return mapper.searchOdoList(params);
	}

	@Override
	public Odo selectOdoInfo(OdoIDForm form) {
		OdoMapper mapper = getMapper(form.getBillsType());
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("odo_inner_sn", form.getOdo_inner_sn());
		Odo odo = mapper.selectOdoInfo(params);
		if (odo != null) {
			List<OdoDetail> details = mapper.selectOdoProductDetails(params);
			odo.setDetails(details);
		}
		return odo;
	}

	@Transactional
	Odo putout(Long orgId, Long odoId, Long wid, BillsType billsType, ContextUserInfo curUser, boolean isRed, boolean isReturn) {
		boolean isLease = false;
		AddNoticeForm notice = null;
		List<NoticeDetailForm> noticeDetails = new ArrayList<>();
		if(centralService.isLease(orgId) != null && curUser != null) {
			isLease = true;
		}
		String lockOdoId = String.format("%s-%s-%s", orgId, odoId, billsType.value() );
		if(VoucherLockService.isExist(lockOdoId)){
			throw new ValidateException("??????????????????????????????!");
		}
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("odo_inner_sn", odoId);
		Odo info = getMapper(billsType).selectOdoInfo(params);
		if(isLease) {
			notice = new AddNoticeForm();
			notice.setDealer_inner_sn(orgId);
			notice.setDealer_name(info.getOrg_name());
			notice.setCompany_name(info.getOrganization_name());
			notice.setBills_type(info.getBillsType());
			notice.setWarehouse_name(info.getWarehouse_name());
			notice.setRecord_inner_sn(info.getOdo_inner_sn());
			notice.setSn(info.getOdo_sn());
		}
		List<OdoDetail> details = getMapper(billsType).selectOdoProductDetails(params);
		for (OdoDetail detail : details) {
			if(isLease) {
				NoticeDetailForm noticeDetail = new NoticeDetailForm();
				noticeDetail.setProduct_vendor_inner_sn(detail.getVendor_inner_sn());
				noticeDetail.setProduct_vendor_name(detail.getVendor_name());
				noticeDetail.setProduct_inner_sn(detail.getProduct_inner_sn());
				noticeDetail.setProduct_name(detail.getProduct_name());
				noticeDetail.setSpecification(detail.getSpecification());
				noticeDetail.setSpecification_inner_sn(detail.getSpecification_inner_sn());
				noticeDetail.setUnit_inner_sn(detail.getUnit_inner_sn());
				noticeDetail.setUnit(detail.getUnit_name());
				noticeDetail.setMdrf_inner_sn(detail.getMdrf_inner_sn());
				noticeDetail.setMdrf_sn(detail.getMdrf_sn());
				noticeDetail.setBatch_number(detail.getBatch_number());
				noticeDetail.setProduce_date(detail.getProduce_date());
				noticeDetail.setOverdue_date(detail.getOverdue_date());
				noticeDetail.setQuantity(detail.getQuantity());
				noticeDetail.setRate(detail.getRate());
				noticeDetail.setUnit_price(detail.getUnit_price());
				noticeDetail.setProduct_type(detail.getProduct_type());
				noticeDetails.add(noticeDetail);
			}
			InventoryKey preLockKey = InventoryKey.of(orgId, wid, detail.getVendor_inner_sn(),
					detail.getProduct_inner_sn(), detail.getSpecification_inner_sn(), detail.getProduct_type(),
					Constants.PRE_LOCK_BATCH_NUMBER, billsType);
			InventoryKey specialLockKey = InventoryKey.of(preLockKey);
			specialLockKey.setBatchNumber(detail.getBatch_number());
			//1.????????????
			String minUnitName = commService.getProductMinUnitName(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
					detail.getSpecification_inner_sn(), detail.getUnit_inner_sn(), detail.getProduct_type().intValue());
			Long minUnitId = commService.getProductMinUnitId(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
					detail.getSpecification_inner_sn(), detail.getUnit_inner_sn(), detail.getProduct_type().intValue());

			// ?????????????????????????????????????????????1???=10???????????????????????? measure ????????????10
			BigDecimal measure = commService.getProductUnitMeasure(detail.getVendor_inner_sn(), detail.getProduct_inner_sn(),
					detail.getSpecification_inner_sn(), detail.getUnit_inner_sn(), detail.getProduct_type().intValue());

			// ?????????????????????????????????????????????????????????10??????????????????????????????????????????????????????10*10=100
			BigDecimal quantity = detail.getQuantity().multiply(measure);	// ??????????????????
			BigDecimal minUnitQuantity = quantity.multiply(new BigDecimal(1000));	// ???????????????


			BigDecimal minUnitPrice;
			if(!isRed) {
				// ?????????????????????????????? ????????????????????????/???????????????????????????????????????????????????20??????????????????1???=10?????????20/10=2???????????????????????????
				minUnitPrice = detail.getBuying_price().divide(measure, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(10000));    //??????????????????
			}else{
				// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
				minUnitPrice = detail.getBuying_price().multiply(new BigDecimal(10000));
			}


			InventoryGrnParam param = InventoryGrnParam.of(specialLockKey, detail.getVendor_name(), detail.getProduct_name(),
					detail.getSpecification(), minUnitName, minUnitId, detail.getProduce_date(),
					detail.getOverdue_date(), minUnitQuantity, minUnitPrice);
			try {
				inventoryService.updateInventoryQuantity(param, ServletUtils.getCurUserSystemType(), isRed, isReturn);
			}catch(ValidateException e) {
				VoucherLockService.remove(lockOdoId);
				throw new ValidateException(e.getMessage());
			}
			if(!isRed && !isReturn) {
				//2.????????????????????????????????????
				inventoryLocker.unlockInventory(specialLockKey, quantity, isRed);
				//3.???????????????????????????
				inventoryLocker.unlockInventory(preLockKey, quantity, isRed);
			}else if(detail.getVoucher_type_c().compareTo(RefVoucherType.NONE) != 0){
				try {
					inventoryLocker.lockInventory(preLockKey, InventoryKey.of(preLockKey), quantity.abs());
				} catch (InventoryException ex) {
					StringBuffer message = new StringBuffer();
					if(preLockKey.getBatchNumber().equalsIgnoreCase(Constants.PRE_LOCK_BATCH_NUMBER)) {
						message.append("????????????:[").append(detail.getProduct_name()).append(">>")
								.append(detail.getSpecification()).append(">>").append(detail.getUnit_name()).append("\\n")
								.append("??????????????????: ").append(ex.getRequestQuantity().longValue()).append(" ??????????????????????????????: ")
								.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
								.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
					}else {
						message.append("????????????:[").append(detail.getProduct_name()).append(">>")
								.append(detail.getSpecification()).append(">>").append(detail.getUnit_name()).append("\\n")
								.append("??????: '").append(preLockKey.getBatchNumber()).append("' ")
								.append("???????????????: ").append(ex.getRequestQuantity().longValue()).append(" ???????????????????????????: ")
								.append(ex.getRequestMinUnitQuantity().longValue()).append("\\n").append("????????????(????????????):")
								.append(ex.getCurQuantity().divide(Constants.QUANTITY_INT_SCALE));
					}
					VoucherLockService.remove(lockOdoId);
					throw new ValidateException(message.toString());
				}
			}
		}
		VoucherLockService.remove(lockOdoId);
		//?????????????????????????????????????????????????????????
		if(isLease) {
			notice.setDetails(noticeDetails);
			centralService.insertOdoNotice(notice, curUser);
		}
		return info;
	}

	@Autowired HospitalAllotVoucherMapper haMapper;
	@Autowired DealerAllotVoucherMapper daMapper;
	@Autowired AllotVoucherService avService;
	@Autowired
	ProductUnitMapper puMapper;
	@Autowired
	DealerProductUnitMapper dpuMapper;

	private AllotVoucherMapper getAllotVoucherMapper() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
			case DEALER:
				return daMapper;
			default:
				return haMapper;
		}

	}

	//???????????????id???????????????????????????????????????(????????????????????????)
	private Map<Long,BigDecimal> detailMinUnitQuantityByOdoInnerSn(Long org_id,Long odo_inner_sn){
		HashMap<Long,BigDecimal> res = new HashMap<>();

		List<AllotVoucherDetail> details=getAllotVoucherMapper().findOdoDetails(org_id,odo_inner_sn);

		for (AllotVoucherDetail detail:details){
			Map<String,Object> map=new HashMap<>();
			map.put("vendor_inner_sn_i", detail.getVd_inner_sn());
			map.put("product_inner_sn_i", detail.getProduct_inner_sn());
			map.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
			map.put("unit_inner_sn_i", detail.getUnit_inner_sn());

			BigDecimal bs;
			if (1==detail.getProduct_type()){
				bs= puMapper.calculate_product_min_unit(map);

			}else {
				bs = dpuMapper.calculate_product_min_unit(map);
			}
			res.put(detail.getDetail_inner_sn(),bs.multiply(detail.getOutOfStock_quantity()));
		}
		return res;
	}

	//???????????????id???????????????????????????????????????(????????????????????????)
	private Map<Long,BigDecimal> detailMinUnitQuantityByAvInnerSn(Long org_id,Long av_inner_sn){
		Map<Long,BigDecimal> res=new HashMap<>();

		//???????????????
		SearchAllotVoucherForm avForm = new SearchAllotVoucherForm();
		avForm.setOrg_id(org_id);
		avForm.setTab_id(0);
		avForm.setAv_inner_sn(String.valueOf(av_inner_sn));
		List<AllotVoucherDetail> details = avService.searchAllotVoucherList(avForm, false);
		for (AllotVoucherDetail detail :details){
			Map<String,Object> map=new HashMap<>();
			map.put("vendor_inner_sn_i", detail.getVd_inner_sn());
			map.put("product_inner_sn_i", detail.getProduct_inner_sn());
			map.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
			map.put("unit_inner_sn_i", detail.getUnit_inner_sn());

			BigDecimal bs;
			if (1==detail.getProduct_type()){
				bs= puMapper.calculate_product_min_unit(map);

			}else {
				bs = dpuMapper.calculate_product_min_unit(map);
			}
			res.put(detail.getDetail_inner_sn(),bs.multiply(detail.getAllot_quantity()));
		}
		return res;
	}

	@Override
	public String putout(OdoListIDForm form) {
		return putout(form, null, false);
	}

	@Override
	@Transactional
	public String putout(OdoListIDForm form, ContextUserInfo curUser, boolean isReturn) {
		List<OdoIds> list = form.getIds();
		StringBuffer msg = new StringBuffer("");
		for (OdoIds ids : list) {
			//???????????????????????????????????????????????????
			Odo info = putout(form.getOrg_id(), ids.getOdo_inner_sn(), ids.getWarehouse_inner_sn(), ids.getBillsType(), curUser, false, isReturn);
			Map<String, Object> params = new HashMap<>();
			params.put("org_id", info.getOrg_id());
			params.put("odo_inner_sn", info.getOdo_inner_sn());
			params.put("out_time", new Date());
			params.put("status", 2);//????????????????????????2-?????????


			//???????????????????????????????????????????????????????????????????????????
			AllotVoucherMapper avMapper = getAllotVoucherMapper();
			Long av_inner_sn=avMapper.findOdoIds(params);
			if (Objects.nonNull(av_inner_sn)){
				//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
				SearchAllotVoucherForm avForm = new SearchAllotVoucherForm();
				avForm.setOrg_id(form.getOrg_id());
				avForm.setTab_id(-1);
				avForm.setAv_inner_sn(String.valueOf(av_inner_sn));
				List<AllotVoucherDetail> details = avService.searchAllotVoucherList(avForm, false);

				//????????????????????????????????????AllotVoucherDetail
				List<AllotVoucherDetail> curDetails=getAllotVoucherMapper().findOdoDetails(form.getOrg_id(),ids.getOdo_inner_sn());

				Set<Long> set = details.stream().map(AllotVoucherDetail::getDetail_inner_sn).collect(Collectors.toSet());

				//????????????????????????+??????????????????????????????
				if (details.stream().anyMatch(x->4==x.getStatus().value()) && !curDetails.stream().allMatch(x->set.contains(x.getDetail_inner_sn())))
					throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????!");

				//??????????????????????????????????????????????????????status=2(?????????????????????????????????)
				List<AllotVoucherDetail> odoList = avMapper.findOdo(params);	//????????????

				List<Map<Long, BigDecimal>> xs=new ArrayList<>(odoList.size());

				//??????????????????????????????????????????????????????????????????????????????????????????(??????????????????????????????????????????????????????????????????????????????????????????)
				for (AllotVoucherDetail odo:odoList){
					Map<Long, BigDecimal> putOutedMap = detailMinUnitQuantityByOdoInnerSn(info.getOrg_id(),odo.getAv_inner_sn());//???????????????
					xs.add(putOutedMap);
				}
				Map<Long, BigDecimal> curMap = detailMinUnitQuantityByOdoInnerSn(info.getOrg_id(),ids.getOdo_inner_sn());//???????????????

				//??????????????????Map<Long, BigDecimal>
				Map<Long, BigDecimal> allotMap = detailMinUnitQuantityByAvInnerSn(info.getOrg_id(),av_inner_sn);

				//????????????Map
				for (Long detail_inner_sn:allotMap.keySet()){
					boolean shouldPutOut=true;

					BigDecimal allotQuantity=allotMap.get(detail_inner_sn);

					BigDecimal curQuantity = curMap.get(detail_inner_sn);
					if (Objects.isNull(curQuantity)) curQuantity=BigDecimal.ZERO;

					BigDecimal outQuantity= xs.stream().map(x -> {
						BigDecimal decimal = x.get(detail_inner_sn);
						if (Objects.isNull(decimal)) return BigDecimal.ZERO;
						return decimal;
					}).reduce(BigDecimal.ZERO, BigDecimal::add);

					if (curQuantity.add(outQuantity).compareTo(allotQuantity)<0) shouldPutOut=false;

					if (shouldPutOut){
						getAllotVoucherMapper().updateStatusAtPutOut(detail_inner_sn);
					}
				}
			}



			getMapper(ids.getBillsType()).updateOdo(params);

			//TODO: ??????????????????HIS??????
			msg.append(sync(form.getOrg_id(), ids.getOdo_inner_sn(), ids.getBillsType()));

			if(ids.getBillsType().equals(BillsType.HH)) {
				continue;
			}
			pushService.sendPutoutMessage(info.getOrganization_inner_sn(), getOrganization_type(ids.getBillsType()), info.getOrg_name(), info.getOdo_sn());
		}
		return msg.toString();
	}

	@Override
	@Transactional
	public Odo odoRed(@Valid OdoIDForm form, ContextUserInfo curUser) {
		OrgEmployee makingPeople = curUser.getEmployee();
		OdoMapper mapper = getMapper(form.getBillsType());
		//?????????????????????????????????
		Odo old_info = selectOdoInfo(form);
		if(2 != old_info.getStatus().intValue()) {
			throw new DBException("?????????????????????????????????????????????!");
		}
		List<OdoDetail> details = old_info.getDetails();
		//???????????????????????????????????????????????????
		Map<String, Object> params = new HashMap<>();
		params.put("organization_inner_sn_i", old_info.getOrganization_inner_sn());
		params.put("organization_name_i", old_info.getOrganization_name());
		params.put("warehouse_inner_sn_i", old_info.getWarehouse_inner_sn());
		params.put("warehouse_name_i", old_info.getWarehouse_name());
		params.put("handler_inner_sn_i", old_info.getHandler_inner_sn());
		params.put("handler_name_i", old_info.getHandler_name());
		params.put("making_people_inner_sn_i",makingPeople.getEmployee_inner_sn());
		params.put("making_people_name_i", makingPeople.getName());
		params.put("auditor_inner_sn_i", makingPeople.getEmployee_inner_sn());
		params.put("auditor_name_i", makingPeople.getName());
		params.put("warehouse_operator_inner_sn_i", old_info.getWarehouse_operator_inner_sn());
		params.put("warehouse_operator_name_i", old_info.getWarehouse_operator_name());
		params.put("type_inner_sn_i", old_info.getType_inner_sn());
		params.put("type_name_i", old_info.getType_name());
		params.put("department_inner_sn_i", old_info.getDepartment_inner_sn());
		params.put("department_name_i", old_info.getDepartment_name());
		params.put("status_i", 5);//5-????????????
		params.put("out_time_i", new Date());
		params.put("odo_sn_i", String.format("%s-???", old_info.getOdo_sn()));
		params.put("description_i", String.format("??????%s", old_info.getOdo_sn()));
		params.put("org_id_i", form.getOrg_id());
		params.put("use_department_i", old_info.getUse_department());
		params.put("printed_i", 0);
		mapper.insertOdo(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long odoId = Long.valueOf(params.get("odo_inner_sn_o").toString());
			for (OdoDetail detail : details) {
				Map<String, Object> params1 = new HashMap<>();
				params1.put("odo_inner_sn_i", odoId);
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("buying_price_i", detail.getBuying_price());
				params1.put("unit_price_i", detail.getUnit_price());
				params1.put("quantity_i", detail.getQuantity().multiply(new BigDecimal(-1)));
				params1.put("produce_date_i", detail.getProduce_date());
				params1.put("overdue_date_i", detail.getOverdue_date());
				params1.put("type_i", detail.getType());
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("batch_number_i", detail.getBatch_number());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				params1.put("voucher_type_c_i", detail.getVoucher_type_c().value());
				params1.put("voucher_inner_sn_c_i", detail.getVoucher_inner_sn_c());
				params1.put("voucher_detail_inner_sn_c_i", detail.getVoucher_detail_inner_sn_c());
				params1.put("health_care_sn_i", StringUtils.isEmpty(detail.getHealth_care_sn())?"":detail.getHealth_care_sn());
				params1.put("org_id_i", form.getOrg_id());
				params1.put("rate_i", StringUtils.isNull(detail.getRate())?BigDecimal.ZERO:detail.getRate());
				if (detail.getProduct_type() == 1) {// ????????????
					params1.put("product_vendor_inner_sn_i", detail.getVendor_inner_sn());
					params1.put("product_vendor_name_i", detail.getVendor_name());
					params1.put("mdrf_inner_sn_i", detail.getMdrf_inner_sn());
					params1.put("mdrf_sn_i", detail.getMdrf_sn());
					mapper.insertOdoProductDetail(params1);

				} else {
					params1.put("product_dealer_inner_sn_i", detail.getVendor_inner_sn());
					params1.put("product_dealer_name_i", detail.getVendor_name());
					mapper.insertOdoDealerProductDetail(params1);

				}
				if (Integer.valueOf(params.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail", "???????????????", params.get("message_string_o")));
				}
				//????????????????????????
				RefVoucherType type = detail.getVoucher_type_c();
				switch (type) {
					case GRANT:
						hgService.rollbackVoucherStatus(form.getOrg_id(), detail.getVoucher_inner_sn_c(),
								detail.getVoucher_detail_inner_sn_c(), detail.getProduct_type(), 0);
						break;
					case SALE_RECORD:
						srService.rollbackVoucherStatus(form.getOrg_id(), detail.getVoucher_inner_sn_c(),
								detail.getVoucher_detail_inner_sn_c(), form.getBillsType(), detail.getProduct_type(), 0);
						break;
					default:
				}
			}
			//??????????????????
			Odo redInfo = putout(form.getOrg_id(), odoId, old_info.getWarehouse_inner_sn(), old_info.getBillsType(), curUser, true, false);
			//???????????????????????????5-??????
			Map<String, Object> params1 = new HashMap<>();
			params1.put("org_id", redInfo.getOrg_id());
			params1.put("odo_inner_sn", redInfo.getOdo_inner_sn());
			params1.put("status", 5);
			mapper.updateOdo(params1);
			//???????????????????????????4-??????
			params1.put("org_id", old_info.getOrg_id());
			params1.put("odo_inner_sn", old_info.getOdo_inner_sn());
			params1.put("status", 4);
			mapper.updateOdo(params1);
			OdoIDForm f1 = new OdoIDForm();
			f1.setOrg_id(form.getOrg_id());
			f1.setOdo_inner_sn(odoId);
			f1.setBillsType(form.getBillsType());
			return selectOdoInfo(f1);
		}else {
			throw new DBException(MessageUtils.message("db.add.fail", "?????????", params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public void unReview(@Valid OdoIDForm form) {
		OdoMapper mapper = getMapper(form.getBillsType());
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("odo_inner_sn", form.getOdo_inner_sn());
		params.put("auditor_inner_sn", null);
		params.put("auditor_name", "");
		Odo old_info = mapper.selectOdoInfo(params);
		if(old_info == null || 1 != old_info.getStatus().intValue()){
			throw new DBException("????????????????????????????????????????????????!");
		}
		params.put("status", 0);
		mapper.updateOdo(params);
	}

	private String sync(Long org_id, Long odo_inner_sn, BillsType billsType) {
		OdoMapper mapper = getMapper(billsType);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", org_id);
		params.put("odo_inner_sn", odo_inner_sn);
		Odo4External odo = mapper.selectOdoInfo4External(params);
		if (odo != null) {
			List<OdoDetail4External> details = mapper.selectOdoProductDetails4External(params);
			odo.setDetails(details);
		}
		return syncService.sendPutout(org_id, ServletUtils.getCurUserSystemType(), odo);
	}

	@Override
	public List<Map<String, Object>> getWarehouseEmployeeList(Long oid, Long wid) {
		OdoMapper mapper = null;
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
			case HOSPITAL:
				mapper = getMapper(BillsType.HH);
				break;
			case DEALER:
				mapper = getMapper(BillsType.DD);
				break;
			case VENDOR:
				mapper = getMapper(BillsType.VD);
				break;
			default:
		}
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("warehouse_model_inner_sn", wid);
		WareHouseMapper whMapper = getWarehouseMapper();
		String wIds = whMapper.selectAllChildrenWarehouseModeIDS(params);
		params.put("warehouse_model_inner_sn", wIds);
		return mapper.selectWarehouseEmployeeList(params);
	}

	@Override
	public List<Map<String, Object>> selectRelatedOrg4DropDownList(SystemType systemType, Long oid, String name) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", oid);
		params.put("name", name);
		SystemType systemType1 = ServletUtils.getCurUserSystemType();
		switch (systemType1) {
			case HOSPITAL:// ??????
				switch (systemType) {
					case HOSPITAL:// HospitalHospital
						return getMapper(BillsType.HH).selectRelatedOrg4DropDownList(params);
					case DEALER:// HospitalDealer
						return getMapper(BillsType.HD).selectRelatedOrg4DropDownList(params);
					case VENDOR:// HospitalVendor
						return getMapper(BillsType.HV).selectRelatedOrg4DropDownList(params);
					default:
				}
				break;
			case DEALER:// ?????????
				switch (systemType) {
					case HOSPITAL:// DealerHospital
						return getMapper(BillsType.DH).selectRelatedOrg4DropDownList(params);
					case DEALER:// DealerDealer
						return getMapper(BillsType.DD).selectRelatedOrg4DropDownList(params);
					case VENDOR:// DealerVendor
						return getMapper(BillsType.DV).selectRelatedOrg4DropDownList(params);
					default:
				}
				break;
			case VENDOR:// ??????
				switch (systemType) {
					case HOSPITAL:// VendorHospital
						return getMapper(BillsType.VH).selectRelatedOrg4DropDownList(params);
					case DEALER:// VendorDealer
						return getMapper(BillsType.VD).selectRelatedOrg4DropDownList(params);
					case VENDOR:// VendorVendor
						return new ArrayList<Map<String, Object>>();
					default:
				}
				break;
			default:
		}

		return new ArrayList<Map<String, Object>>();
	}

	@Override
	public List<ResponseProductInfo> selectVendorProductList(SearchOdoProductForm form, List<Long> warehouseIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("level",form.getLevel());
		params.put("warehouseIds", warehouseIds);
		params.put("org_id", form.getOrg_id());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("name", form.getName());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("specification", form.getSpecification());
		params.put("specification_vendor_sn", form.getSpecification_vendor_sn());
		SystemType systemType = ServletUtils.getCurUserSystemType();

		switch (systemType) {
			case HOSPITAL:
//			if (billsType.equals(BillsType.HV))
//				params.put("vendor_inner_sn", form.getOrganization_inner_sn());
				return getMapper(BillsType.HH).selectVendorProductList(params);
			case DEALER:
//			if (billsType.equals(BillsType.DV))
//				params.put("vendor_inner_sn", form.getOrganization_inner_sn());
				return getMapper(BillsType.DD).selectVendorProductList(params);
			case VENDOR:
//			params.put("vendor_inner_sn", form.getOrg_id());
				return getMapper(BillsType.VD).selectVendorProductList(params);
			default:
		}
		return new ArrayList<ResponseProductInfo>();
	}

	@Override
	public List<Map<String, Object>> selectDealerProductList(SearchOdoProductForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("name", form.getName());
		params.put("code68_sn", form.getCode68_sn());
		// TODO: ??????????????????????????????????????????????????????
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
			case HOSPITAL:
				return getMapper(BillsType.HH).selectDealerProductList(params);
			case DEALER:
				if (form.getBillsType().equals(BillsType.DD)) {
					StringBuffer bf = new StringBuffer();
					bf.append(form.getOrg_id());
					bf.append(",").append(form.getOrganization_inner_sn());
					params.put("vendor_inner_sn", bf.toString());
				}else {
					params.put("vendor_inner_sn", form.getOrg_id());
				}
				return getMapper(BillsType.DD).selectDealerProductList(params);
			case VENDOR:
				return new ArrayList<Map<String, Object>>();
			default:
		}
		return new ArrayList<Map<String, Object>>();
	}

	private OdoMapper getMapperBySystemType() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
			case HOSPITAL:
				return hhMapper;
			case DEALER:
				return ddMapper;
			case VENDOR:
				return vdMapper;
			default:
		}
		throw new DBException("??????????????????????????????!");
	}

	@Override
	public List<Map<String, Object>> selectVendorProductList4Phone(Map<String, Object> params) {
		OdoMapper mapper = getMapperBySystemType();
		return mapper.selectVendorProductList4Phone(params);
	}

	@Override
	public List<Map<String, Object>> selectDealerProductList4Phone(Map<String, Object> params) {
		OdoMapper mapper = getMapperBySystemType();
		if (mapper instanceof VendorDealerOdoMapper) {
			throw new DBException("??????????????????????????????!");
		}
		return mapper.selectDealerProductList4Phone(params);
	}

	@Override
	public List<BatchNumberDetail> selectProductBatchNumList(SearchBatchNumForm form, InventoryKey key) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("product_vendor_inner_sn", form.getProduct_vendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		params.put("isReturn", true);//TODO:????????????????????????????????????????????????,???????????????????????? form.getIsReturn());
		List<BatchNumberDetail> result = new ArrayList<>();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
			case HOSPITAL:
				if (form.getProduct_type() == 1) {
					result = getMapper(BillsType.HH).selectProductBatchNumList(params);
				} else {
					result = getMapper(BillsType.HH).selectDealerProductBatchNumList(params);
				}
				break;
			case DEALER:
				if (form.getProduct_type() == 1) {
					result = getMapper(BillsType.DD).selectProductBatchNumList(params);
				} else {
					result = getMapper(BillsType.DD).selectDealerProductBatchNumList(params);
				}
				break;
			case VENDOR:
				result = getMapper(BillsType.VD).selectProductBatchNumList(params);
				break;
			default:
		}
		Map<String, BigDecimal> map = form.getBatchNumberList();
		for (BatchNumberDetail detail : result) {
			String batchNumber = detail.getBatch_number();
			key.setBatchNumber(batchNumber);
			if(map.containsKey(batchNumber)) {
				detail.setQuantity(inventoryLocker.getInventoryBatchAndWarehouseBannce(key, map.get(batchNumber)));
			}else {
				detail.setQuantity(inventoryLocker.getInventoryBatchAndWarehouseBannce(key, null));
			}
		}
		return result;
	}

	@Override
	public List<TransferSaleRecord> getTransferSaleRecordList(SystemType systemType, long orgId, String key,String key1,String organizationName) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("sl_sn", StringUtils.isNotEmpty(key)?key:null);
		params.put("product_name",StringUtils.isNotEmpty(key1)?key1:null);
		params.put("organization_name", StringUtils.isNotEmpty(organizationName)?organizationName:null);
		switch (systemType) {
			case DEALER:
				return getMapper(BillsType.DD).selectTransferSaleRecordList(params);
			case VENDOR:
				return getMapper(BillsType.VD).selectTransferSaleRecordList(params);
			default:
				throw new DBException("??????????????????????????????!");
		}
	}

	@Override
	public List<SaleRecordDetail> getTransferSaleRecordDetailList(SystemType systemType, long orgId, Long sid, Long detailid,
																  BillsType billsType) {
		if (systemType.equals(SystemType.HOSPITAL)) {
			throw new DBException("??????????????????????????????!");
		}
		OdoMapper mapper = getMapper(billsType);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("sl_inner_sn", sid);
		params.put("detail_inner_sn", detailid);
		return mapper.selectTransferSaleRecordDetailList(params);
	}

	@Override
	public List<SaleRecordDetail> getTransferGroupSaleRecordDetailList(SystemType systemType, long orgId, Long sid, InventoryKey queryKey, BillsType billsType){
		if (systemType.equals(SystemType.HOSPITAL)) {
			throw new DBException("??????????????????????????????!");
		}
		OdoMapper mapper = getMapper(billsType);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("sl_inner_sn", sid);
		params.put("vendor_inner_sn", queryKey.getVendorId());
		params.put("product_inner_sn", queryKey.getProductId());
		params.put("specification_inner_sn", queryKey.getSpecificationId());
		return mapper.selectTransferGroupSaleRecordDetailList(params);
	}

	@Override
	public List<HospitalGrantVoucher> getTransferGrantList(SystemType systemType, long orgId, String key,String key1) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("gv_sn", key);
		params.put("product_name",StringUtils.isNotEmpty(key1)?key1:null);
		if (systemType.equals(SystemType.HOSPITAL)) {
			OdoMapper mapper = hhMapper;
			return mapper.selectTransferGrantList(params);
		}
		throw new DBException("??????????????????????????????!");
	}

	@Override
	public List<HospitalGrantVoucherDetail> getTransferGrantDetailList(SystemType systemType, long orgId, Long gvid, Long detailid) {
		if (!systemType.equals(SystemType.HOSPITAL)) {
			throw new DBException("??????????????????????????????!");
		}
		OdoMapper mapper = hhMapper;
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("gv_inner_sn", gvid);
		params.put("detail_inner_sn", detailid);
		return mapper.selectTransferGrantDetailList(params);
	}

	@Override
	public List<HospitalGrantVoucherDetail> getTransferGroupGrantDetailList(SystemType systemType, long orgId, Long gvid, InventoryKey queryKey){
		if (!systemType.equals(SystemType.HOSPITAL)) {
			throw new DBException("??????????????????????????????!");
		}
		OdoMapper mapper = hhMapper;
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("gv_inner_sn", gvid);
		params.put("vendor_inner_sn", queryKey.getVendorId());
		params.put("product_inner_sn", queryKey.getProductId());
		params.put("specification_inner_sn", queryKey.getSpecificationId());
		return mapper.selectTransferGroupGrantDetailList(params);
	}

	public BigDecimal calculateQuantityByProductMinUnit(Long vid, Long pid, Long psid, Long uid, int product_type, BigDecimal quantity) {
		Map<String, Object> params = new HashMap<>();
		params.put("product_vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("specification_inner_sn", psid);
		params.put("unit_inner_sn", uid);
		params.put("quantity", quantity);
		if(product_type == 0) {
			return ddMapper.calculateQuantityByDealerProductMinUnit(params);
		}else {
			return ddMapper.calculateQuantityByProductMinUnit(params);
		}
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????true?????????????????????false
	 * @param oid
	 * @param odoId
	 * @param type
	 * @return
	 */
	private void checkOverVoucherDetail(Long oid, Long odoId, BillsType type) {
		OdoMapper mapper = getMapper(type);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("odo_inner_sn", odoId);
		Odo odo = mapper.selectOdoInfo(params);
		if(RefVoucherType.NONE.equals(odo.getVoucher_type_c())) {
			return;
		}
		List<VoucherDetailQuantity> list = mapper.getVoucherDetailQuantity(oid, odo.getVoucher_type_c(), odo.getVoucher_inner_sn_c(), null);
		Collections.sort(list);
		for (VoucherDetailQuantity detail : list) {
			if(detail.getFixQuantity().compareTo(BigDecimal.ZERO) < 0) {
				String message = "????????????:[" + detail.getProduct_name() + ">>" + detail.getSpecification() + "\\n"
						+ odo.getVoucher_type_c().text() + "?????????????????????" + detail.getSrcQuantity().divide(Constants.QUANTITY_INT_SCALE) + "\\n"
						+ "?????????????????????????????????: " + detail.getTotalQuantity().divide(Constants.QUANTITY_INT_SCALE) + "\\n"
						+ " ??????: " + detail.getFixQuantity().divide(Constants.QUANTITY_INT_SCALE).abs() + "\\n"
						;
				throw new ValidateException(message);
			}
		}
		return;
	}

	@Override
	public void printed(OdoIDForm form) {
		OdoMapper mapper = getMapper(form.getBillsType());
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("odo_inner_sn", form.getOdo_inner_sn());
		Odo odo = mapper.selectOdoInfo(params);
		params.put("printed", odo.getPrinted().intValue() + 1);

		mapper.updateOdo(params);
	}

	@Override
	public List<Map<String, Object>> getOdoProductList4Return(SearchReturnProductForm form, List<Long> warehouseIds) {
		OdoMapper mapper = getMapper(form.getBillsType());
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("product_vendor_name", form.getProduct_vendor_name());
		params.put("product_name", form.getName());
		params.put("specification", form.getSpecification());
		params.put("sn", form.getSn());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		params.put("warehouseIds", warehouseIds);
		return mapper.selectOdoProductList4Return(params);
	}

	@Autowired ProductSnService snService;

	@Override
	public List<ProductSn> getProductHealthCareSnList(Long oid, ProductHealthCareSnForm form, ProductSnType healthCareSn) {
		List<ProductSn> res = snService.selectProductSnList(oid, form.getVendor_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(), healthCareSn);
		if(StringUtils.isEmpty(res)) return new ArrayList<>();

		OdoMapper mapper = getMapper(form.getBillsType());
		//1???????????????????????????????????????????????????????????????
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		String sn = mapper.selectLastProductHealthCareSn(params);
		ProductSn productSn = snService.selectProductSn(oid, form.getVendor_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(), sn, healthCareSn);
		if(StringUtils.isNotNull(productSn)) {
			res.add(0, productSn);
		}

		return StringUtils.removeDuplicateWithOrder(res);
	}

	@Override
	public List<Map<String, Object>> getOdoProductList4Invoice(SearchInvoiceProductForm form, List<Long> warehouseIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("product_vendor_name", form.getProduct_vendor_name());
		params.put("product_name", form.getName());
		params.put("specification", form.getSpecification());
		params.put("sn", form.getSn());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		params.put("warehouseIds", warehouseIds);
		List<Map<String, Object>> odoProductList;
		if (form.getBillsType()!=null){
			OdoMapper mapper = getMapper(form.getBillsType());
			odoProductList = mapper.selectOdoProductList4Invoice(params);
		}else{
			// ??????????????????ID???????????????????????????union all?????????????????????
			odoProductList = ddMapper.selectOdoProductList4InvoiceNew(params);
		}
		return odoProductList;
	}
}
