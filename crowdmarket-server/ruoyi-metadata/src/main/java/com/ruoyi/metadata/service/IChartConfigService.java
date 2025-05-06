package com.ruoyi.metadata.service;

import java.util.List;
import com.ruoyi.metadata.domain.ChartConfig;

/**
 * 图表配置 服务接口
 *
 * @author ruoyi
 */
public interface IChartConfigService
{
    /**
     * 查询图表配置
     *
     * @param configId 配置ID
     * @return 图表配置
     */
    public ChartConfig selectChartConfigById(String configId);

    /**
     * 查询图表配置列表
     *
     * @param chartConfig 图表配置
     * @return 图表配置集合
     */
    public List<ChartConfig> selectChartConfigList(ChartConfig chartConfig);

    /**
     * 新增图表配置
     *
     * @param chartConfig 图表配置
     * @return 结果
     */
    public int insertChartConfig(ChartConfig chartConfig);

    /**
     * 修改图表配置
     *
     * @param chartConfig 图表配置
     * @return 结果
     */
    public int updateChartConfig(ChartConfig chartConfig);

    /**
     * 批量删除图表配置
     *
     * @param configIds 需要删除的配置ID
     * @return 结果
     */
    public int deleteChartConfigByIds(String[] configIds);

    /**
     * 删除图表配置信息
     *
     * @param configId 配置ID
     * @return 结果
     */
    public int deleteChartConfigById(String configId);

    /**
     * 根据图表ID删除图表配置
     *
     * @param chartId 图表ID
     * @return 结果
     */
    public int deleteChartConfigByChartId(String chartId);
}