package com.yrt.project.modular.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

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

import com.yrt.project.modular.product.domain.ProductUnit;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductUnitTest {

	@Autowired
	private ProductUnitMapper mapper;
	
	@Test
	public void p_add_product_unitTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("vendor_inner_sn_i", 1);
		params.put("product_inner_sn_i",1);
		params.put("specification_inner_sn_i",1);
		params.put("parent_unit_inner_sn_i", 2);
		params.put("name_i","test");
		params.put("measure_i", 1);
		mapper.p_add_product_unit(params);
		assertEquals(0,Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void updateProductUnitTest()throws Exception{
		ProductUnit unit=new ProductUnit();
		unit.setVendor_inner_sn(new Long(1));
		unit.setProduct_inner_sn(new Long(1));
		unit.setSpecification_inner_sn(new Long(1));
		unit.setUnit_inner_sn(new Long(3));
		unit.setName("单元测试改");
		assertEquals(1, mapper.updateProductUnit(unit));
	}
	
	@Test
	public void selectProductUnitInfoTest()throws Exception{
		ProductUnit unit= new ProductUnit();
		unit.setVendor_inner_sn(new Long(1));
		unit.setProduct_inner_sn(new Long(1));
		unit.setSpecification_inner_sn(new Long(1));
		unit.setUnit_inner_sn(new Long(2));
		System.err.println(mapper.selectProductUnitInfo(unit));
		assertEquals("test", mapper.selectProductUnitInfo(unit).getName());
	}
	
	@Test
	public void selectProductUnitListTest()throws Exception {
		ProductUnit unit=new ProductUnit();
		unit.setVendor_inner_sn(new Long(1));
		unit.setProduct_inner_sn(new Long(1));
		unit.setSpecification_inner_sn(new Long(1));
		List<ProductUnit>list=mapper.selectProductUnitList(unit);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void selectProductUnitListByPidTest()throws Exception{
		ProductUnit unit=new ProductUnit();
		unit.setVendor_inner_sn(new Long(1));
		unit.setProduct_inner_sn(new Long(1));
		unit.setSpecification_inner_sn(new Long(1));
		unit.setParent_unit_inner_sn(new Long(2));
		List<ProductUnit>list=mapper.selectProductUnitListByPid(unit);
		assertNotSame(0, list.size());
	}
}
