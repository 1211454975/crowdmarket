package com.ruoyi.metadata.service;
import java.util.List;
import com.ruoyi.metadata.domain.FormMetadata;

/**
 * 表单元数据Service接口
 *
 * @author ruoyi
 */
public interface IFormMetadataService {
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
     * 批量删除表单元数据
     *
     * @param metadataIds 需要删除的表单元数据主键集合
     * @return 结果
     */
    public int deleteFormMetadataByMetadataIds(String[] metadataIds);

    /**
     * 删除表单元数据信息
     *
     * @param metadataId 表单元数据主键
     * @return 结果
     */
    public int deleteFormMetadataByMetadataId(String metadataId);

    /**
     * 发布表单元数据
     *
     * @param metadataId 表单元数据主键
     * @return 结果
     */
    public int publishFormMetadata(String metadataId);

    /**
     * 停用表单元数据
     *
     * @param metadataId 表单元数据主键
     * @return 结果
     */
    public int disableFormMetadata(String metadataId);
}