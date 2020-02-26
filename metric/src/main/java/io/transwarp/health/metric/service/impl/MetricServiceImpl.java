package io.transwarp.health.metric.service.impl;

import io.transwarp.health.metric.api.PvResp;
import io.transwarp.health.metric.api.UvResp;
import io.transwarp.health.metric.common.VersionConstants;
import io.transwarp.health.metric.configuration.properties.HbaseClientProperties;
import io.transwarp.health.metric.service.MetricService;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class MetricServiceImpl implements MetricService {
    public static final Logger LOG = LoggerFactory.getLogger(MetricServiceImpl.class);

    @Resource
    HConnection hConnection;

    @Resource
    HbaseClientProperties hbaseClientProperties;

    @Resource
    org.apache.hadoop.conf.Configuration hConfigration;

    @Override
    public PvResp getPv() {
        PvResp result = new PvResp();
        try {
            String uvCount = this.count(true);
            result.setPvCount(uvCount);
        } catch (Exception e) {
            LOG.error("get count error", e);
        }
        return result;
    }

    @Override
    public UvResp getUv() {
        UvResp result = new UvResp();
        try {
            String uvCount = this.count(false);
            result.setUvCount(uvCount);
        } catch (Exception e) {
            LOG.error("get count error", e);
        }
        return result;
    }


    @Scheduled(fixedDelay = 1000L * 3600 * 2, initialDelay = 1000L * 10)
    public void createTablePeriodically() {
        DateFormat dateFormat = new SimpleDateFormat(VersionConstants.DATEFORMATE);
        String today = hbaseClientProperties.getPvTableName() + "_" + dateFormat.format(new Date(System.currentTimeMillis()));
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String tomory = hbaseClientProperties.getPvTableName() + "_" + dateFormat.format(c.getTime());
        try {
            this.createTable(today);
            this.createTable(tomory);
        } catch (Exception e) {
            LOG.error("create table error", e);
        }
    }

    public String count(boolean raw) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat(VersionConstants.DATEFORMATE);
        String tbName = hbaseClientProperties.getPvTableName() + "_" + dateFormat.format(new Date(System.currentTimeMillis()));


        TableName tableName = TableName.valueOf(hbaseClientProperties.getDbName(), tbName);
        HTable hTable = new HTable(tableName, hConnection);
        int i = 0;
        ResultScanner rs;
        Scan s = new Scan();
        s.setCaching(10000);
        s.setCacheBlocks(true);
        s.setRaw(raw);  // raw == true 表示返回所有数据, raw == false 表示返回最新数据 (即去重)
        if (raw) {
            s.setMaxVersions(Integer.MAX_VALUE);
        }
        rs = hTable.getScanner(s);
        for (org.apache.hadoop.hbase.client.Result r : rs) {
            i += r.size();
        }
        long now = System.currentTimeMillis();
        rs.close();
        LOG.info("get hbase collection count: " + String.valueOf(i));
        return String.valueOf(i);
    }

    public void createTable(String tableName) throws Exception {
        HBaseAdmin admin = new HBaseAdmin(hConfigration);
        HTableDescriptor htds = new HTableDescriptor(tableName);
        HColumnDescriptor h = new HColumnDescriptor(hbaseClientProperties.getCfName());
        htds.addFamily(h);
        boolean tableExists1 = admin.tableExists(Bytes.toBytes(tableName));
        System.out.println(tableExists1 ? "表已存在" : "表不存在");
        try {
            admin.createTable(htds);
            LOG.info("create table" + tableName);
        } catch (TableExistsException e) {
            // skip
            LOG.error("Table exists, skip.", e);
        }
    }
}
