package com.ruoyi.formdata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 表单数据Mapper接口
 *
 * @author ruoyi
 */
public interface FormDataMapper {
    /**
     * 查询表单数据
     *
     * @param tableName 表名
     * @param pkColumn 主键列名
     * @param pkValue 主键值
     * @return 表单数据
     */
    public Map<String, Object> selectFormDataById(@Param("tableName") String tableName, @Param("pkColumn") String pkColumn, @Param("pkValue") String pkValue);

    /**
     * 查询表单数据列表
     *
     * @param tableName 表名
     * @param params 查询参数
     * @return 表单数据集合
     */
    public List<Map<String, Object>> selectFormDataList(@Param("tableName") String tableName, @Param("params") Map<String, Object> params);

    /**
     * 新增表单数据
     *
     * @param tableName 表名
     * @param data 表单数据
     * @return 结果
     */
    public int insertFormData(@Param("tableName") String tableName, @Param("data") Map<String, Object> data);

    /**
     * 修改表单数据
     *
     * @param tableName 表名
     * @param pkColumn 主键列名
     * @param pkValue 主键值
     * @param data 表单数据
     * @return 结果
     */
    public int updateFormData(@Param("tableName") String tableName, @Param("pkColumn") String pkColumn, @Param("pkValue") String pkValue, @Param("data") Map<String, Object> data);

    /**
     * 删除表单数据
     *
     * @param tableName 表名
     * @param pkColumn 主键列名
     * @param pkValue 主键值
     * @return 结果
     */
    public int deleteFormDataById(@Param("tableName") String tableName, @Param("pkColumn") String pkColumn, @Param("pkValue") String pkValue);

    /**
     * 批量删除表单数据
     *
     * @param tableName 表名
     * @param pkColumn 主键列名
     * @param pkValues 主键值数组
     * @return 结果
     */
    public int deleteFormDataByIds(@Param("tableName") String tableName, @Param("pkColumn") String pkColumn, @Param("pkValues") String[] pkValues);

    /**
     * 执行SQL
     *
     * @param sql SQL语句
     * @return 结果
     */
    public int executeSql(@Param("sql") String sql);
}