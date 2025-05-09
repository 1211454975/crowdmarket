package com.ruoyi.formdata.datasource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 租户数据源注解，用于标记需要切换到租户数据库的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantDataSource {
    /**
     * 租户ID
     */
    String value() default "";

    /**
     * 参数名，用于从方法参数中获取租户ID
     */
    String paramName() default "";
}