package com.yrt.project.modular.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.math.BigDecimal;
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

import com.yrt.project.modular.product.domain.DealerProductUnit;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DealerProductUnitTests {

	@Autowired
	private DealerProductUnitMapper mapper;
	
	@Test
	public void d_add_dealer_product_unit()throws Exception{
		Map<String, Object> params =new HashMap<String, Object>();
		params.put("dealer_inner_sn_i", 1);
		params.put("product_inner_sn_i", 1);
		params.put("specification_inner_sn_i", 1);
		params.put("parent_unit_inner_sn_i",null);
		params.put("name_i", "经销商产品单元的单元测试");
		params.put("measure_i", 1);
		mapper.p_add_dealer_product_unit(params);
		assertEquals(0, Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void updateDealerProductUnitTest()throws Exception {
		DealerProductUnit unit =new DealerProductUnit();
		unit.setName("改");
		unit.setMeasure(new BigDecimal(2));
		unit.setDealer_inner_sn(new Long(1));
		unit.setProduct_inner_sn(new Long(1));
		unit.setSpecification_inner_sn(new Long(1));
		unit.setUnit_inner_sn(new Long(1));
		assertEquals(1, mapper.updateDealerProductUnit(unit));
	}
	
	@Test
	public void selectDealerProductUnitInfoTest() throws Exception {
		
		DealerProductUnit unit = new DealerProductUnit();
		unit.setDealer_inner_sn(new Long(1));
		unit.setProduct_inner_sn(new Long(1));
		unit.setSpecification_inner_sn(new Long(1));
		unit.setUnit_inner_sn(new Long(1));
		DealerProductUnit unit2=mapper.selectDealerProductUnitInfo(unit);
		System.err.println(unit2);
		assertEquals("经销商产品单元的单元测试", unit2.getName());
	}
	
	@Test
	public void selectDealerProductUnitListTest() throws Exception{
		DealerProductUnit unit=new DealerProductUnit();
		unit.setProduct_inner_sn(new Long(1));
		unit.setDealer_inner_sn(new Long(1));
		unit.setSpecification_inner_sn(new Long(1));
		List<DealerProductUnit> list= mapper.selectDealerProductUnitList(unit);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void selectDealerProductUnitListByPidTest() throws Exception{
		DealerProductUnit unit =new DealerProductUnit();
		unit.setDealer_inner_sn(new Long(1));
		unit.setProduct_inner_sn(new Long(1));
		unit.setSpecification_inner_sn(new Long(1));
		unit.setParent_unit_inner_sn(new Long(1));
		List<DealerProductUnit> list=mapper.selectDealerProductUnitListByPid(unit);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
}
