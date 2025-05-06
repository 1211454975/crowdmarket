package com.ruoyi.metadata.controller;

import java.util.List;

import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.metadata.domain.ChartConfig;
import com.ruoyi.metadata.service.IChartConfigService;


/**
 * 图表配置 控制层
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/metadata/chartconfig")
public class ChartConfigController extends BaseController
{
    @Autowired
    private IChartConfigService chartConfigService;

    /**
     * 查询图表配置列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:chartconfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChartConfig chartConfig)
    {
        startPage();
        List<ChartConfig> list = chartConfigService.selectChartConfigList(chartConfig);
        return getDataTable(list);
    }

    /**
     * 获取图表配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('metadata:chartconfig:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") String configId)
    {
        return AjaxResult.success(chartConfigService.selectChartConfigById(configId));
    }

    /**
     * 根据图表ID获取图表配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:chartconfig:query')")
    @GetMapping(value = "/chart/{chartId}")
    public AjaxResult getConfigByChartId(@PathVariable("chartId") String chartId)
    {
        ChartConfig chartConfig = new ChartConfig();
        chartConfig.setChartId(chartId);
        List<ChartConfig> configs = chartConfigService.selectChartConfigList(chartConfig);
        return AjaxResult.success(configs.isEmpty() ? null : configs.get(0));
    }

    /**
     * 新增图表配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:chartconfig:add')")
    @Log(title = "图表配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChartConfig chartConfig)
    {
//        chartConfig.sesetCreateBy(SecurityUtils.getUsername());
        return toAjax(chartConfigService.insertChartConfig(chartConfig));
    }

    /**
     * 修改图表配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:chartconfig:edit')")
    @Log(title = "图表配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChartConfig chartConfig)
    {
//        chartConfig.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(chartConfigService.updateChartConfig(chartConfig));
    }

    /**
     * 删除图表配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:chartconfig:remove')")
    @Log(title = "图表配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable String[] configIds)
    {
        return toAjax(chartConfigService.deleteChartConfigByIds(configIds));
    }

    /**
     * 根据图表ID删除图表配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:chartconfig:remove')")
    @Log(title = "图表配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/chart/{chartId}")
    public AjaxResult removeByChartId(@PathVariable String chartId)
    {
        return toAjax(chartConfigService.deleteChartConfigByChartId(chartId));
    }
}