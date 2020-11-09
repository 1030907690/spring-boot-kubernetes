package com.springboot.sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @RequestMapping("/")
    public String index() {
        String hostName = System.getenv("COMPUTERNAME");
        return "hostName:" +hostName;
    }
}