package com.ruoyi.tenant.mapper;


import java.util.List;
import com.ruoyi.framework.domain.TenantDatabase;

/**
 * 通知公告表 数据层
 *
 * @author ruoyi
 */
public interface TenantDatabaseMapper
{
    /**
     * 查询公告信息
     *
     * @param tenantDatabaseId 公告ID
     * @return 公告信息
     */
    public TenantDatabase selectTenantDatabaseById(Long tenantDatabaseId);
    public TenantDatabase selectTenantDatabaseByTenantId(Long tenantId);
    /**
     * 查询公告列表
     *
     * @param tenantDatabase 公告信息
     * @return 公告集合
     */
    public List<TenantDatabase> selectTenantDatabaseList(TenantDatabase tenantDatabase);

    /**
     * 新增公告
     *
     * @param tenantDatabase 公告信息
     * @return 结果
     */
    public int insertTenantDatabase(TenantDatabase tenantDatabase);

    /**
     * 修改公告
     *
     * @param tenantDatabase 公告信息
     * @return 结果
     */
    public int updateTenantDatabase(TenantDatabase tenantDatabase);

    /**
     * 批量删除公告
     *
     * @param tenantDatabaseId 公告ID
     * @return 结果
     */
    public int deleteTenantDatabaseById(Long tenantDatabaseId);

    /**
     * 批量删除公告信息
     *
     * @param tenantDatabaseIds 需要删除的公告ID
     * @return 结果
     */
    public int deleteTenantDatabaseByIds(Long[] tenantDatabaseIds);
}