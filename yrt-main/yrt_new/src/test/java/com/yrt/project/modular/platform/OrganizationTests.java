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
import com.yrt.project.modular.organization.domain.NotAuditOrganization;
import com.yrt.project.modular.organization.domain.SearchForm;
import com.yrt.project.modular.organization.service.IOrganizationService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrganizationTests {

	@Autowired
	private IOrganizationService organizationService;
	
	@Test
	public void selectNoAuditOrgInfoTest() throws Exception {
		NotAuditOrganization org = organizationService.selectNoAuditOrgInfo(new Long(2));
		System.err.print(org);
		assertEquals(new Long(330604), org.getAdministrative_division_sn());
		assertEquals(new Integer(1), org.getSystem_type());
		assertEquals(new Integer(0), org.getAudit_status());
		assertEquals("一家小医院", org.getAlias());
		assertEquals("13888555444", org.getRegistrant_phone());
	}
	
	
	@Test
	public void selectAllNoAuditOrgListTest() throws Exception {
		SearchForm form=new SearchForm();
		form.setName("一家小厂商");
		form.setAlias("一家小厂商");
		List<NotAuditOrganization> list = organizationService.selectAllNoAuditOrgList(SystemType.VENDOR, form);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void insertNoAuditOrgTest() throws Exception{
		NotAuditOrganization org =new NotAuditOrganization();
		org.setAdministrative_division_sn(new Long(330604));
		org.setName("一家小小经销商");
		org.setSystem_type(SystemType.DEALER);
		org.setAlias("一家小小经销商");
		System.err.println(org);
		assertEquals(1, organizationService.insertNoAuditOrg(org));
	}
	
	@Test
	public void updateNoAuditOrgTest() throws Exception {
		NotAuditOrganization org = new NotAuditOrganization();
		org.setNot_audit_organization_inner_sn(new Long(3));
		org.setSystem_type(SystemType.HOSPITAL);
		org.setAdministrative_division_sn(new Long(110000));
		org.setName("一家小小医院");
		org.setAlias("一家小小医院");
		System.err.println(org);
		assertEquals(1, organizationService.updateNoAuditOrg(org));
	}
	
	@Test
	public void deleteNoAuditOrgTest() throws Exception {
		assertEquals(1, organizationService.deleteNoAuditOrg(new Long(4)));
	}
	
	@Test
	public void updateStatusTest() throws Exception {
		NotAuditOrganization org = new NotAuditOrganization();
		org.setNot_audit_organization_inner_sn(new Long(3));
		org.setAudit_status(0);
		assertEquals(1,organizationService.updateStatus(org));
	}
	
	@Test
	public void passAduitOrgTest() throws Exception {
		assertEquals(1, organizationService.passAduitOrg(new Long(4)));
	}
}
