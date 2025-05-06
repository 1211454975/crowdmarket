package com.ruoyi.formdata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.StringUtils;
//import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.formdata.service.ITableGenerationService;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.domain.FormMetadata;
import com.ruoyi.metadata.domain.FormTenantTableMapping;
import com.ruoyi.metadata.service.IFormFieldMetadataService;
import com.ruoyi.metadata.service.IFormMetadataService;
import com.ruoyi.metadata.service.IFormTenantTableMappingService;


/**
 * 表单数据表生成 服务实现类
 *
 * @author ruoyi
 */
@Service
public class TableGenerationServiceImpl implements ITableGenerationService {
    private static final Logger log = LoggerFactory.getLogger(TableGenerationServiceImpl.class);

    @Autowired
    private IFormMetadataService formMetadataService;

    @Autowired
    private IFormFieldMetadataService formFieldMetadataService;

    @Autowired
    private IFormTenantTableMappingService formTenantTableMappingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据表单元数据生成租户数据表
     *
     * @param metadataId 元数据ID
     * @param tenantId   租户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean generateTable(String metadataId, String tenantId) {
        try {
            // 获取表单元数据
            FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
            if (metadata == null) {
                log.error("表单元数据不存在，metadataId: {}", metadataId);
                return false;
            }

            // 检查是否已经存在映射
            FormTenantTableMapping existingMapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
            if (existingMapping != null && existingMapping.getIsCreated() == 1) {
                log.info("租户表已存在，metadataId: {}, tenantId: {}, tableName: {}", metadataId, tenantId, existingMapping.getTableName());
                return true;
            }

            // 生成表名
            String tableName = "form_data_" + metadataId.replace("-", "");

            // 生成建表SQL
            String createTableSQL = generateCreateTableSQL(metadata, tableName);

            // 切换到租户数据源
            try {
                DynamicDataSourceContextHolder.setDataSourceType(tenantId);

                // 执行建表SQL
                jdbcTemplate.execute(createTableSQL);

                // 保存或更新映射关系
                if (existingMapping == null) {
                    FormTenantTableMapping mapping = new FormTenantTableMapping();
//                    mapping.setMappingId(UUID.fastUUID().toString());
                    mapping.setMetadataId(metadataId);
                    mapping.setTenantId(tenantId);
                    mapping.setTableName(tableName);
                    mapping.setIsCreated(1);
                    formTenantTableMappingService.insertFormTenantTableMapping(mapping);
                } else {
                    existingMapping.setTableName(tableName);
                    existingMapping.setIsCreated(1);
                    formTenantTableMappingService.updateFormTenantTableMapping(existingMapping);
                }

                log.info("成功创建租户表，metadataId: {}, tenantId: {}, tableName: {}", metadataId, tenantId, tableName);
                return true;
            } finally {
                // 清除数据源上下文
                DynamicDataSourceContextHolder.clearDataSourceType();
            }
        } catch (Exception e) {
            log.error("创建租户表失败", e);
            return false;
        }
    }

    /**
     * 根据表单元数据更新租户数据表结构
     *
     * @param metadataId 元数据ID
     * @param tenantId   租户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean updateTableStructure(String metadataId, String tenantId) {
        try {
            // 获取表单元数据
            FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
            if (metadata == null) {
                log.error("表单元数据不存在，metadataId: {}", metadataId);
                return false;
            }

            // 获取映射关系
            FormTenantTableMapping mapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
            if (mapping == null || mapping.getIsCreated() != 1) {
                log.error("租户表不存在，metadataId: {}, tenantId: {}", metadataId, tenantId);
                return false;
            }

            // 生成修改表结构SQL
            String alterTableSQL = generateAlterTableSQL(metadata, mapping.getTableName());
            if (StringUtils.isEmpty(alterTableSQL)) {
                log.info("无需更新表结构，metadataId: {}, tenantId: {}", metadataId, tenantId);
                return true;
            }

            // 切换到租户数据源
            try {
                DynamicDataSourceContextHolder.setDataSourceType(tenantId);

                // 执行修改表结构SQL
                String[] sqlStatements = alterTableSQL.split(";");
                for (String sql : sqlStatements) {
                    if (StringUtils.isNotEmpty(sql.trim())) {
                        jdbcTemplate.execute(sql);
                    }
                }

                log.info("成功更新租户表结构，metadataId: {}, tenantId: {}, tableName: {}", metadataId, tenantId, mapping.getTableName());
                return true;
            } finally {
                // 清除数据源上下文
                DynamicDataSourceContextHolder.clearDataSourceType();
            }
        } catch (Exception e) {
            log.error("更新租户表结构失败", e);
            return false;
        }
    }

    /**
     * 删除租户数据表
     *
     * @param metadataId 元数据ID
     * @param tenantId   租户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean dropTable(String metadataId, String tenantId) {
        try {
            // 获取映射关系
            FormTenantTableMapping mapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
            if (mapping == null || mapping.getIsCreated() != 1) {
                log.error("租户表不存在，metadataId: {}, tenantId: {}", metadataId, tenantId);
                return false;
            }

            // 切换到租户数据源
            try {
                DynamicDataSourceContextHolder.setDataSourceType(tenantId);

                // 执行删除表SQL
                String dropTableSQL = "DROP TABLE IF EXISTS " + mapping.getTableName();
                jdbcTemplate.execute(dropTableSQL);

                // 更新映射关系
                mapping.setIsCreated(0);
                formTenantTableMappingService.updateFormTenantTableMapping(mapping);

                log.info("成功删除租户表，metadataId: {}, tenantId: {}, tableName: {}", metadataId, tenantId, mapping.getTableName());
                return true;
            } finally {
                // 清除数据源上下文
                DynamicDataSourceContextHolder.clearDataSourceType();
            }
        } catch (Exception e) {
            log.error("删除租户表失败", e);
            return false;
        }
    }

    /**
     * 检查租户数据表是否存在
     *
     * @param metadataId 元数据ID
     * @param tenantId   租户ID
     * @return 是否存在
     */
    @Override
    public boolean isTableExists(String metadataId, String tenantId) {
        try {
            // 获取映射关系
            FormTenantTableMapping mapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
            if (mapping == null || mapping.getIsCreated() != 1) {
                return false;
            }

            // 切换到租户数据源
            try {
                DynamicDataSourceContextHolder.setDataSourceType(tenantId);

                // 查询表是否存在
                try {
                    String checkTableSQL = "SELECT 1 FROM " + mapping.getTableName() + " LIMIT 1";
                    jdbcTemplate.execute(checkTableSQL);
                    return true;
                } catch (Exception e) {
                    // 表不存在
                    return false;
                }
            } finally {
                // 清除数据源上下文
                DynamicDataSourceContextHolder.clearDataSourceType();
            }
        } catch (Exception e) {
            log.error("检查租户表是否存在失败", e);
            return false;
        }
    }

    /**
     * 生成建表SQL语句
     *
     * @param metadata  表单元数据
     * @param tableName 表名
     * @return SQL语句
     */
    @Override
    public String generateCreateTableSQL(FormMetadata metadata, String tableName) {
        // 获取字段元数据
        List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadata.getMetadataId());
        if (fields.isEmpty()) {
            log.error("表单字段元数据不存在，metadataId: {}", metadata.getMetadataId());
            return null;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");

        // 添加字段定义
        boolean hasPrimaryKey = false;
        String primaryKeyField = null;

        for (int i = 0; i < fields.size(); i++) {
            FormFieldMetadata field = fields.get(i);

            if (i > 0) {
                sql.append(",\n");
            }

            sql.append("  ").append(field.getColumnName()).append(" ").append(field.getColumnType());

            // 添加非空约束
            if (field.getIsRequired() == 1) {
                sql.append(" NOT NULL");
            } else {
                sql.append(" NULL");
            }

            // 添加注释
            sql.append(" COMMENT '").append(field.getFieldLabel()).append("'");

            // 记录主键字段
            if (field.getIsPk() == 1) {
                hasPrimaryKey = true;
                primaryKeyField = field.getColumnName();
            }
        }

        // 添加主键约束
        if (hasPrimaryKey && primaryKeyField != null) {
            sql.append(",\n  PRIMARY KEY (").append(primaryKeyField).append(")");
        }

        sql.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='").append(metadata.getMetadataName()).append("'");

        return sql.toString();
    }

    /**
     * 生成修改表结构SQL语句
     *
     * @param metadata  表单元数据
     * @param tableName 表名
     * @return SQL语句
     */
    @Override
    public String generateAlterTableSQL(FormMetadata metadata, String tableName) {
        // 获取字段元数据
        List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadata.getMetadataId());
        if (fields.isEmpty()) {
            log.error("表单字段元数据不存在，metadataId: {}", metadata.getMetadataId());
            return null;
        }

        // 获取表结构信息
        Map<String, Object> tableStructure = getTableStructure(tableName, metadata.getTenantId());
        if (tableStructure == null) {
            log.error("获取表结构信息失败，tableName: {}", tableName);
            return null;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> columns = (List<Map<String, Object>>) tableStructure.get("columns");

        // 构建现有列映射
        Map<String, Map<String, Object>> existingColumns = new HashMap<>();
        for (Map<String, Object> column : columns) {
            existingColumns.put(column.get("COLUMN_NAME").toString(), column);
        }

        StringBuilder sql = new StringBuilder();

        // 添加新列或修改列
        for (FormFieldMetadata field : fields) {
            String columnName = field.getColumnName();
            String columnType = field.getColumnType();
            boolean isRequired = field.getIsRequired() == 1;
            String comment = field.getFieldLabel();

            if (existingColumns.containsKey(columnName)) {
                // 检查列类型是否需要修改
                Map<String, Object> existingColumn = existingColumns.get(columnName);
                String existingType = existingColumn.get("COLUMN_TYPE").toString();
                String existingComment = existingColumn.get("COLUMN_COMMENT") != null ?
                        existingColumn.get("COLUMN_COMMENT").toString() : "";
                boolean existingNullable = "YES".equals(existingColumn.get("IS_NULLABLE"));

                boolean needModify = false;

                // 检查类型是否变更
                if (!existingType.equalsIgnoreCase(columnType)) {
                    needModify = true;
                }

                // 检查是否为空约束变更
                if (existingNullable == isRequired) {
                    needModify = true;
                }

                // 检查注释是否变更
                if (!existingComment.equals(comment)) {
                    needModify = true;
                }

                if (needModify) {
                    if (sql.length() > 0) {
                        sql.append(";\n");
                    }

                    sql.append("ALTER TABLE ").append(tableName)
                            .append(" MODIFY COLUMN ").append(columnName)
                            .append(" ").append(columnType);

                    if (isRequired) {
                        sql.append(" NOT NULL");
                    } else {
                        sql.append(" NULL");
                    }

                    sql.append(" COMMENT '").append(comment).append("'");
                }

                // 从现有列映射中移除，剩下的就是要删除的列
                existingColumns.remove(columnName);
            } else {
                // 添加新列
                if (sql.length() > 0) {
                    sql.append(";\n");
                }

                sql.append("ALTER TABLE ").append(tableName)
                        .append(" ADD COLUMN ").append(columnName)
                        .append(" ").append(columnType);

                if (isRequired) {
                    sql.append(" NOT NULL");
                } else {
                    sql.append(" NULL");
                }

                sql.append(" COMMENT '").append(comment).append("'");
            }
        }

        // 删除不再使用的列（可选，取决于业务需求）
        // 注意：删除列可能导致数据丢失，应谨慎处理
    /*
    for (String columnName : existingColumns.keySet())
    {
        // 跳过系统列或保留列
        if (isSystemColumn(columnName))
        {
            continue;
        }

        if (sql.length() > 0)
        {
            sql.append(";\n");
        }

        sql.append("ALTER TABLE ").append(tableName)
           .append(" DROP COLUMN ").append(columnName);
    }
    */

        return sql.toString();
    }

    /**
     * 获取表结构信息
     *
     * @param tableName 表名
     * @param tenantId  租户ID
     * @return 表结构信息
     */
    @Override
    public Map<String, Object> getTableStructure(String tableName, String tenantId) {
        try {
            // 切换到租户数据源
            DynamicDataSourceContextHolder.setDataSourceType(tenantId);

            try {
                Map<String, Object> result = new HashMap<>();

                // 获取表信息
                String tableInfoSql = "SELECT * FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";
                List<Map<String, Object>> tableInfo = jdbcTemplate.queryForList(tableInfoSql, tableName);

                if (tableInfo.isEmpty()) {
                    return null;
                }

                result.put("tableInfo", tableInfo.get(0));

                // 获取列信息
                String columnsSql = "SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION";
                List<Map<String, Object>> columns = jdbcTemplate.queryForList(columnsSql, tableName);

                result.put("columns", columns);

                // 获取索引信息
                String indexesSql = "SELECT * FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";
                List<Map<String, Object>> indexes = jdbcTemplate.queryForList(indexesSql, tableName);

                result.put("indexes", indexes);

                return result;
            } finally {
                // 清除数据源上下文
                DynamicDataSourceContextHolder.clearDataSourceType();
            }
        } catch (Exception e) {
            log.error("获取表结构信息失败", e);
            return null;
        }
    }

    /**
     * 判断是否为系统列
     *
     * @param columnName 列名
     * @return 是否为系统列
     */
    private boolean isSystemColumn(String columnName) {
        // 根据实际情况定义系统列
        String[] systemColumns = {"id", "create_time", "create_by", "update_time", "update_by", "del_flag"};

        for (String sysCol : systemColumns) {
            if (sysCol.equalsIgnoreCase(columnName)) {
                return true;
            }
        }

        return false;
    }
}