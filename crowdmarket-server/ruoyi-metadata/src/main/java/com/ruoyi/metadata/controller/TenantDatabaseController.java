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

import com.ruoyi.metadata.domain.TenantDatabase;
import com.ruoyi.metadata.service.TenantDatabaseService;

/**
 * 租户数据库配置Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/metadata/tenant/database")
public class TenantDatabaseController extends BaseController {
    @Autowired
    private TenantDatabaseService tenantDatabaseService;

    /**
     * 查询租户数据库配置列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:tenant:list')")
    @GetMapping("/list")
    public TableDataInfo list(TenantDatabase tenantDatabase) {
        startPage();
        List<TenantDatabase> list = tenantDatabaseService.selectTenantDatabaseList(tenantDatabase);
        return getDataTable(list);
    }

    /**
     * 获取租户数据库配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('metadata:tenant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(tenantDatabaseService.selectTenantDatabaseById(id));
    }

    /**
     * 新增租户数据库配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:tenant:add')")
    @Log(title = "租户数据库配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TenantDatabase tenantDatabase) {
        return toAjax(tenantDatabaseService.insertTenantDatabase(tenantDatabase));
    }

    /**
     * 修改租户数据库配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:tenant:edit')")
    @Log(title = "租户数据库配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TenantDatabase tenantDatabase) {
        return toAjax(tenantDatabaseService.updateTenantDatabase(tenantDatabase));
    }

    /**
     * 删除租户数据库配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:tenant:remove')")
    @Log(title = "租户数据库配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(tenantDatabaseService.deleteTenantDatabaseByIds(ids));
    }

    /**
     * 测试数据库连接
     */
    @PreAuthorize("@ss.hasPermi('metadata:tenant:test')")
    @Log(title = "租户数据库配置", businessType = BusinessType.OTHER)
    @PostMapping("/test")
    public AjaxResult testConnection(@RequestBody TenantDatabase tenantDatabase) {
        boolean result = tenantDatabaseService.testConnection(tenantDatabase);
        return result ? AjaxResult.success("连接成功") : AjaxResult.error("连接失败");
    }

    /**
     * 创建租户数据库
     */
    @PreAuthorize("@ss.hasPermi('metadata:tenant:create')")
    @Log(title = "租户数据库配置", businessType = BusinessType.OTHER)
    @PostMapping("/create")
    public AjaxResult createDatabase(@RequestBody TenantDatabase tenantDatabase) {
        boolean result = tenantDatabaseService.createTenantDatabase(tenantDatabase);
        return result ? AjaxResult.success("创建成功") : AjaxResult.error("创建失败");
    }

    /**
     * 根据租户ID获取数据库配置
     */
    @PreAuthorize("@ss.hasPermi('metadata:tenant:query')")
    @GetMapping(value = "/tenant/{tenantId}")
    public AjaxResult getInfoByTenantId(@PathVariable("tenantId") String tenantId) {
        return AjaxResult.success(tenantDatabaseService.selectTenantDatabaseByTenantId(tenantId));
    }
}