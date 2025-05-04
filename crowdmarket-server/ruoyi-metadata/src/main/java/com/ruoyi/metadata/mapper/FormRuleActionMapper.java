package com.ruoyi.metadata.mapper;

import java.util.List;
import com.ruoyi.metadata.domain.FormRuleAction;

/**
 * 表单规则动作Mapper接口
 *
 * @author ruoyi
 */
public interface FormRuleActionMapper {
    /**
     * 查询表单规则动作
     *
     * @param actionId 表单规则动作主键
     * @return 表单规则动作
     */
    public FormRuleAction selectFormRuleActionByActionId(String actionId);

    /**
     * 查询表单规则动作列表
     *
     * @param formRuleAction 表单规则动作
     * @return 表单规则动作集合
     */
    public List<FormRuleAction> selectFormRuleActionList(FormRuleAction formRuleAction);

    /**
     * 根据规则ID查询动作列表
     *
     * @param ruleId 规则ID
     * @return 表单规则动作集合
     */
    public List<FormRuleAction> selectFormRuleActionByRuleId(String ruleId);

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
     * 删除表单规则动作
     *
     * @param actionId 表单规则动作主键
     * @return 结果
     */
    public int deleteFormRuleActionByActionId(String actionId);

    /**
     * 批量删除表单规则动作
     *
     * @param actionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFormRuleActionByActionIds(String[] actionIds);

    /**
     * 根据规则ID删除动作
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    public int deleteFormRuleActionByRuleId(String ruleId);
}