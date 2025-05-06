package com.ruoyi.formdata.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.formdata.service.IFormDataService;
import com.ruoyi.formdata.service.IFormImportExportService;
import com.ruoyi.formdata.service.IFormQueryService;
import com.ruoyi.formdata.service.IRuleExecutionEngine;
import com.ruoyi.formdata.service.ITableGenerationService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 表单数据 控制层
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/formdata")
public class FormDataController extends BaseController
{
    @Autowired
    private IFormDataService formDataService;

    @Autowired
    private IFormQueryService formQueryService;

    @Autowired
    private IFormImportExportService formImportExportService;

    @Autowired
    private ITableGenerationService tableGenerationService;

    @Autowired
    private IRuleExecutionEngine ruleExecutionEngine;

    /**
     * 查询表单数据列表
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:list')")
    @GetMapping("/list/{metadataId}")
    public TableDataInfo list(@PathVariable("metadataId") String metadataId, @RequestParam Map<String, Object> params)
    {
        startPage();
        // 添加当前租户ID
        params.put("tenantId", SecurityUtils.getCurrComId());
        List<Map<String, Object>> list = formQueryService.queryFormData(metadataId, params);
        return getDataTable(list);
    }

    /**
     * 获取表单数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:query')")
    @GetMapping(value = "/{metadataId}/{id}")
    public AjaxResult getInfo(@PathVariable("metadataId") String metadataId, @PathVariable("id") String id)
    {
        Map<String, Object> data = formQueryService.queryFormDataById(metadataId, id);
        return AjaxResult.success(data);
    }

    /**
     * 新增表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:add')")
    @Log(title = "表单数据", businessType = BusinessType.INSERT)
    @PostMapping("/{metadataId}")
    public AjaxResult add(@PathVariable("metadataId") String metadataId, @RequestBody Map<String, Object> formData)
    {
        // 添加创建者信息
        formData.put("createBy", SecurityUtils.getUsername());

        // 执行表单提交前规则
        Map<String, Object> ruleResult = ruleExecutionEngine.executeSubmitRules(metadataId, formData);

        // 验证规则是否通过
        if (!(Boolean) ruleResult.getOrDefault("isValid", true))
        {
            @SuppressWarnings("unchecked")
            List<String> errors = (List<String>) ruleResult.get("errors");
            return AjaxResult.error(StringUtils.join(errors, "\n"));
        }

        // 更新表单数据
        if (ruleResult.containsKey("formData"))
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> updatedFormData = (Map<String, Object>) ruleResult.get("formData");
            formData.putAll(updatedFormData);
        }

        // 保存表单数据
        int id = formDataService.insertFormData(metadataId, formData);

        // 执行表单提交后规则
        ruleExecutionEngine.executeAfterSubmitRules(metadataId, formData);

        return AjaxResult.success("新增成功", id);
    }

    /**
     * 修改表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:edit')")
    @Log(title = "表单数据", businessType = BusinessType.UPDATE)
    @PutMapping("/{metadataId}/{id}")
    public AjaxResult edit(@PathVariable("metadataId") String metadataId, @PathVariable("id") String id,
                           @RequestBody Map<String, Object> formData)
    {
        // 添加更新者信息
        formData.put("updateBy", SecurityUtils.getUsername());

        // 执行表单提交前规则
        Map<String, Object> ruleResult = ruleExecutionEngine.executeSubmitRules(metadataId, formData);

        // 验证规则是否通过
        if (!(Boolean) ruleResult.getOrDefault("isValid", true))
        {
            @SuppressWarnings("unchecked")
            List<String> errors = (List<String>) ruleResult.get("errors");
            return AjaxResult.error(StringUtils.join(errors, "\n"));
        }

        // 更新表单数据
        if (ruleResult.containsKey("formData"))
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> updatedFormData = (Map<String, Object>) ruleResult.get("formData");
            formData.putAll(updatedFormData);
        }

        // 保存表单数据
        formDataService.updateFormData(metadataId, formData);

        // 执行表单提交后规则
        ruleExecutionEngine.executeAfterSubmitRules(metadataId, formData);

        return AjaxResult.success();
    }

    /**
     * 删除表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:remove')")
    @Log(title = "表单数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{metadataId}/{ids}")
    public AjaxResult remove(@PathVariable("metadataId") String metadataId, @PathVariable String[] ids)
    {
        return toAjax(formDataService.deleteFormDataByIds(metadataId, ids));
    }

    /**
     * 导出表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:export')")
    @Log(title = "表单数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export/{metadataId}")
    public void export(@PathVariable("metadataId") String metadataId, @RequestParam Map<String, Object> params,
                       HttpServletResponse response) throws IOException
    {
        // 添加当前租户ID
        params.put("tenantId", SecurityUtils.getCurrComId());

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=formdata.xlsx");

        // 导出数据
        formImportExportService.exportToExcel(metadataId, params, response.getOutputStream());
    }

    /**
     * 下载导入模板
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:import')")
    @GetMapping("/importTemplate/{metadataId}")
    public void importTemplate(@PathVariable("metadataId") String metadataId, HttpServletResponse response) throws IOException
    {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=template.xlsx");

        // 导出模板
        formImportExportService.exportTemplate(metadataId, response.getOutputStream());
    }

    /**
     * 导入表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:import')")
    @Log(title = "表单数据", businessType = BusinessType.IMPORT)
    @PostMapping("/import/{metadataId}")
    public AjaxResult importData(@PathVariable("metadataId") String metadataId, MultipartFile file, boolean updateSupport) throws Exception
    {
        // 导入数据
        Map<String, Object> result = formImportExportService.importFromExcel(metadataId, file.getInputStream(), updateSupport);

        return AjaxResult.success(result);
    }

    /**
     * 生成租户数据表
     */
    @PreAuthorize("@ss.hasPermi('formdata:table:generate')")
    @Log(title = "租户数据表", businessType = BusinessType.INSERT)
    @PostMapping("/table/{metadataId}")
    public AjaxResult generateTable(@PathVariable("metadataId") String metadataId)
    {
        // 获取当前租户ID
        String tenantId = SecurityUtils.getCurrComId();

        // 生成表
        boolean result = tableGenerationService.generateTable(metadataId, tenantId);

        return result ? AjaxResult.success() : AjaxResult.error("生成表失败");
    }

    /**
     * 更新租户数据表结构
     */
    @PreAuthorize("@ss.hasPermi('formdata:table:update')")
    @Log(title = "租户数据表", businessType = BusinessType.UPDATE)
    @PutMapping("/table/{metadataId}")
    public AjaxResult updateTableStructure(@PathVariable("metadataId") String metadataId)
    {
        // 获取当前租户ID
        String tenantId = SecurityUtils.getCurrComId();

        // 更新表结构
        boolean result = tableGenerationService.updateTableStructure(metadataId, tenantId);

        return result ? AjaxResult.success() : AjaxResult.error("更新表结构失败");
    }

    /**
     * 删除租户数据表
     */
    @PreAuthorize("@ss.hasPermi('formdata:table:remove')")
    @Log(title = "租户数据表", businessType = BusinessType.DELETE)
    @DeleteMapping("/table/{metadataId}")
    public AjaxResult dropTable(@PathVariable("metadataId") String metadataId)
    {
        // 获取当前租户ID
        String tenantId = SecurityUtils.getCurrComId();

        // 删除表
        boolean result = tableGenerationService.dropTable(metadataId, tenantId);

        return result ? AjaxResult.success() : AjaxResult.error("删除表失败");
    }

    /**
     * 执行表单加载规则
     */
    @GetMapping("/rule/load/{metadataId}")
    public AjaxResult executeLoadRules(@PathVariable("metadataId") String metadataId, @RequestParam Map<String, Object> formData)
    {
        // 执行表单加载规则
        Map<String, Object> result = ruleExecutionEngine.executeLoadRules(metadataId, formData);

        return AjaxResult.success(result);
    }

    /**
     * 执行字段值变化规则
     */
    @PostMapping("/rule/fieldChange/{metadataId}/{fieldName}")
    public AjaxResult executeFieldChangeRules(@PathVariable("metadataId") String metadataId,
                                              @PathVariable("fieldName") String fieldName, @RequestBody Map<String, Object> params)
    {
        Object oldValue = params.get("oldValue");
        Object newValue = params.get("newValue");
        @SuppressWarnings("unchecked")
        Map<String, Object> formData = (Map<String, Object>) params.get("formData");

        if (formData == null)
        {
            formData = new HashMap<>();
        }

        // 执行字段值变化规则
        Map<String, Object> result = ruleExecutionEngine.executeFieldChangeRules(metadataId, fieldName, oldValue, newValue, formData);

        return AjaxResult.success(result);
    }
}