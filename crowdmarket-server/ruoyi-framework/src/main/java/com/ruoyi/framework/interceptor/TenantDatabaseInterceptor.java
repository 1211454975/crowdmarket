package com.ruoyi.framework.interceptor;


import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.security.LoginUser;
import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.framework.datasource.TenantContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TenantDatabaseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            // 设置租户ID
            String tenantId = loginUser.getUser().getComId();
            if (StringUtils.isNotEmpty(tenantId)) {
                TenantContextHolder.setTenantId(tenantId);

                // 如果是超级管理员，标记为系统操作
                if (loginUser.getUser().getSuperAdminFlag() == 1) {
                    TenantContextHolder.setSystemOperation(true);
                }
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除租户上下文
        TenantContextHolder.clearTenantId();
        TenantContextHolder.clearSystemOperation();
    }
}
