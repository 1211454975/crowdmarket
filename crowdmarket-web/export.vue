<template>  
  <div class="app-container">  
    <el-card class="box-card">  
      <div slot="header" class="clearfix">  
        <span>图表导出</span>  
      </div>  
        
      <el-form ref="exportForm" :model="exportForm" label-width="120px">  
        <el-form-item label="选择图表" prop="chartId">  
          <el-select v-model="exportForm.chartId" placeholder="请选择要导出的图表" style="width: 100%" @change="handleChartChange">  
            <el-option  
              v-for="item in chartOptions"  
              :key="item.chartId"  
              :label="item.chartName"  
              :value="item.chartId"  
            />  
          </el-select>  
        </el-form-item>  
          
        <el-form-item label="导出格式" prop="exportType">  
          <el-radio-group v-model="exportForm.exportType">  
            <el-radio label="png">PNG图片</el-radio>  
            <el-radio label="jpeg">JPEG图片</el-radio>  
            <el-radio label="pdf">PDF文档</el-radio>  
          </el-radio-group>  
        </el-form-item>  
          
        <el-form-item label="图片质量" prop="pixelRatio" v-if="exportForm.exportType !== 'pdf'">  
          <el-slider v-model="exportForm.pixelRatio" :min="1" :max="4" :step="0.5" :format-tooltip="formatPixelRatio"></el-slider>  
        </el-form-item>  
          
        <el-form-item label="背景颜色" prop="backgroundColor">  
          <el-color-picker v-model="exportForm.backgroundColor" show-alpha></el-color-picker>  
        </el-form-item>  
          
        <el-form-item label="图表尺寸" prop="size">  
          <el-row :gutter="10">  
            <el-col :span="11">  
              <el-input-number v-model="exportForm.width" :min="300" :max="3000" placeholder="宽度"></el-input-number>  
              <span class="unit">px</span>  
            </el-col>  
            <el-col :span="2" class="text-center">  
              <span>×</span>  
            </el-col>  
            <el-col :span="11">  
              <el-input-number v-model="exportForm.height" :min="200" :max="2000" placeholder="高度"></el-input-number>  
              <span class="unit">px</span>  
            </el-col>  
          </el-row>  
        </el-form-item>  
          
        <el-form-item label="文件名" prop="filename">  
          <el-input v-model="exportForm.filename" placeholder="请输入导出文件名"></el-input>  
        </el-form-item>  
          
        <el-form-item>  
          <el-button type="primary" @click="handleExport" :loading="exporting">导出图表</el-button>  
          <el-button @click="handlePreview">预览图表</el-button>  
        </el-form-item>  
      </el-form>  
        
      <el-divider content-position="left">图表预览</el-divider>  
        
      <div class="chart-container" :style="{ width: exportForm.width + 'px', height: exportForm.height + 'px', backgroundColor: exportForm.backgroundColor }">  
        <div class="chart-wrapper" ref="chartRef"></div>  
      </div>  
    </el-card>  
      
    <!-- PDF预览对话框 -->  
    <el-dialog title="PDF预览" :visible.sync="pdfPreviewVisible" width="80%" append-to-body>  
      <div class="pdf-container">  
        <iframe :src="pdfUrl" width="100%" height="500px" frameborder="0"></iframe>  
      </div>  
      <span slot="footer" class="dialog-footer">  
        <el-button @click="pdfPreviewVisible = false">关闭</el-button>  
        <el-button type="primary" @click="downloadPdf">下载PDF</el-button>  
      </span>  
    </el-dialog>  
  </div>  
</template>  
  
<script>  
import { getChartList, getChart, getChartData } from '@/api/metadata/chart.js'  
import { exportChartToPdf } from '@/api/metadata/export.js'  
import echarts from 'echarts'  
require('echarts/theme/macarons')  
import { saveAs } from 'file-saver'  
import { Loading } from 'element-ui'  
  
export default {  
  name: 'ChartExport',  
  data() {  
    return {  
      chartOptions: [],  
      exportForm: {  
        chartId: '',  
        exportType: 'png',  
        pixelRatio: 2,  
        backgroundColor: '#ffffff',  
        width: 800,  
        height: 600,  
        filename: ''  
      },  
      chartData: null,  
      chartInstance: null,  
      exporting: false,  
      pdfPreviewVisible: false,  
      pdfUrl: '',  
      dimensionFields: [],  
      measureFields: [],  
      filterCondition: []  
    }  
  },  
  created() {  
    this.getChartOptions()  
      
    // 如果从预览页面跳转过来，自动选择对应的图表  
    const chartId = this.$route.params.chartId  
    if (chartId) {  
      this.exportForm.chartId = chartId  
      this.handleChartChange(chartId)  
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
  },  
  methods: {  
    // 获取图表选项  
    getChartOptions() {  
      getChartList().then(response => {  
        this.chartOptions = response.data  
      })  
    },  
      
    // 处理图表变更  
    handleChartChange(chartId) {  
      if (!chartId) return  
        
      const loadingInstance = Loading.service({ target: '.chart-container' })  
        
      getChart(chartId).then(response => {  
        this.chartData = response.data  
          
        // 设置默认文件名  
        this.exportForm.filename = this.chartData.chartName  
          
        // 解析图表配置  
        if (this.chartData.chartConfig) {  
          this.dimensionFields = JSON.parse(this.chartData.chartConfig.dimensionFields || '[]')  
          this.measureFields = JSON.parse(this.chartData.chartConfig.measureFields || '[]')  
          this.filterCondition = JSON.parse(this.chartData.chartConfig.filterCondition || '[]')  
            
          // 渲染图表  
          this.renderChart()  
        }  
          
        loadingInstance.close()  
      }).catch(() => {  
        loadingInstance.close()  
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
        dataLimit: this.chartData.chartConfig?.dataLimit || 1000  
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
      
    // 格式化像素比例  
    formatPixelRatio(val) {  
      return `${val}x`  
    },  
      
    // 调整图表大小  
    resizeChart() {  
      if (this.chartInstance) {  
        this.chartInstance.resize()  
      }  
    },  
      
    // 预览图表  
    handlePreview() {  
      if (!this.exportForm.chartId) {  
        this.$message.warning('请先选择图表')  
        return  
      }  
        
      this.renderChart()  
    },  
      
    // 导出图表  
    handleExport() {  
      if (!this.exportForm.chartId) {  
        this.$message.warning('请先选择图表')  
        return  
      }  
        
      if (!this.exportForm.filename) {  
        this.$message.warning('请输入导出文件名')  
        return  
      }  
        
      this.exporting = true  
        
      // 根据导出类型执行不同的导出操作  
      switch (this.exportForm.exportType) {  
        case 'png':  
          this.exportToPng()  
          break  
        case 'jpeg':  
          this.exportToJpeg()  
          break  
        case 'pdf':  
          this.exportToPdf()  
          break  
        default:  
          this.exportToPng()  
      }  
    },  
      
    // 导出为PNG  
    exportToPng() {  
      if (!this.chartInstance) {  
        this.$message.error('图表未渲染')  
        this.exporting = false  
        return  
      }  
        
      try {  
        // 获取图表的base64数据URL  
        const dataURL = this.chartInstance.getDataURL({  
          type: 'png',  
          pixelRatio: this.exportForm.pixelRatio,  
          backgroundColor: this.exportForm.backgroundColor  
        })  
          
        // 将base64转换为Blob  
        const binary = atob(dataURL.split(',')[1])  
        const array = []  
        for (let i = 0; i < binary.length; i++) {  
          array.push(binary.charCodeAt(i))  
        }  
        const blob = new Blob([new Uint8Array(array)], { type: 'image/png' })  
          
        // 下载文件  
        saveAs(blob, `${this.exportForm.filename}.png`)  
          
        this.$message.success('导出成功')  
      } catch (error) {  
        this.$message.error('导出失败: ' + error.message)  
      } finally {  
        this.exporting = false  
      }  
    },  
      
    // 导出为JPEG  
    exportToJpeg() {  
      if (!this.chartInstance) {  
        this.$message.error('图表未渲染')  
        this.exporting = false  
        return  
      }  
        
      try {  
        // 获取图表的base64数据URL  
        const dataURL = this.chartInstance.getDataURL({  
          type: 'jpeg',  
          pixelRatio: this.exportForm.pixelRatio,  
          backgroundColor: this.exportForm.backgroundColor  
        })  
          
        // 将base64转换为Blob  
        const binary = atob(dataURL.split(',')[1])  
        const array = []  
        for (let i = 0; i < binary.length; i++) {  
          array.push(binary.charCodeAt(i))  
        }  
        const blob = new Blob([new Uint8Array(array)], { type: 'image/jpeg' })  
          
        // 下载文件  
        saveAs(blob, `${this.exportForm.filename}.jpg`)  
          
        this.$message.success('导出成功')  
      } catch (error) {  
        this.$message.error('导出失败: ' + error.message)  
      } finally {  
        this.exporting = false  
      }  
    },  
      
    // 导出为PDF  
    exportToPdf() {  
      if (!this.chartInstance) {  
        this.$message.error('图表未渲染')  
        this.exporting = false  
        return  
      }  
        
      try {  
        // 获取图表的base64数据URL  
        const dataURL = this.chartInstance.getDataURL({  
          type: 'png',  
          pixelRatio: this.exportForm.pixelRatio,  
          backgroundColor: this.exportForm.backgroundColor  
        })  
          
        // 调用后端API生成PDF  
        const params = {  
          chartId: this.exportForm.chartId,  
          chartName: this.chartData.chartName,  
          chartDesc: this.chartData.chartDesc,  
          imageData: dataURL,  
          width: this.exportForm.width,  
          height: this.exportForm.height,  
          filename: this.exportForm.filename  
        }  
          
        exportChartToPdf(params).then(response => {  
          // 预览PDF  
          this.pdfUrl = URL.createObjectURL(response)  
          this.pdfPreviewVisible = true  
          this.exporting = false  
        }).catch(error => {  
          this.$message.error('导出PDF失败: ' + error.message)  
          this.exporting = false  
        })  
      } catch (error) {  
        this.$message.error('导出失败: ' + error.message)  
        this.exporting = false  
      }  
    },  
      
    // 下载PDF  
    downloadPdf() {  
      if (!this.pdfUrl) {  
        this.$message.error('PDF未生成')  
        return  
      }  
        
      // 创建下载链接  
      const link = document.createElement('a')  
      link.href = this.pdfUrl  
      link.download = `${this.exportForm.filename}.pdf`  
      document.body.appendChild(link)  
      link.click()  
      document.body.removeChild(link)  
        
      this.$message.success('下载成功')  
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
    }  
  }  
}  
</script>  
  
<style scoped>  
.chart-container {  
  margin: 20px auto;  
  border: 1px solid #ebeef5;  
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);  
  background-color: #fff;  
}  
  
.chart-wrapper {  
  width: 100%;  
  height: 100%;  
}  
  
.text-center {  
  text-align: center;  
  line-height: 40px;  
}  
  
.unit {  
  margin-left: 5px;  
}  
  
.pdf-container {  
  width: 100%;  
  height: 500px;  
  overflow: auto;  
}  
</style>