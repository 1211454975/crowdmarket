package com.ruoyi.metadata.domain;


import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单元数据对象 form_metadata
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FormMetadata extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 元数据ID */
    private String metadataId;

    /** 元数据名称 */
    @Excel(name = "元数据名称")
    private String metadataName;

    /** 元数据描述 */
    @Excel(name = "元数据描述")
    private String metadataDesc;

    /** 关联表名 */
    @Excel(name = "关联表名")
    private String tableName;

    /** 状态(0草稿,1发布,2停用) */
    @Excel(name = "状态", readConverterExp = "0=草稿,1=发布,2=停用")
    private Integer status;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;
}