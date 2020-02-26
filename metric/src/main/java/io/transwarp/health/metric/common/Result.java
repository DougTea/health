package io.transwarp.health.metric.common;

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
