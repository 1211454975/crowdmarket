package com.ruoyi.metadata.domain;


import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户数据库配置对象 tenant_database
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantDatabase extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private String id;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;

    /** 数据库名称 */
    @Excel(name = "数据库名称")
    private String dbName;

    /** 数据库主机 */
    @Excel(name = "数据库主机")
    private String dbHost;

    /** 数据库端口 */
    @Excel(name = "数据库端口")
    private Integer dbPort;

    /** 数据库用户名 */
    @Excel(name = "数据库用户名")
    private String dbUsername;

    /** 数据库密码(加密) */
    @Excel(name = "数据库密码")
    private String dbPassword;

    /** 状态(0停用,1启用) */
    @Excel(name = "状态", readConverterExp = "0=停用,1=启用")
    private Integer status;
}