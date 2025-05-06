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

import com.ruoyi.metadata.domain.FormViewConfig;
import com.ruoyi.metadata.service.IFormViewConfigService;

/**
 * 表单视图配置Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/metadata/view")
public class FormViewConfigController extends BaseController {
    @Autowired
    private IFormViewConfigService IFormViewConfigService;

    /**
     * 查询表单视图配置列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:view:list')")
    @GetMapping("/list")
    public TableDataInfo list(FormViewConfig formViewConfig) {
        startPage();
        List<FormViewConfig> list = IFormViewConfigService.selectFormViewConfigList(formViewConfig);
        return getDataTable(list);
    }

    /**
     * 根据元数据ID查询视图配置列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:view:list')")
    @GetMapping("/listByMetadataId/{metadataId}")
    public AjaxResult listByMetadataId(@PathVariable("metadataId") String metadataId) {
        List<FormViewConfig> list = IFormViewConfigService.selectFormViewConfigByMetadataId(metadataId);
        return AjaxResult.success(list);
    }

    /**
     * 根据元数据ID和视图类型查询默认视图
     */
    @PreAuthorize("@ss.hasPermi('metadata:view:query')")
    @GetMapping("/default/{metadataId}/{viewType}")
    public AjaxResult getDefaultView(@PathVariable("metadataId") String metadataId, @PathVariable("viewType") String viewType) {
        FormViewConfig viewConfig = IFormViewConfigService.selectDefaultFormViewConfig(metadataId, viewType);
        return AjaxResult.success(viewConfig);
    }

    /**
     * 获取表单视图配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('metadata:view:query')")
    @GetMapping(value = "/{viewId}")
    public AjaxResult getInfo(@PathVariable("viewId") String viewId) {
        return AjaxResult.success(IFormViewConfigService.selectFormViewConfigByViewId(viewId));
    }

    /**
     * 新增表单视图配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:view:add')")
    @Log(title = "表单视图配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FormViewConfig formViewConfig) {
        return toAjax(IFormViewConfigService.insertFormViewConfig(formViewConfig));
    }

    /**
     * 修改表单视图配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:view:edit')")
    @Log(title = "表单视图配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FormViewConfig formViewConfig) {
        return toAjax(IFormViewConfigService.updateFormViewConfig(formViewConfig));
    }

    /**
     * 删除表单视图配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:view:remove')")
    @Log(title = "表单视图配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{viewIds}")
    public AjaxResult remove(@PathVariable String[] viewIds) {
        return toAjax(IFormViewConfigService.deleteFormViewConfigByViewIds(viewIds));
    }

    /**
     * 设置默认视图
     */
    @PreAuthorize("@ss.hasPermi('metadata:view:edit')")
    @Log(title = "表单视图配置", businessType = BusinessType.UPDATE)
    @PutMapping("/setDefault/{viewId}")
    public AjaxResult setDefault(@PathVariable("viewId") String viewId) {
        return toAjax(IFormViewConfigService.setDefaultFormViewConfig(viewId));
    }
}