package com.ruoyi.metadata.service;

import java.util.List;
import com.ruoyi.metadata.domain.FormRuleCondition;

/**
 * 表单规则条件 服务接口
 *
 * @author ruoyi
 */
public interface IFormRuleConditionService
{
    /**
     * 查询表单规则条件
     *
     * @param conditionId 条件ID
     * @return 表单规则条件
     */
    public FormRuleCondition selectFormRuleConditionById(String conditionId);

    /**
     * 查询表单规则条件列表
     *
     * @param formRuleCondition 表单规则条件
     * @return 表单规则条件集合
     */
    public List<FormRuleCondition> selectFormRuleConditionList(FormRuleCondition formRuleCondition);

    /**
     * 根据规则ID查询规则条件列表
     *
     * @param ruleId 规则ID
     * @return 表单规则条件集合
     */
    public List<FormRuleCondition> selectFormRuleConditionListByRuleId(String ruleId);

    /**
     * 新增表单规则条件
     *
     * @param formRuleCondition 表单规则条件
     * @return 结果
     */
    public int insertFormRuleCondition(FormRuleCondition formRuleCondition);

    /**
     * 修改表单规则条件
     *
     * @param formRuleCondition 表单规则条件
     * @return 结果
     */
    public int updateFormRuleCondition(FormRuleCondition formRuleCondition);

    /**
     * 批量删除表单规则条件
     *
     * @param conditionIds 需要删除的条件ID
     * @return 结果
     */
    public int deleteFormRuleConditionByIds(String[] conditionIds);

    /**
     * 删除表单规则条件信息
     *
     * @param conditionId 条件ID
     * @return 结果
     */
    public int deleteFormRuleConditionById(String conditionId);

    /**
     * 根据规则ID删除规则条件
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    public int deleteFormRuleConditionByRuleId(String ruleId);
}
