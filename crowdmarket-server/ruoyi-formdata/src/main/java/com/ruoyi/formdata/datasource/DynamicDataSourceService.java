package com.ruoyi.formdata.datasource;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.druid.pool.DruidDataSource;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.metadata.domain.TenantDatabase;
import com.ruoyi.metadata.service.ITenantDatabaseService;

/**
 * 动态数据源服务，用于管理租户数据源
 */
@Service
public class DynamicDataSourceService {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceService.class);

    @Autowired
    private TenantAwareDynamicDataSource dataSource;

    @Autowired
    private ITenantDatabaseService tenantDatabaseService;

    /**
     * 切换到租户数据源
     *
     * @param tenantId 租户ID
     */
    public void switchToTenantDataSource(String tenantId) {
        if (StringUtils.isEmpty(tenantId)) {
            log.error("Tenant ID cannot be empty");
            return;
        }

        // 设置租户上下文
        TenantContextHolder.setTenantId(tenantId);

        // 确保租户数据源已初始化
        initTenantDataSource(tenantId);

        log.debug("Switched to tenant database: {}", tenantId);
    }
    /**
     * 切换到主数据源
     */
    public void switchToMainDataSource() {
        // 清除租户上下文
        TenantContextHolder.clear();
        log.debug("Switched to main database");
    }
    /**
     * 初始化租户数据源
     *
     * @param tenantId 租户ID
     */
    public void initTenantDataSource(String tenantId) {
        if (StringUtils.isEmpty(tenantId)) {
            log.error("Tenant ID cannot be empty");
            return;
        }

        TenantDatabase tenantDB = tenantDatabaseService.selectTenantDatabaseByTenantId(tenantId);
        if (tenantDB == null) {
            log.error("Tenant database configuration not found for tenant: {}", tenantId);
            return;
        }

        if (tenantDB.getStatus() != 1) {
            log.warn("Tenant database is disabled for tenant: {}", tenantId);
            return;
        }

        createTenantDataSource(tenantDB);
    }

    /**
     * 创建租户数据源
     *
     * @param tenantDB 租户数据库配置
     */
    private void createTenantDataSource(TenantDatabase tenantDB) {
        String tenantId = tenantDB.getTenantId();

        // 检查数据源是否已存在
        if (dataSource.getTargetDataSources().containsKey("TENANT_" + tenantId)) {
            log.info("Data source already exists for tenant: {}", tenantId);
            return;
        }

        try {
            DruidDataSource ds = new DruidDataSource();
            // 设置数据源基本属性
            ds.setUrl(buildJdbcUrl(tenantDB));
            ds.setUsername(tenantDB.getDbUsername());
            ds.setPassword(decryptPassword(tenantDB.getDbPassword()));
            ds.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // 设置连接池属性
            ds.setInitialSize(5);
            ds.setMinIdle(5);
            ds.setMaxActive(20);
            ds.setMaxWait(60000);
            ds.setTimeBetweenEvictionRunsMillis(60000);
            ds.setMinEvictableIdleTimeMillis(300000);
            ds.setValidationQuery("SELECT 1");
            ds.setTestWhileIdle(true);
            ds.setTestOnBorrow(false);
            ds.setTestOnReturn(false);

            // 初始化数据源
            ds.init();

            // 添加到动态数据源
            dataSource.addTenantDataSource(tenantId, ds);
            log.info("Successfully created data source for tenant: {}", tenantId);
        } catch (Exception e) {
            log.error("Failed to create data source for tenant: {}", tenantId, e);
        }
    }

    /**
     * 构建JDBC URL
     *
     * @param tenantDB 租户数据库配置
     * @return JDBC URL
     */
    private String buildJdbcUrl(TenantDatabase tenantDB) {
        return String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%%2B8",
                tenantDB.getDbHost(), tenantDB.getDbPort(), tenantDB.getDbName());
    }

    /**
     * 解密密码
     *
     * @param encryptedPassword 加密密码
     * @return 解密后的密码
     */
    private String decryptPassword(String encryptedPassword) {
        // TODO: 实现密码解密逻辑
        return encryptedPassword;
    }

    /**
     * 移除租户数据源
     *
     * @param tenantId 租户ID
     */
    public void removeTenantDataSource(String tenantId) {
        if (StringUtils.isEmpty(tenantId)) {
            return;
        }

        dataSource.removeTenantDataSource(tenantId);
        log.info("Removed data source for tenant: {}", tenantId);
    }

    /**
     * 刷新租户数据源
     *
     * @param tenantId 租户ID
     */
    public void refreshTenantDataSource(String tenantId) {
        removeTenantDataSource(tenantId);
        initTenantDataSource(tenantId);
    }

    /**
     * 刷新所有租户数据源
     */
    public void refreshAllTenantDataSources() {
        log.info("Refreshing all tenant data sources");
        tenantDatabaseService.selectTenantDatabaseList(new TenantDatabase()).stream()
                .filter(db -> db.getStatus() == 1)
                .forEach(this::createTenantDataSource);
    }
}