<template>  
  <div class="chart-designer-container">  
    <el-container>  
      <el-aside width="300px">  
        <el-card class="config-panel">  
          <div slot="header">  
            <span>图表配置</span>  
          </div>  
          <el-form ref="chartForm" :model="chartForm" :rules="rules" label-width="100px" size="small">  
            <el-form-item label="图表名称" prop="chartName">  
              <el-input v-model="chartForm.chartName" placeholder="请输入图表名称" />  
            </el-form-item>  
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
            <el-form-item label="图表描述">  
              <el-input v-model="chartForm.chartDesc" type="textarea" rows="3" placeholder="请输入图表描述" />  
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
              
            <el-divider content-position="left">维度与度量</el-divider>  
              
            <el-form-item label="维度字段" prop="dimensions">  
              <el-select  
                v-model="selectedDimensions"  
                multiple  
                collapse-tags  
                placeholder="请选择维度字段"  
                style="width: 100%"  
                @change="handleDimensionChange"  
              >  
                <el-option  
                  v-for="field in fieldOptions"  
                  :key="field.fieldId"  
                  :label="field.fieldLabel"  
                  :value="field.fieldId"  
                />  
              </el-select>  
            </el-form-item>  
              
            <el-form-item label="度量字段" prop="measures">  
              <el-select  
                v-model="selectedMeasures"  
                multiple  
                collapse-tags  
                placeholder="请选择度量字段"  
                style="width: 100%"  
                @change="handleMeasureChange"  
              >  
                <el-option  
                  v-for="field in fieldOptions"  
                  :key="field.fieldId"  
                  :label="field.fieldLabel"  
                  :value="field.fieldId"  
                />  
              </el-select>  
            </el-form-item>  
              
            <template v-if="chartConfig.measureFields.length > 0">  
              <el-divider content-position="left">度量配置</el-divider>  
              <div v-for="(measure, index) in chartConfig.measureFields" :key="index" class="measure-config">  
                <el-form-item :label="measure.fieldLabel">  
                  <el-select v-model="measure.aggregation" style="width: 100%">  
                    <el-option label="求和" value="SUM" />  
                    <el-option label="平均值" value="AVG" />  
                    <el-option label="最大值" value="MAX" />  
                    <el-option label="最小值" value="MIN" />  
                    <el-option label="计数" value="COUNT" />  
                  </el-select>  
                </el-form-item>  
              </div>  
            </template>  
              
            <el-divider content-position="left">数据筛选</el-divider>  
              
            <div v-for="(filter, index) in chartConfig.filterCondition" :key="'filter-'+index" class="filter-condition">  
              <el-row :gutter="10">  
                <el-col :span="8">  
                  <el-select v-model="filter.fieldId" placeholder="字段" style="width: 100%">  
                    <el-option  
                      v-for="field in fieldOptions"  
                      :key="field.fieldId"  
                      :label="field.fieldLabel"  
                      :value="field.fieldId"  
                    />  
                  </el-select>  
                </el-col>  
                <el-col :span="6">  
                  <el-select v-model="filter.operator" placeholder="操作符" style="width: 100%">  
                    <el-option label="等于" value="eq" />  
                    <el-option label="不等于" value="ne" />  
                    <el-option label="大于" value="gt" />  
                    <el-option label="小于" value="lt" />  
                    <el-option label="大于等于" value="ge" />  
                    <el-option label="小于等于" value="le" />  
                    <el-option label="包含" value="contains" />  
                  </el-select>  
                </el-col>  
                <el-col :span="6">  
                  <el-input v-model="filter.value" placeholder="值" />  
                </el-col>  
                <el-col :span="4">  
                  <el-button type="danger" icon="el-icon-delete" circle @click="removeFilter(index)" />  
                </el-col>  
              </el-row>  
            </div>  
              
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="addFilter" style="margin-top: 10px;">添加筛选条件</el-button>  
              
            <el-divider content-position="left">样式配置</el-divider>  
              
            <el-collapse v-model="activeStylePanel">  
              <el-collapse-item title="基础配置" name="basic">  
                <el-form-item label="标题">  
                  <el-input v-model="chartConfig.styleConfig.title.text" placeholder="图表标题" />  
                </el-form-item>  
                <el-form-item label="副标题">  
                  <el-input v-model="chartConfig.styleConfig.title.subtext" placeholder="图表副标题" />  
                </el-form-item>  
                <el-form-item label="标题位置">  
                  <el-select v-model="chartConfig.styleConfig.title.left" style="width: 100%">  
                    <el-option label="居左" value="left" />  
                    <el-option label="居中" value="center" />  
                    <el-option label="居右" value="right" />  
                  </el-select>  
                </el-form-item>  
              </el-collapse-item>  
                
              <el-collapse-item title="图例配置" name="legend">  
                <el-form-item label="显示图例">  
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
              </el-collapse-item>  
                
              <el-collapse-item title="坐标轴配置" name="axis" v-if="['bar', 'line', 'scatter'].includes(chartForm.chartType)">  
                <el-form-item label="X轴名称">  
                  <el-input v-model="chartConfig.styleConfig.xAxis.name" placeholder="X轴名称" />  
                </el-form-item>  
                <el-form-item label="Y轴名称">  
                  <el-input v-model="chartConfig.styleConfig.yAxis.name" placeholder="Y轴名称" />  
                </el-form-item>  
              </el-collapse-item>  
            </el-collapse>  
              
            <el-form-item label="数据限制">  
              <el-input-number v-model="chartConfig.dataLimit" :min="1" :max="10000" />  
            </el-form-item>  
              
            <el-form-item label="刷新间隔">  
              <el-input-number v-model="chartConfig.refreshInterval" :min="0" :max="3600" />  
              <span class="unit">秒</span>  
            </el-form-item>  
              
            <el-form-item>  
              <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>  
              <el-button @click="handlePreview">预览</el-button>  
            </el-form-item>  
          </el-form>  
        </el-card>  
      </el-aside>  
        
      <el-main>  
        <el-card class="chart-preview-panel">  
          <div slot="header">  
            <span>图表预览</span>  
          </div>  
          <div class="chart-container" ref="chartContainer"></div>  
        </el-card>  
      </el-main>  
    </el-container>  
      
    <!-- 预览对话框 -->  
    <el-dialog title="图表预览" :visible.sync="previewVisible" width="80%" append-to-body>  
      <div class="preview-chart-container" ref="previewChartContainer"></div>  
      <span slot="footer" class="dialog-footer">  
        <el-button @click="previewVisible = false">关闭</el-button>  
        <el-button type="primary" @click="handleExport">导出图表</el-button>  
      </span>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { getMetadataList, getMetadataFields } from '@/api/metadata/form'  
import { saveChart, getChart, getChartData } from '@/api/metadata/chart'  
import echarts from 'echarts'  
require('echarts/theme/macarons')  
  
export default {  
  name: 'ChartDesigner',  
  props: {  
    chartId: {  
      type: String,  
      default: ''  
    }  
  },  
  data() {  
    return {  
      loading: false,  
      // 图表基本信息  
      chartForm: {  
        chartId: '',  
        chartName: '',  
        chartDesc: '',  
        chartType: 'bar',  
        metadataId: '',  
        status: 0  
      },  
      // 表单验证规则  
      rules: {  
        chartName: [  
          { required: true, message: '请输入图表名称', trigger: 'blur' }  
        ],  
        chartType: [  
          { required: true, message: '请选择图表类型', trigger: 'change' }  
        ],  
        metadataId: [  
          { required: true, message: '请选择数据源', trigger: 'change' }  
        ],  
        dimensions: [  
          { required: true, message: '请至少选择一个维度字段', trigger: 'change' }  
        ],  
        measures: [  
          { required: true, message: '请至少选择一个度量字段', trigger: 'change' }  
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
      // 样式配置面板  
      activeStylePanel: ['basic'],  
      // 预览对话框可见性  
      previewVisible: false,  
      // 图表实例  
      chart: null,  
      previewChart: null  
    }  
  },  
  created() {  
    this.getMetadataOptions()  
      
    // 如果有chartId，则加载图表数据  
    if (this.chartId) {  
      this.loadChartData()  
    }  
  },  
  mounted() {  
    this.$nextTick(() => {  
      window.addEventListener('resize', this.resizeChart)  
    })  
  },  
  beforeDestroy() {  
    window.removeEventListener('resize', this.resizeChart)  
    if (this.chart) {  
      this.chart.dispose()  
      this.chart = null  
    }  
    if (this.previewChart) {  
      this.previewChart.dispose()  
      this.previewChart = null  
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
    loadChartData() {  
      this.loading = true  
      getChart(this.chartId).then(response => {  
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
          
        // 渲染图表  
        this.$nextTick(() => {  
          this.renderChart()  
        })  
          
        this.loading = false  
      }).catch(() => {  
        this.loading = false  
      })  
    },  
      
    // 处理元数据变更  
    handleMetadataChange(metadataId) {  
      if (!metadataId) return  
        
      this.loading = true  
      getMetadataFields(metadataId).then(response => {  
        const fields = response.data  
        this.fieldOptions = fields.map(field => ({  
          fieldId: field.fieldId,  
          fieldName: field.fieldName,  
          fieldLabel: field.fieldLabel,  
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
          const field = this.fieldOptions.find(item => item.fieldId === fieldId)  
          if (field) {  
            this.chartConfig.dimensionFields.push({  
              fieldId: field.fieldId,  
              fieldName: field.fieldName,  
              displayName: field.fieldLabel  
            })  
          }  
        }  
      })  
        
      // 更新图表  
      this.renderChart()  
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
          const field = this.fieldOptions.find(item => item.fieldId === fieldId)  
          if (field) {  
            this.chartConfig.measureFields.push({  
              fieldId: field.fieldId,  
              fieldName: field.fieldName,  
              fieldLabel: field.fieldLabel,  
              displayName: field.fieldLabel,  
              aggregation: 'SUM'  
            })  
          }  
        }  
      })  
        
      // 更新图表  
      this.renderChart()  
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
        
      // 更新图表  
      this.renderChart()  
    },  
      
    // 调整图表大小  
    resizeChart() {  
      if (this.chart) {  
        this.chart.resize()  
      }  
    },  
      
    // 渲染图表  
    renderChart() {  
      if (!this.chartForm.metadataId ||   
          this.chartConfig.dimensionFields.length === 0 ||   
          this.chartConfig.measureFields.length === 0) {  
        return  
      }  
        
      // 销毁旧图表实例  
      if (this.chart) {  
        this.chart.dispose()  
      }  
        
      // 创建新图表实例  
      const chartDom = this.$refs.chartContainer  
      this.chart = echarts.init(chartDom, 'macarons')  
      this.chart.showLoading()  
        
      // 获取图表数据  
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
        this.chart.hideLoading()  
        this.chart.setOption(option)  
      }).catch(() => {  
        this.chart.hideLoading()  
        this.$message.error('获取图表数据失败')  
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
        series: [            {              name: measureField.displayName,              type: 'pie',              radius: '55%',              center: ['50%', '50%'],  
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
        series: [            {              type: 'radar',              data: series            }          ]  
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
        series: [  
          {  
            name: measureField.displayName,  
            type: 'funnel',  
            left: '10%',  
            top: 60,  
            bottom: 60,  
            width: '80%',  
            min: 0,  
            max: Math.max(...data.map(item => item.value)) * 1.2,  
            minSize: '0%',  
            maxSize: '100%',  
            sort: 'descending',  
            gap: 2,  
            label: {  
              show: true,  
              position: 'inside'  
            },  
            emphasis: {  
              label: {  
                fontSize: 20  
              }  
            },  
            data: data  
          }  
        ]  
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
      
    // 预览图表  
    handlePreview() {  
      this.previewVisible = true  
      this.$nextTick(() => {  
        // 销毁旧图表实例  
        if (this.previewChart) {  
          this.previewChart.dispose()  
        }  
          
        // 创建新图表实例  
        const chartDom = this.$refs.previewChartContainer  
        this.previewChart = echarts.init(chartDom, 'macarons')  
          
        // 复制当前图表的配置  
        if (this.chart) {  
          const option = this.chart.getOption()  
          this.previewChart.setOption(option)  
        }  
      })  
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
    }  
  }  
}  
</script>  
  
<style scoped>  
.chart-designer-container {  
  height: 100%;  
}  
  
.el-container {  
  height: 100%;  
}  
  
.el-aside {  
  background-color: #f5f7fa;  
  overflow-y: auto;  
}  
  
.el-main {  
  padding: 15px;  
  overflow-y: auto;  
}  
  
.config-panel {  
  height: 100%;  
  overflow-y: auto;  
}  
  
.chart-preview-panel {  
  height: 100%;  
}  
  
.chart-container {  
  height: 500px;  
  margin-top: 20px;  
}  
  
.preview-chart-container {  
  height: 500px;  
}  
  
.measure-config {  
  margin-bottom: 10px;  
  padding: 10px;  
  border: 1px dashed #dcdfe6;  
  border-radius: 4px;  
}  
  
.filter-condition {  
  margin-bottom: 10px;  
}  
  
.unit {  
  margin-left: 5px;  
  color: #909399;  
}  
</style>