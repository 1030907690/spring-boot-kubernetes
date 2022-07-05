package com.springboot.sample.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

/**
 * @author Zhou Zhongqing
 * @description:
 * @date 2022/7/5 11:40
 */
public class KubernetesExample {
    public static void main(String[] args) throws IOException, ApiException {
        getAllPodNameAndService();
    }

    public static List<String> getAllPodNameAndService() throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listPodForAllNamespaces(
                null,null,null,null,null,null,null,null,null,null);

        System.out.println("全部的Pod ");
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
        System.out.println("全部的Service");
        V1ServiceList v1ServiceList = api.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
        for (V1Service item : v1ServiceList.getItems()) {
            V1ObjectMeta metadata = item.getMetadata();
            String clusterIP = item.getSpec().getClusterIP();
            System.out.println(metadata.getName() + "----" + clusterIP);
            if ("spring-boot".equals(metadata.getName())) {
                List<V1ServicePort> ports = item.getSpec().getPorts();
                for (V1ServicePort port : ports) {
                    System.out.println("name  " + port.getName() + " port " + port.getPort() + " nodePort "
                            + port.getNodePort() + " targetPort " + port.getTargetPort());


                    RestTemplate restTemplate = new RestTemplate();
                    String response = restTemplate.getForObject("http://" + clusterIP + ":" + port.getPort() + "/", String.class);
                    System.out.println("请求返回值 "+ response);
                }
            }

        }
        return null;
    }
}
