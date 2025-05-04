package com.ruoyi.metadata.domain;

import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单视图配置对象 form_view_config  
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FormViewConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 视图ID */
    private String viewId;

    /** 元数据ID */
    @Excel(name = "元数据ID")
    private String metadataId;

    /** 视图名称 */
    @Excel(name = "视图名称")
    private String viewName;

    /** 视图类型(form表单,list列表,detail详情) */
    @Excel(name = "视图类型", readConverterExp = "f=orm表单,l=ist列表,d=etail详情")
    private String viewType;

    /** 视图配置JSON */
    @Excel(name = "视图配置JSON")
    private String viewConfig;

    /** 是否默认视图(0否1是) */
    @Excel(name = "是否默认视图", readConverterExp = "0=否,1=是")
    private Integer isDefault;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;
}