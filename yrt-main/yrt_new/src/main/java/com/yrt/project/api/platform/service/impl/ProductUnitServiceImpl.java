package com.yrt.project.api.platform.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.exception.DBException;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.dealer.vo.dealerproduct.UnitIDForm;
import com.yrt.project.api.platform.service.IProductUnitService;
import com.yrt.project.api.platform.vo.product.AddProductUnitForm;
import com.yrt.project.api.platform.vo.product.EditProductUnitForm;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.project.modular.product.domain.ProductUnit;
import com.yrt.project.modular.product.mapper.ProductMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;

@Service
public class ProductUnitServiceImpl implements IProductUnitService {

	@Autowired private ProductUnitMapper mapper;
	@Autowired private ProductMapper pMapper;
	
	@Override
	@Transactional
	public ProductUnit insertProductUnit(AddProductUnitForm form) {
		BigDecimal measure = form.getMeasure();
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", form.getVendor_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("specification_inner_sn_i", form.getSpecification_inner_sn());
		params.put("is_common_use_i", form.getIs_common_use());
		if(StringUtils.isNull(form.getParent_unit_inner_sn())){
			params.put("parent_unit_inner_sn_i", null);
			measure = new BigDecimal(1); //TODO: 2021-04-13 最小包装单位的倍率只能是1
			ProductUnit old = new ProductUnit();
			old.setVendor_inner_sn(form.getVendor_inner_sn());
			old.setParent_unit_inner_sn(form.getProduct_inner_sn());
			old.setSpecification_inner_sn(form.getSpecification_inner_sn());
			List<ProductUnit> tmpList = mapper.selectProductUnitList(old);
			if(StringUtils.isNotEmpty(tmpList)) {
				throw new ValidateException("最小包装单位已经存在!不能添加多个最小包装单位!");
			}
		}else {
			params.put("parent_unit_inner_sn_i", form.getParent_unit_inner_sn());
		}
		params.put("name_i", form.getName());
		params.put("measure_i", measure);
		mapper.p_add_product_unit(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long puid = Long.valueOf(params.get("unit_inner_sn_o").toString());
			if(form.getIs_common_use() == 1) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("vendor_inner_sn", form.getVendor_inner_sn());
				params1.put("product_inner_sn", form.getProduct_inner_sn());
				params1.put("specification_inner_sn", form.getSpecification_inner_sn());
				params1.put("common_use_unit_inner_sn", puid);
				params1.put("common_use_unit", form.getName());
				pMapper.updateProductSpec(params1);
			}
			ProductUnit unit = new ProductUnit();
			unit.setVendor_inner_sn(form.getVendor_inner_sn());
			unit.setProduct_inner_sn(form.getProduct_inner_sn());
			unit.setSpecification_inner_sn(form.getSpecification_inner_sn());
			unit.setUnit_inner_sn(puid);
			return mapper.selectProductUnitInfo(unit);
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","产品单位",params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public int updateProductUnit(Long vid, Long pid, Long psid, Long puid, EditProductUnitForm form) {
		ProductUnit unit = new ProductUnit();
		unit.setVendor_inner_sn(vid);
		unit.setProduct_inner_sn(pid);
		unit.setSpecification_inner_sn(psid);
		unit.setUnit_inner_sn(puid);
		if(puid == null) {
			unit.setMeasure(new BigDecimal(1));
		}else {
			unit.setMeasure(form.getMeasure());
		}
		unit.setName(form.getName());
		unit.setIs_common_use(form.getIs_common_use());
		if(form.getIs_common_use() == 1) {
			mapper.cleanCommonUse(unit);//清除规格型号下所有的常用包装单位
			Map<String,Object> params1 = new HashMap<>();
			params1.put("vendor_inner_sn", vid);
			params1.put("product_inner_sn", pid);
			params1.put("specification_inner_sn", psid);
			params1.put("common_use_unit_inner_sn", puid);
			params1.put("common_use_unit", form.getName());
			pMapper.updateProductSpecCommonUnit(params1);
		}
		return mapper.updateProductUnit(unit);
	}

	@Override
	@Transactional
	public int delProductUnit(UnitIDForm form) {
		if(form.getIs_common_use() == 1) {
			ProductSpecification spec = new ProductSpecification();
			spec.setVendor_inner_sn(form.getOrg_id());
			spec.setProduct_inner_sn(form.getProduct_inner_sn());
			spec.setSpecification_inner_sn(form.getSpecification_inner_sn());
			pMapper.cleanCommonUse(spec);
		}
		ProductUnit unit = new ProductUnit();
		unit.setVendor_inner_sn(form.getOrg_id());
		unit.setProduct_inner_sn(form.getProduct_inner_sn());
		unit.setSpecification_inner_sn(form.getSpecification_inner_sn());
		unit.setUnit_inner_sn(form.getUnit_inner_sn());
		return mapper.deleteProductUnit(unit);
	}
	
	@Override
	public ProductUnit selectProductUnitInfo(Long vid, Long pid, Long psid, Long puid) {
		ProductUnit unit = new ProductUnit();
		unit.setVendor_inner_sn(vid);
		unit.setProduct_inner_sn(pid);
		unit.setSpecification_inner_sn(psid);
		unit.setUnit_inner_sn(puid);
		ProductUnit result = mapper.selectProductUnitInfo(unit);
		if(result != null) {
			getProductUnitChildren(result);
			selectParent(result);
		}
		return result;
	}

	@Override
	public List<ProductUnit> selectProductUnitList(Long vid, Long pid, Long psid) {
		ProductUnit unit = new ProductUnit();
		unit.setVendor_inner_sn(vid);
		unit.setProduct_inner_sn(pid);
		unit.setSpecification_inner_sn(psid);
		return mapper.selectProductUnitList(unit);
	}

	@Override
	public List<ProductUnit> selectProductUnitListByPid(Long vid, Long pid, Long psid, Long pupid) {
		ProductUnit unit = new ProductUnit();
		unit.setVendor_inner_sn(vid);
		unit.setProduct_inner_sn(pid);
		unit.setSpecification_inner_sn(psid);
		unit.setParent_unit_inner_sn(pupid);
		return mapper.selectProductUnitListByPid(unit);
	}

	@Override
	public List<ProductUnit> selectAllProductUnitList(Long vid, Long pid, Long psid) {
		List<ProductUnit> uList = selectProductUnitList(vid, pid, psid);
		for (ProductUnit productUnit : uList) {
			getProductUnitChildren(productUnit);
		}
		return uList;
	}
	
	private void getProductUnitChildren(ProductUnit unit) {
		List<ProductUnit> children = selectProductUnitListByPid(unit.getVendor_inner_sn(), unit.getProduct_inner_sn(),
				unit.getSpecification_inner_sn(),unit.getUnit_inner_sn());
		for (ProductUnit child : children) {
			getProductUnitChildren(child);
		}
		unit.setChildren(children);
	}
	
	@Override
	public void selectParent(ProductUnit unit) {
		if(unit != null && unit.getParent_unit_inner_sn() != null) {
			ProductUnit parent = selectProductUnitInfo(unit.getVendor_inner_sn(), unit.getProduct_inner_sn(), unit.getSpecification_inner_sn(), unit.getParent_unit_inner_sn());
			unit.setParent(parent);
			selectParent(parent);
		}
	}
	
	@Override
	public BigDecimal calculateProductMinUnit(Long vid, Long pid, Long psid, Long puid) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("specification_inner_sn_i", psid);
		params.put("unit_inner_sn_i", puid);
		return mapper.calculate_product_min_unit(params);
	}

}
