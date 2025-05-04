package com.ruoyi.metadata.domain;


import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 表单租户表映射对象 form_tenant_table_mapping
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FormTenantTableMapping  extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 映射ID */
    private String mappingId;

    /** 元数据ID */
    @Excel(name = "元数据ID")
    private String metadataId;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;

    /** 租户数据库中的表名 */
    @Excel(name = "租户数据库中的表名")
    private String tableName;

    /** 表是否已创建(0否1是) */
    @Excel(name = "表是否已创建", readConverterExp = "0=否,1=是")
    private Integer isCreated;

    /** 创建时间 */
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;

    /** 更新时间 */
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;
}