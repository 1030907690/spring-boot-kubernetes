package com.springboot.sample.bean;

import org.springframework.http.HttpStatus;

public class RedisQueueProcessResp {


    public RedisQueueProcessResp() {

    }
    public RedisQueueProcessResp(int code) {
        this.code = code;
    }

    private int code;

    public static RedisQueueProcessResp success(){
        return new RedisQueueProcessResp(HttpStatus.OK.value());
    }

    public static RedisQueueProcessResp fail(){
        return new RedisQueueProcessResp(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
