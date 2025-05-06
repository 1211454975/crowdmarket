// src/api/metadata/field.js  
import request from '@/utils/request'  
  
// 查询表单字段元数据列表  
export function listFormFieldMetadata(query) {  
  return request({  
    url: '/metadata/field/list',  
    method: 'get',  
    params: query  
  })  
}  
  
// 查询表单字段元数据详细  
export function getFormFieldMetadata(fieldId) {  
  return request({  
    url: '/metadata/field/' + fieldId,  
    method: 'get'  
  })  
}  
  
// 新增表单字段元数据  
export function addFormFieldMetadata(data) {  
  return request({  
    url: '/metadata/field',  
    method: 'post',  
    data: data  
  })  
}  
  
// 修改表单字段元数据  
export function updateFormFieldMetadata(data) {  
  return request({  
    url: '/metadata/field',  
    method: 'put',  
    data: data  
  })  
}  
  
// 删除表单字段元数据  
export function delFormFieldMetadata(fieldId) {  
  return request({  
    url: '/metadata/field/' + fieldId,  
    method: 'delete'  
  })  
}