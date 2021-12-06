package com.yrt.project.api.platform.surgery.service;

import java.util.List;
import java.util.Map;

import com.yrt.project.api.platform.surgery.vo.AddBrand;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicPackageForm;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicPackageToPathForm;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicPathForm;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicKitForm;
import com.yrt.project.api.platform.surgery.vo.EditBrand;
import com.yrt.project.api.platform.surgery.vo.EditOrthopaedicKitForm;
import com.yrt.project.api.platform.surgery.vo.EditOrthopaedicPackageForm;
import com.yrt.project.api.platform.surgery.vo.EditOrthopaedicPathForm;
import com.yrt.project.api.platform.surgery.vo.SurgerySearchProductForm;
import com.yrt.project.api.platform.surgery.vo.SearchSurgeryKitForm;
import com.yrt.project.api.platform.surgery.vo.SearchSurgeryPackageForm;
import com.yrt.project.modular.surgery.domain.Brand;
import com.yrt.project.modular.surgery.domain.OrthopaedicPackage;
import com.yrt.project.modular.surgery.domain.OrthopaedicPath;
import com.yrt.project.modular.surgery.domain.OrthopaedicKit;

public interface ISurgeryService {

	//骨科路径
	public OrthopaedicPath addOrthopaedicPath(AddOrthopaedicPathForm form);

	public int editOrthopaedicPath(EditOrthopaedicPathForm form);

	public int delOrthopaedicPath(Long pid);

	public List<OrthopaedicPath> selectTopLevelOrthopaedicPathList();

	public List<OrthopaedicPath> selectOrthopaedicPathChildrenById(Long id);

	public OrthopaedicPath selectOrthopaedicPathById(Long id, Integer type_inner_sn,Long brand_inner_sn);
	
	public OrthopaedicPath addOrthopaedicPackageToPath(AddOrthopaedicPackageToPathForm form);

	public int delOrthopaedicPackageToPath(AddOrthopaedicPackageToPathForm form);
	
	//骨科手术包
	public OrthopaedicPackage addOrthopaedicPackage(AddOrthopaedicPackageForm form);
	
	public int editOrthopaedicPackage(EditOrthopaedicPackageForm form);
	
	public int delOrthopaedicPackage(Long pid);
	
	public int delOrthopaedicPackageDetail(Long pid, Long did);
	
	public int delOrthopaedicPackageKit(Long pid, Long kid);
	
	public OrthopaedicKit addOrthopaedicPackageKit(Long kid);

	public List<OrthopaedicPackage> searchOrthopaedicPackageList(SearchSurgeryPackageForm form);

	public OrthopaedicPackage selectOrthopaedicPackageInfoById(Long pid);

	public List<Map<String,Object>> selectVendorProductList(SurgerySearchProductForm form);
	
	//品牌
	public List<Map<String, Object>> searchBrandList();

	public Brand addBrand(AddBrand form);
	
	public int delBrand(Long brand_inner_sn);
	
	public int editBrand(EditBrand form);

	public Brand searchBrandById(Long brand_inner_sn);

	//骨科工具包
	public OrthopaedicKit addOrthopaedicKit(AddOrthopaedicKitForm form);

	public int editOrthopaedicKit(EditOrthopaedicKitForm form);

	public int delOrthopaedicKit(Long kid);

	public int delOrthopaedicKitDetail(Long kid, Long did);

	public List<OrthopaedicKit> searchOrthopaedicKitList(SearchSurgeryKitForm form);

	public OrthopaedicKit selectOrthopaedicKitInfoById(Long kid);

}
