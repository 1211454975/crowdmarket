package com.ruoyi.formdata.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 租户数据源工具类，提供手动切换租户数据源的方法
 */
@Component
public class TenantDataSourceUtils {
    private static final Logger log = LoggerFactory.getLogger(TenantDataSourceUtils.class);

    private static DynamicDataSourceService dataSourceService;

    @Autowired
    public void setDataSourceService(DynamicDataSourceService service) {
        TenantDataSourceUtils.dataSourceService = service;
    }

    /**
     * 切换到租户数据源
     *
     * @param tenantId 租户ID
     */
    public static void switchToTenant(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            log.warn("Tenant ID is empty, will not switch data source");
            return;
        }

        TenantContextHolder.setTenantId(tenantId);

        // 确保租户数据源已初始化
        if (dataSourceService != null) {
            dataSourceService.initTenantDataSource(tenantId);
        }

        log.debug("Switched to tenant database: {}", tenantId);
    }

    /**
     * 恢复到默认数据源
     */
    public static void restoreDefaultDataSource() {
        TenantContextHolder.clear();
        log.debug("Restored to default database");
    }

    /**
     * 在租户数据源上执行操作
     *
     * @param tenantId 租户ID
     * @param callback 回调函数
     * @param <T> 返回类型
     * @return 操作结果
     */
    public static <T> T executeOnTenant(String tenantId, TenantCallback<T> callback) {
        try {
            switchToTenant(tenantId);
            return callback.execute();
        } finally {
            restoreDefaultDataSource();
        }
    }

    /**
     * 租户回调接口
     *
     * @param <T> 返回类型
     */
    @FunctionalInterface
    public interface TenantCallback<T> {
        /**
         * 执行操作
         *
         * @return 操作结果
         */
        T execute();
    }
}