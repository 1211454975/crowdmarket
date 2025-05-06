package com.ruoyi.metadata.service;

import com.ruoyi.metadata.domain.TenantDatabase;

import java.sql.Connection;
import java.util.List;

/**
 * 租户数据库配置Service接口
 *
 * @author ruoyi
 */
public interface ITenantDatabaseService {
    /**
     * 查询租户数据库配置
     *
     * @param id 租户数据库配置主键
     * @return 租户数据库配置
     */
    public TenantDatabase selectTenantDatabaseById(String id);

    /**
     * 根据租户ID查询数据库配置
     *
     * @param tenantId 租户ID
     * @return 租户数据库配置
     */
    public TenantDatabase selectTenantDatabaseByTenantId(String tenantId);

    /**
     * 根据数据库名称查询数据库配置
     *
     * @param dbName 数据库名称
     * @return 租户数据库配置
     */
    public TenantDatabase selectTenantDatabaseByDbName(String dbName);

    /**
     * 查询租户数据库配置列表
     *
     * @param tenantDatabase 租户数据库配置
     * @return 租户数据库配置集合
     */
    public List<TenantDatabase> selectTenantDatabaseList(TenantDatabase tenantDatabase);

    /**
     * 新增租户数据库配置
     *
     * @param tenantDatabase 租户数据库配置
     * @return 结果
     */
    public int insertTenantDatabase(TenantDatabase tenantDatabase);

    /**
     * 修改租户数据库配置
     *
     * @param tenantDatabase 租户数据库配置
     * @return 结果
     */
    public int updateTenantDatabase(TenantDatabase tenantDatabase);

    /**
     * 删除租户数据库配置信息
     *
     * @param id 租户数据库配置主键
     * @return 结果
     */
    public int deleteTenantDatabaseById(String id);

    /**
     * 批量删除租户数据库配置信息
     *
     * @param ids 需要删除的租户数据库配置主键集合
     * @return 结果
     */
    public int deleteTenantDatabaseByIds(String[] ids);

    /**
     * 获取租户数据库连接
     *
     * @param tenantDatabase 租户数据库配置
     * @return 数据库连接
     */
    public Connection getConnection(TenantDatabase tenantDatabase);

    /**
     * 测试数据库连接
     *
     * @param tenantDatabase 租户数据库配置
     * @return 是否连接成功
     */
    public boolean testConnection(TenantDatabase tenantDatabase);

    /**
     * 创建租户数据库
     *
     * @param tenantDatabase 租户数据库配置
     * @return 是否创建成功
     */
    public boolean createTenantDatabase(TenantDatabase tenantDatabase);

    /**
     * 加密数据库密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public String encryptPassword(String password);

    /**
     * 解密数据库密码
     *
     * @param encryptedPassword 加密后的密码
     * @return 原始密码
     */
    public String decryptPassword(String encryptedPassword);
}