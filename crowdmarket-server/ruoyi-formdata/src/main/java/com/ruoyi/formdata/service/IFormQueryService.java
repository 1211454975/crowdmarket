package com.ruoyi.formdata.service;

import java.util.List;
import java.util.Map;

/**
 * 表单数据查询 服务接口
 *
 * @author ruoyi
 */
public interface IFormQueryService
{
    /**
     * 根据元数据ID和查询条件查询表单数据
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @return 表单数据列表
     */
    public List<Map<String, Object>> queryFormData(String metadataId, Map<String, Object> params);

    /**
     * 根据元数据ID和主键值查询单条表单数据
     *
     * @param metadataId 元数据ID
     * @param primaryKey 主键值
     * @return 表单数据
     */
    public Map<String, Object> queryFormDataById(String metadataId, String primaryKey);

    /**
     * 根据元数据ID和查询条件统计表单数据数量
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @return 数据数量
     */
    public int countFormData(String metadataId, Map<String, Object> params);

    /**
     * 根据元数据ID和查询条件分页查询表单数据
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 表单数据列表
     */
    public List<Map<String, Object>> queryFormDataPage(String metadataId, Map<String, Object> params, int pageNum, int pageSize);

    /**
     * 根据元数据ID和字段名查询字段唯一值列表
     *
     * @param metadataId 元数据ID
     * @param fieldName 字段名
     * @return 唯一值列表
     */
    public List<Object> queryFieldDistinctValues(String metadataId, String fieldName);

    /**
     * 根据元数据ID和聚合条件执行聚合查询
     *
     * @param metadataId 元数据ID
     * @param groupFields 分组字段
     * @param aggregateFields 聚合字段和聚合函数
     * @param params 查询参数
     * @return 聚合结果
     */
    public List<Map<String, Object>> queryAggregateData(String metadataId, List<String> groupFields,
                                                        Map<String, String> aggregateFields, Map<String, Object> params);
}