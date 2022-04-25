package com.redis.queue.service.impl;

import com.redis.queue.bean.redis.RedisQueueMessage;
import com.redis.queue.bean.redis.RedisQueueProcessResp;
import com.redis.queue.service.RedisQueueProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractRedisQueueProcessServiceImpl implements RedisQueueProcessService {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /***
     * zzq
     * 2022年2月14日09:49:20
     * 处理消息 如果返回失败或业务代码抛出异常，将重新加入队列
     * */
    protected abstract RedisQueueProcessResp doHandler(RedisQueueMessage redisQueueMessage);



    @Override
    @Transactional(rollbackFor = Exception.class)
    public RedisQueueProcessResp handler(RedisQueueMessage redisQueueMessage) {
        before(redisQueueMessage);
        RedisQueueProcessResp result = doHandler(redisQueueMessage);
        after(redisQueueMessage,result);
        return result;
    }

    protected void after(RedisQueueMessage redisQueueMessage,RedisQueueProcessResp result){

    }

    protected void before(RedisQueueMessage redisQueueMessage){

    }


    /***
     * zzq
     * 2022年2月21日17:23:33
     * 上面的handle方法完成后调用，返回成功，事务完成并且无异常，相当于完成后的回调
     * */
    @Override
    public void complete(RedisQueueMessage redisQueueMessage, RedisQueueProcessResp redisQueueProcessResp) {

    }
}
