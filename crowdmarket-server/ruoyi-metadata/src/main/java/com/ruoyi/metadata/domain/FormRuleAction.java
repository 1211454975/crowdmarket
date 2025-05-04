package com.ruoyi.metadata.domain;


import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

/**
 * 表单规则动作对象 form_rule_action
 *
 * @author ruoyi
 */
@Data
public class FormRuleAction {
    private static final long serialVersionUID = 1L;

    /** 动作ID */
    private String actionId;

    /** 规则ID */
    @Excel(name = "规则ID")
    private String ruleId;

    /** 动作类型(setValue,setVisible,setRequired,setReadOnly,calculate,message,api) */
    @Excel(name = "动作类型", readConverterExp = "s=etValue,s=etVisible,s=etRequired,s=etReadOnly,c=alculate,m=essage,a=pi")
    private String actionType;

    /** 目标字段ID */
    @Excel(name = "目标字段ID")
    private String targetFieldId;

    /** 动作值 */
    @Excel(name = "动作值")
    private String actionValue;

    /** 动作脚本 */
    @Excel(name = "动作脚本")
    private String actionScript;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;
}