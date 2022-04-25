package com.redis.queue.service;

import com.redis.queue.bean.redis.RedisQueueMessage;
import com.redis.queue.bean.redis.RedisQueueProcessResp;

/***
 * zzq
 * 2022年2月14日09:49:12
 * redis队列消息处理
 * */
public interface RedisQueueProcessService {

    /***
     * zzq
     * 2022年2月14日09:49:20
     * 处理消息 如果返回失败或业务代码抛出异常，将重新加入队列
     * */
    RedisQueueProcessResp handler(RedisQueueMessage redisQueueMessage);

    /***
     * zzq
     * 2022年2月21日17:23:33
     * 上面的handle方法完成后调用，返回成功，事务完成并且无异常，相当于完成后的回调
     * */
    void complete(RedisQueueMessage redisQueueMessage,RedisQueueProcessResp redisQueueProcessResp);
}
