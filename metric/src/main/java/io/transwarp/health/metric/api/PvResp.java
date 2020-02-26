package io.transwarp.health.metric.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Pv Response")
public class PvResp {
    @ApiModelProperty("pv")
    private String pvCount;


    public String getPvCount() {
        return pvCount;
    }

    public void setPvCount(String pvCount) {
        this.pvCount = pvCount;
    }

    @Override
    public String toString() {
        return "PvResp{" +
                "pvCount='" + pvCount + '\'' +
                '}';
    }


}