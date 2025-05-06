import request from '@/utils/request'  
  
// API base path for rule condition operations  
const baseUrl = '/metadata/condition'  
  
/**  
 * 查询规则条件列表  
 *   
 * @param {Object} query 查询参数  
 * @returns {Promise} 请求Promise  
 */  
export function listConditions(query) {  
  return request({  
    url: baseUrl + '/list',  
    method: 'get',  
    params: query  
  })  
}  
  
/**  
 * 查询规则条件详细信息  
 *   
 * @param {String} conditionId 条件ID  
 * @returns {Promise} 请求Promise  
 */  
export function getCondition(conditionId) {  
  return request({  
    url: baseUrl + '/' + conditionId,  
    method: 'get'  
  })  
}  
  
/**  
 * 根据规则ID查询条件列表  
 *   
 * @param {String} ruleId 规则ID  
 * @returns {Promise} 请求Promise  
 */  
export function listConditionsByRuleId(ruleId) {  
  return request({  
    url: baseUrl + '/rule/' + ruleId,  
    method: 'get'  
  })  
}  
  
/**  
 * 新增规则条件  
 *   
 * @param {Object} data 条件数据  
 * @returns {Promise} 请求Promise  
 */  
export function addCondition(data) {  
  return request({  
    url: baseUrl,  
    method: 'post',  
    data: data  
  })  
}  
  
/**  
 * 修改规则条件  
 *   
 * @param {Object} data 条件数据  
 * @returns {Promise} 请求Promise  
 */  
export function updateCondition(data) {  
  return request({  
    url: baseUrl,  
    method: 'put',  
    data: data  
  })  
}  
  
/**  
 * 删除规则条件  
 *   
 * @param {String} conditionId 条件ID  
 * @returns {Promise} 请求Promise  
 */  
export function deleteCondition(conditionId) {  
  return request({  
    url: baseUrl + '/' + conditionId,  
    method: 'delete'  
  })  
}  
  
/**  
 * 批量删除规则条件  
 *   
 * @param {Array} conditionIds 条件ID数组  
 * @returns {Promise} 请求Promise  
 */  
export function deleteConditions(conditionIds) {  
  return request({  
    url: baseUrl + '/batch/' + conditionIds.join(','),  
    method: 'delete'  
  })  
}  
  
/**  
 * 获取条件操作符列表  
 *   
 * @returns {Promise} 请求Promise  
 */  
export function listOperators() {  
  return request({  
    url: baseUrl + '/operators',  
    method: 'get'  
  })  
}  
  
/**  
 * 获取条件逻辑类型列表  
 *   
 * @returns {Promise} 请求Promise  
 */  
export function listLogicTypes() {  
  return request({  
    url: baseUrl + '/logictypes',  
    method: 'get'  
  })  
}  
  
/**  
 * 根据字段类型获取适用的操作符  
 *   
 * @param {String} fieldType 字段类型  
 * @returns {Promise} 请求Promise  
 */  
export function getOperatorsByFieldType(fieldType) {  
  return request({  
    url: baseUrl + '/operators/fieldtype/' + fieldType,  
    method: 'get'  
  })  
}