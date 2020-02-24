package io.transwarp.health;

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
@ComponentScan("io.transwarp.health")
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
public class HealthServer {
  public static void main(String[] args) {
    SpringApplication.run(HealthServer.class, args);
  }
}
