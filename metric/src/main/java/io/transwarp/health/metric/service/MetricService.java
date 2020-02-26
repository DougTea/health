package io.transwarp.health.metric.service;

import io.transwarp.health.metric.api.PvResp;
import io.transwarp.health.metric.api.UvResp;

public interface MetricService {
    PvResp getPv();

    UvResp getUv();
}
