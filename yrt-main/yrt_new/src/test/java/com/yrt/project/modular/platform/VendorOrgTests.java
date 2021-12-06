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
import com.yrt.project.api.platform.service.IVendorOrgService;
import com.yrt.project.api.platform.vo.organization.AddOrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.AddOrgRelImageForm;
import com.yrt.project.api.platform.vo.organization.AddVendorMDPLForm;
import com.yrt.project.api.platform.vo.organization.EditOrgInfo;
import com.yrt.project.api.platform.vo.organization.OrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.VendorMDPLForm;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.Hospital;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class VendorOrgTests {

	@Autowired
	private IVendorOrgService OrgService;
	
	@Test
	public void searchOrgListTest()throws Exception {
		SearchForm form=new SearchForm();
		form.setStatus(1);
		form.setName("测试用企业");
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
		List<OrgRelImage>list=OrgService.selectCertImageList2( new Long(1), 2);
		System.err.println(list);
		assertNotSame(0, list.size());
		}
	
//	@Test
//	public void orgAuthPassTest() throws Exception{
//		assertEquals(1, OrgService.orgAuthPass(new Long(2)));
//	}
//	
//	@Test
//	public void orgAuthNoPassTest() throws Exception{
//		assertEquals(1, OrgService.orgAuthNoPass(new Long(1)));
//	}
	
	@Test
	public void selectNoAuditOrgInfoTest() throws Exception {
		Company vendor=OrgService.selectNoAuditOrgInfo(new Long(1));
		assertEquals(new Long(330604),vendor.getAdministrative_division_sn());
		assertEquals("测试用企业", vendor.getName());
		assertEquals("啊啊啊", vendor.getLegal_representative());
	}
	
	@Test
	public void updateNoAuditOrgInfoTest() throws Exception{
		EditOrgInfo vendor = new EditOrgInfo();
		vendor.setName("test_0");
		vendor.setLegal_representative("test_1");
		int num = OrgService.updateNoAuditOrgInfo(new Long(1), vendor, SystemType.PLATFORM);
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
		assertEquals("测试用企业", licenseForm.getName());
		assertEquals("啊啊啊", licenseForm.getLegal_representative());
		assertEquals("所有范围", licenseForm.getBusiness_scope());
		assertEquals("我发的", licenseForm.getIssue_organization());
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
	public void selectVendorMDPLByIdTest()throws Exception {
		VendorMDPLForm form=OrgService.selectVendorMDPLById(new Long(1));
		System.err.println(form);
		assertEquals("wevlkjef02348",form.getLicense_sn());
		assertEquals("发我", form.getProduction_address());
		assertEquals("神佛我", form.getIssue_organization());
		assertEquals("是否为", form.getProduction_range());
	}
	
	@Test
	public void insertVendorMDPLTest() throws Exception {
		AddVendorMDPLForm form=new AddVendorMDPLForm();
		Date date=new Date(2019-04-16); 
		form.setEnd_date(date);
		form.setIssue_date(date);
		form.setLicense_sn("test");
		form.setProduction_address("test");
		form.setIssue_organization("test");
		form.setProduction_range("test");
		assertEquals(1, OrgService.insertVendorMDPL(new Long(2), form));
	}
	
	@Test
	public void updateVendorMDPLTest() throws Exception {
		VendorMDPLForm form = new VendorMDPLForm();
		form.setLicense_sn("test改");
		form.setProduction_address("test改");
		form.setIssue_organization("test改");
		assertEquals(1, OrgService.updateVendorMDPL(new Long(1), form, SystemType.PLATFORM));
	}
}
