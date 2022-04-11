package com.springboot.sample.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.springboot.sample.bean.RedisQueueMessage;
import com.springboot.sample.bean.RedisQueueProcessResp;
import com.springboot.sample.redis.process.RedisQueueProcessService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/***
 * zzq
 * 2022年2月13日13:39:34
 * 延迟队列
 * */
//@Component
public class DelayingQueueService implements InitializingBean {

    private static ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

    /** 是否销毁标记 volatile 保证可见性 **/
    private volatile boolean destroyFlag = false;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ApplicationContext applicationContext;

    // 设定空轮询最大次数
    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD = 512;

    // deadline 以及任务穿插逻辑处理  ，业务处理事件可能是5毫秒
    private long timeoutMillis = TimeUnit.MILLISECONDS.toNanos(5);

    /**
     * 可以不同业务用不同的key
     */
    @Value("${redisQueue.name}")
    public String queueName;


    /**
     * 插入消息
     *
     * @param redisQueueMessage
     * @return
     */
    public Boolean push(RedisQueueMessage redisQueueMessage)  {
        Boolean addFlag = null;
        try {
            addFlag = stringRedisTemplate.opsForZSet().add(queueName, mapper.writeValueAsString(redisQueueMessage), redisQueueMessage.getDelayTime());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return addFlag;
    }

    /**
     * 移除消息
     *
     * @param redisQueueMessage
     * @return
     */
    public Boolean remove(RedisQueueMessage redisQueueMessage) {
        Long remove = 0L;
        try {
            remove = stringRedisTemplate.opsForZSet().remove(queueName, mapper.writeValueAsString(redisQueueMessage));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return remove > 0 ? true : false;
    }


    /**
     * 拉取最新需要
     * 被消费的消息
     * rangeByScore 根据score范围获取 0-当前时间戳可以拉取当前时间及以前的需要被消费的消息
     *
     * @return
     */
    public RedisQueueMessage pop() {
        Set<String> strings = stringRedisTemplate.opsForZSet().rangeByScore(queueName, 0, System.currentTimeMillis());
        if (strings == null) {
            return null;
        }
        List<RedisQueueMessage> msgList = strings.stream().map(msg -> {
            RedisQueueMessage redisQueueMessage = null;
            try {
                redisQueueMessage = mapper.readValue(msg, RedisQueueMessage.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return redisQueueMessage;
        }).collect(Collectors.toList());

        // 有可能是集群，多个节点，设置抢占
        for (RedisQueueMessage redisQueueMessage : msgList) {
            if (remove(redisQueueMessage)) { // 为true 表示抢占到了
                return redisQueueMessage;
            }
        }
        return null;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread("loop-redis-queue") {
            @Override
            public void run() {
                int selectCnt = 0;

                while (!Thread.interrupted() && !destroyFlag) {
                    long currentTimeNanos = System.nanoTime();

                    RedisQueueMessage redisQueueMessage = pop();
                    System.out.println("拉取的数据 " + redisQueueMessage);
                    if (!StringUtils.isEmpty(redisQueueMessage)) {
                        try {
                            RedisQueueProcessService redisQueueProcessService = adapterHandler(redisQueueMessage.getBeanName());

                            invokeHandler(redisQueueMessage,redisQueueProcessService);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    selectCnt++;


                    // 解决空轮询问题
                    long time = System.nanoTime();
                    System.out.println("执行纳秒数" + (time - currentTimeNanos));
                    System.out.println(time + " -- " + (time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis)) + "--" + currentTimeNanos);
                    // 当前时间减去阻塞使用的时间  >= 上面的当前时间
                    if (time - timeoutMillis >= currentTimeNanos) {
                        // 有效的轮询
                        selectCnt = 1;
                    } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
                        // 如果空轮询次数大于等于SELECTOR_AUTO_REBUILD_THRESHOLD 默认512
                        selectCnt = 1;
                        threadSleep();
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    private void invokeHandler(RedisQueueMessage redisQueueMessage, RedisQueueProcessService redisQueueProcessService) {
        RedisQueueProcessResp result = null;
        try {
            result =  redisQueueProcessService.handler(redisQueueMessage);
            ifFailAgainAddQueue(redisQueueMessage, result);
        } catch (Exception e) {
            // 执行出现异常重新加入队列
            push(redisQueueMessage);
            System.out.println("执行业务代码程序异常");
            throw new RuntimeException("执行业务代码异常");
        }
    }

    protected void ifFailAgainAddQueue(RedisQueueMessage redisQueueMessage, RedisQueueProcessResp result) {
        if (!StringUtils.isEmpty(result) && HttpStatus.OK.value() != result.getCode()) {
            // 错误要重新加入队列
            push(redisQueueMessage);
        }
    }


    private RedisQueueProcessService adapterHandler(String beanName) {
       return applicationContext.getBean(beanName, RedisQueueProcessService.class);
    }

    private void threadSleep() {
        try {
            System.out.println("睡眠了");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @PreDestroy
    public void destroy() throws Exception {
        System.out.println("应用程序已关闭");
        this.destroyFlag = true;
    }



}
