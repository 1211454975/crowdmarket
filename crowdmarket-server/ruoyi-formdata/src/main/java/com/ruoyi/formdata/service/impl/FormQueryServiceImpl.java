package com.ruoyi.formdata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
//import com.ruoyi.formdata.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.formdata.service.IFormQueryService;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.domain.FormMetadata;
import com.ruoyi.metadata.domain.FormTenantTableMapping;
import com.ruoyi.metadata.service.IFormFieldMetadataService;
import com.ruoyi.metadata.service.IFormMetadataService;
import com.ruoyi.metadata.service.IFormTenantTableMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
/**
 * 表单数据查询 服务实现类
 *
 * @author ruoyi
 */
@Service
public class FormQueryServiceImpl implements IFormQueryService {
    private static final Logger log = LoggerFactory.getLogger(FormQueryServiceImpl.class);

    @Autowired
    private IFormMetadataService formMetadataService;

    @Autowired
    private IFormFieldMetadataService formFieldMetadataService;

    @Autowired
    private IFormTenantTableMappingService formTenantTableMappingService;

    /**
     * 根据元数据ID和查询条件查询表单数据
     *
     * @param metadataId 元数据ID
     * @param params     查询参数
     * @return 表单数据列表
     */
    @Override
    public List<Map<String, Object>> queryFormData(String metadataId, Map<String, Object> params) {
        // 获取表单元数据
        FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            log.error("表单元数据不存在，metadataId: {}", metadataId);
            return new ArrayList<>();
        }

        // 获取租户ID
        String tenantId = (String) params.get("tenantId");
        if (StringUtils.isEmpty(tenantId)) {
            log.error("租户ID不能为空");
            return new ArrayList<>();
        }

        // 获取表单字段元数据
        List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fields.isEmpty()) {
            log.error("表单字段元数据不存在，metadataId: {}", metadataId);
            return new ArrayList<>();
        }

        // 获取租户表映射
        FormTenantTableMapping mapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            log.error("租户表未创建，metadataId: {}, tenantId: {}", metadataId, tenantId);
            return new ArrayList<>();
        }

        // 构建查询SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(mapping.getTableName()).append(" WHERE 1=1");

        // 添加查询条件
        List<Object> paramValues = new ArrayList<>();
        for (FormFieldMetadata field : fields) {
            if (field.getIsQuery() == 1 && params.containsKey(field.getFieldName())) {
                Object value = params.get(field.getFieldName());
                if (value != null) {
                    String queryType = field.getQueryType();
                    if ("EQ".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" = ?");
                        paramValues.add(value);
                    } else if ("NE".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" != ?");
                        paramValues.add(value);
                    } else if ("GT".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" > ?");
                        paramValues.add(value);
                    } else if ("LT".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" < ?");
                        paramValues.add(value);
                    } else if ("LIKE".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" LIKE ?");
                        paramValues.add("%" + value + "%");
                    } else if ("BETWEEN".equals(queryType) && value instanceof List) {
                        List<?> rangeValues = (List<?>) value;
                        if (rangeValues.size() == 2) {
                            sql.append(" AND ").append(field.getColumnName()).append(" BETWEEN ? AND ?");
                            paramValues.add(rangeValues.get(0));
                            paramValues.add(rangeValues.get(1));
                        }
                    }
                }
            }
        }

        // 切换到租户数据源
        try {
            DynamicDataSourceContextHolder.setDataSourceType(tenantId);

            // 执行查询
            // 注意：这里应该使用JDBC或MyBatis执行动态SQL，这里简化处理
            // 实际实现应该使用jdbcTemplate或自定义Mapper
            log.info("执行查询SQL: {}, 参数: {}", sql.toString(), paramValues);

            // 模拟查询结果
            List<Map<String, Object>> result = new ArrayList<>();
            // 实际代码应该是执行SQL并返回结果

            return result;
        } finally {
            // 清除数据源上下文
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 根据元数据ID和主键值查询单条表单数据
     *
     * @param metadataId 元数据ID
     * @param primaryKey 主键值
     * @return 表单数据
     */
    @Override
    public Map<String, Object> queryFormDataById(String metadataId, String primaryKey) {
        // 获取表单元数据
        FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            log.error("表单元数据不存在，metadataId: {}", metadataId);
            return new HashMap<>();
        }

        // 获取表单字段元数据
        List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fields.isEmpty()) {
            log.error("表单字段元数据不存在，metadataId: {}", metadataId);
            return new HashMap<>();
        }

        // 获取主键字段
        FormFieldMetadata pkField = null;
        for (FormFieldMetadata field : fields) {
            if (field.getIsPk() == 1) {
                pkField = field;
                break;
            }
        }

        if (pkField == null) {
            log.error("未找到主键字段，metadataId: {}", metadataId);
            return new HashMap<>();
        }

        // 获取租户ID（实际应该从上下文或参数中获取）
        String tenantId = "current_tenant_id";

        // 获取租户表映射
        FormTenantTableMapping mapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            log.error("租户表未创建，metadataId: {}, tenantId: {}", metadataId, tenantId);
            return new HashMap<>();
        }

        // 构建查询SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(mapping.getTableName())
                .append(" WHERE ").append(pkField.getColumnName()).append(" = ?");

        // 切换到租户数据源
        try {
            DynamicDataSourceContextHolder.setDataSourceType(tenantId);

            // 执行查询
            // 注意：这里应该使用JDBC或MyBatis执行动态SQL，这里简化处理
            log.info("执行查询SQL: {}, 参数: {}", sql.toString(), primaryKey);

            // 模拟查询结果
            Map<String, Object> result = new HashMap<>();
            // 实际代码应该是执行SQL并返回结果

            return result;
        } finally {
            // 清除数据源上下文
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 根据元数据ID和查询条件统计表单数据数量
     *
     * @param metadataId 元数据ID
     * @param params     查询参数
     * @return 数据数量
     */
    @Override
    public int countFormData(String metadataId, Map<String, Object> params) {
        // 获取表单元数据
        FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            log.error("表单元数据不存在，metadataId: {}", metadataId);
            return 0;
        }

        // 获取租户ID
        String tenantId = (String) params.get("tenantId");
        if (StringUtils.isEmpty(tenantId)) {
            log.error("租户ID不能为空");
            return 0;
        }

        // 获取表单字段元数据
        List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);

        // 获取租户表映射
        FormTenantTableMapping mapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            log.error("租户表未创建，metadataId: {}, tenantId: {}", metadataId, tenantId);
            return 0;
        }

        // 构建查询SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ").append(mapping.getTableName()).append(" WHERE 1=1");

        // 添加查询条件
        List<Object> paramValues = new ArrayList<>();
        for (FormFieldMetadata field : fields) {
            if (field.getIsQuery() == 1 && params.containsKey(field.getFieldName())) {
                Object value = params.get(field.getFieldName());
                if (value != null) {
                    String queryType = field.getQueryType();
                    if ("EQ".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" = ?");
                        paramValues.add(value);
                    } else if ("NE".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" != ?");
                        paramValues.add(value);
                    } else if ("GT".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" > ?");
                        paramValues.add(value);
                    } else if ("LT".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" < ?");
                        paramValues.add(value);
                    } else if ("LIKE".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" LIKE ?");
                        paramValues.add("%" + value + "%");
                    } else if ("BETWEEN".equals(queryType) && value instanceof List) {
                        List<?> rangeValues = (List<?>) value;
                        if (rangeValues.size() == 2) {
                            sql.append(" AND ").append(field.getColumnName()).append(" BETWEEN ? AND ?");
                            paramValues.add(rangeValues.get(0));
                            paramValues.add(rangeValues.get(1));
                        }
                    }
                }
            }
        }

        // 切换到租户数据源
        try {
            DynamicDataSourceContextHolder.setDataSourceType(tenantId);

            // 执行查询
            // 注意：这里应该使用JDBC或MyBatis执行动态SQL，这里简化处理
            log.info("执行查询SQL: {}, 参数: {}", sql.toString(), paramValues);

            // 模拟查询结果
            // 实际代码应该是执行SQL并返回结果
            return 0; // 实际应该返回查询结果
        } finally {
            // 清除数据源上下文
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 根据元数据ID和查询条件分页查询表单数据
     *
     * @param metadataId 元数据ID
     * @param params     查询参数
     * @param pageNum    页码
     * @param pageSize   每页记录数
     * @return 表单数据列表
     */
    @Override
    public List<Map<String, Object>> queryFormDataPage(String metadataId, Map<String, Object> params, int pageNum, int pageSize) {
        // 获取表单元数据
        FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            log.error("表单元数据不存在，metadataId: {}", metadataId);
            return new ArrayList<>();
        }

        // 获取租户ID
        String tenantId = (String) params.get("tenantId");
        if (StringUtils.isEmpty(tenantId)) {
            log.error("租户ID不能为空");
            return new ArrayList<>();
        }

        // 获取表单字段元数据
        List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fields.isEmpty()) {
            log.error("表单字段元数据不存在，metadataId: {}", metadataId);
            return new ArrayList<>();
        }

        // 获取租户表映射
        FormTenantTableMapping mapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            log.error("租户表未创建，metadataId: {}, tenantId: {}", metadataId, tenantId);
            return new ArrayList<>();
        }

        // 构建查询SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(mapping.getTableName()).append(" WHERE 1=1");

        // 添加查询条件
        List<Object> paramValues = new ArrayList<>();
        for (FormFieldMetadata field : fields) {
            if (field.getIsQuery() == 1 && params.containsKey(field.getFieldName())) {
                Object value = params.get(field.getFieldName());
                if (value != null) {
                    String queryType = field.getQueryType();
                    if ("EQ".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" = ?");
                        paramValues.add(value);
                    } else if ("NE".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" != ?");
                        paramValues.add(value);
                    } else if ("GT".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" > ?");
                        paramValues.add(value);
                    } else if ("LT".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" < ?");
                        paramValues.add(value);
                    } else if ("LIKE".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" LIKE ?");
                        paramValues.add("%" + value + "%");
                    } else if ("BETWEEN".equals(queryType) && value instanceof List) {
                        List<?> rangeValues = (List<?>) value;
                        if (rangeValues.size() == 2) {
                            sql.append(" AND ").append(field.getColumnName()).append(" BETWEEN ? AND ?");
                            paramValues.add(rangeValues.get(0));
                            paramValues.add(rangeValues.get(1));
                        }
                    }
                }
            }
        }

        // 添加分页
        int offset = (pageNum - 1) * pageSize;
        sql.append(" LIMIT ").append(offset).append(", ").append(pageSize);

        // 切换到租户数据源
        try {
            DynamicDataSourceContextHolder.setDataSourceType(tenantId);

            // 执行查询
            // 注意：这里应该使用JDBC或MyBatis执行动态SQL，这里简化处理
            log.info("执行查询SQL: {}, 参数: {}", sql.toString(), paramValues);

            // 模拟查询结果
            List<Map<String, Object>> result = new ArrayList<>();
            // 实际代码应该是执行SQL并返回结果

            return result;
        } finally {
            // 清除数据源上下文
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 根据元数据ID和字段名查询字段唯一值列表
     *
     * @param metadataId 元数据ID
     * @param fieldName  字段名
     * @return 唯一值列表
     */
    @Override
    public List<Object> queryFieldDistinctValues(String metadataId, String fieldName) {
        // 获取表单元数据
        FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            log.error("表单元数据不存在，metadataId: {}", metadataId);
            return new ArrayList<>();
        }

        // 获取表单字段元数据
        List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fields.isEmpty()) {
            log.error("表单字段元数据不存在，metadataId: {}", metadataId);
            return new ArrayList<>();
        }

        // 查找指定字段
        FormFieldMetadata targetField = null;
        for (FormFieldMetadata field : fields) {
            if (field.getFieldName().equals(fieldName)) {
                targetField = field;
                break;
            }
        }

        if (targetField == null) {
            log.error("字段不存在，metadataId: {}, fieldName: {}", metadataId, fieldName);
            return new ArrayList<>();
        }

        // 获取租户ID（实际应该从上下文或参数中获取）
        String tenantId = "current_tenant_id";

        // 获取租户表映射
        FormTenantTableMapping mapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            log.error("租户表未创建，metadataId: {}, tenantId: {}", metadataId, tenantId);
            return new ArrayList<>();
        }

        // 构建查询SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT ").append(targetField.getColumnName())
                .append(" FROM ").append(mapping.getTableName())
                .append(" WHERE ").append(targetField.getColumnName()).append(" IS NOT NULL")
                .append(" ORDER BY ").append(targetField.getColumnName());

        // 切换到租户数据源
        try {
            DynamicDataSourceContextHolder.setDataSourceType(tenantId);

            // 执行查询
            // 注意：这里应该使用JDBC或MyBatis执行动态SQL，这里简化处理
            log.info("执行查询SQL: {}", sql.toString());

            // 模拟查询结果
            List<Object> result = new ArrayList<>();
            // 实际代码应该是执行SQL并返回结果

            return result;
        } finally {
            // 清除数据源上下文
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 根据元数据ID和聚合条件执行聚合查询
     *
     * @param metadataId      元数据ID
     * @param groupFields     分组字段
     * @param aggregateFields 聚合字段和聚合函数
     * @param params          查询参数
     * @return 聚合结果
     */
    @Override
    public List<Map<String, Object>> queryAggregateData(String metadataId, List<String> groupFields,
                                                        Map<String, String> aggregateFields, Map<String, Object> params) {
        // 获取表单元数据
        FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            log.error("表单元数据不存在，metadataId: {}", metadataId);
            return new ArrayList<>();
        }

        // 获取租户ID
        String tenantId = (String) params.get("tenantId");
        if (StringUtils.isEmpty(tenantId)) {
            log.error("租户ID不能为空");
            return new ArrayList<>();
        }

        // 获取表单字段元数据
        List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fields.isEmpty()) {
            log.error("表单字段元数据不存在，metadataId: {}", metadataId);
            return new ArrayList<>();
        }

        // 获取租户表映射
        FormTenantTableMapping mapping = formTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            log.error("租户表未创建，metadataId: {}, tenantId: {}", metadataId, tenantId);
            return new ArrayList<>();
        }

        // 构建查询SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");

        // 添加分组字段
        Map<String, FormFieldMetadata> fieldMap = new HashMap<>();
        for (FormFieldMetadata field : fields) {
            fieldMap.put(field.getFieldName(), field);
        }

        boolean hasFields = false;
        for (String groupField : groupFields) {
            FormFieldMetadata field = fieldMap.get(groupField);
            if (field != null) {
                if (hasFields) {
                    sql.append(", ");
                }
                sql.append(field.getColumnName());
                hasFields = true;
            }
        }

        // 添加聚合字段
        for (Map.Entry<String, String> entry : aggregateFields.entrySet()) {
            String fieldName = entry.getKey();
            String aggregateFunc = entry.getValue();

            FormFieldMetadata field = fieldMap.get(fieldName);
            if (field != null) {
                if (hasFields) {
                    sql.append(", ");
                }
                sql.append(aggregateFunc).append("(").append(field.getColumnName()).append(") AS ")
                        .append(aggregateFunc.toLowerCase()).append("_").append(field.getColumnName());
                hasFields = true;
            }
        }

        if (!hasFields) {
            log.error("未找到有效的分组字段或聚合字段");
            return new ArrayList<>();
        }

        sql.append(" FROM ").append(mapping.getTableName()).append(" WHERE 1=1");

        // 添加查询条件
        List<Object> paramValues = new ArrayList<>();
        for (FormFieldMetadata field : fields) {
            if (field.getIsQuery() == 1 && params.containsKey(field.getFieldName())) {
                Object value = params.get(field.getFieldName());
                if (value != null) {
                    String queryType = field.getQueryType();
                    if ("EQ".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" = ?");
                        paramValues.add(value);
                    } else if ("NE".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" != ?");
                        paramValues.add(value);
                    } else if ("GT".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" > ?");
                        paramValues.add(value);
                    } else if ("LT".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" < ?");
                        paramValues.add(value);
                    } else if ("LIKE".equals(queryType)) {
                        sql.append(" AND ").append(field.getColumnName()).append(" LIKE ?");
                        paramValues.add("%" + value + "%");
                    } else if ("BETWEEN".equals(queryType) && value instanceof List) {
                        List<?> rangeValues = (List<?>) value;
                        if (rangeValues.size() == 2) {
                            sql.append(" AND ").append(field.getColumnName()).append(" BETWEEN ? AND ?");
                            paramValues.add(rangeValues.get(0));
                            paramValues.add(rangeValues.get(1));
                        }
                    }
                }
            }
        }

        // 添加分组语句
        if (!groupFields.isEmpty()) {
            sql.append(" GROUP BY ");
            boolean isFirst = true;
            for (String groupField : groupFields) {
                FormFieldMetadata field = fieldMap.get(groupField);
                if (field != null) {
                    if (!isFirst) {
                        sql.append(", ");
                    }
                    sql.append(field.getColumnName());
                    isFirst = false;
                }
            }
        }

        // 切换到租户数据源
        try {
            DynamicDataSourceContextHolder.setDataSourceType(tenantId);

            // 执行查询
            // 注意：这里应该使用JDBC或MyBatis执行动态SQL，这里简化处理
            log.info("执行查询SQL: {}, 参数: {}", sql.toString(), paramValues);

            // 模拟查询结果
            List<Map<String, Object>> result = new ArrayList<>();
            // 实际代码应该是执行SQL并返回结果

            return result;
        } finally {
            // 清除数据源上下文
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }
}