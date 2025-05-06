package com.ruoyi.metadata.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.metadata.mapper.FormTenantTableMappingMapper;
import com.ruoyi.metadata.domain.FormTenantTableMapping;
import com.ruoyi.metadata.service.IFormTenantTableMappingService;

/**
 * 表单租户表映射Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class FormTenantTableMappingServiceImpl implements IFormTenantTableMappingService {
    @Autowired
    private FormTenantTableMappingMapper formTenantTableMappingMapper;

    /**
     * 查询表单租户表映射
     *
     * @param mappingId 表单租户表映射主键
     * @return 表单租户表映射
     */
    @Override
    public FormTenantTableMapping selectFormTenantTableMappingByMappingId(String mappingId) {
        return formTenantTableMappingMapper.selectFormTenantTableMappingByMappingId(mappingId);
    }

    /**
     * 查询表单租户表映射列表
     *
     * @param formTenantTableMapping 表单租户表映射
     * @return 表单租户表映射
     */
    @Override
    public List<FormTenantTableMapping> selectFormTenantTableMappingList(FormTenantTableMapping formTenantTableMapping) {
        return formTenantTableMappingMapper.selectFormTenantTableMappingList(formTenantTableMapping);
    }

    /**
     * 根据元数据ID和租户ID查询映射
     *
     * @param metadataId 元数据ID
     * @param tenantId 租户ID
     * @return 表单租户表映射
     */
    @Override
    public FormTenantTableMapping selectFormTenantTableMappingByMetadataIdAndTenantId(String metadataId, String tenantId) {
        return formTenantTableMappingMapper.selectFormTenantTableMappingByMetadataIdAndTenantId(metadataId, tenantId);
    }

    /**
     * 新增表单租户表映射
     *
     * @param formTenantTableMapping 表单租户表映射
     * @return 结果
     */
    @Override
    public int insertFormTenantTableMapping(FormTenantTableMapping formTenantTableMapping) {
        return formTenantTableMappingMapper.insertFormTenantTableMapping(formTenantTableMapping);
    }

    /**
     * 修改表单租户表映射
     *
     * @param formTenantTableMapping 表单租户表映射
     * @return 结果
     */
    @Override
    public int updateFormTenantTableMapping(FormTenantTableMapping formTenantTableMapping) {
        return formTenantTableMappingMapper.updateFormTenantTableMapping(formTenantTableMapping);
    }

    /**
     * 批量删除表单租户表映射
     *
     * @param mappingIds 需要删除的表单租户表映射主键
     * @return 结果
     */
    @Override
    public int deleteFormTenantTableMappingByMappingIds(String[] mappingIds) {
        return formTenantTableMappingMapper.deleteFormTenantTableMappingByMappingIds(mappingIds);
    }

    /**
     * 删除表单租户表映射信息
     *
     * @param mappingId 表单租户表映射主键
     * @return 结果
     */
    @Override
    public int deleteFormTenantTableMappingByMappingId(String mappingId) {
        return formTenantTableMappingMapper.deleteFormTenantTableMappingByMappingId(mappingId);
    }

    /**
     * 根据元数据ID删除映射
     *
     * @param metadataId 元数据ID
     * @return 结果
     */
    @Override
    public int deleteFormTenantTableMappingByMetadataId(String metadataId) {
        return formTenantTableMappingMapper.deleteFormTenantTableMappingByMetadataId(metadataId);
    }
}