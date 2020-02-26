package io.transwarp.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Crearted  on 2020/2/25
 */
@Api("Health Check Api for rolling update")
@RestController
@RequestMapping("/healthCheck")
public class HealthCheckController {
    @ApiOperation("readiness check")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public void healthCheck() {
        // just check health
    }
}
