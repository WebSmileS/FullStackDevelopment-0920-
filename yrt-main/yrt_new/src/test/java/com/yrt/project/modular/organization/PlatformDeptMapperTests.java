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

import com.yrt.project.api.common.vo.SearchForm;
import com.yrt.project.api.platform.vo.organization.AddPlatformDeptForm;
import com.yrt.project.api.platform.vo.organization.AddPlatformEmployeeForm;
import com.yrt.project.api.platform.vo.organization.EditPlatformDeptForm;
import com.yrt.project.modular.organization.domain.PlatformDept;
import com.yrt.project.modular.organization.domain.PlatformEmployee;
import com.yrt.project.modular.organization.mapper.PlatformDeptMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PlatformDeptMapperTests {

	@Autowired
	private PlatformDeptMapper mapper;
	
	@Test
	public void selectPlatformDeptInfoTest()throws Exception {
		PlatformDept dept= mapper.selectPlatformDeptInfo(new Long(1));
		System.err.println(dept);
		assertEquals("升级老大", dept.getName());
		assertEquals("001", dept.getDepartment_sn());
		assertEquals("123121231", dept.getPhone());
		assertEquals("1大师傅似的", dept.getDescription());
	}
	
	@Test
	public void selectPlatformDeptChildrenListTest()throws Exception {
		List<PlatformDept> list= mapper.selectPlatformDeptChildrenList(new Long(1));
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void selectTopLevelPlatformDeptlistTest()throws Exception{
		List<PlatformDept> list = mapper.selectTopLevelPlatformDeptlist();
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void insertPlatformDeptTest()throws Exception{
		AddPlatformDeptForm platformDept=new AddPlatformDeptForm();
		platformDept.setParent_department_inner_sn(new Long(1));
		platformDept.setDirector_inner_sn(new Long(1));
		platformDept.setName("test");
		platformDept.setDepartment_sn("110");
		platformDept.setPhone("1231643564");
		platformDept.setDescription("test");
		assertEquals(1, mapper.insertPlatformDept(platformDept));
	}
	
	@Test
	public void updatePlatformDeptTest()throws Exception{
		EditPlatformDeptForm platformDept=new EditPlatformDeptForm();
		platformDept.setDepartment_inner_sn(new Long(5));
		platformDept.setDepartment_sn("110");
		platformDept.setPhone("1231643564");
		assertEquals(1, mapper.updatePlatformDept(platformDept));
	}
	
	@Test
	public void deletePlatformDeptTest()throws Exception{
		assertEquals(1, mapper.deletePlatformDept(new Long(8)));
	}
	
	
	/**
	 * 平台员工
	 */
	@Test
	public void selectPlatformEmployeeInfoByIdTest()throws Exception{
		PlatformEmployee employee= mapper.selectPlatformEmployeeInfoById(new Long(1));
		assertEquals(new Long(1), employee.getParent_employee_inner_sn());
		assertEquals("zhailei", employee.getName());
		assertEquals("呢欧洲@hie.com", employee.getEmail());
	}
	
	@Test
	public void selectPlatformEmployeeListTest()throws Exception{
		SearchForm form=new SearchForm();
		form.setName("老板");
		List<PlatformEmployee> list=mapper.selectPlatformEmployeeList(form);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void insertPlatformEmployeeTest()throws Exception{
		AddPlatformEmployeeForm form =new AddPlatformEmployeeForm();
		Date date=new Date(2019-01-01);
		form.setParent_employee_inner_sn(new Long(1));
		form.setSex(1);
		form.setStatus(1);
		form.setPersonal_certificate_type(1);
		form.setDegree(1);
		form.setMarriage_status(1);
		form.setBirthday(date);
		form.setEntry_date(date);
		form.setDeparture_date(date);
		form.setName("test");
		form.setEmployee_sn("test");
		form.setNation("test");
		form.setPosition("test");
		form.setPersonal_certificate_sn("test");
		form.setPhone("test");
		form.setEmail("test");
		form.setQq("test");
		form.setDescription("test");
		form.setPhoto_url("test");
		Map<String, Object> params = new HashMap<>();
		params.put("parent_employee_inner_sn", form.getParent_employee_inner_sn());
		params.put("sex", form.getSex());
		params.put("status", form.getStatus());
		params.put("personal_certificate_type", form.getPersonal_certificate_type());
		params.put("degree", form.getDegree());
		params.put("marriage_status", form.getMarriage_status());
		params.put("birthday", form.getBirthday());
		params.put("entry_date", form.getEntry_date());
		params.put("departure_date", form.getDeparture_date());
		params.put("name", form.getName());
		params.put("employee_sn", form.getEmployee_sn());
		params.put("nation", form.getNation());
		params.put("position", form.getPosition());
		params.put("personal_certificate_sn", form.getPersonal_certificate_sn());
		params.put("phone", form.getPhone());
		params.put("email", form.getEmail());
		params.put("qq", form.getQq());
		params.put("description", form.getDescription());
		params.put("photo_url", form.getPhoto_url());
		params.put("login_account_status", 0);
		params.put("user_inner_sn", null);
		assertEquals(1, mapper.insertPlatformEmployee(params));
	}
	
	@Test
	public void updatePlatformEmployeeTest()throws Exception{
		Map<String,Object> platformEmployee = new HashMap<>();
		platformEmployee.put("employee_inner_sn", 1);
		platformEmployee.put("name", "test");
		platformEmployee.put("employee_sn", "test");
		platformEmployee.put("nation", "test");
		platformEmployee.put("position", "test");
		platformEmployee.put("personal_certificate_sn", "test");
		platformEmployee.put("phone", "test");
		platformEmployee.put("email", "121345467@www.com");
		assertEquals(1, mapper.updatePlatformEmployee(platformEmployee));
	}
	
	@Test
	public void deletePlatformEmployeeTest()throws Exception{
		assertEquals(1, mapper.deletePlatformEmployee(new Long(1)));
	}
}
