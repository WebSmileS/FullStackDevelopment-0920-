package com.yrt.framework.config;

import java.util.Map;

import javax.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.xss.XssFilter;

/**
 * Filter配置
 *
 * @author Runner
 */
@Configuration
public class FilterConfig
{
    @Value("${xss.enabled}")
    private String enabled;
    
    @Value("${xss.isIncludeRichText}")
    private String isIncludeRichText;

    @Value("${xss.excludes}")
    private String excludes;

    @Value("${xss.urlPatterns}")
    private String urlPatterns;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean xssFilterRegistration()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns(StringUtils.split(urlPatterns, ","));
        registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        Map<String, String> initParameters = Maps.newHashMap();
        initParameters.put("excludes", excludes);
        initParameters.put("enabled", enabled);
        initParameters.put("isIncludeRichText", isIncludeRichText);
        registration.setInitParameters(initParameters);
        return registration;
    }
}
