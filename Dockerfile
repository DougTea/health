FROM 172.16.1.99/transwarp/hbase:transwarp-6.0.2-final
RUN mkdir -p /opt/hbasehealth/
ADD run.sh /opt/hbasehealth/
ADD target/health-1.0-SNAPSHOT.jar /opt/hbasehealth/
