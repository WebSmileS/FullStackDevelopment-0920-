package com.yrt.project.modular.category.service;

import java.util.List;

import com.yrt.project.modular.category.domain.ProductType;

/**
 * 自定义分类服务
 * @author Runner
 *
 */
public interface IProductTypeService {
	
	/**
	 * 获取顶级自定义分类
	 * @return
	 */
	public List<ProductType> selectTopLevelProductType(boolean all);
	/**
	 * 根据分类ID查询分类信息
	 * @param id
	 * @return
	 */
	public ProductType selectProductType(Long id);
	
	/**
	 * 根据分类ID查询下级分类信息列表
	 * @param id
	 * @return
	 */
	public List<ProductType> selectProductTypeChildrenList(Long id);
	
	/**
	 * 新增自定义分类信息
	 * @param productType
	 */
	public ProductType insertProductType(ProductType productType);
	
	/**
	 * 更新自定义分类列表
	 * @param productType
	 */
	public int updateProductType(Long id, ProductType productType);
	
	/**
	 * 删除自定义分类列表
	 * @param id
	 */
	public int deleteProductType(Long id);
	
	/**
	 * 根据id查询所有上级信息
	 * @param type
	 * @param parent_type_inner_sn
	 */
	public void selectParent(ProductType type, Long parent_type_inner_sn);
	
	/**
	 * 获取全部产品自定义分类
	 * @return
	 */
	public List<ProductType> selectAllProductType();
}
