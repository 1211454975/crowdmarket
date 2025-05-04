package com.ruoyi.metadata.mapper;

import java.util.List;
import com.ruoyi.metadata.domain.FormRuleCondition;

/**
 * 表单规则条件Mapper接口
 *
 * @author ruoyi
 */
public interface FormRuleConditionMapper {
    /**
     * 查询表单规则条件
     *
     * @param conditionId 表单规则条件主键
     * @return 表单规则条件
     */
    public FormRuleCondition selectFormRuleConditionByConditionId(String conditionId);

    /**
     * 查询表单规则条件列表
     *
     * @param formRuleCondition 表单规则条件
     * @return 表单规则条件集合
     */
    public List<FormRuleCondition> selectFormRuleConditionList(FormRuleCondition formRuleCondition);

    /**
     * 根据规则ID查询条件列表
     *
     * @param ruleId 规则ID
     * @return 表单规则条件集合
     */
    public List<FormRuleCondition> selectFormRuleConditionByRuleId(String ruleId);

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
     * 删除表单规则条件
     *
     * @param conditionId 表单规则条件主键
     * @return 结果
     */
    public int deleteFormRuleConditionByConditionId(String conditionId);

    /**
     * 批量删除表单规则条件
     *
     * @param conditionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFormRuleConditionByConditionIds(String[] conditionIds);

    /**
     * 根据规则ID删除条件
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    public int deleteFormRuleConditionByRuleId(String ruleId);
}