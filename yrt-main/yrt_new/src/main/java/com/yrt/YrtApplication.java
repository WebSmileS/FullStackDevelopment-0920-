package com.yrt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.framework.config.YrtConfig;

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication
@MapperScan({ "com.yrt.project.modular.**.mapper", "com.yrt.system.*.*.mapper", "com.yrt.system.*.mapper" })
public class YrtApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(YrtApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(YrtApplication.class);
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();

		objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);// NON_NULL

		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleModule module = new SimpleModule();
//		ToStringSerializer toStrSerializer = new ToStringSerializer();
//		module.addSerializer(Long.class, toStrSerializer);
//		module.addSerializer(long.class, toStrSerializer);
		objectMapper.registerModule(module);
		return objectMapper;
	}
	
	@Bean
	public CompressedIdGenerator idGenerator(YrtConfig config) {
		return CompressedIdGenerator.initDefaultInstance(config.getDataNodeId());
	}

}
