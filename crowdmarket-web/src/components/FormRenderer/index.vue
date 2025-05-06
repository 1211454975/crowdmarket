<template>  
  <div class="form-renderer-container">  
    <el-form  
      ref="dynamicForm"  
      :model="formModel"  
      :rules="formRules"  
      :label-position="formConf.labelPosition"  
      :label-width="formConf.labelWidth + 'px'"  
      :size="formConf.size"  
      :disabled="formConf.disabled"  
    >  
      <template v-for="(field, index) in renderFields">  
        <!-- 普通表单项 -->  
        <el-form-item  
          v-if="field.layout === 'colFormItem' || !field.layout"  
          :key="field.vModel || index"  
          :label="field.label"  
          :prop="field.vModel"  
          :required="field.required"  
          :label-width="field.labelWidth ? `${field.labelWidth}px` : null"  
        >  
          <!-- 根据字段类型渲染不同的表单控件 -->  
          <component  
            :is="getComponentName(field)"  
            v-model="formModel[field.vModel]"  
            v-bind="getComponentProps(field)"  
            v-on="getComponentEvents(field)"  
          >  
            <!-- 处理特殊组件的插槽内容 -->  
            <template v-if="field.tag === 'el-select' && field.options && field.options.length > 0">  
              <el-option  
                v-for="(option, optionIndex) in field.options"  
                :key="optionIndex"  
                :label="option.label"  
                :value="option.value"  
                :disabled="option.disabled"  
              ></el-option>  
            </template>  
              
            <template v-if="field.tag === 'el-radio-group' && field.options && field.options.length > 0">  
              <template v-if="field.optionType === 'button'">  
                <el-radio-button  
                  v-for="(option, optionIndex) in field.options"  
                  :key="optionIndex"  
                  :label="option.value"  
                  :disabled="option.disabled"  
                >{{ option.label }}</el-radio-button>  
              </template>  
              <template v-else>  
                <el-radio  
                  v-for="(option, optionIndex) in field.options"  
                  :key="optionIndex"  
                  :label="option.value"  
                  :border="field.border"  
                  :disabled="option.disabled"  
                >{{ option.label }}</el-radio>  
              </template>  
            </template>  
              
            <template v-if="field.tag === 'el-checkbox-group' && field.options && field.options.length > 0">  
              <template v-if="field.optionType === 'button'">  
                <el-checkbox-button  
                  v-for="(option, optionIndex) in field.options"  
                  :key="optionIndex"  
                  :label="option.value"  
                  :disabled="option.disabled"  
                >{{ option.label }}</el-checkbox-button>  
              </template>  
              <template v-else>  
                <el-checkbox  
                  v-for="(option, optionIndex) in field.options"  
                  :key="optionIndex"  
                  :label="option.value"  
                  :border="field.border"  
                  :disabled="option.disabled"  
                >{{ option.label }}</el-checkbox>  
              </template>  
            </template>  
              
            <template v-if="field.tag === 'el-upload'">  
              <template v-if="field['list-type'] === 'picture-card'">  
                <i class="el-icon-plus"></i>  
              </template>  
              <template v-else>  
                <el-button size="small" type="primary" icon="el-icon-upload">{{ field.buttonText || '点击上传' }}</el-button>  
              </template>  
              <div v-if="field.showTip" slot="tip" class="el-upload__tip">  
                只能上传不超过 {{ field.fileSize || 2 }}{{ field.sizeUnit || 'MB' }} 的{{ field.accept || '文件' }}  
              </div>  
            </template>  
          </component>  
        </el-form-item>  
          
        <!-- 栅格布局 -->  
        <el-row  
          v-else-if="field.layout === 'rowFormItem'"  
          :key="field.formId || index"  
          :type="field.type"  
          :justify="field.justify"  
          :align="field.align"  
          :gutter="field.gutter"  
        >  
          <el-col  
            v-for="(col, colIndex) in field.children"  
            :key="colIndex"  
            :span="col.span"  
          >  
            <template v-for="(item, itemIndex) in col.list">  
              <el-form-item  
                :key="item.vModel || itemIndex"  
                :label="item.label"  
                :prop="item.vModel"  
                :required="item.required"  
                :label-width="item.labelWidth ? `${item.labelWidth}px` : null"  
              >  
                <!-- 递归渲染栅格内的表单控件 -->  
                <component  
                  :is="getComponentName(item)"  
                  v-model="formModel[item.vModel]"  
                  v-bind="getComponentProps(item)"  
                  v-on="getComponentEvents(item)"  
                >  
                  <!-- 处理特殊组件的插槽内容 -->  
                  <template v-if="item.tag === 'el-select' && item.options && item.options.length > 0">  
                    <el-option  
                      v-for="(option, optionIndex) in item.options"  
                      :key="optionIndex"  
                      :label="option.label"  
                      :value="option.value"  
                      :disabled="option.disabled"  
                    ></el-option>  
                  </template>  
                    
                  <template v-if="item.tag === 'el-radio-group' && item.options && item.options.length > 0">  
                    <template v-if="item.optionType === 'button'">  
                      <el-radio-button  
                        v-for="(option, optionIndex) in item.options"  
                        :key="optionIndex"  
                        :label="option.value"  
                        :disabled="option.disabled"  
                      >{{ option.label }}</el-radio-button>  
                    </template>  
                    <template v-else>  
                      <el-radio  
                        v-for="(option, optionIndex) in item.options"  
                        :key="optionIndex"  
                        :label="option.value"  
                        :border="item.border"  
                        :disabled="option.disabled"  
                      >{{ option.label }}</el-radio>  
                    </template>  
                  </template>  
                    
                  <template v-if="item.tag === 'el-checkbox-group' && item.options && item.options.length > 0">  
                    <template v-if="item.optionType === 'button'">  
                      <el-checkbox-button  
                        v-for="(option, optionIndex) in item.options"  
                        :key="optionIndex"  
                        :label="option.value"  
                        :disabled="option.disabled"  
                      >{{ option.label }}</el-checkbox-button>  
                    </template>  
                    <template v-else>  
                      <el-checkbox  
                        v-for="(option, optionIndex) in item.options"  
                        :key="optionIndex"  
                        :label="option.value"  
                        :border="item.border"  
                        :disabled="option.disabled"  
                      >{{ option.label }}</el-checkbox>  
                    </template>  
                  </template>  
                </component>  
              </el-form-item>  
            </template>  
          </el-col>  
        </el-row>  
      </template>  
        
      <!-- 表单按钮 -->  
      <el-form-item v-if="formConf.formBtns">  
        <el-button type="primary" @click="submitForm">提交</el-button>  
        <el-button @click="resetForm">重置</el-button>  
      </el-form-item>  
    </el-form>  
  </div>  
</template>  
  
<script>  
import { getDictDataByType } from '@/api/system/dict/data'  
  
export default {  
  name: 'FormRenderer',  
  props: {  
    // 表单配置  
    formConf: {  
      type: Object,  
      required: true  
    },  
    // 表单字段  
    formFields: {  
      type: Array,  
      required: true  
    },  
    // 表单数据  
    value: {  
      type: Object,  
      default: () => ({})  
    },  
    // 是否只读  
    readonly: {  
      type: Boolean,  
      default: false  
    }  
  },  
  data() {  
    return {  
      // 表单数据模型  
      formModel: {},  
      // 表单验证规则  
      formRules: {},  
      // 字典数据缓存  
      dictCache: {}  
    }  
  },  
  computed: {  
    // 处理后的渲染字段  
    renderFields() {  
      return this.formFields.map(field => {  
        // 处理字典类型  
        if (field.dictType && !field.options) {  
          this.loadDictData(field)  
        }  
        return field  
      })  
    }  
  },  
  watch: {  
    value: {  
      handler(val) {  
        if (val) {  
          this.formModel = { ...val }  
        }  
      },  
      immediate: true,  
      deep: true  
    },  
    formFields: {  
      handler(val) {  
        if (val && val.length > 0) {  
          this.initFormModel()  
          this.initFormRules()  
        }  
      },  
      immediate: true,  
      deep: true  
    }  
  },  
  created() {  
    this.initFormModel()  
    this.initFormRules()  
  },  
  methods: {  
    // 初始化表单数据模型  
    initFormModel() {  
      const model = {}  
        
      // 遍历所有字段，设置默认值  
      const processFields = (fields) => {  
        fields.forEach(field => {  
          if (field.vModel) {  
            // 如果外部传入了值，优先使用外部值  
            if (this.value && this.value[field.vModel] !== undefined) {  
              model[field.vModel] = this.value[field.vModel]  
            } else {  
              // 否则使用字段默认值  
              model[field.vModel] = field.defaultValue !== undefined ? field.defaultValue : ''  
            }  
          }  
            
          // 处理栅格布局中的字段  
          if (field.layout === 'rowFormItem' && field.children) {  
            field.children.forEach(col => {  
              if (col.list && col.list.length > 0) {  
                processFields(col.list)  
              }  
            })  
          }  
        })  
      }  
        
      processFields(this.formFields)  
      this.formModel = model  
    },  
      
    // 初始化表单验证规则  
    initFormRules() {  
      const rules = {}  
        
      // 遍历所有字段，设置验证规则  
      const processFields = (fields) => {  
        fields.forEach(field => {  
          if (field.vModel) {  
            const fieldRules = []  
              
            // 必填验证  
            if (field.required) {  
              const rule = {  
                required: true,  
                message: `请${this.getPlaceholderText(field)}${field.label}`,  
                trigger: this.getTrigger(field)  
              }  
                
              // 对于数组类型的字段（如多选框），添加类型验证  
              if (field.tag === 'el-checkbox-group') {  
                rule.type = 'array'  
              }  
                
              fieldRules.push(rule)  
            }  
              
            // 添加其他验证规则  
            if (field.regList && field.regList.length > 0) {  
              field.regList.forEach(item => {  
                fieldRules.push({  
                  pattern: item.pattern,  
                  message: item.message,  
                  trigger: this.getTrigger(field)  
                })  
              })  
            }  
              
            if (fieldRules.length > 0) {  
              rules[field.vModel] = fieldRules  
            }  
          }  
            
          // 处理栅格布局中的字段  
          if (field.layout === 'rowFormItem' && field.children) {  
            field.children.forEach(col => {  
              if (col.list && col.list.length > 0) {  
                processFields(col.list)  
              }  
            })  
          }  
        })  
      }  
        
      processFields(this.formFields)  
      this.formRules = rules  
    },  
      
    // 获取组件名称  
    getComponentName(field) {  
      return field.tag  
    },  
      
    // 获取组件属性  
    getComponentProps(field) {  
      const props = { ...field }  
        
      // 移除不需要传递给组件的属性  
      const excludeKeys = ['tag', 'label', 'vModel', 'formId', 'layout', 'required', 'regList', 'labelWidth']  
      excludeKeys.forEach(key => {  
        delete props[key]  
      })  
        
      // 处理只读模式  
      if (this.readonly) {  
        props.disabled = true  
      }  
        
      return props  
    },  
      
    // 获取组件事件  
    getComponentEvents(field) {  
      const events = {}  
        
      // 添加自定义事件处理  
      if (field.events && typeof field.events === 'object') {  
        Object.keys(field.events).forEach(eventName => {  
          events[eventName] = field.events[eventName]  
        })  
      }  
        
      return events  
    },  
      
    // 获取触发方式  
    getTrigger(field) {  
      switch (field.tag) {  
        case 'el-input':  
        case 'el-input-number':  
        case 'el-textarea':  
          return 'blur'  
        case 'el-select':  
        case 'el-radio-group':  
        case 'el-checkbox-group':  
        case 'el-date-picker':  
        case 'el-time-picker':  
        case 'el-switch':  
          return 'change'  
        default:  
          return 'blur'  
      }  
    },  
      
    // 获取占位文本  
    getPlaceholderText(field) {  
      switch (field.tag) {  
        case 'el-input':  
          return field.type === 'textarea' ? '输入' : '输入'  
        case 'el-select':  
          return '选择'  
        case 'el-radio-group':  
          return '选择'  
        case 'el-checkbox-group':  
          return '选择'  
        case 'el-date-picker':  
          return '选择'  
        case 'el-time-picker':  
          return '选择'  
        default:  
          return '输入'  
      }  
    },  
      
    // 加载字典数据  
    loadDictData(field) {  
      // 如果已经缓存了字典数据，直接使用缓存  
      if (this.dictCache[field.dictType]) {  
        field.options = this.dictCache[field.dictType]  
        return  
      }  
        
      // 否则从服务器获取字典数据  
      getDictDataByType(field.dictType).then(response => {  
        const dictData = response.data  
        if (dictData && dictData.length > 0) {  
          const options = dictData.map(item => ({  
            label: item.dictLabel,  
            value: item.dictValue,  
            disabled: item.status === '1'  
          }))  
            
          // 更新字段选项  
          field.options = options  
            
          // 缓存字典数据  
          this.dictCache[field.dictType] = options  
        }  
      })  
    },  
      
    // 提交表单  
    submitForm() {  
      this.$refs.dynamicForm.validate(valid => {  
        if (valid) {  
          this.$emit('submit', this.formModel)  
        } else {  
          this.$message.error('表单验证失败，请检查填写内容')  
          return false  
        }  
      })  
    },  
      
    // 重置表单  
    resetForm() {  
      this.$refs.dynamicForm.resetFields()  
      this.$emit('reset')  
    },  
      
    // 获取表单数据  
    getFormData() {  
      return this.formModel  
    },  
      
    // 设置表单数据  
    setFormData(data) {  
      if (data) {  
        Object.keys(data).forEach(key => {  
          if (this.formModel.hasOwnProperty(key)) {  
            this.formModel[key] = data[key]  
          }  
        })  
      }  
    },  
      
    // 验证表单  
    validate() {  
      return new Promise((resolve, reject) => {  
        this.$refs.dynamicForm.validate(valid => {  
          if (valid) {  
            resolve(this.formModel)  
          } else {  
            reject(new Error('表单验证失败'))  
          }  
        })  
      })  
    },  
      
    // 清除验证  
    clearValidate(props) {  
      this.$refs.dynamicForm.clearValidate(props)  
    }  
  }  
}  
</script>  
  
<style scoped>  
.form-renderer-container {  
  padding: 10px;  
}  
</style>