package com.ruoyi.formdata.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Web MVC配置，用于注册租户数据库拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private TenantDatabaseInterceptor tenantDatabaseInterceptor;

    /**
     * 添加租户数据库拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加租户数据库拦截器，拦截所有表单数据相关的请求
        registry.addInterceptor(tenantDatabaseInterceptor)
                .addPathPatterns("/formdata/**")
                .addPathPatterns("/metadata/**");
    }
}