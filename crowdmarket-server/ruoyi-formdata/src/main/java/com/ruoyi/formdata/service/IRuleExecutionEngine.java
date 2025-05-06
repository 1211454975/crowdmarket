package com.ruoyi.formdata.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.metadata.domain.FormRule;

/**
 * 规则执行引擎 服务接口
 *
 * @author ruoyi
 */
public interface IRuleExecutionEngine
{
    /**
     * 执行表单加载时的规则
     *
     * @param metadataId 元数据ID
     * @param formData 表单数据
     * @return 规则执行结果
     */
    public Map<String, Object> executeLoadRules(String metadataId, Map<String, Object> formData);

    /**
     * 执行字段值变化时的规则
     *
     * @param metadataId 元数据ID
     * @param fieldName 字段名称
     * @param oldValue 旧值
     * @param newValue 新值
     * @param formData 表单数据
     * @return 规则执行结果
     */
    public Map<String, Object> executeFieldChangeRules(String metadataId, String fieldName,
                                                       Object oldValue, Object newValue, Map<String, Object> formData);

    /**
     * 执行表单提交前的规则
     *
     * @param metadataId 元数据ID
     * @param formData 表单数据
     * @return 规则执行结果，包含验证结果
     */
    public Map<String, Object> executeSubmitRules(String metadataId, Map<String, Object> formData);

    /**
     * 执行表单提交后的规则
     *
     * @param metadataId 元数据ID
     * @param formData 表单数据
     * @return 规则执行结果
     */
    public Map<String, Object> executeAfterSubmitRules(String metadataId, Map<String, Object> formData);

    /**
     * 执行指定规则
     *
     * @param rule 规则
     * @param formData 表单数据
     * @return 规则执行结果
     */
    public Map<String, Object> executeRule(FormRule rule, Map<String, Object> formData);

    /**
     * 执行指定规则列表
     *
     * @param rules 规则列表
     * @param formData 表单数据
     * @return 规则执行结果
     */
    public Map<String, Object> executeRules(List<FormRule> rules, Map<String, Object> formData);

    /**
     * 验证规则条件是否满足
     *
     * @param ruleId 规则ID
     * @param formData 表单数据
     * @return 是否满足
     */
    public boolean evaluateRuleConditions(String ruleId, Map<String, Object> formData);

    /**
     * 执行规则动作
     *
     * @param ruleId 规则ID
     * @param formData 表单数据
     * @return 执行结果
     */
    public Map<String, Object> executeRuleActions(String ruleId, Map<String, Object> formData);
}