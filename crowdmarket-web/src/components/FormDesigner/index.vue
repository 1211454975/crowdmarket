<template>  
  <div class="form-designer-container">  
    <el-container>  
      <el-aside width="280px">  
        <el-card class="component-list">  
          <div slot="header">  
            <span>组件列表</span>  
          </div>  
          <div class="components-group">  
            <div class="group-title">基础组件</div>  
            <draggable  
              :list="basicComponents"  
              :group="{ name: 'components', pull: 'clone', put: false }"  
              :clone="cloneComponent"  
              :sort="false"  
              class="components-draggable"  
            >  
              <div  
                v-for="(item, index) in basicComponents"  
                :key="index"  
                class="component-item"  
                @click="addComponent(item)"  
              >  
                <i :class="'el-icon-' + item.tagIcon"></i>  
                <span>{{ item.label }}</span>  
              </div>  
            </draggable>  
          </div>  
          <div class="components-group">  
            <div class="group-title">布局组件</div>  
            <draggable  
              :list="layoutComponents"  
              :group="{ name: 'components', pull: 'clone', put: false }"  
              :clone="cloneComponent"  
              :sort="false"  
              class="components-draggable"  
            >  
              <div  
                v-for="(item, index) in layoutComponents"  
                :key="index"  
                class="component-item"  
                @click="addComponent(item)"  
              >  
                <i :class="'el-icon-' + item.tagIcon"></i>  
                <span>{{ item.label }}</span>  
              </div>  
            </draggable>  
          </div>  
        </el-card>  
      </el-aside>  
        
      <el-container>  
        <el-header height="50px">  
          <el-button-group>  
            <el-button size="small" type="primary" @click="handleSave">保存</el-button>  
            <el-button size="small" @click="handlePreview">预览</el-button>  
            <el-button size="small" @click="handleClear">清空</el-button>  
          </el-button-group>  
          <el-button-group style="margin-left: 10px;">  
            <el-button size="small" @click="handleUndo" :disabled="!canUndo">撤销</el-button>  
            <el-button size="small" @click="handleRedo" :disabled="!canRedo">重做</el-button>  
          </el-button-group>  
        </el-header>  
          
        <el-main>  
          <el-card class="form-canvas">  
            <div slot="header">  
              <span>表单设计器</span>  
              <el-button style="float: right; padding: 3px 0" type="text" @click="handleFormConfig">表单配置</el-button>  
            </div>  
            <el-form  
              :model="formData"  
              :label-position="formConf.labelPosition"  
              :label-width="formConf.labelWidth + 'px'"  
              :size="formConf.size"  
            >  
              <draggable  
                v-model="drawingList"  
                :animation="340"  
                group="components"  
                class="drawing-board"  
                @start="dragStart"  
                @end="dragEnd"  
              >  
                <template v-for="(item, index) in drawingList">  
                  <form-item-wrapper  
                    :key="item.formId"  
                    :drawing-list="drawingList"  
                    :current-item="item"  
                    :index="index"  
                    :active-id="activeId"  
                    @activeItem="activeFormItem"  
                    @copyItem="copyFormItem"  
                    @deleteItem="deleteFormItem"  
                  />  
                </template>  
              </draggable>  
              <div v-show="drawingList.length === 0" class="empty-tip">  
                从左侧拖入或点击组件进行表单设计  
              </div>  
            </el-form>  
          </el-card>  
        </el-main>  
      </el-container>  
        
      <el-aside width="350px">  
        <el-card v-show="activeId !== null" class="property-panel">  
          <div slot="header">  
            <span>属性配置</span>  
          </div>  
          <el-form v-if="activeFormItem" label-position="top" size="small">  
            <el-form-item label="标签名称">  
              <el-input v-model="activeFormItem.label" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="字段名称">  
              <el-input v-model="activeFormItem.vModel" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="字段类型">  
              <el-select v-model="activeFormItem.fieldType" style="width: 100%">  
                <el-option label="文本" value="string" />  
                <el-option label="数字" value="number" />  
                <el-option label="日期" value="date" />  
                <el-option label="时间" value="datetime" />  
                <el-option label="布尔" value="boolean" />  
                <el-option label="枚举" value="enum" />  
                <el-option label="文件" value="file" />  
                <el-option label="图片" value="image" />  
                <el-option label="富文本" value="richtext" />  
              </el-select>  
            </el-form-item>  
            <el-form-item label="数据库列名">  
              <el-input v-model="activeFormItem.columnName" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="数据库列类型">  
              <el-select v-model="activeFormItem.columnType" style="width: 100%">  
                <el-option label="VARCHAR(255)" value="VARCHAR(255)" />  
                <el-option label="INT" value="INT" />  
                <el-option label="DECIMAL(10,2)" value="DECIMAL(10,2)" />  
                <el-option label="TEXT" value="TEXT" />  
                <el-option label="DATE" value="DATE" />  
                <el-option label="DATETIME" value="DATETIME" />  
                <el-option label="TINYINT(1)" value="TINYINT(1)" />  
              </el-select>  
            </el-form-item>  
            <el-form-item label="占位内容" v-if="activeFormItem.placeholder !== undefined">  
              <el-input v-model="activeFormItem.placeholder" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="宽度">  
              <el-input-number v-model="activeFormItem.span" :min="1" :max="24" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="标签宽度">  
              <el-input-number v-model="activeFormItem.labelWidth" :min="1" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="默认值">  
              <el-input v-model="activeFormItem.defaultValue" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="是否必填">  
              <el-switch v-model="activeFormItem.required" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="是否禁用">  
              <el-switch v-model="activeFormItem.disabled" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="是否主键">  
              <el-switch v-model="activeFormItem.isPk" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="是否列表显示">  
              <el-switch v-model="activeFormItem.isList" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="是否查询条件">  
              <el-switch v-model="activeFormItem.isQuery" @change="updateDrawingList" />  
            </el-form-item>  
            <el-form-item label="查询方式" v-if="activeFormItem.isQuery">  
              <el-select v-model="activeFormItem.queryType" style="width: 100%">  
                <el-option label="等于" value="EQ" />  
                <el-option label="不等于" value="NE" />  
                <el-option label="大于" value="GT" />  
                <el-option label="小于" value="LT" />  
                <el-option label="大于等于" value="GE" />  
                <el-option label="小于等于" value="LE" />  
                <el-option label="模糊" value="LIKE" />  
                <el-option label="范围" value="BETWEEN" />  
              </el-select>  
            </el-form-item>  
            <el-form-item label="字典类型" v-if="['select', 'radio', 'checkbox'].includes(activeFormItem.tag.replace('el-', ''))">  
              <el-select v-model="activeFormItem.dictType" style="width: 100%">  
                <el-option  
                  v-for="dict in dictOptions"  
                  :key="dict.dictType"  
                  :label="dict.dictName"  
                  :value="dict.dictType"  
                />  
              </el-select>  
            </el-form-item>  
            <el-form-item label="选项" v-if="['select', 'radio', 'checkbox'].includes(activeFormItem.tag.replace('el-', '')) && !activeFormItem.dictType">  
              <el-button size="small" type="text" @click="addOption">添加选项</el-button>  
              <el-table :data="activeFormItem.options" size="small" border>  
                <el-table-column prop="label" label="标签">  
                  <template slot-scope="scope">  
                    <el-input v-model="scope.row.label" size="mini" @change="updateDrawingList" />  
                  </template>  
                </el-table-column>  
                <el-table-column prop="value" label="值">  
                  <template slot-scope="scope">  
                    <el-input v-model="scope.row.value" size="mini" @change="updateDrawingList" />  
                  </template>  
                </el-table-column>  
                <el-table-column width="50">  
                  <template slot-scope="scope">  
                    <el-button  
                      size="mini"  
                      type="text"  
                      @click="removeOption(scope.$index)"  
                      icon="el-icon-delete"  
                    />  
                  </template>  
                </el-table-column>  
              </el-table>  
            </el-form-item>  
            <el-form-item label="排序">  
              <el-input-number v-model="activeFormItem.sortOrder" :min="0" @change="updateDrawingList" />  
            </el-form-item>  
          </el-form>  
          <div v-else class="empty-tip">  
            请选择一个组件  
          </div>  
        </el-card>  
        <el-card v-show="activeId === null" class="form-config-panel">  
          <div slot="header">  
            <span>表单配置</span>  
          </div>  
          <el-form label-position="top" size="small">  
            <el-form-item label="表单名称">  
              <el-input v-model="formConf.formName" />  
            </el-form-item>  
            <el-form-item label="表单描述">  
              <el-input type="textarea" v-model="formConf.formDesc" />  
            </el-form-item>  
            <el-form-item label="关联表名">  
              <el-input v-model="formConf.tableName" />  
            </el-form-item>  
            <el-form-item label="标签位置">  
              <el-radio-group v-model="formConf.labelPosition">  
                <el-radio-button label="left">左对齐</el-radio-button>  
                <el-radio-button label="right">右对齐</el-radio-button>  
                <el-radio-button label="top">顶部对齐</el-radio-button>  
              </el-radio-group>  
            </el-form-item>  
            <el-form-item label="标签宽度">  
              <el-input-number v-model="formConf.labelWidth" :min="50" :max="200" />  
            </el-form-item>  
            <el-form-item label="表单尺寸">  
              <el-radio-group v-model="formConf.size">  
                <el-radio-button label="medium">中等</el-radio-button>  
                <el-radio-button label="small">小型</el-radio-button>  
                <el-radio-button label="mini">迷你</el-radio-button>  
              </el-radio-group>  
            </el-form-item>  
            <el-form-item label="栅格间隔">  
              <el-input-number v-model="formConf.gutter" :min="0" :max="50" />  
            </el-form-item>  
            <el-form-item label="是否禁用">  
              <el-switch v-model="formConf.disabled" />  
            </el-form-item>  
            <el-form-item label="表单按钮">  
              <el-switch v-model="formConf.formBtns" />  
            </el-form-item>  
          </el-form>  
        </el-card>  
      </el-aside>  
    </el-container>  
      
    <!-- 表单配置对话框 -->  
    <el-dialog title="表单配置" :visible.sync="formConfigVisible" width="600px">  
      <el-form label-position="top">  
        <el-form-item label="表单名称">  
          <el-input v-model="formConf.formName" />  
        </el-form-item>  
        <el-form-item label="表单描述">  
          <el-input type="textarea" v-model="formConf.formDesc" />  
        </el-form-item>  
        <el-form-item label="关联表名">  
          <el-input v-model="formConf.tableName" />  
        </el-form-item>  
        <el-form-item label="标签位置">  
          <el-radio-group v-model="formConf.labelPosition">  
            <el-radio-button label="left">左对齐</el-radio-button>  
            <el-radio-button label="right">右对齐</el-radio-button>  
            <el-radio-button label="top">顶部对齐</el-radio-button>  
          </el-radio-group>  
        </el-form-item>  
        <el-form-item label="标签宽度">  
          <el-input-number v-model="formConf.labelWidth" :min="50" :max="200" />  
        </el-form-item>  
        <el-form-item label="表单尺寸">  
          <el-radio-group v-model="formConf.size">  
            <el-radio-button label="medium">中等</el-radio-button>  
            <el-radio-button label="small">小型</el-radio-button>  
            <el-radio-button label="mini">迷你</el-radio-button>  
          </el-radio-group>  
        </el-form-item>  
      </el-form>  
      <div slot="footer" class="dialog-footer">  
        <el-button @click="formConfigVisible = false">取 消</el-button>  
        <el-button type="primary" @click="saveFormConfig">确 定</el-button>  
      </div>  
    </el-dialog>  
      
    <!-- 预览表单对话框 -->  
    <el-dialog title="表单预览" :visible.sync="previewVisible" width="800px" append-to-body>  
      <form-renderer :form-conf="formConf" :form-fields="drawingList" />  
      <div slot="footer" class="dialog-footer">  
        <el-button @click="previewVisible = false">关闭</el-button>  
      </div>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import draggable from 'vuedraggable'  
import { deepClone } from '@/utils'  
import { getDictList } from '@/api/system/dict/data'  
import { saveFormMetadata, getFormMetadata } from '@/api/metadata/form'  
import { saveFormFieldMetadata } from '@/api/metadata/field'  
import { saveFormViewConfig } from '@/api/metadata/view'  
import FormItemWrapper from './FormItemWrapper'  
import FormRenderer from '@/components/FormRenderer'  
import { generateId } from '@/utils'  
  
export default {  
  name: 'FormDesigner',  
  components: {  
    draggable,  
    FormItemWrapper,  
    FormRenderer  
  },  
  props: {  
    metadataId: {  
      type: String,  
      default: ''  
    }  
  },  
  data() {  
    return {  
      // 基础组件列表  
      basicComponents: [  
        {  
          label: '单行文本',  
          tag: 'el-input',  
          tagIcon: 'edit',  
          placeholder: '请输入',  
          defaultValue: '',  
          span: 24,  
          labelWidth: null,  
          style: { width: '100%' },  
          clearable: true,  
          required: false,  
          disabled: false,  
          fieldType: 'string',  
          columnName: '',  
          columnType: 'VARCHAR(255)',  
          isPk: false,  
          isList: true,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'input',  
          dictType: '',  
          sortOrder: 0  
        },  
        {  
          label: '多行文本',  
          tag: 'el-input',  
          tagIcon: 'document',  
          type: 'textarea',  
          placeholder: '请输入',  
          defaultValue: '',  
          span: 24,  
          labelWidth: null,  
          required: false,  
          disabled: false,  
          fieldType: 'string',  
          columnName: '',  
          columnType: 'TEXT',  
          isPk: false,  
          isList: true,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'textarea',  
          dictType: '',  
          sortOrder: 0  
        },  
        {  
          label: '数字输入',  
          tag: 'el-input-number',  
          tagIcon: 'number',  
          defaultValue: 0,  
          span: 24,  
          labelWidth: null,  
          min: undefined,  
          max: undefined,  
          step: 1,  
          required: false,  
          disabled: false,  
          fieldType: 'number',  
          columnName: '',  
          columnType: 'INT',  
          isPk: false,  
          isList: true,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'input',  
          dictType: '',  
          sortOrder: 0  
        },  
        {  
          label: '下拉选择',  
          tag: 'el-select',  
          tagIcon: 'select',  
          placeholder: '请选择',  
          defaultValue: '',  
          span: 24,  
          labelWidth: null,  
          style: { width: '100%' },  
          clearable: true,  
          required: false,  
          disabled: false,  
          options: [  
            {  
              label: '选项一',  
              value: '1'  
            },  
            {  
              label: '选项二',  
              value: '2'  
            }  
          ],  
          fieldType: 'enum',  
          columnName: '',  
          columnType: 'VARCHAR(50)',  
          isPk: false,  
          isList: true,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'select',  
          dictType: '',  
          sortOrder: 0  
        },  
        {  
          label: '单选框组',  
          tag: 'el-radio-group',  
          tagIcon: 'radio',  
          defaultValue: '',  
          span: 24,  
          labelWidth: null,  
          style: {},  
          optionType: 'default',  
          border: false,  
          required: false,  
          disabled: false,  
          options: [  
            {  
              label: '选项一',  
              value: '1'  
            },  
            {  
              label: '选项二',  
              value: '2'  
            }  
          ],  
          fieldType: 'enum',  
          columnName: '',  
          columnType: 'VARCHAR(50)',  
          isPk: false,  
          isList: true,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'radio',  
          dictType: '',  
          sortOrder: 0  
        },  
        {  
          label: '复选框组',  
          tag: 'el-checkbox-group',  
          tagIcon: 'check',  
          defaultValue: [],  
          span: 24,  
          labelWidth: null,  
          style: {},  
          optionType: 'default',  
          border: false,  
          required: false,  
          disabled: false,  
          options: [  
            {  
              label: '选项一',  
              value: '1'  
            },  
            {  
              label: '选项二',  
              value: '2'  
            }  
          ],  
          fieldType: 'enum',  
          columnName: '',  
          columnType: 'VARCHAR(255)',  
          isPk: false,  
          isList: true,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'checkbox',  
          dictType: '',  
          sortOrder: 0  
        },  
        {  
          label: '日期选择',  
          tag: 'el-date-picker',  
          tagIcon: 'date',  
          defaultValue: '',  
          placeholder: '请选择',  
          span: 24,  
          labelWidth: null,  
          style: { width: '100%' },  
          type: 'date',  
          format: 'yyyy-MM-dd',  
          valueFormat: 'yyyy-MM-dd',  
          required: false,  
          disabled: false,  
          fieldType: 'date',  
          columnName: '',  
          columnType: 'DATE',  
          isPk: false,  
          isList: true,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'datetime',  
          dictType: '',  
          sortOrder: 0  
        },  
        {  
          label: '时间选择',  
          tag: 'el-date-picker',  
          tagIcon: 'time',  
          defaultValue: '',  
          placeholder: '请选择',  
          span: 24,  
          labelWidth: null,  
          style: { width: '100%' },  
          type: 'datetime',  
          format: 'yyyy-MM-dd HH:mm:ss',  
          valueFormat: 'yyyy-MM-dd HH:mm:ss',  
          required: false,  
          disabled: false,  
          fieldType: 'datetime',  
          columnName: '',  
          columnType: 'DATETIME',  
          isPk: false,  
          isList: true,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'datetime',  
          dictType: '',  
          sortOrder: 0  
        },  
        {  
          label: '上传文件',  
          tag: 'el-upload',  
          tagIcon: 'upload',  
          action: '/common/upload',  
          defaultValue: '',  
          span: 24,  
          labelWidth: null,  
          required: false,  
          disabled: false,  
          fieldType: 'file',  
          columnName: '',  
          columnType: 'VARCHAR(255)',  
          isPk: false,  
          isList: true,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'upload',  
          dictType: '',  
          sortOrder: 0  
        },  
        {  
          label: '富文本编辑器',  
          tag: 'tinymce',  
          tagIcon: 'document',  
          defaultValue: '',  
          span: 24,  
          labelWidth: null,  
          required: false,  
          disabled: false,  
          fieldType: 'richtext',  
          columnName: '',  
          columnType: 'TEXT',  
          isPk: false,  
          isList: false,  
          isQuery: false,  
          queryType: 'EQ',  
          htmlType: 'editor',  
          dictType: '',  
          sortOrder: 0  
        }  
      ],  
      // 布局组件列表  
      layoutComponents: [  
        {  
          label: '栅格布局',  
          tag: 'el-row',  
          tagIcon: 'table',  
          type: 'flex',  
          justify: 'start',  
          align: 'top',  
          gutter: 0,  
          children: [  
            {  
              span: 12,  
              list: []  
            },  
            {  
              span: 12,  
              list: []  
            }  
          ],  
          layout: 'rowFormItem'  
        },  
        {  
          label: '分割线',  
          tag: 'el-divider',  
          tagIcon: 'minus',  
          defaultValue: '',  
          span: 24,  
          labelWidth: null,  
          contentPosition: 'center',  
          layout: 'colFormItem'  
        }  
      ],  
      // 表单配置  
      formConf: {  
        formRef: 'elForm',  
        formModel: 'formData',  
        formRules: 'rules',  
        formName: '新建表单',  
        formDesc: '',  
        tableName: '',  
        size: 'medium',  
        labelPosition: 'right',  
        labelWidth: 100,  
        gutter: 15,  
        disabled: false,  
        formBtns: true  
      },  
      // 绘制列表  
      drawingList: [],  
      // 历史记录  
      historyData: [],  
      // 当前历史记录索引  
      historyIndex: -1,  
      // 当前激活的组件ID  
      activeId: null,  
      // 表单数据  
      formData: {},  
      // 表单配置对话框可见性  
      formConfigVisible: false,  
      // 预览对话框可见性  
      previewVisible: false,  
      // 字典选项  
      dictOptions: [],  
      // 是否正在拖拽  
      dragging: false  
    }  
  },  
  computed: {  
    // 当前激活的表单项  
    activeFormItem() {  
      if (this.activeId === null) return null  
      return this.drawingList.find(item => item.formId === this.activeId)  
    },  
    // 是否可以撤销  
    canUndo() {  
      return this.historyIndex > 0  
    },  
    // 是否可以重做  
    canRedo() {  
      return this.historyIndex < this.historyData.length - 1  
    }  
  },  
  created() {  
    this.getDictOptions()  
    if (this.metadataId) {  
      this.loadFormMetadata(this.metadataId)  
    }  
  },  
  methods: {  
    // 获取字典选项  
    getDictOptions() {  
      getDictList().then(response => {  
        this.dictOptions = response.data  
      })  
    },  
      
    // 加载表单元数据  
    loadFormMetadata(metadataId) {  
      getFormMetadata(metadataId).then(response => {  
        const metadata = response.data  
        this.formConf.formName = metadata.metadataName  
        this.formConf.formDesc = metadata.metadataDesc  
        this.formConf.tableName = metadata.tableName  
          
        // 加载字段元数据  
        this.drawingList = metadata.fields.map(field => {  
          // 根据字段类型和HTML类型找到对应的组件模板  
          const template = this.findComponentTemplate(field.fieldType, field.htmlType)  
          if (!template) return null  
            
          // 克隆模板并设置字段属性  
          const component = deepClone(template)  
          component.formId = generateId()  
          component.label = field.fieldLabel  
          component.vModel = field.fieldName  
          component.fieldType = field.fieldType  
          component.columnName = field.columnName  
          component.columnType = field.columnType  
          component.required = field.isRequired === 1  
          component.isPk = field.isPk === 1  
          component.isList = field.isList === 1  
          component.isQuery = field.isQuery === 1  
          component.queryType = field.queryType  
          component.htmlType = field.htmlType  
          component.dictType = field.dictType  
          component.sortOrder = field.sortOrder  
            
          return component  
        }).filter(Boolean)  
          
        // 加载视图配置  
        if (metadata.viewConfig) {  
          const viewConfig = JSON.parse(metadata.viewConfig)  
          if (viewConfig.formConf) {  
            Object.assign(this.formConf, viewConfig.formConf)  
          }  
        }  
          
        // 保存历史记录  
        this.saveHistory()  
      })  
    },  
      
    // 查找组件模板  
    findComponentTemplate(fieldType, htmlType) {  
      // 先在基础组件中查找  
      let template = this.basicComponents.find(comp =>   
        comp.fieldType === fieldType && comp.htmlType === htmlType  
      )  
        
      // 如果没找到，尝试匹配最接近的组件  
      if (!template) {  
        template = this.basicComponents.find(comp => comp.fieldType === fieldType)  
      }  
        
      // 如果还没找到，使用默认的文本输入组件  
      if (!template) {  
        template = this.basicComponents[0]  
      }  
        
      return template  
    },  
      
    // 克隆组件  
    cloneComponent(item) {  
      const clone = deepClone(item)  
      clone.formId = generateId()  
      clone.vModel = `field${this.drawingList.length + 1}`  
      clone.columnName = clone.vModel.toUpperCase()  
      return clone  
    },  
      
    // 添加组件  
    addComponent(item) {  
      const clone = this.cloneComponent(item)  
      this.drawingList.push(clone)  
      this.activeFormItem(clone.formId)  
      this.saveHistory()  
    },  
      
    // 激活表单项  
    activeFormItem(formId) {  
      this.activeId = formId  
    },  
      
    // 复制表单项  
    copyFormItem(formId) {  
      const item = this.drawingList.find(item => item.formId === formId)  
      if (item) {  
        const clone = deepClone(item)  
        clone.formId = generateId()  
        clone.vModel = `${item.vModel}_copy`  
        clone.columnName = clone.vModel.toUpperCase()  
        this.drawingList.push(clone)  
        this.activeFormItem(clone.formId)  
        this.saveHistory()  
      }  
    },  
      
    // 删除表单项  
    deleteFormItem(formId) {  
      const index = this.drawingList.findIndex(item => item.formId === formId)  
      if (index !== -1) {  
        this.drawingList.splice(index, 1)  
        this.activeId = null  
        this.saveHistory()  
      }  
    },  
      
    // 更新绘制列表  
    updateDrawingList() {  
      // 触发视图更新  
      this.drawingList = [...this.drawingList]  
      this.saveHistory()  
    },  
      
    // 保存历史记录  
    saveHistory() {  
      // 如果当前不是最新的历史记录，则删除后面的历史记录  
      if (this.historyIndex < this.historyData.length - 1) {  
        this.historyData = this.historyData.slice(0, this.historyIndex + 1)  
      }  
        
      // 添加新的历史记录  
      this.historyData.push({  
        drawingList: deepClone(this.drawingList),  
        formConf: deepClone(this.formConf)  
      })  
        
      // 更新历史记录索引  
      this.historyIndex = this.historyData.length - 1  
    },  
      
    // 撤销操作  
    handleUndo() {  
      if (this.canUndo) {  
        this.historyIndex--  
        const history = this.historyData[this.historyIndex]  
        this.drawingList = deepClone(history.drawingList)  
        this.formConf = deepClone(history.formConf)  
        this.activeId = null  
      }  
    },  
      
    // 重做操作  
    handleRedo() {  
      if (this.canRedo) {  
        this.historyIndex++  
        const history = this.historyData[this.historyIndex]  
        this.drawingList = deepClone(history.drawingList)  
        this.formConf = deepClone(history.formConf)  
        this.activeId = null  
      }  
    },  
      
    // 清空操作  
    handleClear() {  
      this.$confirm('确定要清空所有组件吗？', '提示', {  
        type: 'warning'  
      }).then(() => {  
        this.drawingList = []  
        this.activeId = null  
        this.saveHistory()  
      }).catch(() => {})  
    },  
      
    // 表单配置操作  
    handleFormConfig() {  
      this.formConfigVisible = true  
    },  
      
    // 保存表单配置  
    saveFormConfig() {  
      this.formConfigVisible = false  
      this.saveHistory()  
    },  
      
    // 预览操作  
    handlePreview() {  
      this.previewVisible = true  
    },  
      
    // 添加选项  
    addOption() {  
      if (this.activeFormItem && this.activeFormItem.options) {  
        this.activeFormItem.options.push({  
          label: '新选项',  
          value: `option${this.activeFormItem.options.length + 1}`  
        })  
        this.updateDrawingList()  
      }  
    },  
      
    // 移除选项  
    removeOption(index) {  
      if (this.activeFormItem && this.activeFormItem.options) {  
        this.activeFormItem.options.splice(index, 1)  
        this.updateDrawingList()  
      }  
    },  
      
    // 拖拽开始  
    dragStart() {  
      this.dragging = true  
    },  
      
    // 拖拽结束  
    dragEnd() {  
      this.dragging = false  
      this.saveHistory()  
    },  
      
    // 保存表单  
    handleSave() {  
      // 验证表单配置  
      if (!this.formConf.formName) {  
        this.$message.error('请输入表单名称')  
        return  
      }  
        
      if (!this.formConf.tableName) {  
        this.$message.error('请输入关联表名')  
        return  
      }  
        
      if (this.drawingList.length === 0) {  
        this.$message.error('请至少添加一个表单字段')  
        return  
      }  
        
      // 验证字段配置  
      for (const item of this.drawingList) {  
        if (!item.vModel) {  
          this.$message.error(`${item.label} 的字段名称不能为空`)  
          return  
        }  
          
        if (!item.columnName) {  
          this.$message.error(`${item.label} 的数据库列名不能为空`)  
          return  
        }  
      }  
        
      // 构建保存数据  
      const formMetadata = {  
        metadataId: this.metadataId,  
        metadataName: this.formConf.formName,  
        metadataDesc: this.formConf.formDesc,  
        tableName: this.formConf.tableName,  
        status: 0, // 草稿状态  
        tenantId: this.$store.getters.tenantId  
      }  
        
      // 保存表单元数据  
      saveFormMetadata(formMetadata).then(response => {  
        const metadataId = response.data  
          
        // 保存字段元数据  
        const fieldPromises = this.drawingList.map(item => {  
          const fieldMetadata = {  
            fieldId: item.fieldId, // 如果是编辑模式，可能已有ID  
            metadataId: metadataId,  
            fieldName: item.vModel,  
            fieldLabel: item.label,  
            fieldType: item.fieldType,  
            columnName: item.columnName,  
            columnType: item.columnType,  
            isRequired: item.required ? 1 : 0,  
            isPk: item.isPk ? 1 : 0,  
            isList: item.isList ? 1 : 0,  
            isQuery: item.isQuery ? 1 : 0,  
            queryType: item.queryType,  
            htmlType: item.htmlType,  
            dictType: item.dictType,  
            sortOrder: item.sortOrder,  
            tenantId: this.$store.getters.tenantId  
          }  
            
          return saveFormFieldMetadata(fieldMetadata)  
        })  
          
        // 保存视图配置  
        const viewConfig = {  
          viewId: '', // 新增时为空  
          metadataId: metadataId,  
          viewName: `${this.formConf.formName}默认表单视图`,  
          viewType: 'form',  
          viewConfig: JSON.stringify({  
            formConf: this.formConf,  
            drawingList: this.drawingList.map(item => {  
              // 移除不需要的属性  
              const { formId, ...rest } = item  
              return rest  
            })  
          }),  
          isDefault: 1, // 默认视图  
          tenantId: this.$store.getters.tenantId  
        }  
          
        Promise.all([...fieldPromises, saveFormViewConfig(viewConfig)]).then(() => {  
          this.$message.success('保存成功')  
            
          // 如果是新增，跳转到编辑模式  
          if (!this.metadataId) {  
            this.$router.push({ name: 'FormDesign', params: { metadataId: metadataId } })  
          }  
        }).catch(error => {  
          console.error('保存失败', error)  
          this.$message.error('保存失败')  
        })  
      }).catch(error => {  
        console.error('保存失败', error)  
        this.$message.error('保存失败')  
      })  
    }  
  }  
}  
</script>  
  
<style scoped>  
.form-designer-container {  
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
  
.component-list {  
  margin-bottom: 10px;  
}  
  
.components-group {  
  margin-bottom: 15px;  
}  
  
.group-title {  
  font-size: 14px;  
  font-weight: bold;  
  margin-bottom: 10px;  
  color: #606266;  
}  
  
.components-draggable {  
  display: grid;  
  grid-template-columns: repeat(2, 1fr);  
  grid-gap: 10px;  
}  
  
.component-item {  
  padding: 8px 10px;  
  border: 1px solid #dcdfe6;  
  border-radius: 4px;  
  background-color: #fff;  
  cursor: pointer;  
  display: flex;  
  align-items: center;  
  transition: all 0.2s;  
}  
  
.component-item:hover {  
  border-color: #409eff;  
  background-color: #ecf5ff;  
}  
  
.component-item i {  
  margin-right: 5px;  
  font-size: 16px;  
  color: #606266;  
}  
  
.form-canvas {  
  min-height: 500px;  
}  
  
.drawing-board {  
  min-height: 400px;  
}  
  
.property-panel, .form-config-panel {  
  margin-bottom: 10px;  
  height: calc(100vh - 120px);  
  overflow-y: auto;  
}  
  
.empty-tip {  
  text-align: center;  
  color: #909399;  
  padding: 30px 0;  
}  
</style>