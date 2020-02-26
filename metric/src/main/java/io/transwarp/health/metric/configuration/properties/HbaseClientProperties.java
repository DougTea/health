package io.transwarp.health.metric.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Shannon on 2020/2/23.
 */
@Getter
@Setter
@Component("hbaseClientProperties")
@ConfigurationProperties(prefix = "hbaseconf")
public class HbaseClientProperties {
    private String hbaseZkQuorum;

    private String zkZnodeParent;

    private String hbaseZkPropertyClientPort;

    private String dbName;

    private String tableName;

    private int decodeNum;

    private String principal;

    private String authentication;

    private String keytabFile;

    private String loginUser;

    private int hbaseRpcTimeout = 5000;

    private int hbaseClientRetriesNumber = 3;

    private String pvTableName = "hbbase_health_metric";

    private String cfName = "f";

    private String cqName = "c";
}
