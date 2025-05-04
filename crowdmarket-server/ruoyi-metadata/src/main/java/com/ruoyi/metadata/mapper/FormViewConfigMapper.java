package com.ruoyi.metadata.mapper;

import java.util.List;
import com.ruoyi.metadata.domain.FormViewConfig;

/**
 * 表单视图配置Mapper接口
 *
 * @author ruoyi
 */
public interface FormViewConfigMapper {
    /**
     * 查询表单视图配置
     *
     * @param viewId 表单视图配置主键
     * @return 表单视图配置
     */
    public FormViewConfig selectFormViewConfigByViewId(String viewId);

    /**
     * 查询表单视图配置列表
     *
     * @param formViewConfig 表单视图配置
     * @return 表单视图配置集合
     */
    public List<FormViewConfig> selectFormViewConfigList(FormViewConfig formViewConfig);

    /**
     * 根据元数据ID查询视图配置列表
     *
     * @param metadataId 元数据ID
     * @return 表单视图配置集合
     */
    public List<FormViewConfig> selectFormViewConfigByMetadataId(String metadataId);

    /**
     * 根据元数据ID和视图类型查询默认视图
     *
     * @param metadataId 元数据ID
     * @param viewType 视图类型
     * @return 表单视图配置
     */
    public FormViewConfig selectDefaultFormViewConfig(String metadataId, String viewType);

    /**
     * 新增表单视图配置
     *
     * @param formViewConfig 表单视图配置
     * @return 结果
     */
    public int insertFormViewConfig(FormViewConfig formViewConfig);

    /**
     * 修改表单视图配置
     *
     * @param formViewConfig 表单视图配置
     * @return 结果
     */
    public int updateFormViewConfig(FormViewConfig formViewConfig);

    /**
     * 删除表单视图配置
     *
     * @param viewId 表单视图配置主键
     * @return 结果
     */
    public int deleteFormViewConfigByViewId(String viewId);

    /**
     * 批量删除表单视图配置
     *
     * @param viewIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFormViewConfigByViewIds(String[] viewIds);

    /**
     * 根据元数据ID删除视图配置
     *
     * @param metadataId 元数据ID
     * @return 结果
     */
    public int deleteFormViewConfigByMetadataId(String metadataId);

    /**
     * 重置默认视图
     *
     * @param metadataId 元数据ID
     * @param viewType 视图类型
     * @return 结果
     */
    public int resetDefaultFormViewConfig(String metadataId, String viewType);
}