package io.transwarp.health.metric.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Uv Response")
public class UvResp {

    @ApiModelProperty("uv")
    private String uvCount;

    public String getUvCount() {
        return uvCount;
    }

    public void setUvCount(String uvCount) {
        this.uvCount = uvCount;
    }

    @Override
    public String toString() {
        return "UvResp{" +
                "uvCount='" + uvCount + '\'' +
                '}';
    }

}
