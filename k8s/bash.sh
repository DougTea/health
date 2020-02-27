#!/bin/bash
#多进程对多个机器同时发指令
set -x
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

REQUEST_NUM=$1
CONCURRENT_NUM=$2
 
#服务器和用户账号对应关系
tot_s=${#server[*]}
 
i=0
while [ $i -lt $tot_s ];do
    (ssh root@${server[$i]} "ulimit -n 100000 && nohup ab -n ${REQUEST_NUM} -c ${CONCURRENT_NUM} -r 'http://172.27.148.228:31420/api/v1/health?xm=abc&zjhm=${ids[$i]}$RANDOM' > /root/rest.result 2>&1") & 
    #注意一定要在最后面加&符号，否则就是串行执行，不能体现并行。
    #将错误重定向到日志文件中
 
    if [ $? -ne 0 ];then
        echo "root@${server[$i]}执行过程中出现异常"
    fi
 
    #注意迭代器别忘了自增
    i=$[ $i + 1 ]
done

wait

