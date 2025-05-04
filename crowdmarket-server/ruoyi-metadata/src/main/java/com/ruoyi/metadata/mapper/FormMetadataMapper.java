package com.ruoyi.metadata.mapper;


import com.ruoyi.metadata.domain.FormMetadata;

import java.util.List;

/**
 * 表单元数据Mapper接口
 *
 * @author ruoyi
 */
public interface FormMetadataMapper {
    /**
     * 查询表单元数据
     *
     * @param metadataId 表单元数据主键
     * @return 表单元数据
     */
    public FormMetadata selectFormMetadataByMetadataId(String metadataId);

    /**
     * 查询表单元数据列表
     *
     * @param formMetadata 表单元数据
     * @return 表单元数据集合
     */
    public List<FormMetadata> selectFormMetadataList(FormMetadata formMetadata);

    /**
     * 新增表单元数据
     *
     * @param formMetadata 表单元数据
     * @return 结果
     */
    public int insertFormMetadata(FormMetadata formMetadata);

    /**
     * 修改表单元数据
     *
     * @param formMetadata 表单元数据
     * @return 结果
     */
    public int updateFormMetadata(FormMetadata formMetadata);

    /**
     * 删除表单元数据
     *
     * @param metadataId 表单元数据主键
     * @return 结果
     */
    public int deleteFormMetadataByMetadataId(String metadataId);

    /**
     * 批量删除表单元数据
     *
     * @param metadataIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFormMetadataByMetadataIds(String[] metadataIds);
}