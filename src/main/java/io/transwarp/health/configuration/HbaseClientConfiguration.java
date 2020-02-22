package io.transwarp.health.configuration;

import io.transwarp.health.configuration.properties.HbaseClientProperties;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by Shannon on 2020/2/23.
 */
@Configuration
public class HbaseClientConfiguration {
  public static final Logger LOG = LoggerFactory.getLogger(HbaseClientConfiguration.class);

  @Resource
  HbaseClientProperties hbaseClientProperties;

  @Bean
  public HConnection hConnection() throws IOException {
    org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
    configuration.set("hbase.zookeeper.quorum", hbaseClientProperties.getHbaseZkQuorum());
    configuration.set("zookeeper.znode.parent", hbaseClientProperties.getZkZnodeParent());
    configuration.set("hbase.zookeeper.property.clientPort", hbaseClientProperties.getHbaseZkPropertyClientPort());

    if (!StringUtils.isEmpty(hbaseClientProperties.getAuthentication())) {
      // security
      configuration.set("hbase.master.kerberos.principal", hbaseClientProperties.getPrincipal());
      configuration.set("hbase.regionserver.kerberos.principal", hbaseClientProperties.getPrincipal());
      configuration.set("hbase.security.authentication", hbaseClientProperties.getAuthentication());
      configuration.set("hadoop.security.authentication", hbaseClientProperties.getPrincipal());

      UserGroupInformation.setConfiguration(configuration);
      UserGroupInformation.loginUserFromKeytab(hbaseClientProperties.getLoginUser(), hbaseClientProperties.getKeytabFile());
    }
    HConnection hConnection = HConnectionManager.getConnection(configuration);

    return hConnection;
  }
}
