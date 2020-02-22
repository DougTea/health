package io.transwarp.health.service.impl;

import io.transwarp.health.api.HealthResponse;
import io.transwarp.health.common.HealthType;
import io.transwarp.health.configuration.properties.HbaseClientProperties;
import io.transwarp.health.service.HealthService;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hyperbase.datatype.PrimaryDataType;
import org.apache.hadoop.hyperbase.datatype.StringHDataType;
import org.apache.hadoop.hyperbase.datatype.StructHDataType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shannon on 2020/2/23.
 */
@Service
public class HealthServiceImpl implements HealthService {
  @Resource
  HConnection hConnection;

  @Resource
  HbaseClientProperties hbaseClientProperties;

  @Override
  public HealthResponse getHealth(String xm, String zjxm) {

    TableName tableName = TableName.valueOf(hbaseClientProperties.getDbName(), hbaseClientProperties.getTableName());
    HealthResponse response = new HealthResponse();

    try {
      HTable hTable = new HTable(tableName, hConnection);

      // struct serde
      List<Pair<PrimaryDataType, Integer>> typeList = new ArrayList<Pair<PrimaryDataType, Integer>>();

      // 这里的18就是建表时指定的数字,用来反编码
      typeList.add(new Pair<PrimaryDataType, Integer>(new StringHDataType(), hbaseClientProperties.getDecodeNum()));
      typeList.add(new Pair<PrimaryDataType, Integer>(new StringHDataType(), hbaseClientProperties.getDecodeNum()));
      StructHDataType structHDataType = new StructHDataType(typeList);
      Object[] values = {zjxm, xm};
      byte[] structValue = structHDataType.encode(values);

      // 1. 获取表数据
      Result result = hTable.get(new Get(structValue));
      // 2. 获取rk
      byte[] rk = result.getRow(); // rk其实就是 new Get里面的(structValue)
      // 3. 获取某一列的值,具体的可以从desc formatted table中看column mappings
      StringHDataType stringHDataType = new StringHDataType();

      byte[] type = result.getValue(Bytes.toBytes("f"), Bytes.toBytes("a3"));
      String sType = stringHDataType.decode(type);

      if (sType.equals(HealthType.GREEN.getCode()) || sType.equals(HealthType.YELLOW.getCode())
        || sType.equals(HealthType.RED.getCode())) {
        response.setType(sType);
      } else {
        response.setType(HealthType.NOT_FOUND.getCode());
      }
    } catch (IOException e) {
      response.setType(HealthType.NOT_FOUND.getCode());
    }
    return response;
  }
}
