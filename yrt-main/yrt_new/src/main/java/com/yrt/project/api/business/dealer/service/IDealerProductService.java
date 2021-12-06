package com.yrt.project.api.business.dealer.service;

import java.util.List;

import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductImageForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.AddDealerProductSpecForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.EditDealerProductForm;
import com.yrt.project.api.business.dealer.vo.dealerproduct.EditDealerProductSpecForm;
import com.yrt.project.api.platform.vo.product.SearchProductForm;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.product.domain.DealerProduct;
import com.yrt.project.modular.product.domain.DealerProductRelImage;
import com.yrt.project.modular.product.domain.DealerProductSpecification;
import com.yrt.project.modular.product.domain.ProductDetail;

public interface IDealerProductService {

	/**
	 * 添加商品
	 * @param goods
	 * @return
	 */
	public DealerProduct insertDealerProduct (AddDealerProductForm form);
	
	/**
	 * 修改商品信息
	 * @param id
	 * @param product
	 * @return
	 */
	public int updateDealerProduct(Long id, Long did, EditDealerProductForm form);
	
	/**
	 * 删除商品信息
	 * @param id
	 * @return
	 */
	public int deleteDealerProduct(Long id, Long did);
	
	/**
	 * 批量导入商品
	 * @return
	 */
	public int importDealerProduct();
	
	/**
	 * 新增产品相关图片
	 * @param form
	 * @return
	 */
	public DealerProductRelImage insertDealerProductRelImage(AddDealerProductImageForm form);
	
	/**
	 * 获取产品图片信息
	 * @param did
	 * @param pid
	 * @param piid
	 * @param certType
	 * @return
	 */
	public DealerProductRelImage selectCertImageInfo(Long did, Long pid, Long piid, int certType);
	
	public int delProductImage(Long did, Long pid, int certType, int id);
	
	/**
	 * 根据商品ID和图片类型获取相关图片
	 * @param id
	 * @param certType
	 * @return
	 */
	public List<DealerProductRelImage> selectCertImageList(Long did, Long pid, int certType);
	
	/**
	 * 按照产品编号查询未删除的正常商品信息
	 */
	public DealerProduct selectDealerProductInfo(Long pid, Long did);
	
	public ProductDetail getProductDetailInfo(Long pid, Long vid);
	
	/**
	 * 获取所有经销商的商品列表
	 * @param form
	 * @return
	 */
	 public List<DealerProduct> searchAllDealerProductList(SearchProductForm form);
	 
	 /**
	 * 按条件搜索经销商的商品列表
	 * @param form
	 * @return
	 */
	 public List<DealerProduct> searchDealerProductList(SearchProductForm form);
	 
	 /**
		 * 搜索所有可以添加到合同的经销商自建产品
		 * @param searchGoodsForm
		 * @param systemType
		 * @param oid
		 * @return
		 */
	 public List<ResponseProductInfo> searchDealerProductList4Contract(SearchProductForm form, int systemType, Long oid);
	 
	/**
	 * 根据厂商ID和产品ID的规格型号列表
	 * @param vid
	 * @param pid
	 * @return
	 */
	public List<DealerProductSpecification> selectDealerProductSpecListById(Long did, Long pid);
	
	/**
	 * 根据厂商ID和产品ID新增规格型号
	 * @param vid
	 * @param pid
	 * @return
	 */
	public DealerProductSpecification insertDealerProductSpec(AddDealerProductSpecForm form);

	/**
	 * 根据厂商ID、产品ID和规格型号ID修改规格型号
	 * @param vid
	 * @param pid
	 * @return
	 */
	public int updateDealerProductSpec(EditDealerProductSpecForm form);
	
	/**
	 * 根据厂商ID和产品ID删除规格型号
	 * @param vid
	 * @param pid
	 * @return
	 */
	public int deleteDealerProductSpec(Long did, Long pid, Long psid);
	
	/**
	 * 获取产品规格型号信息
	 * @param oid
	 * @param pid
	 * @param psid
	 * @return
	 */
	public DealerProductSpecification selectDealerProductSpecInfoById(Long oid, Long pid, Long psid);
}
