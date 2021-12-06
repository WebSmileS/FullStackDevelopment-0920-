package com.yrt.project.modular.surgery.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.surgery.domain.Brand;
import com.yrt.project.modular.surgery.domain.OrthopaedicKit;
import com.yrt.project.modular.surgery.domain.OrthopaedicKitDetail;
import com.yrt.project.modular.surgery.domain.OrthopaedicPackage;
import com.yrt.project.modular.surgery.domain.OrthopaedicPackageDetail;
import com.yrt.project.modular.surgery.domain.OrthopaedicPath;

@Repository
public interface SurgeryPackageMapper {

	//手术路径
	public List<OrthopaedicPath> selectOrthopaedicPathChildrenById(Long id);
	
	public List<OrthopaedicPath> selectTopLevelOrthopaedicPathList();
	
	public OrthopaedicPath selectOrthopaedicPathById(Long id);
	
	public int insertOrthopaedicPath(Map<String,Object> params);
	
	public int updateOrthopaedicPath(Map<String,Object> params);
	
	//手术包
	public OrthopaedicPackage selectOrthopaedicPackageById(Long id);
	
	public List<OrthopaedicPackage> searchOrthopaedicPackageList(Map<String,Object> params);
	
	public List<OrthopaedicPackage> selectOrthopaedicPackageByPathId(Map<String,Object> params);
	
	public List<OrthopaedicPackageDetail> selectOrthopaedicPackageDetail(Long id);
	
	public int insertOrthopaedicPackage(Map<String,Object> params);
	
	public int updateOrthopaedicPackage(Map<String,Object> params);
	
	public int deleteOrthopaedicPackage(Long id);
	
	public List<OrthopaedicPackage> selectOrthopaedicPackageExistsRef(Map<String,Object> params);
	
	public void p_add_orthopaedic_ois_product_detail(Map<String,Object> params);
	
	public int deleteOrthopaedicPackageDetail(Map<String,Object> params);
	
	//工具包
	public OrthopaedicKit selectOrthopaedicKitById(Long id);
	
	public List<OrthopaedicKit> searchOrthopaedicKitList(Map<String,Object> params);
	
	public int deleteOrthopaedicKit(Long id);
	
	public int insertOrthopaedicKit(Map<String,Object> params);
	
	public int updateOrthopaedicKit(Map<String,Object> params);
	
	public List<OrthopaedicKitDetail> selectOrthopaedicKitDetail(Long id);
	
	public void p_add_orthopaedic_kit_product_detail(Map<String,Object> params);
	
	public int deleteOrthopaedicKitDetail(Map<String,Object> params);
	
	public int insertOrthopaedicKitRefPackage(Map<String,Object> params);
	public int deleteOrthopaedicKitRefPackage(Map<String,Object> params);
	public List<OrthopaedicKit> selectOrthopaedicKitListByPackageId(Long id);
	//用于判断手术工具包是否被手术包引用
	public List<OrthopaedicKit> selectOrthopaedicKitExistsRef(Map<String,Object> params);
	
	//手术包与路径管理
	public int insertOrthopaedicPackageRef(Map<String,Object> params);
	public int deleteOrthopaedicPackageRef(Map<String,Object> params);

	//获取厂商产品列表(平台所有发布产品)
	public List<Map<String, Object>> selectVendorProductList(Map<String, Object> params);
	
	//品牌
	public List<Map<String, Object>> selectBrandList();

	public int insertBrand(Map<String,Object> params);
	
	public int deleteBrand(Long brand_inner_sn);
	
	public int updateBrand(Map<String,Object> params);

	public Brand selectBrandById(Long brand_inner_sn);
	
	public List<Brand> selectBrandByName(String name); //should be single


}
