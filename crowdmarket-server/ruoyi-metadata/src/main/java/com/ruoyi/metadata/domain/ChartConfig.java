package com.ruoyi.metadata.domain;


import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

/**
 * 图表配置对象 chart_config
 *
 * @author ruoyi
 */
@Data
public class ChartConfig {
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private String configId;

    /** 图表ID */
    @Excel(name = "图表ID")
    private String chartId;

    /** 维度字段JSON */
    @Excel(name = "维度字段JSON")
    private String dimensionFields;

    /** 度量字段JSON */
    @Excel(name = "度量字段JSON")
    private String measureFields;

    /** 过滤条件JSON */
    @Excel(name = "过滤条件JSON")
    private String filterCondition;

    /** 排序配置JSON */
    @Excel(name = "排序配置JSON")
    private String sortConfig;

    /** 样式配置JSON */
    @Excel(name = "样式配置JSON")
    private String styleConfig;

    /** 数据限制条数 */
    @Excel(name = "数据限制条数")
    private Integer dataLimit;

    /** 刷新间隔(秒) */
    @Excel(name = "刷新间隔(秒)")
    private Integer refreshInterval;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;
}