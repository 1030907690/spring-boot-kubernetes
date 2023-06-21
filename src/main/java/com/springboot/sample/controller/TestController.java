package com.springboot.sample.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.sample.bean.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhou Zhongqing
 * @ClassName TestController
 * @description: TODO
 * @date 2023-06-21 16:07
 */
@RestController
public class TestController extends BaseController {

    @RequestMapping("/test")
    public User test(User user){
        return user;
    }
}
