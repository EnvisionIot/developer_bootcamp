package com.example.batteryweb.controller;

import com.envisioniot.enos.asset_service.v2_1.GetAssetResponse;
import com.envisioniot.enos.asset_tree_service.v2_1.SearchRelatedAssetNodeResponse;
import com.envisioniot.enos.asset_tree_service.vo.AssetVo;
import com.example.batteryweb.model.Alert;
import com.example.batteryweb.model.GetAlertResponse;
import com.example.batteryweb.model.GetDataResponse;
import com.example.batteryweb.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class DataController {
    @Autowired
    private DataService dataService;

    /**
     * Returning all the battery assets under the "Envision Smart Car Factory" asset tree.
     * @return
     */
    @RequestMapping("/battery/list")
    public Map<String, Object> list() {

        SearchRelatedAssetNodeResponse resp = dataService.getAssetList();

        List<Map<String, String>> batteries=new ArrayList();
        for (AssetVo asert: resp.getData()) {
            Map<String, String> item = new HashMap<>();
            item.put("assetId", asert.getAssetId());
            item.put("name", asert.getName().getDefaultValue());
            batteries.add(item);
        }

        // Ordering the display of batteries
        Collections.sort(batteries, new Comparator<Map<String,String>>() {
            @Override
            public int compare(Map<String, String> o1,
                               Map<String, String> o2) {
                return o1.get("name").compareTo(o2.get("name"));
            }
        });

        Map<String, Object> result = new HashMap<>();
        result.put("batteries", batteries);
        return result;
    }

    @RequestMapping("/battery/asset/{id}")
    public GetAssetResponse asset(@PathVariable("id") String id) {
        return dataService.getAssetById(id);
    }

    @RequestMapping("/battery/status/{id}")
    public Map<String, Object> status(@PathVariable("id") String id) {
        GetAssetResponse asset = dataService.getAssetById(id);
        GetDataResponse data = dataService.getData(id, "health_level,accumulating_power");

        int health_level=0;
        String remaining_power="";

        for (Map<String, Object> item: data.getData().getItems()) {
            if (item.containsKey("health_level")) {
                health_level = (int)item.get("health_level");
            }
            double capacity = (double)asset.getData().getAttributes().get("Capacity");
            if (item.containsKey("accumulating_power")) {
                Number power = (Number)item.get("accumulating_power");
                remaining_power = String.format("%.0f%%", (100*power.intValue()/capacity));
            }
        }

        // Simplifying returned data
        Map<String, Object> result = new HashMap<>();
        result.put("health_level", health_level);
        result.put("remaining_power", remaining_power);

        return result;
    }

    @RequestMapping("/battery/tsdb/{id}")
    public Map<String, Object> tsdb(@PathVariable("id") String id) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date nowTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.HOUR, -1);
        Date oneHourBefore=calendar.getTime();

        String startTime = fmt.format(oneHourBefore);
        String endTime = fmt.format(nowTime);
        GetDataResponse resp = dataService.getHistoryData(id, startTime, endTime);


        // Reformat returned data for front-end display
        Map<String, Object> result = new HashMap<>();
        List<Double> voltage = new ArrayList<>();
        List<Double> current = new ArrayList<>();
        List<Double> temp = new ArrayList<>();
        result.put("voltage", voltage);
        result.put("current", current);
        result.put("temp", temp);

        Set<String> time = new HashSet<>();
        String measurepoints = "voltage,current,temp";
        for(Map<String, Object> items: resp.getData().getItems()){
            for (String point: measurepoints.split(",")) {
                if (items.containsKey(point)) {
                    time.add(items.get("localtime").toString());
                    List<Double> pointList = (List<Double>) result.get(point);
                    pointList.add((Double) items.get(point));
                    break;
                }
            }
        }
        result.put("time", time);

        return result;
    }

    @RequestMapping("/battery/alerts")
    public GetAlertResponse alerts() {
        GetAlertResponse alerts = dataService.getAlerts();
        for (Alert alert: alerts.getData()) {
            GetAssetResponse asset = dataService.getAssetById(alert.getAssetId());
            String assetName = asset.getData().getName().getDefaultValue();
            alert.setAssetName(assetName);
        }
        return alerts;
    }

    @RequestMapping(value = "/battery/service/{id}", method = RequestMethod.POST)
    public Object setFrequency(@PathVariable("id") String id,
                               @RequestParam("command") String command,
                               @RequestParam("parameter") Integer parameter) {
        return dataService.invokeFrequencyService(id, parameter);
    }


}
