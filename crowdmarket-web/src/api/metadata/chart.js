import request from '@/utils/request'  
  
// API base URL for chart management  
const baseUrl = '/metadata/chart'  
  
/**  
 * Get chart list by metadata ID  
 * @param {string} metadataId - Form metadata ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getChartList(metadataId) {  
  return request({  
    url: `${baseUrl}/list`,  
    method: 'get',  
    params: {  
      metadataId  
    }  
  })  
}  
  
/**  
 * Get all charts  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getAllCharts() {  
  return request({  
    url: `${baseUrl}/all`,  
    method: 'get'  
  })  
}  
  
/**  
 * Get chart details by ID  
 * @param {string} chartId - Chart ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getChart(chartId) {  
  return request({  
    url: `${baseUrl}/${chartId}`,  
    method: 'get'  
  })  
}  
  
/**  
 * Save chart  
 * @param {Object} data - Chart data  
 * @returns {Promise} - Promise object representing the request  
 */  
export function saveChart(data) {  
  return request({  
    url: `${baseUrl}`,  
    method: data.chartId ? 'put' : 'post',  
    data: data  
  })  
}  
  
/**  
 * Delete chart by ID  
 * @param {string} chartId - Chart ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function deleteChart(chartId) {  
  return request({  
    url: `${baseUrl}/${chartId}`,  
    method: 'delete'  
  })  
}  
  
/**  
 * Update chart status  
 * @param {Object} data - Status update data  
 * @returns {Promise} - Promise object representing the request  
 */  
export function updateChartStatus(data) {  
  return request({  
    url: `${baseUrl}/status`,  
    method: 'put',  
    data: data  
  })  
}  
  
/**  
 * Get chart data for rendering  
 * @param {Object} params - Chart data parameters  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getChartData(params) {  
  return request({  
    url: `${baseUrl}/data`,  
    method: 'post',  
    data: params  
  })  
}  
  
/**  
 * Export chart as image  
 * @param {string} chartId - Chart ID  
 * @param {string} type - Image type (png, jpeg)  
 * @param {number} width - Image width  
 * @param {number} height - Image height  
 * @returns {Promise} - Promise object representing the request  
 */  
export function exportChartImage(chartId, type = 'png', width = 800, height = 600) {  
  return request({  
    url: `${baseUrl}/export/image/${chartId}`,  
    method: 'get',  
    params: {  
      type,  
      width,  
      height  
    },  
    responseType: 'blob'  
  })  
}  
  
/**  
 * Export chart to PDF  
 * @param {Object} data - PDF export parameters  
 * @returns {Promise} - Promise object representing the request  
 */  
export function exportChartToPdf(data) {  
  return request({  
    url: `${baseUrl}/export/pdf`,  
    method: 'post',  
    data: data,  
    responseType: 'blob'  
  })  
}  
  
/**  
 * Get chart config by ID  
 * @param {string} chartId - Chart ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getChartConfig(chartId) {  
  return request({  
    url: `${baseUrl}/config/${chartId}`,  
    method: 'get'  
  })  
}  
  
/**  
 * Save chart config  
 * @param {Object} data - Chart config data  
 * @returns {Promise} - Promise object representing the request  
 */  
export function saveChartConfig(data) {  
  return request({  
    url: `${baseUrl}/config`,  
    method: data.configId ? 'put' : 'post',  
    data: data  
  })  
}  
  
/**  
 * Export chart configuration  
 * @param {string} chartId - Chart ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function exportChartConfig(chartId) {  
  return request({  
    url: `${baseUrl}/export/config/${chartId}`,  
    method: 'get',  
    responseType: 'blob'  
  })  
}  
  
/**  
 * Import chart configuration  
 * @param {FormData} formData - Form data containing the configuration file  
 * @returns {Promise} - Promise object representing the request  
 */  
export function importChartConfig(formData) {  
  return request({  
    url: `${baseUrl}/import/config`,  
    method: 'post',  
    data: formData,  
    headers: {  
      'Content-Type': 'multipart/form-data'  
    }  
  })  
}