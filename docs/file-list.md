需要生成的文件
# 1. 数据库脚本文件
首先，需要生成包含所有表结构定义的SQL脚本文件：

```
sql/metadata_tables.sql  # 包含所有元数据相关表的创建脚本  
```

# 2. ruoyi-metadata模块核心文件
## 2.1 实体类（Entity）
- com.ruoyi.metadata.domain.FormMetadata.java  
- com.ruoyi.metadata.domain.FormFieldMetadata.java  
- com.ruoyi.metadata.domain.FormViewConfig.java  
- com.ruoyi.metadata.domain.FormTenantTableMapping.java  
- com.ruoyi.metadata.domain.FormRule.java  
- com.ruoyi.metadata.domain.FormRuleCondition.java  
- com.ruoyi.metadata.domain.FormRuleAction.java  
- com.ruoyi.metadata.domain.ChartMetadata.java  
- com.ruoyi.metadata.domain.ChartConfig.java  
- com.ruoyi.metadata.domain.TenantDatabase.java  

## 2.2 数据访问层（Mapper）
- com.ruoyi.metadata.mapper.FormMetadataMapper.java  
- com.ruoyi.metadata.mapper.FormFieldMetadataMapper.java  
- com.ruoyi.metadata.mapper.FormViewConfigMapper.java  
- com.ruoyi.metadata.mapper.FormTenantTableMappingMapper.java  
- com.ruoyi.metadata.mapper.FormRuleMapper.java  
- com.ruoyi.metadata.mapper.FormRuleConditionMapper.java  
- com.ruoyi.metadata.mapper.FormRuleActionMapper.java  
- com.ruoyi.metadata.mapper.ChartMetadataMapper.java  
- com.ruoyi.metadata.mapper.ChartConfigMapper.java  
- com.ruoyi.metadata.mapper.TenantDatabaseMapper.java  

对应的XML映射文件：

- resources/mapper/metadata/FormMetadataMapper.xml  
- resources/mapper/metadata/FormFieldMetadataMapper.xml  
- resources/mapper/metadata/FormViewConfigMapper.xml  
- resources/mapper/metadata/FormTenantTableMappingMapper.xml  
- resources/mapper/metadata/FormRuleMapper.xml  
- resources/mapper/metadata/FormRuleConditionMapper.xml  
- resources/mapper/metadata/FormRuleActionMapper.xml  
- resources/mapper/metadata/ChartMetadataMapper.xml  
- resources/mapper/metadata/ChartConfigMapper.xml  
- resources/mapper/metadata/TenantDatabaseMapper.xml  

## 2.3 服务接口和实现类（Service）
- com.ruoyi.metadata.service.IFormMetadataService.java  
- com.ruoyi.metadata.service.IFormFieldMetadataService.java  
- com.ruoyi.metadata.service.IFormViewConfigService.java  
- com.ruoyi.metadata.service.IFormTenantTableMappingService.java  
- com.ruoyi.metadata.service.IFormRuleService.java  
- com.ruoyi.metadata.service.IFormRuleConditionService.java  
- com.ruoyi.metadata.service.IFormRuleActionService.java  
- com.ruoyi.metadata.service.IChartMetadataService.java  
- com.ruoyi.metadata.service.IChartConfigService.java  
- com.ruoyi.metadata.service.ITenantDatabaseService.java  
- com.ruoyi.metadata.service.IDatabaseMetadataService.java  
  
- com.ruoyi.metadata.service.impl.FormMetadataServiceImpl.java  
- com.ruoyi.metadata.service.impl.FormFieldMetadataServiceImpl.java  
- com.ruoyi.metadata.service.impl.FormViewConfigServiceImpl.java  
- com.ruoyi.metadata.service.impl.FormTenantTableMappingServiceImpl.java  
- com.ruoyi.metadata.service.impl.FormRuleServiceImpl.java  
- com.ruoyi.metadata.service.impl.FormRuleConditionServiceImpl.java  
- com.ruoyi.metadata.service.impl.FormRuleActionServiceImpl.java  
- com.ruoyi.metadata.service.impl.ChartMetadataServiceImpl.java  
- com.ruoyi.metadata.service.impl.ChartConfigServiceImpl.java  
- com.ruoyi.metadata.service.impl.TenantDatabaseServiceImpl.java  
- com.ruoyi.metadata.service.impl.DatabaseMetadataServiceImpl.java  

## 2.4 控制器（Controller）
- com.ruoyi.metadata.controller.FormMetadataController.java  
- com.ruoyi.metadata.controller.FormFieldMetadataController.java  
- com.ruoyi.metadata.controller.FormViewConfigController.java  
- com.ruoyi.metadata.controller.FormRuleController.java  
- com.ruoyi.metadata.controller.ChartMetadataController.java  
- com.ruoyi.metadata.controller.ChartConfigController.java  
- com.ruoyi.metadata.controller.TenantDatabaseController.java  
- com.ruoyi.metadata.controller.DatabaseMetadataController.java  

# 3. ruoyi-formdata模块核心文件

## 3.1 服务接口和实现类（Service）
- com.ruoyi.formdata.service.ITableGenerationService.java  
- com.ruoyi.formdata.service.IFormDataService.java  
- com.ruoyi.formdata.service.IFormQueryService.java  
- com.ruoyi.formdata.service.IFormImportExportService.java  
- com.ruoyi.formdata.service.IRuleExecutionEngine.java  
  
- com.ruoyi.formdata.service.impl.TableGenerationServiceImpl.java  
- com.ruoyi.formdata.service.impl.FormDataServiceImpl.java  
- com.ruoyi.formdata.service.impl.FormQueryServiceImpl.java  
- com.ruoyi.formdata.service.impl.FormImportExportServiceImpl.java  
- com.ruoyi.formdata.service.impl.RuleExecutionEngineImpl.java  

## 3.2 控制器（Controller）
- com.ruoyi.formdata.controller.FormDataController.java  

## 3.3 数据源管理
- com.ruoyi.formdata.datasource.DynamicDataSourceService.java  
- com.ruoyi.formdata.datasource.TenantDatabaseInterceptor.java  
- com.ruoyi.formdata.datasource.DynamicDataSourceConfig.java 

# 4. 前端文件

## 4.1 表单元数据管理前端
- src/views/metadata/form/index.vue  # 表单元数据列表  
- src/views/metadata/form/edit.vue   # 表单元数据编辑  
- src/views/metadata/field/index.vue # 字段元数据管理  
- src/views/metadata/view/index.vue  # 视图配置管理  
- src/views/metadata/import/index.vue # 数据库导入元数据  

## 4.2 表单数据管理前端
- src/views/formdata/index.vue       # 表单数据列表  
- src/views/formdata/form.vue        # 表单数据录入和编辑  
- src/views/formdata/detail.vue      # 表单数据详情  
- src/views/formdata/import.vue      # 数据导入导出  

## 4.3 业务规则管理前端
- src/views/rule/index.vue           # 业务规则列表  
- src/views/rule/edit.vue            # 业务规则编辑  
- src/views/rule/condition.vue       # 规则条件编辑  
- src/views/rule/action.vue          # 规则动作编辑  

## 4.4 图表管理前端
- src/views/chart/index.vue          # 图表列表  
- src/views/chart/design.vue         # 图表设计  
- src/views/chart/preview.vue        # 图表预览  
- src/views/chart/export.vue         # 图表导出  


## 4.5 前端组件库

- src/components/FormDesigner/index.vue  
- src/components/FormRenderer/index.vue  
- src/components/ChartDesigner/index.vue  
- src/components/RuleDesigner/index.vue  

## 4.6 前端API调用服务

- src/api/metadata/form.js  
- src/api/metadata/field.js  
- src/api/metadata/view.js  
- src/api/metadata/rule.js  
- src/api/metadata/chart.js  
- src/api/formdata/index.js  
- 
# 5. 配置文件
- ruoyi-metadata/src/main/resources/application.yml  
- ruoyi-formdata/src/main/resources/application.yml  

# 6. 单元测试文件
- ruoyi-metadata/src/test/java/com/ruoyi/metadata/service/*Test.java  
- ruoyi-formdata/src/test/java/com/ruoyi/formdata/service/*Test.java  

# 7. 模块配置文件
- ruoyi-metadata/pom.xml  
- ruoyi-formdata/pom.xml  

# 8. 权限菜单SQL
sql/metadata_menu.sql  # 包含所有元数据相关菜单和权限的SQL  



8. 系统集成配置：

- ruoyi-admin/src/main/resources/application.yml中添加新模块配置  
- 主pom.xml中添加新模块依赖  

