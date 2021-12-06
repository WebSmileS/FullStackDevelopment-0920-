package com.yrt.framework.config;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiOperation;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
@ConditionalOnProperty(value = {"knife4j.enable"}, matchIfMissing = true)
public class SwaggerConfig {

	/** 系统基础配置 */
    @Autowired
    private YrtConfig yrtConfig;
    
    @Bean(value = "defaultApi")
    public Docket createRestApi() {
    	ParameterBuilder parameterBuilder1 = new ParameterBuilder();
    	ParameterBuilder parameterBuilder2 = new ParameterBuilder();
    	ParameterBuilder parameterBuilder3 = new ParameterBuilder();
    	ParameterBuilder parameterBuilder4 = new ParameterBuilder();
    	ParameterBuilder parameterBuilder5 = new ParameterBuilder();
    	ParameterBuilder parameterBuilder6 = new ParameterBuilder();
        List<Parameter> parameters= Lists.newArrayList();
        parameterBuilder1.name("systemType").modelRef(new ModelRef("Integer")).defaultValue("1")
                .parameterType("header")
                .required(true).build();
        parameterBuilder2.name("uid").modelRef(new ModelRef("Long")).defaultValue("23")
        .parameterType("header")
        .required(true).build();
        
        parameterBuilder3.name("Content-Type").modelRef(new ModelRef("String")).defaultValue("application/json")
        .parameterType("header")
        .required(true).build();
        parameterBuilder4.name("pageNum").modelRef(new ModelRef("Integer")).defaultValue("1")
        .parameterType("query")
        .required(true).build();
        parameterBuilder5.name("pageSize").modelRef(new ModelRef("Integer")).defaultValue("10")
        .parameterType("query")
        .required(true).build();
        parameterBuilder6.name("oid").modelRef(new ModelRef("Long")).defaultValue("1")
        .parameterType("header")
        .required(true).build();
        parameters.add(parameterBuilder3.build());
        parameters.add(parameterBuilder1.build());
        parameters.add(parameterBuilder2.build());
        parameters.add(parameterBuilder4.build());
        parameters.add(parameterBuilder5.build());
        parameters.add(parameterBuilder6.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("1-默认接口")
                .select()
                // 扫描所有有注解的api，用这种方式更灵活
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .apis(RequestHandlerSelectors.basePackage("com.yrt.project.api.business;com.yrt.project.api.common;com.yrt.project.api.platform;com.yrt.project.api.app.product"))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(parameters);
//                .pathMapping("/");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("医润通API")
                .description("医疗耗材供应链系统")
                .contact(new Contact(yrtConfig.getName(), "https://yrtdev.bjyixiu.com", "xxx@163.com"))
                .version(yrtConfig.getVersion())
                .build();
    }
    
    @Bean(value = "externalRestApi")
    public Docket createExternalApi() {
    	ParameterBuilder parameterBuilder1 = new ParameterBuilder();
    	ParameterBuilder parameterBuilder2 = new ParameterBuilder();
        List<Parameter> parameters= Lists.newArrayList();
        parameterBuilder1.name("pageNum").modelRef(new ModelRef("Integer")).defaultValue("1")
        .parameterType("query")
        .required(true).build();
        parameterBuilder2.name("pageSize").modelRef(new ModelRef("Integer")).defaultValue("10")
        .parameterType("query")
        .required(true).build();
        parameters.add(parameterBuilder1.build());
        parameters.add(parameterBuilder2.build());
    	return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(externalApiInfo())
                .groupName("2-外部调用接口")
                .select()
                // 扫描所有有注解的api，用这种方式更灵活
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.yrt.project.api.external"))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(parameters);
//                .pathMapping("/");
    }
    
    private ApiInfo externalApiInfo() {
        return new ApiInfoBuilder()
                .title("医润通外部调用API")
                .description("医疗耗材供应链系统")
                .contact(new Contact(yrtConfig.getName(), "https://yrt.bjyixiu.com", "xxx@163.com"))
                .version(yrtConfig.getVersion())
                .build();
    }
}