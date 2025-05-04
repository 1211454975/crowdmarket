package com.ruoyi.metadata.mapper;

import java.util.List;
import com.ruoyi.metadata.domain.FormRule;

/**
 * 表单规则Mapper接口
 *
 * @author ruoyi
 */
public interface FormRuleMapper {
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
     * 删除表单规则
     *
     * @param ruleId 表单规则主键
     * @return 结果
     */
    public int deleteFormRuleByRuleId(String ruleId);

    /**
     * 批量删除表单规则
     *
     * @param ruleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFormRuleByRuleIds(String[] ruleIds);

    /**
     * 根据元数据ID删除规则
     *
     * @param metadataId 元数据ID
     * @return 结果
     */
    public int deleteFormRuleByMetadataId(String metadataId);
}