package com.yrt.project.api.business.warehouse.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.yrt.common.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.yrt.common.dict.BaseIntEnum;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.warehouse.service.ISasService;
import com.yrt.project.api.business.warehouse.service.InventoryLockService;
import com.yrt.project.api.business.warehouse.vo.sas.AddSasForm;
import com.yrt.project.api.business.warehouse.vo.sas.CheckBarcodeForm;
import com.yrt.project.api.business.warehouse.vo.sas.CheckSasForm;
import com.yrt.project.api.business.warehouse.vo.sas.EditSasDetailForm;
import com.yrt.project.api.business.warehouse.vo.sas.EditSasForm;
import com.yrt.project.api.business.warehouse.vo.sas.SasIDForm;
import com.yrt.project.api.business.warehouse.vo.sas.SasProductForm;
import com.yrt.project.api.business.warehouse.vo.sas.ScanBarcodeForm;
import com.yrt.project.api.business.warehouse.vo.sas.SearchSasForm;
import com.yrt.project.api.business.warehouse.vo.sas.StockProductForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.warehouse.domain.Sas;
import com.yrt.project.modular.warehouse.domain.SasDetail;
import com.yrt.project.modular.warehouse.domain.VoucherDetailQuantity;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import com.yrt.project.modular.warehouse.mapper.DealerSasMapper;
import com.yrt.project.modular.warehouse.mapper.DealerWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalSasMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.SasMapper;
import com.yrt.project.modular.warehouse.mapper.VendorSasMapper;
import com.yrt.project.modular.warehouse.mapper.VendorWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.WareHouseMapper;

@Service
public class SasServiceImpl implements ISasService {

	@Autowired DealerSasMapper dsMapper;
	@Autowired HospitalSasMapper hsMapper;
	@Autowired VendorSasMapper vsMapper;
	
	@Autowired DealerMapper dMapper;
	@Autowired HospitalMapper hMapper;
	@Autowired VendorMapper vMapper;
	
	@Autowired DealerWarehouseMapper dwMapper;
	@Autowired HospitalWarehouseMapper hwMapper;
	@Autowired VendorWarehouseMapper vwMapper;
	
	@Autowired private VoucherNumberService numberUtils;
	
	private WareHouseMapper getWarehouseMapper() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case HOSPITAL:
			return hwMapper;
		case DEALER:
			return dwMapper;
		case VENDOR:
			return vwMapper;
		default:
		}
		throw new DBException("当前机构不支持该功能!");
	}
	
	private SasMapper getMapper() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case HOSPITAL:
			return hsMapper;
		case DEALER:
			return dsMapper;
		case VENDOR:
			return vsMapper;
		default:
		}
		throw new DBException("当前机构不支持该功能!");
	}
	
	private int convertGrnTypeToOrganizationType(BillsType grn_type) {
		int organization_type = 0;//0-DealerDealer 1-DealerVendor 2-DealerHospital 3-HospitalHospital 4-HospitalDealer 5-HospitalVendor
		switch (grn_type) {
		case DD:
			organization_type = 2;
			break;
		case DV:
			organization_type = 3;
			break;
		case DH:
			organization_type = 1;
			break;
		case HH:
			organization_type = 1;
			break;
		case HD:
			organization_type = 2;
			break;
		case HV:
			organization_type = 3;
			break;
		case VD:
			organization_type = 2;
			break;
		case VH:
			organization_type = 1;
			break;
		case VV:
			organization_type = 3;
			break;
		}
		return organization_type;
	}
	
	private Long getCurUserEmployeeId() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		OrgEmployee curEmp = new OrgEmployee();
		switch (systemType){
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
		if(curEmp != null) {
			id = curEmp.getEmployee_inner_sn();
		}
		return id;
	}
	
	private String getCurUserEmployeeName() {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		OrgEmployee curEmp = new OrgEmployee();
		switch (systemType){
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
		if(curEmp != null) {
			curEmpName = curEmp.getName();
		}
		return curEmpName;
	}
	
	@Override
	public List<Sas> searchSasList(SearchSasForm form) {
		SasMapper sasMapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("sas_sn", form.getSas_sn());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("operator_name", form.getOperator_name());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		if(form.getIsOnlyEffective()) {
			params.put("status", "0,1");
		}

		params.put("status",form.getStatus());

		if(form.getOnlySelf() == true) {
			params.put("operator_inner_sn", getCurUserEmployeeId());
		}
		return sasMapper.searchSasList(params);
	}

	@Override
	public Sas getSasInfo(SasIDForm form) {
		SasMapper sasMapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("sas_inner_sn", form.getSas_inner_sn());
		Sas info = sasMapper.selectSasInfo(params);
		if(info != null) {
			info.setDetails(sasMapper.selectSasDetail(params));
		}
		return info;
	}

	@Override
	@Transactional
	public Sas addSas(@Valid AddSasForm form) {
		SasMapper sasMapper = getMapper();
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn_i", form.getWarehouse_inner_sn());
		params.put("making_people_inner_sn_i", getCurUserEmployeeId());
		params.put("operator_inner_sn_i", form.getOperator_inner_sn());
		params.put("sas_sn_i", numberUtils.getMaxSn(VoucherType.STORAGE_LOC, ServletUtils.getCurUserSystemType(), form.getOrg_id()));//.getStorageMaxSn(ServletUtils.getCurUserSystemType(), form.getOrg_id()));
		params.put("description_i", form.getDescription());
		params.put("warehouse_name_i", form.getWarehouse_name());
		params.put("making_people_name_i", getCurUserEmployeeName());
		params.put("operator_name_i", form.getOperator_name());
		params.put("data_i", JSONObject.toJSONString(form.getDetails()));
		
		sasMapper.insertSas(params);
		if(Integer.valueOf(params.get("result_o").toString()) != 0) {
			if("1062".equalsIgnoreCase(params.get("error_code_o").toString())) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","储位分配单",params.get("message_string_o")));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","储位分配单",params.get("message_string_o")));
			}
		}else {
			Long sid = Long.valueOf(params.get("sas_inner_sn_o").toString());
			Map<String, Object> params1 = new HashMap<>();
			params1.put("org_id", form.getOrg_id());
			params1.put("sas_inner_sn", sid);
			checkOverGrnDetail(form.getOrg_id(), form.getWarehouse_inner_sn(), sid);
			return sasMapper.selectSasInfo(params1);
		}
	}
	
	@Override
	@Transactional
	public int editSas(@Valid EditSasForm form) {
		SasMapper sasMapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("sas_inner_sn", form.getSas_inner_sn());
		params.put("description", StringUtils.isNotEmpty(form.getDescription())?form.getDescription():"");
		List<EditSasDetailForm> details = form.getDetails();
		for (EditSasDetailForm detailForm : details) {
			int action = detailForm.getAction();
			switch (action) {
			case 0://0-删除
				deleteSasDetail(sasMapper, detailForm, form.getOrg_id(), form.getWarehouse_inner_sn(), form.getSas_inner_sn());
				break;
			case 1:// 1-新增
				insertSasDetail(sasMapper, detailForm, form.getOrg_id(), form.getWarehouse_inner_sn(), form.getSas_inner_sn());
				break;
			case 2:// 2-修改
				updateSasDetail(sasMapper, detailForm, form.getOrg_id(), form.getWarehouse_inner_sn(), form.getSas_inner_sn());
				break;
			}
		}
		checkOverGrnDetail(form.getOrg_id(), form.getWarehouse_inner_sn(), form.getSas_inner_sn());
		return sasMapper.updateSas(params);
	}
	
	private void checkOverGrnDetail(Long org_id, Long wid, Long sasId) {
		SasMapper sasMapper = getMapper();
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", org_id);
		params.put("warehouse_inner_sn", wid);
		params.put("sas_inner_sn", sasId);
		List<VoucherDetailQuantity> list = sasMapper.selectVoucherDetailQuantity(params);
		Collections.sort(list);
		for (VoucherDetailQuantity detail : list) {
			if(detail.getFixQuantity().compareTo(BigDecimal.ZERO) < 0) {
				String message = "保存出错:[" + detail.getProduct_name() + ">>" + detail.getSpecification() + "\\n"
						+ "入库数量：" + detail.getSrcQuantity().divide(Constants.QUANTITY_INT_SCALE) + "\\n"
						+ "所有储位分配单数量: " + detail.getTotalQuantity().divide(Constants.QUANTITY_INT_SCALE) + "\\n"
						+ " 超出: " + detail.getFixQuantity().divide(Constants.QUANTITY_INT_SCALE).abs() + "\\n"
						;
				throw new RuntimeException(message);
			}
		}
		return;
	}
	
	@Override
	@Transactional
	public int stopSas(SasIDForm form) {
		SasMapper sasMapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("sas_inner_sn", form.getSas_inner_sn());
		params.put("status", 2);//人工干预，直接结束储位分配单
		return sasMapper.updateSas(params);
	}

	@Transactional
	private void insertSasDetail(SasMapper mapper, EditSasDetailForm form, Long org_id,
			Long warehouse_inner_sn, Long sas_inner_sn) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", org_id);
		params.put("warehouse_inner_sn_i", warehouse_inner_sn);
		params.put("sas_inner_sn_i", sas_inner_sn);
		params.put("inventory_inner_sn_i", form.getInventory_inner_sn());
		params.put("product_vendor_inner_sn_i", form.getProduct_vendor_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("specification_inner_sn_i", form.getSpecification_inner_sn());
		params.put("unit_inner_sn_i", form.getUnit_inner_sn());
		params.put("location_inner_sn_i", form.getLocation_inner_sn());
		params.put("expect_quantity_i", form.getQuantity());
		params.put("sort_number_i", StringUtils.isNotNull(form.getSort_number())?form.getSort_number():0);
		params.put("batch_number_i", StringUtils.isNotEmpty(form.getBatch_number())?form.getBatch_number():"");
		params.put("description_i", "");
		params.put("product_vendor_name_i", form.getProduct_vendor_name());
		params.put("product_name_i", form.getProduct_name());
		params.put("specification_i", form.getSpecification());
		params.put("unit_i", form.getUnit_name());
		params.put("location_i", form.getLocation());
		if(form.getProduct_type() == 1) {
			mapper.insertSasVendorDetail(params);
		}else {
			mapper.insertSasDealerDetail(params);
		}
		if(Integer.valueOf(params.get("result_o").toString()) != 0) {
			throw new DBException(MessageUtils.message("db.add.fail","储位分配单明细",params.get("message_string_o")));
		}
	}
	
	@Transactional
	private void updateSasDetail(SasMapper mapper, EditSasDetailForm form, Long org_id,
			Long warehouse_inner_sn, Long sas_inner_sn) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", org_id);
		params.put("warehouse_inner_sn", warehouse_inner_sn);
		params.put("sas_inner_sn", sas_inner_sn);
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		params.put("location_inner_sn", form.getLocation_inner_sn());
		params.put("location", form.getLocation());
		params.put("expect_quantity", form.getQuantity().multiply(new BigDecimal(1000)));
		if(form.getProduct_type() == 1) {
			mapper.updateSasVendorDetail(params);
		}else {
			mapper.updateSasDealerDetail(params);
		}
	}
	
	@Transactional
	private void deleteSasDetail(SasMapper mapper, EditSasDetailForm form, Long org_id,
			Long warehouse_inner_sn, Long sas_inner_sn) {
		Long detailId = form.getDetail_inner_sn();
		if(detailId == null) {
			return;
		}
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", org_id);
		params.put("warehouse_inner_sn", warehouse_inner_sn);
		params.put("sas_inner_sn", sas_inner_sn);
		params.put("detail_inner_sn", detailId);
		if(form.getProduct_type() == 1) {
			mapper.deleteSasVendorDetail(params);
		}else {
			mapper.deleteSasDealerDetail(params);
		}
	}

	@Override
	@Transactional
	public int deleteSas(SasIDForm form) {
		SasMapper sasMapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("sas_inner_sn", form.getSas_inner_sn());
		sasMapper.deleteSasVendorDetail(params);
		if(!ServletUtils.getCurUserSystemType().equals(SystemType.VENDOR)) {
			sasMapper.deleteSasDealerDetail(params);
		}
		
		return sasMapper.deleteSas(params);
	}

	@Override
	public Sas checkSas(CheckSasForm form) {
		SasMapper sasMapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("organization_type", convertGrnTypeToOrganizationType(form.getBillsType()));
		params.put("grn_inner_sn", form.getGrn_inner_sn());
		List<Map<String, Object>> tmp = sasMapper.selectSasId(params);
		if(StringUtils.isNotEmpty(tmp)) {
			params.put("sas_inner_sn", tmp.get(0).get("sas_inner_sn"));
			Sas info = sasMapper.selectSasInfo(params);
			if(info != null) {
				info.setDetails(sasMapper.selectSasDetail(params));
			}
			return info;
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getNoHaveSasProductList(SasProductForm form) {
		SasMapper sasMapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("organization_type", convertGrnTypeToOrganizationType(form.getBillsType()));
		params.put("grn_inner_sn", form.getGrn_inner_sn());
		params.put("grn_detail_inner_sn", form.getGrn_detail_inner_sn());
		params.put("product_vendor_inner_sn", form.getProduct_vendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		params.put("check", true);
		return sasMapper.selectNoHaveSasProduct(params);
	}

	@Override
	public Map<String, Object> getProductListByWarehouseBarcode(ScanBarcodeForm form) {
		Map<String,Object> result = new HashMap<>();
		WareHouseMapper wMapper = getWarehouseMapper();
		
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("barcode", form.getBarcode());
		WarehouseModel model = wMapper.selectWarehouseModeInfoByBarcode(params);
		if(model == null) {
			throw new DBException("错误的储位条码!");
		}
		result.put("warehose_name", model.getName() + "(" + model.getWarehouse_model_sn()+")");
		SasMapper sasMapper = getMapper();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", getWarehoseTopID(model, wMapper).getWarehouse_model_inner_sn());
		params.put("location_inner_sn", model.getWarehouse_model_inner_sn());
		if(form.getOnlySelf() == true) {
			params.put("operator_inner_sn", getCurUserEmployeeId());
		}
		List<Map<String, Object>> list = sasMapper.selectPendingProductList(params);
		Map<String, List<Map<String, Object>>> group = new HashMap<>();
		for (Map<String, Object> map : list) {
			String key = map.get("sas_sn").toString();
			if(group.keySet().contains(key)) {
				List<Map<String, Object>> value = group.get(key);
				value.add(map);
			}else {
				List<Map<String, Object>> value = new ArrayList<>();
				value.add(map);
				group.put(key, value);
			}
		}
		result.put("list", group);
		return result;
	}

	private WarehouseModel getWarehoseTopID(WarehouseModel model, WareHouseMapper wMapper) {
		if(model.getParent_warehouse_model_inner_sn() != null) {
			WarehouseModel params = new WarehouseModel();
			params.setOrg_id(model.getOrg_id());
			params.setWarehouse_model_inner_sn(model.getParent_warehouse_model_inner_sn());
			WarehouseModel parent = wMapper.selectWarehouseModelInfo(params);
			return getWarehoseTopID(parent, wMapper);
		}else {
			return model;
		}
	}

	@Override
	public Map<String, Object> getStorageListByProductBarcode(ScanBarcodeForm form) {
		Map<String,Object> result = new HashMap<>();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		SasMapper sasMapper = getMapper();

		String barcode = form.getBarcode();
		if(barcode.length()==31) {
			try {
				SystemType orgType = BaseIntEnum.valueOf(SystemType.class, Integer.parseInt(barcode.substring(0, 2)));
				long orgId = Long.parseLong(barcode.substring(2,12));
				String code = barcode.substring(12);
				if(!systemType.equals(orgType) || orgId != form.getOrg_id().longValue()) {
					throw new DBException("其他机构的条码!");
				}else {
					Map<String,Object> params = new HashMap<>();
					params.put("org_id", orgId);
					params.put("barcode_sn", code);
					List<Map<String,Object>> codes = sasMapper.selectPendingStorageList(params);
					if(StringUtils.isNotEmpty(codes)) {
						result.put("info", codes.get(0));
						List<Map<String, List<Map<String, Object>>>> groupList = new ArrayList<>();
						for (Map<String, Object> productInfo : codes) {
							WareHouseMapper wMapper = getWarehouseMapper();
							WarehouseModel tmp = new WarehouseModel();
							tmp.setOrg_id(form.getOrg_id());
							tmp.setWarehouse_model_inner_sn(Long.valueOf(productInfo.get("warehouse_inner_sn").toString()));
							WarehouseModel model = wMapper.selectWarehouseModelInfo(tmp);
							params.put("org_id", form.getOrg_id());
							params.put("warehouse_inner_sn", model.getWarehouse_model_inner_sn());
							params.put("inventory_inner_sn", Long.valueOf(productInfo.get("inventory_inner_sn").toString()));
							if(form.getOnlySelf() == true) {
								params.put("operator_inner_sn", getCurUserEmployeeId());
							}
							List<Map<String, Object>> productList = sasMapper.selectPendingProductList(params);
							Map<String, List<Map<String, Object>>> group = new HashMap<>();
							for (Map<String, Object> map : productList) {
								String key = map.get("sas_sn").toString();
								String keyName = key +"("+ model.getName()+")";
								if(group.keySet().contains(keyName)) {
									List<Map<String, Object>> value = group.get(keyName);
									value.add(map);
								}else {
									List<Map<String, Object>> value = new ArrayList<>();
									value.add(map);
									group.put(keyName, value);
								}
							}
							if(StringUtils.isEmpty(group))
								continue;
							groupList.add(group);
						}
						result.put("list", groupList);
					}else {
						throw new DBException("不能识别的条码!");
					}
				}
			} catch (NumberFormatException e) {
				throw new DBException("不能识别的条码!");
			}
		}else {
			throw new DBException("不能识别的条码!");
		}
		return result;
	}

	@Override
	public Map<String, Object> getCheckStorageByProductBarcode(CheckBarcodeForm form) {
		Map<String,Object> result = new HashMap<>();
		SasMapper sasMapper = getMapper();
		SystemType systemType = ServletUtils.getCurUserSystemType();

		String barcode = form.getBarcode();
		if(barcode.length()==31) {
			try {
				SystemType orgType = BaseIntEnum.valueOf(SystemType.class, Integer.parseInt(barcode.substring(0, 2)));
				long orgId = Long.parseLong(barcode.substring(2,12));
				String code = barcode.substring(12);
				if(!systemType.equals(orgType) || orgId != form.getOrg_id().longValue()) {
					throw new DBException("其他机构的条码!");
				}else {
					Map<String,Object> params = new HashMap<>();
					params.put("org_id", orgId);
					params.put("barcode_sn", code);
					Map<String,Object> codeInfo = sasMapper.checkProductInfoByBarcode(params);
					if(StringUtils.isNotEmpty(codeInfo)) {//检查该条码是否是已入库条码
						int barcode_type = Integer.valueOf(codeInfo.get("barcode_type").toString());
						result.put("barcode_type", barcode_type);
						if(barcode_type == 1) {//多条码
							List<Map<String,Object>> tmp = sasMapper.selectBarCodeInventoryWarehouseRef(params);
							if(StringUtils.isNotEmpty(tmp)) {//判断是否上过货
								throw new DBException("该产品已经上过货!");
							}
						}
						//直接放货不需要检查储位分配单
						if(form.getLocation_inner_sn() == null) {
							if(form.getProduct_vendor_inner_sn() == Long.valueOf(codeInfo.get("product_vendor_inner_sn").toString())
									&& form.getProduct_inner_sn() == Long.valueOf(codeInfo.get("product_inner_sn").toString())
									&& form.getSpecification_inner_sn() == Long.valueOf(codeInfo.get("specification_inner_sn").toString())
									&& form.getUnit_inner_sn() == Long.valueOf(codeInfo.get("unit_inner_sn").toString())) {
								result.put("quantity", 1);
								return result;
							}else {
								throw new DBException("产品信息不符!");
							}
						}
						if(form.getProduct_vendor_inner_sn() == Long.valueOf(codeInfo.get("product_vendor_inner_sn").toString())
								&& form.getProduct_inner_sn() == Long.valueOf(codeInfo.get("product_inner_sn").toString())
								&& form.getSpecification_inner_sn() == Long.valueOf(codeInfo.get("specification_inner_sn").toString())
								&& form.getUnit_inner_sn() == Long.valueOf(codeInfo.get("unit_inner_sn").toString())) {
							params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
							params.put("inventory_inner_sn", form.getInventory_inner_sn());
							params.put("location_inner_sn", form.getLocation_inner_sn());
							params.put("product_vendor_inner_sn", form.getProduct_vendor_inner_sn());
							params.put("product_inner_sn", form.getProduct_inner_sn());
							params.put("specification_inner_sn", form.getSpecification_inner_sn());
							params.put("unit_inner_sn", form.getUnit_inner_sn());
							List<Map<String, Object>> productList = sasMapper.selectPendingProductList(params);
							if(StringUtils.isNotEmpty(productList)) {
								result.put("quantity", 1);
							}else {
								throw new DBException("入库记录中没有这个产品!");
							}
						}else {
							throw new DBException("不是同一种产品，请检查产品条码！");
						}
					}else {
						throw new DBException("入库记录中没有这个产品!");
					}
				}
			} catch (NumberFormatException e) {
				throw new DBException("不能识别的条码!");
			}
		}else {
			throw new DBException("不能识别的条码!");
		}
		return result;
	}

	@Override
	@Transactional
	public int stockProduct(@Valid StockProductForm form) {
		WareHouseMapper wMapper = getWarehouseMapper();
		
		int product_type = form.getProduct_type();
		SasMapper sasMapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("sas_inner_sn", form.getSas_inner_sn());
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		
		//更新储位分配单实放数量、状态等信息
		updateSasInfo(sasMapper, form);
		
		//更新储位与库存数量对应表
		params.put("inventory_inner_sn", form.getInventory_inner_sn());
		params.put("location_inner_sn", form.getLocation_inner_sn());
		BigDecimal newQuantity = form.getQuantity();
		List<Map<String, Object>> ref = sasMapper.selectBarCodeInventoryQuantityRef(params);
		if(StringUtils.isEmpty(ref)) {
			params.put("quantity", newQuantity.multiply(new BigDecimal(1000)));
			params.put("location_description", getLocationDescription(form.getOrg_id(), form.getLocation_inner_sn(), wMapper));
			if(product_type == 1) {
				sasMapper.insertVendorBarCodeInventoryQuantityRef(params);
			}else {
				sasMapper.insertDealerBarCodeInventoryQuantityRef(params);
			}
		}else {
			BigDecimal quantity = new BigDecimal(ref.get(0).get("quantity").toString());
			params.put("quantity", quantity.add(newQuantity).multiply(new BigDecimal(1000)));
			if(product_type == 1) {
				sasMapper.updateVendorBarCodeInventoryQuantityRef(params);
			}else {
				sasMapper.updateDealerBarCodeInventoryQuantityRef(params);
			}
		}
				
		//有条码则更新条码、库存、仓库模型对应表
		List<String> barcodes = form.getBarcodes();
		for (String barcode : barcodes) {
			if(StringUtils.isNotEmpty(barcode)) {
				SystemType systemType = ServletUtils.getCurUserSystemType();
				if(barcode.length()==31) {
					try {
						SystemType orgType = BaseIntEnum.valueOf(SystemType.class, Integer.parseInt(barcode.substring(0, 2)));
						long orgId = Long.parseLong(barcode.substring(2,12));
						String code = barcode.substring(12);
						if(!systemType.equals(orgType) || orgId != form.getOrg_id().longValue()) {
							throw new DBException("其他机构的条码!");
						}else {
							barcode = code;
						}
					} catch (NumberFormatException e) {
						throw new DBException("不能识别的条码!");
					}
				}else {
					throw new DBException("不能识别的条码!");
				}
			}
			if(StringUtils.isNotEmpty(barcode)) {
				params.put("barcode_sn", barcode);
				List<Map<String, Object>> ref1 = sasMapper.selectBarCodeInventoryWarehouseRef(params);
				if(StringUtils.isEmpty(ref1)) {
					if(product_type == 1) {
						sasMapper.insertVendorBarCodeInventoryWarehouseRef(params);
					}else {
						sasMapper.insertDealerBarCodeInventoryWarehouseRef(params);
					}
				}else {
					Map<String,Object> codeInfo = sasMapper.checkProductInfoByBarcode(params);
					if(StringUtils.isNotEmpty(codeInfo)) {//检查该条码是否是已入库条码
						int barcode_type = Integer.valueOf(codeInfo.get("barcode_type").toString());
						if(barcode_type == 1) {//多条码
							throw new DBException("已经有人在你之前把这个产品存放了!");
						}
					}
				}
			}
		}
		
		
		
		return 1;
	}
	
	private void updateSasInfo(SasMapper sasMapper, StockProductForm form) {
		if(form.getSas_inner_sn() == null) {
			return;
		}
		int product_type = form.getProduct_type();
		//更新储位分配单实放数量
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_inner_sn());
		params.put("sas_inner_sn", form.getSas_inner_sn());
		params.put("detail_inner_sn", form.getDetail_inner_sn());
		SasDetail detail = sasMapper.selectSasDetail(params).get(0);
		BigDecimal expect_quantity = detail.getExpect_quantity().multiply(new BigDecimal(1000));
		BigDecimal real_quantity = detail.getReal_quantity();
		BigDecimal new_real_quantity = real_quantity.add(form.getQuantity()).multiply(new BigDecimal(1000));
		params.put("real_quantity", new_real_quantity);
		if(expect_quantity.compareTo(new_real_quantity) == 1) {
			params.put("status", 1);//部分放置
		}else {
			params.put("status", 2);//放置完成
		}
		if(product_type == 1) {
			sasMapper.updateSasVendorDetail(params);
		}else {
			sasMapper.updateSasDealerDetail(params);
		}
		//检查储位分配单是否完成放置
		if(StringUtils.isNotEmpty(sasMapper.checkSasFinish(params))){
			params.put("status", 1);//部分放置
			sasMapper.updateSas(params);
		}else {
			params.put("status", 2);//放置完成
			sasMapper.updateSas(params);
		}
	}
	
	/**
	 * 递归的方式拼接仓库名称，比如“上级名称-下级名称”
	 * @param orgId
	 * @param lid
	 * @param wMapper
	 * @return
	 */
	private String getLocationDescription(Long orgId, Long lid, WareHouseMapper wMapper) {
		WarehouseModel params = new WarehouseModel();
		params.setOrg_id(orgId);
		params.setWarehouse_model_inner_sn(lid);
		WarehouseModel model = wMapper.selectWarehouseModelInfo(params);
		if(model.getParent_warehouse_model_inner_sn() != null) {
			return getLocationDescription(orgId, model.getParent_warehouse_model_inner_sn(), wMapper)+"-"+model.getName();
		}
		return model.getName();
	}
}
