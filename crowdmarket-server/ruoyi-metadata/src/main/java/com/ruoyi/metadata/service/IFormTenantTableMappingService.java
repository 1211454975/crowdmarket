package com.ruoyi.metadata.service;

import java.util.List;
import com.ruoyi.metadata.domain.FormTenantTableMapping;

/**
 * 表单租户表映射Service接口
 *
 * @author ruoyi
 */
public interface IFormTenantTableMappingService {
    /**
     * 查询表单租户表映射
     *
     * @param mappingId 表单租户表映射主键
     * @return 表单租户表映射
     */
    public FormTenantTableMapping selectFormTenantTableMappingByMappingId(String mappingId);

    /**
     * 查询表单租户表映射列表
     *
     * @param formTenantTableMapping 表单租户表映射
     * @return 表单租户表映射集合
     */
    public List<FormTenantTableMapping> selectFormTenantTableMappingList(FormTenantTableMapping formTenantTableMapping);

    /**
     * 根据元数据ID和租户ID查询映射
     *
     * @param metadataId 元数据ID
     * @param tenantId 租户ID
     * @return 表单租户表映射
     */
    public FormTenantTableMapping selectFormTenantTableMappingByMetadataIdAndTenantId(String metadataId, String tenantId);

    /**
     * 新增表单租户表映射
     *
     * @param formTenantTableMapping 表单租户表映射
     * @return 结果
     */
    public int insertFormTenantTableMapping(FormTenantTableMapping formTenantTableMapping);

    /**
     * 修改表单租户表映射
     *
     * @param formTenantTableMapping 表单租户表映射
     * @return 结果
     */
    public int updateFormTenantTableMapping(FormTenantTableMapping formTenantTableMapping);

    /**
     * 批量删除表单租户表映射
     *
     * @param mappingIds 需要删除的表单租户表映射主键集合
     * @return 结果
     */
    public int deleteFormTenantTableMappingByMappingIds(String[] mappingIds);

    /**
     * 删除表单租户表映射信息
     *
     * @param mappingId 表单租户表映射主键
     * @return 结果
     */
    public int deleteFormTenantTableMappingByMappingId(String mappingId);

    /**
     * 根据元数据ID删除映射
     *
     * @param metadataId 元数据ID
     * @return 结果
     */
    public int deleteFormTenantTableMappingByMetadataId(String metadataId);
}