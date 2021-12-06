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

import com.yrt.project.modular.product.domain.DealerProductType;
import com.yrt.project.modular.product.mapper.DealerProductTypeMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DealerProductTypeTests {
	
	@Autowired
	private DealerProductTypeMapper mapper;
	
	@Test
	public void d_add_dealer_product_typeTest()throws Exception {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("dealer_inner_sn_i",1);
		params.put("parent_type_inner_sn_i", 1);
		params.put("type_i", "单元测试类型");
		params.put("description_i", "描述");
		mapper.d_add_dealer_product_type(params);
		assertEquals(0,Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void selectTopLevelProductTypeTest()throws Exception{
		List<DealerProductType> list=mapper.selectTopLevelProductType(new Long(1));
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void selectProductTypeTest()throws Exception{
		DealerProductType type =new DealerProductType();
		type.setDealer_inner_sn(new Long(1));
		type.setType_inner_sn(new Long(1));
		System.err.println(mapper.selectProductType(type));
		assertEquals("单元测试类型", mapper.selectProductType(type).getType());
	}
	
	@Test
	public void selectProductTypeChildrenListTest() throws Exception{
		DealerProductType type=new DealerProductType();
		type.setParent_type_inner_sn(new Long(1));
		type.setDealer_inner_sn(new Long(1));
		List<DealerProductType>list=mapper.selectProductTypeChildrenList(type);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void updateProductTypeTest()throws Exception{
		DealerProductType type=new DealerProductType();
		type.setType_inner_sn(new Long(2));
		type.setDealer_inner_sn(new Long(1));
		type.setType("改");
		assertEquals(1, mapper.updateProductType(type));
	}
	
}
