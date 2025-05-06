<template>  
  <div class="app-container">  
    <el-card class="box-card">  
      <div slot="header" class="clearfix">  
        <span>{{ chartData.chartName }}</span>  
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleExport">导出图表</el-button>  
        <el-button style="float: right; padding: 3px 0; margin-right: 10px;" type="text" @click="handleRefresh">刷新数据</el-button>  
      </div>  
        
      <div class="chart-container">  
        <div class="chart-wrapper" ref="chartRef"></div>  
      </div>  
        
      <el-divider content-position="left">图表信息</el-divider>  
        
      <el-descriptions :column="2" border>  
        <el-descriptions-item label="图表名称">{{ chartData.chartName }}</el-descriptions-item>  
        <el-descriptions-item label="图表类型">{{ chartTypeText }}</el-descriptions-item>  
        <el-descriptions-item label="数据源">{{ metadataName }}</el-descriptions-item>  
        <el-descriptions-item label="创建者">{{ chartData.createBy }}</el-descriptions-item>  
        <el-descriptions-item label="创建时间">{{ chartData.createTime }}</el-descriptions-item>  
        <el-descriptions-item label="更新时间">{{ chartData.updateTime }}</el-descriptions-item>  
        <el-descriptions-item label="图表描述" :span="2">{{ chartData.chartDesc }}</el-descriptions-item>  
      </el-descriptions>  
        
      <el-divider content-position="left">数据配置</el-divider>  
        
      <el-row :gutter="20">  
        <el-col :span="12">  
          <el-card class="inner-card">  
            <div slot="header">维度字段</div>  
            <el-table :data="dimensionFields" border stripe size="small">  
              <el-table-column prop="fieldName" label="字段名称" />  
              <el-table-column prop="displayName" label="显示名称" />  
            </el-table>  
          </el-card>  
        </el-col>  
        <el-col :span="12">  
          <el-card class="inner-card">  
            <div slot="header">度量字段</div>  
            <el-table :data="measureFields" border stripe size="small">  
              <el-table-column prop="fieldName" label="字段名称" />  
              <el-table-column prop="displayName" label="显示名称" />  
              <el-table-column prop="aggregation" label="聚合方式" />  
            </el-table>  
          </el-card>  
        </el-col>  
      </el-row>  
        
      <el-divider content-position="left">筛选条件</el-divider>  
        
      <el-table :data="filterCondition" border stripe size="small" v-if="filterCondition.length > 0">  
        <el-table-column prop="fieldName" label="字段" />  
        <el-table-column prop="operator" label="操作符">  
          <template slot-scope="scope">  
            {{ getOperatorText(scope.row.operator) }}  
          </template>  
        </el-table-column>  
        <el-table-column prop="value" label="值" />  
        <el-table-column prop="logicType" label="逻辑" />  
      </el-table>  
      <el-empty description="暂无筛选条件" v-else></el-empty>  
    </el-card>  
  </div>  
</template>  
  
<script>  
import { getChart, getChartData } from '@/api/metadata/chart'  
import { getMetadata } from '@/api/metadata/form'  
import echarts from 'echarts'  
require('echarts/theme/macarons')  
  
export default {  
  name: 'ChartPreview',  
  data() {  
    return {  
      loading: false,  
      chartId: null,  
      chartData: {  
        chartId: '',  
        chartName: '',  
        chartDesc: '',  
        chartType: '',  
        metadataId: '',  
        status: 0,  
        createBy: '',  
        createTime: '',  
        updateTime: '',
        chartConfig: {}
      },  
      metadataName: '',  
      dimensionFields: [],  
      measureFields: [],  
      filterCondition: [],  
      chartInstance: null,  
      refreshTimer: null  
    }  
  },  
  computed: {  
    chartTypeText() {  
      const chartTypeMap = {  
        'bar': '柱状图',  
        'line': '折线图',  
        'pie': '饼图',  
        'radar': '雷达图',  
        'scatter': '散点图',  
        'funnel': '漏斗图'  
      }  
      return chartTypeMap[this.chartData.chartType] || this.chartData.chartType  
    }  
  },  
  created() {  
    this.chartId = this.$route.params.chartId  
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
    if (this.chartInstance) {  
      this.chartInstance.dispose()  
      this.chartInstance = null  
    }  
    if (this.refreshTimer) {  
      clearInterval(this.refreshTimer)  
      this.refreshTimer = null  
    }  
  },  
  methods: {  
    // 加载图表数据  
    loadChartData() {  
      this.loading = true  
      getChart(this.chartId).then(response => {  
        this.chartData = response.data  
          
        // 获取元数据名称  
        this.getMetadataName(this.chartData.metadataId)  
          
        // 解析图表配置  
        if (this.chartData.chartConfig) {  
          this.dimensionFields = JSON.parse(this.chartData.chartConfig.dimensionFields || '[]')  
          this.measureFields = JSON.parse(this.chartData.chartConfig.measureFields || '[]')  
          this.filterCondition = JSON.parse(this.chartData.chartConfig.filterCondition || '[]')  
            
          // 渲染图表  
          this.renderChart()  
            
          // 设置自动刷新  
          const refreshInterval = this.chartData.chartConfig.refreshInterval || 0  
          if (refreshInterval > 0) {  
            this.refreshTimer = setInterval(() => {  
              this.renderChart()  
            }, refreshInterval * 1000)  
          }  
        }  
          
        this.loading = false  
      }).catch(() => {  
        this.loading = false  
      })  
    },  
      
    // 获取元数据名称  
    getMetadataName(metadataId) {  
      if (!metadataId) return  
        
      getMetadata(metadataId).then(response => {  
        this.metadataName = response.data.metadataName  
      })  
    },  
      
    // 渲染图表  
    renderChart() {  
      // 销毁旧图表实例  
      if (this.chartInstance) {  
        this.chartInstance.dispose()  
      }  
        
      // 创建新图表实例  
      const chartDom = this.$refs.chartRef  
      this.chartInstance = echarts.init(chartDom, 'macarons')  
      this.chartInstance.showLoading()  
        
      // 获取图表数据  
      const params = {  
        metadataId: this.chartData.metadataId,  
        chartType: this.chartData.chartType,  
        dimensionFields: this.dimensionFields,  
        measureFields: this.measureFields,  
        filterCondition: this.filterCondition,  
        dataLimit: this.chartData?.chartConfig?.dataLimit || 1000  
      }  
        
      getChartData(params).then(response => {  
        const chartData = response.data  
          
        // 根据图表类型构建不同的配置  
        let option = {}  
          
        switch (this.chartData.chartType) {  
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
        this.chartInstance.hideLoading()  
        this.chartInstance.setOption(option)  
      }).catch(() => {  
        this.chartInstance.hideLoading()  
        this.$message.error('获取图表数据失败')  
      })  
    },  
      
    // 构建柱状图配置  
    buildBarChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.dimensionFields[0]  
        
      // 构建X轴数据  
      const xAxisData = dimensions.map(item => item[dimensionField.fieldName])  
        
      // 构建系列数据  
      const series = this.measureFields.map(measure => {  
        return {  
          name: measure.displayName,  
          type: 'bar',  
          data: measures.map(item => item[measure.fieldName])  
        }  
      })  
        
      // 构建图表配置  
      const styleConfig = JSON.parse(this.chartData.chartConfig.styleConfig || '{}')  
      return {  
        title: styleConfig.title || { text: this.chartData.chartName },  
        tooltip: {  
          trigger: 'axis',  
          axisPointer: {  
            type: 'shadow'  
          }  
        },  
        legend: {  
          data: this.measureFields.map(item => item.displayName),  
          ...this.getPositionConfig(styleConfig.legend?.position)  
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
          name: styleConfig.xAxis?.name || ''  
        },  
        yAxis: {  
          type: 'value',  
          name: styleConfig.yAxis?.name || ''  
        },  
        series: series  
      }  
    },  
      
    // 构建折线图配置  
    buildLineChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.dimensionFields[0]  
        
      // 构建X轴数据  
      const xAxisData = dimensions.map(item => item[dimensionField.fieldName])  
        
      // 构建系列数据  
      const series = this.measureFields.map(measure => {  
        return {  
          name: measure.displayName,  
          type: 'line',  
          data: measures.map(item => item[measure.fieldName])  
        }  
      })  
        
      // 构建图表配置  
      const styleConfig = JSON.parse(this.chartData.chartConfig.styleConfig || '{}')  
      return {  
        title: styleConfig.title || { text: this.chartData.chartName },  
        tooltip: {  
          trigger: 'axis'  
        },  
        legend: {  
          data: this.measureFields.map(item => item.displayName),  
          ...this.getPositionConfig(styleConfig.legend?.position)  
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
          name: styleConfig.xAxis?.name || ''  
        },  
        yAxis: {  
          type: 'value',  
          name: styleConfig.yAxis?.name || ''  
        },  
        series: series  
      }  
    },  
      
    // 构建饼图配置  
    buildPieChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.dimensionFields[0]  
      const measureField = this.measureFields[0]  
        
      // 构建系列数据  
      const data = dimensions.map((item, index) => {  
        return {  
          name: item[dimensionField.fieldName],  
          value: measures[index][measureField.fieldName]  
        }  
      })  
        
      // 构建图表配置  
      const styleConfig = JSON.parse(this.chartData.chartConfig.styleConfig || '{}')  
      return {  
        title: styleConfig.title || { text: this.chartData.chartName },  
        tooltip: {  
          trigger: 'item',  
          formatter: '{a} <br/>{b}: {c} ({d}%)'  
        },  
        legend: {  
          data: dimensions.map(item => item[dimensionField.fieldName]),  
          ...this.getPositionConfig(styleConfig.legend?.position)  
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
      const dimensionField = this.dimensionFields[0]  
        
      // 构建雷达图指示器  
      const indicator = this.measureFields.map(measure => {  
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
          value: this.measureFields.map(measure => measures[index][measure.fieldName]),  
          name: dimension[dimensionField.fieldName]  
        }  
      })  
        
      // 构建图表配置  
      const styleConfig = JSON.parse(this.chartData.chartConfig.styleConfig || '{}')  
      return {  
        title: styleConfig.title || { text: this.chartData.chartName },
        tooltip: {  
          trigger: 'item'  
        },  
        legend: {  
          data: dimensions.map(item => item[dimensionField.fieldName]),  
          ...this.getPositionConfig(styleConfig.legend?.position)  
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
      const dimensionField = this.dimensionFields[0]  
      const measureField = this.measureFields[0]  
      const secondMeasureField = this.measureFields.length > 1   
        ? this.measureFields[1]   
        : this.measureFields[0]  
        
      // 构建系列数据  
      const data = dimensions.map((item, index) => {  
        return [  
          measures[index][measureField.fieldName],  
          measures[index][secondMeasureField.fieldName],  
          item[dimensionField.fieldName]  
        ]  
      })  
        
      // 构建图表配置  
      const styleConfig = JSON.parse(this.chartData.chartConfig.styleConfig || '{}')  
      return {  
        title: styleConfig.title || { text: this.chartData.chartName },  
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
        series: [  
          {  
            type: 'scatter',  
            data: data,  
            symbolSize: 10  
          }  
        ]  
      }  
    },  
      
    // 构建漏斗图配置  
    buildFunnelChartOption(chartData) {  
      const { dimensions, measures } = chartData  
      const dimensionField = this.dimensionFields[0]  
      const measureField = this.measureFields[0]  
        
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
      const styleConfig = JSON.parse(this.chartData.chartConfig.styleConfig || '{}')  
      return {  
        title: styleConfig.title || { text: this.chartData.chartName },  
        tooltip: {  
          trigger: 'item',  
          formatter: '{a} <br/>{b}: {c}'  
        },  
        legend: {  
          data: dimensions.map(item => item[dimensionField.fieldName]),  
          ...this.getPositionConfig(styleConfig.legend?.position)  
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
      
    // 获取操作符文本  
    getOperatorText(operator) {  
      const operatorMap = {  
        'eq': '等于',  
        'ne': '不等于',  
        'gt': '大于',  
        'lt': '小于',  
        'ge': '大于等于',  
        'le': '小于等于',  
        'contains': '包含',  
        'startswith': '开始于',  
        'endswith': '结束于'  
      }  
      return operatorMap[operator] || operator  
    },  
      
    // 刷新图表数据  
    handleRefresh() {  
      this.renderChart()  
      this.$message.success('数据已刷新')  
    },  
      
    // 导出图表  
    handleExport() {  
      if (!this.chartInstance) {  
        this.$message.error('图表未渲染')  
        return  
      }  
        
      // 获取图表的base64数据URL  
      const dataURL = this.chartInstance.getDataURL({  
        type: 'png',  
        pixelRatio: 2,  
        backgroundColor: '#fff'  
      })  
        
      // 创建下载链接  
      const link = document.createElement('a')  
      link.download = this.chartData.chartName + '.png'  
      link.href = dataURL  
      document.body.appendChild(link)  
      link.click()  
      document.body.removeChild(link)  
        
      this.$message.success('导出成功')  
    },  
      
    // 调整图表大小  
    resizeChart() {  
      if (this.chartInstance) {  
        this.chartInstance.resize()  
      }  
    }  
  }  
}  
</script>  
  
<style scoped>  
.chart-container {  
  position: relative;  
  width: 100%;  
  height: 500px;  
  margin-bottom: 20px;  
}  
  
.chart-wrapper {  
  position: absolute;  
  width: 100%;  
  height: 100%;  
}  
  
.inner-card {  
  margin-bottom: 20px;  
}  
</style>