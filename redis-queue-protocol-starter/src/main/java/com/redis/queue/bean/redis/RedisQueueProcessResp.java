package com.redis.queue.bean.redis;

import org.springframework.http.HttpStatus;

public class RedisQueueProcessResp {


    public RedisQueueProcessResp() {

    }
    public RedisQueueProcessResp(int code) {
        this.code = code;
    }

    private String remarks;

    private int code;

    /** 扩展字段 **/
    private Object extra;


    public static RedisQueueProcessResp success(){
        return new RedisQueueProcessResp(HttpStatus.OK.value());
    }

    public static RedisQueueProcessResp fail(){
        return new RedisQueueProcessResp(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /***
     * 业务异常
     * */
    public static RedisQueueProcessResp businessFail(){
        return new RedisQueueProcessResp(HttpStatus.NOT_IMPLEMENTED.value());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
