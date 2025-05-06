package com.ruoyi.framework.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.sql.DataSource;

import com.ruoyi.framework.domain.TenantDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.framework.config.properties.DruidProperties;
import com.ruoyi.framework.datasource.DynamicDataSource;

/**
 * druid 配置多数据源
 * 
 * @author ruoyi
 */
@Slf4j
@Configuration
public class DruidConfig
{
    @Bean
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource masterDataSource(DruidProperties druidProperties)
    {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource(DruidProperties druidProperties)
    {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource masterDataSource)
    {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
        setDataSource(targetDataSources, DataSourceType.SLAVE.name(), "slaveDataSource");

        // 租户数据源扩展
        // Initialize with tenant data sources that are already configured
//        initTenantDataSources(targetDataSources);

        return new DynamicDataSource(masterDataSource, targetDataSources);
    }


    /**
     * 租户数据源扩展
     * Initialize tenant data sources from the database
     */
//    private void initTenantDataSources(Map<Object, Object> targetDataSources) {
//        try {
//            // Get tenant database service using SpringUtils to avoid circular dependency
//            ITenantDatabaseService tenantDatabaseService = SpringUtils.getBean(ITenantDatabaseService.class);
//            if (tenantDatabaseService != null) {
//                // Get all active tenant databases
//                TenantDatabase query = new TenantDatabase();
//                query.setStatus(1); // Only active databases
//                List<TenantDatabase> tenantDatabases = tenantDatabaseService.selectTenantDatabaseList(query);
//
//                // Add each tenant database to the target data sources
//                for (TenantDatabase tenantDb : tenantDatabases) {
//                    try {
//                        DataSource tenantDataSource = createTenantDataSource(tenantDb);
//                        targetDataSources.put(tenantDb.getTenantId(), tenantDataSource);
//                    } catch (Exception e) {
//                        log.error("Failed to initialize tenant data source for tenant: " + tenantDb.getTenantId(), e);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("Failed to initialize tenant data sources", e);
//        }
//    }

    /**
     * Create a data source for a tenant
     */
//    private DataSource createTenantDataSource(TenantDatabase tenantDb) {
//        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setUrl("jdbc:mysql://" + tenantDb.getDbHost() + ":" + tenantDb.getDbPort() + "/" + tenantDb.getDbName());
//        dataSource.setUsername(tenantDb.getDbUsername());
//        dataSource.setPassword(decryptPassword(tenantDb.getDbPassword()));
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//
//        // Apply the same Druid properties as the master data source
//        DruidProperties druidProperties = SpringUtils.getBean(DruidProperties.class);
//        return druidProperties.dataSource(dataSource);
//    }


    /**
     * Decrypt the password
     */
//    private String decryptPassword(String encryptedPassword) {
//        try {
//            // Implement password decryption logic
//            return encryptedPassword; // Placeholder
//        } catch (Exception e) {
//            log.error("Failed to decrypt password", e);
//            return encryptedPassword;
//        }
//    }


    /**
     * 设置数据源
     * 
     * @param targetDataSources 备选数据源集合
     * @param sourceName 数据源名称
     * @param beanName bean名称
     */
    public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName)
    {
        try
        {
            DataSource dataSource = SpringUtils.getBean(beanName);
            targetDataSources.put(sourceName, dataSource);
        }
        catch (Exception e)
        {
        }
    }

    /**
     * 去除监控页面底部的广告
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties)
    {
        // 获取web监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        final String filePath = "support/http/resources/js/common.js";
        // 创建filter进行过滤
        Filter filter = new Filter()
        {
            @Override
            public void init(javax.servlet.FilterConfig filterConfig) throws ServletException
            {
            }
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException
            {
                chain.doFilter(request, response);
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer();
                // 获取common.js
                String text = Utils.readFromResource(filePath);
                // 正则替换banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }
            @Override
            public void destroy()
            {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
}
