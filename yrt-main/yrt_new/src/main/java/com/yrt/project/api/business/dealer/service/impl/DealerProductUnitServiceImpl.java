package com.yrt.project.api.business.dealer.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.exception.DBException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.dealer.service.IDealerProductUnitService;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductUnitForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.UnitIDForm;
import com.yrt.project.api.platform.vo.product.EditProductUnitForm;
import com.yrt.project.modular.product.domain.DealerProductSpecification;
import com.yrt.project.modular.product.domain.DealerProductUnit;
import com.yrt.project.modular.product.mapper.DealerProductMapper;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;

@Service
public class DealerProductUnitServiceImpl implements IDealerProductUnitService {

	@Autowired private DealerProductUnitMapper mapper;
	@Autowired private DealerProductMapper pMapper;
	
	@Override
	@Transactional
	public DealerProductUnit insertDealerProductUnit(AddDealerProductUnitForm form) {
		BigDecimal measure = form.getMeasure();
		Map<String, Object> params = new HashMap<>();
		params.put("dealer_inner_sn_i", form.getDealer_inner_sn());
		params.put("product_inner_sn_i", form.getProduct_inner_sn());
		params.put("specification_inner_sn_i", form.getSpecification_inner_sn());
		params.put("is_common_use_i", form.getIs_common_use());
		if(StringUtils.isNull(form.getParent_unit_inner_sn())){
			params.put("parent_unit_inner_sn_i", null);
			//measure = new BigDecimal(1);
		}else {
			params.put("parent_unit_inner_sn_i", form.getParent_unit_inner_sn());
		}
		params.put("name_i", form.getName());
		params.put("measure_i", measure);
		mapper.p_add_dealer_product_unit(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long puid = Long.valueOf(params.get("unit_inner_sn_o").toString());
			if(form.getIs_common_use() == 1) {
				DealerProductSpecification spec = new DealerProductSpecification();
				spec.setDealer_inner_sn(form.getDealer_inner_sn());
				spec.setProduct_inner_sn(form.getProduct_inner_sn());
				spec.setSpecification_inner_sn(form.getSpecification_inner_sn());
				spec.setCommon_use_unit_inner_sn(puid);
				spec.setCommon_use_unit(form.getName());
				pMapper.updateDealerProductSpec(spec);
			}
			return selectDealerProductUnitInfo(form.getDealer_inner_sn(), form.getProduct_inner_sn(), form.getSpecification_inner_sn(), puid);
		}else {
			throw new DBException(MessageUtils.message("db.procedures.error",params.get("message_string_o")));
		}
	}

	@Override
	@Transactional
	public int updateDealerProductUnit(Long did, Long pid, Long psid, Long puid, EditProductUnitForm form) {
		DealerProductUnit unit = new DealerProductUnit();
		unit.setDealer_inner_sn(did);
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
			DealerProductSpecification spec = new DealerProductSpecification();
			spec.setDealer_inner_sn(did);
			spec.setProduct_inner_sn(pid);
			spec.setSpecification_inner_sn(psid);
			spec.setCommon_use_unit_inner_sn(puid);
			spec.setCommon_use_unit(form.getName());
			pMapper.updateDealerProductSpec(spec);
		}
		return mapper.updateDealerProductUnit(unit);
	}

	@Override
	@Transactional
	public int delDealerProductUnit(UnitIDForm form) {
		if(form.getIs_common_use() == 1) {
			DealerProductSpecification spec = new DealerProductSpecification();
			spec.setDealer_inner_sn(form.getOrg_id());
			spec.setProduct_inner_sn(form.getProduct_inner_sn());
			spec.setSpecification_inner_sn(form.getSpecification_inner_sn());
			pMapper.cleanCommonUse(spec);
		}
		DealerProductUnit unit = new DealerProductUnit();
		unit.setDealer_inner_sn(form.getOrg_id());
		unit.setProduct_inner_sn(form.getProduct_inner_sn());
		unit.setSpecification_inner_sn(form.getSpecification_inner_sn());
		unit.setUnit_inner_sn(form.getUnit_inner_sn());
		return mapper.deleteProductUnit(unit);
	}
	
	@Override
	public DealerProductUnit selectDealerProductUnitInfo(Long did, Long pid, Long psid, Long puid) {
		DealerProductUnit unit = new DealerProductUnit();
		unit.setDealer_inner_sn(did);
		unit.setProduct_inner_sn(pid);
		unit.setSpecification_inner_sn(psid);
		unit.setUnit_inner_sn(puid);
		DealerProductUnit result = mapper.selectDealerProductUnitInfo(unit);
		if(result != null) {
			getProductUnitChildren(result);
			selectParent(result);
		}
		return result;
	}

	@Override
	public List<DealerProductUnit> selectDealerProductUnitList(Long did, Long pid, Long psid) {
		DealerProductUnit unit = new DealerProductUnit();
		unit.setDealer_inner_sn(did);
		unit.setProduct_inner_sn(pid);
		unit.setSpecification_inner_sn(psid);
		return mapper.selectDealerProductUnitList(unit);
	}

	@Override
	public List<DealerProductUnit> selectDealerProductUnitListByPid(Long did, Long pid, Long psid, Long pupid) {
		DealerProductUnit unit = new DealerProductUnit();
		unit.setDealer_inner_sn(did);
		unit.setProduct_inner_sn(pid);
		unit.setSpecification_inner_sn(psid);
		unit.setParent_unit_inner_sn(pupid);
		return mapper.selectDealerProductUnitListByPid(unit);
	}

	@Override
	public List<DealerProductUnit> selectAllDealerProductUnitList(Long did, Long pid, Long psid) {
		List<DealerProductUnit> uList = selectDealerProductUnitList(did, pid, psid);
		for (DealerProductUnit productUnit : uList) {
			getProductUnitChildren(productUnit);
		}
		return uList;
	}

	private void getProductUnitChildren(DealerProductUnit unit) {
		List<DealerProductUnit> children = selectDealerProductUnitListByPid(unit.getDealer_inner_sn(), unit.getProduct_inner_sn(),
				unit.getSpecification_inner_sn(),unit.getUnit_inner_sn());
		for (DealerProductUnit child : children) {
			getProductUnitChildren(child);
		}
		unit.setChildren(children);
	}

	@Override
	public void selectParent(DealerProductUnit unit) {
		if(unit.getParent_unit_inner_sn() != null) {
			DealerProductUnit parent = selectDealerProductUnitInfo(unit.getDealer_inner_sn(), unit.getProduct_inner_sn(), unit.getSpecification_inner_sn(), unit.getParent_unit_inner_sn());
			unit.setParent(parent);
			selectParent(parent);
		}
	}

	@Override
	public BigDecimal calculateProductMinUnit(Long did, Long pid, Long psid, Long puid) {
		Map<String, Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", did);
		params.put("product_inner_sn_i", pid);
		params.put("specification_inner_sn_i", psid);
		params.put("unit_inner_sn_i", puid);
		return mapper.calculate_product_min_unit(params);
	}
}
