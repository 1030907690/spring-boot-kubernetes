package com.springboot.sample.redis.process;

import com.redis.queue.bean.redis.RedisQueueMessage;
import com.redis.queue.bean.redis.RedisQueueProcessResp;
import com.redis.queue.service.RedisQueueProcessService;
import com.redis.queue.service.impl.AbstractRedisQueueProcessServiceImpl;
import com.springboot.sample.bean.Users;
import com.springboot.sample.mapper.UsersMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SampleRedisQueueProcessServiceImpl  extends AbstractRedisQueueProcessServiceImpl{

    @Resource
    private UsersMapper usersMapper;

    @Override
    protected RedisQueueProcessResp doHandler(RedisQueueMessage redisQueueMessage) {
        System.out.println("消费到数据 " + redisQueueMessage);
//                int a = 1 / 0;
        Users entity = new Users();
        entity.setName(redisQueueMessage.getId());
        usersMapper.insert(entity);
        if (true){
            throw new RuntimeException("测试事务回滚");
        }
        return RedisQueueProcessResp.success();
    }

}
