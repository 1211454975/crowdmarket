package com.ruoyi.formdata.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ruoyi.framework.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.metadata.domain.TenantDatabase;
import com.ruoyi.metadata.service.ITenantDatabaseService;

/**
 * 租户数据库拦截器
 *
 * 用于自动根据当前租户ID切换数据源
 *
 * @author ruoyi
 */
@Component
public class TenantDatabaseInterceptor implements HandlerInterceptor
{
    private static final Logger log = LoggerFactory.getLogger(TenantDatabaseInterceptor.class);

    @Autowired
    private ITenantDatabaseService tenantDatabaseService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // 获取当前租户ID
        String tenantId = getTenantId(request);

        if (StringUtils.isNotEmpty(tenantId))
        {
            // 检查请求路径是否需要切换到租户数据源
            String requestPath = request.getRequestURI();
            if (isFormDataRequest(requestPath))
            {
                log.debug("切换到租户数据源, tenantId: {}, requestPath: {}", tenantId, requestPath);

                // 验证租户数据库配置是否存在
                TenantDatabase tenantDatabase = tenantDatabaseService.selectTenantDatabaseByTenantId(tenantId);
                if (tenantDatabase != null && tenantDatabase.getStatus() == 1)
                {
                    // 切换到租户数据源
                    DynamicDataSourceContextHolder.setDataSourceType(tenantId);
                    return true;
                }
                else
                {
                    log.warn("租户数据库配置不存在或已停用, tenantId: {}", tenantId);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Tenant database not available");
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        // 不需要处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        // 清除数据源上下文
        String requestPath = request.getRequestURI();
        if (isFormDataRequest(requestPath))
        {
            log.debug("清除租户数据源上下文, requestPath: {}", requestPath);
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 获取当前租户ID
     *
     * @param request HTTP请求
     * @return 租户ID
     */
    private String getTenantId(HttpServletRequest request)
    {
        // 优先从请求头获取
        String tenantId = request.getHeader("X-Tenant-ID");

        // 如果请求头中没有，则从安全上下文获取
        if (StringUtils.isEmpty(tenantId))
        {
            try
            {
                tenantId = SecurityUtils.getCurrComId();
            }
            catch (Exception e)
            {
                log.debug("从安全上下文获取租户ID失败", e);
            }
        }

        // 如果安全上下文中没有，则从请求参数获取
        if (StringUtils.isEmpty(tenantId))
        {
            tenantId = request.getParameter("tenantId");
        }

        return tenantId;
    }

    /**
     * 判断是否为表单数据请求
     *
     * @param requestPath 请求路径
     * @return 是否为表单数据请求
     */
    private boolean isFormDataRequest(String requestPath)
    {
        // 判断请求路径是否以/formdata开头
        return requestPath != null && requestPath.startsWith("/formdata");
    }
}