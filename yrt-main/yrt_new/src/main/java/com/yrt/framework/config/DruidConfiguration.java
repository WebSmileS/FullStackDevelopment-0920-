package com.yrt.framework.config;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.yrt.framework.config.properties.DruidProperties;
 
@Configuration
public class DruidConfiguration {
	
	@Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource masterDataSource(DruidProperties druidProperties) throws SQLException
    {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }
 
}