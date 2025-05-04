package com.ruoyi.metadata.domain;


import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单规则对象 form_rule
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FormRule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private String ruleId;

    /** 元数据ID */
    @Excel(name = "元数据ID")
    private String metadataId;

    /** 规则名称 */
    @Excel(name = "规则名称")
    private String ruleName;

    /** 规则类型(validation,calculation,visibility,dependency,workflow) */
    @Excel(name = "规则类型", readConverterExp = "v=alidation,c=alculation,v=isibility,d=ependency,w=orkflow")
    private String ruleType;

    /** 规则描述 */
    @Excel(name = "规则描述")
    private String ruleDesc;

    /** 是否启用(0否1是) */
    @Excel(name = "是否启用", readConverterExp = "0=否,1=是")
    private Integer isActive;

    /** 执行顺序 */
    @Excel(name = "执行顺序")
    private Integer executionOrder;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;
}