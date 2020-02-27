package io.transwarp.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.transwarp.health.api.ApiResp;
import io.transwarp.health.api.HealthResponse;
import io.transwarp.health.common.ApiRespUtils;
import io.transwarp.health.common.HealthConstants;
import io.transwarp.health.common.Value;
import io.transwarp.health.service.HealthService;
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
    @Resource
    HealthService healthService;

    @ApiOperation("Get Health")
    @RequestMapping(value = "/health",method = RequestMethod.GET)
    public ApiResp<String> getHealth(@RequestParam String xm, @RequestParam String zjhm) {
        return  getHealthInfo(xm,zjhm);
    }

    @ApiOperation("Get Health")
    @RequestMapping(value = "/health",method = RequestMethod.POST)
    public ApiResp<String> getHealth(@RequestBody Value value) {
        return  getHealthInfo(value.getXm(),value.getZjhm());
    }

    private ApiResp<String> getHealthInfo(String username,String code){
        HealthResponse response;
        List<String> data = new ArrayList<>();
        try{
            response = healthService.getHealth(username, code);
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