package com.yrt.project.modular.organization.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.organization.domain.BusinessLicense;
import com.yrt.project.modular.organization.domain.CheckCompany;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgDept;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.domain.OrgEmployeeRelDept;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;

@Repository
public interface OrgMapper {

public void setGroupconcatmaxLen();
	
	public Company selectOrgInfo(Long id);
	
	public List<Map<String,Object>> selectOrgList4Dropdown(SearchForm form);
	
	public List<Company> selectOrgList(Company org);
	
	public List<Company> selectAllOrgList(Company org);
	
	public List<Company> searchOrgList(Map<String, Object> params);
	public Company selectOrgByName(String name);
	
	public int insertOrg(Company Org);
	
	public int updateOrg(Company Org);
	
	public int updateOrg4Map(Map<String, Object> params);
	
	public int updateStatus(Company Org);
	
	public int updateCertifieStatus(Company Org);
	
	public int updateCertifieStatus1(Long id);
	
	public int deleteOrg(Long id);
	
	public int trueDeleteOrg(Long id);
	
	/**
	 * 新增产品相关图片
	 * @param params
	 */
	public void insertOrgImage(Map<String, Object> params);
	
	public List<OrgRelImage> selectCertImageList(Long id);
	
	public List<OrgRelImage> selectCertImageList2(OrgRelImage info);
	
	public int trueDeleteCertImage(Map<String, Object> params);
	
	//营业执照
	public BusinessLicense selectLicenseById(Long id);
	
	public int insertLicense(BusinessLicense license);
	
	public int updateLicense(BusinessLicense license);
	
	public int updateLicense4Map(Map<String, Object> params);
	
	public int trueDeleteLicense(Long id);
	
	//经销商部门管理
	public void insertOrgDepartment(Map<String, Object> params);
	
	public OrgDept selectOrgDeptInfoById(OrgDept dept);
	
	public List<OrgDept> selectTopLevelOrgDeptListByOrgId(OrgDept dept);
	
	public List<OrgDept> selectOrgDeptListByPid(OrgDept dept);
	
	public int updateOrgDept(OrgDept dept);
	
	public int deleteOrgDept(OrgDept dept);
	
	public int trueDeleteOrgDept(OrgDept dept);
	
	public List<Map<String,Object>> selectRelDeptList(Map<String, Object> params);
	
	//经销商人员管理
	public void insertOrgEmployee(Map<String, Object> params);
	
	public OrgEmployee selectOrgEmployeeInfoById(Map<String, Object> params);

	public OrgEmployee selectOrgEmployeeInfoByUid(Map<String, Object> params);

	public List<OrgEmployee> selectOrgEmployeeListById(OrgEmployee emp);
	
	public List<OrgEmployee> selectTopLevelOrgEmpListByOrgId(OrgEmployee emp);
	
	public List<OrgEmployee> selectOrgEmployeeListByPid(OrgEmployee emp);
	
	public int updateOrgEmployee(Map<String, Object> params);
	
	public int deleteOrgEmployee(OrgEmployee emp);
	
	public int deleteOrgEmployeeRelDept(OrgEmployee emp);
	
	public int insertOrgEmployeeRelDept(OrgEmployeeRelDept empRelDept);
	
	//与经销商签订合同的厂商
	public List<Map<String,Object>> selectContractVendor(Map<String,Object> params);
	//与经销商签订合同的医院
	public List<Map<String,Object>> selectContractHospital(Map<String,Object> params);
	//与经销商签订合同的经销商
	public List<Map<String,Object>> selectContractDealer(Map<String,Object> params);
	
	public int updateCertifieStatus2(Company company);
	
	public OrgEmployee selectOrgEmployeeInfoByUserId(Long id);
	
	public CheckCompany checkExistOrg(Map<String, Object> params);
	
	public Company selectOrgById(Long id);
	
	//检查部门下是否存在员工
	public int existEmployeeInDept(Map<String, Object> params);
	//检查部门是否在业务单据中被使用
	public int existBusinessByDept(Map<String, Object> params);
	//检查部门是否在仓库模型中被使用
	public int existWarehouseByDept(Map<String, Object> params);

	List<OrgDept> deptList(Map<String,Object> params);
}
