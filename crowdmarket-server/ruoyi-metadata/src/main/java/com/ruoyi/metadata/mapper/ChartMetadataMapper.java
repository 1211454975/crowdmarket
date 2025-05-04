package com.ruoyi.metadata.mapper;

import java.util.List;
import com.ruoyi.metadata.domain.ChartMetadata;

/**
 * 图表元数据Mapper接口
 *
 * @author ruoyi
 */
public interface ChartMetadataMapper {
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
     * 删除图表元数据
     *
     * @param chartId 图表元数据主键
     * @return 结果
     */
    public int deleteChartMetadataByChartId(String chartId);

    /**
     * 批量删除图表元数据
     *
     * @param chartIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChartMetadataByChartIds(String[] chartIds);
}