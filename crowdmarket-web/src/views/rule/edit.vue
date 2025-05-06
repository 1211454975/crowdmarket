<template>  
  <div class="app-container">  
    <el-card>  
      <div slot="header" class="clearfix">  
        <span>{{ title }}</span>  
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回</el-button>  
      </div>  
        
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">  
        <!-- 基本信息 -->  
        <el-divider content-position="left">基本信息</el-divider>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="所属元数据" prop="metadataId">  
              <el-select v-model="form.metadataId" placeholder="请选择所属元数据" @change="handleMetadataChange" :disabled="mode === 'edit'">  
                <el-option  
                  v-for="item in metadataOptions"  
                  :key="item.metadataId"  
                  :label="item.metadataName"  
                  :value="item.metadataId"  
                />  
              </el-select>  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="规则名称" prop="ruleName">  
              <el-input v-model="form.ruleName" placeholder="请输入规则名称" />  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="规则类型" prop="ruleType">  
              <el-select v-model="form.ruleType" placeholder="请选择规则类型">  
                <el-option v-for="dict in ruleTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
              </el-select>  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="执行顺序" prop="executionOrder">  
              <el-input-number v-model="form.executionOrder" :min="0" :max="999" controls-position="right" />  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="是否启用" prop="isActive">  
              <el-radio-group v-model="form.isActive">  
                <el-radio :label="1">是</el-radio>  
                <el-radio :label="0">否</el-radio>  
              </el-radio-group>  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="24">  
            <el-form-item label="规则描述" prop="ruleDesc">  
              <el-input v-model="form.ruleDesc" type="textarea" :rows="3" placeholder="请输入规则描述" />  
            </el-form-item>  
          </el-col>  
        </el-row>  
          
        <!-- 规则条件 -->  
        <el-divider content-position="left">规则条件</el-divider>  
        <div class="condition-container">  
          <div v-for="(group, groupIndex) in conditionGroups" :key="'group-' + groupIndex" class="condition-group">  
            <div class="group-header">  
              <span class="group-title">条件组 {{ groupIndex + 1 }}</span>  
              <el-button   
                v-if="groupIndex > 0"   
                type="danger"   
                size="mini"   
                icon="el-icon-delete"   
                circle   
                @click="removeConditionGroup(groupIndex)"  
              ></el-button>  
            </div>  
              
            <div v-for="(condition, condIndex) in group.conditions" :key="'cond-' + groupIndex + '-' + condIndex" class="condition-item">  
              <el-row :gutter="10">  
                <el-col :span="condIndex === 0 ? 6 : 4">  
                  <el-select   
                    v-if="condIndex > 0"   
                    v-model="condition.logicType"   
                    style="width: 80px"  
                  >  
                    <el-option label="AND" value="AND" />  
                    <el-option label="OR" value="OR" />  
                  </el-select>  
                  <el-select   
                    v-model="condition.fieldId"   
                    placeholder="选择字段"   
                    @change="(val) => handleFieldChange(val, groupIndex, condIndex)"  
                  >  
                    <el-option  
                      v-for="field in fieldOptions"  
                      :key="field.fieldId"  
                      :label="field.fieldLabel"  
                      :value="field.fieldId"  
                    />  
                  </el-select>  
                </el-col>  
                <el-col :span="4">  
                  <el-select v-model="condition.operator" placeholder="选择操作符">  
                    <el-option  
                      v-for="op in getOperatorOptions(condition.fieldType)"  
                      :key="op.value"  
                      :label="op.label"  
                      :value="op.value"  
                    />  
                  </el-select>  
                </el-col>  
                <el-col :span="10">  
                  <!-- 字符串类型 -->  
                  <el-input   
                    v-if="condition.fieldType === 'string' || condition.fieldType === 'text'"   
                    v-model="condition.value"   
                    placeholder="请输入比较值"  
                  />  
                    
                  <!-- 数字类型 -->  
                  <el-input-number   
                    v-else-if="condition.fieldType === 'integer'"   
                    v-model="condition.value"   
                    placeholder="请输入比较值"   
                    :controls="false"   
                    style="width: 100%"  
                  />  
                  <el-input-number   
                    v-else-if="condition.fieldType === 'decimal'"   
                    v-model="condition.value"   
                    placeholder="请输入比较值"   
                    :controls="false"   
                    :precision="2"   
                    style="width: 100%"  
                  />  
                    
                  <!-- 布尔类型 -->  
                  <el-select   
                    v-else-if="condition.fieldType === 'boolean'"   
                    v-model="condition.value"   
                    placeholder="请选择比较值"  
                  >  
                    <el-option label="是" :value="true" />  
                    <el-option label="否" :value="false" />  
                  </el-select>  
                    
                  <!-- 日期类型 -->  
                  <el-date-picker   
                    v-else-if="condition.fieldType === 'date'"   
                    v-model="condition.value"   
                    type="date"   
                    placeholder="选择日期"   
                    value-format="yyyy-MM-dd"   
                    style="width: 100%"  
                  />  
                  <el-date-picker   
                    v-else-if="condition.fieldType === 'datetime'"   
                    v-model="condition.value"   
                    type="datetime"   
                    placeholder="选择日期时间"   
                    value-format="yyyy-MM-dd HH:mm:ss"   
                    style="width: 100%"  
                  />  
                    
                  <!-- 枚举类型 -->  
                  <el-select   
                    v-else-if="condition.fieldType === 'enum' && condition.dictType"   
                    v-model="condition.value"   
                    placeholder="请选择比较值"  
                  >  
                    <el-option  
                      v-for="dict in getDictOptions(condition.dictType)"  
                      :key="dict.dictValue"  
                      :label="dict.dictLabel"  
                      :value="dict.dictValue"  
                    />  
                  </el-select>  
                    
                  <!-- 默认输入框 -->  
                  <el-input   
                    v-else   
                    v-model="condition.value"   
                    placeholder="请输入比较值"  
                  />  
                </el-col>  
                <el-col :span="4">  
                  <el-button   
                    type="danger"   
                    icon="el-icon-delete"   
                    circle   
                    @click="removeCondition(groupIndex, condIndex)"  
                  ></el-button>  
                  <el-button   
                    type="primary"   
                    icon="el-icon-plus"   
                    circle   
                    @click="addCondition(groupIndex)"  
                  ></el-button>  
                </el-col>  
              </el-row>  
            </div>  
          </div>  
            
          <div class="add-group">  
            <el-button type="primary" icon="el-icon-plus" @click="addConditionGroup">添加条件组</el-button>  
          </div>  
        </div>  
          
        <!-- 规则动作 -->  
        <el-divider content-position="left">规则动作</el-divider>  
        <div class="action-container">  
          <div v-for="(action, index) in actions" :key="'action-' + index" class="action-item">  
            <el-row :gutter="10">  
              <el-col :span="4">  
                <el-select v-model="action.actionType" placeholder="选择动作类型" @change="(val) => handleActionTypeChange(val, index)">  
                  <el-option  
                    v-for="type in actionTypeOptions"  
                    :key="type.value"  
                    :label="type.label"  
                    :value="type.value"  
                  />  
                </el-select>  
              </el-col>  
              <el-col :span="6" v-if="needsTargetField(action.actionType)">  
                <el-select v-model="action.targetFieldId" placeholder="选择目标字段">  
                  <el-option  
                    v-for="field in fieldOptions"  
                    :key="field.fieldId"  
                    :label="field.fieldLabel"  
                    :value="field.fieldId"  
                  />  
                </el-select>  
              </el-col>  
              <el-col :span="needsTargetField(action.actionType) ? 10 : 16">  
                <!-- 设置值动作 -->  
                <el-input   
                  v-if="action.actionType === 'setValue'"   
                  v-model="action.actionValue"   
                  placeholder="请输入设置的值"  
                />  
                  
                <!-- 设置可见性动作 -->  
                <el-select   
                  v-else-if="action.actionType === 'setVisible'"   
                  v-model="action.actionValue"   
                  placeholder="请选择可见性"  
                >  
                  <el-option label="显示" value="true" />  
                  <el-option label="隐藏" value="false" />  
                </el-select>  
                  
                <!-- 设置必填动作 -->  
                <el-select   
                  v-else-if="action.actionType === 'setRequired'"   
                  v-model="action.actionValue"   
                  placeholder="请选择是否必填"  
                >  
                  <el-option label="必填" value="true" />  
                  <el-option label="非必填" value="false" />  
                </el-select>  
                  
                <!-- 设置只读动作 -->  
                <el-select   
                  v-else-if="action.actionType === 'setReadOnly'"   
                  v-model="action.actionValue"   
                  placeholder="请选择是否只读"  
                >  
                  <el-option label="只读" value="true" />  
                  <el-option label="可编辑" value="false" />  
                </el-select>  
                  
                <!-- 消息提示动作 -->  
                <el-input   
                  v-else-if="action.actionType === 'message'"   
                  v-model="action.actionValue"   
                  placeholder="请输入提示消息"  
                />  
                  
                <!-- 计算动作 -->  
                <el-input   
                  v-else-if="action.actionType === 'calculate'"   
                  v-model="action.actionScript"   
                  type="textarea"   
                  :rows="2"   
                  placeholder="请输入计算表达式，例如: field1 + field2"  
                />  
                  
                <!-- API调用动作 -->  
                <el-input   
                  v-else-if="action.actionType === 'api'"   
                  v-model="action.actionValue"   
                  placeholder="请输入API地址"  
                />  
                  
                <!-- 默认输入框 -->  
                <el-input   
                  v-else   
                  v-model="action.actionValue"   
                  placeholder="请输入动作值"  
                />  
              </el-col>  
              <el-col :span="4">  
                <el-button   
                  type="danger"   
                  icon="el-icon-delete"   
                  circle   
                  @click="removeAction(index)"  
                ></el-button>  
                <el-button   
                  type="primary"   
                  icon="el-icon-plus"   
                  circle   
                  @click="addAction"  
                ></el-button>  
              </el-col>  
            </el-row>  
          </div>  
            
          <div v-if="actions.length === 0" class="add-action">  
            <el-button type="primary" icon="el-icon-plus" @click="addAction">添加动作</el-button>  
          </div>  
        </div>  
          
        <el-form-item>  
          <el-button type="primary" @click="submitForm">保存</el-button>  
          <el-button @click="goBack">取消</el-button>  
        </el-form-item>  
      </el-form>  
    </el-card>  
  </div>  
</template>  
  
<script>  
import { listFormMetadata, getFormMetadata } from "@/api/metadata/form";  
import { listFormFieldMetadata } from "@/api/metadata/field";  
import { getFormRule, addFormRule, updateFormRule } from "@/api/metadata/rule";  
import { listRuleCondition, addRuleCondition, updateRuleCondition, delRuleCondition } from "@/api/metadata/condition";  
import { listRuleAction, addRuleAction, updateRuleAction, delRuleAction } from "@/api/metadata/action";  
  
export default {  
  name: "FormRuleEdit",  
  data() {  
    return {  
      // 操作模式：add-新增，edit-编辑  
      mode: "add",  
      // 表单标题  
      title: "",  
      // 规则ID  
      ruleId: null,  
      // 元数据ID  
      metadataId: null,  
      // 表单参数  
      form: {  
        ruleId: null,  
        metadataId: null,  
        ruleName: null,  
        ruleType: "validation",  
        ruleDesc: null,  
        isActive: 1,  
        executionOrder: 0,  
        tenantId: null  
      },  
      // 表单校验规则  
      rules: {  
        metadataId: [  
          { required: true, message: "所属元数据不能为空", trigger: "change" }  
        ],  
        ruleName: [  
          { required: true, message: "规则名称不能为空", trigger: "blur" }  
        ],  
        ruleType: [  
          { required: true, message: "规则类型不能为空", trigger: "change" }  
        ]  
      },  
      // 元数据选项  
      metadataOptions: [],  
      // 字段选项  
      fieldOptions: [],  
      // 字段映射（字段ID -> 字段信息）  
      fieldMap: {},  
      // 条件组  
      conditionGroups: [  
        {  
          groupId: this.generateUuid(),  
          conditions: [  
            {  
              conditionId: this.generateUuid(),  
              fieldId: null,  
              fieldType: null,  
              dictType: null,  
              operator: "eq",  
              value: null,  
              logicType: "AND",  
              groupId: null  
            }  
          ]  
        }  
      ],  
      // 动作列表  
      actions: [],  
      // 规则类型选项  
      ruleTypeOptions: [  
        { value: "validation", label: "验证规则" },  
        { value: "calculation", label: "计算规则" },  
        { value: "visibility", label: "可见性规则" },  
        { value: "dependency", label: "依赖规则" },  
        { value: "workflow", label: "工作流规则" }  
      ],  
      // 操作符选项  
      operatorOptions: {  
        string: [  
          { value: "eq", label: "等于" },  
          { value: "ne", label: "不等于" },  
          { value: "contains", label: "包含" },  
          { value: "startswith", label: "开始于" },  
          { value: "endswith", label: "结束于" }  
        ],  
        number: [  
          { value: "eq", label: "等于" },  
          { value: "ne", label: "不等于" },  
          { value: "gt", label: "大于" },  
          { value: "lt", label: "小于" },  
          { value: "ge", label: "大于等于" },  
          { value: "le", label: "小于等于" }  
        ],  
        boolean: [  
          { value: "eq", label: "等于" }  
        ],  
        date: [  
          { value: "eq", label: "等于" },  
          { value: "ne", label: "不等于" },  
          { value: "gt", label: "大于" },  
          { value: "lt", label: "小于" },  
          { value: "ge", label: "大于等于" },  
          { value: "le", label: "小于等于" }  
        ],  
        enum: [  
          { value: "eq", label: "等于" },  
          { value: "ne", label: "不等于" }  
        ]  
      },  
      // 动作类型选项  
      actionTypeOptions: [  
        { value: "setValue", label: "设置值" },  
        { value: "setVisible", label: "设置可见性" },  
        { value: "setRequired", label: "设置必填" },  
        { value: "setReadOnly", label: "设置只读" },  
        { value: "calculate", label: "计算" },  
        { value: "message", label: "消息提示" },  
        { value: "api", label: "调用API" }  
      ],  
      // 字典数据缓存  
      dictCache: {}  
    };  
  },  
  created() {  
    // 获取路由参数  
    this.ruleId = this.$route.query.ruleId;  
    this.metadataId = this.$route.query.metadataId;  
    this.mode = this.$route.query.mode || "add";  
      
    // 设置标题  
    if (this.mode === "add") {  
      this.title = "新增业务规则";  
      if (this.metadataId) {  
        this.form.metadataId = this.metadataId;  
      }  
    } else {  
      this.title = "编辑业务规则";  
    }  
      
    // 加载元数据选项  
    this.getMetadataOptions();  
      
    // 如果是编辑模式，加载规则数据  
    if (this.mode === "edit" && this.ruleId) {  
      this.getRuleData();  
    }  
  },  
  methods: {  
    /** 生成UUID */  
    generateUuid() {  
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {  
        var r = Math.random() * 16 | 0,  
            v = c === 'x' ? r : (r & 0x3 | 0x8);  
        return v.toString(16);  
      });  
    },  
    /** 查询元数据选项 */  
    getMetadataOptions() {  
      listFormMetadata().then(response => {  
        this.metadataOptions = response.rows;  
          
        // 如果是新增模式且未指定元数据ID，默认选择第一个  
        if (this.mode === "add" && !this.form.metadataId && this.metadataOptions.length > 0) {  
          this.form.metadataId = this.metadataOptions[0].metadataId;  
          this.handleMetadataChange(this.form.metadataId);  
        }  
      });  
    },  
    /** 获取规则数据 */  
    getRuleData() {  
      getFormRule(this.ruleId).then(response => {  
        this.form = response.data;  
          
        // 加载字段选项  
        this.handleMetadataChange(this.form.metadataId);  
          
        // 加载规则条件  
        this.getConditions();  
          
        // 加载规则动作  
        this.getActions();  
      });  
    },  
    /** 加载规则条件 */  
    getConditions() {  
      listRuleCondition({ ruleId: this.ruleId }).then(response => {  
        if (response.rows && response.rows.length > 0) {  
          // 按条件组分组  
          const groupMap = {};  
          response.rows.forEach(condition => {  
            const groupId = condition.groupId || 'default';  
            if (!groupMap[groupId]) {  
              groupMap[groupId] = [];  
            }  
            groupMap[groupId].push({  
              conditionId: condition.conditionId,  
              fieldId: condition.fieldId,  
              fieldType: this.fieldMap[condition.fieldId] ? this.fieldMap[condition.fieldId].fieldType : null,  
              dictType: this.fieldMap[condition.fieldId] ? this.fieldMap[condition.fieldId].dictType : null,  
              operator: condition.operator,  
              value: condition.value,  
              logicType: condition.logicType,  
              groupId: condition.groupId  
            });  
          });  
            
          // 转换为条件组数组  
          this.conditionGroups = Object.keys(groupMap).map(groupId => {  
            return {  
              groupId: groupId,  
              conditions: groupMap[groupId]  
            };  
          });  
        } else {  
          // 默认一个空条件组  
          this.conditionGroups = [  
            {  
              groupId: this.generateUuid(),  
              conditions: [  
                {  
                  conditionId: this.generateUuid(),  
                  fieldId: null,  
                  fieldType: null,  
                  dictType: null,  
                  operator: "eq",  
                  value: null,  
                  logicType: "AND",  
                  groupId: null  
                }  
              ]  
            }  
          ];  
        }  
      });  
    },  
    /** 加载规则动作 */  
    getActions() {  
      listRuleAction({ ruleId: this.ruleId }).then(response => {  
        if (response.rows && response.rows.length > 0) {  
          this.actions = response.rows.map(action => {  
            return {  
              actionId: action.actionId,  
              actionType: action.actionType,  
              targetFieldId: action.targetFieldId,  
              actionValue: action.actionValue,  
              actionScript: action.actionScript  
            };  
          });  
        } else {  
          this.actions = [];  
        }  
      });  
    },  
    /** 元数据变更操作 */  
    handleMetadataChange(metadataId) {  
      // 加载字段选项  
      listFormFieldMetadata({ metadataId: metadataId }).then(response => {  
        this.fieldOptions = response.rows;  
          
        // 构建字段映射  
        this.fieldMap = {};  
        this.fieldOptions.forEach(field => {  
          this.fieldMap[field.fieldId] = field;  
        });  
      });  
    },  
    /** 字段变更操作 */  
    handleFieldChange(fieldId, groupIndex, condIndex) {  
      const field = this.fieldMap[fieldId];  
      if (field) {  
        // 更新条件的字段类型和字典类型  
        this.conditionGroups[groupIndex].conditions[condIndex].fieldType = field.fieldType;  
        this.conditionGroups[groupIndex].conditions[condIndex].dictType = field.dictType;  
          
        // 重置操作符和值  
        this.conditionGroups[groupIndex].conditions[condIndex].operator = "eq";  
        this.conditionGroups[groupIndex].conditions[condIndex].value = null;  
      }  
    },  
    /** 动作类型变更操作 */  
    handleActionTypeChange(actionType, index) {  
      // 如果是不需要目标字段的动作类型，清空目标字段  
      if (!this.needsTargetField(actionType)) {  
        this.actions[index].targetFieldId = null;  
      }  
        
      // 重置动作值和脚本  
      this.actions[index].actionValue = null;  
      this.actions[index].actionScript = null;  
    },  
    /** 获取操作符选项 */  
    getOperatorOptions(fieldType) {  
      if (fieldType === 'integer' || fieldType === 'decimal') {  
        return this.operatorOptions.number;  
      } else if (fieldType === 'boolean') {  
        return this.operatorOptions.boolean;  
      } else if (fieldType === 'date' || fieldType === 'datetime') {  
        return this.operatorOptions.date;  
      } else if (fieldType === 'enum') {  
        return this.operatorOptions.enum;  
      } else {  
        return this.operatorOptions.string;  
      }  
    },  
    /** 判断动作类型是否需要目标字段 */  
    needsTargetField(actionType) {  
      return ['setValue', 'setVisible', 'setRequired', 'setReadOnly', 'calculate'].includes(actionType);  
    },  
    /** 添加条件组 */  
    addConditionGroup() {  
      const groupId = this.generateUuid();  
      this.conditionGroups.push({  
        groupId: groupId,  
        conditions: [  
          {  
            conditionId: this.generateUuid(),  
            fieldId: null,  
            fieldType: null,  
            dictType: null,  
            operator: "eq",  
            value: null,  
            logicType: "AND",  
            groupId: groupId  
          }  
        ]  
      });  
    },  
    /** 移除条件组 */  
    removeConditionGroup(groupIndex) {  
      this.conditionGroups.splice(groupIndex, 1);  
    },  
    /** 添加条件 */  
    addCondition(groupIndex) {  
      const groupId = this.conditionGroups[groupIndex].groupId;  
      this.conditionGroups[groupIndex].conditions.push({  
        conditionId: this.generateUuid(),  
        fieldId: null,  
        fieldType: null,  
        dictType: null,  
        operator: "eq",  
        value: null,  
        logicType: "AND",  
        groupId: groupId  
      });  
    },  
    /** 移除条件 */  
    removeCondition(groupIndex, condIndex) {  
      // 如果是组内唯一条件，不允许删除  
      if (this.conditionGroups[groupIndex].conditions.length === 1) {  
        this.$message.warning("每个条件组至少需要一个条件");  
        return;  
      }  
        
      this.conditionGroups[groupIndex].conditions.splice(condIndex, 1);  
    },  
    /** 添加动作 */  
    addAction() {  
      this.actions.push({  
        actionId: this.generateUuid(),  
        actionType: "setValue",  
        targetFieldId: null,  
        actionValue: null,  
        actionScript: null  
      });  
    },  
    /** 移除动作 */  
    removeAction(index) {  
      this.actions.splice(index, 1);  
    },  
    /** 获取字典选项 */  
    getDictOptions(dictType) {  
      if (!dictType) {  
        return [];  
      }  
        
      if (this.dictCache[dictType]) {  
        return this.dictCache[dictType];  
      }  
        
      // 这里应该调用获取字典数据的API  
      // 为了简化，这里返回空数组  
      return [];  
    },  
    /** 提交表单 */  
    submitForm() {  
      this.$refs["form"].validate(valid => {  
        if (valid) {  
          // 验证条件和动作  
          if (!this.validateConditionsAndActions()) {  
            return;  
          }  
            
          // 准备提交数据  
          const submitData = {  
            ...this.form,  
            conditions: this.prepareConditions(),  
            actions: this.prepareActions()  
          };  
            
          if (this.mode === "add") {  
            // 新增规则  
            addFormRule(submitData).then(response => {  
              this.$modal.msgSuccess("新增成功");  
              this.goBack();  
            });  
          } else {  
            // 修改规则  
            updateFormRule(submitData).then(response => {  
              this.$modal.msgSuccess("修改成功");  
              this.goBack();  
            });  
          }  
        }  
      });  
    },  
    /** 验证条件和动作 */  
    validateConditionsAndActions() {  
      // 验证条件  
      for (let i = 0; i < this.conditionGroups.length; i++) {  
        const group = this.conditionGroups[i];  
        for (let j = 0; j < group.conditions.length; j++) {  
          const condition = group.conditions[j];  
          if (!condition.fieldId) {  
            this.$message.error(`条件组 ${i + 1} 的第 ${j + 1} 个条件未选择字段`);  
            return false;  
          }  
          if (!condition.operator) {  
            this.$message.error(`条件组 ${i + 1} 的第 ${j + 1} 个条件未选择操作符`);  
            return false;  
          }  
          if (condition.value === null || condition.value === undefined || condition.value === '') {  
            this.$message.error(`条件组 ${i + 1} 的第 ${j + 1} 个条件未设置比较值`);  
            return false;  
          }  
        }  
      }  
        
      // 验证动作  
      if (this.actions.length === 0) {  
        this.$message.error("至少需要添加一个动作");  
        return false;  
      }  
        
      for (let i = 0; i < this.actions.length; i++) {  
        const action = this.actions[i];  
        if (!action.actionType) {  
          this.$message.error(`第 ${i + 1} 个动作未选择动作类型`);  
          return false;  
        }  
          
        if (this.needsTargetField(action.actionType) && !action.targetFieldId) {  
          this.$message.error(`第 ${i + 1} 个动作未选择目标字段`);  
          return false;  
        }  
          
        if (action.actionType === 'calculate') {  
          if (!action.actionScript) {  
            this.$message.error(`第 ${i + 1} 个动作未设置计算表达式`);  
            return false;  
          }  
        } else if (action.actionType !== 'api' && !action.actionValue && action.actionValue !== false) {  
          this.$message.error(`第 ${i + 1} 个动作未设置动作值`);  
          return false;  
        }  
      }  
        
      return true;  
    },  
    /** 准备条件数据 */  
    prepareConditions() {  
      const conditions = [];  
        
      this.conditionGroups.forEach(group => {  
        group.conditions.forEach(condition => {  
          conditions.push({  
            conditionId: condition.conditionId,  
            ruleId: this.form.ruleId,  
            fieldId: condition.fieldId,  
            operator: condition.operator,  
            value: condition.value,  
            logicType: condition.logicType,  
            groupId: group.groupId,  
            tenantId: this.form.tenantId  
          });  
        });  
      });  
        
      return conditions;  
    },  
    /** 准备动作数据 */  
    prepareActions() {  
      return this.actions.map(action => {  
        return {  
          actionId: action.actionId,  
          ruleId: this.form.ruleId,  
          actionType: action.actionType,  
          targetFieldId: action.targetFieldId,  
          actionValue: action.actionValue,  
          actionScript: action.actionScript,  
          tenantId: this.form.tenantId  
        };  
      });  
    },  
    /** 返回列表页 */  
    goBack() {  
      this.$router.push({ path: "/rule" });  
    }  
  }  
};  
</script>  
  
<style scoped>  
.condition-container {  
  margin-bottom: 20px;  
}  
.condition-group {  
  border: 1px solid #e6e6e6;  
  border-radius: 4px;  
  padding: 15px;  
  margin-bottom: 15px;  
  background-color: #f9f9f9;  
}  
.group-header {  
  display: flex;  
  justify-content: space-between;  
  align-items: center;  
  margin-bottom: 10px;  
}  
.group-title {  
  font-weight: bold;  
  color: #606266;  
}  
.condition-item {  
  margin-bottom: 10px;  
}  
.add-group, .add-action {  
  margin-top: 15px;  
  text-align: center;  
}  
.action-container {  
  margin-bottom: 20px;  
}  
.action-item {  
  margin-bottom: 15px;  
  padding: 15px;  
  border: 1px solid #e6e6e6;  
  border-radius: 4px;  
  background-color: #f9f9f9;  
}  
</style>