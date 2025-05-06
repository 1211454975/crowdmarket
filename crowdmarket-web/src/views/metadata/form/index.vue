<template>  
  <div class="app-container">  
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">  
      <el-form-item label="元数据名称" prop="metadataName">  
        <el-input  
          v-model="queryParams.metadataName"  
          placeholder="请输入元数据名称"  
          clearable  
          size="small"  
          @keyup.enter.native="handleQuery"  
        />  
      </el-form-item>  
      <el-form-item label="状态" prop="status">  
        <el-select v-model="queryParams.status" placeholder="元数据状态" clearable size="small">  
          <el-option  
            v-for="dict in statusOptions"  
            :key="dict.dictValue"  
            :label="dict.dictLabel"  
            :value="dict.dictValue"  
          />  
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
          plain  
          icon="el-icon-plus"  
          size="mini"  
          @click="handleAdd"  
          v-hasPermi="['metadata:form:add']"  
        >新增</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="success"  
          plain  
          icon="el-icon-edit"  
          size="mini"  
          :disabled="single"  
          @click="handleUpdate"  
          v-hasPermi="['metadata:form:edit']"  
        >修改</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="danger"  
          plain  
          icon="el-icon-delete"  
          size="mini"  
          :disabled="multiple"  
          @click="handleDelete"  
          v-hasPermi="['metadata:form:remove']"  
        >删除</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="info"  
          plain  
          icon="el-icon-upload2"  
          size="mini"  
          @click="handleImport"  
          v-hasPermi="['metadata:form:import']"  
        >导入</el-button>  
      </el-col>  
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>  
    </el-row>  
  
    <el-table v-loading="loading" :data="formMetadataList" @selection-change="handleSelectionChange">  
      <el-table-column type="selection" width="55" align="center" />  
      <el-table-column label="元数据ID" align="center" prop="metadataId" :show-overflow-tooltip="true" />  
      <el-table-column label="元数据名称" align="center" prop="metadataName" :show-overflow-tooltip="true" />  
      <el-table-column label="关联表名" align="center" prop="tableName" :show-overflow-tooltip="true" />  
      <el-table-column label="状态" align="center" prop="status">  
        <template slot-scope="scope">  
          <dict-tag :options="statusOptions" :value="scope.row.status"/>  
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
            v-hasPermi="['metadata:form:edit']"  
          >修改</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-delete"  
            @click="handleDelete(scope.row)"  
            v-hasPermi="['metadata:form:remove']"  
          >删除</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-s-tools"  
            @click="handleFields(scope.row)"  
            v-hasPermi="['metadata:field:list']"  
          >字段</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-view"  
            @click="handleViews(scope.row)"  
            v-hasPermi="['metadata:view:list']"  
          >视图</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-s-operation"  
            @click="handleRules(scope.row)"  
            v-hasPermi="['metadata:rule:list']"  
          >规则</el-button>  
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
  
    <!-- 添加或修改表单元数据对话框 -->  
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>  
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">  
        <el-form-item label="元数据名称" prop="metadataName">  
          <el-input v-model="form.metadataName" placeholder="请输入元数据名称" />  
        </el-form-item>  
        <el-form-item label="元数据描述" prop="metadataDesc">  
          <el-input v-model="form.metadataDesc" type="textarea" placeholder="请输入元数据描述" />  
        </el-form-item>  
        <el-form-item label="关联表名" prop="tableName">  
          <el-input v-model="form.tableName" placeholder="请输入关联表名" />  
        </el-form-item>  
        <el-form-item label="状态" prop="status">  
          <el-radio-group v-model="form.status">  
            <el-radio  
              v-for="dict in statusOptions"  
              :key="dict.dictValue"  
              :label="parseInt(dict.dictValue)"  
            >{{dict.dictLabel}}</el-radio>  
          </el-radio-group>  
        </el-form-item>  
      </el-form>  
      <div slot="footer" class="dialog-footer">  
        <el-button type="primary" @click="submitForm">确 定</el-button>  
        <el-button @click="cancel">取 消</el-button>  
      </div>  
    </el-dialog>  
  
    <!-- 导入对话框 -->  
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>  
      <el-form ref="upload" :model="upload" :rules="upload.rules" label-width="80px">  
        <el-form-item label="数据库表" prop="tableName">  
          <el-select v-model="upload.tableName" placeholder="请选择数据库表">  
            <el-option  
              v-for="table in tableList"  
              :key="table.tableName"  
              :label="table.tableName + '：' + table.tableComment"  
              :value="table.tableName"  
            ></el-option>  
          </el-select>  
        </el-form-item>  
      </el-form>  
      <div slot="footer" class="dialog-footer">  
        <el-button type="primary" @click="submitImport">确 定</el-button>  
        <el-button @click="upload.open = false">取 消</el-button>  
      </div>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { listFormMetadata, getFormMetadata, delFormMetadata, addFormMetadata, updateFormMetadata, importFromTable } from "@/api/metadata/form";  
import { listDbTables } from "@/api/tool/gen";  
  
export default {  
  name: "FormMetadata",  
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
      // 表单元数据表格数据  
      formMetadataList: [],  
      // 弹出层标题  
      title: "",  
      // 是否显示弹出层  
      open: false,  
      // 状态选项  
      statusOptions: [  
        { dictLabel: "草稿", dictValue: "0" },  
        { dictLabel: "发布", dictValue: "1" },  
        { dictLabel: "停用", dictValue: "2" }  
      ],  
      // 查询参数  
      queryParams: {  
        pageNum: 1,  
        pageSize: 10,  
        metadataName: undefined,  
        status: undefined  
      },  
      // 表单参数  
      form: {},  
      // 表单校验  
      rules: {  
        metadataName: [  
          { required: true, message: "元数据名称不能为空", trigger: "blur" }  
        ],  
        tableName: [  
          { required: true, message: "关联表名不能为空", trigger: "blur" }  
        ],  
        status: [  
          { required: true, message: "状态不能为空", trigger: "change" }  
        ]  
      },  
      // 导入参数  
      upload: {  
        open: false,  
        title: "从数据库表导入",  
        tableName: undefined,  
        rules: {  
          tableName: [  
            { required: true, message: "数据库表不能为空", trigger: "change" }  
          ]  
        }  
      },  
      // 数据库表列表  
      tableList: []  
    };  
  },  
  created() {  
    this.getList();  
  },  
  methods: {  
    /** 查询表单元数据列表 */  
    getList() {  
      this.loading = true;  
      listFormMetadata(this.queryParams).then(response => {  
        this.formMetadataList = response.rows;  
        this.total = response.total;  
        this.loading = false;  
      });  
    },  
    // 取消按钮  
    cancel() {  
      this.open = false;  
      this.reset();  
    },  
    // 表单重置  
    reset() {  
      this.form = {  
        metadataId: undefined,  
        metadataName: undefined,  
        metadataDesc: undefined,  
        tableName: undefined,  
        status: 0  
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
    // 多选框选中数据  
    handleSelectionChange(selection) {  
      this.ids = selection.map(item => item.metadataId);  
      this.single = selection.length !== 1;  
      this.multiple = !selection.length;  
    },  
    /** 新增按钮操作 */  
    handleAdd() {  
      this.reset();  
      this.open = true;  
      this.title = "添加表单元数据";  
    },  
    /** 修改按钮操作 */  
    handleUpdate(row) {  
      this.reset();  
      const metadataId = row.metadataId || this.ids[0];  
      getFormMetadata(metadataId).then(response => {  
        this.form = response.data;  
        this.open = true;  
        this.title = "修改表单元数据";  
      });  
    },  
    /** 提交按钮 */  
    submitForm() {  
      this.$refs["form"].validate(valid => {  
        if (valid) {  
          if (this.form.metadataId != undefined) {  
            updateFormMetadata(this.form).then(response => {  
              this.$modal.msgSuccess("修改成功");  
              this.open = false;  
              this.getList();  
            });  
          } else {  
            addFormMetadata(this.form).then(response => {  
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
      const metadataIds = row.metadataId || this.ids;  
      this.$modal.confirm('是否确认删除表单元数据编号为"' + metadataIds + '"的数据项？').then(function() {  
        return delFormMetadata(metadataIds);  
      }).then(() => {  
        this.getList();  
        this.$modal.msgSuccess("删除成功");  
      }).catch(() => {});  
    },  
    /** 导入按钮操作 */  
    handleImport() {  
      this.upload.open = true;  
      this.upload.tableName = undefined;  
      // 获取数据库表列表  
      listDbTables().then(response => {  
        this.tableList = response;  
      });  
    },  
    /** 提交导入 */  
    submitImport() {  
      this.$refs["upload"].validate(valid => {  
        if (valid) {  
          importFromTable(this.upload.tableName).then(response => {  
            this.$modal.msgSuccess("导入成功");  
            this.upload.open = false;  
            this.getList();  
          });  
        }  
      });  
    },  
    /** 字段管理 */  
    handleFields(row) {  
        const metadataId = row.metadataId;  
        this.$router.push({ path: '/metadata/field', query: { metadataId: metadataId } });  
    },  
    
    /** 视图管理 */  
    handleViews(row) {  
        const metadataId = row.metadataId;  
        this.$router.push({ path: '/metadata/view', query: { metadataId: metadataId } });  
    },  
    
    /** 规则管理 */  
    handleRules(row) {  
        const metadataId = row.metadataId;  
        this.$router.push({ path: '/metadata/rule', query: { metadataId: metadataId } });  
    }
}
}

</script>