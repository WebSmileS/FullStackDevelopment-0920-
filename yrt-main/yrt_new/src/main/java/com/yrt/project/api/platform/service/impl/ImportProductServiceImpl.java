package com.yrt.project.api.platform.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import com.yrt.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.DBException;
import com.yrt.framework.async.AsyncManager;
import com.yrt.project.api.platform.service.IImportProductService;
import com.yrt.project.api.platform.service.IProductService;
import com.yrt.project.modular.category.domain.ProductType;
import com.yrt.project.modular.category.mapper.CategoryMapper;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.Vendor;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.product.domain.ImportProduct;
import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductMDRF;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.project.modular.product.domain.ProductUnit;
import com.yrt.project.modular.product.mapper.ProductMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;

@Service
public class ImportProductServiceImpl implements IImportProductService {

	@Autowired VendorMapper vMapper;
	@Autowired ProductMapper pMapper;
	@Autowired CategoryMapper cMapper;
	@Autowired IProductService productService;
	@Autowired ProductUnitMapper uMapper;
	
	private Map<String, Future<List<ImportProduct>>> tasks = new HashMap<>();
	
	public String asyncImportData(List<ImportProduct> productList) {
		if (StringUtils.isNull(productList) || StringUtils.isEmpty(productList)){
            throw new BusinessException("导入产品数据不能为空！");
        }
		Future<List<ImportProduct>> future = AsyncManager.me().executeImediately(()->importData(productList));
		String id = UUID.randomUUID().toString();
		tasks.put(id, future);
		return id;
	}
	
	public Future<List<ImportProduct>> fetchImportTask(String id) {
		return tasks.remove(id);
	}
	
	/**
	 * 顺序 （顺序不能颠倒）：
	 * 1.产商
	 * 2.产品
	 * 3.注册证
	 * 4.规格型号
	 * 5.发布
	 * 6.包装单位
	 */
	private List<ImportProduct> importData(List<ImportProduct> productList) {
		if (StringUtils.isNull(productList) || StringUtils.isEmpty(productList)){
            throw new BusinessException("导入产品数据不能为空！");
        }
		List<ImportProduct> errorProductList = new ArrayList<>();
		for (ImportProduct goodsInput : productList) {
			Long vid = null;
			Long pid = null;
			Long tid = null;
			Long sid = null;
			boolean hasCert = true;
			if(StringUtils.isEmpty(goodsInput.getMfg_name()) || StringUtils.isEmpty(goodsInput.getGoods_name())) {
				goodsInput.setMsg("厂商名称或者产品名称为空");
				errorProductList.add(goodsInput);
				continue;
			}
			//1.是否有产商  有则保存
			vid = getVid(goodsInput.getMfg_name());
			//1.1.没有则新增
			if(vid == null) {
				try {
					vid = addVendor(goodsInput.getMfg_name(), goodsInput.getVendor_address());
				} catch (Exception e) {
					goodsInput.setMsg(String.format("添加厂商出错!错误信息 %s", e.getMessage()));
					errorProductList.add(goodsInput);
					continue;
				}
			}
			//2.是否有产品 有则保存
			pid = getPid(vid, goodsInput.getGoods_name());
			if(pid == null) {
				try {
					//从部门字段获取值保存成新的产品分类
					if(StringUtils.isNotEmpty(goodsInput.getType())) {
						Map<String,Object> params = new HashMap<>();
						params.put("type", goodsInput.getType());
						List<ProductType> typeList = cMapper.selectTopLevelProductType(params);
						if(StringUtils.isNotEmpty(typeList)) {
							tid = typeList.get(0).getType_inner_sn();
						}else {
							ProductType type = new ProductType();
							type.setType(goodsInput.getType());
							type.setParent_type_inner_sn(null);
							type.setDescription("");
							cMapper.insertProductType(type);
							tid = type.getType_inner_sn();
						}
					}
					String desc = goodsInput.getDescription();
					if(StringUtils.isEmpty(desc)) {
						desc = "";
					}
					//非医疗器械
					if(StringUtils.isEmpty(goodsInput.getGoods_cert_no())) {
						pid = insertProduct(vid, goodsInput.getGoods_name(), desc, goodsInput.getGoods_spec(), tid, true);
						hasCert = false;
					//医疗器械	
					}else {
						pid = insertProduct(vid, goodsInput.getGoods_name(), desc, goodsInput.getGoods_spec(), tid, false);
					}
				} catch(Exception e) {
					goodsInput.setMsg(String.format("添加产品出错!错误信息 %s", e.getMessage()));
					errorProductList.add(goodsInput);
					continue;
				}
			}
		
			//3.是否有注册证
			if(hasCert && StringUtils.isNotEmpty(goodsInput.getGoods_cert_no())) {
				String cert = goodsInput.getGoods_cert_no();
				try {
					ProductMDRF mdrfExist = null;
					List<ProductMDRF> mdrfList = getproductMdrf(vid, pid);
					if(StringUtils.isEmpty(mdrfList)) {//注册证不存在，则新增注册证信息
						insertProductMdrf(vid, pid, goodsInput.getGoods_cert_no(),
								goodsInput.getStructure_composition(), goodsInput.getScope_application(),
								goodsInput.getProduct_tandards(), goodsInput.getProduction_address(),
								"", goodsInput.getStart_date(), goodsInput.getEnd_date());
					}else {
						for (ProductMDRF mdrf : mdrfList) {
							if(cert.equalsIgnoreCase(mdrf.getRegister_sn())) {
								mdrfExist = mdrf;
								break;
							}
						}
						if(mdrfExist ==null){//注册证不存在重复，则新增注册证信息
							insertProductMdrf(vid, pid, goodsInput.getGoods_cert_no(),
									goodsInput.getStructure_composition(), goodsInput.getScope_application(),
									goodsInput.getProduct_tandards(), goodsInput.getProduction_address(),
									"", goodsInput.getStart_date(), goodsInput.getEnd_date());
						}
					}
					
				}catch(Exception e) {
					goodsInput.setMsg(String.format("厂商id:%d,商品id:%d 导入注册证失败!错误信息 %s", vid, pid, e.getMessage()));
					errorProductList.add(goodsInput);
				}
			}
			//4.规格型号
			String spec_str = Utils.trimObject(goodsInput.getGoods_spec());
			if(StringUtils.isNotEmpty(spec_str)) {
				String sn = "";
				try {
					ProductSpecification spec = new ProductSpecification();
					List<ProductSpecification> spcResult = getproductSpc(vid, pid, spec_str);
					if(spcResult != null && spcResult.size() > 0) {
						String spcName = null;
						for(ProductSpecification spc : spcResult) {
							if(Utils.trimObject(spc.getSpecification()).equalsIgnoreCase(spec_str)) {
								spcName = goodsInput.getGoods_spec();
								sid = spc.getSpecification_inner_sn();
								spc.setSpecification_vendor_sn(goodsInput.getProduct_vendor_sn());
								pMapper.updateProductSpec(spc);
								sn = goodsInput.getProduct_vendor_sn();
								break;
							}
						}
						//该规格型号没有重复 则添加
						if(StringUtils.isEmpty(spcName)) {
							spec = insertProducSpec(vid, pid, spec_str, goodsInput.getProduct_vendor_sn());
							sid = spec.getSpecification_inner_sn();
						}
					}else {//该产商产品下没有规格型号 则添加
						spec = insertProducSpec(vid, pid, spec_str,  goodsInput.getProduct_vendor_sn());
						sid = spec.getSpecification_inner_sn();
					}
				} catch(Exception e) {
					goodsInput.setMsg(String.format("厂商id:%d,商品id:%d 导入规格型号失败!错误信息 %s", vid, pid, e.getMessage()));
					errorProductList.add(goodsInput);
					continue;
				}
				if(StringUtils.isEmpty(sn)) {
					goodsInput.setMsg(String.format("添加规格型号成功！厂商ID：%d  产品ID：%d 规格型号ID: %d;", vid, pid, sid));
				}else {
					goodsInput.setMsg(String.format("更新产品编码: %s", sn));
				}
			}else {
				goodsInput.setMsg("规格型号为空!");
				errorProductList.add(goodsInput);
				continue;
			}
			//5.发布
			try {
				Product p1 = productService.selectProductInfo(pid, vid);
				if(p1.getRelease_status() == 0) {
					int result = productService.publishProduct(pid, vid);
					if(result <= 0) {
						goodsInput.setMsg(String.format("厂商id:%d,商品id:%d 发布产品出错!", vid, pid));
						errorProductList.add(goodsInput);
						continue;
					}
				}
			}catch(Exception e) {
				goodsInput.setMsg(String.format("厂商id:%d,商品id:%d 发布产品出错!错误信息 %s", vid, pid, e.getMessage()));
				errorProductList.add(goodsInput);
				continue;
			}
			//6.包装单位
			if(StringUtils.isNotNull(sid)) {
				if(StringUtils.isNotEmpty(goodsInput.getProduct_unit())) {
					try {
						List<ProductUnit> unitList = selectProductUnitList(vid, pid, sid);
						String unitName = null;
						if(unitList != null && unitList.size() > 0 ) {
							for(ProductUnit pu : unitList) {
								if(pu.getName().equalsIgnoreCase(goodsInput.getProduct_unit())) {
									unitName = pu.getName();
									break;
								}
							}
							if(StringUtils.isEmpty(unitName)) {
								insertProductUnit(vid, pid, sid, null, goodsInput.getProduct_unit(), new BigDecimal(1));
							}
						}else {
							insertProductUnit(vid, pid, sid, null, goodsInput.getProduct_unit(), new BigDecimal(1));
						}
					} catch(Exception e) {
						goodsInput.setMsg(String.format("厂商id:%d,商品id:%d,规格id:%d 导入包装单位失败!错误信息 %s", vid, pid, sid, e.getMessage()));
						errorProductList.add(goodsInput);
						continue;
					}
				}else {
					goodsInput.setMsg("包装单位为空");
					errorProductList.add(goodsInput);
					continue;
				}
			} 
		}					
		return errorProductList;
	}

	/**
	 * 1 获取厂商id
	 */
	private Long getVid(String vname) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", vname);
		List<Company> list = vMapper.searchOrgList(params);
		Long vid = null;
		if(StringUtils.isNotEmpty(list)) {
			vid = list.get(0).getId();
		}
		return vid;
	} 
	
	/**
	 * 1.1 如果vid为空则添加产商
	 */
	private Long addVendor(String vname, String vaddress) {
		Vendor vendor = new Vendor();
		vendor.setName(vname);
		vendor.setAddress(vaddress);
		vendor.setEnterprise_type(0);
		vendor.setSocial_credit_code(RandomPasswordsUtils.randomPasswords(18));
		vMapper.insertOrg(vendor);
		return vendor.getId();
	}
	
	/**
	 * 2.(获取产品)返回一,查询产商vid ,根据产商vid vname 查询product ,获取产品pid
	 */
	private Long getPid(Long vid, String pname) {
		Long pid = null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", vid);
		params.put("name", pname);
		List<Product> list = pMapper.searchProductList(params);
		if(StringUtils.isNotEmpty(list)) {
			pid = list.get(0).getProduct_inner_sn();
		}

		return pid;
	}
	
	/**
	 * 2.1 如果pid为空 添加商品
	 */
	private Long insertProduct(Long vid, String pname, String description, String spec, Long tid, boolean type1) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn_i", vid);
		params.put("data_source_i", 0);
		params.put("storage_condition_i", 0);
		params.put("health_care_type_i", 0);
		params.put("name_i", pname);
		params.put("product_sn_i", "");
		params.put("description_i", description);
		if(type1) {
			params.put("type_i", 1);
			params.put("specifications_i", "");
		}else {
			params.put("type_i", 0);
			params.put("specifications_i", "");
		}
		params.put("type_inner_sn_i", tid);
		params.put("pinyin_i", PinyinUtils.getPy(pname));

		pMapper.p_add_product(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			return Long.valueOf(params.get("product_inner_sn_o").toString());
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","产品",params.get("message_string_o")));
		}
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
		List<ProductSpecification> spcResult = pMapper.searchProductSpecifications(spc);
		return spcResult;
	} 
	/**
	 * 3.1添加规格型号
	 * @param vid
	 * @param pid
	 * @param spc
	 * @param pvd
	 */
	private ProductSpecification insertProducSpec(Long vid, Long pid, String spc,String pvd) {
		if(StringUtils.isEmpty(pvd)) {
			pvd =null;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("specification_i", spc);
		params.put("specification_vendor_sn_i",pvd);
		params.put("status_i",1);
		pMapper.p_add_product_specification(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			ProductSpecification sp = new ProductSpecification();
			sp.setVendor_inner_sn(vid);
			sp.setProduct_inner_sn(pid);
			sp.setSpecification_inner_sn(Long.valueOf(params.get("specification_inner_sn_o").toString()));
			sp.setSpecification_vendor_sn(pvd);
			return pMapper.selectProductSpecInfoById(sp);
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","产品规格型号",params.get("message_string_o")));
		}
	}
	/**
	 * 4.查询包装单位
	 * @param vid
	 * @param pid
	 * @param psid
	 * @return
	 */
	private List<ProductUnit> selectProductUnitList(Long vid, Long pid, Long sid) {
		ProductUnit unit = new ProductUnit();
		unit.setVendor_inner_sn(vid);
		unit.setProduct_inner_sn(pid);
		unit.setSpecification_inner_sn(sid);
		return uMapper.selectProductUnitList(unit);
	}
	/**
	 * 4.1新增包装单位
	 * @param vid
	 * @param pid
	 * @param sid
	 * @param parentId
	 * @param name
	 * @param measure
	 */
	private void insertProductUnit(Long vid, Long pid, Long sid, Long parentId, String name, BigDecimal measure) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("specification_inner_sn_i", sid);
		if(StringUtils.isNull(parentId)){
			params.put("parent_unit_inner_sn_i", null);
		}else {
			params.put("parent_unit_inner_sn_i", parentId);
		}
		params.put("name_i", name);
		params.put("measure_i", measure);
		params.put("is_common_use_i", 1);
		uMapper.p_add_product_unit(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long puid = Long.valueOf(params.get("unit_inner_sn_o").toString());
			Map<String,Object> params1 = new HashMap<>();
			params1.put("vendor_inner_sn", vid);
			params1.put("product_inner_sn", pid);
			params1.put("specification_inner_sn", sid);
			params1.put("common_use_unit_inner_sn", puid);
			params1.put("common_use_unit", name);
			pMapper.updateProductSpec(params1);
		}
	}
	
	/**
	 * 5. 查询注册证 回到一 到三 获取 vid pid 如果没有查到 新增一个
	 */
	private List<ProductMDRF> getproductMdrf(Long vid, Long pid) {
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("vendor_inner_sn", vid);
		info.put("product_inner_sn", pid);
		List<ProductMDRF> mdrf1 = pMapper.searchProductMDRFList(info);
		return mdrf1;
	}
	
	/**
	 * 5.1 添加注册证
	 * 
	 * @param vname
	 * @param pname
	 * @param register_sn
	 * @throws ParseException
	 */
	private void insertProductMdrf(Long vid, Long pid, String register_sn, String scucture_composition,
			String scope_application, String product_tandards, String production_address, String spec, String startDate,
			String endDate) throws ParseException {
		Map<String, Object> params = new HashMap<>();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

		Date date = dateformat.parse("2000-01-01");
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("issue_date_i", date);
		params.put("register_sn_i", register_sn);
		params.put("issue_organization_i", "");
		params.put("standard_i", product_tandards);
		params.put("production_address_i", production_address);
		params.put("performance_structure_i", scucture_composition);
		params.put("application_range_i", scope_application);
		params.put("contraindication_i", "");
		params.put("description_i", "");
		params.put("specifications_i", spec);
		params.put("begin_date_i", dateformat.parse(startDate));
		params.put("end_date_i", dateformat.parse(endDate));

		pMapper.p_add_product_mdrf(params);
		Map<String, Object> params1 = new HashMap<>();
		params1.put("vendor_inner_sn", vid);
		params1.put("product_inner_sn", pid);
		params1.put("cert_no", register_sn);
		pMapper.updateProduct4Map(params1);
	}
}
