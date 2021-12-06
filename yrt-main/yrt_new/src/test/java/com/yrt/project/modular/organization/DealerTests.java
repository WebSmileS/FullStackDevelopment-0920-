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
import com.yrt.project.modular.organization.domain.Dealer;
import com.yrt.project.modular.organization.domain.DealerMAAIOL;
import com.yrt.project.modular.organization.domain.DealerSTMDBRC;
import com.yrt.project.modular.organization.domain.OrgDept;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.mapper.DealerMapper;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DealerTests {

	@Autowired
	private DealerMapper mapper;
	
	@Test
	public void selectDealerInfoTest()throws Exception {
		Company dealer=mapper.selectOrgInfo(new Long(1));
		System.err.println(dealer);
		assertEquals(new Long(110000),dealer.getAdministrative_division_sn() );
		assertEquals("单元测试经销商", dealer.getName());
	}
	
	@Test
	public void selectDealerListTest()throws Exception {
		Dealer dealer = new Dealer();
		dealer.setStatus(1);
//		dealer.setIs_delete(0);
		List<Company> list=mapper.selectOrgList(dealer);
		System.err.print(list);
		assertNotSame(0, list);
	}
	
	@Test
	public void insertDealerTest() throws Exception {
		Dealer dealer = new Dealer();
//		dealer.setIs_delete(0);
		dealer.setStatus(1);
		dealer.setData_source(0);
		dealer.setCertification_status(0);
		dealer.setEnterprise_type(0);
		dealer.setSocial_credit_code("fasdfas");
		dealer.setName("hfdhfgsdfgsd");
		dealer.setLegal_representative("");
		dealer.setPrincipal("");
		dealer.setAddress("");
		dealer.setRegistered_capital("");
		dealer.setBank_name("");
		dealer.setBank_account("");
		dealer.setPhone("");
		dealer.setEmail("");
		dealer.setDescription("");
		
		assertEquals(1,mapper.insertOrg(dealer));
		System.err.println(dealer.getId());
	}
	
	@Test
	public void updateDealerTest()throws Exception {
		Dealer dealer=new Dealer();
		dealer.setId(new Long(1));
		dealer.setAddress("abc");
		dealer.setName("cba");
		assertEquals(1,mapper.updateOrg(dealer));
	}
	
	@Test
	public void updateDealer4MapTest()throws Exception{
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
		Dealer dealer=new Dealer();
		dealer.setId(new Long(1));
		dealer.setStatus(0);
		assertEquals(1, mapper.updateStatus(dealer));
	}
	
	@Test
	public void updateCertifieStatusTest()throws Exception{
		Dealer dealer =new Dealer();
		dealer.setId(new Long(1));
		dealer.setCertification_status(1);
		dealer.setCertifie_employee_inner_sn(new Long(1));
		assertEquals(1, mapper.updateCertifieStatus(dealer));
	}
	
	@Test
	public void deleteDealerTest() throws Exception {
		assertEquals(1, mapper.deleteOrg(new Long(1)));
	}
	
	/**
	 * 新增产品相关图片
	 * @param params
	 */
	@Test
	public void d_add_dealer_imageTest() throws Exception{
		Map<String, Object>params=new HashMap<String, Object>();
		params.put("dealer_inner_sn_i",1);
		params.put("type_i", 1);
		params.put("url_i", "adsfasf");
		mapper.insertOrgImage(params);
		assertEquals(0,Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void selectCertImageListTest()throws Exception{
		List<OrgRelImage>list=mapper.selectCertImageList(new Long(1));
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
		assertNotSame(0, list.size());
	}
	
	//营业执照
	@Test
	public void selectLicenseByIdTest()throws Exception{
		BusinessLicense license=mapper.selectLicenseById(new Long(1));
		System.err.print(license);
		assertEquals("test", license.getIssue_organization());
		assertEquals("test", license.getBusiness_scope());
	}
	
	@Test
	public void insertLicenseTest()throws Exception {
		BusinessLicense license = new BusinessLicense();
		Date date=new Date(2009-1-13);
		license.setId(new Long(2));
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
	
	//经销商医疗器械经营许可证(就是第三类)
	@Test
	public void selectMAAIOLByIdTest()throws Exception {
		DealerMAAIOL maaiol=mapper.selectMAAIOLById(new Long(1));
		System.err.println(maaiol);
		assertEquals(new Integer(1), maaiol.getOperation_mode());
		assertEquals("1", maaiol.getLicense_sn());
		assertEquals("1", maaiol.getBusiness_scope());
	}
	
	@Test
	public void insertMAAIOLTest()throws Exception {
		DealerMAAIOL maaiol=new DealerMAAIOL();
		Date date=new Date(2009-1-13);
		maaiol.setId(new Long(2));
		maaiol.setEnd_date(date);
		maaiol.setIssue_date(date);
		maaiol.setLicense_sn("1");
		maaiol.setPremises("1");
		maaiol.setWarehouse_address("1");
		maaiol.setBusiness_scope("680501");
		maaiol.setIssue_organization("cba");
		assertEquals(1, mapper.insertMAAIOL(maaiol));
	}
	
	@Test
	public void updateMAAIOLTest()throws Exception {
		DealerMAAIOL maaiol=new DealerMAAIOL();
		maaiol.setId(new Long(1));
		maaiol.setBusiness_scope("680303");
		maaiol.setIssue_organization("ea");
		assertEquals(1, mapper.updateMAAIOL(maaiol));
	}
	
	@Test
	public void updateMAAIOL4MapTest()throws Exception {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("id", 1);
		params.put("business_scope", "680303");
		params.put("issue_organization", "adf");
		assertEquals(1, mapper.updateMAAIOL4Map(params));
	}
	
	//经销商第二类医疗器械经营备案凭证
	@Test
	public void selectSTMDBRCByIdTest()throws Exception {
		DealerSTMDBRC stmdbrc=mapper.selectSTMDBRCById(new Long(1));
		System.err.println(stmdbrc);
		assertEquals(new Integer(0), stmdbrc.getOperation_mode());
		assertEquals("1", stmdbrc.getPremises());
		assertEquals("1", stmdbrc.getBusiness_scope());
	}
	@Test
	public void insertSTMDBRCTest()throws Exception{
		DealerSTMDBRC stmdbrc=new DealerSTMDBRC();
		Date date=new Date(2009-1-13);
		stmdbrc.setId(new Long(2));
		stmdbrc.setOperation_mode(0);
		stmdbrc.setRecord_sn("asfd");
		stmdbrc.setIssue_date(date);
		stmdbrc.setPremises("1");
		stmdbrc.setWarehouse_address("1");
		stmdbrc.setIssue_organization("1");
		stmdbrc.setBusiness_scope("680303");
		assertEquals(1,mapper.insertSTMDBRC(stmdbrc));
	}
	
	@Test
	public void updateSTMDBRCTest()throws Exception {
		DealerSTMDBRC stmdbrc=new DealerSTMDBRC();
		stmdbrc.setId(new Long(1));
		stmdbrc.setOperation_mode(3);
		stmdbrc.setRecord_sn("asdwer");
		assertEquals(1, mapper.updateSTMDBRC(stmdbrc));
	}
	@Test
	public void updateSTMDBRC4MapTest()throws Exception {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("id", 1);
		params.put("operation_mode", 0);
		params.put("record_sn", "zxcva");
		assertEquals(1, mapper.updateSTMDBRC4Map(params));
	}
	
	//经销商部门管理
	@Test 
	public void d_add_dealer_departmentTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("dealer_inner_sn_i", 1);
		params.put("director_inner_sn_i", 1);
		params.put("name_i", "嘎达");
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
		dept.setId(new Long(1));
		dept.setDepartment_inner_sn(new Long(1));
		assertEquals("嘎达", mapper.selectOrgDeptInfoById(dept).getName());
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
	
	//经销商人员管理
	@Test
	public void d_add_dealer_employeeTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("dealer_inner_sn_i", 2);
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
		emp.setId(new Long(1));
		List<OrgEmployee>list=mapper.selectOrgEmployeeListById(emp);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void updateOrgEmployeeTest()throws Exception{
		Map<String,Object> emp = new HashMap<>();
		emp.put("id", 1);
		emp.put("employee_inner_sn", 1);
		assertEquals(1, mapper.updateOrgEmployee(emp));
	}
	
	@Test
	public void deleteOrgEmployeeTest()throws Exception {
		OrgEmployee emp=new OrgEmployee();
		emp.setId(new Long(1));
		emp.setEmployee_inner_sn(new Long(1));
		assertEquals(1, mapper.deleteOrgEmployee(emp));
	}
}
