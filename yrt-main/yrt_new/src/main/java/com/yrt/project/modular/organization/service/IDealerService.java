package com.yrt.project.modular.organization.service;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.organization.domain.BusinessLicense;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;

public interface IDealerService {
	
	public Company selectOrgInfo(Long id);
	
	public List<Map<String,Object>> selectOrgList4Dropdown(String name, Boolean is_certifie);
	
	public List<Company> selectOrgList(Company dealer);
	
	public List<Company> searchOrgList(SearchForm form);
	
	public int insertOrg(Company dealer);
	
	public int updateOrg(Company dealer);
	
	public int updateOrg4Map(Map<String, Object> params);
	
	public int updateStatus(Company dealer);
	
	public int updateCertifieStatus(Company dealer);
	
	public int deleteOrg(Long id);

	public List<OrgRelImage> getCertImageList(Long id);
	
	public List<OrgRelImage> selectCertImageList2(Long id, int type);
	
	public BusinessLicense selectLicenseById(Long id);
	
	public int insertLicense(BusinessLicense license);
	
	public int updateLicense(BusinessLicense license);
	
	public int updateLicense4Map(Map<String, Object> params);
}
