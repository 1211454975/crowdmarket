package com.ruoyi.metadata.service;

import java.util.List;
import com.ruoyi.metadata.domain.FormRuleAction;

/**
 * 表单规则动作 服务接口
 *
 * @author ruoyi
 */
public interface IFormRuleActionService
{
    /**
     * 查询表单规则动作
     *
     * @param actionId 动作ID
     * @return 表单规则动作
     */
    public FormRuleAction selectFormRuleActionById(String actionId);

    /**
     * 查询表单规则动作列表
     *
     * @param formRuleAction 表单规则动作
     * @return 表单规则动作集合
     */
    public List<FormRuleAction> selectFormRuleActionList(FormRuleAction formRuleAction);

    /**
     * 根据规则ID查询规则动作列表
     *
     * @param ruleId 规则ID
     * @return 表单规则动作集合
     */
    public List<FormRuleAction> selectFormRuleActionListByRuleId(String ruleId);

    /**
     * 新增表单规则动作
     *
     * @param formRuleAction 表单规则动作
     * @return 结果
     */
    public int insertFormRuleAction(FormRuleAction formRuleAction);

    /**
     * 修改表单规则动作
     *
     * @param formRuleAction 表单规则动作
     * @return 结果
     */
    public int updateFormRuleAction(FormRuleAction formRuleAction);

    /**
     * 批量删除表单规则动作
     *
     * @param actionIds 需要删除的动作ID
     * @return 结果
     */
    public int deleteFormRuleActionByIds(String[] actionIds);

    /**
     * 删除表单规则动作信息
     *
     * @param actionId 动作ID
     * @return 结果
     */
    public int deleteFormRuleActionById(String actionId);

    /**
     * 根据规则ID删除规则动作
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    public int deleteFormRuleActionByRuleId(String ruleId);
}