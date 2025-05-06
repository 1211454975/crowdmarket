<template>  
  <div class="app-container">  
    <!-- 元数据选择 -->  
    <el-card class="box-card" style="margin-bottom: 15px">  
      <div slot="header" class="clearfix">  
        <span>选择表单</span>  
      </div>  
      <el-form :inline="true">  
        <el-form-item>  
          <el-select v-model="selectedMetadata" placeholder="请选择表单" @change="handleMetadataChange">  
            <el-option  
              v-for="item in metadataOptions"  
              :key="item.metadataId"  
              :label="item.metadataName"  
              :value="item.metadataId"  
            />  
          </el-select>  
        </el-form-item>  
      </el-form>  
    </el-card>  
  
    <!-- 查询条件 -->  
    <el-form v-if="selectedMetadata" :model="queryParams" ref="queryForm" :inline="true" label-width="68px">  
      <el-form-item v-for="field in queryFields" :key="field.fieldId" :label="field.fieldLabel" :prop="field.fieldName">  
        <!-- 文本框 -->  
        <el-input  
          v-if="field.htmlType === 'input' && (field.queryType === 'EQ' || field.queryType === 'NE' || field.queryType === 'LIKE')"  
          v-model="queryParams[field.fieldName]"  
          :placeholder="'请输入' + field.fieldLabel"  
          clearable  
          size="small"  
          @keyup.enter.native="handleQuery"  
        />  
          
        <!-- 下拉框 -->  
        <el-select   
          v-else-if="field.htmlType === 'select'"  
          v-model="queryParams[field.fieldName]"  
          :placeholder="'请选择' + field.fieldLabel"  
          clearable  
          size="small"  
        >  
          <el-option  
            v-for="dict in getDictOptions(field.dictType)"  
            :key="dict.dictValue"  
            :label="dict.dictLabel"  
            :value="dict.dictValue"  
          />  
        </el-select>  
          
        <!-- 单选框 -->  
        <el-radio-group  
          v-else-if="field.htmlType === 'radio'"  
          v-model="queryParams[field.fieldName]"  
          size="small"  
        >  
          <el-radio-button  
            v-for="dict in getDictOptions(field.dictType)"  
            :key="dict.dictValue"  
            :label="dict.dictValue"  
          >{{dict.dictLabel}}</el-radio-button>  
        </el-radio-group>  
          
        <!-- 日期控件 -->  
        <el-date-picker  
          v-else-if="field.htmlType === 'datetime' && field.queryType !== 'BETWEEN'"  
          v-model="queryParams[field.fieldName]"  
          :type="field.fieldType === 'date' ? 'date' : 'datetime'"  
          :placeholder="'选择' + field.fieldLabel"  
          value-format="yyyy-MM-dd HH:mm:ss"  
          clearable  
          size="small"  
        />  
          
        <!-- 日期范围 -->  
        <el-date-picker  
          v-else-if="field.htmlType === 'datetime' && field.queryType === 'BETWEEN'"  
          v-model="dateRangeMap[field.fieldName]"  
          type="daterange"  
          range-separator="至"  
          start-placeholder="开始日期"  
          end-placeholder="结束日期"  
          value-format="yyyy-MM-dd HH:mm:ss"  
          clearable  
          size="small"  
          @change="handleDateRangeChange(field.fieldName)"  
        />  
          
        <!-- 数字范围 -->  
        <div v-else-if="(field.fieldType === 'integer' || field.fieldType === 'decimal') && field.queryType === 'BETWEEN'" class="el-range-editor--mini">  
          <el-input-number  
            v-model="queryParams[field.fieldName + 'Begin']"  
            :placeholder="'最小值'"  
            size="small"  
            style="width: 100px"  
          />  
          <span class="el-range-separator">-</span>  
          <el-input-number  
            v-model="queryParams[field.fieldName + 'End']"  
            :placeholder="'最大值'"  
            size="small"  
            style="width: 100px"  
          />  
        </div>  
          
        <!-- 数字比较 -->  
        <el-input-number  
          v-else-if="field.fieldType === 'integer' || field.fieldType === 'decimal'"  
          v-model="queryParams[field.fieldName]"  
          :placeholder="'请输入' + field.fieldLabel"  
          clearable  
          size="small"  
        />  
      </el-form-item>  
      <el-form-item>  
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>  
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>  
      </el-form-item>  
    </el-form>  
  
    <!-- 操作按钮 -->  
    <el-row v-if="selectedMetadata" :gutter="10" class="mb8">  
      <el-col :span="1.5">  
        <el-button  
          type="primary"  
          icon="el-icon-plus"  
          size="mini"  
          @click="handleAdd"  
          v-hasPermi="['formdata:data:add']"  
        >新增</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="success"  
          icon="el-icon-edit"  
          size="mini"  
          :disabled="single"  
          @click="handleUpdate"  
          v-hasPermi="['formdata:data:edit']"  
        >修改</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="danger"  
          icon="el-icon-delete"  
          size="mini"  
          :disabled="multiple"  
          @click="handleDelete"  
          v-hasPermi="['formdata:data:remove']"  
        >删除</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="warning"  
          icon="el-icon-download"  
          size="mini"  
          @click="handleExport"  
          v-hasPermi="['formdata:data:export']"  
        >导出</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="info"  
          icon="el-icon-upload2"  
          size="mini"  
          @click="handleImport"  
          v-hasPermi="['formdata:data:import']"  
        >导入</el-button>  
      </el-col>  
    </el-row>  
  
    <!-- 数据表格 -->  
    <el-table  
      v-if="selectedMetadata"  
      v-loading="loading"  
      :data="dataList"  
      @selection-change="handleSelectionChange"  
    >  
      <el-table-column type="selection" width="55" align="center" />  
      <el-table-column  
        v-for="field in listFields"  
        :key="field.fieldId"  
        :prop="field.fieldName"  
        :label="field.fieldLabel"  
        :width="field.listWidth || ''"  
        :align="field.listAlign || 'center'"  
        :sortable="field.listSortable ? 'custom' : false"  
        :show-overflow-tooltip="true"  
      >  
        <template slot-scope="scope">  
          <!-- 布尔值显示 -->  
          <dict-tag  
            v-if="field.fieldType === 'boolean'"  
            :options="[{dictValue: true, dictLabel: '是'}, {dictValue: false, dictLabel: '否'}]"  
            :value="scope.row[field.fieldName]"  
          />  
            
          <!-- 字典值显示 -->  
          <dict-tag  
            v-else-if="field.dictType"  
            :options="getDictOptions(field.dictType)"  
            :value="scope.row[field.fieldName]"  
          />  
            
          <!-- 日期显示 -->  
          <span v-else-if="field.fieldType === 'date' || field.fieldType === 'datetime'">  
            {{ parseTime(scope.row[field.fieldName]) }}  
          </span>  
            
          <!-- 默认显示 -->  
          <span v-else>{{ scope.row[field.fieldName] }}</span>  
        </template>  
      </el-table-column>  
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">  
        <template slot-scope="scope">  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-edit"  
            @click="handleUpdate(scope.row)"  
            v-hasPermi="['formdata:data:edit']"  
          >修改</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-delete"  
            @click="handleDelete(scope.row)"  
            v-hasPermi="['formdata:data:remove']"  
          >删除</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-view"  
            @click="handleView(scope.row)"  
            v-hasPermi="['formdata:data:query']"  
          >查看</el-button>  
        </template>  
      </el-table-column>  
    </el-table>  
      
    <!-- 分页 -->  
    <pagination  
      v-if="selectedMetadata && total > 0"  
      :total="total"  
      :page.sync="queryParams.pageNum"  
      :limit.sync="queryParams.pageSize"  
      @pagination="getList"  
    />  
  
    <!-- 导入对话框 -->  
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>  
      <el-upload  
        ref="upload"  
        :limit="1"  
        accept=".xlsx, .xls"  
        :headers="upload.headers"  
        :action="upload.url + '?metadataId=' + selectedMetadata"  
        :disabled="upload.isUploading"  
        :on-progress="handleFileUploadProgress"  
        :on-success="handleFileSuccess"  
        :auto-upload="false"  
        drag  
      >  
        <i class="el-icon-upload"></i>  
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>  
        <div class="el-upload__tip text-center" slot="tip">  
          <span>仅允许导入Excel文件</span>  
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>  
        </div>  
      </el-upload>  
      <div slot="footer" class="dialog-footer">  
        <el-button type="primary" @click="submitFileForm">确 定</el-button>  
        <el-button @click="upload.open = false">取 消</el-button>  
      </div>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { listFormMetadata, getFormMetadata } from "@/api/metadata/form";  
import { listFormFieldMetadata } from "@/api/metadata/field";  
import { listFormViewConfig } from "@/api/metadata/view";  
import { listFormData, getFormData, delFormData, exportFormData, importFormDataTemplate } from "@/api/formdata/index";  
import { getToken } from "@/utils/auth";  
  
export default {  
  name: "FormDataList",  
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
      // 总条数  
      total: 0,  
      // 表单数据列表  
      dataList: [],  
      // 弹出层标题  
      title: "",  
      // 是否显示弹出层  
      open: false,  
      // 查询参数  
      queryParams: {  
        pageNum: 1,  
        pageSize: 10,  
        metadataId: null  
      },  
      // 表单元数据选项  
      metadataOptions: [],  
      // 选中的元数据ID  
      selectedMetadata: null,  
      // 元数据信息  
      metadataInfo: null,  
      // 字段列表  
      fieldList: [],  
      // 查询字段  
      queryFields: [],  
      // 列表字段  
      listFields: [],  
      // 日期范围映射  
      dateRangeMap: {},  
      // 字典数据缓存  
      dictCache: {},  
      // 上传参数  
      upload: {  
        // 是否显示弹出层  
        open: false,  
        // 弹出层标题  
        title: "",  
        // 是否禁用上传  
        isUploading: false,  
        // 设置上传的请求头部  
        headers: { Authorization: "Bearer " + getToken() },  
        // 上传的地址  
        url: process.env.VUE_APP_BASE_API + "/formdata/import"  
      }  
    };  
  },  
  created() {  
    this.getMetadataOptions();  
  },  
  methods: {  
    /** 查询元数据选项 */  
    getMetadataOptions() {  
      listFormMetadata().then(response => {  
        this.metadataOptions = response.rows;  
      });  
    },  
    /** 元数据变更操作 */  
    handleMetadataChange(metadataId) {  
      this.selectedMetadata = metadataId;  
      this.queryParams.metadataId = metadataId;  
        
      // 重置查询参数  
      this.resetQuery();  
        
      // 加载元数据信息  
      getFormMetadata(metadataId).then(response => {  
        this.metadataInfo = response.data;  
          
        // 加载字段列表  
        this.loadFieldsAndViews();  
      });  
    },  
    /** 加载字段和视图配置 */  
    loadFieldsAndViews() {  
      // 加载字段列表  
      listFormFieldMetadata({ metadataId: this.selectedMetadata }).then(response => {  
        this.fieldList = response.rows;  
          
        // 设置查询字段  
        this.queryFields = this.fieldList.filter(field => field.isQuery === 1);  
          
        // 加载视图配置  
        listFormViewConfig({ metadataId: this.selectedMetadata, viewType: 'list', isDefault: 1 }).then(response => {  
          if (response.rows && response.rows.length > 0) {  
            const viewConfig = JSON.parse(response.rows[0].viewConfig || "{}");  
              
            // 设置列表字段  
            if (viewConfig.fields && viewConfig.fields.length > 0) {  
              this.listFields = this.fieldList.filter(field => {  
                const fieldConfig = viewConfig.fields.find(f => f.fieldId === field.fieldId);  
                return fieldConfig && fieldConfig.listVisible;  
              }).map(field => {  
                const fieldConfig = viewConfig.fields.find(f => f.fieldId === field.fieldId);  
                return {  
                  ...field,  
                  listWidth: fieldConfig.listWidth || "",  
                  listAlign: fieldConfig.listAlign || "center",  
                  listSortable: fieldConfig.listSortable || false,  
                  listOrder: fieldConfig.listOrder || 0  
                };  
              }).sort((a, b) => a.listOrder - b.listOrder);  
            } else {  
              // 默认显示所有列表字段  
              this.listFields = this.fieldList.filter(field => field.isList === 1)  
                .sort((a, b) => a.sortOrder - b.sortOrder);  
            }  
          } else {  
            // 默认显示所有列表字段  
            this.listFields = this.fieldList.filter(field => field.isList === 1)  
              .sort((a, b) => a.sortOrder - b.sortOrder);  
          }  
            
          // 加载数据  
          this.getList();  
        });  
      });  
    },  
    /** 查询表单数据列表 */  
    getList() {  
      this.loading = true;  
      listFormData(this.queryParams).then(response => {  
        this.dataList = response.rows;  
        this.total = response.total;  
        this.loading = false;  
      });  
    },  
    /** 搜索按钮操作 */  
    handleQuery() {  
      this.queryParams.pageNum = 1;  
      this.getList();  
    },  
    /** 重置按钮操作 */  
    resetQuery() {  
      // 保留元数据ID  
      const metadataId = this.queryParams.metadataId;  
      this.queryParams = {  
        pageNum: 1,  
        pageSize: 10,  
        metadataId: metadataId  
      };  
      this.dateRangeMap = {};  
      this.resetForm("queryForm");  
      this.handleQuery();  
    },  
    /** 日期范围变更操作 */  
    handleDateRangeChange(fieldName) {  
      if (this.dateRangeMap[fieldName]) {  
        this.queryParams[fieldName + 'Begin'] = this.dateRangeMap[fieldName][0];  
        this.queryParams[fieldName + 'End'] = this.dateRangeMap[fieldName][1];  
      } else {  
        this.queryParams[fieldName + 'Begin'] = null;  
        this.queryParams[fieldName + 'End'] = null;  
      }  
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
    /** 多选框选中数据 */  
    handleSelectionChange(selection) {  
      this.ids = selection.map(item => {  
        // 查找主键字段  
        const pkField = this.fieldList.find(field => field.isPk === 1);  
        if (pkField) {  
          return item[pkField.fieldName];  
        }  
        return null;  
      }).filter(id => id !== null);  
        
      this.single = selection.length !== 1;  
      this.multiple = !selection.length;  
    },  
    /** 新增按钮操作 */  
    handleAdd() {  
      this.$router.push({  
        path: '/formdata/form',  
        query: {  
          metadataId: this.selectedMetadata,  
          mode: 'add'  
        }  
      });  
    },  
    /** 修改按钮操作 */  
    handleUpdate(row) {  
      // 查找主键字段  
      const pkField = this.fieldList.find(field => field.isPk === 1);  
      if (!pkField) {  
        this.$modal.msgError("未找到主键字段，无法编辑");  
        return;  
      }  
        
      const id = row[pkField.fieldName];  
      this.$router.push({  
        path: '/formdata/form',  
        query: {  
          metadataId: this.selectedMetadata,  
          id: id,  
          mode: 'edit'  
        }  
      });  
    },  
    /** 查看按钮操作 */  
    handleView(row) {  
      // 查找主键字段  
      const pkField = this.fieldList.find(field => field.isPk === 1);  
      if (!pkField) {  
        this.$modal.msgError("未找到主键字段，无法查看");  
        return;  
      }  
        
      const id = row[pkField.fieldName];  
      this.$router.push({  
        path: '/formdata/detail',  
        query: {  
          metadataId: this.selectedMetadata,  
          id: id  
        }  
      });  
    },  
    /** 删除按钮操作 */  
    handleDelete(row) {  
      // 查找主键字段  
      const pkField = this.fieldList.find(field => field.isPk === 1);  
      if (!pkField) {  
        this.$modal.msgError("未找到主键字段，无法删除");  
        return;  
      }  
        
      const id = row ? row[pkField.fieldName] : this.ids;  
        
      this.$modal.confirm('是否确认删除所选数据项?').then(() => {  
        return delFormData({  
          metadataId: this.selectedMetadata,  
          ids: Array.isArray(id) ? id : [id]  
        });  
      }).then(() => {  
        this.getList();  
        this.$modal.msgSuccess("删除成功");  
      }).catch(() => {});  
    },  
    /** 导出按钮操作 */  
    handleExport() {  
      this.$modal.confirm('是否确认导出所有数据项?').then(() => {  
        this.exportLoading = true;  
        return exportFormData({  
          ...this.queryParams,  
          metadataId: this.selectedMetadata  
        });  
      }).then(response => {  
        this.$download.excel(response, this.metadataInfo.metadataName + '数据');  
        this.exportLoading = false;  
      }).catch(() => {});  
    },  
    /** 导入按钮操作 */  
    handleImport() {  
      this.upload.title = "导入" + this.metadataInfo.metadataName + "数据";  
      this.upload.open = true;  
    },  
    /** 下载模板操作 */  
    importTemplate() {  
      importFormDataTemplate(this.selectedMetadata).then(response => {  
        this.$download.excel(response, this.metadataInfo.metadataName + '模板');  
      });  
    },  
    // 文件上传中处理  
    handleFileUploadProgress(event, file, fileList) {  
      this.upload.isUploading = true;  
    },  
    // 文件上传成功处理  
    handleFileSuccess(response, file, fileList) {  
      this.upload.open = false;  
      this.upload.isUploading = false;  
      this.$refs.upload.clearFiles();  
      this.$alert(response.msg || '导入成功', "导入结果", { dangerouslyUseHTMLString: true });  
      this.getList();  
    },  
    // 提交上传文件  
    submitFileForm() {  
      this.$refs.upload.submit();  
    }  
  }  
};  
</script>  
  
<style scoped>  
.el-range-separator {  
  padding: 0 5px;  
}  
.el-range-editor--mini {  
  display: flex;  
  align-items: center;  
}  
</style>