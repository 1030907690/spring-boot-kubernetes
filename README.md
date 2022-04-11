# v1.0.4 
- 支持同一个属性多次使用相同注解
- 测试事务

# spring-boot-kubernetes
spring-boot-kubernetes例子

## 构建Docker镜像
- docker build -t spring-boot-kubernetes:1.0.0 .

### 导出tar包
- docker save spring-boot-kubernetes:1.0.0 > spring-boot-kubernetes-1.0.0.tar

### 导入tar包 
- eval $(minikube docker-env)  切换到minikube daemon
- docker load < spring-boot-kubernetes-1.0.0.tar   导入

### kubernetes
- kubectl apply -f  deploy.yaml
- kubectl expose deployment spring-boot-kubernetes --type=NodePort
- minikube service spring-boot-kubernetes --url

## 遇到的异常
- 1、no matches for kind "Deployment" in version "apps/v1beta1"
- 这是因为API版本已正式发布，不再是beta了，将apps/v1beta1修改为apps/v1。
- 2、Container image "spring-boot-kubernetes" is not present with pull policy of Never Error: ErrImageNe
- 因为我的镜像版本是1.0.0，不写应该会默认使用latest版本，找不到，一直报这个错，强制声明spring-boot-kubernetes:1.0.0<image:tag>即可。

### 参考
- https://www.jianshu.com/p/592da53cdff0
- https://www.jianshu.com/p/a9aec78418df
