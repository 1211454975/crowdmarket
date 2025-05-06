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
      <el-form-item label="图表名称" prop="chartName">  
        <el-input  
          v-model="queryParams.chartName"  
          placeholder="请输入图表名称"  
          clearable  
          size="small"  
          @keyup.enter.native="handleQuery"  
        />  
      </el-form-item>  
      <el-form-item label="图表类型" prop="chartType">  
        <el-select v-model="queryParams.chartType" placeholder="请选择图表类型" clearable size="small">  
          <el-option v-for="dict in chartTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
        </el-select>  
      </el-form-item>  
      <el-form-item label="状态" prop="status">  
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">  
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
          v-hasPermi="['metadata:chart:add']"  
        >新增</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="success"  
          icon="el-icon-edit"  
          size="mini"  
          :disabled="single"  
          @click="handleUpdate"  
          v-hasPermi="['metadata:chart:edit']"  
        >修改</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="danger"  
          icon="el-icon-delete"  
          size="mini"  
          :disabled="multiple"  
          @click="handleDelete"  
          v-hasPermi="['metadata:chart:remove']"  
        >删除</el-button>  
      </el-col>  
      <el-col :span="1.5">  
        <el-button  
          type="warning"  
          icon="el-icon-download"  
          size="mini"  
          @click="handleExport"  
          v-hasPermi="['metadata:chart:export']"  
        >导出</el-button>  
      </el-col>  
    </el-row>  
  
    <el-table v-loading="loading" :data="chartList" @selection-change="handleSelectionChange">  
      <el-table-column type="selection" width="55" align="center" />  
      <el-table-column label="图表ID" align="center" prop="chartId" :show-overflow-tooltip="true" />  
      <el-table-column label="图表名称" align="center" prop="chartName" :show-overflow-tooltip="true" />  
      <el-table-column label="图表类型" align="center" prop="chartType">  
        <template slot-scope="scope">  
          <dict-tag :options="chartTypeOptions" :value="scope.row.chartType"/>  
        </template>  
      </el-table-column>  
      <el-table-column label="图表描述" align="center" prop="chartDesc" :show-overflow-tooltip="true" />  
      <el-table-column label="关联元数据" align="center" prop="metadataName" :show-overflow-tooltip="true" />  
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
            v-hasPermi="['metadata:chart:edit']"  
          >修改</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-delete"  
            @click="handleDelete(scope.row)"  
            v-hasPermi="['metadata:chart:remove']"  
          >删除</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-view"  
            @click="handlePreview(scope.row)"  
            v-hasPermi="['metadata:chart:query']"  
          >预览</el-button>  
          <el-button  
            size="mini"  
            type="text"  
            icon="el-icon-s-data"  
            @click="handleDesign(scope.row)"  
            v-hasPermi="['metadata:chart:edit']"  
          >设计</el-button>  
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
  
    <!-- 添加或修改图表对话框 -->  
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>  
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">  
        <el-form-item label="所属元数据" prop="metadataId">  
          <el-select v-model="form.metadataId" placeholder="请选择所属元数据">  
            <el-option  
              v-for="item in metadataOptions"  
              :key="item.metadataId"  
              :label="item.metadataName"  
              :value="item.metadataId"  
            />  
          </el-select>  
        </el-form-item>  
        <el-form-item label="图表名称" prop="chartName">  
          <el-input v-model="form.chartName" placeholder="请输入图表名称" />  
        </el-form-item>  
        <el-form-item label="图表类型" prop="chartType">  
          <el-select v-model="form.chartType" placeholder="请选择图表类型">  
            <el-option v-for="dict in chartTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />  
          </el-select>  
        </el-form-item>  
        <el-form-item label="图表描述" prop="chartDesc">  
          <el-input v-model="form.chartDesc" type="textarea" :rows="3" placeholder="请输入图表描述" />  
        </el-form-item>  
        <el-form-item label="状态" prop="status">  
          <el-radio-group v-model="form.status">  
            <el-radio :label="0">草稿</el-radio>  
            <el-radio :label="1">发布</el-radio>  
            <el-radio :label="2">停用</el-radio>  
          </el-radio-group>  
        </el-form-item>  
      </el-form>  
      <div slot="footer" class="dialog-footer">  
        <el-button type="primary" @click="submitForm">确 定</el-button>  
        <el-button @click="cancel">取 消</el-button>  
      </div>  
    </el-dialog>  
  
    <!-- 图表预览对话框 -->  
    <el-dialog title="图表预览" :visible.sync="previewOpen" width="900px" append-to-body>  
      <div class="chart-preview-container">  
        <div class="chart-container" ref="chartContainer"></div>  
      </div>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { listChartMetadata, getChartMetadata, delChartMetadata, addChartMetadata, updateChartMetadata, exportChartMetadata } from "@/api/metadata/chart";  
import { listFormMetadata } from "@/api/metadata/form";  
import { getChartData } from "@/api/metadata/chart";  
import echarts from 'echarts';  
require('echarts/theme/macarons');  
  
export default {  
  name: "ChartMetadata",  
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
      // 图表表格数据  
      chartList: [],  
      // 弹出层标题  
      title: "",  
      // 是否显示弹出层  
      open: false,  
      // 是否显示预览弹出层  
      previewOpen: false,  
      // 预览图表实例  
      previewChart: null,  
      // 查询参数  
      queryParams: {  
        pageNum: 1,  
        pageSize: 10,  
        metadataId: null,  
        chartName: null,  
        chartType: null,  
        status: null  
      },  
      // 表单参数  
      form: {},  
      // 表单校验  
      rules: {  
        metadataId: [  
          { required: true, message: "所属元数据不能为空", trigger: "change" }  
        ],  
        chartName: [  
          { required: true, message: "图表名称不能为空", trigger: "blur" }  
        ],  
        chartType: [  
          { required: true, message: "图表类型不能为空", trigger: "change" }  
        ]  
      },  
      // 元数据选项  
      metadataOptions: [],  
      // 图表类型选项  
      chartTypeOptions: [  
        { value: "bar", label: "柱状图" },  
        { value: "line", label: "折线图" },  
        { value: "pie", label: "饼图" },  
        { value: "radar", label: "雷达图" },  
        { value: "scatter", label: "散点图" },  
        { value: "heatmap", label: "热力图" },  
        { value: "tree", label: "树图" },  
        { value: "treemap", label: "矩形树图" },  
        { value: "sunburst", label: "旭日图" },  
        { value: "funnel", label: "漏斗图" }  
      ],  
      // 状态选项  
      statusOptions: [  
        { value: 0, label: "草稿" },  
        { value: 1, label: "发布" },  
        { value: 2, label: "停用" }  
      ]  
    };  
  },  
  created() {  
    this.getMetadataOptions();  
    this.getList();  
  },  
  methods: {  
    /** 查询元数据选项 */  
    getMetadataOptions() {  
      listFormMetadata().then(response => {  
        this.metadataOptions = response.rows;  
      });  
    },  
    /** 查询图表列表 */  
    getList() {  
      this.loading = true;  
      listChartMetadata(this.queryParams).then(response => {  
        this.chartList = response.rows;  
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
        chartId: null,  
        metadataId: null,  
        chartName: null,  
        chartDesc: null,  
        chartType: "bar",  
        status: 0,  
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
    // 多选框选中数据  
    handleSelectionChange(selection) {  
      this.ids = selection.map(item => item.chartId);  
      this.single = selection.length !== 1;  
      this.multiple = !selection.length;  
    },  
    /** 新增按钮操作 */  
    handleAdd() {  
      this.reset();  
      this.open = true;  
      this.title = "添加图表";  
    },  
    /** 修改按钮操作 */  
    handleUpdate(row) {  
      this.reset();  
      const chartId = row.chartId || this.ids[0];  
      getChartMetadata(chartId).then(response => {  
        this.form = response.data;  
        this.open = true;  
        this.title = "修改图表";  
      });  
    },  
    /** 预览按钮操作 */  
    handlePreview(row) {  
      this.previewOpen = true;  
      this.$nextTick(() => {  
        // 销毁之前的图表实例  
        if (this.previewChart) {  
          this.previewChart.dispose();  
          this.previewChart = null;  
        }  
          
        // 初始化图表  
        this.previewChart = echarts.init(this.$refs.chartContainer, 'macarons');  
          
        // 获取图表数据  
        getChartData(row.chartId).then(response => {  
          // 根据图表类型设置不同的配置  
          let option = {};  
          const chartData = response.data;  
            
          switch (row.chartType) {  
            case 'bar':  
              option = this.getBarChartOption(chartData);  
              break;  
            case 'line':  
              option = this.getLineChartOption(chartData);  
              break;  
            case 'pie':  
              option = this.getPieChartOption(chartData);  
              break;  
            case 'radar':  
              option = this.getRadarChartOption(chartData);  
              break;  
            default:  
              option = this.getDefaultChartOption(chartData);  
              break;  
          }  
            
          // 设置图表配置  
          this.previewChart.setOption(option);  
        });  
      });  
    },  
    /** 设计按钮操作 */  
    handleDesign(row) {  
      this.$router.push({  
        path: '/chart/design',  
        query: {  
          chartId: row.chartId  
        }  
      });  
    },  
    /** 提交按钮 */  
    submitForm() {  
      this.$refs["form"].validate(valid => {  
        if (valid) {  
          if (this.form.chartId != null) {  
            updateChartMetadata(this.form).then(response => {  
              this.$modal.msgSuccess("修改成功");  
              this.open = false;  
              this.getList();  
            });  
          } else {  
            addChartMetadata(this.form).then(response => {  
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
      const chartIds = row.chartId || this.ids;  
      this.$modal.confirm('是否确认删除图表编号为"' + chartIds + '"的数据项?').then(() => {  
        return delChartMetadata(chartIds);  
      }).then(() => {  
        this.getList();  
        this.$modal.msgSuccess("删除成功");  
      }).catch(() => {});  
    },  
    /** 导出按钮操作 */  
    handleExport() {  
      this.$modal.confirm('是否确认导出所有图表数据项?').then(() => {  
        this.exportLoading = true;  
        return exportChartMetadata(this.queryParams);  
      }).then(response => {  
        this.$download.excel(response, '图表元数据数据');  
        this.exportLoading = false;  
      }).catch(() => {});  
    },  
    /** 获取柱状图配置 */  
    getBarChartOption(data) {  
      return {  
        title: {  
          text: data.title || '柱状图示例',  
          subtext: data.subtitle || ''  
        },  
        tooltip: {  
          trigger: 'axis',  
          axisPointer: {  
            type: 'shadow'  
          }  
        },  
        legend: {  
          data: data.legend || []  
        },  
        grid: {  
          left: '3%',  
          right: '4%',  
          bottom: '3%',  
          containLabel: true  
        },  
        xAxis: {  
          type: 'category',  
          data: data.xAxis || []  
        },  
        yAxis: {  
          type: 'value'  
        },  
        series: data.series || [  
          {  
            name: '示例数据',  
            type: 'bar',  
            data: [10, 20, 30, 40, 50, 60, 70]  
          }  
        ]  
      };  
    },  
    /** 获取折线图配置 */  
    getLineChartOption(data) {  
      return {  
        title: {  
          text: data.title || '折线图示例',  
          subtext: data.subtitle || ''  
        },  
        tooltip: {  
          trigger: 'axis'  
        },  
        legend: {  
          data: data.legend || []  
        },  
        grid: {  
          left: '3%',  
          right: '4%',  
          bottom: '3%',  
          containLabel: true  
        },  
        xAxis: {  
          type: 'category',  
          boundaryGap: false,  
          data: data.xAxis || []  
        },  
        yAxis: {  
          type: 'value'  
        },  
        series: data.series || [  
          {  
            name: '示例数据',  
            type: 'line',  
            data: [10, 20, 30, 40, 50, 60, 70]  
          }  
        ]  
      };  
    },  
    /** 获取饼图配置 */  
    getPieChartOption(data) {  
      return {  
        title: {  
          text: data.title || '饼图示例',  
          subtext: data.subtitle || '',  
          left: 'center'  
        },  
        tooltip: {  
          trigger: 'item',  
          formatter: '{a} <br/>{b} : {c} ({d}%)'  
        },  
        legend: {  
          orient: 'vertical',  
          left: 'left',  
          data: data.legend || []  
        },  
        series: [  
          {  
            name: data.seriesName || '示例数据',  
            type: 'pie',  
            radius: '55%',  
            center: ['50%', '60%'],  
            data: data.series || [  
              { value: 335, name: '直接访问' },  
              { value: 310, name: '邮件营销' },  
              { value: 234, name: '联盟广告' },  
              { value: 135, name: '视频广告' },  
              { value: 1548, name: '搜索引擎' }  
            ],  
            emphasis: {  
              itemStyle: {  
                shadowBlur: 10,  
                shadowOffsetX: 0,  
                shadowColor: 'rgba(0, 0, 0, 0.5)'  
              }  
            }  
          }  
        ]  
      };  
    },  
    /** 获取雷达图配置 */  
    getRadarChartOption(data) {  
      return {  
        title: {  
          text: data.title || '雷达图示例',  
          subtext: data.subtitle || ''  
        },  
        tooltip: {  
          trigger: 'item'  
        },  
        legend: {  
          data: data.legend || ['预算分配', '实际开销']  
        },  
        radar: {  
          // shape: 'circle',  
          indicator: data.indicator || [  
            { name: '销售', max: 6500 },  
            { name: '管理', max: 16000 },  
            { name: '信息技术', max: 30000 },  
            { name: '客服', max: 38000 },  
            { name: '研发', max: 52000 },  
            { name: '市场', max: 25000 }  
          ]  
        },  
        series: [{  
          name: data.seriesName || '预算 vs 开销',  
          type: 'radar',  
          data: data.series || [  
            {  
              value: [4300, 10000, 28000, 35000, 50000, 19000],  
              name: '预算分配'  
            },  
            {  
              value: [5000, 14000, 28000, 31000, 42000, 21000],  
              name: '实际开销'  
            }  
          ]  
        }]  
      };  
    },  
    /** 获取默认图表配置 */  
    getDefaultChartOption(data) {  
      return {  
        title: {  
          text: data.title || '图表示例',  
          subtext: data.subtitle || ''  
        },  
        tooltip: {  
          trigger: 'axis'  
        },  
        legend: {  
          data: data.legend || []  
        },  
        grid: {  
          left: '3%',  
          right: '4%',  
          bottom: '3%',  
          containLabel: true  
        },  
        xAxis: {  
          type: 'category',  
          data: data.xAxis || []  
        },  
        yAxis: {  
          type: 'value'  
        },  
        series: data.series || [  
          {  
            name: '示例数据',  
            type: 'bar',  
            data: [10, 20, 30, 40, 50, 60, 70]  
          }  
        ]  
      };  
    }  
  }  
};  
</script>  
  
<style scoped>  
.chart-preview-container {  
  width: 100%;  
  height: 500px;  
  padding: 20px;  
}  
.chart-container {  
  width: 100%;  
  height: 100%;  
  background-color: #fff;  
}  
</style>