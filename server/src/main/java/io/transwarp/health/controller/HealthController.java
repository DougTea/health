package io.transwarp.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.transwarp.health.api.ApiResp;
import io.transwarp.health.api.HealthResponse;
import io.transwarp.health.common.ApiRespUtils;
import io.transwarp.health.common.HealthConstants;
import io.transwarp.health.service.HealthService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Resource
    HealthService healthService;

    @ApiOperation("Get Health")
    @RequestMapping(value = "/health")
    public ApiResp<String> getHealth(@RequestParam String xm, @RequestParam String zjhm) {
        HealthResponse response;
        List<String> data = new ArrayList<>();

        try{
            response = healthService.getHealth(xm, zjhm);
            data.add(response.toString());
        }catch (Throwable throwable){
            return ApiRespUtils.response("1",throwable.getMessage(),data.toString());
        }
        // TODO:local test monitor
        //HealthResponse response = new HealthResponse();
        //response.setType("00");

        return ApiRespUtils.success(data.toString());
    }
}