<template>  
  <div class="app-container">  
    <el-card class="box-card">  
      <div slot="header" class="clearfix">  
        <span>图表设计</span>  
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleSave">保存图表</el-button>  
        <el-button style="float: right; padding: 3px 0; margin-right: 10px;" type="text" @click="handlePreview">预览图表</el-button>  
      </div>  
        
      <el-form ref="chartForm" :model="chartForm" :rules="rules" label-width="120px">  
        <el-row :gutter="20">  
          <el-col :span="12">  
            <el-form-item label="图表名称" prop="chartName">  
              <el-input v-model="chartForm.chartName" placeholder="请输入图表名称" />  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="图表类型" prop="chartType">  
              <el-select v-model="chartForm.chartType" placeholder="请选择图表类型" style="width: 100%">  
                <el-option label="柱状图" value="bar" />  
                <el-option label="折线图" value="line" />  
                <el-option label="饼图" value="pie" />  
                <el-option label="雷达图" value="radar" />  
                <el-option label="散点图" value="scatter" />  
                <el-option label="漏斗图" value="funnel" />  
              </el-select>  
            </el-form-item>  
          </el-col>  
        </el-row>  
          
        <el-form-item label="图表描述" prop="chartDesc">  
          <el-input v-model="chartForm.chartDesc" type="textarea" placeholder="请输入图表描述" />  
        </el-form-item>  
          
        <el-form-item label="数据源" prop="metadataId">  
          <el-select v-model="chartForm.metadataId" placeholder="请选择数据源" style="width: 100%" @change="handleMetadataChange">  
            <el-option  
              v-for="item in metadataOptions"  
              :key="item.metadataId"  
              :label="item.metadataName"  
              :value="item.metadataId"  
            />  
          </el-select>  
        </el-form-item>  
          
        <el-divider content-position="left">维度与度量配置</el-divider>  
          
        <el-row :gutter="20">  
          <el-col :span="12">  
            <el-form-item label="维度字段">  
              <el-transfer  
                v-model="selectedDimensions"  
                :data="fieldOptions"  
                :titles="['可选字段', '已选维度']"  
                :button-texts="['移除', '添加']"  
                :format="{  
                  noChecked: '${total}',  
                  hasChecked: '${checked}/${total}'  
                }"  
                @change="handleDimensionChange"  
              />  
            </el-form-item>  
          </el-col>  
          <el-col :span="12">  
            <el-form-item label="度量字段">  
              <el-transfer  
                v-model="selectedMeasures"  
                :data="fieldOptions"  
                :titles="['可选字段', '已选度量']"  
                :button-texts="['移除', '添加']"  
                :format="{  
                  noChecked: '${total}',  
                  hasChecked: '${checked}/${total}'  
                }"  
                @change="handleMeasureChange"  
              />  
            </el-form-item>  
          </el-col>  
        </el-row>  
          
        <el-divider content-position="left">度量配置</el-divider>  
          
        <div v-for="(measure, index) in chartConfig.measureFields" :key="index" class="measure-config">  
          <el-row :gutter="20">  
            <el-col :span="8">  
              <el-form-item :label="'字段名称'" label-width="80px">  
                <el-input v-model="measure.fieldName" disabled />  
              </el-form-item>  
            </el-col>  
            <el-col :span="8">  
              <el-form-item :label="'聚合方式'" label-width="80px">  
                <el-select v-model="measure.aggregation" style="width: 100%">  
                  <el-option label="求和" value="SUM" />  
                  <el-option label="平均值" value="AVG" />  
                  <el-option label="最大值" value="MAX" />  
                  <el-option label="最小值" value="MIN" />  
                  <el-option label="计数" value="COUNT" />  
                </el-select>  
              </el-form-item>  
            </el-col>  
            <el-col :span="8">  
              <el-form-item :label="'显示名称'" label-width="80px">  
                <el-input v-model="measure.displayName" placeholder="请输入显示名称" />  
              </el-form-item>  
            </el-col>  
          </el-row>  
        </div>  
          
        <el-divider content-position="left">数据筛选</el-divider>  
          
        <div v-for="(filter, index) in chartConfig.filterCondition" :key="'filter-'+index" class="filter-condition">  
          <el-row :gutter="20">  
            <el-col :span="6">  
              <el-form-item :label="'字段'" label-width="50px">  
                <el-select v-model="filter.fieldId" style="width: 100%">  
                  <el-option  
                    v-for="field in fieldOptions"  
                    :key="field.key"  
                    :label="field.label"  
                    :value="field.key"  
                  />  
                </el-select>  
              </el-form-item>  
            </el-col>  
            <el-col :span="6">  
              <el-form-item :label="'操作符'" label-width="60px">  
                <el-select v-model="filter.operator" style="width: 100%">  
                  <el-option label="等于" value="eq" />  
                  <el-option label="不等于" value="ne" />  
                  <el-option label="大于" value="gt" />  
                  <el-option label="小于" value="lt" />  
                  <el-option label="大于等于" value="ge" />  
                  <el-option label="小于等于" value="le" />  
                  <el-option label="包含" value="contains" />  
                  <el-option label="开始于" value="startswith" />  
                  <el-option label="结束于" value="endswith" />  
                </el-select>  
              </el-form-item>  
            </el-col>  
            <el-col :span="6">  
              <el-form-item :label="'值'" label-width="40px">  
                <el-input v-model="filter.value" placeholder="请输入值" />  
              </el-form-item>  
            </el-col>  
            <el-col :span="4">  
              <el-form-item :label="'逻辑'" label-width="50px">  
                <el-select v-model="filter.logicType" style="width: 100%">  
                  <el-option label="AND" value="AND" />  
                  <el-option label="OR" value="OR" />  
                </el-select>  
              </el-form-item>  
            </el-col>  
            <el-col :span="2">  
              <el-button type="danger" icon="el-icon-delete" circle @click="removeFilter(index)" />  
            </el-col>  
          </el-row>  
        </div>  
          
        <el-button type="primary" icon="el-icon-plus" @click="addFilter">添加筛选条件</el-button>  
          
        <el-divider content-position="left">样式配置</el-divider>  
          
        <el-form-item label="数据限制" prop="dataLimit">  
          <el-input-number v-model="chartConfig.dataLimit" :min="1" :max="10000" />  
        </el-form-item>  
          
        <el-form-item label="刷新间隔(秒)" prop="refreshInterval">  
          <el-input-number v-model="chartConfig.refreshInterval" :min="0" :max="3600" />  
        </el-form-item>  
          
        <el-collapse>  
          <el-collapse-item title="高级样式配置" name="1">  
            <el-tabs v-model="styleTab">  
              <el-tab-pane label="标题配置" name="title">  
                <el-form-item label="标题文本">  
                  <el-input v-model="chartConfig.styleConfig.title.text" />  
                </el-form-item>  
                <el-form-item label="副标题文本">  
                  <el-input v-model="chartConfig.styleConfig.title.subtext" />  
                </el-form-item>  
                <el-form-item label="标题位置">  
                  <el-select v-model="chartConfig.styleConfig.title.left" style="width: 100%">  
                    <el-option label="居左" value="left" />  
                    <el-option label="居中" value="center" />  
                    <el-option label="居右" value="right" />  
                  </el-select>  
                </el-form-item>  
              </el-tab-pane>  
                
              <el-tab-pane label="图例配置" name="legend">  
                <el-form-item label="图例显示">  
                  <el-switch v-model="chartConfig.styleConfig.legend.show" />  
                </el-form-item>  
                <el-form-item label="图例位置">  
                  <el-select v-model="chartConfig.styleConfig.legend.position" style="width: 100%">  
                    <el-option label="顶部" value="top" />  
                    <el-option label="底部" value="bottom" />  
                    <el-option label="左侧" value="left" />  
                    <el-option label="右侧" value="right" />  
                  </el-select>  
                </el-form-item>  
              </el-tab-pane>  
                
              <el-tab-pane label="坐标轴配置" name="axis" v-if="['bar', 'line', 'scatter'].includes(chartForm.chartType)">  
                <el-form-item label="X轴名称">  
                  <el-input v-model="chartConfig.styleConfig.xAxis.name" />  
                </el-form-item>  
                <el-form-item label="Y轴名称">  
                  <el-input v-model="chartConfig.styleConfig.yAxis.name" />  
                </el-form-item>  
                <el-form-item label="显示网格线">  
                  <el-switch v-model="chartConfig.styleConfig.grid.show" />  
                </el-form-item>  
              </el-tab-pane>  
                
              <el-tab-pane label="提示框配置" name="tooltip">  
                <el-form-item label="提示框显示">  
                  <el-switch v-model="chartConfig.styleConfig.tooltip.show" />  
                </el-form-item>  
                <el-form-item label="触发方式">  
                  <el-select v-model="chartConfig.styleConfig.tooltip.trigger" style="width: 100%">  
                    <el-option label="数据项" value="item" />  
                    <el-option label="坐标轴" value="axis" />  
                  </el-select>  
                </el-form-item>  
              </el-tab-pane>  
            </el-tabs>  
          </el-collapse-item>  
        </el-collapse>  
      </el-form>  
    </el-card>  
      
    <!-- 预览对话框 -->  
    <el-dialog title="图表预览" :visible.sync="previewVisible" width="70%">  
      <div class="chart-preview" style="height: 400px;"></div>  
      <span slot="footer" class="dialog-footer">  
        <el-button @click="previewVisible = false">关闭</el-button>  
        <el-button type="primary" @click="handleExport">导出图表</el-button>  
      </span>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { getMetadataList } from '@/api/metadata/form'  
import { getFieldList } from '@/api/metadata/field'  
import { getChartData, saveChart, getChart } from '@/api/metadata/chart'  
import echarts from 'echarts'  
  
export default {  
  name: 'ChartDesign',  
  data() {  
    return {  
      loading: false,  
      // 图表基本信息表单  
      chartForm: {  
        chartId: undefined,  
        chartName: '',  
        chartDesc: '',  
        chartType: 'bar',  
        metadataId: '',  
        status: 0  
      },  
      // 表单验证规则  
      rules: {  
        chartName: [  
          { required: true, message: '请输入图表名称', trigger: 'blur' },  
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }  
        ],  
        chartType: [  
          { required: true, message: '请选择图表类型', trigger: 'change' }  
        ],  
        metadataId: [  
          { required: true, message: '请选择数据源', trigger: 'change' }  
        ]  
      },  
      // 元数据选项  
      metadataOptions: [],  
      // 字段选项  
      fieldOptions: [],  
      // 已选维度字段  
      selectedDimensions: [],  
      // 已选度量字段  
      selectedMeasures: [],  
      // 图表配置  
      chartConfig: {  
        dimensionFields: [],  
        measureFields: [],  
        filterCondition: [],  
        sortConfig: [],  
        styleConfig: {  
          title: {  
            text: '',  
            subtext: '',  
            left: 'center'  
          },  
          legend: {  
            show: true,  
            position: 'bottom'  
          },  
          tooltip: {  
            show: true,  
            trigger: 'item'  
          },  
          grid: {  
            show: true  
          },  
          xAxis: {  
            name: '',  
            nameLocation: 'end'  
          },  
          yAxis: {  
            name: '',  
            nameLocation: 'end'  
          }  
        },  
        dataLimit: 1000,  
        refreshInterval: 0  
      },  
      // 样式配置当前标签页  
      styleTab: 'title',  
      // 预览对话框可见性  
      previewVisible: false,  
      // 图表实例  
      chart: null  
    }  
  },  
  created() {  
    this.getMetadataOptions()  
    // 如果是编辑模式，加载图表数据  
    const chartId = this.$route.params.chartId  
    if (chartId) {  
      this.loadChartData(chartId)  
    }  
  },  
  methods: {  
    // 获取元数据选项  
    getMetadataOptions() {  
      this.loading = true  
      getMetadataList().then(response => {  
        this.metadataOptions = response.data  
        this.loading = false  
      }).catch(() => {  
        this.loading = false  
      })  
    },  
      
    // 加载图表数据  
    loadChartData(chartId) {  
      this.loading = true  
      getChart(chartId).then(response => {  
        const chartData = response.data  
        // 设置基本信息  
        this.chartForm = {  
          chartId: chartData.chartId,  
          chartName: chartData.chartName,  
          chartDesc: chartData.chartDesc,  
          chartType: chartData.chartType,  
          metadataId: chartData.metadataId,  
          status: chartData.status  
        }  
          
        // 加载字段选项  
        this.handleMetadataChange(chartData.metadataId)  
          
        // 设置图表配置  
        if (chartData.chartConfig) {  
          const config = JSON.parse(chartData.chartConfig.dimensionFields || '[]')  
          this.chartConfig.dimensionFields = config  
          this.selectedDimensions = config.map(item => item.fieldId)  
            
          const measureConfig = JSON.parse(chartData.chartConfig.measureFields || '[]')  
          this.chartConfig.measureFields = measureConfig  
          this.selectedMeasures = measureConfig.map(item => item.fieldId)  
            
          this.chartConfig.filterCondition = JSON.parse(chartData.chartConfig.filterCondition || '[]')  
          this.chartConfig.sortConfig = JSON.parse(chartData.chartConfig.sortConfig || '[]')  
          this.chartConfig.styleConfig = JSON.parse(chartData.chartConfig.styleConfig || '{}')  
          this.chartConfig.dataLimit = chartData.chartConfig.dataLimit || 1000  
          this.chartConfig.refreshInterval = chartData.chartConfig.refreshInterval || 0  
        }  
          
        this.loading = false  
      }).catch(() => {  
        this.loading = false  
      })  
    },  
      
    // 处理元数据变更  
    handleMetadataChange(metadataId) {  
      if (!metadataId) return  
        
      this.loading = true  
      getFieldList(metadataId).then(response => {  
        const fields = response.data  
        this.fieldOptions = fields.map(field => ({  
          key: field.fieldId,  
          label: field.fieldLabel,  
          fieldName: field.fieldName,  
          fieldType: field.fieldType  
        }))  
        this.loading = false  
      }).catch(() => {  
        this.loading = false  
      })  
    },  
      
    // 处理维度字段变更  
    handleDimensionChange(value) {  
      // 清除已移除的维度字段  
      this.chartConfig.dimensionFields = this.chartConfig.dimensionFields.filter(  
        item => value.includes(item.fieldId)  
      )  
        
      // 添加新选择的维度字段  
      value.forEach(fieldId => {  
        const exists = this.chartConfig.dimensionFields.some(item => item.fieldId === fieldId)  
        if (!exists) {  
          const field = this.fieldOptions.find(item => item.key === fieldId)  
          if (field) {  
            this.chartConfig.dimensionFields.push({  
              fieldId: field.key,  
              fieldName: field.fieldName,  
              displayName: field.label  
            })  
          }  
        }  
      })  
    },  
      
    // 处理度量字段变更  
    handleMeasureChange(value) {  
      // 清除已移除的度量字段  
      this.chartConfig.measureFields = this.chartConfig.measureFields.filter(  
        item => value.includes(item.fieldId)  
      )  
        
      // 添加新选择的度量字段  
      value.forEach(fieldId => {  
        const exists = this.chartConfig.measureFields.some(item => item.fieldId === fieldId)  
        if (!exists) {  
          const field = this.fieldOptions.find(item => item.key === fieldId)  
          if (field) {  
            this.chartConfig.measureFields.push({  
              fieldId: field.key,  
              fieldName: field.fieldName,  
              displayName: field.label,  
              aggregation: 'SUM'  
            })  
          }  
        }  
      })  
    },  
      
    // 添加筛选条件  
    addFilter() {  
      this.chartConfig.filterCondition.push({  
        fieldId: '',  
        operator: 'eq',  
        value: '',  
        logicType: 'AND'  
      })  
    },  
      
    // 移除筛选条件  
    removeFilter(index) {  
      this.chartConfig.filterCondition.splice(index, 1)  
    },  
      
    // 保存图表  
    handleSave() {  
      this.$refs.chartForm.validate(valid => {  
        if (!valid) {  
          return  
        }  
          
        // 验证维度和度量字段  
        if (this.chartConfig.dimensionFields.length === 0) {  
          this.$message.error('请至少选择一个维度字段')  
          return  
        }  
          
        if (this.chartConfig.measureFields.length === 0) {  
          this.$message.error('请至少选择一个度量字段')  
          return  
        }  
          
        // 构建保存数据  
        const data = {  
          ...this.chartForm,  
          chartConfig: {  
            dimensionFields: JSON.stringify(this.chartConfig.dimensionFields),  
            measureFields: JSON.stringify(this.chartConfig.measureFields),  
            filterCondition: JSON.stringify(this.chartConfig.filterCondition),  
            sortConfig: JSON.stringify(this.chartConfig.sortConfig),  
            styleConfig: JSON.stringify(this.chartConfig.styleConfig),  
            dataLimit: this.chartConfig.dataLimit,  
            refreshInterval: this.chartConfig.refreshInterval  
          }  
        }  
          
        this.loading = true  
        saveChart(data).then(response => {  
          this.$message.success('保存成功')  
          this.loading = false  
          // 如果是新建，保存后跳转到编辑模式  
          if (!this.chartForm.chartId) {  
            this.$router.push({ name: 'ChartDesign', params: { chartId: response.data } })  
          }  
        }).catch(() => {  
          this.loading = false  
        })  
      })  
    },  
      
    // 预览图表  
    handlePreview() {  
      this.previewVisible = true  
      this.$nextTick(() => {  
        this.renderChart()  
      })  
    },  
      
    // 渲染图表  
    renderChart() {  
      // 销毁旧图表实例  
      if (this.chart) {  
        this.chart.dispose()  
      }  
        
      // 创建新图表实例  
      const chartDom = document.querySelector('.chart-preview')  
      this.chart = echarts.init(chartDom)  
        
      // 获取图表数据  
      this.loading = true  
        
      // 构建查询参数  
      const params = {  
        metadataId: this.chartForm.metadataId,  
        chartType: this.chartForm.chartType,  
        dimensionFields: this.chartConfig.dimensionFields,  
        measureFields: this.chartConfig.measureFields,  
        filterCondition: this.chartConfig.filterCondition,  
        sortConfig: this.chartConfig.sortConfig,  
        dataLimit: this.chartConfig.dataLimit  
      }  
        
      getChartData(params).then(response => {  
        const chartData = response.data  
          
        // 根据图表类型构建不同的配置  
        let option = {}  
          
        switch (this.chartForm.chartType) {  
          case 'bar':  
            option = this.buildBarChartOption(chartData)  
            break  
          case 'line':  
            option = this.buildLineChartOption(chartData)  
            break  
          case 'pie':  
            option = this.buildPieChartOption(chartData)  
            break  
          case 'radar':  
            option = this.buildRadarChartOption(chartData)  
            break  
          case 'scatter':  
            option = this.buildScatterChartOption(chartData)  
            break  
          case 'funnel':  
            option = this.buildFunnelChartOption(chartData)  
            break  
          default:  
            option = this.buildBarChartOption(chartData)  
        }  
          
        // 设置图表选项  
        this.chart.setOption(option)  
        this.loading = false  
      }).catch(() => {  
        this.loading = false  
      })  
    },  
      
    // 构建柱状图配置  
    buildBarChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.chartConfig.dimensionFields[0]  
        
      // 构建X轴数据  
      const xAxisData = dimensions.map(item => item[dimensionField.fieldName])  
        
      // 构建系列数据  
      const series = this.chartConfig.measureFields.map(measure => {  
        return {  
          name: measure.displayName,  
          type: 'bar',  
          data: measures.map(item => item[measure.fieldName])  
        }  
      })  
        
      // 构建图表配置  
      return {  
        title: this.chartConfig.styleConfig.title,  
        tooltip: {  
          trigger: 'axis',  
          axisPointer: {  
            type: 'shadow'  
          }  
        },  
        legend: {  
          data: this.chartConfig.measureFields.map(item => item.displayName),  
          ...this.getPositionConfig(this.chartConfig.styleConfig.legend.position)  
        },  
        grid: {  
          left: '3%',  
          right: '4%',  
          bottom: '3%',  
          containLabel: true  
        },  
        xAxis: {  
          type: 'category',  
          data: xAxisData,  
          name: this.chartConfig.styleConfig.xAxis.name  
        },  
        yAxis: {  
          type: 'value',  
          name: this.chartConfig.styleConfig.yAxis.name  
        },  
        series: series  
      }  
    },  
      
    // 构建折线图配置  
    buildLineChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.chartConfig.dimensionFields[0]  
        
      // 构建X轴数据  
      const xAxisData = dimensions.map(item => item[dimensionField.fieldName])  
        
      // 构建系列数据  
      const series = this.chartConfig.measureFields.map(measure => {  
        return {  
          name: measure.displayName,  
          type: 'line',  
          data: measures.map(item => item[measure.fieldName])  
        }  
      })  
        
      // 构建图表配置  
      return {  
        title: this.chartConfig.styleConfig.title,  
        tooltip: {  
          trigger: 'axis'  
        },  
        legend: {  
          data: this.chartConfig.measureFields.map(item => item.displayName),  
          ...this.getPositionConfig(this.chartConfig.styleConfig.legend.position)  
        },  
        grid: {  
          left: '3%',  
          right: '4%',  
          bottom: '3%',  
          containLabel: true  
        },  
        xAxis: {  
          type: 'category',  
          data: xAxisData,  
          name: this.chartConfig.styleConfig.xAxis.name  
        },  
        yAxis: {  
          type: 'value',  
          name: this.chartConfig.styleConfig.yAxis.name  
        },  
        series: series  
      }  
    },  
      
    // 构建饼图配置  
    buildPieChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.chartConfig.dimensionFields[0]  
      const measureField = this.chartConfig.measureFields[0]  
        
      // 构建系列数据  
      const data = dimensions.map((item, index) => {  
        return {  
          name: item[dimensionField.fieldName],  
          value: measures[index][measureField.fieldName]  
        }  
      })  
        
      // 构建图表配置  
      return {  
        title: this.chartConfig.styleConfig.title,  
        tooltip: {  
          trigger: 'item',  
          formatter: '{a} <br/>{b}: {c} ({d}%)'  
        },  
        legend: {  
          data: dimensions.map(item => item[dimensionField.fieldName]),  
          ...this.getPositionConfig(this.chartConfig.styleConfig.legend.position)  
        },  
        series: [  
          {  
            name: measureField.displayName,  
            type: 'pie',  
            radius: '55%',  
            center: ['50%', '50%'],  
            data: data,  
            emphasis: {  
              itemStyle: {  
                shadowBlur: 10,  
                shadowOffsetX: 0,  
                shadowColor: 'rgba(0, 0, 0, 0.5)'  
              }  
            }  
          }  
        ]  
      }  
    },  
      
    // 构建雷达图配置  
    buildRadarChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.chartConfig.dimensionFields[0]  
        
      // 构建雷达图指示器  
      const indicator = this.chartConfig.measureFields.map(measure => {  
        // 计算最大值  
        const max = Math.max(...measures.map(item => item[measure.fieldName])) * 1.2  
        return {  
          name: measure.displayName,  
          max: max  
        }  
      })  
        
      // 构建系列数据  
      const series = dimensions.map((dimension, index) => {  
        return {  
          value: this.chartConfig.measureFields.map(measure => measures[index][measure.fieldName]),  
          name: dimension[dimensionField.fieldName]  
        }  
      })  
        
      // 构建图表配置  
      return {  
        title: this.chartConfig.styleConfig.title,  
        tooltip: {  
          trigger: 'item'  
        },  
        legend: {  
          data: dimensions.map(item => item[dimensionField.fieldName]),  
          ...this.getPositionConfig(this.chartConfig.styleConfig.legend.position)  
        },  
        radar: {  
          indicator: indicator  
        },  
        series: [  
          {  
            type: 'radar',  
            data: series  
          }  
        ]  
      }  
    },  

    // 构建散点图配置  
    buildScatterChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.chartConfig.dimensionFields[0]  
      const measureField = this.chartConfig.measureFields[0]  
      const secondMeasureField = this.chartConfig.measureFields.length > 1   
        ? this.chartConfig.measureFields[1]   
        : this.chartConfig.measureFields[0]  
        
      // 构建系列数据  
      const data = dimensions.map((item, index) => {  
        return [            measures[index][measureField.fieldName],  
          measures[index][secondMeasureField.fieldName],  
          item[dimensionField.fieldName]  
        ]  
      })  
        
      // 构建图表配置  
      return {  
        title: this.chartConfig.styleConfig.title,  
        tooltip: {  
          trigger: 'item',  
          formatter: function(params) {  
            return params.data[2] + '<br/>' +  
              measureField.displayName + ': ' + params.data[0] + '<br/>' +  
              secondMeasureField.displayName + ': ' + params.data[1]  
          }  
        },  
        xAxis: {  
          type: 'value',  
          name: measureField.displayName,  
          nameLocation: 'end'  
        },  
        yAxis: {  
          type: 'value',  
          name: secondMeasureField.displayName,  
          nameLocation: 'end'  
        },  
        series: [            {              type: 'scatter',              data: data,              symbolSize: 10            }          ]  
      }  
    },  
      
    // 构建漏斗图配置  
    buildFunnelChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.chartConfig.dimensionFields[0]  
      const measureField = this.chartConfig.measureFields[0]  
        
      // 构建系列数据  
      const data = dimensions.map((item, index) => {  
        return {  
          name: item[dimensionField.fieldName],  
          value: measures[index][measureField.fieldName]  
        }  
      })  
        
      // 按值排序  
      data.sort((a, b) => b.value - a.value)  
        
      // 构建图表配置  
      return {  
        title: this.chartConfig.styleConfig.title,  
        tooltip: {  
          trigger: 'item',  
          formatter: '{a} <br/>{b}: {c}'  
        },  
        legend: {  
          data: dimensions.map(item => item[dimensionField.fieldName]),  
          ...this.getPositionConfig(this.chartConfig.styleConfig.legend.position)  
        },  
        series: [            {              name: measureField.displayName,              type: 'funnel',              left: '10%',              top: 60,              bottom: 60,              width: '80%',              min: 0,              max: Math.max(...data.map(item => item.value)) * 1.2,              minSize: '0%',              maxSize: '100%',              sort: 'descending',              gap: 2,              label: {                show: true,                position: 'inside'              },              emphasis: {                label: {                  fontSize: 20                }              },              data: data            }          ]  
      }  
    },  
      
    // 根据位置获取配置  
    getPositionConfig(position) {  
      switch (position) {  
        case 'top':  
          return { top: 'top', left: 'center' }  
        case 'bottom':  
          return { bottom: 'bottom', left: 'center' }  
        case 'left':  
          return { left: 'left', top: 'middle' }  
        case 'right':  
          return { right: 'right', top: 'middle' }  
        default:  
          return { bottom: 'bottom', left: 'center' }  
      }  
    },  
      
    // 导出图表  
    handleExport() {  
      if (!this.chart) {  
        this.$message.error('图表未渲染')  
        return  
      }  
        
      // 获取图表的base64数据URL  
      const dataURL = this.chart.getDataURL({  
        type: 'png',  
        pixelRatio: 2,  
        backgroundColor: '#fff'  
      })  
        
      // 创建下载链接  
      const link = document.createElement('a')  
      link.download = this.chartForm.chartName + '.png'  
      link.href = dataURL  
      document.body.appendChild(link)  
      link.click()  
      document.body.removeChild(link)  
        
      this.$message.success('导出成功')  
    }  
  }  
}  
</script>  
  
<style scoped>  
.measure-config {  
  margin-bottom: 15px;  
  padding: 10px;  
  border: 1px dashed #ccc;  
  border-radius: 4px;  
}  
  
.filter-condition {  
  margin-bottom: 15px;  
  padding: 10px;  
  border: 1px dashed #ccc;  
  border-radius: 4px;  
}  
  
.chart-preview {  
  width: 100%;  
  height: 400px;  
}  
</style>