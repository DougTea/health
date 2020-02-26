package io.transwarp.health.service.impl;

import io.transwarp.health.common.MetricTask;
import io.transwarp.health.configuration.properties.HbaseClientProperties;
import io.transwarp.health.service.MetricService;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.*;

@Service
public class MetricServiceImpl implements MetricService, SmartInitializingSingleton, DisposableBean {

    public static final Logger LOG = LoggerFactory.getLogger(MetricServiceImpl.class);

    private BlockingQueue<MetricTask> queue = new LinkedBlockingQueue<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean shutdown = false;

    @Resource
    private HConnection hConnection;

    @Resource
    private org.apache.hadoop.conf.Configuration hConfigration;


    @Resource
    HbaseClientProperties hbaseClientProperties;

    @Override
    public void afterSingletonsInstantiated() {
        executor.submit(() -> {
            this.consumeMetricTasks();
        });
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdownNow();
        shutdown = true;
    }

    @Override
    public void addMetricTask(String id) {
        queue.add(new MetricTask());
    }

    @Override
    public void consumeMetricTasks() {
        while (!shutdown) {
            try {
                MetricTask task = queue.poll(5000, TimeUnit.MILLISECONDS);
                // put to htable
                if (task != null) {
                    putData(hbaseClientProperties.getPvTableName(), task.getId());
                }
            } catch (Exception e) {
                LOG.error("put view access error", e);
            }
        }
    }

    private void createTable(String tableName) throws Exception {
        HBaseAdmin admin = new HBaseAdmin(hConfigration);
        HTableDescriptor htds = new HTableDescriptor(tableName);
        HColumnDescriptor h = new HColumnDescriptor(hbaseClientProperties.getCfName());
        htds.addFamily(h);
        boolean tableExists1 = admin.tableExists(Bytes.toBytes(tableName));
        LOG.info(tableExists1 ? "表已存在" : "表不存在");
        admin.createTable(htds);
        boolean tableExists = admin.tableExists(Bytes.toBytes(tableName));
        LOG.info(tableExists ? "创建表成功" : "创建失败");
    }

    public void dropTable(String tableName) throws Exception {
        HBaseAdmin admin = new HBaseAdmin(hConfigration);
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
        boolean tableExists1 = admin.tableExists(Bytes.toBytes(tableName));
        System.out.println(tableExists1 ? "表未删除" : "表已删除");
    }

    public void putData(String tableName, String zjhm) throws Exception {
        TableName tName = TableName.valueOf(hbaseClientProperties.getDbName(), hbaseClientProperties.getPvTableName());

        HTable hTable = new HTable(tName, hConnection);
        Put put = new Put(Bytes.toBytes(zjhm));
        put.add(Bytes.toBytes(hbaseClientProperties.getCfName()), Bytes.toBytes(hbaseClientProperties.getCqName()), Bytes.toBytes(""));
        hTable.setAutoFlush(true);
        try {
            hTable.put(put);
            System.out.println("插入成功");
        } catch (Exception e) {
            if (e.getCause() instanceof TableNotFoundException) {
                createTable(tableName);
                putData(tableName, zjhm);
            } else {
                throw e;
            }
        }
    }
}
