package com.yrt.project.modular.organization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.project.modular.organization.domain.BusinessLicense;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.OrgDept;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.Vendor;
import com.yrt.project.modular.organization.domain.VendorMDPL;
import com.yrt.project.modular.organization.mapper.VendorMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class VendorTests {

	@Autowired
	private VendorMapper mapper;
	
	@Test
	public void selectVendorInfoTest()throws Exception {
		Company vendor = mapper.selectOrgInfo(new Long(1));
		System.err.println(vendor);
		assertEquals(new Long(1), vendor.getId());
		assertEquals(new Long(330604),vendor.getAdministrative_division_sn() );
		assertEquals("测试用企业", vendor.getName());
	}
	
	@Test
	public void selectAllVendorListTest()throws Exception {
		Vendor vendor = new Vendor();
		vendor.setStatus(1);
//		vendor.setIs_delete(0);
		List<Company> list=mapper.selectAllOrgList(vendor);
		System.err.print(list);
		assertNotSame(0, list);
	}
	
	@Test
	public void insertVendorTest() throws Exception {
		Vendor vendor = new Vendor();
//		vendor.setIs_delete(0);
		vendor.setStatus(1);
		vendor.setData_source(0);
		vendor.setCertification_status(0);
		vendor.setEnterprise_type(0);
		vendor.setSocial_credit_code("fasdfas");
		vendor.setName("hfdhfgsdfgsd");
		vendor.setLegal_representative("");
		vendor.setPrincipal("");
		vendor.setAddress("dass");
		vendor.setRegistered_capital("");
		vendor.setBank_name("");
		vendor.setBank_account("");
		vendor.setPhone("");
		vendor.setEmail("");
		vendor.setDescription("");
		assertEquals(1,mapper.insertOrg(vendor));
	}
	
	@Test
	public void updateVendorTest()throws Exception {
		Vendor vendor = new Vendor();
		vendor.setId(new Long(1));
		vendor.setAddress("abc");
		vendor.setName("cba");
		assertEquals(1,mapper.updateOrg(vendor));
	}
	
	@Test
	public void updateVendor4MapTest()throws Exception{
		Map<String, Object> params =new HashMap<String, Object>();
		params.put("id", 1);
		params.put("address", "abc");
		params.put("name", "cba");
		params.put("status", 1);
		System.err.print(params);
		assertEquals(1, mapper.updateOrg4Map(params));
	}
	
	@Test
	public void updateStatusTest()throws Exception{
		Vendor vendor = new Vendor();
		vendor.setId(new Long(1));
		vendor.setStatus(0);
		assertEquals(1, mapper.updateStatus(vendor));
	}
	
	@Test
	public void updateCertifieStatusTest()throws Exception{
		Vendor vendor = new Vendor();
		vendor.setId(new Long(1));
		vendor.setCertification_status(1);
		vendor.setCertifie_employee_inner_sn(new Long(1));
		assertEquals(1, mapper.updateCertifieStatus(vendor));
	}
	
	@Test
	public void deleteVendorTest() throws Exception {
		assertEquals(1, mapper.deleteOrg(new Long(1)));
	}
	
	/**
	 * 新增产品相关图片
	 * @param params
	 */
	@Test
	public void  v_add_vendor_imageTest() throws Exception{
		Map<String, Object>params=new HashMap<String, Object>();
		params.put("vendor_inner_sn_i",1);
		params.put("type_i", 1);
		params.put("url_i", "adsfasf");
		mapper.insertOrgImage(params);
		assertEquals(0,Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void selectCertImageListTest()throws Exception{
		List<OrgRelImage>list=mapper.selectCertImageList(new Long(1));
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void selectCertImageList2Test()throws Exception {
		OrgRelImage info=new OrgRelImage();
		info.setId(new Long(1));
		info.setType(1);
		info.setUrl("dfadfas");
		info.setOrg_related_image_inner_sn(1L);
		List<OrgRelImage> list=mapper.selectCertImageList2(info);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	//营业执照
	@Test
	public void selectLicenseByIdTest()throws Exception{
		BusinessLicense license=mapper.selectLicenseById(new Long(1));
		System.err.print(license);
		assertEquals("我发的", license.getIssue_organization());
		assertEquals("所有范围", license.getBusiness_scope());
	}
	
	@Test
	public void insertLicenseTest()throws Exception {
		BusinessLicense license = new BusinessLicense();
		Date date=new Date(2009-1-13);
		license.setId(new Long(6));
		license.setBusiness_scope("abc");
		license.setBegin_date(date);
		license.setEnd_date(date);
		license.setIssue_date(date);
		license.setIssue_organization("abc");
		assertEquals(1, mapper.insertLicense(license));
	}
	
	@Test
	public void updateLicenseTest()throws Exception {
		BusinessLicense license = new BusinessLicense();
		Date date=new Date(2009-1-13);
		license.setId(new Long(1));
		license.setBusiness_scope("cba");
		license.setBegin_date(date);
		license.setEnd_date(date);
		license.setIssue_date(date);
		license.setIssue_organization("cba");
		assertEquals(1, mapper.updateLicense(license));
	}
	
	
	@Test
	public void updateLicense4Map()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		Date date=new Date(2009-1-13);
		params.put("id", 1);
		params.put("business_scope", "cba");
		params.put("begin_date", date);
		params.put("end_date", date);
		params.put("issue_date", date);
		params.put("issue_organization", "cba");
		assertEquals(1, mapper.updateLicense4Map(params));
	}
	
	//厂商医疗器械生产许可证
	@Test
	public void selectVendorMDPLByIdTest()throws Exception{
		VendorMDPL mdpl=mapper.selectVendorMDPLById(new Long(1));
		System.err.println(mdpl);
	}
	
	@Test
	public void insertVendorMDPLTest()throws Exception{
		VendorMDPL mdpl=new VendorMDPL();
		Date date=new Date(2010-01-01);
		mdpl.setId(new Long(6));
		mdpl.setLicense_sn("???");
		mdpl.setProduction_address("???");
		mdpl.setIssue_organization("???");
		mdpl.setProduction_range("???");
		mdpl.setEnd_date(date);
		mdpl.setIssue_date(date);
		assertEquals(1,mapper.insertVendorMDPL(mdpl));
	}
	
	@Test
	public void updateVendorMDPLTest()throws Exception{
		VendorMDPL mdpl=new VendorMDPL();
		mdpl.setId(new Long(1));
		mdpl.setLicense_sn("??");
		mdpl.setProduction_address("??");
		mdpl.setIssue_organization("??");
		mdpl.setProduction_range("??");
		System.err.println(mdpl);
		assertEquals(1, mapper.updateVendorMDPL(mdpl));
	}
	
	@Test
	public void updateVendorMDPL4MapTest()throws Exception {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("id", 1);
		params.put("license_sn", "?");
		params.put("production_address", "?");
		params.put("issue_organization", "?");
		params.put("production_range", "?");
		System.err.println(params);
		assertEquals(1, mapper.updateVendorMDPL4Map(params));
	}
	
	
	//厂商部门管理
	@Test 
	public void v_add_vendor_departmentTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("vendor_inner_sn_i", 6);
		params.put("director_inner_sn_i", 1);
		params.put("name_i", "单元测试厂商");
		params.put("parent_department_inner_sn_i",1);
		params.put("department_sn_i",1);
		params.put("phone_i", 123546789);
		params.put("description_i", 111);
		mapper.insertOrgDepartment(params);
		assertEquals(0, Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void selectOrgDeptInfoByIdTest()throws Exception{
		OrgDept dept= new OrgDept();
		dept.setId(new Long(6));
		dept.setDepartment_inner_sn(new Long(1));
		assertEquals("单元测试厂商", mapper.selectOrgDeptInfoById(dept).getName());
	}
	
	@Test
	public void  selectTopLevelOrgDeptListByOrgIdTest()throws Exception {
		OrgDept dept=new OrgDept();
		dept.setId(new Long(1));
		List<OrgDept> list= mapper.selectTopLevelOrgDeptListByOrgId(dept);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	@Test
	public void selectOrgDeptListByPidTest()throws Exception{
		OrgDept dept=new OrgDept();
		dept.setId(new Long(1));
		dept.setParent_department_inner_sn(new Long(1));
		List<OrgDept> list=mapper.selectOrgDeptListByPid(dept);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	@Test
	public void updateOrgDeptTest()throws Exception {
		OrgDept dept=new OrgDept();
		dept.setId(new Long(1));
		dept.setDepartment_inner_sn(new Long(1));
		dept.setDepartment_sn("asdfdasfdasf");
		dept.setDescription("pouwpot");
		dept.setName("adpfa");
		assertEquals(1, mapper.updateOrgDept(dept));
	}
	
	@Test
	public void deleteOrgDeptTest()throws Exception{
		OrgDept dept= new OrgDept();
		dept.setId(new Long(1));
//		dept.setIs_delete(0);
		dept.setDepartment_inner_sn(new Long(1));
		assertEquals(1, mapper.deleteOrgDept(dept));
	}
	
	//厂商人员管理
	@Test
	public void v_add_vendor_employeeTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("vendor_inner_sn_i", 6);
		params.put("sex_i", 0);
		params.put("name_i", "王麻子");
		params.put("employee_sn_i","213354384");
		params.put("marriage_status_i",1);
		params.put("status_i", 1);
		params.put("personal_certificate_type_i", 0);
		params.put("degree_i", 1);
		params.put("birthday_i",new Date(2019-8-1));
		params.put("entry_date_i", new Date(2019-8-1));
		params.put("departure_date_i",new Date(2019-8-1));
		params.put("nation_i", "汉族");
		params.put("position_i","销售");
		params.put("personal_certificate_sn_i", 1235468513);
		params.put("phone_i", 123548);
		params.put("email_i", "123458678@www.com");
		params.put("qq_i", 12315468);
		params.put("description_i", "无");
		params.put("photo_url_i", "asd13156");
		mapper.insertOrgEmployee(params);
		assertEquals(0, Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void selectOrgEmployeeInfoByIdTest()throws Exception {
		Map<String,Object> emp = new HashMap<>();
		emp.put("id", 2);
		emp.put("employee_inner_sn", 1);
		mapper.selectOrgEmployeeInfoById(emp);
	}
	
	@Test
	public void selectOrgEmployeeListByIdTest()throws Exception{
		OrgEmployee emp= new OrgEmployee();
		emp.setId(new Long(6));
		List<OrgEmployee>list=mapper.selectOrgEmployeeListById(emp);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void updateOrgEmployeeTest()throws Exception{
		Map<String,Object> emp = new HashMap<>();
		emp.put("id", 6);
		emp.put("employee_inner_sn", 1);
		emp.put("email", "121345467@www.com");
		assertEquals(1, mapper.updateOrgEmployee(emp));
	}
	
	@Test
	public void deleteOrgEmployeeTest()throws Exception {
		OrgEmployee emp=new OrgEmployee();
		emp.setId(new Long(6));
		emp.setEmployee_inner_sn(new Long(1));
		assertEquals(1, mapper.deleteOrgEmployee(emp));
	}
}
