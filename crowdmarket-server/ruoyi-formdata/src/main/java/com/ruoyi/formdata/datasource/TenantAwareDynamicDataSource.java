package com.ruoyi.formdata.datasource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.metadata.domain.TenantDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.framework.datasource.DynamicDataSource;

/**
 * 租户感知的动态数据源，扩展框架的DynamicDataSource
 */
public class TenantAwareDynamicDataSource extends DynamicDataSource {
    private static final Logger log = LoggerFactory.getLogger(TenantAwareDynamicDataSource.class);

    private final TenantDataSourceRegistry registry;

    public TenantAwareDynamicDataSource(DataSource defaultTargetDataSource,
                                        Map<Object, Object> targetDataSources,
                                        TenantDataSourceRegistry registry) {
        super(defaultTargetDataSource, targetDataSources);
        this.registry = registry;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null && registry.containsTenantDataSource(tenantId)) {
            log.debug("Using tenant data source for tenant: {}", tenantId);
            return "TENANT_" + tenantId;
        }
        // 如果没有租户上下文或租户数据源不存在，则使用默认的数据源选择逻辑
        return null; // Return null to use the default data source
    }

    /**
     * 添加租户数据源
     *
     * @param tenantId 租户ID
     * @param dataSource 数据源
     */
    public void addTenantDataSource(String tenantId, DataSource dataSource) {
        String key = "TENANT_" + tenantId;
        registry.registerTenantDataSource(tenantId, dataSource);
        Map<Object, Object> targetDataSources = new HashMap<>(getResolvedDataSources());
        targetDataSources.put(key, dataSource);
        setTargetDataSources(targetDataSources);
        afterPropertiesSet();
    }

    /**
     * 移除租户数据源
     *
     * @param tenantId 租户ID
     */
    public void removeTenantDataSource(String tenantId) {
        registry.removeTenantDataSource(tenantId);
        afterPropertiesSet();
    }

    /**
     * 获取已解析的数据源
     *
     * @return 数据源映射
     */
    private Map<Object, Object> getResolvedDataSources() {
        // This is a workaround since we can't access the internal targetDataSources map
        Map<Object, Object> resolvedDataSources = new HashMap<>();
        resolvedDataSources.put("MASTER", determineTargetDataSource());
        return resolvedDataSources;
    }



}