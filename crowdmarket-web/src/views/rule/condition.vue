<template>  
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
</template>  
  
<script>  
export default {  
  name: "RuleCondition",  
  props: {  
    // 条件组数据  
    value: {  
      type: Array,  
      required: true  
    },  
    // 字段选项  
    fieldOptions: {  
      type: Array,  
      required: true  
    },  
    // 字段映射（字段ID -> 字段信息）  
    fieldMap: {  
      type: Object,  
      required: true  
    }  
  },  
  data() {  
    return {  
      // 条件组  
      conditionGroups: this.value,  
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
      // 字典数据缓存  
      dictCache: {}  
    };  
  },  
  watch: {  
    value: {  
      handler(newVal) {  
        this.conditionGroups = newVal;  
      },  
      deep: true  
    },  
    conditionGroups: {  
      handler(newVal) {  
        this.$emit('input', newVal);  
      },  
      deep: true  
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
    /** 验证条件 */  
    validate() {  
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
        
      return true;  
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
.add-group {  
  margin-top: 15px;  
  text-align: center;  
}  
</style>