<template>  
  <div class="app-container">  
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">  
      <el-form-item label="表名称" prop="tableName">  
        <el-input  
          v-model="queryParams.tableName"  
          placeholder="请输入表名称"  
          clearable  
          size="small"  
          @keyup.enter.native="handleQuery"  
        />  
      </el-form-item>  
      <el-form-item label="表描述" prop="tableComment">  
        <el-input  
          v-model="queryParams.tableComment"  
          placeholder="请输入表描述"  
          clearable  
          size="small"  
          @keyup.enter.native="handleQuery"  
        />  
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
          @click="handleImport"  
          :disabled="multiple"  
          v-hasPermi="['metadata:import:import']"  
        >导入</el-button>  
      </el-col>  
    </el-row>  
  
    <el-table v-loading="loading" :data="dbTableList" @selection-change="handleSelectionChange">  
      <el-table-column type="selection" width="55" align="center" />  
      <el-table-column label="表名称" align="center" prop="tableName" :show-overflow-tooltip="true" />  
      <el-table-column label="表描述" align="center" prop="tableComment" :show-overflow-tooltip="true" />  
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">  
        <template slot-scope="scope">  
          <span>{{ parseTime(scope.row.createTime) }}</span>  
        </template>  
      </el-table-column>  
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">  
        <template slot-scope="scope">  
          <span>{{ parseTime(scope.row.updateTime) }}</span>  
        </template>  
      </el-table-column>  
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">  
        <template slot-scope="scope">  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-view"  
            @click="handlePreview(scope.row)"  
            v-hasPermi="['metadata:import:preview']"  
          >预览</el-button>  
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
  
    <!-- 导入配置对话框 -->  
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>  
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">  
        <el-form-item label="元数据名称" prop="metadataName">  
          <el-input v-model="form.metadataName" placeholder="请输入元数据名称" />  
        </el-form-item>  
        <el-form-item label="元数据描述" prop="metadataDesc">  
          <el-input v-model="form.metadataDesc" placeholder="请输入元数据描述" />  
        </el-form-item>  
        <el-form-item label="关联表名" prop="tableName">  
          <el-input v-model="form.tableName" placeholder="请输入关联表名" disabled />  
        </el-form-item>  
        <el-divider content-position="center">字段映射配置</el-divider>  
        <el-table :data="columnList" border>  
          <el-table-column type="index" width="50" label="序号" />  
          <el-table-column prop="columnName" label="列名" width="150" />  
          <el-table-column prop="columnComment" label="列描述" width="150" />  
          <el-table-column prop="columnType" label="列类型" width="100" />  
          <el-table-column label="字段名称" width="150">  
            <template slot-scope="scope">  
              <el-input v-model="scope.row.fieldName" placeholder="请输入字段名称" size="small" />  
            </template>  
          </el-table-column>  
          <el-table-column label="字段标签" width="150">  
            <template slot-scope="scope">  
              <el-input v-model="scope.row.fieldLabel" placeholder="请输入字段标签" size="small" />  
            </template>  
          </el-table-column>  
          <el-table-column label="字段类型" width="120">  
            <template slot-scope="scope">  
              <el-select v-model="scope.row.fieldType" placeholder="请选择" size="small">  
                <el-option v-for="dict in fieldTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
              </el-select>  
            </template>  
          </el-table-column>  
          <el-table-column label="是否必填" width="80">  
            <template slot-scope="scope">  
              <el-checkbox v-model="scope.row.isRequired">必填</el-checkbox>  
            </template>  
          </el-table-column>  
          <el-table-column label="是否主键" width="80">  
            <template slot-scope="scope">  
              <el-checkbox v-model="scope.row.isPk">主键</el-checkbox>  
            </template>  
          </el-table-column>  
          <el-table-column label="是否列表" width="80">  
            <template slot-scope="scope">  
              <el-checkbox v-model="scope.row.isList">列表</el-checkbox>  
            </template>  
          </el-table-column>  
          <el-table-column label="是否查询" width="80">  
            <template slot-scope="scope">  
              <el-checkbox v-model="scope.row.isQuery">查询</el-checkbox>  
            </template>  
          </el-table-column>  
          <el-table-column label="显示类型" width="120">  
            <template slot-scope="scope">  
              <el-select v-model="scope.row.htmlType" placeholder="请选择" size="small">  
                <el-option v-for="dict in htmlTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
              </el-select>  
            </template>  
          </el-table-column>  
        </el-table>  
      </el-form>  
      <div slot="footer" class="dialog-footer">  
        <el-button type="primary" @click="submitForm">确 定</el-button>  
        <el-button @click="cancel">取 消</el-button>  
      </div>  
    </el-dialog>  
  
    <!-- 预览对话框 -->  
    <el-dialog title="元数据预览" :visible.sync="previewOpen" width="90%" append-to-body>  
      <el-tabs v-model="previewActiveTab">  
        <el-tab-pane label="元数据信息" name="metadata">  
          <el-form label-width="120px">  
            <el-row>  
              <el-col :span="12">  
                <el-form-item label="表名称">  
                  <el-input v-model="preview.tableName" readonly />  
                </el-form-item>  
              </el-col>  
              <el-col :span="12">  
                <el-form-item label="表描述">  
                  <el-input v-model="preview.tableComment" readonly />  
                </el-form-item>  
              </el-col>  
            </el-row>  
          </el-form>  
        </el-tab-pane>  
        <el-tab-pane label="字段信息" name="columns">  
          <el-table :data="preview.columns" border>  
            <el-table-column type="index" width="50" label="序号" />  
            <el-table-column prop="columnName" label="列名" width="150" />  
            <el-table-column prop="columnComment" label="列描述" width="150" />  
            <el-table-column prop="columnType" label="列类型" width="100" />  
            <el-table-column prop="javaType" label="Java类型" width="120" />  
            <el-table-column prop="javaField" label="Java属性" width="120" />  
            <el-table-column prop="isPk" label="主键" width="60">  
              <template slot-scope="scope">  
                <el-tag v-if="scope.row.isPk === '1'" type="success">是</el-tag>  
                <el-tag v-else type="info">否</el-tag>  
              </template>  
            </el-table-column>  
            <el-table-column prop="isRequired" label="必填" width="60">  
              <template slot-scope="scope">  
                <el-tag v-if="scope.row.isRequired === '1'" type="success">是</el-tag>  
                <el-tag v-else type="info">否</el-tag>  
              </template>  
            </el-table-column>  
            <el-table-column prop="isList" label="列表" width="60">  
              <template slot-scope="scope">  
                <el-tag v-if="scope.row.isList === '1'" type="success">是</el-tag>  
                <el-tag v-else type="info">否</el-tag>  
              </template>  
            </el-table-column>  
            <el-table-column prop="isQuery" label="查询" width="60">  
              <template slot-scope="scope">  
                <el-tag v-if="scope.row.isQuery === '1'" type="success">是</el-tag>  
                <el-tag v-else type="info">否</el-tag>  
              </template>  
            </el-table-column>  
          </el-table>  
        </el-tab-pane>  
      </el-tabs>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { listDbTable, previewTable } from "@/api/tool/gen";  
import { importMetadata } from "@/api/metadata/import";  
  
export default {  
  name: "ImportMetadata",  
  data() {  
    return {  
      // 遮罩层  
      loading: true,  
      // 选中数组  
      ids: [],  
      // 选中表数组  
      tableNames: [],  
      // 非单个禁用  
      single: true,  
      // 非多个禁用  
      multiple: true,  
      // 显示搜索条件  
      showSearch: true,  
      // 总条数  
      total: 0,  
      // 表数据  
      dbTableList: [],  
      // 弹出层标题  
      title: "",  
      // 是否显示弹出层  
      open: false,  
      // 是否显示预览弹出层  
      previewOpen: false,  
      // 预览激活的标签页  
      previewActiveTab: "metadata",  
      // 预览数据  
      preview: {},  
      // 查询参数  
      queryParams: {  
        pageNum: 1,  
        pageSize: 10,  
        tableName: undefined,  
        tableComment: undefined  
      },  
      // 表单参数  
      form: {},  
      // 表单校验  
      rules: {  
        metadataName: [  
          { required: true, message: "元数据名称不能为空", trigger: "blur" },  
          { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: "元数据名称只能包含字母、数字和下划线，且必须以字母开头", trigger: "blur" }  
        ],  
        metadataDesc: [  
          { required: true, message: "元数据描述不能为空", trigger: "blur" }  
        ]  
      },  
      // 列信息  
      columnList: [],  
      // 字段类型选项  
      fieldTypeOptions: [  
        { value: "string", label: "字符串" },  
        { value: "integer", label: "整数" },  
        { value: "decimal", label: "小数" },  
        { value: "boolean", label: "布尔值" },  
        { value: "date", label: "日期" },  
        { value: "datetime", label: "日期时间" },  
        { value: "text", label: "长文本" },  
        { value: "enum", label: "枚举" },  
        { value: "file", label: "文件" },  
        { value: "image", label: "图片" }  
      ],  
      // HTML类型选项  
      htmlTypeOptions: [  
        { value: "input", label: "文本框" },  
        { value: "textarea", label: "文本域" },  
        { value: "select", label: "下拉框" },  
        { value: "radio", label: "单选框" },  
        { value: "checkbox", label: "复选框" },  
        { value: "datetime", label: "日期控件" },  
        { value: "upload", label: "上传控件" },  
        { value: "editor", label: "富文本编辑器" }  
      ]  
    };  
  },  
  created() {  
    this.getList();  
  },  
  methods: {  
    /** 查询表数据 */  
    getList() {  
      this.loading = true;  
      listDbTable(this.queryParams).then(res => {  
        if (res.code === 200) {  
          this.dbTableList = res.rows;  
          this.total = res.total;  
          this.loading = false;  
        }  
      });  
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
    /** 多选框选中数据 */  
    handleSelectionChange(selection) {  
      this.ids = selection.map(item => item.tableId);  
      this.tableNames = selection.map(item => item.tableName);  
      this.single = selection.length !== 1;  
      this.multiple = !selection.length;  
    },  
    /** 预览按钮操作 */  
    handlePreview(row) {  
      previewTable(row.tableName).then(response => {  
        this.preview = response.data;  
        this.previewOpen = true;  
        this.previewActiveTab = "metadata";  
      });  
    },  
    /** 导入按钮操作 */  
    handleImport() {  
      if (this.tableNames.length <= 0) {  
        this.$modal.msgError("请选择要导入的表");  
        return;  
      }  
      const tableName = this.tableNames[0];  
      previewTable(tableName).then(response => {  
        const { columns } = response.data;  
        this.columnList = columns.map(column => {  
          // 根据数据库列类型推断字段类型和HTML类型  
          const fieldType = this.getFieldTypeByColumnType(column.columnType);  
          const htmlType = this.getHtmlTypeByFieldType(fieldType);  
            
          return {  
            ...column,  
            fieldName: this.formatToCamelCase(column.columnName),  
            fieldLabel: column.columnComment || column.columnName,  
            fieldType: fieldType,  
            htmlType: htmlType,  
            isRequired: column.isRequired === "1",  
            isPk: column.isPk === "1",  
            isList: column.isList === "1",  
            isQuery: column.isQuery === "1"  
          };  
        });  
          
        this.form = {  
          metadataName: this.formatToCamelCase(tableName),  
          metadataDesc: response.data.tableComment || tableName,  
          tableName: tableName  
        };  
          
        this.open = true;  
        this.title = "导入表 " + tableName;  
      });  
    },  
    /** 取消按钮 */  
    cancel() {  
      this.open = false;  
      this.reset();  
    },  
    /** 表单重置 */  
    reset() {  
      this.form = {  
        metadataName: "",  
        metadataDesc: "",  
        tableName: ""  
      };  
      this.columnList = [];  
      this.resetForm("form");  
    },  
    /** 提交按钮 */  
    submitForm() {  
      this.$refs["form"].validate(valid => {  
        if (valid) {  
          // 构建导入参数  
          const importData = {  
            metadataName: this.form.metadataName,  
            metadataDesc: this.form.metadataDesc,  
            tableName: this.form.tableName,  
            fields: this.columnList.map(column => {  
              return {  
                fieldName: column.fieldName,  
                fieldLabel: column.fieldLabel,  
                fieldType: column.fieldType,  
                columnName: column.columnName,  
                columnType: column.dataType,  
                isRequired: column.isRequired ? 1 : 0,  
                isPk: column.isPk ? 1 : 0,  
                isList: column.isList ? 1 : 0,  
                isQuery: column.isQuery ? 1 : 0,  
                queryType: column.isQuery ? "EQ" : null,  
                htmlType: column.htmlType,  
                dictType: null,  
                sortOrder: column.sort || 0  
              };  
            })  
          };  
            
          importMetadata(importData).then(response => {  
            this.$modal.msgSuccess("导入成功");  
            this.open = false;  
            this.$router.push({ path: "/metadata/form" });  
          });  
        }  
      });  
    },  
    /** 根据数据库列类型推断字段类型 */  
    getFieldTypeByColumnType(columnType) {  
      if (/varchar|char|text/i.test(columnType)) {  
        if (/text/i.test(columnType)) {  
          return "text";  
        }  
        return "string";  
      } else if (/int|tinyint|smallint|mediumint|bigint/i.test(columnType)) {  
        if (/tinyint\(1\)/i.test(columnType)) {  
          return "boolean";  
        }  
        return "integer";  
      } else if (/decimal|double|float/i.test(columnType)) {  
        return "decimal";  
      } else if (/date/i.test(columnType)) {  
        if (/datetime|timestamp/i.test(columnType)) {  
          return "datetime";  
        }  
        return "date";  
      } else {  
        return "string";  
      }  
    },  
    /** 根据字段类型推断HTML类型 */  
    getHtmlTypeByFieldType(fieldType) {  
      switch (fieldType) {  
        case "string":  
          return "input";  
        case "integer":  
        case "decimal":  
          return "input";  
        case "boolean":  
          return "radio";  
        case "date":  
        case "datetime":  
          return "datetime";  
        case "text":  
          return "textarea";  
        case "enum":  
          return "select";  
        case "file":  
        case "image":  
          return "upload";  
        default:  
          return "input";  
      }  
    },  
    /** 格式化为驼峰命名 */  
    formatToCamelCase(str) {  
      // 将下划线分隔的字符串转换为驼峰命名  
      return str.replace(/_([a-z])/g, function(match, p1) {  
        return p1.toUpperCase();  
      });  
    }  
  }  
};  
</script>  
  
<style scoped>  
.el-table {  
  margin-top: 15px;  
}  
</style>