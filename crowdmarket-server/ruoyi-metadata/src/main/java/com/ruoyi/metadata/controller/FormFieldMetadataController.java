package com.ruoyi.metadata.controller;

import java.util.List;

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
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.service.FormFieldMetadataService;

/**
 * 表单字段元数据Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/metadata/field")
public class FormFieldMetadataController extends BaseController {
    @Autowired
    private FormFieldMetadataService formFieldMetadataService;

    /**
     * 查询表单字段元数据列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:field:list')")
    @GetMapping("/list")
    public TableDataInfo list(FormFieldMetadata formFieldMetadata) {
        startPage();
        List<FormFieldMetadata> list = formFieldMetadataService.selectFormFieldMetadataList(formFieldMetadata);
        return getDataTable(list);
    }

    /**
     * 根据元数据ID查询字段列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:field:list')")
    @GetMapping("/listByMetadataId/{metadataId}")
    public AjaxResult listByMetadataId(@PathVariable("metadataId") String metadataId) {
        List<FormFieldMetadata> list = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        return AjaxResult.success(list);
    }

    /**
     * 获取表单字段元数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('metadata:field:query')")
    @GetMapping(value = "/{fieldId}")
    public AjaxResult getInfo(@PathVariable("fieldId") String fieldId) {
        return AjaxResult.success(formFieldMetadataService.selectFormFieldMetadataByFieldId(fieldId));
    }

    /**
     * 新增表单字段元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:field:add')")
    @Log(title = "表单字段元数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FormFieldMetadata formFieldMetadata) {
        return toAjax(formFieldMetadataService.insertFormFieldMetadata(formFieldMetadata));
    }

    /**
     * 修改表单字段元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:field:edit')")
    @Log(title = "表单字段元数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FormFieldMetadata formFieldMetadata) {
        return toAjax(formFieldMetadataService.updateFormFieldMetadata(formFieldMetadata));
    }

    /**
     * 删除表单字段元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:field:remove')")
    @Log(title = "表单字段元数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fieldIds}")
    public AjaxResult remove(@PathVariable String[] fieldIds) {
        return toAjax(formFieldMetadataService.deleteFormFieldMetadataByFieldIds(fieldIds));
    }

    /**
     * 批量保存字段元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:field:edit')")
    @Log(title = "表单字段元数据", businessType = BusinessType.UPDATE)
    @PostMapping("/batchSave/{metadataId}")
    public AjaxResult batchSave(@PathVariable("metadataId") String metadataId, @RequestBody List<FormFieldMetadata> fieldList) {
        return toAjax(formFieldMetadataService.batchSaveFormFieldMetadata(metadataId, fieldList));
    }
}