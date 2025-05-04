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
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.formdata.service.FormDataService;

/**
 * 表单数据Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/formdata")
public class FormDataController extends BaseController {
    @Autowired
    private FormDataService formDataService;

    /**
     * 查询表单数据列表
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:list')")
    @GetMapping("/list/{metadataId}")
    public TableDataInfo list(@PathVariable("metadataId") String metadataId, @RequestBody(required = false) Map<String, Object> params) {
        startPage();
        List<Map<String, Object>> list = formDataService.selectFormDataList(metadataId, params);
        return getDataTable(list);
    }

    /**
     * 获取表单数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:query')")
    @GetMapping(value = "/{metadataId}/{dataId}")
    public AjaxResult getInfo(@PathVariable("metadataId") String metadataId, @PathVariable("dataId") String dataId) {
        return AjaxResult.success(formDataService.selectFormDataById(metadataId, dataId));
    }

    /**
     * 新增表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:add')")
    @Log(title = "表单数据", businessType = BusinessType.INSERT)
    @PostMapping("/{metadataId}")
    public AjaxResult add(@PathVariable("metadataId") String metadataId, @RequestBody Map<String, Object> data) {
        return toAjax(formDataService.insertFormData(metadataId, data));
    }

    /**
     * 修改表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:edit')")
    @Log(title = "表单数据", businessType = BusinessType.UPDATE)
    @PutMapping("/{metadataId}")
    public AjaxResult edit(@PathVariable("metadataId") String metadataId, @RequestBody Map<String, Object> data) {
        return toAjax(formDataService.updateFormData(metadataId, data));
    }

    /**
     * 删除表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:remove')")
    @Log(title = "表单数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{metadataId}/{dataId}")
    public AjaxResult remove(@PathVariable("metadataId") String metadataId, @PathVariable("dataId") String dataId) {
        return toAjax(formDataService.deleteFormDataById(metadataId, dataId));
    }

    /**
     * 批量删除表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:remove')")
    @Log(title = "表单数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{metadataId}/batch/{dataIds}")
    public AjaxResult batchRemove(@PathVariable("metadataId") String metadataId, @PathVariable("dataIds") String[] dataIds) {
        return toAjax(formDataService.deleteFormDataByIds(metadataId, dataIds));
    }

    /**
     * 导出表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:export')")
    @Log(title = "表单数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export/{metadataId}")
    public AjaxResult export(@PathVariable("metadataId") String metadataId, @RequestBody(required = false) Map<String, Object> params) {
        List<Map<String, Object>> list = formDataService.exportFormData(metadataId, params);
        return AjaxResult.success(list);
    }

    /**
     * 导入表单数据
     */
    @PreAuthorize("@ss.hasPermi('formdata:data:import')")
    @Log(title = "表单数据", businessType = BusinessType.IMPORT)
    @PostMapping("/import/{metadataId}/{updateSupport}")
    public AjaxResult importData(@PathVariable("metadataId") String metadataId, @PathVariable("updateSupport") boolean updateSupport, @RequestBody List<Map<String, Object>> dataList) {
        String message = formDataService.importFormData(metadataId, dataList, updateSupport);
        return AjaxResult.success(message);
    }
}