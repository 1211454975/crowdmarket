package com.ruoyi.formdata.datasource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 租户数据源注册表，用于管理租户数据源
 */
@Component
public class TenantDataSourceRegistry {
    private static final Logger log = LoggerFactory.getLogger(TenantDataSourceRegistry.class);

    private final Map<String, DataSource> tenantDataSources = new ConcurrentHashMap<>();

    /**
     * 注册租户数据源
     *
     * @param tenantId 租户ID
     * @param dataSource 数据源
     */
    public void registerTenantDataSource(String tenantId, DataSource dataSource) {
        log.info("Registering data source for tenant: {}", tenantId);
        tenantDataSources.put(tenantId, dataSource);
    }

    /**
     * 获取租户数据源
     *
     * @param tenantId 租户ID
     * @return 数据源
     */
    public DataSource getTenantDataSource(String tenantId) {
        return tenantDataSources.get(tenantId);
    }

    /**
     * 移除租户数据源
     *
     * @param tenantId 租户ID
     */
    public void removeTenantDataSource(String tenantId) {
        log.info("Removing data source for tenant: {}", tenantId);
        tenantDataSources.remove(tenantId);
    }

    /**
     * 检查租户数据源是否存在
     *
     * @param tenantId 租户ID
     * @return 是否存在
     */
    public boolean containsTenantDataSource(String tenantId) {
        return tenantDataSources.containsKey(tenantId);
    }

    /**
     * 获取所有租户ID
     *
     * @return 租户ID集合
     */
    public Iterable<String> getTenantIds() {
        return tenantDataSources.keySet();
    }
}