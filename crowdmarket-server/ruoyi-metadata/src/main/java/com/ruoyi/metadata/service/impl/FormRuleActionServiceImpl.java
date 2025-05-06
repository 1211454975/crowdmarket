package com.ruoyi.metadata.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.metadata.mapper.FormRuleActionMapper;
import com.ruoyi.metadata.domain.FormRuleAction;
import com.ruoyi.metadata.service.IFormRuleActionService;
import com.ruoyi.common.core.text.Convert;

/**
 * 表单规则动作 服务实现类
 *
 * @author ruoyi
 */
@Service
public class FormRuleActionServiceImpl implements IFormRuleActionService
{
    @Autowired
    private FormRuleActionMapper formRuleActionMapper;

    /**
     * 查询表单规则动作
     *
     * @param actionId 动作ID
     * @return 表单规则动作
     */
    @Override
    public FormRuleAction selectFormRuleActionById(String actionId)
    {
        return formRuleActionMapper.selectFormRuleActionByActionId(actionId);
    }

    /**
     * 查询表单规则动作列表
     *
     * @param formRuleAction 表单规则动作
     * @return 表单规则动作
     */
    @Override
    public List<FormRuleAction> selectFormRuleActionList(FormRuleAction formRuleAction)
    {
        return formRuleActionMapper.selectFormRuleActionList(formRuleAction);
    }

    /**
     * 根据规则ID查询规则动作列表
     *
     * @param ruleId 规则ID
     * @return 表单规则动作集合
     */
    @Override
    public List<FormRuleAction> selectFormRuleActionListByRuleId(String ruleId)
    {
        FormRuleAction formRuleAction = new FormRuleAction();
        formRuleAction.setRuleId(ruleId);
        return formRuleActionMapper.selectFormRuleActionList(formRuleAction);
    }

    /**
     * 新增表单规则动作
     *
     * @param formRuleAction 表单规则动作
     * @return 结果
     */
    @Override
    public int insertFormRuleAction(FormRuleAction formRuleAction)
    {
        return formRuleActionMapper.insertFormRuleAction(formRuleAction);
    }

    /**
     * 修改表单规则动作
     *
     * @param formRuleAction 表单规则动作
     * @return 结果
     */
    @Override
    public int updateFormRuleAction(FormRuleAction formRuleAction)
    {
        return formRuleActionMapper.updateFormRuleAction(formRuleAction);
    }

    /**
     * 删除表单规则动作对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteFormRuleActionByIds(String[] ids)
    {
        return formRuleActionMapper.deleteFormRuleActionByActionIds(ids);
    }

    /**
     * 删除表单规则动作信息
     *
     * @param actionId 动作ID
     * @return 结果
     */
    @Override
    public int deleteFormRuleActionById(String actionId)
    {
        return formRuleActionMapper.deleteFormRuleActionByActionId(actionId);
    }

    /**
     * 根据规则ID删除规则动作
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    @Override
    public int deleteFormRuleActionByRuleId(String ruleId)
    {
        return formRuleActionMapper.deleteFormRuleActionByRuleId(ruleId);
    }
}