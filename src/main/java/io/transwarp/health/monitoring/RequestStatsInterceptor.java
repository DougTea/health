package io.transwarp.health.monitoring;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class RequestStatsInterceptor extends HandlerInterceptorAdapter {
    private static final String REQ_PARAM_TIMING = "request_timing";

    private final Counter totalRequests = Counter.build()
            .name("http_requests_total")
            .labelNames("method", "handler", "status")
            .help("Http Request Total")
            .register();

    private final Gauge inflightRequests = Gauge.build()
            .name("http_requests_inflight")
            .help("Inflight HTTP Requests")
            .register();

    private final Summary responseTimeInMs = Summary.build()
            .name("http_response_time_millis")
            .labelNames("method", "handler", "status")
            .help("Request completed time in milliseconds")
            .register();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        inflightRequests.inc();
        request.setAttribute(REQ_PARAM_TIMING, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception e)
            throws Exception {
        String handlerLabel = handler.toString();
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            handlerLabel = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        }
        inflightRequests.dec();
        totalRequests.labels(request.getMethod(), handlerLabel, Integer.toString(response.getStatus())).inc();
        Long timingAttr = (Long) request.getAttribute(REQ_PARAM_TIMING);
        if (timingAttr != null) {
            long completedTime = System.currentTimeMillis() - timingAttr;
            responseTimeInMs.labels(request.getMethod(), handlerLabel,
                    Integer.toString(response.getStatus())).observe(completedTime);
        }
    }
}