import request from '@/utils/request'  
  
// API base path for metadata import operations  
const baseUrl = '/metadata/import'  
  
/**  
 * 获取数据库表列表  
 *   
 * @param {Object} query 查询参数  
 * @returns {Promise} 请求Promise  
 */  
export function listDbTables(query) {  
  return request({  
    url: baseUrl + '/tables',  
    method: 'get',  
    params: query  
  })  
}  
  
/**  
 * 获取数据库表字段列表  
 *   
 * @param {String} tableName 表名  
 * @returns {Promise} 请求Promise  
 */  
export function listTableColumns(tableName) {  
  return request({  
    url: baseUrl + '/columns/' + tableName,  
    method: 'get'  
  })  
}  
  
/**  
 * 预览元数据  
 *   
 * @param {String} tableName 表名  
 * @returns {Promise} 请求Promise  
 */  
export function previewMetadata(tableName) {  
  return request({  
    url: baseUrl + '/preview/' + tableName,  
    method: 'get'  
  })  
}  
  
/**  
 * 导入表结构为元数据  
 *   
 * @param {Object} data 导入参数  
 * @returns {Promise} 请求Promise  
 */  
export function importTableMetadata(data) {  
  return request({  
    url: baseUrl + '/import',  
    method: 'post',  
    data: data  
  })  
}  
  
/**  
 * 获取导入历史记录  
 *   
 * @param {Object} query 查询参数  
 * @returns {Promise} 请求Promise  
 */  
export function listImportHistory(query) {  
  return request({  
    url: baseUrl + '/history',  
    method: 'get',  
    params: query  
  })  
}  
  
/**  
 * 检查表名是否可用  
 *   
 * @param {String} tableName 表名  
 * @param {String} tenantId 租户ID  
 * @returns {Promise} 请求Promise  
 */  
export function checkTableNameAvailable(tableName, tenantId) {  
  return request({  
    url: baseUrl + '/check',  
    method: 'get',  
    params: {  
      tableName: tableName,  
      tenantId: tenantId  
    }  
  })  
}