<template>  
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
</template>  
  
<script>  
export default {  
  name: "RuleAction",  
  props: {  
    // 动作数据  
    value: {  
      type: Array,  
      required: true  
    },  
    // 字段选项  
    fieldOptions: {  
      type: Array,  
      required: true  
    }  
  },  
  data() {  
    return {  
      // 动作列表  
      actions: this.value,  
      // 动作类型选项  
      actionTypeOptions: [  
        { value: "setValue", label: "设置值" },  
        { value: "setVisible", label: "设置可见性" },  
        { value: "setRequired", label: "设置必填" },  
        { value: "setReadOnly", label: "设置只读" },  
        { value: "calculate", label: "计算" },  
        { value: "message", label: "消息提示" },  
        { value: "api", label: "调用API" }  
      ]  
    };  
  },  
  watch: {  
    value: {  
      handler(newVal) {  
        this.actions = newVal;  
      },  
      deep: true  
    },  
    actions: {  
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
    /** 判断动作类型是否需要目标字段 */  
    needsTargetField(actionType) {  
      return ['setValue', 'setVisible', 'setRequired', 'setReadOnly', 'calculate'].includes(actionType);  
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
    /** 验证动作 */  
    validate() {  
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
    }  
  }  
};  
</script>  
  
<style scoped>  
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
.add-action {  
  margin-top: 15px;  
  text-align: center;  
}  
</style>