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

import com.yrt.common.dict.SystemType;
import com.yrt.project.modular.organization.domain.NotAuditOrganization;
import com.yrt.project.modular.organization.mapper.OrganizationMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrganizationTests {
	
	@Autowired
	private OrganizationMapper mapper;
	
	@Test
	public void selectNoAuditOrgInfoTest()throws Exception{
		NotAuditOrganization notAudit= mapper.selectNoAuditOrgInfo(new Long(2));
		System.err.println(notAudit);
		assertEquals(new Long(330604), notAudit.getAdministrative_division_sn());
		assertEquals( new Integer(1) , notAudit.getSystem_type());
		assertEquals("一家小医院", notAudit.getName());
	}
	
	@Test
	public void selectAllNoAuditOrgListTest() throws Exception {
		NotAuditOrganization form=new NotAuditOrganization();
		form.setName("一家小厂商");
		form.setAlias("一家小厂商");
		form.setNot_audit_organization_inner_sn(new Long(3));
		Map<String,Object> params = new HashMap<>();
		params.put("name", form.getName());
		List<NotAuditOrganization> list = mapper.selectAllNoAuditOrgList(params);
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
		org.setAudit_status(0);
		org.setRegister_time(new Date(2019-01-01));
		org.setSocial_credit_code("123456789");
		org.setAddress("dasss");
		org.setRegistrant_phone("123456789");
		org.setRegistrant_email("192@www.com");
		org.setDescription("wu");
		System.err.println(org);
		assertEquals(1, mapper.insertNoAuditOrg(org));
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
		assertEquals(1, mapper.updateNoAuditOrg(org));
	}
	
	@Test
	public void deleteNoAuditOrgTest() throws Exception {
		assertEquals(1, mapper.deleteNoAuditOrg(new Long(4)));
	}
	
	@Test
	public void updateStatusTest() throws Exception {
		NotAuditOrganization org = new NotAuditOrganization();
		org.setNot_audit_organization_inner_sn(new Long(3));
		org.setAudit_status(0);
		assertEquals(1,mapper.updateStatus(org));
	}
	
}
