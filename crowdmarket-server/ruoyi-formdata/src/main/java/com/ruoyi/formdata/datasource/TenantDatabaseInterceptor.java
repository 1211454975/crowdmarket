package com.ruoyi.formdata.datasource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.ruoyi.common.utils.StringUtils;

/**
 * 租户数据库拦截器，用于设置当前线程的租户上下文
 */
@Component
public class TenantDatabaseInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(TenantDatabaseInterceptor.class);

//    @Autowired
    private DynamicDataSourceService dataSourceService;
    public TenantDatabaseInterceptor(DynamicDataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求中获取租户ID
        String tenantId = getTenantIdFromRequest(request);

        if (StringUtils.isNotEmpty(tenantId)) {
            log.debug("Setting tenant context for tenant: {}", tenantId);
            TenantContextHolder.setTenantId(tenantId);

            // 确保租户数据源已初始化
            ensureTenantDataSourceInitialized(tenantId);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 不需要额外处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除租户上下文
        TenantContextHolder.clear();
    }

    /**
     * 从请求中获取租户ID
     *
     * @param request HTTP请求
     * @return 租户ID
     */
    private String getTenantIdFromRequest(HttpServletRequest request) {
        // 优先从请求头中获取
        String tenantId = request.getHeader("X-Tenant-ID");

        // 如果请求头中没有，则从会话中获取
        if (StringUtils.isEmpty(tenantId) && request.getSession() != null) {
            tenantId = (String) request.getSession().getAttribute("tenantId");
        }

        // 如果会话中没有，则从请求参数中获取
        if (StringUtils.isEmpty(tenantId)) {
            tenantId = request.getParameter("tenantId");
        }

        return tenantId;
    }

    /**
     * 确保租户数据源已初始化
     *
     * @param tenantId 租户ID
     */
    private void ensureTenantDataSourceInitialized(String tenantId) {
        try {
            dataSourceService.initTenantDataSource(tenantId);
        } catch (Exception e) {
            log.error("Failed to initialize data source for tenant: {}", tenantId, e);
        }
    }
}