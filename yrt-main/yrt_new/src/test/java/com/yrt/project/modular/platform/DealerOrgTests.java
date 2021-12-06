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
import com.yrt.project.api.platform.service.IDealerOrgService;
import com.yrt.project.api.platform.vo.organization.AddDealerMAAIOLForm;
import com.yrt.project.api.platform.vo.organization.AddDealerSTMDBRCForm;
import com.yrt.project.api.platform.vo.organization.AddOrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.AddOrgRelImageForm;
import com.yrt.project.api.platform.vo.organization.DealerMAAIOLForm;
import com.yrt.project.api.platform.vo.organization.DealerSTMDBRCForm;
import com.yrt.project.api.platform.vo.organization.EditOrgInfo;
import com.yrt.project.api.platform.vo.organization.OrgLicenseForm;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.Dealer;
import com.yrt.project.modular.organization.domain.OrgRelImage;
import com.yrt.project.modular.organization.domain.SearchForm;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DealerOrgTests {
	
	@Autowired
	private IDealerOrgService OrgService;
	
	@Test
	public void searchOrgListTest()throws Exception {
		SearchForm form=new SearchForm();
		form.setStatus(1);
		form.setName("测试经销商玄泽经销商");
		List<?>list= new ArrayList<Dealer>();
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
		assertEquals(1,OrgService.insertOrgRelImage(new Long(1), 1, form));
	}
	
	@Test
	public void getImageListByOrgIdTest()throws Exception {
		List<OrgRelImage>list=OrgService.getImageListByOrgId(new Long(1));
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void selectCertImageList2Test()throws Exception {
		List<OrgRelImage>list=OrgService.selectCertImageList2( new Long(1), 1);
		System.err.println(list);
		assertNotSame(0, list.size());
		}
	
//	@Test
//	public void orgAuthPassTest() throws Exception{
//		assertEquals(1, OrgService.orgAuthPass(2, new Long(1)));
//	}
//	
//	@Test
//	public void orgAuthNoPassTest() throws Exception{
//		assertEquals(1, OrgService.orgAuthNoPass(new Long(1)));
//	}
	
	@Test
	public void selectNoAuditOrgInfoTest() throws Exception {
		Company dealer=OrgService.selectNoAuditOrgInfo(new Long(1));
		assertEquals("测试经销商玄泽经销商", dealer.getName());
		assertEquals("", dealer.getLegal_representative());
	}
	
	@Test
	public void updateNoAuditOrgInfoTest() throws Exception{
		EditOrgInfo dealer =new EditOrgInfo();
		dealer.setName("test_0");
		dealer.setLegal_representative("test_1");
		int num = OrgService.updateNoAuditOrgInfo(new Long(1), dealer, SystemType.PLATFORM);
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
		assertEquals("测试经销商玄泽经销商", licenseForm.getName());
		assertEquals("", licenseForm.getLegal_representative());
		assertEquals(null, licenseForm.getBusiness_scope());
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
		assertEquals(1, OrgService.insertOrgLicenseInfo(new Long(1), form));
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
	public void selectDealerMAAIOLTest()throws Exception {
		DealerMAAIOLForm form=OrgService.selectDealerMAAIOL(new Long(1));
		System.err.print(form);
		assertEquals(null, form.getOperation_mode());
		assertEquals("测试经销商玄泽经销商", form.getName());
		assertEquals("", form.getLegal_representative());
	}
	
	@Test
	public void insertDealerMAAIOLTest()throws Exception {
		Date date= new Date(2019-02-10);
		AddDealerMAAIOLForm form =new AddDealerMAAIOLForm();
		form.setOperation_mode(1);
		form.setEnd_date(date);
		form.setIssue_date(date);
		form.setIssue_organization("test登记机关");
		form.setBusiness_scope("昨天到今天");
		form.setName("NameTest");
		form.setLegal_representative("testLegal_representative");
		form.setAddress("testAddress");
		form.setPremises("testPremises");
		form.setWarehouse_address("testWarehouse_address");
		form.setLicense_sn("1352313");
		assertEquals(1, OrgService.insertDealerMAAIOL(new Long(1), form));
	}
	
	@Test
	public void updateDealerMAAIOLTest() throws Exception {
		DealerMAAIOLForm form =new DealerMAAIOLForm();
		form.setOperation_mode(1);
		assertEquals(1, OrgService.updateDealerMAAIOL(new Long(1), form, SystemType.PLATFORM));
	}
	
	@Test
	public void selectDealerSTMDBRCTest() throws Exception{
		DealerSTMDBRCForm form =OrgService.selectDealerSTMDBRC(new Long(1));
		System.err.println(form);
		assertEquals(null,form.getOperation_mode());
		assertEquals(null, form.getRecord_sn());
		assertEquals("",form.getPremises());
		assertEquals("",form.getWarehouse_address());
		assertEquals("", form.getIssue_organization());
		assertEquals("", form.getBusiness_scope());
	}
	@Test
	public void insertDealerSTMDBRCTest() throws Exception {
		AddDealerSTMDBRCForm form=new AddDealerSTMDBRCForm();
		form.setOperation_mode(1);
		form.setRecord_sn("testRecord_sn");
		form.setPrincipal("testPrincipal");
		form.setIssue_date(new Date(2019-02-10));
		form.setIssue_organization("test登记机关");
		form.setBusiness_scope("昨天到今天");
		form.setName("NameTest");
		form.setLegal_representative("testLegal_representative");
		form.setAddress("testAddress");
		form.setPremises("testPremises");
		form.setWarehouse_address("testWarehouse_address");
		assertEquals(1, OrgService.insertDealerSTMDBRC(new Long(1), form));
	}
	
	@Test
	public void updateDealerSTMDBRCTest()throws Exception {
		DealerSTMDBRCForm form=new DealerSTMDBRCForm();
		form.setOperation_mode(2);
		assertEquals(1, OrgService.updateDealerSTMDBRC(new Long(1), form, SystemType.PLATFORM));
	}
}
