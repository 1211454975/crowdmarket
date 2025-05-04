package com.ruoyi.metadata.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.metadata.mapper.FormFieldMetadataMapper;
import com.ruoyi.metadata.domain.FormFieldMetadata;
import com.ruoyi.metadata.service.FormFieldMetadataService;

/**
 * 表单字段元数据Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class FormFieldMetadataServiceImpl implements FormFieldMetadataService {
    @Autowired
    private FormFieldMetadataMapper formFieldMetadataMapper;

    /**
     * 查询表单字段元数据
     *
     * @param fieldId 表单字段元数据主键
     * @return 表单字段元数据
     */
    @Override
    public FormFieldMetadata selectFormFieldMetadataByFieldId(String fieldId) {
        return formFieldMetadataMapper.selectFormFieldMetadataByFieldId(fieldId);
    }

    /**
     * 查询表单字段元数据列表
     *
     * @param formFieldMetadata 表单字段元数据
     * @return 表单字段元数据
     */
    @Override
    public List<FormFieldMetadata> selectFormFieldMetadataList(FormFieldMetadata formFieldMetadata) {
        return formFieldMetadataMapper.selectFormFieldMetadataList(formFieldMetadata);
    }

    /**
     * 根据元数据ID查询字段列表
     *
     * @param metadataId 元数据ID
     * @return 表单字段元数据集合
     */
    @Override
    public List<FormFieldMetadata> selectFormFieldMetadataByMetadataId(String metadataId) {
        return formFieldMetadataMapper.selectFormFieldMetadataByMetadataId(metadataId);
    }

    /**
     * 新增表单字段元数据
     *
     * @param formFieldMetadata 表单字段元数据
     * @return 结果
     */
    @Override
    public int insertFormFieldMetadata(FormFieldMetadata formFieldMetadata) {
        // 生成UUID作为字段ID
        formFieldMetadata.setFieldId(UUID.randomUUID().toString().replace("-", ""));
        // 设置租户ID
        formFieldMetadata.setTenantId(SecurityUtils.getCurrComId());

        return formFieldMetadataMapper.insertFormFieldMetadata(formFieldMetadata);
    }

    /**
     * 修改表单字段元数据
     *
     * @param formFieldMetadata 表单字段元数据
     * @return 结果
     */
    @Override
    public int updateFormFieldMetadata(FormFieldMetadata formFieldMetadata) {
        return formFieldMetadataMapper.updateFormFieldMetadata(formFieldMetadata);
    }

    /**
     * 批量删除表单字段元数据
     *
     * @param fieldIds 需要删除的表单字段元数据主键
     * @return 结果
     */
    @Override
    public int deleteFormFieldMetadataByFieldIds(String[] fieldIds) {
        return formFieldMetadataMapper.deleteFormFieldMetadataByFieldIds(fieldIds);
    }

    /**
     * 删除表单字段元数据信息
     *
     * @param fieldId 表单字段元数据主键
     * @return 结果
     */
    @Override
    public int deleteFormFieldMetadataByFieldId(String fieldId) {
        return formFieldMetadataMapper.deleteFormFieldMetadataByFieldId(fieldId);
    }

    /**
     * 批量保存字段元数据
     *
     * @param metadataId 元数据ID
     * @param fieldList 字段列表
     * @return 结果
     */
    @Override
    @Transactional
    public int batchSaveFormFieldMetadata(String metadataId, List<FormFieldMetadata> fieldList) {
        // 先删除原有字段
        formFieldMetadataMapper.deleteFormFieldMetadataByMetadataId(metadataId);

        // 批量添加新字段
        int rows = 0;
        for (FormFieldMetadata field : fieldList) {
            field.setMetadataId(metadataId);
            rows += insertFormFieldMetadata(field);
        }

        return rows;
    }
}