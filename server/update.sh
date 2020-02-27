#!/bin/bash

docker build -t 172.27.148.11:5000/transwarp/hbase_health:v1 .
docker push 172.27.148.11:5000/transwarp/hbase_health:v1
