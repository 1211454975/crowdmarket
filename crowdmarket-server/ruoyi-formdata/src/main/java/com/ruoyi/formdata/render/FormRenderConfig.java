package com.ruoyi.formdata.render;  
  
import java.io.Serializable;  
  
/**  
 * 表单渲染配置类  
 *   
 * @author ruoyi  
 */  
public class FormRenderConfig implements Serializable {  
      
    private static final long serialVersionUID = 1L;  
  
    /** 是否显示标签 */  
    private boolean showLabel = true;  
      
    /** 标签宽度 */  
    private String labelWidth = "80px";  
      
    /** 表单尺寸 (large/default/small) */  
    private String size = "default";  
      
    /** 是否禁用表单 */  
    private boolean disabled = false;  
      
    /** 是否只读 */  
    private boolean readonly = false;  
      
    /** 是否显示必填星号 */  
    private boolean hideRequiredAsterisk = false;  
      
    /** 是否显示校验错误信息 */  
    private boolean showMessage = true;  
      
    /** 是否行内表单 */  
    private boolean inline = false;  
      
    /** 校验触发方式 (blur/change/submit) */  
    private String validateOnRuleChange = "blur";  
      
    /** 是否应用租户样式 */  
    private boolean applyTenantStyle = true;  
      
    /** 自定义CSS类 */  
    private String customClass = "";  
  
    // Getters and setters  
    public boolean isShowLabel() {  
        return showLabel;  
    }  
  
    public void setShowLabel(boolean showLabel) {  
        this.showLabel = showLabel;  
    }  
  
    public String getLabelWidth() {  
        return labelWidth;  
    }  
  
    public void setLabelWidth(String labelWidth) {  
        this.labelWidth = labelWidth;  
    }  
  
    public String getSize() {  
        return size;  
    }  
  
    public void setSize(String size) {  
        this.size = size;  
    }  
  
    public boolean isDisabled() {  
        return disabled;  
    }  
  
    public void setDisabled(boolean disabled) {  
        this.disabled = disabled;  
    }  
  
    public boolean isReadonly() {  
        return readonly;  
    }  
  
    public void setReadonly(boolean readonly) {  
        this.readonly = readonly;  
    }  
  
    public boolean isHideRequiredAsterisk() {  
        return hideRequiredAsterisk;  
    }  
  
    public void setHideRequiredAsterisk(boolean hideRequiredAsterisk) {  
        this.hideRequiredAsterisk = hideRequiredAsterisk;  
    }  
  
    public boolean isShowMessage() {  
        return showMessage;  
    }  
  
    public void setShowMessage(boolean showMessage) {  
        this.showMessage = showMessage;  
    }  
  
    public boolean isInline() {  
        return inline;  
    }  
  
    public void setInline(boolean inline) {  
        this.inline = inline;  
    }  
  
    public String getValidateOnRuleChange() {  
        return validateOnRuleChange;  
    }  
  
    public void setValidateOnRuleChange(String validateOnRuleChange) {  
        this.validateOnRuleChange = validateOnRuleChange;  
    }  
  
    public boolean isApplyTenantStyle() {  
        return applyTenantStyle;  
    }  
  
    public void setApplyTenantStyle(boolean applyTenantStyle) {  
        this.applyTenantStyle = applyTenantStyle;  
    }  
  
    public String getCustomClass() {  
        return customClass;  
    }  
  
    public void setCustomClass(String customClass) {  
        this.customClass = customClass;  
    }  
}