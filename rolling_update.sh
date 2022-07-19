#!/bin/bash
ls
cp src/main/resources/Dockerfile target 
ls target
cd target
date=`date "+%Y%m%d%H%M%S"`
docker build -t spring-boot-kubernetes:$date .
kubectl set image  deployment/spring-boot-kubernetes-deployment  spring-boot-kubernetes=spring-boot-kubernetes:$date --record
kubectl get pod -o wide
kubectl rollout status deployment/spring-boot-kubernetes-deployment
kubectl get pod -o wide
