# spring-boot-kubernetes
spring-boot-kubernetes例子

## 构建Docker镜像
- docker build -t spring-boot-kubernetes:1.0.0 .

### 导出tar包
- docker save spring-boot-kubernetes:1.0.0 > spring-boot-kubernetes-1.0.0.tar

### 导入tar包
- eval $(minikube docker-env)
- docker load < spring-boot-kubernetes-1.0.0.tar