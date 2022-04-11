package com.springboot.sample.controller;

import com.springboot.sample.annotation.ConditionalValidate;
import com.springboot.sample.bean.Person;
import com.springboot.sample.bean.TestValidate;
import com.springboot.sample.service.TestService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

@RestController
@RequestMapping("/validate")
public class ValidateController {
/*    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    *//**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     *//*
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                try {
                    setValue(DateUtils.parseDate(text,parsePatterns));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
/*
    @RequestMapping("/test")
    public String test(@Validated Person person) {
        return "success";
    }*/

    @Resource
    private TestService testService;


    @RequestMapping("/test")
    @ConditionalValidate
    public String test(@Validated TestValidate testValidate) {
        testService.testInsert();
        return "success";
    }

}
