package com.ruoyi.metadata.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.domain.FormMetadata;

/**
 * 数据库元数据提取服务接口
 *
 * @author ruoyi
 */
public interface DatabaseMetadataService {
    /**
     * 获取数据库表列表
     *
     * @param dbName 数据库名称
     * @return 表列表
     */
    public List<Map<String, Object>> listTables(String dbName);

    /**
     * 获取表的列信息
     *
     * @param dbName 数据库名称
     * @param tableName 表名
     * @return 列信息列表
     */
    public List<Map<String, Object>> listColumns(String dbName, String tableName);

    /**
     * 从数据库表生成表单元数据
     *
     * @param dbName 数据库名称
     * @param tableName 表名
     * @return 表单元数据
     */
    public FormMetadata generateFormMetadataFromTable(String dbName, String tableName);

    /**
     * 从数据库表生成字段元数据列表
     *
     * @param dbName 数据库名称
     * @param tableName 表名
     * @return 字段元数据列表
     */
    public List<FormFieldMetadata> generateFieldMetadataFromTable(String dbName, String tableName);

    /**
     * 在租户数据库中创建表
     *
     * @param tenantId 租户ID
     * @param metadataId 元数据ID
     * @param tableName 表名
     * @param fieldList 字段列表
     * @return 是否成功
     */
    public boolean createTableInTenantDatabase(String tenantId, String metadataId, String tableName, List<FormFieldMetadata> fieldList);

    /**
     * 在租户数据库中更新表结构
     *
     * @param tenantId 租户ID
     * @param metadataId 元数据ID
     * @param tableName 表名
     * @param fieldList 字段列表
     * @return 是否成功
     */
    public boolean updateTableInTenantDatabase(String tenantId, String metadataId, String tableName, List<FormFieldMetadata> fieldList);

    /**
     * 在租户数据库中删除表
     *
     * @param tenantId 租户ID
     * @param tableName 表名
     * @return 是否成功
     */
    public boolean dropTableInTenantDatabase(String tenantId, String tableName);

    /**
     * 检查表是否存在
     *
     * @param tenantId 租户ID
     * @param tableName 表名
     * @return 是否存在
     */
    public boolean isTableExist(String tenantId, String tableName);
}