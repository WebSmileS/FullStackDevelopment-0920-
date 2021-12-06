package com.yrt.project.modular.organization.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.project.modular.organization.domain.BusinessLicense;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;
import com.yrt.project.modular.organization.domain.VendorMDPL;
import com.yrt.project.modular.organization.mapper.VendorMapper;
import com.yrt.project.modular.organization.service.IVendorService;

@Service
public class VendorServiceImpl implements IVendorService {
	
	@Autowired
	private VendorMapper mapper;
	
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
	public List<Map<String,Object>> selectOrgList4Dropdown1(String name) {
		SearchForm form = new SearchForm();
		form.setName(name);
		return mapper.selectOrgList4Dropdown1(form);
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
	public List<Company> selectAllOrgList(Company vendor) {
		return mapper.selectAllOrgList(vendor);
	}

	@Override
	public int insertOrg(Company vendor) {
		return mapper.insertOrg(vendor);
	}

	@Override
	public int updateOrg(Company vendor) {
		return mapper.updateOrg(vendor);
	}
	
	@Override
	public int updateOrg4Map(Map<String, Object> params) {
		return mapper.updateOrg4Map(params);
	}

	@Override
	public int updateStatus(Company vendor) {
		return mapper.updateStatus(vendor);
	}

	@Override
	public int deleteOrg(Long id) {
		return mapper.deleteOrg(id);
	}

	@Override
	public int updateCertifieStatus(Company vendor) {
		return mapper.updateCertifieStatus(vendor);
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

	//营业执照
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

	//厂商医疗器械生产许可证
	@Override
	public VendorMDPL selectVendorMDPLById(Long id) {
		return mapper.selectVendorMDPLById(id);
	}

	@Override
	public int insertVendorMDPL(VendorMDPL mdpl) {
		return mapper.insertVendorMDPL(mdpl);
	}

	@Override
	public int updateVendorMDPL(VendorMDPL mdpl) {
		return mapper.updateVendorMDPL(mdpl);
	}

	@Override
	public int updateVendorMDPL4Map(Map<String, Object> params) {
		return mapper.updateVendorMDPL4Map(params);
	}
}
