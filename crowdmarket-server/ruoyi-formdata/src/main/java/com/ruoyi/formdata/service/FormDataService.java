package com.ruoyi.formdata.service;

import java.util.List;
import java.util.Map;

/**
 * 表单数据Service接口
 *
 * @author ruoyi
 */
public interface FormDataService {
    /**
     * 查询表单数据
     *
     * @param metadataId 元数据ID
     * @param dataId 数据ID
     * @return 表单数据
     */
    public Map<String, Object> selectFormDataById(String metadataId, String dataId);

    /**
     * 查询表单数据列表
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @return 表单数据集合
     */
    public List<Map<String, Object>> selectFormDataList(String metadataId, Map<String, Object> params);

    /**
     * 新增表单数据
     *
     * @param metadataId 元数据ID
     * @param data 表单数据
     * @return 结果
     */
    public int insertFormData(String metadataId, Map<String, Object> data);

    /**
     * 修改表单数据
     *
     * @param metadataId 元数据ID
     * @param data 表单数据
     * @return 结果
     */
    public int updateFormData(String metadataId, Map<String, Object> data);

    /**
     * 删除表单数据
     *
     * @param metadataId 元数据ID
     * @param dataId 数据ID
     * @return 结果
     */
    public int deleteFormDataById(String metadataId, String dataId);

    /**
     * 批量删除表单数据
     *
     * @param metadataId 元数据ID
     * @param dataIds 需要删除的数据ID集合
     * @return 结果
     */
    public int deleteFormDataByIds(String metadataId, String[] dataIds);

    /**
     * 导入表单数据
     *
     * @param metadataId 元数据ID
     * @param dataList 数据列表
     * @param updateSupport 是否更新已存在数据
     * @return 结果
     */
    public String importFormData(String metadataId, List<Map<String, Object>> dataList, boolean updateSupport);

    /**
     * 导出表单数据
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @return 表单数据集合
     */
    public List<Map<String, Object>> exportFormData(String metadataId, Map<String, Object> params);
}