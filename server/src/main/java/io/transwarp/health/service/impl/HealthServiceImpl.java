package io.transwarp.health.service.impl;

import io.transwarp.health.api.HealthResponse;
import io.transwarp.health.clock.Clock;
import io.transwarp.health.common.HealthType;
import io.transwarp.health.configuration.properties.HbaseClientProperties;
import io.transwarp.health.service.HealthService;
import io.transwarp.health.service.MetricService;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hyperbase.datatype.PrimaryDataType;
import org.apache.hadoop.hyperbase.datatype.StringHDataType;
import org.apache.hadoop.hyperbase.datatype.StructHDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Shannon on 2020/2/23.
 */
@Service
public class HealthServiceImpl implements HealthService {
    public static final Logger LOG = LoggerFactory.getLogger(HealthServiceImpl.class);
    public static Clock clock = null;

    static {
        clock = new Clock();
    }

    private AtomicLong counter = new AtomicLong(0);

    @Resource
    HConnection hConnection;

    @Resource
    MetricService metricService;

    @Resource
    HbaseClientProperties hbaseClientProperties;

    @Override
    public HealthResponse getHealth(String xm, String zjxm) {


        long startTime = clock.currentTimeMillis();
        long time1 = startTime;
        long time2 = startTime;
        long time3 = startTime;
        boolean success = true;

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
            time1 = clock.currentTimeMillis();

            // 3. 获取某一列的值,具体的可以从desc formatted table中看column mappings
            StringHDataType stringHDataType = new StringHDataType();

            time2 = clock.currentTimeMillis();

            byte[] type = result.getValue(Bytes.toBytes("f"), Bytes.toBytes("a3"));
            if (type == null || type.length == 0) {
                response.setType(HealthType.GREEN.getCode());
                counter.incrementAndGet();
                return response;
            }
            String sType = stringHDataType.decode(type);
            time3 = clock.currentTimeMillis();

            if (sType.equals(HealthType.GREEN.getCode()) || sType.equals(HealthType.YELLOW.getCode())
                    || sType.equals(HealthType.RED.getCode()) || sType.equals(HealthType.NOT_FOUND.getCode())) {

                response.setType(sType);
            } else {
                response.setType(HealthType.GREEN.getCode());
            }
        } catch (IOException e) {
            LOG.error("IO Exception", e);
            success = false;
            response.setType(HealthType.GREEN.getCode());
        }

        counter.incrementAndGet();
        long period1 = time1 - startTime;
        long period2 = time2 - time1;
        long period3 = time3 - time2;
        if (period1 > 500 || period2 > 500 || period3 > 500) {
            LOG.error("request {}, success = {}, period1 = {}, period2 = {}, period3 = {}, xm={}, zjhm={}",
                counter.get(), success, period1, period2, period3, xm, zjxm);
        } else {
            LOG.info("request {}, success = {}, period1 = {}, period2 = {}, period3 = {}", counter.get(), success, period1, period2, period3);
        }

        // addmetric info
        metricService.addMetricTask(zjxm);

        return response;
    }
}
