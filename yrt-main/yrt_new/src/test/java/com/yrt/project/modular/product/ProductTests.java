package com.yrt.project.modular.product;

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

import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductMDRF;
import com.yrt.project.modular.product.domain.ProductRelImage;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.project.modular.product.mapper.ProductMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductTests {

	@Autowired
	private ProductMapper mapper;

	/**
	 * 新增产品
	 * @param Map<String, Object> params
	 * @return
	 */
	@Test
	public void p_add_productTest()throws Exception{
		Map<String, Object> params =new HashMap<String, Object>();
//		params.put("vendor_inner_sn_i", 1);
//		params.put("release_employee_inner_sn_i", 1);
//		params.put("type_inner_sn_i", 1);
//		params.put("code68_sn_i", 680101);
//		params.put("data_source_i", 0);
//		params.put("storage_condition_i", 1);
//		params.put("health_care_type_i", 0);
//		params.put("name_i", "齐白石");
//		params.put("product_sn_i", "单元测试产品编号");
//		params.put("description_i", "单元测试描述");
//		params.put("specifications_i", "单元测试型号规格");
		params.put("vendor_inner_sn_i",1);
    	params.put("data_source_i", 0);
    	params.put("storage_condition_i", 1);
    	params.put("health_care_type_i", 0);
    	params.put("name_i", "test");
    	params.put("product_sn_i","");
    	params.put("description_i", "");
    	params.put("specifications_i", "");
    	params.put("type_i", 0);
		mapper.p_add_product(params);
		assertEquals(0,Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void updateProductTest()throws Exception{
		Product product=new Product();
		product.setVendor_inner_sn(new Long(1));
		product.setProduct_inner_sn(new Long(6));
		product.setName("test");
		product.setProduct_vendor_sn("test");
		product.setDescription("test");
		product.setSpecifications("test");
		assertEquals(1,mapper.updateProduct(product));
	}
	
	@Test
	public void updateProduct4MapTest()throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn", 1);
		params.put("product_inner_sn", 6);
		params.put("name", "test000");
		params.put("description", "test000");
		params.put("specifications", "test000");
		assertEquals(1, mapper.updateProduct4Map(params));
	}
	
	@Test
	public void deleteProductTest()throws Exception{
		Product product=new Product();
		product.setVendor_inner_sn(new Long(1));
		product.setProduct_inner_sn(new Long(1));
		assertEquals(1, mapper.deleteProduct(product));
	}
	
	
	@Test
	public void selectProductInfoTest()throws Exception{
		Product product=new Product();
		product.setVendor_inner_sn(new Long(1));
		product.setProduct_inner_sn(new Long(6));
		Product product2=mapper.selectProductInfo(product);
		System.err.println(mapper.selectProductInfo(product));
		assertEquals("齐白石", product2.getName());
		assertEquals("单元测试产品编号", product2.getProduct_vendor_sn());
	}
	
	@Test
	public void searchProductListTest()throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("release_status", 0);
		List<Product> list=mapper.searchProductList(params);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	@Test
	public void selectCertImageListTest() throws Exception{
		ProductRelImage info =new ProductRelImage();
		info.setVendor_inner_sn(new Long(1));
		info.setProduct_inner_sn(new Long(1));
		info.setType(3);
		List<ProductRelImage> list=mapper.selectCertImageList(info);
		System.err.println(list);
		assertNotSame(0, list.size());
	}
	
	/**
	 * 新增产品相关图片
	 * @param params
	 */
	@Test
	public void p_add_product_imageTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		Date date=new Date(2018-01-01);
		params.put("vendor_inner_sn_i",1);
		params.put("product_inner_sn_i", 6);
		params.put("type_i", 1);
		params.put("begin_date_i", date);
		params.put("end_date_i", date);
		params.put("url_i", "单元测试URL");
		mapper.p_add_product_image(params);
		assertEquals(0,Integer.parseInt(params.get("result_o").toString()));
	}

	
	/**
	 * 根据厂商ID和已发布产品ID的规格型号列表
	 * @param spec
	 * @return
	 */
	@Test
	public void selectProductSpecListByIdTest()throws Exception{
		ProductSpecification spec=new ProductSpecification();
		spec.setVendor_inner_sn(new Long(1));
		spec.setProduct_inner_sn(new Long(1));
		List<ProductSpecification> list=mapper.selectProductSpecListById(spec);
		System.err.print(list);
		assertNotSame(0, list.size());
	}
	
	/**
	 * 新增发布产品规格型号
	 * @param params
	 */
	@Test
	public void p_add_product_specificationTest()throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vendor_inner_sn_i", 1);
		params.put("product_inner_sn_i", 6);
		params.put("specification_i","单元测试规格型号test1");
		mapper.p_add_product_specification(params);
		assertEquals(0,Integer.parseInt(params.get("result_o").toString()));
	}
	
	@Test
	public void updateProductSpecTest()throws Exception {
		ProductSpecification spec = new ProductSpecification();
		spec.setVendor_inner_sn(new Long(1));
		spec.setProduct_inner_sn(new Long(6));
		spec.setSpecification_inner_sn(new Long(2));
		spec.setSpecification("改");
		Map<String,Object> params1 = new HashMap<>();
		params1.put("vendor_inner_sn", 1);
		params1.put("product_inner_sn", 6);
		params1.put("specification_inner_sn", 2);
		params1.put("specification", "改");
		assertEquals(1, mapper.updateProductSpec(params1));
	}
	
	@Test
	public void deleteProductSpecTest()throws Exception{
		ProductSpecification spec = new ProductSpecification();
		spec.setVendor_inner_sn(new Long(1));
		spec.setProduct_inner_sn(new Long(6));
		assertEquals(1, mapper.deleteProductSpec(spec));
	}
	
	@Test
	public void insertProductMDRFTest()throws Exception{
    	Map<String, Object> params = new HashMap<>();
    	params.put("vendor_inner_sn_i", new Long(1));
    	params.put("product_inner_sn_i", new Long(6));
    	params.put("issue_date_i", new Date(2018-01-01));
    	params.put("register_sn_i", "单元测注册号");
    	params.put("issue_organization_i", "单元测试发证部门");
    	params.put("standard_i", "单元测试产品标准");
    	params.put("production_address_i", "单元测试生产地址");
    	params.put("performance_structure_i", "单元测试性能结构及组成");
    	params.put("application_range_i", "单元测试适用范围");
    	params.put("contraindication_i", "单元测试适用范围");
    	params.put("description_i", "单元测试禁忌症");
    	params.put("specifications_i", "xxx");

    	mapper.p_add_product_mdrf(params);
		assertEquals(0, params.get("result_o"));
	}
	
	@Test
	public void updateProductMDRFTest()throws Exception {
		ProductMDRF mdrf=new ProductMDRF();
		mdrf.setVendor_inner_sn(new Long(1));
		mdrf.setProduct_inner_sn(new Long(5));
		mdrf.setRegister_sn("单元测注册号1");
		mdrf.setIssue_organization("单元测试发证部门1");
		mdrf.setStandard("单元测试产品标准1");
		assertEquals(1, mapper.updateProductMDRF(mdrf));
	}
	
	@Test
	public void updateProductMDRF4MapTest()throws Exception{
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("vendor_inner_sn", 1);
		params.put("product_inner_sn", 5);
		params.put("register_sn", "单元测注册号");
		params.put("issue_organization", "单元测试发证部门");
		params.put("standard", "单元测试产品标准");
		assertEquals(1, mapper.updateProductMDRF4Map(params));
	}
	
	@Test
	public void selectProductMDRFTest()throws Exception{
		ProductMDRF mdrf=new ProductMDRF();
		mdrf.setVendor_inner_sn(new Long(1));
		mdrf.setProduct_inner_sn(new Long(5));
		ProductMDRF mdrf2=mapper.selectProductMDRF(mdrf);
		assertEquals("sdddd", mdrf2.getRegister_sn());
		assertEquals("ddd", mdrf2.getIssue_organization());
		assertEquals("dd", mdrf2.getStandard());
	}
}
