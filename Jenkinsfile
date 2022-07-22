pipeline {
    agent any
    tools {
        maven 'maven3.6' 
    }
    stages {
        stage('开始') {
            steps {
                echo '开始 '
            }
        }
        
     //stage('克隆项目') {
     //   steps {
     //   git branch: 'main',
     //   url: 'https://github.com/1030907690/spring-boot-kubernetes.git'
     //       sh 'pwd'
     //       sh "ls -lat"
     //     }
     //   }
        
       stage('构建') {
          steps {
            sh 'mvn clean package -DskipTests'
            sh 'echo tag  ${BUILD_TAG}'
            sh 'cp src/main/resources/Dockerfile target'
            sh 'cd target && docker build -t spring-boot-kubernetes:${BUILD_TAG} .'
            sh 'kubectl set image  deployment/spring-boot-kubernetes-deployment  spring-boot-kubernetes=spring-boot-kubernetes:${BUILD_TAG} --record'
        
      
          }
        }
        
        stage('滚动更新') {
          steps {
            sh 'kubectl set image  deployment/spring-boot-kubernetes-deployment  spring-boot-kubernetes=spring-boot-kubernetes:${BUILD_TAG} --record'
            sh 'kubectl get pod -o wide'
            sh 'kubectl rollout status deployment/spring-boot-kubernetes-deployment'
            sh 'kubectl get pod -o wide'
      
          }
        }
        
        
        
    }
    post {
        success {
            echo '更新成功'
                   
        }

        always {
            echo 'goodbye'
        }
    }
}
