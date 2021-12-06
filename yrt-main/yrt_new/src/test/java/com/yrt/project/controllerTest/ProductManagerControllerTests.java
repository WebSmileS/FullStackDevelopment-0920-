package com.yrt.project.controllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.yrt.YrtApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=YrtApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ProductManagerControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired  
    private WebApplicationContext wac;
	
	@Before // 在测试开始前初始化工作  
    public void setup() {  
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();  
    } 
	
	@Test
	public void addGoodsTest()throws Exception{
		String result=mockMvc.perform(post("/v1/platform/product/add")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("vendor_inner_sn", "1")
				.param("name", "name")
				.param("health_care_type", "0")
				.param("storage_condition", "0")
				)
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void editProductTest()throws Exception{
		String result=mockMvc.perform(put("/v1/platform/product/edit/1/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		
				.param("name", "name")
			
				)
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void addGoodsTest1()throws Exception{
		String result=mockMvc.perform(post("/v1/platform/product/image/add")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("vendor_inner_sn", "1")
				.param("product_inner_sn", "1")
				.param("type", "1")
				.param("url", "123456789www")
				)
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void publicProductTest()throws Exception{
		String result=mockMvc.perform(put("/v1/platform/product/public/5/8")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "不知道")
				)
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void addProductSpecTest()throws Exception{
		String result =mockMvc.perform(post("/v1/platform/product/spec/add/2/2/1ada0")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
	System.err.println(result);
				
	}
	
	@Test
	public void editProductSpecTest()throws Exception{
		String result =mockMvc.perform(put("/v1/platform/product/spec/edit/2/2/1/10000")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
	System.err.println(result);
	}
	
	@Test
	public void addProductMDRFTest() throws Exception{
		String result =mockMvc.perform(post("/v1/platform/product/mdrf/add/1/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("register_sn", "hapy")
				.param("issue_date", "2019-05-05")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
	System.err.println(result);
	}
	
	@Test
	public void editProductMDRFTest()throws Exception{
		String result =mockMvc.perform(put("/v1/platform/product/mdrf/edit/2/4")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("register_sn", "hapy")
				.param("issue_date", "2019-05-05")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
	System.err.println(result);
	}
}
