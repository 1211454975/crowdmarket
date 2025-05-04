package com.ruoyi.tenant.controller;

import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.framework.domain.TenantDatabase;
import com.ruoyi.framework.service.ITenantDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/tenant/database")
public class TenantDatabaseController extends BaseController {

    @Autowired
    private ITenantDatabaseService tenantDatabaseService;

    /**
     * 查询租户数据库配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:tenant:list')")
    @GetMapping("/list")
    public TableDataInfo list(TenantDatabase tenantDatabase) {
        startPage();
        List<TenantDatabase> list = tenantDatabaseService.selectTenantDatabaseList(tenantDatabase);
        return getDataTable(list);
    }

    /**
     * 获取租户数据库配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:tenant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(tenantDatabaseService.selectTenantDatabaseById(Long.parseLong(id)));
    }
    @PreAuthorize("@ss.hasPermi('system:tenant:query')")
    @GetMapping(value = "/ds")
    public AjaxResult getTenantDataSource() {
        String tenantId = SecurityUtils.getCurrComId();
        return AjaxResult.success(tenantDatabaseService.getTenantDataSource(tenantId));
    }
    /**
     * 新增租户数据库配置
     */
    @PreAuthorize("@ss.hasPermi('system:tenant:add')")
    @Log(title = "租户数据库配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TenantDatabase tenantDatabase) {
        return toAjax(tenantDatabaseService.insertTenantDatabase(tenantDatabase));
    }

    /**
     * 修改租户数据库配置
     */
    @PreAuthorize("@ss.hasPermi('system:tenant:edit')")
    @Log(title = "租户数据库配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TenantDatabase tenantDatabase) {
        return toAjax(tenantDatabaseService.updateTenantDatabase(tenantDatabase));
    }

    /**
     * 删除租户数据库配置
     */
    @PreAuthorize("@ss.hasPermi('system:tenant:remove')")
    @Log(title = "租户数据库配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(tenantDatabaseService.deleteTenantDatabaseByIds(ids));
    }

    /**
     * 创建租户数据库
     */
    @PreAuthorize("@ss.hasPermi('system:tenant:create')")
    @Log(title = "租户数据库", businessType = BusinessType.INSERT)
    @PostMapping("/create/{tenantId}")
    public AjaxResult createDatabase(@PathVariable("tenantId") String tenantId) {
        tenantDatabaseService.createTenantDatabase(tenantId);
        return AjaxResult.success();
    }

    /**
     * 测试数据库连接
     */
    @PreAuthorize("@ss.hasPermi('system:tenant:test')")
    @PostMapping("/test")
    public AjaxResult testConnection() {
        String tenantId = SecurityUtils.getCurrComId();
        TenantDatabase tenantDatabase = tenantDatabaseService.selectTenantDatabaseByTenantId(Long.parseLong(tenantId));
        boolean result = tenantDatabaseService.testConnection(tenantDatabase);
        return result ? AjaxResult.success() : AjaxResult.error("连接失败");
    }
}