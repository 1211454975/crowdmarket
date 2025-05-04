package com.ruoyi.formdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.formdata.service.FormDataService;
import com.ruoyi.metadata.service.ChartDataProvider;

/**
 * 图表数据提供者实现
 *
 * @author ruoyi
 */
@Service
public class ChartDataProviderImpl implements ChartDataProvider {
    @Autowired
    private FormDataService formDataService;

    @Override
    public List<Map<String, Object>> selectFormDataList(String metadataId, Map<String, Object> params) {
        return formDataService.selectFormDataList(metadataId, params);
    }
}