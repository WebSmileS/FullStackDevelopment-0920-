package com.yrt.project.api.platform.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.exception.DuplicateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.platform.service.IVendorOrgService;
import com.yrt.project.api.platform.vo.organization.AddOrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.AddOrgRelImageForm;
import com.yrt.project.api.platform.vo.organization.AddVendorForm;
import com.yrt.project.api.platform.vo.organization.AddVendorMDPLForm;
import com.yrt.project.api.platform.vo.organization.EditOrgInfo;
import com.yrt.project.api.platform.vo.organization.OrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.VendorMDPLForm;
import com.yrt.project.modular.organization.domain.BusinessLicense;
import com.yrt.project.modular.organization.domain.BusinessScopeRelCode68;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;
import com.yrt.project.modular.organization.domain.Vendor;
import com.yrt.project.modular.organization.domain.VendorMDPL;
import com.yrt.project.modular.organization.mapper.VendorMapper;

@Service
public class VendorOrgServiceImpl implements IVendorOrgService {

	@Autowired
	private VendorMapper mapper;
	
	@Override
	public List<?> searchOrgList(SearchForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("administrative_division_sn", form.getAdministrative_division_sn());
		params.put("status", form.getStatus());
		params.put("data_source", form.getData_source());
		params.put("begin_time", form.getBegin_time());
		params.put("end_time", form.getEnd_time());
		params.put("name", form.getName());
		params.put("social_credit_code", form.getSocial_credit_code());
		params.put("certification_status", form.getCertification_status());
		if(!form.getIsAll()) {
			params.put("is_delete", 0);
		}
		return mapper.searchOrgList(params);
	}
	
	@Override
	@Transactional
	public OrgRelImage insertOrgRelImage(Long id, int certType, AddOrgRelImageForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id_i", id);
		params.put("type_i", certType);
		if(StringUtils.isNotNull(form.getBegin_date())) {
			params.put("begin_date_i", form.getBegin_date());
		}
		if(StringUtils.isNotNull(form.getEnd_date())) {
			params.put("end_date_i", form.getEnd_date());
		}
		params.put("url_i", form.getUrl());
		params.put("file_name_i", form.getFile_name());
		mapper.insertOrgImage(params);
		if (Integer.valueOf(params.get("result_o").toString()) == 0) {
			OrgRelImage img = new OrgRelImage();
			BeanUtils.copyProperties(form, img);
			img.setId(id);
			img.setType(certType);
			img.setOrg_related_image_inner_sn(Long.valueOf(params.get("image_inner_sn_o").toString()));
			return img;
		} else {
			throw new DBException(MessageUtils.message("db.add.fail", getCertTypeName(certType)));
		}
	}

	@Override
	public List<OrgRelImage> getImageListByOrgId(Long id) {
		return mapper.selectCertImageList(id);
	}

	@Override
	public List<OrgRelImage> selectCertImageList2(Long id, int certType) {
		OrgRelImage rImage = new OrgRelImage();
		rImage.setId(id);
		rImage.setType(certType);
		return mapper.selectCertImageList2(rImage);
	}
	
	@Override
	@Transactional
	public int deleteCertImage(Long oid, int certType, int iid) {
		Map<String,Object> params = new HashMap<>();
		params.put("oid", oid);
		params.put("type", certType);
		params.put("id", iid);
		return mapper.trueDeleteCertImage(params);
	}

	@Override
	public OrgLicenseForm selectOrgLicenseInfoById(Long id) {
		OrgLicenseForm orgLicenseForm = new OrgLicenseForm();
		Company v = mapper.selectOrgInfo(id);
		BeanUtils.copyProperties(v, orgLicenseForm);
		BusinessLicense dlicense = mapper.selectLicenseById(id);
		if(dlicense == null) {
			orgLicenseForm.setNew(true);
		}else {
			BeanUtils.copyProperties(dlicense, orgLicenseForm);
		}
		OrgRelImage img = new OrgRelImage();
		img.setId(id);
		img.setType(1);//certType:营业执照
		List<OrgRelImage> dlist = mapper.selectCertImageList2(img);
		orgLicenseForm.setImages(dlist);
		return orgLicenseForm;
	}
	
	@Override
	@Transactional
	public int insertOrgLicenseInfo(Long id, AddOrgLicenseForm form) {
		if(form.hasOrgInfo()) {
			Map<String, Object> params = form.getOrgInfo();
			params.put("id", id);
			try {
				if (mapper.updateOrg4Map(params) > 0) {
					BusinessLicense license = new BusinessLicense();
					BeanUtils.copyProperties(form, license);
					license.setId(id);
					return mapper.insertLicense(license);
				}else {
					throw new DBException(MessageUtils.message("db.add.fail","厂商营业执照","厂商信息更新失败"));
				}
			}catch (Exception e) {
				if(e instanceof DuplicateKeyException) {
					throw new DuplicateException(MessageUtils.message("db.add.fail","营业执照","机构名称或者社会信用代码重复！"));
				}else {
					throw new DBException(MessageUtils.message("db.add.fail","营业执照",e.getMessage()));
				}
			}
		}else {
			BusinessLicense license = new BusinessLicense();
			BeanUtils.copyProperties(form, license);
			license.setId(id);
			return mapper.insertLicense(license);
		}
	}

	@Override
	@Transactional
	public int updateOrgLicenseInfo(Long id, OrgLicenseForm form, SystemType curUserSystemType) {
		boolean isUpdate = false;
		try {
			if(form.hasOrgInfo()) {
				Map<String, Object> params = form.getOrgInfo();
				params.put("id", id);
				if (mapper.updateOrg4Map(params) > 0)
					isUpdate = true;
			}
			if(form.hasLicense()) {
				Map<String, Object> params = form.getOrgLicense();
				params.put("id", id);
				if (mapper.updateLicense4Map(params) > 0)
					isUpdate = true;
			}
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","营业执照","机构名称或者社会信用代码重复！"));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","营业执照",e.getMessage()));
			}
		}
		if(isUpdate && !SystemType.PLATFORM.equals(curUserSystemType)) {
			Company oldOrg = mapper.selectOrgById(id);
			if(oldOrg.getCertification_status().intValue() == 2) {
				mapper.updateCertifieStatus1(id);//已认证的机构如果不是由平台人员更新机构信息，则设置机构为1-用户申请认证，等待平台人员认证
			}
		}
		return isUpdate ? 1 : 0;
	}

	@Override
	public Company selectNoAuditOrgInfo(Long id) {
		return mapper.selectOrgInfo(id);
	}

	@Override
	@Transactional
	public int updateNoAuditOrgInfo(Long id, EditOrgInfo org, SystemType curUserSystemType) {
		try {
			boolean isUpdate = false;
			if(org.hasUpdate()) {
				Map<String,Object> info = org.getInfo();
				info.put("id", id);
				if(mapper.updateOrg4Map(info) > 0) {
					isUpdate = true;
				}
			}
			if(isUpdate && !SystemType.PLATFORM.equals(curUserSystemType)) {
				Company oldOrg = mapper.selectOrgById(id);
				if(oldOrg.getCertification_status().intValue() == 2) {
					mapper.updateCertifieStatus1(id);//已认证的机构如果不是由平台人员更新机构信息，则设置机构为1-用户申请认证，等待平台人员认证
				}
			}
			return isUpdate ? 1 : 0;
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException("修改机构信息失败！机构名称或者社会信用代码重复！");
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","机构信息",e.getMessage()));
			}
		}
	}

	@Override
	@Transactional
	public int deleteNoAuditOrgInfo(Long id) {
		return mapper.deleteOrg(id);
	}

 
	@Override
	public VendorMDPLForm selectVendorMDPLById(Long id) {
		VendorMDPLForm vendorMDPLForm = new VendorMDPLForm();
		Company vendor = mapper.selectOrgInfo(id);
		VendorMDPL vendorMDPL = mapper.selectVendorMDPLById(id);
		 BeanUtils.copyProperties(vendor, vendorMDPLForm);
		
		  if (vendorMDPL == null) {
			  vendorMDPLForm.setNew(true);
		  }else {
			  BeanUtils.copyProperties(vendorMDPL, vendorMDPLForm);
		  }
		  List<OrgRelImage> img = this.selectCertImageList2(id, 2) ;
		  vendorMDPLForm.setImages(img);
		return vendorMDPLForm;
	}

	@Override
	@Transactional
	public int insertVendorMDPL(long id, AddVendorMDPLForm form) {
		if(form.hasOrgInfo()) {
			Map<String, Object> params = form.getOrgInfo();
			params.put("id", id);
			try {
				if (mapper.updateOrg4Map(params) > 0) {
					VendorMDPL vendorMDPL = new VendorMDPL();
					BeanUtils.copyProperties(form, vendorMDPL);
					vendorMDPL.setId(id);
					if (mapper.insertVendorMDPL(vendorMDPL) > 0) {
						String range = vendorMDPL.getProduction_range();
						return insertMDPLScopeRelCode68(vendorMDPL.getId(), range)?1:0;
					}else {
						throw new DBException(MessageUtils.message("db.add.fail","厂商医疗器械生产许可证","生产许可证添加出错"));
					}
				}else {
					throw new DBException(MessageUtils.message("db.add.fail","厂商医疗器械生产许可证","厂商信息更新失败"));
				}
			}catch (Exception e) {
				if(e instanceof DuplicateKeyException) {
					throw new DuplicateException(MessageUtils.message("db.add.fail","厂商医疗器械生产许可证","机构名称或者社会信用代码重复！"));
				}else {
					throw new DBException(MessageUtils.message("db.add.fail","厂商医疗器械生产许可证",e.getMessage()));
				}
			}
		}else {
			VendorMDPL vendorMDPL = new VendorMDPL();
			BeanUtils.copyProperties(form, vendorMDPL);
			vendorMDPL.setId(id);
			if (mapper.insertVendorMDPL(vendorMDPL) > 0) {
				String range = vendorMDPL.getProduction_range();
				return insertMDPLScopeRelCode68(vendorMDPL.getId(), range)?1:0;
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","厂商医疗器械生产许可证","生产许可证添加出错"));
			}
		}
	}
	
	@Transactional
	private boolean insertMDPLScopeRelCode68(Long orgId, String range) {
		if(StringUtils.isEmpty(range)) return true;
		String[] ranges = range.split(",");
		for (String scope : ranges) {
			BusinessScopeRelCode68 code = new BusinessScopeRelCode68();
			code.setCode68_sn(Long.valueOf(scope));
			code.setOrg_id(orgId);
			if(mapper.insertMDPLScopeRelCode68(code) <= 0) {
				throw new DBException(MessageUtils.message("db.add.fail","厂商医疗器械生产许可证","68分类添加出错"));
			}
		}
		return true;
	}

	@Override
	@Transactional
	public int updateVendorMDPL(long id, VendorMDPLForm mdpl, SystemType curUserSystemType) {
		boolean isUpdate = false;
		try {
			if(mdpl.hasOrgInfo()) {
				Map<String,Object> params = mdpl.getOrgInfo();
				params.put("id",id);
				if (mapper.updateOrg4Map(params) > 0)
					isUpdate = true;
			}
			if(mdpl.hasDealerMDPL()) {
				Map<String, Object> params = mdpl.getDealerMDPL();
				params.put("id", id);
				if (mapper.updateVendorMDPL4Map(params) > 0) {
					isUpdate = true;
				}
			}
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.update.fail","厂商医疗器械生产许可证","机构名称或者社会信用代码重复！"));
			}else {
				throw new DBException(MessageUtils.message("db.update.fail","厂商医疗器械生产许可证",e.getMessage()));
			}
		}
		if(StringUtils.isNotEmpty(mdpl.getProduction_range()) || StringUtils.isNotEmpty(mdpl.getProduction_range_old())) {
			String production_range = mdpl.getProduction_range();
			mapper.deleteMDPLScopeRelCode68(id);
			insertMDPLScopeRelCode68(id, production_range);
			isUpdate = true;
		}
		if(isUpdate && !SystemType.PLATFORM.equals(curUserSystemType)) {
			Company oldOrg = mapper.selectOrgById(id);
			if(oldOrg.getCertification_status().intValue() == 2) {
				mapper.updateCertifieStatus1(id);//已认证的机构如果不是由平台人员更新机构信息，则设置机构为1-用户申请认证，等待平台人员认证
			}
		}
		return isUpdate?1:0;
	}

	@Override
	@Transactional
	public Vendor insertVendor(AddVendorForm form) {
		Vendor vendor = new Vendor();
		BeanUtils.copyProperties(form, vendor);
		try {
			if(mapper.insertOrg(vendor) > 0) {
				return vendor;
			}
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException("厂商添加失败！厂商名称或者社会信用代码重复！");
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","厂商",e.getMessage()));
			}
		}
		return null;
	}

	@Override
	public String getCertTypeName(int certType) {
		switch(certType) {
		case CERT_TYPE_OTHER:
			return "其他";
		case CERT_TYPE_LICENSE:
			return "营业执照";
		case CERT_TYPE_MDPL:
			return "医疗器械生产许可证";
		case CERT_TYPE_ANNUAL_PUBLICITY:
			return "企业年度公示信息";
		case CERT_TYPE_SEAL:
			return "印章印模";
		case CERT_TYPE_OUT_ORDER:
			return "空白销售出库单";
		case CERT_TYPE_QMS:
			return "质量体系调查表";
		case CERT_TYPE_CONTRACT:
			return "合同供货方档案";
		}
		return "";
	}



}
