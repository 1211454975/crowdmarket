package com.ruoyi.formdata.datasource;

/**
 * 租户上下文持有者，用于存储当前线程的租户ID
 */
public class TenantContextHolder {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public static void setTenantId(String tenantId) {
        CONTEXT.set(tenantId);
    }

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    public static String getTenantId() {
        return CONTEXT.get();
    }

    /**
     * 清除租户ID
     */
    public static void clear() {
        CONTEXT.remove();
    }
}