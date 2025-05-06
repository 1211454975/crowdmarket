<template>  
  <div class="app-container">  
    <el-card class="box-card">  
      <div slot="header" class="clearfix">  
        <span>{{ title }}</span>  
      </div>  
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="元数据名称" prop="metadataName">  
              <el-input v-model="form.metadataName" placeholder="请输入元数据名称" />  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="关联表名" prop="tableName">  
              <el-input v-model="form.tableName" placeholder="请输入关联表名" />  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="24">  
            <el-form-item label="元数据描述" prop="metadataDesc">  
              <el-input v-model="form.metadataDesc" type="textarea" placeholder="请输入元数据描述" />  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-row>  
          <el-col :span="12">  
            <el-form-item label="状态" prop="status">  
              <el-radio-group v-model="form.status">  
                <el-radio  
                  v-for="dict in statusOptions"  
                  :key="dict.dictValue"  
                  :label="parseInt(dict.dictValue)"  
                >{{dict.dictLabel}}</el-radio>  
              </el-radio-group>  
            </el-form-item>  
          </el-col>  
        </el-row>  
        <el-form-item>  
          <el-button type="primary" @click="submitForm">保 存</el-button>  
          <el-button @click="cancel">取 消</el-button>  
        </el-form-item>  
      </el-form>  
    </el-card>  
  
    <!-- 字段列表 -->  
    <el-card class="box-card" v-if="form.metadataId">  
      <div slot="header" class="clearfix">  
        <span>字段列表</span>  
        <el-button  
          style="float: right; padding: 3px 0"  
          type="text"  
          icon="el-icon-plus"  
          @click="handleAddField"  
          v-hasPermi="['metadata:field:add']"  
        >添加字段</el-button>  
      </div>  
      <el-table :data="fieldList" border>  
        <el-table-column label="字段名称" align="center" prop="fieldName" />  
        <el-table-column label="字段标签" align="center" prop="fieldLabel" />  
        <el-table-column label="字段类型" align="center" prop="fieldType" />  
        <el-table-column label="数据库列名" align="center" prop="columnName" />  
        <el-table-column label="数据库列类型" align="center" prop="columnType" />  
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
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">  
          <template slot-scope="scope">  
            <el-button  
              size="mini"  
              type="text"  
              icon="el-icon-edit"  
              @click="handleEditField(scope.row)"  
              v-hasPermi="['metadata:field:edit']"  
            >修改</el-button>  
            <el-button  
              size="mini"  
              type="text"  
              icon="el-icon-delete"  
              @click="handleDeleteField(scope.row)"  
              v-hasPermi="['metadata:field:remove']"  
            >删除</el-button>  
          </template>  
        </el-table-column>  
      </el-table>  
    </el-card>  
  </div>  
</template>  
  
<script>  
import { getFormMetadata, addFormMetadata, updateFormMetadata } from "@/api/metadata/form";  
import { listFormFieldMetadata, addFormFieldMetadata, delFormFieldMetadata } from "@/api/metadata/field";  
  
export default {  
  name: "FormMetadataEdit",  
  data() {  
    return {  
      // 表单参数  
      form: {  
        metadataId: undefined,  
        metadataName: undefined,  
        metadataDesc: undefined,  
        tableName: undefined,  
        status: 0  
      },  
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
      // 状态选项  
      statusOptions: [  
        { dictLabel: "草稿", dictValue: "0" },  
        { dictLabel: "发布", dictValue: "1" },  
        { dictLabel: "停用", dictValue: "2" }  
      ],  
      // 是否选项  
      yesNoOptions: [  
        { dictLabel: "否", dictValue: "0" },  
        { dictLabel: "是", dictValue: "1" }  
      ],  
      // 标题  
      title: "",  
      // 字段列表  
      fieldList: []  
    };  
  },  
  created() {  
    const metadataId = this.$route.query.metadataId;  
    if (metadataId) {  
      this.title = "编辑表单元数据";  
      this.form.metadataId = metadataId;  
      this.getInfo(metadataId);  
      this.getFieldList(metadataId);  
    } else {  
      this.title = "添加表单元数据";  
    }  
  },  
  methods: {  
    /** 获取表单元数据详细信息 */  
    getInfo(metadataId) {  
      getFormMetadata(metadataId).then(response => {  
        this.form = response.data;  
      });  
    },  
    /** 获取字段列表 */  
    getFieldList(metadataId) {  
      listFormFieldMetadata({ metadataId: metadataId }).then(response => {  
        this.fieldList = response.rows;  
      });  
    },  
    /** 取消按钮 */  
    cancel() {  
      this.$router.push({ path: "/metadata/form" });  
    },  
    /** 提交按钮 */  
    submitForm() {  
      this.$refs["form"].validate(valid => {  
        if (valid) {  
          if (this.form.metadataId != undefined) {  
            updateFormMetadata(this.form).then(response => {  
              this.$modal.msgSuccess("修改成功");  
              this.cancel();  
            });  
          } else {  
            addFormMetadata(this.form).then(response => {  
              this.$modal.msgSuccess("新增成功");  
              this.cancel();  
            });  
          }  
        }  
      });  
    },  
    /** 添加字段按钮操作 */  
    handleAddField() {  
      this.$router.push({   
        path: "/metadata/field/edit",   
        query: {   
          metadataId: this.form.metadataId   
        }  
      });  
    },  
    /** 修改字段按钮操作 */  
    handleEditField(row) {  
      this.$router.push({   
        path: "/metadata/field/edit",   
        query: {   
          metadataId: this.form.metadataId,  
          fieldId: row.fieldId  
        }  
      });  
    },  
    /** 删除字段按钮操作 */  
    handleDeleteField(row) {  
      this.$modal.confirm('是否确认删除字段名称为"' + row.fieldName + '"的数据项？').then(function() {  
        return delFormFieldMetadata(row.fieldId);  
      }).then(() => {  
        this.getFieldList(this.form.metadataId);  
        this.$modal.msgSuccess("删除成功");  
      }).catch(() => {});  
    }  
  }  
};  
</script>