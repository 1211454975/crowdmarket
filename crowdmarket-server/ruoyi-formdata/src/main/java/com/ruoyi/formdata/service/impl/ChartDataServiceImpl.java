package com.ruoyi.formdata.service.impl;  

import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.StringUtils;
//import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.formdata.datasource.DynamicDataSourceService;
import com.ruoyi.formdata.datasource.TenantAwareDynamicDataSource;
import com.ruoyi.formdata.service.IChartDataService;
//import com.ruoyi.formdata.util.ChartDataUtils;
import com.ruoyi.framework.datasource.DynamicDataSource;
import com.ruoyi.metadata.domain.ChartConfig;
import com.ruoyi.metadata.domain.ChartMetadata;  
import com.ruoyi.metadata.domain.FormFieldMetadata;  
import com.ruoyi.metadata.domain.FormMetadata;  
import com.ruoyi.metadata.service.IChartConfigService;  
import com.ruoyi.metadata.service.IChartMetadataService;  
import com.ruoyi.metadata.service.IFormFieldMetadataService;  
import com.ruoyi.metadata.service.IFormMetadataService;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.cache.annotation.CacheEvict;  
import org.springframework.cache.annotation.Cacheable;  
import org.springframework.jdbc.core.JdbcTemplate;  
import org.springframework.stereotype.Service;  
  
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.util.*;  
import java.util.stream.Collectors;  
import com.fasterxml.jackson.databind.ObjectMapper;  
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;  
  
/**  
 * 图表数据服务实现类  
 *   
 * 负责根据图表配置查询和处理图表数据  
 *   
 * @author ruoyi  
 */  
@Service  
public class ChartDataServiceImpl implements IChartDataService {  
      
    private static final Logger log = LoggerFactory.getLogger(ChartDataServiceImpl.class);  
      
    @Autowired  
    private IChartMetadataService chartMetadataService;  
      
    @Autowired  
    private IChartConfigService chartConfigService;  
      
    @Autowired  
    private IFormMetadataService formMetadataService;  
      
    @Autowired  
    private IFormFieldMetadataService fieldMetadataService;  
      
    @Autowired  
    private DynamicDataSourceService dataSourceService;
      
    @Autowired  
    private JdbcTemplate jdbcTemplate;  
      
    @Autowired  
    private ObjectMapper objectMapper;  
      
    /**  
     * 获取图表数据  
     *   
     * @param chartId 图表ID  
     * @param params 查询参数  
     * @return 图表数据  
     */  
    @Override  
    @Cacheable(value = "chartData", key = "#chartId + '_' + #params.toString()")  
    public Map<String, Object> getChartData(String chartId, Map<String, Object> params) {  
        try {  
            // 获取图表元数据和配置  
            ChartMetadata chartMetadata = chartMetadataService.selectChartMetadataByChartId(chartId);
            if (chartMetadata == null) {  
                throw new CustomException("图表不存在");
            }  
              
            ChartConfig chartConfig = chartConfigService.selectChartConfigById(chartId);
            if (chartConfig == null) {  
                throw new CustomException("图表配置不存在");
            }  
              
            // 获取表单元数据  
            FormMetadata formMetadata = formMetadataService.selectFormMetadataByMetadataId(chartMetadata.getMetadataId());
            if (formMetadata == null) {  
                throw new CustomException("关联的表单元数据不存在");
            }  
              
            // 切换到租户数据源  
            dataSourceService.switchToTenantDataSource(chartMetadata.getTenantId());
              
            try {  
                // 构建查询SQL  
                String sql = buildChartSql(formMetadata, chartConfig, params);  
                log.debug("Chart query SQL: {}", sql);  
                  
                // 执行查询  
                List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);  
                  
                // 处理数据格式  
                Map<String, Object> result = processChartData(dataList, chartMetadata.getChartType(), chartConfig);  
                  
                // 添加图表元数据  
                result.put("chartInfo", chartMetadata);  
                  
                return result;  
            } finally {  
                // 切换回主数据源  
                dataSourceService.switchToMainDataSource();  
            }  
        } catch (Exception e) {  
            log.error("获取图表数据失败", e);  
            throw new CustomException("获取图表数据失败: " + e.getMessage());
        }  
    }  
      
    /**  
     * 获取图表维度数据  
     *   
     * @param chartId 图表ID  
     * @param params 查询参数  
     * @return 维度数据列表  
     */  
    @Override  
    public List<String> getDimensionData(String chartId, Map<String, Object> params) {  
        try {  
            // 获取图表元数据和配置  
            ChartMetadata chartMetadata = chartMetadataService.selectChartMetadataByChartId(chartId);
            if (chartMetadata == null) {  
                throw new CustomException("图表不存在");
            }  
              
            ChartConfig chartConfig = chartConfigService.selectChartConfigById(chartId);
            if (chartConfig == null) {  
                throw new CustomException("图表配置不存在");
            }  
              
            // 获取表单元数据  
            FormMetadata formMetadata = formMetadataService.selectFormMetadataByMetadataId(chartMetadata.getMetadataId());
            if (formMetadata == null) {  
                throw new CustomException("关联的表单元数据不存在");
            }  
              
            // 切换到租户数据源  
            dataSourceService.switchToTenantDataSource(chartMetadata.getTenantId());  
              
            try {  
                // 解析维度字段  
                List<Map<String, Object>> dimensionFields = parseDimensionFields(chartConfig.getDimensionFields());  
                if (dimensionFields.isEmpty()) {  
                    return new ArrayList<>();  
                }  
                  
                // 构建维度查询SQL  
                String sql = buildDimensionSql(formMetadata, dimensionFields, params, chartConfig);  
                log.debug("Dimension query SQL: {}", sql);  
                  
                // 执行查询  
                List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);  
                  
                // 提取维度值  
                String fieldName = dimensionFields.get(0).get("field").toString();  
                return dataList.stream()  
                        .map(row -> row.get(fieldName))  
                        .filter(Objects::nonNull)  
                        .map(Object::toString)  
                        .collect(Collectors.toList());  
            } finally {  
                // 切换回主数据源  
                dataSourceService.switchToMainDataSource();  
            }  
        } catch (Exception e) {  
            log.error("获取图表维度数据失败", e);  
            throw new CustomException("获取图表维度数据失败: " + e.getMessage());
        }  
    }  
      
    /**  
     * 获取图表度量数据  
     *   
     * @param chartId 图表ID  
     * @param dimensionValue 维度值  
     * @param params 查询参数  
     * @return 度量数据列表  
     */  
    @Override  
    public List<Object> getMeasureData(String chartId, String dimensionValue, Map<String, Object> params) {  
        try {  
            // 获取图表元数据和配置  
            ChartMetadata chartMetadata = chartMetadataService.selectChartMetadataByChartId(chartId);
            if (chartMetadata == null) {  
                throw new CustomException("图表不存在");
            }  
              
            ChartConfig chartConfig = chartConfigService.selectChartConfigById(chartId);
            if (chartConfig == null) {  
                throw new CustomException("图表配置不存在");
            }  
              
            // 获取表单元数据  
            FormMetadata formMetadata = formMetadataService.selectFormMetadataByMetadataId(chartMetadata.getMetadataId());
            if (formMetadata == null) {  
                throw new CustomException("关联的表单元数据不存在");
            }  
              
            // 切换到租户数据源  
            dataSourceService.switchToTenantDataSource(chartMetadata.getTenantId());  
              
            try {  
                // 解析维度和度量字段  
                List<Map<String, Object>> dimensionFields = parseDimensionFields(chartConfig.getDimensionFields());  
                List<Map<String, Object>> measureFields = parseMeasureFields(chartConfig.getMeasureFields());  
                  
                if (dimensionFields.isEmpty() || measureFields.isEmpty()) {  
                    return new ArrayList<>();  
                }  
                  
                // 构建度量查询SQL  
                String sql = buildMeasureSql(formMetadata, dimensionFields, measureFields, dimensionValue, params, chartConfig);  
                log.debug("Measure query SQL: {}", sql);  
                  
                // 执行查询  
                List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);  
                  
                // 提取度量值  
                String aliasName = measureFields.get(0).get("alias").toString();  
                return dataList.stream()  
                        .map(row -> row.get(aliasName))  
                        .collect(Collectors.toList());  
            } finally {  
                // 切换回主数据源  
                dataSourceService.switchToMainDataSource();  
            }  
        } catch (Exception e) {  
            log.error("获取图表度量数据失败", e);  
            throw new CustomException("获取图表度量数据失败: " + e.getMessage());
        }  
    }  
      
    /**  
     * 执行自定义图表查询  
     *   
     * @param metadataId 元数据ID  
     * @param dimensions 维度字段列表  
     * @param measures 度量字段列表  
     * @param filters 过滤条件  
     * @param sorts 排序条件  
     * @param limit 数据限制  
     * @return 查询结果  
     */  
    @Override  
    public Map<String, Object> executeChartQuery(  
            String metadataId,   
            List<String> dimensions,   
            List<Map<String, String>> measures,   
            Map<String, Object> filters,   
            List<Map<String, String>> sorts,   
            Integer limit) {  
        try {  
            // 获取表单元数据  
            FormMetadata formMetadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
            if (formMetadata == null) {  
                throw new CustomException("表单元数据不存在");
            }  
              
            // 切换到租户数据源  
            dataSourceService.switchToTenantDataSource(formMetadata.getTenantId());  
              
            try {  
                // 获取字段元数据  
                List<FormFieldMetadata> fieldList = fieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);  
                Map<String, FormFieldMetadata> fieldMap = fieldList.stream()  
                        .collect(Collectors.toMap(FormFieldMetadata::getFieldName, field -> field));  
                  
                // 构建自定义查询SQL  
                String sql = buildCustomQuerySql(formMetadata, dimensions, measures, filters, sorts, limit, fieldMap);  
                log.debug("Custom query SQL: {}", sql);  
                  
                // 执行查询  
                List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);  
                  
                // 处理结果  
                Map<String, Object> result = new HashMap<>();  
                result.put("data", dataList);  
                result.put("total", dataList.size());  
                  
                return result;  
            } finally {  
                // 切换回主数据源  
                dataSourceService.switchToMainDataSource();  
            }  
        } catch (Exception e) {  
            log.error("执行自定义图表查询失败", e);  
            throw new CustomException("执行自定义图表查询失败: " + e.getMessage());
        }  
    }  
      
    /**  
     * 导出图表数据  
     *   
     * @param chartId 图表ID  
     * @param params 查询参数  
     * @param fileType 文件类型(excel, csv, pdf)  
     * @return 文件字节数组  
     */  
    @Override  
    public byte[] exportChartData(String chartId, Map<String, Object> params, String fileType) {  
        try {  
            // 获取图表数据  
            Map<String, Object> chartData = getChartData(chartId, params);  
              
            // 根据文件类型导出数据  
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
              
            switch (fileType.toLowerCase()) {  
                case "excel":  
                    exportToExcel(chartData, outputStream);  
                    break;  
                case "csv":  
                    exportToCsv(chartData, outputStream);  
                    break;  
                case "pdf":  
                    exportToPdf(chartData, outputStream);  
                    break;  
                default:  
                    throw new CustomException("不支持的文件类型: " + fileType);
            }  
              
            return outputStream.toByteArray();  
        } catch (Exception e) {  
            log.error("导出图表数据失败", e);  
            throw new CustomException("导出图表数据失败: " + e.getMessage());
        }  
    }  
      
    /**  
     * 获取图表数据统计信息  
     *   
     * @param chartId 图表ID  
     * @param params 查询参数  
     * @return 统计信息  
     */  
    @Override  
    public Map<String, Object> getChartStatistics(String chartId, Map<String, Object> params) {  
        try {  
            // 获取图表元数据和配置  
            ChartMetadata chartMetadata = chartMetadataService.selectChartMetadataByChartId(chartId);
            if (chartMetadata == null) {  
                throw new CustomException("图表不存在");
            }  
              
            ChartConfig chartConfig = chartConfigService.selectChartConfigById(chartId);
            if (chartConfig == null) {  
                throw new CustomException("图表配置不存在");
            }  
              
            // 获取表单元数据  
            FormMetadata formMetadata = formMetadataService.selectFormMetadataByMetadataId(chartMetadata.getMetadataId());
            if (formMetadata == null) {  
                throw new CustomException("关联的表单元数据不存在");
            }  
              
            // 切换到租户数据源  
            dataSourceService.switchToTenantDataSource(chartMetadata.getTenantId());  
              
            try {  
                // 解析度量字段  
                List<Map<String, Object>> measureFields = parseMeasureFields(chartConfig.getMeasureFields());  
                  
                // 构建统计查询SQL  
                String sql = buildStatisticsSql(formMetadata, measureFields, params, chartConfig);  
                log.debug("Statistics query SQL: {}", sql);  
                  
                // 执行查询  
                Map<String, Object> statistics = jdbcTemplate.queryForMap(sql);  
                  
                return statistics;  
            } finally {  
                // 切换回主数据源  
                dataSourceService.switchToMainDataSource();  
            }  
        } catch (Exception e) {  
            log.error("获取图表数据统计信息失败", e);  
            throw new CustomException("获取图表数据统计信息失败: " + e.getMessage());
        }  
    }  
      
    /**  
     * 刷新图表数据缓存  
     *   
     * @param chartId 图表ID  
     * @return 是否成功  
     */  
    @Override  
    @CacheEvict(value = "chartData", allEntries = true)  
    public boolean refreshChartDataCache(String chartId) {  
        log.info("刷新图表数据缓存: {}", chartId);  
        return true;  
    }  
      
    /**  
     * 构建图表查询SQL  
     *   
     * @param formMetadata 表单元数据  
     * @param chartConfig 图表配置  
     * @param params 查询参数  
     * @return SQL语句  
     */  
    private String buildChartSql(FormMetadata formMetadata, ChartConfig chartConfig, Map<String, Object> params) {  
        StringBuilder sql = new StringBuilder();  
          
        // 获取表名  
        String tableName = "form_data_" + formMetadata.getMetadataId().replace("-", "");  
          
        // 解析维度和度量字段  
        List<Map<String, Object>> dimensionFields = parseDimensionFields(chartConfig.getDimensionFields());  
        List<Map<String, Object>> measureFields = parseMeasureFields(chartConfig.getMeasureFields());  
          
        // 构建SELECT子句  
        sql.append("SELECT ");  
          
        // 添加维度字段  
        for (int i = 0; i < dimensionFields.size(); i++) {  
            Map<String, Object> dimension = dimensionFields.get(i);  
            String field = dimension.get("field").toString();  
            sql.append(field);  
            if (dimension.containsKey("alias") && StringUtils.isNotEmpty(dimension.get("alias").toString())) {  
                sql.append(" AS ").append(dimension.get("alias"));  
            }  
            sql.append(", ");  
        }  
          
        // 添加度量字段  
        for (int i = 0; i < measureFields.size(); i++) {  
            Map<String, Object> measure = measureFields.get(i);  
            String field = measure.get("field").toString();  
            String aggregation = measure.get("aggregation").toString();  
            String alias = measure.get("alias").toString();  
              
            sql.append(aggregation).append("(").append(field).append(") AS ").append(alias);  
              
            if (i < measureFields.size() - 1) {  
                sql.append(", ");  
            }  
        }  
          
        // 构建FROM子句  
        sql.append(" FROM ").append(tableName);  
          
        // 构建WHERE子句  
        Map<String, Object> filterCondition = parseFilterCondition(chartConfig.getFilterCondition());  
        if (filterCondition != null && !filterCondition.isEmpty()) {  
            sql.append(" WHERE ");  
            buildWhereClause(sql, filterCondition, params);  
        }  
          
        // 构建GROUP BY子句  
        if (!dimensionFields.isEmpty()) {  
            sql.append(" GROUP BY ");  
            for (int i = 0; i < dimensionFields.size(); i++) {  
                Map<String, Object> dimension = dimensionFields.get(i);  
                String field = dimension.get("field").toString();  
                sql.append(field);  
                  
                if (i < dimensionFields.size() - 1) {  
                    sql.append(", ");  
                }  
            }  
        }  
          
        // 构建ORDER BY子句  
        List<Map<String, Object>> sortConfig = parseSortConfig(chartConfig.getSortConfig());  
        if (sortConfig != null && !sortConfig.isEmpty()) {  
            sql.append(" ORDER BY ");  
            for (int i = 0; i < sortConfig.size(); i++) {  
                Map<String, Object> sort = sortConfig.get(i);  
                String field = sort.get("field").toString();  
                String direction = sort.get("direction").toString();  
                  
                sql.append(field).append(" ").append(direction);  
                  
                if (i < sortConfig.size() - 1) {  
                    sql.append(", ");  
                }  
            }  
        }  
          
        // 添加数据限制  
        Integer dataLimit = chartConfig.getDataLimit();  
        if (dataLimit != null && dataLimit > 0) {  
            sql.append(" LIMIT ").append(dataLimit);  
        }  
          
        return sql.toString();  
    }  
      
    /**  
     * 构建维度查询SQL  
     *   
     * @param formMetadata 表单元数据  
     * @param dimensionFields 维度字段  
     * @param params 查询参数  
     * @param chartConfig 图表配置  
     * @return SQL语句  
     */  
    private String buildDimensionSql(FormMetadata formMetadata, List<Map<String, Object>> dimensionFields,   
                                    Map<String, Object> params, ChartConfig chartConfig) {  
        StringBuilder sql = new StringBuilder();  
          
        // 获取表名  
        String tableName = "form_data_" + formMetadata.getMetadataId().replace("-", "");  
          
        // 构建SELECT子句  
        sql.append("SELECT DISTINCT ");  
          
        // 添加维度字段  
        Map<String, Object> dimension = dimensionFields.get(0);  
        String field = dimension.get("field").toString();  
        sql.append(field);  
          
        // 构建FROM子句  
        sql.append(" FROM ").append(tableName);  
          
        // 构建WHERE子句  
        Map<String, Object> filterCondition = parseFilterCondition(chartConfig.getFilterCondition());  
        if (filterCondition != null && !filterCondition.isEmpty()) {  
            sql.append(" WHERE ");  
            buildWhereClause(sql, filterCondition, params);  
        }  
          
        // 构建ORDER BY子句  
        sql.append(" ORDER BY ").append(field);  
          
        // 添加数据限制  
        Integer dataLimit = chartConfig.getDataLimit();  
        if (dataLimit != null && dataLimit > 0) {  
            sql.append(" LIMIT ").append(dataLimit);  
        }  
          
        return sql.toString();  
    }  
      
    /**  
     * 构建度量查询SQL  
     *   
     * @param formMetadata 表单元数据  
     * @param dimensionFields 维度字段  
     * @param measureFields 度量字段  
     * @param dimensionValue 维度值  
     * @param params 查询参数  
     * @param chartConfig 图表配置  
     * @return SQL语句  
     */  
    private String buildMeasureSql(FormMetadata formMetadata, List<Map<String, Object>> dimensionFields,  
                                  List<Map<String, Object>> measureFields, String dimensionValue,  
                                  Map<String, Object> params, ChartConfig chartConfig) {  
        StringBuilder sql = new StringBuilder();  
          
        // 获取表名  
        String tableName = "form_data_" + formMetadata.getMetadataId().replace("-", "");  
          
        // 构建SELECT子句  
        sql.append("SELECT ");  
          
        // 添加度量字段  
        Map<String, Object> measure = measureFields.get(0);  
        String field = measure.get("field").toString();  
        String aggregation = measure.get("aggregation").toString();  
        String alias = measure.get("alias").toString();  
          
        sql.append(aggregation).append("(").append(field).append(") AS ").append(alias);  
          
        // 构建FROM子句  
        sql.append(" FROM ").append(tableName);  
          
        // 构建WHERE子句  
        sql.append(" WHERE ");  
          
        // 添加维度条件  
        Map<String, Object> dimension = dimensionFields.get(0);  
        String dimensionField = dimension.get("field").toString();  
        sql.append(dimensionField).append(" = '").append(dimensionValue).append("'");  
          
        // 添加其他过滤条件  
        Map<String, Object> filterCondition = parseFilterCondition(chartConfig.getFilterCondition());  
        if (filterCondition != null && !filterCondition.isEmpty()) {  
            sql.append(" AND ");  
            buildWhereClause(sql, filterCondition, params);  
        }  
          
        return sql.toString();  
    }  
      
    /**  
     * 构建自定义查询SQL  
     *   
     * @param formMetadata 表单元数据  
     * @param dimensions 维度字段列表  
     * @param measures 度量字段列表  
     * @param filters 过滤条件  
     * @param sorts 排序条件  
     * @param limit 数据限制  
     * @param fieldMap 字段映射  
     * @return SQL语句  
     */  
    private String buildCustomQuerySql(FormMetadata formMetadata, List<String> dimensions,  
                                      List<Map<String, String>> measures, Map<String, Object> filters,  
                                      List<Map<String, String>> sorts, Integer limit,  
                                      Map<String, FormFieldMetadata> fieldMap) {  
        StringBuilder sql = new StringBuilder();  
          
        // 获取表名  
        String tableName = "form_data_" + formMetadata.getMetadataId().replace("-", "");  
          
        // 构建SELECT子句  
        sql.append("SELECT ");  
          
        // 添加维度字段  
        for (int i = 0; i < dimensions.size(); i++) {  
            String dimension = dimensions.get(i);  
            FormFieldMetadata field = fieldMap.get(dimension);  
            if (field != null) {  
                sql.append(field.getColumnName());  
                sql.append(" AS ").append(dimension);  
                sql.append(", ");  
            }  
        }  
          
        // 添加度量字段  
        for (int i = 0; i < measures.size(); i++) {  
            Map<String, String> measure = measures.get(i);  
            String fieldName = measure.get("field");  
            String aggregation = measure.get("aggregation");  
            String alias = measure.get("alias");  
              
            FormFieldMetadata field = fieldMap.get(fieldName);  
            if (field != null) {  
                sql.append(aggregation).append("(").append(field.getColumnName()).append(")");  
                if (StringUtils.isNotEmpty(alias)) {  
                    sql.append(" AS ").append(alias);  
                } else {  
                    sql.append(" AS ").append(fieldName).append("_").append(aggregation);  
                }  
                  
                if (i < measures.size() - 1) {  
                    sql.append(", ");  
                }  
            }  
        }  
          
        // 移除最后一个逗号  
        if (sql.charAt(sql.length() - 2) == ',') {  
            sql.delete(sql.length() - 2, sql.length() - 1);  
        }  
          
        // 构建FROM子句  
        sql.append(" FROM ").append(tableName);  
          
        // 构建WHERE子句  
        if (filters != null && !filters.isEmpty()) {  
            sql.append(" WHERE ");  
            int i = 0;  
            for (Map.Entry<String, Object> entry : filters.entrySet()) {  
                String fieldName = entry.getKey();  
                Object value = entry.getValue();  
                  
                FormFieldMetadata field = fieldMap.get(fieldName);  
                if (field != null) {  
                    if (i > 0) {  
                        sql.append(" AND ");  
                    }  
                      
                    if (value instanceof Map) {  
                        // 复杂条件  
                        Map<String, Object> condition = (Map<String, Object>) value;  
                        String operator = condition.get("operator").toString();  
                        Object conditionValue = condition.get("value");  
                          
                        sql.append(field.getColumnName());  
                          
                        switch (operator) {  
                            case "eq":  
                                sql.append(" = ");  
                                break;  
                            case "ne":  
                                sql.append(" != ");  
                                break;  
                            case "gt":  
                                sql.append(" > ");  
                                break;  
                            case "lt":  
                                sql.append(" < ");  
                                break;  
                            case "ge":  
                                sql.append(" >= ");  
                                break;  
                            case "le":  
                                sql.append(" <= ");  
                                break;  
                            case "like":  
                                sql.append(" LIKE ");  
                                conditionValue = "%" + conditionValue + "%";  
                                break;  
                            case "in":  
                                sql.append(" IN (");  
                                if (conditionValue instanceof List) {  
                                    List<Object> values = (List<Object>) conditionValue;  
                                    for (int j = 0; j < values.size(); j++) {  
                                        sql.append("'").append(values.get(j)).append("'");  
                                        if (j < values.size() - 1) {  
                                            sql.append(", ");  
                                        }  
                                    }  
                                }  
                                sql.append(")");  
                                break;  
                            default:  
                                sql.append(" = ");  
                        }  
                          
                        if (!"in".equals(operator)) {  
                            sql.append("'").append(conditionValue).append("'");  
                        }  
                    } else {  
                        // 简单条件
                        sql.append(field.getColumnName()).append(" = '").append(value).append("'");  
                    }  
                      
                    i++;  
                }  
            }  
        }  
          
        // 构建GROUP BY子句  
        if (!dimensions.isEmpty()) {  
            sql.append(" GROUP BY ");  
            for (int i = 0; i < dimensions.size(); i++) {  
                String dimension = dimensions.get(i);  
                FormFieldMetadata field = fieldMap.get(dimension);  
                if (field != null) {  
                    sql.append(field.getColumnName());  
                      
                    if (i < dimensions.size() - 1) {  
                        sql.append(", ");  
                    }  
                }  
            }  
        }  
          
        // 构建ORDER BY子句  
        if (sorts != null && !sorts.isEmpty()) {  
            sql.append(" ORDER BY ");  
            for (int i = 0; i < sorts.size(); i++) {  
                Map<String, String> sort = sorts.get(i);  
                String fieldName = sort.get("field");  
                String direction = sort.get("direction");  
                  
                FormFieldMetadata field = fieldMap.get(fieldName);  
                if (field != null) {  
                    sql.append(field.getColumnName()).append(" ").append(direction);  
                      
                    if (i < sorts.size() - 1) {  
                        sql.append(", ");  
                    }  
                }  
            }  
        }  
          
        // 添加数据限制  
        if (limit != null && limit > 0) {  
            sql.append(" LIMIT ").append(limit);  
        }  
          
        return sql.toString();  
    }  
      
    /**  
     * 构建统计查询SQL  
     *   
     * @param formMetadata 表单元数据  
     * @param measureFields 度量字段  
     * @param params 查询参数  
     * @param chartConfig 图表配置  
     * @return SQL语句  
     */  
    private String buildStatisticsSql(FormMetadata formMetadata, List<Map<String, Object>> measureFields,   
                                     Map<String, Object> params, ChartConfig chartConfig) {  
        StringBuilder sql = new StringBuilder();  
          
        // 获取表名  
        String tableName = "form_data_" + formMetadata.getMetadataId().replace("-", "");  
          
        // 构建SELECT子句  
        sql.append("SELECT ");  
          
        // 添加统计函数  
        for (int i = 0; i < measureFields.size(); i++) {  
            Map<String, Object> measure = measureFields.get(i);  
            String field = measure.get("field").toString();  
              
            // 添加常用统计函数  
            sql.append("COUNT(").append(field).append(") AS count_").append(field).append(", ");  
            sql.append("SUM(").append(field).append(") AS sum_").append(field).append(", ");  
            sql.append("AVG(").append(field).append(") AS avg_").append(field).append(", ");  
            sql.append("MIN(").append(field).append(") AS min_").append(field).append(", ");  
            sql.append("MAX(").append(field).append(") AS max_").append(field);  
              
            if (i < measureFields.size() - 1) {  
                sql.append(", ");  
            }  
        }  
          
        // 构建FROM子句  
        sql.append(" FROM ").append(tableName);  
          
        // 构建WHERE子句  
        Map<String, Object> filterCondition = parseFilterCondition(chartConfig.getFilterCondition());  
        if (filterCondition != null && !filterCondition.isEmpty()) {  
            sql.append(" WHERE ");  
            buildWhereClause(sql, filterCondition, params);  
        }  
          
        return sql.toString();  
    }  
      
    /**  
     * 构建WHERE子句  
     *   
     * @param sql SQL构建器  
     * @param filterCondition 过滤条件  
     * @param params 查询参数  
     */  
    private void buildWhereClause(StringBuilder sql, Map<String, Object> filterCondition, Map<String, Object> params) {  
        if (filterCondition.containsKey("conditions")) {  
            // 复合条件  
            List<Map<String, Object>> conditions = (List<Map<String, Object>>) filterCondition.get("conditions");  
            String logicType = filterCondition.containsKey("logic") ? filterCondition.get("logic").toString() : "AND";  
              
            if (!conditions.isEmpty()) {  
                sql.append("(");  
                  
                for (int i = 0; i < conditions.size(); i++) {  
                    Map<String, Object> condition = conditions.get(i);  
                      
                    if (condition.containsKey("conditions")) {  
                        // 嵌套条件  
                        buildWhereClause(sql, condition, params);  
                    } else {  
                        // 简单条件  
                        String field = condition.get("field").toString();  
                        String operator = condition.get("operator").toString();  
                        Object value = condition.get("value");  
                          
                        // 替换参数值  
                        if (value instanceof String && ((String) value).startsWith("${") && ((String) value).endsWith("}")) {  
                            String paramName = ((String) value).substring(2, ((String) value).length() - 1);  
                            if (params.containsKey(paramName)) {  
                                value = params.get(paramName);  
                            }  
                        }  
                          
                        sql.append(field);  
                          
                        switch (operator) {  
                            case "eq":  
                                sql.append(" = ");  
                                break;  
                            case "ne":  
                                sql.append(" != ");  
                                break;  
                            case "gt":  
                                sql.append(" > ");  
                                break;  
                            case "lt":  
                                sql.append(" < ");  
                                break;  
                            case "ge":  
                                sql.append(" >= ");  
                                break;  
                            case "le":  
                                sql.append(" <= ");  
                                break;  
                            case "like":  
                                sql.append(" LIKE ");  
                                value = "%" + value + "%";  
                                break;  
                            case "in":  
                                sql.append(" IN (");  
                                if (value instanceof List) {  
                                    List<Object> values = (List<Object>) value;  
                                    for (int j = 0; j < values.size(); j++) {  
                                        sql.append("'").append(values.get(j)).append("'");  
                                        if (j < values.size() - 1) {  
                                            sql.append(", ");  
                                        }  
                                    }  
                                    sql.append(")");  
                                    value = null; // 已处理，不需要再添加值  
                                } else {  
                                    sql.append("'").append(value).append("')");  
                                    value = null; // 已处理，不需要再添加值  
                                }  
                                break;  
                            case "between":  
                                sql.append(" BETWEEN ");  
                                if (value instanceof List && ((List) value).size() >= 2) {  
                                    List<Object> values = (List<Object>) value;  
                                    sql.append("'").append(values.get(0)).append("' AND '").append(values.get(1)).append("'");  
                                    value = null; // 已处理，不需要再添加值  
                                }  
                                break;  
                            default:  
                                sql.append(" = ");  
                        }  
                          
                        if (value != null) {  
                            sql.append("'").append(value).append("'");  
                        }  
                    }  
                      
                    if (i < conditions.size() - 1) {  
                        sql.append(" ").append(logicType).append(" ");  
                    }  
                }  
                  
                sql.append(")");  
            }  
        } else if (filterCondition.containsKey("field")) {  
            // 简单条件  
            String field = filterCondition.get("field").toString();  
            String operator = filterCondition.get("operator").toString();  
            Object value = filterCondition.get("value");  
              
            // 替换参数值  
            if (value instanceof String && ((String) value).startsWith("${") && ((String) value).endsWith("}")) {  
                String paramName = ((String) value).substring(2, ((String) value).length() - 1);  
                if (params.containsKey(paramName)) {  
                    value = params.get(paramName);  
                }  
            }  
              
            sql.append(field);  
              
            switch (operator) {  
                case "eq":  
                    sql.append(" = ");  
                    break;  
                case "ne":  
                    sql.append(" != ");  
                    break;  
                case "gt":  
                    sql.append(" > ");  
                    break;  
                case "lt":  
                    sql.append(" < ");  
                    break;  
                case "ge":  
                    sql.append(" >= ");  
                    break;  
                case "le":  
                    sql.append(" <= ");  
                    break;  
                case "like":  
                    sql.append(" LIKE ");  
                    value = "%" + value + "%";  
                    break;  
                case "in":  
                    sql.append(" IN (");  
                    if (value instanceof List) {  
                        List<Object> values = (List<Object>) value;  
                        for (int j = 0; j < values.size(); j++) {  
                            sql.append("'").append(values.get(j)).append("'");  
                            if (j < values.size() - 1) {  
                                sql.append(", ");  
                            }  
                        }  
                        sql.append(")");  
                        value = null; // 已处理，不需要再添加值  
                    } else {  
                        sql.append("'").append(value).append("')");  
                        value = null; // 已处理，不需要再添加值  
                    }  
                    break;  
                case "between":  
                    sql.append(" BETWEEN ");  
                    if (value instanceof List && ((List) value).size() >= 2) {  
                        List<Object> values = (List<Object>) value;  
                        sql.append("'").append(values.get(0)).append("' AND '").append(values.get(1)).append("'");  
                        value = null; // 已处理，不需要再添加值  
                    }  
                    break;  
                default:  
                    sql.append(" = ");  
            }  
              
            if (value != null) {  
                sql.append("'").append(value).append("'");  
            }  
        }  
    }  
      
    /**  
     * 解析维度字段  
     *   
     * @param dimensionFieldsJson 维度字段JSON  
     * @return 维度字段列表  
     */  
    private List<Map<String, Object>> parseDimensionFields(String dimensionFieldsJson) {  
        if (StringUtils.isEmpty(dimensionFieldsJson)) {  
            return new ArrayList<>();  
        }  
          
        try {  
            return objectMapper.readValue(dimensionFieldsJson, List.class);  
        } catch (Exception e) {  
            log.error("解析维度字段失败", e);  
            return new ArrayList<>();  
        }  
    }  
      
    /**  
     * 解析度量字段  
     *   
     * @param measureFieldsJson 度量字段JSON  
     * @return 度量字段列表  
     */  
    private List<Map<String, Object>> parseMeasureFields(String measureFieldsJson) {  
        if (StringUtils.isEmpty(measureFieldsJson)) {  
            return new ArrayList<>();  
        }  
          
        try {  
            return objectMapper.readValue(measureFieldsJson, List.class);  
        } catch (Exception e) {  
            log.error("解析度量字段失败", e);  
            return new ArrayList<>();  
        }  
    }  
      
    /**  
     * 解析过滤条件  
     *   
     * @param filterConditionJson 过滤条件JSON  
     * @return 过滤条件  
     */  
    private Map<String, Object> parseFilterCondition(String filterConditionJson) {  
        if (StringUtils.isEmpty(filterConditionJson)) {  
            return new HashMap<>();  
        }  
          
        try {  
            return objectMapper.readValue(filterConditionJson, Map.class);  
        } catch (Exception e) {  
            log.error("解析过滤条件失败", e);  
            return new HashMap<>();  
        }  
    }  
      
    /**  
     * 解析排序配置  
     *   
     * @param sortConfigJson 排序配置JSON  
     * @return 排序配置  
     */  
    private List<Map<String, Object>> parseSortConfig(String sortConfigJson) {  
        if (StringUtils.isEmpty(sortConfigJson)) {  
            return new ArrayList<>();  
        }  
          
        try {  
            return objectMapper.readValue(sortConfigJson, List.class);  
        } catch (Exception e) {  
            log.error("解析排序配置失败", e);  
            return new ArrayList<>();  
        }  
    }  
      
    /**  
     * 处理图表数据  
     *   
     * @param dataList 数据列表  
     * @param chartType 图表类型  
     * @param chartConfig 图表配置  
     * @return 处理后的图表数据  
     */  
    private Map<String, Object> processChartData(List<Map<String, Object>> dataList, String chartType, ChartConfig chartConfig) {  
        Map<String, Object> result = new HashMap<>();  
          
        // 解析维度和度量字段  
        List<Map<String, Object>> dimensionFields = parseDimensionFields(chartConfig.getDimensionFields());  
        List<Map<String, Object>> measureFields = parseMeasureFields(chartConfig.getMeasureFields());  
          
        // 根据图表类型处理数据  
        switch (chartType) {  
            case "bar":  
            case "line":  
                processBarOrLineChartData(result, dataList, chartType,dimensionFields, measureFields);
                break;  
            case "pie":  
                processPieChartData(result, dataList, chartType,dimensionFields, measureFields);
                break;  
            case "radar":  
                processRadarChartData(result, dataList, chartType,dimensionFields, measureFields);
                break;  
            case "scatter":  
                processScatterChartData(result, dataList, chartType,dimensionFields, measureFields);
                break;  
            default:  
                // 默认处理方式  
                result.put("data", dataList);  
        }  
          
        return result;  
    }  
      
    /**  
     * 处理柱状图或折线图数据  
     *   
     * @param result 结果  
     * @param dataList 数据列表  
     * @param dimensionFields 维度字段  
     * @param measureFields 度量字段  
     */  
    private void processBarOrLineChartData(Map<String, Object> result, List<Map<String, Object>> dataList,  
                                         String chartType, List<Map<String, Object>> dimensionFields, List<Map<String, Object>> measureFields) {
        if (dimensionFields.isEmpty() || measureFields.isEmpty() || dataList.isEmpty()) {  
            result.put("categories", new ArrayList<>());  
            result.put("series", new ArrayList<>());  
            return;  
        }  

        // 获取维度字段和度量字段  
        String dimensionField = dimensionFields.get(0).get("field").toString();  
          
        // 提取类别（X轴数据）  
        List<String> categories = dataList.stream()  
                .map(row -> row.get(dimensionField))  
                .filter(Objects::nonNull)  
                .map(Object::toString)  
                .collect(Collectors.toList());  
          
        // 构建系列数据  
        List<Map<String, Object>> series = new ArrayList<>();  
        for (Map<String, Object> measure : measureFields) {  
            String alias = measure.get("alias").toString();  
            String name = measure.containsKey("name") ? measure.get("name").toString() : alias;  
              
            Map<String, Object> seriesItem = new HashMap<>();  
            seriesItem.put("name", name);  
            seriesItem.put("type", chartType);  
              
            // 提取度量值  
            List<Object> data = dataList.stream()  
                    .map(row -> row.get(alias))  
                    .collect(Collectors.toList());  
              
            seriesItem.put("data", data);  
            series.add(seriesItem);  
        }  
          
        result.put("categories", categories);  
        result.put("series", series);  
    }  
      
    /**  
     * 处理饼图数据  
     *   
     * @param result 结果  
     * @param dataList 数据列表  
     * @param dimensionFields 维度字段  
     * @param measureFields 度量字段  
     */  
    private void processPieChartData(Map<String, Object> result, List<Map<String, Object>> dataList,
                                     String chartType, List<Map<String, Object>> dimensionFields, List<Map<String, Object>> measureFields) {
        if (dimensionFields.isEmpty() || measureFields.isEmpty() || dataList.isEmpty()) {  
            result.put("data", new ArrayList<>());  
            return;  
        }  
          
        // 获取维度字段和度量字段  
        String dimensionField = dimensionFields.get(0).get("field").toString();  
        String measureAlias = measureFields.get(0).get("alias").toString();  
          
        // 构建饼图数据  
        List<Map<String, Object>> pieData = new ArrayList<>();  
        for (Map<String, Object> row : dataList) {  
            Map<String, Object> item = new HashMap<>();  
            item.put("name", row.get(dimensionField));  
            item.put("value", row.get(measureAlias));  
            pieData.add(item);  
        }  
          
        result.put("data", pieData);  
    }  
      
    /**  
     * 处理雷达图数据  
     *   
     * @param result 结果  
     * @param dataList 数据列表  
     * @param dimensionFields 维度字段  
     * @param measureFields 度量字段  
     */  
    private void processRadarChartData(Map<String, Object> result, List<Map<String, Object>> dataList,
                                       String chartType, List<Map<String, Object>> dimensionFields, List<Map<String, Object>> measureFields) {
        if (dimensionFields.isEmpty() || measureFields.isEmpty() || dataList.isEmpty()) {  
            result.put("indicator", new ArrayList<>());  
            result.put("series", new ArrayList<>());  
            return;  
        }  
          
        // 获取维度字段和度量字段  
        String dimensionField = dimensionFields.get(0).get("field").toString();  
          
        // 构建雷达图指示器  
        List<Map<String, Object>> indicator = new ArrayList<>();  
        for (Map<String, Object> row : dataList) {  
            Map<String, Object> item = new HashMap<>();  
            item.put("name", row.get(dimensionField));  
              
            // 计算最大值  
            double max = 0;  
            for (Map<String, Object> measure : measureFields) {  
                String alias = measure.get("alias").toString();  
                Object value = row.get(alias);  
                if (value instanceof Number) {  
                    max = Math.max(max, ((Number) value).doubleValue() * 1.2); // 最大值增加20%作为上限  
                }  
            }  
              
            item.put("max", max);  
            indicator.add(item);  
        }  
          
        // 构建系列数据  
        List<Map<String, Object>> series = new ArrayList<>();  
        for (Map<String, Object> measure : measureFields) {  
            String alias = measure.get("alias").toString();  
            String name = measure.containsKey("name") ? measure.get("name").toString() : alias;  
              
            Map<String, Object> seriesItem = new HashMap<>();  
            seriesItem.put("name", name);  
            seriesItem.put("type", "radar");  
              
            // 提取度量值  
            List<Object> data = dataList.stream()  
                    .map(row -> row.get(alias))  
                    .collect(Collectors.toList());  
              
            Map<String, Object> dataItem = new HashMap<>();  
            dataItem.put("value", data);  
            dataItem.put("name", name);  
              
            List<Map<String, Object>> seriesData = new ArrayList<>();  
            seriesData.add(dataItem);  
              
            seriesItem.put("data", seriesData);  
            series.add(seriesItem);  
        }  
          
        result.put("indicator", indicator);  
        result.put("series", series);  
    }  
      
    /**  
     * 处理散点图数据  
     *   
     * @param result 结果  
     * @param dataList 数据列表  
     * @param dimensionFields 维度字段  
     * @param measureFields 度量字段  
     */  
    private void processScatterChartData(Map<String, Object> result, List<Map<String, Object>> dataList,
                                         String chartType, List<Map<String, Object>> dimensionFields, List<Map<String, Object>> measureFields) {
        if (dimensionFields.isEmpty() || measureFields.size() < 2 || dataList.isEmpty()) {  
            result.put("series", new ArrayList<>());  
            return;  
        }  
          
        // 获取X轴和Y轴度量字段  
        String xField = measureFields.get(0).get("alias").toString();  
        String yField = measureFields.get(1).get("alias").toString();  
          
        // 构建散点图数据  
        List<List<Object>> scatterData = new ArrayList<>();  
        for (Map<String, Object> row : dataList) {  
            List<Object> point = new ArrayList<>();  
            point.add(row.get(xField));  
            point.add(row.get(yField));  
            scatterData.add(point);  
        }  
          
        // 构建系列数据  
        List<Map<String, Object>> series = new ArrayList<>();  
        Map<String, Object> seriesItem = new HashMap<>();  
        seriesItem.put("type", "scatter");  
        seriesItem.put("data", scatterData);  
        series.add(seriesItem);  
          
        result.put("series", series);  
    }  
      
    /**  
     * 导出到Excel  
     *   
     * @param chartData 图表数据  
     * @param outputStream 输出流  
     * @throws IOException IO异常  
     */  
    private void exportToExcel(Map<String, Object> chartData, ByteArrayOutputStream outputStream) throws IOException {  
        // 获取图表信息  
        ChartMetadata chartMetadata = (ChartMetadata) chartData.get("chartInfo");  
          
        // 创建工作簿  
        Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();  
          
        // 创建工作表  
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet(chartMetadata.getChartName());  
          
        // 创建标题行  
        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);  
          
        // 根据图表类型处理数据  
        switch (chartMetadata.getChartType()) {  
            case "bar":  
            case "line":  
                exportBarOrLineChartToExcel(chartData, sheet, headerRow);  
                break;  
            case "pie":  
                exportPieChartToExcel(chartData, sheet, headerRow);  
                break;  
            case "radar":  
                exportRadarChartToExcel(chartData, sheet, headerRow);  
                break;  
            case "scatter":  
                exportScatterChartToExcel(chartData, sheet, headerRow);  
                break;  
            default:  
                // 默认导出原始数据  
                if (chartData.containsKey("data")) {  
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) chartData.get("data");  
                    if (!dataList.isEmpty()) {  
                        // 创建标题行  
                        int cellIndex = 0;  
                        for (String key : dataList.get(0).keySet()) {  
                            headerRow.createCell(cellIndex++).setCellValue(key);  
                        }  
                          
                        // 创建数据行  
                        int rowIndex = 1;  
                        for (Map<String, Object> row : dataList) {  
                            org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowIndex++);  
                            cellIndex = 0;  
                            for (Object value : row.values()) {  
                                dataRow.createCell(cellIndex++).setCellValue(value != null ? value.toString() : "");  
                            }  
                        }  
                    }  
                }  
        }  
          
        // 调整列宽  
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {  
            sheet.autoSizeColumn(i);  
        }  
          
        // 写入输出流  
        workbook.write(outputStream);  
        workbook.close();  
    }  
      
    /**  
     * 导出柱状图或折线图到Excel  
     *   
     * @param chartData 图表数据  
     * @param sheet 工作表  
     * @param headerRow 标题行  
     */  
    private void exportBarOrLineChartToExcel(Map<String, Object> chartData, org.apache.poi.ss.usermodel.Sheet sheet,  
                                           org.apache.poi.ss.usermodel.Row headerRow) {  
        List<String> categories = (List<String>) chartData.get("categories");  
        List<Map<String, Object>> series = (List<Map<String, Object>>) chartData.get("series");  
          
        // 创建标题行  
        headerRow.createCell(0).setCellValue("类别");  
        for (int i = 0; i < series.size(); i++) {  
            Map<String, Object> seriesItem = series.get(i);  
            headerRow.createCell(i + 1).setCellValue(seriesItem.get("name").toString());  
        }  
          
        // 创建数据行  
        for (int i = 0; i < categories.size(); i++) {  
            org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(i + 1);  
            dataRow.createCell(0).setCellValue(categories.get(i));  
              
            for (int j = 0; j < series.size(); j++) {  
                Map<String, Object> seriesItem = series.get(j);  
                List<Object> data = (List<Object>) seriesItem.get("data");  
                Object value = data.get(i);  
                dataRow.createCell(j + 1).setCellValue(value != null ? value.toString() : "");  
            }  
        }  
    }  
      
    /**  
     * 导出饼图到Excel  
     *   
     * @param chartData 图表数据  
     * @param sheet 工作表  
     * @param headerRow 标题行  
     */  
    private void exportPieChartToExcel(Map<String, Object> chartData, org.apache.poi.ss.usermodel.Sheet sheet,  
                                     org.apache.poi.ss.usermodel.Row headerRow) {  
        List<Map<String, Object>> data = (List<Map<String, Object>>) chartData.get("data");  
          
        // 创建标题行  
        headerRow.createCell(0).setCellValue("名称");  
        headerRow.createCell(1).setCellValue("值");  
          
        // 创建数据行  
        for (int i = 0; i < data.size(); i++) {  
            Map<String, Object> item = data.get(i);  
            org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(i + 1);  
            dataRow.createCell(0).setCellValue(item.get("name").toString());  
            dataRow.createCell(1).setCellValue(item.get("value").toString());  
        }  
    }  
      
    /**  
     * 导出雷达图到Excel  
     *   
     * @param chartData 图表数据  
     * @param sheet 工作表  
     * @param headerRow 标题行  
     */  
    private void exportRadarChartToExcel(Map<String, Object> chartData, org.apache.poi.ss.usermodel.Sheet sheet,  
                                       org.apache.poi.ss.usermodel.Row headerRow) {  
        List<Map<String, Object>> indicator = (List<Map<String, Object>>) chartData.get("indicator");  
        List<Map<String, Object>> series = (List<Map<String, Object>>) chartData.get("series");  
          
        // 创建标题行  
        headerRow.createCell(0).setCellValue("指标");  
        headerRow.createCell(1).setCellValue("最大值");  
          
        for (int i = 0; i < series.size(); i++) {  
            Map<String, Object> seriesItem = series.get(i);  
            headerRow.createCell(i + 2).setCellValue(seriesItem.get("name").toString());  
        }  
          
        // 创建数据行  
        for (int i = 0; i < indicator.size(); i++) {  
            Map<String, Object> item = indicator.get(i);  
            org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(i + 1);  
            dataRow.createCell(0).setCellValue(item.get("name").toString());  
            dataRow.createCell(1).setCellValue(item.get("max").toString());  
              
            for (int j = 0; j < series.size(); j++) {  
                Map<String, Object> seriesItem = series.get(j);  
                List<Map<String, Object>> seriesData = (List<Map<String, Object>>) seriesItem.get("data");  
                Map<String, Object> dataItem = seriesData.get(0);  
                List<Object> values = (List<Object>) dataItem.get("value");  
                Object value = values.get(i);  
                dataRow.createCell(j + 2).setCellValue(value != null ? value.toString() : "");  
            }  
        }  
    }  
      
    /**  
     * 导出散点图到Excel  
     *   
     * @param chartData 图表数据  
     * @param sheet 工作表  
     * @param headerRow 标题行  
     */  
    private void exportScatterChartToExcel(Map<String, Object> chartData, org.apache.poi.ss.usermodel.Sheet sheet,  
                                         org.apache.poi.ss.usermodel.Row headerRow) {  
        List<Map<String, Object>> series = (List<Map<String, Object>>) chartData.get("series");  
          
        // 创建标题行  
        headerRow.createCell(0).setCellValue("X值");  
        headerRow.createCell(1).setCellValue("Y值");  
          
        // 创建数据行  
        if (!series.isEmpty()) {  
            Map<String, Object> seriesItem = series.get(0);  
            List<List<Object>> data = (List<List<Object>>) seriesItem.get("data");  
              
            for (int i = 0; i < data.size(); i++) {  
                List<Object> point = data.get(i);  
                org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(i + 1);  
                dataRow.createCell(0).setCellValue(point.get(0) != null ? point.get(0).toString() : "");  
                dataRow.createCell(1).setCellValue(point.get(1) != null ? point.get(1).toString() : "");  
            }  
        }  
    }  
      
    /**  
     * 导出到CSV  
     *   
     * @param chartData 图表数据  
     * @param outputStream 输出流  
     * @throws IOException IO异常  
     */  
    private void exportToCsv(Map<String, Object> chartData, ByteArrayOutputStream outputStream) throws IOException {  
        // 获取图表信息  
        ChartMetadata chartMetadata = (ChartMetadata) chartData.get("chartInfo");  
          
        // 创建CSV打印器  
        CSVPrinter csvPrinter = new CSVPrinter(  
                new java.io.OutputStreamWriter(outputStream),  
                CSVFormat.DEFAULT.withHeader("Chart: " + chartMetadata.getChartName())  
        );  
          
        // 根据图表类型处理数据  
        switch (chartMetadata.getChartType()) {  
            case "bar":  
            case "line":  
                exportBarOrLineChartToCsv(chartData, csvPrinter);  
                break;  
            case "pie":  
                exportPieChartToCsv(chartData, csvPrinter);  
                break;  
            case "radar":  
                exportRadarChartToCsv(chartData, csvPrinter);  
                break;  
            case "scatter":  
                exportScatterChartToCsv(chartData, csvPrinter);  
                break;  
            default:  
                // 默认导出原始数据  
                if (chartData.containsKey("data")) {  
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) chartData.get("data");  
                    if (!dataList.isEmpty()) {  
                        // 创建标题行  
                        csvPrinter.printRecord(dataList.get(0).keySet());  
                          
                        // 创建数据行  
                        for (Map<String, Object> row : dataList) {  
                            csvPrinter.printRecord(row.values());  
                        }  
                    }  
                }  
        }  
          
        csvPrinter.flush();  
        csvPrinter.close();  
    }  
      
    /**  
     * 导出柱状图或折线图到CSV  
     *   
     * @param chartData 图表数据  
     * @param csvPrinter CSV打印器  
     * @throws IOException IO异常  
     */  
    private void exportBarOrLineChartToCsv(Map<String, Object> chartData, CSVPrinter csvPrinter) throws IOException {  
        List<String> categories = (List<String>) chartData.get("categories");  
        List<Map<String, Object>> series = (List<Map<String, Object>>) chartData.get("series");  
          
        // 创建标题行  
        List<String> header = new ArrayList<>();  
        header.add("类别");  
        for (Map<String, Object> seriesItem : series) {  
            header.add(seriesItem.get("name").toString());  
        }  
        csvPrinter.printRecord(header);  
          
        // 创建数据行  
        for (int i = 0; i < categories.size(); i++) {  
            List<Object> row = new ArrayList<>();  
            row.add(categories.get(i));  
              
            for (Map<String, Object> seriesItem : series) {  
                List<Object> data = (List<Object>) seriesItem.get("data");  
                row.add(data.get(i));  
            }  
              
            csvPrinter.printRecord(row);  
        }  
    }  
      
    /**  
     * 导出饼图到CSV  
     *   
     * @param chartData 图表数据  
     * @param csvPrinter CSV打印器  
     * @throws IOException IO异常  
     */  
    private void exportPieChartToCsv(Map<String, Object> chartData, CSVPrinter csvPrinter) throws IOException {  
        List<Map<String, Object>> data = (List<Map<String, Object>>) chartData.get("data");  
          
        // 创建标题行  
        csvPrinter.printRecord("名称", "值");  
          
        // 创建数据行  
        for (Map<String, Object> item : data) {  
            csvPrinter.printRecord(item.get("name"), item.get("value"));  
        }  
    }  
      
    /**  
     * 导出雷达图到CSV  
     *   
     * @param chartData 图表数据  
     * @param csvPrinter CSV打印器  
     * @throws IOException IO异常  
     */  
    private void exportRadarChartToCsv(Map<String, Object> chartData, CSVPrinter csvPrinter) throws IOException {  
        List<Map<String, Object>> indicator = (List<Map<String, Object>>) chartData.get("indicator");  
        List<Map<String, Object>> series = (List<Map<String, Object>>) chartData.get("series");  
          
        // 创建标题行  
        List<String> header = new ArrayList<>();  
        header.add("指标");  
        header.add("最大值");  
          
        for (Map<String, Object> seriesItem : series) {  
            header.add(seriesItem.get("name").toString());  
        }  
          
        csvPrinter.printRecord(header);  
          
        // 创建数据行  
        for (int i = 0; i < indicator.size(); i++) {  
            Map<String, Object> item = indicator.get(i);  
            List<Object> row = new ArrayList<>();  
            row.add(item.get("name"));  
            row.add(item.get("max"));  
              
            for (Map<String, Object> seriesItem : series) {  
                List<Map<String, Object>> seriesData = (List<Map<String, Object>>) seriesItem.get("data");  
                Map<String, Object> dataItem = seriesData.get(0);  
                List<Object> values = (List<Object>) dataItem.get("value");  
                row.add(values.get(i));  
            }  
              
            csvPrinter.printRecord(row);  
        }  
    }  
      
    /**  
     * 导出散点图到CSV  
     *   
     * @param chartData 图表数据  
     * @param csvPrinter CSV打印器  
     * @throws IOException IO异常  
     */  
    private void exportScatterChartToCsv(Map<String, Object> chartData, CSVPrinter csvPrinter) throws IOException {  
        List<Map<String, Object>> series = (List<Map<String, Object>>) chartData.get("series");  
          
        // 创建标题行  
        csvPrinter.printRecord("X值", "Y值");  
          
        // 创建数据行  
        if (!series.isEmpty()) {  
            Map<String, Object> seriesItem = series.get(0);  
            List<List<Object>> data = (List<List<Object>>) seriesItem.get("data");  
              
            for (List<Object> point : data) {  
                csvPrinter.printRecord(point.get(0), point.get(1));  
            }  
        }  
    }  
      
    /**  
     * 导出到PDF  
     *   
     * @param chartData 图表数据  
     * @param outputStream 输出流  
     * @throws IOException IO异常  
     */  
    private void exportToPdf(Map<String, Object> chartData, ByteArrayOutputStream outputStream) throws IOException {  
        // 此处需要引入PDF生成库，如iText或Apache PDFBox  
        // 由于依赖较多，此处仅提供示例代码框架  
          
        try {  
            // 获取图表信息  
            ChartMetadata chartMetadata = (ChartMetadata) chartData.get("chartInfo");  
              
            // 创建临时Excel文件  
            ByteArrayOutputStream excelStream = new ByteArrayOutputStream();  
            exportToExcel(chartData, excelStream);  
              
            // 将Excel转换为PDF  
            // 此处需要实现Excel到PDF的转换逻辑  
            // 可以使用第三方库如Apache POI + iText或Apache PDFBox  
              
            // 示例：使用iText将数据直接写入PDF  
            /*  
            Document document = new Document();  
            PdfWriter.getInstance(document, outputStream);  
            document.open();  
              
            // 添加标题  
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);  
            Paragraph title = new Paragraph(chartMetadata.getChartName(), titleFont);  
            title.setAlignment(Element.ALIGN_CENTER);  
            document.add(title);  
              
            // 添加表格  
            PdfPTable table = new PdfPTable(2);  
            table.setWidthPercentage(100);  
              
            // 根据图表类型添加数据  
            switch (chartMetadata.getChartType()) {  
                case "pie":  
                    List<Map<String, Object>> data = (List<Map<String, Object>>) chartData.get("data");  
                    table.addCell("名称");  
                    table.addCell("值");  
                      
                    for (Map<String, Object> item : data) {  
                        table.addCell(item.get("name").toString());  
                        table.addCell(item.get("value").toString());  
                    }  
                    break;  
                // 其他图表类型的处理...  
            }  
              
            document.add(table);  
            document.close();  
            */  
              
            // 将Excel数据写入输出流  
            outputStream.write(excelStream.toByteArray());  
              
        } catch (Exception e) {  
            log.error("导出PDF失败", e);  
            throw new IOException("导出PDF失败: " + e.getMessage());  
        }  
    }  
}
