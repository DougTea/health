package io.transwarp.health.service;

import io.transwarp.health.common.MetricTask;

public interface MetricService {

    void addMetricTask(String id);

    void consumeMetricTasks();

    void stopPvInsert();

}
