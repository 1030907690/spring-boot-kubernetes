package com.springboot.sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class IndexController {

    @RequestMapping("/")
    public String index() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostName = addr.getHostName();
        System.out.println("Local host name: "+hostName);
        return "hostName:" +hostName;
    }
}