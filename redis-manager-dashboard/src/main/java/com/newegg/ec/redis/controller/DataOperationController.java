package com.newegg.ec.redis.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.newegg.ec.redis.entity.AutoCommandParam;
import com.newegg.ec.redis.entity.AutoCommandResult;
import com.newegg.ec.redis.entity.Cluster;
import com.newegg.ec.redis.entity.Result;
import com.newegg.ec.redis.service.IClusterService;
import com.newegg.ec.redis.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jay.H.Zou
 * @date 9/26/2019
 */
@RequestMapping("/data/*")
@Controller
public class DataOperationController {

    @Autowired
    private IClusterService clusterService;

    @Autowired
    private IRedisService redisService;

    @RequestMapping(value = "/getDBList/{clusterId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getDBList(@PathVariable("clusterId") Integer clusterId) {
        if (clusterId == null) {
            return Result.failResult().setMessage("Request Param is empty.");
        }
        Cluster cluster = clusterService.getClusterById(clusterId);
        if (cluster == null) {
            return Result.failResult().setMessage("Get cluster failed.");
        }
        Map<String, Long> databaseMap = redisService.getDatabase(cluster);
        List<JSONObject> databaseList = new ArrayList<>();
        databaseMap.forEach((key, val) -> {
            JSONObject item = new JSONObject();
            item.put("database", key);
            item.put("keys", val);
            databaseList.add(item);
        });
        return Result.successResult(databaseList);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public Result queryRedis(@RequestBody AutoCommandParam autoCommandParam) {
        Cluster cluster = clusterService.getClusterById(autoCommandParam.getClusterId());
        if (cluster == null) {
            return Result.failResult().setMessage("Get cluster failed.");
        }
        AutoCommandResult queryResult = redisService.query(cluster, autoCommandParam);
        return queryResult != null ? Result.successResult(queryResult) : Result.failResult().setMessage("Query redis failed.");
    }

    @RequestMapping(value = "/scan", method = RequestMethod.POST)
    @ResponseBody
    public Result scanRedis(@RequestBody AutoCommandParam autoCommandParam) {
        return Result.successResult();
    }

}
