<template>  
  <div class="app-container">  
    <el-card>  
      <div slot="header" class="clearfix">  
        <span>{{ title }}</span>  
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回</el-button>  
      </div>  
        
      <!-- 标签页布局 -->  
      <template v-if="detailViewConfig.layout === 'tabs'">  
        <el-tabs v-model="activeTab">  
          <el-tab-pane   
            v-for="group in detailGroups"   
            :key="group"   
            :label="group"   
            :name="group"  
          >  
            <el-descriptions   
              :column="1"   
              :size="detailViewConfig.size"   
              :label-width="detailViewConfig.labelWidth"  
              border  
            >  
              <el-descriptions-item   
                v-for="field in getGroupFields(group)"   
                :key="field.fieldId"   
                :label="field.fieldLabel"  
              >  
                <template v-if="field.fieldType === 'boolean'">  
                  {{ formData[field.fieldName] ? '是' : '否' }}  
                </template>  
                <template v-else-if="field.fieldType === 'date' || field.fieldType === 'datetime'">  
                  {{ parseTime(formData[field.fieldName]) }}  
                </template>  
                <template v-else-if="field.dictType">  
                  <dict-tag :options="getDictOptions(field.dictType)" :value="formData[field.fieldName]" />  
                </template>  
                <template v-else-if="field.fieldType === 'image'">  
                  <el-image   
                    v-if="formData[field.fieldName]"  
                    style="max-width: 100%; max-height: 200px"   
                    :src="getImageUrl(formData[field.fieldName])"   
                    :preview-src-list="[getImageUrl(formData[field.fieldName])]"  
                  />  
                  <span v-else>-</span>  
                </template>  
                <template v-else-if="field.fieldType === 'file'">  
                  <el-link   
                    v-if="formData[field.fieldName]"  
                    type="primary"   
                    :href="getFileUrl(formData[field.fieldName])"   
                    target="_blank"  
                  >  
                    {{ getFileName(formData[field.fieldName]) }}  
                  </el-link>  
                  <span v-else>-</span>  
                </template>  
                <template v-else>  
                  {{ formData[field.fieldName] || '-' }}  
                </template>  
              </el-descriptions-item>  
            </el-descriptions>  
          </el-tab-pane>  
        </el-tabs>  
      </template>  
        
      <!-- 普通布局 -->  
      <template v-else>  
        <el-descriptions   
          :column="parseInt(detailViewConfig.layout)"   
          :size="detailViewConfig.size"   
          :label-width="detailViewConfig.labelWidth"  
          border  
        >  
          <el-descriptions-item   
            v-for="field in sortedFields"   
            :key="field.fieldId"   
            :label="field.fieldLabel"  
          >  
            <template v-if="field.fieldType === 'boolean'">  
              {{ formData[field.fieldName] ? '是' : '否' }}  
            </template>  
            <template v-else-if="field.fieldType === 'date' || field.fieldType === 'datetime'">  
              {{ parseTime(formData[field.fieldName]) }}  
            </template>  
            <template v-else-if="field.dictType">  
              <dict-tag :options="getDictOptions(field.dictType)" :value="formData[field.fieldName]" />  
            </template>  
            <template v-else-if="field.fieldType === 'image'">  
              <el-image   
                v-if="formData[field.fieldName]"  
                style="max-width: 100%; max-height: 200px"   
                :src="getImageUrl(formData[field.fieldName])"   
                :preview-src-list="[getImageUrl(formData[field.fieldName])]"  
              />  
              <span v-else>-</span>  
            </template>  
            <template v-else-if="field.fieldType === 'file'">  
              <el-link   
                v-if="formData[field.fieldName]"  
                type="primary"   
                :href="getFileUrl(formData[field.fieldName])"   
                target="_blank"  
              >  
                {{ getFileName(formData[field.fieldName]) }}  
              </el-link>  
              <span v-else>-</span>  
            </template>  
            <template v-else>  
              {{ formData[field.fieldName] || '-' }}  
            </template>  
          </el-descriptions-item>  
        </el-descriptions>  
      </template>  
        
      <div class="detail-actions">  
        <el-button type="primary" icon="el-icon-edit" @click="handleEdit" v-hasPermi="['formdata:data:edit']">编辑</el-button>  
        <el-button @click="goBack">返回</el-button>  
      </div>  
    </el-card>  
  </div>  
</template>  
  
<script>  
import { getFormMetadata } from "@/api/metadata/form";  
import { listFormFieldMetadata } from "@/api/metadata/field";  
import { listFormViewConfig } from "@/api/metadata/view";  
import { getFormData } from "@/api/formdata/index";  
import { getDictData } from "@/api/system/dict/data";  
  
export default {  
  name: "FormDataDetail",  
  data() {  
    return {  
      // 表单标题  
      title: "数据详情",  
      // 元数据ID  
      metadataId: null,  
      // 数据ID  
      dataId: null,  
      // 元数据信息  
      metadataInfo: null,  
      // 字段列表  
      fieldList: [],  
      // 表单数据  
      formData: {},  
      // 详情视图配置  
      detailViewConfig: {  
        layout: "1",  
        labelWidth: 120,  
        size: "medium"  
      },  
      // 当前激活的标签页  
      activeTab: "",  
      // 字典数据缓存  
      dictCache: {}  
    };  
  },  
  computed: {  
    // 详情分组  
    detailGroups() {  
      const groups = new Set();  
      this.fieldList.forEach(field => {  
        if (field.detailVisible && field.detailGroup) {  
          groups.add(field.detailGroup);  
        }  
      });  
      return Array.from(groups);  
    },  
    // 排序后的字段  
    sortedFields() {  
      return [...this.fieldList]  
        .filter(field => field.detailVisible)  
        .sort((a, b) => a.detailOrder - b.detailOrder);  
    }  
  },  
  created() {  
    // 获取路由参数  
    this.metadataId = this.$route.query.metadataId;  
    this.dataId = this.$route.query.id;  
      
    // 加载元数据信息  
    this.loadMetadataInfo();  
  },  
  methods: {  
    /** 加载元数据信息 */  
    loadMetadataInfo() {  
      getFormMetadata(this.metadataId).then(response => {  
        this.metadataInfo = response.data;  
        this.title = this.metadataInfo.metadataName + " 详情";  
          
        // 加载字段和视图配置  
        this.loadFieldsAndViews();  
      });  
    },  
    /** 加载字段和视图配置 */  
    loadFieldsAndViews() {  
      // 加载字段列表  
      listFormFieldMetadata({ metadataId: this.metadataId }).then(response => {  
        this.fieldList = response.rows;  
          
        // 加载视图配置  
        listFormViewConfig({ metadataId: this.metadataId, viewType: 'detail', isDefault: 1 }).then(response => {  
          if (response.rows && response.rows.length > 0) {  
            const viewConfig = JSON.parse(response.rows[0].viewConfig || "{}");  
              
            // 设置详情视图配置  
            this.detailViewConfig = {  
              layout: viewConfig.layout || "1",  
              labelWidth: viewConfig.labelWidth || 120,  
              size: viewConfig.size || "medium"  
            };  
              
            // 设置字段配置  
            if (viewConfig.fields && viewConfig.fields.length > 0) {  
              this.fieldList = this.fieldList.map(field => {  
                const fieldConfig = viewConfig.fields.find(f => f.fieldId === field.fieldId) || {};  
                  
                return {  
                  ...field,  
                  detailVisible: fieldConfig.detailVisible !== undefined ? fieldConfig.detailVisible : field.isList === 1,  
                  detailOrder: fieldConfig.detailOrder !== undefined ? fieldConfig.detailOrder : field.sortOrder,  
                  detailGroup: fieldConfig.detailGroup || ""  
                };  
              });  
            } else {  
              // 默认配置  
              this.fieldList = this.fieldList.map(field => {  
                return {  
                  ...field,  
                  detailVisible: field.isList === 1,  
                  detailOrder: field.sortOrder,  
                  detailGroup: ""  
                };  
              });  
            }  
              
            // 设置默认激活的标签页  
            if (this.detailViewConfig.layout === "tabs" && this.detailGroups.length > 0) {  
              this.activeTab = this.detailGroups[0];  
            }  
              
            // 加载字典数据  
            this.loadDictData();  
              
            // 加载表单数据  
            this.loadFormData();  
          } else {  
            // 使用默认配置  
            this.fieldList = this.fieldList.map(field => {  
              return {  
                ...field,  
                detailVisible: field.isList === 1,  
                detailOrder: field.sortOrder,  
                detailGroup: ""  
              };  
            });  
              
            // 加载字典数据  
            this.loadDictData();  
              
            // 加载表单数据  
            this.loadFormData();  
          }  
        });  
      });  
    },  
    /** 加载字典数据 */  
    loadDictData() {  
      // 获取所有需要加载的字典类型  
      const dictTypes = new Set();  
      this.fieldList.forEach(field => {  
        if (field.dictType) {  
          dictTypes.add(field.dictType);  
        }  
      });  
        
      // 加载字典数据  
      dictTypes.forEach(dictType => {  
        getDictData(dictType).then(response => {  
          this.dictCache[dictType] = response.data;  
        });  
      });  
    },  
    /** 加载表单数据 */  
    loadFormData() {  
      getFormData({  
        metadataId: this.metadataId,  
        id: this.dataId  
      }).then(response => {  
        if (response.data) {  
          this.formData = response.data;  
            
          // 处理特殊类型的数据  
          this.fieldList.forEach(field => {  
            // 处理布尔类型  
            if (field.fieldType === "boolean" && this.formData[field.fieldName] !== undefined) {  
              if (typeof this.formData[field.fieldName] === "string") {  
                this.formData[field.fieldName] = this.formData[field.fieldName] === "true" || this.formData[field.fieldName] === "1";  
              }  
            }  
              
            // 处理数字类型  
            if ((field.fieldType === "integer" || field.fieldType === "decimal") && this.formData[field.fieldName] !== undefined) {  
              if (typeof this.formData[field.fieldName] === "string") {  
                this.formData[field.fieldName] = Number(this.formData[field.fieldName]);  
              }  
            }  
          });  
        }  
      });  
    },  
    /** 获取指定分组的字段 */  
    getGroupFields(group) {  
      return this.fieldList  
        .filter(field => field.detailVisible && field.detailGroup === group)  
        .sort((a, b) => a.detailOrder - b.detailOrder);  
    },  
    /** 获取字典选项 */  
    getDictOptions(dictType) {  
      if (!dictType) {  
        return [];  
      }  
        
      if (this.dictCache[dictType]) {  
        return this.dictCache[dictType];  
      }  
        
      // 这里应该调用获取字典数据的API  
      // 为了简化，这里返回空数组  
      return [];  
    },  
    /** 获取图片URL */  
    getImageUrl(path) {  
      if (!path) return '';  
      if (path.startsWith('http')) {  
        return path;  
      }  
      return process.env.VUE_APP_BASE_API + path;  
    },  
    /** 获取文件URL */  
    getFileUrl(path) {  
      if (!path) return '';  
      if (path.startsWith('http')) {  
        return path;  
      }  
      return process.env.VUE_APP_BASE_API + path;  
    },  
    /** 获取文件名 */  
    getFileName(path) {  
      if (!path) return '';  
      return path.substring(path.lastIndexOf('/') + 1);  
    },  
    /** 编辑按钮操作 */  
    handleEdit() {  
      this.$router.push({  
        path: '/formdata/form',  
        query: {  
          metadataId: this.metadataId,  
          id: this.dataId,  
          mode: 'edit'  
        }  
      });  
    },  
    /** 返回列表页 */  
    goBack() {  
      this.$router.push({ path: "/formdata" });  
    }  
  }  
};  
</script>  
  
<style scoped>  
.el-card {  
  margin-bottom: 20px;  
}  
.detail-actions {  
  margin-top: 20px;  
  text-align: center;  
}  
</style>