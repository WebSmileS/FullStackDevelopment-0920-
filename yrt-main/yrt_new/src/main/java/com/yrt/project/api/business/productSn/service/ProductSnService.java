package com.yrt.project.api.business.productSn.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.yrt.common.dict.ProductSnType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.productSn.vo.EditProductSnForm;
import com.yrt.project.api.business.productSn.vo.ProductSnForm;
import com.yrt.project.modular.product.domain.ProductSn;
import com.yrt.project.modular.product.mapper.DealerProductSnMapper;
import com.yrt.project.modular.product.mapper.ProductSnMapper;
import com.yrt.project.modular.product.mapper.VendorProductSnMapper;

@Service
public class ProductSnService {

	@Autowired DealerProductSnMapper dMapper;
	@Autowired VendorProductSnMapper vMapper;
	
	public ProductSnMapper getMapper() {
		SystemType type = ServletUtils.getCurUserSystemType();
		switch (type) {
		case DEALER:
			return dMapper;
		case VENDOR:
			return vMapper;
		default:
		}
		throw new DBException("当前机构不支持该功能!");
	}
	
	@Transactional
	public void addOrEditProductSn(Long oid, ProductSnForm form) {
		ProductSnMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		params.put("org_id", oid);
		params.put("sn", form.getSn());
		boolean isDefault = form.getIs_default();
		params.put("is_default", form.getIs_default());
		Long sn_inner_sn = null;
		//编号为空表示没有默认编号
		if(StringUtils.isEmpty(form.getSn())) {
			updateNotDefault(oid, form.getVendor_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(), null, form.getType());
			return;
		}
		if(form.getType().equals(ProductSnType.HEALTH_CARE_SN)) {
			//编号已经存在表示设置为默认编号，否则新增为默认编号
			Long id = mapper.existHealthCaseSn(params);
			if(id != null) {
				sn_inner_sn = id;
				params.put("sn_inner_sn", sn_inner_sn);
				mapper.updateHealthCaseSn(params);
			}else {
				mapper.insertHealthCaseSn(params);
				if(Integer.valueOf(params.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","产品医保编号",params.get("message_string_o")));
				}else {
					sn_inner_sn = Long.valueOf(params.get("health_care_sn_inner_sn_o").toString());
				}
			}
		}else {
			Long id = mapper.existSunSn(params);
			if(id != null) {
				sn_inner_sn = id;
				params.put("sn_inner_sn", sn_inner_sn);
				mapper.updateSunSn(params);
			}else {
				mapper.insertSunSn(params);
				if(Integer.valueOf(params.get("result_o").toString()) != 0) {
					throw new DBException(MessageUtils.message("db.add.fail","产品阳光流水号",params.get("message_string_o")));
				}else {
					sn_inner_sn = Long.valueOf(params.get("sun_sn_inner_sn_o").toString());
				}
			}
		}
		if(isDefault) {
			updateNotDefault(oid, form.getVendor_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(), sn_inner_sn, form.getType());
		}
	}
	
	@Transactional
	public void updateProductSn(Long oid, @Validated EditProductSnForm form) {
		ProductSnMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("product_inner_sn", form.getProduct_inner_sn());
		params.put("specification_inner_sn", form.getSpecification_inner_sn());
		params.put("org_id", oid);
		params.put("sn_inner_sn", form.getSn_inner_sn());
		params.put("sn", form.getSn());
		Boolean isDefault = form.getIs_default();
		params.put("is_default", isDefault);
		//编号为空表示没有默认编号
		if(StringUtils.isEmpty(form.getSn())) {
			updateNotDefault(oid, form.getVendor_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(), null, form.getType());
			return;
		}
		if(form.getType().equals(ProductSnType.HEALTH_CARE_SN)) {
			Long id = mapper.existHealthCaseSn(params);
			if(id != null && id.longValue() != form.getSn_inner_sn().longValue()) {
				throw new ValidateException("产品医保编号重复!");
			}else {
				mapper.updateHealthCaseSn(params);
			}
		}else {
			Long id = mapper.existSunSn(params);
			if(id != null && id.longValue() != form.getSn_inner_sn().longValue()) {
				throw new ValidateException("产品阳光流水号重复!");
			}else {
				mapper.updateSunSn(params);
			}
		}
		if(isDefault) {
			updateNotDefault(oid, form.getVendor_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(), form.getSn_inner_sn(), form.getType());
		}
	}
	
	@Transactional
	public void deleteProductSn(Long oid, Long vid, Long pid, Long psid, Long snid, ProductSnType type) {
//		if(snid == null) {
//			throw new ValidateException("'sn_inner_sn'产品编号ID不能为空!");
//		}
		ProductSnMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("specification_inner_sn", psid);
		params.put("org_id", oid);
		params.put("sn_inner_sn", snid);
		if(type.equals(ProductSnType.HEALTH_CARE_SN)) {
			mapper.trueDeleteHealthCaseSn(params);
		}else {
			mapper.trueDeleteSunSn(params);
		}
	}
	
	@Transactional
	public void deleteBatchProductSn(Long oid, Long vid, Long pid, Long psid, ProductSnType type) {
		ProductSnMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("specification_inner_sn", psid);
		params.put("org_id", oid);
		if(type.equals(ProductSnType.HEALTH_CARE_SN)) {
			mapper.trueDeleteHealthCaseSn(params);
		}else {
			mapper.trueDeleteSunSn(params);
		}
	}
	
	public String selectDefaultProductSn(Long oid, Long vid, Long pid, Long psid, ProductSnType type) {
		ProductSnMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("specification_inner_sn", psid);
		params.put("org_id", oid);
		if(type.equals(ProductSnType.HEALTH_CARE_SN)) {
			return mapper.selectDefaultHealthCaseSn(params);
		}else {
			return mapper.selectDefaultSunSn(params);
		}
	}
	
	public List<ProductSn> selectProductSnList(Long oid, Long vid, Long pid, Long psid, ProductSnType type) {
		if(StringUtils.isNull(vid) || StringUtils.isNull(pid) || StringUtils.isNull(psid)) {
			return new ArrayList<>();
		}
		ProductSnMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("specification_inner_sn", psid);
		params.put("org_id", oid);
		if(type.equals(ProductSnType.HEALTH_CARE_SN)) {
			return mapper.selectHealthCaseSnList(params);
		}else {
			return mapper.selectSunSnList(params);
		}
	}
	
	public ProductSn selectProductSn(Long oid, Long vid, Long pid, Long psid, String sn, ProductSnType type) {
		if(StringUtils.isNull(vid) || StringUtils.isNull(pid) || StringUtils.isNull(psid) || StringUtils.isEmpty(sn)) {
			return null;
		}
		List<ProductSn> res = new ArrayList<>();;
		ProductSnMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("specification_inner_sn", psid);
		params.put("sn", sn);
		params.put("org_id", oid);
		if(type.equals(ProductSnType.HEALTH_CARE_SN)) {
			res = mapper.selectHealthCaseSnList(params);
		}else {
			res = mapper.selectSunSnList(params);
		}
		if(StringUtils.isEmpty(res)) {
			return null;
		}else {
			return res.get(0);
		}
	}
	
	@Transactional
	private void updateNotDefault(Long oid, Long vid, Long pid, Long psid, Long snid, ProductSnType type) {
		ProductSnMapper mapper = getMapper();
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("specification_inner_sn", psid);
		params.put("org_id", oid);
		params.put("sn_inner_sn", snid);
		
		if(type.equals(ProductSnType.HEALTH_CARE_SN)) {
			mapper.updateNotDefaultHealthCaseSn(params);
		}else {
			mapper.updateNotDefaultSunSn(params);
		}
	}
}
