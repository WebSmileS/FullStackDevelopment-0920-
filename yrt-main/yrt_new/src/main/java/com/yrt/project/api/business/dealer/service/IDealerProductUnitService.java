package com.yrt.project.api.business.dealer.service;

import java.math.BigDecimal;
import java.util.List;

import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductUnitForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.UnitIDForm;
import com.yrt.project.api.platform.vo.product.EditProductUnitForm;
import com.yrt.project.modular.product.domain.DealerProductUnit;

public interface IDealerProductUnitService {

	/**
	 * 添加产品单位
	 * @param form
	 * @return
	 */
	public DealerProductUnit insertDealerProductUnit(AddDealerProductUnitForm form);
	
	/**
	 * 编辑产品单位
	 * @param vid
	 * @param pid
	 * @param psid
	 * @param puid
	 * @param form
	 * @return
	 */
	public int updateDealerProductUnit(Long did, Long pid, Long psid, Long puid, EditProductUnitForm form);
	
	/**
	 * 根据产品单位ID查询单位信息
	 * @param vid
	 * @param pid
	 * @param psid
	 * @param puid
	 * @return
	 */
	public DealerProductUnit selectDealerProductUnitInfo(Long did, Long pid, Long psid, Long puid);
	
	/**
	 * 根据产品ID查询所有产品单位
	 * @param vid
	 * @param pid
	 * @param psid
	 * @return
	 */
	public List<DealerProductUnit> selectDealerProductUnitList(Long did, Long pid, Long psid);
	
	/**
	 * 根据产品ID和产品单位上级ID查询所有下级产品单位
	 * @param vid
	 * @param pid
	 * @param psid
	 * @param pupid
	 * @return
	 */
	public List<DealerProductUnit> selectDealerProductUnitListByPid(Long did, Long pid, Long psid, Long pupid);
	
	/**
	 * 根据产品ID查询所有产品单位(包括所有下级单位)
	 * @param did
	 * @param pid
	 * @param psid
	 * @return
	 */
	public List<DealerProductUnit> selectAllDealerProductUnitList(Long did, Long pid, Long psid);

	public void selectParent(DealerProductUnit unit);

	public int delDealerProductUnit(UnitIDForm form);

	public BigDecimal calculateProductMinUnit(Long did, Long pid, Long psid, Long puid);
}
