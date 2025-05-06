package com.ruoyi.framework.datasource;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 *
 * @author ruoyi
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);

    private Map<Object, Object> customTargetDataSources = new HashMap<>();

    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        this.customTargetDataSources.putAll(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }

    /**
     * 添加数据源
     *
     * @param key 数据源键
     * @param dataSource 数据源
     */
    public synchronized void addDataSource(Object key, Object dataSource) {
        this.customTargetDataSources.put(key, dataSource);
        super.setTargetDataSources(new HashMap<>(this.customTargetDataSources));
        super.afterPropertiesSet();
        log.info("添加数据源: {}", key);
    }

    /**
     * 移除数据源
     *
     * @param key 数据源键
     */
    public synchronized void removeDataSource(Object key) {
        this.customTargetDataSources.remove(key);
        super.setTargetDataSources(new HashMap<>(this.customTargetDataSources));
        super.afterPropertiesSet();
        log.info("移除数据源: {}", key);
    }

    /**
     * 获取数据源映射
     *
     * @return 数据源映射
     */
    public Map<Object, Object> getDataSources() {
        return new HashMap<>(this.customTargetDataSources);
    }

    protected void setTargetDataSources(String key, DataSource dataSource) {
        this.addDataSource(key,dataSource);
    }

    public Map<Object, Object> getTargetDataSources() {
        return customTargetDataSources;
    }

    public DataSource getResolvedDefaultDataSource() {
        return super.determineTargetDataSource();
    }


}