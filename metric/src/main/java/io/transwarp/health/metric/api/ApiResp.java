package io.transwarp.health.metric.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Api Data Structure")
public class ApiResp<T> {
    @ApiModelProperty(
            value = "code",
            required = true
    )
    private String code;
    @ApiModelProperty("reponse message")
    private String message;
    @ApiModelProperty("response data")
    private T data;

    public ApiResp() {
    }

    public ApiResp(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
