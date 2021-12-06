package com.yrt.project.controllerTest;

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
public class OrgRegisterAuditControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired  
    private WebApplicationContext wac;
	
	@Before // 在测试开始前初始化工作  
    public void setup() {  
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();  
    } 
	
	
	
	@Test
	public void updateNoAuditOrgTest()throws Exception{
		String result=mockMvc.perform(put("/v1/platform/orgReg/edit/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "good good stady")
				.param("social_credit_code", "社会信用代码")
				.param("administrative_division_sn", "330604")
				.param("registrant", "注册人")
				.param("registrant_phone", "15975389543")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
	System.err.println(result);
	}
	
	@Test
	public void passNoAuditOrgTest()throws Exception{
		String result = mockMvc.perform(put("/v1/platform/orgReg/pass/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
	System.err.println(result);
	}
	
	@Test
	public void nopassNoAuditOrgTest() throws Exception{
		String result = mockMvc.perform(put("/v1/platform/orgReg/nopass/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
}
