package com.ruoyi.metadata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.domain.FormRule;
import com.ruoyi.metadata.domain.FormRuleAction;
import com.ruoyi.metadata.domain.FormRuleCondition;
import com.ruoyi.metadata.mapper.FormRuleActionMapper;
import com.ruoyi.metadata.mapper.FormRuleConditionMapper;
import com.ruoyi.metadata.mapper.FormRuleMapper;
import com.ruoyi.metadata.service.IFormFieldMetadataService;
import com.ruoyi.metadata.service.IFormRuleService;

/**
 * 表单规则Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class FormRuleServiceImpl implements IFormRuleService {
    @Autowired
    private FormRuleMapper formRuleMapper;

    @Autowired
    private FormRuleConditionMapper formRuleConditionMapper;

    @Autowired
    private FormRuleActionMapper formRuleActionMapper;

    @Autowired
    private IFormFieldMetadataService IFormFieldMetadataService;

    /**
     * 查询表单规则
     *
     * @param ruleId 表单规则主键
     * @return 表单规则
     */
    @Override
    public FormRule selectFormRuleByRuleId(String ruleId) {
        return formRuleMapper.selectFormRuleByRuleId(ruleId);
    }

    /**
     * 查询表单规则列表
     *
     * @param formRule 表单规则
     * @return 表单规则
     */
    @Override
    public List<FormRule> selectFormRuleList(FormRule formRule) {
        return formRuleMapper.selectFormRuleList(formRule);
    }

    /**
     * 根据元数据ID查询规则列表
     *
     * @param metadataId 元数据ID
     * @return 表单规则集合
     */
    @Override
    public List<FormRule> selectFormRuleByMetadataId(String metadataId) {
        return formRuleMapper.selectFormRuleByMetadataId(metadataId);
    }

    /**
     * 新增表单规则
     *
     * @param formRule 表单规则
     * @return 结果
     */
    @Override
    @Transactional
    public int insertFormRule(FormRule formRule) {
        // 生成UUID作为规则ID
        formRule.setRuleId(UUID.randomUUID().toString().replace("-", ""));
        // 设置创建时间
        formRule.setCreateTime(DateUtils.getNowDate());
        // 设置创建者
        formRule.setCreateBy(SecurityUtils.getUsername());
        // 设置租户ID
        formRule.setTenantId(SecurityUtils.getCurrComId());

        return formRuleMapper.insertFormRule(formRule);
    }

    /**
     * 修改表单规则
     *
     * @param formRule 表单规则
     * @return 结果
     */
    @Override
    @Transactional
    public int updateFormRule(FormRule formRule) {
        // 设置更新时间
        formRule.setUpdateTime(DateUtils.getNowDate());
        // 设置更新者
        formRule.setUpdateBy(SecurityUtils.getUsername());

        return formRuleMapper.updateFormRule(formRule);
    }

    /**
     * 批量删除表单规则
     *
     * @param ruleIds 需要删除的表单规则主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFormRuleByRuleIds(String[] ruleIds) {
        // 删除规则条件和动作
        for (String ruleId : ruleIds) {
            formRuleConditionMapper.deleteFormRuleConditionByRuleId(ruleId);
            formRuleActionMapper.deleteFormRuleActionByRuleId(ruleId);
        }

        return formRuleMapper.deleteFormRuleByRuleIds(ruleIds);
    }

    /**
     * 删除表单规则信息
     *
     * @param ruleId 表单规则主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFormRuleByRuleId(String ruleId) {
        // 删除规则条件和动作
        formRuleConditionMapper.deleteFormRuleConditionByRuleId(ruleId);
        formRuleActionMapper.deleteFormRuleActionByRuleId(ruleId);

        return formRuleMapper.deleteFormRuleByRuleId(ruleId);
    }

    /**
     * 获取规则详情，包含条件和动作
     *
     * @param ruleId 规则ID
     * @return 规则详情
     */
    @Override
    public Map<String, Object> getFormRuleDetail(String ruleId) {
        Map<String, Object> result = new HashMap<>();

        // 获取规则信息
        FormRule rule = formRuleMapper.selectFormRuleByRuleId(ruleId);
        if (rule == null) {
            return result;
        }

        // 获取规则条件
        List<FormRuleCondition> conditions = formRuleConditionMapper.selectFormRuleConditionByRuleId(ruleId);

        // 获取规则动作
        List<FormRuleAction> actions = formRuleActionMapper.selectFormRuleActionByRuleId(ruleId);

        result.put("rule", rule);
        result.put("conditions", conditions);
        result.put("actions", actions);

        return result;
    }

    /**
     * 保存规则详情，包含条件和动作
     *
     * @param ruleId     规则ID
     * @param conditions 条件列表
     * @param actions    动作列表
     * @return 结果
     */
    @Override
    @Transactional
    public int saveFormRuleDetail(String ruleId, List<FormRuleCondition> conditions, List<FormRuleAction> actions) {
        // 删除原有条件和动作
        formRuleConditionMapper.deleteFormRuleConditionByRuleId(ruleId);
        formRuleActionMapper.deleteFormRuleActionByRuleId(ruleId);

        // 保存条件
        if (conditions != null && !conditions.isEmpty()) {
            for (FormRuleCondition condition : conditions) {
                condition.setConditionId(UUID.randomUUID().toString().replace("-", ""));
                condition.setRuleId(ruleId);
                condition.setTenantId(SecurityUtils.getCurrComId());
                formRuleConditionMapper.insertFormRuleCondition(condition);
            }
        }

        // 保存动作
        if (actions != null && !actions.isEmpty()) {
            for (FormRuleAction action : actions) {
                action.setActionId(UUID.randomUUID().toString().replace("-", ""));
                action.setRuleId(ruleId);
                action.setTenantId(SecurityUtils.getCurrComId());
                formRuleActionMapper.insertFormRuleAction(action);
            }
        }

        return 1;
    }

    /**
     * 启用规则
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    @Override
    @Transactional
    public int enableFormRule(String ruleId) {
        FormRule rule = new FormRule();
        rule.setRuleId(ruleId);
        rule.setIsActive(1);
        rule.setUpdateTime(DateUtils.getNowDate());
        rule.setUpdateBy(SecurityUtils.getUsername());

        return formRuleMapper.updateFormRule(rule);
    }

    /**
     * 禁用规则
     *
     * @param ruleId 规则ID
     * @return 结果
     */
    @Override
    @Transactional
    public int disableFormRule(String ruleId) {
        FormRule rule = new FormRule();
        rule.setRuleId(ruleId);
        rule.setIsActive(0);
        rule.setUpdateTime(DateUtils.getNowDate());
        rule.setUpdateBy(SecurityUtils.getUsername());

        return formRuleMapper.updateFormRule(rule);
    }

    /**
     * 获取规则条件列表
     *
     * @param ruleId 规则ID
     * @return 条件列表
     */
    @Override
    public List<FormRuleCondition> selectFormRuleConditionByRuleId(String ruleId) {
        return formRuleConditionMapper.selectFormRuleConditionByRuleId(ruleId);
    }

    /**
     * 获取规则动作列表
     *
     * @param ruleId 规则ID
     * @return 动作列表
     */
    @Override
    public List<FormRuleAction> selectFormRuleActionByRuleId(String ruleId) {
        return formRuleActionMapper.selectFormRuleActionByRuleId(ruleId);
    }

    /**
     * 执行规则
     *
     * @param metadataId   元数据ID
     * @param formData     表单数据
     * @param triggerPoint 触发点(load,change,submit,after)
     * @return 执行结果
     */
    @Override
    public Map<String, Object> executeRules(String metadataId, Map<String, Object> formData, String triggerPoint) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> fieldChanges = new HashMap<>();

        // 获取元数据对应的所有规则
        FormRule queryRule = new FormRule();
        queryRule.setMetadataId(metadataId);
        queryRule.setIsActive(1); // 只执行启用的规则
        List<FormRule> rules = formRuleMapper.selectFormRuleList(queryRule);

        if (rules.isEmpty()) {
            result.put("success", true);
            result.put("messages", messages);
            result.put("fieldChanges", fieldChanges);
            return result;
        }

        // 获取字段列表，用于字段ID到字段名的映射
        List<FormFieldMetadata> fieldList = IFormFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        Map<String, FormFieldMetadata> fieldMap = new HashMap<>();
        for (FormFieldMetadata field : fieldList) {
            fieldMap.put(field.getFieldId(), field);
        }

        // 按执行顺序排序规则
        rules.sort((r1, r2) -> {
            if (r1.getExecutionOrder() == null) return 1;
            if (r2.getExecutionOrder() == null) return -1;
            return r1.getExecutionOrder().compareTo(r2.getExecutionOrder());
        });

        // 执行规则
        for (FormRule rule : rules) {
            // 获取规则条件
            List<FormRuleCondition> conditions = formRuleConditionMapper.selectFormRuleConditionByRuleId(rule.getRuleId());

            // 判断条件是否满足
            boolean conditionsMet = evaluateConditions(conditions, formData, fieldMap);

            if (conditionsMet) {
                // 获取规则动作
                List<FormRuleAction> actions = formRuleActionMapper.selectFormRuleActionByRuleId(rule.getRuleId());

                // 执行动作
                for (FormRuleAction action : actions) {
                    executeAction(action, formData, fieldMap, fieldChanges, messages);
                }
            }
        }

        result.put("success", true);
        result.put("messages", messages);
        result.put("fieldChanges", fieldChanges);

        return result;
    }

    /**
     * 评估条件
     *
     * @param conditions 条件列表
     * @param formData   表单数据
     * @param fieldMap   字段映射
     * @return 条件是否满足
     */
    private boolean evaluateConditions(List<FormRuleCondition> conditions, Map<String, Object> formData, Map<String, FormFieldMetadata> fieldMap) {
        if (conditions == null || conditions.isEmpty()) {
            return true; // 没有条件，默认满足
        }

        // 按条件组分组
        Map<String, List<FormRuleCondition>> groupedConditions = new HashMap<>();
        for (FormRuleCondition condition : conditions) {
            String groupId = condition.getGroupId();
            if (groupId == null) {
                groupId = "default";
            }

            if (!groupedConditions.containsKey(groupId)) {
                groupedConditions.put(groupId, new ArrayList<>());
            }

            groupedConditions.get(groupId).add(condition);
        }

        // 评估每个条件组
        boolean result = true;
        for (List<FormRuleCondition> group : groupedConditions.values()) {
            boolean groupResult = evaluateConditionGroup(group, formData, fieldMap);
            result = result && groupResult;

            if (!result) {
                break; // 短路评估
            }
        }

        return result;
    }

    /**
     * 评估条件组
     *
     * @param group    条件组
     * @param formData 表单数据
     * @param fieldMap 字段映射
     * @return 条件组是否满足
     */
    private boolean evaluateConditionGroup(List<FormRuleCondition> group, Map<String, Object> formData, Map<String, FormFieldMetadata> fieldMap) {
        if (group == null || group.isEmpty()) {
            return true;
        }

        // 初始化结果，根据逻辑类型设置初始值
        // 对于AND逻辑，初始值为true；对于OR逻辑，初始值为false
        boolean result = "AND".equals(group.get(0).getLogicType());

        for (FormRuleCondition condition : group) {
            boolean conditionResult = evaluateCondition(condition, formData, fieldMap);

            if ("AND".equals(condition.getLogicType())) {
                // 对于AND逻辑，任一条件不满足则整体不满足
                result = result && conditionResult;
                if (!result) {
                    // 短路评估，一旦有一个条件不满足，整个AND组就不满足
                    break;
                }
            } else {
                // 对于OR逻辑，任一条件满足则整体满足
                result = result || conditionResult;
                if (result) {
                    // 短路评估，一旦有一个条件满足，整个OR组就满足
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 评估单个条件
     *
     * @param condition 条件
     * @param formData  表单数据
     * @param fieldMap  字段映射
     * @return 条件是否满足
     */
    private boolean evaluateCondition(FormRuleCondition condition, Map<String, Object> formData, Map<String, FormFieldMetadata> fieldMap) {
        // 获取字段信息
        FormFieldMetadata field = fieldMap.get(condition.getFieldId());
        if (field == null) {
            return false;
        }

        // 获取字段值
        String fieldName = field.getFieldName();
        Object fieldValue = formData.get(fieldName);

        // 获取比较值
        String compareValue = condition.getValue();

        // 根据操作符评估条件
        String operator = condition.getOperator();

        if (fieldValue == null) {
            // 如果字段值为空，只有在操作符为"eq"且比较值为空时才满足
            return "eq".equals(operator) && (compareValue == null || "".equals(compareValue));
        }

        // 字符串值比较
        String fieldValueStr = fieldValue.toString();

        switch (operator) {
            case "eq": // 等于
                return fieldValueStr.equals(compareValue);
            case "ne": // 不等于
                return !fieldValueStr.equals(compareValue);
            case "gt": // 大于
                try {
                    if (fieldValue instanceof Number && compareValue != null) {
                        double fieldNum = ((Number) fieldValue).doubleValue();
                        double compareNum = Double.parseDouble(compareValue);
                        return fieldNum > compareNum;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                return false;
            case "lt": // 小于
                try {
                    if (fieldValue instanceof Number && compareValue != null) {
                        double fieldNum = ((Number) fieldValue).doubleValue();
                        double compareNum = Double.parseDouble(compareValue);
                        return fieldNum < compareNum;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                return false;
            case "ge": // 大于等于
                try {
                    if (fieldValue instanceof Number && compareValue != null) {
                        double fieldNum = ((Number) fieldValue).doubleValue();
                        double compareNum = Double.parseDouble(compareValue);
                        return fieldNum >= compareNum;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                return false;
            case "le": // 小于等于
                try {
                    if (fieldValue instanceof Number && compareValue != null) {
                        double fieldNum = ((Number) fieldValue).doubleValue();
                        double compareNum = Double.parseDouble(compareValue);
                        return fieldNum <= compareNum;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                return false;
            case "contains": // 包含
                return compareValue != null && fieldValueStr.contains(compareValue);
            case "startswith": // 以...开始
                return compareValue != null && fieldValueStr.startsWith(compareValue);
            case "endswith": // 以...结束
                return compareValue != null && fieldValueStr.endsWith(compareValue);
            default:
                return false;
        }
    }

    /**
     * 执行动作
     *
     * @param action       动作
     * @param formData     表单数据
     * @param fieldMap     字段映射
     * @param fieldChanges 字段变更
     * @param messages     消息列表
     */
    private void executeAction(FormRuleAction action, Map<String, Object> formData, Map<String, FormFieldMetadata> fieldMap,
                               Map<String, Object> fieldChanges, List<Map<String, Object>> messages) {
        String actionType = action.getActionType();

        switch (actionType) {
            case "setValue": // 设置值
                if (action.getTargetFieldId() != null) {
                    FormFieldMetadata targetField = fieldMap.get(action.getTargetFieldId());
                    if (targetField != null) {
                        String fieldName = targetField.getFieldName();
                        Object value = action.getActionValue();

                        // 类型转换
                        if (value != null && value instanceof String) {
                            String valueStr = (String) value;
                            if ("number".equals(targetField.getFieldType())) {
                                try {
                                    if (valueStr.contains(".")) {
                                        value = Double.parseDouble(valueStr);
                                    } else {
                                        value = Integer.parseInt(valueStr);
                                    }
                                } catch (NumberFormatException e) {
                                    // 忽略转换错误
                                }
                            } else if ("boolean".equals(targetField.getFieldType())) {
                                value = Boolean.parseBoolean(valueStr) ? 1 : 0;
                            }
                        }

                        // 更新表单数据
                        formData.put(fieldName, value);

                        // 记录字段变更
                        Map<String, Object> change = new HashMap<>();
                        change.put("field", fieldName);
                        change.put("value", value);
                        fieldChanges.put(fieldName, change);
                    }
                }
                break;
            case "setVisible": // 设置可见性
                if (action.getTargetFieldId() != null) {
                    FormFieldMetadata targetField = fieldMap.get(action.getTargetFieldId());
                    if (targetField != null) {
                        String fieldName = targetField.getFieldName();
                        boolean visible = "true".equals(action.getActionValue());

                        // 记录字段变更
                        Map<String, Object> change = new HashMap<>();
                        change.put("field", fieldName);
                        change.put("visible", visible);
                        fieldChanges.put(fieldName + "_visible", change);
                    }
                }
                break;
            case "setRequired": // 设置必填
                if (action.getTargetFieldId() != null) {
                    FormFieldMetadata targetField = fieldMap.get(action.getTargetFieldId());
                    if (targetField != null) {
                        String fieldName = targetField.getFieldName();
                        boolean required = "true".equals(action.getActionValue());

                        // 记录字段变更
                        Map<String, Object> change = new HashMap<>();
                        change.put("field", fieldName);
                        change.put("required", required);
                        fieldChanges.put(fieldName + "_required", change);
                    }
                }
                break;
            case "setReadOnly": // 设置只读
                if (action.getTargetFieldId() != null) {
                    FormFieldMetadata targetField = fieldMap.get(action.getTargetFieldId());
                    if (targetField != null) {
                        String fieldName = targetField.getFieldName();
                        boolean readOnly = "true".equals(action.getActionValue());

                        // 记录字段变更
                        Map<String, Object> change = new HashMap<>();
                        change.put("field", fieldName);
                        change.put("readOnly", readOnly);
                        fieldChanges.put(fieldName + "_readOnly", change);
                    }
                }
                break;
            case "calculate": // 计算
                // 计算逻辑需要根据实际需求实现，这里只是一个简单示例
                if (action.getTargetFieldId() != null && StringUtils.isNotEmpty(action.getActionScript())) {
                    FormFieldMetadata targetField = fieldMap.get(action.getTargetFieldId());
                    if (targetField != null) {
                        String fieldName = targetField.getFieldName();
                        // 这里应该有一个脚本引擎来执行计算脚本
                        // 简单示例，实际应该使用脚本引擎
                        Object result = evaluateScript(action.getActionScript(), formData);

                        // 更新表单数据
                        formData.put(fieldName, result);

                        // 记录字段变更
                        Map<String, Object> change = new HashMap<>();
                        change.put("field", fieldName);
                        change.put("value", result);
                        fieldChanges.put(fieldName, change);
                    }
                }
                break;
            case "message": // 消息
                if (StringUtils.isNotEmpty(action.getActionValue())) {
                    Map<String, Object> message = new HashMap<>();
                    message.put("type", "info");
                    message.put("content", action.getActionValue());
                    messages.add(message);
                }
                break;
            case "api": // API调用
                // API调用逻辑需要根据实际需求实现
                break;
        }
    }

    /**
     * 评估脚本
     *
     * @param script   脚本
     * @param formData 表单数据
     * @return 脚本执行结果
     */
    private Object evaluateScript(String script, Map<String, Object> formData) {
        // 这里应该使用JavaScript引擎或其他脚本引擎来执行脚本
        // 简单示例，实际应该使用脚本引擎
        // 例如：使用Nashorn或GraalVM JavaScript引擎

        // 简单返回，实际实现中应该执行脚本并返回结果
        return script;
    }
}