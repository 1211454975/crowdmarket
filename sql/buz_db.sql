-- --------------
-- 提供租户业务库
-- --------------
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

ALTER TABLE `sys_company` ADD COLUMN `db_status` tinyint NOT NULL DEFAULT '0' COMMENT '数据库状态(0未创建,1已创建,2创建失败)';


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
  `query_type` varchar(20) DEFAULT 'EQ' COMMENT '查询方式',  
  `html_type` varchar(50) DEFAULT 'input' COMMENT '显示类型',  
  `dict_type` varchar(100) DEFAULT NULL COMMENT '字典类型',  
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',  
  `tenant_id` varchar(20) NOT NULL COMMENT '租户ID',  
  PRIMARY KEY (`field_id`),  
  KEY `idx_metadata_id` (`metadata_id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表单字段元数据表';


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

