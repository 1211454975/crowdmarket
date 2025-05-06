<template>  
  <div class="app-container">  
    <el-card>  
      <div slot="header" class="clearfix">  
        <span>数据导入导出</span>  
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回</el-button>  
      </div>  
        
      <!-- 元数据选择 -->  
      <el-form :inline="true" label-width="80px">  
        <el-form-item label="选择表单">  
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
        
      <el-divider content-position="center">数据导入</el-divider>  
        
      <el-row :gutter="20">  
        <el-col :span="12">  
          <el-card shadow="hover">  
            <div slot="header" class="clearfix">  
              <span>导入说明</span>  
            </div>  
            <div class="import-guide">  
              <p>1. 点击下载导入模板</p>  
              <p>2. 按照模板格式填写数据</p>  
              <p>3. 选择填写好的Excel文件上传</p>  
              <p>4. 点击"开始导入"按钮</p>  
              <p>注意事项：</p>  
              <ul>  
                <li>请严格按照模板格式填写数据</li>  
                <li>必填字段不能为空</li>  
                <li>日期格式为：YYYY-MM-DD</li>  
                <li>时间格式为：YYYY-MM-DD HH:MM:SS</li>  
                <li>文件大小不能超过10MB</li>  
              </ul>  
            </div>  
            <div class="import-actions">  
              <el-button type="primary" icon="el-icon-download" @click="downloadTemplate">下载导入模板</el-button>  
            </div>  
          </el-card>  
        </el-col>  
        <el-col :span="12">  
          <el-card shadow="hover">  
            <div slot="header" class="clearfix">  
              <span>上传文件</span>  
            </div>  
            <el-upload  
              ref="upload"  
              :action="uploadUrl"  
              :headers="uploadHeaders"  
              :data="uploadData"  
              :before-upload="beforeUpload"  
              :on-progress="handleFileUploadProgress"  
              :on-success="handleFileSuccess"  
              :on-error="handleFileError"  
              :auto-upload="false"  
              :limit="1"  
              :disabled="!selectedMetadata || uploading"  
              drag  
              accept=".xlsx,.xls"  
            >  
              <i class="el-icon-upload"></i>  
              <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>  
              <div class="el-upload__tip" slot="tip">只能上传Excel文件，且不超过10MB</div>  
            </el-upload>  
            <div class="upload-actions">  
              <el-button   
                type="success"   
                icon="el-icon-upload"   
                :loading="uploading"   
                :disabled="!selectedMetadata"   
                @click="submitUpload"  
              >开始导入</el-button>  
            </div>  
          </el-card>  
        </el-col>  
      </el-row>  
        
      <el-divider content-position="center">数据导出</el-divider>  
        
      <el-card shadow="hover">  
        <div slot="header" class="clearfix">  
          <span>导出选项</span>  
        </div>  
          
        <el-form :model="exportForm" label-width="100px">  
          <el-form-item label="导出范围">  
            <el-radio-group v-model="exportForm.exportType">  
              <el-radio label="all">导出全部数据</el-radio>  
              <el-radio label="filter">导出筛选数据</el-radio>  
            </el-radio-group>  
          </el-form-item>  
            
          <template v-if="exportForm.exportType === 'filter'">  
            <el-divider content-position="left">筛选条件</el-divider>  
              
            <el-form-item v-for="field in queryFields" :key="field.fieldId" :label="field.fieldLabel" :prop="field.fieldName">  
              <!-- 文本框 -->  
              <el-input  
                v-if="field.htmlType === 'input' && (field.queryType === 'EQ' || field.queryType === 'NE' || field.queryType === 'LIKE')"  
                v-model="exportForm.queryParams[field.fieldName]"  
                :placeholder="'请输入' + field.fieldLabel"  
                clearable  
              />  
                
              <!-- 下拉框 -->  
              <el-select   
                v-else-if="field.htmlType === 'select'"  
                v-model="exportForm.queryParams[field.fieldName]"  
                :placeholder="'请选择' + field.fieldLabel"  
                clearable  
              >  
                <el-option  
                  v-for="dict in getDictOptions(field.dictType)"  
                  :key="dict.dictValue"  
                  :label="dict.dictLabel"  
                  :value="dict.dictValue"  
                />  
              </el-select>  
                
              <!-- 日期控件 -->  
              <el-date-picker  
                v-else-if="field.htmlType === 'datetime' && field.queryType !== 'BETWEEN'"  
                v-model="exportForm.queryParams[field.fieldName]"  
                :type="field.fieldType === 'date' ? 'date' : 'datetime'"  
                :placeholder="'选择' + field.fieldLabel"  
                value-format="yyyy-MM-dd HH:mm:ss"  
                clearable  
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
                @change="handleDateRangeChange(field.fieldName)"  
              />  
                
              <!-- 数字范围 -->  
              <div v-else-if="(field.fieldType === 'integer' || field.fieldType === 'decimal') && field.queryType === 'BETWEEN'" class="el-range-editor">  
                <el-input-number  
                  v-model="exportForm.queryParams[field.fieldName + 'Begin']"  
                  :placeholder="'最小值'"  
                  style="width: 100px"  
                />  
                <span class="el-range-separator">-</span>  
                <el-input-number  
                  v-model="exportForm.queryParams[field.fieldName + 'End']"  
                  :placeholder="'最大值'"  
                  style="width: 100px"  
                />  
              </div>  
                
              <!-- 数字比较 -->  
              <el-input-number  
                v-else-if="field.fieldType === 'integer' || field.fieldType === 'decimal'"  
                v-model="exportForm.queryParams[field.fieldName]"  
                :placeholder="'请输入' + field.fieldLabel"  
                clearable  
              />  
            </el-form-item>  
          </template>  
            
          <el-form-item label="文件格式">  
            <el-radio-group v-model="exportForm.fileType">  
              <el-radio label="xlsx">Excel 2007+ (.xlsx)</el-radio>  
              <el-radio label="xls">Excel 97-2003 (.xls)</el-radio>  
              <el-radio label="csv">CSV (.csv)</el-radio>  
            </el-radio-group>  
          </el-form-item>  
            
          <el-form-item>  
            <el-button   
              type="primary"   
              icon="el-icon-download"   
              :loading="exporting"   
              :disabled="!selectedMetadata"   
              @click="handleExport"  
            >开始导出</el-button>  
            <el-button icon="el-icon-refresh" @click="resetExportForm">重置</el-button>  
          </el-form-item>  
        </el-form>  
      </el-card>  
        
      <!-- 导入结果对话框 -->  
      <el-dialog title="导入结果" :visible.sync="importResultVisible" width="600px" append-to-body>  
        <div v-if="importResult.success">  
          <el-result icon="success" title="导入成功" :sub-title="'成功导入 ' + importResult.successCount + ' 条数据'">  
            <template slot="extra">  
              <el-button type="primary" @click="importResultVisible = false">确定</el-button>  
            </template>  
          </el-result>  
        </div>  
        <div v-else>  
          <el-result icon="error" title="导入失败" :sub-title="importResult.message">  
            <template slot="extra">  
              <el-button type="primary" @click="importResultVisible = false">确定</el-button>  
            </template>  
          </el-result>  
          <div v-if="importResult.errorDetails && importResult.errorDetails.length > 0">  
            <el-divider content-position="left">错误详情</el-divider>  
            <el-table :data="importResult.errorDetails" border style="width: 100%">  
              <el-table-column prop="row" label="行号" width="80" align="center" />  
              <el-table-column prop="field" label="字段" width="120" />  
              <el-table-column prop="message" label="错误信息" />  
            </el-table>  
          </div>  
        </div>  
      </el-dialog>  
    </el-card>  
  </div>  
</template>  
  
<script>  
import { listFormMetadata, getFormMetadata } from "@/api/metadata/form";  
import { listFormFieldMetadata } from "@/api/metadata/field";  
import { exportFormData, importFormDataTemplate } from "@/api/formdata/index";  
import { getToken } from "@/utils/auth";  
  
export default {  
  name: "FormDataImportExport",  
  data() {  
    return {  
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
      // 上传URL  
      uploadUrl: process.env.VUE_APP_BASE_API + "/formdata/import",  
      // 上传请求头  
      uploadHeaders: {  
        Authorization: "Bearer " + getToken()  
      },  
      // 上传附加数据  
      uploadData: {  
        metadataId: ""  
      },  
      // 是否正在上传  
      uploading: false,  
      // 导出表单  
      exportForm: {  
        exportType: "all",  
        fileType: "xlsx",  
        queryParams: {}  
      },  
      // 是否正在导出  
      exporting: false,  
      // 日期范围映射  
      dateRangeMap: {},  
      // 字典数据缓存  
      dictCache: {},  
      // 导入结果是否可见  
      importResultVisible: false,  
      // 导入结果  
      importResult: {  
        success: false,  
        successCount: 0,  
        message: "",  
        errorDetails: []  
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
      this.uploadData.metadataId = metadataId;  
      this.resetExportForm();  
        
      // 加载元数据信息  
      getFormMetadata(metadataId).then(response => {  
        this.metadataInfo = response.data;  
          
        // 加载字段列表  
        this.loadFields();  
      });  
    },  
    /** 加载字段列表 */  
    loadFields() {  
      // 加载字段列表  
      listFormFieldMetadata({ metadataId: this.selectedMetadata }).then(response => {  
        this.fieldList = response.rows;  
          
        // 设置查询字段  
        this.queryFields = this.fieldList.filter(field => field.isQuery === 1);  
      });  
    },  
    /** 下载导入模板 */  
    downloadTemplate() {  
      if (!this.selectedMetadata) {  
        this.$message.warning("请先选择表单");  
        return;  
      }  
        
      importFormDataTemplate(this.selectedMetadata).then(response => {  
        this.$download.excel(response, this.metadataInfo.metadataName + '导入模板');  
      });  
    },  
    /** 上传前检查 */  
    beforeUpload(file) {  
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||   
                      file.type === 'application/vnd.ms-excel';  
      if (!isExcel) {  
        this.$message.error('只能上传Excel文件!');  
        return false;  
      }  
        
      const isLt10M = file.size / 1024 / 1024 < 10;  
      if (!isLt10M) {  
        this.$message.error('文件大小不能超过10MB!');  
        return false;  
      }  
        
      return true;  
    },  
    /** 提交上传 */  
    submitUpload() {  
      if (!this.selectedMetadata) {  
        this.$message.warning("请先选择表单");  
        return;  
      }  
        
      this.$refs.upload.submit();  
    },  
    /** 文件上传中处理 */  
    handleFileUploadProgress(event, file, fileList) {  
      this.uploading = true;  
    },  
    /** 文件上传成功处理 */  
    handleFileSuccess(response, file, fileList) {  
      this.uploading = false;  
      this.$refs.upload.clearFiles();  
        
      if (response.code === 200) {  
        this.importResult = {  
          success: true,  
          successCount: response.data.successCount || 0,  
          message: response.msg || "导入成功",  
          errorDetails: []  
        };  
      } else {  
        this.importResult = {  
          success: false,  
          successCount: 0,  
          message: response.msg || "导入失败",  
          errorDetails: response.data && response.data.errorDetails ? response.data.errorDetails : []  
        };  
      }  
        
      this.importResultVisible = true;  
    },  
    /** 文件上传错误处理 */  
    handleFileError(err, file, fileList) {  
      this.uploading = false;  
      this.$message.error("上传失败：" + err.message);  
    },  
    /** 日期范围变更操作 */  
    handleDateRangeChange(fieldName) {  
      if (this.dateRangeMap[fieldName]) {  
        this.exportForm.queryParams[fieldName + 'Begin'] = this.dateRangeMap[fieldName][0];  
        this.exportForm.queryParams[fieldName + 'End'] = this.dateRangeMap[fieldName][1];  
      } else {  
        this.exportForm.queryParams[fieldName + 'Begin'] = null;  
        this.exportForm.queryParams[fieldName + 'End'] = null;  
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
    /** 导出按钮操作 */  
    handleExport() {  
      if (!this.selectedMetadata) {  
        this.$message.warning("请先选择表单");  
        return;  
      }  
        
      this.exporting = true;  
        
      const params = {  
        metadataId: this.selectedMetadata,  
        fileType: this.exportForm.fileType  
      };  
        
      // 如果是筛选导出，添加查询参数  
      if (this.exportForm.exportType === "filter") {  
        params.queryParams = this.exportForm.queryParams;  
      }  
        
      exportFormData(params).then(response => {  
        this.$download.excel(response, this.metadataInfo.metadataName + '数据');  
        this.exporting = false;  
      }).catch(() => {  
        this.exporting = false;  
      });  
    },  
    /** 重置导出表单 */  
    resetExportForm() {  
      this.exportForm = {  
        exportType: "all",  
        fileType: "xlsx",  
        queryParams: {}  
      };  
      this.dateRangeMap = {};  
    },  
    /** 返回列表页 */  
    goBack() {  
      this.$router.push({ path: "/formdata" });  
    }  
  }  
};  
</script>  
  
<style scoped>  
.import-guide {  
  margin-bottom: 20px;  
}  
.import-guide p {  
  margin: 5px 0;  
}  
.import-guide ul {  
  padding-left: 20px;  
}  
.import-actions, .upload-actions {  
  margin-top: 20px;  
  text-align: center;  
}  
.el-range-separator {  
  padding: 0 5px;  
}  
.el-range-editor {  
  display: flex;  
  align-items: center;  
}  
</style>