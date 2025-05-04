package com.ruoyi.metadata.mapper;

import java.util.List;
import com.ruoyi.metadata.domain.FormFieldMetadata;

/**
 * 表单字段元数据Mapper接口
 *
 * @author ruoyi
 */
public interface FormFieldMetadataMapper {
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
     * 删除表单字段元数据
     *
     * @param fieldId 表单字段元数据主键
     * @return 结果
     */
    public int deleteFormFieldMetadataByFieldId(String fieldId);

    /**
     * 批量删除表单字段元数据
     *
     * @param fieldIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFormFieldMetadataByFieldIds(String[] fieldIds);

    /**
     * 根据元数据ID删除字段元数据
     *
     * @param metadataId 元数据ID
     * @return 结果
     */
    public int deleteFormFieldMetadataByMetadataId(String metadataId);
}