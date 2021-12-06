package com.yrt.project.modular.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.common.vo.AddNoAuditOrgForm;
import com.yrt.project.api.platform.service.IOrgRegisterAuditService;
import com.yrt.project.api.platform.vo.organization.NoAuditOrgForm;
import com.yrt.project.api.platform.vo.organization.SearchOrgForm;
import com.yrt.project.modular.organization.domain.NotAuditOrganization;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrgRegisterAuditTest {

	
	@Autowired
	private IOrgRegisterAuditService service;
	
	
	@Test
	public void selectNoAuditOrgInfoTest()throws Exception{
		NotAuditOrganization notAudit= service.selectNoAuditOrgInfo(new Long(2));
		System.err.println(notAudit);
		assertEquals(new Long(330604), notAudit.getAdministrative_division_sn());
		assertEquals( new Integer(1) , notAudit.getSystem_type());
		assertEquals("一家小医院", notAudit.getName());
	}
	
	@Test
	public void selectAllNoAuditOrgListTest() throws Exception {
		SearchOrgForm form=new SearchOrgForm();
		form.setName("一家小厂商");
		List<NotAuditOrganization> list= service.selectAllNoAuditOrgList(SystemType.VENDOR, form);
		System.err.println(list);
		assertNotSame(0,list.size());	
	}
	
	@Test
	public void insertNoAuditOrgTest() throws Exception{
		AddNoAuditOrgForm form =new AddNoAuditOrgForm();
		form.setAdministrative_division_sn(new Long(330604));
		form.setName("一家小小经销商");
		form.setSystem_type(2);
		form.setAlias("一家小小经销商");
		form.setSocial_credit_code("123456789");
		form.setAddress("dasss");
		form.setRegistrant_phone("123456789");
		form.setRegistrant_email("192@www.com");
		form.setDescription("wu");
		System.err.println(form);
		assertEquals(1, service.insertNoAuditOrg(form));
	}
	
	@Test
	public void updateNoAuditOrgTest() throws Exception {
		NoAuditOrgForm form = new NoAuditOrgForm();
		form.setSystem_type(1);
		form.setAdministrative_division_sn(new Long(110000));
		form.setName("一家小小医院");
		form.setAlias("一家小小医院");
		System.err.println(form);
		assertEquals(1, service.updateNoAuditOrg(new Long(2), form));
	}
	
	@Test
	public void deleteNoAuditOrgTest() throws Exception {
		assertEquals(1, service.deleteNoAuditOrg(new Long(4)));
	}
	
	@Test
	public void noPassAduitOrgTest()throws Exception{
		assertEquals(1, service.noPassAduitOrg(new Long(2), ""));
	}
	
	@Test
	public void passAduitOrgTest()throws Exception{
		assertEquals(1, service.passAduitOrg(new Long(2)));
	}
}
