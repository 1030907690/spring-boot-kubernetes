package com.springboot.sample.redis.process;

import com.springboot.sample.bean.RedisQueueMessage;
import com.springboot.sample.bean.RedisQueueProcessResp;

/***
 * zzq
 * 2022年2月13日14:42:10
 * redis队列消息处理
 * */
public interface RedisQueueProcessService {

     /***
      * zzq
      * 2022年2月13日14:47:51
      * 处理消息
      * */
     RedisQueueProcessResp handler(RedisQueueMessage redisQueueMessage);
}
