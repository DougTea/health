package io.transwarp.health.common;

/**
 * Created by Shannon on 2020/2/23.
 */
public enum HealthType {
    GREEN("00"),
    YELLOW("01"),
    RED("10"),
    NOT_FOUND("11");

    private String code;

    HealthType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
