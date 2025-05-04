package com.ruoyi.framework.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.framework.config.properties.DruidProperties;
import com.ruoyi.framework.domain.TenantDatabase;
import com.ruoyi.framework.service.ITenantDatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Service
public class TenantDataSourceService {
    //private static final Logger log = LoggerFactory.getLogger(TenantDataSourceService.class);

    @Autowired
    private ITenantDatabaseService tenantDatabaseService;

    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Autowired
    private DruidProperties druidProperties;

    /**
     * Get or create a data source for a tenant
     */
    public DataSource getTenantDataSource(String tenantId) {
        // Check if data source already exists
        Map<Object, Object> targetDataSources = dynamicDataSource.getTargetDataSources();
        if (targetDataSources.containsKey(tenantId)) {
            return (DataSource) targetDataSources.get(tenantId);
        }

        // Get tenant database configuration
        TenantDatabase tenantDb = tenantDatabaseService.selectTenantDatabaseByTenantId(Long.parseLong(tenantId));
        if (tenantDb == null || tenantDb.getStatus() != 1) {
            throw new CustomException("Tenant database not found or inactive");
        }

        // Create new data source
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://" + tenantDb.getDbHost() + ":" + tenantDb.getDbPort() + "/" + tenantDb.getDbName());
        dataSource.setUsername(tenantDb.getDbUsername());
        dataSource.setPassword(decryptPassword(tenantDb.getDbPassword()));
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Apply Druid properties
        DataSource tenantDataSource = druidProperties.dataSource(dataSource);

        // Add to dynamic data source
        dynamicDataSource.addTenantDataSource(tenantId, tenantDataSource);

        return tenantDataSource;
    }

    /**
     * Decrypt the password
     */
    private String decryptPassword(String encryptedPassword) {
        try {
            // Implement password decryption logic
            return encryptedPassword; // Placeholder
        } catch (Exception e) {
            log.error("Failed to decrypt password", e);
            return encryptedPassword;
        }
    }
}