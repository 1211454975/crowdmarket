// src/api/metadata/form.js  
import request from '@/utils/request'  
  
// 查询表单元数据列表  
export function listFormMetadata(query) {  
  return request({  
    url: '/metadata/form/list',  
    method: 'get',  
    params: query  
  })  
}  
  
// 查询表单元数据详细  
export function getFormMetadata(metadataId) {  
  return request({  
    url: '/metadata/form/' + metadataId,  
    method: 'get'  
  })  
}  
  
// 新增表单元数据  
export function addFormMetadata(data) {  
  return request({  
    url: '/metadata/form',  
    method: 'post',  
    data: data  
  })  
}  
  
// 修改表单元数据  
export function updateFormMetadata(data) {  
  return request({  
    url: '/metadata/form',  
    method: 'put',  
    data: data  
  })  
}  
  
// 删除表单元数据  
export function delFormMetadata(metadataId) {  
  return request({  
    url: '/metadata/form/' + metadataId,  
    method: 'delete'  
  })  
}  
  
// 从数据库表导入元数据  
export function importFromTable(tableName) {  
  return request({  
    url: '/metadata/form/import/' + tableName,  
    method: 'post'  
  })  
}