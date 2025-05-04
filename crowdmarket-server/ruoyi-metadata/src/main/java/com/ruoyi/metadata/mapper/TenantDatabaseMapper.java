package com.ruoyi.metadata.mapper;

import java.util.List;
import com.ruoyi.metadata.domain.TenantDatabase;

/**
 * 租户数据库配置Mapper接口
 *
 * @author ruoyi
 */
public interface TenantDatabaseMapper {
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
     * 删除租户数据库配置
     *
     * @param id 租户数据库配置主键
     * @return 结果
     */
    public int deleteTenantDatabaseById(String id);

    /**
     * 批量删除租户数据库配置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTenantDatabaseByIds(String[] ids);
}