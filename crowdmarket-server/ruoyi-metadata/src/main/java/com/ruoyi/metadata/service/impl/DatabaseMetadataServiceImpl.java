package com.ruoyi.metadata.service.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.domain.FormMetadata;
import com.ruoyi.metadata.domain.TenantDatabase;
import com.ruoyi.metadata.service.IDatabaseMetadataService;
import com.ruoyi.metadata.service.ITenantDatabaseService;

/**
 * 数据库元数据提取服务实现
 *
 * @author ruoyi
 */
@Service
public class DatabaseMetadataServiceImpl implements IDatabaseMetadataService {
    private static final Logger log = LoggerFactory.getLogger(DatabaseMetadataServiceImpl.class);

    @Autowired
    private ITenantDatabaseService tenantDatabaseService;

    @Autowired
    private DataSource masterDataSource;

    /**
     * 获取数据库表列表
     *
     * @param dbName 数据库名称
     * @return 表列表
     */
    @Override
    public List<Map<String, Object>> listTables(String dbName) {
        List<Map<String, Object>> tables = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;

        try {
            // 如果未指定数据库名，则使用主数据库
            if (StringUtils.isEmpty(dbName)) {
                conn = masterDataSource.getConnection();
            } else {
                // 获取指定数据库的连接
                TenantDatabase tenantDb = tenantDatabaseService.selectTenantDatabaseByDbName(dbName);
                if (tenantDb != null) {
                    conn = tenantDatabaseService.getConnection(tenantDb);
                } else {
                    return tables;
                }
            }

            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});

            while (rs.next()) {
                Map<String, Object> table = new HashMap<>();
                table.put("tableName", rs.getString("TABLE_NAME"));
                table.put("tableComment", rs.getString("REMARKS"));
                tables.add(table);
            }
        } catch (SQLException e) {
            log.error("获取数据库表列表失败", e);
        } finally {
            closeResultSet(rs);
            closeConnection(conn);
        }

        return tables;
    }

    /**
     * 获取表的列信息
     *
     * @param dbName    数据库名称
     * @param tableName 表名
     * @return 列信息列表
     */
    @Override
    public List<Map<String, Object>> listColumns(String dbName, String tableName) {
        List<Map<String, Object>> columns = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;

        try {
            // 如果未指定数据库名，则使用主数据库
            if (StringUtils.isEmpty(dbName)) {
                conn = masterDataSource.getConnection();
            } else {
                // 获取指定数据库的连接
                TenantDatabase tenantDb = tenantDatabaseService.selectTenantDatabaseByDbName(dbName);
                if (tenantDb != null) {
                    conn = tenantDatabaseService.getConnection(tenantDb);
                } else {
                    return columns;
                }
            }

            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getColumns(conn.getCatalog(), null, tableName, "%");

            while (rs.next()) {
                Map<String, Object> column = new HashMap<>();
                column.put("columnName", rs.getString("COLUMN_NAME"));
                column.put("columnType", rs.getString("TYPE_NAME"));
                column.put("columnSize", rs.getInt("COLUMN_SIZE"));
                column.put("nullable", rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                column.put("columnComment", rs.getString("REMARKS"));
                column.put("columnDefault", rs.getString("COLUMN_DEF"));

                // 获取主键信息
                ResultSet pkRs = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName);
                while (pkRs.next()) {
                    if (pkRs.getString("COLUMN_NAME").equals(rs.getString("COLUMN_NAME"))) {
                        column.put("isPk", true);
                        break;
                    }
                }
                pkRs.close();

                columns.add(column);
            }
        } catch (SQLException e) {
            log.error("获取表的列信息失败", e);
        } finally {
            closeResultSet(rs);
            closeConnection(conn);
        }

        return columns;
    }

    /**
     * 从数据库表生成表单元数据
     *
     * @param dbName    数据库名称
     * @param tableName 表名
     * @return 表单元数据
     */
    @Override
    public FormMetadata generateFormMetadataFromTable(String dbName, String tableName) {
        FormMetadata metadata = new FormMetadata();
        Connection conn = null;
        ResultSet rs = null;

        try {
            // 如果未指定数据库名，则使用主数据库
            if (StringUtils.isEmpty(dbName)) {
                conn = masterDataSource.getConnection();
            } else {
                // 获取指定数据库的连接
                TenantDatabase tenantDb = tenantDatabaseService.selectTenantDatabaseByDbName(dbName);
                if (tenantDb != null) {
                    conn = tenantDatabaseService.getConnection(tenantDb);
                } else {
                    return metadata;
                }
            }

            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(conn.getCatalog(), null, tableName, new String[]{"TABLE"});

            if (rs.next()) {
                metadata.setMetadataId(UUID.randomUUID().toString().replace("-", ""));
                metadata.setMetadataName(StringUtils.toCamelCase(tableName));
                metadata.setMetadataDesc(rs.getString("REMARKS"));
                metadata.setTableName(tableName);
                metadata.setStatus(0); // 草稿状态
                metadata.setTenantId(SecurityUtils.getCurrComId());
            }
        } catch (SQLException e) {
            log.error("从数据库表生成表单元数据失败", e);
        } finally {
            closeResultSet(rs);
            closeConnection(conn);
        }

        return metadata;
    }

    /**
     * 从数据库表生成字段元数据列表
     *
     * @param dbName    数据库名称
     * @param tableName 表名
     * @return 字段元数据列表
     */
    @Override
    public List<FormFieldMetadata> generateFieldMetadataFromTable(String dbName, String tableName) {
        List<FormFieldMetadata> fieldList = new ArrayList<>();
        List<Map<String, Object>> columns = listColumns(dbName, tableName);

        int sortOrder = 1;
        for (Map<String, Object> column : columns) {
            FormFieldMetadata field = new FormFieldMetadata();
            field.setFieldId(UUID.randomUUID().toString().replace("-", ""));
            field.setFieldName(StringUtils.toCamelCase((String) column.get("columnName")));
            field.setFieldLabel((String) column.get("columnComment"));
            if (StringUtils.isEmpty(field.getFieldLabel())) {
                field.setFieldLabel(field.getFieldName());
            }

            String columnType = (String) column.get("columnType");
            field.setColumnName((String) column.get("columnName"));
            field.setColumnType(columnType);

            // 设置字段类型
            if (columnType.contains("char") || columnType.contains("text")) {
                field.setFieldType("string");
                if (columnType.contains("text")) {
                    field.setHtmlType("textarea");
                } else {
                    field.setHtmlType("input");
                }
            } else if (columnType.contains("int") || columnType.contains("number") || columnType.contains("decimal")) {
                field.setFieldType("number");
                field.setHtmlType("input");
            } else if (columnType.contains("date") || columnType.contains("time")) {
                field.setFieldType("date");
                field.setHtmlType("datetime");
            } else if (columnType.contains("bool")) {
                field.setFieldType("boolean");
                field.setHtmlType("radio");
            } else {
                field.setFieldType("string");
                field.setHtmlType("input");
            }

            // 设置是否主键
            Boolean isPk = (Boolean) column.get("isPk");
            field.setIsPk(isPk != null && isPk ? 1 : 0);

            // 设置是否必填
            Boolean nullable = (Boolean) column.get("nullable");
            field.setIsRequired(nullable != null && !nullable ? 1 : 0);

            // 设置是否列表显示和查询条件
            field.setIsList(1);
            field.setIsQuery(0);

            // 设置查询方式
            if ("string".equals(field.getFieldType())) {
                field.setQueryType("LIKE");
            } else {
                field.setQueryType("EQ");
            }

            // 设置排序
            field.setSortOrder(sortOrder++);

            // 设置租户ID
            field.setTenantId(SecurityUtils.getCurrComId());

            fieldList.add(field);
        }

        return fieldList;
    }

    /**
     * 在租户数据库中创建表
     *
     * @param tenantId   租户ID
     * @param metadataId 元数据ID
     * @param tableName  表名
     * @param fieldList  字段列表
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean createTableInTenantDatabase(String tenantId, String metadataId, String tableName, List<FormFieldMetadata> fieldList) {
        Connection conn = null;
        Statement stmt = null;
        boolean success = false;

        try {
            // 获取租户数据库连接
            TenantDatabase tenantDb = tenantDatabaseService.selectTenantDatabaseByTenantId(tenantId);
            if (tenantDb == null) {
                return false;
            }

            conn = tenantDatabaseService.getConnection(tenantDb);
            stmt = conn.createStatement();

            // 构建创建表的SQL
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE ").append(tableName).append(" (");

            // 添加字段定义
            for (int i = 0; i < fieldList.size(); i++) {
                FormFieldMetadata field = fieldList.get(i);
                sql.append(field.getColumnName()).append(" ").append(field.getColumnType());

                // 是否允许为空
                if (field.getIsRequired() == 1) {
                    sql.append(" NOT NULL");
                } else {
                    sql.append(" NULL");
                }

                // 添加注释
                if (StringUtils.isNotEmpty(field.getFieldLabel())) {
                    sql.append(" COMMENT '").append(field.getFieldLabel()).append("'");
                }

                // 不是最后一个字段，添加逗号
                if (i < fieldList.size() - 1) {
                    sql.append(", ");
                }
            }

            // 添加主键
            List<String> pkColumns = new ArrayList<>();
            for (FormFieldMetadata field : fieldList) {
                if (field.getIsPk() == 1) {
                    pkColumns.add(field.getColumnName());
                }
            }

            if (!pkColumns.isEmpty()) {
                sql.append(", PRIMARY KEY (");
                for (int i = 0; i < pkColumns.size(); i++) {
                    sql.append(pkColumns.get(i));
                    if (i < pkColumns.size() - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(")");
            }

            sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='").append(tableName).append("'");

            // 执行创建表SQL
            stmt.executeUpdate(sql.toString());

            success = true;
        } catch (SQLException e) {
            log.error("在租户数据库中创建表失败", e);
            success = false;
        } finally {
            closeStatement(stmt);
            closeConnection(conn);
        }

        return success;
    }

    /**
     * 在租户数据库中更新表结构
     *
     * @param tenantId   租户ID
     * @param metadataId 元数据ID
     * @param tableName  表名
     * @param fieldList  字段列表
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean updateTableInTenantDatabase(String tenantId, String metadataId, String tableName, List<FormFieldMetadata> fieldList) {
        // 如果表不存在，则创建表
        if (!isTableExist(tenantId, tableName)) {
            return createTableInTenantDatabase(tenantId, metadataId, tableName, fieldList);
        }

        Connection conn = null;
        Statement stmt = null;
        boolean success = false;

        try {
            // 获取租户数据库连接
            TenantDatabase tenantDb = tenantDatabaseService.selectTenantDatabaseByTenantId(tenantId);
            if (tenantDb == null) {
                return false;
            }

            conn = tenantDatabaseService.getConnection(tenantDb);
            stmt = conn.createStatement();

            // 获取现有表的列信息
            List<Map<String, Object>> existingColumns = listColumns(tenantDb.getDbName(), tableName);
            Map<String, Map<String, Object>> existingColumnMap = new HashMap<>();
            for (Map<String, Object> column : existingColumns) {
                existingColumnMap.put((String) column.get("columnName"), column);
            }

            // 处理每个字段
            for (FormFieldMetadata field : fieldList) {
                String columnName = field.getColumnName();

                // 如果列已存在，检查是否需要修改
                if (existingColumnMap.containsKey(columnName)) {
                    Map<String, Object> existingColumn = existingColumnMap.get(columnName);
                    String existingType = (String) existingColumn.get("columnType");
                    Boolean existingNullable = (Boolean) existingColumn.get("nullable");

                    // 如果类型或可空性不同，则修改列
                    if (!existingType.equalsIgnoreCase(field.getColumnType()) ||
                            (existingNullable != null && existingNullable.booleanValue() == (field.getIsRequired() == 1))) {

                        StringBuilder alterSql = new StringBuilder();
                        alterSql.append("ALTER TABLE ").append(tableName)
                                .append(" MODIFY COLUMN ").append(columnName)
                                .append(" ").append(field.getColumnType());

                        // 是否允许为空
                        if (field.getIsRequired() == 1) {
                            alterSql.append(" NOT NULL");
                        } else {
                            alterSql.append(" NULL");
                        }

                        // 添加注释
                        if (StringUtils.isNotEmpty(field.getFieldLabel())) {
                            alterSql.append(" COMMENT '").append(field.getFieldLabel()).append("'");
                        }

                        stmt.executeUpdate(alterSql.toString());
                    }

                    // 从现有列映射中移除，剩下的将是需要删除的列
                    existingColumnMap.remove(columnName);
                } else {
                    // 如果列不存在，则添加新列
                    StringBuilder addSql = new StringBuilder();
                    addSql.append("ALTER TABLE ").append(tableName)
                            .append(" ADD COLUMN ").append(columnName)
                            .append(" ").append(field.getColumnType());

                    // 是否允许为空
                    if (field.getIsRequired() == 1) {
                        addSql.append(" NOT NULL");
                    } else {
                        addSql.append(" NULL");
                    }

                    // 添加注释
                    if (StringUtils.isNotEmpty(field.getFieldLabel())) {
                        addSql.append(" COMMENT '").append(field.getFieldLabel()).append("'");
                    }

                    stmt.executeUpdate(addSql.toString());
                }
            }

            // 处理主键
            List<String> pkColumns = new ArrayList<>();
            for (FormFieldMetadata field : fieldList) {
                if (field.getIsPk() == 1) {
                    pkColumns.add(field.getColumnName());
                }
            }

            // 删除原有主键并添加新主键
            if (!pkColumns.isEmpty()) {
                // 检查表是否有主键
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet rs = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName);
                boolean hasPrimaryKey = rs.next();
                rs.close();

                if (hasPrimaryKey) {
                    // 删除原有主键
                    stmt.executeUpdate("ALTER TABLE " + tableName + " DROP PRIMARY KEY");
                }

                // 添加新主键
                StringBuilder pkSql = new StringBuilder();
                pkSql.append("ALTER TABLE ").append(tableName)
                        .append(" ADD PRIMARY KEY (");

                for (int i = 0; i < pkColumns.size(); i++) {
                    pkSql.append(pkColumns.get(i));
                    if (i < pkColumns.size() - 1) {
                        pkSql.append(", ");
                    }
                }

                pkSql.append(")");
                stmt.executeUpdate(pkSql.toString());
            }

            success = true;
        } catch (SQLException e) {
            log.error("在租户数据库中更新表结构失败", e);
            success = false;
        } finally {
            closeStatement(stmt);
            closeConnection(conn);
        }

        return success;
    }


    /**
     * 在租户数据库中删除表
     *
     * @param tenantId  租户ID
     * @param tableName 表名
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean dropTableInTenantDatabase(String tenantId, String tableName) {
        Connection conn = null;
        Statement stmt = null;
        boolean success = false;

        try {
            // 获取租户数据库连接
            TenantDatabase tenantDb = tenantDatabaseService.selectTenantDatabaseByTenantId(tenantId);
            if (tenantDb == null) {
                return false;
            }

            conn = tenantDatabaseService.getConnection(tenantDb);
            stmt = conn.createStatement();

            // 执行删除表SQL
            stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName);

            success = true;
        } catch (SQLException e) {
            log.error("在租户数据库中删除表失败", e);
            success = false;
        } finally {
            closeStatement(stmt);
            closeConnection(conn);
        }

        return success;
    }

    /**
     * 检查表是否存在
     *
     * @param tenantId  租户ID
     * @param tableName 表名
     * @return 是否存在
     */
    @Override
    public boolean isTableExist(String tenantId, String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            // 获取租户数据库连接
            TenantDatabase tenantDb = tenantDatabaseService.selectTenantDatabaseByTenantId(tenantId);
            if (tenantDb == null) {
                return false;
            }

            conn = tenantDatabaseService.getConnection(tenantDb);
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(conn.getCatalog(), null, tableName, new String[]{"TABLE"});

            exists = rs.next();
        } catch (SQLException e) {
            log.error("检查表是否存在失败", e);
        } finally {
            closeResultSet(rs);
            closeConnection(conn);
        }

        return exists;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn 数据库连接
     */
    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("关闭数据库连接失败", e);
            }
        }
    }

    /**
     * 关闭Statement
     *
     * @param stmt Statement
     */
    private void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("关闭Statement失败", e);
            }
        }
    }

    /**
     * 关闭ResultSet
     *
     * @param rs ResultSet
     */
    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("关闭ResultSet失败", e);
            }
        }
    }
}