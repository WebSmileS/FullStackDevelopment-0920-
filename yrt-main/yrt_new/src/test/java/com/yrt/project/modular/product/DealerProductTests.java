package com.yrt.project.modular.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

import com.yrt.project.modular.product.domain.DealerProduct;
import com.yrt.project.modular.product.domain.DealerProductRelImage;
import com.yrt.project.modular.product.domain.DealerProductSpecification;
import com.yrt.project.modular.product.mapper.DealerProductMapper;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DealerProductTests {

	@Autowired
	private DealerProductMapper mapper;
	
	@Test
	public void d_add_dealer_productTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("dealer_inner_sn_i", 1);
		params.put("create_employee_inner_sn_i", 1);
		params.put("type_inner_sn_i", 1);
		params.put("code68_sn_i", 6801);
		params.put("storage_condition_i", 1);
		params.put("health_care_type_i", 0);
		params.put("name_i", "单元测试添加经销商产品");
		params.put("product_sn_i", "测试产品编号");
		params.put("description_i", "描述");
		params.put("specifications_i", "测试型号规格");
		mapper.d_add_dealer_product(params);
		assertEquals(0,Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void updateDealerProductTest()throws Exception{
		Map<String,Object> params = new HashMap<>();
		params.put("name", "单元测试改");
		params.put("dealer_inner_sn", 1L);
		params.put("product_inner_sn", 1L);
		assertEquals(1, mapper.updateDealerProduct(params));
	}
	
	@Test
	public void updateDealerProduct4MapTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("dealer_inner_sn", 1);
		params.put("product_inner_sn", 1);
		params.put("name", "改4map");
		assertEquals(1, mapper.updateDealerProduct4Map(params));
	}
	
	@Test
	public void deleteDealerProductTest()throws Exception{
		DealerProduct product =new DealerProduct();
		product.setDealer_inner_sn(new Long(1));
		product.setProduct_inner_sn(new Long(1));
		assertEquals(1, mapper.deleteDealerProduct(product));
	}
	
	@Test
	public void selectDealerProductInfoTest()throws Exception{
		DealerProduct product =new DealerProduct();
		product.setDealer_inner_sn(new Long(1));
		product.setProduct_inner_sn(new Long(1));
		System.err.println(mapper.selectDealerProductInfo(product));
		assertEquals("单元测试添加经销商产品", mapper.selectDealerProductInfo(product).getName());
	}
	
	@Test
	public void searchDealerProductListTest()throws Exception{
		DealerProduct product =new DealerProduct();
		product.setName("单元测试添加经销商产品");
		List<DealerProduct>list=mapper.searchDealerProductList(product);
		System.err.println(list);
		assertNotEquals(0, list.size());
	}
	
	@Test
	public void selectCertImageListTest()throws Exception{
		DealerProductRelImage info=new DealerProductRelImage();
		info.setDealer_inner_sn(new Long(1));
		info.setProduct_inner_sn(new Long(1));
		info.setType(1);
		List<DealerProductRelImage>list=mapper.selectCertImageList(info);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void d_add_dealer_product_imageTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		Date date=new Date(2000-01-01);
		params.put("dealer_inner_sn_i", 1);
		params.put("product_inner_sn_i", 1);
		params.put("type_i", 1);
		params.put("begin_date_i", date);
		params.put("end_date_i", date);
		params.put("url_i", "test");
		mapper.d_add_dealer_product_image(params);
		assertEquals(0, Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void selectDealerProductSpecListByIdTestTest()throws Exception{
		DealerProductSpecification spec =new DealerProductSpecification();
		spec.setDealer_inner_sn(new Long(1));
		spec.setProduct_inner_sn(new Long(1));
		List<DealerProductSpecification>list=mapper.selectDealerProductSpecListById(spec);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void d_add_dealer_product_specificationTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("dealer_inner_sn_i", 1);
		params.put("product_inner_sn_i", 1);
		params.put("specification_i", "test");
		mapper.d_add_dealer_product_specification(params);
		assertEquals(0, Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void updateDealerProductSpecTest()throws Exception{
		DealerProductSpecification spec=new DealerProductSpecification();
		spec.setSpecification("改");
		spec.setDealer_inner_sn(new Long(1));
		spec.setProduct_inner_sn(new Long(1));
		assertEquals(1, mapper.updateDealerProductSpec(spec));
	}
	
	@Test
	public void deleteDealerProductSpecTest()throws Exception{
		DealerProductSpecification spec=new DealerProductSpecification();
		spec.setDealer_inner_sn(new Long(1));
		spec.setProduct_inner_sn(new Long(1));
		assertEquals(1, mapper.deleteDealerProductSpec(spec));
	}
}
