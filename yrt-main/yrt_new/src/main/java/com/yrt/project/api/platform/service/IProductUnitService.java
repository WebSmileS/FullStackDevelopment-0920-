package com.yrt.project.api.platform.service;

import java.math.BigDecimal;
import java.util.List;

import com.yrt.project.api.business.dealer.vo.dealerproduct.UnitIDForm;
import com.yrt.project.api.platform.vo.product.AddProductUnitForm;
import com.yrt.project.api.platform.vo.product.EditProductUnitForm;
import com.yrt.project.modular.product.domain.ProductUnit;

public interface IProductUnitService {

	/**
	 * 添加产品单位
	 * @param form
	 * @return
	 */
	public ProductUnit insertProductUnit(AddProductUnitForm form);
	
	/**
	 * 编辑产品单位
	 * @param vid
	 * @param pid
	 * @param psid
	 * @param puid
	 * @param form
	 * @return
	 */
	public int updateProductUnit(Long vid, Long pid, Long psid, Long puid, EditProductUnitForm form);
	
	/**
	 * 根据产品单位ID查询单位信息
	 * @param vid
	 * @param pid
	 * @param psid
	 * @param puid
	 * @return
	 */
	public ProductUnit selectProductUnitInfo(Long vid, Long pid, Long psid, Long puid);
	
	/**
	 * 根据产品ID查询所有产品单位
	 * @param vid
	 * @param pid
	 * @param psid
	 * @return
	 */
	public List<ProductUnit> selectProductUnitList(Long vid, Long pid, Long psid);
	
	/**
	 * 根据产品ID和产品单位上级ID查询所有下级产品单位
	 * @param vid
	 * @param pid
	 * @param psid
	 * @param pupid
	 * @return
	 */
	public List<ProductUnit> selectProductUnitListByPid(Long vid, Long pid, Long psid, Long pupid);
	
	/**
	 * 根据产品ID查询所有产品单位(包括所有下级单位)
	 * @param vid
	 * @param pid
	 * @param psid
	 * @return
	 */
	public List<ProductUnit> selectAllProductUnitList(Long vid, Long pid, Long psid);
	
	/**
	 * 获取全部上级单位
	 * @param unit
	 */
	public void selectParent(ProductUnit unit);
	
	public int delProductUnit(UnitIDForm form);
	
	public BigDecimal calculateProductMinUnit(Long vid, Long pid, Long psid, Long puid);
}
