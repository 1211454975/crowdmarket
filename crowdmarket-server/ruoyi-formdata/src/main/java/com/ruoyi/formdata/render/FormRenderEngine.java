package com.ruoyi.formdata.render;  
  
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
import java.util.stream.Collectors;

import com.ruoyi.framework.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;  
  
import com.fasterxml.jackson.core.JsonProcessingException;  
import com.fasterxml.jackson.databind.ObjectMapper;  

import com.ruoyi.common.utils.StringUtils;  
import com.ruoyi.metadata.domain.FormFieldMetadata;  
import com.ruoyi.metadata.domain.FormMetadata;  
import com.ruoyi.metadata.domain.FormRule;  
import com.ruoyi.metadata.domain.FormViewConfig;  
import com.ruoyi.metadata.service.IFormFieldMetadataService;  
import com.ruoyi.metadata.service.IFormMetadataService;  
import com.ruoyi.metadata.service.IFormRuleService;  
import com.ruoyi.metadata.service.IFormViewConfigService;  
import com.ruoyi.system.service.ISysDictTypeService;  
  
/**  
 * 表单渲染引擎  
 * TODO 删除
 *   
 * @author ruoyi  
 */  
@Component  
public class FormRenderEngine {  
      
    @Autowired  
    private IFormMetadataService formMetadataService;  
      
    @Autowired  
    private IFormFieldMetadataService fieldMetadataService;  
      
    @Autowired  
    private IFormViewConfigService viewConfigService;  
      
    @Autowired  
    private IFormRuleService ruleService;  
      
    @Autowired  
    private ISysDictTypeService dictTypeService;  
      
    @Autowired  
    private ObjectMapper objectMapper;  
      
    /**  
     * 根据元数据ID和视图类型生成表单渲染数据  
     *   
     * @param metadataId 元数据ID  
     * @param viewType 视图类型(form表单,list列表,detail详情)  
     * @param config 渲染配置  
     * @return 表单渲染数据  
     */  
    public AjaxResult generateFormRenderData(String metadataId, String viewType, FormRenderConfig config) {
        if (StringUtils.isEmpty(metadataId)) {  
            return AjaxResult.error("元数据ID不能为空");  
        }  
          
        // 获取表单元数据  
        FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {  
            return AjaxResult.error("表单元数据不存在");  
        }  
          
        // 获取字段元数据  
        List<FormFieldMetadata> fields = fieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);  
        if (fields == null || fields.isEmpty()) {  
            return AjaxResult.error("表单字段元数据不存在");  
        }  
          
        // 获取视图配置  
        FormViewConfig viewConfig = viewConfigService.selectDefaultFormViewConfig(metadataId, viewType);
          
        // 获取业务规则  
        List<FormRule> rules = ruleService.selectFormRuleByMetadataId(metadataId);
          
        // 构建渲染数据  
        Map<String, Object> renderData = buildRenderData(metadata, fields, viewConfig, rules, config);  
          
        return AjaxResult.success(renderData);  
    }  
      
    /**  
     * 构建表单渲染数据  
     */  
    private Map<String, Object> buildRenderData(FormMetadata metadata, List<FormFieldMetadata> fields,   
            FormViewConfig viewConfig, List<FormRule> rules, FormRenderConfig config) {  
        Map<String, Object> renderData = new HashMap<>();  
          
        // 基本信息  
        renderData.put("metadata", metadata);  
          
        // 表单配置  
        Map<String, Object> formConfig = new HashMap<>();  
        formConfig.put("labelWidth", config.getLabelWidth());  
        formConfig.put("size", config.getSize());  
        formConfig.put("disabled", config.isDisabled());  
        formConfig.put("readonly", config.isReadonly());  
        formConfig.put("hideRequiredAsterisk", config.isHideRequiredAsterisk());  
        formConfig.put("showMessage", config.isShowMessage());  
        formConfig.put("inline", config.isInline());  
        formConfig.put("validateOnRuleChange", config.getValidateOnRuleChange());  
        formConfig.put("customClass", config.getCustomClass());  
        renderData.put("formConfig", formConfig);  
          
        // 字段配置  
        List<Map<String, Object>> fieldConfigs = buildFieldConfigs(fields);  
        renderData.put("fields", fieldConfigs);  
          
        // 视图布局  
        if (viewConfig != null && StringUtils.isNotEmpty(viewConfig.getViewConfig())) {  
            try {  
                Object layoutConfig = objectMapper.readValue(viewConfig.getViewConfig(), Object.class);  
                renderData.put("layout", layoutConfig);  
            } catch (JsonProcessingException e) {  
                renderData.put("layout", null);  
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {  
            // 默认布局  
            renderData.put("layout", buildDefaultLayout(fields));  
        }  
          
        // 验证规则  
        renderData.put("rules", buildValidationRules(fields));  
          
        // 业务规则  
        if (rules != null && !rules.isEmpty()) {  
            renderData.put("businessRules", buildBusinessRules(rules));  
        }  
          
        return renderData;  
    }  
      
    /**  
     * 构建字段配置  
     */  
    private List<Map<String, Object>> buildFieldConfigs(List<FormFieldMetadata> fields) {  
        return fields.stream().map(field -> {  
            Map<String, Object> config = new HashMap<>();  
            config.put("fieldId", field.getFieldId());  
            config.put("fieldName", field.getFieldName());  
            config.put("fieldLabel", field.getFieldLabel());  
            config.put("fieldType", field.getFieldType());  
            config.put("htmlType", field.getHtmlType());  
            config.put("required", field.getIsRequired() == 1);  
            config.put("isPk", field.getIsPk() == 1);  
              
            // 处理字典类型  
            if (StringUtils.isNotEmpty(field.getDictType())) {  
                config.put("dictType", field.getDictType());  
                config.put("dictData", dictTypeService.selectDictDataByType(field.getDictType()));  
            }  
              
            return config;  
        }).collect(Collectors.toList());  
    }  
      
    /**  
     * 构建默认布局  
     */  
    private List<Map<String, Object>> buildDefaultLayout(List<FormFieldMetadata> fields) {  
        List<Map<String, Object>> layout = new ArrayList<>();  
          
        for (FormFieldMetadata field : fields) {  
            Map<String, Object> item = new HashMap<>();  
            item.put("fieldId", field.getFieldId());  
            item.put("span", 24); // 默认占满一行  
            item.put("offset", 0);  
            item.put("order", field.getSortOrder());  
              
            layout.add(item);  
        }  
          
        return layout;  
    }  
      
    /**  
     * 构建验证规则  
     */  
    private Map<String, Object> buildValidationRules(List<FormFieldMetadata> fields) {  
        Map<String, Object> rules = new HashMap<>();  
          
        for (FormFieldMetadata field : fields) {  
            if (field.getIsRequired() == 1) {  
                Map<String, Object> rule = new HashMap<>();  
                rule.put("required", true);  
                rule.put("message", field.getFieldLabel() + "不能为空");  
                rule.put("trigger", "blur");  
                  
                rules.put(field.getFieldName(), rule);  
            }  
              
            // 可以根据字段类型添加更多验证规则  
            // 例如：数字类型、邮箱类型、手机号类型等  
        }  
          
        return rules;  
    }  
      
    /**  
     * 构建业务规则  
     */  
    private List<Map<String, Object>> buildBusinessRules(List<FormRule> rules) {  
        return rules.stream()  
                .filter(rule -> rule.getIsActive() == 1)  
                .map(rule -> {  
                    Map<String, Object> ruleMap = new HashMap<>();  
                    ruleMap.put("ruleId", rule.getRuleId());  
                    ruleMap.put("ruleName", rule.getRuleName());  
                    ruleMap.put("ruleType", rule.getRuleType());  
                    ruleMap.put("executionOrder", rule.getExecutionOrder());  
                      
                    return ruleMap;  
                })  
                .sorted((r1, r2) -> {  
                    Integer order1 = (Integer) r1.get("executionOrder");  
                    Integer order2 = (Integer) r2.get("executionOrder");  
                    return order1.compareTo(order2);  
                })  
                .collect(Collectors.toList());  
    }  
      
    /**  
     * 根据元数据ID和数据ID生成表单详情渲染数据  
     *   
     * @param metadataId 元数据ID  
     * @param dataId 数据ID  
     * @param config 渲染配置  
     * @return 表单详情渲染数据  
     */  
    public AjaxResult generateFormDetailRenderData(String metadataId, String dataId, FormRenderConfig config) {  
        // 先获取表单结构  
        AjaxResult formResult = generateFormRenderData(metadataId, "detail", config);  
//        if (formResult.isError()) {
//            return formResult;
//        }
          
        // TODO: 从租户数据库中获取表单数据  
        // 这里需要调用FormDataService获取数据  
          
        return formResult;  
    }  
      
    /**  
     * 根据元数据ID生成表单列表渲染数据  
     *   
     * @param metadataId 元数据ID  
     * @return 表单列表渲染数据  
     */  
    public AjaxResult generateFormListRenderData(String metadataId) {  
        if (StringUtils.isEmpty(metadataId)) {  
            return AjaxResult.error("元数据ID不能为空");  
        }  
          
        // 获取表单元数据  
        FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null) {  
            return AjaxResult.error("表单元数据不存在");  
        }  
          
        // 获取字段元数据  
        List<FormFieldMetadata> fields = fieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);  
        if (fields == null || fields.isEmpty()) {  
            return AjaxResult.error("表单字段元数据不存在");  
        }  
          
        // 获取视图配置  
        FormViewConfig viewConfig = viewConfigService.selectDefaultFormViewConfig(metadataId, "list");
          
        // 构建列表渲染数据  
        Map<String, Object> renderData = new HashMap<>();  
          
        // 基本信息  
        renderData.put("metadata", metadata);  
          
        // 列表字段  
        List<Map<String, Object>> listFields = fields.stream()  
                .filter(field -> field.getIsList() == 1)  
                .map(field -> {  
                    Map<String, Object> config = new HashMap<>();  
                    config.put("fieldId", field.getFieldId());  
                    config.put("fieldName", field.getFieldName());  
                    config.put("fieldLabel", field.getFieldLabel());  
                    config.put("fieldType", field.getFieldType());  
                    config.put("dictType", field.getDictType());  
                    config.put("sortOrder", field.getSortOrder());  
                    return config;  
                })  
                .sorted((f1, f2) -> {  
                    Integer order1 = (Integer) f1.get("sortOrder");  
                    Integer order2 = (Integer) f2.get("sortOrder");  
                    return order1.compareTo(order2);  
                })  
                .collect(Collectors.toList());  
        renderData.put("listFields", listFields);  
          
        // 查询字段  
        List<Map<String, Object>> queryFields = fields.stream()  
                .filter(field -> field.getIsQuery() == 1)  
                .map(field -> {  
                    Map<String, Object> config = new HashMap<>();  
                    config.put("fieldId", field.getFieldId());  
                    config.put("fieldName", field.getFieldName());  
                    config.put("fieldLabel", field.getFieldLabel());  
                    config.put("fieldType", field.getFieldType());  
                    config.put("queryType", field.getQueryType());  
                    config.put("htmlType", field.getHtmlType());  
                    config.put("dictType", field.getDictType());  
                    return config;  
                })  
                .collect(Collectors.toList());  
        renderData.put("queryFields", queryFields);  
          
        // 视图配置  
        if (viewConfig != null && StringUtils.isNotEmpty(viewConfig.getViewConfig())) {  
            try {  
                Object listConfig = objectMapper.readValue(viewConfig.getViewConfig(), Object.class);  
                renderData.put("listConfig", listConfig);  
            } catch (IOException e) {
                renderData.put("listConfig", null);  
            }  
        }  
          
        return AjaxResult.success(renderData);  
    }  
      
    /**  
     * 生成HTML表单代码  
     *   
     * @param metadataId 元数据ID  
     * @param viewType 视图类型  
     * @return HTML代码  
     */  
    public AjaxResult generateFormHtml(String metadataId, String viewType) {  
        // 获取表单渲染数据  
        FormRenderConfig config = new FormRenderConfig();  
        AjaxResult renderResult = generateFormRenderData(metadataId, viewType, config);  
//        if (renderResult.isError()) {
//            return renderResult;
//        }
          
        Map<String, Object> renderData = (Map<String, Object>) renderResult.get("data");  
          
        // 构建HTML代码  
        StringBuilder html = new StringBuilder();  
          
        // 表单开始标签  
        Map<String, Object> formConfig = (Map<String, Object>) renderData.get("formConfig");  
        String labelWidth = (String) formConfig.get("labelWidth");  
        String size = (String) formConfig.get("size");  
        boolean inline = (boolean) formConfig.get("inline");  
          
        html.append("<el-form");  
        html.append(" ref=\"form\"");  
        html.append(" :model=\"form\"");  
        html.append(" :rules=\"rules\"");  
        html.append(" label-width=\"").append(labelWidth).append("\"");  
        if (!"default".equals(size)) {  
            html.append(" size=\"").append(size).append("\"");  
        }  
        if (inline) {  
            html.append(" inline");  
        }  
        html.append(">\n");  
          
        // 表单字段  
        List<Map<String, Object>> fields = (List<Map<String, Object>>) renderData.get("fields");  
        List<Map<String, Object>> layout = (List<Map<String, Object>>) renderData.get("layout");  
          
        // 如果有布局配置，按布局生成  
        if (layout != null && !layout.isEmpty()) {  
            html.append(generateFormItemsByLayout(fields, layout));  
        } else {  
            // 否则按字段顺序生成  
            html.append(generateFormItemsByFields(fields));  
        }  
          
        // 表单按钮  
        html.append("  <el-form-item>\n");  
        html.append("    <el-button type=\"primary\" @click=\"submitForm\">提交</el-button>\n");  
        html.append("    <el-button @click=\"resetForm\">重置</el-button>\n");  
        html.append("  </el-form-item>\n");  
          
        // 表单结束标签  
        html.append("</el-form>");  
          
        return AjaxResult.success(html.toString());  
    }  
      
    /**  
     * 按布局生成表单项  
     */  
    private String generateFormItemsByLayout(List<Map<String, Object>> fields, List<Map<String, Object>> layout) {  
        StringBuilder html = new StringBuilder();  
          
        // 创建字段映射，方便查找  
        Map<String, Map<String, Object>> fieldMap = fields.stream()  
                .collect(Collectors.toMap(  
                        field -> (String) field.get("fieldId"),  
                        field -> field  
                ));  
          
        // 按布局顺序生成表单项  
        for (Map<String, Object> item : layout) {  
            String fieldId = (String) item.get("fieldId");  
            Integer span = (Integer) item.get("span");  
            Integer offset = (Integer) item.get("offset");  
              
            Map<String, Object> field = fieldMap.get(fieldId);  
            if (field == null) {  
                continue;  
            }  
              
            // 生成表单项  
            if (span != 24) {  
                html.append("  <el-col :span=\"").append(span).append("\"");  
                if (offset > 0) {  
                    html.append(" :offset=\"").append(offset).append("\"");  
                }  
                html.append(">\n");  
                html.append("    ").append(generateFormItem(field)).append("\n");  
                html.append("  </el-col>\n");  
            } else {  
                html.append("  ").append(generateFormItem(field)).append("\n");  
            }  
        }  
          
        return html.toString();  
    }  
      
    /**  
     * 按字段顺序生成表单项  
     */  
    private String generateFormItemsByFields(List<Map<String, Object>> fields) {  
        StringBuilder html = new StringBuilder();  
          
        // 按字段顺序生成表单项  
        for (Map<String, Object> field : fields) {  
            html.append("  ").append(generateFormItem(field)).append("\n");  
        }  
          
        return html.toString();  
    }  
      
    /**  
     * 生成单个表单项  
     */  
    private String generateFormItem(Map<String, Object> field) {  
        String fieldName = (String) field.get("fieldName");  
        String fieldLabel = (String) field.get("fieldLabel");  
        String htmlType = (String) field.get("htmlType");  
        boolean required = (boolean) field.get("required");  
          
        StringBuilder html = new StringBuilder();  
          
        // 表单项开始标签  
        html.append("<el-form-item");  
        html.append(" label=\"").append(fieldLabel).append("\"");  
        html.append(" prop=\"").append(fieldName).append("\"");  
        if (required) {  
            html.append(" required");  
        }  
        html.append(">");  
          
        // 表单控件  
        switch (htmlType) {  
            case "input":  
                html.append("<el-input v-model=\"form.").append(fieldName).append("\" placeholder=\"请输入").append(fieldLabel).append("\" />");  
                break;  
            case "textarea":  
                html.append("<el-input v-model=\"form.").append(fieldName).append("\" type=\"textarea\" placeholder=\"请输入").append(fieldLabel).append("\" />");  
                break;  
            case "select":  
                String dictType = (String) field.get("dictType");  
                if (StringUtils.isNotEmpty(dictType)) {  
                    html.append("<el-select v-model=\"form.").append(fieldName).append("\" placeholder=\"请选择").append(fieldLabel).append("\">");  
                    html.append("<el-option v-for=\"dict in ").append(fieldName).append("Options\" :key=\"dict.dictValue\" :label=\"dict.dictLabel\" :value=\"dict.dictValue\" />");  
                    html.append("</el-select>");  
                } else {  
                    html.append("<el-select v-model=\"form.").append(fieldName).append("\" placeholder=\"请选择").append(fieldLabel).append("\">");  
                    html.append("<el-option label=\"请选择字典生成\" value=\"\" />");  
                    html.append("</el-select>");  
                }  
                break;  
            case "radio":  
                String dictTypeRadio = (String) field.get("dictType");  
                if (StringUtils.isNotEmpty(dictTypeRadio)) {  
                    html.append("<el-radio-group v-model=\"form.").append(fieldName).append("\">");  
                    html.append("<el-radio v-for=\"dict in ").append(fieldName).append("Options\" :key=\"dict.dictValue\" :label=\"dict.dictValue\">{{dict.dictLabel}}</el-radio>");  
                    html.append("</el-radio-group>");  
                } else {  
                    html.append("<el-radio-group v-model=\"form.").append(fieldName).append("\">");  
                    html.append("<el-radio label=\"1\">请选择字典生成</el-radio>");  
                    html.append("</el-radio-group>");  
                }  
                break;  
            case "checkbox":  
                String dictTypeCheckbox = (String) field.get("dictType");  
                if (StringUtils.isNotEmpty(dictTypeCheckbox)) {  
                    html.append("<el-checkbox-group v-model=\"form.").append(fieldName).append("\">");  
                    html.append("<el-checkbox v-for=\"dict in ").append(fieldName).append("Options\" :key=\"dict.dictValue\" :label=\"dict.dictValue\">{{dict.dictLabel}}</el-checkbox>");  
                    html.append("</el-checkbox-group>");  
                } else {  
                    html.append("<el-checkbox-group v-model=\"form.").append(fieldName).append("\">");  
                    html.append("<el-checkbox label=\"1\">请选择字典生成</el-checkbox>");  
                    html.append("</el-checkbox-group>");  
                }  
                break;  
            case "datetime":  
                html.append("<el-date-picker v-model=\"form.").append(fieldName).append("\" type=\"datetime\" placeholder=\"选择日期时间\" />");  
                break;  
            case "date":  
                html.append("<el-date-picker v-model=\"form.").append(fieldName).append("\" type=\"date\" placeholder=\"选择日期\" />");  
                break;  
            case "number":  
                html.append("<el-input-number v-model=\"form.").append(fieldName).append("\" :min=\"0\" />");  
                break;  
            default:  
                html.append("<el-input v-model=\"form.").append(fieldName).append("\" placeholder=\"请输入").append(fieldLabel).append("\" />");  
                break;  
        }  
          
        // 表单项结束标签  
        html.append("</el-form-item>");  
          
        return html.toString();  
    }  
      
    /**  
     * 生成表单数据模型  
     *   
     * @param metadataId 元数据ID  
     * @return 表单数据模型  
     */  
    public AjaxResult generateFormModel(String metadataId) {  
        if (StringUtils.isEmpty(metadataId)) {  
            return AjaxResult.error("元数据ID不能为空");  
        }  
          
        // 获取字段元数据  
        List<FormFieldMetadata> fields = fieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);  
        if (fields == null || fields.isEmpty()) {  
            return AjaxResult.error("表单字段元数据不存在");  
        }  
          
        // 构建数据模型  
        Map<String, Object> model = new HashMap<>();  
          
        for (FormFieldMetadata field : fields) {  
            String fieldName = field.getFieldName();  
            String fieldType = field.getFieldType();  
              
            // 根据字段类型设置默认值  
            switch (fieldType) {  
                case "string":  
                    model.put(fieldName, "");  
                    break;  
                case "number":  
                    model.put(fieldName, 0);  
                    break;  
                case "boolean":  
                    model.put(fieldName, false);  
                    break;  
                case "date":  
                case "datetime":  
                    model.put(fieldName, null);  
                    break;  
                case "array":  
                    model.put(fieldName, new ArrayList<>());  
                    break;  
                case "object":  
                    model.put(fieldName, new HashMap<>());  
                    break;  
                default:  
                    model.put(fieldName, null);  
                    break;  
            }  
        }  
          
        return AjaxResult.success(model);  
    }  
}