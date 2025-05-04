package com.ruoyi.metadata.domain;


import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单字段元数据对象 form_field_metadata
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FormFieldMetadata extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 字段ID */
    private String fieldId;

    /** 元数据ID */
    @Excel(name = "元数据ID")
    private String metadataId;

    /** 字段名称 */
    @Excel(name = "字段名称")
    private String fieldName;

    /** 字段标签 */
    @Excel(name = "字段标签")
    private String fieldLabel;

    /** 字段类型 */
    @Excel(name = "字段类型")
    private String fieldType;

    /** 数据库列名 */
    @Excel(name = "数据库列名")
    private String columnName;

    /** 数据库列类型 */
    @Excel(name = "数据库列类型")
    private String columnType;

    /** 是否必填(0否1是) */
    @Excel(name = "是否必填", readConverterExp = "0=否,1=是")
    private Integer isRequired;

    /** 是否主键(0否1是) */
    @Excel(name = "是否主键", readConverterExp = "0=否,1=是")
    private Integer isPk;

    /** 是否列表显示(0否1是) */
    @Excel(name = "是否列表显示", readConverterExp = "0=否,1=是")
    private Integer isList;

    /** 是否查询条件(0否1是) */
    @Excel(name = "是否查询条件", readConverterExp = "0=否,1=是")
    private Integer isQuery;

    /** 查询方式(EQ等于,NE不等于,GT大于,LT小于,LIKE模糊,BETWEEN范围) */
    @Excel(name = "查询方式", readConverterExp = "E=Q等于,N=E不等于,G=T大于,L=T小于,L=IKE模糊,B=ETWEEN范围")
    private String queryType;

    /** 显示类型(input文本框,textarea文本域,select下拉框,checkbox复选框,radio单选框,datetime日期控件) */
    @Excel(name = "显示类型", readConverterExp = "i=nput文本框,t=extarea文本域,s=elect下拉框,c=heckbox复选框,r=adio单选框,d=atetime日期控件")
    private String htmlType;

    /** 字典类型 */
    @Excel(name = "字典类型")
    private String dictType;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;
}