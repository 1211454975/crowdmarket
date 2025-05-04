package com.ruoyi.metadata.mapper;

import java.util.List;
import com.ruoyi.metadata.domain.ChartConfig;

/**
 * 图表配置Mapper接口
 *
 * @author ruoyi
 */
public interface ChartConfigMapper {
    /**
     * 查询图表配置
     *
     * @param configId 图表配置主键
     * @return 图表配置
     */
    public ChartConfig selectChartConfigByConfigId(String configId);

    /**
     * 根据图表ID查询配置
     *
     * @param chartId 图表ID
     * @return 图表配置
     */
    public ChartConfig selectChartConfigByChartId(String chartId);

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
     * 删除图表配置
     *
     * @param configId 图表配置主键
     * @return 结果
     */
    public int deleteChartConfigByConfigId(String configId);

    /**
     * 根据图表ID删除配置
     *
     * @param chartId 图表ID
     * @return 结果
     */
    public int deleteChartConfigByChartId(String chartId);

    /**
     * 批量删除图表配置
     *
     * @param configIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChartConfigByConfigIds(String[] configIds);
}