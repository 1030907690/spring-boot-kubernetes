package com.springboot.sample.controller;

import com.springboot.sample.example.KubernetesExample;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Zhou Zhongqing
 * @description:
 * @date 2022/7/5 14:21
 */
@RestController
public class KubernetesController {

    @RequestMapping("/getAllService")
    public String getAllService() {
        try {
            KubernetesExample.getAllPodNameAndService();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
