package com.ruoyi.formdata.datasource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 动态数据源配置
 */
@Configuration
public class DynamicDataSourceConfig {

    /**
     * 注册租户数据源注册表
     */
    @Bean
    public TenantDataSourceRegistry tenantDataSourceRegistry() {
        return new TenantDataSourceRegistry();
    }

    /**
     * 注册租户感知的动态数据源
     * 扩展框架的DynamicDataSource
     */
    @Bean
    @Primary
    public TenantAwareDynamicDataSource tenantAwareDynamicDataSource(
            @Qualifier("dynamicDataSource") DataSource dynamicDataSource,
            TenantDataSourceRegistry registry) {
        // 创建租户感知的动态数据源
        // 使用已有的动态数据源作为默认数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        return new TenantAwareDynamicDataSource(dynamicDataSource, targetDataSources, registry);
    }

    /**
     * 初始化数据源服务
     */
    @Bean
    public DynamicDataSourceInitializer dynamicDataSourceInitializer(
            DynamicDataSourceService dataSourceService) {
        return new DynamicDataSourceInitializer(dataSourceService);
    }
}