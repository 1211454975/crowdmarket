package com.ruoyi.formdata.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.formdata.service.IFormDataService;
import com.ruoyi.formdata.service.IFormImportExportService;
import com.ruoyi.formdata.service.IFormQueryService;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.domain.FormMetadata;
import com.ruoyi.metadata.service.IFormFieldMetadataService;
import com.ruoyi.metadata.service.IFormMetadataService;

import static org.apache.poi.ss.usermodel.Cell.*;

/**
 * 表单数据导入导出 服务实现类
 *
 * @author ruoyi
 */
@Service
public class FormImportExportServiceImpl implements IFormImportExportService
{
    private static final Logger log = LoggerFactory.getLogger(FormImportExportServiceImpl.class);

    @Autowired
    private IFormMetadataService formMetadataService;

    @Autowired
    private IFormFieldMetadataService formFieldMetadataService;

    @Autowired
    private IFormQueryService formQueryService;

    @Autowired
    private IFormDataService formDataService;

    /**
     * 导出表单数据到Excel
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @param outputStream 输出流
     * @return 导出记录数
     */
    @Override
    public int exportToExcel(String metadataId, Map<String, Object> params, OutputStream outputStream)
    {
        try
        {
            // 获取表单元数据
            FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
            if (metadata == null)
            {
                log.error("表单元数据不存在，metadataId: {}", metadataId);
                return 0;
            }

            // 获取表单字段元数据
            List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
            if (fields.isEmpty())
            {
                log.error("表单字段元数据不存在，metadataId: {}", metadataId);
                return 0;
            }

            // 查询数据
            List<Map<String, Object>> dataList = formQueryService.queryFormData(metadataId, params);

            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(metadata.getMetadataName());

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            int columnIndex = 0;

            // 按字段排序
            fields.sort((a, b) -> a.getSortOrder() - b.getSortOrder());

            // 记录字段索引映射
            Map<String, Integer> fieldIndexMap = new HashMap<>();

            // 设置标题行
            for (FormFieldMetadata field : fields)
            {
                if (field.getIsList() == 1)
                {
                    Cell cell = headerRow.createCell(columnIndex);
                    cell.setCellValue(field.getFieldLabel());
                    fieldIndexMap.put(field.getFieldName(), columnIndex);
                    columnIndex++;
                }
            }

            // 填充数据行
            int rowIndex = 1;
            for (Map<String, Object> data : dataList)
            {
                Row dataRow = sheet.createRow(rowIndex++);

                for (FormFieldMetadata field : fields)
                {
                    if (field.getIsList() == 1)
                    {
                        Integer colIndex = fieldIndexMap.get(field.getFieldName());
                        if (colIndex != null)
                        {
                            Cell cell = dataRow.createCell(colIndex);
                            Object value = data.get(field.getFieldName());
                            setCellValue(cell, value);
                        }
                    }
                }
            }

            // 自动调整列宽
            for (int i = 0; i < columnIndex; i++)
            {
                sheet.autoSizeColumn(i);
            }

            // 写入输出流
            workbook.write(outputStream);
            workbook.close();

            return dataList.size();
        }
        catch (Exception e)
        {
            log.error("导出Excel异常", e);
            return 0;
        }
    }

    /**
     * 导出表单数据模板
     *
     * @param metadataId 元数据ID
     * @param outputStream 输出流
     */
    @Override
    public void exportTemplate(String metadataId, OutputStream outputStream)
    {
        try
        {
            // 获取表单元数据
            FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
            if (metadata == null)
            {
                log.error("表单元数据不存在，metadataId: {}", metadataId);
                return;
            }

            // 获取表单字段元数据
            List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
            if (fields.isEmpty())
            {
                log.error("表单字段元数据不存在，metadataId: {}", metadataId);
                return;
            }

            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("模板");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            int columnIndex = 0;

            // 按字段排序
            fields.sort((a, b) -> a.getSortOrder() - b.getSortOrder());

            // 设置标题行
            for (FormFieldMetadata field : fields)
            {
                // 排除自动生成的字段
                if (!"auto".equals(field.getFieldType()))
                {
                    Cell cell = headerRow.createCell(columnIndex++);
                    cell.setCellValue(field.getFieldLabel() + (field.getIsRequired() == 1 ? "(*)" : ""));
                }
            }

            // 创建说明行
            Row tipRow = sheet.createRow(1);
            columnIndex = 0;

            for (FormFieldMetadata field : fields)
            {
                // 排除自动生成的字段
                if (!"auto".equals(field.getFieldType()))
                {
                    Cell cell = tipRow.createCell(columnIndex++);
                    StringBuilder tip = new StringBuilder();

                    // 添加字段类型说明
                    tip.append("类型: ").append(getFieldTypeDesc(field.getFieldType()));

                    // 添加必填说明
                    if (field.getIsRequired() == 1)
                    {
                        tip.append(", 必填");
                    }

                    // 添加字典类型说明
                    if (StringUtils.isNotEmpty(field.getDictType()))
                    {
                        tip.append(", 字典: ").append(field.getDictType());
                    }

                    cell.setCellValue(tip.toString());
                }
            }

            // 自动调整列宽
            for (int i = 0; i < columnIndex; i++)
            {
                sheet.autoSizeColumn(i);
            }

            // 写入输出流
            workbook.write(outputStream);
            workbook.close();
        }
        catch (Exception e)
        {
            log.error("导出模板异常", e);
        }
    }

    /**
     * 从Excel导入表单数据
     *
     * @param metadataId 元数据ID
     * @param inputStream 输入流
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     */
    @Override
    public Map<String, Object> importFromExcel(String metadataId, InputStream inputStream, boolean updateSupport)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("total", 0);
        result.put("success", 0);
        result.put("fail", 0);
        result.put("errors", new ArrayList<String>());

        try
        {
            // 获取表单元数据
            FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
            if (metadata == null)
            {
                log.error("表单元数据不存在，metadataId: {}", metadataId);
                ((List<String>) result.get("errors")).add("表单元数据不存在");
                return result;
            }

            // 获取表单字段元数据
            List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
            if (fields.isEmpty())
            {
                log.error("表单字段元数据不存在，metadataId: {}", metadataId);
                ((List<String>) result.get("errors")).add("表单字段元数据不存在");
                return result;
            }

            // 按字段排序
            fields.sort((a, b) -> a.getSortOrder() - b.getSortOrder());

            // 过滤出可导入的字段（非自动生成）
            List<FormFieldMetadata> importFields = new ArrayList<>();
            for (FormFieldMetadata field : fields)
            {
                if (!"auto".equals(field.getFieldType()))
                {
                    importFields.add(field);
                }
            }

            // 打开工作簿
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // 跳过标题行和说明行
            int startRow = 2;
            int totalRows = sheet.getPhysicalNumberOfRows();

            // 记录总行数
            int total = totalRows - startRow;
            result.put("total", total);

            if (total <= 0)
            {
                workbook.close();
                return result;
            }

            // 读取数据
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (int i = startRow; i < totalRows; i++)
            {
                Row row = sheet.getRow(i);
                if (row == null)
                {
                    continue;
                }

                Map<String, Object> data = new HashMap<>();
                boolean hasData = false;

                for (int j = 0; j < importFields.size(); j++)
                {
                    FormFieldMetadata field = importFields.get(j);
                    Cell cell = row.getCell(j);

                    if (cell != null)
                    {
                        Object value = getCellValue(cell, field.getFieldType());
                        if (value != null)
                        {
                            data.put(field.getFieldName(), value);
                            hasData = true;
                        }
                    }
                }

                if (hasData)
                {
                    dataList.add(data);
                }
            }

            workbook.close();

            // 验证数据
            List<Map<String, Object>> validationResults = validateImportData(metadataId, dataList);

            // 统计验证结果
            int failCount = 0;
            List<String> errors = (List<String>) result.get("errors");

            for (Map<String, Object> validationResult : validationResults)
            {
                Boolean valid = (Boolean) validationResult.get("valid");
                if (!valid)
                {
                    failCount++;
                    List<String> fieldErrors = (List<String>) validationResult.get("errors");
                    errors.addAll(fieldErrors);
                }
            }

            // 保存有效数据
            int successCount = 0;
            for (int i = 0; i < dataList.size(); i++)
            {
                Map<String, Object> data = dataList.get(i);
                Map<String, Object> validationResult = validationResults.get(i);

                Boolean valid = (Boolean) validationResult.get("valid");
                if (valid)
                {
                    try
                    {
                        // 判断是新增还是更新
                        boolean isUpdate = false;
                        if (updateSupport)
                        {
                            // 查找主键字段
                            FormFieldMetadata pkField = null;
                            for (FormFieldMetadata field : fields)
                            {
                                if (field.getIsPk() == 1)
                                {
                                    pkField = field;
                                    break;
                                }
                            }

                            if (pkField != null && data.containsKey(pkField.getFieldName()))
                            {
                                Object pkValue = data.get(pkField.getFieldName());
                                if (pkValue != null)
                                {
                                    // 查询是否存在
                                    Map<String, Object> existingData = formQueryService.queryFormDataById(metadataId, pkValue.toString());
                                    if (existingData != null && !existingData.isEmpty())
                                    {
                                        isUpdate = true;
                                    }
                                }
                            }
                        }

                        // 保存数据
                        if (isUpdate)
                        {
                            formDataService.updateFormData(metadataId, data);
                        }
                        else
                        {
                            formDataService.insertFormData(metadataId, data);
                        }
                        successCount++;
                    }
                    catch (Exception e)
                    {
                        failCount++;
                        errors.add("第" + (i + startRow + 1) + "行数据保存失败：" + e.getMessage());
                        log.error("保存导入数据失败", e);
                    }
                }
            }

            // 更新结果统计
            result.put("success", successCount);
            result.put("fail", failCount);

            return result;
        }
        catch (Exception e)
        {
            log.error("导入Excel异常", e);
            ((List<String>) result.get("errors")).add("导入异常：" + e.getMessage());
            return result;
        }
    }

    /**
     * 验证导入数据
     *
     * @param metadataId 元数据ID
     * @param dataList 数据列表
     * @return 验证结果
     */
    @Override
    public List<Map<String, Object>> validateImportData(String metadataId, List<Map<String, Object>> dataList)
    {
        List<Map<String, Object>> results = new ArrayList<>();

        // 获取表单元数据
        FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
        if (metadata == null)
        {
            log.error("表单元数据不存在，metadataId: {}", metadataId);
            return results;
        }

        // 获取表单字段元数据
        List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
        if (fields.isEmpty())
        {
            log.error("表单字段元数据不存在，metadataId: {}", metadataId);
            return results;
        }

        // 验证每行数据
        for (int i = 0; i < dataList.size(); i++)
        {
            Map<String, Object> data = dataList.get(i);
            Map<String, Object> result = new HashMap<>();
            List<String> errors = new ArrayList<>();
            boolean valid = true;

            // 验证必填字段
            for (FormFieldMetadata field : fields)
            {
                if (field.getIsRequired() == 1 && !"auto".equals(field.getFieldType()))
                {
                    Object value = data.get(field.getFieldName());
                    if (value == null || (value instanceof String && StringUtils.isEmpty((String) value)))
                    {
                        valid = false;
                        errors.add("第" + (i + 3) + "行：" + field.getFieldLabel() + "不能为空");
                    }
                }
            }

            // TODO: 添加更多验证规则，如数据类型验证、字典值验证等

            result.put("valid", valid);
            result.put("errors", errors);
            results.add(result);
        }

        return results;
    }

    /**
     * 导出查询结果为CSV
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @param outputStream 输出流
     * @return 导出记录数
     */
    @Override
    public int exportToCsv(String metadataId, Map<String, Object> params, OutputStream outputStream)
    {
        try
        {
            // 获取表单元数据
            FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
            if (metadata == null)
            {
                log.error("表单元数据不存在，metadataId: {}", metadataId);
                return 0;
            }

            // 获取表单字段元数据
            List<FormFieldMetadata> fields = formFieldMetadataService.selectFormFieldMetadataByMetadataId(metadataId);
            if (fields.isEmpty())
            {
                log.error("表单字段元数据不存在，metadataId: {}", metadataId);
                return 0;
            }

            // 查询数据
            List<Map<String, Object>> dataList = formQueryService.queryFormData(metadataId, params);

            // 按字段排序
            fields.sort((a, b) -> a.getSortOrder() - b.getSortOrder());

            // 过滤出可导出的字段
            List<FormFieldMetadata> exportFields = new ArrayList<>();
            for (FormFieldMetadata field : fields)
            {
                if (field.getIsList() == 1)
                {
                    exportFields.add(field);
                }
            }

            // 写入CSV头
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < exportFields.size(); i++)
            {
                if (i > 0)
                {
                    header.append(",");
                }
                header.append("\"").append(escapeCsv(exportFields.get(i).getFieldLabel())).append("\"");
            }
            header.append("\n");
            outputStream.write(header.toString().getBytes("UTF-8"));

            // 写入数据行
            for (Map<String, Object> data : dataList)
            {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < exportFields.size(); i++)
                {
                    if (i > 0)
                    {
                        line.append(",");
                    }

                    FormFieldMetadata field = exportFields.get(i);
                    Object value = data.get(field.getFieldName());
                    if (value != null)
                    {
                        line.append("\"").append(escapeCsv(value.toString())).append("\"");
                    }
                    else
                    {
                        line.append("\"\"");
                    }
                }
                line.append("\n");
                outputStream.write(line.toString().getBytes("UTF-8"));
            }

            return dataList.size();
        }
        catch (Exception e)
        {
            log.error("导出CSV异常", e);
            return 0;
        }
    }

    /**
     * 导出查询结果为JSON
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @param outputStream 输出流
     * @return 导出记录数
     */
    @Override
    public int exportToJson(String metadataId, Map<String, Object> params, OutputStream outputStream)
    {
        try
        {
            // 获取表单元数据
            FormMetadata metadata = formMetadataService.selectFormMetadataByMetadataId(metadataId);
            if (metadata == null)
            {
                log.error("表单元数据不存在，metadataId: {}", metadataId);
                return 0;
            }

            // 查询数据
            List<Map<String, Object>> dataList = formQueryService.queryFormData(metadataId, params);

            // 创建JSON对象
            Map<String, Object> jsonObject = new HashMap<>();
            jsonObject.put("metadata", metadata);
            jsonObject.put("data", dataList);

            // 转换为JSON字符串
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(jsonObject);

            // 写入输出流
            outputStream.write(jsonString.getBytes("UTF-8"));

            return dataList.size();
        }
        catch (Exception e)
        {
            log.error("导出JSON异常", e);
            return 0;
        }
    }

    /**
     * 设置单元格值
     *
     * @param cell 单元格
     * @param value 值
     */
    private void setCellValue(Cell cell, Object value)
    {
        if (value == null)
        {
            cell.setCellValue("");
            return;
        }

        if (value instanceof String)
        {
            cell.setCellValue((String) value);
        }
        else if (value instanceof Integer)
        {
            cell.setCellValue((Integer) value);
        }
        else if (value instanceof Long)
        {
            cell.setCellValue((Long) value);
        }
        else if (value instanceof Double)
        {
            cell.setCellValue((Double) value);
        }
        else if (value instanceof Boolean)
        {
            cell.setCellValue((Boolean) value);
        }
        else if (value instanceof java.util.Date)
        {
            cell.setCellValue((java.util.Date) value);
        }
        else
        {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 获取单元格值
     *
     * @param cell 单元格
     * @param fieldType 字段类型
     * @return 值
     */
    private Object getCellValue(Cell cell, String fieldType)
    {
        if (cell == null)
        {
            return null;
        }

        Object value = null;
        switch (cell.getCellType())
        {
            case CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case CELL_TYPE_NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell))
                {
                    value = cell.getDateCellValue();
                }
                else
                {
                    value = cell.getNumericCellValue();
                    // 根据字段类型转换
                    if ("int".equals(fieldType) || "integer".equals(fieldType))
                    {
                        value = (int) ((Double) value).doubleValue();
                    }
                    else if ("long".equals(fieldType))
                    {
                        value = (long) ((Double) value).doubleValue();
                    }
                }
                break;
            case CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case CELL_TYPE_FORMULA:
                try
                {
                    value = cell.getStringCellValue();
                }
                catch (Exception e)
                {
                    try
                    {
                        value = cell.getNumericCellValue();
                    }
                    catch (Exception ex)
                    {
                        value = cell.getCellFormula();
                    }
                }
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 获取字段类型描述
     *
     * @param fieldType 字段类型
     * @return 类型描述
     */
    private String getFieldTypeDesc(String fieldType)
    {
        if ("string".equals(fieldType))
        {
            return "文本";
        }
        else if ("int".equals(fieldType) || "integer".equals(fieldType))
        {
            return "整数";
        }
        else if ("long".equals(fieldType))
        {
            return "长整数";
        }
        else if ("double".equals(fieldType) || "float".equals(fieldType))
        {
            return "小数";
        }
        else if ("boolean".equals(fieldType))
        {
            return "布尔值";
        }
        else if ("date".equals(fieldType))
        {
            return "日期";
        }
        else if ("datetime".equals(fieldType))
        {
            return "日期时间";
        }
        else if ("dict".equals(fieldType))
        {
            return "字典值";
        }
        else
        {
            return fieldType;
        }
    }

    /**
     * 转义CSV字符串
     *
     * @param str 字符串
     * @return 转义后的字符串
     */
    private String escapeCsv(String str)
    {
        if (str == null)
        {
            return "";
        }
        return str.replace("\"", "\"\"");
    }
}