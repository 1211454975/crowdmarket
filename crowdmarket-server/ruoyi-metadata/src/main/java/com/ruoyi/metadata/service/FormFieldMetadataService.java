package com.ruoyi.metadata.service;

import java.util.List;
import com.ruoyi.metadata.domain.FormFieldMetadata;

/**
 * 表单字段元数据Service接口
 *
 * @author ruoyi
 */
public interface FormFieldMetadataService {
    /**
     * 查询表单字段元数据
     *
     * @param fieldId 表单字段元数据主键
     * @return 表单字段元数据
     */
    public FormFieldMetadata selectFormFieldMetadataByFieldId(String fieldId);

    /**
     * 查询表单字段元数据列表
     *
     * @param formFieldMetadata 表单字段元数据
     * @return 表单字段元数据集合
     */
    public List<FormFieldMetadata> selectFormFieldMetadataList(FormFieldMetadata formFieldMetadata);

    /**
     * 根据元数据ID查询字段列表
     *
     * @param metadataId 元数据ID
     * @return 表单字段元数据集合
     */
    public List<FormFieldMetadata> selectFormFieldMetadataByMetadataId(String metadataId);

    /**
     * 新增表单字段元数据
     *
     * @param formFieldMetadata 表单字段元数据
     * @return 结果
     */
    public int insertFormFieldMetadata(FormFieldMetadata formFieldMetadata);

    /**
     * 修改表单字段元数据
     *
     * @param formFieldMetadata 表单字段元数据
     * @return 结果
     */
    public int updateFormFieldMetadata(FormFieldMetadata formFieldMetadata);

    /**
     * 批量删除表单字段元数据
     *
     * @param fieldIds 需要删除的表单字段元数据主键集合
     * @return 结果
     */
    public int deleteFormFieldMetadataByFieldIds(String[] fieldIds);

    /**
     * 删除表单字段元数据信息
     *
     * @param fieldId 表单字段元数据主键
     * @return 结果
     */
    public int deleteFormFieldMetadataByFieldId(String fieldId);

    /**
     * 批量保存字段元数据
     *
     * @param metadataId 元数据ID
     * @param fieldList 字段列表
     * @return 结果
     */
    public int batchSaveFormFieldMetadata(String metadataId, List<FormFieldMetadata> fieldList);
}