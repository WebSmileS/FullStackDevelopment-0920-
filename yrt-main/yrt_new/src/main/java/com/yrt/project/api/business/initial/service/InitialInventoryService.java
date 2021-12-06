package com.yrt.project.api.business.initial.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.PrdType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.framework.async.AsyncManager;
import com.yrt.project.api.business.initial.vo.InitialSearchProductForm;
import com.yrt.project.api.business.initial.vo.InventoryProductForm;
import com.yrt.project.api.business.warehouse.service.impl.InventoryServiceImpl;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.modular.initial.domain.ImportInventoryProduct;
import com.yrt.project.modular.initial.mapper.DealerInitialInventoryMapper;
import com.yrt.project.modular.initial.mapper.HospitalInitialInventoryMapper;
import com.yrt.project.modular.initial.mapper.InitialInventoryMapper;
import com.yrt.project.modular.initial.mapper.VendorInitialInventoryMapper;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.project.modular.product.mapper.ProductMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.warehouse.domain.InventoryGrnParam;
import com.yrt.project.modular.warehouse.domain.InventoryKey;
import com.yrt.project.modular.warehouse.mapper.DealerWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.VendorWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.WareHouseMapper;

@Service
public class InitialInventoryService {

	private Map<String, Future<List<ImportInventoryProduct>>> tasks = new HashMap<>();
	private Map<String, Future<String>> accTasks = new HashMap<>();
	
	@Autowired HospitalInitialInventoryMapper hMapper;
	@Autowired DealerInitialInventoryMapper dMapper;
	@Autowired VendorInitialInventoryMapper vMapper;
	
	@Autowired DealerWarehouseMapper dwMapper;
	@Autowired HospitalWarehouseMapper hwMapper;
	@Autowired VendorWarehouseMapper vwMapper;
	
	@Autowired VendorMapper vendorMapper;
	@Autowired ProductMapper productMapper;
	@Autowired ProductUnitMapper unitMapper;
	
	@Autowired InventoryServiceImpl inventoryService;
	@Autowired CommService commService;
	
	private InitialInventoryMapper getMapperByCurSystemType(SystemType type) {
		switch (type) {
		case HOSPITAL:
			return hMapper;
		case DEALER:
			return dMapper;
		case VENDOR:
			return vMapper;
		default:
		}
		throw new ValidateException("错误的系统机构！");
	}
	
	private WareHouseMapper getWarehouseMapperByCurSystemType(SystemType type) {
		switch (type) {
		case HOSPITAL:
			return hwMapper;
		case DEALER:
			return dwMapper;
		case VENDOR:
			return vwMapper;
		default:
		}
		throw new ValidateException("错误的系统机构！");
	}
	
	public String asyncImportData(List<ImportInventoryProduct> productList, ContextUserInfo curUser) {
		if (StringUtils.isNull(productList) || StringUtils.isEmpty(productList)){
            throw new BusinessException("导入产品数据不能为空！");
        }
		InitialInventoryMapper mapper = getMapperByCurSystemType(curUser.getSystemType());
		Future<List<ImportInventoryProduct>> future = AsyncManager.me().executeImediately(()->importData(productList, curUser, mapper));
		String id = UUID.randomUUID().toString();
		tasks.put(id, future);
		return id;
	}

	public Future<List<ImportInventoryProduct>> fetchImportTask(String taskId) {
		return tasks.remove(taskId);
	}
	
	/**
	 * 1、导入期初库存Excel表
	 * 2、补齐相应的ID并保持到期初库存表
	 */
	@Transactional
	private List<ImportInventoryProduct> importData(List<ImportInventoryProduct> productList, ContextUserInfo curUser, InitialInventoryMapper mapper) {
		if (StringUtils.isNull(productList) || StringUtils.isEmpty(productList)){
            throw new BusinessException("导入产品数据不能为空！");
        }
		List<ImportInventoryProduct> errorProductList = new ArrayList<>();
		productList.forEach(e -> {
			if(e == null) {
				throw new BusinessException("错误的模板数据！");
			}
			e.setOrg_id(curUser.getOrgId());
			e.setId(CompressedIdGenerator.generateId());
			e.setCreate_date(new Date());
			e.setCreate_id(curUser.getEmployee().getEmployee_inner_sn());
			e.setCreate_by(curUser.getEmployee().getName());
			try {
				packageProductInfo(e, curUser.getSystemType());
				mapper.insertInitialInventory(e);//只保存没有问题的期初数据
			}catch(ValidateException ex) {
				e.setStatus(2);
				e.setError_msg(ex.getMessage());
				errorProductList.add(e);
			}
		});
		
		return errorProductList;
	}
	
	private void packageProductInfo(ImportInventoryProduct info, SystemType type) throws ValidateException {
		info.trim();
		if(StringUtils.isNull(info.getOverdue_date())) {
			throw new ValidateException("产品失效日期不能空");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("orgId", info.getOrg_id());
		params.put("warehouseName", info.getWarehouse_name());
		if(StringUtils.isEmpty(info.getWarehouse_name())) {
			throw new ValidateException("仓库名称不能空");
		}
		Long warehouseId = getWarehouseMapperByCurSystemType(type).existWarehouse(params);
		if(warehouseId == null) {
			throw new ValidateException("错误的仓库名称");
		}else {
			info.setWarehouse_id(warehouseId);
		}
		if(StringUtils.isEmpty(info.getVendor_name()) || StringUtils.isEmpty(info.getProduct_name())
				|| StringUtils.isEmpty(info.getProduct_spec()) || StringUtils.isEmpty(info.getProduct_unit())) {
			throw new ValidateException("产品信息不能空");
		}
		Long vendorId = getVid(info.getVendor_name());
		if(vendorId == null) {
			throw new ValidateException("厂商名称不存在");
		}else {
			info.setVendor_id(vendorId);
		}
		Long productId = getPid(vendorId, info.getProduct_name());
		if(productId == null) {
			throw new ValidateException("产品名称不存在");
		}else {
			info.setProduct_id(productId);
		}
		Long specificationId = null;
		List<ProductSpecification> spcResult = getproductSpc(vendorId, productId, info.getProduct_spec());
		if(spcResult != null && spcResult.size() > 0) {
			for(ProductSpecification spc : spcResult) {
				if(Utils.trimObject(spc.getSpecification()).equalsIgnoreCase(info.getProduct_spec())) {
					specificationId = spc.getSpecification_inner_sn();
					break;
				}
			}
		}
		if(specificationId == null) {
			throw new ValidateException("产品规格型号不存在");
		}else {
			info.setProduct_spec_id(specificationId);
		}
		params.clear();
		params.put("vendor_inner_sn", vendorId);
		params.put("product_inner_sn", productId);
		params.put("specification_inner_sn", specificationId);
		params.put("unit", info.getProduct_unit());
		Long unitId = unitMapper.existProductUnit(params);
		if(unitId == null) {
			throw new ValidateException("产品包装单位不存在");
		}else {
			info.setProduct_unit_id(unitId);
		}
		if((info.getQuantity() != null && info.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
				|| (info.getUnit_price() != null && info.getUnit_price().compareTo(BigDecimal.ZERO) < 0)) {
			throw new ValidateException("数量必须>0并且单价必须>=0");
		}
		if(StringUtils.isNotNull(info.getProduce_date()) && StringUtils.isNotNull(info.getOverdue_date())) {
			if(info.getProduce_date().compareTo(info.getOverdue_date()) >= 0) {
				throw new ValidateException("生产日期不能>=失效日期");
			}
		}
		info.setStatus(0);
	}
	
	@Transactional
	private void creationOfAccount(ImportInventoryProduct info, SystemType type) {
		BigDecimal measure = commService.getProductUnitMeasure(info.getVendor_id(), info.getProduct_id(),
				info.getProduct_spec_id(), info.getProduct_unit_id(), info.getProduct_type());
		String minUnitName = commService.getProductMinUnitName(info.getVendor_id(), info.getProduct_id(),
				info.getProduct_spec_id(), info.getProduct_unit_id(), info.getProduct_type());
		Long minUnitId = commService.getProductMinUnitId(info.getVendor_id(), info.getProduct_id(),
				info.getProduct_spec_id(), info.getProduct_unit_id(), info.getProduct_type());
		BigDecimal minUnitQuantity = info.getQuantity().multiply(measure);
		BigDecimal minUnitPrice = info.getUnit_price().divide(measure, 4, RoundingMode.HALF_UP);
		InventoryKey key = InventoryKey.of(type,
				info.getProduct_type() == 1 ? PrdType.VENDOR_PRD : PrdType.DELEAR_PRD, info.getOrg_id(),
				info.getWarehouse_id(), info.getVendor_id(), info.getProduct_id(), info.getProduct_spec_id(),
				info.getBatch_number());
		InventoryGrnParam param = InventoryGrnParam.of(key, info.getVendor_name(), info.getProduct_name(),
				info.getProduct_spec(), minUnitName, minUnitId, info.getProduce_date(),
				info.getOverdue_date(), minUnitQuantity, minUnitPrice);
		inventoryService.insertOrUpdateInventoryProduct(param, type, false, false);
		
	}
	
	/**
	 * 1 获取厂商id
	 */
	private Long getVid(String vname) {
		Company company = vendorMapper.selectOrgByName(vname);
		Long vid = null;
		if(StringUtils.isNotNull(company)) {
			vid = company.getId();
		}
		return vid;
	}
	
	/**
	 * 2.(获取产品)返回一,查询产商vid ,根据产商vid vname 查询product ,获取产品pid
	 */
	private Long getPid(Long vid, String pname) {
		Long pid = null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", vid);
		params.put("name", pname);
		List<Product> list = productMapper.selectProductByVendorIdAndName(params);
		if(StringUtils.isNotEmpty(list)) {
			pid = list.get(0).getProduct_inner_sn();
		}

		return pid;
	}
	
	/**
	 * 3.查询规格型号
	 * @param vid
	 * @param pid
	 * @return
	 */
	private List<ProductSpecification> getproductSpc(Long vid, Long pid, String spec) {
		ProductSpecification spc = new ProductSpecification();
		spc.setVendor_inner_sn(vid);
		spc.setProduct_inner_sn(pid);
		spc.setSpecification(spec);
		List<ProductSpecification> spcResult = productMapper.searchProductSpecifications(spc);
		return spcResult;
	}

	public String create(Long orgId, SystemType type) {
		InitialInventoryMapper mapper = getMapperByCurSystemType(type);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("org_id", orgId);
		params.put("status", 0);
		List<ImportInventoryProduct> productList = mapper.selectInventoryProductList(params);
		Future<String> future = AsyncManager.me().executeImediately(()->createData(productList, type));
		String id = UUID.randomUUID().toString();
		accTasks.put(id, future);
		
		return id;
	}
	
	@Transactional
	private String createData(List<ImportInventoryProduct> productList, SystemType type){
		InitialInventoryMapper mapper = getMapperByCurSystemType(type);
		for (ImportInventoryProduct info : productList) {
			creationOfAccount(info, type);
			info.setStatus(1);
			mapper.updateInitialInventory(info);
		}
		return "建账成功!";
	}
	
	public Future<String> fetchCreateTask(String taskId) {
		return accTasks.remove(taskId);
	}

	@Transactional
	public int deleteInventoryProduct(Long id, SystemType type) {
		getMapperByCurSystemType(type).deleteInitialInventory(id);
		return 1;
	}

	@Transactional
	public int clean(Long orgId, SystemType type) {
		getMapperByCurSystemType(type).cleanInitialInventory(orgId);
		return 1;
	}
	
	public List<ImportInventoryProduct> getAllInventoryProductList(InitialSearchProductForm form, SystemType type){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("org_id", form.getOrg_id());
		params.put("vendor_name", form.getVendor_name());
		params.put("warehouse_id", form.getWarehouse_id());
		params.put("product_name", form.getProduct_name());
		if(!form.getStatus().equals(-1)) {
			params.put("status", form.getStatus());
		}
		return getMapperByCurSystemType(type).selectInventoryProductList(params);
	}

	@Transactional
	public ImportInventoryProduct save(InventoryProductForm entity, ContextUserInfo curUser) {
		ImportInventoryProduct res = Utils.clone(entity, ImportInventoryProduct.class);
		if(entity.getId() == null) {
			res.setId(CompressedIdGenerator.generateId());
			res.setCreate_date(new Date());
			res.setCreate_id(curUser.getEmployee().getEmployee_inner_sn());
			res.setCreate_by(curUser.getEmployee().getName());
			getMapperByCurSystemType(curUser.getSystemType()).insertInitialInventory(res);
		}else {
			getMapperByCurSystemType(curUser.getSystemType()).updateInitialInventory(res);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("org_id", res.getOrg_id());
		params.put("id", res.getId());
		return getMapperByCurSystemType(curUser.getSystemType()).selectInventoryProduct(params);
	}
}
