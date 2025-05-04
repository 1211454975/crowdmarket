package com.ruoyi.metadata.service;

import java.util.List;
import java.util.Map;

/**
 * 图表数据提供者接口
 *
 * 引入这个Provider，就是避免对ruoyi-formdata模块的直接依赖。
 *
 * @author ruoyi
 */
public interface ChartDataProvider {
    /**
     * 查询表单数据列表
     *
     * @param metadataId 元数据ID
     * @param params 查询参数
     * @return 表单数据集合
     */
    List<Map<String, Object>> selectFormDataList(String metadataId, Map<String, Object> params);
}