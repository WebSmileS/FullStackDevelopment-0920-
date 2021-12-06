package com.yrt.project.modular.product.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.yrt.project.modular.product.domain.TmpProduct;
import com.yrt.project.modular.product.domain.TmpProductMDRF;
import com.yrt.project.modular.product.domain.TmpProductMDRFImage;
import com.yrt.project.modular.product.domain.TmpProductRelImage;
import com.yrt.project.modular.product.domain.TmpProductSpecification;

@Mapper
public interface TmpProductMapper {

	//待审核产品
	public List<TmpProduct> searchTmpProduct(Map<String,Object> params);
	
	public TmpProduct selectTmpProductInfo(Map<String,Object> params);
	
	public void addOrEditTmpProduct(Map<String,Object> params);
	
	public int updateTmpProductStatus(Map<String,Object> params);
	
	public int deleteTmpProduct(Map<String,Object> params);
	
	//待审核规格型号
	public TmpProductSpecification selectTmpProductSpecInfo(Map<String,Object> params);
	
	public List<TmpProductSpecification> selectTmpProductSpecList(Map<String,Object> params);
	
	public List<TmpProductSpecification> selectTmpProductSpecList1(Map<String,Object> params);
	
	public void addOrEditTmpProductSpec(Map<String,Object> params);
	
	public int updateTmpProductSpecStatus(Map<String,Object> params);
	
	public int deleteTmpProductSpec(Map<String,Object> params);
	
	//待审核注册证
	public void addOrEditTmpProductMDRF(Map<String,Object> params);
	
	public int updateTmpProductMDRFStatus(Map<String,Object> params);
	
	public int deleteTmpProductMDRF(Map<String,Object> params);
	
	public TmpProductMDRF selectTmpProductMDRFInfo(Map<String,Object> params);
	
	public List<TmpProductMDRF> searchTmpProductMDRFList(Map<String,Object> params);
	
	public int deleteTmpMDRFImage(Map<String,Object> params);
	
	public TmpProductMDRFImage selectTmpMDRFImageInfo(Map<String,Object> params);
	
	public List<TmpProductMDRFImage> selectTmpMDRFImageList(Map<String,Object> params);
	
	//图片
	public List<TmpProductRelImage> selectCertImageList(Map<String,Object> params);
	
	public TmpProductRelImage selectCertImageInfo(Map<String,Object> params);
	
	public void addOrEditTmpProductImage(Map<String,Object> params);
	
	public int updateTmpProductImageStatus(Map<String,Object> params);
	
	public int deleteTmpProductImage(Map<String,Object> params);
	
	//审核
	public List<TmpProduct> searchPendingAuditProductList(Map<String,Object> params);
	
	public List<TmpProductSpecification> selectPendingAuditProductSpecList(Map<String,Object> params);
	
	public List<TmpProductRelImage> selectPendingAuditCertImageList(Map<String,Object> params);
	
	public List<TmpProductMDRF> selectPendingAuditMDRFList(Map<String,Object> params);
	
	public TmpProduct selectProductInfo(Map<String,Object> params);
	
	public TmpProductSpecification selectProductSpecInfo(Map<String,Object> params);
	
	public TmpProductMDRF selectProductMDRFInfo(Map<String,Object> params);
	
	public List<TmpProductMDRFImage> selectMDRFImageList(Map<String,Object> params);
	
}
