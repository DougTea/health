package io.transwarp.health.metric;

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Shannon on 2020/2/23.
 */
@SpringBootApplication
@ComponentScan("io.transwarp.health.metric")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
public class MetricServer {
    public static void main(String[] args) {
        SpringApplication.run(MetricServer.class, args);
    }
}
