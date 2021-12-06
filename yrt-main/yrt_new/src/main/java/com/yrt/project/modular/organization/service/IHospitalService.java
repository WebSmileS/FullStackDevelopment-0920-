package com.yrt.project.modular.organization.service;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.organization.domain.BusinessLicense;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.HospitalPLHMI;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;

public interface IHospitalService {

	public Company selectOrgInfo(Long id);
	
	public List<Map<String,Object>> selectOrgList4Dropdown(String name, Boolean is_certifie);
	
	public List<Company>  searchOrgList(SearchForm form);
	
	public List<Company> selectAllOrgList(Company hospital);
	
	public int insertOrg(Company hospital);
	
	public int updateOrg(Company hospital);
	
	public int updateOrg4Map(Map<String, Object> params);
	
	public int updateStatus(Company hospital);
	
	public int updateCertifieStatus(Company hospital);
	
	public int deleteOrg(Long id);
	
	public List<OrgRelImage> getCertImageList(Long id);
	
	public List<OrgRelImage> selectCertImageList2(Long id, int type);
	
	//营业执照
	public BusinessLicense selectLicenseById(Long id);
	
	public int insertLicense(BusinessLicense license);
	
	public int updateLicense(BusinessLicense license);
	
	public int updateLicense4Map(Map<String, Object> params);
	
	//医院医疗机构执业许可证
	public HospitalPLHMI selectHospitalPLHMIById(Long id);
	
	public int insertHospitalPLHMI(HospitalPLHMI plhmi);
	
	public int updateHospitalPLHMI(HospitalPLHMI plhmi);
	
	public int updateHospitalPLHMI4Map(Map<String, Object> params);
}
