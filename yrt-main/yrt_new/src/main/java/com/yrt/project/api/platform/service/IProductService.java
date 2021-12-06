package com.yrt.project.api.platform.service;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.platform.vo.product.*;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.product.domain.*;

public interface IProductService {

	public static int CERT_TYPE_OTHER = 0;
	public static int CERT_TYPE_PRODUCT = 1;
	public static int CERT_TYPE_LICENSE = 2;
	public static int CERT_TYPE_LICENSE_TABLE = 3;
	public static int CERT_TYPE_QMS = 4;
	
	/**
	 * 添加商品
	 * @param goods
	 * @return
	 */
	public Product insertProduct (AddProductForm product) throws DBException, DuplicateException;
	
	/**
	 * 修改商品信息
	 * @param id
	 * @param product
	 * @return
	 */
	public int updateProduct(Long id, Long vid, EditProductForm product);
	
	/**
	 * 删除商品信息
	 * @param id
	 * @return
	 */
	public int deleteProduct(Long id, Long vid);
	
	/**
	 * 批量导入商品
	 * @return
	 */
	public int importProduct();
	
	/**
	 * 新增产品相关图片
	 * @param form
	 * @return
	 */
	public ProductRelImage insertProductRelImage(AddProductImageForm form);
	
	/**
	 * 删除产品图片
	 * @param vid
	 * @param pid
	 * @param certType
	 * @param id
	 * @return
	 */
	public int delProductImage(Long vid, Long pid, Integer certType, Integer id);
	
	/**
	 * 根据商品ID和图片类型获取相关图片
	 * @param id
	 * @param certType
	 * @return
	 */
	public List<ProductRelImage> selectCertImageList(Long vid, Long pid, Integer certType);
	
	/**
	 * 按照产品编号查询未删除的正常商品信息
	 */
	public Product selectProductInfo(Long id, Long vid);
	
	public ProductDetail getProductDetailInfo(Long pid, Long vid);
	
	/**
	 * 按条件搜索未发布的商品列表
	 * @param product
	 * @return
	 */
	 public List<Product> searchUnPublicProductList(SearchProductForm form);
	 
	 /**
	 * 按条件搜索已发布的商品列表
	 * @param product
	 * @return
	 */
	 public List<Product> searchPublicedProductList(SearchProductForm form);
	 
	
	/**
	 * 根据搜索条件和商品类型(平台、厂商自建、经销商自建)获取商品(未发布)列表
	 * @param searchGoodsForm
	 * @param source
	 * @return
	 */
	public List<Product> searchProductListBySource(SearchProductForm form,int source);
	
	/**
	 * 搜索所有可以添加到合同的厂商产品
	 * @param searchGoodsForm
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public TableDataInfo<ResponseProductInfo> searchProductList4Contract(SearchProductForm form,int systemType, Long oid, Integer pageNum, Integer pageSize);
	
	/**
	 * 发布厂商产品
	 * @param id
	 * @param vid
	 * @return
	 */
	public int publishProduct(Long id, Long vid);
	
	public int publishProduct(@Validated List<PublicProductIDSForm> list);
	
	/**
	 * 发布经销商自建产品为平台产品
	 * @param form
	 * @return
	 */
	public Product publishDealerProduct(PublishDealerProductForm form);
	
	/**
	 * 根据厂商ID和已发布产品ID的规格型号列表
	 * @param vid
	 * @param pid
	 * @return
	 */
	public List<ProductSpecification> selectProductSpecListById(Long vid, Long pid);
	
	/**
	 * 根据厂商ID和已发布产品ID新增规格型号
	 * @param vid
	 * @param pid
	 * @return
	 */
	public ProductSpecification insertProductSpec(AddProductSpecForm form) throws DBException, DuplicateException;

	/**
	 * 根据厂商ID、已发布产品ID和规格型号ID修改规格型号
	 * @param vid
	 * @param pid
	 * @return
	 */
	public int updateProductSpec(EditProductSpecForm form);
	
	/**
	 * 根据厂商ID和已发布产品ID删除规格型号
	 * @param vid
	 * @param pid
	 * @return
	 */
	public int deleteProductSpec(Long vid, Long pid, Long id);
	
	/**
	 * 添加产品医疗器械注册登记表
	 * @param vid
	 * @param pid
	 * @param form
	 * @return
	 */
	public ProductMDRF insertProductMDRF(Long vid, Long pid,AddProductMDRFForm form);
	
	/**
	 * 更新产品医疗器械注册登记表
	 * @param vid
	 * @param pid
	 * @param form
	 * @return
	 */
	public ProductMDRF updateProductMDRF(Long vid, Long pid, Long mid, EditProductMDRFForm form);
	
	/**
	 * 删除产品医疗器械注册登记表
	 * @param vid
	 * @param pid
	 * @return
	 */
	public int delProductMDRF(Long vid, Long pid, Long mid);
	
	/**
	 * 查看产品医疗器械注册登记表
	 * @param vid
	 * @param pid
	 * @return
	 */
	public ViewProductMDRFForm selectProductMDRF(Long vid, Long pid, Long mid);
	
	public ViewProductMDRFForm getNewestProductMDRF(Long vid, Long pid);
	
	public List<ProductMDRF> searchProductMDRFList(Long vid, Long pid, Long mid);
	
	public List<ProductMDRF> searchProductMDRFList(SearchProductMDRFForm form);
	
	public int copyMDRF(Long vid, Long pid, Long mid, List<Long> list);
	
	public List<ProductMDRFImage> selectProductMDRFImageList(Long vid, Long pid, Long mid);
	
	public Long addProductMDRFImage(AddMDRFImageForm form);
	
	public int delProductMDRFImage(Long vid, Long pid, Long mid, Long miid);
	
	/*
	 * public ProductMDRFImage insertProductMDRFImage(AddProductMDRFImageForm form);
	 * 
	 * public int deleteProductMDRFImage(Long vid, Long pid, Long mid, Long miid);
	 * 
	 * public List<ProductMDRFImage> selectMDRFImageList(Long vid, Long pid, Long
	 * mid);
	 */
	
//	public boolean syncProductSpec(Long vid, Long pid);

	//获取厂商产品列表(平台所有发布产品)
	public TableDataInfo<ResponseProductInfo> selectVendorProductList(SearchProductForm form, Integer pageNum, Integer pageSize);
	public List<ResponseProductInfo4Dropdown> getVendorProductDropdown(Long vid, String name);
	
	//经销商编辑产品
	public List<TmpProduct> searchPendingAuditProductList(SearchPendingAuditForm form);
	public TmpProduct selectPendingAuditProductOtherInfo(IdsForm form);
	public List<MatchBean> selectPendingAuditProductDetailInfo(PendingAuditProductForm form);
	
	public int audit(AuditProductForm form);

	//产品内容检测
	public int checkProudctExist(CheckForm form);

	public int checkProudctSpecExist(CheckForm form);

	public int checkProudctMDRFExist(CheckForm form);

	/**
	 * 同步平台产品信息与所有仓库的库存产品信息
	 */
	public void sync(Long vid, Long pid);
}
