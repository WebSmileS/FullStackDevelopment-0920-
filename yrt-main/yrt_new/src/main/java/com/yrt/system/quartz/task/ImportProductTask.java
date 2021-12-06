package com.yrt.system.quartz.task;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yrt.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yrt.common.exception.DBException;
import com.yrt.project.api.platform.service.IProductService;
import com.yrt.project.modular.category.domain.ProductType;
import com.yrt.project.modular.category.mapper.CategoryMapper;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.Vendor;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductMDRF;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.project.modular.product.domain.ProductUnit;
import com.yrt.project.modular.product.mapper.ProductMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.system.quartz.domain.GoodsInput;
import com.yrt.system.quartz.mapper.GoodsInputMapper;

@Component("importProductTask")
public class ImportProductTask {
	private static Integer vendorname_or_productname_null = -1;//厂商名称或者产品名称为空
	private static Integer add_vendor_error = -2;//添加厂商出错
	private static Integer add_product_error = -3;//添加产品出错
//	private static Integer product_mdrf_duplicate = -6;//产品注册证重复
	private static Integer add_product_mdrf_error = -7;//添加产品注册证出错
	private static Integer product_publish_error = -8;//发布产品出错
	private static Integer product_spc_null = -9;//规格型号为空
	private static Integer product_spc_error = -10;//添加规格型号错误
//	private static Integer product_spc_id_null = -11;//规格型号返回的id为空
	private static Integer product_unit_null = -12;//添加包装单位为空
	private static Integer product_unit_error = -13;//添加包装单位错误

	@Autowired 
	private VendorMapper vMapper;
	@Autowired 
	private ProductMapper pMapper;
	@Autowired 
	private GoodsInputMapper gMapper;
	@Autowired 
	private CategoryMapper cMapper;
	@Autowired 
	private IProductService productService;
	@Autowired
	private ProductUnitMapper uMapper;
	
	
	/**
	 * 1 获取厂商id
	 */
	public Long getVid(String vname) {
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
	public Long addVendor(String vname, String vaddress) {
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
	public Long getPid(Long vid, String pname) {
		Long pid = null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", vid);
		params.put("name", pname);
		List<Product> list = pMapper.searchExistsProductList(params);
		if(StringUtils.isNotEmpty(list)) {
			pid = list.get(0).getProduct_inner_sn();
		}

		return pid;
	}
	
	/**
	 * 2.1 如果pid为空 添加商品
	 */
	public Long insertProduct(Long vid, String pname, String description, String spec, Long tid, boolean type1) {
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
	public List<ProductSpecification> getproductSpc(Long vid, Long pid, String spec) {
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
	public ProductSpecification insertProducSpec(Long vid, Long pid, String spc,String pvd) {
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
	public List<ProductUnit> selectProductUnitList(Long vid, Long pid, Long sid) {
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
	public void insertProductUnit(Long vid, Long pid, Long sid, Long parentId, String name, BigDecimal measure) {
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
	public List<ProductMDRF> getproductMdrf(Long vid, Long pid) {
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
	public void insertProductMdrf(Long vid, Long pid, String register_sn, String scucture_composition,
			String scope_application, String product_tandards, String production_address, String spec, Date startDate,
			Date endDate) throws ParseException {
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
		params.put("begin_date_i", startDate);
		params.put("end_date_i", endDate);

		pMapper.p_add_product_mdrf(params);
		Map<String, Object> params1 = new HashMap<>();
		params1.put("vendor_inner_sn", vid);
		params1.put("product_inner_sn", pid);
		params1.put("cert_no", register_sn);
		pMapper.updateProduct4Map(params1);
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
	public void inputGoods() {
		System.err.println("开始定时任务importProductTask");
		//查询中间表数据
		List<GoodsInput> list = new ArrayList<GoodsInput>();
		list = gMapper.selectAllGoods2();
		//为方便测试某一条数据
		/*GoodsInput input = new GoodsInput();
		input.setId(Long.parseLong("66668"));
		list = gMapper.selectGoodsById2(input);*/
		//循环list存数据 准备分发进不同的表中
		for (GoodsInput goodsInput : list) {
			Long vid = null;
			Long pid = null;
			Long tid = null;
			Long sid = null;
			boolean hasCert = true;
			StringBuffer successMsg = new StringBuffer();
			String errorMsg;
			Long id = goodsInput.getId();
			if(StringUtils.isEmpty(goodsInput.getMfg_name()) || StringUtils.isEmpty(goodsInput.getGoods_name())) {
				gMapper.updatePublishErrorMsg2(id, vendorname_or_productname_null, "厂商名称或者产品名称为空");
				continue;
			}
			//1.是否有产商  有则保存
			vid = getVid(goodsInput.getMfg_name());
			//1.1.没有则新增
			if(vid == null) {
				try {
					vid = addVendor(goodsInput.getMfg_name(), goodsInput.getVendor_address());
					successMsg.append(String.format("添加厂商成功！厂商ID：%d", vid));
					successMsg.append(";");
					gMapper.updateMsgAndStatus2(id, successMsg.toString());
				} catch (Exception e) {
					gMapper.updatePublishErrorMsg2(id, add_vendor_error, e.getMessage());
					continue;
				}
			}else{
				successMsg.append(String.format("该厂商已存在！厂商ID：%d", vid));
				successMsg.append(";");
				gMapper.updateMsgAndStatus2(id, successMsg.toString());
			}
			//2.是否有产品 有则保存
			pid = getPid(vid, goodsInput.getGoods_name());
			if(pid == null) {
				try {
					//从部门字段获取值保存成新的产品分类
					if(StringUtils.isNotEmpty(goodsInput.getDepartment())) {
						Map<String,Object> params = new HashMap<>();
						params.put("type", goodsInput.getDepartment());
						List<ProductType> typeList = cMapper.selectTopLevelProductType(params);
						if(StringUtils.isNotEmpty(typeList)) {
							tid = typeList.get(0).getType_inner_sn();
						}else {
							ProductType type = new ProductType();
							type.setType(goodsInput.getDepartment());
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
					if(pid != null) {
						successMsg.append(String.format("添加产品成功！厂商ID：%d  产品ID：%d", vid, pid));
						successMsg.append(";");
						gMapper.updateMsgAndStatus2(id, successMsg.toString());
					}
				} catch(Exception e) {
					gMapper.updatePublishErrorMsg2(id, add_product_error, e.getMessage());
					continue;
				}
			}else{
				successMsg.append(String.format("该产品已存在！厂商ID：%d  产品ID：%d", vid, pid));
				successMsg.append(";");
				gMapper.updateMsgAndStatus2(id, successMsg.toString());
			}
		
			//3.是否有注册证
			if(hasCert && StringUtils.isNotEmpty(goodsInput.getGoods_cert_no())) {
				String cert = goodsInput.getGoods_cert_no();
				try {
					boolean success = false;
					ProductMDRF mdrfExist = null;
					List<ProductMDRF> mdrfList = getproductMdrf(vid, pid);
					if(StringUtils.isEmpty(mdrfList)) {//注册证不存在，则新增注册证信息
						insertProductMdrf(vid, pid, goodsInput.getGoods_cert_no(),
								goodsInput.getStructure_composition(), goodsInput.getScope_application(),
								goodsInput.getProduct_tandards(), goodsInput.getProduction_address(),
								"", goodsInput.getStart_date(), goodsInput.getEnd_date());
						success = true;
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
							success = true;
						}else{
							successMsg.append(String.format("该注册证号已存在！厂商ID：%d  产品ID：%d ", vid, pid));
							successMsg.append(";");
							gMapper.updateMsgAndStatus2(id, successMsg.toString());
						}
					}
					if(success) {
						successMsg.append(String.format("添加注册证号成功！厂商ID：%d  产品ID：%d", vid, pid));
						successMsg.append(";");
						gMapper.updateMsgAndStatus2(id, successMsg.toString());
					}
				}catch(Exception e) {
					errorMsg = "导入注册证失败,新增语句出错:厂商id:" + vid + ",商品id:" + pid;
					gMapper.updateMDRFErrorMsg2(id, add_product_mdrf_error, errorMsg);
				}
			}
			//4.规格型号
			String spec_str = Utils.trimObject(goodsInput.getGoods_spec());
			if(StringUtils.isNotEmpty(spec_str)) {
				String sn = "";
				// String[] spec_array = goodsInput.getGoods_spec().split(",");
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
					e.printStackTrace();
					errorMsg = "导入规格型号失败,新增语句出错:厂商id:" + vid + ",商品id:" + pid;
					gMapper.updateSPecErrorMsg2(id, product_spc_error, errorMsg);
					continue;
				}
//				for (String spec_str : spec_array) {
//				}
				if(StringUtils.isEmpty(sn)) {
					successMsg.append(String.format("添加规格型号成功！厂商ID：%d  产品ID：%d 规格型号ID: %d", vid, pid, sid));
					successMsg.append(";");
				}else {
					successMsg.append(String.format("更新产品编码: %s", sn));
				}
				gMapper.updateMsgAndStatus2(id, successMsg.toString());
			}else {
				gMapper.updateSPecErrorMsg2(id, product_spc_null, "规格型号为空");
				continue;
			}
			//5.发布
			try {
				Product p1 = productService.selectProductInfo(pid, vid);
				if(p1.getRelease_status() == 0) {
					int result = productService.publishProduct(pid, vid);
					if(result <= 0) {
						errorMsg = "发布产品出错:厂商id:" + vid + ",商品id:" + pid;
						gMapper.updatePublishErrorMsg2(id, product_publish_error, errorMsg);
						continue;
					}else {
						successMsg.append(String.format("发布产品成功！厂商ID：%d", vid));
						successMsg.append(";");
						gMapper.updateMsgAndStatus2(id, successMsg.toString());
					}
				}else {
					successMsg.append(String.format("该商品已发布！厂商ID：%d  产品ID：%d", vid, pid));
					successMsg.append(";");
					gMapper.updateMsgAndStatus2(id, successMsg.toString());
				}
			}catch(Exception e) {
				errorMsg = "发布产品出错:"+e.getMessage()+ "厂商id:" + vid + ",商品id:" + pid;
				gMapper.updatePublishErrorMsg2(id, product_publish_error, errorMsg);
				continue;
			}
			//6.包装单位
			if(StringUtils.isNotNull(sid)) {
				if(StringUtils.isNotEmpty(goodsInput.getProduct_unit())) {
					try {
						List<ProductUnit> unitList = selectProductUnitList(vid, pid, sid);
						String unitName = null;
						Long uId = null;
						boolean success = false;
						if(unitList != null && unitList.size() > 0 ) {
							for(ProductUnit pu : unitList) {
								if(pu.getName().equalsIgnoreCase(goodsInput.getProduct_unit())) {
									unitName = pu.getName();
									uId = pu.getUnit_inner_sn();
									break;
								}
							}
							if(StringUtils.isEmpty(unitName)) {
								insertProductUnit(vid, pid, sid, null, goodsInput.getProduct_unit(), new BigDecimal(1));
								success = true;
							}else{
								successMsg.append(String.format("该包装单位已存在！厂商ID：%d  产品ID：%d  规格型号ID：%d  包装单位：%d", vid, pid, sid, uId));
								successMsg.append(";");
								gMapper.updateMsgAndStatus2(id, successMsg.toString());
							}
						}else {
							insertProductUnit(vid, pid, sid, null, goodsInput.getProduct_unit(), new BigDecimal(1));
							success = true;
						}
						if(success) {
							successMsg.append(String.format("添加包装单位成功！厂商ID：%d  产品ID：%d", vid, pid));
							successMsg.append(";");
							gMapper.updateMsgAndStatus2(id, successMsg.toString());
						}
					} catch(Exception e) {
						errorMsg = "导入包装单位失败,新增语句出错:厂商id:" + vid + ",商品id:" + pid;
						gMapper.updateUnitErrorMsg2(id, product_unit_error, errorMsg);
					}
				}else {
					gMapper.updateUnitErrorMsg2(id, product_unit_null, "包装单位为空");
				}
				
			} 
//			else {
//				gMapper.updateSPecErrorMsg2(id, product_spc_id_null, "规格型号返回的id为空");
//			}
		}					
		System.err.println("结束定时任务importProductTask");
	}
}
