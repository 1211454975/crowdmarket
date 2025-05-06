package com.ruoyi.metadata.controller;

import java.util.List;
import java.util.Map;

import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.domain.FormMetadata;
import com.ruoyi.metadata.service.IDatabaseMetadataService;

/**
 * 数据库元数据Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/metadata/database")
public class DatabaseMetadataController extends BaseController {
    @Autowired
    private IDatabaseMetadataService IDatabaseMetadataService;

    /**
     * 获取数据库表列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:database:list')")
    @GetMapping("/tables/{dbName}")
    public AjaxResult listTables(@PathVariable(value = "dbName", required = false) String dbName) {
        List<Map<String, Object>> tables = IDatabaseMetadataService.listTables(dbName);
        return AjaxResult.success(tables);
    }

    /**
     * 获取表的列信息
     */
    @PreAuthorize("@ss.hasPermi('metadata:database:list')")
    @GetMapping("/columns/{dbName}/{tableName}")
    public AjaxResult listColumns(@PathVariable("dbName") String dbName, @PathVariable("tableName") String tableName) {
        List<Map<String, Object>> columns = IDatabaseMetadataService.listColumns(dbName, tableName);
        return AjaxResult.success(columns);
    }

    /**
     * 从数据库表生成表单元数据
     */
    @PreAuthorize("@ss.hasPermi('metadata:database:generate')")
    @Log(title = "数据库元数据", businessType = BusinessType.INSERT)
    @PostMapping("/generate/metadata/{dbName}/{tableName}")
    public AjaxResult generateFormMetadata(@PathVariable("dbName") String dbName, @PathVariable("tableName") String tableName) {
        FormMetadata metadata = IDatabaseMetadataService.generateFormMetadataFromTable(dbName, tableName);
        return AjaxResult.success(metadata);
    }

    /**
     * 从数据库表生成字段元数据列表
     */
    @PreAuthorize("@ss.hasPermi('metadata:database:generate')")
    @Log(title = "数据库元数据", businessType = BusinessType.INSERT)
    @PostMapping("/generate/fields/{dbName}/{tableName}")
    public AjaxResult generateFieldMetadata(@PathVariable("dbName") String dbName, @PathVariable("tableName") String tableName) {
        List<FormFieldMetadata> fieldList = IDatabaseMetadataService.generateFieldMetadataFromTable(dbName, tableName);
        return AjaxResult.success(fieldList);
    }
}