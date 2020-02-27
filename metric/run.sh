#!/bin/bash

basedir=$(cd `dirname $0`; pwd)

cp /etc/hbasehealth/conf/application.yml ${basedir}/application.yml

mkdir -p /var/log/app
JAVA_HEAP_ARGS="-XX:+UnlockExperimentalVMOptions  -XX:MaxRAMFraction=1 -XX:+UseG1GC"
exec java -cp ${basedir}/application.yml $JAVA_HEAP_ARGS -jar ${basedir}/health-metric-1.0-SNAPSHOT.jar
