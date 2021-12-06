package com.yrt.project.api.business.dealer.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.exception.BusinessException;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.business.dealer.service.IDealerProductTypeService;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductTypeForm;
import com.yrt.project.api.common.vo.EditProductType;
import com.yrt.project.modular.product.domain.DealerProductType;
import com.yrt.project.modular.product.mapper.DealerProductTypeMapper;

@Service
public class DealerProductTypeServiceImpl implements IDealerProductTypeService {

	@Autowired
	private DealerProductTypeMapper mapper;
	
	@Override
	public List<DealerProductType> selectTopLevelProductType(Long did) {
		return mapper.selectTopLevelProductType(did);
	}

	@Override
	public DealerProductType selectProductType(Long oid, Long tid) {
		DealerProductType type = new DealerProductType();
		type.setDealer_inner_sn(oid);
		type.setType_inner_sn(tid);
		return mapper.selectProductType(type);
	}

	@Override
	public List<DealerProductType> selectProductTypeChildrenList(Long oid, Long tid) {
		DealerProductType type = new DealerProductType();
		type.setDealer_inner_sn(oid);
		type.setParent_type_inner_sn(tid);
		return mapper.selectProductTypeChildrenList(type);
	}

	@Override
	public DealerProductType insertDealerProductType(Long oid, AddDealerProductTypeForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("dealer_inner_sn_i", oid);
		if(StringUtils.isNull(form.getParent_type_inner_sn()) 
				|| form.getParent_type_inner_sn() == 0L) {
			params.put("parent_type_inner_sn_i", null);
		}else {
			params.put("parent_type_inner_sn_i", form.getParent_type_inner_sn());
		}
		params.put("type_i", form.getType());
		params.put("description_i", form.getDescription());
		mapper.d_add_dealer_product_type(params);
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			Long tid = Long.valueOf(params.get("type_inner_sn_o").toString());
			return selectProductType(oid, tid);
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","经销商产品类别",params.get("message_string_o")));
		}
	}

	@Override
	public int updateProductType(Long oid, Long tid, EditProductType pType) {
		if(!pType.hasUpdate()) {
			throw new BusinessException(MessageUtils.message("update.param.not.null"));
		}
		DealerProductType type = new DealerProductType();
		BeanUtils.copyProperties(pType, type);
		type.setDealer_inner_sn(oid);
		type.setType_inner_sn(tid);
		return mapper.updateProductType(type);
	}

	@Override
	public int deleteProductType(Long oid, Long tid) {
		DealerProductType type = new DealerProductType();
		type.setDealer_inner_sn(oid);
		type.setType_inner_sn(tid);
		return mapper.deleteProductType(type);
	}

	@Override
	public void selectParent(DealerProductType type, Long oid, Long ptid) {
		if(ptid != null) {
			DealerProductType parent = selectProductType(oid, ptid);
			type.setParent(parent);
			selectParent(parent, parent.getDealer_inner_sn(), parent.getParent_type_inner_sn());
		}
	}

	@Override
	public List<DealerProductType> selectAllProductType(Long oid) {
		List<DealerProductType> list = selectTopLevelProductType(oid);
		for (DealerProductType type : list) {
			setChildren(type);
		}
		return list;
	}

	private void setChildren(DealerProductType type) {
		if (Integer.valueOf(1).equals(type.getIs_leaf())) {
			return;
		}
		Long oid = type.getDealer_inner_sn();
		Long pid = type.getType_inner_sn();
		List<DealerProductType> children = selectProductTypeChildrenList(oid, pid);
		for (DealerProductType type1 : children) {
			setChildren(type1);
		}
		type.setChildren(children);
	}
}
