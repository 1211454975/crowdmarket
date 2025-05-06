import request from '@/utils/request'  
  
// API base path for rule action operations  
const baseUrl = '/metadata/action'  
  
/**  
 * 查询规则动作列表  
 *   
 * @param {Object} query 查询参数  
 * @returns {Promise} 请求Promise  
 */  
export function listActions(query) {  
  return request({  
    url: baseUrl + '/list',  
    method: 'get',  
    params: query  
  })  
}  
  
/**  
 * 查询规则动作详细信息  
 *   
 * @param {String} actionId 动作ID  
 * @returns {Promise} 请求Promise  
 */  
export function getAction(actionId) {  
  return request({  
    url: baseUrl + '/' + actionId,  
    method: 'get'  
  })  
}  
  
/**  
 * 根据规则ID查询动作列表  
 *   
 * @param {String} ruleId 规则ID  
 * @returns {Promise} 请求Promise  
 */  
export function listActionsByRuleId(ruleId) {  
  return request({  
    url: baseUrl + '/rule/' + ruleId,  
    method: 'get'  
  })  
}  
  
/**  
 * 新增规则动作  
 *   
 * @param {Object} data 动作数据  
 * @returns {Promise} 请求Promise  
 */  
export function addAction(data) {  
  return request({  
    url: baseUrl,  
    method: 'post',  
    data: data  
  })  
}  
  
/**  
 * 修改规则动作  
 *   
 * @param {Object} data 动作数据  
 * @returns {Promise} 请求Promise  
 */  
export function updateAction(data) {  
  return request({  
    url: baseUrl,  
    method: 'put',  
    data: data  
  })  
}  
  
/**  
 * 删除规则动作  
 *   
 * @param {String} actionId 动作ID  
 * @returns {Promise} 请求Promise  
 */  
export function deleteAction(actionId) {  
  return request({  
    url: baseUrl + '/' + actionId,  
    method: 'delete'  
  })  
}  
  
/**  
 * 批量删除规则动作  
 *   
 * @param {Array} actionIds 动作ID数组  
 * @returns {Promise} 请求Promise  
 */  
export function deleteActions(actionIds) {  
  return request({  
    url: baseUrl + '/batch/' + actionIds.join(','),  
    method: 'delete'  
  })  
}  
  
/**  
 * 获取动作类型列表  
 *   
 * @returns {Promise} 请求Promise  
 */  
export function listActionTypes() {  
  return request({  
    url: baseUrl + '/types',  
    method: 'get'  
  })  
}  
  
/**  
 * 根据字段ID获取可用的动作类型  
 *   
 * @param {String} fieldId 字段ID  
 * @returns {Promise} 请求Promise  
 */  
export function getActionTypesByFieldId(fieldId) {  
  return request({  
    url: baseUrl + '/types/field/' + fieldId,  
    method: 'get'  
  })  
}  
  
/**  
 * 验证动作脚本  
 *   
 * @param {Object} data 脚本数据  
 * @returns {Promise} 请求Promise  
 */  
export function validateActionScript(data) {  
  return request({  
    url: baseUrl + '/validate/script',  
    method: 'post',  
    data: data  
  })  
}  
  
/**  
 * 测试动作执行  
 *   
 * @param {Object} data 测试数据  
 * @returns {Promise} 请求Promise  
 */  
export function testActionExecution(data) {  
  return request({  
    url: baseUrl + '/test',  
    method: 'post',  
    data: data  
  })  
}