package com.example.batteryweb.service;

import com.envision.apim.poseidon.config.PConfig;
import com.envision.apim.poseidon.core.Poseidon;
import com.envision.apim.poseidon.exception.PoseidonException;
import com.envision.apim.poseidon.request.IPoseidonRequest;
import com.envision.apim.poseidon.request.PoseidonRequest;
import com.envisioniot.enos.asset_service.v2_1.GetAssetRequest;
import com.envisioniot.enos.asset_service.v2_1.GetAssetResponse;
import com.envisioniot.enos.asset_tree_service.v2_1.SearchRelatedAssetNodeRequest;
import com.envisioniot.enos.asset_tree_service.v2_1.SearchRelatedAssetNodeResponse;
import com.envisioniot.enos.asset_tree_service.vo.RelatedAssetSearchVo;
import com.envisioniot.enos.connect_service.v2_1.service.InvokeServiceRequest;
import com.envisioniot.enos.connect_service.v2_1.service.InvokeServiceResponse;
import com.envisioniot.enos.event_service.v2_1.SearchActiveAlertRequest;
import com.envisioniot.enos.event_service.vo.Scope;
import com.example.batteryweb.AppConfig;
import com.example.batteryweb.model.GetAlertResponse;
import com.example.batteryweb.model.GetDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class DataService {
    @Autowired
    AppConfig config;

    public <T> T getResponse(IPoseidonRequest request, Class<T> cls) throws PoseidonException {
        return Poseidon.config(PConfig.init().appKey(config.accessKey).appSecret(config.accessSecret)).url(config.addr).getResponse(request, cls);
    }

    /**
     * Getting the list of assets of specified models and asset tree.
     * @return SearchRelatedAssetNodeResponse
     */
    public SearchRelatedAssetNodeResponse getAssetList() {
        SearchRelatedAssetNodeRequest request= new SearchRelatedAssetNodeRequest();
        request.setOrgId(config.orgId);
        request.setTreeId(config.assetTreeId);

        RelatedAssetSearchVo filter = new RelatedAssetSearchVo();
        filter.setIsChildOfAssetId(config.parentAssetId);
        request.setFilter(filter);

        return getResponse(request, SearchRelatedAssetNodeResponse.class);
    }

    public GetAssetResponse getAssetById(String id) {
        GetAssetRequest request= new GetAssetRequest();
        request.setAssetId(id);
        request.setOrgId(config.orgId);

        return getResponse(request, GetAssetResponse.class);
    }

    public GetDataResponse getData(String assetId, String measurepoints) {
        IPoseidonRequest request = new PoseidonRequest() {
            @Override
            public String baseUri() {
                return "/tsdb-service/v2.0/latest";
            }

            @Override
            public String method() {
                return "GET";
            }

            @Override
            public Map<String, Object> queryParams() {
                Map<String, Object> params = new HashMap();
                params.put("assetIds", assetId);
                params.put("measurepoints", measurepoints);
                params.put("orgId", config.orgId);
                params.put("accessKey", config.accessKey);
                return params;
            }
        };

        return getResponse(request, GetDataResponse.class);
    }

    public GetDataResponse getHistoryData(String assetId, String startTime, String endTime) {
        IPoseidonRequest request = new PoseidonRequest() {
            @Override
            public String baseUri() {
                return "/tsdb-service/v2.0/ai";
            }

            @Override
            public String method() {
                return "GET";
            }

            @Override
            public Map<String, Object> queryParams() {
                Map<String, Object> params = new HashMap();
                params.put("assetIds", assetId);

                String measurepoints = "voltage,current,temp";
                params.put("measurepoints", measurepoints);

                params.put("startTime", startTime);
                params.put("endTime", endTime);
                params.put("orgId", config.orgId);
                return params;
            }
        };

        return getResponse(request, GetDataResponse.class);
    }

    public GetAlertResponse getAlerts() {
        SearchActiveAlertRequest request = new SearchActiveAlertRequest();
        request.setOrgId(config.orgId);
        Scope scope = new  Scope();
        scope.setTreeId(config.assetTreeId);
        scope.setFromAssetId(config.parentAssetId);
        request.setScope(scope);
        return getResponse(request, GetAlertResponse.class);
    }


    public InvokeServiceResponse invokeFrequencyService(String assetId, Integer interval) {
        InvokeServiceRequest request = new InvokeServiceRequest();
        request.setOrgId(config.orgId);
        request.setAssetId(assetId);
        request.setServiceId("high_frequency_report_service");

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("interval",interval);
        request.setInputData(inputData);
        request.setPendingTtl(1000L);

        return getResponse(request, InvokeServiceResponse.class);
    }


}
