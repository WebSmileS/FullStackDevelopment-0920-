package com.yrt.project.modular.category.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.category.domain.Code68;
import com.yrt.project.modular.category.domain.ProductType;

/**
 * 医疗器械68分类与自定义分类  数据层
 * @author Runner
 *
 */
@Repository
public interface CategoryMapper {

	public Code68 selectCode68ById(Long id);
	
	public List<Code68> selectCode68ChildrenById(Long id);
	
	public List<Code68> selectCode68Mogami();
	
	public List<Code68> selectTopLevelCode68List();
	
	public List<Code68> selectCode68ChildrenByPid(Long id);
	
	public List<ProductType> selectTopLevelProductType(Map<String,Object> params);
	
	public ProductType selectProductTypeInfo(Map<String,Object> params);
	
	public List<ProductType> selectProductTypeChildrenList(Long id);
	
	public int insertProductType(ProductType productType);
	
	public int updateProductType(ProductType productType);
	
	public int deleteProductType(Long id);
}
