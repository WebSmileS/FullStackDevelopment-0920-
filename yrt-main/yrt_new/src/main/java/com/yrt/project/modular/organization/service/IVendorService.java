package com.yrt.project.modular.organization.service;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.organization.domain.BusinessLicense;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;
import com.yrt.project.modular.organization.domain.VendorMDPL;

public interface IVendorService {

	public Company selectOrgInfo(Long id);
	
	public List<Map<String,Object>> selectOrgList4Dropdown(String name, Boolean is_certifie);
	
	public List<Map<String,Object>> selectOrgList4Dropdown1(String name);
	
	public List<Company>  searchOrgList(SearchForm form);
	
	public List<Company> selectAllOrgList(Company vendor);
	
	public int insertOrg(Company vendor);
	
	public int updateOrg(Company vendor);
	
	public int updateOrg4Map(Map<String, Object> params);
	
	public int updateStatus(Company vendor);
	
	public int updateCertifieStatus(Company vendor);
	
	public int deleteOrg(Long id);
	
	public List<OrgRelImage> getCertImageList(Long id);
	
	public List<OrgRelImage> selectCertImageList2(Long id, int type);
	
	//营业执照
	public BusinessLicense selectLicenseById(Long id);
	
	public int insertLicense(BusinessLicense license);
	
	public int updateLicense(BusinessLicense license);
	
	public int updateLicense4Map(Map<String, Object> params);
	
	//厂商医疗器械生产许可证
	public VendorMDPL selectVendorMDPLById(Long id);
	
	public int insertVendorMDPL(VendorMDPL mdpl);
	
	public int updateVendorMDPL(VendorMDPL mdpl);
	
	public int updateVendorMDPL4Map(Map<String, Object> params);
}
