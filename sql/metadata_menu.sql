-- ----------------------------  
-- 元数据表单管理系统菜单  
-- ----------------------------  
  
-- 添加主菜单项  
INSERT INTO `sys_menu` VALUES (2200, '元数据表单', 0, 130, 'metadata', NULL, 1, 'M', '0', '0', '', 'form', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '元数据表单管理系统', 0);  
  
-- 添加表单设计子菜单  
INSERT INTO `sys_menu` VALUES (2210, '表单设计', 2200, 1, 'form', 'metadata/form/index', 1, 'C', '0', '0', 'metadata:form:list', 'list', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '表单元数据管理', 0);  
INSERT INTO `sys_menu` VALUES (2211, '表单查询', 2210, 1, '', '', 1, 'F', '0', '0', 'metadata:form:query', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2212, '表单新增', 2210, 2, '', '', 1, 'F', '0', '0', 'metadata:form:add', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2213, '表单修改', 2210, 3, '', '', 1, 'F', '0', '0', 'metadata:form:edit', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2214, '表单删除', 2210, 4, '', '', 1, 'F', '0', '0', 'metadata:form:remove', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2215, '表单导出', 2210, 5, '', '', 1, 'F', '0', '0', 'metadata:form:export', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
  
-- 添加字段管理子菜单  
INSERT INTO `sys_menu` VALUES (2220, '字段管理', 2200, 2, 'field', 'metadata/field/index', 1, 'C', '0', '0', 'metadata:field:list', 'dict', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '表单字段元数据管理', 0);  
INSERT INTO `sys_menu` VALUES (2221, '字段查询', 2220, 1, '', '', 1, 'F', '0', '0', 'metadata:field:query', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2222, '字段新增', 2220, 2, '', '', 1, 'F', '0', '0', 'metadata:field:add', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2223, '字段修改', 2220, 3, '', '', 1, 'F', '0', '0', 'metadata:field:edit', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2224, '字段删除', 2220, 4, '', '', 1, 'F', '0', '0', 'metadata:field:remove', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2225, '字段导出', 2220, 5, '', '', 1, 'F', '0', '0', 'metadata:field:export', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
  
-- 添加视图配置子菜单  
INSERT INTO `sys_menu` VALUES (2230, '视图配置', 2200, 3, 'view', 'metadata/view/index', 1, 'C', '0', '0', 'metadata:view:list', 'eye-open', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '表单视图配置管理', 0);  
INSERT INTO `sys_menu` VALUES (2231, '视图查询', 2230, 1, '', '', 1, 'F', '0', '0', 'metadata:view:query', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2232, '视图新增', 2230, 2, '', '', 1, 'F', '0', '0', 'metadata:view:add', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2233, '视图修改', 2230, 3, '', '', 1, 'F', '0', '0', 'metadata:view:edit', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2234, '视图删除', 2230, 4, '', '', 1, 'F', '0', '0', 'metadata:view:remove', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2235, '视图导出', 2230, 5, '', '', 1, 'F', '0', '0', 'metadata:view:export', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
  
-- 添加数据库导入子菜单  
INSERT INTO `sys_menu` VALUES (2240, '数据库导入', 2200, 4, 'import', 'metadata/import/index', 1, 'C', '0', '0', 'metadata:import:list', 'upload', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '从数据库导入元数据', 0);  
INSERT INTO `sys_menu` VALUES (2241, '导入操作', 2240, 1, '', '', 1, 'F', '0', '0', 'metadata:import:import', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
  
-- 添加业务规则管理菜单  
INSERT INTO `sys_menu` VALUES (2250, '业务规则', 2200, 5, 'rule', 'rule/index', 1, 'C', '0', '0', 'metadata:rule:list', 'validCode', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '业务规则管理', 0);  
INSERT INTO `sys_menu` VALUES (2251, '规则查询', 2250, 1, '', '', 1, 'F', '0', '0', 'metadata:rule:query', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2252, '规则新增', 2250, 2, '', '', 1, 'F', '0', '0', 'metadata:rule:add', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2253, '规则修改', 2250, 3, '', '', 1, 'F', '0', '0', 'metadata:rule:edit', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2254, '规则删除', 2250, 4, '', '', 1, 'F', '0', '0', 'metadata:rule:remove', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2255, '规则导出', 2250, 5, '', '', 1, 'F', '0', '0', 'metadata:rule:export', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2256, '规则条件管理', 2250, 6, '', '', 1, 'F', '0', '0', 'metadata:rule:condition', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2257, '规则动作管理', 2250, 7, '', '', 1, 'F', '0', '0', 'metadata:rule:action', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
  
-- 添加图表管理菜单  
INSERT INTO `sys_menu` VALUES (2260, '图表管理', 2200, 6, 'chart', 'chart/index', 1, 'C', '0', '0', 'metadata:chart:list', 'chart', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '图表管理', 0);  
INSERT INTO `sys_menu` VALUES (2261, '图表查询', 2260, 1, '', '', 1, 'F', '0', '0', 'metadata:chart:query', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2262, '图表新增', 2260, 2, '', '', 1, 'F', '0', '0', 'metadata:chart:add', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2263, '图表修改', 2260, 3, '', '', 1, 'F', '0', '0', 'metadata:chart:edit', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2264, '图表删除', 2260, 4, '', '', 1, 'F', '0', '0', 'metadata:chart:remove', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2265, '图表导出', 2260, 5, '', '', 1, 'F', '0', '0', 'metadata:chart:export', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2266, '图表设计', 2260, 6, 'design', 'chart/design', 1, 'C', '0', '0', 'metadata:chart:design', 'edit', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '图表设计', 0);  
INSERT INTO `sys_menu` VALUES (2267, '图表预览', 2260, 7, 'preview/:chartId', 'chart/preview', 1, 'C', '1', '0', 'metadata:chart:preview', 'eye', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '图表预览', 0);  
INSERT INTO `sys_menu` VALUES (2268, '图表导出工具', 2260, 8, 'export/:chartId', 'chart/export', 1, 'C', '1', '0', 'metadata:chart:exporttool', 'download', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '图表导出工具', 0);  
  
-- 添加表单数据管理菜单  
INSERT INTO `sys_menu` VALUES (2270, '表单数据', 2200, 7, 'formdata', 'formdata/index', 1, 'C', '0', '0', 'formdata:list', 'table', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '表单数据管理', 0);  
INSERT INTO `sys_menu` VALUES (2271, '数据查询', 2270, 1, '', '', 1, 'F', '0', '0', 'formdata:query', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2272, '数据新增', 2270, 2, '', '', 1, 'F', '0', '0', 'formdata:add', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2273, '数据修改', 2270, 3, '', '', 1, 'F', '0', '0', 'formdata:edit', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2274, '数据删除', 2270, 4, '', '', 1, 'F', '0', '0', 'formdata:remove', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2275, '数据导出', 2270, 5, '', '', 1, 'F', '0', '0', 'formdata:export', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2276, '数据导入', 2270, 6, '', '', 1, 'F', '0', '0', 'formdata:import', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2277, '表单详情', 2270, 7, 'detail/:metadataId/:id', 'formdata/detail', 1, 'C', '1', '0', 'formdata:detail', 'eye', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '表单数据详情', 0);  
INSERT INTO `sys_menu` VALUES (2278, '表单编辑', 2270, 8, 'form/:metadataId/:id?', 'formdata/form', 1, 'C', '1', '0', 'formdata:form', 'edit', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '表单数据编辑', 0);  
  
-- 添加租户数据库管理菜单  
INSERT INTO `sys_menu` VALUES (2280, '租户数据库', 2200, 8, 'tenant', 'metadata/tenant/index', 1, 'C', '0', '0', 'metadata:tenant:list', 'database', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '租户数据库管理', 0);  
INSERT INTO `sys_menu` VALUES (2281, '数据库查询', 2280, 1, '', '', 1, 'F', '0', '0', 'metadata:tenant:query', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2282, '数据库新增', 2280, 2, '', '', 1, 'F', '0', '0', 'metadata:tenant:add', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2283, '数据库修改', 2280, 3, '', '', 1, 'F', '0', '0', 'metadata:tenant:edit', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2284, '数据库删除', 2280, 4, '', '', 1, 'F', '0', '0', 'metadata:tenant:remove', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2285, '数据库测试', 2280, 5, '', '', 1, 'F', '0', '0', 'metadata:tenant:test', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2286, '数据库同步', 2280, 6, '', '', 1, 'F', '0', '0', 'metadata:tenant:sync', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
  
-- 添加表单映射管理菜单  
INSERT INTO `sys_menu` VALUES (2290, '表单映射', 2200, 9, 'mapping', 'metadata/mapping/index', 1, 'C', '0', '0', 'metadata:mapping:list', 'link', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '表单租户表映射管理', 0);  
INSERT INTO `sys_menu` VALUES (2291, '映射查询', 2290, 1, '', '', 1, 'F', '0', '0', 'metadata:mapping:query', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2292, '映射新增', 2290, 2, '', '', 1, 'F', '0', '0', 'metadata:mapping:add', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2293, '映射修改', 2290, 3, '', '', 1, 'F', '0', '0', 'metadata:mapping:edit', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2294, '映射删除', 2290, 4, '', '', 1, 'F', '0', '0', 'metadata:mapping:remove', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2295, '表创建', 2290, 5, '', '', 1, 'F', '0', '0', 'metadata:mapping:create', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
INSERT INTO `sys_menu` VALUES (2296, '表更新', 2290, 6, '', '', 1, 'F', '0', '0', 'metadata:mapping:update', '#', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '', 0);  
  
-- 为管理员角色添加元数据表单管理权限  
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES   
(1, 2200),   
(1, 2210), (1, 2211), (1, 2212), (1, 2213), (1, 2214), (1, 2215),  
(1, 2220), (1, 2221), (1, 2222), (1, 2223), (1, 2224), (1, 2225),  
(1, 2230), (1, 2231), (1, 2232), (1, 2233), (1, 2234), (1, 2235),  
(1, 2240), (1, 2241),  
(1, 2250), (1, 2251), (1, 2252), (1, 2253), (1, 2254), (1, 2255), (1, 2256), (1, 2257),  
(1, 2260), (1, 2261), (1, 2262), (1, 2263), (1, 2264), (1, 2265), (1, 2266), (1, 2267), (1, 2268),  
(1, 2270), (1, 2271), (1, 2272), (1, 2273), (1, 2274), (1, 2275), (1, 2276), (1, 2277), (1, 2278),  
(1, 2280), (1, 2281), (1, 2282), (1, 2283), (1, 2284), (1, 2285), (1, 2286),  
(1, 2290), (1, 2291), (1, 2292), (1, 2293), (1, 2294), (1, 2295), (1, 2296);  
  
-- 添加数据字典  
INSERT INTO `sys_dict_type` VALUES(100, '字段类型', 'metadata_field_type', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '元数据字段类型');  
INSERT INTO `sys_dict_type` VALUES(101, '查询方式', 'metadata_query_type', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '元数据查询方式');  
INSERT INTO `sys_dict_type` VALUES(102, '显示类型', 'metadata_html_type', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '元数据显示类型');  
INSERT INTO `sys_dict_type` VALUES(103, '视图类型', 'metadata_view_type', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '元数据视图类型');  
INSERT INTO `sys_dict_type` VALUES(104, '规则类型', 'metadata_rule_type', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '元数据规则类型');  
INSERT INTO `sys_dict_type` VALUES(105, '动作类型', 'metadata_action_type', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '元数据动作类型');  
INSERT INTO `sys_dict_type` VALUES(106, '图表类型', 'metadata_chart_type', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '元数据图表类型');  
  
-- 字段类型字典数据  
INSERT INTO `sys_dict_data` VALUES(1000, 1, '文本', 'string', 'metadata_field_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '文本类型');  
INSERT INTO `sys_dict_data` VALUES(1001, 2, '数字', 'number', 'metadata_field_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '数字类型');  
INSERT INTO `sys_dict_data` VALUES(1002, 3, '日期', 'date', 'metadata_field_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '日期类型');  
INSERT INTO `sys_dict_data` VALUES(1003, 4, '时间', 'datetime', 'metadata_field_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '时间类型');  
INSERT INTO `sys_dict_data` VALUES(1004, 5, '布尔', 'boolean', 'metadata_field_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '布尔类型');  
INSERT INTO `sys_dict_data` VALUES(1005, 6, '枚举', 'enum', 'metadata_field_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '枚举类型');  
INSERT INTO `sys_dict_data` VALUES(1006, 7, '文件', 'file', 'metadata_field_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '文件类型');  
INSERT INTO `sys_dict_data` VALUES(1007, 8, '图片', 'image', 'metadata_field_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '图片类型');  
INSERT INTO `sys_dict_data` VALUES(1008, 9, '富文本', 'richtext', 'metadata_field_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '富文本类型');  
  
-- 查询方式字典数据  
INSERT INTO `sys_dict_data` VALUES(1010, 1, '等于', 'EQ', 'metadata_query_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '等于查询');  
INSERT INTO `sys_dict_data` VALUES(1011, 2, '不等于', 'NE', 'metadata_query_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '不等于查询');  
INSERT INTO `sys_dict_data` VALUES(1012, 3, '大于', 'GT', 'metadata_query_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '大于查询');  
INSERT INTO `sys_dict_data` VALUES(1013, 4, '小于', 'LT', 'metadata_query_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '小于查询');  
INSERT INTO `sys_dict_data` VALUES(1014, 5, '大于等于', 'GE', 'metadata_query_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '大于等于查询');  
INSERT INTO `sys_dict_data` VALUES(1015, 6, '小于等于', 'LE', 'metadata_query_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '小于等于查询');  
INSERT INTO `sys_dict_data` VALUES(1016, 7, '模糊', 'LIKE', 'metadata_query_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '模糊查询');  
INSERT INTO `sys_dict_data` VALUES(1017, 8, '范围', 'BETWEEN', 'metadata_query_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '范围查询');  
  
-- 显示类型字典数据  
INSERT INTO `sys_dict_data` VALUES(1020, 1, '文本框', 'input', 'metadata_html_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '文本框');  
INSERT INTO `sys_dict_data` VALUES(1021, 2, '文本域', 'textarea', 'metadata_html_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '文本域');  
INSERT INTO `sys_dict_data` VALUES(1022, 3, '下拉框', 'select', 'metadata_html_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '下拉框');  
INSERT INTO `sys_dict_data` VALUES(1023, 4, '复选框', 'checkbox', 'metadata_html_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '复选框');  
INSERT INTO `sys_dict_data` VALUES(1024, 5, '单选框', 'radio', 'metadata_html_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '单选框');  
INSERT INTO `sys_dict_data` VALUES(1025, 6, '日期控件', 'datetime', 'metadata_html_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '日期控件');  
INSERT INTO `sys_dict_data` VALUES(1026, 7, '上传控件', 'upload', 'metadata_html_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '上传控件');  
INSERT INTO `sys_dict_data` VALUES(1027, 8, '富文本编辑器', 'editor', 'metadata_html_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '富文本编辑器');  
  
-- 视图类型字典数据  
INSERT INTO `sys_dict_data` VALUES(1030, 1, '表单', 'form', 'metadata_view_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '表单视图');  
INSERT INTO `sys_dict_data` VALUES(1031, 2, '列表', 'list', 'metadata_view_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '列表视图');  
INSERT INTO `sys_dict_data` VALUES(1032, 3, '详情', 'detail', 'metadata_view_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '详情视图');  
  
-- 规则类型字典数据  
INSERT INTO `sys_dict_data` VALUES(1040, 1, '验证规则', 'validation', 'metadata_rule_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '验证规则');  
INSERT INTO `sys_dict_data` VALUES(1041, 2, '计算规则', 'calculation', 'metadata_rule_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '计算规则');  
INSERT INTO `sys_dict_data` VALUES(1042, 3, '可见性规则', 'visibility', 'metadata_rule_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '可见性规则');  
INSERT INTO `sys_dict_data` VALUES(1043, 4, '依赖规则', 'dependency', 'metadata_rule_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '依赖规则');  
INSERT INTO `sys_dict_data` VALUES(1044, 5, '工作流规则', 'workflow', 'metadata_rule_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '工作流规则');  
  
-- 动作类型字典数据  
INSERT INTO `sys_dict_data` VALUES(1050, 1, '设置值', 'setValue', 'metadata_action_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '设置值');  
INSERT INTO `sys_dict_data` VALUES(1051, 2, '设置可见性', 'setVisible', 'metadata_action_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '设置可见性');  
INSERT INTO `sys_dict_data` VALUES(1052, 3, '设置必填', 'setRequired', 'metadata_action_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '设置必填');  
INSERT INTO `sys_dict_data` VALUES(1053, 4, '设置只读', 'setReadOnly', 'metadata_action_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '设置只读');  
INSERT INTO `sys_dict_data` VALUES(1054, 5, '计算', 'calculate', 'metadata_action_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '计算');  
INSERT INTO `sys_dict_data` VALUES(1055, 6, '消息提示', 'message', 'metadata_action_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '消息提示');  
INSERT INTO `sys_dict_data` VALUES(1056, 7, '调用API', 'api', 'metadata_action_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '调用API');  
  
-- 图表类型字典数据  
INSERT INTO `sys_dict_data` VALUES(1060, 1, '柱状图', 'bar', 'metadata_chart_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '柱状图');  
INSERT INTO `sys_dict_data` VALUES(1061, 2, '折线图', 'line', 'metadata_chart_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '折线图');  
INSERT INTO `sys_dict_data` VALUES(1062, 3, '饼图', 'pie', 'metadata_chart_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '饼图');  
INSERT INTO `sys_dict_data` VALUES(1063, 4, '雷达图', 'radar', 'metadata_chart_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '雷达图');  
INSERT INTO `sys_dict_data` VALUES(1064, 5, '散点图', 'scatter', 'metadata_chart_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '散点图');  
INSERT INTO `sys_dict_data` VALUES(1065, 6, '漏斗图', 'funnel', 'metadata_chart_type', '', '', 'N', '0', 'admin', '2025-05-05 11:33:00', 'admin', '2025-05-05 11:33:00', '漏斗图');  

