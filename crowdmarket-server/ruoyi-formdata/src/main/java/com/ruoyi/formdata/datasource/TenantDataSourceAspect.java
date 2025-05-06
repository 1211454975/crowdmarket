package com.ruoyi.formdata.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 租户数据源切面，用于处理带有@TenantDataSource注解的方法
 */
@Aspect
@Component
public class TenantDataSourceAspect {
    private static final Logger log = LoggerFactory.getLogger(TenantDataSourceAspect.class);

    @Autowired
    private DynamicDataSourceService dataSourceService;

    @Around("@annotation(com.ruoyi.formdata.datasource.TenantDataSource)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        TenantDataSource annotation = method.getAnnotation(TenantDataSource.class);
        if (annotation != null) {
            String tenantId = determineTenantId(point, annotation);
            if (tenantId != null) {
                try {
                    // 设置租户上下文
                    TenantContextHolder.setTenantId(tenantId);
                    // 确保租户数据源已初始化
                    dataSourceService.initTenantDataSource(tenantId);

                    log.debug("Switch to tenant database: {}", tenantId);
                    return point.proceed();
                } finally {
                    // 清除租户上下文
                    TenantContextHolder.clear();
                    log.debug("Restored to default database");
                }
            }
        }

        return point.proceed();
    }

    /**
     * 确定租户ID
     *
     * @param point 连接点
     * @param annotation 注解
     * @return 租户ID
     */
    private String determineTenantId(ProceedingJoinPoint point, TenantDataSource annotation) {
        String tenantId = annotation.value();

        // 如果注解中指定了租户ID，则直接使用
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId;
        }

        // 如果注解中指定了参数名，则从方法参数中获取
        String paramName = annotation.paramName();
        if (paramName != null && !paramName.isEmpty()) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = point.getArgs();

            for (int i = 0; i < paramNames.length; i++) {
                if (paramName.equals(paramNames[i]) && args[i] != null) {
                    return args[i].toString();
                }
            }
        }

        return null;
    }
}