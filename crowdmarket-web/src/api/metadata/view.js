import request from '@/utils/request'  
  
// API base URL for form view configuration  
const baseUrl = '/metadata/view'  
  
/**  
 * Get view list by metadata ID  
 * @param {string} metadataId - Form metadata ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getViewList(metadataId) {  
  return request({  
    url: `${baseUrl}/list`,  
    method: 'get',  
    params: {  
      metadataId  
    }  
  })  
}  
  
/**  
 * Get view details by ID  
 * @param {string} viewId - View ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getView(viewId) {  
  return request({  
    url: `${baseUrl}/${viewId}`,  
    method: 'get'  
  })  
}  
  
/**  
 * Get default view by metadata ID and view type  
 * @param {string} metadataId - Form metadata ID  
 * @param {string} viewType - View type (form, list, detail)  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getDefaultView(metadataId, viewType) {  
  return request({  
    url: `${baseUrl}/default`,  
    method: 'get',  
    params: {  
      metadataId,  
      viewType  
    }  
  })  
}  
  
/**  
 * Save form view configuration  
 * @param {Object} data - View configuration data  
 * @returns {Promise} - Promise object representing the request  
 */  
export function saveFormViewConfig(data) {  
  return request({  
    url: `${baseUrl}`,  
    method: data.viewId ? 'put' : 'post',  
    data: data  
  })  
}  
  
/**  
 * Delete view by ID  
 * @param {string} viewId - View ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function deleteView(viewId) {  
  return request({  
    url: `${baseUrl}/${viewId}`,  
    method: 'delete'  
  })  
}  
  
/**  
 * Set view as default  
 * @param {string} viewId - View ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function setDefaultView(viewId) {  
  return request({  
    url: `${baseUrl}/default/${viewId}`,  
    method: 'put'  
  })  
}  
  
/**  
 * Preview view  
 * @param {string} viewId - View ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function previewView(viewId) {  
  return request({  
    url: `${baseUrl}/preview/${viewId}`,  
    method: 'get'  
  })  
}  
  
/**  
 * Export view configuration  
 * @param {string} viewId - View ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function exportViewConfig(viewId) {  
  return request({  
    url: `${baseUrl}/export/${viewId}`,  
    method: 'get',  
    responseType: 'blob'  
  })  
}  
  
/**  
 * Import view configuration  
 * @param {FormData} formData - Form data containing the configuration file  
 * @returns {Promise} - Promise object representing the request  
 */  
export function importViewConfig(formData) {  
  return request({  
    url: `${baseUrl}/import`,  
    method: 'post',  
    data: formData,  
    headers: {  
      'Content-Type': 'multipart/form-data'  
    }  
  })  
}