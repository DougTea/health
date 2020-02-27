package io.transwarp.health.service;

import io.transwarp.health.api.HealthResponse;

/**
 * Created by Shannon on 2020/2/23.
 */
public interface HealthService {
    HealthResponse getHealth(String xm, String zjxm);

    HealthResponse getHealthNot00IfNotExist(String xm, String zjxm);
}
