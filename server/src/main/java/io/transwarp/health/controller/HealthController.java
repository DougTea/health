package io.transwarp.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.transwarp.health.api.ApiResp;
import io.transwarp.health.api.HealthResponse;
import io.transwarp.health.common.ApiRespUtils;
import io.transwarp.health.common.HealthConstants;
import io.transwarp.health.common.Value;
import io.transwarp.health.service.HealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shannon on 2020/2/23.
 */
@Api("Health Api")
@RestController
@RequestMapping("/" + HealthConstants.VERSION)
public class HealthController {
    public static final Logger LOG = LoggerFactory.getLogger(HealthController.class);

    @Resource
    HealthService healthService;

    @ApiOperation("Get Health")
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public ApiResp<String> getHealth(@RequestParam String xm, @RequestParam String zjhm) {
        HealthResponse response;
        List<String> data = new ArrayList<>();
        try {
            response = healthService.getHealth(xm, zjhm);
            data.add(response.toString());
        } catch (Throwable throwable) {
            return ApiRespUtils.response("1", throwable.getMessage(), data.toString());
        }
        return ApiRespUtils.success(data.toString());
    }

    @ApiOperation("Get Health")
    @RequestMapping(value = "/health", method = RequestMethod.POST)
    public ApiResp<String> getHealth(@RequestBody Value value) {
        HealthResponse response;
        List<String> data = new ArrayList<>();

        try {
            response = healthService.getHealthNot00IfNotExist(value.getXm(), value.getZjhm());
            if(response.getType()!=null){
                data.add(response.toString());
            }
            LOG.info("xm: {}, zjhm: {}, response: {}", value.getXm(), value.getZjhm(), response.toString());
        } catch (Throwable throwable) {
            LOG.info("xm: {}, zjhm: {}, response: {}", value.getXm(), value.getZjhm(), throwable.getMessage());
            return ApiRespUtils.response("1", throwable.getMessage(), data.toString());
        }

        return ApiRespUtils.success(data.toString());

    }

}