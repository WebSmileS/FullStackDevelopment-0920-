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
public class PlatformDeptControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired  
    private WebApplicationContext wac;
	
	@Before // 在测试开始前初始化工作  
    public void setup() {  
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();  
    } 
	
	@Test
	public void addPlatformDeptTest()throws Exception{
		String result=mockMvc.perform(post("/v1/platform/dept/add")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "不知道")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void updatePlatformDeptTest()throws Exception{
		String result=mockMvc.perform(put("/v1/platform/dept/edit")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "知道")
				.param("department_inner_sn", "552")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void addPlatformEmpTest()throws Exception{
		String result=mockMvc.perform(post("/v1/platform/emp/add")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "光")
				.param("department_inner_sn", "552")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void editPlatformEmpTest()throws Exception{
		String result=mockMvc.perform(put("/v1/platform/emp/edit")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "hei")
				.param("employee_inner_sn", "1")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
}
