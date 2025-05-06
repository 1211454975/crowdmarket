<template>  
  <div class="app-container">  
    <el-card>  
      <div slot="header" class="clearfix">  
        <span>{{ title }}</span>  
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回</el-button>  
      </div>  
        
      <el-form ref="form" :model="formData" :rules="rules" :label-width="formViewConfig.labelWidth + 'px'" :size="formViewConfig.size">  
        <!-- 标签页布局 -->  
        <template v-if="formViewConfig.layout === 'tabs'">  
          <el-tabs v-model="activeTab">  
            <el-tab-pane   
              v-for="group in formGroups"   
              :key="group"   
              :label="group"   
              :name="group"  
            >  
              <el-row :gutter="20">  
                <el-col :span="24" v-for="field in getGroupFields(group)" :key="field.fieldId">  
                  <el-form-item   
                    :label="field.fieldLabel"   
                    :prop="field.fieldName"  
                    v-if="field.formVisible"  
                    :required="field.formRequired"  
                  >  
                    <component   
                      :is="getFieldComponent(field)"   
                      v-model="formData[field.fieldName]"   
                      :disabled="field.formReadonly || mode === 'view'"  
                      :placeholder="getFieldPlaceholder(field)"  
                      v-bind="getFieldProps(field)"  
                    >  
                      <template v-if="['select', 'radio', 'checkbox'].includes(field.htmlType)">  
                        <component   
                          :is="getOptionComponent(field.htmlType)"  
                          v-for="dict in getDictOptions(field.dictType)"  
                          :key="dict.dictValue"  
                          :label="dict.dictLabel"  
                          :value="dict.dictValue"  
                        />  
                      </template>  
                    </component>  
                  </el-form-item>  
                </el-col>  
              </el-row>  
            </el-tab-pane>  
          </el-tabs>  
        </template>  
          
        <!-- 普通布局 -->  
        <template v-else>  
          <el-row :gutter="20">  
            <el-col :span="getColSpan(formViewConfig.layout)" v-for="field in sortedFields" :key="field.fieldId" v-if="field.formVisible">  
              <el-form-item   
                :label="field.fieldLabel"   
                :prop="field.fieldName"  
                :required="field.formRequired"  
              >  
                <component   
                  :is="getFieldComponent(field)"   
                  v-model="formData[field.fieldName]"   
                  :disabled="field.formReadonly || mode === 'view'"  
                  :placeholder="getFieldPlaceholder(field)"  
                  v-bind="getFieldProps(field)"  
                >  
                  <template v-if="['select', 'radio', 'checkbox'].includes(field.htmlType)">  
                    <component   
                      :is="getOptionComponent(field.htmlType)"  
                      v-for="dict in getDictOptions(field.dictType)"  
                      :key="dict.dictValue"  
                      :label="dict.dictLabel"  
                      :value="dict.dictValue"  
                    />  
                  </template>  
                </component>  
              </el-form-item>  
            </el-col>  
          </el-row>  
        </template>  
          
        <el-form-item v-if="mode !== 'view'">  
          <el-button type="primary" @click="submitForm">保存</el-button>  
          <el-button @click="goBack">取消</el-button>  
        </el-form-item>  
        <el-form-item v-else>  
          <el-button @click="goBack">返回</el-button>  
        </el-form-item>  
      </el-form>  
    </el-card>  
  </div>  
</template>  
  
<script>  
import { getFormMetadata } from "@/api/metadata/form";  
import { listFormFieldMetadata } from "@/api/metadata/field";  
import { listFormViewConfig } from "@/api/metadata/view";  
import { getFormData, addFormData, updateFormData } from "@/api/formdata/index";  
import { getDictData } from "@/api/system/dict/data";  
  
export default {  
  name: "FormDataForm",  
  data() {  
    return {  
      // 操作模式：add-新增，edit-编辑，view-查看  
      mode: "add",  
      // 表单标题  
      title: "",  
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
      // 表单校验规则  
      rules: {},  
      // 表单视图配置  
      formViewConfig: {  
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
    // 表单分组  
    formGroups() {  
      const groups = new Set();  
      this.fieldList.forEach(field => {  
        if (field.formVisible && field.formGroup) {  
          groups.add(field.formGroup);  
        }  
      });  
      return Array.from(groups);  
    },  
    // 排序后的字段  
    sortedFields() {  
      return [...this.fieldList]  
        .filter(field => field.formVisible)  
        .sort((a, b) => a.formOrder - b.formOrder);  
    }  
  },  
  created() {  
    // 获取路由参数  
    this.metadataId = this.$route.query.metadataId;  
    this.dataId = this.$route.query.id;  
    this.mode = this.$route.query.mode || "add";  
      
    // 设置标题  
    if (this.mode === "add") {  
      this.title = "新增数据";  
    } else if (this.mode === "edit") {  
      this.title = "编辑数据";  
    } else {  
      this.title = "查看数据";  
    }  
      
    // 加载元数据信息  
    this.loadMetadataInfo();  
  },  
  methods: {  
    /** 加载元数据信息 */  
    loadMetadataInfo() {  
      getFormMetadata(this.metadataId).then(response => {  
        this.metadataInfo = response.data;  
          
        // 加载字段和视图配置  
        this.loadFieldsAndViews();  
      });  
    },  
    /** 加载字段和视图配置 */  
    loadFieldsAndViews() {  
      // 加载字段列表  
      listFormFieldMetadata({ metadataId: this.metadataId }).then(response => {  
        this.fieldList = response.rows;  
          
        // 初始化表单数据和校验规则  
        this.initFormData();  
        this.initFormRules();  
          
        // 加载视图配置  
        listFormViewConfig({ metadataId: this.metadataId, viewType: 'form', isDefault: 1 }).then(response => {  
          if (response.rows && response.rows.length > 0) {  
            const viewConfig = JSON.parse(response.rows[0].viewConfig || "{}");  
              
            // 设置表单视图配置  
            this.formViewConfig = {  
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
                  formVisible: fieldConfig.formVisible !== undefined ? fieldConfig.formVisible : field.isList === 1,  
                  formRequired: fieldConfig.formRequired !== undefined ? fieldConfig.formRequired : field.isRequired === 1,  
                  formReadonly: fieldConfig.formReadonly || false,  
                  formOrder: fieldConfig.formOrder !== undefined ? fieldConfig.formOrder : field.sortOrder,  
                  formGroup: fieldConfig.formGroup || ""  
                };  
              });  
            } else {  
              // 默认配置  
              this.fieldList = this.fieldList.map(field => {  
                return {  
                  ...field,  
                  formVisible: field.isList === 1,  
                  formRequired: field.isRequired === 1,  
                  formReadonly: false,  
                  formOrder: field.sortOrder,  
                  formGroup: ""  
                };  
              });  
            }  
              
            // 设置默认激活的标签页  
            if (this.formViewConfig.layout === "tabs" && this.formGroups.length > 0) {  
              this.activeTab = this.formGroups[0];  
            }  
              
            // 加载字典数据  
            this.loadDictData();  
              
            // 如果是编辑或查看模式，加载数据  
            if (this.mode === "edit" || this.mode === "view") {  
              this.loadFormData();  
            }  
          }  
        });  
      });  
    },  
    /** 初始化表单数据 */  
    initFormData() {  
      this.formData = {};  
      this.fieldList.forEach(field => {  
        // 设置默认值  
        if (field.fieldType === "boolean") {  
          this.formData[field.fieldName] = false;  
        } else if (field.fieldType === "integer") {  
          this.formData[field.fieldName] = null;  
        } else if (field.fieldType === "decimal") {  
          this.formData[field.fieldName] = null;  
        } else {  
          this.formData[field.fieldName] = "";  
        }  
      });  
    },  
    /** 初始化表单校验规则 */  
    initFormRules() {  
      this.rules = {};  
      this.fieldList.forEach(field => {  
        if (field.isRequired === 1) {  
          this.rules[field.fieldName] = [  
            { required: true, message: field.fieldLabel + "不能为空", trigger: this.getTrigger(field.htmlType) }  
          ];  
            
          // 添加特定类型的验证规则  
          if (field.fieldType === "integer") {  
            this.rules[field.fieldName].push({ type: "integer", message: "请输入整数", trigger: "blur" });  
          } else if (field.fieldType === "decimal") {  
            this.rules[field.fieldName].push({ type: "number", message: "请输入数字", trigger: "blur" });  
          } else if (field.fieldType === "email") {  
            this.rules[field.fieldName].push({ type: "email", message: "请输入正确的邮箱地址", trigger: "blur" });  
          }  
        }  
      });  
    },  
    /** 获取触发方式 */  
    getTrigger(htmlType) {  
      if (["select", "radio", "checkbox", "datetime"].includes(htmlType)) {  
        return "change";  
      }  
      return "blur";  
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
        .filter(field => field.formVisible && field.formGroup === group)  
        .sort((a, b) => a.formOrder - b.formOrder);  
    },  
    /** 根据布局获取列宽 */  
    getColSpan(layout) {  
      if (layout === "1") return 24;  
      if (layout === "2") return 12;  
      if (layout === "3") return 8;  
      return 24;  
    },  
    /** 获取字段对应的表单组件 */  
    getFieldComponent(field) {  
      switch (field.htmlType) {  
        case "input":  
          return "el-input";  
        case "textarea":  
          return "el-input";  
        case "select":  
          return "el-select";  
        case "radio":  
          return "el-radio-group";  
        case "checkbox":  
          return "el-checkbox-group";  
        case "datetime":  
          return "el-date-picker";  
        case "upload":  
          return "el-upload";  
        case "editor":  
          return "tinymce-editor";  
        default:  
          return "el-input";  
      }  
    },  
    /** 获取选项组件 */  
    getOptionComponent(htmlType) {  
      switch (htmlType) {  
        case "select":  
          return "el-option";  
        case "radio":  
          return "el-radio";  
        case "checkbox":  
          return "el-checkbox";  
        default:  
          return null;  
      }  
    },  
    /** 获取字段占位符 */  
    getFieldPlaceholder(field) {  
      if (field.htmlType === "input") {  
        return "请输入" + field.fieldLabel;  
      } else if (field.htmlType === "textarea") {  
        return "请输入" + field.fieldLabel;  
      } else if (field.htmlType === "select") {  
        return "请选择" + field.fieldLabel;  
      } else if (field.htmlType === "datetime") {  
        return "请选择" + field.fieldLabel;  
      }  
      return "";  
    },  
    /** 获取字段属性 */  
    getFieldProps(field) {  
      const props = {};  
        
      if (field.htmlType === "textarea") {  
        props.type = "textarea";  
        props.rows = 4;  
      } else if (field.htmlType === "datetime") {  
        if (field.fieldType === "date") {  
          props.type = "date";  
          props.valueFormat = "yyyy-MM-dd";  
        } else {  
          props.type = "datetime";  
          props.valueFormat = "yyyy-MM-dd HH:mm:ss";  
        }  
      } else if (field.htmlType === "upload") {  
        props.action = process.env.VUE_APP_BASE_API + "/common/upload";  
        props.multiple = false;  
        props.limit = 1;  
        props["file-list"] = this.getUploadFileList(field.fieldName);  
        props["on-success"] = (response, file, fileList) => this.handleUploadSuccess(field.fieldName, response, file, fileList);  
        props["on-remove"] = (file, fileList) => this.handleUploadRemove(field.fieldName, file, fileList);  
        props["before-upload"] = file => this.handleBeforeUpload(field.fieldType, file);  
      }  
        
      return props;  
    },  
    /** 获取上传文件列表 */  
    getUploadFileList(fieldName) {  
      const value = this.formData[fieldName];  
      if (!value) {  
        return [];  
      }  
        
      return [{  
        name: value.substring(value.lastIndexOf('/') + 1),  
        url: process.env.VUE_APP_BASE_API + value  
      }];  
    },  
    /** 上传成功处理 */  
    handleUploadSuccess(fieldName, response, file, fileList) {  
      if (response.code === 200) {  
        this.formData[fieldName] = response.data.url;  
        this.$message.success("上传成功");  
      } else {  
        this.$message.error(response.msg);  
      }  
    },  
    /** 移除文件处理 */  
    handleUploadRemove(fieldName, file, fileList) {  
      this.formData[fieldName] = null;  
    },  
    /** 上传前处理 */  
    handleBeforeUpload(fieldType, file) {  
      if (fieldType === "image") {  
        const isImage = file.type.indexOf("image") !== -1;  
        if (!isImage) {  
          this.$message.error("只能上传图片文件!");  
          return false;  
        }  
      }  
      const isLt10M = file.size / 1024 / 1024 < 10;  
      if (!isLt10M) {  
        this.$message.error("文件大小不能超过 10MB!");  
        return false;  
      }  
      return true;  
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
    /** 提交表单 */  
    submitForm() {  
      this.$refs["form"].validate(valid => {  
        if (valid) {  
          // 处理特殊类型的数据  
          const submitData = { ...this.formData };  
            
          if (this.mode === "add") {  
            addFormData({  
              metadataId: this.metadataId,  
              data: submitData  
            }).then(response => {  
              this.$modal.msgSuccess("添加成功");  
              this.goBack();  
            });  
          } else if (this.mode === "edit") {  
            updateFormData({  
              metadataId: this.metadataId,  
              id: this.dataId,  
              data: submitData  
            }).then(response => {  
              this.$modal.msgSuccess("修改成功");  
              this.goBack();  
            });  
          }  
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
</style>