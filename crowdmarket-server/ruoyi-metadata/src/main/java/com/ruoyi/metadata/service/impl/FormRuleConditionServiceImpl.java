package com.ruoyi.metadata.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.metadata.mapper.FormRuleConditionMapper;
import com.ruoyi.metadata.domain.FormRuleCondition;
import com.ruoyi.metadata.service.IFormRuleConditionService;
import com.ruoyi.common.core.text.Convert;

/**
 * 表单规则条件 服务实现类
 *
 * @author ruoyi
 */
@Service
public class FormRuleConditionServiceImpl implements IFormRuleConditionService
{
    @Autowired
    private FormRuleConditionMapper formRuleConditionMapper;

    /**
     * 查询表单规则条件
     *
     * @param conditionId 条件ID
     * @return 表单规则条件
     */
    @Override
    public FormRuleCondition selectFormRuleConditionById(String conditionId)
    {
        return formRuleConditionMapper.selectFormRuleConditionByConditionId(conditionId);
    }

    /**
     * 查询表单规则条件列表
     *
     * @param formRuleCondition 表单规则条件
     * @return 表单规则条件
     */
    @Override
    public List<FormRuleCondition> selectFormRuleConditionList(FormRuleCondition formRuleCondition)
    {
        return formRuleConditionMapper.selectFormRuleConditionList(formRuleCondition);
    }

    /**
     * 根据规则ID查询规则条件列表
     *
     * @param ruleId 规则ID
     * @return 表单规则条件集合
     */
    @Override
    public List<FormRuleCondition> selectFormRuleConditionListByRuleId(String ruleId)
    {
        FormRuleCondition formRuleCondition = new FormRuleCondition();
        formRuleCondition.setRuleId(ruleId);
        return formRuleConditionMapper.selectFormRuleConditionList(formRuleCondition);
    }

    /**
     * 新增表单规则条件
     *
     * @param formRuleCondition 表单规则条件
     * @return 结果
     */
    @Override
    public int insertFormRuleCondition(FormRuleCondition formRuleCondition)
    {
        return formRuleConditionMapper.insertFormRuleCondition(formRuleCondition);
    }

    /**
     * 修改表单规则条件
     *
     * @param formRuleCondition 表单规则条件
     * @return 结果
     */
    @Override
    public int updateFormRuleCondition(FormRuleCondition formRuleCondition)
    {
        return formRuleConditionMapper.updateFormRuleCondition(formRuleCondition);
    }

    /**
     * 删除表单规则条件对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteFormRuleConditionByIds(String[] ids)
    {
        return formRuleConditionMapper.deleteFormRuleConditionByConditionIds(ids);
    }

    /**
     * 删除表单规则条件信息
     *
     * @param conditionId 条件ID
     * @return 结果
     */
    @Override
    public int deleteFormRuleConditionById(String conditionId)
    {
        return formRuleConditionMapper.deleteFormRuleConditionByConditionId(conditionId);
    }

    /**
     * 根据规则ID删除规则条件
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    @Override
    public int deleteFormRuleConditionByRuleId(String ruleId)
    {
        return formRuleConditionMapper.deleteFormRuleConditionByRuleId(ruleId);
    }
}