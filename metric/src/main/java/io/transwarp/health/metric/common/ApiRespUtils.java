package io.transwarp.health.metric.common;

import io.transwarp.health.metric.api.ApiResp;

public class ApiRespUtils {
    public ApiRespUtils() {
    }

    public static <T> ApiResp<T> response(String code, String message, T data) {
        return new ApiResp(code, message, data);
    }

    public static <T> ApiResp<T> response(Result result, T data) {
        return response(result.getCode(), result.getDesc(), data);
    }

    public static <T> ApiResp<T> success(T data) {
        return response(Result.SUCCESS, data);
    }
}

