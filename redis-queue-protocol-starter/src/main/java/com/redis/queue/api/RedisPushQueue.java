package com.redis.queue.api;

import com.redis.queue.core.DelayingQueueService;
import com.redis.queue.bean.redis.RedisQueueMessage;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/***
 * zzq
 * 2022年2月14日10:13:54
 * 推送redis队列消息
 * */
public class RedisPushQueue {

    @Resource
    private DelayingQueueService delayingQueueService;



    /***
     * zzq
     * 2022年2月22日18:13:31
     * 加入队列
     * @param queueName 队列名称
     * @param handleBeanName  Spring BeanName 对应处理类
     * @param json            json数据
     * @param delayMillis     延迟消费（毫秒）
     * @return  添加成功返回true 失败false
     * */
    public boolean enqueue(String queueName, String handleBeanName, String json, long delayMillis) {
        RedisQueueMessage redisQueueMessage = new RedisQueueMessage();
        String seqId = UUID.randomUUID().toString().replace("-", "");
        //时间戳默认为毫秒
        long time = System.currentTimeMillis() + delayMillis;
        redisQueueMessage.setDelayTime(time);
        redisQueueMessage.setCreateTime(new Date());
        redisQueueMessage.setBody(json);
        redisQueueMessage.setId(seqId);
        redisQueueMessage.setBeanName(handleBeanName);
        redisQueueMessage.setRetryCount(0);
        return delayingQueueService.enqueue(redisQueueMessage, queueName);
    }

}
