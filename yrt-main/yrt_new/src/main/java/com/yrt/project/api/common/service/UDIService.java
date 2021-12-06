package com.yrt.project.api.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.exception.DBException;
import com.yrt.common.exception.NotFoundUDIException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.BarcodeUtil;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.PinyinUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.common.utils.barcode.ApplicationIdentifier;
import com.yrt.common.utils.barcode.ElementStrings.ParseResult;
import com.yrt.project.api.business.tag.vo.AddUDIRefproductForm;
import com.yrt.project.api.business.tag.vo.SearchProductBaseForm;
import com.yrt.project.api.business.tag.vo.SearchUDIRefProductForm;
import com.yrt.project.api.common.vo.UDIDInfo;
import com.yrt.project.api.platform.service.IProductService;
import com.yrt.project.modular.category.domain.ProductType;
import com.yrt.project.modular.category.mapper.CategoryMapper;
import com.yrt.project.modular.organization.domain.Vendor;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.product.domain.ProductBaseRefTag;
import com.yrt.project.modular.product.domain.ProductUnit;
import com.yrt.project.modular.product.domain.UDIDeviceRecord;
import com.yrt.project.modular.product.domain.UDIRefProduct;
import com.yrt.project.modular.product.mapper.ProductMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.product.mapper.UDIRefProductMapper;

@Service
public class UDIService {

	@Autowired UDIRefProductMapper mapper;
	
	@Autowired VendorMapper vMapper;
	@Autowired ProductMapper pMapper;

	@Autowired CategoryMapper cMapper;
	@Autowired IProductService productService;
	@Autowired ProductUnitMapper uMapper;
	
	@Transactional
	public UDIDInfo parseBarcode(String code, boolean isScan) throws NotFoundUDIException{
		UDIDInfo info = new UDIDInfo();
		info.setWhole_barcode(code);
		//1.查询条码与产品关联表
		UDIRefProduct refProduct = getUDIRefProductInfo(code);
		
		//2.存在关联关系的产品时，返回产品关键信息
		if(refProduct != null) {
			info.setProduct_vendor_inner_sn(refProduct.getVendor_inner_sn());
			info.setProduct_inner_sn(refProduct.getProduct_inner_sn());
			info.setSpecification_inner_sn(refProduct.getSpecification_inner_sn());
			Map<String,Object> params = new HashMap<>();
			params.put("product_vendor_inner_sn", refProduct.getVendor_inner_sn());
			params.put("product_inner_sn", refProduct.getProduct_inner_sn());
			params.put("specification_inner_sn", refProduct.getSpecification_inner_sn());
			ProductBaseRefTag productInfo = mapper.selectProductBaseRefTagInfo(params);
			info.setProduct_vendor_name(productInfo.getProduct_vendor_name());
			info.setProduct_name(productInfo.getProduct_name());
			info.setSpecification(productInfo.getSpecification());
			return info;
		}else {//2.不存在关联关系产品时，解析条码再次查找关联关系
			try {
				//3.解析条码
				ParseResult res = BarcodeUtil.parseGS1Barcode(code);
				info.setUdi_barcode(res.getString(ApplicationIdentifier.GTIN));
				info.setProduce_date(res.getDate(ApplicationIdentifier.PRODUCTION_DATE));
				info.setOverdue_date(res.getDate(ApplicationIdentifier.EXPIRATION_DATE));
				info.setBatch_number(res.getString(ApplicationIdentifier.BATCH_OR_LOT_NUMBER));
				info.setSerial_number(res.getString(ApplicationIdentifier.SERIAL_NUMBER));
				if(!info.includeUDI()) return info;
				//4.再次查询关联关系
				refProduct = getUDIRefProductInfo(info.getUdi_barcode());
				
				//5.存在关联关系产品时，返回产品信息
				if(refProduct != null) {
					info.setProduct_vendor_inner_sn(refProduct.getVendor_inner_sn());
					info.setProduct_inner_sn(refProduct.getProduct_inner_sn());
					info.setSpecification_inner_sn(refProduct.getSpecification_inner_sn());
					Map<String,Object> params = new HashMap<>();
					params.put("product_vendor_inner_sn", refProduct.getVendor_inner_sn());
					params.put("product_inner_sn", refProduct.getProduct_inner_sn());
					params.put("specification_inner_sn", refProduct.getSpecification_inner_sn());
					ProductBaseRefTag productInfo = mapper.selectProductBaseRefTagInfo(params);
					info.setProduct_vendor_name(productInfo.getProduct_vendor_name());
					info.setProduct_name(productInfo.getProduct_name());
					info.setSpecification(productInfo.getSpecification());
					return info;
				}else {
					//3.不存在关联关系的产品时，查找与国家官网同步的UDI记录表，创建平台新产品或者抛出NOTFOUND错误
					if(isScan) {
						return getUDIDeviceInfoAndCreateUDIRefProductInfo(info);
					}else {
						info.setUdi_barcode(null);
					}
				}
			}catch (ValidateException e) {
				
			}
		}
		return info;
	}
	
	private UDIDInfo getUDIDeviceInfoAndCreateUDIRefProductInfo(UDIDInfo info) throws NotFoundUDIException{
		Map<String,Object> params = new HashMap<>();
		params.put("udi_barcode", info.getUdi_barcode());
		
		UDIDeviceRecord record = mapper.selectUDIDeviceRecordInfo(params);
		if(record == null) throw new NotFoundUDIException("条码还没有记录在国家药监局医疗器械唯一标识数据库中！");
		
		UDIRefProduct refProduct = createUDIRefProductInfo(record);
		info.setProduct_vendor_inner_sn(refProduct.getVendor_inner_sn());
		info.setProduct_inner_sn(refProduct.getProduct_inner_sn());
		info.setSpecification_inner_sn(refProduct.getSpecification_inner_sn());
		info.setProduct_vendor_name(record.getYlqxzcrbarmc());
		info.setProduct_name(record.getCpmctymc());
		info.setSpecification(record.getGgxh());
		return info;
	}
	
	@Transactional
	public UDIRefProduct createUDIRefProductInfo(UDIDeviceRecord record) {
		Long vid = mapper.selectVendorId(record.getYlqxzcrbarmc());
		if(vid == null) {
			Vendor vendor = new Vendor();
			vendor.setName(record.getYlqxzcrbarmc());
			vendor.setAddress("");
			vendor.setEnterprise_type(0);
			vendor.setSocial_credit_code(record.getTyshxydm());
			vMapper.insertOrg(vendor);
			vid = vendor.getId();
		}
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("name", record.getCpmctymc());
		Long pid = mapper.selectProductId(params);
		if(pid == null) {
			pid = insertProduct(vid, record);
		}
		params.put("product_inner_sn", pid);
		params.put("specification", record.getGgxh());
		Long psid = mapper.selectSpecificationId(params);
		if(psid == null) {
			psid = insertProducSpec(vid, pid, record.getGgxh());
		}
		productService.publishProduct(pid, vid);
		createDefaultUnit(vid, pid, psid, "个");
		return saveUDIRefProduct(vid, pid, psid, record.getZxxsdycpbs());
	}

	public UDIRefProduct getUDIRefProductInfo(String barcode) {
		Map<String,Object> params = new HashMap<>();
		params.put("udi_barcode", barcode);
		return mapper.selectUDIRefProductInfo(params);
	}
	
	@Transactional
	public int batchSaveUDIRefProduct(List<AddUDIRefproductForm> list) {
		for (AddUDIRefproductForm form : list) {
			saveUDIRefProduct(form);
		}
		return 1;
	}
	
	@Transactional
	public void saveUDIRefProduct(@Valid AddUDIRefproductForm form) {
		saveUDIRefProduct(form.getVendor_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(), form.getUdi_barcode());
	}
	
	@Transactional
	private UDIRefProduct saveUDIRefProduct(Long vid, Long pid, Long psid, @NotBlank(message = "条码不能为空!") String udi_barcode) {
		String code = null;
		UDIDInfo info = parseBarcode(udi_barcode, false);
		if(info.getUdi_barcode() == null) {
			code = info.getWhole_barcode();
		}else {
			code = info.getUdi_barcode();
		}
		UDIRefProduct res = UDIRefProduct.of(vid, pid, psid, code);
		try {
			mapper.insertUDIRefProduct(res);
		}catch(DuplicateKeyException e) {
			throw new ValidateException(String.format("'%s'已经和其他产品绑定！", code));
		}
		return res;
	}
	
	public List<ProductBaseRefTag> getProductBaseRefTagList(SearchProductBaseForm form, boolean hasTag){
		String code = null;
		if(!Utils.isEmpty(form.getUdi_barcode())){
			UDIDInfo info = parseBarcode(form.getUdi_barcode(), false);
			if(info.getUdi_barcode() == null) {
				code = info.getWhole_barcode();
			}else {
				code = info.getUdi_barcode();
			}
		}
		Map<String,Object> params = new HashMap<>();
		params.put("udi_barcode", code);
		params.put("product_vendor_name", form.getProduct_vendor_name());
		params.put("product_name", form.getProduct_name());
		params.put("specification", form.getSpecification());
		params.put("hasTag", hasTag?1:0);
		
		return mapper.selectProductBaseRefTagList(params);
	}
	
	@Transactional
	public int deleteUDIRefProduct(String barcode) {
		mapper.deleteUDIRefProduct(barcode);
		return 1;
	}
	
	@Transactional
	private Long insertProduct(Long vid, UDIDeviceRecord record) {
		Long typeId = null;
		boolean type1 = StringUtils.isEmpty(record.getZczbhhzbapzbh());
		if(StringUtils.isNotEmpty(record.getQxlb())) {
			Map<String,Object> params = new HashMap<>();
			params.put("type", record.getQxlb());
			List<ProductType> typeList = cMapper.selectTopLevelProductType(params);
			if(StringUtils.isNotEmpty(typeList)) {
				typeId = typeList.get(0).getType_inner_sn();
			}else {
				ProductType type = new ProductType();
				type.setType(record.getQxlb());
				type.setParent_type_inner_sn(null);
				type.setDescription("");
				cMapper.insertProductType(type);
				typeId = type.getType_inner_sn();
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn_i", vid);
		params.put("data_source_i", 0);
		params.put("storage_condition_i", 0);
		params.put("health_care_type_i", 0);
		params.put("name_i", record.getCpmctymc());
		params.put("product_sn_i", "");
		params.put("description_i", record.getCpms());
		if(type1) {
			params.put("type_i", 1);
			params.put("specifications_i", "");
		}else {
			params.put("type_i", 0);
			params.put("specifications_i", "");
		}
		params.put("type_inner_sn_i", typeId);
		params.put("pinyin_i", PinyinUtils.getPy(record.getCpmctymc()));

		pMapper.p_add_product(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			return Long.valueOf(params.get("product_inner_sn_o").toString());
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","产品",params.get("message_string_o")));
		}
	}
	
	@Transactional
	private Long insertProducSpec(Long vid, Long pid, String spc) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("specification_i", spc);
		params.put("specification_vendor_sn_i", null);
		params.put("status_i",1);
		pMapper.p_add_product_specification(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			return Long.valueOf(params.get("specification_inner_sn_o").toString());
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","产品规格型号",params.get("message_string_o")));
		}
	}
	
	@Transactional
	private void createDefaultUnit(Long vid, Long pid, Long psid, String defaultUnit) {
		ProductUnit unit = new ProductUnit();
		unit.setVendor_inner_sn(vid);
		unit.setProduct_inner_sn(pid);
		unit.setSpecification_inner_sn(psid);
		if(StringUtils.isEmpty(uMapper.selectProductUnitList(unit))){
			Map<String, Object> params = new HashMap<>();
			params.put("vendor_inner_sn_i", vid);
			params.put("product_inner_sn_i", pid);
			params.put("specification_inner_sn_i", psid);
			params.put("parent_unit_inner_sn_i", null);
			params.put("name_i", defaultUnit);
			params.put("measure_i", 1);
			params.put("is_common_use_i", 1);
			uMapper.p_add_product_unit(params);
		}
	}

	public List<Map<String,Object>> selectVendorProductList(SearchUDIRefProductForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("product_name", form.getName());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("specification", form.getSpecification());
		params.put("specification_vendor_sn", form.getSpecification_vendor_sn());
		return mapper.selectVendorProductList(params);
	}
}
