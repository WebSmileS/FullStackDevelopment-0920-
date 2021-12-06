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
import com.yrt.project.api.platform.service.IHospitalOrgService;
import com.yrt.project.api.platform.vo.organization.AddHospitalPLHMIForm;
import com.yrt.project.api.platform.vo.organization.AddOrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.AddOrgRelImageForm;
import com.yrt.project.api.platform.vo.organization.EditOrgInfo;
import com.yrt.project.api.platform.vo.organization.HospitalPLHMIForm;
import com.yrt.project.api.platform.vo.organization.OrgLicenseForm;
import com.yrt.project.modular.organization.domain.BusinessLicense;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.HospitalPLHMI;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;
import com.yrt.project.modular.organization.mapper.HospitalMapper;

@Service
public class HospitalOrgServiceImpl implements IHospitalOrgService {

	@Autowired
	private HospitalMapper mapper;
	
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
		if(Integer.valueOf(params.get("result_o").toString()) == 0) {
			OrgRelImage img = new OrgRelImage();
			BeanUtils.copyProperties(form, img);
			img.setId(id);
			img.setType(certType);
			img.setOrg_related_image_inner_sn(Long.valueOf(params.get("image_inner_sn_o").toString()));
			return img;
		}else {
			throw new DBException(MessageUtils.message("db.add.fail",getCertTypeName(certType)));
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
		Company h = mapper.selectOrgInfo(id);
		BeanUtils.copyProperties(h, orgLicenseForm);
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
					throw new DBException(MessageUtils.message("db.add.fail","营业执照","医院信息更新失败"));
				}
			}catch (Exception e) {
				if(e instanceof DuplicateKeyException) {
					throw new DuplicateException(MessageUtils.message("db.update.fail","营业执照","机构名称或者社会信用代码重复！"));
				}else {
					throw new DBException(MessageUtils.message("db.update.fail","营业执照",e.getMessage()));
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
		return isUpdate?1:0;
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
	public HospitalPLHMIForm selectHospitalPLHMI(Long id) {
		HospitalPLHMIForm form = new HospitalPLHMIForm();
		Company h = mapper.selectOrgInfo(id);
		BeanUtils.copyProperties(h, form);
		HospitalPLHMI plhmi = mapper.selectHospitalPLHMIById(id);
		if(plhmi == null) {
			form.setNew(true);
		}else {
			BeanUtils.copyProperties(plhmi, form);
		}
		OrgRelImage img = new OrgRelImage();
		img.setId(id);
		img.setType(2);//certType:医疗机构执业许可证
		List<OrgRelImage> dlist = mapper.selectCertImageList2(img);
		form.setImages(dlist);
		return form;
	}

	@Override
	@Transactional
	public int updateHospitalPLHMI(Long id, HospitalPLHMIForm form, SystemType curUserSystemType) {
		boolean isUpdate = false;
		try {
			if(form.hasOrgInfo()) {
				Map<String, Object> params = form.getOrgInfo();
				params.put("id", id);
				if (mapper.updateOrg4Map(params) > 0)
					isUpdate = true;
			}
			if(form.hasHospitalPLHMI()) {
				Map<String, Object> params = form.getHospitalPLHMI();
				params.put("id", id);
				if (mapper.updateHospitalPLHMI4Map(params) > 0)
					isUpdate = true;
			}
		}catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateException(MessageUtils.message("db.add.fail","医院医疗机构执业许可证","机构名称或者社会信用代码重复！"));
			}else {
				throw new DBException(MessageUtils.message("db.add.fail","医院医疗机构执业许可证",e.getMessage()));
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
	@Transactional
	public int insertHospitalPLHMI(Long id, AddHospitalPLHMIForm form) {
		if(form.hasOrgInfo()) {
			try {
				Map<String, Object> params = form.getOrgInfo();
				params.put("id", id);
				if (mapper.updateOrg4Map(params) > 0) {
					HospitalPLHMI plhmi = new HospitalPLHMI();
					BeanUtils.copyProperties(form, plhmi);
					plhmi.setId(id);
					return mapper.insertHospitalPLHMI(plhmi);
				}else {
					throw new DBException(MessageUtils.message("db.add.fail","医院医疗机构执业许可证","经销商信息更新失败"));
				}
			}catch (Exception e) {
				if(e instanceof DuplicateKeyException) {
					throw new DuplicateException(MessageUtils.message("db.add.fail","医院医疗机构执业许可证","机构名称或者社会信用代码重复！"));
				}else {
					throw new DBException(MessageUtils.message("db.add.fail","医院医疗机构执业许可证",e.getMessage()));
				}
			}
		}else {
			HospitalPLHMI plhmi = new HospitalPLHMI();
			BeanUtils.copyProperties(form, plhmi);
			plhmi.setId(id);
			return mapper.insertHospitalPLHMI(plhmi);
		}
	}

	@Override
	public String getCertTypeName(int certType) {
		switch(certType) {
		case CERT_TYPE_OTHER:
			return "其他";
		case CERT_TYPE_LICENSE:
			return "营业执照";
		case CERT_TYPE_PLHMI:
			return "医疗机构执业许可证";
		case CERT_TYPE_KH:
			return "开户许可证";
		}
		return "";
	}
}
