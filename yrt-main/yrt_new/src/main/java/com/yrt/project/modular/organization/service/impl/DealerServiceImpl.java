package com.yrt.project.modular.organization.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.project.modular.organization.domain.BusinessLicense;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.service.IDealerService;

@Service
public class DealerServiceImpl implements IDealerService{

	@Autowired
	private DealerMapper mapper;
	
	@Override
	public Company selectOrgInfo(Long id) {
		return mapper.selectOrgInfo(id);
	}

	@Override
	public List<Map<String,Object>> selectOrgList4Dropdown(String name, Boolean is_certifie) {
		SearchForm form = new SearchForm();
		form.setName(name);
		form.setIs_certifie(is_certifie);
		return mapper.selectOrgList4Dropdown(form);
	}
	
	@Override
	public List<Company> selectOrgList(Company dealer) {
		return mapper.selectOrgList(dealer);
	}

	@Override
	@Transactional
	public int insertOrg(Company dealer) {
		return mapper.insertOrg(dealer);
	}

	@Override
	@Transactional
	public int updateOrg(Company dealer) {
		return mapper.updateOrg(dealer);
	}

	@Override
	@Transactional
	public int updateStatus(Company dealer) {
		return mapper.updateStatus(dealer);
	}

	@Override
	public List<Company> searchOrgList(SearchForm form) {
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
	public int deleteOrg(Long id) {
		return mapper.deleteOrg(id);
	}

	@Override
	public int updateCertifieStatus(Company dealer) {
		return mapper.updateCertifieStatus(dealer);
	}

	@Override
	public List<OrgRelImage> getCertImageList(Long id) {
		return mapper.selectCertImageList(id);
	}

	@Override
	public List<OrgRelImage> selectCertImageList2(Long id, int type) {
		OrgRelImage rImage = new OrgRelImage();
		rImage.setId(id);
		rImage.setType(type);
		return mapper.selectCertImageList2(rImage);
	}

	@Override
	public BusinessLicense selectLicenseById(Long id) {
		return mapper.selectLicenseById(id);
	}

	@Override
	public int insertLicense(BusinessLicense license) {
		return mapper.insertLicense(license);
	}

	@Override
	public int updateLicense(BusinessLicense license) {
		return mapper.updateLicense(license);
	}

	@Override
	public int updateLicense4Map(Map<String, Object> params) {
		return mapper.updateLicense4Map(params);
	}

	@Override
	public int updateOrg4Map(Map<String, Object> params) {
		return mapper.updateOrg4Map(params);
	}

}
