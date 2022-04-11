package com.springboot.sample.redis.process.impl;

import com.springboot.sample.bean.RedisQueueMessage;
import com.springboot.sample.bean.RedisQueueProcessResp;
import com.springboot.sample.redis.DelayingQueueService;
import com.springboot.sample.redis.process.RedisQueueProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

public abstract class AbstractRedisQueueProcessServiceImpl implements RedisQueueProcessService {


    protected abstract RedisQueueProcessResp doHandler(RedisQueueMessage redisQueueMessage);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RedisQueueProcessResp handler(RedisQueueMessage redisQueueMessage) {
        before(redisQueueMessage);
        RedisQueueProcessResp redisQueueProcessResp = doHandler(redisQueueMessage);
        after(redisQueueMessage,redisQueueProcessResp);
        return redisQueueProcessResp;
    }

    protected void after(RedisQueueMessage redisQueueMessage,RedisQueueProcessResp redisQueueProcessResp){

    }

    protected void before(RedisQueueMessage redisQueueMessage){

    }


}
