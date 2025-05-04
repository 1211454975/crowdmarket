package com.ruoyi.metadata.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.metadata.domain.FormRule;
import com.ruoyi.metadata.domain.FormRuleCondition;
import com.ruoyi.metadata.domain.FormRuleAction;

/**
 * 表单规则Service接口
 *
 * @author ruoyi
 */
public interface FormRuleService {
    /**
     * 查询表单规则
     *
     * @param ruleId 表单规则主键
     * @return 表单规则
     */
    public FormRule selectFormRuleByRuleId(String ruleId);

    /**
     * 查询表单规则列表
     *
     * @param formRule 表单规则
     * @return 表单规则集合
     */
    public List<FormRule> selectFormRuleList(FormRule formRule);

    /**
     * 根据元数据ID查询规则列表
     *
     * @param metadataId 元数据ID
     * @return 表单规则集合
     */
    public List<FormRule> selectFormRuleByMetadataId(String metadataId);

    /**
     * 新增表单规则
     *
     * @param formRule 表单规则
     * @return 结果
     */
    public int insertFormRule(FormRule formRule);

    /**
     * 修改表单规则
     *
     * @param formRule 表单规则
     * @return 结果
     */
    public int updateFormRule(FormRule formRule);

    /**
     * 批量删除表单规则
     *
     * @param ruleIds 需要删除的表单规则主键集合
     * @return 结果
     */
    public int deleteFormRuleByRuleIds(String[] ruleIds);

    /**
     * 删除表单规则信息
     *
     * @param ruleId 表单规则主键
     * @return 结果
     */
    public int deleteFormRuleByRuleId(String ruleId);

    /**
     * 获取规则详情，包含条件和动作
     *
     * @param ruleId 规则ID
     * @return 规则详情
     */
    public Map<String, Object> getFormRuleDetail(String ruleId);

    /**
     * 保存规则详情，包含条件和动作
     *
     * @param ruleId 规则ID
     * @param conditions 条件列表
     * @param actions 动作列表
     * @return 结果
     */
    public int saveFormRuleDetail(String ruleId, List<FormRuleCondition> conditions, List<FormRuleAction> actions);

    /**
     * 启用规则
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    public int enableFormRule(String ruleId);

    /**
     * 禁用规则
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    public int disableFormRule(String ruleId);

    /**
     * 获取规则条件列表
     *
     * @param ruleId 规则ID
     * @return 条件列表
     */
    public List<FormRuleCondition> selectFormRuleConditionByRuleId(String ruleId);

    /**
     * 获取规则动作列表
     *
     * @param ruleId 规则ID
     * @return 动作列表
     */
    public List<FormRuleAction> selectFormRuleActionByRuleId(String ruleId);

    /**
     * 执行规则
     *
     * @param metadataId 元数据ID
     * @param formData 表单数据
     * @param triggerPoint 触发点(load,change,submit,after)
     * @return 执行结果
     */
    public Map<String, Object> executeRules(String metadataId, Map<String, Object> formData, String triggerPoint);
}