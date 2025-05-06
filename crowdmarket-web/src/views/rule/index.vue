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
      <el-form-item label="规则名称" prop="ruleName">  
        <el-input  
          v-model="queryParams.ruleName"  
          placeholder="请输入规则名称"  
          clearable  
          size="small"  
          @keyup.enter.native="handleQuery"  
        />  
      </el-form-item>  
      <el-form-item label="规则类型" prop="ruleType">  
        <el-select v-model="queryParams.ruleType" placeholder="请选择规则类型" clearable size="small">  
          <el-option v-for="dict in ruleTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
        </el-select>  
      </el-form-item>  
      <el-form-item label="状态" prop="isActive">  
        <el-select v-model="queryParams.isActive" placeholder="请选择状态" clearable size="small">  
          <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
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
          v-hasPermi="['metadata:rule:add']"  
        >新增</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="success"  
          icon="el-icon-edit"  
          size="mini"  
          :disabled="single"  
          @click="handleUpdate"  
          v-hasPermi="['metadata:rule:edit']"  
        >修改</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="danger"  
          icon="el-icon-delete"  
          size="mini"  
          :disabled="multiple"  
          @click="handleDelete"  
          v-hasPermi="['metadata:rule:remove']"  
        >删除</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="warning"  
          icon="el-icon-download"  
          size="mini"  
          @click="handleExport"  
          v-hasPermi="['metadata:rule:export']"  
        >导出</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="info"  
          icon="el-icon-sort"  
          size="mini"  
          @click="handleSort"  
          v-hasPermi="['metadata:rule:edit']"  
        >排序</el-button>  
      </el-col>  
    </el-row>  
  
    <el-table v-loading="loading" :data="ruleList" @selection-change="handleSelectionChange">  
      <el-table-column type="selection" width="55" align="center" />  
      <el-table-column label="规则ID" align="center" prop="ruleId" :show-overflow-tooltip="true" />  
      <el-table-column label="规则名称" align="center" prop="ruleName" :show-overflow-tooltip="true" />  
      <el-table-column label="规则类型" align="center" prop="ruleType">  
        <template slot-scope="scope">  
          <dict-tag :options="ruleTypeOptions" :value="scope.row.ruleType"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="规则描述" align="center" prop="ruleDesc" :show-overflow-tooltip="true" />  
      <el-table-column label="状态" align="center" prop="isActive">  
        <template slot-scope="scope">  
          <el-switch  
            v-model="scope.row.isActive"  
            :active-value="1"  
            :inactive-value="0"  
            @change="handleStatusChange(scope.row)"  
          ></el-switch>  
        </template>  
      </el-table-column>  
      <el-table-column label="执行顺序" align="center" prop="executionOrder" width="80" />  
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
            v-hasPermi="['metadata:rule:edit']"  
          >修改</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-delete"  
            @click="handleDelete(scope.row)"  
            v-hasPermi="['metadata:rule:remove']"  
          >删除</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-view"  
            @click="handlePreview(scope.row)"  
            v-hasPermi="['metadata:rule:query']"  
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
  
    <!-- 规则排序对话框 -->  
    <el-dialog title="规则排序" :visible.sync="sortOpen" width="800px" append-to-body>  
      <el-table  
        ref="dragTable"  
        :data="sortList"  
        row-key="ruleId"  
        :max-height="500"  
      >  
        <el-table-column label="序号" type="index" width="50" class-name="allowDrag" />  
        <el-table-column label="规则名称" prop="ruleName" :show-overflow-tooltip="true" />  
        <el-table-column label="规则类型" prop="ruleType">  
          <template slot-scope="scope">  
            <dict-tag :options="ruleTypeOptions" :value="scope.row.ruleType"/>  
          </template>  
        </el-table-column>  
        <el-table-column label="执行顺序" prop="executionOrder" width="80" />  
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
  
    <!-- 规则预览对话框 -->  
    <el-dialog title="规则预览" :visible.sync="previewOpen" width="900px" append-to-body>  
      <el-descriptions title="规则基本信息" :column="2" border>  
        <el-descriptions-item label="规则名称">{{ previewRule.ruleName }}</el-descriptions-item>  
        <el-descriptions-item label="规则类型">  
          <dict-tag :options="ruleTypeOptions" :value="previewRule.ruleType"/>  
        </el-descriptions-item>  
        <el-descriptions-item label="规则描述">{{ previewRule.ruleDesc }}</el-descriptions-item>  
        <el-descriptions-item label="状态">  
          <dict-tag :options="statusOptions" :value="previewRule.isActive"/>  
        </el-descriptions-item>  
        <el-descriptions-item label="执行顺序">{{ previewRule.executionOrder }}</el-descriptions-item>  
        <el-descriptions-item label="创建时间">{{ parseTime(previewRule.createTime) }}</el-descriptions-item>  
      </el-descriptions>  
  
      <el-divider content-position="left">规则条件</el-divider>  
      <el-table :data="previewConditions" border>  
        <el-table-column type="index" width="50" label="序号" />  
        <el-table-column prop="fieldName" label="字段" width="150" />  
        <el-table-column prop="operator" label="操作符" width="100">  
          <template slot-scope="scope">  
            <dict-tag :options="operatorOptions" :value="scope.row.operator"/>  
          </template>  
        </el-table-column>  
        <el-table-column prop="value" label="比较值" />  
        <el-table-column prop="logicType" label="逻辑类型" width="80">  
          <template slot-scope="scope">  
            <el-tag type="info">{{ scope.row.logicType }}</el-tag>  
          </template>  
        </el-table-column>  
        <el-table-column prop="groupId" label="条件组" width="100" />  
      </el-table>  
  
      <el-divider content-position="left">规则动作</el-divider>  
      <el-table :data="previewActions" border>  
        <el-table-column type="index" width="50" label="序号" />  
        <el-table-column prop="actionType" label="动作类型" width="120">  
          <template slot-scope="scope">  
            <dict-tag :options="actionTypeOptions" :value="scope.row.actionType"/>  
          </template>  
        </el-table-column>  
        <el-table-column prop="targetFieldName" label="目标字段" width="150" />  
        <el-table-column prop="actionValue" label="动作值" :show-overflow-tooltip="true" />  
        <el-table-column prop="actionScript" label="动作脚本" :show-overflow-tooltip="true" />  
      </el-table>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { listFormRule, getFormRule, delFormRule, addFormRule, updateFormRule, exportFormRule, updateRuleStatus, updateRuleOrder } from "@/api/metadata/rule";  
import { listFormMetadata } from "@/api/metadata/form";  
import { listFormFieldMetadata } from "@/api/metadata/field";  
import { listRuleCondition } from "@/api/metadata/condition";  
import { listRuleAction } from "@/api/metadata/action";  
  
export default {  
  name: "FormRule",  
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
      // 业务规则表格数据  
      ruleList: [],  
      // 排序表格数据  
      sortList: [],  
      // 弹出层标题  
      title: "",  
      // 是否显示弹出层  
      open: false,  
      // 是否显示排序弹出层  
      sortOpen: false,  
      // 是否显示预览弹出层  
      previewOpen: false,  
      // 预览规则数据  
      previewRule: {},  
      // 预览条件数据  
      previewConditions: [],  
      // 预览动作数据  
      previewActions: [],  
      // 查询参数  
      queryParams: {  
        pageNum: 1,  
        pageSize: 10,  
        metadataId: null,  
        ruleName: null,  
        ruleType: null,  
        isActive: null  
      },  
      // 表单参数  
      form: {},  
      // 元数据选项  
      metadataOptions: [],  
      // 规则类型选项  
      ruleTypeOptions: [  
        { value: "validation", label: "验证规则" },  
        { value: "calculation", label: "计算规则" },  
        { value: "visibility", label: "可见性规则" },  
        { value: "dependency", label: "依赖规则" },  
        { value: "workflow", label: "工作流规则" }  
      ],  
      // 状态选项  
      statusOptions: [  
        { value: 0, label: "停用" },  
        { value: 1, label: "启用" }  
      ],  
      // 操作符选项  
      operatorOptions: [  
        { value: "eq", label: "等于" },  
        { value: "ne", label: "不等于" },  
        { value: "gt", label: "大于" },  
        { value: "lt", label: "小于" },  
        { value: "ge", label: "大于等于" },  
        { value: "le", label: "小于等于" },  
        { value: "contains", label: "包含" },  
        { value: "startswith", label: "开始于" },  
        { value: "endswith", label: "结束于" }  
      ],  
      // 动作类型选项  
      actionTypeOptions: [  
        { value: "setValue", label: "设置值" },  
        { value: "setVisible", label: "设置可见性" },  
        { value: "setRequired", label: "设置必填" },  
        { value: "setReadOnly", label: "设置只读" },  
        { value: "calculate", label: "计算" },  
        { value: "message", label: "消息提示" },  
        { value: "api", label: "调用API" }  
      ]  
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
        if (this.metadataOptions.length > 0) {  
          this.queryParams.metadataId = this.metadataOptions[0].metadataId;  
          this.getList();  
        }  
      });  
    },  
    /** 查询规则列表 */  
    getList() {  
      this.loading = true;  
      listFormRule(this.queryParams).then(response => {  
        this.ruleList = response.rows;  
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
      this.resetForm("queryForm");  
      this.handleQuery();  
    },  
    /** 元数据变更操作 */  
    handleMetadataChange(metadataId) {  
      this.queryParams.metadataId = metadataId;  
      this.handleQuery();  
    },  
    // 多选框选中数据  
    handleSelectionChange(selection) {  
      this.ids = selection.map(item => item.ruleId);  
      this.single = selection.length !== 1;  
      this.multiple = !selection.length;  
    },  
    /** 新增按钮操作 */  
    handleAdd() {  
      this.$router.push({  
        path: '/rule/edit',  
        query: {  
          metadataId: this.queryParams.metadataId,  
          mode: 'add'  
        }  
      });  
    },  
    /** 修改按钮操作 */  
    handleUpdate(row) {  
      this.$router.push({  
        path: '/rule/edit',  
        query: {  
          ruleId: row.ruleId || this.ids[0],  
          mode: 'edit'  
        }  
      });  
    },  
    /** 预览按钮操作 */  
    handlePreview(row) {  
      const ruleId = row.ruleId;  
        
      // 获取规则详情  
      getFormRule(ruleId).then(response => {  
        this.previewRule = response.data;  
          
        // 获取规则条件  
        listRuleCondition({ ruleId: ruleId }).then(conditionResponse => {  
          // 获取字段信息，用于显示字段名称  
          listFormFieldMetadata({ metadataId: this.previewRule.metadataId }).then(fieldResponse => {  
            const fieldMap = {};  
            fieldResponse.rows.forEach(field => {  
              fieldMap[field.fieldId] = field;  
            });  
              
            // 设置条件数据，添加字段名称  
            this.previewConditions = conditionResponse.rows.map(condition => {  
              const field = fieldMap[condition.fieldId] || {};  
              return {  
                ...condition,  
                fieldName: field.fieldName || condition.fieldId  
              };  
            });  
              
            // 获取规则动作  
            listRuleAction({ ruleId: ruleId }).then(actionResponse => {  
              // 设置动作数据，添加目标字段名称  
              this.previewActions = actionResponse.rows.map(action => {  
                const field = action.targetFieldId ? fieldMap[action.targetFieldId] : null;  
                return {  
                  ...action,  
                  targetFieldName: field ? field.fieldName : (action.targetFieldId || '-')  
                };  
              });  
                
              // 显示预览对话框  
              this.previewOpen = true;  
            });  
          });  
        });  
      });  
    },  
    /** 状态修改操作 */  
    handleStatusChange(row) {  
      const text = row.isActive === 1 ? "启用" : "停用";  
      this.$modal.confirm('确认要' + text + '"' + row.ruleName + '"规则吗?').then(() => {  
        return updateRuleStatus(row.ruleId, row.isActive);  
      }).then(() => {  
        this.$modal.msgSuccess(text + "成功");  
      }).catch(() => {  
        row.isActive = row.isActive === 1 ? 0 : 1;  
      });  
    },  
    /** 排序按钮操作 */  
    handleSort() {  
      if (!this.queryParams.metadataId) {  
        this.$message.warning("请先选择元数据");  
        return;  
      }  
        
      // 复制规则列表用于排序  
      this.sortList = JSON.parse(JSON.stringify(this.ruleList));  
      // 按执行顺序排序  
      this.sortList.sort((a, b) => a.executionOrder - b.executionOrder);  
      this.sortOpen = true;  
    },  
    /** 上移操作 */  
    moveUp(index) {  
      if (index > 0) {  
        const temp = this.sortList[index];  
        this.sortList[index] = this.sortList[index - 1];  
        this.sortList[index - 1] = temp;  
      }  
    },  
    /** 下移操作 */  
    moveDown(index) {  
      if (index < this.sortList.length - 1) {  
        const temp = this.sortList[index];  
        this.sortList[index] = this.sortList[index + 1];  
        this.sortList[index + 1] = temp;  
      }  
    },  
    /** 取消排序 */  
    cancelSort() {  
      this.sortOpen = false;  
    },  
    /** 提交排序 */  
    submitSortForm() {  
      const data = this.sortList.map((item, index) => {  
        return {  
          ruleId: item.ruleId,  
          executionOrder: index * 10  
        };  
      });  
        
      updateRuleOrder(data).then(response => {  
        this.$modal.msgSuccess("排序成功");  
        this.sortOpen = false;  
        this.getList();  
      });  
    },  
    /** 删除按钮操作 */  
    handleDelete(row) {  
      const ruleIds = row.ruleId || this.ids;  
      this.$modal.confirm('是否确认删除规则编号为"' + ruleIds + '"的数据项?').then(() => {  
        return delFormRule(ruleIds);  
      }).then(() => {  
        this.getList();  
        this.$modal.msgSuccess("删除成功");  
      }).catch(() => {});  
    },  
    /** 导出按钮操作 */  
    handleExport() {  
      this.$modal.confirm('是否确认导出所有规则数据项?').then(() => {  
        this.exportLoading = true;  
        return exportFormRule(this.queryParams);  
      }).then(response => {  
        this.$download.excel(response, '业务规则数据');  
        this.exportLoading = false;  
      }).catch(() => {});  
    }  
  }  
};  
</script>  
  
<style scoped>  
.el-tag + .el-tag {  
  margin-left: 10px;  
}  
</style>