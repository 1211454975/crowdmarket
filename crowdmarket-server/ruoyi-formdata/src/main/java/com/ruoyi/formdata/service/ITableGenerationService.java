package com.ruoyi.formdata.service;

import java.util.Map;
import com.ruoyi.metadata.domain.FormMetadata;

/**
 * 表单数据表生成 服务接口
 *
 * @author ruoyi
 */
public interface ITableGenerationService
{
    /**
     * 根据表单元数据生成租户数据表
     *
     * @param metadataId 元数据ID
     * @param tenantId 租户ID
     * @return 结果
     */
    public boolean generateTable(String metadataId, String tenantId);

    /**
     * 根据表单元数据更新租户数据表结构
     *
     * @param metadataId 元数据ID
     * @param tenantId 租户ID
     * @return 结果
     */
    public boolean updateTableStructure(String metadataId, String tenantId);

    /**
     * 删除租户数据表
     *
     * @param metadataId 元数据ID
     * @param tenantId 租户ID
     * @return 结果
     */
    public boolean dropTable(String metadataId, String tenantId);

    /**
     * 检查租户数据表是否存在
     *
     * @param metadataId 元数据ID
     * @param tenantId 租户ID
     * @return 是否存在
     */
    public boolean isTableExists(String metadataId, String tenantId);

    /**
     * 生成建表SQL语句
     *
     * @param metadata 表单元数据
     * @param tableName 表名
     * @return SQL语句
     */
    public String generateCreateTableSQL(FormMetadata metadata, String tableName);

    /**
     * 生成修改表结构SQL语句
     *
     * @param metadata 表单元数据
     * @param tableName 表名
     * @return SQL语句
     */
    public String generateAlterTableSQL(FormMetadata metadata, String tableName);

    /**
     * 获取表结构信息
     *
     * @param tableName 表名
     * @param tenantId 租户ID
     * @return 表结构信息
     */
    public Map<String, Object> getTableStructure(String tableName, String tenantId);
}