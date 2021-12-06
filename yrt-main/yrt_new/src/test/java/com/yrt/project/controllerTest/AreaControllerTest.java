package com.yrt.project.controllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yrt.common.client.HttpAPIService;
import com.yrt.common.client.HttpResult;
import com.yrt.common.dict.SystemType;
import com.yrt.project.api.external.warehouse.SyncWarehouseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.yrt.YrtApplication;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = YrtApplication.class)
@ActiveProfiles("prod")
@AutoConfigureMockMvc
public class AreaControllerTest {

    @Autowired
    SyncWarehouseService syncWarehouseService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before // 在测试开始前初始化工作
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void getProvinceListTest() throws Exception {
        String result = mockMvc.perform(get("/v1/comm/area/province"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.err.println(result);
    }


    @Test
    public void main() {
        try {
            syncWarehouseService.sendPutin(5L, SystemType.HOSPITAL, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
