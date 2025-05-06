package com.ruoyi.formdata.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

/**
 * 动态数据源初始化器，在应用启动时加载所有租户数据源
 */
public class DynamicDataSourceInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceInitializer.class);

    private final DynamicDataSourceService dataSourceService;

    public DynamicDataSourceInitializer(DynamicDataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing tenant data sources...");
        try {
            dataSourceService.refreshAllTenantDataSources();
            log.info("Tenant data sources initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize tenant data sources", e);
        }
    }
}