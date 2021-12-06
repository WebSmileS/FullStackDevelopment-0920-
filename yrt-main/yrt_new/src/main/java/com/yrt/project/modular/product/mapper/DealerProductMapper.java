package com.yrt.project.modular.product.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.product.domain.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DealerProductMapper {

	/**
	 * 新增经销商产品
	 * @param Map<String, Object> params
	 * @return
	 */
	public void d_add_dealer_product(Map<String, Object> params);
	
	public int updateDealerProduct(Map<String, Object> params);
	
	public int updateDealerProduct4Map(Map<String, Object> params);
	
	public int deleteDealerProduct(DealerProduct product);
	
	public DealerProduct selectDealerProductInfo(DealerProduct product);
	
	public ProductDetail selectProductDetailInfo(Map<String, Object> params);
	
	public List<DealerProduct> searchAllDealerProductList(Map<String, Object> params);
	
	public List<DealerProduct> searchDealerProductList(DealerProduct product);
	
	public List<ResponseProductInfo> searchDealerProductList4Contract(Map<String, Object> params);
	
	public List<DealerProductRelImage> selectCertImageList(DealerProductRelImage info);
	public List<ProductRelImage> selectCertImageList1(Map<String, Object> params);
	
	public DealerProductRelImage selectCertImageInfo(DealerProductRelImage info);
	
	/**
	 * 新增经销商产品产品相关图片
	 * @param params
	 */
	public void d_add_dealer_product_image(Map<String, Object> params);
	
	public int deleteProductImage(Map<String, Object> params);
	
	/**
	 * 根据厂商ID和经销商产品产品ID的规格型号列表
	 * @param spec
	 * @return
	 */
	public List<DealerProductSpecification> selectDealerProductSpecListById(DealerProductSpecification spec);
	public List<ProductSpecification> selectProductSpecListById(Map<String, Object> params);
	
	public DealerProductSpecification selectDealerProductSpecInfoById(DealerProductSpecification spec);
	
	/**
	 * 新增发布产品规格型号
	 * @param params
	 */
	public void d_add_dealer_product_specification(Map<String, Object> params);
	
	public int updateDealerProductSpec(DealerProductSpecification spec);
	
	public int cleanCommonUse(DealerProductSpecification spec);
	
	public int deleteDealerProductSpec(DealerProductSpecification spec);


	List<DealerProduct> selectDealerProductAll();
}
