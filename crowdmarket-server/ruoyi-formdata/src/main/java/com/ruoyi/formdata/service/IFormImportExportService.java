package com.ruoyi.formdata.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 表单数据导入导出 服务接口
 *
 * @author ruoyi
 */
public interface IFormImportExportService
{
    /**
     * 导出表单数据到Excel
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @param outputStream 输出流
     * @return 导出记录数
     */
    public int exportToExcel(String metadataId, Map<String, Object> params, OutputStream outputStream);

    /**
     * 导出表单数据模板
     *
     * @param metadataId 元数据ID
     * @param outputStream 输出流
     */
    public void exportTemplate(String metadataId, OutputStream outputStream);

    /**
     * 从Excel导入表单数据
     *
     * @param metadataId 元数据ID
     * @param inputStream 输入流
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     */
    public Map<String, Object> importFromExcel(String metadataId, InputStream inputStream, boolean updateSupport);

    /**
     * 验证导入数据
     *
     * @param metadataId 元数据ID
     * @param dataList 数据列表
     * @return 验证结果
     */
    public List<Map<String, Object>> validateImportData(String metadataId, List<Map<String, Object>> dataList);

    /**
     * 导出查询结果为CSV
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @param outputStream 输出流
     * @return 导出记录数
     */
    public int exportToCsv(String metadataId, Map<String, Object> params, OutputStream outputStream);

    /**
     * 导出查询结果为JSON
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @param outputStream 输出流
     * @return 导出记录数
     */
    public int exportToJson(String metadataId, Map<String, Object> params, OutputStream outputStream);
}