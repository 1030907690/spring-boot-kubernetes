package com.springboot.sample.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@RestController
public class IndexController {

    @Value("${custom.value}")
    private String customValue;

    @RequestMapping("/")
    public String index() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostName = addr.getHostName();
        System.out.println("Local host name(本地host名称): " + hostName + " date（时间）: " + new Date());
        return "Local host name(本地host名称): " + hostName + " date（时间）: " + new Date() + " customValue: " + customValue;
    }
}