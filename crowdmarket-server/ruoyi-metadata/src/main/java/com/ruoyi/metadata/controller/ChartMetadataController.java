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

import com.ruoyi.metadata.domain.ChartConfig;
import com.ruoyi.metadata.domain.ChartMetadata;
import com.ruoyi.metadata.service.ChartMetadataService;

/**
 * 图表元数据Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/metadata/chart")
public class ChartMetadataController extends BaseController {
    @Autowired
    private ChartMetadataService chartMetadataService;

    /**
     * 查询图表元数据列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChartMetadata chartMetadata) {
        startPage();
        List<ChartMetadata> list = chartMetadataService.selectChartMetadataList(chartMetadata);
        return getDataTable(list);
    }

    /**
     * 根据元数据ID查询图表列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:list')")
    @GetMapping("/listByMetadataId/{metadataId}")
    public AjaxResult listByMetadataId(@PathVariable("metadataId") String metadataId) {
        List<ChartMetadata> list = chartMetadataService.selectChartMetadataByMetadataId(metadataId);
        return AjaxResult.success(list);
    }

    /**
     * 获取图表元数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:query')")
    @GetMapping(value = "/{chartId}")
    public AjaxResult getInfo(@PathVariable("chartId") String chartId) {
        return AjaxResult.success(chartMetadataService.selectChartMetadataByChartId(chartId));
    }

    /**
     * 获取图表详情，包含配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:query')")
    @GetMapping(value = "/detail/{chartId}")
    public AjaxResult getDetail(@PathVariable("chartId") String chartId) {
        return AjaxResult.success(chartMetadataService.getChartDetail(chartId));
    }

    /**
     * 新增图表元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:add')")
    @Log(title = "图表元数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChartMetadata chartMetadata) {
        return toAjax(chartMetadataService.insertChartMetadata(chartMetadata));
    }

    /**
     * 修改图表元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:edit')")
    @Log(title = "图表元数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChartMetadata chartMetadata) {
        return toAjax(chartMetadataService.updateChartMetadata(chartMetadata));
    }

    /**
     * 删除图表元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:remove')")
    @Log(title = "图表元数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{chartIds}")
    public AjaxResult remove(@PathVariable String[] chartIds) {
        return toAjax(chartMetadataService.deleteChartMetadataByChartIds(chartIds));
    }

    /**
     * 发布图表
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:edit')")
    @Log(title = "图表元数据", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{chartId}")
    public AjaxResult publish(@PathVariable("chartId") String chartId) {
        return toAjax(chartMetadataService.publishChart(chartId));
    }

    /**
     * 停用图表
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:edit')")
    @Log(title = "图表元数据", businessType = BusinessType.UPDATE)
    @PutMapping("/disable/{chartId}")
    public AjaxResult disable(@PathVariable("chartId") String chartId) {
        return toAjax(chartMetadataService.disableChart(chartId));
    }

    /**
     * 保存图表配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:edit')")
    @Log(title = "图表配置", businessType = BusinessType.UPDATE)
    @PostMapping("/config/{chartId}")
    public AjaxResult saveConfig(@PathVariable("chartId") String chartId, @RequestBody ChartConfig chartConfig) {
        return toAjax(chartMetadataService.saveChartConfig(chartId, chartConfig));
    }

    /**
     * 获取图表数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:chart:query')")
    @PostMapping("/data/{chartId}")
    public AjaxResult getData(@PathVariable("chartId") String chartId, @RequestBody(required = false) Map<String, Object> params) {
        return AjaxResult.success(chartMetadataService.getChartData(chartId, params));
    }
}