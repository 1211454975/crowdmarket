package com.ruoyi.metadata.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.metadata.mapper.FormMetadataMapper;
import com.ruoyi.metadata.domain.FormMetadata;
import com.ruoyi.metadata.service.IFormMetadataService;
import com.ruoyi.metadata.mapper.FormFieldMetadataMapper;
import com.ruoyi.metadata.mapper.FormTenantTableMappingMapper;

/**
 * 表单元数据Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class FormMetadataServiceImpl implements IFormMetadataService {
    @Autowired
    private FormMetadataMapper formMetadataMapper;

    @Autowired
    private FormFieldMetadataMapper formFieldMetadataMapper;

    @Autowired
    private FormTenantTableMappingMapper formTenantTableMappingMapper;

    /**
     * 查询表单元数据
     *
     * @param metadataId 表单元数据主键
     * @return 表单元数据
     */
    @Override
    public FormMetadata selectFormMetadataByMetadataId(String metadataId) {
        return formMetadataMapper.selectFormMetadataByMetadataId(metadataId);
    }

    /**
     * 查询表单元数据列表
     *
     * @param formMetadata 表单元数据
     * @return 表单元数据集合
     */
    @Override
    public List<FormMetadata> selectFormMetadataList(FormMetadata formMetadata) {
        return formMetadataMapper.selectFormMetadataList(formMetadata);
    }

    /**
     * 新增表单元数据
     *
     * @param formMetadata 表单元数据
     * @return 结果
     */
    @Override
    @Transactional
    public int insertFormMetadata(FormMetadata formMetadata) {
        // 生成UUID作为元数据ID
        formMetadata.setMetadataId(UUID.randomUUID().toString().replace("-", ""));
        // 设置创建时间
        formMetadata.setCreateTime(DateUtils.getNowDate());
        // 设置创建者
        formMetadata.setCreateBy(SecurityUtils.getUsername());
        // 默认状态为草稿
        formMetadata.setStatus(0);
        // 设置租户ID
        formMetadata.setTenantId(SecurityUtils.getCurrComId());

        return formMetadataMapper.insertFormMetadata(formMetadata);
    }

    /**
     * 修改表单元数据
     *
     * @param formMetadata 表单元数据
     * @return 结果
     */
    @Override
    @Transactional
    public int updateFormMetadata(FormMetadata formMetadata) {
        // 设置更新时间
        formMetadata.setUpdateTime(DateUtils.getNowDate());
        // 设置更新者
        formMetadata.setUpdateBy(SecurityUtils.getUsername());

        return formMetadataMapper.updateFormMetadata(formMetadata);
    }

    /**
     * 批量删除表单元数据
     *
     * @param metadataIds 需要删除的表单元数据主键集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFormMetadataByMetadataIds(String[] metadataIds) {
        // 删除关联的字段元数据
        for (String metadataId : metadataIds) {
            formFieldMetadataMapper.deleteFormFieldMetadataByMetadataId(metadataId);
            // 删除租户表映射
            formTenantTableMappingMapper.deleteFormTenantTableMappingByMetadataId(metadataId);
        }

        return formMetadataMapper.deleteFormMetadataByMetadataIds(metadataIds);
    }

    /**
     * 删除表单元数据信息
     *
     * @param metadataId 表单元数据主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFormMetadataByMetadataId(String metadataId) {
        // 删除关联的字段元数据
        formFieldMetadataMapper.deleteFormFieldMetadataByMetadataId(metadataId);
        // 删除租户表映射
        formTenantTableMappingMapper.deleteFormTenantTableMappingByMetadataId(metadataId);

        return formMetadataMapper.deleteFormMetadataByMetadataId(metadataId);
    }

    /**
     * 发布表单元数据
     *
     * @param metadataId 表单元数据主键
     * @return 结果
     */
    @Override
    @Transactional
    public int publishFormMetadata(String metadataId) {
        FormMetadata formMetadata = new FormMetadata();
        formMetadata.setMetadataId(metadataId);
        formMetadata.setStatus(1); // 设置状态为发布
        formMetadata.setUpdateTime(DateUtils.getNowDate());
        formMetadata.setUpdateBy(SecurityUtils.getUsername());

        return formMetadataMapper.updateFormMetadata(formMetadata);
    }

    /**
     * 停用表单元数据
     *
     * @param metadataId 表单元数据主键
     * @return 结果
     */
    @Override
    @Transactional
    public int disableFormMetadata(String metadataId) {
        FormMetadata formMetadata = new FormMetadata();
        formMetadata.setMetadataId(metadataId);
        formMetadata.setStatus(2); // 设置状态为停用
        formMetadata.setUpdateTime(DateUtils.getNowDate());
        formMetadata.setUpdateBy(SecurityUtils.getUsername());

        return formMetadataMapper.updateFormMetadata(formMetadata);
    }
}