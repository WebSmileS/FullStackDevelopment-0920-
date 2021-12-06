package com.yrt.project.api.platform.surgery.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.exception.RefException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.PinyinUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.platform.surgery.service.ISurgeryService;
import com.yrt.project.api.platform.surgery.vo.AddBrand;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicKitForm;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicPackageForm;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicPackageToPathForm;
import com.yrt.project.api.platform.surgery.vo.AddOrthopaedicPathForm;
import com.yrt.project.api.platform.surgery.vo.EditBrand;
import com.yrt.project.api.platform.surgery.vo.EditOrthopaedicKitForm;
import com.yrt.project.api.platform.surgery.vo.EditOrthopaedicPackageForm;
import com.yrt.project.api.platform.surgery.vo.EditOrthopaedicPathForm;
import com.yrt.project.api.platform.surgery.vo.KitIDs;
import com.yrt.project.api.platform.surgery.vo.OrthopaedicDetailForm;
import com.yrt.project.api.platform.surgery.vo.SearchSurgeryKitForm;
import com.yrt.project.api.platform.surgery.vo.SearchSurgeryPackageForm;
import com.yrt.project.api.platform.surgery.vo.SurgerySearchProductForm;
import com.yrt.project.modular.surgery.domain.Brand;
import com.yrt.project.modular.surgery.domain.OrthopaedicKit;
import com.yrt.project.modular.surgery.domain.OrthopaedicKitDetail;
import com.yrt.project.modular.surgery.domain.OrthopaedicPackage;
import com.yrt.project.modular.surgery.domain.OrthopaedicPackageDetail;
import com.yrt.project.modular.surgery.domain.OrthopaedicPath;
import com.yrt.project.modular.surgery.mapper.SurgeryPackageMapper;

@Service
public class SurgeryServiceImpl implements ISurgeryService {
	private static final int MAX_KEYWORDS_LEN = 100;

	@Autowired
	private SurgeryPackageMapper mapper;
	
	//骨科路径
	@Override
	@Transactional
	public OrthopaedicPath addOrthopaedicPath(AddOrthopaedicPathForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("parent_op_inner_sn", form.getParent_op_inner_sn());
		params.put("name", form.getName());
		if(mapper.insertOrthopaedicPath(params) > 0){
			Long op_inner_sn = Long.valueOf(params.get("op_inner_sn").toString());
			return mapper.selectOrthopaedicPathById(op_inner_sn);
		}else {
			throw new DBException(MessageUtils.message("db.add.fail","骨科路径"));
		}
	}

	@Override
	@Transactional
	public int editOrthopaedicPath(EditOrthopaedicPathForm form) {
		Map<String,Object> params = form.getInfo();
		return mapper.updateOrthopaedicPath(params);
	}

	@Override
	@Transactional
	public int delOrthopaedicPath(Long pid) {
		Map<String,Object> params = new HashMap<>();
		params.put("op_inner_sn", pid);
		//
		mapper.deleteOrthopaedicPackageRef(params); // delete by op
		params.put("is_delete", 1);
		return mapper.updateOrthopaedicPath(params);
	}

	@Override
	public List<OrthopaedicPath> selectTopLevelOrthopaedicPathList() {
		return mapper.selectTopLevelOrthopaedicPathList();
	}

	@Override
	public List<OrthopaedicPath> selectOrthopaedicPathChildrenById(Long id) {
		return mapper.selectOrthopaedicPathChildrenById(id);
	}

	@Override
	public OrthopaedicPath selectOrthopaedicPathById(Long id, Integer type_inner_sn, Long brand_inner_sn) {
		OrthopaedicPath info = mapper.selectOrthopaedicPathById(id);
		if(info != null) {
			Map<String,Object> params = new HashMap<>();
			params.put("id", id);
			params.put("type_inner_sn", type_inner_sn);
			params.put("brand_inner_sn", brand_inner_sn);
			List<OrthopaedicPackage> details = mapper.selectOrthopaedicPackageByPathId(params);
			info.setPackages(details);
		}
		if(info != null) {
			if(info.getParent_op_inner_sn() != null) {
				OrthopaedicPath parent = getParent(info);
				info.setTopParent(parent);
			}else {
				info.setTopParent(null);
			}
		}
		return info;
	}
	
	private OrthopaedicPath getParent(OrthopaedicPath info) {
		if(info.getParent_op_inner_sn() != null) {
			OrthopaedicPath parent = mapper.selectOrthopaedicPathById(info.getParent_op_inner_sn());
			return getParent(parent);
		}else {
			return info;
		}
	}
	
	@Override
	@Transactional
	public OrthopaedicPath addOrthopaedicPackageToPath(AddOrthopaedicPackageToPathForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("op_inner_sn", form.getOp_inner_sn());
		String[] packages = form.getPackages().split(",");
		for (String pid : packages) {
			params.put("ois_inner_sn", pid);
			try {
				mapper.insertOrthopaedicPackageRef(params);
			} catch (DuplicateKeyException e) {
				// path-package pair exists already:
				// just ignore it.
			}
		}
		return selectOrthopaedicPathById(form.getOp_inner_sn(), null, null);
	}
	
	@Override
	@Transactional
	public int delOrthopaedicPackageToPath(AddOrthopaedicPackageToPathForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("op_inner_sn", form.getOp_inner_sn());
		String[] packages = form.getPackages().split(",");
		for (String pid : packages) {
			params.put("ois_inner_sn", pid);
			mapper.deleteOrthopaedicPackageRef(params);
		}
		return 1;
	}

	//骨科手术包
	@Override
	@Transactional
	public OrthopaedicPackage addOrthopaedicPackage(AddOrthopaedicPackageForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("brand_inner_sn", form.getBrand_inner_sn());
		params.put("ois_sn", form.getOis_sn());
		params.put("name", form.getName());
		params.put("description", form.getDescription());
		String keywords =  form.getKeywords(); // add from dealer, UI has keywords field
		if(StringUtils.isEmpty(keywords)) {
			keywords = PinyinUtils.getPy(form.getName());
		}
		params.put("keywords", Utils.maxLength(keywords, MAX_KEYWORDS_LEN));
		
		try {
			if(mapper.insertOrthopaedicPackage(params) > 0){
				Long ois_inner_sn = Long.valueOf(params.get("ois_inner_sn").toString());
				List<OrthopaedicDetailForm> details = form.getDetails();
				for (OrthopaedicDetailForm detail : details) {
					Map<String,Object> params1 = new HashMap<>();
					params1.put("ois_inner_sn_i", ois_inner_sn);
					params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
					params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
					params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
					params1.put("quantity_i", detail.getQuantity());
					params1.put("sort_number_i", detail.getSort_number());
					params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
					params1.put("product_name_i", detail.getProduct_name());
					params1.put("specification_i", detail.getSpecification());
					params1.put("unit_i", detail.getUnit_name());
					mapper.p_add_orthopaedic_ois_product_detail(params1);
				}
				List<KitIDs> kits = form.getKits();
				for (KitIDs ids : kits) {
					Map<String,Object> params1 = new HashMap<>();
					params1.put("ois_inner_sn", ois_inner_sn);
					params1.put("kit_inner_sn", ids.getKit_inner_sn());
					params1.put("sort_number", ids.getSort_number());
					mapper.insertOrthopaedicKitRefPackage(params1);
				}
				return selectOrthopaedicPackageInfoById(ois_inner_sn);
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","骨科手术包"));
			}
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","骨科手术包","骨科手术包名称重复"));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","骨科手术包",e.getMessage()));
			}
		}
	}

	@Override
	@Transactional
	public int editOrthopaedicPackage(EditOrthopaedicPackageForm form) {
		int result = 1;
		Map<String,Object> params = form.getInfo();
		if (form.hasUpdate()) {
			if (params.containsKey("name")) {
				params.put("keywords", Utils.maxLength(PinyinUtils.getPy(form.getName()), MAX_KEYWORDS_LEN));
			}
			try {
				result = result & mapper.updateOrthopaedicPackage(params);
			} catch(Exception e) {
				if(e instanceof DuplicateKeyException) {
					throw new DuplicateException(MessageUtils.message("db.update.fail","骨科手术包","骨科手术包名称重复"));
				}else {
					throw new DBException(MessageUtils.message("db.update.fail","骨科手术包",e.getMessage()));
				}
			}
		}
		if(result > 0) {
			mapper.deleteOrthopaedicPackageDetail(params);
			List<OrthopaedicDetailForm> details = form.getDetails();
			for (OrthopaedicDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("ois_inner_sn_i", form.getOis_inner_sn());
				params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				mapper.p_add_orthopaedic_ois_product_detail(params1);
			}
			mapper.deleteOrthopaedicKitRefPackage(params);
			List<KitIDs> kits = form.getKits();
			for (KitIDs ids : kits) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("ois_inner_sn", form.getOis_inner_sn());
				params1.put("kit_inner_sn", ids.getKit_inner_sn());
				params1.put("sort_number", ids.getSort_number());
				mapper.insertOrthopaedicKitRefPackage(params1);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public int delOrthopaedicPackage(Long pid) {
		Map<String,Object> params = new HashMap<>();
		params.put("ois_inner_sn", pid);
		mapper.deleteOrthopaedicPackageDetail(params);
		mapper.deleteOrthopaedicKitRefPackage(params);
		mapper.deleteOrthopaedicPackageRef(params); // delete by ois
		return mapper.deleteOrthopaedicPackage(pid);
//		
//		List<OrthopaedicPackage> tmp = mapper.selectOrthopaedicPackageExistsRef(params);
//		if(StringUtils.isEmpty(tmp)) {
//			mapper.deleteOrthopaedicPackageDetail(params);
//			mapper.deleteOrthopaedicKitRefPackage(params);
//			return mapper.deleteOrthopaedicPackage(pid);
//		}else {
//			throw new RefException(MessageUtils.message("exists.ref","骨科手术包","不能删除"));
//		}
	}
	
	@Override
	@Transactional
	public int delOrthopaedicPackageDetail(Long pid, Long did) {
		Map<String,Object> params = new HashMap<>();
		params.put("ois_inner_sn", pid);
		params.put("detail_inner_sn", did);
		
		return mapper.deleteOrthopaedicPackageDetail(params);
	}

	@Override
	@Transactional
	public int delOrthopaedicPackageKit(Long pid, Long kid) {
		Map<String,Object> params = new HashMap<>();
		params.put("ois_inner_sn", pid);
		params.put("kit_inner_sn", kid);
		return mapper.deleteOrthopaedicKitRefPackage(params);
	}
	
	@Override
	public OrthopaedicKit addOrthopaedicPackageKit(Long kid) {
		return selectOrthopaedicKitInfoById(kid);
	}
	
	@Override
	public List<OrthopaedicPackage> searchOrthopaedicPackageList(SearchSurgeryPackageForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("pid", form.getPid());//排除掉已添加的手术包
		params.put("name", form.getSearch_name());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("brand_inner_sn", form.getBrand_inner_sn());
		return mapper.searchOrthopaedicPackageList(params);
	}

	@Override
	public OrthopaedicPackage selectOrthopaedicPackageInfoById(Long pid) {
		OrthopaedicPackage info = mapper.selectOrthopaedicPackageById(pid);
		if(info != null) {
			List<OrthopaedicPackageDetail> details = mapper.selectOrthopaedicPackageDetail(pid);
			info.setDetails(details);
			List<OrthopaedicKit> kits = mapper.selectOrthopaedicKitListByPackageId(pid);
			for (OrthopaedicKit orthopaedicKit : kits) {
				List<OrthopaedicKitDetail> kd = mapper.selectOrthopaedicKitDetail(orthopaedicKit.getKit_inner_sn());
				orthopaedicKit.setDetails(kd);
			}
			info.setKits(kits);
		}
		return info;
	}

	@Override
	public List<Map<String,Object>> selectVendorProductList(SurgerySearchProductForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("name", form.getName());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		return mapper.selectVendorProductList(params);
	}

	@Override
	public List<Map<String, Object>> searchBrandList() {
		return mapper.selectBrandList();
	}

	@Override
	@Transactional
	public Brand addBrand(AddBrand form) {
		
		Brand  brand = mapper.selectBrandByName(form.getName()).stream().findFirst().orElse(null); //TODO: add uk(name) after clean duplicated data.
		if(brand == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", form.getName());
			params.put("status", form.getStatus());
			if(mapper.insertBrand(params) > 0){
				Long op_inner_sn = Long.valueOf(params.get("brand_inner_sn").toString());
				return mapper.selectBrandById(op_inner_sn);
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","骨科品牌"));
			}
		} else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("brand_inner_sn", brand.getBrand_inner_sn());
			params.put("is_delete", "0"); //recovery if deleted
			params.put("status", form.getStatus());
			params.put("name", brand.getName()); // optional
			mapper.updateBrand(params);
			
			brand.setStatus(Integer.valueOf(form.getStatus()));
			return brand;
		}
	}

	@Override
	@Transactional
	public int delBrand(Long brand_inner_sn) {
		return mapper.deleteBrand(brand_inner_sn);
	}

	@Override
	@Transactional
	public int editBrand(EditBrand form) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", form.getName());
		params.put("status", form.getStatus());
		params.put("brand_inner_sn", form.getBrand_inner_sn());
		
		return mapper.updateBrand(params);
	}

	@Override
	public Brand searchBrandById(Long brand_inner_sn) {
		return mapper.selectBrandById(brand_inner_sn);
	}

	//骨科工具包
	@Override
	@Transactional
	public OrthopaedicKit addOrthopaedicKit(AddOrthopaedicKitForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("brand_inner_sn", form.getBrand_inner_sn());
		params.put("kit_sn", form.getKit_sn());
		params.put("name", form.getName());
		params.put("description", form.getDescription());
		String keywords =  form.getKeywords(); // add from dealer, UI has keywords field
		if(StringUtils.isEmpty(keywords)) {
			keywords = PinyinUtils.getPy(form.getName());
		}
		params.put("keywords", Utils.maxLength(keywords, MAX_KEYWORDS_LEN));
		
		try {
			if(mapper.insertOrthopaedicKit(params) > 0){
				Long kit_inner_sn = Long.valueOf(params.get("kit_inner_sn").toString());
				List<OrthopaedicDetailForm> details = form.getDetails();
				for (OrthopaedicDetailForm detail : details) {
					Map<String,Object> params1 = new HashMap<>();
					params1.put("kit_inner_sn_i", kit_inner_sn);
					params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
					params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
					params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
					params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
					params1.put("quantity_i", detail.getQuantity());
					params1.put("sort_number_i", detail.getSort_number());
					params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
					params1.put("product_name_i", detail.getProduct_name());
					params1.put("specification_i", detail.getSpecification());
					params1.put("unit_i", detail.getUnit_name());
					mapper.p_add_orthopaedic_kit_product_detail(params1);
				}
				return selectOrthopaedicKitInfoById(kit_inner_sn);
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","骨科工具包"));
			}
		} catch(Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","骨科工具包","骨科工具包名称重复"));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","骨科工具包",e.getMessage()));
			}
		}
	}

	@Override
	@Transactional
	public int editOrthopaedicKit(EditOrthopaedicKitForm form) {
		int result = 1;
		Map<String,Object> params = new HashMap<>();
		if (form.hasUpdate()) {
			params = form.getInfo();
			params.put("kit_inner_sn", form.getKit_inner_sn());
			if (params.containsKey("name")) {
				params.put("keywords", Utils.maxLength(PinyinUtils.getPy(form.getName()), MAX_KEYWORDS_LEN));
			}
			try {
				result = result & mapper.updateOrthopaedicKit(params);
			} catch(Exception e) {
				if(e instanceof DuplicateKeyException) {
					throw new DuplicateException(MessageUtils.message("db.update.fail","骨科工具包","骨科工具包名称重复"));
				}else {
					throw new DBException(MessageUtils.message("db.update.fail","骨科工具包",e.getMessage()));
				}
			}
		}
		if(result > 0) {
			mapper.deleteOrthopaedicKitDetail(params);
			List<OrthopaedicDetailForm> details = form.getDetails();
			for (OrthopaedicDetailForm detail : details) {
				Map<String,Object> params1 = new HashMap<>();
				params1.put("kit_inner_sn_i", form.getKit_inner_sn());
				params1.put("product_vendor_inner_sn_i", detail.getProduct_vendor_inner_sn());
				params1.put("product_inner_sn_i", detail.getProduct_inner_sn());
				params1.put("specification_inner_sn_i", detail.getSpecification_inner_sn());
				params1.put("unit_inner_sn_i", detail.getUnit_inner_sn());
				params1.put("quantity_i", detail.getQuantity());
				params1.put("sort_number_i", detail.getSort_number());
				params1.put("product_vendor_name_i", detail.getProduct_vendor_name());
				params1.put("product_name_i", detail.getProduct_name());
				params1.put("specification_i", detail.getSpecification());
				params1.put("unit_i", detail.getUnit_name());
				mapper.p_add_orthopaedic_kit_product_detail(params1);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public int delOrthopaedicKit(Long kid) {
		Map<String,Object> params = new HashMap<>();
		params.put("kit_inner_sn", kid);
		List<OrthopaedicKit> tmp = mapper.selectOrthopaedicKitExistsRef(params);
		if(StringUtils.isEmpty(tmp)) {
			mapper.deleteOrthopaedicKitDetail(params);
			return mapper.deleteOrthopaedicKit(kid);
		}else {
			throw new RefException(MessageUtils.message("exists.ref","骨科工具包","不能删除"));
		}
	}

	@Override
	@Transactional
	public int delOrthopaedicKitDetail(Long kid, Long did) {
		Map<String,Object> params = new HashMap<>();
		params.put("kit_inner_sn", kid);
		params.put("detail_inner_sn", did);
		return mapper.deleteOrthopaedicKitDetail(params);
	}

	@Override
	public List<OrthopaedicKit> searchOrthopaedicKitList(SearchSurgeryKitForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("name", form.getSearch_name());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("brand_inner_sn", form.getBrand_inner_sn());
		params.put("kit_sn", form.getKit_sn());
		return mapper.searchOrthopaedicKitList(params);
	}

	@Override
	public OrthopaedicKit selectOrthopaedicKitInfoById(Long kid) {
		OrthopaedicKit info = mapper.selectOrthopaedicKitById(kid);
		List<OrthopaedicKitDetail> details = mapper.selectOrthopaedicKitDetail(kid);
		info.setDetails(details);
		return info;
	}
}
