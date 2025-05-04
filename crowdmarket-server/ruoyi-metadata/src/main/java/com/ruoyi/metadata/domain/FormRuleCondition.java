package com.ruoyi.metadata.domain;


import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

/**
 * 表单规则条件对象 form_rule_condition
 *
 * @author ruoyi
 */
@Data
public class FormRuleCondition {
    private static final long serialVersionUID = 1L;

    /** 条件ID */
    private String conditionId;

    /** 规则ID */
    @Excel(name = "规则ID")
    private String ruleId;

    /** 字段ID */
    @Excel(name = "字段ID")
    private String fieldId;

    /** 操作符(eq,ne,gt,lt,ge,le,contains,startswith,endswith) */
    @Excel(name = "操作符", readConverterExp = "e=q,n=e,g=t,l=t,g=e,l=e,c=ontains,s=tartswith,e=ndswith")
    private String operator;

    /** 比较值 */
    @Excel(name = "比较值")
    private String value;

    /** 逻辑类型(AND,OR) */
    @Excel(name = "逻辑类型", readConverterExp = "A=ND,O=R")
    private String logicType;

    /** 条件组ID */
    @Excel(name = "条件组ID")
    private String groupId;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;
}