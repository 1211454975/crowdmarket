package com.ruoyi.metadata.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.encrypt.EncryptUtils;
import com.ruoyi.metadata.domain.TenantDatabase;
import com.ruoyi.metadata.mapper.TenantDatabaseMapper;
import com.ruoyi.metadata.service.ITenantDatabaseService;

/**
 * 租户数据库配置Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class TenantDatabaseServiceImpl implements ITenantDatabaseService {
    private static final Logger log = LoggerFactory.getLogger(TenantDatabaseServiceImpl.class);

    @Autowired
    private TenantDatabaseMapper tenantDatabaseMapper;

    /**
     * 查询租户数据库配置
     *
     * @param id 租户数据库配置主键
     * @return 租户数据库配置
     */
    @Override
    public TenantDatabase selectTenantDatabaseById(String id) {
        return tenantDatabaseMapper.selectTenantDatabaseById(id);
    }

    /**
     * 根据租户ID查询数据库配置
     *
     * @param tenantId 租户ID
     * @return 租户数据库配置
     */
    @Override
    public TenantDatabase selectTenantDatabaseByTenantId(String tenantId) {
        return tenantDatabaseMapper.selectTenantDatabaseByTenantId(tenantId);
    }

    /**
     * 根据数据库名称查询数据库配置
     *
     * @param dbName 数据库名称
     * @return 租户数据库配置
     */
    @Override
    public TenantDatabase selectTenantDatabaseByDbName(String dbName) {
        return tenantDatabaseMapper.selectTenantDatabaseByDbName(dbName);
    }

    /**
     * 查询租户数据库配置列表
     *
     * @param tenantDatabase 租户数据库配置
     * @return 租户数据库配置
     */
    @Override
    public List<TenantDatabase> selectTenantDatabaseList(TenantDatabase tenantDatabase) {
        return tenantDatabaseMapper.selectTenantDatabaseList(tenantDatabase);
    }

    /**
     * 新增租户数据库配置
     *
     * @param tenantDatabase 租户数据库配置
     * @return 结果
     */
    @Override
    @Transactional
    public int insertTenantDatabase(TenantDatabase tenantDatabase) {
        // 生成UUID作为ID
        tenantDatabase.setId(UUID.randomUUID().toString().replace("-", ""));
        // 设置创建时间
        tenantDatabase.setCreateTime(DateUtils.getNowDate());
        // 设置创建者
        tenantDatabase.setCreateBy(SecurityUtils.getUsername());
        // 加密数据库密码
        if (StringUtils.isNotEmpty(tenantDatabase.getDbPassword())) {
            tenantDatabase.setDbPassword(encryptPassword(tenantDatabase.getDbPassword()));
        }

        return tenantDatabaseMapper.insertTenantDatabase(tenantDatabase);
    }

    /**
     * 修改租户数据库配置
     *
     * @param tenantDatabase 租户数据库配置
     * @return 结果
     */
    @Override
    @Transactional
    public int updateTenantDatabase(TenantDatabase tenantDatabase) {
        // 设置更新时间
        tenantDatabase.setUpdateTime(DateUtils.getNowDate());
        // 设置更新者
        tenantDatabase.setUpdateBy(SecurityUtils.getUsername());
        // 加密数据库密码
        if (StringUtils.isNotEmpty(tenantDatabase.getDbPassword())) {
            // 检查密码是否已经加密
            TenantDatabase oldDb = tenantDatabaseMapper.selectTenantDatabaseById(tenantDatabase.getId());
            if (oldDb != null && !tenantDatabase.getDbPassword().equals(oldDb.getDbPassword())) {
                tenantDatabase.setDbPassword(encryptPassword(tenantDatabase.getDbPassword()));
            }
        }

        return tenantDatabaseMapper.updateTenantDatabase(tenantDatabase);
    }

    /**
     * 批量删除租户数据库配置
     *
     * @param ids 需要删除的租户数据库配置主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteTenantDatabaseByIds(String[] ids) {
        return tenantDatabaseMapper.deleteTenantDatabaseByIds(ids);
    }

    /**
     * 删除租户数据库配置信息
     *
     * @param id 租户数据库配置主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteTenantDatabaseById(String id) {
        return tenantDatabaseMapper.deleteTenantDatabaseById(id);
    }

    /**
     * 获取租户数据库连接
     *
     * @param tenantDatabase 租户数据库配置
     * @return 数据库连接
     */
    @Override
    public Connection getConnection(TenantDatabase tenantDatabase) {
        try {
            // 加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 构建连接URL
            String url = String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%%2B8",
                    tenantDatabase.getDbHost(), tenantDatabase.getDbPort(), tenantDatabase.getDbName());

            // 获取原始密码
            String password = decryptPassword(tenantDatabase.getDbPassword());

            // 创建连接
            return DriverManager.getConnection(url, tenantDatabase.getDbUsername(), password);
        } catch (ClassNotFoundException | SQLException e) {
            log.error("获取租户数据库连接失败", e);
            return null;
        }
    }

    /**
     * 测试数据库连接
     *
     * @param tenantDatabase 租户数据库配置
     * @return 是否连接成功
     */
    @Override
    public boolean testConnection(TenantDatabase tenantDatabase) {
        Connection conn = null;
        try {
            conn = getConnection(tenantDatabase);
            return conn != null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("关闭数据库连接失败", e);
                }
            }
        }
    }

    /**
     * 创建租户数据库
     *
     * @param tenantDatabase 租户数据库配置
     * @return 是否创建成功
     */
    @Override
    public boolean createTenantDatabase(TenantDatabase tenantDatabase) {
        Connection conn = null;
        Statement stmt = null;
        boolean success = false;

        try {
            // 构建不带数据库名的连接URL
            String url = String.format("jdbc:mysql://%s:%d/?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%%2B8",
                    tenantDatabase.getDbHost(), tenantDatabase.getDbPort());

            // 获取原始密码
            String password = decryptPassword(tenantDatabase.getDbPassword());

            // 创建连接
            conn = DriverManager.getConnection(url, tenantDatabase.getDbUsername(), password);
            stmt = conn.createStatement();

            // 创建数据库
            String sql = "CREATE DATABASE IF NOT EXISTS " + tenantDatabase.getDbName() + " DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci";
            stmt.executeUpdate(sql);

            success = true;
        } catch (SQLException e) {
            log.error("创建租户数据库失败", e);
            success = false;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("关闭Statement失败", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("关闭数据库连接失败", e);
                }
            }
        }

        return success;
    }

    /**
     * 加密数据库密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    @Override
    public String encryptPassword(String password) {
        return EncryptUtils.encryptAES(password);
    }

    /**
     * 解密数据库密码
     *
     * @param encryptedPassword 加密后的密码
     * @return 原始密码
     */
    @Override
    public String decryptPassword(String encryptedPassword) {
        return EncryptUtils.decryptAES(encryptedPassword);
    }
}