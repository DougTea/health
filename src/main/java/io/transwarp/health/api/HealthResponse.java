package io.transwarp.health.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Created by Shannon on 2020/2/23.
 */
@ApiModel("Health Response")
public class HealthResponse {
  @ApiModelProperty("type")
  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "{" +
      "\"type\":" + " \""  + type + '\"' +
      '}';
  }
}
