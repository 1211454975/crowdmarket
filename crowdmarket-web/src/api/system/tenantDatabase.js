import request from '@/utils/request'
, , 


// 查询租户数据库配置列表
export function listTenantDatabase(query) {  
    return request({  
      url: '/system/tenant/database/list',  
      method: 'get',  
      params: query  
    })  
  }  


// 获取租户数据库配置详情DataSource
export function getTenantDatabase() {
  return request({
    url: '/system/tenant/database/ds',
    method: 'get'
  })
}

 
// 新增租户数据库配置  NOT USED
export function addTenantDatabase(data) {  
    return request({  
      url: '/system/tenant/database',  
      method: 'post',  
      data: data  
    })  
  }  
    
  // 修改租户数据库配置  NOT USED
  export function updateTenantDatabase(data) {  
    return request({  
      url: '/system/tenant/database',  
      method: 'put',  
      data: data  
    })  
  }  
    
// 删除租户数据库配置  NOT USED
export function delTenantDatabase(ids) {  
return request({  
    url: '/system/tenant/database/' + ids,  
    method: 'delete'  
})  
}  


// 新增数据库
export function createTenantDatabase(tenantId) {
  return request({
    url: '/system/tenant/database/create/' + tenantId,
    method: 'post'
  })
}

// 测试数据库
export function testConnection() {
  return request({
    url: '/system/tenant/database/test',
    method: 'post'
  })
}
