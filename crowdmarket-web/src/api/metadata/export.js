import request from '@/utils/request'  
  
// API base path for chart export operations  
const baseUrl = '/metadata/export'  
  
/**  
 * 导出图表为PDF  
 *   
 * @param {Object} data 导出参数  
 * @returns {Promise} 请求Promise，返回Blob对象  
 */  
export function exportChartToPdf(data) {  
  return request({  
    url: baseUrl + '/pdf',  
    method: 'post',  
    data: data,  
    responseType: 'blob'  
  })  
}  
  
/**  
 * 获取导出历史记录  
 *   
 * @param {Object} query 查询参数  
 * @returns {Promise} 请求Promise  
 */  
export function listExportHistory(query) {  
  return request({  
    url: baseUrl + '/history',  
    method: 'get',  
    params: query  
  })  
}  
  
/**  
 * 批量导出图表  
 *   
 * @param {Array} chartIds 图表ID数组  
 * @returns {Promise} 请求Promise，返回Blob对象  
 */  
export function batchExportCharts(chartIds) {  
  return request({  
    url: baseUrl + '/batch',  
    method: 'post',  
    data: { chartIds },  
    responseType: 'blob'  
  })  
}  
  
/**  
 * 导出图表为Excel  
 *   
 * @param {String} chartId 图表ID  
 * @param {Object} options 导出选项  
 * @returns {Promise} 请求Promise，返回Blob对象  
 */  
export function exportChartToExcel(chartId, options) {  
  return request({  
    url: baseUrl + '/excel/' + chartId,  
    method: 'get',  
    params: options,  
    responseType: 'blob'  
  })  
}  
  
/**  
 * 导出图表数据为CSV  
 *   
 * @param {String} chartId 图表ID  
 * @param {Object} options 导出选项  
 * @returns {Promise} 请求Promise，返回Blob对象  
 */  
export function exportChartToCsv(chartId, options) {  
  return request({  
    url: baseUrl + '/csv/' + chartId,  
    method: 'get',  
    params: options,  
    responseType: 'blob'  
  })  
}  
  
/**  
 * 获取导出模板  
 *   
 * @param {String} chartType 图表类型  
 * @returns {Promise} 请求Promise，返回Blob对象  
 */  
export function getExportTemplate(chartType) {  
  return request({  
    url: baseUrl + '/template/' + chartType,  
    method: 'get',  
    responseType: 'blob'  
  })  
}