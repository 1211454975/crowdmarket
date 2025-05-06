<template>  
  <div class="rule-designer-container">  
    <el-container>  
      <el-aside width="300px">  
        <el-card class="rule-list-panel">  
          <div slot="header" class="clearfix">  
            <span>规则列表</span>  
            <el-button style="float: right; padding: 3px 0" type="text" @click="handleAddRule">新增规则</el-button>  
          </div>  
          <el-input  
            placeholder="搜索规则"  
            v-model="searchKeyword"  
            clearable  
            prefix-icon="el-icon-search"  
            style="margin-bottom: 10px"  
          ></el-input>  
          <el-table  
            :data="filteredRules"  
            style="width: 100%"  
            @row-click="handleSelectRule"  
            highlight-current-row  
            :row-class-name="tableRowClassName"  
          >  
            <el-table-column prop="ruleName" label="规则名称"></el-table-column>  
            <el-table-column prop="ruleType" label="规则类型">  
              <template slot-scope="scope">  
                <el-tag :type="getRuleTypeTag(scope.row.ruleType)">{{ getRuleTypeText(scope.row.ruleType) }}</el-tag>  
              </template>  
            </el-table-column>  
            <el-table-column width="80">  
              <template slot-scope="scope">  
                <el-switch  
                  v-model="scope.row.isActive"  
                  @change="(val) => handleToggleActive(scope.row, val)"  
                ></el-switch>  
              </template>  
            </el-table-column>  
          </el-table>  
        </el-card>  
      </el-aside>  
        
      <el-container>  
        <el-header height="50px">  
          <el-button-group v-if="currentRule.ruleId">  
            <el-button size="small" type="primary" @click="handleSaveRule">保存规则</el-button>  
            <el-button size="small" type="danger" @click="handleDeleteRule">删除规则</el-button>  
          </el-button-group>  
        </el-header>  
          
        <el-main>  
          <el-card v-if="currentRule.ruleId" class="rule-config-panel">  
            <div slot="header">  
              <span>规则配置</span>  
            </div>  
            <el-form ref="ruleForm" :model="currentRule" :rules="rules" label-width="100px" size="small">  
              <el-form-item label="规则名称" prop="ruleName">  
                <el-input v-model="currentRule.ruleName" placeholder="请输入规则名称"></el-input>  
              </el-form-item>  
              <el-form-item label="规则类型" prop="ruleType">  
                <el-select v-model="currentRule.ruleType" placeholder="请选择规则类型" style="width: 100%">  
                  <el-option label="验证规则" value="validation"></el-option>  
                  <el-option label="计算规则" value="calculation"></el-option>  
                  <el-option label="可见性规则" value="visibility"></el-option>  
                  <el-option label="依赖规则" value="dependency"></el-option>  
                  <el-option label="工作流规则" value="workflow"></el-option>  
                </el-select>  
              </el-form-item>  
              <el-form-item label="规则描述">  
                <el-input type="textarea" v-model="currentRule.ruleDesc" placeholder="请输入规则描述"></el-input>  
              </el-form-item>  
              <el-form-item label="执行顺序" prop="executionOrder">  
                <el-input-number v-model="currentRule.executionOrder" :min="0" :max="999"></el-input-number>  
              </el-form-item>  
                
              <el-divider content-position="left">条件配置</el-divider>  
                
              <div v-for="(condition, index) in conditions" :key="'condition-'+index" class="condition-item">  
                <el-row :gutter="10">  
                  <el-col :span="index === 0 ? 24 : 4" v-if="index !== 0">  
                    <el-select v-model="condition.logicType" style="width: 100%">  
                      <el-option label="并且(AND)" value="AND"></el-option>  
                      <el-option label="或者(OR)" value="OR"></el-option>  
                    </el-select>  
                  </el-col>  
                  <el-col :span="index === 0 ? 8 : 6">  
                    <el-select v-model="condition.fieldId" placeholder="选择字段" style="width: 100%">  
                      <el-option  
                        v-for="field in fieldOptions"  
                        :key="field.fieldId"  
                        :label="field.fieldLabel"  
                        :value="field.fieldId"  
                      ></el-option>  
                    </el-select>  
                  </el-col>  
                  <el-col :span="6">  
                    <el-select v-model="condition.operator" placeholder="操作符" style="width: 100%">  
                      <el-option label="等于" value="eq"></el-option>  
                      <el-option label="不等于" value="ne"></el-option>  
                      <el-option label="大于" value="gt"></el-option>  
                      <el-option label="小于" value="lt"></el-option>  
                      <el-option label="大于等于" value="ge"></el-option>  
                      <el-option label="小于等于" value="le"></el-option>  
                      <el-option label="包含" value="contains"></el-option>  
                      <el-option label="开始于" value="startswith"></el-option>  
                      <el-option label="结束于" value="endswith"></el-option>  
                    </el-select>  
                  </el-col>  
                  <el-col :span="6">  
                    <el-input v-model="condition.value" placeholder="值"></el-input>  
                  </el-col>  
                  <el-col :span="2">  
                    <el-button  
                      type="danger"  
                      icon="el-icon-delete"  
                      circle  
                      @click="removeCondition(index)"  
                    ></el-button>  
                  </el-col>  
                </el-row>  
              </div>  
                
              <div style="margin-bottom: 20px">  
                <el-button type="primary" icon="el-icon-plus" size="small" @click="addCondition">添加条件</el-button>  
              </div>  
                
              <el-divider content-position="left">动作配置</el-divider>  
                
              <div v-for="(action, index) in actions" :key="'action-'+index" class="action-item">  
                <el-row :gutter="10">  
                  <el-col :span="6">  
                    <el-select v-model="action.actionType" placeholder="动作类型" style="width: 100%">  
                      <el-option label="设置值" value="setValue"></el-option>  
                      <el-option label="设置可见性" value="setVisible"></el-option>  
                      <el-option label="设置必填" value="setRequired"></el-option>  
                      <el-option label="设置只读" value="setReadOnly"></el-option>  
                      <el-option label="计算" value="calculate"></el-option>  
                      <el-option label="消息提示" value="message"></el-option>  
                      <el-option label="调用API" value="api"></el-option>  
                    </el-select>  
                  </el-col>  
                  <el-col :span="6" v-if="action.actionType !== 'message' && action.actionType !== 'api'">  
                    <el-select v-model="action.targetFieldId" placeholder="目标字段" style="width: 100%">  
                      <el-option  
                        v-for="field in fieldOptions"  
                        :key="field.fieldId"  
                        :label="field.fieldLabel"  
                        :value="field.fieldId"  
                      ></el-option>  
                    </el-select>  
                  </el-col>  
                  <el-col :span="action.actionType !== 'message' && action.actionType !== 'api' ? 10 : 16">  
                    <el-input   
                      v-if="action.actionType !== 'calculate' && action.actionType !== 'api'"  
                      v-model="action.actionValue"   
                      :placeholder="getActionValuePlaceholder(action.actionType)"  
                    ></el-input>  
                    <el-input   
                      v-else  
                      type="textarea"   
                      v-model="action.actionScript"   
                      :placeholder="getActionValuePlaceholder(action.actionType)"  
                      :rows="3"  
                    ></el-input>  
                  </el-col>  
                  <el-col :span="2">  
                    <el-button  
                      type="danger"  
                      icon="el-icon-delete"  
                      circle  
                      @click="removeAction(index)"  
                    ></el-button>  
                  </el-col>  
                </el-row>  
              </div>  
                
              <div style="margin-bottom: 20px">  
                <el-button type="primary" icon="el-icon-plus" size="small" @click="addAction">添加动作</el-button>  
              </div>  
            </el-form>  
          </el-card>  
            
          <el-empty v-else description="请选择或创建一个规则"></el-empty>  
        </el-main>  
      </el-container>  
    </el-container>  
      
    <!-- 新增规则对话框 -->  
    <el-dialog title="新增规则" :visible.sync="addRuleDialogVisible" width="500px">  
      <el-form ref="addRuleForm" :model="newRule" :rules="rules" label-width="100px">  
        <el-form-item label="规则名称" prop="ruleName">  
          <el-input v-model="newRule.ruleName" placeholder="请输入规则名称"></el-input>  
        </el-form-item>  
        <el-form-item label="规则类型" prop="ruleType">  
          <el-select v-model="newRule.ruleType" placeholder="请选择规则类型" style="width: 100%">  
            <el-option label="验证规则" value="validation"></el-option>  
            <el-option label="计算规则" value="calculation"></el-option>  
            <el-option label="可见性规则" value="visibility"></el-option>  
            <el-option label="依赖规则" value="dependency"></el-option>  
            <el-option label="工作流规则" value="workflow"></el-option>  
          </el-select>  
        </el-form-item>  
        <el-form-item label="规则描述">  
          <el-input type="textarea" v-model="newRule.ruleDesc" placeholder="请输入规则描述"></el-input>  
        </el-form-item>  
      </el-form>  
      <div slot="footer" class="dialog-footer">  
        <el-button @click="addRuleDialogVisible = false">取 消</el-button>  
        <el-button type="primary" @click="confirmAddRule">确 定</el-button>  
      </div>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import {   
  getRuleList,   
  getRule,   
  saveRule,   
  deleteRule,   
  updateRuleStatus,  
  getRuleConditions,  
  saveRuleCondition,  
  deleteRuleCondition,  
  getRuleActions,  
  saveRuleAction,  
  deleteRuleAction  
} from '@/api/metadata/rule'  
import { getMetadataFields } from '@/api/metadata/form'  
  
export default {  
  name: 'RuleDesigner',  
  props: {  
    metadataId: {  
      type: String,  
      required: true  
    }  
  },  
  data() {  
    return {  
      // 规则列表  
      ruleList: [],  
      // 当前选中的规则  
      currentRule: {  
        ruleId: '',  
        metadataId: this.metadataId,  
        ruleName: '',  
        ruleType: '',  
        ruleDesc: '',  
        isActive: true,  
        executionOrder: 0  
      },  
      // 条件列表  
      conditions: [],  
      // 动作列表  
      actions: [],  
      // 字段选项  
      fieldOptions: [],  
      // 搜索关键字  
      searchKeyword: '',  
      // 新增规则对话框可见性  
      addRuleDialogVisible: false,  
      // 新规则  
      newRule: {  
        metadataId: this.metadataId,  
        ruleName: '',  
        ruleType: '',  
        ruleDesc: '',  
        isActive: true,  
        executionOrder: 0  
      },  
      // 表单验证规则  
      rules: {  
        ruleName: [  
          { required: true, message: '请输入规则名称', trigger: 'blur' },  
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }  
        ],  
        ruleType: [  
          { required: true, message: '请选择规则类型', trigger: 'change' }  
        ],  
        executionOrder: [  
          { required: true, message: '请输入执行顺序', trigger: 'blur' }  
        ]  
      },  
      loading: false  
    }  
  },  
  computed: {  
    // 过滤后的规则列表  
    filteredRules() {  
      if (!this.searchKeyword) {  
        return this.ruleList  
      }  
      return this.ruleList.filter(rule =>   
        rule.ruleName.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||  
        this.getRuleTypeText(rule.ruleType).toLowerCase().includes(this.searchKeyword.toLowerCase())  
      )  
    }  
  },  
  created() {  
    this.loadRuleList()  
    this.loadFieldOptions()  
  },  
  methods: {  
    // 加载规则列表  
    loadRuleList() {  
      this.loading = true  
      getRuleList(this.metadataId).then(response => {  
        this.ruleList = response.data  
        this.loading = false  
      }).catch(() => {  
        this.loading = false  
      })  
    },  

    // 加载规则列表  
    loadRuleList() {  
      this.loading = true  
      getRuleList(this.metadataId).then(response => {  
        this.ruleList = response.data  
        this.loading = false  
      }).catch(() => {  
        this.loading = false  
      })  
    },  
      
    // 加载字段选项  
    loadFieldOptions() {  
      this.loading = true  
      getMetadataFields(this.metadataId).then(response => {  
        this.fieldOptions = response.data  
        this.loading = false  
      }).catch(() => {  
        this.loading = false  
      })  
    },  
      
    // 获取规则类型文本  
    getRuleTypeText(ruleType) {  
      const ruleTypeMap = {  
        'validation': '验证规则',  
        'calculation': '计算规则',  
        'visibility': '可见性规则',  
        'dependency': '依赖规则',  
        'workflow': '工作流规则'  
      }  
      return ruleTypeMap[ruleType] || ruleType  
    },  
      
    // 获取规则类型标签样式  
    getRuleTypeTag(ruleType) {  
      const ruleTypeTagMap = {  
        'validation': 'success',  
        'calculation': 'primary',  
        'visibility': 'info',  
        'dependency': 'warning',  
        'workflow': 'danger'  
      }  
      return ruleTypeTagMap[ruleType] || ''  
    },  
      
    // 表格行样式  
    tableRowClassName({ row }) {  
      if (row.ruleId === this.currentRule.ruleId) {  
        return 'selected-row'  
      }  
      return ''  
    },  
      
    // 选择规则  
    handleSelectRule(row) {  
      if (this.currentRule.ruleId === row.ruleId) {  
        return  
      }  
        
      this.currentRule = { ...row }  
      this.loadRuleDetails(row.ruleId)  
    },  
      
    // 加载规则详情  
    loadRuleDetails(ruleId) {  
      this.loading = true  
        
      // 加载规则条件  
      getRuleConditions(ruleId).then(response => {  
        this.conditions = response.data || []  
      }).catch(() => {  
        this.conditions = []  
      })  
        
      // 加载规则动作  
      getRuleActions(ruleId).then(response => {  
        this.actions = response.data || []  
        this.loading = false  
      }).catch(() => {  
        this.actions = []  
        this.loading = false  
      })  
    },  
      
    // 切换规则激活状态  
    handleToggleActive(rule, value) {  
      updateRuleStatus({  
        ruleId: rule.ruleId,  
        isActive: value ? 1 : 0  
      }).then(() => {  
        this.$message.success(`规则已${value ? '启用' : '停用'}`)  
        // 如果是当前选中的规则，更新当前规则状态  
        if (rule.ruleId === this.currentRule.ruleId) {  
          this.currentRule.isActive = value  
        }  
      }).catch(() => {  
        // 恢复原状态  
        rule.isActive = !value  
        this.$message.error('操作失败')  
      })  
    },  
      
    // 添加规则  
    handleAddRule() {  
      this.newRule = {  
        metadataId: this.metadataId,  
        ruleName: '',  
        ruleType: '',  
        ruleDesc: '',  
        isActive: true,  
        executionOrder: 0  
      }  
      this.addRuleDialogVisible = true  
    },  
      
    // 确认添加规则  
    confirmAddRule() {  
      this.$refs.addRuleForm.validate(valid => {  
        if (!valid) {  
          return  
        }  
          
        this.loading = true  
        saveRule(this.newRule).then(response => {  
          this.$message.success('添加成功')  
          this.addRuleDialogVisible = false  
            
          // 刷新规则列表  
          this.loadRuleList()  
            
          // 选中新添加的规则  
          const newRuleId = response.data  
          getRule(newRuleId).then(response => {  
            this.currentRule = response.data  
            this.conditions = []  
            this.actions = []  
          })  
            
          this.loading = false  
        }).catch(() => {  
          this.loading = false  
        })  
      })  
    },  
      
    // 保存规则  
    handleSaveRule() {  
      this.$refs.ruleForm.validate(valid => {  
        if (!valid) {  
          return  
        }  
          
        this.loading = true  
          
        // 保存规则基本信息  
        saveRule(this.currentRule).then(() => {  
          // 保存规则条件  
          const conditionPromises = this.conditions.map(condition => {  
            return saveRuleCondition({  
              ...condition,  
              ruleId: this.currentRule.ruleId,  
              tenantId: this.$store.getters.tenantId  
            })  
          })  
            
          // 保存规则动作  
          const actionPromises = this.actions.map(action => {  
            return saveRuleAction({  
              ...action,  
              ruleId: this.currentRule.ruleId,  
              tenantId: this.$store.getters.tenantId  
            })  
          })  
            
          Promise.all([...conditionPromises, ...actionPromises]).then(() => {  
            this.$message.success('保存成功')  
            this.loading = false  
              
            // 刷新规则列表  
            this.loadRuleList()  
          }).catch(() => {  
            this.loading = false  
            this.$message.error('保存失败')  
          })  
        }).catch(() => {  
          this.loading = false  
          this.$message.error('保存失败')  
        })  
      })  
    },  
      
    // 删除规则  
    handleDeleteRule() {  
      this.$confirm('确定要删除该规则吗？', '提示', {  
        type: 'warning'  
      }).then(() => {  
        this.loading = true  
        deleteRule(this.currentRule.ruleId).then(() => {  
          this.$message.success('删除成功')  
            
          // 刷新规则列表  
          this.loadRuleList()  
            
          // 清空当前规则  
          this.currentRule = {  
            ruleId: '',  
            metadataId: this.metadataId,  
            ruleName: '',  
            ruleType: '',  
            ruleDesc: '',  
            isActive: true,  
            executionOrder: 0  
          }  
          this.conditions = []  
          this.actions = []  
            
          this.loading = false  
        }).catch(() => {  
          this.loading = false  
          this.$message.error('删除失败')  
        })  
      }).catch(() => {})  
    },  
      
    // 添加条件  
    addCondition() {  
      this.conditions.push({  
        conditionId: '',  
        ruleId: this.currentRule.ruleId,  
        fieldId: '',  
        operator: 'eq',  
        value: '',  
        logicType: this.conditions.length === 0 ? 'AND' : 'AND',  
        groupId: null,  
        tenantId: this.$store.getters.tenantId  
      })  
    },  
      
    // 移除条件  
    removeCondition(index) {  
      const condition = this.conditions[index]  
        
      // 如果条件已保存到数据库，则调用删除API  
      if (condition.conditionId) {  
        deleteRuleCondition(condition.conditionId).then(() => {  
          this.conditions.splice(index, 1)  
        }).catch(() => {  
          this.$message.error('删除失败')  
        })  
      } else {  
        // 否则直接从数组中移除  
        this.conditions.splice(index, 1)  
      }  
    },  
      
    // 添加动作  
    addAction() {  
      this.actions.push({  
        actionId: '',  
        ruleId: this.currentRule.ruleId,  
        actionType: 'setValue',  
        targetFieldId: '',  
        actionValue: '',  
        actionScript: '',  
        tenantId: this.$store.getters.tenantId  
      })  
    },  
      
    // 移除动作  
    removeAction(index) {  
      const action = this.actions[index]  
        
      // 如果动作已保存到数据库，则调用删除API  
      if (action.actionId) {  
        deleteRuleAction(action.actionId).then(() => {  
          this.actions.splice(index, 1)  
        }).catch(() => {  
          this.$message.error('删除失败')  
        })  
      } else {  
        // 否则直接从数组中移除  
        this.actions.splice(index, 1)  
      }  
    },  
      
    // 获取动作值占位符  
    getActionValuePlaceholder(actionType) {  
      const placeholderMap = {  
        'setValue': '请输入设置的值',  
        'setVisible': '请输入true或false',  
        'setRequired': '请输入true或false',  
        'setReadOnly': '请输入true或false',  
        'calculate': '请输入计算表达式',  
        'message': '请输入消息内容',  
        'api': '请输入API调用脚本'  
      }  
      return placeholderMap[actionType] || '请输入值'  
    }  
  }  
}  
</script>  
  
<style scoped>  
.rule-designer-container {  
  height: 100%;  
}  
  
.el-container {  
  height: 100%;  
}  
  
.el-aside {  
  background-color: #f5f7fa;  
  overflow-y: auto;  
}  
  
.el-header {  
  background-color: #fff;  
  border-bottom: 1px solid #dcdfe6;  
  padding: 10px 15px;  
  display: flex;  
  align-items: center;  
}  
  
.el-main {  
  padding: 15px;  
  overflow-y: auto;  
}  
  
.rule-list-panel {  
  height: 100%;  
  overflow-y: auto;  
}  
  
.rule-config-panel {  
  height: 100%;  
  overflow-y: auto;  
}  
  
.condition-item {  
  margin-bottom: 15px;  
  padding: 10px;  
  border: 1px dashed #dcdfe6;  
  border-radius: 4px;  
}  
  
.action-item {  
  margin-bottom: 15px;  
  padding: 10px;  
  border: 1px dashed #dcdfe6;  
  border-radius: 4px;  
}  
  
.selected-row {  
  background-color: #f0f9eb;  
}  
</style>