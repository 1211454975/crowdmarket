package com.ruoyi.metadata.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.metadata.mapper.FormViewConfigMapper;
import com.ruoyi.metadata.domain.FormViewConfig;
import com.ruoyi.metadata.service.IFormViewConfigService;

/**
 * 表单视图配置Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class FormViewConfigServiceImpl implements IFormViewConfigService {
    @Autowired
    private FormViewConfigMapper formViewConfigMapper;

    /**
     * 查询表单视图配置
     *
     * @param viewId 表单视图配置主键
     * @return 表单视图配置
     */
    @Override
    public FormViewConfig selectFormViewConfigByViewId(String viewId) {
        return formViewConfigMapper.selectFormViewConfigByViewId(viewId);
    }

    /**
     * 查询表单视图配置列表
     *
     * @param formViewConfig 表单视图配置
     * @return 表单视图配置
     */
    @Override
    public List<FormViewConfig> selectFormViewConfigList(FormViewConfig formViewConfig) {
        return formViewConfigMapper.selectFormViewConfigList(formViewConfig);
    }

    /**
     * 根据元数据ID查询视图配置列表
     *
     * @param metadataId 元数据ID
     * @return 表单视图配置集合
     */
    @Override
    public List<FormViewConfig> selectFormViewConfigByMetadataId(String metadataId) {
        return formViewConfigMapper.selectFormViewConfigByMetadataId(metadataId);
    }

    /**
     * 根据元数据ID和视图类型查询默认视图
     *
     * @param metadataId 元数据ID
     * @param viewType 视图类型
     * @return 表单视图配置
     */
    @Override
    public FormViewConfig selectDefaultFormViewConfig(String metadataId, String viewType) {
        return formViewConfigMapper.selectDefaultFormViewConfig(metadataId, viewType);
    }

    /**
     * 新增表单视图配置
     *
     * @param formViewConfig 表单视图配置
     * @return 结果
     */
    @Override
    @Transactional
    public int insertFormViewConfig(FormViewConfig formViewConfig) {
        // 生成UUID作为视图ID
        formViewConfig.setViewId(UUID.randomUUID().toString().replace("-", ""));
        // 设置创建时间
        formViewConfig.setCreateTime(DateUtils.getNowDate());
        // 设置创建者
        formViewConfig.setCreateBy(SecurityUtils.getUsername());
        // 设置租户ID
        formViewConfig.setTenantId(SecurityUtils.getCurrComId());

        // 如果是默认视图，则重置其他同类型视图的默认标志
        if (formViewConfig.getIsDefault() != null && formViewConfig.getIsDefault() == 1) {
            formViewConfigMapper.resetDefaultFormViewConfig(formViewConfig.getMetadataId(), formViewConfig.getViewType());
        }

        return formViewConfigMapper.insertFormViewConfig(formViewConfig);
    }

    /**
     * 修改表单视图配置
     *
     * @param formViewConfig 表单视图配置
     * @return 结果
     */
    @Override
    @Transactional
    public int updateFormViewConfig(FormViewConfig formViewConfig) {
        // 设置更新时间
        formViewConfig.setUpdateTime(DateUtils.getNowDate());
        // 设置更新者
        formViewConfig.setUpdateBy(SecurityUtils.getUsername());

        // 如果是默认视图，则重置其他同类型视图的默认标志
        if (formViewConfig.getIsDefault() != null && formViewConfig.getIsDefault() == 1) {
            formViewConfigMapper.resetDefaultFormViewConfig(formViewConfig.getMetadataId(), formViewConfig.getViewType());
        }

        return formViewConfigMapper.updateFormViewConfig(formViewConfig);
    }

    /**
     * 批量删除表单视图配置
     *
     * @param viewIds 需要删除的表单视图配置主键
     * @return 结果
     */
    @Override
    public int deleteFormViewConfigByViewIds(String[] viewIds) {
        return formViewConfigMapper.deleteFormViewConfigByViewIds(viewIds);
    }

    /**
     * 删除表单视图配置信息
     *
     * @param viewId 表单视图配置主键
     * @return 结果
     */
    @Override
    public int deleteFormViewConfigByViewId(String viewId) {
        return formViewConfigMapper.deleteFormViewConfigByViewId(viewId);
    }

    /**
     * 设置默认视图
     *
     * @param viewId 视图ID
     * @return 结果
     */
    @Override
    @Transactional
    public int setDefaultFormViewConfig(String viewId) {
        // 获取视图信息
        FormViewConfig viewConfig = formViewConfigMapper.selectFormViewConfigByViewId(viewId);
        if (viewConfig == null) {
            return 0;
        }

        // 重置同类型视图的默认标志
        formViewConfigMapper.resetDefaultFormViewConfig(viewConfig.getMetadataId(), viewConfig.getViewType());

        // 设置为默认视图
        FormViewConfig updateConfig = new FormViewConfig();
        updateConfig.setViewId(viewId);
        updateConfig.setIsDefault(1);
        updateConfig.setUpdateTime(DateUtils.getNowDate());
        updateConfig.setUpdateBy(SecurityUtils.getUsername());

        return formViewConfigMapper.updateFormViewConfig(updateConfig);
    }
}