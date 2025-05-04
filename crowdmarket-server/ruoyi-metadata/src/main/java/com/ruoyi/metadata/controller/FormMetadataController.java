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

import com.ruoyi.metadata.domain.FormMetadata;
import com.ruoyi.metadata.service.FormMetadataService;

/**
 * 表单元数据Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/metadata/form")
public class FormMetadataController extends BaseController {
    @Autowired
    private FormMetadataService formMetadataService;

    /**
     * 查询表单元数据列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:form:list')")
    @GetMapping("/list")
    public TableDataInfo list(FormMetadata formMetadata) {
        startPage();
        List<FormMetadata> list = formMetadataService.selectFormMetadataList(formMetadata);
        return getDataTable(list);
    }

    /**
     * 获取表单元数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('metadata:form:query')")
    @GetMapping(value = "/{metadataId}")
    public AjaxResult getInfo(@PathVariable("metadataId") String metadataId) {
        return AjaxResult.success(formMetadataService.selectFormMetadataByMetadataId(metadataId));
    }

    /**
     * 新增表单元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:form:add')")
    @Log(title = "表单元数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FormMetadata formMetadata) {
        return toAjax(formMetadataService.insertFormMetadata(formMetadata));
    }

    /**
     * 修改表单元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:form:edit')")
    @Log(title = "表单元数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FormMetadata formMetadata) {
        return toAjax(formMetadataService.updateFormMetadata(formMetadata));
    }

    /**
     * 删除表单元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:form:remove')")
    @Log(title = "表单元数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{metadataIds}")
    public AjaxResult remove(@PathVariable String[] metadataIds) {
        return toAjax(formMetadataService.deleteFormMetadataByMetadataIds(metadataIds));
    }

    /**
     * 发布表单元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:form:publish')")
    @Log(title = "表单元数据", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{metadataId}")
    public AjaxResult publish(@PathVariable("metadataId") String metadataId) {
        return toAjax(formMetadataService.publishFormMetadata(metadataId));
    }

    /**
     * 停用表单元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:form:disable')")
    @Log(title = "表单元数据", businessType = BusinessType.UPDATE)
    @PutMapping("/disable/{metadataId}")
    public AjaxResult disable(@PathVariable("metadataId") String metadataId) {
        return toAjax(formMetadataService.disableFormMetadata(metadataId));
    }
}