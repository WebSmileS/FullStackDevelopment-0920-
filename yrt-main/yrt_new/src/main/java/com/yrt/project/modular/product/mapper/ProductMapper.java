package com.yrt.project.modular.product.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.product.domain.CompleteProduct;
import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductBasic;
import com.yrt.project.modular.product.domain.ProductDetail;
import com.yrt.project.modular.product.domain.ProductMDRF;
import com.yrt.project.modular.product.domain.ProductMDRFImage;
import com.yrt.project.modular.product.domain.ProductRelImage;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.project.modular.product.domain.ResponseProductInfo4Dropdown;

@Mapper
public interface ProductMapper {

	/**
	 * 新增产品
	 * @param Map<String, Object> params
	 * @return
	 */
	public void p_add_product(Map<String, Object> params);
	
	public int insertProduct(Product product);
	
	public int updateProduct(Product product);
	
	public int updateProduct4Map(Map<String, Object> params);
	
	public int deleteProduct(Product product);
	
	public int trueDeleteProduct(Map<String, Object> params);
	
	public Product selectProductInfo(Product product);
	
	public ProductDetail selectProductDetailInfo(Map<String, Object> params);
	
	public List<Product> searchProductList(Map<String, Object> params);
	
	public List<Product> searchExistsProductList(Map<String, Object> params);

	public List<Product> selectProductAll();
	
	public List<Product> selectProductByVendorIdAndName(Map<String, Object> params);

	/**
	 * 搜索所有可以添加到合同的产品
	 * @param product
	 * @return
	 */
//	public List<Product> searchProductList4Contract(Product product);
	
	public List<ProductRelImage> selectCertImageList(ProductRelImage info);
	
	public ProductRelImage selectCertImageInfoById(ProductRelImage info);
	
	/**
	 * 新增产品相关图片
	 * @param params
	 */
	public void p_add_product_image(Map<String, Object> params);
	
	public int insertProductImage(ProductRelImage info);
	
	public int updateProductImage(ProductRelImage info);
	
	public int deleteProductImage(Map<String, Object> params);
	
	/**
	 * 根据厂商ID和已发布产品ID的规格型号列表
	 * @param spec
	 * @return
	 */
	public List<ProductSpecification> selectProductSpecListById(ProductSpecification spec);
	
	/**
	 * 新增发布产品规格型号
	 * @param params
	 */
	public void p_add_product_specification(Map<String, Object> params);
	
	public int insertProductSpec(ProductSpecification spec);
	
	public int updateProductSpec(Map<String, Object> params);
	public int updateProductSpec(ProductSpecification spec);
	
	public int updateProductSpecCommonUnit(Map<String, Object> params);
	
	public int cleanCommonUse(ProductSpecification spec);
	
	public int deleteProductSpec(ProductSpecification spec);
	
	public ProductSpecification selectProductSpecInfoById(ProductSpecification spec);
	
	public List<ProductSpecification> searchProductSpecifications(ProductSpecification spec);
	
	public int trueDeleteProductSpec(Map<String, Object> params);
	
	/**
	 * 查询产品下所有的规格型号名称
	 * @param spec
	 * @return
	 */
	public List<String> selectAllProductSpecifications(Map<String, Object> params);
	
	/**
	 * 添加产品注册证图片
	 * @param params
	 * @return
	 */
	public void p_add_product_mdrf_image(Map<String, Object> params);
	
	public int insertMDRFImage(ProductMDRFImage info);
	/**
	 * 删除产品注册证图片
	 * @param info
	 * @return
	 */
	public int deleteMDRFImage(ProductMDRFImage info);
	
	/**
	 * 获取最新的产品注册证登记表编号
	 * @param params
	 * @return mdrf_inner_sn, register_sn
	 */
	public List<Map<String, Object>> selectNewestRegister_sn(Map<String, Object> params);
	
	/**
	 * 查询产品注册证列表
	 * @param info
	 * @return
	 */
	public List<ProductMDRFImage> selectMDRFImageList(ProductMDRFImage info);
	
	public ProductMDRFImage selectMDRFImageInfo(ProductMDRFImage info);
	
	public void p_add_product_mdrf(Map<String, Object> params);
	
	public int insertProductMDRF(ProductMDRF mdrf);
	
	public int updateProductMDRF(ProductMDRF mdrf);
	
	public int updateProductMDRF4Map(Map<String, Object> params);
	
	public int deleteProductMDRF(Map<String, Object> params);
	
	public List<ProductMDRF> searchProductMDRFList(Map<String, Object> params);
	public List<ProductMDRF> searchProductMDRFList1(Map<String, Object> params);
	
	public ProductMDRF selectProductMDRF(ProductMDRF mdrf);
	
	//手机专用
	public List<ProductBasic> searchProductList4App(Map<String, Object> params);
	
	//获取厂商产品列表(平台所有发布产品)
	public List<ResponseProductInfo> selectVendorProductList(Map<String,Object> params);
	public List<ResponseProductInfo4Dropdown> selectVendorProductDropdown(Map<String,Object> params);
	
	//产品内容检测
	public int checkProudctExist(Map<String,Object> params);

	public int checkProudctSpecExist(Map<String,Object> params);

	public int checkProudctMDRFExist(Map<String,Object> params);
	
	/**
	 * 查询完整产品信息(可用厂商、可用产品、可用规格型号、可用最小单位)
	 * @param params
	 * @return
	 */
	public List<CompleteProduct> selectCompleteProductInfo(Map<String,Object> params);
}
