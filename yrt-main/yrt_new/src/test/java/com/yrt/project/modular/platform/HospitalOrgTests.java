package com.yrt.project.modular.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.platform.service.IHospitalOrgService;
import com.yrt.project.api.platform.vo.organization.AddHospitalPLHMIForm;
import com.yrt.project.api.platform.vo.organization.AddOrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.AddOrgRelImageForm;
import com.yrt.project.api.platform.vo.organization.EditOrgInfo;
import com.yrt.project.api.platform.vo.organization.HospitalPLHMIForm;
import com.yrt.project.api.platform.vo.organization.OrgLicenseForm;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.Hospital;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class HospitalOrgTests {

	@Autowired
	private IHospitalOrgService OrgService;
	
	@Test
	public void searchOrgListTest()throws Exception {
		SearchForm form=new SearchForm();
		form.setStatus(1);
		form.setName("一家小医院");
		List<?>list= new ArrayList<Hospital>();
		list=OrgService.searchOrgList(form);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void insertOrgRelImageTest() throws Exception {
		AddOrgRelImageForm form=new AddOrgRelImageForm();
		form.setBegin_date(new Date(2019-04-16));
		form.setEnd_date(new Date(2019-04-17));
		form.setUrl("https://www.baidu.com/img/bd_logo1.png");
		assertEquals(1,OrgService.insertOrgRelImage(new Long(1), 2, form));
	}
	
	@Test
	public void getImageListByOrgIdTest()throws Exception {
		List<OrgRelImage>list=OrgService.getImageListByOrgId(new Long(1));
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void selectCertImageList2Test()throws Exception {
		List<OrgRelImage>list=OrgService.selectCertImageList2(new Long(1),2);
		System.err.println(list);
		assertNotSame(0, list.size());
		}
	
//	@Test
//	public void orgAuthPassTest() throws Exception{
//		assertEquals(1, OrgService.orgAuthPass(new Long(1)));
//	}
//	
//	@Test
//	public void orgAuthNoPassTest() throws Exception{
//		assertEquals(1, OrgService.orgAuthNoPass(new Long(1)));
//	}
	
	@Test
	public void selectNoAuditOrgInfoTest() throws Exception {
		Company hospital=OrgService.selectNoAuditOrgInfo(new Long(1));
		assertEquals("一家小医院", hospital.getName());
		assertEquals("一家小医院", hospital.getAlias());
	}
	
	@Test
	public void updateNoAuditOrgInfoTest() throws Exception{
		EditOrgInfo hospital =new EditOrgInfo();
		hospital.setName("test_0");
		hospital.setLegal_representative("test_1");
		int num = OrgService.updateNoAuditOrgInfo(new Long(1), hospital, SystemType.PLATFORM);
		assertEquals(1, num);
	}
	
	@Test
	public void deleteNoAuditOrgInfoTest()throws Exception {
		int num=OrgService.deleteNoAuditOrgInfo(new Long(1));
		assertEquals(1, num);
	}
	
	@Test
	public void selectOrgLicenseInfoByIdTest()throws Exception {
		OrgLicenseForm licenseForm = OrgService.selectOrgLicenseInfoById(new Long(1));
		System.err.println(licenseForm);
		assertEquals("一家小医院", licenseForm.getName());
		assertEquals("", licenseForm.getLegal_representative());
		assertEquals("test", licenseForm.getBusiness_scope());
	}
	
	@Test
	public void insertOrgLicenseInfoTest()throws Exception{
		AddOrgLicenseForm form=new AddOrgLicenseForm();
		Date date= new Date(1998-01-10);
		form.setBegin_date(date);
		form.setEnd_date(date);
		form.setIssue_date(date);
		form.setIssue_organization("test登记机关");
		form.setBusiness_scope("昨天到今天");
		assertEquals(1, OrgService.insertOrgLicenseInfo(new Long(2), form));
	}
	
	@Test
	public void updateOrgLicenseInfoTest()throws Exception {
		OrgLicenseForm form=new OrgLicenseForm();
		Date date= new Date(2019-01-10);
		form.setBegin_date(date);
		form.setEnd_date(date);
		form.setIssue_date(date);
		form.setIssue_organization("test登记机关改");
		form.setBusiness_scope("昨天到今天改");
		assertEquals(1, OrgService.updateOrgLicenseInfo(new Long(1), form, SystemType.PLATFORM));
	}
	
	@Test
	public void selectHospitalPLHMITest()throws Exception {
		HospitalPLHMIForm form=OrgService.selectHospitalPLHMI(new Long(1));
		System.err.println(form);
		assertEquals("一家小医院",form.getName());
		assertEquals(null, form.getDiagnosis_subject());
	}
	
	@Test
	public void insertHospitalPLHMITest() throws Exception {
		AddHospitalPLHMIForm form=new AddHospitalPLHMIForm();
		form.setRegistered_number("登记号");
		form.setBegin_date(new Date(2019-4-17));
		form.setEnd_date(new Date(2019-4-20));
		form.setIssue_organization("发证机关");
		form.setIssue_date(new Date(2019-4-17));
		form.setDiagnosis_subject("诊疗科目");
		assertEquals(1, OrgService.insertHospitalPLHMI(new Long(2), form));
	}
	
	@Test
	public void updateHospitalPLHMITest() throws Exception {
		HospitalPLHMIForm form = new HospitalPLHMIForm();
		form.setRegistered_number("登记号改");
		form.setDiagnosis_subject("诊疗科目改");
		assertEquals(1, OrgService.updateHospitalPLHMI(new Long(1), form, SystemType.PLATFORM));
	}
}
