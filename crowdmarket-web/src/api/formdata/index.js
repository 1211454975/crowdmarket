import request from '@/utils/request'  
  
// Base URL for form data API  
const baseUrl = '/formdata'  
  
// Form data list query  
export function listFormData(metadataId, query) {  
  return request({  
    url: `${baseUrl}/list/${metadataId}`,  
    method: 'get',  
    params: query  
  })  
}  
  
// Get form data detail by ID  
export function getFormData(metadataId, id) {  
  return request({  
    url: `${baseUrl}/${metadataId}/${id}`,  
    method: 'get'  
  })  
}  
  
// Add new form data  
export function addFormData(metadataId, data) {  
  return request({  
    url: `${baseUrl}/${metadataId}`,  
    method: 'post',  
    data: data  
  })  
}  
  
// Update form data  
export function updateFormData(metadataId, data) {  
  return request({  
    url: `${baseUrl}/${metadataId}`,  
    method: 'put',  
    data: data  
  })  
}  
  
// Delete form data  
export function delFormData(metadataId, ids) {  
  return request({  
    url: `${baseUrl}/${metadataId}/${ids}`,  
    method: 'delete'  
  })  
}  
  
// Export form data  
export function exportFormData(metadataId, query) {  
  return request({  
    url: `${baseUrl}/export/${metadataId}`,  
    method: 'get',  
    params: query,  
    responseType: 'blob'  
  })  
}  
  
// Import form data template download  
export function importTemplate(metadataId) {  
  return request({  
    url: `${baseUrl}/importTemplate/${metadataId}`,  
    method: 'get',  
    responseType: 'blob'  
  })  
}  
  
// Import form data  
export function importData(metadataId, file) {  
  const formData = new FormData()  
  formData.append('file', file)  
  return request({  
    url: `${baseUrl}/importData/${metadataId}`,  
    method: 'post',  
    data: formData,  
    headers: {  
      'Content-Type': 'multipart/form-data'  
    }  
  })  
}  
  
// Execute form rule  
export function executeRule(metadataId, ruleId, formData) {  
  return request({  
    url: `${baseUrl}/executeRule/${metadataId}/${ruleId}`,  
    method: 'post',  
    data: formData  
  })  
}  
  
// Get form metadata with fields for rendering  
export function getFormMetadata(metadataId) {  
  return request({  
    url: `${baseUrl}/metadata/${metadataId}`,  
    method: 'get'  
  })  
}  
  
// Get form view configuration  
export function getFormViewConfig(metadataId, viewType) {  
  return request({  
    url: `${baseUrl}/viewConfig/${metadataId}/${viewType}`,  
    method: 'get'  
  })  
}  
  
// Preview form data  
export function previewFormData(metadataId, id) {  
  return request({  
    url: `${baseUrl}/preview/${metadataId}/${id}`,  
    method: 'get'  
  })  
}  
  
// Get form data statistics  
export function getFormDataStats(metadataId, query) {  
  return request({  
    url: `${baseUrl}/stats/${metadataId}`,  
    method: 'get',  
    params: query  
  })  
}