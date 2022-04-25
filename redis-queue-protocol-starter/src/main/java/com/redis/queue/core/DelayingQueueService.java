package com.redis.queue.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.redis.queue.bean.redis.RedisQueueMessage;
import com.redis.queue.bean.redis.RedisQueueProcessResp;
import com.redis.queue.exception.RedisConsumerBusinessException;
import com.redis.queue.service.IGenericRedisConsumerLogService;
import com.redis.queue.service.RedisQueueProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.CollectionUtils;
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
 * 2022年2月14日09:49:02
 * 延迟队列
 * */
public class DelayingQueueService implements InitializingBean {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

    /**
     * 最大重试次数
     **/
    private final Integer MAX_RETRY_COUNT = 10;

    /**
     * 是否销毁标记 volatile 保证可见性
     **/
    private volatile boolean destroyFlag = false;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ApplicationContext applicationContext;

    // 设定空轮询最大次数
    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD = 512;

    // deadline 以及任务穿插逻辑处理  ，业务处理事件可能是1秒
    private long timeoutMillis = TimeUnit.SECONDS.toNanos(1);

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
    public Boolean enqueue(RedisQueueMessage redisQueueMessage) {

        return enqueue(redisQueueMessage, this.queueName);
    }


    /**
     * 超过最大重试次数 ，不再重试
     *
     * @param redisQueueMessage
     * @return
     */
    public Boolean retryEnenqueue(RedisQueueMessage redisQueueMessage) {
        if (redisQueueMessage.getRetryCount() >= MAX_RETRY_COUNT) {
            return Boolean.FALSE;
        }
        redisQueueMessage.setRetryCount(redisQueueMessage.getRetryCount() + 1);
        return enqueue(redisQueueMessage, this.queueName);
    }


    /**
     * 插入消息
     *
     * @param redisQueueMessage
     * @return
     */
    public Boolean enqueue(RedisQueueMessage redisQueueMessage, String queueName) {
        Boolean addFlag = null;
        try {
            addFlag = stringRedisTemplate.opsForZSet().add(queueName, mapper.writeValueAsString(redisQueueMessage), redisQueueMessage.getDelayTime());
        } catch (Exception e) {
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
        } catch (Exception e) {
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
    public RedisQueueMessage dequeue() {
        Set<String> stringSet = null;
        try {
            stringSet = stringRedisTemplate.opsForZSet().rangeByScore(queueName, 0, System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(stringSet)) {
            return null;
        }
        List<RedisQueueMessage> msgList = stringSet.stream().map(msg -> {
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
            seizeLog(redisQueueMessage);
            //TODO 抢占也可以用lua脚本执行，就省去for循环，获取到却可能抢不到的情况
            if (remove(redisQueueMessage)) { // 为true 表示抢占到了
                return redisQueueMessage;
            }
        }
        return null;
    }

    private void seizeLog(RedisQueueMessage redisQueueMessage) {
        IGenericRedisConsumerLogService genericRedisConsumerLogService = applicationContext.getBean(IGenericRedisConsumerLogService.class);
        if (!StringUtils.isEmpty(genericRedisConsumerLogService)) {
            try {
                genericRedisConsumerLogService.seize(redisQueueMessage, this.queueName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        threadLoop();
    }

    private void threadLoop() {
        Thread thread = new Thread("loop-redis-queue") {
            @Override
            public void run() {
                int selectCnt = 0;
                while (!Thread.interrupted() && !destroyFlag) {
                    long currentTimeNanos = System.nanoTime();

                    RedisQueueMessage redisQueueMessage = dequeue();
//                    log.info("弹出数据 " + redisQueueMessage);
                    if (!StringUtils.isEmpty(redisQueueMessage)) {
                        try {
                            RedisQueueProcessService redisQueueProcessService = adapterHandler(redisQueueMessage.getBeanName());
                            if (!StringUtils.isEmpty(redisQueueProcessService)) {
                                invokeHandler(redisQueueMessage, redisQueueProcessService);
                            } else {
                                log.error("accept not handler message " + redisQueueMessage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    selectCnt++;

                    // 解决空轮询问题
                    long time = System.nanoTime();

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
        IGenericRedisConsumerLogService genericRedisConsumerLogService = applicationContext.getBean(IGenericRedisConsumerLogService.class);
        Long redisConsumerLogId = before(genericRedisConsumerLogService, redisQueueMessage, this.queueName);

        RedisQueueProcessResp result = RedisQueueProcessResp.fail();
        try {
            result = redisQueueProcessService.handler(redisQueueMessage);
            ifFailAgainAddQueue(redisQueueMessage, result);
            ifSuccessfulInvokeComplete(redisQueueProcessService, redisQueueMessage, result);
            after(genericRedisConsumerLogService, redisConsumerLogId, redisQueueMessage, result);
        } catch (RedisConsumerBusinessException e) {
            result = RedisQueueProcessResp.businessFail();
            log.info("执行业务代码程序异常 RedisConsumerBusinessException 异常信息 " + e.getMessage() + e.getStackTrace());
            log.info("e: " + e);
            result.setRemarks(e.getMessage());
            after(genericRedisConsumerLogService, redisConsumerLogId, redisQueueMessage, result);
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            result = RedisQueueProcessResp.fail();
            log.info("执行业务代码程序异常 异常信息 " + e.getMessage() + e.getStackTrace());
            log.info("e: " + e);
            e.printStackTrace();
            result.setRemarks(e.getMessage());
            if (StringUtils.isEmpty(result.getRemarks()) && !StringUtils.isEmpty(e)) {
                result.setRemarks(e.toString());
            }

            if (!StringUtils.isEmpty(result.getRemarks()) && result.getRemarks().length() > 400) {
                result.setRemarks(result.getRemarks().substring(0, 400));
            }

            after(genericRedisConsumerLogService, redisConsumerLogId, redisQueueMessage, result);
            // 执行出现异常重新加入队列
            retryEnenqueue(redisQueueMessage);
            throw e;
        }
    }

    private void ifSuccessfulInvokeComplete(RedisQueueProcessService redisQueueProcessService, RedisQueueMessage redisQueueMessage, RedisQueueProcessResp result) {
        if (!StringUtils.isEmpty(result) && HttpStatus.OK.value() == result.getCode()) {
            try {
                redisQueueProcessService.complete(redisQueueMessage, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void after(IGenericRedisConsumerLogService genericRedisConsumerLogService, Long redisConsumerLogId, RedisQueueMessage redisQueueMessage, RedisQueueProcessResp result) {
        try {
            if (!StringUtils.isEmpty(genericRedisConsumerLogService)) {
                if (!StringUtils.isEmpty(redisConsumerLogId) && redisConsumerLogId > 0) {
                    genericRedisConsumerLogService.after(redisConsumerLogId, redisQueueMessage, result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long before(IGenericRedisConsumerLogService genericRedisConsumerLogService, RedisQueueMessage redisQueueMessage, String queueName) {
        Long redisConsumerLogId = null;
        if (!StringUtils.isEmpty(genericRedisConsumerLogService)) {
            try {
                redisConsumerLogId = genericRedisConsumerLogService.before(redisQueueMessage, this.queueName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return redisConsumerLogId;
    }

    protected void ifFailAgainAddQueue(RedisQueueMessage redisQueueMessage, RedisQueueProcessResp result) {
        if (!StringUtils.isEmpty(result) && HttpStatus.OK.value() != result.getCode()) {
            // 错误要重新加入队列
            retryEnenqueue(redisQueueMessage);
        }
    }


    private RedisQueueProcessService adapterHandler(String beanName) {
        return applicationContext.getBean(beanName, RedisQueueProcessService.class);
    }

    private void threadSleep() {
        try {
//           log.info("睡眠了");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @PreDestroy
    public void destroy() throws Exception {
        log.info("application destroy");
        this.destroyFlag = true;
    }
}
