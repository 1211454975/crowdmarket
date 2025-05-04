package com.ruoyi.framework.datasource;


import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;

/**
 * 租户上下文持有者
 */
public class TenantContextHolder {
    private static final ThreadLocal<String> TENANT_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> SYSTEM_OPERATION_HOLDER = new ThreadLocal<>();

    /**
     * 设置租户ID
     */
    public static void setTenantId(String tenantId) {
        TENANT_ID_HOLDER.set(tenantId);
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        return TENANT_ID_HOLDER.get();
    }

    /**
     * 清除租户ID
     */
    public static void clearTenantId() {
        TENANT_ID_HOLDER.remove();
    }

    /**
     * 设置为系统操作
     */
    public static void setSystemOperation(boolean isSystemOperation) {
        SYSTEM_OPERATION_HOLDER.set(isSystemOperation);

        // If it's a system operation, use master data source
        if (isSystemOperation) {
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.MASTER.name());
        } else {
            // Otherwise, use tenant data source
            String tenantId = getTenantId();
            if (StringUtils.isNotEmpty(tenantId)) {
                DynamicDataSourceContextHolder.setDataSourceType(tenantId);
            }
        }
    }

    /**
     * 是否是系统操作
     */
    public static boolean isSystemOperation() {
        return Boolean.TRUE.equals(SYSTEM_OPERATION_HOLDER.get());
    }

    /**
     * 清除系统操作标记
     */
    public static void clearSystemOperation() {
        SYSTEM_OPERATION_HOLDER.remove();
    }
}