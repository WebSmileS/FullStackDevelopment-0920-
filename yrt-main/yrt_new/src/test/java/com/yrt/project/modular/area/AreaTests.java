package com.yrt.project.modular.area;

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

import com.yrt.project.modular.area.domain.AdministrativeDivision;
import com.yrt.project.modular.area.mapper.AdministrativeDivisionMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AreaTests {

	@Autowired
	private AdministrativeDivisionMapper administrativeDivisionMapper;
	
	@Test
	public void selectProvinceListTest() throws Exception{
		List<AdministrativeDivision> admin=  administrativeDivisionMapper.selectProvinceList();
		assertNotSame(0, admin.size());
	}
	
	@Test
	public void selectChildrenListTest() throws Exception{
		Long id=new Long(110000);
		List<AdministrativeDivision>list = administrativeDivisionMapper.selectChildrenList(id);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void selectParentTest() throws Exception{
		Long id=new Long(110119);
		AdministrativeDivision admin=administrativeDivisionMapper.selectParent(id);
		System.err.print(admin);
		assertEquals(new Long(110100), admin.getAdministrative_division_sn());
		assertEquals(new Long(110000), admin.getParent_administrative_division_sn());
		assertEquals(new Integer(1), admin.getStatus());
		assertEquals("北京市市辖区", admin.getName());
	}
	
	@Test
	public void selectDivisionByIdTest() throws Exception{
		Long id = new Long(110107);
		AdministrativeDivision admin=administrativeDivisionMapper.selectDivisionById(id);
		assertEquals(new Long(110100), admin.getParent_administrative_division_sn());
		assertEquals(new Integer(1), admin.getStatus());
		assertEquals("石景山区", admin.getName());
	}
	
}
