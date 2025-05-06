-- -----------------------
-- 下面的脚本由AI帮助生成的
-- -----------------------

-- 设置字符集和外键检查  
SET NAMES utf8mb4;  
SET FOREIGN_KEY_CHECKS = 0;  
  
-- ----------------------------  
-- 表单元数据表  
-- ----------------------------  
DROP TABLE IF EXISTS `form_metadata`;  
CREATE TABLE `form_metadata` (  
  `metadata_id` varchar(36) NOT NULL COMMENT '元数据ID',  
  `metadata_name` varchar(100) NOT NULL COMMENT '元数据名称',  
  `metadata_desc` varchar(500) DEFAULT NULL COMMENT '元数据描述',  
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0草稿,1发布,2停用)',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',  
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',  
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  
  PRIMARY KEY (`metadata_id`),  
  UNIQUE KEY `uk_metadata_name_tenant` (`metadata_name`,`tenant_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表单元数据表';  
  
-- ----------------------------  
-- 表单字段元数据表  
-- ----------------------------  
DROP TABLE IF EXISTS `form_field_metadata`;  
CREATE TABLE `form_field_metadata` (  
  `field_id` varchar(36) NOT NULL COMMENT '字段ID',  
  `metadata_id` varchar(36) NOT NULL COMMENT '元数据ID',  
  `field_name` varchar(50) NOT NULL COMMENT '字段名称',  
  `field_label` varchar(100) NOT NULL COMMENT '字段标签',  
  `field_type` varchar(20) NOT NULL COMMENT '字段类型',  
  `column_name` varchar(50) NOT NULL COMMENT '数据库列名',  
  `column_type` varchar(50) NOT NULL COMMENT '数据库列类型',  
  `is_required` tinyint NOT NULL DEFAULT '0' COMMENT '是否必填(0否1是)',  
  `is_pk` tinyint NOT NULL DEFAULT '0' COMMENT '是否主键(0否1是)',  
  `is_list` tinyint NOT NULL DEFAULT '1' COMMENT '是否列表显示(0否1是)',  
  `is_query` tinyint NOT NULL DEFAULT '0' COMMENT '是否查询条件(0否1是)',  
  `query_type` varchar(20) DEFAULT 'EQ' COMMENT '查询方式(EQ等于,NE不等于,GT大于,LT小于,LIKE模糊,BETWEEN范围)',  
  `html_type` varchar(50) DEFAULT 'input' COMMENT '显示类型(input文本框,textarea文本域,select下拉框,checkbox复选框,radio单选框,datetime日期控件)',  
  `dict_type` varchar(100) DEFAULT NULL COMMENT '字典类型',  
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  PRIMARY KEY (`field_id`),  
  KEY `idx_metadata_id` (`metadata_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表单字段元数据表';  
  
-- ----------------------------  
-- 表单视图配置表  
-- ----------------------------  
DROP TABLE IF EXISTS `form_view_config`;  
CREATE TABLE `form_view_config` (  
  `view_id` varchar(36) NOT NULL COMMENT '视图ID',  
  `metadata_id` varchar(36) NOT NULL COMMENT '元数据ID',  
  `view_name` varchar(100) NOT NULL COMMENT '视图名称',  
  `view_type` varchar(20) NOT NULL COMMENT '视图类型(form表单,list列表,detail详情)',  
  `view_config` text NOT NULL COMMENT '视图配置JSON',  
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认视图(0否1是)',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',  
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',  
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  
  PRIMARY KEY (`view_id`),  
  KEY `idx_metadata_id` (`metadata_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表单视图配置表';  
  
-- ----------------------------  
-- 表单租户表映射  
-- ----------------------------  
DROP TABLE IF EXISTS `form_tenant_table_mapping`;  
CREATE TABLE `form_tenant_table_mapping` (  
  `mapping_id` varchar(36) NOT NULL COMMENT '映射ID',  
  `metadata_id` varchar(36) NOT NULL COMMENT '元数据ID',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  `table_name` varchar(100) NOT NULL COMMENT '租户数据库中的表名',  
  `is_created` tinyint NOT NULL DEFAULT '0' COMMENT '表是否已创建(0否1是)',  
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  
  PRIMARY KEY (`mapping_id`),  
  UNIQUE KEY `uk_metadata_tenant` (`metadata_id`,`tenant_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表单租户表映射';  
  
-- ----------------------------  
-- 业务规则表  
-- ----------------------------  
DROP TABLE IF EXISTS `form_rule`;  
CREATE TABLE `form_rule` (  
  `rule_id` varchar(36) NOT NULL COMMENT '规则ID',  
  `metadata_id` varchar(36) NOT NULL COMMENT '元数据ID',  
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',  
  `rule_type` varchar(20) NOT NULL COMMENT '规则类型(validation,calculation,visibility,dependency,workflow)',  
  `rule_desc` varchar(500) DEFAULT NULL COMMENT '规则描述',  
  `is_active` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用(0否1是)',  
  `execution_order` int NOT NULL DEFAULT '0' COMMENT '执行顺序',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',  
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',  
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  
  PRIMARY KEY (`rule_id`),  
  KEY `idx_metadata_id` (`metadata_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表单规则定义表';  
  
-- ----------------------------  
-- 规则条件表  
-- ----------------------------  
DROP TABLE IF EXISTS `form_rule_condition`;  
CREATE TABLE `form_rule_condition` (  
  `condition_id` varchar(36) NOT NULL COMMENT '条件ID',  
  `rule_id` varchar(36) NOT NULL COMMENT '规则ID',  
  `field_id` varchar(36) NOT NULL COMMENT '字段ID',  
  `operator` varchar(20) NOT NULL COMMENT '操作符(eq,ne,gt,lt,ge,le,contains,startswith,endswith)',  
  `value` varchar(500) DEFAULT NULL COMMENT '比较值',  
  `logic_type` varchar(10) NOT NULL DEFAULT 'AND' COMMENT '逻辑类型(AND,OR)',  
  `group_id` varchar(36) DEFAULT NULL COMMENT '条件组ID',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  PRIMARY KEY (`condition_id`),  
  KEY `idx_rule_id` (`rule_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表单规则条件表';  
  
-- ----------------------------  
-- 规则动作表  
-- ----------------------------  
DROP TABLE IF EXISTS `form_rule_action`;  
CREATE TABLE `form_rule_action` (  
  `action_id` varchar(36) NOT NULL COMMENT '动作ID',  
  `rule_id` varchar(36) NOT NULL COMMENT '规则ID',  
  `action_type` varchar(20) NOT NULL COMMENT '动作类型(setValue,setVisible,setRequired,setReadOnly,calculate,message,api)',  
  `target_field_id` varchar(36) DEFAULT NULL COMMENT '目标字段ID',  
  `action_value` text COMMENT '动作值',  
  `action_script` text COMMENT '动作脚本',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  PRIMARY KEY (`action_id`),  
  KEY `idx_rule_id` (`rule_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表单规则动作表';  
  
-- ----------------------------  
-- 图表元数据表  
-- ----------------------------  
DROP TABLE IF EXISTS `chart_metadata`;  
CREATE TABLE `chart_metadata` (  
  `chart_id` varchar(36) NOT NULL COMMENT '图表ID',  
  `chart_name` varchar(100) NOT NULL COMMENT '图表名称',  
  `chart_desc` varchar(500) DEFAULT NULL COMMENT '图表描述',  
  `chart_type` varchar(20) NOT NULL COMMENT '图表类型(bar柱状图,line折线图,pie饼图,radar雷达图等)',  
  `metadata_id` varchar(36) NOT NULL COMMENT '关联的表单元数据ID',  
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0草稿,1发布,2停用)',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',  
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',  
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  
  PRIMARY KEY (`chart_id`),  
  KEY `idx_metadata_id` (`metadata_id`),  
  KEY `idx_tenant_id` (`tenant_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图表元数据表';  
  
-- ----------------------------  
-- 图表配置表  
-- ----------------------------  
DROP TABLE IF EXISTS `chart_config`;  
CREATE TABLE `chart_config` (  
  `config_id` varchar(36) NOT NULL COMMENT '配置ID',  
  `chart_id` varchar(36) NOT NULL COMMENT '图表ID',  
  `dimension_fields` text COMMENT '维度字段JSON',  
  `measure_fields` text COMMENT '度量字段JSON',  
  `filter_condition` text COMMENT '过滤条件JSON',  
  `sort_config` text COMMENT '排序配置JSON',  
  `style_config` text COMMENT '样式配置JSON',  
  `data_limit` int DEFAULT '1000' COMMENT '数据限制条数',  
  `refresh_interval` int DEFAULT '0' COMMENT '刷新间隔(秒)',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  PRIMARY KEY (`config_id`),  
  KEY `idx_chart_id` (`chart_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图表配置表';  
  
-- ----------------------------  
-- 租户数据库配置表  
-- ----------------------------  
DROP TABLE IF EXISTS `tenant_database`;  
CREATE TABLE `tenant_database` (  
  `id` varchar(36) NOT NULL COMMENT '主键ID',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  `db_name` varchar(100) NOT NULL COMMENT '数据库名称',  
  `db_host` varchar(100) NOT NULL COMMENT '数据库主机',  
  `db_port` int NOT NULL COMMENT '数据库端口',  
  `db_username` varchar(100) NOT NULL COMMENT '数据库用户名',  
  `db_password` varchar(255) NOT NULL COMMENT '数据库密码(加密)',  
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0停用,1启用)',  
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',  
  `create_time` datetime NOT NULL COMMENT '创建时间',  
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',  
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',  
  PRIMARY KEY (`id`),  
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='租户数据库配置表';  
  
-- ----------------------------  
-- 修改sys_company表，添加数据库状态字段  
-- ----------------------------  
-- ALTER TABLE `sys_company` ADD COLUMN `db_status` tinyint NOT NULL DEFAULT '0' COMMENT '数据库状态(0未创建,1已创建,2创建失败)';  
  
-- ----------------------------  
-- 添加菜单项  
-- ----------------------------  
INSERT INTO `sys_menu` VALUES (3001, '表单管理', 0, 1, 'form', null, 1, 'M', '0', '0', '', 'form', 'admin', NOW(), '', null, '表单管理菜单', 1);  
INSERT INTO `sys_menu` VALUES (3002, '元数据管理', 3001, 1, 'metadata', 'form/metadata/index', 1, 'C', '0', '0', 'form:metadata:list', 'table', 'admin', NOW(), '', null, '表单元数据管理菜单', 1);  
INSERT INTO `sys_menu` VALUES (3003, '表单数据', 3001, 2, 'data', 'form/data/index', 1, 'C', '0', '0', 'form:data:list', 'list', 'admin', NOW(), '', null, '表单数据管理菜单', 1);  
INSERT INTO `sys_menu` VALUES (3004, '业务规则', 3001, 3, 'rule', 'form/rule/index', 1, 'C', '0', '0', 'form:rule:list', 'tree', 'admin', NOW(), '', null, '业务规则管理菜单', 1);  
INSERT INTO `sys_menu` VALUES (3005, '图表管理', 3001, 4, 'chart', 'form/chart/index', 1, 'C', '0', '0', 'form:chart:list', 'chart', 'admin', NOW(), '', null, '图表管理菜单', 1);  
INSERT INTO `sys_menu` VALUES (3006, '租户数据库', 3001, 5, 'database', 'form/database/index', 1, 'C', '0', '0', 'form:database:list', 'database', 'admin', NOW(), '', null, '租户数据库管理菜单', 1);  
  
-- ----------------------------  
-- 添加权限项  
-- ----------------------------  
-- 表单元数据管理权限  
INSERT INTO `sys_menu` VALUES (3101, '表单元数据查询', 3002, 1, '', '', 1, 'F', '0', '0', 'form:metadata:query', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3102, '表单元数据新增', 3002, 2, '', '', 1, 'F', '0', '0', 'form:metadata:add', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3103, '表单元数据修改', 3002, 3, '', '', 1, 'F', '0', '0', 'form:metadata:edit', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3104, '表单元数据删除', 3002, 4, '', '', 1, 'F', '0', '0', 'form:metadata:remove', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3105, '表单元数据导入', 3002, 5, '', '', 1, 'F', '0', '0', 'form:metadata:import', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3106, '表单元数据导出', 3002, 6, '', '', 1, 'F', '0', '0', 'form:metadata:export', '#', 'admin', NOW(), '', null, '', 1);  
  
-- 表单数据管理权限  
INSERT INTO `sys_menu` VALUES (3201, '表单数据查询', 3003, 1, '', '', 1, 'F', '0', '0', 'form:data:query', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3202, '表单数据新增', 3003, 2, '', '', 1, 'F', '0', '0', 'form:data:add', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3203, '表单数据修改', 3003, 3, '', '', 1, 'F', '0', '0', 'form:data:edit', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3204, '表单数据删除', 3003, 4, '', '', 1, 'F', '0', '0', 'form:data:remove', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3205, '表单数据导入', 3003, 5, '', '', 1, 'F', '0', '0', 'form:data:import', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3206, '表单数据导出', 3003, 6, '', '', 1, 'F', '0', '0', 'form:data:export', '#', 'admin', NOW(), '', null, '', 1);  
  
-- 业务规则管理权限  
INSERT INTO `sys_menu` VALUES (3301, '业务规则查询', 3004, 1, '', '', 1, 'F', '0', '0', 'form:rule:query', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3302, '业务规则新增', 3004, 2, '', '', 1, 'F', '0', '0', 'form:rule:add', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3303, '业务规则修改', 3004, 3, '', '', 1, 'F', '0', '0', 'form:rule:edit', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3304, '业务规则删除', 3004, 4, '', '', 1, 'F', '0', '0', 'form:rule:remove', '#', 'admin', NOW(), '', null, '', 1);  
  
-- 图表管理权限  
INSERT INTO `sys_menu` VALUES (3401, '图表查询', 3005, 1, '', '', 1, 'F', '0', '0', 'form:chart:query', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3402, '图表新增', 3005, 2, '', '', 1, 'F', '0', '0', 'form:chart:add', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3403, '图表修改', 3005, 3, '', '', 1, 'F', '0', '0', 'form:chart:edit', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3404, '图表删除', 3005, 4, '', '', 1, 'F', '0', '0', 'form:chart:remove', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3405, '图表导出', 3005, 5, '', '', 1, 'F', '0', '0', 'form:chart:export', '#', 'admin', NOW(), '', null, '', 1);  
  
-- 租户数据库管理权限  
INSERT INTO `sys_menu` VALUES (3501, '租户数据库查询', 3006, 1, '', '', 1, 'F', '0', '0', 'form:database:query', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3502, '租户数据库新增', 3006, 2, '', '', 1, 'F', '0', '0', 'form:database:add', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3503, '租户数据库修改', 3006, 3, '', '', 1, 'F', '0', '0', 'form:database:edit', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3504, '租户数据库删除', 3006, 4, '', '', 1, 'F', '0', '0', 'form:database:remove', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3505, '租户数据库创建', 3006, 5, '', '', 1, 'F', '0', '0', 'form:database:create', '#', 'admin', NOW(), '', null, '', 1);  
INSERT INTO `sys_menu` VALUES (3506, '租户数据库测试', 3006, 6, '', '', 1, 'F', '0', '0', 'form:database:test', '#', 'admin', NOW(), '', null, '', 1);  
  
-- 重置外键检查  
SET FOREIGN_KEY_CHECKS = 1;
