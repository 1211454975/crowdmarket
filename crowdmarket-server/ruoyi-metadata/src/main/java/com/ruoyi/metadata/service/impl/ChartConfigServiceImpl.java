package com.ruoyi.metadata.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.metadata.mapper.ChartConfigMapper;
import com.ruoyi.metadata.domain.ChartConfig;
import com.ruoyi.metadata.service.IChartConfigService;
import com.ruoyi.common.utils.DateUtils;

/**
 * 图表配置 服务实现类
 *
 * @author ruoyi
 */
@Service
public class ChartConfigServiceImpl implements IChartConfigService
{
    @Autowired
    private ChartConfigMapper chartConfigMapper;

    /**
     * 查询图表配置
     *
     * @param configId 配置ID
     * @return 图表配置
     */
    @Override
    public ChartConfig selectChartConfigById(String configId)
    {
        return chartConfigMapper.selectChartConfigByConfigId(configId);
    }

    /**
     * 查询图表配置列表
     *
     * @param chartConfig 图表配置
     * @return 图表配置
     */
    @Override
    public List<ChartConfig> selectChartConfigList(ChartConfig chartConfig)
    {
        return chartConfigMapper.selectChartConfigList(chartConfig);
    }

    /**
     * 新增图表配置
     *
     * @param chartConfig 图表配置
     * @return 结果
     */
    @Override
    public int insertChartConfig(ChartConfig chartConfig)
    {
        return chartConfigMapper.insertChartConfig(chartConfig);
    }

    /**
     * 修改图表配置
     *
     * @param chartConfig 图表配置
     * @return 结果
     */
    @Override
    public int updateChartConfig(ChartConfig chartConfig)
    {
        return chartConfigMapper.updateChartConfig(chartConfig);
    }

    /**
     * 批量删除图表配置
     *
     * @param configIds 需要删除的配置ID
     * @return 结果
     */
    @Override
    public int deleteChartConfigByIds(String[] configIds)
    {
        return chartConfigMapper.deleteChartConfigByConfigIds(configIds);
    }

    /**
     * 删除图表配置信息
     *
     * @param configId 配置ID
     * @return 结果
     */
    @Override
    public int deleteChartConfigById(String configId)
    {
        return chartConfigMapper.deleteChartConfigByConfigId(configId);
    }

    /**
     * 根据图表ID删除图表配置
     *
     * @param chartId 图表ID
     * @return 结果
     */
    @Override
    public int deleteChartConfigByChartId(String chartId)
    {
        return chartConfigMapper.deleteChartConfigByChartId(chartId);
    }
}