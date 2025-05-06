package com.ruoyi.formdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.formdata.service.IFormDataService;
import com.ruoyi.metadata.service.IChartDataProvider;

/**
 * 图表数据提供者实现
 *
 * @author ruoyi
 */
@Service
public class ChartDataProviderImpl implements IChartDataProvider {
    @Autowired
    private IFormDataService formDataService;

    @Override
    public List<Map<String, Object>> selectFormDataList(String metadataId, Map<String, Object> params) {
        return formDataService.selectFormDataList(metadataId, params);
    }
}