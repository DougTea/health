package io.transwarp.health.service;

import io.transwarp.health.api.HealthResponse;

import java.io.IOException;

/**
 * Created by Shannon on 2020/2/23.
 */
public interface HealthService {
    HealthResponse getHealth(String xm, String zjxm);
}
