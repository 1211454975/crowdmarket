package com.ruoyi.formdata.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ruoyi.common.utils.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.ruoyi.framework.datasource.DynamicDataSource;
import com.ruoyi.framework.config.properties.DruidProperties;
import com.ruoyi.metadata.domain.TenantDatabase;
import com.ruoyi.metadata.service.ITenantDatabaseService;

/**
 * 动态数据源配置
 *
 * @author ruoyi
 */
@Slf4j
@Configuration
public class DynamicDataSourceConfig
{
    @Autowired(required = false)
    private ITenantDatabaseService tenantDatabaseService;

    @Autowired
    private DruidProperties druidProperties;

    /**
     * 创建动态数据源
     */
    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource masterDataSource)
    {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource);

        // 加载所有租户数据源
        loadTenantDataSources(targetDataSources);

        return new DynamicDataSource(masterDataSource, targetDataSources);
    }

    /**
     * 加载所有租户数据源
     *
     * @param targetDataSources 数据源映射
     */
    private void loadTenantDataSources(Map<Object, Object> targetDataSources)
    {
        // 检查租户数据库服务是否可用
        if (tenantDatabaseService == null) {
            log.warn("租户数据库服务不可用，跳过加载租户数据源");
            return;
        }

        // 查询所有启用的租户数据库配置
        TenantDatabase query = new TenantDatabase();
        query.setStatus(1); // 启用状态
        List<TenantDatabase> tenantDatabases = tenantDatabaseService.selectTenantDatabaseList(query);

        for (TenantDatabase tenantDatabase : tenantDatabases)
        {
            String tenantId = tenantDatabase.getTenantId();

            // 创建租户数据源
            DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
            dataSource.setUrl(buildJdbcUrl(tenantDatabase));
            dataSource.setUsername(tenantDatabase.getDbUsername());
            dataSource.setPassword(tenantDatabase.getDbPassword());

            // 应用Druid配置
            druidProperties.dataSource(dataSource);

            // 添加到数据源映射
            targetDataSources.put(tenantId, dataSource);
        }
    }

    /**
     * 构建JDBC URL
     *
     * @param tenantDatabase 租户数据库配置
     * @return JDBC URL
     */
    private String buildJdbcUrl(TenantDatabase tenantDatabase)
    {
        return String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%%2B8",
                tenantDatabase.getDbHost(),
                tenantDatabase.getDbPort(),
                tenantDatabase.getDbName());
    }

    /**
     * 动态添加租户数据源
     *
     * @param tenantDatabase 租户数据库配置
     * @return 数据源
     */
    public DataSource addTenantDataSource(TenantDatabase tenantDatabase)
    {
        // 创建租户数据源
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setUrl(buildJdbcUrl(tenantDatabase));
        dataSource.setUsername(tenantDatabase.getDbUsername());
        dataSource.setPassword(tenantDatabase.getDbPassword());

        // 应用Druid配置
        druidProperties.dataSource(dataSource);

        // 获取动态数据源
        DynamicDataSource dynamicDataSource = (DynamicDataSource) SpringUtils.getBean("dynamicDataSource");

        // 添加到数据源映射
        dynamicDataSource.addDataSource(tenantDatabase.getTenantId(), dataSource);

        return dataSource;
    }

    /**
     * 移除租户数据源
     *
     * @param tenantId 租户ID
     */
    public void removeTenantDataSource(String tenantId)
    {
        // 获取动态数据源
        DynamicDataSource dynamicDataSource = (DynamicDataSource) SpringUtils.getBean("dynamicDataSource");

        // 从数据源映射中移除
        dynamicDataSource.removeDataSource(tenantId);
    }
}