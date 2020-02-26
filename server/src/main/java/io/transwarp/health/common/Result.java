package io.transwarp.health.common;

/**
 * Created by Shannon on 2020/2/23.
 */
public interface Result {
    Result SUCCESS = new Result() {
        public String getCode() {
            return "0";
        }

        public String getDesc() {
            return "success";
        }
    };
    Result FAILURE = new Result() {
        public String getCode() {
            return "1";
        }

        public String getDesc() {
            return "failure";
        }
    };

    String getCode();

    String getDesc();

}
