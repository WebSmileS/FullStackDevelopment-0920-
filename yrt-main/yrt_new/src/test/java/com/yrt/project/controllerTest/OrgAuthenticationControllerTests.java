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
public class OrgAuthenticationControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired  
    private WebApplicationContext wac;
	
	@Before // 在测试开始前初始化工作  
    public void setup() {  
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();  
    } 
	
	/**
	 * 这个是新增图片的.....
	 * @throws Exception
	 */
	@Test
	public void addGoodsTest()throws Exception{
		String result=mockMvc.perform(post("/v1/platform/org/image/add/2/1/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("url", "testestestestest")
				)
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void passTest()throws Exception{
		String result = mockMvc.perform(put("/v1/platform/org/pass/2/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void noPassTest()throws Exception{
		String result = mockMvc.perform(put("/v1/platform/org/nopass/2/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void editOrgInfoTest()throws Exception{
		String result = mockMvc.perform(put("/v1/platform/org/edit/2/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "测试经销商4")
				.param("social_credit_code", "1254896256QWERTYUI")
				.param("enterprise_type", "1")
				.param("administrative_division_sn", "140105")
				.param("legal_representative", "奇怪的法定代表人")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void addOrgLicenseInfoByIdTest()throws Exception{
		String result = mockMvc.perform(post("/v1/platform/org/add/license/2/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "测试经销商营业执照")
				.param("social_credit_code", "1254896256QWERTYUI")
				.param("enterprise_type", "1")
				.param("issue_date", "2019-04-30")
				.param("begin_date", "2019-04-30")
				.param("end_date", "2019-04-30")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
			System.err.println(result);
	}
	
	@Test
	public void editOrgLicenseInfoTest()throws Exception{
		String result = mockMvc.perform(put("/v1/platform/org/edit/license/2/2")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "测试经销商营业执照")
				.param("social_credit_code", "1254896256QWERTYUI")
				.param("enterprise_type", "1")
				.param("issue_date", "2019-04-30")
				.param("begin_date", "2019-04-30")
				.param("end_date", "2019-04-30")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	
	@Test
	public  void addHospitalPLHMIByIdTest()throws Exception{
		String result = mockMvc.perform(post("/v1/platform/org/add/plhmi/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "企业名称")
				.param("registered_number", "登记号")
				.param("begin_date", "2019-04-30")
				.param("end_date", "2019-04-30")
				.param("issue_date", "2019-04-30")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public  void editHospitalPLHMIByIdTest()throws Exception{
		String result = mockMvc.perform(put("/v1/platform/org/edit/plhmi/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "企业名称改")
				.param("registered_number", "登记号改")
				.param("begin_date", "2019-05-01")
				.param("end_date", "2019-05-01")
				.param("issue_date", "2019-05-01")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void addDealerMAAIOLByIdTest()throws Exception{
		String result = mockMvc.perform(post("/v1/platform/org/add/maaiol/1")
				.param("name", "企业名称")
				.param("license_sn", "许可证编号")
				.param("operation_mode", "1")
				.param("end_date", "2019-04-30")
				.param("issue_date", "2019-04-30")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void editDealerMAAIOLByIdTest()throws Exception{
		String result = mockMvc.perform(put("/v1/platform/org/edit/maaiol/1")
				.param("name", "企业名称改")
				.param("license_sn", "许可证编号改")
				.param("operation_mode", "0")
				.param("end_date", "2019-04-29")
				.param("issue_date", "2019-04-29")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void addDealerSTMDBRCByIdTest() throws Exception{
		String result = mockMvc.perform(post("/v1/platform/org/add/stmdbrc/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "经销商第二类医疗器械经营备案凭证")
				.param("operation_mode", "0")
				.param("issue_date", "2019-04-29")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	
	@Test
	public void editDealerSTMDBRCByIdTest()throws Exception{
		String result = mockMvc.perform(put("/v1/platform/org/edit/stmdbrc/1")
				.param("name", "经销商第二类医疗器械经营备案凭证改")
				.param("operation_mode", "1")
				.param("issue_date", "2019-04-30")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void addVenderMDPLTest()throws Exception{
		String result = mockMvc.perform(post("/v1/platform/org/add/mdpl/2")
				.param("name", "厂商医疗器械生产许可证")
				.param("end_date", "2019-04-30")
				.param("issue_date", "2019-04-30")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void updateVenderMDPLTest()throws Exception{
		String result = mockMvc.perform(put("/v1/platform/org/edit/mdpl/2")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "厂商医疗器械生产许可证改")
				.param("end_date", "2019-04-29")
				.param("issue_date", "2019-04-29")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
	
	@Test
	public void addVenderTest()throws Exception{
		String result = mockMvc.perform(post("/v1/platform/org/vendor/add")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("name", "厂商")
				
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString();
		System.err.println(result);
	}
}
