#!/bin/bash
#多进程对多个机器同时发指令
#注意下面的用户和ip是一一对应的，顺序不要搞错，额否则不能登录
#user1登录server1
server=(
    "namenode002"
    "namenode003"
    "namenode004"
    "namenode005"
    "datanode002"
    "datanode003"
    "datanode004"
    "datanode005"
)

ids=(
    "131"
    "1427"
    "2302"
    "310103"
    "310106"
    "310108"
    "310110"
    "310114"
    "310224"
    "310227"
)

tot_s=${#server[*]};i=0;while [ $i -lt $tot_s ];do ssh root@${server[$i]} 'cat /root/rest.result'; ((i=i+1)); done


