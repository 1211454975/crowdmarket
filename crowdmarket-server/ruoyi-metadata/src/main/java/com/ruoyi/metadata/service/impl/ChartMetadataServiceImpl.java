package com.ruoyi.metadata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ruoyi.metadata.service.ChartDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
//import com.ruoyi.formdata.service.FormDataService;
import com.ruoyi.metadata.domain.ChartConfig;
import com.ruoyi.metadata.domain.ChartMetadata;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.mapper.ChartConfigMapper;
import com.ruoyi.metadata.mapper.ChartMetadataMapper;
import com.ruoyi.metadata.service.ChartMetadataService;
import com.ruoyi.metadata.service.FormFieldMetadataService;

/**
 * 图表元数据Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class ChartMetadataServiceImpl implements ChartMetadataService {
    @Autowired
    private ChartMetadataMapper chartMetadataMapper;

    @Autowired
    private ChartConfigMapper chartConfigMapper;

    @Autowired
    private FormFieldMetadataService formFieldMetadataService;

//    @Autowired
//    private FormDataService formDataService;

    @Autowired
    private ChartDataProvider chartDataProvider;
    /**
     * 查询图表元数据
     *
     * @param chartId 图表元数据主键
     * @return 图表元数据
     */
    @Override
    public ChartMetadata selectChartMetadataByChartId(String chartId) {
        return chartMetadataMapper.selectChartMetadataByChartId(chartId);
    }

    /**
     * 查询图表元数据列表
     *
     * @param chartMetadata 图表元数据
     * @return 图表元数据
     */
    @Override
    public List<ChartMetadata> selectChartMetadataList(ChartMetadata chartMetadata) {
        return chartMetadataMapper.selectChartMetadataList(chartMetadata);
    }

    /**
     * 根据元数据ID查询图表列表
     *
     * @param metadataId 元数据ID
     * @return 图表元数据集合
     */
    @Override
    public List<ChartMetadata> selectChartMetadataByMetadataId(String metadataId) {
        return chartMetadataMapper.selectChartMetadataByMetadataId(metadataId);
    }

    /**
     * 新增图表元数据
     *
     * @param chartMetadata 图表元数据
     * @return 结果
     */
    @Override
    @Transactional
    public int insertChartMetadata(ChartMetadata chartMetadata) {
        // 生成UUID作为图表ID
        chartMetadata.setChartId(UUID.randomUUID().toString().replace("-", ""));
        // 设置创建时间
        chartMetadata.setCreateTime(DateUtils.getNowDate());
        // 设置创建者
        chartMetadata.setCreateBy(SecurityUtils.getUsername());
        // 设置租户ID
        chartMetadata.setTenantId(SecurityUtils.getCurrComId());
        // 默认状态为草稿
        chartMetadata.setStatus(0);

        return chartMetadataMapper.insertChartMetadata(chartMetadata);
    }

    /**
     * 修改图表元数据
     *
     * @param chartMetadata 图表元数据
     * @return 结果
     */
    @Override
    @Transactional
    public int updateChartMetadata(ChartMetadata chartMetadata) {
        // 设置更新时间
        chartMetadata.setUpdateTime(DateUtils.getNowDate());
        // 设置更新者
        chartMetadata.setUpdateBy(SecurityUtils.getUsername());

        return chartMetadataMapper.updateChartMetadata(chartMetadata);
    }

    /**
     * 批量删除图表元数据
     *
     * @param chartIds 需要删除的图表元数据主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteChartMetadataByChartIds(String[] chartIds) {
        // 删除图表配置
        for (String chartId : chartIds) {
            chartConfigMapper.deleteChartConfigByChartId(chartId);
        }

        return chartMetadataMapper.deleteChartMetadataByChartIds(chartIds);
    }

    /**
     * 删除图表元数据信息
     *
     * @param chartId 图表元数据主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteChartMetadataByChartId(String chartId) {
        // 删除图表配置
        chartConfigMapper.deleteChartConfigByChartId(chartId);

        return chartMetadataMapper.deleteChartMetadataByChartId(chartId);
    }

    /**
     * 发布图表
     *
     * @param chartId 图表ID
     * @return 结果
     */
    @Override
    @Transactional
    public int publishChart(String chartId) {
        ChartMetadata chartMetadata = new ChartMetadata();
        chartMetadata.setChartId(chartId);
        chartMetadata.setStatus(1); // 设置状态为发布
        chartMetadata.setUpdateTime(DateUtils.getNowDate());
        chartMetadata.setUpdateBy(SecurityUtils.getUsername());

        return chartMetadataMapper.updateChartMetadata(chartMetadata);
    }

    /**
     * 停用图表
     *
     * @param chartId 图表ID
     * @return 结果
     */
    @Override
    @Transactional
    public int disableChart(String chartId) {
        ChartMetadata chartMetadata = new ChartMetadata();
        chartMetadata.setChartId(chartId);
        chartMetadata.setStatus(2); // 设置状态为停用
        chartMetadata.setUpdateTime(DateUtils.getNowDate());
        chartMetadata.setUpdateBy(SecurityUtils.getUsername());

        return chartMetadataMapper.updateChartMetadata(chartMetadata);
    }

    /**
     * 获取图表详情，包含配置
     *
     * @param chartId 图表ID
     * @return 图表详情
     */
    @Override
    public Map<String, Object> getChartDetail(String chartId) {
        Map<String, Object> result = new HashMap<>();

        // 获取图表元数据
        ChartMetadata chartMetadata = chartMetadataMapper.selectChartMetadataByChartId(chartId);
        if (chartMetadata == null) {
            return result;
        }

        // 获取图表配置
        ChartConfig chartConfig = chartConfigMapper.selectChartConfigByChartId(chartId);

        // 获取表单字段列表
        List<FormFieldMetadata> fieldList = formFieldMetadataService.selectFormFieldMetadataByMetadataId(chartMetadata.getMetadataId());

        result.put("chart", chartMetadata);
        result.put("config", chartConfig);
        result.put("fields", fieldList);

        return result;
    }

    /**
     * 保存图表配置
     *
     * @param chartId     图表ID
     * @param chartConfig 图表配置
     * @return 结果
     */
    @Override
    @Transactional
    public int saveChartConfig(String chartId, ChartConfig chartConfig) {
        // 设置图表ID
        chartConfig.setChartId(chartId);
        // 设置租户ID
        chartConfig.setTenantId(SecurityUtils.getCurrComId());

        // 检查是否已存在配置
        ChartConfig existingConfig = chartConfigMapper.selectChartConfigByChartId(chartId);
        if (existingConfig != null) {
            // 更新配置ID
            chartConfig.setConfigId(existingConfig.getConfigId());
            return chartConfigMapper.updateChartConfig(chartConfig);
        } else {
            // 生成UUID作为配置ID
            chartConfig.setConfigId(UUID.randomUUID().toString().replace("-", ""));
            return chartConfigMapper.insertChartConfig(chartConfig);
        }
    }

    /**
     * 获取图表数据
     *
     * @param chartId 图表ID
     * @param params  查询参数
     * @return 图表数据
     */
    @Override
    public Map<String, Object> getChartData(String chartId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        // 获取图表元数据
        ChartMetadata chartMetadata = chartMetadataMapper.selectChartMetadataByChartId(chartId);
        if (chartMetadata == null) {
            return result;
        }

        // 获取图表配置
        ChartConfig chartConfig = chartConfigMapper.selectChartConfigByChartId(chartId);
        if (chartConfig == null) {
            return result;
        }

        // 获取表单字段列表
        List<FormFieldMetadata> fieldList = formFieldMetadataService.selectFormFieldMetadataByMetadataId(chartMetadata.getMetadataId());
        Map<String, FormFieldMetadata> fieldMap = new HashMap<>();
        for (FormFieldMetadata field : fieldList) {
            fieldMap.put(field.getFieldId(), field);
        }

        // 构建查询参数
        Map<String, Object> queryParams = new HashMap<>();

        // 添加过滤条件
        if (StringUtils.isNotEmpty(chartConfig.getFilterCondition())) {
            try {
                JSONArray filterConditions = JSON.parseArray(chartConfig.getFilterCondition());
                for (int i = 0; i < filterConditions.size(); i++) {
                    JSONObject condition = filterConditions.getJSONObject(i);
                    String fieldId = condition.getString("fieldId");
                    String operator = condition.getString("operator");
                    String value = condition.getString("value");

                    FormFieldMetadata field = fieldMap.get(fieldId);
                    if (field != null) {
                        String fieldName = field.getFieldName();

                        if ("eq".equals(operator)) {
                            queryParams.put(fieldName, value);
                        } else if ("like".equals(operator)) {
                            queryParams.put(fieldName + "_like", value);
                        } else if ("gt".equals(operator)) {
                            queryParams.put(fieldName + "_gt", value);
                        } else if ("lt".equals(operator)) {
                            queryParams.put(fieldName + "_lt", value);
                        } else if ("between".equals(operator)) {
                            String[] values = value.split(",");
                            if (values.length >= 2) {
                                queryParams.put("begin_" + fieldName, values[0]);
                                queryParams.put("end_" + fieldName, values[1]);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        // 合并传入的查询参数
        if (params != null) {
            queryParams.putAll(params);
        }

        // 设置数据限制
        if (chartConfig.getDataLimit() != null && chartConfig.getDataLimit() > 0) {
            queryParams.put("limit", chartConfig.getDataLimit());
        }

        // 查询数据
//        List<Map<String, Object>> dataList = formDataService.selectFormDataList(chartMetadata.getMetadataId(), queryParams);
        List<Map<String, Object>> dataList = chartDataProvider.selectFormDataList(chartMetadata.getMetadataId(), queryParams);


        // 处理维度和度量字段
        List<String> dimensions = new ArrayList<>();
        List<String> measures = new ArrayList<>();

        if (StringUtils.isNotEmpty(chartConfig.getDimensionFields())) {
            try {
                JSONArray dimensionFields = JSON.parseArray(chartConfig.getDimensionFields());
                for (int i = 0; i < dimensionFields.size(); i++) {
                    JSONObject dimension = dimensionFields.getJSONObject(i);
                    String fieldId = dimension.getString("fieldId");
                    FormFieldMetadata field = fieldMap.get(fieldId);
                    if (field != null) {
                        dimensions.add(field.getFieldName());
                    }
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        if (StringUtils.isNotEmpty(chartConfig.getMeasureFields())) {
            try {
                JSONArray measureFields = JSON.parseArray(chartConfig.getMeasureFields());
                for (int i = 0; i < measureFields.size(); i++) {
                    JSONObject measure = measureFields.getJSONObject(i);
                    String fieldId = measure.getString("fieldId");
                    FormFieldMetadata field = fieldMap.get(fieldId);
                    if (field != null) {
                        measures.add(field.getFieldName());
                    }
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        // 处理排序
        if (StringUtils.isNotEmpty(chartConfig.getSortConfig())) {
            try {
                JSONArray sortConfigs = JSON.parseArray(chartConfig.getSortConfig());
                List<Map<String, Object>> sortList = new ArrayList<>();

                for (int i = 0; i < sortConfigs.size(); i++) {
                    JSONObject sortConfig = sortConfigs.getJSONObject(i);
                    String fieldId = sortConfig.getString("fieldId");
                    String direction = sortConfig.getString("direction");

                    FormFieldMetadata field = fieldMap.get(fieldId);
                    if (field != null) {
                        Map<String, Object> sort = new HashMap<>();
                        sort.put("field", field.getFieldName());
                        sort.put("direction", direction);
                        sortList.add(sort);
                    }
                }

                result.put("sort", sortList);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        // 设置结果
        result.put("data", dataList);
        result.put("dimensions", dimensions);
        result.put("measures", measures);
        result.put("chartType", chartMetadata.getChartType());

        // 添加样式配置
        if (StringUtils.isNotEmpty(chartConfig.getStyleConfig())) {
            try {
                JSONObject styleConfig = JSON.parseObject(chartConfig.getStyleConfig());
                result.put("style", styleConfig);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        return result;
    }
}