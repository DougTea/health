package io.transwarp.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.transwarp.health.common.HealthConstants;
import io.transwarp.health.service.MetricService;
import org.mortbay.log.Log;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api("Metric Api")
@RestController
@RequestMapping("/metrics")
public class MetricController {
    @Resource
    MetricService metricService;

    @ApiOperation("stop pv insert")
    @RequestMapping(value = "stop", method = RequestMethod.GET)
    public void stopPvInsert() {
        // just check health
        metricService.stopPvInsert();
        Log.info("stop pv insert to hbmaster");
    }
}
