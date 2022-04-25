package com.redis.queue.service;

import com.redis.queue.bean.redis.RedisQueueMessage;
import com.redis.queue.bean.redis.RedisQueueProcessResp;

/***
 * zzq
 * 2022年2月15日14:56:02
 * 通用redis消费记录
 * */
public interface IGenericRedisConsumerLogService {

    /***
     * zzq
     * 2022年3月1日10:56:11
     * 抢占消息的记录
     * */
    void seize(RedisQueueMessage redisQueueMessage, String queueName);

    /**
     * 返回数据id
     **/
    Long before(RedisQueueMessage redisQueueMessage,String queueName);


    /***
     * @param id
     * @param redisQueueMessage
     * @param result
     * */
    void after(Long id, RedisQueueMessage redisQueueMessage, RedisQueueProcessResp result);
}
