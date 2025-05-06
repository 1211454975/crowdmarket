package com.ruoyi.metadata.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.metadata.domain.ChartMetadata;
import com.ruoyi.metadata.domain.ChartConfig;

/**
 * 图表元数据Service接口
 *
 * @author ruoyi
 */
public interface IChartMetadataService {
    /**
     * 查询图表元数据
     *
     * @param chartId 图表元数据主键
     * @return 图表元数据
     */
    public ChartMetadata selectChartMetadataByChartId(String chartId);

    /**
     * 查询图表元数据列表
     *
     * @param chartMetadata 图表元数据
     * @return 图表元数据集合
     */
    public List<ChartMetadata> selectChartMetadataList(ChartMetadata chartMetadata);

    /**
     * 根据元数据ID查询图表列表
     *
     * @param metadataId 元数据ID
     * @return 图表元数据集合
     */
    public List<ChartMetadata> selectChartMetadataByMetadataId(String metadataId);

    /**
     * 新增图表元数据
     *
     * @param chartMetadata 图表元数据
     * @return 结果
     */
    public int insertChartMetadata(ChartMetadata chartMetadata);

    /**
     * 修改图表元数据
     *
     * @param chartMetadata 图表元数据
     * @return 结果
     */
    public int updateChartMetadata(ChartMetadata chartMetadata);

    /**
     * 批量删除图表元数据
     *
     * @param chartIds 需要删除的图表元数据主键集合
     * @return 结果
     */
    public int deleteChartMetadataByChartIds(String[] chartIds);

    /**
     * 删除图表元数据信息
     *
     * @param chartId 图表元数据主键
     * @return 结果
     */
    public int deleteChartMetadataByChartId(String chartId);

    /**
     * 发布图表
     *
     * @param chartId 图表ID
     * @return 结果
     */
    public int publishChart(String chartId);

    /**
     * 停用图表
     *
     * @param chartId 图表ID
     * @return 结果
     */
    public int disableChart(String chartId);

    /**
     * 获取图表详情，包含配置
     *
     * @param chartId 图表ID
     * @return 图表详情
     */
    public Map<String, Object> getChartDetail(String chartId);

    /**
     * 保存图表配置
     *
     * @param chartId 图表ID
     * @param chartConfig 图表配置
     * @return 结果
     */
    public int saveChartConfig(String chartId, ChartConfig chartConfig);

    /**
     * 获取图表数据
     *
     * @param chartId 图表ID
     * @param params 查询参数
     * @return 图表数据
     */
    public Map<String, Object> getChartData(String chartId, Map<String, Object> params);
}