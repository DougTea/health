package io.transwarp.health.metric.controller;

import com.sun.corba.se.impl.util.Version;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.transwarp.health.metric.api.ApiResp;
import io.transwarp.health.metric.api.PvResp;
import io.transwarp.health.metric.api.UvResp;
import io.transwarp.health.metric.common.ApiRespUtils;
import io.transwarp.health.metric.common.VersionConstants;
import io.transwarp.health.metric.service.MetricService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api("Metric Api")
@RestController
@RequestMapping("/" + VersionConstants.VERSION)
public class MetricController {
    @Resource
    MetricService metricService;


    @ApiOperation("Get pv")
    @RequestMapping(value = "/pv", method = RequestMethod.GET)
    public ApiResp<String> getPv() {
        PvResp response = metricService.getPv();
        List<String> data = new ArrayList<>();
        data.add(response.toString());
        return ApiRespUtils.success(data.toString());
    }

    @ApiOperation("Get uv")
    @RequestMapping(value = "/uv", method = RequestMethod.GET)
    public ApiResp<String> getUv() {
        UvResp response = metricService.getUv();
        List<String> data = new ArrayList<>();
        data.add(response.toString());
        return ApiRespUtils.success(data.toString());
    }
}
