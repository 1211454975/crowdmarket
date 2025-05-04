package com.ruoyi.tenant.service.impl;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.domain.SysCompany;
import com.ruoyi.framework.mapper.SysCompanyMapper;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.tenant.utils.AESUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.framework.domain.TenantDatabase;
import com.ruoyi.tenant.mapper.TenantDatabaseMapper;
import com.ruoyi.framework.service.ITenantDatabaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * 公告 服务层实现
 *
 * @author ruoyi
 */
@Slf4j
@Service
public class TenantDatabaseServiceImpl implements ITenantDatabaseService
{

    @Autowired
    private DataSource masterDataSource;

    // 租户数据源缓存
    private Map<String, DataSource> tenantDataSources = new ConcurrentHashMap<>();


    @Value("${tenant.db.host}")
    private String host;
    @Value("${tenant.db.port}")
    private String port;
    @Value("${tenant.db.root.username}")
    private String username;
    @Value("${tenant.db.root.password}")
    private String password;
    @Value("${tenant.db.password.key}")
    private String passwordKey;

    @Autowired
    private TenantDatabaseMapper tenantDatabaseMapper;
    @Autowired
    private SysCompanyMapper companyMapper;

    /**
     * 查询公告信息
     *
     * @param tenantDatabaseId 公告ID
     * @return 公告信息
     */
    @Override
    public TenantDatabase selectTenantDatabaseById(Long tenantDatabaseId)
    {
        return tenantDatabaseMapper.selectTenantDatabaseById(tenantDatabaseId);
    }

    @Override
    public TenantDatabase selectTenantDatabaseByTenantId(Long tenantId) {
        return tenantDatabaseMapper.selectTenantDatabaseByTenantId(tenantId);
    }
    /**
     * 查询公告列表
     *
     * @param tenantDatabase 公告信息
     * @return 公告集合
     */
    @Override
    public List<TenantDatabase> selectTenantDatabaseList(TenantDatabase tenantDatabase)
    {
        return tenantDatabaseMapper.selectTenantDatabaseList(tenantDatabase);
    }

    /**
     * 新增公告
     *
     * @param tenantDatabase 公告信息
     * @return 结果
     */
    @Override
    public int insertTenantDatabase(TenantDatabase tenantDatabase)
    {
        return tenantDatabaseMapper.insertTenantDatabase(tenantDatabase);
    }

    /**
     * 修改公告
     *
     * @param tenantDatabase 公告信息
     * @return 结果
     */
    @Override
    public int updateTenantDatabase(TenantDatabase tenantDatabase)
    {
        return tenantDatabaseMapper.updateTenantDatabase(tenantDatabase);
    }

    /**
     * 删除公告对象
     *
     * @param tenantDatabaseId 公告ID
     * @return 结果
     */
    @Override
    public int deleteTenantDatabaseById(Long tenantDatabaseId)
    {
        return tenantDatabaseMapper.deleteTenantDatabaseById(tenantDatabaseId);
    }

    /**
     * 批量删除公告信息
     *
     * @param tenantDatabaseIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteTenantDatabaseByIds(Long[] tenantDatabaseIds)
    {
        return tenantDatabaseMapper.deleteTenantDatabaseByIds(tenantDatabaseIds);
    }




    @Override
    @Transactional
    public void createTenantDatabase(String tenantId) {
        // 查询租户信息
        SysCompany company = companyMapper.selectSysCompanyById(tenantId);
        if (company == null) {
            throw new CustomException("租户不存在");
        }

        // 检查是否已创建数据库
        TenantDatabase existDb = tenantDatabaseMapper.selectTenantDatabaseById(Long.parseLong(tenantId));
        if (existDb != null) {
            throw new CustomException("该租户已创建数据库");
        }

        try {
            // 生成数据库名称
            String dbName = "cm_tenant_" + tenantId.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

            // 获取系统配置的数据库连接信息
            String dbHost = host;//ConfigUtils.getConfigValue("tenant.db.host", "localhost");
            int dbPort = Integer.parseInt(port);// Integer.parseInt(ConfigUtils.getConfigValue("tenant.db.port", "3306"));
            String dbRootUser = username;//ConfigUtils.getConfigValue("tenant.db.root.username", "root");
            String dbRootPassword = password;//ConfigUtils.getConfigValue("tenant.db.root.password", "password");

            // 生成租户数据库用户名和密码
            String dbUsername = "user_" + tenantId.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
            String dbPassword = generateRandomPassword();

            // 创建数据库和用户
            JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
            jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            jdbcTemplate.execute("CREATE USER '" + dbUsername + "'@'%' IDENTIFIED BY '" + dbPassword + "'");
            jdbcTemplate.execute("GRANT ALL PRIVILEGES ON `" + dbName + "`.* TO '" + dbUsername + "'@'%'");
            jdbcTemplate.execute("FLUSH PRIVILEGES");

            // 保存租户数据库配置
            TenantDatabase tenantDatabase = new TenantDatabase();
//            tenantDatabase.setId(IdUtils.fastUUID());
            tenantDatabase.setTenantId(tenantId);
            tenantDatabase.setDbName(dbName);
            tenantDatabase.setDbHost(dbHost);
            tenantDatabase.setDbPort(dbPort);
            tenantDatabase.setDbUsername(dbUsername);
            tenantDatabase.setDbPassword(encryptPassword(dbPassword));
            tenantDatabase.setStatus(1);
            tenantDatabase.setCreateBy(SecurityUtils.getUsername());
            tenantDatabase.setCreateTime(DateUtils.getNowDate());

            tenantDatabaseMapper.insertTenantDatabase(tenantDatabase);

            // 更新租户数据库状态
            SysCompany updateCompany = new SysCompany();
            updateCompany.setId(tenantId);
            updateCompany.setDbStatus(1);
            companyMapper.updateSysCompany(updateCompany);

            log.info("租户[{}]数据库创建成功: {}", tenantId, dbName);
        } catch (Exception e) {
            // 更新租户数据库状态为创建失败
            SysCompany updateCompany = new SysCompany();
            updateCompany.setId(tenantId);
            updateCompany.setDbStatus(2);
            companyMapper.updateSysCompany(updateCompany);

            log.error("租户[{}]数据库创建失败", tenantId, e);
            throw new CustomException("创建租户数据库失败: " + e.getMessage());
        }
    }


    @Override
    public boolean testConnection(TenantDatabase tenantDatabase) {
        try {
            // 创建临时数据源测试连接
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + tenantDatabase.getDbHost() + ":" + tenantDatabase.getDbPort() + "/" + tenantDatabase.getDbName());
            config.setUsername(tenantDatabase.getDbUsername());
            config.setPassword(decryptPassword(tenantDatabase.getDbPassword()));
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setConnectionTimeout(5000); // 5秒超时

            DataSource dataSource = new HikariDataSource(config);
            Connection conn = dataSource.getConnection();
            conn.close();
            ((HikariDataSource) dataSource).close();

            return true;
        } catch (Exception e) {
            log.error("测试数据库连接失败", e);
            return false;
        }
    }
    @Override
    public DataSource getTenantDataSource(String tenantId) {
        // 检查缓存中是否存在
        //Map<Object, Object> targetDataSources = dynamicDataSource.getTargetDataSources();
        if (tenantDataSources.containsKey(tenantId)) {
            return tenantDataSources.get(tenantId);
        }

        // 查询租户数据库配置
        TenantDatabase tenantDb = tenantDatabaseMapper.selectTenantDatabaseByTenantId(Long.parseLong(tenantId));
        if (tenantDb == null) {
            throw new CustomException("租户数据库配置不存在");
        }

        if (tenantDb.getStatus() != 1) {
            throw new CustomException("租户数据库已停用");
        }

        try {
            // 创建数据源
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + tenantDb.getDbHost() + ":" + tenantDb.getDbPort() + "/" + tenantDb.getDbName());
            config.setUsername(tenantDb.getDbUsername());
            config.setPassword(decryptPassword(tenantDb.getDbPassword()));
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // 设置连接池参数
            config.setMinimumIdle(5);
            config.setMaximumPoolSize(20);
            config.setIdleTimeout(30000);
            config.setPoolName("HikariPool-Tenant-" + tenantId);

            DataSource dataSource = new HikariDataSource(config);
            tenantDataSources.put(tenantId, dataSource);

            return dataSource;
        } catch (Exception e) {
            log.error("获取租户数据源失败", e);
            throw new CustomException("获取租户数据源失败: " + e.getMessage());
        }
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 加密密码
     */
    private String encryptPassword(String password) {
        try {
            return AESUtil.encrypt(password, passwordKey);
        } catch (Exception e) {
            log.error("加密密码失败", e);
            return password;
        }
    }


    /**
     * 解密密码
     */
    private String decryptPassword(String encryptedPassword) {
        try {
            return AESUtil.decrypt(encryptedPassword);
        } catch (Exception e) {
            log.error("解密密码失败", e);
            return encryptedPassword;
        }
    }
}
