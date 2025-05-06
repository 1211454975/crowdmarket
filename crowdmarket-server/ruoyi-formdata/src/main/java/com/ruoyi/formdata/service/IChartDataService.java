package com.ruoyi.formdata.service;  
  
import java.util.List;  
import java.util.Map;  
  
/**  
 * 图表数据服务接口  
 *   
 * 负责根据图表配置查询和处理图表数据  
 *   
 * @author ruoyi  
 */  
public interface IChartDataService {  
      
    /**  
     * 获取图表数据  
     *   
     * @param chartId 图表ID  
     * @param params 查询参数  
     * @return 图表数据  
     */  
    Map<String, Object> getChartData(String chartId, Map<String, Object> params);  
      
    /**  
     * 获取图表维度数据  
     *   
     * @param chartId 图表ID  
     * @param params 查询参数  
     * @return 维度数据列表  
     */  
    List<String> getDimensionData(String chartId, Map<String, Object> params);  
      
    /**  
     * 获取图表度量数据  
     *   
     * @param chartId 图表ID  
     * @param dimensionValue 维度值  
     * @param params 查询参数  
     * @return 度量数据列表  
     */  
    List<Object> getMeasureData(String chartId, String dimensionValue, Map<String, Object> params);  
      
    /**  
     * 执行自定义图表查询  
     *   
     * @param metadataId 元数据ID  
     * @param dimensions 维度字段列表  
     * @param measures 度量字段列表  
     * @param filters 过滤条件  
     * @param sorts 排序条件  
     * @param limit 数据限制  
     * @return 查询结果  
     */  
    Map<String, Object> executeChartQuery(  
        String metadataId,   
        List<String> dimensions,   
        List<Map<String, String>> measures,   
        Map<String, Object> filters,   
        List<Map<String, String>> sorts,   
        Integer limit  
    );  
      
    /**  
     * 导出图表数据  
     *   
     * @param chartId 图表ID  
     * @param params 查询参数  
     * @param fileType 文件类型(excel, csv, pdf)  
     * @return 文件字节数组  
     */  
    byte[] exportChartData(String chartId, Map<String, Object> params, String fileType);  
      
    /**  
     * 获取图表数据统计信息  
     *   
     * @param chartId 图表ID  
     * @param params 查询参数  
     * @return 统计信息  
     */  
    Map<String, Object> getChartStatistics(String chartId, Map<String, Object> params);  
      
    /**  
     * 刷新图表数据缓存  
     *   
     * @param chartId 图表ID  
     * @return 是否成功  
     */  
    boolean refreshChartDataCache(String chartId);  
}