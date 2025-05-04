package com.ruoyi.metadata.domain;


import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图表元数据对象 chart_metadata
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChartMetadata extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 图表ID */
    private String chartId;

    /** 图表名称 */
    @Excel(name = "图表名称")
    private String chartName;

    /** 图表描述 */
    @Excel(name = "图表描述")
    private String chartDesc;

    /** 图表类型(bar柱状图,line折线图,pie饼图,radar雷达图等) */
    @Excel(name = "图表类型", readConverterExp = "b=ar柱状图,l=ine折线图,p=ie饼图,r=adar雷达图等")
    private String chartType;

    /** 关联的表单元数据ID */
    @Excel(name = "关联的表单元数据ID")
    private String metadataId;

    /** 状态(0草稿,1发布,2停用) */
    @Excel(name = "状态", readConverterExp = "0=草稿,1=发布,2=停用")
    private Integer status;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;
}