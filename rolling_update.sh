#!/bin/bash
ls
cp src/main/resources/Dockerfile target 
ls target
cd target
date=`date "+%Y%m%d%H%M%S"`
# 构建镜像
docker build -t spring-boot-kubernetes:$date .
# 滚动更新
kubectl set image  deployment/spring-boot-kubernetes-deployment  spring-boot-kubernetes=spring-boot-kubernetes:$date --record
# 查看Pod情况
kubectl get pod -o wide
# 查看滚动更新状态
kubectl rollout status deployment/spring-boot-kubernetes-deployment
# 查看Pod情况
kubectl get pod -o wide
