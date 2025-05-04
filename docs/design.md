
# 表单管理系统模块划分分析

基于之前讨论的元数据驱动表单管理系统的综合设计文档，我理解您希望将功能分为两个独立的Maven模块：

设计相关功能：与表单、规则和图表的定义和配置相关的功能
应用相关功能：与使用和交互已定义表单、规则和图表相关的功能

# 这种划分是否合理？
是的，这种划分非常合理，符合良好的软件架构原则，原因如下：

关注点分离：设计时和运行时的关注点本质上是不同的
不同用户角色：表单设计者和表单使用者有不同的需求
部署灵活性：您可以只在最终用户环境中部署应用模块
开发独立性：团队可以独立开发各个模块
测试隔离：每个模块可以独立测试

# 建议的模块结构
以下是我建议的两个Maven模块的结构：

```
crowdmarket-server/  
├── ruoyi-form-design/             # 设计相关功能  
│   ├── src/main/java/com/ruoyi/form/design/  
│   │   ├── controller/            # 设计操作的REST控制器  
│   │   ├── domain/                # 元数据的实体类  
│   │   ├── mapper/                # 元数据的MyBatis映射器  
│   │   ├── service/               # 表单设计服务  
│   │   └── util/                  # 设计工具类  
│   └── src/main/resources/  
│       ├── mapper/                # MyBatis XML映射  
│       └── vm/                    # Velocity模板（如需要）  
│  
├── ruoyi-form-runtime/            # 应用相关功能  
│   ├── src/main/java/com/ruoyi/form/runtime/  
│   │   ├── controller/            # 运行时操作的REST控制器  
│   │   ├── domain/                # 运行时数据的实体类  
│   │   ├── mapper/                # 运行时数据的MyBatis映射器  
│   │   ├── service/               # 表单运行时服务  
│   │   └── util/                  # 运行时工具类  
│   └── src/main/resources/  
│       └── mapper/                # MyBatis XML映射  
```

# 详细功能划分
ruoyi-form-design模块
此模块将包含所有与表单设计和配置相关的功能：

1. 表单元数据管理
表单定义和结构
字段元数据管理
视图配置
数据库元数据提取

2. 业务规则设计
规则定义
条件配置
动作配置

3. 图表设计
图表元数据管理
图表配置
维度和度量定义

4. 租户数据库配置
数据库连接管理
数据库创建和配置


# ruoyi-form-runtime模块
此模块将包含所有与表单使用和交互相关的功能：

1. 表单渲染和数据管理
基于元数据的表单渲染
数据CRUD操作
数据导入/导出
在租户数据库中生成表

2. 规则执行
规则评估引擎
条件评估
动作执行

3. 图表渲染
图表数据查询
图表渲染
图表导出

4. 租户数据库运行时
动态数据源路由
租户上下文管理
数据库连接池管理

# 模块间依赖关系
ruoyi-form-runtime模块将依赖于ruoyi-form-design模块，因为它需要访问元数据定义来渲染表单和执行规则。但是，ruoyi-form-design模块不应该依赖于ruoyi-form-runtime模块。


# Maven POM配置
## 父POM (crowdmarket-server/pom.xml)
将新模块添加到父POM中：

```
<modules>  
    <!-- 现有模块 -->  
    <module>ruoyi-common</module>  
    <module>ruoyi-framework</module>  
    <module>ruoyi-system</module>  
    <module>ruoyi-tool</module>  
    <module>ruoyi-business</module>  
    <module>ruoyi-admin</module>  
    <module>ruoyi-monitor</module>  
      
    <!-- 新的表单管理模块 -->  
    <module>ruoyi-form-design</module>  
    <module>ruoyi-form-runtime</module>  
</modules>
```

## ruoyi-form-design/pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>  
<project xmlns="http://maven.apache.org/POM/4.0.0"  
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">  
    <parent>  
        <artifactId>ruoyi-vue-multi-tenant</artifactId>  
        <groupId>com.ruoyi</groupId>  
        <version>1.0.0</version>  
    </parent>  
    <modelVersion>4.0.0</modelVersion>  
  
    <artifactId>ruoyi-form-design</artifactId>  
    <description>表单设计模块</description>  
  
    <dependencies>  
        <!-- 依赖common模块 -->  
        <dependency>  
            <groupId>com.ruoyi</groupId>  
            <artifactId>ruoyi-common</artifactId>  
        </dependency>  
          
        <!-- 依赖system模块 -->  
        <dependency>  
            <groupId>com.ruoyi</groupId>  
            <artifactId>ruoyi-system</artifactId>  
        </dependency>  
          
        <!-- 依赖tool模块 -->  
        <dependency>  
            <groupId>com.ruoyi</groupId>  
            <artifactId>ruoyi-tool</artifactId>  
        </dependency>  
    </dependencies>  
</project>
```

## ruoyi-form-runtime/pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>  
<project xmlns="http://maven.apache.org/POM/4.0.0"  
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">  
    <parent>  
        <artifactId>ruoyi-vue-multi-tenant</artifactId>  
        <groupId>com.ruoyi</groupId>  
        <version>1.0.0</version>  
    </parent>  
    <modelVersion>4.0.0</modelVersion>  
  
    <artifactId>ruoyi-form-runtime</artifactId>  
    <description>表单运行时模块</description>  
  
    <dependencies>  
        <!-- 依赖form-design模块 -->  
        <dependency>  
            <groupId>com.ruoyi</groupId>  
            <artifactId>ruoyi-form-design</artifactId>  
        </dependency>  
          
        <!-- 依赖common模块 -->  
        <dependency>  
            <groupId>com.ruoyi</groupId>  
            <artifactId>ruoyi-common</artifactId>  
        </dependency>  
          
        <!-- 依赖framework模块 -->  
        <dependency>  
            <groupId>com.ruoyi</groupId>  
            <artifactId>ruoyi-framework</artifactId>  
        </dependency>  
    </dependencies>  
</project>
```

# 实体类分布
## 设计模块实体
```
// com.ruoyi.form.design.domain.FormMetadata  
public class FormMetadata extends BaseEntity {  
    private String metadataId;  
    private String metadataName;  
    private String metadataDesc;  
    private Integer status;  
    private String tenantId;  
    // Getters and setters  
}  
  
// com.ruoyi.form.design.domain.FormFieldMetadata  
public class FormFieldMetadata extends BaseEntity {  
    private String fieldId;  
    private String metadataId;  
    private String fieldName;  
    private String fieldLabel;  
    private String fieldType;  
    private String columnName;  
    private String columnType;  
    private Integer isRequired;  
    // Getters and setters  
}  
  
// 其他设计相关实体...
```

## 运行时模块实体

```
// com.ruoyi.form.runtime.domain.FormTenantTableMapping  
public class FormTenantTableMapping extends BaseEntity {  
    private String mappingId;  
    private String metadataId;  
    private String tenantId;  
    private String tableName;  
    private Integer isCreated;  
    // Getters and setters  
}  
  
// 其他运行时相关实体...
```

# 服务分布
## 设计模块服务

```
// com.ruoyi.form.design.service.IFormMetadataService  
public interface IFormMetadataService {  
    public FormMetadata selectFormMetadataById(String metadataId);  
    public List<FormMetadata> selectFormMetadataList(FormMetadata formMetadata);  
    public int insertFormMetadata(FormMetadata formMetadata);  
    public int updateFormMetadata(FormMetadata formMetadata);  
    public int deleteFormMetadataById(String metadataId);  
    public List<GenTable> selectDbTableListByNames(String[] tableNames);  
    public void importMetadata(List<GenTable> tableList);  
}  
// 其他设计相关服务...
```

## 运行时模块服务

```
// com.ruoyi.form.runtime.service.IFormDataService  
public interface IFormDataService {  
    public void saveFormData(String metadataId, Map<String, Object> formData, String tenantId, String username);  
    public List<Map<String, Object>> queryFormData(String metadataId, Map<String, Object> queryParams, String tenantId);  
    public int updateFormData(String metadataId, String dataId, Map<String, Object> formData, String tenantId, String username);  
    public int deleteFormData(String metadataId, String dataId, String tenantId);  
}  
  
// 其他运行时相关服务...
```

# 结论
将表单管理功能划分为设计和运行时模块是一个合理的架构决策，它提供了明确的关注点分离、更好的可维护性和更灵活的开发与部署。这种结构与现有的CrowdMarket架构很好地契合，同时为新的表单管理功能提供了清晰的组织结构。

设计模块专注于元数据定义和配置，而运行时模块专注于使用这些元数据来渲染表单、执行规则和管理数据。这种分离将使代码库更易于维护和理解。
