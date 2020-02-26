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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricServiceImpl implements MetricService, SmartInitializingSingleton, DisposableBean {

    private static final long BATCH_MILLIS = 5000;

    public static final Logger LOG = LoggerFactory.getLogger(MetricServiceImpl.class);

    private BlockingQueue<List<MetricTask>> queue = new LinkedBlockingQueue<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean shutdown = false;
    private AtomicLong lastMetricTime = new AtomicLong(0);
    private List<MetricTask> cacheQueue = Collections.synchronizedList(new ArrayList<>());

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
        MetricTask task = new MetricTask();
        task.setId(id);
        cacheQueue.add(task);
        if (System.currentTimeMillis() - lastMetricTime.get() > BATCH_MILLIS) {
            List<MetricTask> copyList = new ArrayList<>();
            Collections.copy(copyList, cacheQueue);
            cacheQueue.clear();
            lastMetricTime.set(System.currentTimeMillis());
            this.queue.add(copyList);
        }
    }

    @Override
    public void consumeMetricTasks() {
        while (!shutdown) {
            try {
                List<MetricTask> task = queue.poll(5000, TimeUnit.MILLISECONDS);
                // put to htable
                if (task != null) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd");
                    String tbName = hbaseClientProperties.getPvTableName() + "_" + dateFormat.format(new Date(System.currentTimeMillis()));
                    putData(tbName, task);
                }
            } catch (Exception e) {
                LOG.error("put view access error", e);
            }
        }
    }


    public void putData(String tableName, List<MetricTask> taskList) throws Exception {

        TableName tName = TableName.valueOf(hbaseClientProperties.getDbName(), tableName);

        HTable hTable = new HTable(tName, hConnection);
        hTable.setAutoFlush(false);
        for (int i = 0; i < taskList.size(); i++) {
            Put put = new Put(Bytes.toBytes(taskList.get(i).getId()));
            put.add(Bytes.toBytes(hbaseClientProperties.getCfName()), Bytes.toBytes(hbaseClientProperties.getCqName()), Bytes.toBytes(""));
            try {
                hTable.put(put);
            } catch (Exception e) {
                throw e;
            }
        }
        hTable.flushCommits();
    }
}
