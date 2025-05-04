package com.ruoyi.formdata.config;

/**
 * 数据源类型
 *
 * @author ruoyi
 */
public interface DataSourceType {
    /**
     * 主库
     */
    public static final String MASTER = "master";

    /**
     * 租户前缀
     */
    public static final String TENANT_PREFIX = "tenant_";
}