package com.ruoyi.metadata.controller;

import java.util.List;
import java.util.Map;

import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.metadata.domain.FormRule;
import com.ruoyi.metadata.domain.FormRuleAction;
import com.ruoyi.metadata.domain.FormRuleCondition;
import com.ruoyi.metadata.service.IFormRuleService;

/**
 * 表单规则Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/metadata/rule")
public class FormRuleController extends BaseController {
    @Autowired
    private IFormRuleService IFormRuleService;

    /**
     * 查询表单规则列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(FormRule formRule) {
        startPage();
        List<FormRule> list = IFormRuleService.selectFormRuleList(formRule);
        return getDataTable(list);
    }

    /**
     * 根据元数据ID查询规则列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:list')")
    @GetMapping("/listByMetadataId/{metadataId}")
    public AjaxResult listByMetadataId(@PathVariable("metadataId") String metadataId) {
        List<FormRule> list = IFormRuleService.selectFormRuleByMetadataId(metadataId);
        return AjaxResult.success(list);
    }

    /**
     * 获取表单规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:query')")
    @GetMapping(value = "/{ruleId}")
    public AjaxResult getInfo(@PathVariable("ruleId") String ruleId) {
        return AjaxResult.success(IFormRuleService.selectFormRuleByRuleId(ruleId));
    }

    /**
     * 获取表单规则详情，包含条件和动作
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:query')")
    @GetMapping(value = "/detail/{ruleId}")
    public AjaxResult getDetail(@PathVariable("ruleId") String ruleId) {
        return AjaxResult.success(IFormRuleService.getFormRuleDetail(ruleId));
    }

    /**
     * 新增表单规则
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:add')")
    @Log(title = "表单规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FormRule formRule) {
        return toAjax(IFormRuleService.insertFormRule(formRule));
    }

    /**
     * 修改表单规则
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:edit')")
    @Log(title = "表单规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FormRule formRule) {
        return toAjax(IFormRuleService.updateFormRule(formRule));
    }

    /**
     * 删除表单规则
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:remove')")
    @Log(title = "表单规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable String[] ruleIds) {
        return toAjax(IFormRuleService.deleteFormRuleByRuleIds(ruleIds));
    }

    /**
     * 启用规则
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:edit')")
    @Log(title = "表单规则", businessType = BusinessType.UPDATE)
    @PutMapping("/enable/{ruleId}")
    public AjaxResult enable(@PathVariable("ruleId") String ruleId) {
        return toAjax(IFormRuleService.enableFormRule(ruleId));
    }

    /**
     * 禁用规则
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:edit')")
    @Log(title = "表单规则", businessType = BusinessType.UPDATE)
    @PutMapping("/disable/{ruleId}")
    public AjaxResult disable(@PathVariable("ruleId") String ruleId) {
        return toAjax(IFormRuleService.disableFormRule(ruleId));
    }

    /**
     * 保存规则详情，包含条件和动作
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:edit')")
    @Log(title = "表单规则", businessType = BusinessType.UPDATE)
    @PostMapping("/saveDetail/{ruleId}")
    public AjaxResult saveDetail(@PathVariable("ruleId") String ruleId,
                                 @RequestBody Map<String, Object> detail) {
        @SuppressWarnings("unchecked")
        List<FormRuleCondition> conditions = (List<FormRuleCondition>) detail.get("conditions");
        @SuppressWarnings("unchecked")
        List<FormRuleAction> actions = (List<FormRuleAction>) detail.get("actions");

        return toAjax(IFormRuleService.saveFormRuleDetail(ruleId, conditions, actions));
    }

    /**
     * 执行规则
     */
    @PreAuthorize("@ss.hasPermi('metadata:rule:execute')")
    @PostMapping("/execute/{metadataId}/{triggerPoint}")
    public AjaxResult execute(@PathVariable("metadataId") String metadataId,
                              @PathVariable("triggerPoint") String triggerPoint,
                              @RequestBody Map<String, Object> formData) {
        return AjaxResult.success(IFormRuleService.executeRules(metadataId, formData, triggerPoint));
    }
}