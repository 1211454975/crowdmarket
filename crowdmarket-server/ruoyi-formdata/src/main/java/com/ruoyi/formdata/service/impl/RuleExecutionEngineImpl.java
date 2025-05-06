package com.ruoyi.formdata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.formdata.service.IRuleExecutionEngine;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.domain.FormRule;
import com.ruoyi.metadata.domain.FormRuleAction;
import com.ruoyi.metadata.domain.FormRuleCondition;
import com.ruoyi.metadata.service.IFormFieldMetadataService;
import com.ruoyi.metadata.service.IFormRuleActionService;
import com.ruoyi.metadata.service.IFormRuleConditionService;
import com.ruoyi.metadata.service.IFormRuleService;

/**
 * 规则执行引擎 服务实现类
 *
 * @author ruoyi
 */
@Service
public class RuleExecutionEngineImpl implements IRuleExecutionEngine {
    private static final Logger log = LoggerFactory.getLogger(RuleExecutionEngineImpl.class);

    @Autowired
    private IFormRuleService formRuleService;

    @Autowired
    private IFormRuleConditionService formRuleConditionService;

    @Autowired
    private IFormRuleActionService formRuleActionService;

    @Autowired
    private IFormFieldMetadataService formFieldMetadataService;

    private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");

    /**
     * 执行表单加载时的规则
     *
     * @param metadataId 元数据ID
     * @param formData   表单数据
     * @return 规则执行结果
     */
    @Override
    public Map<String, Object> executeLoadRules(String metadataId, Map<String, Object> formData) {
        // 查询所有加载时执行的规则
        FormRule queryRule = new FormRule();
        queryRule.setMetadataId(metadataId);
        queryRule.setIsActive(1);
        List<FormRule> rules = formRuleService.selectFormRuleList(queryRule);

        // 过滤出加载时执行的规则
        List<FormRule> loadRules = new ArrayList<>();
        for (FormRule rule : rules) {
            if ("load".equals(rule.getRuleType())) {
                loadRules.add(rule);
            }
        }

        // 按执行顺序排序
        loadRules.sort((a, b) -> a.getExecutionOrder() - b.getExecutionOrder());

        // 执行规则
        return executeRules(loadRules, formData);
    }

    /**
     * 执行字段值变化时的规则
     *
     * @param metadataId 元数据ID
     * @param fieldName  字段名称
     * @param oldValue   旧值
     * @param newValue   新值
     * @param formData   表单数据
     * @return 规则执行结果
     */
    @Override
    public Map<String, Object> executeFieldChangeRules(String metadataId, String fieldName,
                                                       Object oldValue, Object newValue, Map<String, Object> formData) {
        // 查询所有字段值变化时执行的规则
        FormRule queryRule = new FormRule();
        queryRule.setMetadataId(metadataId);
        queryRule.setIsActive(1);
        List<FormRule> rules = formRuleService.selectFormRuleList(queryRule);

        // 过滤出字段值变化时执行的规则
        List<FormRule> fieldChangeRules = new ArrayList<>();
        for (FormRule rule : rules) {
            if ("fieldChange".equals(rule.getRuleType())) {
                // 检查规则是否与当前字段相关
                List<FormRuleCondition> conditions = formRuleConditionService.selectFormRuleConditionListByRuleId(rule.getRuleId());
                for (FormRuleCondition condition : conditions) {
                    // 获取字段元数据
                    FormFieldMetadata field = formFieldMetadataService.selectFormFieldMetadataByFieldId(condition.getFieldId());
                    if (field != null && field.getFieldName().equals(fieldName)) {
                        fieldChangeRules.add(rule);
                        break;
                    }
                }
            }
        }

        // 按执行顺序排序
        fieldChangeRules.sort((a, b) -> a.getExecutionOrder() - b.getExecutionOrder());

        // 更新表单数据中的字段值
        formData.put(fieldName, newValue);

        // 执行规则
        return executeRules(fieldChangeRules, formData);
    }

    /**
     * 执行表单提交前的规则
     *
     * @param metadataId 元数据ID
     * @param formData   表单数据
     * @return 规则执行结果，包含验证结果
     */
    @Override
    public Map<String, Object> executeSubmitRules(String metadataId, Map<String, Object> formData) {
        // 查询所有提交前执行的规则
        FormRule queryRule = new FormRule();
        queryRule.setMetadataId(metadataId);
        queryRule.setIsActive(1);
        List<FormRule> rules = formRuleService.selectFormRuleList(queryRule);

        // 过滤出提交前执行的规则
        List<FormRule> submitRules = new ArrayList<>();
        for (FormRule rule : rules) {
            if ("submit".equals(rule.getRuleType()) || "validation".equals(rule.getRuleType())) {
                submitRules.add(rule);
            }
        }

        // 按执行顺序排序
        submitRules.sort((a, b) -> a.getExecutionOrder() - b.getExecutionOrder());

        // 执行规则
        Map<String, Object> result = executeRules(submitRules, formData);

        // 添加验证结果
        boolean isValid = true;
        List<String> errors = new ArrayList<>();

        if (result.containsKey("errors")) {
            @SuppressWarnings("unchecked")
            List<String> ruleErrors = (List<String>) result.get("errors");
            if (!ruleErrors.isEmpty()) {
                isValid = false;
                errors.addAll(ruleErrors);
            }
        }

        result.put("isValid", isValid);
        result.put("errors", errors);

        return result;
    }

    /**
     * 执行表单提交后的规则
     *
     * @param metadataId 元数据ID
     * @param formData   表单数据
     * @return 规则执行结果
     */
    @Override
    public Map<String, Object> executeAfterSubmitRules(String metadataId, Map<String, Object> formData) {
        // 查询所有提交后执行的规则
        FormRule queryRule = new FormRule();
        queryRule.setMetadataId(metadataId);
        queryRule.setIsActive(1);
        List<FormRule> rules = formRuleService.selectFormRuleList(queryRule);

        // 过滤出提交后执行的规则
        List<FormRule> afterSubmitRules = new ArrayList<>();
        for (FormRule rule : rules) {
            if ("afterSubmit".equals(rule.getRuleType()) || "workflow".equals(rule.getRuleType())) {
                afterSubmitRules.add(rule);
            }
        }

        // 按执行顺序排序
        afterSubmitRules.sort((a, b) -> a.getExecutionOrder() - b.getExecutionOrder());

        // 执行规则
        return executeRules(afterSubmitRules, formData);
    }

    /**
     * 执行指定规则
     *
     * @param rule     规则
     * @param formData 表单数据
     * @return 规则执行结果
     */
    @Override
    public Map<String, Object> executeRule(FormRule rule, Map<String, Object> formData) {
        Map<String, Object> result = new HashMap<>();
        result.put("ruleId", rule.getRuleId());
        result.put("ruleName", rule.getRuleName());
        result.put("executed", false);
        result.put("success", false);

        try {
            // 验证规则条件
            boolean conditionsMet = evaluateRuleConditions(rule.getRuleId(), formData);
            result.put("conditionsMet", conditionsMet);

            // 如果条件满足，执行规则动作
            if (conditionsMet) {
                Map<String, Object> actionResult = executeRuleActions(rule.getRuleId(), formData);
                result.put("executed", true);
                result.put("success", actionResult.getOrDefault("success", false));
                result.put("actions", actionResult.getOrDefault("actions", new ArrayList<>()));

                // 合并动作执行结果
                for (Map.Entry<String, Object> entry : actionResult.entrySet()) {
                    if (!result.containsKey(entry.getKey())) {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (Exception e) {
            log.error("执行规则异常，ruleId: {}, error: {}", rule.getRuleId(), e.getMessage(), e);
            result.put("error", e.getMessage());

            // 添加错误信息
            List<String> errors = (List<String>) result.getOrDefault("errors", new ArrayList<String>());
            errors.add("规则 [" + rule.getRuleName() + "] 执行异常: " + e.getMessage());
            result.put("errors", errors);
        }

        return result;
    }

    /**
     * 执行指定规则列表
     *
     * @param rules    规则列表
     * @param formData 表单数据
     * @return 规则执行结果
     */
    @Override
    public Map<String, Object> executeRules(List<FormRule> rules, Map<String, Object> formData) {
        Map<String, Object> result = new HashMap<>();
        result.put("totalRules", rules.size());
        result.put("executedRules", 0);
        result.put("successRules", 0);

        List<Map<String, Object>> ruleResults = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // 创建表单数据副本，用于规则执行
        Map<String, Object> workingFormData = new HashMap<>(formData);

        for (FormRule rule : rules) {
            Map<String, Object> ruleResult = executeRule(rule, workingFormData);
            ruleResults.add(ruleResult);

            // 更新执行统计
            if ((Boolean) ruleResult.getOrDefault("executed", false)) {
                result.put("executedRules", (Integer) result.get("executedRules") + 1);

                if ((Boolean) ruleResult.getOrDefault("success", false)) {
                    result.put("successRules", (Integer) result.get("successRules") + 1);
                }
            }

            // 收集错误信息
            if (ruleResult.containsKey("errors")) {
                @SuppressWarnings("unchecked")
                List<String> ruleErrors = (List<String>) ruleResult.get("errors");
                errors.addAll(ruleErrors);
            }

            // 更新表单数据
            if (ruleResult.containsKey("formData")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> updatedFormData = (Map<String, Object>) ruleResult.get("formData");
                workingFormData.putAll(updatedFormData);
            }
        }

        result.put("ruleResults", ruleResults);
        result.put("errors", errors);
        result.put("formData", workingFormData);

        return result;
    }

    /**
     * 验证规则条件是否满足
     *
     * @param ruleId   规则ID
     * @param formData 表单数据
     * @return 是否满足
     */
    @Override
    public boolean evaluateRuleConditions(String ruleId, Map<String, Object> formData) {
        // 查询规则条件
        List<FormRuleCondition> conditions = formRuleConditionService.selectFormRuleConditionListByRuleId(ruleId);

        // 如果没有条件，默认满足
        if (conditions.isEmpty()) {
            return true;
        }

        // 按条件组分组
        Map<String, List<FormRuleCondition>> conditionGroups = new HashMap<>();
        for (FormRuleCondition condition : conditions) {
            String groupId = condition.getGroupId() != null ? condition.getGroupId() : "default";
            if (!conditionGroups.containsKey(groupId)) {
                conditionGroups.put(groupId, new ArrayList<>());
            }
            conditionGroups.get(groupId).add(condition);
        }

        // 如果有多个条件组，任一组满足即可
        for (List<FormRuleCondition> group : conditionGroups.values()) {
            if (evaluateConditionGroup(group, formData)) {
                return true;
            }
        }

        // 如果只有一个默认组，则必须满足
        return conditionGroups.size() == 1 && conditionGroups.containsKey("default") &&
                evaluateConditionGroup(conditionGroups.get("default"), formData);
    }

    /**
     * 验证条件组是否满足
     *
     * @param conditions 条件组
     * @param formData   表单数据
     * @return 是否满足
     */
    private boolean evaluateConditionGroup(List<FormRuleCondition> conditions, Map<String, Object> formData) {
        if (conditions.isEmpty()) {
            return true;
        }

        // 检查逻辑类型（AND/OR）
        String logicType = conditions.get(0).getLogicType();
        boolean isAnd = "AND".equalsIgnoreCase(logicType);

        for (FormRuleCondition condition : conditions) {
            // 获取字段元数据
            FormFieldMetadata field = formFieldMetadataService.selectFormFieldMetadataByFieldId(condition.getFieldId());
            if (field == null) {
                log.error("字段元数据不存在，fieldId: {}", condition.getFieldId());
                continue;
            }

            // 获取字段值
            Object fieldValue = formData.get(field.getFieldName());

            // 获取比较值
            Object compareValue = condition.getValue();

            // 验证条件
            boolean conditionMet = evaluateCondition(fieldValue, compareValue, condition.getOperator());

            // 根据逻辑类型判断结果
            if (isAnd) {
                // AND逻辑，任一条件不满足则整组不满足
                if (!conditionMet) {
                    return false;
                }
            } else {
                // OR逻辑，任一条件满足则整组满足
                if (conditionMet) {
                    return true;
                }
            }
        }

        // 如果是AND逻辑，所有条件都满足才返回true
        // 如果是OR逻辑，所有条件都不满足才返回false
        return isAnd;
    }

    /**
     * 验证单个条件是否满足
     *
     * @param fieldValue   字段值
     * @param compareValue 比较值
     * @param operator     操作符
     * @return 是否满足
     */
    private boolean evaluateCondition(Object fieldValue, Object compareValue, String operator) {
        // 处理null值
        if (fieldValue == null) {
            // 如果比较值也为null，则eq操作符返回true，其他返回false
            if (compareValue == null) {
                return "eq".equals(operator);
            }
            return false;
        }

        // 转换比较值类型
        if (compareValue instanceof String && fieldValue instanceof Number) {
            try {
                if (fieldValue instanceof Integer) {
                    compareValue = Integer.parseInt((String) compareValue);
                } else if (fieldValue instanceof Long) {
                    compareValue = Long.parseLong((String) compareValue);
                } else if (fieldValue instanceof Double) {
                    compareValue = Double.parseDouble((String) compareValue);
                } else if (fieldValue instanceof Float) {
                    compareValue = Float.parseFloat((String) compareValue);
                }
            } catch (NumberFormatException e) {
                log.error("比较值类型转换失败", e);
                return false;
            }
        }

        // 根据操作符验证条件
        switch (operator.toLowerCase()) {
            case "eq":
                return fieldValue.equals(compareValue);
            case "ne":
                return !fieldValue.equals(compareValue);
            case "gt":
                if (fieldValue instanceof Comparable && compareValue instanceof Comparable) {
                    @SuppressWarnings("unchecked")
                    Comparable<Object> comparableField = (Comparable<Object>) fieldValue;
                    return comparableField.compareTo(compareValue) > 0;
                }
                return false;
            case "lt":
                if (fieldValue instanceof Comparable && compareValue instanceof Comparable) {
                    @SuppressWarnings("unchecked")
                    Comparable<Object> comparableField = (Comparable<Object>) fieldValue;
                    return comparableField.compareTo(compareValue) < 0;
                }
                return false;
            case "ge":
                if (fieldValue instanceof Comparable && compareValue instanceof Comparable) {
                    @SuppressWarnings("unchecked")
                    Comparable<Object> comparableField = (Comparable<Object>) fieldValue;
                    return comparableField.compareTo(compareValue) >= 0;
                }
                return false;
            case "le":
                if (fieldValue instanceof Comparable && compareValue instanceof Comparable) {
                    @SuppressWarnings("unchecked")
                    Comparable<Object> comparableField = (Comparable<Object>) fieldValue;
                    return comparableField.compareTo(compareValue) <= 0;
                }
                return false;
            case "contains":
                if (fieldValue instanceof String && compareValue instanceof String) {
                    return ((String) fieldValue).contains((String) compareValue);
                }
                return false;
            case "startswith":
                if (fieldValue instanceof String && compareValue instanceof String) {
                    return ((String) fieldValue).startsWith((String) compareValue);
                }
                return false;
            case "endswith":
                if (fieldValue instanceof String && compareValue instanceof String) {
                    return ((String) fieldValue).endsWith((String) compareValue);
                }
                return false;
            default:
                log.error("不支持的操作符: {}", operator);
                return false;
        }
    }

    /**
     * 执行规则动作
     *
     * @param ruleId   规则ID
     * @param formData 表单数据
     * @return 执行结果
     */
    @Override
    public Map<String, Object> executeRuleActions(String ruleId, Map<String, Object> formData) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("actions", new ArrayList<Map<String, Object>>());

        // 查询规则动作
        List<FormRuleAction> actions = formRuleActionService.selectFormRuleActionListByRuleId(ruleId);
        if (actions.isEmpty()) {
            return result;
        }

        // 创建表单数据副本，用于记录变更
        Map<String, Object> updatedFormData = new HashMap<>();

        // 执行每个动作
        for (FormRuleAction action : actions) {
            Map<String, Object> actionResult = new HashMap<>();
            actionResult.put("actionId", action.getActionId());
            actionResult.put("actionType", action.getActionType());
            actionResult.put("success", false);

            try {
                // 获取目标字段
                FormFieldMetadata targetField = null;
                if (action.getTargetFieldId() != null) {
                    targetField = formFieldMetadataService.selectFormFieldMetadataByFieldId(action.getTargetFieldId());
                    if (targetField == null) {
                        log.error("目标字段不存在，fieldId: {}", action.getTargetFieldId());
                        actionResult.put("error", "目标字段不存在");
                        ((List<Map<String, Object>>) result.get("actions")).add(actionResult);
                        continue;
                    }
                    actionResult.put("targetField", targetField.getFieldName());
                }

                // 根据动作类型执行不同操作
                switch (action.getActionType()) {
                    case "setValue":
                        if (targetField != null) {
                            Object value = action.getActionValue();
                            // 如果是脚本，则执行脚本计算值
                            if (StringUtils.isNotEmpty(action.getActionScript())) {
                                value = executeScript(action.getActionScript(), formData);
                            }
                            updatedFormData.put(targetField.getFieldName(), value);
                            actionResult.put("value", value);
                            actionResult.put("success", true);
                        }
                        break;
                    case "setVisible":
                        if (targetField != null) {
                            boolean visible = "true".equalsIgnoreCase(action.getActionValue());
                            updatedFormData.put("_visible_" + targetField.getFieldName(), visible);
                            actionResult.put("visible", visible);
                            actionResult.put("success", true);
                        }
                        break;
                    case "setRequired":
                        if (targetField != null) {
                            boolean required = "true".equalsIgnoreCase(action.getActionValue());
                            updatedFormData.put("_required_" + targetField.getFieldName(), required);
                            actionResult.put("required", required);
                            actionResult.put("success", true);
                        }
                        break;
                    case "setReadOnly":
                        if (targetField != null) {
                            boolean readOnly = "true".equalsIgnoreCase(action.getActionValue());
                            updatedFormData.put("_readonly_" + targetField.getFieldName(), readOnly);
                            actionResult.put("readOnly", readOnly);
                            actionResult.put("success", true);
                        }
                        break;
                    case "calculate":
                        if (targetField != null && StringUtils.isNotEmpty(action.getActionScript())) {
                            Object value = executeScript(action.getActionScript(), formData);
                            updatedFormData.put(targetField.getFieldName(), value);
                            actionResult.put("value", value);
                            actionResult.put("success", true);
                        }
                        break;
                    case "message":
                        String message = action.getActionValue();
                        if (StringUtils.isNotEmpty(action.getActionScript())) {
                            Object scriptResult = executeScript(action.getActionScript(), formData);
                            if (scriptResult != null) {
                                message = scriptResult.toString();
                            }
                        }

                        List<String> messages = (List<String>) result.getOrDefault("messages", new ArrayList<String>());
                        messages.add(message);
                        result.put("messages", messages);
                        actionResult.put("message", message);
                        actionResult.put("success", true);
                        break;
                    case "api":
                        // 调用API的逻辑，实际实现应该根据具体需求开发
                        // 这里简化处理，仅记录API调用信息
                        actionResult.put("api", action.getActionValue());
                        actionResult.put("success", true);
                        break;
                    default:
                        log.error("不支持的动作类型: {}", action.getActionType());
                        actionResult.put("error", "不支持的动作类型");
                        break;
                }
            } catch (Exception e) {
                log.error("执行规则动作异常", e);
                actionResult.put("error", e.getMessage());
                result.put("success", false);
            }

            ((List<Map<String, Object>>) result.get("actions")).add(actionResult);
        }

        // 添加更新后的表单数据
        result.put("formData", updatedFormData);

        return result;
    }

    /**
     * 执行脚本
     *
     * @param script   脚本
     * @param formData 表单数据
     * @return 执行结果
     * @throws ScriptException 脚本执行异常
     */
    private Object executeScript(String script, Map<String, Object> formData) throws ScriptException {
        // 将表单数据绑定到脚本引擎
        scriptEngine.put("formData", formData);

        // 执行脚本
        return scriptEngine.eval(script);
    }
}