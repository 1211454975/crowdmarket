<template>  
  <div class="app-container">  
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">  
      <el-form-item label="元数据ID" prop="metadataId">  
        <el-select v-model="queryParams.metadataId" placeholder="请选择元数据" clearable size="small" @change="handleMetadataChange">  
          <el-option  
            v-for="item in metadataOptions"  
            :key="item.metadataId"  
            :label="item.metadataName"  
            :value="item.metadataId"  
          />  
        </el-select>  
      </el-form-item>  
      <el-form-item label="视图名称" prop="viewName">  
        <el-input  
          v-model="queryParams.viewName"  
          placeholder="请输入视图名称"  
          clearable  
          size="small"  
          @keyup.enter.native="handleQuery"  
        />  
      </el-form-item>  
      <el-form-item label="视图类型" prop="viewType">  
        <el-select v-model="queryParams.viewType" placeholder="请选择视图类型" clearable size="small">  
          <el-option v-for="dict in viewTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
        </el-select>  
      </el-form-item>  
      <el-form-item>  
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>  
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>  
      </el-form-item>  
    </el-form>  
  
    <el-row :gutter="10" class="mb8">  
      <el-col :span="1.5">  
        <el-button  
          type="primary"  
          icon="el-icon-plus"  
          size="mini"  
          @click="handleAdd"  
          v-hasPermi="['metadata:view:add']"  
        >新增</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="success"  
          icon="el-icon-edit"  
          size="mini"  
          :disabled="single"  
          @click="handleUpdate"  
          v-hasPermi="['metadata:view:edit']"  
        >修改</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="danger"  
          icon="el-icon-delete"  
          size="mini"  
          :disabled="multiple"  
          @click="handleDelete"  
          v-hasPermi="['metadata:view:remove']"  
        >删除</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="warning"  
          icon="el-icon-download"  
          size="mini"  
          @click="handleExport"  
          v-hasPermi="['metadata:view:export']"  
        >导出</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="info"  
          icon="el-icon-view"  
          size="mini"  
          :disabled="single"  
          @click="handlePreview"  
          v-hasPermi="['metadata:view:query']"  
        >预览</el-button>  
      </el-col>  
    </el-row>  
  
    <el-table v-loading="loading" :data="viewList" @selection-change="handleSelectionChange">  
      <el-table-column type="selection" width="55" align="center" />  
      <el-table-column label="视图ID" align="center" prop="viewId" :show-overflow-tooltip="true" />  
      <el-table-column label="视图名称" align="center" prop="viewName" :show-overflow-tooltip="true" />  
      <el-table-column label="视图类型" align="center" prop="viewType">  
        <template slot-scope="scope">  
          <dict-tag :options="viewTypeOptions" :value="scope.row.viewType"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="是否默认" align="center" prop="isDefault">  
        <template slot-scope="scope">  
          <dict-tag :options="yesNoOptions" :value="scope.row.isDefault"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">  
        <template slot-scope="scope">  
          <span>{{ parseTime(scope.row.createTime) }}</span>  
        </template>  
      </el-table-column>  
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">  
        <template slot-scope="scope">  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-edit"  
            @click="handleUpdate(scope.row)"  
            v-hasPermi="['metadata:view:edit']"  
          >修改</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-delete"  
            @click="handleDelete(scope.row)"  
            v-hasPermi="['metadata:view:remove']"  
          >删除</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-view"  
            @click="handlePreview(scope.row)"  
            v-hasPermi="['metadata:view:query']"  
          >预览</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-star-off"  
            @click="handleSetDefault(scope.row)"  
            v-if="scope.row.isDefault === 0"  
            v-hasPermi="['metadata:view:edit']"  
          >设为默认</el-button>  
        </template>  
      </el-table-column>  
    </el-table>  
      
    <pagination  
      v-show="total>0"  
      :total="total"  
      :page.sync="queryParams.pageNum"  
      :limit.sync="queryParams.pageSize"  
      @pagination="getList"  
    />  
  
    <!-- 添加或修改视图配置对话框 -->  
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>  
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">  
        <el-form-item label="所属元数据" prop="metadataId">  
          <el-select v-model="form.metadataId" placeholder="请选择所属元数据" @change="handleFormMetadataChange">  
            <el-option  
              v-for="item in metadataOptions"  
              :key="item.metadataId"  
              :label="item.metadataName"  
              :value="item.metadataId"  
            />  
          </el-select>  
        </el-form-item>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="视图名称" prop="viewName">  
              <el-input v-model="form.viewName" placeholder="请输入视图名称" />  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="视图类型" prop="viewType">  
              <el-select v-model="form.viewType" placeholder="请选择视图类型" @change="handleViewTypeChange">  
                <el-option v-for="dict in viewTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
              </el-select>  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="是否默认视图" prop="isDefault">  
              <el-radio-group v-model="form.isDefault">  
                <el-radio :label="1">是</el-radio>  
                <el-radio :label="0">否</el-radio>  
              </el-radio-group>  
            </el-form-item>  
          </el-col>  
        </el-row>  
          
        <!-- 表单视图配置 -->  
        <div v-if="form.viewType === 'form'">  
          <el-divider content-position="left">表单布局配置</el-divider>  
          <el-form-item label="表单布局" prop="viewConfig.layout">  
            <el-radio-group v-model="formViewConfig.layout">  
              <el-radio label="1">一列布局</el-radio>  
              <el-radio label="2">两列布局</el-radio>  
              <el-radio label="3">三列布局</el-radio>  
              <el-radio label="tabs">标签页布局</el-radio>  
            </el-radio-group>  
          </el-form-item>  
            
          <el-form-item label="标签宽度" prop="viewConfig.labelWidth">  
            <el-input-number v-model="formViewConfig.labelWidth" :min="80" :max="200" :step="10" controls-position="right" />  
          </el-form-item>  
            
          <el-form-item label="表单尺寸" prop="viewConfig.size">  
            <el-radio-group v-model="formViewConfig.size">  
              <el-radio label="medium">中等</el-radio>  
              <el-radio label="small">小型</el-radio>  
              <el-radio label="mini">迷你</el-radio>  
            </el-radio-group>  
          </el-form-item>  
            
          <el-divider content-position="left">字段配置</el-divider>  
          <el-table :data="fieldList" border>  
            <el-table-column type="index" width="50" label="序号" />  
            <el-table-column prop="fieldLabel" label="字段标签" width="150" />  
            <el-table-column prop="fieldName" label="字段名称" width="150" />  
            <el-table-column prop="fieldType" label="字段类型" width="100" />  
            <el-table-column label="是否显示" width="100">  
              <template slot-scope="scope">  
                <el-checkbox v-model="scope.row.formVisible">显示</el-checkbox>  
              </template>  
            </el-table-column>  
            <el-table-column label="是否必填" width="100">  
              <template slot-scope="scope">  
                <el-checkbox v-model="scope.row.formRequired">必填</el-checkbox>  
              </template>  
            </el-table-column>  
            <el-table-column label="是否只读" width="100">  
              <template slot-scope="scope">  
                <el-checkbox v-model="scope.row.formReadonly">只读</el-checkbox>  
              </template>  
            </el-table-column>  
            <el-table-column label="排序" width="80">  
              <template slot-scope="scope">  
                <el-input-number v-model="scope.row.formOrder" :min="0" :max="999" controls-position="right" size="mini" />  
              </template>  
            </el-table-column>  
            <el-table-column label="分组" width="150">  
              <template slot-scope="scope">  
                <el-input v-model="scope.row.formGroup" placeholder="分组名称" size="mini" />  
              </template>  
            </el-table-column>  
          </el-table>  
        </div>  
          
        <!-- 列表视图配置 -->  
        <div v-if="form.viewType === 'list'">  
          <el-divider content-position="left">列表配置</el-divider>  
          <el-form-item label="表格尺寸" prop="viewConfig.size">  
            <el-radio-group v-model="listViewConfig.size">  
              <el-radio label="medium">中等</el-radio>  
              <el-radio label="small">小型</el-radio>  
              <el-radio label="mini">迷你</el-radio>  
            </el-radio-group>  
          </el-form-item>  
            
          <el-form-item label="显示边框" prop="viewConfig.border">  
            <el-switch v-model="listViewConfig.border" />  
          </el-form-item>  
            
          <el-form-item label="显示斑马纹" prop="viewConfig.stripe">  
            <el-switch v-model="listViewConfig.stripe" />  
          </el-form-item>  
            
          <el-form-item label="显示分页" prop="viewConfig.pagination">  
            <el-switch v-model="listViewConfig.pagination" />  
          </el-form-item>  
            
          <el-divider content-position="left">字段配置</el-divider>  
          <el-table :data="fieldList" border>  
            <el-table-column type="index" width="50" label="序号" />  
            <el-table-column prop="fieldLabel" label="字段标签" width="150" />  
            <el-table-column prop="fieldName" label="字段名称" width="150" />  
            <el-table-column prop="fieldType" label="字段类型" width="100" />  
            <el-table-column label="是否显示" width="100">  
              <template slot-scope="scope">  
                <el-checkbox v-model="scope.row.listVisible">显示</el-checkbox>  
              </template>  
            </el-table-column>  
            <el-table-column label="是否排序" width="100">  
              <template slot-scope="scope">  
                <el-checkbox v-model="scope.row.listSortable">排序</el-checkbox>  
              </template>  
            </el-table-column>  
            <el-table-column label="宽度" width="100">  
              <template slot-scope="scope">  
                <el-input-number v-model="scope.row.listWidth" :min="0" :max="500" controls-position="right" size="mini" />  
              </template>  
            </el-table-column>  
            <el-table-column label="排序" width="80">  
              <template slot-scope="scope">  
                <el-input-number v-model="scope.row.listOrder" :min="0" :max="999" controls-position="right" size="mini" />  
              </template>  
            </el-table-column>  
            <el-table-column label="对齐方式" width="120">  
              <template slot-scope="scope">  
                <el-select v-model="scope.row.listAlign" size="mini">  
                  <el-option label="左对齐" value="left" />  
                  <el-option label="居中对齐" value="center" />  
                  <el-option label="右对齐" value="right" />  
                </el-select>  
              </template>  
            </el-table-column>  
          </el-table>  
        </div>  
          
        <!-- 详情视图配置 -->  
        <div v-if="form.viewType === 'detail'">  
          <el-divider content-position="left">详情配置</el-divider>  
          <el-form-item label="详情布局" prop="viewConfig.layout">  
            <el-radio-group v-model="detailViewConfig.layout">  
              <el-radio label="1">一列布局</el-radio>  
              <el-radio label="2">两列布局</el-radio>  
              <el-radio label="3">三列布局</el-radio>  
              <el-radio label="tabs">标签页布局</el-radio>  
            </el-radio-group>  
          </el-form-item>  
            
          <el-form-item label="标签宽度" prop="viewConfig.labelWidth">  
            <el-input-number v-model="detailViewConfig.labelWidth" :min="80" :max="200" :step="10" controls-position="right" />  
          </el-form-item>  
            
          <el-form-item label="详情尺寸" prop="viewConfig.size">  
            <el-radio-group v-model="detailViewConfig.size">  
              <el-radio label="medium">中等</el-radio>  
              <el-radio label="small">小型</el-radio>  
              <el-radio label="mini">迷你</el-radio>  
            </el-radio-group>  
          </el-form-item>  
            
          <el-divider content-position="left">字段配置</el-divider>  
          <el-table :data="fieldList" border>  
            <el-table-column type="index" width="50" label="序号" />  
            <el-table-column prop="fieldLabel" label="字段标签" width="150" />  
            <el-table-column prop="fieldName" label="字段名称" width="150" />  
            <el-table-column prop="fieldType" label="字段类型" width="100" />  
            <el-table-column label="是否显示" width="100">  
              <template slot-scope="scope">  
                <el-checkbox v-model="scope.row.detailVisible">显示</el-checkbox>  
              </template>  
            </el-table-column>  
            <el-table-column label="排序" width="80">  
              <template slot-scope="scope">  
                <el-input-number v-model="scope.row.detailOrder" :min="0" :max="999" controls-position="right" size="mini" />  
              </template>  
            </el-table-column>  
            <el-table-column label="分组" width="150">  
              <template slot-scope="scope">  
                <el-input v-model="scope.row.detailGroup" placeholder="分组名称" size="mini" />  
              </template>  
            </el-table-column>  
          </el-table>  
        </div>  
      </el-form>  
      <div slot="footer" class="dialog-footer">  
        <el-button type="primary" @click="submitForm">确 定</el-button>  
        <el-button @click="cancel">取 消</el-button>  
      </div>  
    </el-dialog>  
      
    <!-- 视图预览对话框 -->  
    <el-dialog title="视图预览" :visible.sync="previewOpen" width="90%" append-to-body>  
      <div v-if="previewView.viewType === 'form'">  
        <h3>表单视图预览</h3>  
        <el-divider></el-divider>  
        <div class="preview-container">  
          <el-form   
            :model="previewData"   
            :label-width="previewView.viewConfig.labelWidth + 'px'"  
            :size="previewView.viewConfig.size"  
          >  
            <template v-if="previewView.viewConfig.layout === 'tabs'">  
              <el-tabs v-model="activeTab">  
                <el-tab-pane   
                  v-for="group in formGroups"   
                  :key="group"   
                  :label="group"   
                  :name="group"  
                >  
                  <el-row :gutter="20">  
                    <el-col :span="24" v-for="field in getGroupFields(group)" :key="field.fieldId">  
                      <el-form-item :label="field.fieldLabel">  
                        <component :is="getFieldComponent(field)" v-model="previewData[field.fieldName]" :disabled="field.formReadonly"></component>  
                      </el-form-item>  
                    </el-col>  
                  </el-row>  
                </el-tab-pane>  
              </el-tabs>  
            </template>  
            <template v-else>  
              <el-row :gutter="20">  
                <el-col :span="getColSpan(previewView.viewConfig.layout)" v-for="field in sortedFields" :key="field.fieldId" v-if="field.formVisible">  
                  <el-form-item :label="field.fieldLabel" :required="field.formRequired">  
                    <component :is="getFieldComponent(field)" v-model="previewData[field.fieldName]" :disabled="field.formReadonly"></component>  
                  </el-form-item>  
                </el-col>  
              </el-row>  
            </template>  
          </el-form>  
        </div>  
      </div>  
        
      <div v-if="previewView.viewType === 'list'">  
        <h3>列表视图预览</h3>  
        <el-divider></el-divider>  
        <div class="preview-container">  
          <el-table  
            :data="previewListData"  
            :border="previewView.viewConfig.border"  
            :stripe="previewView.viewConfig.stripe"  
            :size="previewView.viewConfig.size"  
          >  
            <el-table-column type="selection" width="55" align="center" />  
            <el-table-column   
              v-for="field in sortedListFields"   
              :key="field.fieldId"   
              :prop="field.fieldName"   
              :label="field.fieldLabel"   
              :width="field.listWidth || ''"   
              :align="field.listAlign || 'left'"  
              :sortable="field.listSortable"  
            />  
            <el-table-column label="操作" align="center" width="180">  
              <template>  
                <el-button size="mini" type="text" icon="el-icon-edit">修改</el-button>  
                <el-button size="mini" type="text" icon="el-icon-delete">删除</el-button>  
                <el-button size="mini" type="text" icon="el-icon-view">查看</el-button>  
              </template>  
            </el-table-column>  
          </el-table>  
          <div v-if="previewView.viewConfig.pagination" class="pagination-container">  
            <pagination :total="100" :page.sync="previewListQuery.pageNum" :limit.sync="previewListQuery.pageSize" />  
          </div>  
        </div>  
      </div>  
        
      <div v-if="previewView.viewType === 'detail'">  
        <h3>详情视图预览</h3>  
        <el-divider></el-divider>  
        <div class="preview-container">  
          <template v-if="previewView.viewConfig.layout === 'tabs'">  
            <el-tabs v-model="activeDetailTab">  
              <el-tab-pane   
                v-for="group in detailGroups"   
                :key="group"   
                :label="group"   
                :name="group"  
              >  
                <el-descriptions   
                  :column="1"   
                  :size="previewView.viewConfig.size"   
                  :label-width="previewView.viewConfig.labelWidth"  
                  border  
                >  
                  <el-descriptions-item   
                    v-for="field in getDetailGroupFields(group)"   
                    :key="field.fieldId"   
                    :label="field.fieldLabel"  
                  >  
                    {{ getFieldDisplayValue(field, previewData[field.fieldName]) }}  
                  </el-descriptions-item>  
                </el-descriptions>  
              </el-tab-pane>  
            </el-tabs>  
          </template>  
          <template v-else>  
            <el-descriptions   
              :column="parseInt(previewView.viewConfig.layout)"   
              :size="previewView.viewConfig.size"   
              :label-width="previewView.viewConfig.labelWidth"  
              border  
            >  
              <el-descriptions-item   
                v-for="field in sortedDetailFields"   
                :key="field.fieldId"   
                :label="field.fieldLabel"  
              >  
                {{ getFieldDisplayValue(field, previewData[field.fieldName]) }}  
              </el-descriptions-item>  
            </el-descriptions>  
          </template>  
        </div>  
      </div>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { listFormViewConfig, getFormViewConfig, delFormViewConfig, addFormViewConfig, updateFormViewConfig, exportFormViewConfig, setDefaultView } from "@/api/metadata/view";  
import { listFormMetadata } from "@/api/metadata/form";  
import { listFormFieldMetadata } from "@/api/metadata/field";  
  
export default {  
  name: "FormViewConfig",  
  data() {  
    return {  
      // 遮罩层  
      loading: true,  
      // 选中数组  
      ids: [],  
      // 非单个禁用  
      single: true,  
      // 非多个禁用  
      multiple: true,  
      // 显示搜索条件  
      showSearch: true,  
      // 总条数  
      total: 0,  
      // 视图配置表格数据  
      viewList: [],  
      // 弹出层标题  
      title: "",  
      // 是否显示弹出层  
      open: false,  
      // 是否显示预览弹出层  
      previewOpen: false,  
      // 查询参数  
      queryParams: {  
        pageNum: 1,  
        pageSize: 10,  
        metadataId: null,  
        viewName: null,  
        viewType: null  
      },  
      // 表单参数  
      form: {},  
      // 表单校验  
      rules: {  
        metadataId: [  
          { required: true, message: "所属元数据不能为空", trigger: "blur" }  
        ],  
        viewName: [  
          { required: true, message: "视图名称不能为空", trigger: "blur" }  
        ],  
        viewType: [  
          { required: true, message: "视图类型不能为空", trigger: "change" }  
        ]  
      },  
      // 元数据选项  
      metadataOptions: [],  
      // 视图类型选项  
      viewTypeOptions: [  
        { value: "form", label: "表单视图" },  
        { value: "list", label: "列表视图" },  
        { value: "detail", label: "详情视图" }  
      ],  
      // 是否选项  
      yesNoOptions: [  
        { value: 0, label: "否" },  
        { value: 1, label: "是" }  
      ],  
      // 字段列表  
      fieldList: [],  
      // 表单视图配置  
      formViewConfig: {  
        layout: "1",  
        labelWidth: 120,  
        size: "medium"  
      },  
      // 列表视图配置  
      listViewConfig: {  
        size: "medium",  
        border: true,  
        stripe: true,  
        pagination: true  
      },  
      // 详情视图配置  
      detailViewConfig: {  
        layout: "1",  
        labelWidth: 120,  
        size: "medium"  
      },  
      // 预览视图  
      previewView: {},  
      // 预览数据  
      previewData: {},  
      // 预览列表数据  
      previewListData: [],  
      // 预览列表查询参数  
      previewListQuery: {  
        pageNum: 1,  
        pageSize: 10  
      },  
      // 当前激活的标签页  
      activeTab: "",  
      // 当前激活的详情标签页  
      activeDetailTab: ""  
    };  
  },  
  computed: {  
    // 表单分组  
    formGroups() {  
      const groups = new Set();  
      this.fieldList.forEach(field => {  
        if (field.formVisible && field.formGroup) {  
          groups.add(field.formGroup);  
        }  
      });  
      return Array.from(groups);  
    },  
    // 详情分组  
    detailGroups() {  
      const groups = new Set();  
      this.fieldList.forEach(field => {  
        if (field.detailVisible && field.detailGroup) {  
          groups.add(field.detailGroup);  
        }  
      });  
      return Array.from(groups);  
    },  
    // 排序后的字段（表单）  
    sortedFields() {  
      return [...this.fieldList].filter(field => field.formVisible).sort((a, b) => a.formOrder - b.formOrder);  
    },  
    // 排序后的字段（列表）  
    sortedListFields() {  
      return [...this.fieldList].filter(field => field.listVisible).sort((a, b) => a.listOrder - b.listOrder);  
    },  
    // 排序后的字段（详情）  
    sortedDetailFields() {  
      return [...this.fieldList].filter(field => field.detailVisible).sort((a, b) => a.detailOrder - b.detailOrder);  
    }  
  },  
  created() {  
    this.getMetadataOptions();  
  },  
  methods: {  
    /** 查询元数据选项 */  
    getMetadataOptions() {  
      listFormMetadata().then(response => {  
        this.metadataOptions = response.rows;  
        if (this.metadataOptions.length > 0) {  
          this.queryParams.metadataId = this.metadataOptions[0].metadataId;  
          this.getList();  
        }  
      });  
    },  
    /** 查询视图配置列表 */  
    getList() {  
      this.loading = true;  
      listFormViewConfig(this.queryParams).then(response => {  
        this.viewList = response.rows;  
        this.total = response.total;  
        this.loading = false;  
      });  
    },  
    /** 查询字段列表 */  
    getFieldList(metadataId) {  
      return new Promise((resolve, reject) => {  
        listFormFieldMetadata({ metadataId: metadataId }).then(response => {  
          resolve(response.rows);  
        }).catch(error => {  
          reject(error);  
        });  
      });  
    },  
    /** 获取指定分组的字段（表单） */  
    getGroupFields(group) {  
      return this.fieldList.filter(field => field.formVisible && field.formGroup === group)  
        .sort((a, b) => a.formOrder - b.formOrder);  
    },  
      
    /** 获取指定分组的字段（详情） */  
    getDetailGroupFields(group) {  
      return this.fieldList.filter(field => field.detailVisible && field.detailGroup === group)  
        .sort((a, b) => a.detailOrder - b.detailOrder);  
    },  
      
    /** 根据布局获取列宽 */  
    getColSpan(layout) {  
      if (layout === "1") return 24;  
      if (layout === "2") return 12;  
      if (layout === "3") return 8;  
      return 24;  
    },  
      
    /** 获取字段对应的表单组件 */  
    getFieldComponent(field) {  
      switch (field.htmlType) {  
        case "input":  
          return "el-input";  
        case "textarea":  
          return "el-input";  
        case "select":  
          return "el-select";  
        case "radio":  
          return "el-radio-group";  
        case "checkbox":  
          return "el-checkbox-group";  
        case "datetime":  
          return "el-date-picker";  
        case "upload":  
          return "el-upload";  
        case "editor":  
          return "tinymce-editor";  
        default:  
          return "el-input";  
      }  
    },  
      
    /** 获取字段显示值 */  
    getFieldDisplayValue(field, value) {  
      if (value === undefined || value === null) {  
        return "-";  
      }  
        
      if (field.fieldType === "boolean") {  
        return value ? "是" : "否";  
      }  
        
      if (field.fieldType === "date" || field.fieldType === "datetime") {  
        return this.parseTime(value);  
      }  
        
      return value;  
    },  
      
    // 取消按钮  
    cancel() {  
      this.open = false;  
      this.reset();  
    },  
      
    // 表单重置  
    reset() {  
      this.form = {  
        viewId: null,  
        metadataId: this.queryParams.metadataId,  
        viewName: null,  
        viewType: "form",  
        viewConfig: null,  
        isDefault: 0,  
        tenantId: null  
      };  
        
      // 重置视图配置  
      this.formViewConfig = {  
        layout: "1",  
        labelWidth: 120,  
        size: "medium"  
      };  
        
      this.listViewConfig = {  
        size: "medium",  
        border: true,  
        stripe: true,  
        pagination: true  
      };  
        
      this.detailViewConfig = {  
        layout: "1",  
        labelWidth: 120,  
        size: "medium"  
      };  
        
      // 重置字段配置  
      this.fieldList = [];  
        
      this.resetForm("form");  
    },  
      
    /** 搜索按钮操作 */  
    handleQuery() {  
      this.queryParams.pageNum = 1;  
      this.getList();  
    },  
      
    /** 重置按钮操作 */  
    resetQuery() {  
      this.resetForm("queryForm");  
      this.handleQuery();  
    },  
      
    /** 元数据变更操作 */  
    handleMetadataChange(metadataId) {  
      this.queryParams.metadataId = metadataId;  
      this.handleQuery();  
    },  
      
    /** 表单元数据变更操作 */  
    handleFormMetadataChange(metadataId) {  
      // 加载字段列表  
      this.getFieldList(metadataId).then(fields => {  
        this.fieldList = fields.map(field => {  
          return {  
            ...field,  
            // 表单视图属性  
            formVisible: field.isList === 1,  
            formRequired: field.isRequired === 1,  
            formReadonly: false,  
            formOrder: field.sortOrder,  
            formGroup: "",  
            // 列表视图属性  
            listVisible: field.isList === 1,  
            listSortable: field.isQuery === 1,  
            listWidth: "",  
            listOrder: field.sortOrder,  
            listAlign: "left",  
            // 详情视图属性  
            detailVisible: field.isList === 1,  
            detailOrder: field.sortOrder,  
            detailGroup: ""  
          };  
        });  
      });  
    },  
      
    /** 视图类型变更操作 */  
    handleViewTypeChange(viewType) {  
      // 根据视图类型初始化预览数据  
      if (viewType === "form" || viewType === "detail") {  
        this.previewData = {};  
        this.fieldList.forEach(field => {  
          // 根据字段类型设置默认值  
          if (field.fieldType === "string") {  
            this.previewData[field.fieldName] = "示例文本";  
          } else if (field.fieldType === "integer") {  
            this.previewData[field.fieldName] = 100;  
          } else if (field.fieldType === "decimal") {  
            this.previewData[field.fieldName] = 100.00;  
          } else if (field.fieldType === "boolean") {  
            this.previewData[field.fieldName] = true;  
          } else if (field.fieldType === "date" || field.fieldType === "datetime") {  
            this.previewData[field.fieldName] = new Date();  
          } else if (field.fieldType === "enum") {  
            this.previewData[field.fieldName] = "选项1";  
          } else {  
            this.previewData[field.fieldName] = null;  
          }  
        });  
      } else if (viewType === "list") {  
        // 生成示例列表数据  
        this.previewListData = [];  
        for (let i = 0; i < 5; i++) {  
          const row = {};  
          this.fieldList.forEach(field => {  
            if (field.fieldType === "string") {  
              row[field.fieldName] = `示例文本${i + 1}`;  
            } else if (field.fieldType === "integer") {  
              row[field.fieldName] = (i + 1) * 100;  
            } else if (field.fieldType === "decimal") {  
              row[field.fieldName] = (i + 1) * 100.00;  
            } else if (field.fieldType === "boolean") {  
              row[field.fieldName] = i % 2 === 0;  
            } else if (field.fieldType === "date" || field.fieldType === "datetime") {  
              const date = new Date();  
              date.setDate(date.getDate() + i);  
              row[field.fieldName] = date;  
            } else if (field.fieldType === "enum") {  
              row[field.fieldName] = `选项${i + 1}`;  
            } else {  
              row[field.fieldName] = null;  
            }  
          });  
          this.previewListData.push(row);  
        }  
      }  
    },  
      
    // 多选框选中数据  
    handleSelectionChange(selection) {  
      this.ids = selection.map(item => item.viewId);  
      this.single = selection.length !== 1;  
      this.multiple = !selection.length;  
    },  
      
    /** 新增按钮操作 */  
    handleAdd() {  
      this.reset();  
      if (this.queryParams.metadataId) {  
        this.form.metadataId = this.queryParams.metadataId;  
        this.handleFormMetadataChange(this.queryParams.metadataId);  
      }  
      this.open = true;  
      this.title = "添加视图配置";  
    },  
      
    /** 修改按钮操作 */  
    handleUpdate(row) {  
      this.reset();  
      const viewId = row.viewId || this.ids;  
      getFormViewConfig(viewId).then(response => {  
        this.form = response.data;  
          
        // 解析视图配置  
        const viewConfig = JSON.parse(this.form.viewConfig || "{}");  
        if (this.form.viewType === "form") {  
          this.formViewConfig = {  
            layout: viewConfig.layout || "1",  
            labelWidth: viewConfig.labelWidth || 120,  
            size: viewConfig.size || "medium"  
          };  
        } else if (this.form.viewType === "list") {  
          this.listViewConfig = {  
            size: viewConfig.size || "medium",  
            border: viewConfig.border !== false,  
            stripe: viewConfig.stripe !== false,  
            pagination: viewConfig.pagination !== false  
          };  
        } else if (this.form.viewType === "detail") {  
          this.detailViewConfig = {  
            layout: viewConfig.layout || "1",  
            labelWidth: viewConfig.labelWidth || 120,  
            size: viewConfig.size || "medium"  
          };  
        }  
          
        // 加载字段配置  
        this.getFieldList(this.form.metadataId).then(fields => {  
          this.fieldList = fields.map(field => {  
            const fieldConfig = (viewConfig.fields || []).find(f => f.fieldId === field.fieldId) || {};  
              
            return {  
              ...field,  
              // 表单视图属性  
              formVisible: fieldConfig.formVisible !== undefined ? fieldConfig.formVisible : field.isList === 1,  
              formRequired: fieldConfig.formRequired !== undefined ? fieldConfig.formRequired : field.isRequired === 1,  
              formReadonly: fieldConfig.formReadonly || false,  
              formOrder: fieldConfig.formOrder !== undefined ? fieldConfig.formOrder : field.sortOrder,  
              formGroup: fieldConfig.formGroup || "",  
              // 列表视图属性  
              listVisible: fieldConfig.listVisible !== undefined ? fieldConfig.listVisible : field.isList === 1,  
              listSortable: fieldConfig.listSortable !== undefined ? fieldConfig.listSortable : field.isQuery === 1,  
              listWidth: fieldConfig.listWidth || "",  
              listOrder: fieldConfig.listOrder !== undefined ? fieldConfig.listOrder : field.sortOrder,  
              listAlign: fieldConfig.listAlign || "left",  
              // 详情视图属性  
              detailVisible: fieldConfig.detailVisible !== undefined ? fieldConfig.detailVisible : field.isList === 1,  
              detailOrder: fieldConfig.detailOrder !== undefined ? fieldConfig.detailOrder : field.sortOrder,  
              detailGroup: fieldConfig.detailGroup || ""  
            };  
          });  
        });  
          
        this.open = true;  
        this.title = "修改视图配置";  
      });  
    },  
      
    /** 预览按钮操作 */  
    handlePreview(row) {  
      getFormViewConfig(row.viewId).then(response => {  
        this.previewView = response.data;  
        this.previewView.viewConfig = JSON.parse(this.previewView.viewConfig || "{}");  
          
        // 加载字段配置  
        this.getFieldList(this.previewView.metadataId).then(fields => {  
          const viewConfig = this.previewView.viewConfig;  
          this.fieldList = fields.map(field => {  
            const fieldConfig = (viewConfig.fields || []).find(f => f.fieldId === field.fieldId) || {};  
              
            return {  
              ...field,  
              // 表单视图属性  
              formVisible: fieldConfig.formVisible !== undefined ? fieldConfig.formVisible : field.isList === 1,  
              formRequired: fieldConfig.formRequired !== undefined ? fieldConfig.formRequired : field.isRequired === 1,  
              formReadonly: fieldConfig.formReadonly || false,  
              formOrder: fieldConfig.formOrder !== undefined ? fieldConfig.formOrder : field.sortOrder,  
              formGroup: fieldConfig.formGroup || "",  
              // 列表视图属性  
              listVisible: fieldConfig.listVisible !== undefined ? fieldConfig.listVisible : field.isList === 1,  
              listSortable: fieldConfig.listSortable !== undefined ? fieldConfig.listSortable : field.isQuery === 1,  
              listWidth: fieldConfig.listWidth || "",  
              listOrder: fieldConfig.listOrder !== undefined ? fieldConfig.listOrder : field.sortOrder,  
              listAlign: fieldConfig.listAlign || "left",  
              // 详情视图属性  
              detailVisible: fieldConfig.detailVisible !== undefined ? fieldConfig.detailVisible : field.isList === 1,  
              detailOrder: fieldConfig.detailOrder !== undefined ? fieldConfig.detailOrder : field.sortOrder,  
              detailGroup: fieldConfig.detailGroup || ""  
            };  
          });  
            
          // 初始化预览数据  
          if (this.previewView.viewType === "form" || this.previewView.viewType === "detail") {  
            this.previewData = {};  
            this.fieldList.forEach(field => {  
              // 根据字段类型设置默认值  
              if (field.fieldType === "string") {  
                this.previewData[field.fieldName] = "示例文本";  
              } else if (field.fieldType === "integer") {  
                this.previewData[field.fieldName] = 100;  
              } else if (field.fieldType === "decimal") {  
                this.previewData[field.fieldName] = 100.00;  
              } else if (field.fieldType === "boolean") {  
                this.previewData[field.fieldName] = true;  
              } else if (field.fieldType === "date" || field.fieldType === "datetime") {  
                this.previewData[field.fieldName] = new Date();  
              } else if (field.fieldType === "enum") {  
                this.previewData[field.fieldName] = "选项1";  
              } else {  
                this.previewData[field.fieldName] = null;  
              }  
            });  
              
            // 设置默认激活的标签页  
            if (this.previewView.viewType === "form") {  
              this.activeTab = this.formGroups.length > 0 ? this.formGroups[0] : "";  
            } else {  
              this.activeDetailTab = this.detailGroups.length > 0 ? this.detailGroups[0] : "";  
            }  
          } else if (this.previewView.viewType === "list") {  
            // 生成示例列表数据  
            this.previewListData = [];  
            for (let i = 0; i < 5; i++) {  
              const row = {};  
              this.fieldList.forEach(field => {  
                if (field.fieldType === "string") {  
                  row[field.fieldName] = `示例文本${i + 1}`;  
                } else if (field.fieldType === "integer") {  
                  row[field.fieldName] = (i + 1) * 100;  
                } else if (field.fieldType === "decimal") {  
                  row[field.fieldName] = (i + 1) * 100.00;  
                } else if (field.fieldType === "boolean") {  
                  row[field.fieldName] = i % 2 === 0;  
                } else if (field.fieldType === "date" || field.fieldType === "datetime") {  
                  const date = new Date();  
                  date.setDate(date.getDate() + i);  
                  row[field.fieldName] = date;  
                } else if (field.fieldType === "enum") {  
                  row[field.fieldName] = `选项${i + 1}`;  
                } else {  
                  row[field.fieldName] = null;  
                }  
              });  
              this.previewListData.push(row);  
            }  
          }  
            
          this.previewOpen = true;  
        });  
      });  
    },  
      
    /** 设为默认视图 */  
    handleSetDefault(row) {  
      this.$modal.confirm('确认要将"' + row.viewName + '"设为默认视图吗?').then(() => {  
        return setDefaultView(row.viewId);  
      }).then(() => {  
        this.getList();  
        this.$modal.msgSuccess("设置成功");  
      }).catch(() => {});  
    },  
      
    /** 提交按钮 */  
    submitForm() {  
      this.$refs["form"].validate(valid => {  
        if (valid) {  
          // 准备视图配置  
          let viewConfig = {};  
            
          if (this.form.viewType === "form") {  
            viewConfig = {  
              ...this.formViewConfig,  
              fields: this.fieldList.map(field => {  
                return {  
                  fieldId: field.fieldId,  
                  formVisible: field.formVisible,  
                  formRequired: field.formRequired,  
                  formReadonly: field.formReadonly,  
                  formOrder: field.formOrder,  
                  formGroup: field.formGroup  
                };  
              })  
            };  
          } else if (this.form.viewType === "list") {  
            viewConfig = {  
              ...this.listViewConfig,  
              fields: this.fieldList.map(field => {  
                return {  
                  fieldId: field.fieldId,  
                  listVisible: field.listVisible,  
                  listSortable: field.listSortable,  
                  listWidth: field.listWidth,  
                  listOrder: field.listOrder,  
                  listAlign: field.listAlign  
                };  
              })  
            };  
          } else if (this.form.viewType === "detail") {  
            viewConfig = {  
              ...this.detailViewConfig,  
              fields: this.fieldList.map(field => {  
                return {  
                  fieldId: field.fieldId,  
                  detailVisible: field.detailVisible,  
                  detailOrder: field.detailOrder,  
                  detailGroup: field.detailGroup  
                };  
              })  
            };  
          }  
            
          // 将视图配置转换为JSON字符串  
          this.form.viewConfig = JSON.stringify(viewConfig);  
            
          if (this.form.viewId != null) {  
            updateFormViewConfig(this.form).then(response => {  
              this.$modal.msgSuccess("修改成功");  
              this.open = false;  
              this.getList();  
            });  
          } else {  
            addFormViewConfig(this.form).then(response => {  
              this.$modal.msgSuccess("新增成功");  
              this.open = false;  
              this.getList();  
            });  
          }  
        }  
      });  
    },  
      
    /** 删除按钮操作 */  
    handleDelete(row) {  
      const viewIds = row.viewId || this.ids;  
      this.$modal.confirm('是否确认删除视图配置编号为"' + viewIds + '"的数据项?').then(() => {  
        return delFormViewConfig(viewIds);  
      }).then(() => {  
        this.getList();  
        this.$modal.msgSuccess("删除成功");  
      }).catch(() => {});  
    },  
      
    /** 导出按钮操作 */  
    handleExport() {  
      this.$modal.confirm('是否确认导出所有视图配置数据项?').then(() => {  
        this.exportLoading = true;  
        return exportFormViewConfig(this.queryParams);  
      }).then(response => {  
        this.$download.excel(response, '视图配置数据');  
        this.exportLoading = false;  
      }).catch(() => {});  
    }  
  }  
};  
</script>  
  
<style scoped>  
.preview-container {  
  padding: 20px;  
  border: 1px solid #ebeef5;  
  border-radius: 4px;  
  background-color: #fff;  
}  
.pagination-container {  
  margin-top: 15px;  
  text-align: right;  
}  
</style>