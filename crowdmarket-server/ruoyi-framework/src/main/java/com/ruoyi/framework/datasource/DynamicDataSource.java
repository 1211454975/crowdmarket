package com.ruoyi.framework.datasource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 * 
 * @author ruoyi
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource
{
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources)
    {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey()
    {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }



    /**
     * Add a tenant data source
     */
    public synchronized void addTenantDataSource(String tenantId, DataSource dataSource) {
        Map<Object, Object> targetDataSources = getTargetDataSources();
        targetDataSources.put(tenantId, dataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }
    /**
     * Get the target data sources map
     */
    @SuppressWarnings("unchecked")
    public Map<Object, Object> getTargetDataSources() {
        try {
            Field field = AbstractRoutingDataSource.class.getDeclaredField("targetDataSources");
            field.setAccessible(true);
            return (Map<Object, Object>) field.get(this);
        } catch (Exception e) {
            log.error("Failed to get targetDataSources", e);
            return new HashMap<>();
        }
    }
}















