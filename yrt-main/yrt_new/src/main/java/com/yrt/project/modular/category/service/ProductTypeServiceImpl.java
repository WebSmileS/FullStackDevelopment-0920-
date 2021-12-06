package com.yrt.project.modular.category.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.exception.DBException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.modular.category.domain.ProductType;
import com.yrt.project.modular.category.mapper.CategoryMapper;

@Service
public class ProductTypeServiceImpl implements IProductTypeService {

	@Autowired
	private CategoryMapper mapper;
	
	@Override
	public ProductType selectProductType(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("type_inner_sn", id);
		return mapper.selectProductTypeInfo(params);
	}

	@Override
	public List<ProductType> selectProductTypeChildrenList(Long id) {
		return mapper.selectProductTypeChildrenList(id);
	}

	@Override
	@Transactional
	public ProductType insertProductType(ProductType productType) {
		int result = 1;
		if(StringUtils.isNull(productType.getParent_type_inner_sn()) 
			|| productType.getParent_type_inner_sn() == 0L) {
			productType.setParent_type_inner_sn(null);
		}
		result = result & mapper.insertProductType(productType);
		if(result>0) {
			return selectProductType(productType.getType_inner_sn());
		} else {
			throw new DBException(MessageUtils.message("db.add.fail","产品分类","产品新增分类失败"));
		}
	}

	@Override
	@Transactional
	public int updateProductType(Long id, ProductType productType) {
		productType.setType_inner_sn(id);
		return mapper.updateProductType(productType);
	}

	@Override
	@Transactional
	public int deleteProductType(Long id) {
		//先要删除下级分类
		List<ProductType> children = selectProductTypeChildrenList(id);
		for (ProductType productType : children) {
			delProductTypeChild(productType.getType_inner_sn());
		}
		return mapper.deleteProductType(id);
	}

	private void delProductTypeChild(Long id) {
		List<ProductType> children = selectProductTypeChildrenList(id);
		for (ProductType productType : children) {
			delProductTypeChild(productType.getType_inner_sn());
		}
		mapper.deleteProductType(id);
	}

	@Override
	public List<ProductType> selectTopLevelProductType(boolean all) {
		Map<String,Object> params = new HashMap<>();
		if(!all) {
			params.put("notall", 1);
		}
		return mapper.selectTopLevelProductType(params);
	}

	@Override
	public void selectParent(ProductType type, Long pid) {
		if(pid != null) {
			ProductType parent = selectProductType(pid);
			type.setParent(parent);
			selectParent(parent, parent.getParent_type_inner_sn());
		}
	}

	@Override
	public List<ProductType> selectAllProductType() {
		List<ProductType> list = selectTopLevelProductType(false);
		for (ProductType type : list) {
			setChildren(type);
		}
		return list;
	}
	
	private void setChildren(ProductType type) {
		Long pid = type.getType_inner_sn();
		List<ProductType> children = selectProductTypeChildrenList(pid);
		for (ProductType type1 : children) {
			if(type1.getIs_leaf() == 0)
				setChildren(type1);
		}
		type.setChildren(children);
	}

}
