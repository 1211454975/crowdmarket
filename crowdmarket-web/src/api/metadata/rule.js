import request from '@/utils/request'  
  
// API base URL for form rule management  
const baseUrl = '/metadata/rule'  
  
/**  
 * Get rule list by metadata ID  
 * @param {string} metadataId - Form metadata ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getRuleList(metadataId) {  
  return request({  
    url: `${baseUrl}/list`,  
    method: 'get',  
    params: {  
      metadataId  
    }  
  })  
}  
  
/**  
 * Get rule details by ID  
 * @param {string} ruleId - Rule ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getRule(ruleId) {  
  return request({  
    url: `${baseUrl}/${ruleId}`,  
    method: 'get'  
  })  
}  
  
/**  
 * Save rule  
 * @param {Object} data - Rule data  
 * @returns {Promise} - Promise object representing the request  
 */  
export function saveRule(data) {  
  return request({  
    url: `${baseUrl}`,  
    method: data.ruleId ? 'put' : 'post',  
    data: data  
  })  
}  
  
/**  
 * Delete rule by ID  
 * @param {string} ruleId - Rule ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function deleteRule(ruleId) {  
  return request({  
    url: `${baseUrl}/${ruleId}`,  
    method: 'delete'  
  })  
}  
  
/**  
 * Update rule status (active/inactive)  
 * @param {Object} data - Status update data  
 * @returns {Promise} - Promise object representing the request  
 */  
export function updateRuleStatus(data) {  
  return request({  
    url: `${baseUrl}/status`,  
    method: 'put',  
    data: data  
  })  
}  
  
/**  
 * Get rule conditions by rule ID  
 * @param {string} ruleId - Rule ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getRuleConditions(ruleId) {  
  return request({  
    url: `${baseUrl}/condition/list`,  
    method: 'get',  
    params: {  
      ruleId  
    }  
  })  
}  
  
/**  
 * Save rule condition  
 * @param {Object} data - Condition data  
 * @returns {Promise} - Promise object representing the request  
 */  
export function saveRuleCondition(data) {  
  return request({  
    url: `${baseUrl}/condition`,  
    method: data.conditionId ? 'put' : 'post',  
    data: data  
  })  
}  
  
/**  
 * Delete rule condition by ID  
 * @param {string} conditionId - Condition ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function deleteRuleCondition(conditionId) {  
  return request({  
    url: `${baseUrl}/condition/${conditionId}`,  
    method: 'delete'  
  })  
}  
  
/**  
 * Get rule actions by rule ID  
 * @param {string} ruleId - Rule ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function getRuleActions(ruleId) {  
  return request({  
    url: `${baseUrl}/action/list`,  
    method: 'get',  
    params: {  
      ruleId  
    }  
  })  
}  
  
/**  
 * Save rule action  
 * @param {Object} data - Action data  
 * @returns {Promise} - Promise object representing the request  
 */  
export function saveRuleAction(data) {  
  return request({  
    url: `${baseUrl}/action`,  
    method: data.actionId ? 'put' : 'post',  
    data: data  
  })  
}  
  
/**  
 * Delete rule action by ID  
 * @param {string} actionId - Action ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function deleteRuleAction(actionId) {  
  return request({  
    url: `${baseUrl}/action/${actionId}`,  
    method: 'delete'  
  })  
}  
  
/**  
 * Test rule execution  
 * @param {string} ruleId - Rule ID  
 * @param {Object} testData - Test data for rule execution  
 * @returns {Promise} - Promise object representing the request  
 */  
export function testRule(ruleId, testData) {  
  return request({  
    url: `${baseUrl}/test/${ruleId}`,  
    method: 'post',  
    data: testData  
  })  
}  
  
/**  
 * Export rule configuration  
 * @param {string} ruleId - Rule ID  
 * @returns {Promise} - Promise object representing the request  
 */  
export function exportRuleConfig(ruleId) {  
  return request({  
    url: `${baseUrl}/export/${ruleId}`,  
    method: 'get',  
    responseType: 'blob'  
  })  
}  
  
/**  
 * Import rule configuration  
 * @param {FormData} formData - Form data containing the configuration file  
 * @returns {Promise} - Promise object representing the request  
 */  
export function importRuleConfig(formData) {  
  return request({  
    url: `${baseUrl}/import`,  
    method: 'post',  
    data: formData,  
    headers: {  
      'Content-Type': 'multipart/form-data'  
    }  
  })  
}