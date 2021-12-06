package com.yrt.project.modular.category;

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

import com.yrt.project.modular.category.domain.Code68;
import com.yrt.project.modular.category.domain.ProductType;
import com.yrt.project.modular.category.mapper.CategoryMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryTest {

	@Autowired
	private CategoryMapper categoryMapper;
	
	@Test
	public void selectCode68ByIdTest() throws Exception{
		Long id=new Long(6801); 
		Code68 code68= categoryMapper.selectCode68ById(id);
		/* System.err.print(code68); */
		assertEquals(new Long(0), code68.getParent_code68_sn());
		assertEquals(new Integer(0), code68.getManagement_type());
		assertEquals("基础外科手术器械", code68.getName());
	}
	
	@Test
	public void selectCode68ChildrenByIdTest() throws Exception {
		Long id=new Long(6801);
		List<Code68>list=categoryMapper.selectCode68ChildrenById(id);
		/* System.err.println(list); */
		assertNotSame(0, list);
	}
	
	@Test
	public void selectCode68MogamiTest() throws Exception {
		List<Code68> list= categoryMapper.selectCode68Mogami();
		/* System.err.println(list); */
		assertNotSame(0, list);
	}
	
	@Test
	public void selectTopLevelProductTypeTest() throws Exception{
		Map<String,Object> params = new HashMap<>();
		List<ProductType> list = categoryMapper.selectTopLevelProductType(params);
		System.err.println(list);
		assertNotSame(0, list);
	}
	
	@Test
	public void selectProductTypeTest() throws Exception {
		Long id=new Long(1);
		Map<String,Object> params = new HashMap<>();
		params.put("type_inner_sn", id);
		ProductType productType=categoryMapper.selectProductTypeInfo(params);
		System.err.println(productType);
		assertEquals(null, productType.getParent_type_inner_sn());
		assertEquals(new Integer(1), productType.getStatus());
		assertEquals("通用", productType.getType());
	}
	
	@Test
	public void selectProductTypeChildrenListTest() throws Exception {
		Long id = new Long(1);
		List<ProductType>list=categoryMapper.selectProductTypeChildrenList(id);
		System.err.println(list);
		assertNotSame(0, list);
	}
	
	@Test
	public void insertProductTypeTest() throws Exception{
		ProductType productType=new ProductType();
		productType.setParent_type_inner_sn(new Long(1));
		productType.setStatus(1);
		productType.setType("单元测试类型");
		productType.setDescription("");
		System.err.println(productType.getType_inner_sn());
		assertEquals(1,categoryMapper.insertProductType(productType));
		System.err.println(productType.getType_inner_sn());
	}
	
	@Test
	public void updateProductTypeTest() throws Exception{
		ProductType productType = new ProductType();
		productType.setType_inner_sn(new Long(119));
		productType.setParent_type_inner_sn(new Long(2));
		productType.setStatus(1);
		productType.setType("单元测试类型修改");
		productType.setDescription("");
		assertEquals(1, categoryMapper.updateProductType(productType));
	}
	
	
}
