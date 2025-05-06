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
      <el-form-item label="字段名称" prop="fieldName">  
        <el-input  
          v-model="queryParams.fieldName"  
          placeholder="请输入字段名称"  
          clearable  
          size="small"  
          @keyup.enter.native="handleQuery"  
        />  
      </el-form-item>  
      <el-form-item label="字段标签" prop="fieldLabel">  
        <el-input  
          v-model="queryParams.fieldLabel"  
          placeholder="请输入字段标签"  
          clearable  
          size="small"  
          @keyup.enter.native="handleQuery"  
        />  
      </el-form-item>  
      <el-form-item label="字段类型" prop="fieldType">  
        <el-select v-model="queryParams.fieldType" placeholder="请选择字段类型" clearable size="small">  
          <el-option v-for="dict in fieldTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
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
          v-hasPermi="['metadata:field:add']"  
        >新增</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="success"  
          icon="el-icon-edit"  
          size="mini"  
          :disabled="single"  
          @click="handleUpdate"  
          v-hasPermi="['metadata:field:edit']"  
        >修改</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="danger"  
          icon="el-icon-delete"  
          size="mini"  
          :disabled="multiple"  
          @click="handleDelete"  
          v-hasPermi="['metadata:field:remove']"  
        >删除</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="warning"  
          icon="el-icon-download"  
          size="mini"  
          @click="handleExport"  
          v-hasPermi="['metadata:field:export']"  
        >导出</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="info"  
          icon="el-icon-sort"  
          size="mini"  
          @click="handleSort"  
          v-hasPermi="['metadata:field:edit']"  
        >排序</el-button>  
      </el-col>  
    </el-row>  
  
    <el-table v-loading="loading" :data="fieldList" @selection-change="handleSelectionChange">  
      <el-table-column type="selection" width="55" align="center" />  
      <el-table-column label="字段ID" align="center" prop="fieldId" :show-overflow-tooltip="true" />  
      <el-table-column label="字段名称" align="center" prop="fieldName" :show-overflow-tooltip="true" />  
      <el-table-column label="字段标签" align="center" prop="fieldLabel" :show-overflow-tooltip="true" />  
      <el-table-column label="字段类型" align="center" prop="fieldType">  
        <template slot-scope="scope">  
          <dict-tag :options="fieldTypeOptions" :value="scope.row.fieldType"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="数据库列名" align="center" prop="columnName" :show-overflow-tooltip="true" />  
      <el-table-column label="数据库列类型" align="center" prop="columnType" :show-overflow-tooltip="true" />  
      <el-table-column label="是否必填" align="center" prop="isRequired">  
        <template slot-scope="scope">  
          <dict-tag :options="yesNoOptions" :value="scope.row.isRequired"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="是否主键" align="center" prop="isPk">  
        <template slot-scope="scope">  
          <dict-tag :options="yesNoOptions" :value="scope.row.isPk"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="是否列表显示" align="center" prop="isList">  
        <template slot-scope="scope">  
          <dict-tag :options="yesNoOptions" :value="scope.row.isList"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="是否查询条件" align="center" prop="isQuery">  
        <template slot-scope="scope">  
          <dict-tag :options="yesNoOptions" :value="scope.row.isQuery"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="显示类型" align="center" prop="htmlType">  
        <template slot-scope="scope">  
          <dict-tag :options="htmlTypeOptions" :value="scope.row.htmlType"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />  
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">  
        <template slot-scope="scope">  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-edit"  
            @click="handleUpdate(scope.row)"  
            v-hasPermi="['metadata:field:edit']"  
          >修改</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-delete"  
            @click="handleDelete(scope.row)"  
            v-hasPermi="['metadata:field:remove']"  
          >删除</el-button>  
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
  
    <!-- 添加或修改字段元数据对话框 -->  
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>  
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">  
        <el-form-item label="所属元数据" prop="metadataId">  
          <el-select v-model="form.metadataId" placeholder="请选择所属元数据" disabled>  
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
            <el-form-item label="字段名称" prop="fieldName">  
              <el-input v-model="form.fieldName" placeholder="请输入字段名称" />  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="字段标签" prop="fieldLabel">  
              <el-input v-model="form.fieldLabel" placeholder="请输入字段标签" />  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="字段类型" prop="fieldType">  
              <el-select v-model="form.fieldType" placeholder="请选择字段类型" @change="handleFieldTypeChange">  
                <el-option v-for="dict in fieldTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
              </el-select>  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="数据库列名" prop="columnName">  
              <el-input v-model="form.columnName" placeholder="请输入数据库列名" />  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="数据库列类型" prop="columnType">  
              <el-select v-model="form.columnType" placeholder="请选择数据库列类型">  
                <el-option v-for="dict in columnTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
              </el-select>  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="显示类型" prop="htmlType">  
              <el-select v-model="form.htmlType" placeholder="请选择显示类型">  
                <el-option v-for="dict in htmlTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
              </el-select>  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="是否必填" prop="isRequired">  
              <el-radio-group v-model="form.isRequired">  
                <el-radio :label="1">是</el-radio>  
                <el-radio :label="0">否</el-radio>  
              </el-radio-group>  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="是否主键" prop="isPk">  
              <el-radio-group v-model="form.isPk">  
                <el-radio :label="1">是</el-radio>  
                <el-radio :label="0">否</el-radio>  
              </el-radio-group>  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="是否列表显示" prop="isList">  
              <el-radio-group v-model="form.isList">  
                <el-radio :label="1">是</el-radio>  
                <el-radio :label="0">否</el-radio>  
              </el-radio-group>  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="是否查询条件" prop="isQuery">  
              <el-radio-group v-model="form.isQuery">  
                <el-radio :label="1">是</el-radio>  
                <el-radio :label="0">否</el-radio>  
              </el-radio-group>  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row v-if="form.isQuery === 1">  
          <el-col :span="12">  
            <el-form-item label="查询方式" prop="queryType">  
              <el-select v-model="form.queryType" placeholder="请选择查询方式">  
                <el-option label="等于" value="EQ" />  
                <el-option label="不等于" value="NE" />  
                <el-option label="大于" value="GT" />  
                <el-option label="小于" value="LT" />  
                <el-option label="模糊" value="LIKE" />  
                <el-option label="范围" value="BETWEEN" />  
              </el-select>  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row v-if="form.htmlType === 'select' || form.htmlType === 'radio' || form.htmlType === 'checkbox'">  
          <el-col :span="24">  
            <el-form-item label="字典类型" prop="dictType">  
              <el-select v-model="form.dictType" placeholder="请选择字典类型">  
                <el-option v-for="dict in dictTypeOptions" :key="dict.dictType" :label="dict.dictName" :value="dict.dictType" />  
              </el-select>  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="排序" prop="sortOrder">  
              <el-input-number v-model="form.sortOrder" :min="0" :max="999" controls-position="right" />  
            </el-form-item>  
          </el-col>  
        </el-row>  
      </el-form>  
      <div slot="footer" class="dialog-footer">  
        <el-button type="primary" @click="submitForm">确 定</el-button>  
        <el-button @click="cancel">取 消</el-button>  
      </div>  
    </el-dialog>  
  
    <!-- 字段排序对话框 -->  
    <el-dialog title="字段排序" :visible.sync="sortOpen" width="800px" append-to-body>  
      <el-table  
        ref="dragTable"  
        :data="sortList"  
        row-key="fieldId"  
        :max-height="500"  
      >  
        <el-table-column label="序号" type="index" width="50" class-name="allowDrag" />  
        <el-table-column label="字段名称" prop="fiel<el-table-column label="字段名称" prop="fieldName" :show-overflow-tooltip="true" />  
        <el-table-column label="字段标签" prop="fieldLabel" :show-overflow-tooltip="true" />  
        <el-table-column label="字段类型" prop="fieldType" :show-overflow-tooltip="true" />  
        <el-table-column label="排序" prop="sortOrder" width="80" />  
        <el-table-column label="操作" width="160" align="center">  
          <template slot-scope="scope">  
            <el-button  
              size="mini"  
              type="text"  
              icon="el-icon-top"  
              @click="moveUp(scope.$index)"  
              v-if="scope.$index > 0"  
            >上移</el-button>  
            <el-button  
              size="mini"  
              type="text"  
              icon="el-icon-bottom"  
              @click="moveDown(scope.$index)"  
              v-if="scope.$index < sortList.length - 1"  
            >下移</el-button>  
          </template>  
        </el-table-column>  
      </el-table>  
      <div slot="footer" class="dialog-footer">  
        <el-button type="primary" @click="submitSortForm">确 定</el-button>  
        <el-button @click="cancelSort">取 消</el-button>  
      </div>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { listFormFieldMetadata, getFormFieldMetadata, delFormFieldMetadata, addFormFieldMetadata, updateFormFieldMetadata, exportFormFieldMetadata, updateFieldSortOrder } from "@/api/metadata/field";  
import { listFormMetadata } from "@/api/metadata/form";  
import { listDictType } from "@/api/system/dict/type";  
  
export default {  
  name: "FormFieldMetadata",  
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
      // 字段元数据表格数据  
      fieldList: [],  
      // 排序表格数据  
      sortList: [],  
      // 弹出层标题  
      title: "",  
      // 是否显示弹出层  
      open: false,  
      // 是否显示排序弹出层  
      sortOpen: false,  
      // 查询参数  
      queryParams: {  
        pageNum: 1,  
        pageSize: 10,  
        metadataId: null,  
        fieldName: null,  
        fieldLabel: null,  
        fieldType: null  
      },  
      // 表单参数  
      form: {},  
      // 表单校验  
      rules: {  
        metadataId: [  
          { required: true, message: "所属元数据不能为空", trigger: "blur" }  
        ],  
        fieldName: [  
          { required: true, message: "字段名称不能为空", trigger: "blur" },  
          { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: "字段名称只能包含字母、数字和下划线，且必须以字母开头", trigger: "blur" }  
        ],  
        fieldLabel: [  
          { required: true, message: "字段标签不能为空", trigger: "blur" }  
        ],  
        fieldType: [  
          { required: true, message: "字段类型不能为空", trigger: "change" }  
        ],  
        columnName: [  
          { required: true, message: "数据库列名不能为空", trigger: "blur" },  
          { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: "数据库列名只能包含字母、数字和下划线，且必须以字母开头", trigger: "blur" }  
        ],  
        columnType: [  
          { required: true, message: "数据库列类型不能为空", trigger: "change" }  
        ],  
        htmlType: [  
          { required: true, message: "显示类型不能为空", trigger: "change" }  
        ]  
      },  
      // 元数据选项  
      metadataOptions: [],  
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
      // 数据库列类型选项  
      columnTypeOptions: [  
        { value: "varchar(50)", label: "varchar(50)" },  
        { value: "varchar(100)", label: "varchar(100)" },  
        { value: "varchar(255)", label: "varchar(255)" },  
        { value: "text", label: "text" },  
        { value: "int", label: "int" },  
        { value: "bigint", label: "bigint" },  
        { value: "decimal(10,2)", label: "decimal(10,2)" },  
        { value: "tinyint", label: "tinyint" },  
        { value: "date", label: "date" },  
        { value: "datetime", label: "datetime" }  
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
      ],  
      // 是否选项  
      yesNoOptions: [  
        { value: 0, label: "否" },  
        { value: 1, label: "是" }  
      ],  
      // 字典类型选项  
      dictTypeOptions: []  
    };  
  },  
  created() {  
    this.getMetadataOptions();  
    this.getDictTypeOptions();  
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
    /** 查询字典类型选项 */  
    getDictTypeOptions() {  
      listDictType().then(response => {  
        this.dictTypeOptions = response.rows;  
      });  
    },  
    /** 查询字段元数据列表 */  
    getList() {  
      this.loading = true;  
      listFormFieldMetadata(this.queryParams).then(response => {  
        this.fieldList = response.rows;  
        this.total = response.total;  
        this.loading = false;  
      });  
    },  
    // 取消按钮  
    cancel() {  
      this.open = false;  
      this.reset();  
    },  
    // 取消排序按钮  
    cancelSort() {  
      this.sortOpen = false;  
    },  
    // 表单重置  
    reset() {  
      this.form = {  
        fieldId: null,  
        metadataId: this.queryParams.metadataId,  
        fieldName: null,  
        fieldLabel: null,  
        fieldType: null,  
        columnName: null,  
        columnType: null,  
        isRequired: 0,  
        isPk: 0,  
        isList: 1,  
        isQuery: 0,  
        queryType: "EQ",  
        htmlType: "input",  
        dictType: null,  
        sortOrder: 0,  
        tenantId: null  
      };  
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
    /** 字段类型变更操作 */  
    handleFieldTypeChange(fieldType) {  
      // 根据字段类型自动设置数据库列类型和HTML类型  
      if (fieldType === "string") {  
        this.form.columnType = "varchar(100)";  
        this.form.htmlType = "input";  
      } else if (fieldType === "integer") {  
        this.form.columnType = "int";  
        this.form.htmlType = "input";  
      } else if (fieldType === "decimal") {  
        this.form.columnType = "decimal(10,2)";  
        this.form.htmlType = "input";  
      } else if (fieldType === "boolean") {  
        this.form.columnType = "tinyint";  
        this.form.htmlType = "radio";  
      } else if (fieldType === "date") {  
        this.form.columnType = "date";  
        this.form.htmlType = "datetime";  
      } else if (fieldType === "datetime") {  
        this.form.columnType = "datetime";  
        this.form.htmlType = "datetime";  
      } else if (fieldType === "text") {  
        this.form.columnType = "text";  
        this.form.htmlType = "textarea";  
      } else if (fieldType === "enum") {  
        this.form.columnType = "varchar(50)";  
        this.form.htmlType = "select";  
      } else if (fieldType === "file" || fieldType === "image") {  
        this.form.columnType = "varchar(255)";  
        this.form.htmlType = "upload";  
      }  
    },  
    // 多选框选中数据  
    handleSelectionChange(selection) {  
      this.ids = selection.map(item => item.fieldId);  
      this.single = selection.length !== 1;  
      this.multiple = !selection.length;  
    },  
    /** 新增按钮操作 */  
    handleAdd() {  
      this.reset();  
      this.open = true;  
      this.title = "添加字段元数据";  
    },  
    /** 修改按钮操作 */  
    handleUpdate(row) {  
      this.reset();  
      const fieldId = row.fieldId || this.ids;  
      getFormFieldMetadata(fieldId).then(response => {  
        this.form = response.data;  
        this.open = true;  
        this.title = "修改字段元数据";  
      });  
    },  
    /** 排序按钮操作 */  
    handleSort() {  
      if (!this.queryParams.metadataId) {  
        this.$message.warning("请先选择元数据");  
        return;  
      }  
      this.sortList = JSON.parse(JSON.stringify(this.fieldList));  
      this.sortOpen = true;  
    },  
    /** 上移操作 */  
    moveUp(index) {  
      if (index > 0) {  
        const temp = this.sortList[index];  
        this.sortList[index] = this.sortList[index - 1];  
        this.sortList[index - 1] = temp;  
        // 交换排序值  
        const tempOrder = this.sortList[index].sortOrder;  
        this.sortList[index].sortOrder = this.sortList[index - 1].sortOrder;  
        this.sortList[index - 1].sortOrder = tempOrder;  
      }  
    },  
    /** 下移操作 */  
    moveDown(index) {  
      if (index < this.sortList.length - 1) {  
        const temp = this.sortList[index];  
        this.sortList[index] = this.sortList[index + 1];  
        this.sortList[index + 1] = temp;  
        // 交换排序值  
        const tempOrder = this.sortList[index].sortOrder;  
        this.sortList[index].sortOrder = this.sortList[index + 1].sortOrder;  
        this.sortList[index + 1].sortOrder = tempOrder;  
      }  
    },  
    /** 提交排序 */  
    submitSortForm() {  
      const data = {  
        metadataId: this.queryParams.metadataId,  
        fields: this.sortList.map((item, index) => {  
          return {  
            fieldId: item.fieldId,  
            sortOrder: index * 10  
          };  
        })  
      };  
      updateFieldSortOrder(data).then(response => {  
        this.$modal.msgSuccess("排序成功");  
        this.sortOpen = false;  
        this.getList();  
      });  
    },  
    /** 提交按钮 */  
    submitForm() {  
      this.$refs["form"].validate(valid => {  
        if (valid) {  
          if (this.form.fieldId != null) {  
            updateFormFieldMetadata(this.form).then(response => {  
              this.$modal.msgSuccess("修改成功");  
              this.open = false;  
              this.getList();  
            });  
          } else {  
            addFormFieldMetadata(this.form).then(response => {  
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
      const fieldIds = row.fieldId || this.ids;  
      this.$modal.confirm('是否确认删除字段元数据编号为"' + fieldIds + '"的数据项?').then(() => {  
        return delFormFieldMetadata(fieldIds);  
      }).then(() => {  
        this.getList();  
        this.$modal.msgSuccess("删除成功");  
      }).catch(() => {});  
    },  
    /** 导出按钮操作 */  
    handleExport() {  
      this.$modal.confirm('是否确认导出所有字段元数据数据项?').then(() => {  
        this.exportLoading = true;  
        return exportFormFieldMetadata(this.queryParams);  
      }).then(response => {  
        this.$download.excel(response, '字段元数据数据');  
        this.exportLoading = false;  
      }).catch(() => {});  
    }  
  }  
};  
</script>