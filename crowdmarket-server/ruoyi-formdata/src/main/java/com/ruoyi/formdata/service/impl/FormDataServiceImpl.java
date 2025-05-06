package com.ruoyi.formdata.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.metadata.service.IFormTenantTableMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.formdata.config.DataSourceType;
import com.ruoyi.formdata.mapper.FormDataMapper;
import com.ruoyi.formdata.service.IFormDataService;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.domain.FormMetadata;
import com.ruoyi.metadata.domain.FormTenantTableMapping;
import com.ruoyi.metadata.service.IFormFieldMetadataService;
import com.ruoyi.metadata.service.IFormMetadataService;

/**
 * 表单数据Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class FormDataServiceImpl implements IFormDataService {
    private static final Logger log = LoggerFactory.getLogger(FormDataServiceImpl.class);

    @Autowired
    private FormDataMapper formDataMapper;

    @Autowired
    private IFormMetadataService IFormMetadataService;

    @Autowired
    private IFormFieldMetadataService IFormFieldMetadataService;

    @Autowired
    private IFormTenantTableMappingService IFormTenantTableMappingService;

    /**
     * 查询表单数据
     *
     * @param metadataId 元数据ID
     * @param dataId     数据ID
     * @return 表单数据
     */
    @Override
    public Map<String, Object> selectFormDataById(String metadataId, String dataId) {
        // 获取表单元数据
        FormMetadata metadata = IFormMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            return null;
        }

        // 获取租户表映射
        String tenantId = SecurityUtils.getCurrComId();
        FormTenantTableMapping mapping = IFormTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            return null;
        }

        // 获取主键字段
        List<FormFieldMetadata> fieldList = IFormFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        String pkColumn = getPrimaryKeyColumn(fieldList);
        if (StringUtils.isEmpty(pkColumn)) {
            return null;
        }

        try {
            // 切换到租户数据源
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.TENANT_PREFIX + tenantId);

            // 查询数据
            return formDataMapper.selectFormDataById(mapping.getTableName(), pkColumn, dataId);
        } finally {
            // 恢复默认数据源
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 查询表单数据列表
     *
     * @param metadataId 元数据ID
     * @param params     查询参数
     * @return 表单数据集合
     */
    @Override
    public List<Map<String, Object>> selectFormDataList(String metadataId, Map<String, Object> params) {
        // 获取表单元数据
        FormMetadata metadata = IFormMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            return new ArrayList<>();
        }

        // 获取租户表映射
        String tenantId = SecurityUtils.getCurrComId();
        FormTenantTableMapping mapping = IFormTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            return new ArrayList<>();
        }

        // 获取字段列表
        List<FormFieldMetadata> fieldList = IFormFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fieldList.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // 切换到租户数据源
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.TENANT_PREFIX + tenantId);

            // 构建查询条件
            Map<String, Object> queryParams = buildQueryParams(params, fieldList);

            // 查询数据
            return formDataMapper.selectFormDataList(mapping.getTableName(), queryParams);
        } finally {
            // 恢复默认数据源
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 新增表单数据
     *
     * @param metadataId 元数据ID
     * @param data       表单数据
     * @return 结果
     */
    @Override
    @Transactional
    public int insertFormData(String metadataId, Map<String, Object> data) {
        // 获取表单元数据
        FormMetadata metadata = IFormMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            return 0;
        }

        // 获取租户表映射
        String tenantId = SecurityUtils.getCurrComId();
        FormTenantTableMapping mapping = IFormTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null) {
            // 创建租户表映射
            mapping = new FormTenantTableMapping();
            mapping.setMappingId(UUID.randomUUID().toString().replace("-", ""));
            mapping.setMetadataId(metadataId);
            mapping.setTenantId(tenantId);
            mapping.setTableName("form_data_" + metadataId.replace("-", ""));
            mapping.setIsCreated(0);
            mapping.setCreateTime(new Date());
            IFormTenantTableMappingService.insertFormTenantTableMapping(mapping);
        }

        // 获取字段列表
        List<FormFieldMetadata> fieldList = IFormFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fieldList.isEmpty()) {
            return 0;
        }

        try {
            // 切换到租户数据源
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.TENANT_PREFIX + tenantId);

            // 如果表不存在，则创建表
            if (mapping.getIsCreated() != 1) {
                boolean created = createTable(mapping.getTableName(), fieldList);
                if (created) {
                    mapping.setIsCreated(1);
                    mapping.setUpdateTime(new Date());
                    IFormTenantTableMappingService.updateFormTenantTableMapping(mapping);
                } else {
                    return 0;
                }
            }

            // 处理数据
            Map<String, Object> insertData = processData(data, fieldList);

            // 添加创建时间和创建者
            insertData.put("create_time", DateUtils.getNowDate());
            insertData.put("create_by", SecurityUtils.getUsername());

            // 插入数据
            return formDataMapper.insertFormData(mapping.getTableName(), insertData);
        } finally {
            // 恢复默认数据源
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 修改表单数据
     *
     * @param metadataId 元数据ID
     * @param data       表单数据
     * @return 结果
     */
    @Override
    @Transactional
    public int updateFormData(String metadataId, Map<String, Object> data) {
        // 获取表单元数据
        FormMetadata metadata = IFormMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            return 0;
        }

        // 获取租户表映射
        String tenantId = SecurityUtils.getCurrComId();
        FormTenantTableMapping mapping = IFormTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            return 0;
        }

        // 获取字段列表
        List<FormFieldMetadata> fieldList = IFormFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fieldList.isEmpty()) {
            return 0;
        }

        // 获取主键字段
        String pkColumn = getPrimaryKeyColumn(fieldList);
        if (StringUtils.isEmpty(pkColumn)) {
            return 0;
        }

        // 获取主键值
        Object pkValue = data.get(pkColumn);
        if (pkValue == null) {
            return 0;
        }

        try {
            // 切换到租户数据源
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.TENANT_PREFIX + tenantId);

            // 处理数据
            Map<String, Object> updateData = processData(data, fieldList);

            // 添加更新时间和更新者
            updateData.put("update_time", DateUtils.getNowDate());
            updateData.put("update_by", SecurityUtils.getUsername());

            // 更新数据
            return formDataMapper.updateFormData(mapping.getTableName(), pkColumn, pkValue.toString(), updateData);
        } finally {
            // 恢复默认数据源
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 删除表单数据
     *
     * @param metadataId 元数据ID
     * @param dataId     数据ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFormDataById(String metadataId, String dataId) {
        // 获取表单元数据
        FormMetadata metadata = IFormMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            return 0;
        }

        // 获取租户表映射
        String tenantId = SecurityUtils.getCurrComId();
        FormTenantTableMapping mapping = IFormTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            return 0;
        }

        // 获取字段列表
        List<FormFieldMetadata> fieldList = IFormFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        String pkColumn = getPrimaryKeyColumn(fieldList);
        if (StringUtils.isEmpty(pkColumn)) {
            return 0;
        }

        try {
            // 切换到租户数据源
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.TENANT_PREFIX + tenantId);

            // 删除数据
            return formDataMapper.deleteFormDataById(mapping.getTableName(), pkColumn, dataId);
        } finally {
            // 恢复默认数据源
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 批量删除表单数据
     *
     * @param metadataId 元数据ID
     * @param dataIds    需要删除的数据ID集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFormDataByIds(String metadataId, String[] dataIds) {
        // 获取表单元数据
        FormMetadata metadata = IFormMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            return 0;
        }

        // 获取租户表映射
        String tenantId = SecurityUtils.getCurrComId();
        FormTenantTableMapping mapping = IFormTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null || mapping.getIsCreated() != 1) {
            return 0;
        }

        // 获取字段列表
        List<FormFieldMetadata> fieldList = IFormFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        String pkColumn = getPrimaryKeyColumn(fieldList);
        if (StringUtils.isEmpty(pkColumn)) {
            return 0;
        }

        try {
            // 切换到租户数据源
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.TENANT_PREFIX + tenantId);

            // 批量删除数据
            return formDataMapper.deleteFormDataByIds(mapping.getTableName(), pkColumn, dataIds);
        } finally {
            // 恢复默认数据源
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 导入表单数据
     *
     * @param metadataId    元数据ID
     * @param dataList      数据列表
     * @param updateSupport 是否更新已存在数据
     * @return 结果
     */
    @Override
    @Transactional
    public String importFormData(String metadataId, List<Map<String, Object>> dataList, boolean updateSupport) {
        if (dataList == null || dataList.isEmpty()) {
            return "导入数据为空";
        }

        // 获取表单元数据
        FormMetadata metadata = IFormMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {
            return "表单元数据不存在";
        }

        // 获取租户表映射
        String tenantId = SecurityUtils.getCurrComId();
        FormTenantTableMapping mapping = IFormTenantTableMappingService.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
        if (mapping == null) {
            // 创建租户表映射
            mapping = new FormTenantTableMapping();
            mapping.setMappingId(UUID.randomUUID().toString().replace("-", ""));
            mapping.setMetadataId(metadataId);
            mapping.setTenantId(tenantId);
            mapping.setTableName("form_data_" + metadataId.replace("-", ""));
            mapping.setIsCreated(0);
            mapping.setCreateTime(new Date());
            IFormTenantTableMappingService.insertFormTenantTableMapping(mapping);
        }

        // 获取字段列表
        List<FormFieldMetadata> fieldList = IFormFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fieldList.isEmpty()) {
            return "表单字段不存在";
        }

        // 获取主键字段
        String pkColumn = getPrimaryKeyColumn(fieldList);
        if (StringUtils.isEmpty(pkColumn)) {
            return "主键字段不存在";
        }

        try {
            // 切换到租户数据源
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.TENANT_PREFIX + tenantId);

            // 如果表不存在，则创建表
            if (mapping.getIsCreated() != 1) {
                boolean created = createTable(mapping.getTableName(), fieldList);
                if (created) {
                    mapping.setIsCreated(1);
                    mapping.setUpdateTime(new Date());
                    IFormTenantTableMappingService.updateFormTenantTableMapping(mapping);
                } else {
                    return "创建表失败";
                }
            }

            // 导入结果
            StringBuilder successMsg = new StringBuilder();
            StringBuilder failureMsg = new StringBuilder();
            int successCount = 0;
            int failureCount = 0;

            // 处理每条数据
            for (Map<String, Object> data : dataList) {
                try {
                    // 处理数据
                    Map<String, Object> processedData = processData(data, fieldList);

                    // 获取主键值
                    Object pkValue = processedData.get(pkColumn);
                    if (pkValue == null) {
                        failureCount++;
                        failureMsg.append("<br/>第 ").append(failureCount).append(" 条数据主键为空");
                        continue;
                    }

                    // 检查数据是否已存在
                    Map<String, Object> existingData = formDataMapper.selectFormDataById(mapping.getTableName(), pkColumn, pkValue.toString());
                    if (existingData != null) {
                        if (updateSupport) {
                            // 添加更新时间和更新者
                            processedData.put("update_time", DateUtils.getNowDate());
                            processedData.put("update_by", SecurityUtils.getUsername());

                            // 更新数据
                            formDataMapper.updateFormData(mapping.getTableName(), pkColumn, pkValue.toString(), processedData);
                            successCount++;
                        } else {
                            failureCount++;
                            failureMsg.append("<br/>第 ").append(failureCount).append(" 条数据已存在");
                        }
                    } else {
                        // 添加创建时间和创建者
                        processedData.put("create_time", DateUtils.getNowDate());
                        processedData.put("create_by", SecurityUtils.getUsername());

                        // 插入数据
                        formDataMapper.insertFormData(mapping.getTableName(), processedData);
                        successCount++;
                    }
                } catch (Exception e) {
                    failureCount++;
                    String message = "<br/>第 " + failureCount + " 条数据导入失败：" + e.getMessage();
                    failureMsg.append(message);
                    log.error(message, e);
                }
            }

            // 返回结果
            if (failureCount > 0) {
                failureMsg.insert(0, "共 " + dataList.size() + " 条数据，成功导入 " + successCount + " 条，失败 " + failureCount + " 条");
                return failureMsg.toString();
            } else {
                return "共 " + dataList.size() + " 条数据，全部导入成功";
            }
        } finally {
            // 恢复默认数据源
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 导出表单数据
     *
     * @param metadataId 元数据ID
     * @param params     查询参数
     * @return 表单数据集合
     */
    @Override
    public List<Map<String, Object>> exportFormData(String metadataId, Map<String, Object> params) {
        return selectFormDataList(metadataId, params);
    }

    /**
     * 获取主键字段列名
     *
     * @param fieldList 字段列表
     * @return 主键字段列名
     */
    private String getPrimaryKeyColumn(List<FormFieldMetadata> fieldList) {
        for (FormFieldMetadata field : fieldList) {
            if (field.getIsPk() == 1) {
                return field.getColumnName();
            }
        }
        return null;
    }

    /**
     * 处理表单数据
     *
     * @param data      原始数据
     * @param fieldList 字段列表
     * @return 处理后的数据
     */
    private Map<String, Object> processData(Map<String, Object> data, List<FormFieldMetadata> fieldList) {
        Map<String, Object> result = new HashMap<>();

        for (FormFieldMetadata field : fieldList) {
            String fieldName = field.getFieldName();
            String columnName = field.getColumnName();

            if (data.containsKey(fieldName)) {
                Object value = data.get(fieldName);

                // 类型转换
                if (value != null) {
                    if ("number".equals(field.getFieldType())) {
                        if (value instanceof String) {
                            try {
                                if (((String) value).contains(".")) {
                                    value = Double.parseDouble((String) value);
                                } else {
                                    value = Integer.parseInt((String) value);
                                }
                            } catch (NumberFormatException e) {
                                log.warn("数字类型转换失败: " + value, e);
                            }
                        }
                    } else if ("date".equals(field.getFieldType()) && value instanceof String) {
                        try {
                            value = DateUtils.parseDate((String) value);
                        } catch (Exception e) {
                            log.warn("日期类型转换失败: " + value, e);
                        }
                    } else if ("boolean".equals(field.getFieldType())) {
                        if (value instanceof String) {
                            value = Boolean.parseBoolean((String) value) ? 1 : 0;
                        } else if (value instanceof Boolean) {
                            value = (Boolean) value ? 1 : 0;
                        }
                    }
                }

                result.put(columnName, value);
            }
        }

        return result;
    }

    /**
     * 构建查询参数
     *
     * @param params    原始参数
     * @param fieldList 字段列表
     * @return 查询参数
     */
    private Map<String, Object> buildQueryParams(Map<String, Object> params, List<FormFieldMetadata> fieldList) {
        Map<String, Object> result = new HashMap<>();

        if (params == null) {
            return result;
        }

        // 字段名到列名的映射
        Map<String, FormFieldMetadata> fieldMap = new HashMap<>();
        for (FormFieldMetadata field : fieldList) {
            fieldMap.put(field.getFieldName(), field);
        }

        // 处理查询参数
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 跳过空值
            if (value == null || (value instanceof String && StringUtils.isEmpty((String) value))) {
                continue;
            }

            // 处理特殊查询参数
            if (key.startsWith("begin_") || key.startsWith("end_")) {
                String fieldName = key.substring(key.indexOf("_") + 1);
                FormFieldMetadata field = fieldMap.get(fieldName);

                if (field != null) {
                    String columnName = field.getColumnName();
                    if (key.startsWith("begin_")) {
                        result.put("begin_" + columnName, value);
                    } else {
                        result.put("end_" + columnName, value);
                    }
                }
            } else {
                FormFieldMetadata field = fieldMap.get(key);
                if (field != null) {
                    String columnName = field.getColumnName();
                    String queryType = field.getQueryType();

                    if ("LIKE".equals(queryType)) {
                        result.put(columnName + "_like", value);
                    } else if ("NE".equals(queryType)) {
                        result.put(columnName + "_ne", value);
                    } else if ("GT".equals(queryType)) {
                        result.put(columnName + "_gt", value);
                    } else if ("LT".equals(queryType)) {
                        result.put(columnName + "_lt", value);
                    } else if ("BETWEEN".equals(queryType)) {
                        // BETWEEN查询通过begin_和end_参数处理
                    } else {
                        // 默认为EQ查询
                        result.put(columnName, value);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 创建表
     *
     * @param tableName 表名
     * @param fieldList 字段列表
     * @return 是否成功
     */
    private boolean createTable(String tableName, List<FormFieldMetadata> fieldList) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

        // 添加字段
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

            // 添加逗号
            sql.append(", ");
        }

        // 添加公共字段
        sql.append("create_by VARCHAR(64) NULL COMMENT '创建者', ");
        sql.append("create_time DATETIME NULL COMMENT '创建时间', ");
        sql.append("update_by VARCHAR(64) NULL COMMENT '更新者', ");
        sql.append("update_time DATETIME NULL COMMENT '更新时间'");

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

        try {
            formDataMapper.executeSql(sql.toString());
            return true;
        } catch (Exception e) {
            log.error("创建表失败: " + sql, e);
            return false;
        }
    }
}